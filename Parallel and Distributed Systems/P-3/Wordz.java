// Wordz.java
// Author: Gregory Hoople

import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.IOException;

/**
 * The Wordz program is a client program which connects to a server
 * and allows a user to play a multiplayer scrabble-like game.
 *
 * Usage: java Wordz <host> <port> <playername>
 *
 * @author Gregory Hoople
 * @version 2015-4-18
 */
public class Wordz {

	/*
	 * Main Program.
	 */
	public static void main( String[] args ) throws Exception {
		// Check for correct usage.
		if ( args.length != 3 ) {
			System.err.println( "Usage: java Wordz "
				+ "<host> <port> <playername>");
			System.exit( 1 );
		}

		// Collect arguments
		String host = args[0];
		int port = Integer.parseInt( args[1] );
		String name = args[2];

		// Set up connection
		Socket socket = new Socket();
		try {
			socket.connect( new InetSocketAddress( host, port ) );
		} catch ( IOException exc ) {
			System.err.println( "Unable to connect to "
				+ host + ":" + Integer.toString( port ) );
			System.exit( 1 );
		}

		// Set up program and proxies.
		ClientGUI view = new ClientGUI( name );
		Protocol proxy = new Protocol( socket );
		proxy.join( name );
		view.setViewListener( proxy );
		proxy.setModelListener( view );

	}
}
