// Wordz.java
// Author: Gregory Hoople

import java.net.InetSocketAddress;
import java.net.DatagramSocket;
import java.io.IOException;

/**
 * The Wordz program is a client program which connects to a server
 * and allows a user to play a multiplayer scrabble-like game.
 * <P>
 * Usage: java Wordz <I>serverhost</I> <I>serverport</I>
 * <I>clienthost</I> <I>clientport</I> <I>playername</I>
 *
 * @author Gregory Hoople
 * @version 2015-5-7
 */
public class Wordz {

	/*
	 * Main Program.
	 */
	public static void main( String[] args ) throws Exception {
		// Check for correct usage.
		if ( args.length != 5 ) {
			System.err.println( "Usage: java Wordz <serverhost> <serverport>"
				+ " <clienthost> <clientport> <playername>");
			System.exit( 1 );
		}

		// Collect arguments
		String host = args[0];
		int port = Integer.parseInt( args[1] );
		String clienthost = args[2];
		int clientport = Integer.parseInt( args[3] );
		String name = args[4];

		try {
			// Set up datagram mailbox
			DatagramSocket mailbox =
		         	new DatagramSocket
		            	(new InetSocketAddress (clienthost, clientport));

			// Set up program and proxies.
			WordzView view = WordzView.create( name );
			final ModelProxy proxy =
		         	new ModelProxy
		            	(mailbox, new InetSocketAddress (host, port));
			view.setViewListener( proxy );
			proxy.setModelListener( view );

			Runtime.getRuntime().addShutdownHook (new Thread()
			{
				public void run() {
					try {
						proxy.sendQuit();
					} catch (IOException exc) {}
				}
			});
			proxy.join (null, name);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit( 1 );
		}
	}
}
