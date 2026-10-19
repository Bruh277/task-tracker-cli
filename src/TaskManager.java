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
        this.scanner = new Scanner(System.in);
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

    public void addTask() throws IOException {
        System.out.println("Please provide the Description of the Task.");
        String taskDescription = scanner.nextLine();


        /*Just like you provided a numeric input for deciding Task Status in the updateTaskStatus method, it would be a good idea
        to do so in this section as well.*/
        System.out.println("Optional: Please provide the Status of the Task. An empty input will skip this part.");
        String userInput = scanner.nextLine();
        Task newTask;

        if(userInput.isEmpty()){
            newTask = new Task(this.generateNextId(), taskDescription);
        } else {
            TaskStatus taskStatus = TaskStatus.parseTaskStatusFromInputString(userInput);
            if(taskStatus == null){
                System.out.println("Incorrect Task Status input");
                return;
            } else{
                newTask = new Task(this.generateNextId(),taskDescription, taskStatus);
            }
        }

        this.taskList.add(newTask);
        this.saveTasksToJson();
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

    //Remember that the methods that depend on this method have to account for null.
    private Task findTaskById(int id){
        for(Task task : this.taskList){
            if(task.getId() == id){
                return task;
            }
        }
        return null;
    }

    public void updateTaskDescription(int id) throws IOException{
        Task taskToBeUpdated = this.findTaskById(id);
        if(taskToBeUpdated == null){
            System.out.println("No such task found");
        } else{
            while(true){
                System.out.println("Please enter the new Task Description:");
                String userInput = scanner.nextLine();

                if(userInput.isEmpty()){
                    System.out.println("Empty input entered.");
                } else{
                    taskToBeUpdated.setDescription(userInput);
                    this.saveTasksToJson();
                    break;
                }
            }
        }
    }

    public void updateTaskStatus(int id) throws IOException{
        Task taskToBeUpdated = this.findTaskById(id);

        if(taskToBeUpdated == null) {
            System.out.println("No such task found");
        } else{
            while(true){
                System.out.println("The following status options are:");
                System.out.println("1: todo");
                System.out.println("2: in-progress");
                System.out.println("3: done");
                System.out.println("Please enter the number corresponding to the Task Status you wish to assign to the Task.");

                //We need to account for the possibility the user might enter something that's not an int.
                //Once the status has been changed with a valid input, return gets us out of the method.
                /*If we didn't do return, we would've been thrown back into the 'while' loop after valid input,
                despite having changed the task status already.*/
                try{
                    int userInput = Integer.valueOf(scanner.nextLine());
                    switch(userInput){
                        case 1:
                            taskToBeUpdated.setStatus(TaskStatus.TODO);
                            this.saveTasksToJson();
                            return;
                        case 2:
                            taskToBeUpdated.setStatus(TaskStatus.IN_PROGRESS);
                            this.saveTasksToJson();
                            return;
                        case 3:
                            taskToBeUpdated.setStatus(TaskStatus.DONE);
                            this.saveTasksToJson();
                            return;
                        default:
                            System.out.println("Invalid number entered. Please enter a number that corresponds to one of the following options.");
                    }

                } catch (NumberFormatException e){
                    System.out.println("Invalid input. Please enter a number.");
                }
            }
        }
    }

    public void deleteTask(int id) throws IOException {
        Task taskToBeDeleted = this.findTaskById(id);

        if(taskToBeDeleted == null){
            System.out.println("No such task found.");
        } else{
            this.taskList.remove(taskToBeDeleted);
            this.saveTasksToJson();
        }
    }

    public ArrayList<Task> getTaskList(){
        return this.taskList;
    }
}
