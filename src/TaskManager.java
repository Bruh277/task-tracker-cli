import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.time.LocalDateTime;
import java.util.Scanner;

public class TaskManager {
    private final Path filePath;
    private ArrayList<Task> taskList;
    private Scanner scanner;

    public TaskManager(String fileName) throws IOException {
        this.filePath = Paths.get(fileName);
        this.taskList = new ArrayList<>();
        initializeFile();
        loadTasksFromJson();
        Scanner scanner = new Scanner(System.in);
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
        int id = Integer.parseInt(extractJsonObjectValue(jsonObject, "id"));
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

    //There's no need to specify the filePath, since the filePath never changes from the initialized one.
    public void saveTasksToJson() throws IOException {
        StringBuilder jsonFormattedTaskList = new StringBuilder();

        jsonFormattedTaskList.append("[\n");

        int i = 0;
        while(i < this.taskList.size()){

            String stringifiedTask = this.taskList.get(i).convertTaskToString();
            jsonFormattedTaskList.append(stringifiedTask);

            if(i < this.taskList.size()-1){
                jsonFormattedTaskList.append(",\n");
            } else {
                jsonFormattedTaskList.append("\n");
            }

            i++;
        }

        jsonFormattedTaskList.append("]");

        Files.writeString(this.filePath, jsonFormattedTaskList.toString());
    }

    public void addTask(){
        System.out.println("Please provide the Description of the Task.");
        String taskDescription = scanner.nextLine();

        System.out.println("Optional: Please provide the Status of the Task. An empty input will skip this part.");
        String userInput = scanner.nextLine();
        Task newTask;

        if(userInput.isEmpty()){
            newTask = new Task(this.generateNextId(), taskDescription);
        } else{
            TaskStatus taskStatus = TaskStatus.parseTaskStatusFromInputString(userInput);
            newTask = new Task(this.generateNextId(),taskDescription, taskStatus);
        }

        this.taskList.add(newTask);
    }

    private int generateNextId(){
        int currentMaxId = 0;

        for(Task task : this.taskList){
            if(task.getId() > currentMaxId){
                currentMaxId = task.getId();
            }
        }

        return currentMaxId + 1;
    }

    public ArrayList<Task> getTaskList(){
        return this.taskList;
    }
}
