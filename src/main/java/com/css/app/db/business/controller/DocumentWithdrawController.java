package com.css.app.db.business.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.css.app.db.business.entity.ApprovalOpinion;
import com.css.app.db.business.entity.DocumentBjjl;
import com.css.app.db.business.entity.DocumentInfo;
import com.css.app.db.business.entity.DocumentZbjl;
import com.css.app.db.business.entity.ReplyAttac;
import com.css.app.db.business.entity.ReplyExplain;
import com.css.app.db.business.entity.SubDocInfo;
import com.css.app.db.business.entity.SubDocTracking;
import com.css.app.db.business.service.ApprovalOpinionService;
import com.css.app.db.business.service.DocumentBjjlService;
import com.css.app.db.business.service.DocumentInfoService;
import com.css.app.db.business.service.DocumentZbjlService;
import com.css.app.db.business.service.ReplyAttacService;
import com.css.app.db.business.service.ReplyExplainService;
import com.css.app.db.business.service.SubDocInfoService;
import com.css.app.db.business.service.SubDocTrackingService;
import com.css.app.db.util.DbDocStatusDefined;
import com.css.base.utils.CurrentUser;
import com.css.base.utils.Response;
import com.css.base.utils.StringUtils;

/**
 * 支持管理员撤回以及未审批撤回
 * 
 * @author 中软信息系统工程有限公司
 * @email
 * @date 2019-06-19 09:09:09
 */
@Controller
@RequestMapping("/app/db/withdraw")
public class DocumentWithdrawController {
	private final Logger logger = LoggerFactory.getLogger(DocumentWithdrawController.class);
	@Autowired
	private SubDocInfoService subDocInfoService;
	@Autowired
	private DocumentZbjlService documentZbjlService;
	@Autowired
	private SubDocTrackingService subDocTrackingService;
	@Autowired
	private DocumentBjjlService documentBjjlService;
	@Autowired
	private ReplyExplainService replyExplainService;
	@Autowired
	private ReplyAttacService replyAttacService;
	@Autowired
	private ApprovalOpinionService approvalOpinionService;
	@Autowired
	private DocumentInfoService documentInfoService;

	/**
	 * 在局内待办菜单内增加局管理员超级撤回功能
	 * 
	 * @param infoId 主文件id 
	 * @param subId 分支主鍵ID
	 * 局管理员撤回到初始状态
	 */
	@RequestMapping("/juAdministratorWithdraw")
	@ResponseBody
	public void juAdministratorWithdraw(String subId, String infoId) {
		JSONObject json=new JSONObject();
		logger.info("subId:{}, infoId:{}",subId,infoId);
		//執行撤回操作
		juAdministratorTransactional(subId, infoId, json);
		Response.json(json);
	}
	
	/**
	 * 局內转办撤回功能
	 * 
	 * @param infoId 主文件id
	 * @param subId 分支主鍵ID
	 * 局管理员撤回到初始状态 
	 */
	@RequestMapping("/juInnnerWithdraw")
	@ResponseBody
	public void juInnnerWithdraw(String subId, String infoId) {
		JSONObject json=new JSONObject();
		//執行撤回操作
		json = this.juInnnerTransactional(subId, infoId,json);
		Response.json(json);
	}
	
