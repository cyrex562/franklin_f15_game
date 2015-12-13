package edu.franklin.practicum.f15.strategygame.map;

/*
 * Created by Kent N. on 10/16/15
 *
 * A class that creates RandomEncounter objects for distribution across the
 * gamemap.  It is possible this class belongs in a different packet,
 * or that it needs to be redone to fit game play.  This is more of a 
 * holder class so that I can render maps without error.
*/

import edu.franklin.practicum.f15.strategygame.GamePosition;

public class RandomEncounter extends GamePosition{
    public String name;
    private final String description;
	public boolean beenFound;
	public final RandomEncounterType reType;
	public final int points;

	public RandomEncounter (int x, int y, RandomEncounterType ret, int p) {
        super(x, y);
        beenFound = false;
        reType = ret;
        points = p;
        description = "";
	}
}
