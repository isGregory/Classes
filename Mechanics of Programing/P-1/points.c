// Version: $Id: points.c,v 1.5 2014/03/03 08:01:01 ghh8942 Exp ghh8942 $
// Implements functions for points.h
// @author Gregory Hoople

#include <stdio.h> //fgets, printf
#include <stdlib.h> //atoi
#include "points.h"

// Indicates the winding order of the triangle formed by points
// p, q, and r.  In other words, given the line formed by pq,
// is r to the left (ccw), right(cw) or on (collinear) the line.
// @param p First point (usually the "anchor" point)
// @param q Second point
// @param r Third point
// @returns: ccw > 0, cw < 0, coll = 0
int ccw(struct Point p, struct Point q, struct Point r)
{
    return ((q.x - p.x)*(r.y - p.y) - (q.y - p.y)*(r.x - p.x));
}

// Display a single point to standard output as:
//      label: (x,y)
// @param p The point to display
void displayPoint(struct Point p)
{
    printf("%c: (%d,%d)", p.label, p.x, p.y);
}

// Displays a collection of points, one per line to standard output.
// Assumes numPoints is valid.
// @param points A native array of Point's (immutable)
// @param numPoints The number of valid Point's in points
void displayPoints(struct Point points[], int numPoints)
{
    for ( int i = 0; i < numPoints; i++ )
    {
        displayPoint( points[i] );
        printf( "\n" );
    }
}

// Two points are equal if the label, x and y coordinates
// are all equal to each other.
// @param p1 first point
// @param p2 second point
// @return 1 if equal, 0 if not equal
int equal(struct Point p1, struct Point p2)
{
    return (p1.label == p2.label && p1.x == p2.x
        && p1.y == p2.y);
}

// Return the index of p in points.
// Assumes numPoints is valid.
// @param p The point to search for
// @param points A native array of Point's (immutable)
// @param numPoints The number of valid Point's in points
// @return the index, if found, otherwise -1
int indexOf(struct Point p, struct Point points[], int numPoints)
{
    int index = -1;
    for ( int i = 0; i < numPoints; i++ )
    {
        if ( equal(p, points[i]) )
        {
            index = i;
        }
    }
    return index;
}

// Determine the point with the smallest x-coordinate.
// Assumes numPoints is valid.
// @param points A native array of Point's (immutable)
// @param numPoints The number of valid Point's in points
// @return The resulting Point
struct Point leftmostPoint(struct Point points[], int numPoints)
{
    struct Point left = points[0];

    //Go through each point
    for ( int i = 1; i < numPoints; i++ )
    {
        //check to see if we've found a
        //point that's further left.
        if ( points[i].x < left.x )
        {
            left = points[i];
        }
    }

    return left;
}

// Determine the point with the largest x-coordinate.
// Assumes numPoints is valid.
// @param points A native array of Point's (immutable)
// @param numPoints The number of valid Point's in points
// @return The resulting Point
struct Point rightmostPoint(struct Point points[], int numPoints)
{
    struct Point right = points[0];

    //Go through each point
    for ( int i = 1; i < numPoints; i++ )
    {
        //check to see if we've found a
        //point that's further right.
        if ( points[i].x > right.x )
        {
            right = points[i];
        }
    }

    return right;
}

// Determine the point furthest "left" of the line a->z
// Assumes numPoints is valid.
// @param a Starting point of a line
// @param z Ending point of a line
// @param points A native array of Point's (immutable)
// @param numPoints The number of valid Point's in points
// @return The resulting Point
struct Point furthestLeftPoint(struct Point a, struct Point z, struct Point points[], int numPoints)
{
    int index = -1; //index of farthest
    int current = 0; //current farthest
         int next = 0;
    //We go through all the points
    for ( int i = 0; i < numPoints; i++ )
    {
        //First we check the candidate point is to the left.
        if( ccw( a, z, points[i]) )
        {
            //We check the current point to the two points
            //and find the magnitude of how far away it is.
            //This isn't distance but it's the distance
            //two each end point except for the sqrt'ing
            next = ((a.x - points[i].x) * (a.x - points[i].x) + 
                (a.y - points[i].y) * (a.y - points[i].y)) +
                ((z.x - points[i].x) * (z.x - points[i].x) + 
                (z.y - points[i].y) * (z.y - points[i].y));

            //if the magnitude is greater we set
            //the new current farthest
            if ( next > current )
            {
                current = next;
                index = i;
            }
        }
    }
    //return the farthest
    if ( index == -1 ) {
        return a;
    } else {
        return points[index];
    }
}


