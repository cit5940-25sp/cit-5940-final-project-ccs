import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.*;
import com.googlecode.lanterna.screen.*;
import com.googlecode.lanterna.terminal.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Handles the terminal-based user interface for the Movie Game.
 * Manages player input, screen updates, turn timer, and game progression.
 */
public class GameView {
    /** Maximum allowed time (in seconds) for each player's turn. */
    private final int TIMELIMIT = 60;
    // Tracks the current input phase of the game (e.g., player name entry, gameplay, etc.)
    private InputStage stage = InputStage.PLAYER1_NAME;
    private String player1Name = "";
    private String player2Name = "";
    private List<WinCondition> winConditions = Arrays.asList(
            new TwoHorrorMoviesWin(),
            new TwoNolanMoviesWin()
    );

    private GameController controller;
    private Terminal terminal;
    private Screen screen;
    private StringBuilder currentInput = new StringBuilder();
    private List<String> suggestions = new ArrayList<>();
    private int selectedSuggestionIndex = -1;
    private int cursorPosition = 0;

    // Timer variables
    private int secondsRemaining = TIMELIMIT;
    private boolean timerRunning = true;
    private volatile boolean turnInProgress = false;
    private ScheduledExecutorService scheduler;

    /**
     * Constructs a GameView object that handles the game interface, timer, and screen rendering.
     *
     * @param controller The GameController instance managing game logic.
     * @throws IOException If there is an error initializing the screen or terminal.
     */
    public GameView(GameController controller) throws IOException {
        this.controller = controller;
        this.terminal = new DefaultTerminalFactory().createTerminal();
        this.screen = new TerminalScreen(terminal);
        screen.startScreen();

        // Initialize timer thread
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            if (stage == InputStage.IN_GAME && timerRunning && secondsRemaining > 0) {
                if (turnInProgress) {
                    return;  // Wait until turn is done
                }

                secondsRemaining--;
                try {
                    updateScreen();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (secondsRemaining == 0) {
                    timerRunning = false;
                    try {
                        printInfo("Time's up! " + controller.getGameState().
                                getOtherPlayer().getName() + " wins!");
                        screen.close();
                        terminal.close();
                        System.exit(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }
    /**
     * Starts the main game loop, handling player input, screen updates, and game events.
     *
     * @throws IOException If there is an issue with screen rendering.
     * @throws InterruptedException If the game loop is interrupted during execution.
     */
    public void run() throws IOException, InterruptedException {
        boolean running = true;

        // Initial screen
        screen.clear();
        updateScreen();
        screen.refresh();

        while (running) {
            KeyStroke keyStroke = terminal.pollInput();
            if (keyStroke != null) {
                switch (keyStroke.getKeyType()) {
                    case Character:
                        handleCharacter(keyStroke.getCharacter());
                        break;
                    case Backspace:
                        handleBackspace();
                        break;
                    case Enter:
                        running = handleEnter();  // returns false if game ends
                        break;
                    case Escape:
                    case EOF:
                        running = false;
                        break;
                    case ArrowDown:
                        if (!suggestions.isEmpty()) {
                            selectedSuggestionIndex = (selectedSuggestionIndex + 1) %
                                    suggestions.size();
                        }
                        break;
                    case ArrowUp:
                        if (!suggestions.isEmpty()) {
                            selectedSuggestionIndex = (selectedSuggestionIndex - 1 +
                                    suggestions.size()) % suggestions.size();
                        }
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: "
                                + keyStroke.getKeyType());
                }
                updateSuggestions();
                updateScreen();
            }

            Thread.sleep(10);
        }

        scheduler.shutdown();
        screen.close();
        terminal.close();
    }
    /**
     * Handles character input from the player, inserting it into the current input field.
     *
     * @param c The character entered by the player.
     */
    private void handleCharacter(char c) {
        currentInput.insert(cursorPosition, c);
        cursorPosition++;
    }
    /**
     * Handles backspace input from the player, removing the last character from the current input.
     */
    private void handleBackspace() {
        if (cursorPosition > 0) {
            currentInput.deleteCharAt(cursorPosition - 1);
            cursorPosition--;
        }
    }
    /**
     * Handles the Enter key event, processing the current input according to the game stage.
     *
     * @return true if the game continues, false if it ends.
     */
    private boolean handleEnter() {
        String input = currentInput.toString().trim();

        switch (stage) {
            case PLAYER1_NAME:
                player1Name = input;
                currentInput.setLength(0);
                cursorPosition = 0;
                stage = InputStage.PLAYER2_NAME;
                break;

            case PLAYER2_NAME:
                player2Name = input;
                currentInput.setLength(0);
                cursorPosition = 0;
                stage = InputStage.WIN_CONDITION_SELECTION;
                break;

            case WIN_CONDITION_SELECTION:
                try {
                    int winConditionIndex = Integer.parseInt(input);
                    if (winConditionIndex >= 1 && winConditionIndex <= winConditions.size()) {
                        WinCondition selected = winConditions.get(winConditionIndex - 1);
                        controller.startGame(player1Name, player2Name, selected);
                        secondsRemaining = TIMELIMIT;
                        stage = InputStage.IN_GAME;
                    } else {
                        printInfo("Please enter a number from 1 to " + winConditions.size());
                    }
                } catch (NumberFormatException e) {
                    printInfo("Invalid input. Please enter a number.");
                }
                currentInput.setLength(0);
                cursorPosition = 0;
                break;

            case IN_GAME:
                if (input.equalsIgnoreCase("exit")) {
                    return false;
                }

                // Handle suggestion selection
                if (selectedSuggestionIndex >= 0) {
                    currentInput.setLength(0);
                    currentInput.append(suggestions.get(selectedSuggestionIndex));
                    cursorPosition = currentInput.length();
                    selectedSuggestionIndex = -1;
                    return true;
                }

                // Mark that we're processing a turn
                turnInProgress = true;

                TurnResult result = controller.processTurn(input);
                printInfo(result.getMessage());

                turnInProgress = false;

                if (!result.isSuccess()) {
                    return true;
                }

                if (result.isGameOver()) {
                    return false;
                }

                currentInput.setLength(0);
                cursorPosition = 0;
                secondsRemaining = TIMELIMIT;
                resumeTimer();
                return true;
            default:
                throw new IllegalStateException("Unexpected value: " + stage);
        }

        return true;
    }
    /**
     * Updates the list of autocomplete suggestions based on the current input string.
     */
    private void updateSuggestions() {
        String prefix = currentInput.toString();
        suggestions.clear();
        if (!prefix.isEmpty()) {
            suggestions = new ArrayList<>(controller.getAutocompleteSuggestions(prefix));
        }
    }
    /**
     * Updates the game screen display, reflecting the current state and input.
     *
     * @throws IOException If there is an issue with screen rendering.
     */
    private void updateScreen() throws IOException {
        synchronized (screen) {
            screen.clear();
            TerminalSize size = screen.getTerminalSize();

            switch (stage) {
                case PLAYER1_NAME:
                    printString(0, 0, "Hi there! Welcome to Movie Game!");
                    printString(0, 2, "Please enter Player 1 name:");
                    printString(0, 4, "> " + currentInput.toString());
                    screen.setCursorPosition(new TerminalPosition(cursorPosition + 2, 4));
                    break;

                case PLAYER2_NAME:
                    printString(0, 0, "Player 1: " + player1Name);
                    printString(0, 2, "Please enter Player 2 name:");
                    printString(0, 4, "> " + currentInput.toString());
                    screen.setCursorPosition(new TerminalPosition(cursorPosition + 2, 4));
                    break;

                case WIN_CONDITION_SELECTION:
                    printString(0, 0, "Player 1: " + player1Name);
                    printString(0, 1, "Player 2: " + player2Name);
                    printString(0, 3, "Please choose a win condition by number:");
                    for (int i = 0; i < winConditions.size(); i++) {
                        printString(2, 4 + i, (i + 1) + ". " + winConditions.get(i).description());
                    }
                    printString(0, 4 + winConditions.size() + 1, "> " + currentInput.toString());
                    screen.setCursorPosition(new TerminalPosition(cursorPosition + 2,
                            4 + winConditions.size() + 1));
                    break;

                case IN_GAME:
                    GameState state = controller.getGameState();

                    // Header
                    printString(0, 0, "Player: " + state.getCurrentPlayer().getName());
                    printString(0, 1, "Round: " + state.getCurrRound());
                    String timerText = "Time: " + secondsRemaining + "s";
                    printString(size.getColumns() - timerText.length(), 0, timerText);
                    printString(0, 2, "Last movie: " +
                            state.getRecentHistory().getLast().getTitle() +
                            " (" + state.getRecentHistory().get(0).getYear() + ")");

                    // Prompt
                    printString(0, 4, "> " + currentInput.toString());

                    // Suggestions
                    int row = 5;
                    for (int i = 0; i < suggestions.size(); i++) {
                        String s = suggestions.get(i);
                        if (i == selectedSuggestionIndex) {
                            printStringColored(2, row++, "> " + s,
                                    TextColor.ANSI.BLACK, TextColor.ANSI.CYAN); // highlighted
                        } else {
                            printString(2, row++, "- " + s); // normal
                        }
                    }

                    int maxWidth = size.getColumns() - 4;          // Leave some margin for indentation

                    // Recent history
                    row++;
                    printString(0, row++, "Recent History (most recent first):");
                    for (Movie m : state.getRecentHistory().reversed()) {
                        String base = m.getTitle() + " (" + m.getYear() + ")";

                        if (m.equals(controller.getGameState().getStartingMovie())) {
                            printString(2, row++, base);
                        } else {
                            String lastConnectionStr = "";
                            if (!m.getConnectionHistory().isEmpty()) {
                                List<Connection> lastConnection = m.getConnectionHistory().getLast();
                                for (Connection c : lastConnection) {
                                    lastConnectionStr += (c.toString() + " ");
                                }
                            }
                            String full = base + " | Last connected via: " + lastConnectionStr.trim();

                            // Manually wrap the text if it's too long
                            while (full.length() > maxWidth) {
                                int cut = full.lastIndexOf(" ", maxWidth);
                                if (cut == -1) cut = maxWidth;
                                printString(2, row++, full.substring(0, cut));
                                full = full.substring(cut).trim();
                            }
                            printString(2, row++, full); // print remaining
                        }
                    }

                    // Player progress
                    row++;
                    Player player = state.getCurrentPlayer();
                    printString(0, row++,  player.getName() + "'s Progress: " +
                            controller.getGameState().getWinCondition().getPlayerProgress(player));

                    screen.setCursorPosition(new TerminalPosition(cursorPosition + 2, 4));
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + stage);
            }

            screen.refresh();
        }
    }
    /**
     * Prints a string to the terminal at the specified coordinates.
     *
     * @param column The column position on the screen.
     * @param row The row position on the screen.
     * @param text The string text to display.
     */
    private void printString(int column, int row, String text) {
        for (int i = 0; i < text.length(); i++) {
            screen.setCharacter(column + i, row,
                    new TextCharacter(text.charAt(i),
                            TextColor.ANSI.WHITE, TextColor.ANSI.BLACK));
        }
    }
    /**
     * Prints a string with custom foreground and background colors at the specified coordinates.
     *
     * @param column The column position on the screen.
     * @param row The row position on the screen.
     * @param text The string text to display.
     * @param fg The foreground color.
     * @param bg The background color.
     */
    private void printStringColored(int column, int row, String text, TextColor fg, TextColor bg) {
        for (int i = 0; i < text.length(); i++) {
            screen.setCharacter(column + i, row,
                    new TextCharacter(text.charAt(i), fg, bg));
        }
    }
    /**
     * Displays an informational message on the screen, pausing the game timer temporarily.
     *
     * @param msg The message to display.
     */
    private void printInfo(String msg) {
        try {
            pauseTimer(); // ⏸ pause the timer while showing info

            screen.clear();

            int maxWidth = screen.getTerminalSize().getColumns();
            List<String> lines = wrapText(msg, maxWidth);

            for (int i = 0; i < lines.size(); i++) {
                printString(0, i, lines.get(i));
            }

            screen.refresh();
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < 3000) {
                KeyStroke key = terminal.pollInput();  // consume input
                if (key != null && key.getKeyType() == KeyType.EOF) {
                    break;
                }
                Thread.sleep(50);
            }

            if (stage == InputStage.IN_GAME) {
                resumeTimer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Wraps long lines of text to fit within a specified maximum width.
     *
     * @param text The text to wrap.
     * @param maxWidth The maximum width of each line.
     * @return A list of strings, each representing a wrapped line.
     */
    // Utility function to wrap long messages
    private List<String> wrapText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (currentLine.length() + word.length() + 1 > maxWidth) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder();
            }
            if (currentLine.length() > 0) {
                currentLine.append(" ");
            }
            currentLine.append(word);
        }
        lines.add(currentLine.toString());
        return lines;
    }

    /**
     * Pauses the game timer, freezing the countdown temporarily.
     */
    private void pauseTimer() {
        timerRunning = false;
    }
    /**
     * Resumes the game timer, continuing the countdown.
     */
    private void resumeTimer() {
        timerRunning = true;
    }

}