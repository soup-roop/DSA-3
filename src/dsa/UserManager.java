// File: src/dsa/UserManager.java
package dsa;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static final String FILE_PATH = "users.csv";
    private static Map<String, User> usersDb = new HashMap<>();

    static {
        loadUsersFromCSV();
    }

    private static void loadUsersFromCSV() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            // First run: Create file and add demo admin
            usersDb.put("admin", new User("admin", "admin123", 4500, 5, 14));
            saveUsersToCSV();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5) {
                    User u = new User(data[0], data[1], Integer.parseInt(data[2]), 
                                      Integer.parseInt(data[3]), Integer.parseInt(data[4]));
                    usersDb.put(u.username, u);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading users.csv: " + e.getMessage());
        }
    }

    public static void saveUsersToCSV() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (User user : usersDb.values()) {
                bw.write(user.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to users.csv: " + e.getMessage());
        }
    }

    public static boolean userExists(String username) {
        return usersDb.containsKey(username);
    }

    public static boolean authenticate(String username, String password) {
        if (userExists(username)) {
            return usersDb.get(username).password.equals(password);
        }
        return false;
    }

    public static User getUser(String username) {
        return usersDb.get(username);
    }

    public static User register(String username, String password) {
        User newUser = new User(username, password);
        usersDb.put(username, newUser);
        saveUsersToCSV(); // Instantly save new user to CSV
        return newUser;
    }
}