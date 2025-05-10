import java.io.IOException;

/**
 * Main entry point for launching the Movie Game application.
 * It initializes the game controller with the API key, preloads popular movies,
 * and starts the game view.
 *
 * @param args Command-line arguments (not used in this application).
 */
public class Main {
    public static void main(String[] args) {
        String apiKey = ConfigLoader.get("tmdb.api.key");
        GameController controller = new GameController(apiKey);
        controller.getMovieDatabase().preloadPopularMovies();

        try {
            new GameView(controller).run();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
