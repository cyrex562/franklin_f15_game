package edu.franklin.practicum.f15.strategygame.interaction;

import edu.franklin.practicum.f15.strategygame.Logger;
import edu.franklin.practicum.f15.strategygame.ScanType;

class ScanParser {
	public static ScanType parse(String sType)
	{
		if(sType.equalsIgnoreCase("CIRCLE"))
		{
			return ScanType.CIRCLE;
		}
		else if(sType.equalsIgnoreCase("CONE"))
		{
			return ScanType.CONE;
		} else {
			Logger.logMsg(String.format("invalid scan type: %s", sType));
			return ScanType.INVALID;
		}
	}
}
