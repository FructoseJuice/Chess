public class CoorPair {
    private double xCoor;
    private double yCoor;

    public CoorPair(double xCoor, double yCoor) {
        this.xCoor = xCoor;
        this.yCoor = yCoor;
    }

    public CoorPair(CoorPair coorPair) {
        this.xCoor = coorPair.getxCoor();
        this.yCoor = coorPair.getyCoor();
    }

    public void setCoordinates(double xCoor, double yCoor) {
        this.xCoor = xCoor;
        this.yCoor = yCoor;
    }

    public void setxCoor(double xCoor) {
        this.xCoor = xCoor;
    }

    public void setyCoor(double yCoor) {
        this.yCoor = yCoor;
    }

    public double getxCoor() {
        return xCoor;
    }

    public double getyCoor() {
        return yCoor;
    }

    public boolean coorEquals(CoorPair pairToCheck) {
        return (xCoor == pairToCheck.getxCoor() &
                yCoor == pairToCheck.getyCoor());
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
        if (xCoor >= yCoor) {
            return (int) (Math.pow(xCoor, 2) + xCoor + yCoor);
        } else {
            return (int) (xCoor + Math.pow(yCoor, 2));
        }
    }

}
