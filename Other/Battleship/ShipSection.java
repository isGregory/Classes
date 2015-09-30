/*
 * ShipSection.java
 *
 * ShipSection Class
 *
 * Version:
 *     $Id: ShipSection.java,v 1.7 2013/09/19 22:09:46 ghh8942 Exp ghh8942 $
 *
 * Revisions:
 *     $Log: ShipSection.java,v $
 *     Revision 1.7  2013/09/19 22:09:46  ghh8942
 *     Implemented debug mode.
 *
 *     Revision 1.6  2013/09/18 17:08:08  ghh8942
 *     Added and implemented Ship stuff.
 *
 *     Revision 1.5  2013/09/18 16:51:56  ghh8942
 *     Added "fireUpon" method.
 *
 *     Revision 1.4  2013/09/13 17:20:40  ghh8942
 *     Fixed Return Type.
 *
 *     Revision 1.3  2013/09/13 16:23:14  ghh8942
 *     Added ability to check if hit and set hit
 *
 *     Revision 1.2  2013/09/11 19:21:30  ghh8942
 *     fixed it to compile.
 *
 *     Revision 1.1  2013/09/11 18:57:12  ghh8942
 *     Initial revision
 *
 *
 * Author: Gregory Hoople
 *
 */

public class ShipSection extends Cell {
    private char ShipName;
    private Ship myShip;
    
    public ShipSection(char Name, Ship belongsTo){
        ShipName = Name;
        myShip = belongsTo;
        isHit = false;
        Value = '-';
    } //ShipSection()

    public boolean fireUpon(){
        if(!isHit){
            Value = 'X';
            isHit = true;
            //notify ship class.
            myShip.subHit();
            return true;
        }
        return false;
    } //fireUpon()

    public boolean isSunk(){
        //Checks if the ship has been sunk.
        return myShip.isSunk();
    } //isSunk()

    public String toString(){
        if(DEBUG_MODE){
            return "" + ShipName;
        }else{
            return "" + Value;
        }
    } //toString()

} //ShipSection
