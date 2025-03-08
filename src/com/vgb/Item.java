package com.vgb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Item {
    private UUID uuid;
    private String name;

    public Item(String uuid, String name) {
        this.uuid = UUID.fromString(uuid);
        this.name = name;
    }

	public UUID getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}
	
	public double roundToCent(double price) {
	    return Math.round(price * 100.0) / 100.0;
	}
		
	public static List<Item> parseItems(String filePath) {
        List<Item> items = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            lines.remove(0);
            for (String line : lines) {
                String[] parts = line.split(",");
                String uuid = parts[0];
                String type = parts[1];
                String name = parts[2];
                switch (type) {
                    case "E":
                        items.add(new Equipment(uuid, name, parts[3], Double.parseDouble(parts[4])));
                        break;
                    case "M":
                        items.add(new Material(uuid, name, parts[3], Double.parseDouble(parts[4])));
                        break;
                    case "C":
                        items.add(new Contract(uuid, name, parts[3]));
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }

}