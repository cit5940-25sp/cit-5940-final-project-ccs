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



//import static org.junit.Assert.*;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.*;
//
//public class GameStateTest {
//
//    private Player player1;
//    private Player player2;
//    private Movie startingMovie;
//    private GameState gameState;
//
//    @Before
//    public void setUp() {
//        player1 = new Player("Alice");
//        player2 = new Player("Bob");
//
//        startingMovie = new Movie(
//                1L,
//                "Inception",
//                2010,
//                new HashSet<>(Arrays.asList("Sci-Fi")),
//                new HashSet<>(Arrays.asList("Leonardo DiCaprio")),
//                new HashSet<>(Arrays.asList("Christopher Nolan")),
//                new HashSet<>(Arrays.asList("Jonathan Nolan")),
//                new HashSet<>(Arrays.asList("Hans Zimmer")),
//                new HashSet<>(Arrays.asList("Wally Pfister"))
//        );
//
//        WinCondition dummyWinCondition = new WinCondition() {
//            @Override
//            public boolean checkVictory(Player player) {
//                return player.getNumMoviesGuessed() >= 1;
//            }
//
//            @Override
//            public String description() {
//                return "Dummy win after 1 movie";
//            }
//
//            @Override
//            public void updatePlayerProgress(Player player, Movie movie) {
//            }
//
//            @Override
//            public String getPlayerProgress(Player player) {
//                return "1/1 movies guessed";
//            }
//        };
//
//        gameState = new GameState(player1, player2, dummyWinCondition, startingMovie);
//    }
//
//    @Test
//    public void testInitialState() {
//        assertEquals(player1, gameState.getCurrentPlayer());
//        assertEquals(player2, gameState.getOtherPlayer());
//        assertEquals(1, gameState.getCurrRound());
//        assertTrue(gameState.isMovieUsed(startingMovie));
//        assertEquals(startingMovie, gameState.getCurrentMovie());
//    }
//
//    @Test
//    public void testAddMovieToHistory() {
//        Movie movie2 = new Movie(
//                2L,
//                "Titanic",
//                1997,
//                new HashSet<>(Arrays.asList("Romance")),
//                new HashSet<>(Arrays.asList("Leonardo DiCaprio")),
//                new HashSet<>(Arrays.asList("James Cameron")),
//                new HashSet<>(Arrays.asList("James Cameron")),
//                new HashSet<>(Arrays.asList("James Horner")),
//                new HashSet<>(Arrays.asList("Russell Carpenter"))
//        );
//
//        gameState.addMovieToHistory(movie2);
//        assertTrue(gameState.isMovieUsed(movie2));
//        assertEquals(movie2, gameState.getCurrentMovie());
//    }
//
//    @Test
//    public void testSwitchPlayer() {
//        Player current = gameState.getCurrentPlayer();
//        gameState.switchPlayer();
//        assertNotEquals(current, gameState.getCurrentPlayer());
//        assertEquals(2, gameState.getCurrRound());
//    }
//
//    @Test
//    public void testConnectionUsage() {
//        String person = "Leonardo DiCaprio";
//        assertTrue(gameState.canUseConnection(person));
//
//        gameState.incrementConnectionUsage(person);
//        gameState.incrementConnectionUsage(person);
//        gameState.incrementConnectionUsage(person);
//
//        assertFalse(gameState.canUseConnection(person));
//    }
//
//    @Test
//    public void testRecentHistory() {
//        Movie movie2 = new Movie(2L, "Titanic", 1997,
//                new HashSet<>(), new HashSet<>(), new HashSet<>(),
//                new HashSet<>(), new HashSet<>(), new HashSet<>());
//        Movie movie3 = new Movie(3L, "Dunkirk", 2017,
//                new HashSet<>(), new HashSet<>(), new HashSet<>(),
//                new HashSet<>(), new HashSet<>(), new HashSet<>());
//        Movie movie4 = new Movie(4L, "Tenet", 2020,
//                new HashSet<>(), new HashSet<>(), new HashSet<>(),
//                new HashSet<>(), new HashSet<>(), new HashSet<>());
//        Movie movie5 = new Movie(5L, "Memento", 2000,
//                new HashSet<>(), new HashSet<>(), new HashSet<>(),
//                new HashSet<>(), new HashSet<>(), new HashSet<>());
//        Movie movie6 = new Movie(6L, "The Prestige", 2006,
//                new HashSet<>(), new HashSet<>(), new HashSet<>(),
//                new HashSet<>(), new HashSet<>(), new HashSet<>());
//
//        gameState.addMovieToHistory(movie2);
//        gameState.addMovieToHistory(movie3);
//        gameState.addMovieToHistory(movie4);
//        gameState.addMovieToHistory(movie5);
//        gameState.addMovieToHistory(movie6);
//
//        List<Movie> recent = gameState.getRecentHistory();
//        assertEquals(5, recent.size());
//        assertEquals(movie2, recent.get(0)); // oldest among recent
//        assertEquals(movie6, recent.get(4)); // newest
//    }
//
//    @Test
//    public void testWinConditionCheck() {
//        assertTrue(gameState.hasCurrentPlayerWon()); // Based on dummy condition: guessed ≥ 1
//    }
//}