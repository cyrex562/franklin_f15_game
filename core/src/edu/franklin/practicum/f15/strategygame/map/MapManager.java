package edu.franklin.practicum.f15.strategygame.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import edu.franklin.practicum.f15.strategygame.*;
import edu.franklin.practicum.f15.strategygame.GamePosition;
import edu.franklin.practicum.f15.strategygame.Logger;
import edu.franklin.practicum.f15.strategygame.Orientation;
import edu.franklin.practicum.f15.strategygame.StrategyGame;
import edu.franklin.practicum.f15.strategygame.interaction.RobotAction;
import edu.franklin.practicum.f15.strategygame.robot.Robot;
import edu.franklin.practicum.f15.strategygame.robot.RobotPosition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

/*
 * Created by Kent N. on 10/11/15
 * 
 * The purpose of this class is to create the layers required to 
 * produce the final game tileMap.
 */

public class MapManager {
	private static final int ROBOT_LAYER = 1;
	private static final int FOG_OF_WAR_LAYER = 3;
	private static final int TERRAIN_LAYER = 0;
	private static final int ITEM_LAYER = 2;
	public static GameMap gameMap;
	private boolean hasMoved = false;
	private HexTile fowMapTiles[];

	private MapManager() {
	}

	public static MapManager getInstance() {
		return MapManagerHolder.instance;
	}

	public void initFOWMapTiles() {
		fowMapTiles = new HexTile[2];

		Texture fullFowTexture = new Texture(Gdx.files.internal("terrain/FogOfWarHidden.png"));
		TextureRegion[][] fullFowReg = TextureRegion.split(fullFowTexture, 128, 110);
		HexTile fullFowTile = new HexTile(new TextureRegion(fullFowReg[0][0]));
		fullFowTile.fowType = FOWType.FULL;

		Texture partFowTexture = new Texture(Gdx.files.internal("terrain/FogOfWarStale.png"));
		TextureRegion[][] partFowReg = TextureRegion.split(partFowTexture, 128, 110);
		HexTile partFowTile = new HexTile(new TextureRegion(partFowReg[0][0]));
		partFowTile.fowType = FOWType.PARTIAL;

		fowMapTiles = new HexTile[2];
		fowMapTiles[0] = fullFowTile;
		fowMapTiles[1] = partFowTile;
	}


	public void createGameWorld(StrategyGame game, Robot robot) {
		Logger.logMsg("creating game world");
		GameMap map = new GameMap();
		int mapWidth = game.MapWidth;
		int mapHeight = game.MapHeight;

		map.tileMap = new TiledMap();
		MapLayers layers = map.tileMap.getLayers();
		map.width = mapWidth;
		map.height = mapHeight;


		// Create Start, End, and Midpoint positions.
		Logger.logMsg("choosing start position");
		RobotPosition start = chooseStart(mapWidth, mapHeight);
		robot.startPoint = start;
		robot.currPosition = start;

		Logger.logMsg("choosing end position");
		RobotPosition end = chooseEnd(start, mapWidth, mapHeight);
		robot.endPoint = end;

		Logger.logMsg("choosing mid position");
		RobotPosition mid = chooseMid(start, end, mapWidth, mapHeight);
		System.out.println(mid.xPos + ", " + mid.yPos);
		robot.midPoint = mid;

		// create terrain layer
		Logger.logMsg("creating terrain layer");
		map.terrainLayer = createTerrainLayer(start, end, mid, mapWidth, mapHeight);
		map.terrainLayer = testTerrainPath(map.terrainLayer, start, mid);
		map.terrainLayer = testTerrainPath(map.terrainLayer, mid, end);
		layers.add(map.terrainLayer);

		// create robot layer
		Logger.logMsg("creating robot layer");
		map.robotLayer = createRobotLayer(start, mapWidth, mapHeight);
		layers.add(map.robotLayer);

		// Create array of items, array of random encounters, and then ItemObjective layer
		int[] randomSet = createRandomSet(mapWidth, mapHeight);
		Logger.logMsg("choosing item locations");
		map.items = createItems(randomSet, map.terrainLayer, mapWidth, mapHeight);

		Logger.logMsg("choosing random encounter locations");
		map.randomEncounters = createRandomEncounters(map.terrainLayer, mapWidth, mapHeight, randomSet, map.items);

		Logger.logMsg("creating item/objective layer");
		map.itemObjectiveLayer
				= createItemObjectiveLayer(start, end, mid, map.items, map.randomEncounters, mapWidth, mapHeight);
		layers.add(map.itemObjectiveLayer);

		Logger.logMsg("creating visible throug fog of war tiles");
		map.visibleTiles = createVisibleTiles(start, mapWidth, mapHeight);

		Logger.logMsg("creating fog of war layer");
		initFOWMapTiles();
		map.fogOfWarLayer = createFogOfWarLayer(mapWidth, mapHeight, map.visibleTiles);
		layers.add(map.fogOfWarLayer);

		gameMap = map;
	}

