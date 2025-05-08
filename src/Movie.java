import java.util.*;

/**
 * Represents a movie in the database, storing metadata such as title, year,
 * and associated people (actors, directors, etc.).
 */
public class Movie {
    private long movieId;
    private String title;
    private int year;
    private Set<String> genres;
    private Set<String> actors;
    private Set<String> directors;
    private Set<String> writers;
    private Set<String> composers;
    private Set<String> cinematographers;
    private List<Connection> connectionHistory;

    public Movie() {
        this.genres = new HashSet<>();
        this.actors = new HashSet<>();
        this.directors = new HashSet<>();
        this.writers = new HashSet<>();
        this.composers = new HashSet<>();
        this.cinematographers = new HashSet<>();
        this.connectionHistory = new ArrayList<>();
    }

    /**
     * Constructor for full metadata
     */
    public Movie(long movieId, String title, int year,
                 Set<String> genres,
                 Set<String> actors,
                 Set<String> directors,
                 Set<String> writers,
                 Set<String> composers,
                 Set<String> cinematographers) {
        this.movieId = movieId;
        this.title = title;
        this.year = year;
        this.genres = (genres != null) ? genres : new HashSet<>();
        this.actors = (actors != null) ? actors : new HashSet<>();
        this.directors = (directors != null) ? directors : new HashSet<>();
        this.writers = (writers != null) ? writers : new HashSet<>();
        this.composers = (composers != null) ? composers : new HashSet<>();
        this.cinematographers = (cinematographers != null) ? cinematographers : new HashSet<>();
        this.connectionHistory = new ArrayList<>();
    }

    /**
     * Finds all shared connections between this movie and another movie.
     *
     * @param other the other movie to compare with
     * @return a list of connections (person + type) that connect the two movies
     */
    public List<Connection> findConnections(Movie other) {
        List<Connection> connections = new ArrayList<>();

        for (String actor : actors) {
            if (other.actors.contains(actor)) {
                connections.add(new Connection(actor, ConnectionType.ACTOR));
            }
        }

        for (String director : directors) {
            if (other.directors.contains(director)) {
                connections.add(new Connection(director, ConnectionType.DIRECTOR));
            }
        }

        for (String writer : writers) {
            if (other.writers.contains(writer)) {
                connections.add(new Connection(writer, ConnectionType.WRITER));
            }
        }

        for (String composer : composers) {
            if (other.composers.contains(composer)) {
                connections.add(new Connection(composer, ConnectionType.COMPOSER));
            }
        }

        for (String cinematographer : cinematographers) {
            if (other.cinematographers.contains(cinematographer)) {
                connections.add(new Connection(cinematographer, ConnectionType.CINEMATOGRAPHER));
            }
        }

        return connections;
    }

    public void addConnectionHistory(List<Connection> connections) {
        connectionHistory.addAll(connections);
    }

    // ======== Getters ========

    public long getMovieId() {
        return movieId;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public Set<String> getGenres() {
        return genres;
    }

    public Set<String> getActors() {
        return actors;
    }

    public Set<String> getDirectors() {
        return directors;
    }

    public Set<String> getWriters() {
        return writers;
    }

    public Set<String> getComposers() {
        return composers;
    }

    public Set<String> getCinematographers() {
        return cinematographers;
    }


    public List<Connection> getConnectionHistory() {
        return connectionHistory;
    }

    @Override
    public String toString() {
        String actors = "";
        for (String actor: getActors()) {
            actors += actor + " ";
        }
        if (actors.isEmpty()) {
            actors = "no actors fetched";
        }
        String genres = "";
        for (String genre: getGenres()) {
            genres += genre + " ";
        }
        if (genres.isEmpty()) {
            genres = "no genre fetched";
        }
        return getTitle() + " (" + getYear() + ") " +  "\nactors:" + actors + "\ngenres:" + genres;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Movie other = (Movie) obj;
        return year == other.year &&
                title != null &&
                other.title != null &&
                title.equalsIgnoreCase(other.title);  // case-insensitive match
    }

    @Override
    public int hashCode() {
        return Objects.hash(title == null ? 0 : title.toLowerCase(), year);
    }

}