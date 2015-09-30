import java.io.IOException;
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
import javax.swing.JOptionPane;

/**
 * Class WordzView provides the user interface for the Wordz network game.
 *
 * @authors  Alan Kaminsky
 *           Gregory Hoople
 * @version 2015-4-18
 */
public class WordzView
	extends JFrame
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
	private String myName;
	private LetterButton[][] letterButton;
	private JTextField myScoreField;
	private JTextField theirScoreField;
	private JTextField wordField;
	private JButton okButton;
	private ViewListener vListen;

	/**
	 * Construct a new WordzView object.
	 *
	 * @param  myName  Player's name.
	 */
	public WordzView
		(String myName)
		{
		super ("Wordz -- " + myName);
		this.myName = myName;

		JPanel panel = new JPanel();
		add (panel);
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
		addWindowListener (new WindowAdapter()
			{
			public void windowClosing (WindowEvent e)
				{
				// Notify the server of a quit and then
				// close the program.
				alertQuit();
				System.exit(0);
				}
			});

		pack();
		}

	/**
	 * Set a local reference to the ViewListener
	 *
	 * @param  vListen  ViewListener to communicate with.
	 */
	public void setViewListener( ViewListener vListen ) {
		this.vListen = vListen;
	}

	/**
	 * Set the user's information text box
	 *
	 * @param  toSet  Current user's info
	 */
	public void setMyInfo( String toSet ) {
		myScoreField.setText( toSet );
	}

	/**
	 * Set the other player's information text box
	 *
	 * @param  toSet  Other user's info
	 */
	public void setTheirInfo( String toSet ) {
		theirScoreField.setText( toSet );
	}

	/**
	 * Set specific button to a certain letter
	 *
	 * @param  r       Button Row
	 * @param  c       Button Column
	 * @param  letter  Letter to be set
	 */
	public void setLetter( int r, int c, char letter ) {
		letterButton[r][c].setLetter( letter );
	}

	/**
	 * Set a button as chosen and update the Word text.
	 *
	 * @param  r  Button Row
	 * @param  c  Button Column
	 */
	public void choose( int r, int c ) {
		// Hide the button.
		letterButton[r][c].setVisible( false );

		// Update the Word text.
		String cWord = wordField.getText();
		cWord += letterButton[r][c].getLetters();
		wordField.setText( cWord );
	}

	/**
	 * Change who's turn the game board is on.
	 *
	 * @param  myTurn  'true' if it's the local user's turn.
	 */
	public void changeTurn( boolean myTurn ) {
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
			(/*parentComponent*/ this,
			 /*message        */ "I/O error when sending to server",
			 /*title          */ "I/O error",
			 /*messageType    */ JOptionPane.ERROR_MESSAGE);
		System.exit (0);
	}

	}