	@Transactional(rollbackFor = Exception.class)
	private JSONObject juInnnerTransactional(String subId, String infoId, JSONObject json) {
		/**
		 * 局內撤回：A->B(B沒有转办或者承办(承办中没有送审批或者已送审批还未审批，此时可以撤回))
		 * 1、B没有转办支持撤回 ：取出当前用户ID，然后查询出转办记录表最新一条数据的用户ID对比即可；
		 * 2、B点击承办，且已经将此文发送审批，但是没有审批结果，则支持撤回
		 * 3、不能是局管理员
		 */
		//获取到当前用户ID  针对B用户未点击转办可以撤回；
		String userId = CurrentUser.getUserId();
		DocumentZbjl documentZbjl = documentZbjlService.queryBySubIdAndInfoId(subId, infoId);
		if (documentZbjl == null) {
			this.unifiedDealErrorLog(json, infoId, subId, "转办记录表");
			return json;
		}
		String id = documentZbjl.getId();
		SubDocInfo subDocInfo = subDocInfoService.querySubDocInfoBySubIdAndInfoId(subId, infoId);
		if (subDocInfo == null) {
			this.unifiedDealErrorLog(json, infoId, subId, "分支主记录表");
			return json;
		}
		SubDocTracking subDocTracking = subDocTrackingService.queryLatestRecord(subId);
		if (subDocTracking == null) {
			this.unifiedDealErrorLog(json, null, subId, "局内流转记录表");
			return json;
		}
		String senderId = subDocTracking.getSenderId();
		//查询当前文的承办人ID  undertakerId
		String undertakerId = subDocInfo.getUndertaker();
		if (StringUtils.equals(userId, senderId) ) {
			//如果状态为待落实  则此文承办人未送出，撤回删除转办记录表和局内流转表的最新一条
			if (StringUtils.equals(subDocTracking.getTrackingType(), "1")) {
				//支持撤回
				if (StringUtils.equals(userId, undertakerId)) {
					// 更新状态
					//承办人转办撤回
					this.unifiedModifyDocStatus(subDocInfo, DbDocStatusDefined.DAI_LUO_SHI);
				}
				// 删除局内转办最新的一条记录
				documentZbjlService.delete(id);
				// 删除局内流转记录表,前提是有数据，然后删除
				subDocTrackingService.delete(subDocTracking.getId());
			}else if (StringUtils.equals(subDocTracking.getTrackingType(), "2")) {
				//支持撤回     当前情况属于承办人/审批人送审批撤回
				if (StringUtils.equals(userId, undertakerId)) {
					//承办人送审撤回
					this.unifiedModifyDocStatus(subDocInfo, DbDocStatusDefined.BAN_LI_ZHONG);
					String delTeamId="";
					Map<String, Object> map = new HashMap<>();
					map.put("infoId", infoId);
					map.put("subId", subId);
					map.put("showFlag", "0");
					map.put("cbrFlag", "1");
					map.put("userId", userId);
					List<ReplyExplain> replyExplains = replyExplainService.queryList(map);
					if (replyExplains != null && replyExplains.size() > 0) {
						ReplyExplain explain = replyExplains.get(0);
						delTeamId=explain.getTeamId();
						replyExplainService.delete(explain.getId());
					}
					//删除带附件的反馈数据
					replyAttacService.deleteBySubIdAndTeamId(subId,delTeamId);
				}else {
					//审批人送审批
					this.unifiedModifyDocStatus(subDocInfo, DbDocStatusDefined.DAI_SHEN_PI);
					//删除反馈记录表数据,没有显示给首长看
					Map<String, Object> map = new HashMap<>();
					map.put("infoId", infoId);
					map.put("subId", subId);
					map.put("showFlag", 0);
					map.put("userId", userId);
					List<ReplyExplain> replyExplains = replyExplainService.queryList(map);
					if (replyExplains != null && replyExplains.size() > 0) {
						replyExplainService.delete(replyExplains.get(0).getId());
					}
//					replyAttacService.queryList(map);
					Map<String, Object> map1 = new HashMap<>();
					map.put("subId", subId);
					map.put("showFlag", 0);
					map.put("userId", userId);
					List<ApprovalOpinion> approvalOpinions = approvalOpinionService.queryList(map1);
					if (approvalOpinions != null && approvalOpinions.size() > 0) {
						//删除审批意见表数据
						approvalOpinionService.delete(approvalOpinions.get(0).getId());
					}
				}
				// 删除局内流转记录表,前提是有数据，然后删除
				subDocTrackingService.delete(subDocTracking.getId());
				json.put("result", "success");
			}
		}
		return json;
	}
	/**
	 * 数据异常统一失败处理
	 * @param json
	 * @param infoId
	 * @param subId
	 * @param tableName
	 */
	private void unifiedDealErrorLog(JSONObject json, String infoId, String subId, String tableName) {
		if (StringUtils.isBlank(infoId) && !StringUtils.isBlank(subId)) {
			logger.info("根据subId：{}查不到{}的记录！", subId, tableName);
		}else if (!StringUtils.isBlank(infoId) && !StringUtils.isBlank(subId)) {
			logger.info("根据subId：{}，infoId：{}查不到"+tableName+"的记录！", subId, infoId);
		}else {
			logger.info("根据subId：{}，infoId：{}查不到"+tableName+"的记录！", subId, infoId);
		}
		json.put("result", "fail");
	}
	/**
	 * 撤回后，统一修改subDocInfo的DocStatus
	 * @param subDocInfo
	 */
	private void unifiedModifyDocStatus(SubDocInfo subDocInfo, Integer docStatus) {
		if (subDocInfo != null) {
			subDocInfo.setDocStatus(docStatus);
	//		subDocInfo.setUndertaker(null);
	//		subDocInfo.setUpdateTime(null);
	//		subDocInfo.setUndertakerName(null);
	//		subDocInfo.setUndertakerPhone(null);
	//		subDocInfo.setChooseStatus(null);
			subDocInfoService.updateSubDocInfoById(subDocInfo);
		}
	}
	@Transactional(rollbackFor = Exception.class)
	private JSONObject juAdministratorTransactional(String subId, String infoId, JSONObject json) {
		/**
		 * 管理员超级撤回需要删除后续所有操作记录，恢复局内待办状态
		 * 1.首先更新主分支主记录表的文本处理状态dou_status = 1
		 * 2.根据sub_id和info_id删除转办记录表数据；
		 * 3.如果局内流转记录有数据，则根据subId全部删除
		 * 4.如果审批记录表有数据，则根据subId全部删除 
		 * 5.如果办结记录表有数据，则根据subId全部删除 6.如果办理反馈表、反馈附件记录表有数据，则删除；
		 */
		//督办基本信息表 恢复数据
		DocumentInfo documentInfo = documentInfoService.queryObject(infoId);
		if (documentInfo != null) {
			documentInfo.setSzReadIds(null);
			documentInfo.setStatus(1);
			documentInfo.setLatestReply(null);
			documentInfo.setLatestSubDept(null);
			documentInfo.setLatestUndertaker(null);
			documentInfo.setLatestReplyTime(null);
			documentInfoService.updateDocumentInfoById(documentInfo);
		}else {
			this.unifiedDealErrorLog(json, infoId, subId, "督办基本信息表");
			json.put("result", "fail");
			return json;
		}
		// 更新状态   恢复数据
		SubDocInfo subDocInfo = subDocInfoService.querySubDocInfoBySubIdAndInfoId(subId, infoId);
		if (subDocInfo != null) {
			subDocInfo.setDocStatus(1);
			subDocInfo.setUndertaker("");
			subDocInfo.setUpdateTime(null);
			subDocInfo.setUndertakerName(null);
			subDocInfo.setUndertakerPhone(null);
			subDocInfo.setChooseStatus(null);
			subDocInfoService.updateSubDocInfoById(subDocInfo);
		} else {
			this.unifiedDealErrorLog(json, infoId, subId, "分支主记录表");
			json.put("result", "fail");
			return json;
		}
		// 删除局内转办记录
		documentZbjlService.deleteBySubIdAndInfoId(subId, infoId);
		// 删除局内流转记录表,前提是有数据，然后删除
		SubDocTracking subDocTracking = subDocTrackingService.queryLatestRecord(subId);
		if (subDocTracking != null) {
			subDocTrackingService.deleteBySubId(subId);
		}else {
			this.unifiedDealErrorLog(json, null, subId, "局内流转记录表");
			json.put("result", "fail");
			return json;
		}
		// 删除办结记录 ,前提是有数据，然后删除
		DocumentBjjl documentBjjl = documentBjjlService.queryBjjlBySubId(subId);
		if (documentBjjl != null) {
			documentBjjlService.delete(documentBjjl.getId());
		}
		List<ReplyExplain> replyExplains = replyExplainService.querySubLatestReply(infoId, subId);
		Map<String, Object> map = new HashMap<>();
		map.put("subId", subId);
		if (replyExplains != null && replyExplains.size() > 0) {
			replyExplainService.deleteByParam(map);
		}
		// 删除办理反馈附件表
		List<ReplyAttac> replyAttacs = replyAttacService.queryList(map);
		if (replyAttacs != null && replyAttacs.size() > 0) {
			replyAttacService.deleteBySubId(subId);
		}
		//刪除审批意见表数据
		List<ApprovalOpinion> approvalOpinions = approvalOpinionService.queryList(map);
		if (approvalOpinions != null && approvalOpinions.size() > 0) {
			approvalOpinionService.deleteBySubId(subId);
		}
		json.put("result", "success");
		return json;
	}
}
