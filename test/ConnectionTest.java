import static org.junit.Assert.*;
import org.junit.Test;
import java.util.HashSet;
import java.util.Set;

public class ConnectionTest {

    @Test
    public void testConstructorAndGetters() {
        Connection connection = new Connection("Leonardo DiCaprio", ConnectionType.ACTOR);

        assertEquals("Leonardo DiCaprio", connection.getPersonName());
        assertEquals(ConnectionType.ACTOR, connection.getType());
    }
    @Test
    public void testEqualsAndHashCode_SameObject() {
        Connection connection = new Connection("Leonardo DiCaprio", ConnectionType.ACTOR);

        // Same object reference
        assertEquals(connection, connection);
        assertEquals(connection.hashCode(), connection.hashCode());
    }

    @Test
    public void testDifferentConnectionsNotEqual() {
        Connection actorConnection = new Connection(
                "Leonardo DiCaprio", ConnectionType.ACTOR);
        Connection directorConnection = new Connection(
                "Christopher Nolan", ConnectionType.DIRECTOR);

        assertNotEquals(actorConnection, directorConnection);
    }

    @Test
    public void testEqualsAndHashCode() {
        Connection c1 = new Connection("Leonardo DiCaprio", ConnectionType.ACTOR);
        Connection c2 = new Connection("Leonardo DiCaprio", ConnectionType.ACTOR);

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void testSetBehaviorWithEqualConnections() {
        Connection c1 = new Connection("Leonardo DiCaprio", ConnectionType.ACTOR);
        Connection c2 = new Connection("Leonardo DiCaprio", ConnectionType.ACTOR);

        Set<Connection> set = new HashSet<>();
        set.add(c1);

        // try adding a "duplicate" logically equal connection
        set.add(c2);

        // set should only have one element
        assertEquals(1, set.size());
    }

    @Test
    public void testSetEquality() {
        Connection c1 = new Connection("Leonardo DiCaprio", ConnectionType.ACTOR);
        Connection c2 = new Connection("Leonardo DiCaprio", ConnectionType.ACTOR);

        Set<Connection> set1 = new HashSet<>();
        set1.add(c1);

        Set<Connection> set2 = new HashSet<>();
        set2.add(c2);

        assertEquals(set1, set2);
    }
    @Test
    public void testToString() {
        Connection c1 = new Connection("Leonardo DiCaprio", ConnectionType.ACTOR);
        String expected = "Leonardo DiCaprio (ACTOR)";

        assertEquals(expected, c1.toString());
    }
}