// ModelListener.java
// Author: Gregory Hoople

import java.io.IOException;

/**
 * Interface ModelListener specifies the interface for an
 * object that is triggered by events from the model object
 * in the Wordz server application.
 *
 * @author  Gregory Hoople
 * @version 2015-4-18
 */
public interface ModelListener {

	/**
	 * Set the User's assigned ID.
	 *
	 * @param  id  User's identification number
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void setID( int id ) throws IOException;

	/**
	 * Set the specified player's name based on their ID.
	 *
	 * @param  id    Player's identification number
	 * @param  name  Specified player's name
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void setName( int id, String name ) throws IOException;

	/**
	 * Set the specified player's score based on their ID.
	 *
	 * @param  id     Player's identification number
	 * @param  score  Specified player's score
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void setScore( int id, int score ) throws IOException;

	/**
	 * Set a letter to a specified button.
	 *
	 * @param  row     Location for the button's row.
	 * @param  col     Location for the button's column.
	 * @param  letter  Letter to set at that button.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void setAvailable( int row, int col, char letter )
		throws IOException;

	/**
	 * Set that a button has been chosen by a player.
	 *
	 * @param  row     Location for the button's row.
	 * @param  col     Location for the button's column.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void setChosen( int row, int col ) throws IOException;

	/**
	 * Specify who's turn it is currently.
	 *
	 * @param  id  Identification number of the
	 *             player who's turn it is.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void turn( int id ) throws IOException;

	/**
	 * Specify the other player has quit.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void quit() throws IOException;

}
