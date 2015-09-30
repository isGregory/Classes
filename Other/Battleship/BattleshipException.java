/*
 * BattleshipException.java
 *
 *
 * Version:
 *     $Id: BattleshipException.java,v 1.2 2013/09/18 18:10:54 ghh8942 Exp ghh8942 $
 *
 * Revisions:
 *     $Log: BattleshipException.java,v $
 *     Revision 1.2  2013/09/18 18:10:54  ghh8942
 *     Missed a '}' at the end.
 *
 *     Revision 1.1  2013/09/18 17:36:14  ghh8942
 *     Initial revision
 *
 * 
 * Author: Gregory Hoople
 *
 */

public class BattleshipException extends Exception{

    public BattleshipException(){
    }

    public BattleshipException(String message){
        super(message);
    }
}
