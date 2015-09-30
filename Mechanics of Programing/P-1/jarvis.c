// Version: $Id: jarvis.c,v 1.3 2014/03/03 06:41:36 ghh8942 Exp ghh8942 $
// jarvis.c
// Main program for the Jarvis march
// @author Gregory Hoople

#include <stdio.h>
#include "points.h"

//Takes in an array of points and its length and calculates
//out the hull using the Jarvis algorithm and places those
//points in "poh" then returns the length of that array.
static int computeJarvisHull( struct Point points[], 
    int numPoints, struct Point poh[] )
{
    //Take a point we know to be on the hull
    struct Point pointOnHull = leftmostPoint( points, numPoints );
    
    struct Point endpoint = points[0];

    //Keeps track of number of points in the Hull list
    int i = 0;

    do {
        poh[i] = pointOnHull;
        endpoint = points[0];

        //find the next point on the hull
        for ( int j = 1; j < numPoints; j++ )
        {
            //If the next candidate is to the left
            //of the line it becomes the new candidate.
            if ( equal(endpoint, pointOnHull) ||
                ( ccw( poh[i], endpoint, points[j]) > 0 ) )
            {
                endpoint = points[j];
            }
        }

        i++;
        pointOnHull = endpoint;

    } while ( !equal(endpoint, poh[0]) );
    //Loop cancels once we loop back to the start.

    //return number of points on hull
    return i;
}

//Program reads in points, prints them out then calculates
//the hull of those points using the Jarvis method and then
//prints out the result.
int main( void )
{
    //Read in points
    struct Point myPoints[MAX_POINTS];
    int numPoints = readPoints(myPoints);

    //Exit if there was an error reading in points
    if ( !numPoints )
    {
        return 0;
    }

    printf("Set of points:\n");
    //Display the set of points read in
    displayPoints( myPoints, numPoints );

    //Set up an array to hold the hull points
    //It can't be more than the size of points
    //in the array of points. So we can set the
    //array to hold that much and know it to be
    //large enough
    struct Point myHull[numPoints];
    int numHullPoints;

    //Compute the hull
    numHullPoints = computeJarvisHull( myPoints, numPoints, myHull);

    printf("Convex hull:\n");
    //Display the set of points in the Hull calculated
    displayPoints( myHull, numHullPoints );

    return 0;
}


// Revisions: $Log: jarvis.c,v $
// Revisions: Revision 1.3  2014/03/03 06:41:36  ghh8942
// Revisions: Program compiles and works with input1
// Revisions:
// Revisions: Revision 1.2  2014/03/03 06:00:55  ghh8942
// Revisions: Wrote up methods. Did not try to compile.
// Revisions:
// Revisions: Revision 1.1  2014/03/03 04:19:41  ghh8942
// Revisions: Initial revision
// Revisions:
