package edu.franklin.practicum.f15.strategygame.interaction;

import edu.franklin.practicum.f15.strategygame.Orientation;

class DirectionParser {
	public static Orientation parse(String dir)
	{
		String nw = "NW";
		String sw = "SW";
		String s = "S";
		String se = "SE";
		String ne = "NE";
		String n = "N";
		if (dir.equalsIgnoreCase(n) || dir.equalsIgnoreCase("North"))
		{
			return Orientation.N;
		}
		else if (dir.equalsIgnoreCase(s) || dir.equalsIgnoreCase("south"))
		{
			return Orientation.S;
		}
		else if (dir.equalsIgnoreCase(ne) || dir.equalsIgnoreCase("NorthEast") ||
				dir.equalsIgnoreCase("NEast") || dir.equalsIgnoreCase("NorthE"))
		{
			return Orientation.NE;
		}
		else if (dir.equalsIgnoreCase(se) || dir.equalsIgnoreCase("southEast") ||
				dir.equalsIgnoreCase("sEast") || dir.equalsIgnoreCase("SouthE"))
		{
			return Orientation.SE;
		}
		else if (dir.equalsIgnoreCase(sw) || dir.equalsIgnoreCase("southwEst") ||
				dir.equalsIgnoreCase("swest") || dir.equalsIgnoreCase("Southw"))
		{
			return Orientation.SW;
		}
		else if (dir.equalsIgnoreCase(nw) || dir.equalsIgnoreCase("northwEst") ||
				dir.equalsIgnoreCase("nwest") || dir.equalsIgnoreCase("northw"))
		{
			return Orientation.NW;
		}
		return null;
	}
	
}
