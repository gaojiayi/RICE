package com.gaojy.rice.repository.api.entity;
import java.util.Date;

/**
 * task_instance_info  generated at 2022-01-25 02:09:24 by: gaojy
 */

public class TaskInstanceInfo{
	private long id;
	private String taskCode;
	private String instanceParams;
	private int parentInstanceId;
	private Date actualTriggerTime;
	private Date expectedTriggerTime;
	private int runningTimes;
	private String taskTrackerAddress;
	private String type;
	private String result;
	private Date finishedTime;
	private Date createTime;
	private int status;

	public void setId(long id){
		this.id=id;
	}

	public long getId(){
		return id;
	}

	public void setTaskCode(String taskCode){
		this.taskCode=taskCode;
	}

	public String getTaskCode(){
		return taskCode;
	}

	public void setInstanceParams(String instanceParams){
		this.instanceParams=instanceParams;
	}

	public String getInstanceParams(){
		return instanceParams;
	}

	public void setParentInstanceId(int parentInstanceId){
		this.parentInstanceId=parentInstanceId;
	}

	public int getParentInstanceId(){
		return parentInstanceId;
	}

	public void setActualTriggerTime(Date actualTriggerTime){
		this.actualTriggerTime=actualTriggerTime;
	}

	public Date getActualTriggerTime(){
		return actualTriggerTime;
	}

	public void setExpectedTriggerTime(Date expectedTriggerTime){
		this.expectedTriggerTime=expectedTriggerTime;
	}

	public Date getExpectedTriggerTime(){
		return expectedTriggerTime;
	}

	public void setRunningTimes(int runningTimes){
		this.runningTimes=runningTimes;
	}

	public int getRunningTimes(){
		return runningTimes;
	}

	public void setTaskTrackerAddress(String taskTrackerAddress){
		this.taskTrackerAddress=taskTrackerAddress;
	}

	public String getTaskTrackerAddress(){
		return taskTrackerAddress;
	}

	public void setType(String type){
		this.type=type;
	}

	public String getType(){
		return type;
	}

	public void setResult(String result){
		this.result=result;
	}

	public String getResult(){
		return result;
	}

	public void setFinishedTime(Date finishedTime){
		this.finishedTime=finishedTime;
	}

	public Date getFinishedTime(){
		return finishedTime;
	}

	public void setCreateTime(Date createTime){
		this.createTime=createTime;
	}

	public Date getCreateTime(){
		return createTime;
	}

	public void setStatus(int status){
		this.status=status;
	}

	public int getStatus(){
		return status;
	}

}
