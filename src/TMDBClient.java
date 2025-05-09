import java.net.http.*;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.fasterxml.jackson.databind.*;

/**
 * Client for accessing TMDB (The Movie Database) API
 */
public class TMDBClient {
    private static final String BASE_URL = "https://api.themoviedb.org/3";
    private final String apiKey;
    private final HttpClient client;
    private final ObjectMapper mapper;
    /**
     * Constructs a new TMDBClient with API key, HttpClient, and ObjectMapper.
     * The API key is loaded from the configuration.
     */
    public TMDBClient() {
        this.apiKey = ConfigLoader.get("tmdb.api.key");
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }
    /**
     * Fetches a movie by its title from the TMDB API.
     *
     * @param title The title of the movie to search for.
     * @return The Movie object if found; otherwise, returns null.
     */
    public Movie fetchMovieByTitle(String title) {
        try {
            String encoded = URLEncoder.encode(title, StandardCharsets.UTF_8);
            String url = BASE_URL + "/search/movie?query=" + encoded + "&api_key=" + apiKey;

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

            HttpResponse<String> response = client.send(
                    request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = mapper.readTree(response.body());
            JsonNode results = root.path("results");


            if (results.isArray() && results.size() > 0) {
                long id = results.get(0).get("id").asLong();
                return fetchMovieDetailsById(id);
            }
        } catch (Exception e) {
            System.err.println("fetchMovieByTitle error: " + e.getMessage());
        }
        return null;
    }
    /**
     * Fetches detailed information about a movie by its ID from the TMDB API.
     *
     * @param id The TMDB ID of the movie.
     * @return The Movie object with detailed information, or null if not found.
     */
    private Movie fetchMovieDetailsById(long id) {
        try {
            String url = BASE_URL + "/movie/" + id +
                    "?api_key=" + apiKey + "&append_to_response=credits";
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

            HttpResponse<String> response = client.send(
                    request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = mapper.readTree(response.body());
            String title = root.path("title").asText();
            int year = Integer.parseInt(root.path("release_date").asText().split("-")[0]);

            Set<String> genres = new HashSet<>();
            for (JsonNode genre : root.path("genres")) {
                genres.add(genre.path("name").asText());
            }

            Set<String> actors = new HashSet<>();
            for (JsonNode cast : root.path("credits").path("cast")) {
                if (actors.size() >= 5) {
                    break;
                }
                actors.add(cast.path("name").asText());
            }

            Set<String> directors = new HashSet<>();
            for (JsonNode crew : root.path("credits").path("crew")) {
                if ("Director".equals(crew.path("job").asText())) {
                    directors.add(crew.path("name").asText());
                }
            }


            return new Movie(id, title, year, genres, actors, directors,
                    Set.of(), Set.of(), Set.of());
        } catch (Exception e) {
            System.err.println("fetchMovieDetailsById error: " + e.getMessage());
        }
        return null;
    }
    public List<Movie> fetchSimilarMovies(Movie movie) {
        List<Movie> list = new ArrayList<>();
        try {
            long movieId = movie.getMovieId();
            String url = BASE_URL + "/movie/" + movieId + "/similar?api_key=" + apiKey;
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = client.send(
                    request, HttpResponse.BodyHandlers.ofString());

            JsonNode results = mapper.readTree(response.body()).path("results");
            for (JsonNode node : results) {
                long id = node.get("id").asLong();
                Movie similar = fetchMovieDetailsById(id);
                if (similar != null) {
                    list.add(similar);
                }
            }
        } catch (Exception e) {
            System.err.println("fetchSimilarMovies error: " + e.getMessage());
        }
        return list;
    }
    public List<Movie> fetchMoviesByActor(String actorName) {
        List<Movie> movies = new ArrayList<>();
        try {
            String encoded = URLEncoder.encode(actorName, StandardCharsets.UTF_8);
            String searchUrl = BASE_URL + "/search/person?query=" +
                    encoded + "&api_key=" + apiKey;
            HttpRequest searchRequest = HttpRequest.newBuilder().uri(URI.create(searchUrl)).build();
            HttpResponse<String> searchResponse = client.send(
                    searchRequest, HttpResponse.BodyHandlers.ofString());

            JsonNode results = mapper.readTree(searchResponse.body()).path("results");
            if (results.size() == 0) {
                return movies;
            }

            long personId = results.get(0).get("id").asLong();

            String creditsUrl = BASE_URL + "/person/" + personId +
                    "/movie_credits?api_key=" + apiKey;
            HttpRequest creditsRequest = HttpRequest.newBuilder().uri(
                    URI.create(creditsUrl)).build();
            HttpResponse<String> creditsResponse = client.send(
                    creditsRequest, HttpResponse.BodyHandlers.ofString());

            JsonNode cast = mapper.readTree(creditsResponse.body()).path("cast");
            for (JsonNode movieNode : cast) {
                long movieId = movieNode.get("id").asLong();
                Movie m = fetchMovieDetailsById(movieId);
                if (m != null) {
                    movies.add(m);
                }
            }

        } catch (Exception e) {
            System.err.println("fetchMoviesByActor error: " + e.getMessage());
        }
        return movies;
    }
    public List<Movie> fetchPopularMovies(int maxPages) {
        List<Movie> popular = new ArrayList<>();
        try {
            for (int page = 1; page <= maxPages; page++) {
                String url = BASE_URL + "/movie/popular?api_key=" + apiKey + "&page=" + page;
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
                HttpResponse<String> response = client.send(request,
                        HttpResponse.BodyHandlers.ofString());
                JsonNode results = mapper.readTree(response.body()).path("results");

                for (JsonNode node : results) {
                    long id = node.get("id").asLong();
                    Movie movie = fetchMovieDetailsById(id);
                    if (movie != null) {
                        popular.add(movie);
                    }
                }

                // TMDB only allows up to 500 results (25 pages * 20 movies)
                if (results.isEmpty()) {
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("fetchPopularMovies error: " + e.getMessage());
        }
        return popular;
    }
}

