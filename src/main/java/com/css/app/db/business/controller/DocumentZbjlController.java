package com.css.app.db.business.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.css.app.db.business.entity.*;
import com.css.app.db.business.service.*;
import com.css.websocket.WebSocketHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.css.addbase.apporgan.entity.BaseAppOrgan;
import com.css.addbase.apporgan.entity.BaseAppUser;
import com.css.addbase.apporgan.service.BaseAppOrganService;
import com.css.addbase.apporgan.service.BaseAppUserService;
import com.css.addbase.apporgmapped.service.BaseAppOrgMappedService;
import com.css.addbase.msg.MSGTipDefined;
import com.css.addbase.msg.MsgTipUtil;
import com.css.addbase.msg.entity.MsgTip;
import com.css.addbase.msg.service.MsgTipService;
//import com.css.app.db.business.entity.DocXbInfo;
//import com.css.app.db.business.service.DocXbInfoService;
import com.css.app.db.config.service.AdminSetService;
import com.css.app.db.util.DbDocStatusDefined;
import com.css.base.utils.CurrentUser;
import com.css.base.utils.Response;
import com.css.base.utils.StringUtils;
import com.css.base.utils.UUIDUtils;


/**
 * 转办记录表
 * 
 * @author 中软信息系统工程有限公司
 * @email 
 * @date 2019-04-18 16:39:37
 */
@Controller
@RequestMapping("/app/db/documentzbjl")
public class DocumentZbjlController {
	private final Logger logger = LoggerFactory.getLogger(DocumentZbjlController.class);
	@Autowired
	private DocumentZbjlService documentZbjlService;
	@Autowired
	private SubDocInfoService subDocInfoService;
	@Autowired
	private BaseAppOrganService baseAppOrganService;
	@Autowired
	private BaseAppUserService baseAppUserService;
	@Autowired
	private DocumentInfoService documentInfoService;
	@Autowired
	private SubDocTrackingService subDocTrackingService;
	@Autowired
	private BaseAppOrgMappedService baseAppOrgMappedService;
	@Autowired
	private ReplyExplainService replyExplainService;
	@Autowired
	private AdminSetService adminSetService;
	@Autowired
	private MsgTipService msgService;
	@Autowired
	private MsgTipUtil msgUtil;
	@Value("${csse.dccb.appId}")
	private  String appId;	
	@Value("${csse.dccb.appSecret}")
	private  String clientSecret;
	@Autowired
	private DocXbInfoService docXbInfoService;
	@Autowired
	private WebSocketHandle webSocketHandle;
	
