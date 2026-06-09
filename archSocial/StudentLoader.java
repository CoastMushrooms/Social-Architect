import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StudentLoader {

    public static Map<String, Student> loadStudentsFromCSV(String filePath) {
        Map<String, Student> studentMap = new HashMap<>();
        Map<String, String[]> temporaryFriendsMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();

            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (tokens.length < 4) continue;

                String name = tokens[0].replace("\"", "").trim();
                double trustFactor = Double.parseDouble(tokens[1].replace("\"", "").trim());
                double happiness = Double.parseDouble(tokens[2].replace("\"", "").trim()) / 10.0;
                String friendsRaw = tokens[3].replace("\"", "").trim();

                Student student = new Student(name, trustFactor, happiness);
                studentMap.put(name, student);

                String[] friendEntries = friendsRaw.split(",");
                for (int i = 0; i < friendEntries.length; i++) {
                    friendEntries[i] = friendEntries[i].trim();
                }
                temporaryFriendsMap.put(name, friendEntries);
            }

            for (String studentName : studentMap.keySet()) {
                Student student = studentMap.get(studentName);
                String[] friendEntries = temporaryFriendsMap.get(studentName);

                if (friendEntries != null) {
                    for (String entry : friendEntries) {
                        if (entry.isEmpty()) continue;
                        String friendName;
                        double weight = 5.0;

                        if (entry.contains(":")) {
                            String[] pair = entry.split(":");
                            friendName = pair[0].trim();
                            weight = Double.parseDouble(pair[1].trim());
                        } else {
                            friendName = entry.trim();
                        }

                        if (studentMap.containsKey(friendName)) {
                            student.setFriendship(friendName, weight);
                        }
                    }
                }
            }

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        
        return studentMap;
    }
}