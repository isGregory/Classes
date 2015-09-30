// Version: $Id: quickhull.c,v 1.4 2014/03/03 08:01:40 ghh8942 Exp ghh8942 $
// quickhull.c
// Main program for QuickHull
// @author Gregory Hoople

#include <stdio.h>
#include "points.h"


//Takes in an array of points and recursively
//calculates out the hull points and sets the hull
//points to "points" and then returns the length
//of the hull points.
static int quickHullRec( struct Point points[], int numPoints,
    struct Point left, struct Point right, struct Point hullPoints[])
{
    if (numPoints == 0) {
        return 0;
    } else {
        struct Point f = furthestLeftPoint( left, right, points, numPoints);

        struct Point leftOfLine[numPoints];
        struct Point leftHulls[numPoints];
        int numLeft;
        numLeft = leftPointSet(left, f, points, 
            numPoints, leftOfLine);

        struct Point rightOfLine[numPoints];
        struct Point rightHulls[numPoints];
        int numRight;
        numRight = leftPointSet(f, right, points,
            numPoints, rightOfLine);
        numLeft = quickHullRec( leftOfLine, numLeft, left, f, leftHulls );
        numRight = quickHullRec( rightOfLine, numRight, f, right, rightHulls );

        //Add the hull points from the left.
        for ( int i = 0; i < numLeft; i++ )
        {
            hullPoints[i] = leftHulls[i];
        }

        //Add the furthest hull point.
        hullPoints[numLeft] = f;

        //Set up a right buffer to push the index
        int rB = numLeft + 1;

        //Add the hull points from the right.
        for (int i = 0; i < numRight; i++ )
        {
            hullPoints[rB + i] = rightHulls[i];
        }

        //The hull points are set.
        //Return the number of hull points.
        return numLeft + numRight + 1;
    }
}

//Takes in a point array and the length of that array.
//The function calculates out the QuickHull and puts it into 
//points as well as returns the new length.
static int computeQuickHull( struct Point points[], int numPoints, struct Point hullPoints[] )
{
    //Get the two edge points
    struct Point left = leftmostPoint( points, numPoints );
    struct Point right = rightmostPoint( points, numPoints );

    struct Point leftOfLine[numPoints];
    struct Point leftHulls[numPoints];
    int numLeft;
    numLeft = leftPointSet(left, right, points, 
        numPoints, leftOfLine);

    struct Point rightOfLine[numPoints];
    struct Point rightHulls[numPoints];
    int numRight;
    numRight = leftPointSet(right, left, points,
        numPoints, rightOfLine);

    numLeft = quickHullRec( leftOfLine, numLeft, left, right, leftHulls );
    numRight = quickHullRec( rightOfLine, numRight, right, left, rightHulls );

    //Add them together.
    //Add the furthest left hull point.
    hullPoints[0] = left;

    //Add the hull points from the left.
    for ( int i = 0; i < numLeft; i++ )
    {
        hullPoints[1 + i] = leftHulls[i];
    }

    //Add the furthest right hull point.
    hullPoints[1 + numLeft] = right;

    //Set up a right buffer to push the index
    int rB = numLeft + 2;

    //Add the hull points from the right.
    for (int i = 0; i < numRight; i++ )
    {
        hullPoints[rB + i] = rightHulls[i];
    }

    //The hull points are set.
    //Return the number of hull points.
    return numLeft + numRight + 2;
}


//Main Method
//Reads in points
//And then computes a quickhull
//Finally prints out the result
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

    printf("Convex hull:\n");
    //Compute the hull
    numHullPoints = computeQuickHull( myPoints, numPoints, myHull );

    //Display the set of points in the Hull calculated
    displayPoints( myHull, numHullPoints );
}



// Revisions: $Log: quickhull.c,v $
// Revisions: Revision 1.4  2014/03/03 08:01:40  ghh8942
// Revisions: Fixed functions and output.
// Revisions:
// Revisions: Revision 1.3  2014/03/03 06:40:41  ghh8942
// Revisions: Program Compiles. Produces the wrong output.
// Revisions:
// Revisions: Revision 1.2  2014/03/03 06:01:20  ghh8942
// Revisions: Wrote up methods. Did not try to compile.
// Revisions:
// Revisions: Revision 1.1  2014/03/03 04:20:13  ghh8942
// Revisions: Initial revision
// Revisions:
