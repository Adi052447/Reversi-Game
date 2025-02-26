import java.util.List;
import java.util.Random;

/**
 * RandomAI is an AI player that selects a move at random from the list of valid moves.
 * It also chooses the type of disc to play (⬤, ⭕, 💣) randomly, depending on the available disc types.
 */
public class RandomAI extends AIPlayer {

    /**
     * Constructs a RandomAI instance.
     *
     * @param isPlayerOne Indicates if this AI is the first player.
     */
    public RandomAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    /**
     * Makes a random move by selecting a position and a disc type at random.
     *
     * @param gameStatus The current game state, providing valid moves and game logic.
     * @return A randomly selected move with a randomly chosen disc type.
     */
    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        List<Position> positions = gameStatus.ValidMoves(); // Get the list of valid moves.
        Random rand = new Random();
        int index = rand.nextInt(positions.size()); // Select a random position index.
        Position p = positions.get(index); // Get the random position.
        return new Move(p, randomTypeDisc()); // Create a move with the position and a random disc type.
    }

    /**
     * Selects a random disc type (⬤, ⭕, 💣) based on the availability of each type.
     *
     * @return A disc of a random type that the player can place, or null if no valid types are available.
     */
    public Disc randomTypeDisc() {
        String[] str = returnArrayOfDisc(); // Get the array of valid disc types.
        Random rand = new Random();
        int index = rand.nextInt(str.length); // Select a random type from the array.
        String type = str[index];
        if (type.equals("⬤")) {
            return new SimpleDisc(this);
        }
        if (type.equals("⭕")) {
            return new UnflippableDisc(this);
        }
        if (type.equals("💣")) {
            return new BombDisc(this);
        }
        return null; // Should not occur if the array is constructed properly.
    }

    /**
     * Returns an array of available disc types based on the player's remaining bombs and unflippable discs.
     *
     * @return An array of strings representing the available disc types.
     */
    public String[] returnArrayOfDisc() {
        if (this.getNumber_of_bombs() > 0 && this.getNumber_of_unflippedable() > 0) {
            return new String[]{"⬤", "⭕", "💣"}; // All types available.
        }
        if (this.getNumber_of_bombs() == 0 && this.getNumber_of_unflippedable() > 0) {
            return new String[]{"⬤", "⭕"}; // Bombs unavailable.
        }
        if (this.getNumber_of_bombs() > 0 && this.getNumber_of_unflippedable() == 0) {
            return new String[]{"⬤", "💣"}; // Unflippable discs unavailable.
        }
        if (this.getNumber_of_bombs() == 0 && this.getNumber_of_unflippedable() == 0) {
            return new String[]{"⬤"}; // Only normal discs available.
        }
        return null; // Fallback for unexpected cases.
    }
}
