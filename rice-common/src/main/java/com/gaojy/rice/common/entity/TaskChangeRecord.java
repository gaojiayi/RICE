package com.gaojy.rice.common.entity;
import java.util.Date;

/**
 * task_change_record  generated at 2022-01-25 02:09:24 by: gaojy
 */

public class TaskChangeRecord{
	private long id;
	private String taskCode;
	private int optType;
	private Date createTime;
	private String optDesc;

	public String getOptDesc() {
		return optDesc;
	}

	public void setOptDesc(String optDesc) {
		this.optDesc = optDesc;
	}

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

	public void setCreateTime(Date createTime){
		this.createTime=createTime;
	}

	public Date getCreateTime(){
		return createTime;
	}

	@Override
	public String toString() {
		return "TaskChangeRecord{" +
			"id=" + id +
			", taskCode='" + taskCode + '\'' +
			", optType=" + optType +
			", createTime=" + createTime +
			'}';
	}
}
