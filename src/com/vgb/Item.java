package com.vgb;

public abstract class Item {
    protected String uuid;
    protected String name;

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

}