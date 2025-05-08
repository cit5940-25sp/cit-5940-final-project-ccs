import java.util.ArrayList;
import java.util.List;

/**
 * Controls the overall game flow, coordinating interactions between
 * the model (GameState, MovieDatabase), and the view (GameView).
 */
public class GameController {
    private GameState gameState;
    private GameView view;
    private MovieDatabase movieDb;

    /**
     * Constructs a GameController with the specified API key.
     *
     * @param apiKey the API key for the TMDB service
     */
    public GameController(String apiKey) {
        this.movieDb = new MovieDatabase(apiKey);
        this.view = new GameView();
    }

    GameController(MovieDatabase db, GameView view) {
        this.movieDb = db;
        this.view = view;
    }

    GameState getGameState() {
        return gameState;
    }
    GameView getView() {
        return view;
    }
    public MovieDatabase getMovieDatabase() {
        return movieDb;
    }
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
    public Movie startGame(String p1, String p2, WinCondition cond){
        // Create players
        Player player1 = new Player(p1);
        Player player2 = new Player(p2);

        Movie startingMovie = movieDb.getRandomMovie();
        
        // Make sure we have a valid starting movie
        if (startingMovie == null) {
            view.displayInfo("Could not find a starting movie. Please check your database connection.");
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

        System.out.println("Guessed: " + guessedMovie);

        if (gameState.isMovieUsed(guessedMovie)) {
            return new TurnResult(false, "Nice try! However movie " + movieTitle + " already used");
        }

        Movie lastMovie = gameState.getCurrentMovie();
        List<Connection> connections = lastMovie.findConnections(guessedMovie);

        if (connections.isEmpty()) {
            return new TurnResult(false,
                    "Oops, no valid connection found between " + lastMovie.getTitle() + " and " + guessedMovie.getTitle());
        }

        List<Connection> validConnections = gameState.filterConnection(connections);

        if (validConnections.isEmpty()) { // there are connections but the connecting people have been used more than 3 times
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

        String msg = "Nice! " + lastMovie.getTitle() + " and " + guessedMovie.getTitle() + " connected via " +
                validConnStr;

        if (gameState.hasCurrentPlayerWon()) {
            return new TurnResult(true, true,"Congrats! " + currentPlayer.getName() + " has won the game with condition " + gameState.getWinCondition());
        }

        gameState.switchPlayer();
        return new TurnResult(true, msg);
    }

    public List<String> getAutocompleteSuggestions(String input) {
        List<String> results = new ArrayList<>();
        for (Term title : movieDb.getAutocompleteEngine().suggest(input)) {
            results.add(title.getTerm());
        }
        return results;
    }
}
