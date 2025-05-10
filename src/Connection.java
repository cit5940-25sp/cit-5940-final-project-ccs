import java.util.Objects;

/**
 * Represents a connection between two movies based on shared attributes like actor or director.
 */
public class Connection {
    private String personName;
    private ConnectionType type;

    public Connection(String personName, ConnectionType type) {
        this.personName = personName;
        this.type = type;
    }
    /**
     * Retrieves the name of the person associated with the connection.
     *
     * @return The name of the person as a String.
     */
    public String getPersonName() {
        return personName;
    }
    /**
     * Retrieves the type of the connection (e.g., Actor, Director).
     *
     * @return The ConnectionType of the connection.
     */
    public ConnectionType getType() {
        return type;
    }

    /**
     * Checks if two Connection objects are equal based on personName and type.
     *
     * @param o The object to compare with the current instance.
     * @return true if both personName and type are equal, otherwise false.
     */
    // Two Connection objects are considered equal iff both personName and type match
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true; // same object reference
        }
        if (o == null || getClass() != o.getClass()) {
            return false; // null or different class
        }
        Connection that = (Connection) o;
        return Objects.equals(personName, that.personName) && type == that.type;
    }
    /**
     * Generates a hash code for the Connection object based on personName and type.
     *
     * @return A hash code representing the connection.
     */
    @Override
    public int hashCode() {
        return Objects.hash(personName, type);
    }
    /**
     * Provides a string representation of the Connection object.
     *
     * @return A formatted string in the format "personName (type)".
     */
    @Override
    public String toString() {
        return String.format("%s (%s)", personName, type);
    }
}
