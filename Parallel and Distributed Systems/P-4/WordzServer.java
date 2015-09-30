// WordzServer.java
import java.net.InetSocketAddress;
import java.net.DatagramSocket;

/**
 * Class WordzServer is a server main program which allows users to
 * connect and play a multiplayer scrabble-like game. The command
 * line arguments specify the host and port to which the server
 * should listen for connections
 * <P>
 * Usage: java Wordz <I>serverhost</I> <I>serverport</I>
 *
 * @author Gregory Hoople
 * @version 2015-5-9
 */
public class WordzServer {

	/**
	 * Main program.
	 */
	public static void main( String[] args ) throws Exception {
		if ( args.length != 2 ) {
			System.err.println( "Usage: java WordzServer"
				+ " <serverhost> <serverport>" );
		}
		String host = args[0];
		int port = Integer.parseInt( args[1] );

		DatagramSocket mailbox = new DatagramSocket(
				new InetSocketAddress( host, port ) );

		MailboxManager manager = new MailboxManager( mailbox );

		for( ;; ) {
			manager.receiveMessage();
		}
	}
}
