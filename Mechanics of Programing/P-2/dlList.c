// dlList.c
// $Id: dlList.c,v 1.4 2014/04/16 23:32:25 ghh8942 Exp ghh8942 $
//
// double linked list with internal, tracking cursor mechanism.
//
// @author: ghh942 Gregory Hoople
//
// // // // // // // // // // // // // // // // // // // // // // // //

#include <stdlib.h>
#include <assert.h>

// Individual Node Structure
struct listNode {
	struct listNode* previous;
	void* data;
	struct listNode* next;
};

// Structure for the list
struct dlListStruct {
	struct listNode* root;
	struct listNode* cursor;
};

/// DlList_T points to a representation of a double-linked list 
/// of void pointers (to abstract data objects).
typedef struct dlListStruct * DlList_T;

#define _DLL_IMPL_
#include "dlList.h"

/// dll_create constructs an instance of an empty double-linked list.
/// @return instance of double-linked list (this is a pointer).
DlList_T dll_create( void ) {
    DlList_T new;
    // Allocate memory for the list
    new = (DlList_T) malloc( sizeof( struct dlListStruct ) );

    // If the item was established we set up the pointers
    if( new != 0 ) {
        new->root = NULL;
        new->cursor = NULL;
    }

    return( new );
}

/// dll_destroy clears and frees all storage associated with
/// the linked list data structure.
/// The function handles memory management of all data payloads.
/// @param lst the linked list to destroy.
/// @post lst is no longer usable; the function freed all its memory.
void dll_destroy( DlList_T lst ) {
    // First we clear out and free up all the data in the list
    dll_clear( lst );

    // Now we free up the list
    free( lst );
}

/// dll_clear clears the list content, making the list empty.
/// dll_clear assumes all data payloads are dynamic and frees their storage.
/// @param lst the linked list to clear.
/// @post lst is now in an empty state.
void dll_clear( DlList_T lst ) {
    // clear the cursor
    lst->cursor = NULL;

    // Set up items to keep track of the nodes
    struct listNode* next = NULL;
    struct listNode* current = lst->root;

    // We walk down the nodes and free them up as we go
    while( current != NULL ) {
        // Grab the next node right away
        next = current->next;

        // Free up the data of the current node
        free( current->data );
        // We no longer need the current node so free it up too
        free( current );

        // Set the current to be the next
        current = next;
    }

    // Clear the root node
    lst->root = NULL;
}

/// dll_move_to moves the cursor to the requested index, if index is valid.
/// @param lst the list whose cursor should move.
/// @param indx the 0-based index to which to move the cursor.
/// @return true if the cursor move was successful.
bool dll_move_to( DlList_T lst, int indx ) {
    struct listNode* current = lst->root;
    lst->cursor = lst->root;

    // If the root node is NULL, we're still successful at moving
    // to the starting index. That is why we don't check for it

    // Step up to the index
    for( int i = 0; i < indx; i++ ) {
        // If we need to move forward and can't return false
        if( current->next == NULL ) {
            return false;

        // Otherwise move forward
        } else {
            current = current->next;
        }
    }

    // Set the cursor to the current point
    lst->cursor = current;

    // Move was successful
    return true;
}

/// dll_has_next returns whether or not the cursor refers to a valid position.
/// @param lst the list to check.
/// @return non-zero if the cursor refers to a valid current position.
int dll_has_next( DlList_T lst ) {
    // If there is no node in the list return false
    if( lst->cursor == NULL ) { return 0; }

    // If there is no next node in the list return false
    if( lst->cursor->next == NULL ) { return 0; }

    // There is a cursor and there is a next cursor so we return true
    return 1;
}

/// dll_next returns the current item and advance forward to next item.
/// The list module retains ownership of the memory address returned.
/// The pointer returned refers to the actual data; the client does not own it.
/// @param lst the list to iterate over.
/// @return the data pointer of the current item.
/// @pre the cursor refers to a valid current position.
/// @post the cursor refers to the position after the current one, if any.
void * dll_next( DlList_T lst ) {
    // Check cursor is safe as a precondition
    if( lst->cursor == NULL ) { return NULL; }

    // Grab the data to be returned
    void* toReturn = lst->cursor->data;

    // We check if there's a next
    if( lst->cursor->next != NULL ) {
        // Since there's a next we set the cursor to the next place
        lst->cursor = lst->cursor->next;
    }

    return toReturn;
}

