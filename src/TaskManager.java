import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.time.LocalDateTime;

public class TaskManager {
    private final Path filePath;
    private ArrayList<Task> taskList;

    public TaskManager(String fileName){
        this.filePath = Paths.get(fileName);
        this.taskList = new ArrayList<>();
        loadTasksFromJson();
    }

    public void initializeFile() throws IOException {
        if (Files.notExists(filePath)){
            if(filePath.getParent() != null){
                Files.createDirectories(filePath.getParent());
            }
            Files.writeString(filePath, "[]");
        }
    }

    public void loadTasksFromJson(){

        String jsonText = readJsonFile();
        Pattern pattern = Pattern.compile("\\{[^\\}]+\\}");
        Matcher matcher = pattern.matcher(jsonText);

        while(matcher.find()){
            String jsonObject = matcher.group();
            Task task = parseTasksFromJson(jsonObject);
            this.taskList.add(task);
        }
    }

    public String readJsonFile(){
        try {
            return Files.readString(this.filePath);
        } catch (IOException error){
            System.out.println("Failed to read task file : " + error.getMessage());
            error.printStackTrace();
            return "[]";
        }
    }

    public Task parseTasksFromJson(String jsonObject){
        String id = extractJsonObjectValue(jsonObject, "id");
        String description = extractJsonObjectValue(jsonObject, "description");
        String statusString = extractJsonObjectValue(jsonObject, "status");
        String taskCreationTimeString = extractJsonObjectValue(jsonObject, "taskCreationTime");
        String lastUpdatedTimeString = extractJsonObjectValue(jsonObject, "lastUpdatedTime");

        TaskStatus status = TaskStatus.valueOf(statusString);
        LocalDateTime createdAt = LocalDateTime.parse(taskCreationTimeString);
        LocalDateTime updatedAt = LocalDateTime.parse(lastUpdatedTimeString);

        Task task = new Task(id, description, status, createdAt, updatedAt);
        return task;
    }

    public String extractJsonObjectValue(String jsonObject, String key){
        Pattern pattern = Pattern.compile("\"" + key + "\":\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(jsonObject);

        if(matcher.find()){
            String value = matcher.group(1);
            return value;
        } else {
            return "";
        }
    }

    public ArrayList<Task> getTaskList(){
        return this.taskList;
    }
}
