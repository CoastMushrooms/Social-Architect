import java.util.*;

public class GameState {

    private Map<String, Student> students;
    private int day;
    private int week;
    private int actionPoints;
    private final int maxActionPoints = 5;
    private int goodwillAP;
    private List<String> eventLog;
    private List<String> friendshipSuggestions;
    private Random random;

    public static final int DAYS_PER_WEEK = 5;

    public GameState(Map<String, Student> students) {
        this.students = students;
        this.day = 1;
        this.week = 1;
        this.actionPoints = 5;
        this.goodwillAP = 0;
        this.eventLog = new ArrayList<>();
        this.friendshipSuggestions = new ArrayList<>();
        this.random = new Random();
        logEvent("Week 1, Day 1: School starts.");
    }

    public void logEvent(String event) { 
        eventLog.add(event); 
    }

    public List<String> getEventLog() { 
        return eventLog; 
    }
    
    public List<String> getFriendshipSuggestions() { 
        return friendshipSuggestions; 
    }

    public Map<String, Student> getStudents() {
        return students; 
    }

    public int getDay() { 
        return day; 
    }

    public int getWeek() { 
        return week; 
    }

    public int getActionPoints() { 
        return actionPoints; 
    }

    public int getGoodwillAP() { 
        return goodwillAP; 
    }

    public boolean spendAP(int cost) {
        if (actionPoints >= cost) { 
            actionPoints -= cost; 
            return true; 
        }
        return false;
    }

    private double moreLove(Student a, Student b, double bond) {
        double avgTrust = (a.getTrustFactor() + b.getTrustFactor()) / 2.0;
        double avgMood = (a.getHappiness()   + b.getHappiness())   / 2.0;
        double base = 0.5 + random.nextDouble() * 1.0;
        double quality = 0.6 + avgTrust * 0.8 + avgMood * 0.6;
        double ceiling = 1.0 - bond / 10.0;
        return Math.max(0.3, Math.min(2.0, base * quality * ceiling));
    }

    private double moreLoveChance(Student a, Student b) {
        double avgTrust = (a.getTrustFactor() + b.getTrustFactor()) / 2.0;
        double avgMood = (a.getHappiness()   + b.getHappiness())   / 2.0;
        return Math.min(0.90, Math.max(0.25, 0.35 + avgTrust * 0.35 + avgMood * 0.20 + random.nextDouble() * 0.10));
    }

    public boolean hostEvent(String nameA, String nameB) {
        if (!spendAP(2)) { 
            logEvent("Not enough AP to host an event! (Cost: 2)"); 
            return false; 
        }
        Student a = students.get(nameA), b = students.get(nameB);
        if (a == null || b == null) 
            return false;

        double avgTrust = (a.getTrustFactor() + b.getTrustFactor()) / 2.0;
        double avgMood = (a.getHappiness()   + b.getHappiness())   / 2.0;

        int mutuals = 0;
        for (String nb : a.getFriendNames())
            if (!nb.equals(nameB) && b.isFriendsWith(nb)) 
                mutuals++;
        double mutualEventBonus = Math.min(0.10, mutuals * 0.03);

        double chance = Math.min(0.88, Math.max(0.20,
            0.30 + avgMood * 0.35 + avgTrust * 0.23 + random.nextDouble() * 0.12 + mutualEventBonus));

        if (random.nextDouble() < chance) {
            double newBond;
            if (!a.isFriendsWith(nameB)) {
                newBond = Math.min(10.0, Math.max(2.0, 2.0 + random.nextDouble() * 1.0 + avgTrust * 1.0 + avgMood * 0.8));
            } else {
                double cur = a.getFriendshipWeight(nameB);
                double gain = Math.min(2.5, Math.max(1.0, 1.0 + random.nextDouble() * 0.8 + avgTrust * 0.7 + avgMood * 0.5));
                newBond = Math.min(10.0, cur + gain);
            }
            a.setFriendship(nameB, newBond + 1.0);
            b.setFriendship(nameA, newBond + 1.0);
            double boost = 0.05 + avgMood * 0.04;
            a.changeHappiness(boost);
            b.changeHappiness(boost);
            logEvent(String.format("Event succeeded. %s and %s now have bond %.1f.", nameA, nameB, newBond));
            goodwillAP++;
        } else {
            a.changeHappiness(-0.02);
            b.changeHappiness(-0.02);
            logEvent(String.format("Event failed (%.0f%% chance). No improvement between %s and %s.", chance * 100, nameA, nameB));
        }
        return true;
    }

