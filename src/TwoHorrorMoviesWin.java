/**
 * A win condition where the player wins after guessing two horror movies.
 */
public class TwoHorrorMoviesWin implements WinCondition {

    private static final String TARGET_GENRE = "Horror";
    private static final int REQUIRED_COUNT = 2;

    /**
     * Checks if the player has guessed at least five movies of the horror genre.
     *
     * @param player the player to evaluate
     * @return true if the player has guessed five or more horror movies; false otherwise
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

    @Override
    public void updatePlayerProgress(Player player, Movie movie) {
        if (movie.getGenres().contains(TARGET_GENRE)) {
            // System.out.println("Horror movie found: " + movie.getTitle());
            player.updateProgress();
        }
    }

    @Override
    public String getPlayerProgress(Player player) {
        return player.getProgress() + "/" + REQUIRED_COUNT;
    }
}
