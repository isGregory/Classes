// WordzModelProxy.java
// Author: Gregory Hoople

import java.util.Scanner;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Class Protocol provides the view listener for the Wordz network game,
 * which sends all messages to the server and sets up a thread to handle
 * all incoming messages.
 *
 * @author  Gregory Hoople
 * @version 2015-4-18
 */
public class Protocol implements ViewListener {

	// Socket connection to server.
	private Socket socket;

	// Output stream to server.
	private PrintStream out;

	// Input stream from server.
	private Scanner in;

	// Model Listener to process commands from server.
	private ModelListener mListen;

	/**
	 * Construct a new Protocol object.
	 * Set up streams to and from the server.
	 *
	 * @param  socket  Socket to the server.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public Protocol( Socket socket ) throws IOException {
		this.socket = socket;
		out = new PrintStream( socket.getOutputStream(), true );
		in = new Scanner( socket.getInputStream() );
	}

	/**
	 * Specify the local reference to the ModelListener object
	 *
	 * @param  mListen  ModelListener to send commands to.
	 */
	public void setModelListener( ModelListener mListen ) {
		this.mListen = mListen;
		new ReaderThread().start();
	}

	/**
	 * Notify the server User is joining.
	 *
	 * @param  name  Player's name.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void join( String name ) throws IOException {
		out.printf( "join %s%n", name );
	}

	/**
	 * Notify the server User selected a letter
	 *
	 * @param  r  Letter's row.
	 * @param  c  Letter's column.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void select( int r, int c ) throws IOException {
		out.printf( "letter %d %d%n", r, c );
	}

	/**
	 * Notify the server User selected "OK" button.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void sendOK() throws IOException {
		out.printf( "ok%n" );
	}

	/**
	 * Notify the server User is quitting.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void sendQuit() throws IOException {
		out.printf( "quit%n" );
	}

// Hidden helper class

	/**
	 * Class ReaderThread receives messages from the network, decodes them, and
	 * invokes the proper methods to process them.
	 *
	 * @author  Gregory Hoople
	 * @version 2015-4-18
	 */
	private class ReaderThread extends Thread {

		public void run() {
			try {
				while ( in.hasNextLine() ) {

					String next = in.nextLine();
					Scanner s = new Scanner (next);
					String op = s.next();

					if( op.equals( "id" ) ){

						int id = s.nextInt();
						mListen.setID( id );

					} else if ( op.equals( "name" ) ){

						int id = s.nextInt();
						String newName = s.next();
						mListen.setName( id, newName );

					} else if ( op.equals( "score" ) ){

						int id = s.nextInt();
						int score = s.nextInt();
						mListen.setScore( id, score );

					} else if ( op.equals( "available" ) ){

						int row = s.nextInt();
						int col = s.nextInt();
						char letter = s.next().charAt(0);
						mListen.setAvailable( row, col, letter );

					} else if ( op.equals( "chosen" ) ){

						int row = s.nextInt();
						int col = s.nextInt();
						mListen.setChosen( row, col );

					} else if ( op.equals( "turn" ) ){

						int id = s.nextInt();
						mListen.turn( id );

					} else if ( op.equals( "quit" ) ){

						mListen.quit();

					}
				}
			} catch ( IOException exc ) {

			} finally {
				try {
					socket.close();
				} catch ( IOException exc ) {}
			}
		}
	}
}
