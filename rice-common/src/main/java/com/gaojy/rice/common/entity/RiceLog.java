package com.gaojy.rice.common.entity;

/**
 * rice_log  generated at 2022-08-14 12:48:08 by: gaojy
 */

public class RiceLog{
	private long taskInstanceId;
	private String processorAddr;
	private String schedulerAddr;
	private String message;

	public void setTaskInstanceId(long taskInstanceId){
		this.taskInstanceId=taskInstanceId;
	}

	public long getTaskInstanceId(){
		return taskInstanceId;
	}

	public void setProcessorAddr(String processorAddr){
		this.processorAddr=processorAddr;
	}

	public String getProcessorAddr(){
		return processorAddr;
	}

	public void setSchedulerAddr(String schedulerAddr){
		this.schedulerAddr=schedulerAddr;
	}

	public String getSchedulerAddr(){
		return schedulerAddr;
	}

	public void setMessage(String message){
		this.message=message;
	}

	public String getMessage(){
		return message;
	}

}
