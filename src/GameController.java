import java.util.ArrayList;
import java.util.List;

/**
 * Controls the overall game flow, coordinating interactions between
 * the model (GameState, MovieDatabase), and the view (GameView).
 */
public class GameController {
    private GameState gameState;
    private MovieDatabase movieDb;

    /**
     * Constructs a GameController with the specified API key.
     *
     * @param apiKey the API key for the TMDB service
     */
    public GameController(String apiKey) {
        this.movieDb = new MovieDatabase(apiKey);

    }
    /**
     * Constructs a GameController with a given MovieDatabase instance (for testing or custom config).
     *
     * @param db The MovieDatabase instance to use.
     */
    GameController(MovieDatabase db) {
        this.movieDb = db;
    }

    /**
     * Retrieves the current game state, which includes information about
     * players, the current turn, movie history, and the win condition.
     *
     * @return the current GameState object.
     */
    GameState getGameState() {
        return gameState;
    }

    /**
     * Returns the MovieDatabase instance used by the game controller.
     * This provides access to movie data, search, and autocomplete functionality.
     *
     * @return the MovieDatabase associated with this controller.
     */
    public MovieDatabase getMovieDatabase() {
        return movieDb;
    }

    /**
     * Sets the game state to the specified GameState object.
     * Useful for initializing or restoring a game session.
     *
     * @param state the GameState to set as the current state.
     */
    void setGameState(GameState state) {
        this.gameState = state;
    }

    /**
     * Starts a new game session with the specified players and win condition.
     *
     * @param p1   name of Player 1
     * @param p2   name of Player 2
     * @param cond the win condition strategy
     */
    public Movie startGame(String p1, String p2, WinCondition cond) {
        // Create players
        Player player1 = new Player(p1);
        Player player2 = new Player(p2);

        Movie startingMovie = movieDb.getRandomMovie();

        // Make sure we have a valid starting movie
        if (startingMovie == null) {
            return null;
        }
        
        // Initialize game state with players, win condition, and starting movie
        gameState = new GameState(player1, player2, cond, startingMovie);
        // Display initial game state
        return startingMovie;
    }

    /**
     * Processes a player's turn using the guessed movie title.
     *
     * @param movieTitle the title of the movie guessed
     */
    public TurnResult processTurn(String movieTitle) {

        if (movieTitle == null || movieTitle.trim().isEmpty()) {
            return new TurnResult(false, "Movie title cannot be empty.");
        }

        Player currentPlayer = gameState.getCurrentPlayer();
        Movie guessedMovie = movieDb.findByTitle(movieTitle);

        if (guessedMovie == null) {
            return new TurnResult(false, "Oops, " + movieTitle + " is not found in the database.");
        }

        if (gameState.isMovieUsed(guessedMovie)) {
            return new TurnResult(false, "Nice try! However movie " + movieTitle + " already used");
        }

        Movie lastMovie = gameState.getCurrentMovie();
        List<Connection> connections = lastMovie.findConnections(guessedMovie);

        if (connections.isEmpty()) {
            return new TurnResult(false,
                    "Oops, no valid connection found between " + lastMovie.getTitle() + " and " +
                            guessedMovie.getTitle());
        }

        List<Connection> validConnections = gameState.filterConnections(connections);

        if (validConnections.isEmpty()) {
            String connectionStr = "";
            for (Connection con: connections) {
                connectionStr += (con.getPersonName() + " ");
            }
            return new TurnResult(false,
                    "Nice Try! However " + connectionStr + " has already been used 3 times.");
        }

        // Valid move

        guessedMovie.addConnectionHistory(validConnections);
        gameState.addMovieToHistory(guessedMovie);
        currentPlayer.addGuessedMovie(guessedMovie);
        gameState.getWinCondition().updatePlayerProgress(currentPlayer, guessedMovie);

        String validConnStr = "";
        for (Connection con: validConnections) {
            validConnStr += (con.getPersonName() + " (" + con.getType() + ") ");
        }

        String msg = "Nice! " + lastMovie.getTitle() + " and " + guessedMovie.getTitle()
                + " connected via " +
                validConnStr;

        if (gameState.hasCurrentPlayerWon()) {
            return new TurnResult(true, true,"Congrats " + currentPlayer.getName() + ", you won!");
        }

        gameState.switchPlayer();
        return new TurnResult(true, msg);
    }
    /**
     * Retrieves a list of autocomplete suggestions based on the given input string.
     * It queries the autocomplete engine from the movie database to find matching titles.
     *
     * @param input The partial input string for which to fetch autocomplete suggestions.
     * @return A list of suggested movie titles that match the input string.
     */
    public List<String> getAutocompleteSuggestions(String input) {
        List<String> results = new ArrayList<>();
        for (Term title : movieDb.getAutocompleteEngine().suggest(input)) {
            results.add(title.getTerm());
        }
        return results;
    }
}
