public class TurnResult {
    private final boolean success;
    private final boolean gameOver;
    private final String message;
    /**
     * Constructs a TurnResult object representing the outcome of a game turn.
     *
     * @param success Indicates if the turn was successful.
     * @param gameOver Indicates if the game has ended after this turn.
     * @param message A message providing details about the turn result.
     */
    public TurnResult(boolean success, boolean gameOver, String message) {
        this.success = success;
        this.gameOver = gameOver;
        this.message = message;
    }
    /**
     * Constructs a TurnResult object representing the outcome of a game turn.
     * GameOver is set to false by default.
     *
     * @param success Indicates if the turn was successful.
     * @param message A message providing details about the turn result.
     */
    // Keep original constructor for backward compatibility
    public TurnResult(boolean success, String message) {
        this(success, false, message);
    }
    /**
     * Checks if the turn was successful.
     *
     * @return true if the turn succeeded; false otherwise.
     */
    public boolean isSuccess() {
        return success;
    }
    /**
     * Checks if the game is over as a result of this turn.
     *
     * @return true if the game is over; false otherwise.
     */
    public boolean isGameOver() {
        return gameOver;
    }
    /**
     * Retrieves the message associated with the turn result.
     *
     * @return The message string containing details about the turn.
     */
    public String getMessage() {
        return message;
    }
}
