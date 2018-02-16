package me.badbones69.epicsellchest.api;

public enum SellType {
	
	GUI("GUI"),
	WAND("Wand"),
	CHUNK("Chunk"),
	SINGLE("Single"),
	REGION("Region");
	
	private String name;
	
	private SellType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
}