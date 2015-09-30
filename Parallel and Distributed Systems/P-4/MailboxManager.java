// MailboxManager.java

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.HashMap;

/**
 * Class MailboxManager is used by the WordzServer program. This class
 * keeps track of the view proxy objects handling the incoming datagrams
 * and sending them on to the appropriate view proxy.
 *
 * @authors Alan Kaminsky
 * 			Gregory Hoople
 * @version 2015-5-8
 */
public class MailboxManager {

// Hidden data members.
	private DatagramSocket mailbox;
	private HashMap<SocketAddress,ViewProxy> proxyMap =
		new HashMap<SocketAddress,ViewProxy>();
	private byte[] payload = new byte [128];
	private SessionManager sessionManager = new SessionManager();

// Exported constructors.

	/**
	 * Construct a new mailbox manager.
	 *
	 * @param	mailbox		Mailbox from which to read datagrams.
	 */
	public MailboxManager( DatagramSocket mailbox ) {
		this.mailbox = mailbox;
	}

// Exported operations.

	/**
	 * Receive and process the next datagram message.
	 *
	 * @exception	IOException
	 *		Thrown if an I/O error occured.
	 */
	public void receiveMessage() throws IOException {
		DatagramPacket packet = new DatagramPacket (payload, payload.length);
		mailbox.receive (packet);
		SocketAddress clientAddress = packet.getSocketAddress();
		ViewProxy proxy = proxyMap.get (clientAddress);
		if (proxy == null) {
			proxy = new ViewProxy (mailbox, clientAddress);
			proxy.setViewListener (sessionManager);
			proxyMap.put (clientAddress, proxy);
		}
		// Returns true to discard view proxy
		if ( proxy.process( packet ) ) {
			proxyMap.remove( clientAddress );
			// Check if that proxy was waiting to join a game.
			sessionManager.checkRemoveWaiting( proxy );
		}
	}
}
