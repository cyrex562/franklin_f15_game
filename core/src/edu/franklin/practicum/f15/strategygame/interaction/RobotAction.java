package edu.franklin.practicum.f15.strategygame.interaction;

import edu.franklin.practicum.f15.strategygame.Orientation;
import edu.franklin.practicum.f15.strategygame.ScanType;

public class RobotAction {
	public final ActionType actionType;
	public int distance;
	public final Orientation direction;
	public final ScanType scanType;

	public RobotAction(ActionType action, int distance, Orientation direction, ScanType scanType) {
		this.actionType = action;
		this.direction = direction;
		this.distance = distance;
		this.scanType = scanType;
	}


	@Override
	public String toString() {
		return String.format("%s %s %d", actionType.toString(), direction.toString(), distance);
	}
}
