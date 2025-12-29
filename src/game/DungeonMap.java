package game;

import model.exceptions.InvalidActionException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * מחלקה המייצגת את מפת המבוך.
 * משתמשת ב-HashMap למיפוי מזהי מיקום לאובייקטי GameLocation.
 */
public class DungeonMap {
    
    // HashMap ממזהה מיקום לאובייקט המיקום
    private HashMap<String, GameLocation> locations;
    private String currentLocationId;
    private String startLocationId;
    private String bossLocationId;
    
    public DungeonMap() {
        this.locations = new HashMap<>();
        this.currentLocationId = null;
        this.startLocationId = null;
        this.bossLocationId = null;
    }
    
    // ============================================================
    // TODO: ניהול מפה
    // ============================================================

    public void addLocation(GameLocation location) {
        locations.put(location.getId(), location);
        if (startLocationId == null) {
            setStartLocation(location.getId());
        }
    }
    public void connectLocations(String locationId1, String locationId2)
            throws InvalidActionException {
        if (!locations.containsKey(locationId1)) {
            throw new InvalidActionException("connect", "Location " + locationId1 + " does not exist");
        }
        if (!locations.containsKey(locationId2)) {
            throw new InvalidActionException("connect", "Location " + locationId2 + " does not exist");
        }

        locations.get(locationId1).addConnection(locationId2);
        locations.get(locationId2).addConnection(locationId1);
    }
    public GameLocation getLocation(String locationId) {
        return locations.get(locationId);
    }

    public GameLocation getCurrentLocation() {
        if (currentLocationId == null) return null;
        return locations.get(currentLocationId);
    }

    public void moveTo(String locationId) throws InvalidActionException {
        if (!locations.containsKey(locationId)) {
            throw new InvalidActionException("move", "Location " + locationId + " does not exist");
        }

        GameLocation current = getCurrentLocation();
        if (!current.isConnectedTo(locationId)) {
            throw new InvalidActionException("move", "Cannot reach " + locationId + " from current location");
        }

        currentLocationId = locationId;
        locations.get(locationId).markAsVisited();
    }

    public ArrayList<GameLocation> getVisitedLocations() {
        ArrayList<GameLocation> visited = new ArrayList<>();
        for (GameLocation loc : locations.values()) {
            if (loc.isVisited()) {
                visited.add(loc);
            }
        }
        return visited;
    }
    public ArrayList<GameLocation> getUnvisitedLocations() {
        ArrayList<GameLocation> unvisited = new ArrayList<>();
        for (GameLocation loc : locations.values()) {
            if (!loc.isVisited()) {
                unvisited.add(loc);
            }
        }
        return unvisited;
    }
    public ArrayList<GameLocation> getAccessibleLocations() {
        ArrayList<GameLocation> options = new ArrayList<>();
        GameLocation current = getCurrentLocation();

        if (current == null) {
            return options;
        }

        ArrayList<String> neighborIds = current.getConnectedLocationIds();
        for (String id : neighborIds) {
            GameLocation neighbor = locations.get(id);
            if (neighbor != null) {
                options.add(neighbor);
            }
        }
        return options;
    }

    public HashMap<Integer, ArrayList<GameLocation>> getLocationsByDangerLevel() {
        HashMap<Integer, ArrayList<GameLocation>> map = new HashMap<>();

        for (GameLocation loc : locations.values()) {
            int danger = loc.getDangerLevel();
            map.putIfAbsent(danger, new ArrayList<>());
            map.get(danger).add(loc);
        }

        return map;
    }

    public double getExplorationProgress() {
        if (locations.isEmpty()) {
            return 0.0;
        }

        int visitedCount = 0;
        for (GameLocation loc : locations.values()) {
            if (loc.isVisited()) {
                visitedCount++;
            }
        }

        return (double) visitedCount / locations.size();
    }
    // Setters for special locations
    public void setStartLocation(String locationId) {
        this.startLocationId = locationId;
        this.currentLocationId = locationId;
        if (locations.containsKey(locationId)) {
            locations.get(locationId).markAsVisited();
        }
    }
    
    public void setBossLocation(String locationId) {
        this.bossLocationId = locationId;
        if (locations.containsKey(locationId)) {
            locations.get(locationId).setHasMaster(true);
        }
    }
    
    // Getters
    public String getCurrentLocationId() {
        return currentLocationId;
    }
    
    public String getStartLocationId() {
        return startLocationId;
    }
    
    public String getBossLocationId() {
        return bossLocationId;
    }
    
    public int getTotalLocations() {
        return locations.size();
    }
    
    public HashMap<String, GameLocation> getAllLocations() {
        return new HashMap<>(locations);
    }
}
