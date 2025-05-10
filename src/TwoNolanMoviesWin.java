
/**
 * A win condition where the player wins after guessing three movies
 * directed by Christopher Nolan.
 */
public class TwoNolanMoviesWin implements WinCondition {

    private static final String TARGET_DIRECTOR = "Christopher Nolan";
    private static final int REQUIRED_COUNT = 2;

    /**
     * Checks if the player has guessed at least two movies directed by Christopher Nolan.
     *
     * @param player the player to evaluate
     * @return true if the player has guessed two or more Nolan movies; false otherwise
     */
    @Override
    public boolean checkVictory(Player player) {
        return player.getProgress() >= REQUIRED_COUNT;
    }

    /**
     * Returns a description of this win condition.
     *
     * @return a string describing the win condition
     */
    @Override
    public String description() {
        return "Win by guessing two movies directed by Christopher Nolan!";
    }
    /**
     * Updates the player's progress if the guessed movie is directed by Christopher Nolan.
     *
     * @param player The player whose progress is being updated.
     * @param movie The movie that the player has guessed.
     */
    @Override
    public void updatePlayerProgress(Player player, Movie movie) {

        if (movie.getDirectors().contains(TARGET_DIRECTOR)) {
            player.updateProgress();
        }
    }
    /**
     * Retrieves the player's current progress towards guessing two Nolan movies.
     *
     * @param player The player whose progress is being queried.
     * @return A string representing the player's progress in the format "current/required".
     */
    @Override
    public String getPlayerProgress(Player player) {
        return player.getProgress() + "/" + REQUIRED_COUNT;
    }
}
