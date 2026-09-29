public enum TaskStatus {
    TODO("todo"),
    IN_PROGRESS("in-progress"),
    DONE("done");

    private String taskStatus;

    TaskStatus(String status){
        this.taskStatus = status;
    }

    public String getTaskStatus(){
        return this.taskStatus;
    }
}
