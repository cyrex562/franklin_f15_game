package edu.franklin.practicum.f15.strategygame.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;

/**
 * Created by Kent N. 10/10/15.
 */
public class HexTile implements TiledMapTile {

	// Instance variables for the implemented interface methods
    private int id;
	private BlendMode blendMode = BlendMode.ALPHA;
	private MapProperties properties;
	private TextureRegion textureRegion;
	private float offsetX;
	private float offsetY;
	
	// Additional instance variables
	private boolean isPassable;
	private Item item;
	private RandomEncounter re;
	private TerrainType terrain;
	// 0.0 = no penalty, 0.50 = 50% penalty, 1.00 = impassable
	private double movePenalty;
	public FOWType fowType;
	public ObjectiveType objectiveType;

	public HexTile(TextureRegion textureRegion) {
		this.textureRegion = textureRegion;
		id = -1;
		properties = new MapProperties();
		offsetX = 0;
		offsetY = 0;
		isPassable = true;
		item = null;
		re = null;
		movePenalty = 0;
		fowType = FOWType.NONE;
		objectiveType = ObjectiveType.NONE;
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BlendMode getBlendMode() {
		return blendMode;
	}

	public void setBlendMode(BlendMode blendMode) {
		this.blendMode = blendMode;
	}

	public TextureRegion getTextureRegion() {
		return textureRegion;
	}
	
	public void setTextureRegion(TextureRegion textureRegion) {
		this.textureRegion = textureRegion;
	}
	
	public float getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(float offsetX) {
		this.offsetX = offsetX;
	}

	public float getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(float offsetY) {
		this.offsetY = offsetY;
	}
	
	public MapProperties getProperties() {
		if (properties == null) {
			properties = new MapProperties();
		}
		return properties;
	}
	
	// HexTile specific methods.
	
	public boolean getIsPassable() {
		return isPassable;
	}
	
	public void setIsPassable(boolean isPassable) {
		this.isPassable = isPassable;
	}

	public Item getItem() {
		return item;
	}
	
	public void setItem(Item item) {
		this.item = item;
	}

	public RandomEncounter getRandomEncounter() {
		return re;
	}
	
	public void setRandomEncounter(RandomEncounter re) {
		this.re = re;
	}

	public void setTerrainType(TerrainType type) {
		terrain = type;
	}
	
	public TerrainType getTerrainType() {
		return terrain;
	}

	public void setMovePenalty(double mp) {
		this.movePenalty = mp;
	}

	public double getMovePenalty() {
		return movePenalty;
	}
}