    public boolean sendGift(String targetName) {
        if (!spendAP(1)) { 
            logEvent("Not enough AP to send a gift! (Cost: 1)"); 
            return false; 
        }
        Student target = students.get(targetName);
        if (target == null) 
            return false;
        target.changeHappiness(0.25);
        target.changeTF(0.15);
        logEvent(String.format("Gift sent to %s! Happiness: %.0f%%", targetName, target.getHappiness() * 100));
        goodwillAP++;
        return true;
    }

    public boolean reinforceBond(String nameA, String nameB) {
        if (!spendAP(2)) { 
            logEvent("Not enough AP to reinforce! (Cost: 2)"); 
            return false; 
        }
        Student a = students.get(nameA), b = students.get(nameB);
        if (a == null || b == null) 
            return false;
        if (!a.isFriendsWith(nameB)) {
            logEvent(nameA + " and " + nameB + " aren't connected yet. Host an event first!");
            return false;
        }
        double current = (a.getFriendshipWeight(nameB) + b.getFriendshipWeight(nameA)) / 2.0;
        double chance = moreLoveChance(a, b);
        double gain = moreLove(a, b, current);

        if (random.nextDouble() < chance) {
            double newBond = Math.min(25.0, current + gain);
            a.setFriendship(nameB, newBond + 1.5);
            b.setFriendship(nameA, newBond + 1.5);
            double boost = 0.1 + gain * .15;
            a.changeHappiness(boost);
            b.changeHappiness(boost);
            logEvent(String.format("Bond grew by %.1f to %.1f.", gain, newBond));
            goodwillAP++;
        } else {
            a.changeHappiness(-0.05);
            b.changeHappiness(-0.05);
            logEvent(String.format("Reinforcement failed. %s and %s kept their current bond.", nameA, nameB));
        }
        return true;
    }

    public void endDay() {
        logEvent("\nNight Falls: Day " + day);
        mutualConnector();
        passiveRelationships();
        passiveHappiness();
        triggerRandomToxicity();
        checkLonelyNodes();
        generateFriendshipSuggestions(); 

        day++;
        if (day > DAYS_PER_WEEK) {
            day = 1;
            week++;
            actionPoints = maxActionPoints + goodwillAP / 2;
            goodwillAP = 0;
            logEvent("\nNEW WEEK " + week + ": " + actionPoints + " AP");
        } else {
            actionPoints = Math.min(maxActionPoints + goodwillAP, actionPoints + 4);
            logEvent("Morning: " + actionPoints + " AP available.");
        }
    }

    private void mutualConnector() {
        for (String firstName : new ArrayList<>(students.keySet())) {
            Student first = students.get(firstName);
            for (String mutualName : new ArrayList<>(first.getFriendNames())) {
                if (first.getFriendshipWeight(mutualName) < 6) 
                    continue;
                Student mutual = students.get(mutualName);
                if (mutual == null) 
                    continue;
                for (String fofName : new ArrayList<>(mutual.getFriendNames())) {
                    if (fofName.equals(firstName) || first.isFriendsWith(fofName)) 
                        continue;
                    Student fof = students.get(fofName);
                    if (fof == null) 
                        continue;

                    double avgHappy = (first.getHappiness() + mutual.getHappiness() + fof.getHappiness()) / 3.0;
                    double mutualBondToFirst = first.getFriendshipWeight(mutualName);
                    double mutualBondToFof = mutual.getFriendshipWeight(fofName);
                    double mutualStrength = (mutualBondToFirst + mutualBondToFof) / 2.0;
                    double mutualBonus = (mutualStrength / 10.0) * 0.12;
                    double chance = Math.min(0.35, avgHappy * 0.3 + mutualBonus);

                    if (random.nextDouble() < chance) {
                        double bond = 2.5 + random.nextDouble() * 2.0 + (mutualStrength / 10.0) * 1.5;
                        first.setFriendship(fofName, bond);
                        fof.setFriendship(firstName, bond);
                        logEvent(String.format("%s and %s became friends through %s! (Bond: %.1f)", firstName, fofName, mutualName, bond));
                    }
                }
            }
        }
    }

