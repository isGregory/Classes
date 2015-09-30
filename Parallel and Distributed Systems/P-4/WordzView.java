import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import java.io.IOException;

/**
 * Class WordzView provides the user interface for the Wordz network game.
 *
 * @author  Alan Kaminsky
 *  		Gregory Hoople
 * @version 2015-5-11
 */
public class WordzView implements ModelListener
{

	private static final int GAP = 10;
	private static final int COLS = 16;
	private static final Dimension DIM = new Dimension (40, 40);
	private static final Insets INSETS = new Insets (0, 0, 0, 0);
	private static final Font FONT = new Font ("sans-serif", Font.BOLD, 18);
	private static final int GRID_ROWS = 4;
	private static final int GRID_COLS = 4;

	/**
	 * Class LetterButton provides a button labeled with a letter.
	 * To enable the button, call setEnabled (true).
	 * To disable the button, call setEnabled (false).
	 * To show the button, call setVisible (true).
	 * To hide the button, call setVisible (false).
	 */
	private class LetterButton
		extends JButton
		{
		private int row;
		private int col;
		private String letters;

		/**
		 * Construct a new letter button.
		 *
		 * @param  r  Button's row.
		 * @param  c  Button's column.
		 */
		public LetterButton
			(int r,
			 int c)
			{
			super ("");
			row = r;
			col = c;
			setEnabled (false);
			setVisible (true);
			setMinimumSize (DIM);
			setMaximumSize (DIM);
			setPreferredSize (DIM);
			setMargin (INSETS);
			setFont (FONT);

			// Clicking the letter button executes this listener:
			addActionListener (new ActionListener()
				{
				public void actionPerformed (ActionEvent e)
					{
					// Notify the server a button has been chosen.
					alertChosen( row, col );
					}
				});
			}

		/**
		 * Set the letter displayed on this letter button.
		 *
		 * @param  letter  Letter ('A' through 'Z').
		 */
		public void setLetter
			(char letter)
			{
			letters = letter == 'Q' ? "QU" : ""+letter;
			setText (letters);
			}

		/**
		 * Get the letter or letters displayed on this letter button.
		 *
		 * @return  Letter(s).
		 */
		public String getLetters()
			{
			return letters;
			}

		}

	/**
	 * User interface widgets.
	 */
	private JFrame frame;
	private LetterButton[][] letterButton;
	private JTextField myScoreField;
	private JTextField theirScoreField;
	private JTextField wordField;
	private JButton okButton;


	// Private Data Members
	private int myID;				// User's ID number
	private String myName;			// User's name
	private String theirName;		// Other player's name
	private int myScore;			// User's score
	private int theirScore;			// Other player's score
	private ViewListener vListen;	// View Listener Object

