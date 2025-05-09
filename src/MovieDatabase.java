import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MovieDatabase {
    private final TMDBClient tmdb;
    private final Map<String, Movie> movieCache = new HashMap<>();
    private final Map<String, List<Movie>> actorCache = new HashMap<>();
    private final Map<Long, List<Movie>> similarCache = new HashMap<>();
    private final Autocomplete autocompleteEngine = new Autocomplete();

    public MovieDatabase(String apiKey) {
        this.tmdb = new TMDBClient();
        autocompleteEngine.setSuggestionLimit(5);
    }

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
                System.out.println("Loaded " + popular.size() + " movies from local cache.");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to load cache file. " + e.getMessage());
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
                System.out.println("Fetched movies, but failed to write cache. " + e.getMessage());
            }

            System.out.println("Fetched and cached " + popular.size() + " movies.");
        }
    }

    private void populateAutocompleteEngine(Autocomplete autocompleteEngine, List<Movie> movies) {
        for (Movie movie : movies) {
            autocompleteEngine.insert(movie.getTitle(), 0);
        }
        System.out.println("Populated autocomplete engine with " + movies.size() + " movies.");
    }

    public Autocomplete getAutocompleteEngine() {
        return autocompleteEngine;
    }

    public Movie getRandomMovie() {
        if (movieCache.isEmpty()) {
            preloadPopularMovies();
        }

        if (movieCache.isEmpty()) return null;

        List<Movie> all = new ArrayList<>(movieCache.values());
        return all.get(new Random().nextInt(all.size()));
    }
}
