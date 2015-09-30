//
// mish.c
//
// $Id: mish.c,v 1.9 2014/05/11 23:31:30 ghh8942 Exp ghh8942 $
//
// simple interactive shell program
//
// @author: Gregory Hoople (ghh8942)
//
// Note: exec_demo1.c was used as the skeleton
// structure for this program.

#define _GNU_SOURCE

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <stdbool.h>
#include <sys/types.h>
#include <sys/wait.h>

// Keeps track of verbose flag
bool verbose = false;

// Sets max allowed history
const int MAX_HISTORY = 10;

// History structure
typedef struct {
    int used;       // Elements currently used
    char** strings; // Array of strings in history
} History;

// History structure variable for the program
History myHist;

// Initialize the history structure
void initHistory( History *create ) {
    create->strings = (char **)malloc( MAX_HISTORY * sizeof(char*) );
    create->used = 0;
}

// Prints out the history
void printHistory( History *toPrint ) {
    printf("Last %d commands received:\n", toPrint->used);

    // Commands are printed from oldest to newest
    // Only prints the number of commands currently held
    for ( int i = toPrint->used; i > 0; i-- ) {
        printf( "%d: %s\n", i, (char*)toPrint->strings[ i - 1 ] );
    }
}

// Adds a string to the history list
void addHistory( History *h, char* toAdd ) {

    // If we have too many strings we free the oldest
    if ( h->used >= MAX_HISTORY ) {
        free( h->strings[ h->used - 1 ] );
    }

    // Go down the list of history and move them all back one
    for ( int i = 0; i < h->used; i++ ) {
        h->strings[ h->used - i ] = h->strings[ (h->used - i) - 1 ];
    }

    // If we're not full we increase the usage
    if (h->used < MAX_HISTORY ) {
        h->used++;
    }

    // Copy the string and set it as the first element
    h->strings[0] = strdup( toAdd );
}

// Used to free a history object
void freeHistory( History *toFree ) {
    // Frees all held strings
    for ( int i = 0; i < toFree->used; i++ ) {
        free( toFree->strings[ i ] );
    }
    // Frees the array keeping track of strings
    free( toFree->strings );
}

// Executes an external command
int mish_execute( int argc, char* argv[] ) {

    if ( verbose ) {
        printf( "        execvp: %s\n", argv[0] );
    }

    // Call the command
    execvp( argv[0], argv );

    // we only get here if the execvp() failed
    perror( "execvp" );

    // use _exit() to avoid problems with the shared
    // stdout still being used by our parent
    _exit( EXIT_FAILURE );
}

// Function to remove the new line of a string
void killNL(char *s) {
    while(*s && *s != '\n' && *s != '\r') s++;
 
    *s = 0;
}

// Function to convert the strings into arguments
// This function allows for the grouping of quoted tokens
//
// The majority of this code was written by user [torek]
// as found in stackoverflow question 9659697
void tokenStringToArgs( char* str, int* argc, char** argv ) {
    char *start_of_word;
    char *p = str;
    int c;
    enum states { DULL, IN_WORD, IN_STRING } state = DULL;

    bool going = true;

    for (p = str; going; p++) {
        // Convert to unsigned char for is* functions
        c = (unsigned char) *p;

        // Check if we hit a null then we continue through the loop one
        // more time to make sure we close off words if we're in one
        if ( *p == '\0' ) {
            going = false;
        }
        switch (state) {
            case DULL: // not in a word, not in a double quoted string
                if ( c == ' ' ) {
                    // still not in a word, so ignore this char
                    continue;
                }
                // not a space -- if it's a double quote we go
                // to IN_STRING, else to IN_WORD
                if (c == '"') {
                    state = IN_STRING;
                    // Word starts at *next* char, not this one
                    start_of_word = p + 1;
                    continue;
                }
                state = IN_WORD;
                // word starts here
                start_of_word = p;
                continue;

            case IN_STRING:
                // we're in a double quoted string, 
                // so keep going until we hit a close "
                if (c == '"') {
                    // word goes from start_of_word to p-1
                    int wordSize = (p - start_of_word);
                    argv[ *argc ] = malloc( wordSize + 1 );
                    strncpy( argv[ *argc ], start_of_word, wordSize );
                    *argc = (*argc) + 1;
                    // back to "not in word, not in string" state
                    state = DULL;
                }
                // Either still IN_STRING or we handled the end above
                continue;

            case IN_WORD:
                // we're in a word, so keep going until we get to a space
                if ( c == ' ' || c == '\0' ) {
                    // word goes from start_of_word to p-1
                    int wordSize = (p - start_of_word);
                    argv[ *argc ] = malloc( wordSize + 1 );
                    strncpy( argv[ *argc ], start_of_word, wordSize );
                    *argc = (*argc) + 1;
                    // Back to "not in word, not in string" state
                    state = DULL;
                }
                // either still IN_WORD or we handled the end above
                continue;
        }
    }

    // Add the null return
    argv[ *argc ] = NULL;
}

