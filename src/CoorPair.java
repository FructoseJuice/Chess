public class CoorPair {
    private double xCoor;
    private double yCoor;
    private int token;

    public CoorPair(double xCoor, double yCoor) {
        this.xCoor = xCoor;
        this.yCoor = yCoor;
        token = hashCode();
    }

    public CoorPair(CoorPair coorPair) {
        this.xCoor = coorPair.getxCoor();
        this.yCoor = coorPair.getyCoor();
        token = hashCode();
    }

    public void setCoordinates(double xCoor, double yCoor) {
        this.xCoor = xCoor;
        this.yCoor = yCoor;
        token = hashCode();
    }

    public void setxCoor(double xCoor) {
        this.xCoor = xCoor;
        token = hashCode();
    }

    public void setyCoor(double yCoor) {
        this.yCoor = yCoor;
        token = hashCode();
    }

    public double getxCoor() {
        return xCoor;
    }

    public double getyCoor() {
        return yCoor;
    }

    public int getToken() {
        return token;
    }

    public boolean coorEquals(CoorPair pairToCheck) {
        return ( this.token == pairToCheck.getToken() );
    }

    /**
     * Checks if a coordinate is in bounds of the board
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

    @Override
    public int hashCode() {
        int token;
        int x = (int) (xCoor / 60.0);
        int y = (int) (yCoor / 60.0);

        token = (y*8) + x;

        return token;
    }
    public static CoorPair reverseHash(Integer token) {
        int y;
        int x;
        y = (int)Math.floor(token / 8.0);
        x = token - y*8;

        return new CoorPair(x*60, y*60);
    }

}
