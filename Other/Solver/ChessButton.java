/*
 * ChessButton.java
 *
 * $Id: ChessButton.java,v 1.2 2013/12/09 19:33:42 ghh8942 Exp ghh8942 $
 *
 * $Log: ChessButton.java,v $
 * Revision 1.2  2013/12/09 19:33:42  ghh8942
 * Implemented possition function
 *
 * Revision 1.1  2013/12/09 19:31:10  ghh8942
 * Initial revision
 *
 *
 */

/**
 * ChessButton class.
 *
 * @author: Gregory Hoople
 */

import java.awt.*;
import javax.swing.*;

public class ChessButton extends JButton{

    private int myPos;

    public ChessButton(int pos) {
        myPos = pos;
    }

    public int getPos() {
        return myPos;
    }

} //ChessButton
