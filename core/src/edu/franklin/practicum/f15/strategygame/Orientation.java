package edu.franklin.practicum.f15.strategygame;

public enum Orientation {
	N("N"),
	NE("NE"),
	E("E"),
	SE("SE"),
	S("S"),
	SW("SW"),
	W("W"),
	NW("NW");

	private final String name;

	Orientation(String s) {
		name = s;
	}

	public boolean equalsName(String otherName) {
		return otherName != null && name.equals(otherName);
	}

	public String toString() {
		return this.name;
	}
}
