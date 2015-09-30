// ViewListener.java
// Author: Gregory Hoople

import java.io.IOException;

/**
 * Interface ViewListener specifies the interface for an object that
 * is triggered by events from user input in the Wordz client.
 *
 * @author  Gregory Hoople
 * @version 2015-5-9
 */
public interface ViewListener {

	/**
	 * Report the player name of the player joining.
	 *
	 * @param  name  Player's Name
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void join( ViewProxy proxy, String name ) throws IOException;

	/**
	 * Report the player has selected a letter.
	 *
	 * @param  row  Letter's Row
	 * @param  col  Letter's Column
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void select( int row, int col ) throws IOException;

	/**
	 * Report the player clicked the OK button.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void sendOK() throws IOException;


	/**
	 * Report the player is quitting.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void sendQuit() throws IOException;

}