	/**
	 * Construct a new WordzView object.
	 *
	 * @param  myName  Player's name.
	 */
	private WordzView
		(String myName)
		{
		frame = new JFrame ("Wordz -- " + myName);
		this.myName = myName;

		JPanel panel = new JPanel();
		frame.add (panel);
		panel.setLayout (new BoxLayout (panel, BoxLayout.X_AXIS));
		panel.setBorder (BorderFactory.createEmptyBorder (GAP, GAP, GAP, GAP));

		JPanel panel_a = new JPanel();
		panel.add (panel_a);
		panel_a.setLayout (new GridLayout (GRID_ROWS, GRID_COLS));
		panel_a.setAlignmentY (0.0f);
		letterButton = new LetterButton [GRID_ROWS] [GRID_COLS];
		for (int row = 0; row < GRID_ROWS; ++ row)
			for (int col = 0; col < GRID_COLS; ++ col)
				panel_a.add
					(letterButton[row][col] = new LetterButton (row, col));

		panel.add (Box.createHorizontalStrut (GAP));
		JPanel panel_b = new JPanel();
		panel.add (panel_b);
		panel_b.setLayout (new BoxLayout (panel_b, BoxLayout.Y_AXIS));
		panel_b.setAlignmentY (0.0f);
		panel_b.add (myScoreField = new JTextField (COLS));
		myScoreField.setAlignmentX (0.5f);
		myScoreField.setEditable (false);
		myScoreField.setMaximumSize (myScoreField.getPreferredSize());
		myScoreField.setAlignmentX (0.0f);
		panel_b.add (Box.createRigidArea (new Dimension (0, GAP)));
		panel_b.add (theirScoreField = new JTextField (COLS));
		theirScoreField.setAlignmentX (0.5f);
		theirScoreField.setEditable (false);
		theirScoreField.setMaximumSize (theirScoreField.getPreferredSize());
		theirScoreField.setAlignmentX (0.0f);
		panel_b.add (Box.createVerticalGlue());

		JPanel panel_c = new JPanel();
		panel_b.add (panel_c);
		panel_c.setLayout (new BoxLayout (panel_c, BoxLayout.X_AXIS));
		panel_c.setAlignmentX (0.0f);
		panel_c.add (wordField = new JTextField (COLS));
		wordField.setAlignmentY (0.5f);
		wordField.setEditable (false);
		wordField.setMaximumSize (wordField.getPreferredSize());
		panel_c.add (Box.createRigidArea (new Dimension (GAP, 0)));
		panel_c.add (okButton = new JButton ("OK"));
		okButton.setAlignmentX (0.5f);
		okButton.setMaximumSize (okButton.getPreferredSize());
		okButton.setEnabled (false);

		// Clicking the OK button executes this listener:
		okButton.addActionListener (new ActionListener()
			{
			public void actionPerformed (ActionEvent e)
				{
				// Notify the server ok has been clicked.
				alertOK();
				}
			});

		// Closing the window executes this listener:
		frame.addWindowListener (new WindowAdapter()
			{
			public void windowClosing (WindowEvent e)
				{
				// Notify the server of a quit and then
				// close the program.
				alertQuit();
				System.exit(0);
				}
			});

		frame.pack();
		frame.setVisible (true);
		}

	/**
	 * An object holding a reference to a Wordz UI.
	 */
	private static class WordzViewRef
		{
		public WordzView view;
		}

	/**
	 * Construct a new WordzView object.
	 *
	 * @param  myName  Player's name.
	 *
	 * @return  New WordzView object.
	 */
	public static WordzView create
		(final String myName)
		{
		final WordzViewRef ref = new WordzViewRef();
		onSwingThreadDo (new Runnable()
			{
			public void run()
				{
				ref.view = new WordzView (myName);
				}
			});
		return ref.view;
		}

	/**
	 * Execute the given runnable object on the Swing thread.
	 */
	private static void onSwingThreadDo
		(Runnable task)
		{
		try
			{
			SwingUtilities.invokeAndWait (task);
			}
		catch (Throwable exc)
			{
			exc.printStackTrace (System.err);
			System.exit (1);
			}
		}

	/**
	 * Set a local reference to the ViewListener
	 *
	 * @param  vListen  ViewListener to communicate with.
	 */
	public synchronized void setViewListener( ViewListener vListen ) {
		this.vListen = vListen;
	}

	/**
	 * Set the User's assigned ID.
	 *
	 * @param  id  User's identification number
	 */
	public synchronized void setID( int id ) {
		myID = id;
	}

	/**
	 * Set the specified player's name based on their ID.
	 *
	 * @param  id    Player's identification number
	 * @param  name  Specified player's name
	 */
	public synchronized void setName( int id, String name ) {
		if ( id == myID ) {
			myName = name;
			setMyInfo( myName + " = "
				+ Integer.toString( myScore ) );
		} else {
			theirName = name;
			setTheirInfo( theirName + " = "
				+ Integer.toString( theirScore ) );
		}
	}

