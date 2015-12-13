package edu.franklin.practicum.f15.strategygame.map;

/*
 * Created by Kent N. on 10/16/15
 *
 * A class that creates Item objects for distribution across the
 * gamemap.  It is possible this class belongs in a different packet,
 * or that it needs to be redone to fit game play.  This is more of a 
 * holder class so that I can render maps without error.
*/

import edu.franklin.practicum.f15.strategygame.GamePosition;

public class Item extends GamePosition {
    public String name;
    private final String description;
    public boolean beenUsed;
    public final ItemType inside;
    public final int weight;
    public final int points;
    
    // Index of randomSet from which location of Item was pulled.
    // Needed to identify where in randomSet to start building RandomEncounters
    public final int index;

    public Item() {
        super();
        description = "";
        index = -1;
        beenUsed = false;
        inside = ItemType.INVALID;
        weight = -1;
        points = -1;
    }

    public Item(int x, int y, int i, ItemType ii, int w, int p) {
        super(x, y);
        description = "";
        index = i;
        beenUsed = false;
        inside = ii;
        weight = w;
        points = p;
    }
}