	private TiledMapTileLayer createTerrainLayer(
			RobotPosition startPoint, RobotPosition endPoint, RobotPosition midPoint, int w, int h) {
		Logger.logMsg("creating terrain layer");

		Texture grassTexture = new Texture(Gdx.files.internal("terrain/Grass.png"));
		TextureRegion[][] grassReg = TextureRegion.split(grassTexture, 128, 110);

		Texture waterTexture = new Texture(Gdx.files.internal("terrain/WaterQuick2.png"));
		TextureRegion[][] waterReg = TextureRegion.split(waterTexture, 128, 110);

		Texture swampTexture = new Texture(Gdx.files.internal("terrain/Swamp.png"));
		TextureRegion[][] swampReg = TextureRegion.split(swampTexture, 128, 110);

		Texture sandTexture = new Texture(Gdx.files.internal("terrain/SandQuick.png"));
		TextureRegion[][] sandReg = TextureRegion.split(sandTexture, 128, 110);

		Texture rockTexture = new Texture(Gdx.files.internal("terrain/Rock.png"));
		TextureRegion[][] rockReg = TextureRegion.split(rockTexture, 128, 110);

		Texture treeTexture = new Texture(Gdx.files.internal("terrain/Forest.png"));
		TextureRegion[][] treeReg = TextureRegion.split(treeTexture, 128, 110);

		Texture brushTexture = new Texture(Gdx.files.internal("terrain/GrassQuick.png"));
		TextureRegion[][] brushReg = TextureRegion.split(brushTexture, 128, 110);

		TiledMapTile[] tiles = new TiledMapTile[7];

		HexTile tile0 = new HexTile(new TextureRegion(grassReg[0][0]));
		tile0.setIsPassable(true);
		tile0.setTerrainType(TerrainType.GRASS);
		tile0.setMovePenalty(0.0);
		tiles[0] = tile0;

		HexTile tile1 = new HexTile(new TextureRegion(sandReg[0][0]));
		tile1.setIsPassable(true);
		tile1.setTerrainType(TerrainType.SAND);
		tile1.setMovePenalty(0.25);
		tiles[1] = tile1;

		HexTile tile2 = new HexTile(new TextureRegion(brushReg[0][0]));
		tile2.setIsPassable(true);
		tile2.setTerrainType(TerrainType.BRUSH);
		tile2.setMovePenalty(0.5);
		tiles[2] = tile2;

		HexTile tile3 = new HexTile(new TextureRegion(swampReg[0][0]));
		tile3.setIsPassable(true);
		tile3.setTerrainType(TerrainType.SWAMP);
		tile3.setMovePenalty(0.75);
		tiles[3] = tile3;

		HexTile tile4 = new HexTile(new TextureRegion(rockReg[0][0]));
		tile4.setIsPassable(false);
		tile4.setTerrainType(TerrainType.ROCK);
		tile4.setMovePenalty(1.0);
		tiles[4] = tile4;

		HexTile tile5 = new HexTile(new TextureRegion(treeReg[0][0]));
		tile5.setIsPassable(false);
		tile5.setTerrainType(TerrainType.TREE);
		tile5.setMovePenalty(1.0);
		tiles[5] = tile5;

		HexTile tile6 = new HexTile(new TextureRegion(waterReg[0][0]));
		tile6.setIsPassable(false);
		tile6.setTerrainType(TerrainType.WATER);
		tile6.setMovePenalty(1.0);
		tiles[6] = tile6;

		// Making special tiles for start and end positions.
		Texture startTexture = new Texture(Gdx.files.internal("terrain/Grass.png"));
		TextureRegion[][] startReg = TextureRegion.split(startTexture, 128, 110);

		Texture endTexture = new Texture(Gdx.files.internal("terrain/Fire.png"));
		TextureRegion[][] endReg = TextureRegion.split(endTexture, 128, 110);

		Texture midTexture = new Texture(Gdx.files.internal("terrain/MountainQuick.png"));
		TextureRegion[][] midReg = TextureRegion.split(midTexture, 128, 110);

		TiledMapTile[] startEndTiles = new TiledMapTile[3];

		HexTile startTile = new HexTile(new TextureRegion(startReg[0][0]));
		startTile.setIsPassable(true);
		startTile.setTerrainType(TerrainType.GRASS);
		startTile.setMovePenalty(0.0);
//    	startTile.setIsStart(true);
		startTile.objectiveType = ObjectiveType.START_POINT;
		startEndTiles[0] = startTile;

		HexTile endTile = new HexTile(new TextureRegion(endReg[0][0]));
		endTile.setIsPassable(true);
		endTile.setTerrainType(TerrainType.GRASS);
		endTile.setMovePenalty(0.0);
//    	endTile.setIsEnd(true);
		endTile.objectiveType = ObjectiveType.END_POINT;
		startEndTiles[1] = endTile;

		HexTile midTile = new HexTile(new TextureRegion(midReg[0][0]));
		midTile.setIsPassable(true);
		midTile.setTerrainType(TerrainType.GRASS);
		midTile.setMovePenalty(0.0);
//        midTile.setIsMid(true);
		midTile.objectiveType = ObjectiveType.MID_POINT;
		startEndTiles[2] = midTile;

		// Begin layer creation cell by cell.
		TiledMapTileLayer.Cell cell;
		TiledMapTileLayer terrainLayer = new TiledMapTileLayer(w, h, 128, 110);
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				// If starting point
				if (startPoint.xPos == x && startPoint.yPos == y) {
					cell = new TiledMapTileLayer.Cell();
					cell.setTile(startEndTiles[0]);
					terrainLayer.setCell(x, y, cell);
				}
				// If end point
				else if (endPoint.xPos == x && endPoint.yPos == y) {
					cell = new TiledMapTileLayer.Cell();
					cell.setTile(startEndTiles[1]);
					terrainLayer.setCell(x, y, cell);
				}
				// If mid point
				else if (midPoint.xPos == x && midPoint.yPos == y) {
					cell = new TiledMapTileLayer.Cell();
					cell.setTile(startEndTiles[2]);
					terrainLayer.setCell(x, y, cell);
				} else {
					int id = (int) (Math.random() * 7);
					cell = new TiledMapTileLayer.Cell();
					cell.setTile(tiles[id]);
					terrainLayer.setCell(x, y, cell);
				}
			}
		}
		terrainLayer.setName("Terrain");
		return terrainLayer;
	}

	public TiledMapTileLayer createRobotLayer(RobotPosition sp, int w, int h) {
		Logger.logMsg("creating robot layer");
		Texture robot = new Texture(Gdx.files.internal("sprites/RobotN.png"));
		TextureRegion[][] robotReg = TextureRegion.split(robot, 120, 103);
		TiledMapTile robotTile = new HexTile(new TextureRegion(robotReg[0][0]));
		robotTile.setOffsetX(4.0f);
		robotTile.setOffsetY(3.5f);

		TiledMapTileLayer.Cell cell;
		TiledMapTileLayer robotLayer = new TiledMapTileLayer(w, h, 128, 110);
		cell = new TiledMapTileLayer.Cell();
		cell.setTile(robotTile);
		robotLayer.setCell(sp.xPos, sp.yPos, cell);

		robotLayer.setName("Robot");
		return robotLayer;
	}

	private TiledMapTileLayer createItemObjectiveLayer(
			RobotPosition sp, RobotPosition ep, RobotPosition mp, Item[] items, RandomEncounter[] res, int w, int h) {
		Logger.logMsg("creating item/objective layer");
		// Start, mid, and end textures and tiles
		Texture startTexture = new Texture(Gdx.files.internal("sprites/StartPoint.png"));
		TextureRegion[][] startReg = TextureRegion.split(startTexture, 40, 40);

		Texture endTexture = new Texture(Gdx.files.internal("sprites/EndPoint.png"));
		TextureRegion[][] endReg = TextureRegion.split(endTexture, 40, 40);

		Texture midTexture = new Texture(Gdx.files.internal("sprites/MidPoint.png"));
		TextureRegion[][] midReg = TextureRegion.split(midTexture, 40, 40);

		TiledMapTile[] startEndTiles = new TiledMapTile[3];

		HexTile startTile = new HexTile(new TextureRegion(startReg[0][0]));
//		startTile.setIsStart(true);
		startTile.objectiveType = ObjectiveType.START_POINT;
		startTile.setOffsetX(44.0f);
		startTile.setOffsetY(35.0f);
		startEndTiles[0] = startTile;

		HexTile endTile = new HexTile(new TextureRegion(endReg[0][0]));
//		endTile.setIsEnd(true);
		endTile.objectiveType = ObjectiveType.END_POINT;
		endTile.setOffsetX(44.0f);
		endTile.setOffsetY(35.0f);
		startEndTiles[1] = endTile;

		HexTile midTile = new HexTile(new TextureRegion(midReg[0][0]));
//        midTile.setIsMid(true);
		midTile.objectiveType = ObjectiveType.MID_POINT;
		midTile.setOffsetX(44.0f);
		midTile.setOffsetY(35.0f);
		startEndTiles[2] = midTile;

		// Item and Random Encounter textures
		Texture itemTexture = new Texture(Gdx.files.internal("sprites/ItemPoint.png"));
		TextureRegion[][] itemReg = TextureRegion.split(itemTexture, 40, 40);

		Texture reTexture = new Texture(Gdx.files.internal("sprites/REPoint.png"));
		TextureRegion[][] reReg = TextureRegion.split(reTexture, 40, 40);

		// Begin layer creation
		TiledMapTileLayer.Cell cell;
		TiledMapTileLayer itemObjLayer = new TiledMapTileLayer(w, h, 128, 110);

		// Starting point
		cell = new TiledMapTileLayer.Cell();
		cell.setTile(startEndTiles[0]);
		itemObjLayer.setCell(sp.xPos, sp.yPos, cell);

		// End point
		cell = new TiledMapTileLayer.Cell();
		cell.setTile(startEndTiles[1]);
		itemObjLayer.setCell(ep.xPos, ep.yPos, cell);

		// Mid point
		cell = new TiledMapTileLayer.Cell();
		cell.setTile(startEndTiles[2]);
		itemObjLayer.setCell(mp.xPos, mp.yPos, cell);

		// Item tiles
		for (Item item : items) {
			HexTile objTile = new HexTile(new TextureRegion(itemReg[0][0]));
			objTile.setOffsetX(44.0f);
			objTile.setOffsetY(35.0f);
			objTile.objectiveType = ObjectiveType.ITEM;
			objTile.setItem(item);
			cell = new Cell();
			cell.setTile(objTile);
			itemObjLayer.setCell(item.xPos, item.yPos, cell);
		}

		// RandomEncounter tiles
		for (RandomEncounter re : res) {
			HexTile reTile = new HexTile(new TextureRegion(reReg[0][0]));
			reTile.setOffsetX(44.0f);
			reTile.setOffsetY(35.0f);
			reTile.objectiveType = ObjectiveType.RANDOM_ENCOUNTER_POINT;
			reTile.setRandomEncounter(re);
			cell = new Cell();
			cell.setTile(reTile);
			itemObjLayer.setCell(re.xPos, re.yPos, cell);
		}
		itemObjLayer.setName("Item-Objective");
		return itemObjLayer;
	}

	private TiledMapTileLayer createFogOfWarLayer(int w, int h, ArrayList<GamePosition> visibleTiles) {
		Logger.logMsg("creating FOW layer");
		TiledMapTileLayer fowLayer = new TiledMapTileLayer(w, h, Defines.TILE_WIDTH, Defines.TILE_HEIGHT);
		TiledMapTileLayer.Cell cell;

		// Make entire black tileMap
		makeEntireBlackMap(w, h, fowLayer);

		// Remove black tiles so underneath is visible
		for (GamePosition temp : visibleTiles) {
			cell = new Cell();
			cell.setTile(null);
			fowLayer.setCell(temp.xPos, temp.yPos, null);
		}

		fowLayer.setName("FULL");
		return fowLayer;
	}

	private Orientation getRandomOrientation() {
		Logger.logMsg("getting random orientation");
		Orientation[] ov = Orientation.values();
		int idx = (int) (Math.random() * ov.length);
		return ov[idx];
	}

	/*
	 * Randomly chooses the starting position based on width and height of tileMap.
	 */
	private RobotPosition chooseStart(int w, int h) {
		Logger.logMsg("choosing start position");
//		int[] startLoc = new int[2];
//        startLoc[0] = (int) (Math.random() * w);
//		startLoc[1] = (int) (Math.random() * h);
//		return startLoc;
		return new RobotPosition((int) (Math.random() * w), (int) (Math.random() * h), getRandomOrientation());
	}

	/*
	 * Randomly chooses the end position based on tileMap width and height, and start position.
	 */
	private RobotPosition chooseEnd(RobotPosition sp, int w, int h) {
		Logger.logMsg("choosing end position");
		int[] endLoc = new int[2];
		double minDist;
		if (w > h || w >= h) {
			minDist = 0.45 * w;
		} else {
			minDist = 0.45 * h;
		}
		boolean acceptableEnd = false;
		int counter = 0;
		while (!acceptableEnd && counter <= 50) {
			int x = (int) (Math.random() * w);
			int y = (int) (Math.random() * h);
			// Not the start position.
			if (x != sp.xPos && y != sp.yPos) {
				int tempX = Math.abs(sp.xPos - x);
				int tempY = Math.abs(sp.yPos - y);
				double tempX2 = (double) tempX;
				double tempY2 = (double) tempY;

				if (minDist <= Math.hypot(tempX2, tempY2)) {
					endLoc[0] = x;
					endLoc[1] = y;
					acceptableEnd = true;
				}
			}
			counter++;
		}
		// If no end point is found after 50 attempts, the minimum distance
		// requirement is dropped.
		if (counter > 50) {
			while (!acceptableEnd) {
				int x = (int) (Math.random() * w);
				int y = (int) (Math.random() * h);
				// Not the start position.
				if (x != sp.xPos && y != sp.yPos) {
					endLoc[0] = x;
					endLoc[1] = y;
					acceptableEnd = true;
				}
			}
		}
		return new RobotPosition(endLoc[0], endLoc[1], Orientation.N);
	}

	/*
	 * Randomly chooses mid position based on start and end points.
	 * Mid point must be located a certain distance from both the start and end point.
	 * This distance is between 40% and 60% of the total distance between
	 * the start and end points.
	 */
	private RobotPosition chooseMid(
			RobotPosition sp, RobotPosition ep, int w, int h) {
		Logger.logMsg("choosing mid position");
		RobotPosition midLoc = new RobotPosition();
		int tempX = Math.abs(ep.xPos - sp.xPos);
		int tempY = Math.abs(ep.yPos - sp.yPos);
		double tempX2 = (double) tempX;
		double tempY2 = (double) tempY;
		double startEndDist = Math.hypot(tempX2, tempY2);

		boolean acceptableMid = false;
		int i = 0;
		while (!acceptableMid && i < 1000) {
			int x = (int) (Math.random() * w);
			int y = (int) (Math.random() * h);

			// Not start position
			if (x != sp.xPos && y != sp.yPos) {
				// Not end position
				if (x != ep.xPos && y != ep.yPos) {
					int tempSPX = Math.abs(sp.xPos - x);
					int tempSPY = Math.abs(sp.yPos - y);
					double tempSPX2 = (double) tempSPX;
					double tempSPY2 = (double) tempSPY;
					double distanceSP = Math.hypot(tempSPX2, tempSPY2);

					// If random point is acceptable distance from start
					if ((0.4 * startEndDist) <= distanceSP && distanceSP <= (0.6 * startEndDist)) {
						int tempEPX = Math.abs(ep.xPos - x);
						int tempEPY = Math.abs(ep.yPos - y);
						double tempEPX2 = (double) tempEPX;
						double tempEPY2 = (double) tempEPY;
						double distanceEP = Math.hypot(tempEPX2, tempEPY2);

						// If random point is also acceptable distance from end
						if ((0.4 * startEndDist) <= distanceEP && distanceEP <= (0.6 * startEndDist)) {
							midLoc.xPos = x;
							midLoc.yPos = y;
							acceptableMid = true;
						}
					}
				}
			}
			i++;
		}
		midLoc.orientation = Orientation.N;
		return midLoc;
	}

	/*
	 * Used to create random array of numbers, 0 through (tileMap area - 1).
	 * This array is created to eliminate need to check tiles whether they already
	 * contain an item or random encounter.
	 * Only used at tileMap creation.
	 */
	private int[] createRandomSet(int w, int h) {
		int area = w * h;
		Integer[] set = new Integer[area];

		for (int i = 0; i < area; i++) {
			set[i] = i;
		}

		Collections.shuffle(Arrays.asList(set));
		int[] returnSet = new int[area];
		for (int j = 0; j < area; j++) {
			returnSet[j] = set[j];
		}

		return returnSet;
	}

	/*
	 * Creates array of Items with random placement on passable Terrain tiles.
	 */
	private Item[] createItems(int[] set, TiledMapTileLayer terrain, int w, int h) {
		Logger.logMsg("generating items");
		int numOfItems = (w * h) / 10;
		// If tileMap has less than 10 total tiles
		if (numOfItems == 0) {
			numOfItems = 1;
		}

		ArrayList<Item> tempItems = new ArrayList<Item>();
		int counter = 0;

		for (int i = 0; i < numOfItems; i++) {
			boolean acceptableLoc = false;
			while (!acceptableLoc && counter < set.length) {
				int random = set[counter];
				int x = random % w;
				int y = random / w;

				Cell cell = terrain.getCell(x, y);
				HexTile tempTile = (HexTile) cell.getTile();

				// As long as terrain is passable, and the point isnt the start, end, or mid.
				if (tempTile.getIsPassable() && tempTile.objectiveType != ObjectiveType.START_POINT &&
						tempTile.objectiveType != ObjectiveType.END_POINT &&
						tempTile.objectiveType != ObjectiveType.MID_POINT) {
					ItemType inside;
					int weight;
					int points;

					// 0 = health, 1 = poison, 2 = rock, 3 = flora, 4 = points
					int itemType = (int) (Math.random() * 5);

					if (itemType == 0) {
						inside = ItemType.HEALTH;
						weight = 10;
						points = (int) ((Math.random() * 20) + 1);
					} else if (itemType == 1) {
						inside = ItemType.POISON;
						weight = 10;
						points = (-1) * (int) ((Math.random() * 20) + 1);
					} else if (itemType == 2) {
						inside = ItemType.ROCK;
						weight = 20;
						points = 0;
					} else if (itemType == 3) {
						inside = ItemType.FLORA;
						weight = 5;
						points = 0;
					} else {
						inside = ItemType.POINTS;
						weight = 15;
						int p = (int) (Math.random() * 3);
						if (p != 2) {
							points = (int) ((Math.random() * 50) + 1);
						} else {
							points = (-1) * (int) ((Math.random() * 25) + 1);
						}
					}

					Item tempItem = new Item(x, y, counter, inside, weight, points);
					tempItems.add(tempItem);
					acceptableLoc = true;
				}
				counter++;
			}
		}
		// ArrayList is used to handle the case where number of Items created is less than the
		// amount determined at beginning of method.  Also prevents null values in Item array.
		int length = tempItems.size();
		Item[] returnItems = new Item[length];
		for (int j = 0; j < length; j++) {
			returnItems[j] = tempItems.get(j);
		}

		return returnItems;
	}

	/*
	 * Creates array of Random Encounters with random placement on passable Terrain tiles.
	 */
	private RandomEncounter[] createRandomEncounters(
			TiledMapTileLayer terrain, int w, int h, int[] set, Item[] items) {
		Logger.logMsg("generating random encounters");
		int numOfRE = (w * h) / Defines.MAX_RANDOM_ENCOUNTERS;
		// If tileMap has less than 20 total tiles
		if (numOfRE == 0) {
			numOfRE = 1;
		}

		ArrayList<RandomEncounter> tempRE = new ArrayList<RandomEncounter>();
		int l = items.length;
		// Use the next number after the last location used to generate an Item
		int counter = items[l - 1].index + 1;

		for (int i = 0; i < numOfRE; i++) {
			boolean acceptableLoc = false;
			while (!acceptableLoc && counter < set.length) {
				int random = set[counter];
				int x = random % w;
				int y = random / w;

				Cell cell = terrain.getCell(x, y);
				HexTile tempTile = (HexTile) cell.getTile();

				// As long as terrain is passable, and the point isnt the start, end, or mid.
				if (tempTile.getIsPassable() && tempTile.objectiveType != ObjectiveType.START_POINT &&
						tempTile.objectiveType != ObjectiveType.END_POINT &&
						tempTile.objectiveType != ObjectiveType.MID_POINT) {
					RandomEncounterType reType;
					int points;

					// 0 = attack, 1 = supply drop, 2 = coconut, 3 = bump, 4 = mist, 5 = thieves
					int re = (int) (Math.random() * 6);

					if (re == 0) {
						reType = RandomEncounterType.ATTACK;
						points = (-1) * (int) ((Math.random() * 50) + 1);
					} else if (re == 1) {
						reType = RandomEncounterType.SUPPLY_DROP;
						points = (int) ((Math.random() * 50) + 1);
					} else if (re == 2) {
						reType = RandomEncounterType.COCONUT;
						points = (int) ((Math.random() * 50) + 1);
					} else if (re == 3) {
						reType = RandomEncounterType.BUMP;
						points = (-1) * (int) ((Math.random() * 20) + 1);
					} else if (re == 4) {
						reType = RandomEncounterType.MIST;
						points = (int) ((Math.random() * 50) + 1);
					} else {
						reType = RandomEncounterType.THIEVES;
						points = (-1) * (int) ((Math.random() * 20) + 1);
					}

					RandomEncounter temp = new RandomEncounter(x, y, reType, points);
					tempRE.add(temp);
					acceptableLoc = true;
				}
				counter++;
			}
		}
		// ArrayList is used to handle the case where number of RandomEs created is less than the
		// amount determined at beginning of method.  Also prevents null values in RandomE array.
		int length = tempRE.size();
		RandomEncounter[] returnRE = new RandomEncounter[length];
		for (int j = 0; j < length; j++) {
			returnRE[j] = tempRE.get(j);
		}
		return returnRE;
	}

	// Creates an arraylist of tiles that form ring around start point.
	public ArrayList<GamePosition> createVisibleTiles(
			RobotPosition sp, int w, int h) {
		Logger.logMsg("generating visible tiles");
//		ArrayList<int[]> visible = new ArrayList<int[]>();
		ArrayList<GamePosition> visible = new ArrayList<GamePosition>();
		visible.add(sp);

		// checking whether ring of tiles around robot are outside tileMap
		boolean xMinus = (sp.xPos - 1 >= 0);
		boolean yMinus = (sp.yPos - 1 >= 0);
		boolean xPlus = (sp.xPos + 1 < w);
		boolean yPlus = (sp.yPos + 1 < h);

		// Robot located in even column
		if (sp.xPos % 2 == 0) {
			if (yPlus) {
				visible.add(new GamePosition(sp.xPos, sp.yPos + 1));
			}
			if (xPlus) {
				visible.add(new GamePosition(sp.xPos + 1, sp.yPos));
			}
			if (xPlus && yPlus) {
				visible.add(new GamePosition(sp.xPos + 1, sp.yPos + 1));
			}
			if (yMinus) {
				visible.add(new GamePosition(sp.xPos, sp.yPos - 1));
			}
			if (xMinus) {
				visible.add(new GamePosition(sp.xPos - 1, sp.yPos));
			}
			if (xMinus && yPlus) {
				visible.add(new GamePosition(sp.xPos - 1, sp.yPos + 1));
			}
		}

		// Robot located in odd column
		if (sp.xPos % 2 != 0) {
			if (yPlus) {
				visible.add(new GamePosition(sp.xPos, sp.yPos + 1));
			}
			if (xPlus) {
				visible.add(new GamePosition(sp.xPos + 1, sp.yPos));
			}
			if (xPlus && yMinus) {
				visible.add(new GamePosition(sp.xPos + 1, sp.yPos - 1));
			}
			if (yMinus) {
				visible.add(new GamePosition(sp.xPos, sp.yPos - 1));
			}
			if (xMinus) {
				visible.add(new GamePosition(sp.xPos - 1, sp.yPos));
			}
			if (xMinus && yMinus) {
				visible.add(new GamePosition(sp.xPos - 1, sp.yPos - 1));
			}
		}
		return visible;
	}

