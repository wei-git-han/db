package com.css.app.db.business.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;



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
	//文件局内状态（1:待转办；3：退回修改；5：待落实；7：待审批；9：办理中；10：建议办结 ;11：建议落实;12：办结；13:常态落实）
	private Integer docStatus;
	//承办人id
	private String undertaker;
	//更新时间（最近一次呈送、退回、审批完成、建议办结、常态落实时间）
	private Date updateTime;
	//承办人
	private String undertakerName;
	//承办人电话
	private String undertakerPhone;
	//承办人提交选择的状态（1:办理中；2：办结；3：常态落实；）
	private String chooseStatus;
	//当前局是否正式反馈过
	private Integer firstReply;

/*----------------------------以下字段只用来列表接收值用--------------start----------------------*/	
	private String dealUserName;
	private String dealUserId;
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
	//最新反馈
	private String latestReply;
	//最新反馈单位
	private String latestSubDept;
	//最新反馈的承办人
	private String latestUndertaker;
	//最新反馈审批完成时间
	private Date latestReplyTime;
	//工作分工内容
	private String jobContent;
	//最新的首长名字
//	private String leaderName;
	//最新的首长批示
//	private String leaderContent;
	//最新的首长批示时间
	private String leaderTime;
	//我是不是当前处理人(1.文在我这2.文在承办人我为协办人)
	private Integer receiverIsMe;
	//承办单位/人
	private String underDepts;
	//已更新标识
	private String updateFlag;
	//首长批示
	private List<DocumentSzps> szpslist;
	//是否支持撤回  1-显示撤回按钮
	private Integer withdrawFlag;
	//审批撤回提示标志
	private Integer approveWithdrawFlag;
	//批示时间是否超过3个月  1：超过3月
	private Integer isOverTreeMonth;
	//意见统计条数
	private Integer ideaCount;
	//是否是协办人
	private Integer isXBPerson;
	//是否新增意见 1：新增
	private Integer ideaAddFlag;
	//是否主办人 1：主办人
	private Integer isCBPerson;
	
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
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getDealUserName() {
		return dealUserName;
	}
	public void setDealUserName(String dealUserName) {
		this.dealUserName = dealUserName;
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
	public String getLatestReply() {
		return latestReply;
	}
	public void setLatestReply(String latestReply) {
		this.latestReply = latestReply;
	}
	public String getLatestSubDept() {
		return latestSubDept;
	}
	public void setLatestSubDept(String latestSubDept) {
		this.latestSubDept = latestSubDept;
	}
	public String getLatestUndertaker() {
		return latestUndertaker;
	}
	public void setLatestUndertaker(String latestUndertaker) {
		this.latestUndertaker = latestUndertaker;
	}
	public Date getLatestReplyTime() {
		return latestReplyTime;
	}
	public void setLatestReplyTime(Date latestReplyTime) {
		this.latestReplyTime = latestReplyTime;
	}
	public Integer getReceiverIsMe() {
		return receiverIsMe;
	}
	public void setReceiverIsMe(Integer receiverIsMe) {
		this.receiverIsMe = receiverIsMe;
	}
	public String getUnderDepts() {
		return underDepts;
	}
	public void setUnderDepts(String underDepts) {
		this.underDepts = underDepts;
	}
	public String getUpdateFlag() {
		return updateFlag;
	}
	public void setUpdateFlag(String updateFlag) {
		this.updateFlag = updateFlag;
	}
	public String getUndertakerPhone() {
		return undertakerPhone;
	}
	public void setUndertakerPhone(String undertakerPhone) {
		this.undertakerPhone = undertakerPhone;
	}
	public String getChooseStatus() {
		return chooseStatus;
	}
	public void setChooseStatus(String chooseStatus) {
		this.chooseStatus = chooseStatus;
	}
	public List<DocumentSzps> getSzpslist() {
		return szpslist;
	}
	public void setSzpslist(List<DocumentSzps> szpslist) {
		this.szpslist = szpslist;
	}
	public Integer getWithdrawFlag() {
		return withdrawFlag;
	}
	public void setWithdrawFlag(Integer withdrawFlag) {
		this.withdrawFlag = withdrawFlag;
	}
	public String getLeaderTime() {
		return leaderTime;
	}
	public void setLeaderTime(String leaderTime) {
		this.leaderTime = leaderTime;
	}
	public Integer getApproveWithdrawFlag() {
		return approveWithdrawFlag;
	}
	public void setApproveWithdrawFlag(Integer approveWithdrawFlag) {
		this.approveWithdrawFlag = approveWithdrawFlag;
	}
	public Integer getIsOverTreeMonth() {
		return isOverTreeMonth;
	}
	public void setIsOverTreeMonth(Integer isOverTreeMonth) {
		this.isOverTreeMonth = isOverTreeMonth;
	}
	public Integer getIdeaCount() {
		return ideaCount;
	}
	public void setIdeaCount(Integer ideaCount) {
		this.ideaCount = ideaCount;
	}
	public Integer getIdeaAddFlag() {
		return ideaAddFlag;
	}
	public void setIdeaAddFlag(Integer ideaAddFlag) {
		this.ideaAddFlag = ideaAddFlag;
	}
	public Integer getIsXBPerson() {
		return isXBPerson;
	}
	public void setIsXBPerson(Integer isXBPerson) {
		this.isXBPerson = isXBPerson;
	}
	public Integer getIsCBPerson() {
		return isCBPerson;
	}
	public void setIsCBPerson(Integer isCBPerson) {
		this.isCBPerson = isCBPerson;
	}
	public String getJobContent() {
		return jobContent;
	}
	public void setJobContent(String jobContent) {
		this.jobContent = jobContent;
	}

	public Integer getFirstReply() {
		return firstReply;
	}

	public void setFirstReply(Integer firstReply) {
		this.firstReply = firstReply;
	}

	public String getDealUserId() {
		return dealUserId;
	}

	public void setDealUserId(String dealUserId) {
		this.dealUserId = dealUserId;
	}
}
