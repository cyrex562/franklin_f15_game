/**
 *
 */
package edu.franklin.practicum.f15.strategygame.interaction;

import edu.franklin.practicum.f15.strategygame.Logger;
import edu.franklin.practicum.f15.strategygame.Orientation;
import edu.franklin.practicum.f15.strategygame.ScanType;

class ActionParser {
	private static boolean isActionType(String s) {
		String sLower = s.toLowerCase();
		return sLower.equals(ActionType.SCAN.toString().toLowerCase()) ||
				sLower.equals(ActionType.MOVE.toString().toLowerCase()) ||
				sLower.equals(ActionType.PICK_UP_ITEM.toString().toLowerCase()) ||
				sLower.equals(ActionType.TURN.toString().toLowerCase());
	}

	private static boolean isDirection(String s) {
		String sLower = s.toLowerCase();
		return sLower.equals("n") || sLower.equals("north") || sLower.equals("s") || sLower.equals("south") ||
				sLower.equals("se") || sLower.equals("southeast") || sLower.equals("sw") ||
				sLower.equals("southwest") || sLower.equals("ne") || sLower.equals("northeast") ||
				sLower.equals("nw") || sLower.equals("northwest");
	}

	private static boolean isScanType(String s) {
		String sLower = s.toLowerCase();
		return (sLower.equals("cone") || sLower.equals("circle"));
	}

	private static boolean isDistance(String s) {
		try {
			//noinspection ResultOfMethodCallIgnored
			Integer.parseInt(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static RobotAction parse(String inAction) {
		String[] splitAction = inAction.split(" ");
		int len = splitAction.length;
		Orientation direction = Orientation.N;
		int distance = -1;
		ActionType actionType;
		ScanType scanType = ScanType.INVALID;
		if (len == 0) {
			Logger.logMsg("action statement after split was length 0");
			return null;
			// statements of length 1 should be actions like picking up items, clearing vegetation, etc.
		} else if (len == 1) {
			actionType = ActionTypeParser.parse(splitAction[0]);
			if (!(actionType == ActionType.PICK_UP_ITEM || actionType == ActionType.SCAN ||
					actionType == ActionType.REMOVE_VEGITATION)) {
				Logger.logMsg(String.format("invalid action type: %s", actionType.toString()));
				return null;
			}
			// statements of length 2 should only be turns, and some scans
		} else if (len == 2) {
			String part1 = splitAction[0];
			String part2 = splitAction[1];

			if (isActionType(part2) && (isDirection(part1) || isScanType(part1))) {
				actionType = ActionTypeParser.parse(part2);
				if (isDirection(part1)) {
					direction = DirectionParser.parse(part1);
				} else {
					scanType = ScanParser.parse(part1);
				}

			} else if (isActionType(part1) && (isDirection(part2) || isScanType(part2))) {
				actionType = ActionTypeParser.parse(part1);
				if (isDirection(part2)) {
					direction = DirectionParser.parse(part2);
				} else {
					scanType = ScanParser.parse(part2);
				}
			} else {
				Logger.logMsg(String.format("invalid action statement: \"%s\"", inAction));
				return null;
			}

			if (actionType != ActionType.TURN && actionType != ActionType.SCAN) {
				Logger.logMsg(String.format("invalid action statement: \"%s\"", inAction));
				return null;
			}
			// statements of length 3 only includes movement at the moment
		} else if (len == 3) {
			String part1 = splitAction[0];
			String part2 = splitAction[1];
			String part3 = splitAction[2];
			if (isDirection(part1) && isActionType(part2) && isDistance(part3)) {
				direction = DirectionParser.parse(part1);
				actionType = ActionTypeParser.parse(part2);
				distance = DistanceParser.parse(part3);
			} else if (isDirection(part1) && isActionType(part3) && isDistance(part2)) {
				direction = DirectionParser.parse(part1);
				actionType = ActionTypeParser.parse(part3);
				distance = DistanceParser.parse(part2);
			} else if (isDirection(part3) && isActionType(part1) && isDistance(part2)) {
				direction = DirectionParser.parse(part3);
				actionType = ActionTypeParser.parse(part1);
				distance = DistanceParser.parse(part2);
			} else if (isDirection(part3) && isActionType(part2) && isDistance(part1)) {
				direction = DirectionParser.parse(part3);
				actionType = ActionTypeParser.parse(part2);
				distance = DistanceParser.parse(part1);
			} else if (isDirection(part2) && isActionType(part3) && isDistance(part1)) {
				direction = DirectionParser.parse(part2);
				actionType = ActionTypeParser.parse(part3);
				distance = DistanceParser.parse(part1);
			} else if (isDirection(part2) && isActionType(part1) && isDistance(part3)) {
				direction = DirectionParser.parse(part2);
				actionType = ActionTypeParser.parse(part1);
				distance = DistanceParser.parse(part3);
			} else {
				Logger.logMsg(String.format("invalid action statement: \"%s\"", inAction));
				return null;
			}

			if (actionType != ActionType.MOVE) {
				Logger.logMsg(String.format("invalid action statement: \"%s\"", inAction));
				return null;
			}

			if (distance <= 0) {
				Logger.logMsg(String.format("invalid action statement: \"%s\"", inAction));
				return null;
			}
		} else if (len > 3) {
			Logger.logMsg(String.format("invalid action statement: \"%s\"", inAction));
			return null;
		} else {
			Logger.logMsg(String.format("invalid action statement: \"%s\"", inAction));
			return null;
		}

		return new RobotAction(actionType, distance, direction, scanType);
	}
}
