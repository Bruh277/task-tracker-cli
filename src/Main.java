import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            TaskManager taskManager = new TaskManager("tasks.json");
        } catch (IOException error) {
            error.printStackTrace();
        }
    }
}
