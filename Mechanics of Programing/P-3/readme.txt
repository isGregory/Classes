readme.txt
Project 3: mish.c
Author: Gregory Hoople

Design:
The program first checks if a flag was put on to turn on verbose mode. After
that it sets up all the variables for the function to get input and run.
Then it sets up history which is a struct that holds the past specified
commands. Continuing from there the program enters the main mish loop. It
collects input lines, cleans them, checks that the line is usable and then
tokenizes it based on white spaces and groups quotes together. Once it has
the command lines it checks the commands to see if it's an internal command
which it then fires off the appropriate message or it forks the program. The
parent program goes and waits for the child to complete. The child executes
a command and returns to the waiting parent. Then the main loop continues
again till "quit" or control+D is sent.

Problems:
I was not a fan of having to use an external function for quitting.
I would have much preferred to kick the program out of the while loop and
clean up everything in one spot. Though I did my best to limit the memory
usage in the program, local variables such as "str" were allocated in
the main loop, so to have them be freed outside was not clean and not
favorable. Otherwise it wasn't a big issue, just something extra to keep
track of. I also decided against implementing external c files. I was thinking
about doing so with the mish commands and with history, however the "verbose"
variable made me decide against adding the complexity of sending extra
parameters or including the header files at odd spots for limited gains. The
hardest challenge faced was tokenizing. There was so many different things
online that were all radically different. I found one guide online that
I had to modify in order to get to work and I believe the implementation
is solid now.
