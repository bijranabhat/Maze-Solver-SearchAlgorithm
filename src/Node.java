//Bijay Ranabhat


public class Node
{
    private Location parent;
    private Location loc;
    private boolean hasParent;

    public Node(Location pLoc, Location pParent)
    {
        loc = pLoc;
        parent = pParent;
        hasParent = true;
    }

    public Node(int xLoc, int yLoc, int xParent, int yParent)
    {
        loc = new Location(xLoc, yLoc);
        parent = new Location(xParent, yParent);
        hasParent = true;
    }

    public Node(int xLoc, int yLoc)
    {
        loc = new Location(xLoc, yLoc);
        hasParent = false;
    }

    public Location getLoc(){return loc;}
    public Location getParent(){return parent;}
    public boolean getHasParent(){return hasParent;}

    public class Location
    {
        public int row;
        public int col;

        public Location(int pRow, int pCol)
        {
            row = pRow;
            col = pCol;
        }

        public int getRow(){return row;}
        public int getCol(){return col;}

        public boolean equals(Location l)
        {
            return row == l.row && col == l.col;
        }

    }


}
