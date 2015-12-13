package edu.franklin.practicum.f15.strategygame.robot;

import edu.franklin.practicum.f15.strategygame.GamePosition;
import edu.franklin.practicum.f15.strategygame.Orientation;

public class RobotPosition extends GamePosition {
    public Orientation orientation;

    public RobotPosition() {
        super();
        xPos = 0;
        yPos = 0;
        orientation = Orientation.N;
    }

    public RobotPosition(int x, int y, Orientation o) {
        super(x, y);
        orientation = o;
    }

    @Override
    public String toString() {
        return String.format("x: %d, y: %d, o:%s", xPos, yPos, orientation.toString());
    }
}
