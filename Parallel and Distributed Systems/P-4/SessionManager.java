// SessionManager.java

import java.io.IOException;
import java.util.Vector;

/**
 * Class SessionManager keeps track of the sessions model objects in
 * the WordzServer program.
 *
 * @author Gregory Hoople
 * @version 2015-5-9
 */
public class SessionManager implements ViewListener {

// Hidden Data Members
	private Vector<WordzModel> sessions = new Vector<WordzModel>();
	ViewProxy waitingProxy;
	String	waitingName = "";
	WordzModel model;


// Exported Constructors

	/**
	 * Construct a new session manager.
	 */
	public SessionManager() {

	}

// Exported Operations

	/**
	 * Check to make sure a waiting player did not quit.
	 *
	 * @param	ViewProxy	View Proxy of the player that disconnected.
	 */
	public void checkRemoveWaiting( ViewProxy proxy ) {
		if( waitingProxy == proxy ) {
			waitingName = "";
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
	public void join( ViewProxy proxy, String name ) throws IOException {
		if ( waitingName != "" ) {
			model.addModelListener( proxy, name );
			proxy.setViewListener( model );
			sessions.add(model);
			waitingName = "";
		} else {
			model = new WordzModel();
			model.addModelListener( proxy, name );
			proxy.setViewListener( model );
			waitingProxy = proxy;
			waitingName = name;
		}
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

	}

	/**
	 * Report the player clicked the OK button.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void sendOK() {

	}

	/**
	 * Report the player is quitting.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void sendQuit() {

	}

}