/// dll_prev returns the current item and advance backward to previous item.
/// The list module retains ownership of the memory address returned.
/// The pointer returned refers to the actual data; the client does not own it.
/// @param lst the list to iterate over.
/// @return the data pointer of the current item.
/// @pre the cursor refers to a valid current position.
/// @post the cursor refers to the position before the current one, if any.
void * dll_prev( DlList_T lst ) {
    // Check cursor is safe as a precondition
    if( lst->cursor == NULL ) { return NULL; }

    // Grab the data to be returned
    void* toReturn = lst->cursor->data;

    // We check if there's a previous
    if( lst->cursor->previous != NULL ) {
        // Since there's a previous we set the cursor to the next place
        lst->cursor = lst->cursor->previous;
    }

    return toReturn;
}

/// dll_size returns the size of the list.
/// @param lst the subject list.
/// @return the count of items in the list.
int dll_size( DlList_T lst ) {
    int itemCount = 0;
    
    // Set up variable to keep track of the nodes
    struct listNode* current = lst->root;

    // We walk down the nodes and count them up as we go
    while( current != NULL ) {
        // Grab the next node
        current = current->next;
        itemCount++;
    }

    return itemCount;
}

/// dll_append appends an item to the end of the list.
/// The function assumes ownership of the memory of the data payload.
/// @param lst the subject list.
/// @param data a pointer to the item to append.
/// @post cursor moves to refer to the appended item. the lst size grows by 1.
void dll_append( DlList_T lst, void *data ) {
    // Set up node to be added
    struct listNode* toAdd;

    // Request space to hold the node we want to add
    toAdd = malloc( sizeof( struct listNode ) );

    // Catastrophic. Unable to get the memory needed
    assert( toAdd != 0 );

    // Set up the variables for the node to be added
    toAdd->previous = NULL;
    toAdd->data = data;
    toAdd->next = NULL;

    // If we don't have any elements in the list
    if( lst->root == NULL ) {

        // Set what we want to add as the root
        lst->root = toAdd;
        // Have the cursor point to established root
        lst->cursor = lst->root;

    } else {
        // Set up variable to keep track of the nodes
        struct listNode* current = lst->root;

        // Move to the last element of the list
        while( current->next != NULL ) {
            current = current->next;
        }

        // Set the next element to be what we're adding
        current->next = toAdd;
        // Set up the last node's previous as the old last element
        toAdd->previous = current;
    }
}

/// dll_insert_at inserts an item at the requested position, if index is valid.
/// The client is responsible for memory management of the item's storage.
/// The function assumes ownership of the memory of the data payload.
/// @param lst the subject list.
/// @param indx the 0-based position.
/// @param data a pointer to the item to append.
/// @pre indx must be in the range [0...dll_size(lst) ).
/// @post if successful, cursor moves to position of inserted item.
/// @post if successful, the lst size grows by 1.
void dll_insert_at( DlList_T lst, int indx, void *data ) {
    // Make sure the index is valid
    if( indx >= dll_size( lst ) ) {

        // If it's not valid free the data
        // because we're responsable for it
        free( data );

        // Quit out as there's nothing we can do
        return;
    }

    // Set up node to be added
    struct listNode* toAdd;

    // Request space to hold the node we want to add
    toAdd = malloc( sizeof( struct listNode ) );

    // Catastrophic. Unable to get the memory needed
    assert( toAdd != 0 );

    // Set up the variables for the node to be added
    toAdd->previous = NULL;
    toAdd->data = data;
    toAdd->next = NULL;

    // Set up variable to keep track of the nodes
    struct listNode* current = lst->root;

    // Step up to the index
    for( int i = 0; i < indx; i++ ) {
        current = current->next;
    }

    // Link the previous item to the added item
    if( current->previous != NULL ) {
        current->previous->next = toAdd;
        toAdd->previous = current->previous;
    }

    // Link the item to be added to the current node
    current->previous = toAdd;
    toAdd->next = current;

    // Set the cursor to the added item
    lst->cursor = toAdd;

    // If we were altering the root we need
    // to set what we added as the new root
    if( current == lst->root ) {
        lst->root = toAdd;
    }
}

/// dll_get returns a pointer to the item at index; item remains in list.
/// The list module retains ownership of the memory address returned.
/// The pointer returned refers to the actual data; the client does not own it.
/// @param lst the subject list.
/// @param indx the 0-based position.
/// @return pointer to the item requested.
/// @pre indx must be in the range [ 0...dll_size(lst) ).
/// @post cursor position does not change. lst size and content is unchanged.
void * dll_get( DlList_T lst, int indx ) {
    // Make sure the index is valid
    if( indx >= dll_size( lst ) ) {
        // Quit out as there's nothing we can do
        return NULL;
    }

    // Set up variable to keep track of the nodes
    struct listNode* current = lst->root;

    // Step up to the index
    for( int i = 0; i < indx; i++ ) {
        current = current->next;
    }

    return current->data;

}