	/**
	 * 转办记录
	 * @param infoId 主文件id
	 */
	@ResponseBody
	@RequestMapping("/list")
	public void list(String infoId){
		List<DocumentZbjl> zbjlList = null;
		if(StringUtils.isNotBlank(infoId)) {
			Map<String, Object> map = new HashMap<>();
			map.put("infoId", infoId);
			zbjlList = documentZbjlService.queryList(map);
//			this.addXbRecord(zbjlList, infoId);
		}
		Response.json(zbjlList);
	}
	/**
	 * 主办人添加协办记录在转办记录中
	 * @param zbjlList
	 * @param infoId
	 */
//	private void addXbRecord(List<DocumentZbjl> zbjlList, String infoId) {
//		//查询协办记录，显示在转办记录中
//		List<DocXbInfo> docXbInfos = docXbInfoService.queryXbRecord(infoId);
//		DocumentZbjl documentZbjl = null;
//		if (docXbInfos != null && docXbInfos != null) {
//			for (DocXbInfo docXbInfo : docXbInfos) {
//				documentZbjl = new DocumentZbjl();
//				documentZbjl.setCreatedTime(docXbInfo.getCreatedTime());
//				documentZbjl.setOrgName(docXbInfo.getDeptName());
//				documentZbjl.setUserName(docXbInfo.getUndertakerName());
//				documentZbjl.setReceiverDeptName(docXbInfo.getReceiverDeptName());
//				documentZbjl.setReceiverNames(docXbInfo.getXieBanPersonNames());
//				documentZbjl.setIsAddRecord(1);
//				zbjlList.add(documentZbjl);
//			}
//		}
//	}
	/**
	 * 保存部转办信息
	 * @param infoId 主文件id
	 * @param deptIds 选中部门的ids
	 * @param deptNames 选中部门的名称
	 */
	@ResponseBody
	@RequestMapping("/save")
	public void save(String infoIds,String deptIds,String deptNames){
		JSONObject json =new JSONObject();
		if(StringUtils.isNotBlank(infoIds) && StringUtils.isNotBlank(deptIds)) {
			String organId = baseAppOrgMappedService.getBareauByUserId(CurrentUser.getUserId());
			BaseAppOrgan org = baseAppOrganService.queryObject(organId);
			String[] ids = infoIds.split(",");
			for (String infoId : ids) {
				//时间取一个保证数据的统一
				Date date = new Date();
				//添加转办记录
				DocumentZbjl zbjl=new DocumentZbjl();
				zbjl.setInfoId(infoId);
				zbjl.setReceiverIds(deptIds);
				zbjl.setReceiverNames(deptNames);
				zbjl.setCreatedTime(date);
				zbjl.setOrgName(org.getName());
				documentZbjlService.save(zbjl);
				//第一次转办时间同步到主表
				DocumentInfo info = documentInfoService.queryObject(infoId);
				if(info != null) {
					info.setFirstZbTime(date);
					info.setStatus(1);
					documentInfoService.update(info);
				}
				//添加子分支主记录,文件状态为待转办
				String[] deptArray = deptIds.split(",");
				List<String> subDeptIds = subDocInfoService.queryAllSubDeptIds(infoId);
				for (String deptId : deptArray) {
					if(subDeptIds != null && subDeptIds.size()>0) {
						if(subDeptIds.contains(deptId)) {
							continue;
						}
					}
					BaseAppOrgan organ = baseAppOrganService.queryObject(deptId);
					SubDocInfo subInfo=new SubDocInfo();
					subInfo.setId(UUIDUtils.random());
					subInfo.setInfoId(infoId);
					subInfo.setSubDeptId(deptId);
					if(organ !=null) {
						subInfo.setSubDeptName(organ.getName());
					}
					subInfo.setDocStatus(DbDocStatusDefined.DAI_ZHUAN_BAN);
					subInfo.setCreatedTime(date);
					subDocInfoService.save(subInfo);
					//获取局管理员id
					List<String> userIds = adminSetService.queryUserIdByOrgId(deptId);
					if(userIds !=null && userIds.size()>0) {
						// 发送消息提醒
						MsgTip msg = msgService.queryObject(MSGTipDefined.DCCB_BU_ZHUANBAN_MSG_TITLE);
						if (msg != null) {
							String msgUrl = msg.getMsgRedirect()+"&fileId="+infoId+"&subId="+subInfo.getId();
							for (String userId : userIds) {
								msgUtil.sendMsg(msg.getMsgTitle(), msg.getMsgContent(), msgUrl, userId, appId,clientSecret, msg.getGroupName(), msg.getGroupRedirect(), "","true");
								//subDocInfoService.sendMsgByWebSocket(userId,5,false);
								webSocketHandle.addSendMap(userId,5,false);
							}
						}
					}
				}
			}
			json.put("result", "success");
		}else {
			json.put("result", "fail");
		}		
		Response.json(json);
	}