	/**
	 * Set the specified player's score based on their ID.
	 *
	 * @param  id     Player's identification number
	 * @param  score  Specified player's score
	 */
	public synchronized void setScore( int id, int score ) {
		if ( id == myID ) {
			myScore = score;
			setMyInfo( myName + " = "
				+ Integer.toString( myScore ) );
		} else {
			theirScore = score;
			setTheirInfo( theirName + " = "
				+ Integer.toString( theirScore ) );
		}
	}

	/**
	 * Set a letter to a specified button.
	 *
	 * @param  row     Location for the button's row.
	 * @param  col     Location for the button's column.
	 * @param  letter  Letter to set at that button.
	 */
	public synchronized void setAvailable( final int row,
			final int col, final char letter ) {
		onSwingThreadDo (new Runnable() {
			public void run() {
				letterButton[row][col].setLetter( letter );
			}
		});
	}

	/**
	 * Set that a button has been chosen and update the Word text.
	 *
	 * @param  row     Location for the button's row.
	 * @param  col     Location for the button's column.
	 */
	public synchronized void setChosen( final int row, final int col ) {
		onSwingThreadDo (new Runnable() {
			public void run() {
				// Hide the button.
				letterButton[row][col].setVisible( false );

				// Update the Word text.
				String cWord = wordField.getText();
				cWord += letterButton[row][col].getLetters();
				wordField.setText( cWord );
			}
		});
	}

	/**
	 * Specify who's turn it is currently.
	 *
	 * @param  id  Identification number of the
	 *             player who's turn it is.
	 */
	public synchronized void turn( int id ) {
		final boolean myTurn = ( id == myID );
		onSwingThreadDo (new Runnable() {
			public void run() {
				// Go through all buttons and reset their visiblity
				// and specify enables based on who's turn it is.
				for ( int row = 0; row < GRID_ROWS; row++ ) {
					for ( int col = 0; col < GRID_COLS; col++ ) {
						letterButton[row][col].setEnabled( myTurn );
						letterButton[row][col].setVisible( true );
					}
				}
				// Reset word field
				wordField.setText( "" );
				okButton.setEnabled( myTurn );
			}
		});
	}

	/**
	 * Specify the other player has quit.
	 */
	public synchronized void quit() {
		System.exit(0);
	}

	/**
	 * Set the user's information text box
	 *
	 * @param  toSet  Current user's info
	 */
	private synchronized void setMyInfo( final String toSet ) {
		onSwingThreadDo (new Runnable() {
			public void run() {
				myScoreField.setText( toSet );
			}
		});
	}

	/**
	 * Set the other player's information text box
	 *
	 * @param  toSet  Other user's info
	 */
	private synchronized void setTheirInfo( final String toSet ) {
		onSwingThreadDo (new Runnable() {
			public void run() {
				theirScoreField.setText( toSet );
			}
		});
	}

	/**
	 * Notify the server that a button has been chosen
	 *
	 * @param  r  Button Row
	 * @param  c  Button Column
	 */
	public synchronized void alertChosen( int r, int c ) {
		try {
			vListen.select( r, c );
		} catch (IOException exc) {
			errorIOError();
		}
	}

	/**
	 * Notify the server that the "OK" button has been selected.
	 */
	public synchronized void alertOK() {
		try {
			vListen.sendOK();
		} catch (IOException exc) {
			errorIOError();
		}
	}

	/**
	 * Notify the server that the User has selected to quit.
	 */
	public synchronized void alertQuit() {
		try {
			vListen.sendQuit();
		} catch (IOException exc) {
			errorIOError();
		}
	}

	/**
	 * Function to handle IO Errors and alert the user.
	 */
	private void errorIOError() {
		JOptionPane.showMessageDialog
			(/*parentComponent*/ frame,
			/*message        */ "I/O error when sending to server",
			/*title          */ "I/O error",
			/*messageType    */ JOptionPane.ERROR_MESSAGE);
		System.exit (0);
	}
}
