import java.util.*;

/**
 * The GameLogic class manages the game's logic, including placing discs,
 * validating moves, flipping discs, managing player turns, and maintaining move history.
 * It implements the PlayableLogic interface.
 */
public class GameLogic implements PlayableLogic {
    public static Disc[][] board; // The game board (8x8 grid)
    private Player player1; // The first player
    private Player player2; // The second player
    private int countPlayer1, countPlayer2; // Counts of discs for each player
    private final int BOARD_SIZE = 8; // Fixed size of the board
    private boolean turn; // Tracks whose turn it is (true for player1, false for player2)
    private int[][] directions = { // All possible directions for checking flips
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1}, {0, 1},
            {1, -1}, {1, 0}, {1, 1}};
    private Stack<Move> historyMove = new Stack<>(); // Stack to store the history of moves

    /**
     * Constructor for GameLogic. Initializes the board and sets the initial turn.
     */
    public GameLogic() {
        board = new Disc[BOARD_SIZE][BOARD_SIZE]; // Initialize the game board
        turn = true; // Player 1 starts the game
    }

    /**
     * Places a disc on the board at the specified position if the move is valid.
     *
     * @param a    The position to place the disc.
     * @param disc The disc to be placed.
     * @return True if the disc was successfully placed, false otherwise.
     */
    @Override
    public boolean locate_disc(Position a, Disc disc) {
        if (getDiscAtPosition(a) == null && isContainPosition(ValidMoves(), a)) {
            if (!checkIfOkToPutBombOrUnflipp(disc))
                return false;
            board[a.row()][a.col()] = disc; // Place the disc on the board
            board[a.row()][a.col()].setOwner(getCurrentPlayer()); // Set the owner of the disc
            System.out.println("Player " + getNumPlayer() + " placed a " + disc.getType() + " in " + a.toString());

            // Update player resources if placing special discs
            if (board[a.row()][a.col()].getType().equals("⭕")) {
                getCurrentPlayer().reduce_unflippedable();
            }
            if (board[a.row()][a.col()].getType().equals("💣")) {
                getCurrentPlayer().reduce_bomb();
            }

            // Save the move to the history stack
            Move m = new Move(a, getCurrentPlayer(), disc, getFlips(a, disc));
            historyMove.push(m);

            // Flip the affected discs
            flipDiscs(getFlips(a, disc));
            System.out.println();
            changeTurn(turn); // Change the turn to the next player
            return true;
        }
        return false;
    }

    /**
     * Retrieves the disc at the specified position on the board.
     *
     * @param position The position to check.
     * @return The disc at the position, or null if the position is empty.
     */
    @Override
    public Disc getDiscAtPosition(Position position) {
        return board[position.row()][position.col()];
    }

    /**
     * Gets the size of the board.
     *
     * @return The board size.
     */
    @Override
    public int getBoardSize() {
        return BOARD_SIZE;
    }

    /**
     * Calculates and returns the list of valid moves for the current player.
     *
     * @return A list of valid positions where the current player can place a disc.
     */
    @Override
    public List<Position> ValidMoves() {
        List<Position> validMoves = new ArrayList<>();
        Disc tempDisc = new SimpleDisc(getCurrentPlayer()); // Temporary disc for validation

        for (int row = 0; row < getBoardSize(); row++) {
            for (int col = 0; col < getBoardSize(); col++) {
                if (board[row][col] == null) { // Check empty positions
                    List<Position> flips = getFlips(new Position(row, col), tempDisc);
                    if (!flips.isEmpty() && countFlips(new Position(row, col)) > 0) {
                        validMoves.add(new Position(row, col)); // Add valid position
                    }
                }
            }
        }
        return validMoves;
    }

    /**
     * Counts the number of discs that can be flipped for a given move.
     *
     * @param a The position of the move.
     * @return The number of discs that will be flipped.
     */
    @Override
    public int countFlips(Position a) {
        int countUnflipp = 0;
        Disc tempDisc = new SimpleDisc(getCurrentPlayer());
        List<Position> flips = getFlips(a, tempDisc);

        for (Position flip : flips) {
            if (board[flip.row()][flip.col()].getType().equals("⭕")) {
                countUnflipp++;
            }
        }
        return flips.size() - countUnflipp;
    }

    /**
     * Calculates the list of discs that will be flipped for a given move.
     *
     * @param position The position of the move.
     * @param disc     The disc to be placed.
     * @return A list of positions of discs to be flipped.
     */
    public List<Position> getFlips(Position position, Disc disc) {
        List<Position> flips = new ArrayList<>();

        for (int[] dir : directions) { // Check in all directions
            List<Position> potentialFlips = new ArrayList<>();
            int row = position.row() + dir[0];
            int col = position.col() + dir[1];

            while (row >= 0 && row < getBoardSize() && col >= 0 && col < getBoardSize() &&
                    board[row][col] != null && board[row][col].getOwner() != disc.getOwner()) {
                Position tempPos = new Position(row, col);
                potentialFlips.add(tempPos);

                if (getDiscAtPosition(tempPos).getType().equals("💣")) {
                    List<Position> tempPosBomb = discFlipOfBomb(tempPos, potentialFlips);
                    potentialFlips.addAll(tempPosBomb);
                }
                row += dir[0];
                col += dir[1];
            }

            // If the sequence ends with a disc of the same owner, add flips
            if (row >= 0 && row < getBoardSize() && col >= 0 && col < getBoardSize() &&
                    board[row][col] != null && board[row][col].getOwner() == disc.getOwner()) {
                flips.addAll(potentialFlips);
            }
        }
        removeDuplicae(flips); // Ensure no duplicate flips
        return flips;
    }

    /**
     * Handles the flipping of discs when a bomb is placed.
     * Adds all affected positions to the flip list recursively.
     *
     * @param position       The position of the bomb.
     * @param potentialFlips The list of positions affected so far.
     * @return Updated list of positions affected by the bomb.
     */
    public List<Position> discFlipOfBomb(Position position, List<Position> potentialFlips) {
        potentialFlips.add(position);
        int row, col;

        for (int[] dir : directions) { // Check in all directions
            row = position.row() + dir[0];
            col = position.col() + dir[1];
            Position tempPos = new Position(row, col);

            if (row >= 0 && row < getBoardSize() && col >= 0 && col < getBoardSize()) {
                if (board[row][col] != null && board[row][col].getOwner() != getCurrentPlayer()) {
                    if (!isContainPosition(potentialFlips, tempPos)) {
                        potentialFlips.add(tempPos);

                        // Recursive handling for chained bomb flips
                        if (getDiscAtPosition(tempPos).getType().equals("💣")) {
                            discFlipOfBomb(tempPos, potentialFlips);
                        }
                    }
                }
            }
        }
        return potentialFlips;
    }

    /**
     * Gets the first player in the game.
     *
     * @return The first player.
     */
    @Override
    public Player getFirstPlayer() {
        return this.player1.isPlayerOne ? this.player1 : this.player2;
    }

    /**
     * Gets the second player in the game.
     *
     * @return The second player.
     */
    @Override
    public Player getSecondPlayer() {
        return !this.player1.isPlayerOne ? this.player1 : this.player2;
    }

    /**
     * Sets the players for the game.
     *
     * @param player1 The first player.
     * @param player2 The second player.
     */
    @Override
    public void setPlayers(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    /**
     * Checks whether it is the first player's turn.
     *
     * @return True if it is player one's turn, false otherwise.
     */
    @Override
    public boolean isFirstPlayerTurn() {
        return turn;
    }

    /**
     * Checks whether the game is finished by validating moves or determining a winner.
     *
     * @return True if the game is finished, false otherwise.
     */
    @Override
    public boolean isGameFinished() {
        if (ValidMoves().isEmpty()) {
            String winner = isPlayerOneWon();

            if (winner.equals("Player 1")) {
                getFirstPlayer().addWin();
                System.out.println("Player 1 wins with " + countPlayer1 + " discs! Player 2 had " + countPlayer2 + " discs.");
            } else if (winner.equals("Player 2")) {
                getSecondPlayer().addWin();
                System.out.println("Player 2 wins with " + countPlayer2 + " discs! Player 1 had " + countPlayer1 + " discs.");
            } else if (winner.equals("draw")) {
                System.out.println("The game is a draw.");
            }
            return true;
        }
        return false;
    }

    /**
     * Resets the game to its initial state.
     */
    @Override
    public void reset() {
        clearBoard(); // Clears the board
        this.turn = true; // Player 1 starts
        this.board[3][3] = new SimpleDisc(getFirstPlayer());
        this.board[4][4] = new SimpleDisc(getFirstPlayer());
        this.board[3][4] = new SimpleDisc(getSecondPlayer());
        this.board[4][3] = new SimpleDisc(getSecondPlayer());
        historyMove.clear(); // Clear move history
        player1.reset_bombs_and_unflippedable();
        player2.reset_bombs_and_unflippedable();
    }

    /**
     * Undoes the last move. This is only available if both players are human.
     */
    @Override
    public void undoLastMove() {
        if (getFirstPlayer().isHuman() && getSecondPlayer().isHuman()) {
            if (historyMove.isEmpty()) {
                System.out.println("\tNo previous move available to undo.");
                return;
            }

            Move lastMove = historyMove.pop();
            System.out.println("Undoing last move: ");

            // Restore player resources for special discs
            if (lastMove.disc().getType().equals("⭕")) {
                lastMove.player().restoreUnFlippedable();
            }
            if (lastMove.disc().getType().equals("💣")) {
                lastMove.player().restoreBombs();
            }

            // Remove the placed disc and revert flipped discs
            board[lastMove.position().row()][lastMove.position().col()] = null;
            System.out.println("\tUndo: removing " + lastMove.disc().getType() + " from " + lastMove.position().toString());
            for (Position pos : lastMove.getDiscFlips()) {
                changeColorBack(getDiscAtPosition(pos));
                System.out.println("\tUndo: flipping back " + getDiscAtPosition(pos).getType() + " in " + pos.toString());
            }

            System.out.println();
            changeTurn(turn); // Revert the turn
        }
    }

    /**
     * Changes the turn to the next player.
     *
     * @param turn The current turn.
     */
    public void changeTurn(boolean turn) {
        this.turn = !turn;
    }

    /**
     * Gets the current player based on the turn.
     *
     * @return The current player.
     */
    public Player getCurrentPlayer() {
        return turn ? player1 : player2;
    }

    /**
     * Clears the entire board, setting all positions to null.
     */
    public void clearBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = null;
            }
        }
    }

    /**
     * Determines the winner based on the current disc counts on the board.
     *
     * @return "Player 1", "Player 2", or "draw" depending on the result.
     */
    public String isPlayerOneWon() {
        countPlayer1 = 0;
        countPlayer2 = 0;

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] != null) {
                    if (board[i][j].getOwner().equals(getFirstPlayer())) {
                        countPlayer1++;
                    } else {
                        countPlayer2++;
                    }
                }
            }
        }

        if (countPlayer1 > countPlayer2) return "Player 1";
        if (countPlayer1 < countPlayer2) return "Player 2";
        return "draw";
    }

    /**
     * Checks if a given position is in a list of positions.
     *
     * @param poslist The list of positions.
     * @param a       The position to check.
     * @return True if the position is contained in the list, false otherwise.
     */
    public boolean isContainPosition(List<Position> poslist, Position a) {
        for (Position pos : poslist) {
            if (pos.row() == a.row() && pos.col() == a.col()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Flips discs at the specified positions to the current player's color.
     *
     * @param pos The list of positions to flip.
     */
    public void flipDiscs(List<Position> pos) {
        for (Position position : pos) {
            if (!getDiscAtPosition(position).getType().equals("⭕")) {
                board[position.row()][position.col()].setOwner(getCurrentPlayer());
            }
            System.out.println("Player " + getNumPlayer() + " flipped the " + getDiscAtPosition(position).getType() + " in " + position.toString());
        }
    }

    /**
     * Reverts the ownership of a disc to its original owner.
     *
     * @param disc The disc to revert.
     */
    public void changeColorBack(Disc disc) {
        Player temp = disc.getOwner();
        if (temp == getFirstPlayer()) {
            disc.setOwner(getSecondPlayer());
        } else {
            disc.setOwner(getFirstPlayer());
        }
    }

    /**
     * Checks if it is valid to place a bomb or an unflippable disc based on the current player's remaining resources.
     *
     * @param disc The disc being placed (either bomb or unflippable).
     * @return True if the disc can be placed, false otherwise.
     */
    public boolean checkIfOkToPutBombOrUnflipp(Disc disc) {
        if (disc.getType().equals("⭕") && getCurrentPlayer().getNumber_of_unflippedable() == 0) {
            return false; // No unflippable discs left
        }
        if (disc.getType().equals("💣") && getCurrentPlayer().getNumber_of_bombs() == 0) {
            return false; // No bombs left
        }
        return true;
    }

    /**
     * Removes duplicate positions from a list.
     *
     * @param a The list of positions to process.
     */
    public void removeDuplicae(List<Position> a) {
        List<Position> newList = new ArrayList<>();
        for (Position item : a) {
            if (!isContainPosition(newList, item)) {
                newList.add(item); // Add only unique positions
            }
        }
        a.clear(); // Clear the original list
        a.addAll(newList); // Replace with the deduplicated list
    }

    /**
     * Determines the current player's number (1 or 2).
     *
     * @return The current player's number.
     */
    public int getNumPlayer() {
        if (getCurrentPlayer().equals(getFirstPlayer())) {
            return 1; // Current player is Player 1
        }
        if (getCurrentPlayer().equals(getSecondPlayer())) {
            return 2; // Current player is Player 2
        }
        return 0; // Default (should not occur)
    }

}


