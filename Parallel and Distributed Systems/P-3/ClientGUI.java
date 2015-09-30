// ClientGUI.java
// Author: Gregory Hoople


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

/**
 * Class ClientGUI provides the model listener for the Wordz network game,
 * which handles the functions that correspond to incoming messages from
 * the server.
 *
 * @author  Gregory Hoople
 * @version 2015-4-18
 */
public class ClientGUI implements ModelListener {

	// Main GUI jframe
	private WordzView frame;

	// User's ID number
	private int myID;

	// User's name
	private String myName;

	// Other player's name
	private String theirName;

	// User's score
	private int myScore;

	// Other player's score
	private int theirScore;

	// Object to send commands to the server
	private ViewListener vListen;

	/**
	 * Construct a new Protocol object.
	 * Set up streams to and from the server.
	 *
	 * @param  name  Player's name.
	 */
	public ClientGUI ( String name ) {
		frame = new WordzView( name );

		frame.setVisible(true);
	}

	/**
	 * Specify the local reference to the ViewListener object
	 *
	 * @param  vListen  ViewListener to send commands to.
	 */
	public synchronized void setViewListener( ViewListener vListen ) {
		this.vListen = vListen;
		frame.setViewListener( vListen );
	}

	/**
	 * Set the User's assigned ID.
	 *
	 * @param  id  User's identification number
	 */
	public synchronized void setID( int id ) {
		myID = id;
	}

	/**
	 * Set the specified player's name based on their ID.
	 *
	 * @param  id    Player's identification number
	 * @param  name  Specified player's name
	 */
	public synchronized void setName( int id, String name ) {
		if ( id == myID ) {
			myName = name;
			frame.setMyInfo( myName + " = "
				+ Integer.toString( myScore ) );
		} else {
			theirName = name;
			frame.setTheirInfo( theirName + " = "
				+ Integer.toString( theirScore ) );
		}
	}

	/**
	 * Set the specified player's score based on their ID.
	 *
	 * @param  id     Player's identification number
	 * @param  score  Specified player's score
	 */
	public synchronized void setScore( int id, int score ) {
		if ( id == myID ) {
			myScore = score;
			frame.setMyInfo( myName + " = "
				+ Integer.toString( myScore ) );
		} else {
			theirScore = score;
			frame.setTheirInfo( theirName + " = "
				+ Integer.toString( theirScore ) );
		}
	}

	/**
	 * Set a letter to a specified button.
	 *
	 * @param  row     Location for the button's row.
	 * @param  col     Location for the button's column.
	 * @param  letter  Letter to set at that button.
	 */
	public synchronized void setAvailable( int row, int col, char letter ) {
		frame.setLetter( row, col, letter );
	}

	/**
	 * Set that a button has been chosen by a player.
	 *
	 * @param  row     Location for the button's row.
	 * @param  col     Location for the button's column.
	 */
	public synchronized void setChosen( int row, int col ) {
		frame.choose( row, col );
	}

	/**
	 * Specify who's turn it is currently.
	 *
	 * @param  id  Identification number of the
	 *             player who's turn it is.
	 */
	public synchronized void turn( int id ) {
		boolean toSet = ( id == myID );

		frame.changeTurn( toSet );
	}

	/**
	 * Specify the other player has quit.
	 */
	public void quit() {
		System.exit(0);
	}

}
