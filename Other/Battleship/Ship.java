/*
 * Ship.java
 *
 *
 * Version:
 *     $Id: Ship.java,v 1.2 2013/09/18 17:14:06 ghh8942 Exp ghh8942 $
 *
 * Revisions:
 *     $Log: Ship.java,v $
 *     Revision 1.2  2013/09/18 17:14:06  ghh8942
 *     fixed identifier in method for setting ship sections.
 *
 *     Revision 1.1  2013/09/18 17:08:25  ghh8942
 *     Initial revision
 *
 * 
 * Author: Gregory Hoople
 *
 */

public class Ship{
    private int Sections;
    private int SectionsHit;

    public Ship(){
        Sections = 0;
        SectionsHit = 0;
    }

    public void setSections(int mySec){
        Sections = mySec;
    }

    public void subHit(){
        SectionsHit++;
    }

    public boolean isSunk(){
        return SectionsHit >= Sections;
    }
}