    private void passiveRelationships() {
        Set<String> handled = new HashSet<>();
        for (Student src : students.values()) {
            for (String tgtName : new ArrayList<>(src.getFriendNames())) {
                String key = src.getName().compareTo(tgtName) < 0 ? src.getName() + "|" + tgtName : tgtName + "|" + src.getName();
                if (!handled.add(key)) 
                    continue;
                Student tgt = students.get(tgtName);
                if (tgt == null) 
                    continue;

                double wSrc = src.getFriendshipWeight(tgtName);
                double wTgt = tgt.getFriendshipWeight(src.getName());
                double avgBond = (wSrc + wTgt) / 2.0;
                double avgTrust = (src.getTrustFactor() + tgt.getTrustFactor()) / 2.0;
                double avgMood = (src.getHappiness()   + tgt.getHappiness())   / 2.0;

                int mutualCount = 0;
                double mutualBondSum = 0.0;
                for (String nb : src.getFriendNames()) {
                    if (!nb.equals(tgtName) && tgt.isFriendsWith(nb)) {
                        mutualCount++;
                        mutualBondSum += (src.getFriendshipWeight(nb) + tgt.getFriendshipWeight(nb)) / 2.0;
                    }
                }

                double avgMutualBond = mutualCount > 0 ? mutualBondSum / mutualCount : 0.0;
                double stabilityBonus = Math.min(0.10, mutualCount * 0.025 + (avgMutualBond / 10.0) * 0.04);

                if (avgBond > 6) {
                    double weakenChance = Math.min(0.12, Math.max(0.0, (1 - avgTrust) * 0.10 + (1 - avgMood) * 0.06 + random.nextDouble() * 0.04 - stabilityBonus));
                    if (random.nextDouble() < weakenChance) {
                        double amount = 0.05 + random.nextDouble() * 0.20;
                        src.setFriendship(tgtName, Math.max(0, wSrc - amount));
                        tgt.setFriendship(src.getName(), Math.max(0, wTgt - amount));
                        logEvent(String.format("Bond weakened by %.1f between %s and %s.", amount, src.getName(), tgtName));
                    }
                } else {
                    double breakChance = Math.min(0.20, Math.max(0.0, (1.0 - avgBond / 4.0) * 0.15 + (1 - avgTrust) * 0.08 + random.nextDouble() * 0.05 - stabilityBonus));
                    if (random.nextDouble() < breakChance) {
                        src.removeFriend(tgtName);
                        tgt.removeFriend(src.getName());
                        logEvent(String.format("Bond broke between %s and %s.", src.getName(), tgtName));
                        continue;
                    }
                    double weakenChance = Math.min(0.45, Math.max(0.0, 0.10 + (1.0 - avgBond / 4.0) * 0.25 + (1 - avgTrust) * 0.15 + random.nextDouble() * 0.08 - stabilityBonus));
                    if (random.nextDouble() < weakenChance) {
                        double amount = 0.3 + random.nextDouble() * 0.5 + (1.0 - avgTrust) * 0.3;
                        src.setFriendship(tgtName, Math.max(0, wSrc - amount));
                        tgt.setFriendship(src.getName(), Math.max(0, wTgt - amount));
                        logEvent(String.format("Bond weakened by %.1f between %s and %s.", amount, src.getName(), tgtName));
                    }
                }
            }
        }
    }

