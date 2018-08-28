Maze Solving


What the Program Does:
* Prints a nice little welcome message for the user.
* Prompts the user to enter a file name. Opens that file – if the file doesn’t exist, prints an error and end the program. Otherwise, reads in the maze (see below) and prints out the original maze.
* Solve the maze (using Breadth-First/Depth-First search algorithm), and prints the solved maze. 
* Ends the program.


A Maze File:
A maze file is a text file that has the following format:
* The first line contains two integers –first is the number of rows in the maze, and the second is the number of columns in the maze. We’ll say they are N rows and M columns.
* Then, there are N rows of data. Each row consists of M characters, either a # (which represents a wall) or a space (which represents an open space).
You can make a few assumptions about how the maze is constructed:
* The maze’s entrance is on the left (in column 0). There is only one entrance.
* The maze’s exit is on the right side (in column M-1). There could be multiple exits. 
* There may or may not be a path through the maze. If there is no solution, the program prints a message to that effect instead of printing the solved maze.
* You can only move up, down, left, or right in the maze; no diagonal moves are possible.
* Once solved, the solution path is represented with *.

Bijay Ranabhat

