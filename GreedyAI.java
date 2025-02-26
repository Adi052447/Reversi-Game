import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * GreedyAI is an AI player that selects its moves based on a greedy algorithm.
 * It always chooses the move that maximizes the number of opponent discs flipped in the current turn.
 */
public class GreedyAI extends AIPlayer {

    /**
     * Constructs a GreedyAI player.
     *
     * @param isPlayerOne True if the AI is Player 1, false otherwise.
     */
    public GreedyAI(boolean isPlayerOne) {
        super(isPlayerOne); // Call the parent class constructor.
    }

    /**
     * Makes a move for the AI player using a greedy algorithm.
     * The AI chooses the position that flips the maximum number of opponent discs.
     * If there are multiple positions with the same maximum flips, it selects the one
     * closest to the bottom-right corner of the board (based on row and column priority).
     *
     * @param gameStatus The current game logic, providing information about valid moves and flips.
     * @return A Move object representing the AI's selected move.
     */
    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        // Get all valid moves for the current game state.
        List<Position> positions = gameStatus.ValidMoves();
        int maxFlips = 0;
        List<Position> sameMaxPosition = new ArrayList<>(); // Tracks positions with the maximum flips.

        // Iterate through all valid positions to find the one(s) with the maximum flips.
        for (Position position : positions) {
            int flips = gameStatus.countFlips(position);
            if (flips > maxFlips) {
                maxFlips = flips; // Update the maximum flip count.
                sameMaxPosition.clear(); // Clear previous positions.
                sameMaxPosition.add(position); // Add the new position with maximum flips.
            } else if (flips == maxFlips) {
                sameMaxPosition.add(position); // Add position to the list of ties.
            }
        }

        // Comparator to prioritize positions by column (descending), then row (descending).
        Comparator<Position> rightPosition = (p1, p2) -> {
            if (p1.col() != p2.col()) {
                return Integer.compare(p2.col(), p1.col()); // Compare by column, descending.
            }
            return Integer.compare(p2.row(), p1.row()); // Compare by row, descending.
        };

        // Sort positions with the same maximum flips.
        Collections.sort(sameMaxPosition, rightPosition);

        // Select the best position, or null if no valid positions exist.
        Position maxPosition = sameMaxPosition.isEmpty() ? null : sameMaxPosition.get(0);

        // Create a new disc owned by this AI player.
        SimpleDisc disc = new SimpleDisc(this);

        // Create and return the move for the chosen position and disc.
        Move move = new Move(maxPosition, disc);
        return move;
    }
}
