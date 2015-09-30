/*
 * Chess.java
 *
 * $Id: Chess.java,v 1.6 2013/12/10 23:17:42 ghh8942 Exp ghh8942 $
 *
 * $Log: Chess.java,v $
 * Revision 1.6  2013/12/10 23:17:42  ghh8942
 * Updated board colors to checker.
 *
 * Revision 1.5  2013/12/09 21:18:03  ghh8942
 * Program Working. Reads Files.
 *
 * Revision 1.4  2013/12/09 20:59:09  ghh8942
 * Program runs except for reading files.
 *
 * Revision 1.3  2013/12/09 20:39:13  ghh8942
 * Program Compiles
 *
 * Revision 1.2  2013/12/09 20:32:52  ghh8942
 * Implemented methods. Doesn't compile yet.
 *
 * Revision 1.1  2013/12/09 19:06:31  ghh8942
 * Initial revision
 *
 *
 */

/**
 * Chess class.
 *
 * @author: Gregory Hoople
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*; //BufferedReader, FileReader, IOException

public class Chess extends JFrame implements Observer{

    private ChessModel myModel;

    /* Constructor
     * model = Chess Model object
     */
    public Chess (ChessModel model) {
        //Create frame
        super("Chess - Gregory Hoople - ghh8942");

        //Set the model
        myModel = model;

        //Add observer
        myModel.addObserver(this);

        //Set up basic stuff for the frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(100, 100);

        drawBoard();
    }//Chess

    public void drawBoard() {
        //Get the content pane
        Container myCon = getContentPane();
        myCon.removeAll();
        myCon.setPreferredSize(new Dimension(400, 450));
        myCon.setLayout(new BorderLayout());

        //Set up button pad
        int boardWidth = myModel.getBoardWidth();
        JLabel myButtons = new JLabel();
        myButtons.setLayout(new GridLayout(0, boardWidth));
        ArrayList<Integer> board = myModel.getCurBoard();

        //Set up color switcher for background checker pattern.
        int changeCols = 0;

        //Create the buttons
        for(int i = 0; i < board.size(); i++) {
            ChessButton toAdd = new ChessButton(i);
            if (i % boardWidth == 0) {
                changeCols = (changeCols + 1) % 2;
            }
            if ((i % boardWidth) % 2 == changeCols){
                toAdd.setBackground(Color.BLACK);
                toAdd.setForeground(Color.WHITE);
            } else {
                toAdd.setBackground(Color.WHITE);
                toAdd.setForeground(Color.BLACK);
            }
            toAdd.setText("" + (char)(int)board.get(i));
            toAdd.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    pressUnit(((ChessButton)e.getSource()).getPos());
                }
            });
            myButtons.add(toAdd);
        }

        //Set up top label
        String lblTxt = "Moves: " + myModel.getMoveCount() + "  ";
        if(!myModel.isWinnable()) {
            lblTxt += "You can not win at this point.";
        } else if (myModel.hasWon()) {
            lblTxt += "You've won!";
        } else {
	    switch(myModel.howManyUnitsUp()) {
	        case 0: lblTxt += "Select the first unit."; break;
	        case 1: lblTxt += "Select the second unit."; break;
	        case 2: lblTxt += "Nothing Selected: Select a unit."; break;
	    }
        }
        JLabel statusBar = new JLabel(lblTxt);
        statusBar.setForeground(Color.BLACK);
        statusBar.setBackground(Color.GRAY);
        statusBar.setHorizontalAlignment(SwingConstants.LEFT);

        //set up bottom buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.GRAY);
        buttonPanel.setLayout(new GridBagLayout());
        JButton quiB = new JButton("Quit");
        JButton resB = new JButton("Reset");
        JButton helB = new JButton("Help");
        JButton nxtB = new JButton("Next Move");

        //Add Listeners
        quiB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        resB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                myModel.reset();
            }
        });
        helB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                help();
            }
        });
        nxtB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                myModel.next();
            }
        });

        //Orient Bottom Buttons
        GridBagConstraints b = new GridBagConstraints();
        b.fill = GridBagConstraints.HORIZONTAL;
        b.ipadx = 10;
        b.ipady = 5;
        b.gridy = 0;
        b.insets = new Insets(5, 5, 5, 0);
        buttonPanel.add(quiB, b);
        buttonPanel.add(resB, b);
        buttonPanel.add(helB, b);
        buttonPanel.add(nxtB, b);

        //Add Components
        myCon.setBackground(Color.WHITE);
        myCon.add(statusBar, BorderLayout.NORTH);
        myCon.add(myButtons, BorderLayout.CENTER);
        myCon.add(buttonPanel, BorderLayout.SOUTH);

        //Display
        pack();
        setVisible(true);

    }//drawBoard

    public void update(Observable t, Object o) {
        drawBoard();
    }//update

    public void help() {
        //Handle a message dialog.
        JOptionPane.showMessageDialog(null, "Quit - Quits the game"
            + "\nReset - Resets game board"
            + "\nHelp - Displays this dialoge box"
            + "\nNext - Preforms the next best move"
            + "\n\nHow to Play:"
            + "\nSelect a unit followed by a unit"
            + "\nwhere you want the first selection"
            + "\nto move to. Try to clear all units"
            + "\nusing standard chess moves,"
            + "\nleaving only one remaining.");
    }//help

    public void pressUnit(int pos) {
        myModel.selectUnit(pos);
    }//pressUnit

    public static void main (String[] args) {
        //Start off with error checks
        if (args.length == 0) {
            //If there are not enough arguments
            //we alert the user and exit.
            System.err.println("Usage: java Chess input-file");
            System.exit(0);
        }

        Integer mRows = 5;
        Integer mCols = 5;
        ArrayList<Integer> buildBoard = new ArrayList<Integer>();
        try{
            //In this try block we try to open a file
            //and read in all the data in that file to
            //build up the gameboard

            //Check file name can be opened.
            BufferedReader openFile = new BufferedReader(
                new FileReader(args[0]));

            String curLine = "";
            String nextWord = "";

            curLine = openFile.readLine();
            String[] dimensions = curLine.split("\\s+");
            try{
                mRows = Integer.parseInt(dimensions[0]);
                mCols = Integer.parseInt(dimensions[1]);
            } catch (NumberFormatException e) {
                System.err.println("Error: Input File is " 
                    + "unexpectedly formatted.\n"
                    + "    First line should be two numbers");
            }
            //Scan through the file for units
            while (( curLine = openFile.readLine() ) != null) {
                //Slice up the line on all spaces.
                String[] split = curLine.split("\\s+");
                for (int i = 0; i < split.length; i++) {
                    buildBoard.add(new Integer(split[i].charAt(0)));

                }
            }
        } catch (IOException e) {
            //File couldn't open
            System.err.println("Cannot open file " + args[0] + ".");
            System.exit(0);
        }

        //After we've gotten through the error checks
        //And read in the board file,
        //we create the Water object
        ChessModel myChess = new ChessModel(mRows, mCols, buildBoard);

        Chess game = new Chess(myChess);
    }

} //Chess
