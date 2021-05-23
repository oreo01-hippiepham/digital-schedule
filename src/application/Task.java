package application;

import java.util.ArrayList;

public class Task {
    private int taskID;
    private String taskName;
    private String taskType;
    private String startTime;
    private String endTime;
    private String expressTime;
    private String queryID;
    private String hashID;
    public static int numTask = 0;


    public Task (String taskName, String taskType, String startTime, String endTime, String queryID, String hashID)
    {
        numTask++;
        this.taskID = numTask;
        this.taskName = taskName;
        this.taskType = taskType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.expressTime = this.startTime + " - " + this.endTime;
        this.queryID = queryID;
        this.hashID = hashID;
    }

    public int getTaskID()
    {
        return taskID;
    }

    public String getTaskName()
    {
        return this.taskName;
    }

    public String getTaskType()
    {
        return this.taskType;
    }

    public String getStartTime()
    {
        return this.startTime;
    }

    public String getEndTime()
    {
        return this.endTime;
    }

    public String getExpressTime()
    {
        return this.expressTime;
    }

    public String getQueryID(){
        return this.queryID;
    }

    public String getHashID()
    {
        return this.hashID;
    }

    public void setTaskName(String taskName)
    {
        this.taskName = taskName;
    }

    public void setTaskType (String taskType)
    {
        this.taskType = taskType;
    }

    public void setTime(String startTime, String endTime)
    {
        this.startTime = startTime;
        this.endTime = endTime;
        this.expressTime = this.startTime + " - " + this.endTime;
    }

}
