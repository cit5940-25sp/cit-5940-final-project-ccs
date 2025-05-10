import java.util.HashSet;
import java.util.Set;

/**
 * Represents a player in the Movie Name Game.
 * Each player has a name and a record of correctly guessed movies.
 */
public class Player {
    private String name;
    private Set<Movie> moviesGuessed;
    private int progress;

    /**
     * Constructs a Player with the specified name.
     *
     * @param name the name of the player
     */
    public Player(String name) {
        this.name = name;
        this.moviesGuessed = new HashSet<>();
        this.progress = 0;
    }

    /**
     * Adds a correctly guessed movie to the player's record.
     *
     * @param movie the movie to add
     */
    public void addGuessedMovie(Movie movie) {
        moviesGuessed.add(movie);
    }

    // ======== Getters ========
    /**
     * Returns the player's name.
     *
     * @return the name of the player
     */
    public String getName() {
        return name;
    }
    /**
     * Returns an unmodifiable set of all movies guessed by the player.
     *
     * @return set of guessed movies
     */
    public Set<Movie> getMoviesGuessed() {
        return Set.copyOf(moviesGuessed); // creates an unmodifiable Set
    }

    /**
     * Returns the number of correctly guessed movies.
     *
     * @return the number of guessed movies
     */
    public int getNumMoviesGuessed() {
        return moviesGuessed.size();
    }
    /**
     * Returns the current progress count for the player.
     *
     * @return the player's progress
     */
    public int getProgress() {
        return progress;
    }
    /**
     * Increments the player's progress counter by 1.
     */
    public void updateProgress() {
        progress++;
    }
}