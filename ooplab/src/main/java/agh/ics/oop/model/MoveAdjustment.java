package agh.ics.oop.model;

public class MoveAdjustment {
    private final Vector2d position;
    private final MapDirection orientation;

    public MoveAdjustment(Vector2d position, MapDirection direction) {
        this.position = position;
        this.orientation = direction;
    }

    public MapDirection getOrientation() {
        return orientation;
    }

    public Vector2d getPosition() {
        return position;
    }
}
