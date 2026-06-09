import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Student {
    private String name;
    private double trustFactor;
    private double happiness;
    private Map<String, Double> friends;

    public Student(String name, double trustFactor, double happiness) {
        this.name = name;
        this.trustFactor = Math.max(0.0, Math.min(1.0, trustFactor));
        this.happiness = Math.max(0.0, Math.min(1.0, happiness));
        this.friends = new HashMap<>();
    }

    public void setFriendship(String name, double weight) { 
        friends.put(name, weight); 
    }

    public void removeFriend(String name) { 
        friends.remove(name); 
    }

    public boolean isFriendsWith(String name) { 
        return friends.getOrDefault(name, 0.0) > 0; 
    }

    public double getFriendshipWeight(String name) { 
        return friends.getOrDefault(name, 0.0); 
    }

    public void changeHappiness(double amount) {
        happiness = Math.max(0.0, Math.min(1.0, happiness + amount));
    }
    public void changeTF(double amount) {
        trustFactor = Math.max(0.0, Math.min(1.0, trustFactor + amount));
    }

    public String getName() { 
        return name; 
    }

    public double getTrustFactor() { 
        return trustFactor; 
    }

    public double getHappiness() { 
        return happiness; 
    }

    public Set<String> getFriendNames() { 
        return friends.keySet(); 
    }
}