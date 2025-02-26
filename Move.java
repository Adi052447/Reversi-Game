import java.util.List;

/**
 * Represents a single move in the game.
 * A move contains information about the position on the board where the move was made,
 * the type of disc placed, the player making the move, and the discs flipped as a result.
 */
public class Move {

    private Position position; // The position on the board where the move is made.
    private Disc disc;         // The disc placed during the move.
    private Player player;     // The player who made the move.
    private List<Position> discFlips; // The list of positions representing discs flipped by this move.

    /**
     * Constructs a Move with the given position, player, disc, and the discs flipped.
     *
     * @param position  The position on the board where the move was made.
     * @param player    The player who made the move.
     * @param disc      The type of disc placed.
     * @param discFlips The list of discs flipped as a result of this move.
     */
    public Move(Position position, Player player, Disc disc, List<Position> discFlips) {
        this.position = position;
        this.player = player;
        this.disc = disc;
        this.discFlips = discFlips;
    }

    /**
     * Constructs a Move with the given position and disc.
     * This constructor can be used when the player and flipped discs are not yet determined.
     *
     * @param position The position on the board where the move was made.
     * @param disc     The type of disc placed.
     */
    public Move(Position position, Disc disc) {
        this.position = position;
        this.disc = disc;
    }

    /**
     * Gets the position of the move.
     *
     * @return The position where the move was made.
     */
    public Position position() {
        return this.position;
    }

    /**
     * Gets the disc placed during the move.
     *
     * @return The disc placed.
     */
    public Disc disc() {
        return this.disc;
    }

    /**
     * Gets the player who made the move.
     *
     * @return The player who made the move.
     */
    public Player player() {
        return this.player;
    }

    /**
     * Gets the list of positions representing discs flipped during this move.
     *
     * @return A list of flipped disc positions.
     */
    public List<Position> getDiscFlips() {
        return this.discFlips;
    }

    /**
     * Sets the disc for the move.
     *
     * @param disc The disc to set.
     */
    public void setDisc(Disc disc) {
        this.disc = disc;
    }

    /**
     * Sets the position of the move.
     *
     * @param position The position to set.
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * Sets the list of positions representing flipped discs.
     *
     * @param discFlips The list of flipped positions to set.
     */
    public void setDiscFlips(List<Position> discFlips) {
        this.discFlips = discFlips;
    }

    /**
     * Sets the player who made the move.
     *
     * @param player The player to set.
     */
    public void setPlayer(Player player) {
        this.player = player;
    }
}
