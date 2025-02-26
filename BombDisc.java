/**
 * Represents a BombDisc, a special type of disc in the game.
 * When placed, it can affect multiple surrounding discs on the board,
 * based on game-specific logic defined elsewhere.
 */
public class BombDisc implements Disc {
    private Player owner; // The player who owns this disc.

    /**
     * Constructs a BombDisc with the specified owner.
     *
     * @param owner1 The player who owns this bomb disc.
     */
    public BombDisc(Player owner1) {
        this.owner = owner1;
    }

    /**
     * Gets the owner of the bomb disc.
     *
     * @return The Player who owns this disc.
     */
    @Override
    public Player getOwner() {
        return this.owner;
    }

    /**
     * Sets the owner of the bomb disc.
     *
     * @param player The new owner of this disc.
     */
    @Override
    public void setOwner(Player player) {
        this.owner = player;
    }

    /**
     * Returns the type of the disc.
     * For BombDisc, the type is represented by the bomb emoji "💣".
     *
     * @return A string representing the disc type.
     */
    @Override
    public String getType() {
        return "💣";
    }
}
