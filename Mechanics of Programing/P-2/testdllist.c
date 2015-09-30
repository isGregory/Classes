// testdllist.c
// $Id: testdllist.c,v 1.6 2014/04/16 23:32:47 ghh8942 Exp ghh8942 $
//
// Test the dlList implementation.
//
// @author: ghh8942 Gregory Hoople
//
// // // // // // // // // // // // // // // // // // // // // // // //

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "dlList.h"

int main ( void ) {
    DlList_T myList;

    myList = dll_create();
    if( myList == 0 ) {
        fputs( "Cannot create list!\n", stderr );
        return( 1 );
    }
    printf( "Initial list is %s\n", 
        dll_empty( myList ) ? "empty" : "not empty" );

    char* one = (char*)malloc( 11 * sizeof(char) );
    char* two = (char*)malloc( 12 * sizeof(char) );
    char* three = (char*)malloc( 11 * sizeof(char) );
    strcpy( one, "First Line" );
    strcpy( two, "Second Line" );
    strcpy( three, "Third Line" );

    printf( "Checking cursor initialized null...\n");
    if( dll_has_next( myList ) ) {
        printf( "Your possition is valid\n" );
    } else {
        printf( "Your possition is NOT valid\n" );
    }

    // Test append
    printf( "List size: %d\n", dll_size( myList ) );

    printf( "Adding \"%s\"\n", one );
    dll_append( myList, one );
    printf( "List size: %d\n", dll_size( myList ) );

    printf( "Adding \"%s\"\n", two );
    dll_append( myList, two );
    printf( "List size: %d\n", dll_size( myList ) );

    printf( "Adding \"%s\"\n", three );
    dll_append( myList, three );
    printf( "List size: %d\n", dll_size( myList ) );

    printf( "Checking cursor fixed with appends...\n");
    if( dll_has_next( myList ) ) {
        printf( "Your possition is valid\n" );
    } else {
        printf( "Your possition is NOT valid\n" );
    }

    printf( "Test cursor movement...\n" );
    if( dll_move_to( myList, 3 ) ) {
        printf( "You moved to an index you shouldn't be able to\n" );
    } else {
        printf( "You can't move the cursor to 3\n" );
    }

    if( dll_move_to( myList, 2 ) ) {
        printf( "moved to the last index\n" );
    } else { 
        printf( "movement problem to index 2\n" );
    }

    if( dll_move_to( myList, 0 ) ) {
        printf( "moved to the first index\n" );
    } else { 
        printf( "movement problem to index 0\n" );
    }

    printf( "Checking cursor still valid...\n" );
    if( dll_has_next( myList ) ) {
        printf( "Your possition is valid\n" );
    } else {
        printf( "Your possition is NOT valid\n" );
    }

    printf( "Print state and test dll_next:\n" );
    void* data = dll_next( myList );
    int index = 0;
    // Index 0
    printf( "[%d] \"%s\"\n", index, (char*)data );
    data = dll_next( myList );
    index++;
    // Index 1
    printf( "[%d] \"%s\"\n", index, (char*)data );
    data = dll_next( myList );
    index++;
    // Index 2
    printf( "[%d] \"%s\"\n", index, (char*)data );
    data = dll_next( myList );
    index++;
    // Index 3 (Should be the same as index 2 as it should not exist)
    printf( "[%d] \"%s\"\n", index, (char*)data );
    data = dll_next( myList );

    printf( "Lets work backwards:\n" );
    data = dll_prev( myList );
    index = dll_size( myList ) - 1;
    // Index 2
    printf( "[%d] \"%s\"\n", index, (char*)data );
    data = dll_prev( myList );
    index--;
    // Index 1
    printf( "[%d] \"%s\"\n", index, (char*)data );
    data = dll_prev( myList );
    index--;
    // Index 0
    printf( "[%d] \"%s\"\n", index, (char*)data );
    data = dll_prev( myList );
    index--;
    // Index -1 (Should be same as index 0 as it should not exist)
    printf( "[%d] \"%s\"\n", index, (char*)data );
    data = dll_prev( myList );

    char* four = (char*)malloc( 12 * sizeof(char) );
    char* five = (char*)malloc( 11 * sizeof(char) );
    char* six = (char*)malloc( 11 * sizeof(char) );
    char* seven = (char*)malloc( 13 * sizeof(char) );
    char* eight = (char*)malloc( 12 * sizeof(char) );
    strcpy( four, "Fourth Line" );
    strcpy( five, "Fifth Line" );
    strcpy( six, "Sixth Line" );
    strcpy( seven, "Seventh Line" );
    strcpy( eight, "Eighth Line" );

    printf( "Testing inserts\n" );
    dll_insert_at( myList, 0, six );
    printf( "List size: %d\n", dll_size( myList ) );

    dll_insert_at( myList, 2, seven );
    printf( "List size: %d\n", dll_size( myList ) );

    dll_insert_at( myList, 4, eight );
    printf( "List size: %d\n", dll_size( myList ) );

    printf( "Test full print and check inserts\n" );
    index = 0;
    data = dll_get( myList, index );
    while( data != NULL ) {
        printf( "[%d] \"%s\"\n", index, (char*)data );
        index++;
        data = dll_get( myList, index );
    }

    printf( "Test Sets\n" );
    data = dll_set( myList, 0, five );
    printf( "Switched \"%s\" with \"%s\"\n", (char*)data, five );
    free( data );

    data = dll_set( myList, 2, four );
    printf( "Switched \"%s\" with \"%s\"\n", (char*)data, four );
    free( data );

    printf( "Test full print and check sets\n" );
    index = 0;
    data = dll_get( myList, index );
    while( data != NULL ) {
        printf( "[%d] \"%s\"\n", index, (char*)data );
        index++;
        data = dll_get( myList, index );
    }
    
    printf( "Testing popping\n" );
    data = dll_pop( myList, dll_size( myList ) -1 );
    printf( "Last element is: \"%s\"\n", (char*)data );
    free( data );

    data = dll_pop( myList, 2 );
    printf( "Third element is: \"%s\"\n", (char*)data );
    free( data );

    printf( "Poping the rest...\n");
    index = 0;
    data = dll_pop( myList, 0 );
    while( data != NULL ) {
        printf( "[%d] \"%s\"\n", index, (char*)data );
        free( data );
        index++;
        data = dll_pop( myList, 0 );
    }

    printf( "Destroying\n" );
    dll_destroy( myList );
}

// // // // // // // // // // // // // // // // // // // // // // // //
// Revisions:
// $Log: testdllist.c,v $
// Revision 1.6  2014/04/16 23:32:47  ghh8942
// Fixed some errors with the freeing of the test.
//
// Revision 1.5  2014/04/16 22:09:50  ghh8942
// Extended the tests.
//
// Revision 1.4  2014/04/16 21:02:31  ghh8942
// Set up a quick test that works
//
// Revision 1.3  2014/04/16 20:52:38  ghh8942
// File compiles.
//
// Revision 1.2  2014/04/16 20:48:00  ghh8942
// Set up basic check to test compiling.
//
// Revision 1.1  2014/04/16 20:38:02  ghh8942
// Initial revision
//
//

