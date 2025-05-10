import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class MovieTest {

    private Movie inception;
    private Movie titanic;
    private Movie avatar;
    private Movie interstellar;

    @Before
    public void setUp() {
        inception = new Movie(
            1L,
            "Inception",
            2010,
            new HashSet<>(Arrays.asList("Sci-Fi")),
            new HashSet<>(Arrays.asList("Leonardo DiCaprio")),
            new HashSet<>(Arrays.asList("Christopher Nolan")),
            new HashSet<>(Arrays.asList("Jonathan Nolan")),
            new HashSet<>(Arrays.asList("Hans Zimmer")),
            new HashSet<>(Arrays.asList("Wally Pfister"))
        );


        titanic = new Movie(
            2L,
            "Titanic",
            1997,
            new HashSet<>(Arrays.asList("Romance")),
            new HashSet<>(Arrays.asList("Leonardo DiCaprio", "Kate Winslet")),
            new HashSet<>(Arrays.asList("James Cameron")),
            new HashSet<>(Arrays.asList("James Cameron")),
            new HashSet<>(Arrays.asList("James Horner")),
            new HashSet<>(Arrays.asList("Russell Carpenter"))
        );

        avatar = new Movie(
            3L,
            "Avatar",
            2009,
            new HashSet<>(Arrays.asList("Sci-Fi")),
            new HashSet<>(Arrays.asList("Sam Worthington")),
            new HashSet<>(Arrays.asList("James Cameron")),
            new HashSet<>(Arrays.asList("James Cameron")),
            new HashSet<>(Arrays.asList("James Horner")),
            new HashSet<>(Arrays.asList("Mauro Fiore"))
        );

        interstellar = new Movie(
            4L,
            "Interstellar",
            2014,
            new HashSet<>(Arrays.asList("Sci-Fi")),
            new HashSet<>(Arrays.asList("Matthew McConaughey")),
            new HashSet<>(Arrays.asList("Christopher Nolan")),
            new HashSet<>(Arrays.asList("Jonathan Nolan")),
            new HashSet<>(Arrays.asList("Hans Zimmer")),
            new HashSet<>(Arrays.asList("Hoyte van Hoytema"))
        );
    }
    @Test
    public void testNoArgsConstructor() {
        // Arrange
        Movie movie = new Movie();

        assertNotNull(movie.getGenres());
        assertTrue(movie.getGenres().isEmpty());

        assertNotNull(movie.getActors());
        assertTrue(movie.getActors().isEmpty());

        assertNotNull(movie.getDirectors());
    }
    @Test
    public void testFindConnections_SameActor() {
        List<Connection> connections = inception.findConnections(titanic);
        assertFalse(connections.isEmpty());

        boolean found = connections.stream()
            .anyMatch(c -> c.getPersonName().equals("Leonardo DiCaprio") && c.getType() == ConnectionType.ACTOR);

        assertTrue(found);
    }

    @Test
    public void testFindConnections_SameDirector() {
        List<Connection> connections = titanic.findConnections(avatar);
        assertFalse(connections.isEmpty());

        boolean found = connections.stream()
            .anyMatch(c -> c.getPersonName().equals("James Cameron") && c.getType() == ConnectionType.DIRECTOR);

        assertTrue(found);
    }

    @Test
    public void testFindConnections_SameWriter() {
        List<Connection> connections = inception.findConnections(interstellar);
        assertFalse(connections.isEmpty());

        boolean found = connections.stream()
            .anyMatch(c -> c.getPersonName().equals("Jonathan Nolan") && c.getType() == ConnectionType.WRITER);

        assertTrue(found);
    }

    @Test
    public void testFindConnections_SameComposer() {
        List<Connection> connections = inception.findConnections(interstellar);
        assertFalse(connections.isEmpty());

        boolean found = connections.stream()
            .anyMatch(c -> c.getPersonName().equals("Hans Zimmer") && c.getType() == ConnectionType.COMPOSER);

        assertTrue(found);
    }


    @Test
    public void testFindConnections_NoCommonAttributes() {
        Movie randomMovie = new Movie(
            4L,
            "Random Movie",
            2022,
            new HashSet<>(Arrays.asList("Action")),
            new HashSet<>(Arrays.asList("Random Actor")),
            new HashSet<>(Arrays.asList("Random Director")),
            new HashSet<>(Arrays.asList("Random Writer")),
            new HashSet<>(Arrays.asList("Random Composer")),
            new HashSet<>(Arrays.asList("Random Cinematographer"))
        );

        List<Connection> connections = inception.findConnections(randomMovie);
        assertTrue(connections.isEmpty());
    }

    @Test
    public void testAddConnectionHistory() {
        Connection connection = new Connection("Christopher Nolan", ConnectionType.DIRECTOR);
        List<Connection> connectionList = new ArrayList<>();
        connectionList.add(connection);

        inception.addConnectionHistory(connectionList);

        assertEquals(1, inception.getConnectionHistory().size());
        assertEquals(connectionList, inception.getConnectionHistory().get(0));
    }

    @Test
    public void testEqualsAndHashCode() {
        Movie movieCopy = new Movie(
            1L,
            "Inception",
            2010,
            null, null, null, null, null, null
        );

        assertEquals(inception, movieCopy);
        assertEquals(inception.hashCode(), movieCopy.hashCode());
    }

    @Test
    public void testEquals_DifferentTitle() {
        Movie differentTitle = new Movie(5L, "Different Title", 2010, null, null, null, null, null, null);
        assertNotEquals(inception, differentTitle);
    }

    @Test
    public void testEquals_Null() {
        assertNotEquals(inception, null);
    }

    @Test
    public void testEquals_DifferentType() {
        assertNotEquals(inception, "Not a Movie");
    }

//    @Test
//    public void testToString() {
//        String expected = "Inception (2010) \nactors:Leonardo DiCaprio \ngenres:Sci-Fi ";
//        assertEquals(expected, inception.toString());
//    }

    @Test
    public void testGetWritersInception() {
        assertEquals(Set.of("Jonathan Nolan"), inception.getWriters());
    }

    @Test
    public void testGetWritersTitanic() {
        assertEquals(Set.of("James Cameron"), titanic.getWriters());
    }

    @Test
    public void testGetComposersInterstellar() {
        assertEquals(Set.of("Hans Zimmer"), interstellar.getComposers());
    }

    @Test
    public void testGetComposersAvatar() {
        assertEquals(Set.of("James Horner"), avatar.getComposers());
    }

    @Test
    public void testGetCinematographersTitanic() {
        assertEquals(Set.of("Russell Carpenter"), titanic.getCinematographers());
    }

    @Test
    public void testGetCinematographersInterstellar() {
        assertEquals(Set.of("Hoyte van Hoytema"), interstellar.getCinematographers());
    }

}
