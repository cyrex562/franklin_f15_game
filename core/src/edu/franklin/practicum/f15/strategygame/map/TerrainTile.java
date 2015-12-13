package edu.franklin.practicum.f15.strategygame.map;

import edu.franklin.practicum.f15.strategygame.GamePosition;

public class TerrainTile extends GamePosition {
	public TerrainType terrainType;
	public float xOffset;
	public float yOffset;

	public TerrainTile() {
		super();
		terrainType = TerrainType.SAND;
	}
}
