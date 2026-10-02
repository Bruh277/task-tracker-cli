import java.time.LocalDateTime;
import java.util.ArrayList;

public class Task {
    private String id;
    private String description;
    private TaskStatus status;
    private LocalDateTime taskCreationTime;
    private LocalDateTime lastUpdatedTime;

    public Task(String id, String description){
        this.id = id;
        this.description = description;
        this.status = TaskStatus.TODO;
        this.taskCreationTime = LocalDateTime.now();
        this.lastUpdatedTime = LocalDateTime.now();
    }

    public Task(String id, String description, TaskStatus status, LocalDateTime taskCreationTime, LocalDateTime lastUpdatedTime){
        this.id = id;
        this.description = description;
        this.status = status;
        this.taskCreationTime = taskCreationTime;
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public String convertTaskToString(){
        return """
                {
                    "id": "%s",
                    "description": "%s",
                    "status": "%s",
                    "taskCreationTime": "%s",
                    "lastUpdatedTime": "%s"
                }
                """.formatted(this.id, this.description, this.status.name(), this.taskCreationTime, this.lastUpdatedTime);
    }

    public String getId(){
        return this.id;
    }

    public String getDescription(){
        return this.description;
    }

    public TaskStatus getStatus(){
        return this.status;
    }

    public LocalDateTime getTaskCreationTime(){
        return this.taskCreationTime;
    }

    public LocalDateTime getLastUpdatedTime(){
        return this.lastUpdatedTime;
    }


    public void setStatus(TaskStatus newStatus){
        this.status = newStatus;
        this.lastUpdatedTime = LocalDateTime.now();
    }

}
