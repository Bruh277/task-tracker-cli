import java.io.IOException;

public class Main {

    public static void main(String[] args){
        TaskManager taskManager = new TaskManager("tasks.json");

        try {
            taskManager.initializeFile();
        } catch(IOException error){
            error.printStackTrace();
        }
    }
}
