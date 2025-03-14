package com.vgb;

public abstract class Item {
    private String uuid;
    private String name;

    public Item(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    /**
     * Rounds the price to the tenthes place and returns a double
     * @param price
     * @return
     */
    public double roundToCent(double price) {
	    return Math.round(price * 100.0) / 100.0;
	}
    
    /** 
     * Abstract methods to be implemented by subclasses 
     */
    public abstract double getCost();
    public abstract double getTax();

    @Override
    public String toString() {
        return String.format("%-40s (%s)\n%54s $%10.2f $%10.2f",
                name, uuid, "", getTax(), getCost());
    }

}