package edu.franklin.practicum.f15.strategygame.interaction;

class DistanceParser {
	/**
	 * Parses and normalizes a Distance from a passed string 
	 * if not a numerical value passed, distance set to -1 which nullifies 
	 * the action
	 * @param dis the passed string holding a Distance value
	 * @return a Distance
	 */
	public static int parse(String dis)
	{
		try
		{
			//parses a double for safeties sake, then rounds the double
			//casting it back to an int 
			return (int) Math.round(Double.parseDouble(dis));
		}
		catch (Exception e)
		{
			return -1;
		}
	}
	
}