// Determine the set of points to the "left" of the line formed
// from 'a' to 'z' (a->z).
// Assumes numPoints is valid.
// @param a Starting point of a line
// @param z Ending point of a line
// @param points A native array of Point's (immutable)
// @param numPoints The number of valid Point's in points
// @param lps A native array of Point's (set of Points to the left of a->z)
// @return The number of points in the left point set
int leftPointSet(struct Point a, struct Point z, struct Point points[], int numPoints, struct Point lps[])
{
    //Count the number that are to the left
    int numLeft = 0;

    //We go through all the points
    for ( int i = 0; i < numPoints; i++ )
    {
        //We check the current point to the line
        //and find if it is to the left.
        //if the point is to the left we add
        //it to the left points set (lps)
        if ( ccw( a, z, points[i]) > 0 )
        {
            lps[numLeft] = points[i];
            numLeft++;
        }
    }
    return numLeft;
}

// Determine the index of the point with the smallest y-coordinate.
// If the lowest y coordinate exists in more than one point in the
// set, the lowest x coordinate out of the candidates should be selected.
// Assumes numPoints is valid.
// @param points A native array of Point's (immutable)
// @param numPoints The number of valid Point's in points
// @return the lowest Point
struct Point lowestPoint(struct Point points[], int numPoints)
{
    struct Point lowest = points[0];

    //go through all points
    for ( int i = 1; i < numPoints; i++ )
    {
        //if the next point is lower
        //set it as the lowest
        if ( points[i].y < lowest.y )
        {
            lowest = points[i];
        }
        //if they're equal in low-ness
        //check if the point is more left.
        else if ( points[i].y == lowest.y )
        {
            if ( points[i].x < lowest.x )
            {
                lowest = points[i];
            }
        }
    }
    return lowest;
}

// Reads a collection of points from standard input into a 
// native array of points (between MIN_POINTS and MAX_POINTS)
// @param points A native array of Point's to populate (mutable)
// @return 0 if there is an error, otherwise the number of 
//  points read.
int readPoints(struct Point points[])
{
    char buff[MAX_LINE];
    printf("Number of points(3-100): ");

    //Read in the number of points.
    if ( fgets( buff, MAX_LINE, stdin ) == NULL )
    {
        printf("Error reading number of points.\n");
        return 0;
    }
    
    int numPoints = atoi(buff);

    //Check that the number is in range.
    if ( numPoints < MIN_POINTS || numPoints > MAX_POINTS )
    {
        printf("Number of points must be between 3 and 100.\n");
        return 0;
    }

    //We now have the right number of points set.
    //Time to grab the points from standard input.
    for ( int i = 0; i < numPoints; i++ )
    {
        struct Point nextRead = {'x', 0, 0};

        printf("Enter label (character): ");
        //Read in label
        if ( fgets( buff, MAX_LINE, stdin ) == NULL )
        {
            printf("Error reading point label.\n");
            return 0;
        }
    
        //Set the Point's label
        nextRead.label = buff[0];

        printf("Enter x (int): ");
        //Read in x coordinate
        if ( fgets( buff, MAX_LINE, stdin ) == NULL )
        {
            printf("Error reading x coordinate.\n");
            return 0;
        }

        //Set the Point's 'x' coordinate
        nextRead.x = atoi( buff );

        printf("Enter y (int): ");
        //Read in y coordinate
        if ( fgets( buff, MAX_LINE, stdin ) == NULL )
        {
            printf("Error reading y coordinate.\n");
            return 0;
        }

        //Set the Point's 'y' coordinate
        nextRead.y = atoi( buff );

        points[i] = nextRead;
    }
    return numPoints;
}

// Swap two Point's in an array of Point's.  If either
// point is out of range this function should not
// alter the array.
// Assumes numPoints is valid.
// @param a the index of first value to swap
// @param b the index of second value to swap
// @param points A native array of Point's (mutable)
// @param numPoints, 
void swap(int a, int b, struct Point points[], int numPoints)
{
    //Check that 'a' is in range.
    if ( a >= numPoints || a < 0 )
    {
        return;
    }

    //Check that 'b' is in range.
    if ( b >= numPoints || b < 0 )
    {
        return;
    }

    //if both points are in range we swap.
    struct Point toSwap = points[a];
    points[a] = points[b];
    points[b] = toSwap;
}


// Revisions: $Log: points.c,v $
// Revisions: Revision 1.5  2014/03/03 08:01:01  ghh8942
// Revisions: Fixed furthestLeftPoint function
// Revisions:
// Revisions: Revision 1.4  2014/03/03 06:41:02  ghh8942
// Revisions: Compiles. Works.
// Revisions: The farthest left might be wrong.
// Revisions:
// Revisions: Revision 1.3  2014/03/03 06:00:37  ghh8942
// Revisions: Fixed a couple methods
// Revisions:
// Revisions: Revision 1.2  2014/03/03 04:19:18  ghh8942
// Revisions: Implemented all functions. Has not attempted to compile.
// Revisions:
// Revisions: Revision 1.1  2014/03/03 01:41:50  ghh8942
// Revisions: Initial revision
// Revisions:
