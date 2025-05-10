import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

public class WinConditionTest {

    private Player player;

    @Before
    public void setUp() {
        player = new Player("TestPlayer");
    }

    /**
     * Helper method to create a Horror movie
     */
    private Movie makeHorrorMovie(int id) {
        return new Movie(id, "Horror Movie " + id, 2000,
            Set.of("Horror"),
            Set.of(),
            Set.of(),
            Set.of(),
            Set.of(),
            Set.of());
    }

    /**
     * Helper method to create a Non-Horror movie
     */
    private Movie makeNonHorrorMovie(int id) {
        return new Movie(id, "Non-Horror Movie " + id, 2000,
            Set.of("Action"),
            Set.of(),
            Set.of(),
            Set.of(),
            Set.of(),
            Set.of());
    }

    /**
     * Helper method to create a Christopher Nolan movie
     */
    private Movie makeNolanMovie(int id) {
        return new Movie(id, "Nolan Movie " + id, 2000,
            Set.of("Sci-Fi"),
            Set.of(),
            Set.of("Christopher Nolan"),
            Set.of(),
            Set.of(),
            Set.of());
    }

    /**
     * Tests for TwoHorrorMoviesWin
     */
    @Test
    public void testTwoHorrorMoviesWin_NotEnoughMovies() {
        player.addGuessedMovie(makeHorrorMovie(1));
        player.addGuessedMovie(makeNonHorrorMovie(2));

        TwoHorrorMoviesWin winCondition = new TwoHorrorMoviesWin();
        assertFalse("Should not win with less than 2 horror movies.", winCondition.checkVictory(player));
    }

    @Test
    public void testTwoHorrorMoviesWin_ExactMovies() {
        TwoHorrorMoviesWin winCondition = new TwoHorrorMoviesWin();

        Movie horror1 = makeHorrorMovie(1);
        Movie horror2 = makeHorrorMovie(2);

        player.addGuessedMovie(horror1);
        winCondition.updatePlayerProgress(player, horror1);

        player.addGuessedMovie(horror2);
        winCondition.updatePlayerProgress(player, horror2);
        assertTrue("Should win after guessing exactly 2 horror movies.", winCondition.checkVictory(player));
    }


    @Test
    public void testTwoHorrorMoviesWin_MoreThanTwoMovies() {
        TwoHorrorMoviesWin winCondition = new TwoHorrorMoviesWin();

        Movie horror1 = makeHorrorMovie(1);
        Movie horror2 = makeHorrorMovie(2);
        Movie horror3 = makeHorrorMovie(3);

        player.addGuessedMovie(horror1);
        winCondition.updatePlayerProgress(player, horror1);

        player.addGuessedMovie(horror2);
        winCondition.updatePlayerProgress(player, horror2);

        player.addGuessedMovie(horror3);
        winCondition.updatePlayerProgress(player, horror3);

        System.out.println("Player progress: " + player.getProgress()); // 應該是 3

        assertTrue("Should win even if more than 2 horror movies are guessed.", winCondition.checkVictory(player));
    }


    @Test
    public void testTwoHorrorMoviesWin_Description() {
        TwoHorrorMoviesWin winCondition = new TwoHorrorMoviesWin();
        assertEquals("Win by guessing two horror movies!", winCondition.description());
    }

    @Test
    public void testTwoHorrorMoviesWin_Progress() {
        TwoHorrorMoviesWin winCondition = new TwoHorrorMoviesWin();
        player.addGuessedMovie(makeHorrorMovie(1));
        winCondition.updatePlayerProgress(player, makeHorrorMovie(1));
        assertEquals("1/2", winCondition.getPlayerProgress(player));
    }

    /**
     * Tests for TwoNolanMoviesWin
     */
    @Test
    public void testTwoNolanMoviesWin_ExactMovies() {
        // Arrange
        TwoNolanMoviesWin winCondition = new TwoNolanMoviesWin();
        Movie nolanMovie1 = makeNolanMovie(1);
        Movie nolanMovie2 = makeNolanMovie(2);

        // Add movies to the player's guessed list
        player.addGuessedMovie(nolanMovie1);
        player.addGuessedMovie(nolanMovie2);

        // Act - update progress manually
        winCondition.updatePlayerProgress(player, nolanMovie1);
        winCondition.updatePlayerProgress(player, nolanMovie2);

        // Assert
        assertTrue("Should win after guessing exactly 2 Nolan movies.", winCondition.checkVictory(player));
    }

    @Test
    public void testTwoNolanMoviesWin_MoreThanTwoMovies() {
        // Arrange
        TwoNolanMoviesWin winCondition = new TwoNolanMoviesWin();
        Movie nolanMovie1 = makeNolanMovie(1);
        Movie nolanMovie2 = makeNolanMovie(2);
        Movie nolanMovie3 = makeNolanMovie(3);
        Movie nolanMovie4 = makeNolanMovie(4);

        // Add movies to the player's guessed list
        player.addGuessedMovie(nolanMovie1);
        player.addGuessedMovie(nolanMovie2);
        player.addGuessedMovie(nolanMovie3);
        player.addGuessedMovie(nolanMovie4);

        // Act - update progress manually
        winCondition.updatePlayerProgress(player, nolanMovie1);
        winCondition.updatePlayerProgress(player, nolanMovie2);
        winCondition.updatePlayerProgress(player, nolanMovie3);
        winCondition.updatePlayerProgress(player, nolanMovie4);

        // Assert
        assertTrue("Should win even if more than 2 Nolan movies are guessed.", winCondition.checkVictory(player));
    }
    @Test
    public void testTwoNolanMoviesWin_Description() {
        // Arrange
        TwoNolanMoviesWin winCondition = new TwoNolanMoviesWin();

        // Act & Assert
        assertEquals("Win by guessing two movies directed by Christopher Nolan!", winCondition.description());
    }

    @Test
    public void testTwoNolanMoviesWin_Progress() {
        // Arrange
        TwoNolanMoviesWin winCondition = new TwoNolanMoviesWin();
        player.addGuessedMovie(makeNolanMovie(1));
        winCondition.updatePlayerProgress(player, makeNolanMovie(1));

        // Act & Assert
        assertEquals("1/2", winCondition.getPlayerProgress(player));
    }
}
