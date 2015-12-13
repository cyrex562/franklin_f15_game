package edu.franklin.practicum.f15.strategygame.interaction;
/**
 * Written By: Raymond Kershaw
 * Written For: Franklin University Comp294 
 * Version 1.0
 * ActionQueue - a queue keeper made of 2 LinkedLists, 1 text based and 1 action based
 * Actioneer of queue, and acts as an adapter from user to input format validators to robot 
 * In places where //placeHolderRemove is found, these portions of code can be removed in order
 * to increase efficicency.
 */

import edu.franklin.practicum.f15.strategygame.Logger;

import java.util.LinkedList;

public class ActionQueue {

	private final LinkedList<String> actionList;
	private final LinkedList<RobotAction> rActionList;

	/**
	 * Default constructor builds an action queue with a selected index
	 */
	public ActionQueue() {
		this.actionList = new LinkedList<String>();
		this.rActionList = new LinkedList<RobotAction>();
	}
	/**
	 * Sends data to get reformatted, adds String to String queue and roboAction to action queue
	 * @param actionString The String (action) to be added to the queue
	 */
	public void addAction(String actionString)
	{
		RobotAction ra = ActionParser.parse(actionString);
		if (ra == null) {
			Logger.logMsg(String.format("invalid action string: %s", actionString));
		} else {
			this.rActionList.add(ra);
			this.actionList.add(ra.toString());
		}
	}
	/**
	 * Removes the action at the last selected index
	 * @param index index of the selected action to remove
	 * (passed by list from gameplay screen)
	 */
	public void removeAction(int index)
	{
		if (index != -1)
		{
			this.actionList.remove(index);
			this.rActionList.remove(index);	
		}
	}
	/**
	 * Moves the selected item up in the queue
	 * @param sIndex The index of the item passed from the list on gameScreen
	 * 		 (selectedIndex)
	 */
	public void moveUp(int sIndex)
	{
		String tempString;
		RobotAction tempAction;
		
		if (sIndex > 0)
		{
			tempString = this.actionList.get(sIndex - 1);
			tempAction = this.rActionList.get(sIndex - 1);
			actionList.set(sIndex - 1, actionList.get(sIndex));
			rActionList.set(sIndex -1, rActionList.get(sIndex));
			actionList.set(sIndex, tempString);
			rActionList.set(sIndex, tempAction);
		}
	}
	/**
	 * Moves the selected item down in the queue
	 * @param sIndex The index of the item passed from the list on gameScreen
	 * 		 (selectedIndex)
	 */
	public void moveDown(int sIndex)
	{
		String tempString;
		RobotAction tempAction;
		//only doesSomething if index >= 0 (unselected = -1) and < size
		if (sIndex < this.actionList.size()-1 && sIndex >= 0) { 
			tempString = this.actionList.get(sIndex + 1);
			tempAction = this.rActionList.get(sIndex + 1);
			actionList.set(sIndex + 1, actionList.get(sIndex));
			rActionList.set(sIndex + 1, rActionList.get(sIndex));
			actionList.set(sIndex, tempString);
			rActionList.set(sIndex, tempAction);
		}
	}
	/**
	 * Gets the next robot action from the list
	 * @return the next robot action in the queue
	 */
	public RobotAction getNextAction()
	{
		this.actionList.pop();
		return rActionList.pop();
	}
//	public void setAction(int index, String actionString)
//	{
//		aParse = new ActionParser();
//		if(!(aParse.parse(actionString)))
//		{
//			return;
//		}
//		this.rActionList.set(index, aParse.getRobotAction());
//		this.actionList.set(index, aParse.getRobotAction().toString());
//	}
	/** 
	 * Retrieves this entire list as String
	 * @return The entire list of actions in the queue
	 */
//	public LinkedList<String> getStringList()
//	{
//		return this.actionList;
//	}
	/** 
	 * Retrieves this entire list as robotAction
	 * @return The entire list of actions in the queue
	 */
//	public LinkedList<RobotAction> getRobotActionList()
//	{
//		return this.rActionList;
//	}
	/**
	 *  Retrieves the refreshed list as an array
	 *  
	 * @return array of the next placeHolderNum actions
	 */
	public String[] getStringActionArray()
	{
		String[] actionsArray;
			int i = 0;
			actionsArray = new String[actionList.size()];
		 for (String items : this.actionList)
			{
			 	actionsArray[i] = items;
				i++;
			}
		return actionsArray;
	}
	/**
	 * Gets the size of the actionQueue
	 * @return the number of actions in the robot action queue
	 */
//	public int getSize()
//	{
//		return this.rActionList.size();
//	}

}
