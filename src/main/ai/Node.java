package ai;

public class Node {

    public int col;
    public int row;
    protected Node parent; // Corresponding node

    protected int gCost; // Distance between start and current
    protected int hCost; // Distance between current and goal
    protected int fCost; // Sum of g and h

    protected boolean solid; // Collision
    protected boolean open; // Evaluation
    protected boolean checked; // Already evaluated

    public Node(int col, int row) {
        this.col = col;
        this.row = row;
    }
}