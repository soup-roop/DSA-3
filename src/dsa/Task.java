package dsa;
import java.util.ArrayList;
import java.util.List;

public class Task {
    public String id, name;
    public int duration;
    public Integer timeLimit; 
    public List<String> dependencies;

    public int est = 0, eft = 0, lst = 0, lft = 0, slack = 0;
    public boolean isCritical = false;
    public String rarity = "COMMON";
    public int expReward = 0;
    public boolean isCompleted = false; 

    // --- NEW: LIVE TIMER VARIABLES ---
    public String timeMode = "NONE"; // "NONE", "TIMER", "ALARM"
    public long deadlineMs = 0;      // The exact Epoch timestamp when time is up

    public Task(String id, String name, int duration, Integer timeLimit, List<String> dependencies) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.timeLimit = timeLimit;
        this.dependencies = dependencies != null ? dependencies : new ArrayList<>();
    }
}