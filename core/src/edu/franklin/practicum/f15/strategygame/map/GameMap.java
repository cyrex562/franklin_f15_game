package edu.franklin.practicum.f15.strategygame.map;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import edu.franklin.practicum.f15.strategygame.GamePosition;

import java.util.ArrayList;


public class GameMap {
    public TiledMapTileLayer terrainLayer;
    public TiledMapTileLayer robotLayer;
    public TiledMapTileLayer itemObjectiveLayer;
    public TiledMapTileLayer fogOfWarLayer;
    public TiledMap tileMap;
    public int width;
    public int height;
    public Item[] items;
    public RandomEncounter[] randomEncounters;
    public ArrayList<GamePosition> visibleTiles;
    public ArrayList<GamePosition> grayTiles;

    public GameMap()
    {
        terrainLayer = null;
        robotLayer = null;
        itemObjectiveLayer = null;
        fogOfWarLayer = null;
        tileMap = null;
        items = null;
        randomEncounters = null;
        visibleTiles = new ArrayList<GamePosition>();
        grayTiles = new ArrayList<GamePosition>();
        width = -1;
        height = -1;

    }
}
