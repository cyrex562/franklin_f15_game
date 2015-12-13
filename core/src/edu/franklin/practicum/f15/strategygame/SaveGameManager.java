package edu.franklin.practicum.f15.strategygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import edu.franklin.practicum.f15.strategygame.map.*;
import edu.franklin.practicum.f15.strategygame.robot.Robot;

import java.util.ArrayList;
import java.util.List;


public class SaveGameManager {

	private SaveGameManager() {
	}

	private static class SaveGameManagerHolder {
		private static final SaveGameManager instance = new SaveGameManager();
	}

	public static SaveGameManager getInstance() {
		return SaveGameManagerHolder.instance;
	}

	public List<SaveGame> LoadSaveGames() {
		ArrayList<SaveGame> saveGames = new ArrayList<SaveGame>();
		FileHandle[] saveGameFiles = Gdx.files.local("saves/").list();
		for (FileHandle saveGameFile : saveGameFiles) {
			String saveGameJSON = saveGameFile.readString();
			SaveGame saveGame = jsonToSaveGame(saveGameJSON);
			saveGames.add(saveGame);
		}
		return saveGames;
	}

	private String saveGameToJSON(SaveGame saveGame) {
		return JsonWriter.objectToJson(saveGame);
	}

	private SaveGame jsonToSaveGame(String saveGameJSON) {
		return (SaveGame) JsonReader.jsonToJava(saveGameJSON);
	}

	private SaveableGameMap makeSaveableMap(GameMap gameMap) {
		SaveableGameMap sgm = new SaveableGameMap();
		sgm.mapHeight = gameMap.height;
		sgm.mapWidth = gameMap.width;

		for (int x = 0; x < sgm.mapWidth; x++) {
			for (int y = 0; y < sgm.mapHeight; y++) {
				// terrain layer
				TiledMapTileLayer.Cell cell = gameMap.terrainLayer.getCell(x, y);
				if (cell != null) {
					HexTile ht = (HexTile) cell.getTile();
					TerrainTile tt = new TerrainTile();
					tt.xPos = x;
					tt.yPos = y;
					TerrainType terrainType = ht.getTerrainType();
					if (terrainType == TerrainType.NONE) {
						Logger.logMsg("invalid terrain type detected");
						terrainType = TerrainType.SAND;
					}
					tt.terrainType = terrainType;
					tt.xOffset = ht.getOffsetX();
					tt.yOffset = ht.getOffsetY();
					sgm.terrainTiles.add(tt);
				}

				// item layer
				ObjectiveTile it = new ObjectiveTile();
				cell = gameMap.itemObjectiveLayer.getCell(x, y);
				if (cell != null) {
					HexTile ht = (HexTile) cell.getTile();
					it.xPos = x;
					it.yPos = y;
					it.objectiveType = ht.objectiveType;
					it.item = ht.getItem();
					it.xOffset = ht.getOffsetX();
					it.yOffset = ht.getOffsetY();
					sgm.itemTiles.add(it);
				}

				// fow layer
				FOWTile ft = new FOWTile();
				cell = gameMap.fogOfWarLayer.getCell(x, y);
				if (cell != null) {
					HexTile ht = (HexTile) cell.getTile();
					ft.xPos = x;
					ft.yPos = y;
					ft.fowType = ht.fowType;
					ft.xOffset = ht.getOffsetX();
					ft.yOffset = ht.getOffsetY();
					sgm.fowTiles.add(ft);
				}
			}
		}

		return sgm;
	}

