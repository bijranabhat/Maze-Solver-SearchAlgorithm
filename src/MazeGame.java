//Bijay Ranabhat
//Maze Solver

import java.util.*;
import java.awt.*;
import java.awt.Graphics;
import javax.swing.*;
import java.io.*;
import java.util.concurrent.*;


public class MazeGame
{
    // General Information about how the program operates
    // - Central information about the maze is kept in array list:
    // - + 0 means the spot is open
    // - + 1 means the spot is a wall
    // - + 2 means the spot is visited already
    // - + 3 means the spot is part of the final path


    //General boilterplate animation information
    private JFrame frame;
    private DrawingPanel dp;
    private ArrayList<ArrayList<Integer>> maze;
    private Stack<Node> closed = new Stack<Node>();

    //Information about the type of search and maze
    private int numRows;
    private int numCols;
    private int searchType;

    //Window Information
    private int WINDOW_WIDTH = 1200;
    private int WINDOW_HEIGHT = 700;

    //Used to draw the maze
    private static int SQUARE_LENGTH = 10;
    private static int SQUARE_WIDTH = 10;
    private static int DRAWSTART_X = 10;
    private static int DRAWSTART_Y = 10;

    //Diagnostic Variables
    private int nodesExpanded = 0;
    private String searchName;
    private int pathLength = 0;
    private boolean pathFound = false;


    //Getters
    public ArrayList<ArrayList<Integer>> getMaze(){return maze;}


    // Helper method for reading from a text file.
    public static Scanner getFileScanner(String filename)
    {
        Scanner myFile;
        try { myFile = new Scanner(new FileReader(filename)); }
        catch (Exception e)
        {
            System.out.println("\nFile not found: " + filename +"\nTry another name.");
            return null;
        }
        return myFile;
    }


    // Constructor. Used to read a maze in from a file
    public MazeGame()
    {
        Scanner sc = new Scanner(System.in);
        maze = new ArrayList<ArrayList<Integer>>();
        System.out.println("Welcome to the Maze Game!\n\n" +
                            "Please enter the file name of your maze, which should be\n" +
                            "written as a text file (any non-space characters will be\n" +
                            "considered as walls).");

        Scanner mazeReader = null;
        while (mazeReader == null)
        {
            String fileName = sc.nextLine().trim();
            mazeReader = getFileScanner(fileName);
        }

        //Read the maze into the program
        readMaze(mazeReader);

        //Get Search Type
        getSearchType();

        //Draw the Maze
        setDrawingPanel();

        //Search
        if (searchType == 1){searchName = "Depth First Search"; depthFirstSearch();}
        if (searchType == 2){searchName = "Breadth First Search"; breadthFirstSearch();}

    }

    //Is the input for the search type valid?
    public static boolean validSearchType(String s)
    {
        try
        {
            int input = Integer.parseInt(s);
            if(input < 1 || input > 3)
            {
                Integer.parseInt("intentional error");
            }
            return true;
        }
        catch (Exception e)
        {
            System.out.println("\nInput invalid, please enter an integer from 1 to 3.");
            return false;
        }
    }

    //Get Search Type
    public void getSearchType()
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("\nMaze has been read successfully!\n" +
                "What kind of search do you want to do?\n" +
                "Type the NUMBER next to your choice\n\n" +
                " - (1) - Depth First Search\n" +
                " - (2) - Breadth First Search\n");

