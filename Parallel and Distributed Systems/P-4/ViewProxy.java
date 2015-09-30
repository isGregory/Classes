// ViewProxy.java

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

/**
 * Class ViewProxy provides a network proxy for the view object
 * in the game of Wordz. This view proxy resides in the server
 * program and communicates out with the client program.
 *
 * @author Gregory Hoople
 * @version 2015-5-9
 */
public class ViewProxy implements ModelListener {

// Hidden data members.
	private DatagramSocket mailbox;
	private SocketAddress clientAddress;
	private ViewListener vListener;

// Exported constructors.
	/**
	 * Construct a new view proxy.
	 *
	 * @param  mailbox        Server's mailbox.
	 * @param  clientAddress  Client's mailbox address.
	 */
	public ViewProxy (DatagramSocket mailbox, SocketAddress clientAddress) {
		this.mailbox = mailbox;
		this.clientAddress = clientAddress;
	}

// Exported operations.
	/**
	 * Set the view listener object for this view proxy.
	 *
	 * @param  viewListener  View listener.
	 */
	public void setViewListener (ViewListener vListener) {
		this.vListener = vListener;
	}

	/**
	 * Set the User's assigned ID.
	 *
	 * @param  id  User's identification number
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void setID( int id ) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( baos );
		out.writeByte( 'I' );
		out.writeInt( id );
		out.close();
		byte[] payload = baos.toByteArray();
		mailbox.send( new DatagramPacket(
			payload, payload.length, clientAddress ) );
	}

	/**
	 * Set the specified player's name based on their ID.
	 *
	 * @param  id    Player's identification number
	 * @param  name  Specified player's name
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void setName( int id, String name ) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( baos );
		out.writeByte( 'N' );
		out.writeInt( id );
		out.writeUTF( name );
		out.close();
		byte[] payload = baos.toByteArray();
		mailbox.send( new DatagramPacket(
			payload, payload.length, clientAddress ) );
	}

	/**
	 * Set the specified player's score based on their ID.
	 *
	 * @param  id     Player's identification number
	 * @param  score  Specified player's score
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void setScore( int id, int score ) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( baos );
		out.writeByte( 'S' );
		out.writeInt( id );
		out.writeInt( score );
		out.close();
		byte[] payload = baos.toByteArray();
		mailbox.send( new DatagramPacket(
			payload, payload.length, clientAddress ) );
	}

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
		throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( baos );
		out.writeByte( 'A' );
		out.writeByte( row );
		out.writeByte( col );
		out.writeByte( letter );
		out.close();
		byte[] payload = baos.toByteArray();
		mailbox.send( new DatagramPacket(
			payload, payload.length, clientAddress ) );
	}

	/**
	 * Set that a button has been chosen by a player.
	 *
	 * @param  row     Location for the button's row.
	 * @param  col     Location for the button's column.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void setChosen( int row, int col ) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( baos );
		out.writeByte( 'C' );
		out.writeByte( row );
		out.writeByte( col );
		out.close();
		byte[] payload = baos.toByteArray();
		mailbox.send( new DatagramPacket(
			payload, payload.length, clientAddress ) );

	}

	/**
	 * Specify who's turn it is currently.
	 *
	 * @param  id  Identification number of the
	 *             player who's turn it is.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void turn( int id ) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( baos );
		out.writeByte( 'T' );
		out.writeInt( id );
		out.close();
		byte[] payload = baos.toByteArray();
		mailbox.send( new DatagramPacket(
			payload, payload.length, clientAddress ) );
	}

	/**
	 * Specify the other player has quit.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void quit() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( baos );
		out.writeByte( 'Q' );
		out.close();
		byte[] payload = baos.toByteArray();
		mailbox.send( new DatagramPacket(
			payload, payload.length, clientAddress ) );
	}

	/**
	 * Process a received datagram.
	 *
	 * @param  datagram  Datagram.
	 *
	 * @return  True to discard this view proxy, false otherwise.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public boolean process (DatagramPacket datagram) throws IOException {
		boolean discard = false;
		DataInputStream in =
			new DataInputStream
				(new ByteArrayInputStream
					(datagram.getData(), 0, datagram.getLength()));
		String name;
		int r, c;
		byte b = in.readByte();
		switch ( b ) {
			// join
			case 'J':
				name = in.readUTF();
				vListener.join( ViewProxy.this, name );
				break;
			// select
			case 'S':
				r = in.readByte();
				c = in.readByte();
				vListener.select( r, c );
				break;
			// sendOK
			case 'K':
				vListener.sendOK();
				break;
			// sendQuit
			case 'Q':
				vListener.sendQuit();
				discard = true;
				break;
			default:
				System.err.println( "Bad message" );
				break;
		}
		return discard;
	}
}