	private TiledMapTileLayer convertSaveableTerrainLayer(SaveableGameMap sgm) {
		TiledMapTileLayer terrainLayer
				= new TiledMapTileLayer(sgm.mapWidth, sgm.mapHeight, Defines.TILE_WIDTH, Defines.TILE_HEIGHT);
		for (TerrainTile tt : sgm.terrainTiles) {

			String texName;
			boolean passable;
			double movePenalty;
			if (tt.terrainType == TerrainType.BRUSH) {
				texName = "terrain/GrassQuick.png";
				movePenalty = 0.5;
				passable = true;
			} else if (tt.terrainType == TerrainType.GRASS) {
				texName = "terrain/Grass.png";
				movePenalty = 0.0;
				passable = true;
			} else if (tt.terrainType == TerrainType.ROCK) {
				texName = "terrain/Rock.png";
				movePenalty = 1.0;
				passable = false;
			} else if (tt.terrainType == TerrainType.SAND) {
				texName = "terrain/SandQuick.png";
				movePenalty = 0.25;
				passable = true;
			} else if (tt.terrainType == TerrainType.SWAMP) {
				texName = "terrain/Swamp.png";
				movePenalty = 0.75;
				passable = true;
			} else if (tt.terrainType == TerrainType.TREE) {
				texName = "terrain/Forest.png";
				movePenalty = 1.0;
				passable = false;
			} else if (tt.terrainType == TerrainType.WATER) {
				texName = "terrain/WaterQuick2.png";
				movePenalty = 1.0;
				passable = false;
			} else {
				Logger.logMsg("invalid terrain type detected");
				texName = "terrain/SandQuick.png";
				movePenalty = 0.0;
				passable = true;
				tt.terrainType = TerrainType.SAND;
			}

			Texture tex = new Texture(Gdx.files.internal(texName));
			TextureRegion[][] tr = TextureRegion.split(tex, Defines.TILE_WIDTH, Defines.TILE_HEIGHT);
			HexTile ht = new HexTile(tr[0][0]);
			ht.setTerrainType(tt.terrainType);
			ht.setIsPassable(passable);
			ht.setMovePenalty(movePenalty);
			ht.setOffsetX(tt.xOffset);
			ht.setOffsetY(tt.yOffset);
			TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
			cell.setTile(ht);
			terrainLayer.setCell(tt.xPos, tt.yPos, cell);
		}

		terrainLayer.setName("Terrain");
		return terrainLayer;
	}

	private TiledMapTileLayer convertSaveableItemLayer(SaveableGameMap sgm) {
		TiledMapTileLayer itemLayer
				= new TiledMapTileLayer(sgm.mapWidth, sgm.mapHeight, Defines.TILE_WIDTH, Defines.TILE_HEIGHT);
		for (ObjectiveTile it : sgm.itemTiles) {
			String texName;
			if (it.objectiveType == ObjectiveType.START_POINT) {
				texName = "sprites/StartPoint.png";
			} else if (it.objectiveType == ObjectiveType.MID_POINT) {
				texName = "sprites/MidPoint.png";
			} else if (it.objectiveType == ObjectiveType.END_POINT) {
				texName = "sprites/EndPoint.png";
			} else if (it.objectiveType == ObjectiveType.ITEM) {
				texName = "sprites/ItemPoint.png";
			} else if (it.objectiveType == ObjectiveType.RANDOM_ENCOUNTER_POINT) {
				texName = "sprites/REPoint.png";
			} else {
				Logger.logMsg("invalid item type detected");
				continue;
			}

			Texture tex = new Texture(Gdx.files.internal(texName));
			TextureRegion[][] tr = TextureRegion.split(tex, 40, 40);
			HexTile ht = new HexTile(tr[0][0]);

			if (it.objectiveType == ObjectiveType.ITEM) {
				ht.objectiveType = it.objectiveType;
			} else if (it.objectiveType == ObjectiveType.RANDOM_ENCOUNTER_POINT) {
				ht.setRandomEncounter(it.randomEncounter);
			}

			ht.setItem(it.item);
			ht.setOffsetY(it.yOffset);
			ht.setOffsetX(it.xOffset);
			TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
			cell.setTile(ht);
			itemLayer.setCell(it.xPos, it.yPos, cell);
		}
		return itemLayer;
	}

	private TiledMapTileLayer convertSaveableFowLayer(SaveableGameMap sgm) {
		TiledMapTileLayer fowLayer
				= new TiledMapTileLayer(sgm.mapWidth, sgm.mapHeight, Defines.TILE_WIDTH, Defines.TILE_HEIGHT);
		for (FOWTile ft : sgm.fowTiles) {
			String texName = "";
			if (ft.fowType == FOWType.FULL) {
				texName = "terrain/FogOfWarHidden.png";
			} else if (ft.fowType == FOWType.PARTIAL) {
				texName = "terrain/FogOfWarStale.png";
			}
			Texture tex = new Texture(Gdx.files.internal(texName));
			TextureRegion[][] tr = TextureRegion.split(tex, Defines.TILE_WIDTH, Defines.TILE_HEIGHT);
			HexTile ht = new HexTile(tr[0][0]);
			ht.fowType = ft.fowType;
			ht.setOffsetX(ft.xOffset);
			ht.setOffsetY(ft.yOffset);
			TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
			cell.setTile(ht);
			fowLayer.setCell(ft.xPos, ft.yPos, cell);
		}
		return fowLayer;
	}