        while(true)
        {
                String tempSearchType = sc.nextLine();
                if (validSearchType(tempSearchType))
                {
                    searchType = Integer.parseInt(tempSearchType);
                    return;
                }
        }

    }

    //Read maze information in from file
    public void readMaze(Scanner mazeReader)
    {
        try
        {
            numRows = mazeReader.nextInt();
            numCols = mazeReader.nextInt();
            mazeReader.nextLine();

            System.out.println("\nRows: " + numRows + " Columns: " + numCols);

            while (mazeReader.hasNext())
            {
                String rowString = mazeReader.nextLine();
                if (rowString.length() != numCols)
                {
                    System.out.println("\nERROR: Maze not formatted to uniform dimensions.");
                    System.exit(0);
                }
                System.out.println(rowString);
                ArrayList<Integer> tempRow = new ArrayList<Integer>();
                for (char c: rowString.toCharArray())
                {
                    if (c == ' ') {tempRow.add(0);}
                    else {tempRow.add(1);}
                }

                maze.add(tempRow);
            }
        }
        catch (Exception e)
        {
            System.out.println("\nERROR: Maze file not formatted correctly.\n" +
                    "Please fix errors or choose a new file. Details below: ");
            e.printStackTrace();
        }

    }

    //Sets graphics to listen for key events and fit to window.
    private void setDrawingPanel()
    {
        dp = new DrawingPanel(this);
        frame = new JFrame("Maze Search!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        Container pane = frame.getContentPane();
        pane.add(dp);
        frame.setVisible(true);
    }

    //Checks to see if an exit node has been found
    public boolean isExit(Node n)
    {
        return (n.getLoc().col == numCols - 1);
    }

    //Visit the node and change its value after being expanded
    public void visit(Node n)
    {
        int row = n.getLoc().row;
        int col = n.getLoc().col;

        maze.get(row).set(col,2);
    }

    //Change the values of the matrix in the final path of the node
    public void finalPath(Node n)
    {
        int row = n.getLoc().row;
        int col = n.getLoc().col;

        maze.get(row).set(col,3);
    }

    //Checks to see if a particular coordinate (row,col) pair is a valid place to go
    public boolean isValid(Node n)
    {
        int row = n.getLoc().row;
        int col = n.getLoc().col;

        //Spot in a valid range for maze
        if ((row >= 0) && (row < numRows) && (col >= 0) && (col < numCols))
        {
            if (maze.get(row).get(col) == 1){return false;} // Spot is a wall
            if (maze.get(row).get(col) == 2){return false;} // Spot has been visited already
            return true;
        }
        else
        {
            return false;
        }

    }

    //Paints path found by the algorithm (changes the numbers so that it repaints)
    public void paintPath()
    {
        //Set goal node to start sequence
        Node pathNode = closed.pop();
        pathLength++;
        finalPath(pathNode);

        while(!closed.isEmpty())
        {
            Node nextNode = closed.pop();

            if (nextNode.getLoc().equals(pathNode.getParent()))
            {
                pathLength++;
                finalPath(nextNode);
                pathNode = nextNode;
                if (!pathNode.getHasParent())
                    return;
            }
        }
    }

    // Breadth first search
    public void breadthFirstSearch()
    {
        //Queue to track the places to look
        Deque<Node> open = new ArrayDeque<Node>();

        //Find the start node
        for (int row = 0; row < numRows; row++)
        {
            if (maze.get(row).get(0) == 0)
            {
                open.add(new Node(row,0));
            }
        }

        while (!open.isEmpty())
        {
            try
            {
                TimeUnit.MILLISECONDS.sleep(20);
            }
            catch (Exception e)
            {
                System.out.println("\nERROR: Paint refresh has failed.");
            }
            //Remove the node from the queue
            Node tempNode = open.remove();

            //Checks neighbors for up, right, down, left ... in that order
            if (isValid(tempNode))
            {
                //Increase the number of nodes expanded
                nodesExpanded++;

                //Mark that tempNode was visited
                closed.push(tempNode);

                int row = tempNode.getLoc().row;
                int col = tempNode.getLoc().col;
                visit(tempNode);

                if (isExit(tempNode))
                {
                    System.out.println("Path found!");
                    pathFound = true;
                    paintPath();
                    return;
                }

                open.add(new Node(row - 1,col,row,col));    //Above
                open.add(new Node(row,col+1,row,col));      //Right
                open.add(new Node(row+1,col,row,col));      //Below
                open.add(new Node(row,col-1,row,col));      //Left
            }

        }

        System.out.println("\nNo path found.");
    }


    //Depth First Search
    public void depthFirstSearch()
    {
        //Queue to track the places to look
        Stack<Node> open = new Stack<Node>();

        //Find the start node
        for (int row = 0; row < numRows; row++)
        {
            if (maze.get(row).get(0) == 0)
            {
                open.add(new Node(row,0));
            }
        }

        while (!open.isEmpty())
        {
            //Slows down the animation
            try
            {
                TimeUnit.MILLISECONDS.sleep(20);
            }
            catch (Exception e)
            {
                System.out.println("\nERROR: Paint refresh has failed.");
            }

            //Remove the node from the queue
            Node tempNode = open.pop();

            //Checks neighbors for up, right, down, left ... in that order
            if (isValid(tempNode))
            {
                //Increase number of Nodes expanded
                nodesExpanded++;

                //Push tempNode onto list
                closed.push(tempNode);

                int row = tempNode.getLoc().row;
                int col = tempNode.getLoc().col;
                visit(tempNode);

                if (isExit(tempNode))
                {
                    System.out.println("\nPath Found!");
                    pathFound = true;
                    paintPath();
                    return;
                }

                // The order that the neighbors are added
                open.push(new Node(row,col-1,row,col));      //Left
                open.push(new Node(row+1,col,row,col));      //Below
                open.push(new Node(row - 1,col,row,col));    //Above
                open.push(new Node(row,col+1,row,col));      //Right
            }

        }

        System.out.println("\nNo path found.");

    }


    public class DrawingPanel extends JPanel {
        //Takes the game as its sole variable
        private MazeGame game;

        //Constructor activates Key listener and Mouse listener
        private DrawingPanel(MazeGame pGame) {
            game = pGame;
        }

        //Main graphics method. Paints the DrawingPanel based on the game state.
        public void paintComponent(Graphics g) {

            //Call super class graphics to start initial painting.
            super.paintComponent(g);

            //Convert graphics input into 2D graphics
            Graphics2D g2d = (Graphics2D) g;

            //Draw Diagnostic Information
            g2d.setColor(new Color(0,0,0));
            g2d.setFont(new Font("TimesRoman", Font.BOLD, 20));
            g2d.drawString("Game Statistics", 905,535);
            g2d.setFont(new Font("TimesRoman", Font.ITALIC, 18));
            g2d.drawString("Search Name: " + searchName,905,560);
            g2d.drawString("Nodes Expanded: " + nodesExpanded,905,585);
            g2d.drawString("Path Found? " + pathFound, 905, 610);
            g2d.drawString("Path Length: " + pathLength, 905, 635);

            try
            {
                //Draw the maze
                for (int row = 0; row < numRows; row++)
                {
                    for (int col = 0; col < numCols; col++)
                    {
                        if (maze.get(row).get(col) == 1)
                        {
                            g2d.setColor(new Color(0,0,0));
                            g2d.fillRect(DRAWSTART_X + 10*col,DRAWSTART_Y + 10*row,10,10);
                        }

                        else if(maze.get(row).get(col) == 2)
                        {
                            g2d.setColor(new Color(0,0,255));
                            g2d.fillOval(DRAWSTART_X + 10*col,DRAWSTART_Y + 10*(row),6,6);
                        }
                        else if(maze.get(row).get(col) == 3)
                        {
                            g2d.setColor(new Color(255,0,0));
                            g2d.fillOval(DRAWSTART_X + 10*col,DRAWSTART_Y + 10*(row),6,6);
                        }
                    }
                }
                this.repaint();
            }

            catch (Exception e)
            {
                System.out.println("\nERROR: Maze not formatted to uniform dimensions.");
                return;
            }


        }
    }


}
