/**
 * Interface for defining custom game win conditions.
 */
public interface WinCondition {
    /**
     * Checks if a player satisfies the win condition.
     *
     * @param player the player to evaluate
     * @return true if the win condition is met
     */
    boolean checkVictory(Player player);

    /**
     * Provides a description of the win condition.
     *
     * @return textual description of the condition
     */
    String description();


    void updatePlayerProgress(Player player, Movie movie);

    String getPlayerProgress(Player player);
}