// Quits the program
int mish_quit( int argc, char* argv[] ) {

    // Clean up memory
    freeHistory( &myHist );

    exit( EXIT_SUCCESS );

    // Unreachable
    return 0;
}

// Function to toggle verbose mode on and off
// Usage:
//   argv[0] = "verbose"
//   argv[1] = "on" or "off"
int mish_verbose( int argc, char* argv[] ) {

    // Make sure there's enough arguments
    // if the 2nd argument is "on" we turn verbose mode on
    if ( argc > 1 && strcmp( argv[1], "on" ) == 0 ) {
        verbose = true;

    // Make sure there's enough arguments
    // if the 2nd argument is "off" we turn verbose mode off
    } else if ( argc > 1 && strcmp( argv[1], "off" ) == 0 ) {
        verbose = false;

    // Arguments didn't match so print out the usage
    } else {
        printf("usage: verbose [on|off]\n");
        return 1;
    }

    return 0;
}

// Prints the history of commands
// Basically wraps around the history function for printing
int mish_history( int argc, char* argv[] ) {

    // Print the history
    printHistory( &myHist );
    return 0;
}

int mish_help( int argc, char* argv[] ) {
    // Print the help list
    printf("List of internal commands:\n");
    printf("    verbose [on|off] - prints out diagnostic information");
    printf(" as the\n                         program executes.\n");
    printf("    help             - This list of commands.\n");
    printf("    history          - prints up to the last 10 commands.\n");
    printf("    quit             - exits the program.\n");

    return 0;
}

