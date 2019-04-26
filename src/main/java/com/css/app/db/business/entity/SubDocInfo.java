package com.css.app.db.business.entity;

import java.io.Serializable;
import java.util.Date;



/**
 * 各子分支主记录表
 * 
 * @author 中软信息系统工程有限公司
 * @email 
 * @date 2019-04-18 16:40:43
 */
public class SubDocInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//唯一标识
	private String id;
	//主文件id
	private String infoId;
	//分局id
	private String subDeptId;
	//分局名称
	private String subDeptName;
	//第一次转办时间
	private Date createdTime;
	//文件局内状态（1:待转办；3：退回修改；5：待落实；7：待审批；9：办理中；10：建议办结；11：办结；12：常态落实）
	private Integer docStatus;
	//承办人id
	private String undertaker;
	//完成时间
	private String finishTime;
	//承办人
	private String undertakerName;
/*----------------------------以下字段只用来列表接收值用--------------start----------------------*/	
	//文件局内状态
	private String statusName;
	//文件标题
	private String docTitle;
	//密级
	private String securityClassification;
	//紧急程度
	private String urgencyDegree;
	//文件号
	private String docCode;
	//军委办件号
	private String banjianNumber;
	//催办标识
	private String cuibanFlag;
	//文件类型
	private String docTypeName;
	/*----------------------------列表接收值---------end---------------------------*/	
	/**
	 * 设置：唯一标识
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * 获取：唯一标识
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置：主文件id
	 */
	public void setInfoId(String infoId) {
		this.infoId = infoId;
	}
	/**
	 * 获取：主文件id
	 */
	public String getInfoId() {
		return infoId;
	}
	/**
	 * 设置：分局id
	 */
	public void setSubDeptId(String subDeptId) {
		this.subDeptId = subDeptId;
	}
	/**
	 * 获取：分局id
	 */
	public String getSubDeptId() {
		return subDeptId;
	}
	/**
	 * 设置：分局名称
	 */
	public void setSubDeptName(String subDeptName) {
		this.subDeptName = subDeptName;
	}
	/**
	 * 获取：分局名称
	 */
	public String getSubDeptName() {
		return subDeptName;
	}
	/**
	 * 设置：带一次转办时间
	 */
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	/**
	 * 获取：带一次转办时间
	 */
	public Date getCreatedTime() {
		return createdTime;
	}
	/**
	 * 设置：文件局内状态（1：退回修改；2：待落实；3：待审批；4：办理中；5：建议办结）
	 */
	public void setDocStatus(Integer docStatus) {
		this.docStatus = docStatus;
	}
	/**
	 * 获取：文件局内状态（1：退回修改；2：待落实；3：待审批；4：办理中；5：建议办结）
	 */
	public Integer getDocStatus() {
		return docStatus;
	}
	public String getUndertaker() {
		return undertaker;
	}
	public void setUndertaker(String undertaker) {
		this.undertaker = undertaker;
	}
	public String getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(String finishTime) {
		this.finishTime = finishTime;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public String getDocTitle() {
		return docTitle;
	}
	public void setDocTitle(String docTitle) {
		this.docTitle = docTitle;
	}
	public String getSecurityClassification() {
		return securityClassification;
	}
	public void setSecurityClassification(String securityClassification) {
		this.securityClassification = securityClassification;
	}
	public String getUrgencyDegree() {
		return urgencyDegree;
	}
	public void setUrgencyDegree(String urgencyDegree) {
		this.urgencyDegree = urgencyDegree;
	}
	public String getDocCode() {
		return docCode;
	}
	public void setDocCode(String docCode) {
		this.docCode = docCode;
	}
	public String getBanjianNumber() {
		return banjianNumber;
	}
	public void setBanjianNumber(String banjianNumber) {
		this.banjianNumber = banjianNumber;
	}
	public String getCuibanFlag() {
		return cuibanFlag;
	}
	public void setCuibanFlag(String cuibanFlag) {
		this.cuibanFlag = cuibanFlag;
	}
	public String getDocTypeName() {
		return docTypeName;
	}
	public void setDocTypeName(String docTypeName) {
		this.docTypeName = docTypeName;
	}
	public String getUndertakerName() {
		return undertakerName;
	}
	public void setUndertakerName(String undertakerName) {
		this.undertakerName = undertakerName;
	}
	
	
}