    private void passiveHappiness() {
        for (Student s : students.values()) {
            double avg = 0; int count = 0;
            for (String fn : s.getFriendNames()) {
                double w = s.getFriendshipWeight(fn);
                if (w > 0) { avg += w; count++; }
            }
            if (count > 0) avg /= count;

            if (count == 0) {
                double p = Math.min(0.70, 0.25 + (1.0 - s.getHappiness()) * 0.35);
                if (random.nextDouble() < p)
                    s.changeHappiness(-0.03 - random.nextDouble() * 0.03);
            } else if (avg >= 7) {
                if (random.nextDouble() < 0.30)
                    s.changeHappiness(0.01 + random.nextDouble() * 0.02);
            } else if (avg <= 4) {
                double p = Math.min(0.50, 0.15 + (4.0 - avg) * 0.06);
                if (random.nextDouble() < p)
                    s.changeHappiness(-0.02 - random.nextDouble() * 0.02);
            } else {
                if (random.nextDouble() < 0.08)
                    s.changeHappiness(-0.01 - random.nextDouble() * 0.01);
            }
        }
    }

    private void checkLonelyNodes() {
        for (Student s : students.values()) {
            if (s.getFriendNames().isEmpty()) {
                logEvent("WARNING: " + s.getName() + " is isolated!");
                s.changeHappiness(-0.08);
            }
        }
    }

    private void triggerRandomToxicity() {
        List<Student> vulnerable = new ArrayList<>();
        for (Student s : students.values()) {
            if (s.getHappiness() <= 0.6 && !s.getFriendNames().isEmpty()) {
                vulnerable.add(s);
            }
        }

        if (vulnerable.isEmpty()) 
            return;

        if (random.nextDouble() > 0.40) 
            return;

        Student toxic = vulnerable.get(random.nextInt(vulnerable.size()));
        List<String> targets = new ArrayList<>(toxic.getFriendNames());
        if (targets.isEmpty()) 
            return;

        String targetName = targets.get(random.nextInt(targets.size()));
        Student target = students.get(targetName);
        if (target == null) 
            return;

        double bondWeight = toxic.getFriendshipWeight(targetName);
        
        logEvent(String.format("%s is being negative toward %s (bond: %.1f).", toxic.getName(), targetName, bondWeight));

        double happinessDamage = 0.10 + random.nextDouble() * 0.08;
        target.changeHappiness(-happinessDamage);
        logEvent(String.format("%s's happiness dropped by %.0f%%.", targetName, happinessDamage * 100));

        List<String> mutualFriends = new ArrayList<>();
        for (String mutual : target.getFriendNames()) {
            if (!mutual.equals(toxic.getName()) && toxic.isFriendsWith(mutual)) {
                mutualFriends.add(mutual);
            }
        }

        for (String mutualName : mutualFriends) {
            double mutualBondDamage = 0.5 + random.nextDouble() * 1.0;
            target.setFriendship(mutualName, Math.max(0, target.getFriendshipWeight(mutualName) - mutualBondDamage));
            
            Student mutual = students.get(mutualName);
            if (mutual != null) {
                mutual.setFriendship(targetName, Math.max(0, mutual.getFriendshipWeight(targetName) - mutualBondDamage));
            }
            
            logEvent(String.format("    Bond between %s and %s weakened by %.1f (toxic ripple effect).", targetName, mutualName, mutualBondDamage));
        }

        if (!mutualFriends.isEmpty()) {
            logEvent(String.format("  SOLUTION: Break %s–%s bond to restore connections with %d mutual friend(s).", toxic.getName(), targetName, mutualFriends.size()));
        }
    }

    public double getAverageHappiness() {
        if (students.isEmpty()) 
            return 0;
        double total = 0;
        for (Student s : students.values()) {
            total += s.getHappiness();
        }
        return total / students.size();
    }

    public String getMostCentral() {
        String top = ""; int max = -1;
        for (Student s : students.values()) {
            if (s.getFriendNames().size() > max) { 
                max = s.getFriendNames().size(); top = s.getName(); 
            }
        }
        return top;
    }

    private List<String> bfsPath(String start, String end) {
        Map<String, String> parent = new HashMap<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(start);
        parent.put(start, null);
        while (!queue.isEmpty()) {
            String cur = queue.poll();
            if (cur.equals(end)) {
                List<String> path = new ArrayList<>();
                for (String n = end; n != null; n = parent.get(n)) {
                    path.add(0, n);
                }
                return path;
            }
            Student s = students.get(cur);
            if (s == null) continue;
            for (String nb : s.getFriendNames())
                if (!parent.containsKey(nb)) { 
                    parent.put(nb, cur); queue.add(nb); 
                }
        }
        return null;
    }

