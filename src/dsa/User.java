// File: src/dsa/User.java
package dsa;

public class User {
    public String username;
    public String password;
    public int xp;
    public int level;
    public int streak;

    // Constructor for Brand New Users
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.xp = 0;
        this.level = 1;
        this.streak = 0;
    }

    // Constructor for Loading from CSV
    public User(String username, String password, int xp, int level, int streak) {
        this.username = username;
        this.password = password;
        this.xp = xp;
        this.level = level;
        this.streak = streak;
    }

    public void addXP(int amount) {
        this.xp += amount;
        this.level = 1 + (this.xp / 1000); 
    }

    // Formats user data to save into the CSV
    public String toCSV() {
        return username + "," + password + "," + xp + "," + level + "," + streak;
    }
}