//    GamePosition mapCoordToTileCoord(GamePosition in) {
//        GamePosition tileCoord = new GamePosition();
//
//        tileCoord.xPos = in.xPos;
//        if (in.xPos % 2 == 0) {
////            tileCoord.yPos = in.yPos - 1;
//            tileCoord.yPos  = in.yPos;
//        } else {
//            tileCoord.yPos = in.yPos;
//        }
//
//        return tileCoord;
//    }

	private TiledMapTileLayer testTerrainPath(
			TiledMapTileLayer terrain, RobotPosition sp, RobotPosition ep) {
		Logger.logMsg("testing terrain paths");
		// Determine direction of the end point from the start point.
		Orientation direction;

		// Same y value
		if (ep.yPos == sp.yPos) {
			// If start is west of end
			if (sp.xPos < ep.xPos) {
				direction = Orientation.E;
			} else {
				direction = Orientation.W;
			}
		}
		// Same x value
		else if (sp.xPos == ep.xPos) {
			// If start is south of end
			if (sp.yPos < ep.yPos) {
				direction = Orientation.N;
			} else {
				direction = Orientation.S;
			}
		} else if (sp.xPos < ep.xPos && sp.yPos < ep.yPos) {
			direction = Orientation.NE;
		} else if (sp.xPos > ep.xPos && sp.yPos < ep.yPos) {
			direction = Orientation.NW;
		} else if (sp.xPos > ep.xPos && sp.yPos > ep.yPos) {
			direction = Orientation.SW;
		} else {
			direction = Orientation.SE;
		}

		// Establish current position
		GamePosition current = new GamePosition();
		current.xPos = sp.xPos;
		current.yPos = sp.yPos;

		while (!(current.xPos == ep.xPos && current.yPos == ep.yPos)) {
			TiledMapTileLayer.Cell cell = terrain.getCell(current.xPos, current.yPos);
			HexTile tile = (HexTile) cell.getTile();

			// Fix terrain if its not passable.
			if (!(tile.getIsPassable())) {
				HexTile tempTile = fixTerrain();
				cell.setTile(tempTile);
				terrain.setCell(current.xPos, current.yPos, cell);
			}

			// Change current location based on Orientation value
			if (direction.equals(Orientation.N)) {
				current.yPos = current.yPos + 1;
			} else if (direction.equals(Orientation.S)) {
				current.yPos = current.yPos - 1;
			} else if (direction.equals(Orientation.E)) {
				current.xPos = current.xPos + 1;
			} else if (direction.equals(Orientation.W)) {
				current.xPos = current.xPos - 1;
			} else if (direction.equals(Orientation.NE)) {
				// if current is south of end
				if (current.xPos == ep.xPos) {
					direction = Orientation.N;
					current.yPos = current.yPos + 1;
				}
				// if current is west of end
				else if (current.yPos == ep.yPos) {
					direction = Orientation.E;
					current.xPos = current.xPos + 1;
				} else {
					if (current.xPos % 2 == 0) {
						current.xPos = current.xPos + 1;
						current.yPos = current.yPos + 1;
					} else {
						current.xPos = current.xPos + 1;
					}
				}
			} else if (direction.equals(Orientation.NW)) {
				// if current is south of end
				if (current.xPos == ep.xPos) {
					direction = Orientation.N;
					current.yPos = current.yPos + 1;
				}
				// if current is east of end
				else if (current.yPos == ep.yPos) {
					direction = Orientation.W;
					current.xPos = current.xPos - 1;
				} else {
					if (current.xPos % 2 == 0) {
						current.xPos = current.xPos - 1;
						current.yPos = current.yPos + 1;
					} else {
						current.xPos = current.xPos - 1;
					}
				}
			} else if (direction.equals(Orientation.SW)) {
				// if current is north of end
				if (current.xPos == ep.xPos) {
					direction = Orientation.S;
					current.yPos = current.yPos - 1;
				}
				// if current is east of end
				else if (current.yPos == ep.yPos) {
					direction = Orientation.W;
					current.xPos = current.xPos - 1;
				} else {
					if (current.xPos % 2 == 0) {
						current.xPos = current.xPos - 1;
					} else {
						current.xPos = current.xPos - 1;
						current.yPos = current.yPos - 1;
					}
				}
			} else {
				// if current is north of end
				if (current.xPos == ep.xPos) {
					direction = Orientation.S;
					current.yPos = current.yPos - 1;
				}
				// if current is west of end
				else if (current.yPos == ep.yPos) {
					direction = Orientation.E;
					current.xPos = current.xPos + 1;
				} else {
					if (current.xPos % 2 == 0) {
						current.xPos = current.xPos + 1;
					} else {
						current.xPos = current.xPos + 1;
						current.yPos = current.yPos - 1;
					}
				}
			}
		}

		return terrain;
	}

	private HexTile fixTerrain() {
		Logger.logMsg("fixing broken terrain");
		// Generating textures for the various passable terrains.
		Texture grassTexture = new Texture(Gdx.files.internal("terrain/Grass.png"));
		TextureRegion[][] grassReg = TextureRegion.split(grassTexture, 128, 110);

		Texture brushTexture = new Texture(Gdx.files.internal("terrain/GrassQuick.png"));
		TextureRegion[][] brushReg = TextureRegion.split(brushTexture, 128, 110);

		Texture swampTexture = new Texture(Gdx.files.internal("terrain/Swamp.png"));
		TextureRegion[][] swampReg = TextureRegion.split(swampTexture, 128, 110);

		Texture sandTexture = new Texture(Gdx.files.internal("terrain/SandQuick.png"));
		TextureRegion[][] sandReg = TextureRegion.split(sandTexture, 128, 110);

		int id = (int) (Math.random() * 4);
		HexTile tile;
		// Set tile to randomly chosen passable terrain.
		if (id == 0) {
			tile = new HexTile(new TextureRegion(grassReg[0][0]));
			tile.setIsPassable(true);
			tile.setTerrainType(TerrainType.GRASS);
			tile.setMovePenalty(0.0);
		} else if (id == 1) {
			tile = new HexTile(new TextureRegion(sandReg[0][0]));
			tile.setIsPassable(true);
			tile.setTerrainType(TerrainType.SAND);
			tile.setMovePenalty(0.25);
		} else if (id == 2) {
			tile = new HexTile(new TextureRegion(brushReg[0][0]));
			tile.setIsPassable(true);
			tile.setTerrainType(TerrainType.BRUSH);
			tile.setMovePenalty(0.5);
		} else {
			tile = new HexTile(new TextureRegion(swampReg[0][0]));
			tile.setIsPassable(true);
			tile.setTerrainType(TerrainType.SWAMP);
			tile.setMovePenalty(0.75);
		}

		return tile;
	}
	
	/*
	 * 
	 * Below are the helper methods used to pull information from the various tileMap layers
	 * or to update information within the tileMap layers.  This includes setting new textures
	 * for the robot sprite.
	 * 
	 */

	// Returns the HexTile at specific coordinate
	public HexTile getTerrainTile(GamePosition pos) {
		Logger.logMsg("getting terrain tile");
		TiledMap map = gameMap.tileMap;
		int x = pos.xPos;
		int y = pos.yPos;
		TiledMapTileLayer terrain = (TiledMapTileLayer) map.getLayers().get(0);
		TiledMapTileLayer.Cell cell = terrain.getCell(x, y);
		return (HexTile) cell.getTile();
	}

	// Returns the Terrain type at specific coordinate
	public TerrainType getTerrainType(GamePosition pos) {
		Logger.logMsg("getting terrain type");
		TiledMap map = gameMap.tileMap;
		int x = pos.xPos;
		int y = pos.yPos;
		TiledMapTileLayer terrain = (TiledMapTileLayer) map.getLayers().get(TERRAIN_LAYER);
		TiledMapTileLayer.Cell cell = terrain.getCell(x, y);
		HexTile tile = (HexTile) cell.getTile();
		return tile.getTerrainType();
	}

	// Returns whether tile at specific coordinate is passable.
	public boolean getTerrainPassable(GamePosition pos) {
		Logger.logMsg("getting terrain passability");
		TiledMap map = gameMap.tileMap;
		int x = pos.xPos;
		int y = pos.yPos;
		TiledMapTileLayer terrain = (TiledMapTileLayer) map.getLayers().get(TERRAIN_LAYER);
		TiledMapTileLayer.Cell cell = terrain.getCell(x, y);
		HexTile tile = (HexTile) cell.getTile();
		return tile.getIsPassable();
	}

	// Returns the Terrain penalty at specific coordinate
	public double getTerrainPenalty(GamePosition pos) {
		Logger.logMsg("getting terrain penalty");
		TiledMap map = gameMap.tileMap;
		int x = pos.xPos;
		int y = pos.yPos;
		TiledMapTileLayer terrain = (TiledMapTileLayer) map.getLayers().get(TERRAIN_LAYER);
		TiledMapTileLayer.Cell cell = terrain.getCell(x, y);
		HexTile tile = (HexTile) cell.getTile();
		return tile.getMovePenalty();
	}

	// Returns the Item at specific coordinate
	public Item getItem(GamePosition pos) {
		Logger.logMsg("getting item");
		TiledMap map = gameMap.tileMap;
		int x = pos.xPos;
		int y = pos.yPos;
		TiledMapTileLayer itemObj = (TiledMapTileLayer) map.getLayers().get(ITEM_LAYER);
		TiledMapTileLayer.Cell cell = itemObj.getCell(x, y);
		HexTile tile = (HexTile) cell.getTile();
		return tile.getItem();
	}

	// Returns the Random Encounter at specific coordinate
	public RandomEncounter getRandomEncounter(GamePosition pos) {
		Logger.logMsg("getting random encounter");
		TiledMap map = gameMap.tileMap;
		int x = pos.xPos;
		int y = pos.yPos;
		TiledMapTileLayer itemObj = (TiledMapTileLayer) map.getLayers().get(ITEM_LAYER);
		TiledMapTileLayer.Cell cell = itemObj.getCell(x, y);
		HexTile tile = (HexTile) cell.getTile();
		return tile.getRandomEncounter();
	}

	// Returns whether there is Item at specific coordinate
	public boolean getHasItem(GamePosition pos) {
		Logger.logMsg("checking for item at location");
		TiledMap map = gameMap.tileMap;
		int x = pos.xPos;
		int y = pos.yPos;
		TiledMapTileLayer itemObj = (TiledMapTileLayer) map.getLayers().get(ITEM_LAYER);
		TiledMapTileLayer.Cell cell = itemObj.getCell(x, y);
		if (cell != null) {
			HexTile tile = (HexTile) cell.getTile();
			return tile.objectiveType == ObjectiveType.ITEM;
		} else {
			return false;
		}
	}

	// Returns whether there is Random Encounter at specific coordinate
	public boolean getHasRE(GamePosition pos) {
		Logger.logMsg("checking for random encounter at location");
		TiledMap map = gameMap.tileMap;
		int x = pos.xPos;
		int y = pos.yPos;
		TiledMapTileLayer itemObj = (TiledMapTileLayer) map.getLayers().get(ITEM_LAYER);
		TiledMapTileLayer.Cell cell = itemObj.getCell(x, y);
		if (cell != null) {
			HexTile tile = (HexTile) cell.getTile();
			return tile.objectiveType == ObjectiveType.RANDOM_ENCOUNTER_POINT;
		} else {
			return false;
		}
	}

	// Processes the picked up item, changes game info, and returns string to display.
	public String processItem(Item item, Robot robot, StrategyGame game) {
		String display;
		if (item.inside.equals(ItemType.HEALTH)) {
			robot.currWeight += item.weight;
			robot.currHealth += item.points;
			if (robot.currHealth > 100) {
				robot.currHealth = 100;
			}
			display = "Repair pack found! Health: +" + item.points +
					". Weight: +" + item.weight + ".";
		} else if (item.inside.equals(ItemType.POISON)) {
			robot.currWeight += item.weight;
			robot.currHealth += item.points;
			display = "Acid found! Health: " + item.points +
					". Weight: +" + item.weight + ".";
		} else if (item.inside.equals(ItemType.ROCK)) {
			robot.currWeight += item.weight;
			display = "Shiny rock found! Weight: +" + item.weight + ".";
		} else if (item.inside.equals(ItemType.FLORA)) {
			robot.currWeight += item.weight;
			display = "Interesting plant found! Weight: +" + item.weight + ".";
		} else { // Item contains points
			robot.currWeight += item.weight;
			game.currentPlayer.score += item.points;
			display = item.points + " points found! Weight: +" + item.weight + ".";
		}

		showFoundItem(robot.currPosition);
		return display;
	}

	// Processes the picked up item, changes game info, and returns string to display.
	public String processRandomEncounter(RandomEncounter re, Robot robot, StrategyGame game) {
		String display;
		if (re.reType.equals(RandomEncounterType.ATTACK)) {
			robot.currHealth += re.points;
			display = "... vicious wildlife! Health: " + re.points + ".";
		} else if (re.reType.equals(RandomEncounterType.SUPPLY_DROP)) {
			robot.currHealth += re.points;
			if (robot.currHealth > 100) {
				robot.currHealth = 100;
			}
			display = "... a supply drop! Health: +" + re.points + ".";
		} else if (re.reType.equals(RandomEncounterType.COCONUT)) {
			robot.currWeight += re.points;
			display = "... a coconut dropped onto the robot by a swallow! Weight: +" + re.points + ".";
		} else if (re.reType.equals(RandomEncounterType.BUMP)) {
			robot.currWeight += re.points;
			if (robot.currWeight < 0) {
				robot.currWeight = 0;
			}
			display = "... a bump in the path and a sample fell off the robot! Weight: " + re.points + ".";
		} else if (re.reType.equals(RandomEncounterType.MIST)) {
			game.currentPlayer.score += re.points;
			display = "... a mist that formed into points! Points: +" + re.points + ".";
		} else {
			game.currentPlayer.score += re.points;
			display = "... thieves that stole points from the robot! Points: " + re.points + ".";
		}

		showFoundRandomEncounter(robot.currPosition);
		return display;
	}

	// Reflects on tileMap that Item has been used at specific coordinate
	private void showFoundItem(GamePosition pos) {
		Logger.logMsg("exhausting item at location");
		TiledMap map = gameMap.tileMap;
		int x = pos.xPos;
		int y = pos.yPos;
		TiledMapTileLayer itemObj = (TiledMapTileLayer) map.getLayers().get(ITEM_LAYER);
		TiledMapTileLayer.Cell cell = itemObj.getCell(x, y);
		HexTile tile = (HexTile) cell.getTile();

		// Create new texture to show used status.
		Texture itemTexture = new Texture(Gdx.files.internal("sprites/UsedItemPoint.png"));
		TextureRegion[][] itemReg = TextureRegion.split(itemTexture, 40, 40);
		tile.setTextureRegion(new TextureRegion(itemReg[0][0]));

		// Make change to Item to reflect it has been found.
		Item item = tile.getItem();
		item.beenUsed = true;
		tile.setItem(item);
		tile.objectiveType = ObjectiveType.NONE;

		// Set tile back into tileMap layer.
		tile.setOffsetX(44.0f);
		tile.setOffsetY(35.0f);
		cell.setTile(tile);
		itemObj.setCell(x, y, cell);
	}

	// Reflects on tileMap that RandomEncounter has been encountered at specific coordinate
	private void showFoundRandomEncounter(GamePosition pos) {
		Logger.logMsg("exhausting random encounter at location");
		TiledMap map = gameMap.tileMap;
		int x = pos.xPos;
		int y = pos.yPos;
		TiledMapTileLayer itemObj = (TiledMapTileLayer) map.getLayers().get(ITEM_LAYER);
		TiledMapTileLayer.Cell cell = itemObj.getCell(x, y);
		HexTile tile = (HexTile) cell.getTile();

		// Create new texture to show used status.
		Texture reTexture = new Texture(Gdx.files.internal("sprites/FoundREPoint.png"));
		TextureRegion[][] reReg = TextureRegion.split(reTexture, 40, 40);
		tile.setTextureRegion(new TextureRegion(reReg[0][0]));

		// Make change to Item to reflect it has been found.
		RandomEncounter re = tile.getRandomEncounter();
		re.beenFound = true;
		tile.setRandomEncounter(re);
		tile.objectiveType = ObjectiveType.NONE;

		// Set tile back into tileMap layer.
		tile.setOffsetX(44.0f);
		tile.setOffsetY(35.0f);
		cell.setTile(tile);
		itemObj.setCell(x, y, cell);
	}

	private TiledMapTileLayer.Cell getItemCell(int x, int y) {
		TiledMapTileLayer itemObj = (TiledMapTileLayer)gameMap.tileMap.getLayers().get(ITEM_LAYER);
		return itemObj.getCell(x, y);
	}

	private void onFoundPoint(GamePosition pos, String texture) {
		TiledMapTileLayer itemLayer = (TiledMapTileLayer)gameMap.tileMap.getLayers().get(ITEM_LAYER);
		TiledMapTileLayer.Cell cell = getItemCell(pos.xPos, pos.yPos);
		HexTile tile = (HexTile) cell.getTile();
		Texture reTexture = new Texture(Gdx.files.internal(texture));
		TextureRegion[][] reReg = TextureRegion.split(reTexture, 40, 40);
		tile.setTextureRegion(new TextureRegion(reReg[0][0]));
		tile.objectiveType = ObjectiveType.NONE;
		tile.setOffsetX(44.0f);
		tile.setOffsetY(35.0f);
		cell.setTile(tile);
		itemLayer.setCell(pos.xPos, pos.yPos, cell);
	}

	// Reflects on tileMap that midpoint has been encountered.
	private void showFoundStartPoint(GamePosition pos) {
		Logger.logMsg("showing found start point");
		onFoundPoint(pos, "sprites/FoundStartPoint.png");
	}

	// Reflects on tileMap that midpoint has been encountered.
	public void showFoundMidpoint(GamePosition pos) {
		Logger.logMsg("showing found midpoint");
		onFoundPoint(pos, "sprites/FoundMidPoint.png");
	}

	/*
	 * Verifies that the number of tiles the user wants to move the robot doesnt move
	 * it off of the tileMap.
	 */
	public boolean verifyMapMovementDistance(Robot robot, int numTiles, Orientation dir) {
		Logger.logMsg("verifying move distance");
		RobotPosition current = robot.currPosition;
//		int w = mapInfo.getMapWidth();
//		int h = mapInfo.getMapHeight();

		if (current.xPos % 2 == 0) {
			if (dir == Orientation.N) {
				if (current.yPos + numTiles >= gameMap.height) {
					return false;
				}
			} else if (dir == Orientation.S) {
				if (current.yPos - numTiles < 0) {
					return false;
				}
			} else if (dir == Orientation.NE) {
				if ((current.xPos + numTiles >= gameMap.width) || (current.yPos + numTiles >= gameMap.height)) {
					return false;
				}
			} else if (dir == Orientation.SE) {
				if (current.xPos + numTiles >= gameMap.width) {
					return false;
				}
			} else if (dir == Orientation.SW) {
				if (current.xPos - numTiles < 0) {
					return false;
				}
			} else {  // NW orientation
				if ((current.xPos - numTiles < 0) || (current.yPos + numTiles >= gameMap.height)) {
					return false;
				}
			}
		} else {  // odd x-coordinate
			if (dir == Orientation.N) {
				if (current.yPos + numTiles >= gameMap.height) {
					return false;
				}
			} else if (dir == Orientation.S) {
				if (current.yPos - numTiles < 0) {
					return false;
				}
			} else if (dir == Orientation.NE) {
				if (current.xPos + numTiles >= gameMap.width) {
					return false;
				}
			} else if (dir == Orientation.SE) {
				if ((current.xPos + numTiles >= gameMap.width) || (current.yPos - numTiles < 0)) {
					return false;
				}
			} else if (dir == Orientation.SW) {
				if ((current.xPos - numTiles < 0) || (current.yPos - numTiles < 0)) {
					return false;
				}
			} else {  // NW orientation
				if (current.xPos - numTiles < 0) {
					return false;
				}
			}
		}
		return true;
	}

	// Determines the position of the next tile based on orientation of move
	public GamePosition nextPosition(Robot robot, Orientation dir) {
		Logger.logMsg("getting next position");
		int x = robot.currPosition.xPos;
		int y = robot.currPosition.yPos;

		if (x % 2 == 0) {
			if (dir == Orientation.N) {
				y += 1;
			} else if (dir == Orientation.S) {
				y -= 1;
			} else if (dir == Orientation.NE) {
				x += 1;
				y += 1;
			} else if (dir == Orientation.SE) {
				x += 1;
			} else if (dir == Orientation.SW) {
				x -= 1;
			} else {  // NW orientation
				x -= 1;
				y += 1;
			}
		} else {  // odd x-coordinate
			if (dir == Orientation.N) {
				y += 1;
			} else if (dir == Orientation.S) {
				y -= 1;
			} else if (dir == Orientation.NE) {
				x += 1;
			} else if (dir == Orientation.SE) {
				x += 1;
				y -= 1;
			} else if (dir == Orientation.SW) {
				x -= 1;
				y -= 1;
			} else {  // NW orientation
				x -= 1;
			}
		}
		return new GamePosition(x, y);
	}

	// Creates the move queue to break down robot movements into
	// single tile moves.
	public Queue<RobotAction> createMoveQueue(RobotAction action) {
		Logger.logMsg("creating move queue");
		Queue<RobotAction> queue = new LinkedList<RobotAction>();
		int distance = action.distance;
		action.distance = 1;

		if (distance > 0) {
			for (int i = 0; i < distance; i++) {
				queue.add(action);
			}
		}
		return queue;
	}

	private String getRobotOrientationTexture(Orientation o) {
		Logger.logMsg("getting robot orientation texture");
		if (o.equals(Orientation.N)) {
			return "sprites/RobotN.png";
		} else if (o.equals(Orientation.NE)) {
			return "sprites/RobotNE.png";
		} else if (o.equals(Orientation.SE)) {
			return "sprites/RobotSe.png";
		} else if (o.equals(Orientation.S)) {
			return "sprites/RobotS.png";
		} else if (o.equals(Orientation.SW)) {
			return "sprites/RobotSW.png";
		} else {
			return "sprites/RobotNW.png";
		}
	}

	// Draw robot at new location
	public void changeRobotPosition(Robot robot) {
		Logger.logMsg("changing robot position");
		TiledMap map = gameMap.tileMap;
		// Remove robot tile from current position.
		RobotPosition prevRobotMapPos = robot.prevPositions.peek();
		RobotPosition currRobotMapPos = robot.currPosition;
		TiledMapTileLayer robotLayer = (TiledMapTileLayer) map.getLayers().get(ROBOT_LAYER);

		TiledMapTileLayer.Cell cellR = robotLayer.getCell(prevRobotMapPos.xPos, prevRobotMapPos.yPos);
		cellR.setTile(null);
		robotLayer.setCell(prevRobotMapPos.xPos, prevRobotMapPos.yPos, null);

		// Construct new robot tile based on orientation and insert into robot tileMap layer
		// at the new coordinates.
		String textureName = getRobotOrientationTexture(currRobotMapPos.orientation);

		Texture tex = new Texture(Gdx.files.internal(textureName));
		TextureRegion[][] robotReg = TextureRegion.split(tex, 120, 103);
		TiledMapTile rTile = new HexTile(new TextureRegion(robotReg[0][0]));
		rTile.setOffsetX(4.0f);
		rTile.setOffsetY(3.5f);

		TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
		cell.setTile(rTile);
		GamePosition newRobotTilePos = robot.currPosition;
		robotLayer.setCell(newRobotTilePos.xPos, newRobotTilePos.yPos, cell);

		updateFogOfWarLayer(robot);
		if (!hasMoved) {
			hasMoved = true;
			showFoundStartPoint(prevRobotMapPos);
		}
	}

	// Updates Fog of War layer after robot is drawn at new location.
	private void updateFogOfWarLayer(Robot robot) {
		Logger.logMsg("updating fog of war layer");
		TiledMap map = gameMap.tileMap;
		RobotPosition currRobotMapPos = robot.currPosition;

		// Move visible tiles to gray tiles
		for (GamePosition aVisible : gameMap.visibleTiles) {
			gameMap.grayTiles.add(aVisible);
		}

		// Clear out old tiles, and add new visible tiles.
		gameMap.visibleTiles.clear();
		gameMap.visibleTiles = createVisibleTiles(currRobotMapPos, gameMap.width, gameMap.height);

		// Update Fog of War layer
		TiledMapTileLayer fowLayer = (TiledMapTileLayer) map.getLayers().get(FOG_OF_WAR_LAYER);

		// Make entire black tileMap
		makeEntireBlackMap(gameMap.width, gameMap.height, fowLayer);

		// Set gray tiles
		setGrayTiles(gameMap.grayTiles, fowLayer);

		// Remove black tiles so underneath is visible
		removeBlackTiles(gameMap.visibleTiles, fowLayer);
	}

	private void removeBlackTiles(ArrayList<GamePosition> visible, TiledMapTileLayer fowLayer) {
		Logger.logMsg("removing black tiles");
		Cell cell;
		for (GamePosition temp : visible) {
			cell = new Cell();
			cell.setTile(null);
			fowLayer.setCell(temp.xPos, temp.yPos, null);
		}
	}

	// Draw new robot orientation sprite at current location
	public void turnRobot(Robot robot) {
		Logger.logMsg("turning robot");
		TiledMap map = gameMap.tileMap;

		RobotPosition currRobotMapPos = robot.currPosition;
		TiledMapTileLayer robotLayer = (TiledMapTileLayer) map.getLayers().get(ROBOT_LAYER);

		// Set sprite depending on orientation value
		String textureName = getRobotOrientationTexture(currRobotMapPos.orientation);

		Texture tex = new Texture(Gdx.files.internal(textureName));
		TextureRegion[][] robotReg = TextureRegion.split(tex, 120, 103);
		TiledMapTile rTile = new HexTile(new TextureRegion(robotReg[0][0]));
		rTile.setOffsetX(4.0f);
		rTile.setOffsetY(3.5f);

		TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
		cell.setTile(rTile);
		GamePosition newRobotTilePos = robot.currPosition;
		robotLayer.setCell(newRobotTilePos.xPos, newRobotTilePos.yPos, cell);
	}

	/*
	 * Scans a ring around the always visible ring of tiles around the robot.
	 */
	public void scanCircle(Robot robot) {
		Logger.logMsg("performing scan around robot");
		TiledMap map = gameMap.tileMap;
		RobotPosition current = robot.currPosition;
		ArrayList<GamePosition> grayTiles = gameMap.grayTiles;
		ArrayList<GamePosition> visible = gameMap.visibleTiles;

		// Checking common tiles between odd and even x-coord
		if (current.yPos + 2 < gameMap.height) {
			visible.add(new GamePosition(current.xPos, current.yPos + 2));
		}
		if (current.yPos - 2 >= 0) {
			visible.add(new GamePosition(current.xPos, current.yPos - 2));
		}
		if (current.xPos + 2 < gameMap.width) {
			visible.add(new GamePosition(current.xPos + 2, current.yPos));
		}
		if (current.xPos - 2 >= 0) {
			visible.add(new GamePosition(current.xPos - 2, current.yPos));
		}
		if ((current.xPos + 2 < gameMap.width) && (current.yPos + 1 < gameMap.height)) {
			visible.add(new GamePosition(current.xPos + 2, current.yPos + 1));
		}
		if ((current.xPos + 2 < gameMap.width) && (current.yPos - 1 >= 0)) {
			visible.add(new GamePosition(current.xPos + 2, current.yPos - 1));
		}
		if ((current.xPos - 2 >= 0) && (current.yPos - 1 >= 0)) {
			visible.add(new GamePosition(current.xPos - 2, current.yPos - 1));
		}
		if ((current.xPos - 2 >= 0) && (current.yPos + 1 < gameMap.height)) {
			visible.add(new GamePosition(current.xPos - 2, current.yPos + 1));
		}

		// Robot located in even column
		if (current.xPos % 2 == 0) {
			if ((current.xPos + 1 < gameMap.width) && (current.yPos + 2 < gameMap.height)) {
				visible.add(new GamePosition(current.xPos + 1, current.yPos + 2));
			}
			if ((current.xPos + 1 < gameMap.width) && (current.yPos - 1 >= 0)) {
				visible.add(new GamePosition(current.xPos + 1, current.yPos - 1));
			}
			if ((current.xPos - 1 >= 0) && (current.yPos - 1 >= 0)) {
				visible.add(new GamePosition(current.xPos - 1, current.yPos - 1));
			}
			if ((current.xPos - 1 >= 0) && (current.yPos + 2 < gameMap.height)) {
				visible.add(new GamePosition(current.xPos - 1, current.yPos + 2));
			}
		}

		// Robot located in odd column
		if (current.xPos % 2 != 0) {
			if ((current.xPos + 1 < gameMap.width) && (current.yPos + 1 < gameMap.height)) {
				visible.add(new GamePosition(current.xPos + 1, current.yPos + 1));
			}
			if ((current.xPos + 1 < gameMap.width) && (current.yPos - 2 >= 0)) {
				visible.add(new GamePosition(current.xPos + 1, current.yPos - 2));
			}
			if ((current.xPos - 1 >= 0) && (current.yPos - 2 >= 0)) {
				visible.add(new GamePosition(current.xPos - 1, current.yPos - 2));
			}
			if ((current.xPos - 1 >= 0) && (current.yPos + 1 < gameMap.height)) {
				visible.add(new GamePosition(current.xPos - 1, current.yPos + 1));
			}
		}

		gameMap.visibleTiles = visible;

		// Update Fog of War layer
		TiledMapTileLayer fowLayer = (TiledMapTileLayer) map.getLayers().get(3);

		// Make entire black tileMap
		makeEntireBlackMap(gameMap.width, gameMap.height, fowLayer);

		// Set gray tiles, checking to make sure it isnt empty.
		setGrayTiles(grayTiles, fowLayer);

		// Remove black tiles so underneath is visible
		removeBlackTiles(visible, fowLayer);
	}

	private void setGrayTiles(ArrayList<GamePosition> grayTiles, TiledMapTileLayer fowLayer) {
		Logger.logMsg("setting gray tiles");
		Cell cell;
//		if (grayTiles.get(0) != null) {
		if (grayTiles != null) {
			for (GamePosition temp : grayTiles) {
				cell = new Cell();
				cell.setTile(fowMapTiles[1]);
				fowLayer.setCell(temp.xPos, temp.yPos, cell);
			}
		}
	}

	private void makeEntireBlackMap(int w, int h, TiledMapTileLayer fowLayer) {
		Cell cell;
		Logger.logMsg("making entire layer black");
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				cell = new Cell();
				cell.setTile(fowMapTiles[0]);
				fowLayer.setCell(x, y, cell);
			}
		}
	}


	/*
	 * Scans a cone of visible tiles in front of the robot.
	 */
	public void scanCone(Robot robot) {
		Logger.logMsg("scanning in front of robot");
		TiledMap map = gameMap.tileMap;
		RobotPosition current = robot.currPosition;
		Orientation orientation = current.orientation;
		ArrayList<GamePosition> grayTiles = gameMap.grayTiles;
		ArrayList<GamePosition> visible = gameMap.visibleTiles;
		int w = gameMap.width;
		int h = gameMap.height;

		if (orientation.equals(Orientation.NW)) {
			if (current.xPos - 2 >= 0) {
				visible.add(new GamePosition(current.xPos - 2, current.yPos));
			}
			if (current.xPos - 3 >= 0) {
				visible.add(new GamePosition(current.xPos - 3, current.yPos));
			}
			if ((current.xPos - 2 >= 0) && (current.yPos + 1 < h)) {
				visible.add(new GamePosition(current.xPos - 2, current.yPos + 1));
			}
			if ((current.xPos - 3 >= 0) && (current.yPos + 1 < h)) {
				visible.add(new GamePosition(current.xPos - 3, current.yPos + 1));
			}
			if ((current.xPos - 2 >= 0) && (current.yPos + 2 < h)) {
				visible.add(new GamePosition(current.xPos - 2, current.yPos + 2));
			}
			if ((current.xPos - 1 >= 0) && (current.yPos + 2 < h)) {
				visible.add(new GamePosition(current.xPos - 1, current.yPos + 2));
			}
			// Odd column
			if (current.xPos % 2 != 0) {
				if ((current.xPos - 1 >= 0) && (current.yPos + 1 < h)) {
					visible.add(new GamePosition(current.xPos - 1, current.yPos + 1));
				}
				if ((current.xPos - 3 >= 0) && (current.yPos - 1 >= 0)) {
					visible.add(new GamePosition(current.xPos - 3, current.yPos - 1));
				}
			}
			// Even column
			if (current.xPos % 2 == 0) {
				if ((current.xPos - 3 >= 0) && (current.yPos + 2 < h)) {
					visible.add(new GamePosition(current.xPos - 3, current.yPos + 2));
				}
				if ((current.xPos - 1 >= 0) && (current.yPos + 3 < h)) {
					visible.add(new GamePosition(current.xPos - 1, current.yPos + 3));
				}
			}
		}

		if (orientation.equals(Orientation.N)) {
			if (current.yPos + 2 < h) {
				visible.add(new GamePosition(current.xPos, current.yPos + 2));
			}
			if (current.yPos + 3 < h) {
				visible.add(new GamePosition(current.xPos, current.yPos + 3));
			}
			if ((current.xPos - 1 >= 0) && (current.yPos + 2 < h)) {
				visible.add(new GamePosition(current.xPos - 1, current.yPos + 2));
			}
			if ((current.xPos - 2 >= 0) && (current.yPos + 2 < h)) {
				visible.add(new GamePosition(current.xPos - 2, current.yPos + 2));
			}
			if ((current.xPos + 1 < w) && (current.yPos + 2 < h)) {
				visible.add(new GamePosition(current.xPos + 1, current.yPos + 2));
			}
			if ((current.xPos + 2 < w) && (current.yPos + 2 < h)) {
				visible.add(new GamePosition(current.xPos + 2, current.yPos + 2));
			}
			// Odd column
			if (current.xPos % 2 != 0) {
				if ((current.xPos - 1 >= 0) && (current.yPos + 1 < h)) {
					visible.add(new GamePosition(current.xPos - 1, current.yPos + 1));
				}
				if ((current.xPos + 1 < w) && (current.yPos + 1 < h)) {
					visible.add(new GamePosition(current.xPos + 1, current.yPos + 1));
				}
			}
			// Even column
			if (current.xPos % 2 == 0) {
				if ((current.xPos - 1 >= 0) && (current.yPos + 3 < h)) {
					visible.add(new GamePosition(current.xPos - 1, current.yPos + 3));
				}
				if ((current.xPos + 1 < w) && (current.yPos + 3 < h)) {
					visible.add(new GamePosition(current.xPos + 1, current.yPos + 3));
				}
			}
		}

		if (orientation.equals(Orientation.NE)) {
			if (current.xPos + 2 < w) {
				visible.add(new GamePosition(current.xPos + 2, current.yPos));
			}
			if (current.xPos + 3 < w) {
				visible.add(new GamePosition(current.xPos + 3, current.yPos));
			}
			if ((current.xPos + 1 < w) && (current.yPos + 2 < h)) {
				visible.add(new GamePosition(current.xPos + 1, current.yPos + 2));
			}
			if ((current.xPos + 2 < w) && (current.yPos + 2 < h)) {
				visible.add(new GamePosition(current.xPos + 2, current.yPos + 2));
			}
			if ((current.xPos + 2 < w) && (current.yPos + 1 < h)) {
				visible.add(new GamePosition(current.xPos + 2, current.yPos + 1));
			}
			if ((current.xPos + 3 < w) && (current.yPos + 1 < h)) {
				visible.add(new GamePosition(current.xPos + 3, current.yPos + 1));
			}
			// Odd column
			if (current.xPos % 2 != 0) {
				if ((current.xPos + 3 < w) && (current.yPos - 1 >= 0)) {
					visible.add(new GamePosition(current.xPos + 3, current.yPos - 1));
				}
				if ((current.xPos + 1 < w) && (current.yPos + 1 < h)) {
					visible.add(new GamePosition(current.xPos + 1, current.yPos + 1));
				}
			}
			// Even column
			if (current.xPos % 2 == 0) {
				if ((current.xPos + 3 < w) && (current.yPos + 2 < h)) {
					visible.add(new GamePosition(current.xPos + 3, current.yPos + 2));
				}
				if ((current.xPos + 1 < w) && (current.yPos + 3 < h)) {
					visible.add(new GamePosition(current.xPos + 1, current.yPos + 3));
				}
			}
		}

		if (orientation.equals(Orientation.SE)) {
			if (current.xPos + 2 < w) {
				visible.add(new GamePosition(current.xPos + 2, current.yPos));
			}
			if (current.xPos + 3 < w) {
				visible.add(new GamePosition(current.xPos + 3, current.yPos));
			}
			if ((current.xPos + 3 < w) && (current.yPos - 1 >= 0)) {
				visible.add(new GamePosition(current.xPos + 3, current.yPos - 1));
			}
			if ((current.xPos + 2 < w) && (current.yPos - 1 >= 0)) {
				visible.add(new GamePosition(current.xPos + 2, current.yPos - 1));
			}
			if ((current.xPos + 1 < w) && (current.yPos - 2 >= 0)) {
				visible.add(new GamePosition(current.xPos + 1, current.yPos - 2));
			}
			if ((current.xPos + 2 < w) && (current.yPos - 2 >= 0)) {
				visible.add(new GamePosition(current.xPos + 2, current.yPos - 2));
			}
			// Odd column
			if (current.xPos % 2 != 0) {
				if ((current.xPos + 3 < w) && (current.yPos - 2 >= 0)) {
					visible.add(new GamePosition(current.xPos + 3, current.yPos - 2));
				}
				if ((current.xPos + 1 < w) && (current.yPos - 3 >= 0)) {
					visible.add(new GamePosition(current.xPos + 1, current.yPos - 3));
				}
			}
			// Even column
			if (current.xPos % 2 == 0) {
				if ((current.xPos + 3 < w) && (current.yPos + 1 < h)) {
					visible.add(new GamePosition(current.xPos + 3, current.yPos + 1));
				}
				if ((current.xPos + 1 < w) && (current.yPos - 1 >= 0)) {
					visible.add(new GamePosition(current.xPos + 1, current.yPos - 1));
				}
			}
		}

		if (orientation.equals(Orientation.S)) {
			if (current.yPos - 2 >= 0) {
				visible.add(new GamePosition(current.xPos, current.yPos - 2));
			}
			if (current.yPos - 3 >= 0) {
				visible.add(new GamePosition(current.xPos, current.yPos - 3));
			}
			if ((current.xPos + 2 < w) && (current.yPos - 2 >= 0)) {
				visible.add(new GamePosition(current.xPos + 2, current.yPos - 2));
			}
			if ((current.xPos + 1 < w) && (current.yPos - 2 >= 0)) {
				visible.add(new GamePosition(current.xPos + 1, current.yPos - 2));
			}
			if ((current.xPos - 1 >= 0) && (current.yPos - 2 >= 0)) {
				visible.add(new GamePosition(current.xPos - 1, current.yPos - 2));
			}
			if ((current.xPos - 2 >= 0) && (current.yPos - 2 >= 0)) {
				visible.add(new GamePosition(current.xPos - 2, current.yPos - 2));
			}
			// Odd column
			if (current.xPos % 2 != 0) {
				if ((current.xPos - 1 >= 0) && (current.yPos - 3 >= 0)) {
					visible.add(new GamePosition(current.xPos - 1, current.yPos - 3));
				}
				if ((current.xPos + 1 < w) && (current.yPos - 3 >= 0)) {
					visible.add(new GamePosition(current.xPos + 1, current.yPos - 3));
				}
			}
			// Even column
			if (current.xPos % 2 == 0) {
				if ((current.xPos - 1 >= 0) && (current.yPos - 1 >= 0)) {
					visible.add(new GamePosition(current.xPos - 1, current.yPos - 1));
				}
				if ((current.xPos + 1 < w) && (current.yPos - 1 >= 0)) {
					visible.add(new GamePosition(current.xPos + 1, current.yPos - 1));
				}
			}
		}

		if (orientation.equals(Orientation.SW)) {
			if (current.xPos - 2 >= 0) {
				visible.add(new GamePosition(current.xPos - 2, current.yPos));
			}
			if (current.xPos - 3 >= 0) {
				visible.add(new GamePosition(current.xPos - 3, current.yPos));
			}
			if ((current.xPos - 1 >= 0) && (current.yPos - 2 >= 0)) {
				visible.add(new GamePosition(current.xPos - 1, current.yPos - 2));
			}
			if ((current.xPos - 2 >= 0) && (current.yPos - 2 >= 0)) {
				visible.add(new GamePosition(current.xPos - 2, current.yPos - 2));
			}
			if ((current.xPos - 2 >= 0) && (current.yPos - 1 >= 0)) {
				visible.add(new GamePosition(current.xPos - 2, current.yPos - 1));
			}
			if ((current.xPos - 3 >= 0) && (current.yPos - 1 >= 0)) {
				visible.add(new GamePosition(current.xPos - 3, current.yPos - 1));
			}
			// Odd column
			if (current.xPos % 2 != 0) {
				if ((current.xPos - 1 >= 0) && (current.yPos - 3 >= 0)) {
					visible.add(new GamePosition(current.xPos - 1, current.yPos - 3));
				}
				if ((current.xPos - 3 >= 0) && (current.yPos - 2 >= 0)) {
					visible.add(new GamePosition(current.xPos - 3, current.yPos - 2));
				}
			}
			// Even column
			if (current.xPos % 2 == 0) {
				if ((current.xPos - 1 >= 0) && (current.yPos - 1 >= 0)) {
					visible.add(new GamePosition(current.xPos - 1, current.yPos - 1));
				}
				if ((current.xPos - 3 >= 0) && (current.yPos + 1 < h)) {
					visible.add(new GamePosition(current.xPos - 3, current.yPos + 1));
				}
			}
		}

		gameMap.visibleTiles = visible;

		// Update Fog of War layer
		TiledMapTileLayer fowLayer = (TiledMapTileLayer) map.getLayers().get(3);

		// Make entire black tileMap
		makeEntireBlackMap(w, h, fowLayer);

		// Set gray tiles, checking to make sure it isnt empty.
		setGrayTiles(grayTiles, fowLayer);

		// Remove black tiles so underneath is visible
		removeBlackTiles(visible, fowLayer);
	}

	private static class MapManagerHolder {
		private static final MapManager instance = new MapManager();
	}

}