    public List<String> dijkstraPath(String start, String end) {
        Map<String, Double> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> dist.getOrDefault(n, Double.MAX_VALUE)));
        for (String name : students.keySet()) {
            dist.put(name, Double.MAX_VALUE);
        }
        dist.put(start, 0.0);
        pq.add(start);
        while (!pq.isEmpty()) {
            String u = pq.poll();
            if (u.equals(end)) 
                break;
            Student s = students.get(u);
            if (s == null) 
                continue;
            for (String nb : s.getFriendNames()) {
                double newDist = dist.get(u) + (10.0 - s.getFriendshipWeight(nb));
                if (newDist < dist.getOrDefault(nb, Double.MAX_VALUE)) {
                    dist.put(nb, newDist); prev.put(nb, u); pq.add(nb);
                }
            }
        }
        if (!prev.containsKey(end) && !start.equals(end)) 
            return null;
        List<String> path = new ArrayList<>();
        for (String n = end; n != null; n = prev.get(n)) {
            path.add(0, n);
        }
        return path;
    }

    public int countWeakBonds() {
        Set<String> seen = new HashSet<>();
        int count = 0;
        for (Student a : students.values())
            for (String bName : a.getFriendNames()) {
                String key = a.getName().compareTo(bName) < 0 ? a.getName() + "|" + bName : bName + "|" + a.getName();
                if (seen.add(key) && a.getFriendshipWeight(bName) < 3) 
                    count++;
            }
        return count;
    }

    public boolean isGameOver() { return week > 3; }

    public String getWinStatus() {
        double h = getAverageHappiness();
        if (h >= 0.9) 
            return "WINNING";
        if (h >= 0.5) 
            return "PROGRESSING";
        return "STRUGGLING";
    }

    public String getEnding() {
        double h = getAverageHappiness();
        int weak = countWeakBonds();
        if (h >= 0.8 && weak <= 3) 
            return "Successful. The students are connected!";
        if (h >= 0.35)             
            return "Partial Success. Students are fragmented; could be better.";
        return "Failure. Most students are unhappy and disconnected.";
    }

    public boolean breakFriendship(String nameA, String nameB) {
        if (!spendAP(1)) { 
            logEvent("Not enough AP to end a friendship! (Cost: 1)"); 
            return false; 
        }
        Student a = students.get(nameA), b = students.get(nameB);
        if (a == null || b == null) 
            return false;
        if (!a.isFriendsWith(nameB)) {
            logEvent(nameA + " and " + nameB + " are not connected.");
            return false;
        }

        List<String> pathWithBond = bfsPath(nameA, nameB);
        
        a.removeFriend(nameB);
        b.removeFriend(nameA);
        
        List<String> pathWithoutBond = bfsPath(nameA, nameB);
        
        if (pathWithoutBond == null && pathWithBond != null) {
            a.setFriendship(nameB, 5.0);
            b.setFriendship(nameA, 5.0);
            logEvent("Breaking " + nameA + "–" + nameB + " would isolate them. Friendship restored.");
            return false;
        }

        double toxicityA = calcTox(a);
        double toxicityB = calcTox(b);
        double maxToxicity = Math.max(toxicityA, toxicityB);

        String excuse = generateExcuse(nameA, nameB, a, b, maxToxicity);
        logEvent(excuse);

        double bondStrength = pathWithBond != null ? (a.getFriendshipWeight(nameB) + b.getFriendshipWeight(nameA)) / 2.0 : 5.0;
        double toxicBoost = maxToxicity * 0.08;
        double weakBondBoost = Math.max(0, (5.0 - bondStrength) * 0.03);
        double totalBoost = 0.05 + toxicBoost + weakBondBoost;

        for (String friendName : new ArrayList<>(a.getFriendNames())) {
            if (!friendName.equals(nameB)) {
                students.get(friendName).changeHappiness(totalBoost * 0.5);
            }
        }
        for (String friendName : new ArrayList<>(b.getFriendNames())) {
            if (!friendName.equals(nameA)) {
                students.get(friendName).changeHappiness(totalBoost * 0.5);
            }
        }

        List<String> mutualFriends = new ArrayList<>();
        for (String mutual : a.getFriendNames()) {
            if (!mutual.equals(nameB) && b.isFriendsWith(mutual)) {
                mutualFriends.add(mutual);
            }
        }

        for (String mutualName : mutualFriends) {
            Student mutual = students.get(mutualName);
            if (mutual == null) 
                continue;
            
            double healAmount = 1.5 + random.nextDouble() * 1.5;
            b.setFriendship(mutualName, Math.min(10.0, b.getFriendshipWeight(mutualName) + healAmount));
            mutual.setFriendship(nameB, Math.min(10.0, mutual.getFriendshipWeight(nameB) + healAmount));
            
            logEvent(String.format("  ✓ %s and %s reconnected (+%.1f bond).", nameB.split(" ")[0], mutualName.split(" ")[0], healAmount));
        }

        if (toxicityA > toxicityB) {
            b.changeHappiness(0.15);
            logEvent(String.format("%s feels relieved and recovered +15%% happiness.", nameB));
        } else if (toxicityB > toxicityA) {
            a.changeHappiness(0.15);
            logEvent(String.format("%s feels relieved and recovered +15%% happiness.", nameA));
        }

        goodwillAP++;
        return true;
    }

    private double calcTox(Student s) {
        double unhappiness = 1.0 - s.getHappiness();
        double untrustworthiness = 1.0 - s.getTrustFactor();
        double weakBonds = 0;
        for (String fn : s.getFriendNames()){
            if (s.getFriendshipWeight(fn) < 3) 
                weakBonds++;
        }
        return (unhappiness * 0.4 + untrustworthiness * 0.3 + weakBonds * 0.15);
    }

    private String generateExcuse(String nameA, String nameB, Student a, Student b, double toxicity) {
        double happyA = a.getHappiness(), happyB = b.getHappiness();
        double bondWeight = (a.getFriendshipWeight(nameB) + b.getFriendshipWeight(nameA)) / 2.0;
        
        String toxic = happyA < 0.3 ? nameA : nameB;
        if (happyA < 0.3 && happyB < 0.3) toxic = "both";

        if (toxicity >= 0.6) {
            if (toxic.equals("both")) {
                return String.format("%s and %s have been seperated", nameA, nameB);
            } else {
                return String.format("Ended %s–%s bond. %s toxicity is at %.0f%%).", nameA, nameB, toxic, toxicity * 100);
            }
        } else if (bondWeight <= 2) {
            return String.format("%s and %s bond (%.1f) dissolved.", nameA, nameB, bondWeight);
        } else if (happyA > 0.8 || happyB > 0.8) {
            String happier = happyA > happyB ? nameA : nameB;
            return String.format("%s and %s: %s ended this friendship.", nameA, nameB, happier);
        } else {
            return String.format("Ended %s–%s bond to strengthen other friendships.", nameA, nameB);
        }
    }

    public void generateFriendshipSuggestions() {
        friendshipSuggestions.clear();
        List<String> names = new ArrayList<>(students.keySet());
        if (names.size() < 2) 
            return;

        List<String[]> bridgePairs = new ArrayList<>();

        Set<String> checkedPairs = new HashSet<>();
        for (String start : names) {
            Map<String, Integer> checks = new HashMap<>();
            Queue<String> q = new LinkedList<>();
            q.add(start); checks.put(start, 0);
            while (!q.isEmpty()) {
                String cur = q.poll();
                Student s = students.get(cur);
                if (s == null) 
                    continue;
                for (String nb : s.getFriendNames()) {
                    if (!checks.containsKey(nb)) {
                        checks.put(nb, checks.get(cur) + 1);
                        q.add(nb);
                    }
                }
            }
            for (String other : names) {
                if (other.equals(start)) 
                    continue;
                String key = start.compareTo(other) < 0 ? start+"|"+other : other+"|"+start;
                if (!checkedPairs.add(key)) 
                    continue;
                if (!checks.containsKey(other)) {
                    bridgePairs.add(new String[]{start, other, "disconnected"});
                } else if (checks.get(other) >= 3) {
                    bridgePairs.add(new String[]{start, other, "distant:" + checks.get(other)});
                }
            }
        }

        bridgePairs.sort((x, y) -> {
            boolean xDis = x[2].equals("disconnected"), yDis = y[2].equals("disconnected");
            if (xDis != yDis) 
                return xDis ? -1 : 1;
            double hx = students.get(x[0]).getHappiness() + students.get(x[1]).getHappiness();
            double hy = students.get(y[0]).getHappiness() + students.get(y[1]).getHappiness();
            return Double.compare(hx, hy);
        });

        int suggestionCount = 0;
        Set<String> alreadySuggested = new HashSet<>();

        for (String[] pair : bridgePairs) {
            if (suggestionCount >= 3) 
                break;
            String a = pair[0], b = pair[1];
            if (alreadySuggested.contains(a) && alreadySuggested.contains(b)) 
                continue;

            String aFirst = a.split(" ")[0], bFirst = b.split(" ")[0];

            if (pair[2].equals("disconnected")) {
                List<String> path = dijkstraPath(a, b);
                if (path == null || path.size() <= 1) {
                    friendshipSuggestions.add(String.format("%s and %s are in separate social groups. Host an event to bridge them!", aFirst, bFirst));
                } else {
                    String weakA = path.get(path.size() - 2), weakB = path.get(path.size() - 1);
                    String wAFirst = weakA.split(" ")[0], wBFirst = weakB.split(" ")[0];
                    friendshipSuggestions.add(String.format("%s→%s are disconnected. Strengthen %s–%s bond to bridge their groups.", aFirst, bFirst, wAFirst, wBFirst));
                }
            } else {
                int checks = Integer.parseInt(pair[2].split(":")[1]);
                List<String> path = dijkstraPath(a, b);
                if (path != null && path.size() >= 2) {
                    String weakA = null, weakB = null;
                    double minEdge = Double.MAX_VALUE;
                    for (int i = 0; i < path.size() - 1; i++) {
                        Student ps = students.get(path.get(i));
                        double w = ps != null ? ps.getFriendshipWeight(path.get(i + 1)) : 0;
                        if (w < minEdge) { 
                            minEdge = w; weakA = path.get(i); weakB = path.get(i + 1); 
                        }
                    }
                    if (weakA != null) {
                        String wAFirst = weakA.split(" ")[0], wBFirst = weakB.split(" ")[0];
                        friendshipSuggestions.add(String.format("%s and %s are %d fall apart. Reinforce %s–%s (weakest link: %.1f).", aFirst, bFirst, checks, wAFirst, wBFirst, minEdge));
                    }
                } else {
                    friendshipSuggestions.add(String.format("%s and %s are %d falling apart. Host an event to bring them closer!", aFirst, bFirst, checks));
                }
            }
            alreadySuggested.add(a);
            alreadySuggested.add(b);
            suggestionCount++;
        }

        Set<String> weakPairsSeen = new HashSet<>();
        for (Student s : students.values()) {
            if (suggestionCount >= 4) break;
            for (String fn : s.getFriendNames()) {
                double w = s.getFriendshipWeight(fn);
                if (w >= 3.0) 
                    continue;
                String key = s.getName().compareTo(fn) < 0 ? s.getName()+"|"+fn : fn+"|"+s.getName();
                if (!weakPairsSeen.add(key)) 
                    continue;
                Student fs = students.get(fn);
                if (fs == null) 
                    continue;
                int mutuals = 0;
                for (String nb : s.getFriendNames()){
                    if (!nb.equals(fn) && fs.isFriendsWith(nb)) 
                        mutuals++;
                }
                if (mutuals >= 1) {
                    friendshipSuggestions.add(String.format("%s–%s bond is weak (%.1f) but they share %d mutual friend(s). Reinforce it!", s.getName().split(" ")[0], fn.split(" ")[0], w, mutuals));
                    suggestionCount++;
                    if (suggestionCount >= 4) 
                        break;
                }
            }
        }
    }
}