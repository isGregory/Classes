/*
 * Cell.java
 *
 * Abstract Cell Class
 *
 * Version:
 *     $Id: Cell.java,v 1.5 2013/09/19 22:47:43 ghh8942 Exp ghh8942 $
 *
 * Revisions:
 *     $Log: Cell.java,v $
 *     Revision 1.5  2013/09/19 22:47:43  ghh8942
 *     fixed protections
 *
 *     Revision 1.4  2013/09/19 22:09:24  ghh8942
 *     Added hit and value.
 *
 *     Revision 1.3  2013/09/19 21:56:28  ghh8942
 *     Added debug mode stuff.
 *
 *     Revision 1.2  2013/09/18 16:51:22  ghh8942
 *     Added "fireUpon" method.
 *
 *     Revision 1.1  2013/09/11 18:45:16  ghh8942
 *     Initial revision
 *
 *
 *
 */

public abstract class Cell {

    protected static boolean DEBUG_MODE = false;
    protected boolean isHit;
    protected char Value;

    public static void setDebug(){
        DEBUG_MODE = true;
    }

    public static void turnOffDebug(){
        DEBUG_MODE = false;
    }
    
    public abstract String toString();

    public abstract boolean fireUpon();

} //Cell
