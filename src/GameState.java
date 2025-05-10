import java.util.*;

/**
 * Maintains the current state of the game including players, history,
 * active player, round, connection usage, and win condition.
 */
public class GameState {
    private final Player player1;
    private final Player player2;
    private final Movie startingMovie;
    private Player currentPlayer;
    private int currRound;
    private final WinCondition winCondition;
    private final List<Movie> history;
    private final Set<Movie> usedMovies;
    private final Map<String, Integer> connectionUsage;

    public GameState(Player player1, Player player2,
                     WinCondition winCondition, Movie startingMovie) {
        this.player1 = player1;
        this.player2 = player2;
        this.startingMovie = startingMovie;
        this.currentPlayer = player1; // Start with player1
        this.currRound = 1;
        this.winCondition = winCondition;
        this.history = new ArrayList<>();
        this.usedMovies = new HashSet<>();
        this.connectionUsage = new HashMap<>();

        addMovieToHistory(startingMovie); // First movie played
        currentPlayer.addGuessedMovie(startingMovie);
    }

    /**
     * Adds a movie to the game's history and marks it as used.
     *
     * @param movie the movie to add
     */
    public void addMovieToHistory(Movie movie) {
        history.add(movie);
        usedMovies.add(movie);
    }

    /**
     * Retrieves the most recent five movies in play history.
     *
     * @return list of recently played movies
     */
    public List<Movie> getRecentHistory() {
        int fromIndex = Math.max(0, history.size() - 5);
        return history.subList(fromIndex, history.size());
    }

    /**
     * Checks if a movie has already been used in the game.
     *
     * @param movie the movie to check
     * @return true if the movie has been used, false otherwise
     */
    public boolean isMovieUsed(Movie movie) {
        return usedMovies.contains(movie);
    }

    /**
     * Switches the turn to the next player.
     */
    public void switchPlayer() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
        if (currentPlayer == player1) {
            currRound++;
        }
    }

    /**
     * Increments the usage count of a given connection (person name).
     *
     * @param person the person involved in the connection
     */
    public void incrementConnectionUsage(String person) {
        connectionUsage.put(person, connectionUsage.getOrDefault(person, 0) + 1);
    }

    /**
     * Checks if a person can still be used as a connection (limit is 3 times).
     *
     * @param connections the list of connections
     * @return true if usage count < 3, false otherwise
     */
    public List<Connection> filterConnections(List<Connection> connections) {
        List<Connection> canUse = new ArrayList<>();

        for (Connection con: connections) {
            if (connectionUsage.getOrDefault(con.getPersonName(), 0) < 3) {
                canUse.add(con);
                int count = connectionUsage.getOrDefault(con.getPersonName(), 0);
                connectionUsage.put(con.getPersonName(), count + 1);
            }
        }
        return canUse;
    }

    /**
     * Returns the starting movie used to begin the game.
     *
     * @return the starting Movie object
     */
    public Movie getStartingMovie() {
        return startingMovie;
    }

    /**
     * Returns the current active player.
     *
     * @return the player whose turn it is
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Returns the other player.
     *
     * @return the player who is not currently active
     */
    public Player getOtherPlayer() {
        return (currentPlayer == player1) ? player2 : player1;
    }

    /**
     * Returns the current round number.
     *
     * @return current round count
     */
    public int getCurrRound() {
        return currRound;
    }

    /**
     * Returns the movie last played.
     *
     * @return most recently added movie
     */
    public Movie getCurrentMovie() {
        if (history.isEmpty()) {
            return null;
        }
        return history.get(history.size() - 1);
    }

    /**
     * Checks if the current player has met the win condition.
     *
     * @return true if the current player won, false otherwise
     */
    public boolean hasCurrentPlayerWon() {
        return winCondition.checkVictory(currentPlayer);
    }

    /**
     * Returns the active win condition.
     *
     * @return win condition object
     */
    public WinCondition getWinCondition() {
        return winCondition;
    }


}