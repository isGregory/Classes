// WordzBoard.java

import java.util.Random;

/**
 * Class WordzBoard encapsulates the state of the Wordz game grid.
 *
 * @author Gregory Hoople
 * @version 2015-5-9
 */
public class WordzBoard {

// Exported Constants
	public static final int ROWS = 4;
	public static final int COLS = 4;

// Hidden Data Members
	private int nextID = 0;
	private String[] players = {"", ""};
	private int[] scores = {0,0};
	private int turn = -1;

	private Random rand = new Random( System.currentTimeMillis() );
	private char[][] spots = new char[ROWS][COLS];
	private boolean[][] selected = new boolean[ROWS][COLS];
	private String word = "";

	private String[][] Options = new String[ROWS][COLS];

// Exported Constructors
	/**
	 * Construct a new Wordz Board. All spots are initially zero.
	 */
	public WordzBoard() {
		// Options holds all the possible letters for each grid.
		Options[0][0] = "TAOTOW";
		Options[0][1] = "VRHTWE";
		Options[0][2] = "VERLDY";
		Options[0][3] = "GEWHNE";
		Options[1][0] = "NEISEU";
		Options[1][1] = "MIOTCU";
		Options[1][2] = "YSDITT";
		Options[1][3] = "ESTISO";
		Options[2][0] = "HQUMIN";
		Options[2][1] = "RNZNLH";
		Options[2][2] = "ANAEEG";
		Options[2][3] = "JAOBOB";
		Options[3][0] = "RLTYET";
		Options[3][1] = "DEXLIR";
		Options[3][2] = "FFSKAP";
		Options[3][3] = "AOSCPH";
	}

// Exported Operations
	/**
	 * Get if there are enough players on the Board to begin.
	 *
	 * @return	true if the board is ready. false otherwise.
	 */
	public boolean isReady() {
		return nextID >= 2;
	}

	/**
	 * Add a player to the board.
	 *
	 * @param	toAdd	Name of the player to add.
	 *
	 * @return	The id of the newly added player.
	 */
	public int addPlayer(String toAdd) {
		int toReturn = nextID;
		players[nextID] = toAdd;
		nextID++;
		return toReturn;
	}

	/**
	 * Get the list of player names.
	 *
	 * @return	list of player names.
	 */
	public String[] getPlayers() {
		return players;
	}

	/**
	 * Get the list of player scores.
 	 *
	 * @return	list of player scores.
 	 */
	public int[] getScores() {
		return scores;
	}

	/**
	 * Notify the board to submit the
	 * current word and score it.
	 */
	public void submitWord() {
		scores[turn] += word.length();
	}

	/**
	 * Change the state of a letter as selected and
	 * add that selection to the current word.
	 *
	 * @param	r	The letter's row.
	 * @param	c	The letter's column.
	 */
	public void select( int r, int c ) {
		if ( !selected[r][c] ) {
			selected[r][c] = true;
			char toAdd = spots[r][c];
			if ( toAdd == 'Q' ) {
				word += "QU";
			} else {
				word += toAdd;
			}
		}
	}

	/**
	 * Set all the letters to new values
	 * and set the current word to an empty string.
	 */
	public void resetBoard() {
		word = "";
		for ( int r = 0; r < ROWS; r++ ) {
			for ( int c = 0; c < COLS; c++ ) {
				spots[r][c] = Options[r][c].charAt( rand.nextInt( 6 ) );
				selected[r][c] = false;
			}
		}
	}

	/**
	 * Get which player's turn it is.
	 *
	 * @return 	The id of the user who's turn it is.
	 */
	public int getTurn() {
		return turn;
	}

	/**
	 * Change who's turn it is.
	 */
	public void changeTurn() {
		if( turn == 0 ) {
			turn = 1;
		} else {
			turn = 0;
		}
	}

	/**
	 * Retrieve the letter at a specific location.
	 *
	 * @param	row  Location for the letter's row.
	 * @param	col  Location for the letter's column.
	 *
	 * @return	The letter at the specified location.
	 */
	public char getCharAt( int row, int col ) {
		return spots[row][col];
	}
}
