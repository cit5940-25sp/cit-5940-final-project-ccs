import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class GameStateTest {
    private GameState gameState;
    private Player player1;
    private Player player2;
    private Movie startingMovie;
    private WinCondition winCondition;

    @Before
    public void setUp() {
        player1 = new Player("Alice");
        player2 = new Player("Bob");
        startingMovie = new Movie(1L, "The Godfather", 1972,
            Set.of(), Set.of("Al Pacino"), Set.of(), Set.of(), Set.of(), Set.of());
        winCondition = new TwoHorrorMoviesWin();
        gameState = new GameState(player1, player2, winCondition, startingMovie);
    }

    @Test
    public void testInitialization() {
        // Verify initial state
        assertEquals(player1, gameState.getCurrentPlayer());
        assertEquals(1, gameState.getCurrRound());
        assertEquals(startingMovie, gameState.getStartingMovie());
        assertTrue(gameState.isMovieUsed(startingMovie));
        assertTrue(gameState.getRecentHistory().contains(startingMovie));
    }

    @Test
    public void testAddMovieToHistory() {
        Movie heat = new Movie(2L, "Heat", 1995,
            Set.of(), Set.of("Al Pacino"), Set.of(), Set.of(), Set.of(), Set.of());

        gameState.addMovieToHistory(heat);

        assertTrue(gameState.isMovieUsed(heat));
        assertTrue(gameState.getRecentHistory().contains(heat));
        assertEquals(2, gameState.getRecentHistory().size());
    }

    @Test
    public void testGetRecentHistory_LimitToFive() {
        for (int i = 2; i <= 7; i++) {
            Movie movie = new Movie((long) i, "Movie " + i, 2000 + i,
                Set.of(), Set.of("Actor " + i), Set.of(), Set.of(), Set.of(), Set.of());
            gameState.addMovieToHistory(movie);
        }

        List<Movie> recentHistory = gameState.getRecentHistory();
        assertEquals(5, recentHistory.size());
        assertEquals("Movie 7", recentHistory.get(4).getTitle());
        assertEquals("Movie 3", recentHistory.get(0).getTitle());
    }

    @Test
    public void testSwitchPlayer() {
        gameState.switchPlayer();
        assertEquals(player2, gameState.getCurrentPlayer());
        gameState.switchPlayer();
        assertEquals(player1, gameState.getCurrentPlayer());
    }

    @Test
    public void testSwitchPlayer_IncrementRound() {
        gameState.switchPlayer(); // To Bob
        gameState.switchPlayer(); // Back to Alice, should increment round
        assertEquals(2, gameState.getCurrRound());
    }

    @Test
    public void testGetOtherPlayer() {
        assertEquals(player2, gameState.getOtherPlayer());
        gameState.switchPlayer();
        assertEquals(player1, gameState.getOtherPlayer());
    }

    @Test
    public void testIncrementConnectionUsage() {
        gameState.incrementConnectionUsage("Al Pacino");
        gameState.incrementConnectionUsage("Al Pacino");

        List<Connection> filtered = gameState.filterConnections(List.of(
            new Connection("Al Pacino", ConnectionType.ACTOR)
        ));
        assertEquals(1, filtered.size());
        gameState.incrementConnectionUsage("Al Pacino");

        filtered = gameState.filterConnections(List.of(
            new Connection("Al Pacino", ConnectionType.ACTOR)
        ));

        assertEquals(0, filtered.size());
    }

    @Test
    public void testFilterConnections() {
        List<Connection> connections = Arrays.asList(
            new Connection("Al Pacino", ConnectionType.ACTOR),
            new Connection("Robert De Niro", ConnectionType.ACTOR)
        );

        List<Connection> filtered = gameState.filterConnections(connections);
        assertEquals(2, filtered.size());

        // Mark Al Pacino as used 3 times
        gameState.incrementConnectionUsage("Al Pacino");
        gameState.incrementConnectionUsage("Al Pacino");
        gameState.incrementConnectionUsage("Al Pacino");

        filtered = gameState.filterConnections(connections);
        assertEquals(1, filtered.size());
        assertEquals("Robert De Niro", filtered.get(0).getPersonName());
    }

    @Test
    public void testGetCurrentMovie() {
        Movie heat = new Movie(2L, "Heat", 1995,
            Set.of(), Set.of("Al Pacino"), Set.of(), Set.of(), Set.of(), Set.of());
        gameState.addMovieToHistory(heat);

        assertEquals(heat, gameState.getCurrentMovie());
    }

    @Test
    public void testHasCurrentPlayerWon_False() {
        assertFalse(gameState.hasCurrentPlayerWon());
    }


    @Test
    public void testGetWinCondition() {
        assertEquals(winCondition, gameState.getWinCondition());
    }
}

