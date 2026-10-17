public enum TaskStatus {
    TODO("todo"),
    IN_PROGRESS("in-progress"),
    DONE("done");

    private String taskStatus;

    TaskStatus(String status){
        this.taskStatus = status;
    }

    public static TaskStatus parseTaskStatusFromInputString(String input){
        for(TaskStatus status : TaskStatus.values()){
            if(status.getTaskStatus().equals(input)){
                return status;
            }
        }
        return null;
    }

    public String getTaskStatus(){
        return this.taskStatus;
    }
}
