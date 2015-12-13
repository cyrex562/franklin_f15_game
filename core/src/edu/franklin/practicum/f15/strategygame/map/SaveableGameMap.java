package edu.franklin.practicum.f15.strategygame.map;

import edu.franklin.practicum.f15.strategygame.GamePosition;
import edu.franklin.practicum.f15.strategygame.robot.RobotPosition;

import java.util.ArrayList;
import java.util.List;

public class SaveableGameMap {
	public final List<TerrainTile> terrainTiles;
	public final List<FOWTile> fowTiles;
	public final List<ObjectiveTile> itemTiles;
	public int mapWidth;
	public int mapHeight;
	private final RobotPosition robotPosition;
	private final GamePosition startPoint;
	private final GamePosition midPoint;
	private final GamePosition endPoint;

	public SaveableGameMap() {
		robotPosition = new RobotPosition();
		terrainTiles = new ArrayList<TerrainTile>();
		fowTiles = new ArrayList<FOWTile>();
		itemTiles = new ArrayList<ObjectiveTile>();
		mapWidth = 0;
		mapHeight = 0;
		startPoint = new GamePosition();
		midPoint = new GamePosition();
		endPoint = new GamePosition();
	}
}
