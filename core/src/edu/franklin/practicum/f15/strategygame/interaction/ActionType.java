package edu.franklin.practicum.f15.strategygame.interaction;

public enum ActionType {
	MOVE("MOVE"),
	SCAN("SCAN"),
	TURN("TURN"),
	PICK_UP_ITEM("PICK UP ITEM"),
	REMOVE_VEGITATION("REMOVE VEGITATION"),
	INVALID("INVALID");

	private final String name;
	ActionType(String s) {
		name = s;
	}

	public boolean equalsName(String otherName) {
		return otherName != null && name.equals(otherName);
	}

	public String toString() {
		return this.name;
	}
}
