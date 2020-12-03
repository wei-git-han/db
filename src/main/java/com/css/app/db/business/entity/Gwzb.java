package com.css.app.db.business.entity;

import java.io.Serializable;
import java.util.Date;

public class Gwzb implements Serializable {

	// id
	private String id;
	// 公文名称
	private String filename;
	// 文号
	private String lwzh;
	// 收件人
	private String receiveuser;
	// 密级Name
	private String secretlevelname;
	// 密级
	private String secretlevel;
	// 来文单位
	private String lwdw;
	// 来文时间
	private String receivetime;
	// 类型 0 公文 1 电子保密室
	private String lx;
	// 类型名称 0 公文 1 电子保密室  呈报 0  拟办 1
	private String lxmc;
	// 状态
	private String status;
	
	private String statusname;
	
	private Integer documentstatus;
	
	private String sendleadername;
	
	private String zhmsname;
	private Date modifyDate;
	
	/**
	 * 退回备注数据
	 */
	private String remark;
	private String comtype;
	
	public String getComtype() {
		return comtype;
	}

	public void setComtype(String comtype) {
		this.comtype = comtype;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	private String fileserverformatid;
	//其他状态判断  待拟办/拟办中  时间
	private String state;

	private Integer orderid;//首长排序
	private Integer sort;//单位排序
	private Integer sort2;//收文为1;公文为2(正序排)
	
	//拟稿人  公文
	private String ngrname;
//	//收件人
//	private String sjrname;
	//紧急程度名称
	private String jjcdname;
	
	public Integer getSort2() {
		return sort2;
	}

	public void setSort2(Integer sort2) {
		this.sort2 = sort2;
	}

	public Integer getOrderid() {
		return orderid;
	}

	public void setOrderid(Integer orderid) {
		this.orderid = orderid;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getLwzh() {
		return lwzh;
	}

	public void setLwzh(String lwzh) {
		this.lwzh = lwzh;
	}

	public String getReceiveuser() {
		return receiveuser;
	}

	public void setReceiveuser(String receiveuser) {
		this.receiveuser = receiveuser;
	}

	public String getSecretlevelname() {
		return secretlevelname;
	}

	public void setSecretlevelname(String secretlevelname) {
		this.secretlevelname = secretlevelname;
	}

	public String getSecretlevel() {
		return secretlevel;
	}

	public void setSecretlevel(String secretlevel) {
		this.secretlevel = secretlevel;
	}

	public String getLwdw() {
		return lwdw;
	}

	public void setLwdw(String lwdw) {
		this.lwdw = lwdw;
	}

	public String getReceivetime() {
		return receivetime;
	}

	public void setReceivetime(String receivetime) {
		this.receivetime = receivetime;
	}

	public String getLx() {
		return lx;
	}

	public void setLx(String lx) {
		this.lx = lx;
	}

	public String getLxmc() {
		return lxmc;
	}

	public void setLxmc(String lxmc) {
		this.lxmc = lxmc;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusname() {
		return statusname;
	}

	public void setStatusname(String statusname) {
		this.statusname = statusname;
	}

	public Integer getDocumentstatus() {
		return documentstatus;
	}

	public void setDocumentstatus(Integer documentstatus) {
		this.documentstatus = documentstatus;
	}

	public String getZhmsname() {
		return zhmsname;
	}

	public void setZhmsname(String zhmsname) {
		this.zhmsname = zhmsname;
	}

	public String getSendleadername() {
		return sendleadername;
	}

	public void setSendleadername(String sendleadername) {
		this.sendleadername = sendleadername;
	}

	public String getFileserverformatid() {
		return fileserverformatid;
	}

	public void setFileserverformatid(String fileserverformatid) {
		this.fileserverformatid = fileserverformatid;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getNgrname() {
		return ngrname;
	}

	public void setNgrname(String ngrname) {
		this.ngrname = ngrname;
	}

//	public String getSjrname() {
//		return sjrname;
//	}
//
//	public void setSjrname(String sjrname) {
//		this.sjrname = sjrname;
//	}

	public String getJjcdname() {
		return jjcdname;
	}

	public void setJjcdname(String jjcdname) {
		this.jjcdname = jjcdname;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
