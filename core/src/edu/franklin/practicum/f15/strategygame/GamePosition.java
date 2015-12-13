package edu.franklin.practicum.f15.strategygame;

public class GamePosition {
    public int xPos;
    public int yPos;

    public GamePosition() {
        xPos = 0;
        yPos = 0;
    }

    public GamePosition(int x, int y) {
        xPos = x;
        yPos = y;
    }

    @Override
    public String toString() {
        return String.format("x: %d, y: %d", xPos, yPos);
    }
}