	private TiledMapTileLayer convertSaveableRobotLayer(SaveableGameMap sgm, Robot robot) {
		MapManager mapManager = MapManager.getInstance();
		return mapManager.createRobotLayer(robot.currPosition, sgm.mapWidth, sgm.mapHeight);
	}

	private GameMap convertSaveableMap(SaveableGameMap sgm, Robot robot) {
		MapManager mapManager = MapManager.getInstance();
		GameMap gameMap = new GameMap();
		// layers
		gameMap.terrainLayer = convertSaveableTerrainLayer(sgm);
		gameMap.itemObjectiveLayer = convertSaveableItemLayer(sgm);
		gameMap.fogOfWarLayer = convertSaveableFowLayer(sgm);
		gameMap.robotLayer = convertSaveableRobotLayer(sgm, robot);

		// tileMap
		gameMap.tileMap = new TiledMap();
		MapLayers layers = gameMap.tileMap.getLayers();
		layers.add(gameMap.terrainLayer);
		layers.add(gameMap.robotLayer);
		layers.add(gameMap.itemObjectiveLayer);
		layers.add(gameMap.fogOfWarLayer);

		// tileMap info
		mapManager.initFOWMapTiles();
		gameMap.height = sgm.mapHeight;
		gameMap.width = sgm.mapWidth;

		ArrayList<Item> items = new ArrayList<Item>();
		ArrayList<RandomEncounter> randomEncounters = new ArrayList<RandomEncounter>();
		for (ObjectiveTile it : sgm.itemTiles) {
			if (it.item != null) {
				items.add(it.item);
			} else if (it.randomEncounter != null) {
				randomEncounters.add(it.randomEncounter);
			}

		}

		Item[] itemArray = new Item[items.size()];
		for (int i = 0; i < items.size(); i++) {
			itemArray[i] = items.get(i);
		}

		RandomEncounter[] reArray = new RandomEncounter[randomEncounters.size()];
		for (int i = 0; i < randomEncounters.size(); i++) {
			reArray[i] = randomEncounters.get(i);
		}

		gameMap.items = itemArray;
		gameMap.randomEncounters = reArray;
		gameMap.visibleTiles = mapManager.createVisibleTiles(robot.startPoint, sgm.mapWidth, sgm.mapHeight);

		ArrayList<GamePosition> grayTiles = new ArrayList<GamePosition>();
		for (FOWTile f : sgm.fowTiles) {
			if (f.fowType == FOWType.PARTIAL) {
				GamePosition g = new GamePosition();
				g.xPos = f.xPos;
				g.yPos = f.yPos;
			}
		}

		gameMap.grayTiles = grayTiles;

		return gameMap;
	}

	public void saveGame(StrategyGame game, String saveGameName) {
		SaveGame saveGame = new SaveGame();
		saveGame.map = makeSaveableMap(MapManager.gameMap);
		saveGame.robot = game.currentRobot;
		saveGame.player = game.currentPlayer;
		saveGame.name = saveGameName;

		String saveGameJSON = saveGameToJSON(saveGame);

		String saveGameNameNorm = saveGame.name.replaceAll("[^\\dA-Za-z ]", "_");
		FileHandle file = Gdx.files.local(String.format("saves/%s.json", saveGameNameNorm));
		file.writeString(saveGameJSON, false);
	}

	public void loadGame(StrategyGame game, SaveGame saveGame) {
		GameMap gameMap = convertSaveableMap(saveGame.map, saveGame.robot);
		game.currentRobot = saveGame.robot;
		game.currentPlayer = saveGame.player;
		MapManager.gameMap = gameMap;
	}
}
