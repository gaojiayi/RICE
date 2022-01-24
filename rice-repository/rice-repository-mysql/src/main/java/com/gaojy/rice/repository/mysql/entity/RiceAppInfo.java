package com.gaojy.rice.repository.mysql.entity;
import java.util.Date;

/**
 * rice_app_info  generated at 2022-01-25 02:09:24 by: gaojy
 */

public class RiceAppInfo{
	private long id;
	private String appName;
	private String appDesc;
	private Date createTime;
	private int status;

	public void setId(long id){
		this.id=id;
	}

	public long getId(){
		return id;
	}

	public void setAppName(String appName){
		this.appName=appName;
	}

	public String getAppName(){
		return appName;
	}

	public void setAppDesc(String appDesc){
		this.appDesc=appDesc;
	}

	public String getAppDesc(){
		return appDesc;
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
