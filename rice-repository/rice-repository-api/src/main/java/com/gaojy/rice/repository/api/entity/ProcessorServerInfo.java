package com.gaojy.rice.repository.api.entity;
import java.util.Date;

/**
 * processor_server_info  generated at 2022-01-25 02:09:24 by: gaojy
 */

public class ProcessorServerInfo{
	private long id;
	private String address;
	private int port;
	private String taskCode;
	private String version;
	private Date latestActiveTime;
	private Date createTime;
	private int status;

	public void setId(long id){
		this.id=id;
	}

	public long getId(){
		return id;
	}

	public void setAddress(String address){
		this.address=address;
	}

	public String getAddress(){
		return address;
	}

	public void setPort(int port){
		this.port=port;
	}

	public int getPort(){
		return port;
	}

	public void setTaskCode(String taskCode){
		this.taskCode=taskCode;
	}

	public String getTaskCode(){
		return taskCode;
	}

	public void setVersion(String version){
		this.version=version;
	}

	public String getVersion(){
		return version;
	}

	public void setLatestActiveTime(Date latestActiveTime){
		this.latestActiveTime=latestActiveTime;
	}

	public Date getLatestActiveTime(){
		return latestActiveTime;
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
