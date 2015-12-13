package edu.franklin.practicum.f15.strategygame;

import edu.franklin.practicum.f15.strategygame.map.SaveableGameMap;
import edu.franklin.practicum.f15.strategygame.robot.Robot;


public class SaveGame {
    public Player player;
    public SaveableGameMap map;
    public Robot robot;
    public String name;

    public SaveGame() {
        name = "";
        player = null;
        map = null;
        robot = null;
    }

    @Override
    public String toString() {
        return name;
    }
}
