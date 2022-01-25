package com.gaojy.rice.repository.api.entity;
import java.util.Date;

/**
 * task_change_record  generated at 2022-01-25 02:09:24 by: gaojy
 */

public class TaskChangeRecord{
	private long id;
	private String taskCode;
	private int optType;
	private String schedulerServer;
	private Date createTime;

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

	public void setOptType(int optType){
		this.optType=optType;
	}

	public int getOptType(){
		return optType;
	}

	public void setSchedulerServer(String schedulerServer){
		this.schedulerServer=schedulerServer;
	}

	public String getSchedulerServer(){
		return schedulerServer;
	}

	public void setCreateTime(Date createTime){
		this.createTime=createTime;
	}

	public Date getCreateTime(){
		return createTime;
	}

}
