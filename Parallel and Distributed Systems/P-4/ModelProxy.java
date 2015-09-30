// ModelProxy.java

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

/**
 * Class ModelProxy provides a network proxy for the model object
 * in the game of Wordz. This model proxy resides in the client
 * program and communicates out with the server.
 *
 * @author Gregory Hoople
 * @version 2015-5-9
 */
public class ModelProxy implements ViewListener {

// Hidden Data Members
	private DatagramSocket mailbox;
	private SocketAddress destination;
	private ModelListener mListener;

// Exported Constructors
	/**
	 * Construct a new model proxy.
	 *
	 * @param	mailbox		Mailbox.
	 * @param	destination  Destination mailbox address.
	 */
	public ModelProxy (DatagramSocket mailbox, SocketAddress destination)
		throws IOException{

		this.mailbox = mailbox;
		this.destination = destination;
	}

// Exported Operations
	/**
	 * Set the model listener object for this model proxy.
	 *
	 * @param	modelListener	Model Listener.
	 */
	public void setModelListener( ModelListener mListener ) {
		this.mListener = mListener;
		new ReaderThread().start();
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
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( baos );
		out.writeByte( 'J' );
		out.writeUTF( name );
		out.close();
		byte[] payload = baos.toByteArray();
		mailbox.send( new DatagramPacket(
			payload, payload.length, destination ) );
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
	public void select( int row, int col ) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( baos );
		out.writeByte( 'S' );
		out.writeByte( row );
		out.writeByte( col );
		out.close();
		byte[] payload = baos.toByteArray();
		mailbox.send( new DatagramPacket(
			payload, payload.length, destination ) );
	}

	/**
	 * Report the player clicked the OK button.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void sendOK() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( baos );
		out.writeByte( 'K' );
		out.close();
		byte[] payload = baos.toByteArray();
		mailbox.send( new DatagramPacket(
			payload, payload.length, destination ) );
	}


	/**
	 * Report the player is quitting.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void sendQuit() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( baos );
		out.writeByte( 'Q' );
		out.close();
		byte[] payload = baos.toByteArray();
		mailbox.send( new DatagramPacket(
			payload, payload.length, destination ) );
	}

	// Hidden helper classes.

	/**
	 * Class ReaderThread receives messages from the network, decodes them, and
	 * invokes the proper methods to process them.
	 *
	 * @author Gregory Hoople
	 * @version 2015-5-9
	 */
	private class ReaderThread extends Thread {
		public void run() {
			byte[] payload = new byte [128]; /* CAREFUL OF BUFFER SIZE! */
			try {
				for (;;) {
					DatagramPacket packet =
						new DatagramPacket( payload, payload.length );
					mailbox.receive( packet );
					DataInputStream in =
						new DataInputStream(
							new ByteArrayInputStream(
								payload, 0, packet.getLength() ) );
					int r, c, id, score;
					String name;
					char letter;
					byte b = in.readByte();
					switch ( b ) {
						// setID
						case 'I':
							id = in.readInt();
							mListener.setID( id );
							break;
						// setName
						case 'N':
							id = in.readInt();
							name = in.readUTF();
							mListener.setName( id, name );
							break;
						// setScore
						case 'S':
							id = in.readInt();
							score = in.readInt();
							mListener.setScore( id, score );
							break;
						// setAvailable
						case 'A':
							r = in.readByte();
							c = in.readByte();
							letter = (char)in.readByte();
							mListener.setAvailable( r, c, letter );
							break;
						// setChosen
						case 'C':
							r = in.readByte();
							c = in.readByte();
							mListener.setChosen( r, c );
							break;
						// turn
						case 'T':
							id = in.readInt();
							mListener.turn( id );
							break;
						// quit
						case 'Q':
							// Quit
							mListener.quit();
							break;
						default:
							System.err.println( "Bad message" );
							break;
					}
				}
			} catch ( IOException exc ) {
			} finally {
				mailbox.close();
			}
		}
	}
}
