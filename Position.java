/**
 * Represents a position on the game board with a row and column.
 */
public class Position {

    private int row; // The row index of the position.
    private int col; // The column index of the position.

    /**
     * Constructs a Position with the specified row and column indices.
     *
     * @param row The row index of the position.
     * @param col The column index of the position.
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Gets the row index of the position.
     *
     * @return The row index.
     */
    public int row() {
        return this.row;
    }

    /**
     * Gets the column index of the position.
     *
     * @return The column index.
     */
    public int col() {
        return this.col;
    }

    /**
     * Returns a string representation of the position in the format "(row, col)".
     *
     * @return A string representing the position.
     */
    @Override
    public String toString() {
        return "(" + this.row + ", " + this.col + ")";
    }
}
