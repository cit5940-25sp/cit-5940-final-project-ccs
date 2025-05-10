/**
 * A win condition where the player wins after guessing two horror movies.
 */
public class TwoHorrorMoviesWin implements WinCondition {

    private static final String TARGET_GENRE = "Horror";
    private static final int REQUIRED_COUNT = 2;

    /**
     * Checks if the player has guessed at least two movies of the horror genre.
     *
     * @param player the player to evaluate
     * @return true if the player has guessed two or more horror movies; false otherwise
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
        return "Win by guessing two horror movies!";
    }
    /**
     * Updates the player's progress if the specified movie belongs to the target genre.
     *
     * @param player The player whose progress is being updated.
     * @param movie The movie that the player has guessed.
     */
    @Override
    public void updatePlayerProgress(Player player, Movie movie) {
        if (movie.getGenres().contains(TARGET_GENRE)) {
            player.updateProgress();
        }
    }
    /**
     * Retrieves the player's current progress towards the required movie count.
     *
     * @param player The player whose progress is being queried.
     * @return A formatted string representing the player's progress as "current/required".
     */
    @Override
    public String getPlayerProgress(Player player) {
        return player.getProgress() + "/" + REQUIRED_COUNT;
    }
}
