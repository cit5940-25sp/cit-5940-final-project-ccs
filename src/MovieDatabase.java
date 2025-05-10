import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.*;
/**
 * Manages movie data from TMDB and provides caching, lookup, and autocomplete functionality.
 */
public class MovieDatabase {
    private final TMDBClient tmdb;
    private final Map<String, Movie> movieCache = new HashMap<>();
    private final Map<String, List<Movie>> actorCache = new HashMap<>();
    private final Map<Long, List<Movie>> similarCache = new HashMap<>();
    private final Autocomplete autocompleteEngine = new Autocomplete();
    /**
     * Constructs a MovieDatabase object and initializes the TMDB client and autocomplete engine.
     *
     * @param apiKey The API key for accessing the TMDB API.
     */
    public MovieDatabase(String apiKey) {
        this.tmdb = new TMDBClient();
        autocompleteEngine.setSuggestionLimit(5);
    }
    /**
     * Searches for a movie by its title. If the movie is found in the local cache, it is returned.
     * Otherwise, it queries the TMDB API, caches the result, and returns the movie.
     *
     * @param title The title of the movie to search for.
     * @return The Movie object if found; otherwise, null.
     */
    public Movie findByTitle(String title) {
        if (movieCache.containsKey(title.toLowerCase().trim())) {
            return movieCache.get(title);
        }

        Movie movie = tmdb.fetchMovieByTitle(title);
        if (movie != null) {
            movieCache.put(title, movie);
        }

        return movie;
    }
    /**
     * Preloads a list of popular movies from the TMDB API or from a local cache file.
     * The data is stored in the local cache and the autocomplete engine is populated.
     */
    public void preloadPopularMovies() {
        File cacheFile = new File("movie_cache.json");
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        List<Movie> popular;

        if (cacheFile.exists()) {
            // Load from JSON cache
            try {
                Movie[] cached = mapper.readValue(cacheFile, Movie[].class);
                popular = Arrays.asList(cached);
                populateAutocompleteEngine(autocompleteEngine, popular);
                for (Movie movie : popular) {
                    movieCache.put(movie.getTitle(), movie);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Fetch from TMDB and write to cache
            int maxPages = 25;
            popular = tmdb.fetchPopularMovies(maxPages);
            populateAutocompleteEngine(autocompleteEngine, popular);
            for (Movie movie : popular) {
                movieCache.put(movie.getTitle(), movie);
            }

            try {
                mapper.writeValue(cacheFile, popular);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Populates the autocomplete engine with movie titles for quick suggestions.
     *
     * @param autocompleteEngine The autocomplete engine to populate.
     * @param movies The list of movies to insert into the autocomplete engine.
     */
    private void populateAutocompleteEngine(Autocomplete autocompleteEngine, List<Movie> movies) {
        for (Movie movie : movies) {
            autocompleteEngine.insert(movie.getTitle(), 0);
        }
    }
    /**
     * Retrieves the autocomplete engine instance associated with the movie database.
     *
     * @return The Autocomplete engine used for providing movie title suggestions.
     */
    public Autocomplete getAutocompleteEngine() {
        return autocompleteEngine;
    }
    /**
     * Retrieves a random movie from the movie cache. If the cache is empty, it attempts
     * to preload popular movies first.
     *
     * @return A randomly selected Movie object, or null if the cache is empty.
     */
    public Movie getRandomMovie() {
        if (movieCache.isEmpty()) {
            preloadPopularMovies();
        }

        if (movieCache.isEmpty()) {
            return null;
        }

        List<Movie> all = new ArrayList<>(movieCache.values());
        return all.get(new Random().nextInt(all.size()));
    }
}
