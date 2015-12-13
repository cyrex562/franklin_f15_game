package edu.franklin.practicum.f15.strategygame.map;

import edu.franklin.practicum.f15.strategygame.GamePosition;

public class ObjectiveTile extends GamePosition {
	public ObjectiveType objectiveType;
	public float xOffset;
	public float yOffset;
	public Item item;
	public final RandomEncounter randomEncounter;

	public ObjectiveTile() {
		super();
		objectiveType = ObjectiveType.NONE;
		xOffset = 0.0f;
		yOffset = 0.0f;
		item = null;
		randomEncounter = null;
	}
}
