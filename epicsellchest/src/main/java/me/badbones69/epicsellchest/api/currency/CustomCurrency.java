package me.badbones69.epicsellchest.api.currency;

public class CustomCurrency {
	
	private String name;
	private String command;
	
	/**
	 * A custom currency object.
	 * @param name Name of the custom currency.
	 * @param command The command that is ran for this currency.
	 */
	public CustomCurrency(String name, String command) {
		this.name = name;
		this.command = command;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCommand() {
		return command;
	}
	
}