int main( int argc, char* argv[] ) {
    // Maximum arguments a line can have
    int MAX_ARGS = 64;

    if ( argc > 1 && strcmp( argv[1], "1" ) == 0 ) {
        verbose = true;
    }

    // Keep track of the process id's
    pid_t id;
    // Keep track of the status returned by child processes
    int status;

    // number of bytes for getline
    int nbytes = 0;
    // Catch the result from getline
    int result = 0;
    // Set up a string to read user input and file
    char *str = NULL;

    // Variable to track running
    bool running = true;

    // Keep track of the command's number
    int commandNum = 1;

    // Initialize history
    initHistory( &myHist );

    while( running ) {
        // Print out the prompt
        printf( "mish[%d]> ", commandNum );

        // Reset the bytes
        nbytes = 0;

        // Set 'str' to NULL
        str = NULL;

        // Read in the next line
        result = getline(&str, (size_t *) &nbytes, stdin);

        if ( result < 0 ) {
            // The getline function received an end of line
            printf("\n");

            // According to the man pages "str" needs to be
            // freed regardless
            free( str );

            // Clean up memory usage
            freeHistory( &myHist );

            exit( EXIT_SUCCESS );
        }

        // Kill new line
        killNL(str);

        // Input was most likely just a newline so continue
        if ( strlen( str ) == 0 ) {
            continue;
        }

        // Add the string to the history
        addHistory( &myHist, str );

        if ( verbose ) {
            // Print out the command
            printf("        command: %s\n\n", str );
        }

        // Set up variables to convert string
        // to the tokenized components
        int subArgc = 0;
        char* subArgv[MAX_ARGS];

        // get the arguments from the string
        tokenStringToArgs( str, &subArgc, subArgv );

        if ( verbose ) {
            // Print out the elements of the array
            printf( "        input command tokens:\n" );
            for ( int i = 0; i < subArgc; i++ ) {
                printf( "        %d: %s\n", i, subArgv[i] );
            }
        }

        // Check if an internal command
        if ( strcmp( subArgv[0], "quit" ) == 0 ) {

            // Free up local variable and call the quit command
            running = false;

            free( str );

            // Free argv
            for ( int i = 0 ; i < subArgc; i++ ) {
                free( subArgv[i] );
            }

            mish_quit( subArgc, subArgv );

        } else if ( strcmp( subArgv[0], "verbose" ) == 0 ) {

            // Call the verbose function
            mish_verbose( subArgc, subArgv );

        } else if ( strcmp( subArgv[0], "help" ) == 0 ) {

            // Call the help function
            mish_help( subArgc, subArgv );

        } else if ( strcmp( subArgv[0], "history" ) == 0 ) {

            // Call the history function
            mish_history( subArgc, subArgv );

        } else { // In the case it's not an internal command

            // create the child process
            id = fork();

            switch( id ) {

                case -1: // the fork() failed
                    perror( "fork" );
                    exit( EXIT_FAILURE );
                    // Break is unreachable
                    break;

                case 0:	// we are the child process

                    // Execute an external command
                    mish_execute( subArgc, subArgv );

                    // Break is unreachable
                    break;

                default: // we are the parent
                    break;

            } // Switch

            if ( verbose ) {
                printf("        wait for pid %d: %s\n", id, subArgv[0] );
            }

            // parent will wait for child to exit
            id = wait( &status );

            // Check for a problem
            if( id < 0 ) {
                perror( "wait" );
            }

            // If the status wasn't 0, print it out
            if ( status != 0 ) {
                printf( "command status: %d\n", status );
            }

        } // external command

        // Free argv
        for ( int i = 0 ; i < subArgc; i++ ) {
            free( subArgv[i] );
        }

        // Free the string
        free( str );

        // Increase the command numbering
        commandNum++;

    } // While running

    // Clean up memory
    freeHistory( &myHist );

    // Exit the program
    exit( EXIT_SUCCESS );
}

// // // // // // // // // // // // // // // // // // // // // // // //
// Version: 
// $Id: mish.c,v 1.9 2014/05/11 23:31:30 ghh8942 Exp ghh8942 $ 
// 
// Revisions: 
// $Log: mish.c,v $
// Revision 1.9  2014/05/11 23:31:30  ghh8942
// Changed variable?
//
// Revision 1.8  2014/05/11 22:46:33  ghh8942
// Fixed outputs
//
// Revision 1.7  2014/05/11 22:15:06  ghh8942
// Added the ability to tokenize with quotes
//
// Revision 1.6  2014/05/11 21:24:12  ghh8942
// Cleaned up format and added comments
//
// Revision 1.5  2014/05/11 21:02:03  ghh8942
// Implemented mish commands
//
// Revision 1.4  2014/05/11 19:30:33  ghh8942
// Seems to be working except for history.
//
// Revision 1.3  2014/05/11 18:09:33  ghh8942
// Compiles and executes external commands
//
// Revision 1.2  2014/05/11 17:36:38  ghh8942
// Working on reading input. Doesn't compile yet.
//
// Revision 1.1  2014/05/11 16:41:36  ghh8942
// Initial revision
//
// 
// 

