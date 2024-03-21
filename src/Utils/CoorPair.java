package Utils;

public class CoorPair {
    private double xCoor;
    private double yCoor;
    private int token;

    public CoorPair(double xCoor, double yCoor) {
        this.xCoor = xCoor;
        this.yCoor = yCoor;
        token = tokenize();
    }

    public CoorPair(CoorPair coorPair) {
        this.xCoor = coorPair.getXCoor();
        this.yCoor = coorPair.getYCoor();
        token = tokenize();
    }

    public void setCoordinates(double xCoor, double yCoor) {
        this.xCoor = xCoor;
        this.yCoor = yCoor;
        token = tokenize();
    }

    public void setXCoor(double xCoor) {
        this.xCoor = xCoor;
        token = tokenize();
    }

    public void setYCoor(double yCoor) {
        this.yCoor = yCoor;
        token = tokenize();
    }

    public double getXCoor() {
        return xCoor;
    }

    public double getYCoor() {
        return yCoor;
    }

    public int getColumn() { return (int) (xCoor / 60.0);}

    public int getRow() { return (int) (yCoor / 60.0);}

    public int getToken() {
        return token;
    }

    public long getShiftedToken() {
        return BitBoard.shiftToken(token);
    }

    public boolean coorEquals(CoorPair pairToCheck) {
        return ( this.token == pairToCheck.getToken() );
    }

    /**
     * Checks if a coordinate pair is in bounds of the board
     *
     * @return if in bounds
     */
    public boolean isInBounds() {
        return (this.xCoor > -1 & this.xCoor < 480 &
                this.yCoor > -1 & this.yCoor < 480);
    }

    @Override
    public String toString() {
        return "(" + xCoor + ", " + yCoor + ")";
    }

    private int tokenize() {
        int token;
        int x = (int) (xCoor / 60.0);
        int y = (int) (yCoor / 60.0);

        token = (y*8) + x;

        return token;
    }

    public static int tokenize(double oldX, double oldY) {
        int token;
        int x = (int) (oldX / 60.0);
        int y = (int) (oldY / 60.0);

        token = (y*8) + x;

        return token;
    }

    /**
     * Reverses a token back into a pair of coordinates
     * @param token token to be reversed
     * @return Coordinate pair
     */
    public static CoorPair reverseToken(Integer token) {
        int y;
        int x;
        y = (int)Math.floor(token / 8.0);
        x = token - y*8;

        return new CoorPair(x*60, y*60);
    }

}
