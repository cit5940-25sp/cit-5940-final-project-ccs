import java.util.Set;

/**
 * A win condition where the player wins after guessing three movies
 * directed by Christopher Nolan.
 */
public class ThreeNolanMoviesWin implements WinCondition {

    private static final String TARGET_DIRECTOR = "Christopher Nolan";
    private static final int REQUIRED_COUNT = 3;

    /**
     * Checks if the player has guessed at least three movies directed by Christopher Nolan.
     *
     * @param player the player to evaluate
     * @return true if the player has guessed three or more Nolan movies; false otherwise
     */
    @Override
    public boolean checkVictory(Player player) {
        int nolanCount = 0;
        Set<Movie> guessedMovies = player.getMoviesGuessed(); // Assumes getter exists

        for (Movie movie : guessedMovies) {
            if (movie.getDirectors().contains(TARGET_DIRECTOR)) {
                nolanCount++;
            }
            if (nolanCount >= REQUIRED_COUNT) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a description of this win condition.
     *
     * @return a string describing the win condition
     */
    @Override
    public String description() {
        return "Win by guessing three movies directed by Christopher Nolan!";
    }

    @Override
    public void updatePlayerProgress(Player player, Movie movie) {
//        if (movie.getGenres().contains(TARGET_DIRECTOR)) {
//            player.updateProgress();
//        }
        if (movie.getDirectors().contains(TARGET_DIRECTOR)) {
            //System.out.println("Nolan movie found: " + movie.getTitle());
            player.updateProgress();
        }
    }

    @Override
    public String getPlayerProgress(Player player) {
        return player.getProgress() + "/" + REQUIRED_COUNT;
    }
}
