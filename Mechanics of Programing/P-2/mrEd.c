// mrEd.c
// $Id: mrEd.c,v 1.6 2014/04/17 03:47:59 ghh8942 Exp ghh8942 $
//
// Mr Ed program.
//
// @author: ghh8942 Gregory Hoople
//
// // // // // // // // // // // // // // // // // // // // // // // //

#define _GNU_SOURCE

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "dlList.h"

// Function to remove the new line of a string
void killNL(char *s) {
    while(*s && *s != '\n' && *s != '\r') s++;
 
    *s = 0;
}

// Function to print out a specified list
void printList( DlList_T lst ) {
    int index = 0;
    void* data = dll_get( lst, index );
    while( data != NULL ) {
        printf( "%s\n", (char*)data );
        index++;
        data = dll_get( lst, index );
    }
}

// Function to get the index of the cursor of a specified DlList_T
// Since there's no function to get the index of the cursor
int getCursorIndex( DlList_T lst ) {

    // Get the data of the current location
    void* data = dll_next( lst );

    // There is no cursor currently
    if( data == NULL ) {
        return 0;
    }

    // Grab the index of that data
    int index = dll_index( lst, data );

    // Set the cursor back to that point
    dll_move_to( lst, index );

    return index;
}

// Function to get the data of the cursor of a specified DlList_T
// Since there's no function to get the data of the cursor
void* getCursorData( DlList_T lst ) {

    // Get the data of the current location
    void* data = dll_next( lst );

    // If there is currently a cursor
    if( data != NULL ) {

        // Grab the index of that data
         int index = dll_index( lst, data );

        // Set the cursor back to that point
        dll_move_to( lst, index );
    }

    return data;
}

