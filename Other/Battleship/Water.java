/*
 * Water.java
 *
 * Water Class
 *
 * Version:
 *     $Id: Water.java,v 1.3 2013/09/19 22:08:57 ghh8942 Exp ghh8942 $
 *
 * Revisions:
 *     $Log: Water.java,v $
 *     Revision 1.3  2013/09/19 22:08:57  ghh8942
 *     Updated to take over misses.
 *
 *     Revision 1.2  2013/09/18 16:51:39  ghh8942
 *     Added "fireUpon" method.
 *
 *     Revision 1.1  2013/09/11 18:48:39  ghh8942
 *     Initial revision
 *
 *
 *
 */

public class Water extends Cell {
    
    public Water(){
        Value = '-';
        isHit = false;
    } //Water()

    public String toString(){
        if(DEBUG_MODE){
            return "" + '-';
        }else{
            return "" + Value;
        }
    } //toString()

    public boolean fireUpon(){
        //If it hasn't previously been hit the return value is
        //changed to a missed missile.
        if(!isHit){
            Value = 'O';
            isHit = true;
        }
        return false;
    } //fireUpon()

} //Water
