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

    /**
     * Updates the player's progress towards meeting the win condition
     * when a new movie is guessed.
     *
     * @param player The player whose progress is being updated.
     * @param movie The movie that the player has guessed.
     */
    void updatePlayerProgress(Player player, Movie movie);
    /**
     * Retrieves the player's current progress towards fulfilling the win condition.
     *
     * @param player The player whose progress is being queried.
     * @return A string representation of the player's progress.
     */
    String getPlayerProgress(Player player);
}