int main ( int argc, char* argv[] ) {

    DlList_T myList;

    // List buffer to keep track of inserting or appending
    DlList_T listBuff;

    myList = dll_create();
    if( myList == 0 ) {
        fprintf( stderr, "Cannot create list!\n" );
        return( 1 );
    }

    listBuff = dll_create();
    if( listBuff == 0 ) {
        fprintf( stderr, "Cannot create buffer!\n" );
        return( 1 );
    }

    // number of bytes for getline
    int nbytes = 80;
    // Set up a string to read user input and file
    char *str = NULL;
    // Set up name for saving files to
    char *saveFile = NULL;

    // Read in a file

    // Check to make sure there's arguments of the file to open
    if( argc == 2 ) {

        FILE* pFile = fopen(argv[1], "r");
        if( !pFile ) {
            perror("open failed");
            fprintf( stderr, "could not read file '%s'\n", argv[1] );
            return( 1 );
        } else {
            // The file has been opened. Go through
            // and fill the doubly linked list
            while( getline(&str, (size_t *) &nbytes, pFile) ) {
                // Kill the new line
                killNL(str);
                // Set up space
                char* toAdd = (char*)malloc( 
                    ( strlen(str) + 1 ) * sizeof(char) );

                // Add the previous read in line to that space
                strcpy( toAdd, str );
                // Add it to the list
                dll_append( myList, toAdd );

                //Free 'str'
                free( str );
                str = NULL;
            }
            fclose( pFile );
        }
    } else {
        printf( "no file supplied\n" );
    }

    // Keep track if we should stop or not
    bool running = true;

    // Keep track of changes
    bool buffChange = false;

    // Keeps track if we're appending or inserting
    bool grabText = false;
    char grabMode = 'a';

    // Keeps track of the cursor movement
    void* lastData = NULL;

    while( running ) {
        // Read in the next line
        getline(&str, (size_t *) &nbytes, stdin);

        // Kill new line
        killNL(str);

        // We're either appending or inserting
        if( grabText ) {
            if( strlen( str ) == 1 && str[0] == '.' ) {
                // A single period was entered to stop the adding
                // Set grabText to false
                grabText = false;

                // If the mode was set to append
                if( grabMode == 'a' ) {

                    // We go through and pop all the elements
                    // off the buffer and append them to the list
                    int index = 0;
                    void* data = dll_pop( listBuff, 0 );
                    while( data != NULL ) {
                        dll_append( myList, data );
                        index++;
                        data = dll_pop( listBuff, 0 );
                    }
                } else if ( grabMode == 'i' ) {

                    // We go through and pop all the elements
                    // off the buffer and insert them to the list
                    int index = 0;
                    // Set the insert index as the current
                    // pointer's index
                    int insertIndex = getCursorIndex( myList );
                    void* data = dll_pop( listBuff, 0 );
                    while( data != NULL ) {
                        dll_insert_at( myList, insertIndex, data );
                        index++;
                        insertIndex++;
                        data = dll_pop( listBuff, 0 );
                    }
                }
            } else {
                // Set up space
                char* toAdd = (char*)malloc( 
                    ( strlen(str) + 1 ) * sizeof(char) );

                // Add the previous read in line to that space
                strcpy( toAdd, str );
                // Add it to the list
                dll_append( listBuff, toAdd );
            }
        } else if( str[0] == 'a' ) {
            grabText = true;
            grabMode = 'a';
            buffChange = true;
        // Current line
        } else if( str[0] == '.' ) {
            // Print the index of the current line
            if( str[1] == '=' ) {
                printf( "%d\n", getCursorIndex( myList ) );

            // Print the current line
            } else {
                printf( "%s\n", (char*)getCursorData( myList ) );
            }

        // Advance cursor to the next line
        // String length of 1 means they hit enter
        } else if( strlen(str) == 0 || str[0] == '+' ) {
            // Try to advance the cursor
            void* data = dll_next( myList );

            // If the cursor didn't advance, notify the user
            if( data == lastData ){
                printf("?\n");

            // Otherwise the cursor advanced, print the line
            } else {
                lastData = data;
                printf( "%s\n", (char*)data );
            }
        // Advance the cursor to the previous line
        } else if( str[0] == '-' ) {
            // Try to advance the cursor
            void* data = dll_prev( myList );

            // If the cursor didn't advance, notify the user
            if( data == lastData ){
                printf("?\n");

            // Otherwise the cursor advanced, print the line
            } else {
                lastData = data;
                printf( "%s\n", (char*)data );
            }
        // Delete line
        } else if( str[0] == 'd' ) {
            if( dll_has_next( myList ) ){
                int index = getCursorIndex( myList );
                void* data = dll_pop( myList, index );
                free( data );
                buffChange = true;
            }
        } else if( str[0] == 'i' ) {
            grabText = true;
            grabMode = 'i';
            buffChange = true;
        // Last element
        } else if( str[0] == '$' ) {
            // For both "$=" and "$" it prints a "?" upon
            // an empty list
            int size = dll_size( myList );
            if( size == 0 ) {
                printf( "?\n" );
            } else {
                // If the command was "$=" just print
                // the possition of the last element
                if( str[1] == '=' ) {
                    printf( "%d\n", size );

                // Otherwise move to the last position
                // and print the last element
                } else {
                    dll_move_to( myList, size - 1 );
                    void* data = dll_get( myList, size - 1 );
                    printf( "%s\n", (char*)data );
                }
            }

        // Print
        } else if( str[0] == 'p' ) {
            printList( myList );

        // Save
        } else if( str[0] == 'w' ) {

            char grab[(strlen(str) + 1)];

            // Check if a file name has beens pecified
            sscanf(str, "%*s %s", grab);
            
            // If the length is greater than '1' we set 
            // it as the last acceptable saveFile
            if( strlen(grab) > 0 ) {
                if( saveFile != NULL ) {
                    free( saveFile );
                }
                saveFile = (char*)malloc( 
                    ( strlen(str) + 1 ) * sizeof(char) );
                strcpy( saveFile, grab );
            }

            // Check if a quit flag has been given
            bool quit = (str[1] == 'q');

            if( saveFile == NULL || strlen(saveFile) <= 0 ) {
                // We don't have an acceptable file name to check
                continue;
            }
            // Open file to be saved
            FILE* pFile = fopen(saveFile, "w");

            if( !pFile ) {
                perror("open failed: ");
            } else {
                printf( "file name: '%s'\n", saveFile );
                // Go through each node of the doulby linked list
                int index = 0;
                void* data = dll_get( myList, index );
                while( data != NULL ) {
                    // Write to file
                    fputs((char*)data, pFile);
                    fputs("\n", pFile);
                    index++;
                    data = dll_get( myList, index );
                }
                // Close
                fclose( pFile );

                // If we want to quit afterwards
                if( quit ) {
                    running = false;
                    printf("\nBye\n");
                }

                // reset the buffer flag
                buffChange = false;
            }

        // Soft quit
        } else if ( str[0] == 'q' ) {
            // If the buffer hasn't changed stop the program
            if( !buffChange ) {
                running = false;
                printf( "\nBye\n" );
            // Otherwise warn that there's been changes
            } else {
                printf( "? buffer dirty.\n" );
            }

        // Hard quit
        } else if ( str[0] == 'Q' ) {
            running = false;
            printf( "\nBye\n" );
        } else {
            // Check if a number
            char* end;
            int checkNum = strtol( str, &end, 10 );
            // If the conversion did not encounter a string
            // We know the number is good
            if( !*end ) {
                // If we can move to the requested index
                if( dll_move_to( myList, checkNum - 1 ) ) {
                    // Print that index
                    printf( "%s\n", (char*)getCursorData( myList ) );
                } else {
                    printf( "?\n" );
                }
            }
        }

        // Free 'str'
        free( str );

        // Set 'str' to NULL
        str = NULL;
    }

    // Destroy our list
    dll_destroy( myList );

    // Destroy the buffer
    dll_destroy( listBuff );
}

// // // // // // // // // // // // // // // // // // // // // // // //
// Revisions:
// $Log: mrEd.c,v $
// Revision 1.6  2014/04/17 03:47:59  ghh8942
// Fixed some output.
//
// Revision 1.5  2014/04/17 03:33:03  ghh8942
// Fixed a lot of bugs. Switched to getline
//
// Revision 1.4  2014/04/17 02:29:05  ghh8942
// Fixed an error when chaning the file system
//
// Revision 1.3  2014/04/17 02:27:46  ghh8942
// Fixed some comments
//
// Revision 1.2  2014/04/17 02:15:25  ghh8942
// Implemented and compiles.
//
// Revision 1.1  2014/04/16 23:33:17  ghh8942
// Initial revision
//
//
//

