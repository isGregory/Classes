// WordzModel.java

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Class WordzModel provides the server-side model object in the Wordz game.
 *
 * @author Gregory Hoople
 * @version 2015-5-9
 */
public class WordzModel implements ViewListener {

// Hidden Data Members
	private WordzBoard board = new WordzBoard();
	private LinkedList<ModelListener> listeners =
			new LinkedList<ModelListener>();

// Exported Constructors
	/**
	 * Construct a new Wordz Model
	 */
	public WordzModel() {

	}

// Exported Operations
	/**
	 * Add the given model listener to this Wordz Model, and add
	 * the given player name to the board.
	 *
	 * @param	mListener	Model Listener
	 * @param	name		Player's name
	 */
	public synchronized void addModelListener( ModelListener mListener, String name ) {
		try {
			// Send the new client their ID
			int id = board.addPlayer( name );
			mListener.setID( id );
			mListener.setName( id, name );
			mListener.setScore( id, 0 );

			// Record the listener
			listeners.add( mListener );

			if( board.isReady() ) {
				board.resetBoard();
				board.changeTurn();
				int turn = board.getTurn();

				// Report the initial board to all clients
				Iterator<ModelListener> iter = listeners.iterator();
				while ( iter.hasNext() ) {
					ModelListener listener = iter.next();
					try {
						listener.turn( turn );
						String[] names = board.getPlayers();
						for ( int i = 0; i < names.length; i++ ){
							listener.setName( i, names[i] );
						}

						int[] scores = board.getScores();
						for ( int i = 0; i < scores.length; i++ ){
							listener.setScore( i, scores[i] );
						}

						for( int r = 0; r < board.ROWS; r++ ){
							for( int c = 0; c < board.COLS; c++ ) {
								listener.setAvailable( r, c, board.getCharAt( r,c ) );
							}
						}
					} catch (IOException exc) {
						// Client failed, stop reporting to it.
						iter.remove();
						sendQuit();
					}
				}
			}
		} catch( IOException exc ) {
		}
	}

	/**
	 * Report the player name of the player joining.
	 *
	 * @param  name  Player's Name
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void join( ViewProxy proxy, String name ) {

	}

	/**
	 * Report the player has selected a letter.
	 *
	 * @param  row  Letter's Row
	 * @param  col  Letter's Column
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void select( int row, int col ) {
		board.select(row, col);

		Iterator<ModelListener> iter = listeners.iterator();
		while ( iter.hasNext() ) {
			ModelListener listener = iter.next();
			try {
				listener.setChosen( row, col );
			} catch (IOException exc) {
				// Client failed, stop reporting to it.
				iter.remove();
				sendQuit();
			}
		}
	}

	/**
	 * Report the player clicked the OK button.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void sendOK() {
		board.submitWord();
		board.resetBoard();
		board.changeTurn();
		int turn = board.getTurn();

		Iterator<ModelListener> iter = listeners.iterator();
		while ( iter.hasNext() ) {
			ModelListener listener = iter.next();
			try {
				int[] scores = board.getScores();
				for ( int i = 0; i < scores.length; i++ ){
					listener.setScore( i, scores[i] );
				}

				for( int r = 0; r < board.ROWS; r++ ){
					for( int c = 0; c < board.COLS; c++ ) {
						listener.setAvailable( r, c, board.getCharAt( r,c ) );
					}
				}
				listener.turn( turn );
			} catch (IOException exc) {
				// Client failed, stop reporting to it.
				iter.remove();
				sendQuit();
			}
		}
	}

	/**
	 * Report the player is quitting.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void sendQuit() {
		Iterator<ModelListener> iter = listeners.iterator();
		while ( iter.hasNext() ) {
			ModelListener listener = iter.next();
			try {
				listener.quit();
			} catch (IOException exc) {
				// Client failed, stop reporting to it.
				iter.remove();
			}
		}
	}
}