	/**
	 * 承办人放弃承办身份点击转办，恢复之前协办人身份
	 * @param subId 分局ID
	 */
	private void currUserIsXBPerson(String subId) {
		String userId = CurrentUser.getUserId();
		Map<String, Object> hashMap = new HashMap<>();
		hashMap.put("receiverId", userId);
		hashMap.put("subId", subId);
		hashMap.put("publishFlag", "1");
		List<DocXbInfo> docXbInfos = docXbInfoService.queryList(hashMap);
		if (docXbInfos.size() > 0) {
			DocXbInfo docXbInfo = docXbInfos.get(0);
			docXbInfo.setPublishFlag(0);
			docXbInfoService.update(docXbInfo);
		}
	}
	/**
	 * 局内转办
	 * @param infoId 主记录id
	 * @param subId 分支主id
	 * @param userId 转办接收人id
	 * @param userName 转办接收人
	 */
	@ResponseBody
	@RequestMapping("/subZbSave")
	public void subZbSave(String infoId,String subId,String userId,String userName) {
		JSONObject json=new JSONObject();
		String cbuserId = "";//承办人userId
		//承办人
		if (StringUtils.isNotBlank(subId)) {
			SubDocInfo subDocInfo = subDocInfoService.queryObject(subId);
			if (subDocInfo != null) {
				cbuserId = subDocInfo.getUndertaker();
			}
		}
		//待xxx落实的xxx的userId
		String dlsUserId =  subDocTrackingService.findDealUserName(subId);
		String loginUserId=CurrentUser.getUserId();
		if(StringUtils.isNotBlank(infoId) && StringUtils.isNotBlank(subId)) {
			SubDocInfo subInfo = subDocInfoService.queryObject(subId);
			//添加转办记录
			String deptId = null;
			String deptName = null;
			List<BaseAppUser> list = baseAppUserService.findByUserId(userId);
			if(list !=null && list.size()>0) {
				deptId = list.get(0).getOrganid();
				if(StringUtils.isNotBlank(deptId)) {
					BaseAppOrgan organ = baseAppOrganService.queryObject(deptId);
					deptName=organ.getName();
				}
			}
			DocumentZbjl zbjl=new DocumentZbjl();
			zbjl.setInfoId(infoId);
			zbjl.setReceiverIds(userId);
			zbjl.setReceiverNames(userName);
			zbjl.setReceiverDeptId(deptId);
			zbjl.setReceiverDeptName(deptName);
			zbjl.setOrgName(subInfo.getSubDeptName());
			zbjl.setSubId(subId);
			zbjl.setCreatedTime(new Date());
			documentZbjlService.save(zbjl);
			//添加流转记录
			SubDocTracking tracking = new SubDocTracking();
			String loginUserName=CurrentUser.getUsername();
			String loginUserDeptId=CurrentUser.getDepartmentId();
			String loginUserDeptName=CurrentUser.getOrgName();
			tracking.setSenderId(loginUserId);
			tracking.setSenderName(loginUserName);
			tracking.setSenDeptId(loginUserDeptId);
			tracking.setSenDeptName(loginUserDeptName);
			tracking.setReceiverId(userId);
			tracking.setReceiverName(userName);
			tracking.setRecDeptId(deptId);
			tracking.setRecDeptName(deptName);
			tracking.setSubId(subId);
			tracking.setTrackingType("1");
			tracking.setPreviousStatus(subInfo.getDocStatus());
			tracking.setUndertaker(subInfo.getUndertaker());
			if (subId != null ) {
				//承办人点击转办，清空当前未发布的所有意见以及协办人
				if (StringUtils.equals(loginUserId, subInfo.getUndertaker())) {
					//文在承办人这里，承办人点击转办，局内流转记录最新一笔肯定是trackingType in (1,4),此刻肯定会有新的组ID生成
					SubDocTracking subDocTracking = subDocTrackingService.queryLatestRecord(subId);
					if (subDocTracking != null) {
						//承办人点击转办后将这轮意见组ID带过去
						tracking.setIdeaGroupId(subDocTracking.getIdeaGroupId());
					}
				}
			}
			subDocTrackingService.save(tracking);
			//改变文件状态 ，文件状态为待落实
			if(subInfo != null) {
//				Map<String, Object> map = new HashMap<>();
				subInfo.setDocStatus(DbDocStatusDefined.DAI_LUO_SHI);
				subInfo.setUndertaker("");
				subInfo.setUndertakerName("");
				subInfo.setUndertakerPhone("");
				subDocInfoService.update(subInfo);
//				//点击承办后在转办的时候，清空组意见组ID
//				SubDocTracking subDocTracking = subDocTrackingService.queryLatestRecord(subId);
//				if (subDocTracking != null && StringUtils.isNotBlank(subDocTracking.getIdeaGroupId())) {
//					subDocTracking.setIdeaGroupId("");
//					subDocTrackingService.update(subDocTracking);
//				}
				//承办人点击转办，恢复原来默认删除的协办人
				this.currUserIsXBPerson(subInfo.getId());
			}
			//承办人未走流程转办，删除承办人当前临时反馈
			Map<String, Object> map =new HashMap<>();
			map.put("infoId",infoId );
			map.put("subId",subId );
			map.put("userId",loginUserId);
			map.put("showFlag","0" );
			map.put("version","0" );
			replyExplainService.deleteByParam(map);
			// 发送消息提醒
			MsgTip msg = msgService.queryObject(MSGTipDefined.DCCB_JU_ZHUANBAN_MSG_TITLE);
			if (msg != null) {
				String msgUrl = msg.getMsgRedirect()+"&fileId="+infoId+"&subId="+subId;
				if(StringUtils.isNotBlank(userId)){
					msgUtil.sendMsg(msg.getMsgTitle(), msg.getMsgContent(), msgUrl, userId, appId,clientSecret, msg.getGroupName(), msg.getGroupRedirect(), "","true");
					//subDocInfoService.sendMsgByWebSocket(userId,4,false);
					webSocketHandle.addSendMap(userId,4,false);
				}
				//针对于局管理员对办理中和待落实的文件进行转办时（文件当前人离职了），给文件的当前人发送消息提醒，用于触发角标更新
				if(StringUtils.isNotBlank(cbuserId)){
					msgUtil.sendMsgUnvisible(msg.getMsgTitle(), msg.getMsgContent(), msgUrl, cbuserId, appId,clientSecret, msg.getGroupName(), msg.getGroupRedirect(), "","true");
				}
				if(StringUtils.isNotBlank(dlsUserId)){
					msgUtil.sendMsgUnvisible(msg.getMsgTitle(), msg.getMsgContent(), msgUrl, dlsUserId, appId,clientSecret, msg.getGroupName(), msg.getGroupRedirect(), "","true");
				}
				//局长局内待办，转办给自己发消息，用于触发角标
				if(StringUtils.isNotBlank(loginUserId)){
					logger.info("局长转办（局内待办转办和个人待办里面转办），触发自己角标，局长id是"+loginUserId);
					msgUtil.sendMsgUnvisible(msg.getMsgTitle(), msg.getMsgContent(), msgUrl, loginUserId, appId,clientSecret, msg.getGroupName(), msg.getGroupRedirect(), "","true");
				}
			}
			json.put("result", "success");
		}else {
			json.put("result", "fail");
		}
		Response.json(json);
	}
}
