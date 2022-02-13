package com.gaojy.rice.common.entity;
import java.util.Date;

/**
 * rice_task_info  generated at 2022-01-25 02:09:24 by: gaojy
 */

public class RiceTaskInfo{
	private long id;
	private long appId;
	private String taskCode;
	private String taskName;
	private String taskDesc;
	private int taskType;
	private String parameters;
	private String schedulerServer;
	private String scheduleType;
	private String timeExpression;
	private String executeType;
	private int threads;
	private int taskRetryCount;
	private int instanceRetryCount;
	private Date nextTriggerTime;
	private Date createTime;
	private Date updateTime;
	private int status;

	public void setId(long id){
		this.id=id;
	}

	public long getId(){
		return id;
	}

	public void setAppId(long appId){
		this.appId=appId;
	}

	public long getAppId(){
		return appId;
	}

	public void setTaskCode(String taskCode){
		this.taskCode=taskCode;
	}

	public String getTaskCode(){
		return taskCode;
	}

	public void setTaskName(String taskName){
		this.taskName=taskName;
	}

	public String getTaskName(){
		return taskName;
	}

	public void setTaskDesc(String taskDesc){
		this.taskDesc=taskDesc;
	}

	public String getTaskDesc(){
		return taskDesc;
	}

	public void setTaskType(int taskType){
		this.taskType=taskType;
	}

	public int getTaskType(){
		return taskType;
	}

	public void setParameters(String parameters){
		this.parameters=parameters;
	}

	public String getParameters(){
		return parameters;
	}

	public void setSchedulerServer(String schedulerServer){
		this.schedulerServer=schedulerServer;
	}

	public String getSchedulerServer(){
		return schedulerServer;
	}

	public void setScheduleType(String scheduleType){
		this.scheduleType=scheduleType;
	}

	public String getScheduleType(){
		return scheduleType;
	}

	public void setTimeExpression(String timeExpression){
		this.timeExpression=timeExpression;
	}

	public String getTimeExpression(){
		return timeExpression;
	}

	public void setExecuteType(String executeType){
		this.executeType=executeType;
	}

	public String getExecuteType(){
		return executeType;
	}

	public void setThreads(int threads){
		this.threads=threads;
	}

	public int getThreads(){
		return threads;
	}

	public void setTaskRetryCount(int taskRetryCount){
		this.taskRetryCount=taskRetryCount;
	}

	public int getTaskRetryCount(){
		return taskRetryCount;
	}

	public void setInstanceRetryCount(int instanceRetryCount){
		this.instanceRetryCount=instanceRetryCount;
	}

	public int getInstanceRetryCount(){
		return instanceRetryCount;
	}

	public void setNextTriggerTime(Date nextTriggerTime){
		this.nextTriggerTime=nextTriggerTime;
	}

	public Date getNextTriggerTime(){
		return nextTriggerTime;
	}

	public void setCreateTime(Date createTime){
		this.createTime=createTime;
	}

	public Date getCreateTime(){
		return createTime;
	}

	public void setUpdateTime(Date updateTime){
		this.updateTime=updateTime;
	}

	public Date getUpdateTime(){
		return updateTime;
	}

	public void setStatus(int status){
		this.status=status;
	}

	public int getStatus(){
		return status;
	}

}
