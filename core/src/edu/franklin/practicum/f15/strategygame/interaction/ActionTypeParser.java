package edu.franklin.practicum.f15.strategygame.interaction;

import edu.franklin.practicum.f15.strategygame.Logger;

class ActionTypeParser {
	public static ActionType parse(String act)
	{
		if (act.equalsIgnoreCase("m") || act.equalsIgnoreCase(ActionType.MOVE.toString()))
		{
			return ActionType.MOVE;
		}
		else if (act.equalsIgnoreCase("s") || act.equalsIgnoreCase(ActionType.SCAN.toString()))
		{
			return ActionType.SCAN;
		}
		else if (act.equalsIgnoreCase("p") || act.equalsIgnoreCase("pickupitem") ||
				act.equalsIgnoreCase("pick up item") || act.equalsIgnoreCase("pick up") || 
				act.equalsIgnoreCase("pick"))
		{
			return ActionType.PICK_UP_ITEM;
		}
		else if (act.equalsIgnoreCase("remove") || act.equalsIgnoreCase("removeV") ||
				act.equalsIgnoreCase("removevegitation") || act.equalsIgnoreCase("rveg") ||
				act.equalsIgnoreCase("removeveg") || act.equalsIgnoreCase("remove") ||
				act.equalsIgnoreCase("rvegitation") || act.equalsIgnoreCase("rv") || 
				act.equalsIgnoreCase("r"))
		{
			return ActionType.REMOVE_VEGITATION;
		} 
		else if (act.equalsIgnoreCase("turn") || act.equalsIgnoreCase("t") 
				|| act.equalsIgnoreCase("tn") ||act.equalsIgnoreCase("trn"))
		{
			return ActionType.TURN;			
		}
		else {
			Logger.logMsg(String.format("invalid action type: \"%s\"", act));
			return ActionType.INVALID;
		}
	}
	
}
