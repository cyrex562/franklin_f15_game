package edu.franklin.practicum.f15.strategygame.map;

import edu.franklin.practicum.f15.strategygame.GamePosition;

public class FOWTile extends GamePosition {
	public FOWType fowType;
	public float xOffset;
	public float yOffset;

	public FOWTile() {
		super();
		fowType = FOWType.NONE;
		xOffset = 0.0f;
		yOffset = 0.0f;

	}

	public FOWTile(FOWType inFOWType, float inXOff, float inYOff, int inX, int inY) {
		super(inX, inY);
		fowType = inFOWType;
		xOffset = inXOff;
		yOffset = inYOff;
	}
}