/// dll_set replaces the pointer to the item with the data value.
/// The function transfers ownership of the old memory to the client.
/// The client is responsible for memory management of the old, returned data.
/// @param lst the subject list.
/// @param indx the 0-based position.
/// @param data a pointer to the item to set, replacing the existing entry.
/// @return pointer to the item that was replaced.
/// @pre indx must be in the range [ 0...dll_size(lst) ).
/// @post data is the value of the list at the position of the index.
void * dll_set( DlList_T lst, int indx, void *data ) {
    // Make sure the index is valid
    if( indx >= dll_size( lst ) ) {
        // Quit out as there's nothing we can do
        return NULL;
    }

    // Value to be returned
    void* toReturn;

    // Set up variable to keep track of the nodes
    struct listNode* current = lst->root;

    // Step up to the index
    for( int i = 0; i < indx; i++ ) {
        current = current->next;
    }

    // Set the return value
    toReturn = current->data;

    // Replace the data
    current->data = data;

    // Hand off the returned data
    return toReturn;
}

/// dll_pop removes the item at the index and returns its data pointer.
/// The function transfers ownership of the old memory to the client.
/// The client is responsible for the memory management of the data removed.
/// The cursor moves to the next position, if present. Otherwise the cursor
/// moves to the previous position in the list, if present; Otherwise the
/// cursor is invalid since this function deleted the last line in the list.
/// @param lst the subject list.
/// @param indx the 0-based position.
/// @return pointer to the item removed.
/// @pre indx must be in the range [ 0...dll_size(lst) ).
/// @post cursor moves to adjacent position or is invalid if list is now empty.
void * dll_pop( DlList_T lst, int indx ) {
    // Make sure the index is valid
    if( indx >= dll_size( lst ) ) {
        // Quit out as there's nothing we can do
        return NULL;
    }

    // Set up variable to keep track of the nodes
    struct listNode* current = lst->root;

    // Step up to the index
    for( int i = 0; i < indx; i++ ) {
        current = current->next;
    }

    // Set up a variable to carry the return data
    void* toReturn = current->data;

    // Set up variables to keep track of the nodes around the current
    struct listNode* cNext = current->next;
    struct listNode* cPrev = current->previous;

    // Before we flip their connections to pop out the current we
    // Need to take care of some safety stuff. First we look at
    // The root and make sure it's not getting popped. If it is
    // We set it to the next (even if that's NULL). Since it's the
    // root, there is no previous.
    if( lst->root == current ) {
        lst->root = cNext;
    }

    // We now handle the change in the cursor
    if( cNext == NULL && cPrev != NULL ){
        lst->cursor = cPrev;
    } else {
        lst->cursor = cNext;
    }

    // We can now take care of popping out the middle
    if( cPrev != NULL ) {
        cPrev->next = cNext;
    }
    if( cNext != NULL ) {
        cNext->previous = cPrev;
    }

    // Free the popped node
    free( current );

    // Return the data carried inside
    return toReturn;
}

/// dll_index returns a 0-based index of the data in the list or -1 if absent.
/// @param lst the subject list.
/// @param data a pointer to the item to find.
/// @return 0-based index of the data in the list or -1 if absent.
/// @post cursor position does not change. lst size and content is unchanged.
int dll_index( DlList_T lst, void *data ) {
    // Keep track of the index to return
    int dataIndex = 0;

    // Set up variable to keep track of the nodes
    struct listNode* current = lst->root;

    while( current != NULL ) {

        // If the data is the one we're looking for
        if( current->data == data ) {
            // return the data index
            return dataIndex;
        }

        // Increase the current item and increase the index
        current = current->next;
        dataIndex++;
    }

    // Data not found
    return -1;
}

/// dll_empty checks for an empty list.
/// @param lst: the subject list.
/// @return true if the list is empty.
/// @post cursor position does not change. lst size and content is unchanged.
bool dll_empty( DlList_T lst ) {
    return lst->root == NULL;
}

// // // // // // // // // // // // // // // // // // // // // // // //
// Revisions:
// $Log: dlList.c,v $
// Revision 1.4  2014/04/16 23:32:25  ghh8942
// Fixed some errors with insert, as well as pop
//
// Revision 1.3  2014/04/16 20:52:54  ghh8942
// File now compiles.
//
// Revision 1.2  2014/04/16 20:28:31  ghh8942
// Implemented the methods. Did not try compiling yet.
//
// Revision 1.1  2014/04/16 18:28:39  ghh8942
// Initial revision
//
//
