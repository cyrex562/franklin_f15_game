 package edu.franklin.practicum.f15.strategygame.robot;

import edu.franklin.practicum.f15.strategygame.Logger;
import edu.franklin.practicum.f15.strategygame.Orientation;
import edu.franklin.practicum.f15.strategygame.ScanType;
import edu.franklin.practicum.f15.strategygame.interaction.ActionType;
import edu.franklin.practicum.f15.strategygame.interaction.RobotAction;
import edu.franklin.practicum.f15.strategygame.map.HexTile;
import edu.franklin.practicum.f15.strategygame.map.MapManager;
import edu.franklin.practicum.f15.strategygame.map.TerrainTile;
import edu.franklin.practicum.f15.strategygame.map.TerrainType;

import javax.swing.*;

import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.ScheduledExecutorService;

public class Robot{
    public RobotPosition currPosition;
    public RobotPosition startPoint;
    public RobotPosition midPoint;
    public RobotPosition endPoint;
    public final Stack<RobotPosition> prevPositions;
    ScheduledExecutorService service;
    HexTile curTile;
    TerrainTile tile;
    private Orientation dir;
    
    Timer timer;
	public int moveCount;
	public int currWeight;
	public int currHealth;
	private long speed;
	private String newOrder;
	private ArrayList<String> orderList;
    public ArrayList<String> locationList;



	public Robot() {
        currPosition = new RobotPosition();
        prevPositions = new Stack<RobotPosition>();
		moveCount = 0;
		currWeight = 0;
		currHealth = 100;
    }

    public void ProcessAction(RobotAction action) {
        int dist = action.distance;
        dir = action.direction;
		if (action.actionType == ActionType.MOVE) {
	        moveCount += dist;
            
            prevPositions.push(new RobotPosition(currPosition.xPos, currPosition.yPos, currPosition.orientation));
            //setSpeed(tile.terrainType);
            //timer.wait(3000 / getSpeed());
            //orderList.add(action.getAction().toString());
            if (dist > 0) {
            	if(currPosition.xPos % 2 == 0) {
            		if (dir == Orientation.N) {
            			currPosition.yPos += dist;
            			currPosition.orientation = dir;
        	        } else if (dir == Orientation.S) {
        	        	currPosition.yPos -= dist;
        	        	currPosition.orientation = dir;
        	        } else if (dir == Orientation.NE) {
        	        	currPosition.xPos += dist;
                		currPosition.yPos += dist;
                		currPosition.orientation = dir;
                	} else if (dir == Orientation.SE) {
                		currPosition.xPos += dist;
                		currPosition.orientation = dir;
                	} else if (dir == Orientation.SW) {
                   		currPosition.xPos -= dist;
                   		currPosition.orientation = dir;
                	} else {  // NW orientation
		                currPosition.xPos -= dist;
                		currPosition.yPos += dist;
                		currPosition.orientation = dir;
	                }
            	} else {  // odd x-coordinate
                	if (dir == Orientation.N) {
                		currPosition.yPos += dist;
                		currPosition.orientation = dir;
        	        } else if (dir == Orientation.S) {
        	        	currPosition.yPos -= dist;
        	        	currPosition.orientation = dir;
        	        } else if (dir == Orientation.NE) {
        	        	currPosition.xPos += dist;
        	        	currPosition.orientation = dir;
                	} else if (dir == Orientation.SE) {
                		currPosition.xPos += dist;
                		currPosition.yPos -= dist;
                		currPosition.orientation = dir;
                	} else if (dir == Orientation.SW) {
                   		currPosition.xPos -= dist;
		                currPosition.yPos -= dist;
		                currPosition.orientation = dir;
                	} else {  // NW orientation
		                currPosition.xPos -= dist;
		                currPosition.orientation = dir;
	                }
            	}
            } else {
                if (dir != currPosition.orientation) {
                    currPosition.orientation = dir;
                }
            }

        } else if (action.actionType == ActionType.SCAN) {
	        MapManager mapManager = MapManager.getInstance();
	        if(action.scanType == ScanType.CIRCLE)
	        {
		        mapManager.scanCircle(this);
	        }
	        else if (action.scanType == ScanType.CONE) {
	        	mapManager.scanCone(this);
	        }
    
        } else if (action.actionType == ActionType.TURN) {
            if (dir != currPosition.orientation) {
                currPosition.orientation = dir;
            }
        } else {
            Logger.logMsg(String.format("unsupported action type: %s", action.toString()));
        }
    }
    
    public void setSpeed(long setSpeed) {
        speed = setSpeed;
     }

    public long getSpeed() {
        return this.speed;
    }
    
    private Stack<RobotPosition> getPreviousPositions()
    {
        return this.prevPositions;
    }
    
    public String getOrder() {
        return this.newOrder;
    }

    public void setOrder(RobotAction roboAction) {
		if (roboAction.actionType != null && roboAction.distance > 0 &&
                roboAction.direction != null) {
            newOrder = String.format("%s %d %s", roboAction.actionType.toString(), roboAction.distance,
                    roboAction.direction.toString());
        } else {
            newOrder = "No current order";
        }
    }
    
    public String getCurrentState() {
	    return "Current Location: " + this.currPosition + "Current Direction: " +
			    this.dir + "Speed: " + this.speed + "Previous Orders: " +
			    orderList + "Previous Locations: " + getPreviousPositions();
    }
}



