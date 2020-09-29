package com.css.app.db.business.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.css.addbase.msg.MSGTipDefined;
import com.css.addbase.msg.MsgTipUtil;
import com.css.addbase.msg.entity.MsgTip;
import com.css.addbase.msg.service.MsgTipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.css.addbase.apporgan.entity.BaseAppUser;
import com.css.addbase.apporgan.service.BaseAppUserService;
import com.css.app.db.business.entity.ApprovalOpinion;
import com.css.app.db.business.entity.DocXbIdea;
import com.css.app.db.business.entity.DocXbInfo;
import com.css.app.db.business.entity.DocumentBjjl;
import com.css.app.db.business.entity.DocumentInfo;
import com.css.app.db.business.entity.DocumentZbjl;
import com.css.app.db.business.entity.ReplyAttac;
import com.css.app.db.business.entity.ReplyExplain;
import com.css.app.db.business.entity.SubDocInfo;
import com.css.app.db.business.entity.SubDocTracking;
import com.css.app.db.business.service.ApprovalOpinionService;
import com.css.app.db.business.service.DocXbIdeaService;
import com.css.app.db.business.service.DocXbInfoService;
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
	@Autowired
	private BaseAppUserService baseAppUserService;
	@Autowired
	private DocXbInfoService docXbInfoService;
	@Autowired
	private DocXbIdeaService docXbIdeaService;
	@Autowired
	private MsgTipService msgService;
	@Autowired
	private MsgTipUtil msgUtil;
	@Value("${csse.dccb.appId}")
	private String appId;
	@Value("${csse.dccb.appSecret}")
	private String clientSecret;

	/**
	 * 在局内待办菜单内增加局管理员超级撤回功能
	 * 
	 * @param infoId 主文件id 
	 * @param subId 分支主鍵ID
	 * 局管理员撤回到初始状态
	 */
	@RequestMapping("/juAdministratorWithdraw")
	@ResponseBody
	public void juAdministratorWithdraw(String subId, String infoId,String dealUserId) {
		JSONObject json=new JSONObject();
		logger.info("subId:{}, infoId:{}",subId,infoId);
		//執行撤回操作
		juAdministratorTransactional(subId, infoId, json,dealUserId);
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
		this.juInnnerTransactional(subId, infoId,json);
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
		Integer preStatus = subDocTracking.getPreviousStatus();
		String senderId = subDocTracking.getSenderId();
		//查询当前文的承办人ID  undertakerId
		String undertakerId = subDocInfo.getUndertaker();
		if (StringUtils.equals(userId, senderId) ) {
			//如果状态为待落实  则此文承办人未送出，撤回删除转办记录表和局内流转表的最新一条
			if (StringUtils.equals(subDocTracking.getTrackingType(), "1")) {
				// 删除局内转办最新的一条记录
				DocumentZbjl documentZbjl = documentZbjlService.queryBySubIdAndInfoId(subId, infoId);
				if (documentZbjl == null) {
					this.unifiedDealErrorLog(json, infoId, subId, "转办记录表");
					return json;
				}
				if(StringUtils.isBlank(undertakerId)) {//没有点承办
					//承办人转办撤回,更新状态
					if(StringUtils.isNotBlank(subDocTracking.getUndertaker())) {
						subDocInfo.setUndertaker(subDocTracking.getUndertaker());
						subDocInfo.setUndertakerName(CurrentUser.getUsername());
						BaseAppUser appUser = baseAppUserService.queryObject(CurrentUser.getUserId());
						if(appUser !=null) {
							subDocInfo.setUndertakerPhone(appUser.getTelephone());
						}
					}
					this.unifiedModifyDocStatus(subDocInfo,preStatus);
				}else {
					json.put("result", "deal");
					return json;
				}			
				String id = documentZbjl.getId();
				documentZbjlService.delete(id);
				// 删除局内流转记录表,前提是有数据，然后删除
				subDocTrackingService.delete(subDocTracking.getId());
				json.put("result", "success");
			}else if (StringUtils.equals(subDocTracking.getTrackingType(), "2")) {
				//支持撤回     当前情况属于承办人/审批人送审批撤回
				if (StringUtils.equals(userId, undertakerId)) {
					Map<String, Object> map = new HashMap<>();
					map.put("infoId", infoId);
					map.put("subId", subId);
					map.put("showFlag", "0");
					map.put("userId", subDocTracking.getReceiverId());
					List<ReplyExplain> replyExplains = replyExplainService.queryList(map);
					if (replyExplains != null && replyExplains.size() > 0) {
						ReplyExplain explain = replyExplains.get(0);
						replyExplainService.delete(explain.getId());
					}
					//承办人送审撤回
					subDocInfo.setChooseStatus(preStatus+"");
					this.unifiedModifyDocStatus(subDocInfo, DbDocStatusDefined.BAN_LI_ZHONG);
				}else {
					//删除反馈记录表数据,没有显示给首长看
					Map<String, Object> map = new HashMap<>();
					map.put("infoId", infoId);
					map.put("subId", subId);
					map.put("showFlag", 0);
					map.put("userId", subDocTracking.getReceiverId());
					map.put("time",subDocTracking.getCreatedTime());//确保是当前流转中写的反馈
					List<ReplyExplain> replyExplains = replyExplainService.queryList(map);
					if (replyExplains != null && replyExplains.size() > 0) {
						replyExplainService.delete(replyExplains.get(0).getId());
					}
					Map<String, Object> map1 = new HashMap<>();
					map1.put("subId", subId);
					map1.put("showFlag", 0);
					map1.put("userId", userId);
					map1.put("sort", "desc");
					List<ApprovalOpinion> approvalOpinions = approvalOpinionService.queryList(map1);
					if (approvalOpinions != null && approvalOpinions.size() > 0) {
						//删除审批意见表数据
						approvalOpinionService.delete(approvalOpinions.get(0).getId());
					}
					//审批人送审批
					subDocInfo.setChooseStatus(preStatus+"");
					this.unifiedModifyDocStatus(subDocInfo, DbDocStatusDefined.DAI_SHEN_PI);
				}
				// 删除局内流转记录表,前提是有数据，然后删除
				subDocTrackingService.delete(subDocTracking.getId());
				json.put("result", "success");
			}else {
				json.put("result", "deal");
			}
		}else {
			json.put("result", "deal");
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
			logger.info("根据subId：{}，infoId：{}查不到{}的记录！", subId, infoId,tableName);
		}else {
			logger.info("根据subId：{}，infoId：{}查不到{}的记录！", subId, infoId, tableName);
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
			subDocInfoService.updateSubDocInfoById(subDocInfo);
		}
	}
	@Transactional(rollbackFor = Exception.class)
	private JSONObject juAdministratorTransactional(String subId, String infoId, JSONObject json,String dealUserId) {
		/**
		 * 管理员超级撤回需要删除后续所有操作记录，恢复局内待办状态
		 * 1.首先更新主分支主记录表的文本处理状态dou_status = 1
		 * 2.根据sub_id和info_id删除转办记录表数据；
		 * 3.如果局内流转记录有数据，则根据subId全部删除
		 * 4.如果审批记录表有数据，则根据subId全部删除 
		 * 5.如果办结记录表有数据，则根据subId全部删除 
		 * 6.如果办理反馈表、反馈附件记录表有数据，则删除；
		 */
		if(StringUtils.isNotBlank(infoId) && StringUtils.isNotBlank(subId)) {
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
		}else {
			json.put("result", "fail");
			return json;
		}
		// 更新状态   恢复数据
		SubDocInfo subDocInfo = subDocInfoService.querySubDocInfoBySubIdAndInfoId(subId, infoId);
		if (subDocInfo != null) {
			subDocInfo.setDocStatus(1);
			subDocInfo.setUndertaker("");
			subDocInfo.setUpdateTime(null);
			subDocInfo.setUndertakerName("");
			subDocInfo.setUndertakerPhone("");
			subDocInfo.setChooseStatus("");
			//意见记录清空
			subDocInfo.setIdeaCount(0);
			//清空意见记录
			subDocInfo.setIdeaAddFlag(0);
			subDocInfoService.update(subDocInfo);
		} else {
			this.unifiedDealErrorLog(json, infoId, subId, "分支主记录表");
			json.put("result", "fail");
			return json;
		}
		//督办基本信息表 恢复数据
		DocumentInfo documentInfo = documentInfoService.queryObject(infoId);
		if (documentInfo != null) {
			documentInfo.setSzReadIds(null);
			documentInfo.setStatus(1);
			if(StringUtils.equals(subDocInfo.getSubDeptName(), documentInfo.getLatestSubDept())) {
				Map<String, Object> replyMap =new HashMap<>();
				replyMap.put("infoId", infoId);
				replyMap.put("cbrFlag", "1");
				replyMap.put("showFlag", "1");
				List<ReplyExplain> list = replyExplainService.queryList(replyMap);
				if(list !=null && list.size()>0) {
					ReplyExplain explain = list.get(0);
					documentInfo.setLatestUndertaker(explain.getUserName());
					Map<String, Object> repMap =new HashMap<>();
					repMap.put("infoId", infoId);
					repMap.put("subId", explain.getSubId());
					repMap.put("showFlag", "1");
					List<ReplyExplain> repList = replyExplainService.queryList(repMap);
					if(repList != null && repList.size()>0) {
						ReplyExplain repExplain = repList.get(0);
						documentInfo.setLatestReply(repExplain.getReplyContent());
						documentInfo.setLatestSubDept(repExplain.getSubDeptName());
						documentInfo.setLatestReplyTime(repExplain.getVersionTime());
					}
				}else {
					documentInfo.setLatestReply(null);
					documentInfo.setLatestSubDept(null);
					documentInfo.setLatestUndertaker(null);
					documentInfo.setLatestReplyTime(null);
				}
			}
			documentInfoService.updateDocumentInfoById(documentInfo);
		}else {
			this.unifiedDealErrorLog(json, infoId, subId, "督办基本信息表");
			json.put("result", "fail");
			return json;
		}
		//删除对应协办人记录
		Map<String, Object> map =new HashMap<>();
		map.put("infoId", infoId);
		map.put("subId", subId);
		List<DocXbInfo> docXbInfos = docXbInfoService.queryList(map);
		if (docXbInfos != null && docXbInfos.size() > 0) {
			for (DocXbInfo docXbInfo : docXbInfos) {
				docXbInfoService.delete(docXbInfo.getId());
			}
		}
		List<DocXbIdea> docXbIdeas = docXbIdeaService.queryList(map);
		if (docXbIdeas != null && docXbIdeas.size() > 0) {
			for (DocXbIdea docXbIdea : docXbIdeas) {
				docXbIdeaService.delete(docXbIdea.getId());
			}
		}
		String userId = CurrentUser.getUserId();
		MsgTip msg = msgService.queryObject(MSGTipDefined.DCCB_SHENPIWANCHENG_MSG_TITLE);
		if (msg != null) {
			String msgUrl = msg.getMsgRedirect() + "&fileId=" + infoId + "&subId=" + subId;
			if (StringUtils.isNotBlank(userId)) {
				//给自己发空消息，只为触发角标更新
				msgUtil.sendMsgUnvisible(msg.getMsgTitle(), msg.getMsgContent(), msgUrl, userId, appId, clientSecret,
						msg.getGroupName(), msg.getGroupRedirect(), "", "true");

				//给被撤回人发空消息，只为触发角标更新
				msgUtil.sendMsgUnvisible(msg.getMsgTitle(), msg.getMsgContent(), msgUrl, dealUserId, appId, clientSecret,
						msg.getGroupName(), msg.getGroupRedirect(), "", "true");
			}
		}
		json.put("result", "success");
		return json;
	}
}
