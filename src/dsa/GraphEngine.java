// File: src/dsa/GraphEngine.java
package dsa;
import java.io.File;
import java.util.*;
import javax.swing.JOptionPane;

public class GraphEngine {
    public Map<String, Task> taskMap = new LinkedHashMap<>();
    public Map<String, List<String>> adjList = new HashMap<>();
    public Map<String, Integer> inDegree = new HashMap<>();
    public File activeFile = null; 
    
    public List<String> topologicalOrder = new ArrayList<>();
    public int totalProjectDuration = 0;

    public void addTask(Task task) {
        taskMap.put(task.id, task);
        adjList.putIfAbsent(task.id, new ArrayList<>());
        inDegree.putIfAbsent(task.id, 0);
    }

    public void buildGraph() {
        for (Task task : taskMap.values()) {
            for (String depId : task.dependencies) {
                if (!taskMap.containsKey(depId)) continue; 
                adjList.get(depId).add(task.id);
                inDegree.put(task.id, inDegree.get(task.id) + 1);
            }
        }
    }

    public boolean processProject() {
        Queue<String> queue = new LinkedList<>();
        Map<String, Integer> currentInDegree = new HashMap<>(inDegree);

        for (Map.Entry<String, Integer> entry : currentInDegree.entrySet()) {
            if (entry.getValue() == 0) queue.offer(entry.getKey());
        }

        topologicalOrder.clear();
        while (!queue.isEmpty()) {
            String u = queue.poll();
            topologicalOrder.add(u);
            for (String v : adjList.get(u)) {
                currentInDegree.put(v, currentInDegree.get(v) - 1);
                if (currentInDegree.get(v) == 0) queue.offer(v);
            }
        }

        if (topologicalOrder.size() != taskMap.size()) return false; 

        totalProjectDuration = 0;
        for (String uId : topologicalOrder) {
            Task u = taskMap.get(uId);
            u.eft = u.est + u.duration; 
            totalProjectDuration = Math.max(totalProjectDuration, u.eft);
            for (String vId : adjList.get(uId)) {
                Task v = taskMap.get(vId);
                v.est = Math.max(v.est, u.eft);
            }
        }

        for (Task task : taskMap.values()) task.lft = totalProjectDuration;
        
        for (int i = topologicalOrder.size() - 1; i >= 0; i--) {
            String uId = topologicalOrder.get(i);
            Task u = taskMap.get(uId);
            for (String vId : adjList.get(uId)) {
                Task v = taskMap.get(vId);
                u.lft = Math.min(u.lft, v.lst);
            }
            
            u.lst = u.lft - u.duration;
            u.slack = u.lft - u.eft; 
            u.isCritical = (u.slack == 0); 

            int prereqs = u.dependencies.size();
            int h = u.duration; 
            
            if (h <= 5 && prereqs == 0) u.rarity = "COMMON";
            else if (h <= 12 && prereqs <= 1) u.rarity = "UNCOMMON";
            else if (h <= 48 && prereqs == 2) u.rarity = "RARE";
            else if (h <= 72) u.rarity = "EPIC";
            else if (h <= 168 && prereqs >= 10) u.rarity = "LEGENDARY";
            else {
                if (h > 168) u.rarity = "LEGENDARY";
                else if (u.isCritical) u.rarity = "EPIC";
                else u.rarity = "UNCOMMON";
            }

            int multi = switch(u.rarity) {
                case "LEGENDARY" -> 5; case "EPIC" -> 4; case "RARE" -> 3;
                case "UNCOMMON" -> 2; default -> 1; 
            };
            u.expReward = (u.duration * 10) * multi;
        }
        return true; 
    }

    public void saveActiveGraph() {
        if (activeFile == null) return;
        try (java.io.PrintWriter out = new java.io.PrintWriter(new java.io.FileWriter(activeFile))) {
            for (Task t : taskMap.values()) {
                if (t.isCompleted) continue; 
                String deps = t.dependencies.isEmpty() ? "none" : String.join(";", t.dependencies);
                String limit = t.timeLimit == null ? "" : t.timeLimit.toString();
                out.println(t.id + "," + t.name + "," + t.duration + "," + limit + "," + deps + ",false," + t.timeMode + "," + t.deadlineMs);
            }
        } catch (Exception e) { System.err.println("Error saving: " + e.getMessage()); }
    }

    public boolean loadCustomCSV(File file) {
        this.activeFile = file;
        taskMap.clear(); adjList.clear(); inDegree.clear();
        int loadedCount = 0;
        int failedCount = 0;
        String firstError = "";

        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", -1);
                if (parts.length < 3) {
                    failedCount++;
                    if (firstError.isEmpty()) firstError = "Missing commas in line: " + line;
                    continue;
                }

                String id = parts[0].trim().replaceAll("[^a-zA-Z0-9_-]", "");
                String name = parts[1].trim();

                try {
                    int duration = Integer.parseInt(parts[2].trim().replaceAll("[^0-9]", ""));

                    Integer limit = null;
                    if (parts.length > 3 && !parts[3].trim().isEmpty()) {
                        limit = Integer.parseInt(parts[3].trim().replaceAll("[^0-9]", ""));
                    }

                    List<String> deps = new ArrayList<>();
                    if (parts.length > 4 && !parts[4].trim().isEmpty() && !parts[4].trim().equalsIgnoreCase("none")) {
                        String[] depArr = parts[4].split(";");
                        for (String d : depArr) {
                            if (!d.trim().isEmpty()) deps.add(d.trim().replaceAll("[^a-zA-Z0-9_-]", ""));
                        }
                    }

                    Task t = new Task(id, name, duration, limit, deps);

                    if (parts.length > 5 && !parts[5].trim().isEmpty()) {
                        t.isCompleted = Boolean.parseBoolean(parts[5].trim().toLowerCase());
                    }

                    if (parts.length > 6 && !parts[6].trim().isEmpty()) {
                        t.timeMode = parts[6].trim();
                    } else {
                        t.timeMode = "TIMER";
                    }

                    if (parts.length > 7 && !parts[7].trim().isEmpty()) {
                        try { t.deadlineMs = Long.parseLong(parts[7].trim().replaceAll("[^0-9]", "")); } catch (Exception ignored) {}
                    }

                    if ((t.timeMode.equals("NONE") || t.timeMode.equals("TIMER")) && t.deadlineMs == 0) {
                        t.timeMode = "TIMER";
                        t.deadlineMs = System.currentTimeMillis() + (t.duration * 3600000L);
                    }

                    addTask(t);
                    loadedCount++;

                } catch (Exception ex) {
                    failedCount++;
                    if (firstError.isEmpty()) firstError = "Number format error in line: " + line;
                }
            }

            if (loadedCount == 0) {
                JOptionPane.showMessageDialog(null, "Could not load any tasks!\nReason: " + (firstError.isEmpty() ? "File was empty." : firstError), "CSV Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (failedCount > 0) {
                JOptionPane.showMessageDialog(null, "Loaded " + loadedCount + " tasks, but skipped " + failedCount + " corrupted rows.", "Warning", JOptionPane.WARNING_MESSAGE);
            }

            buildGraph();
            boolean success = processProject();
            if (!success) JOptionPane.showMessageDialog(null, "Tasks loaded, but Kahn's Algorithm detected a DEADLOCK (Dependency Cycle)!", "Graph Error", JOptionPane.ERROR_MESSAGE);
            
            return success;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Critical File Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void deleteActiveFile() {
        if (activeFile != null && activeFile.exists()) activeFile.delete();
    }
}