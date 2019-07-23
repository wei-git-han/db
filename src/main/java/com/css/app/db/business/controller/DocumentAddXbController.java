package com.css.app.db.business.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.css.app.db.business.entity.*;
import com.css.app.db.business.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.css.addbase.apporgan.entity.BaseAppOrgan;
import com.css.addbase.apporgan.entity.BaseAppUser;
import com.css.addbase.apporgan.service.BaseAppOrganService;
import com.css.addbase.apporgan.service.BaseAppUserService;
import com.css.base.utils.CurrentUser;
import com.css.base.utils.Response;
import com.css.base.utils.StringUtils;

/**
 * 支持承办人增加协办所有功能
 * 
 * @author 中软信息系统工程有限公司
 * @email
 * @date 2019-07-03 13:40:10
 */
@Controller
@RequestMapping("/app/db/addXbDeal")
public class DocumentAddXbController {
	private final Logger logger = LoggerFactory.getLogger(DocumentAddXbController.class);
	@Autowired
	private SubDocInfoService subDocInfoService;
	@Autowired
	private SubDocTrackingService subDocTrackingService;
	@Autowired
	private DocXbInfoService docXbInfoService;
	@Autowired
	private BaseAppOrganService baseAppOrganService;
	@Autowired
	private BaseAppUserService baseAppUserService;
	@Autowired
	private DocXbIdeaService docXbIdeaService;
	@Autowired
	private DocumentZbjlService documentZbjlService;
	@Autowired
	private ReplyExplainService replyExplainService;
	/**
	 * 控制承办人详情页收集意见按钮显示
	 * @param subId
	 */
	@ResponseBody
	@RequestMapping("/showCollectIdeaButton")
	public void showCollectIdeaButton(String subId) {
		/**
		 * 1、当前人为承办人，且承办人未点击转办或者送审批按钮，此时可以增加协办人
		 * 2、新一轮审批开始，局内流转记录最新一条文接收人为承办人，此时可以增加协办人
		 */
		JSONObject jsonObject = new JSONObject();
		try {
			String userId = CurrentUser.getUserId();
			SubDocInfo subDocInfo = subDocInfoService.queryObject(subId);
			SubDocTracking subDocTracking = subDocTrackingService.queryLatestRecord(subId);
			if (subDocInfo != null) {
				if (StringUtils.equals(userId, subDocInfo.getUndertaker()) && subDocTracking != null) {
					//点击承办按钮未送出
					if (StringUtils.equals(userId, subDocTracking.getReceiverId()) && StringUtils.equals("1", subDocTracking.getTrackingType())) {
						jsonObject.put("result", "success");
					//新一轮反馈
					}else if (StringUtils.equals(userId, subDocTracking.getReceiverId()) && StringUtils.equals("4", subDocTracking.getTrackingType())) {
						jsonObject.put("result", "success");
					}else {
						jsonObject.put("result", "fail");
					}
				}else {
					jsonObject.put("result", "fail");
				}
			}else {
				jsonObject.put("result", "fail");
			}
		} catch (Exception e) {
			logger.info("控制承办人详情页收集意见按钮显示异常：{}", e);
			jsonObject.put("result", "fail");
		}
		Response.json(jsonObject);
	}
	/**
	 * 添加或者编辑协办人
	 * @param subId 分支ID
	 */
	@RequestMapping("/addOrEditXbPerson")
	@ResponseBody
	public void addOrEditXbPerson(String subId) {
		SubDocTracking subDocTracking = subDocTrackingService.queryLatestRecord(subId);
		if (subDocTracking != null) {
			List<DocXbInfo> docXbInfos = this.queryDocXbInfos(subId, "0");
			if (docXbInfos != null && docXbInfos.size() > 0) {
				StringBuffer stringBuffer = new StringBuffer();
				for (DocXbInfo docXbInfo : docXbInfos) {
					stringBuffer.append(docXbInfo.getReceiverId());
					stringBuffer.append(",");
				}
				Response.json("success", stringBuffer.toString());
				}
		}
//		Map<String, Object> map = new HashMap<String, Object>();
//		DocXbIdea docXbIdea = docXbIdeaService.queryLastNewData(subId, infoId);
//		if (docXbIdea != null) {
//			jsonObject.put("docXbIdeas", this.queryDocXbIdeas(infoId, subId, docXbIdea.getGroupId(), map));
//		}
	}
	
	@RequestMapping("/addOrDeleteXbPerson")
	@ResponseBody
	@SuppressWarnings("unchecked")
	public void addOrDeleteXbPerson(String userIds, String infoId, String subId) {
		Integer editXbPersonFlag = 0; //1：新增协办人；0：默认值；2：修改协办人；3：全部删除协办人
		Map<String, Object> map = new HashMap<String, Object>();
		SubDocTracking subDocTracking = subDocTrackingService.queryLatestRecord(subId);
		if (subDocTracking != null) {
			Map<String, Object> dealCurrXBPersons = this.dealCurrXBPersons(subId, userIds, editXbPersonFlag);
			List<String> userIdAdd = (List<String>)dealCurrXBPersons.get("userIdAdd");
			List<String> userIdDelete = (List<String>)dealCurrXBPersons.get("userIdDelete");
			editXbPersonFlag = (Integer)dealCurrXBPersons.get("editXbPersonFlag");
			boolean flag = (boolean)dealCurrXBPersons.get("flag");
			if (userIdAdd != null && userIdAdd.size() > 0) {
				DocXbInfo docXbInfo = null;
				for (String userId : userIdAdd) {
//				map.put("undertakerId", CurrentUser.getUserId());
//				map.put("receiverId", userId);
//				map.put("infoId", infoId);
//				map.put("subId", subId);
//				List<DocXbInfo> docXbInfos = docXbInfoService.queryList(map);
//				if (docXbInfos != null && docXbInfos.size() > 0) {
//					Response.json("result","fail");
//				}else {
					docXbInfo = new DocXbInfo();
					docXbInfo.setId(UUID.randomUUID().toString());
					docXbInfo.setInfoId(infoId);
					docXbInfo.setSubId(subId);
					docXbInfo.setUndertakerId(CurrentUser.getUserId());
					docXbInfo.setUndertakerName(CurrentUser.getUsername());
					docXbInfo.setDeptId(CurrentUser.getDepartmentId());
					BaseAppOrgan organ = this.queryBaseAppOrgan(CurrentUser.getDepartmentId());
					if (organ != null) {
						docXbInfo.setDeptName(organ.getName());
					}else {
						logger.info("请在部门表配置部门ID：{}的部门数据！", CurrentUser.getDepartmentId());
						continue;
					}
					docXbInfo.setReceiverId(userId);
					map.clear();
					map.put("userId", userId);
					List<BaseAppUser> baseAppUsers = baseAppUserService.queryList(map);
					if (baseAppUsers != null && baseAppUsers.size() > 0 ) {
						BaseAppUser baseAppUser = baseAppUsers.get(0);
						docXbInfo.setReceiverName(baseAppUser.getTruename());
						String organId = baseAppUser.getOrganid();
						//去掉重复部门ID
						docXbInfo.setReceiverDeptId(organId);
						organ = this.queryBaseAppOrgan(organId);
						if (organ != null) {
							docXbInfo.setReceiverDeptName(organ.getName());
						}else {
							logger.info("请在部门表配置部门ID：{}的部门数据！", organId);
							continue;
						}
					}else {
						logger.info("在人员表查不到人员ID：{}的数据！", userId);
						continue;
					}
					docXbInfo.setCreatedTime(new Date());
//					docXbInfo.setGroupId(ideaGroupId);
					docXbInfo.setPublishFlag(0);
					docXbInfoService.save(docXbInfo);
//				}
				}
			}
			if (userIdDelete != null && userIdDelete.size() > 0) {
				for (String userId : userIdDelete) {
					map.clear();
					map.put("infoId", infoId);
					map.put("subId", subId);
					map.put("receiverId", userId);
					List<DocXbInfo> docXbInfos = docXbInfoService.queryList(map);
					if (docXbInfos.size() > 0) {
						for (DocXbInfo docXbInfo : docXbInfos) {
							docXbInfo.setPublishFlag(1);
							docXbInfoService.update(docXbInfo);
						}
					}
				}
			}
			if (flag) {
				String[] splitUserIds = userIds.split(",");
				for (String  receiverId: splitUserIds) {
					map.clear();
					map.put("infoId", infoId);
					map.put("subId", subId);
					map.put("receiverId", receiverId);
					List<DocXbInfo> docXbInfos = docXbInfoService.queryList(map);
					if (docXbInfos.size() > 0) {
						for (DocXbInfo docXbInfo : docXbInfos) {
							docXbInfo.setPublishFlag(0);
							docXbInfoService.update(docXbInfo);
						}
					}
				}
			}
		}
		//编辑协办人或者增加协办人之后，记录在转办记录
		this.saveXBPersonOperate(infoId,subId,editXbPersonFlag);
		Response.json("result","success");
	}
	/**
	 * 主办人操作协办人记录在转办记录
	 * @param infoId
	 * @param editXbPersonFlag
	 */
	private void saveXBPersonOperate(String infoId, String subId, Integer editXbPersonFlag) {
		String undertakerId = CurrentUser.getUserId();
		List<DocXbInfo> docXbInfos = docXbInfoService.queryXbRecord(infoId,subId,undertakerId);
		for (DocXbInfo docXbInfo : docXbInfos) {
			DocumentZbjl documentZbjl = new DocumentZbjl();
			documentZbjl.setInfoId(infoId);
			documentZbjl.setUserId(docXbInfo.getUndertakerId());
			documentZbjl.setUserName(docXbInfo.getUndertakerName());
			documentZbjl.setDeptId(docXbInfo.getDeptId());
			documentZbjl.setDeptName(docXbInfo.getDeptName());
			if (!editXbPersonFlag.equals(3)) {
				documentZbjl.setReceiverIds(docXbInfo.getReceiverIds());
				documentZbjl.setReceiverNames(docXbInfo.getReceiverNames());
				documentZbjl.setReceiverDeptId(docXbInfo.getReceiverDeptId());
				documentZbjl.setReceiverDeptName(docXbInfo.getReceiverDeptName());
			}
			SubDocInfo subDocInfo = subDocInfoService.queryObject(subId);
			documentZbjl.setOrgName(subDocInfo.getSubDeptName());
			documentZbjl.setSubId(subId);
			documentZbjl.setCreatedTime(new Date());
			documentZbjl.setXbOperateFlag(editXbPersonFlag);
			documentZbjlService.save(documentZbjl);
		}
	}
	/**
	 * 判断当前主办人是增加协办人还是删除协办人
	 * @param subId
	 * @param userIds
	 * @param editXbPersonFlag 
	 * @return
	 */
	private Map<String, Object> dealCurrXBPersons(String subId, String userIds, Integer editXbPersonFlag) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> userIdDelete = new ArrayList<>();
		List<String> userIdAdd = new ArrayList<>();
		boolean flag = false;
		List<DocXbInfo> docXbInfos = this.queryDocXbInfos(subId);
		if (StringUtils.isNotBlank(userIds)) {
			String[] userIdsSplit = userIds.split(",");
			List<String> receiverIds = new ArrayList<>();
			if (docXbInfos != null && docXbInfos.size() > 0) {
				List<String> userIdsList = Arrays.asList(userIdsSplit);
				//确定有木有删除协办人
				for (DocXbInfo docXbInfo : docXbInfos) {
					if (!userIdsList.contains(docXbInfo.getReceiverId())) {
						userIdDelete.add(docXbInfo.getReceiverId());
					}
					receiverIds.add(docXbInfo.getReceiverId());
				}
				//确定有木有新增协办人
				for (String userId : userIdsSplit) {
					if (!receiverIds.contains(userId)) {
						userIdAdd.add(userId);
					}
				}
				if (userIdsList.containsAll(userIdAdd)) {
					flag = true;
				}
				editXbPersonFlag = 2;
			}else {
				//新文增加协办人
				userIdAdd = Arrays.asList(userIdsSplit);
				editXbPersonFlag = 1;
			}
		}else {//如果不选择协办人，则代表此文此承办人删除之前添加的所有协办人
			if (docXbInfos != null && docXbInfos.size() > 0) {
				for (DocXbInfo docXbInfo : docXbInfos) {
					userIdDelete.add(docXbInfo.getReceiverId());
				}
			}
			editXbPersonFlag = 3;
		}
		//清空当前map,以便使用同一个对象
		map.clear();
		map.put("userIdAdd", userIdAdd);
		map.put("userIdDelete", userIdDelete);
		map.put("editXbPersonFlag", editXbPersonFlag);
		map.put("flag", flag);
		return map ;
	}
	/**
	 * 查询当前文的组协办人
	 * @param subId
	 * @return
	 */
	private List<DocXbInfo> queryDocXbInfos(String subId) {
		Map<String, Object> map = new HashMap<String, Object>();
		//查询当前文的所有协办人
		map.put("subId", subId);
//		SubDocTracking subDocTracking = subDocTrackingService.queryLatestRecord(subId);
		return docXbInfoService.queryList(map);
	}
	/**
	 * 查询当前文的所有协办人
	 * @param subId
	 * @return
	 */
	private List<DocXbInfo> queryDocXbInfos(String subId, String publishFlag) {
		Map<String, Object> map = new HashMap<String, Object>();
		//查询当前文的所有协办人
		map.put("subId", subId);
		map.put("publishFlag",publishFlag);
//		SubDocTracking subDocTracking = subDocTrackingService.queryLatestRecord(subId);
		return docXbInfoService.queryList(map);
	}
	/**
	 * 根据部门ID查询部门信息
	 * @param orgId
	 * @return
	 */
	private BaseAppOrgan queryBaseAppOrgan(String orgId) {
		return baseAppOrganService.queryObject(orgId);
	}
	@ResponseBody
	@RequestMapping("/commitIdea")
	public void collectIdea(String infoId, String subId, String feedBackIdea) {
		/**
		 * 分两种情况
		 * 1、承办人自己提交意见
		 * 2、协办人提交意见
		 */
		//如果当前提交意见是承办人自己，那么相当于自己做协办人，DocXbInfo插入数据
		JSONObject jsonObject = new JSONObject();
		String userId = CurrentUser.getUserId();
//		SubDocInfo subDocInfo = subDocInfoService.queryObject(subId);
		/**
		 * 分两种情况  文必须在承办人那里协办人才能提意见，此时文的状态必须是办理中  这里按钮已经控制
		 * 1、承办人点击承办，文未送审批，文还在我这 没有反馈记录
		 * 2、新一轮反馈开始，文又到我这了  反馈记录最新一条showFlag = "1"
		 */
//		if (subDocInfo != null && StringUtils.equals(userId, subDocInfo.getUndertaker())) {
//			if (StringUtils.isNotBlank(feedBackIdea)) {
		DocXbIdea docXbIdea = this.organizeDocXbIdea(feedBackIdea, infoId, subId);
		this.aquireGroupId(docXbIdea, infoId, subId,userId);
		docXbIdeaService.save(docXbIdea);
//			}
//		}else {
//			DocXbIdea docXbIdea = this.organizeDocXbIdea(feedBackIdea, infoId, subId);
//			this.productGroupId(docXbIdea, infoId, subId);
//			docXbIdeaService.save(docXbIdea);
//		}
		jsonObject.put("result", "success");
		Response.json(jsonObject);
	}
	/**
	 * 统一组织意见对象数据
	 * @param feedBackIdea
	 * @param infoId
	 * @param subId
	 * @return
	 */
	private DocXbIdea organizeDocXbIdea(String feedBackIdea, String infoId, String subId) {
		Date date = new Date();
		DocXbIdea docXbIdea = new DocXbIdea();
		docXbIdea.setId(UUID.randomUUID().toString());
		docXbIdea.setInfoId(infoId);
		docXbIdea.setSubId(subId);
		docXbIdea.setUserId(CurrentUser.getUserId());
		docXbIdea.setUserName(CurrentUser.getUsername());
		docXbIdea.setFeedBackIdea(feedBackIdea);
		docXbIdea.setCreatedTime(date);
		return docXbIdea;
	}
	/**
	 * 主办人提交意见前，看是否协办人已经提交意见；否则相反；
	 * @param infoId
	 * @param subId
	 * @return
	 */
	/*private DocXbIdea isCommitIdea(String infoId, String subId) {
		//查询当前协办人是否已经提交意见
		return docXbIdeaService.queryLastNewData(subId, infoId);
	}*/
	/**
	 * 提意见生成组ID
	 * @param infoId
	 * @param subId
	 * @param userId 
	 */
	private void aquireGroupId(DocXbIdea docXbIdea,String infoId, String subId, String userId) {
		//主办人发起提议  生成新一轮提议组ID
		SubDocTracking subDocTracking = subDocTrackingService.queryLatestRecord(subId);
		SubDocInfo subDocInfo = subDocInfoService.queryObject(subId);
		String ideaGroupId = null;
		if (subDocTracking != null) {
			//获取本轮意见组ID
			ideaGroupId = subDocTracking.getIdeaGroupId();
			docXbIdea.setGroupId(ideaGroupId);
		}
		Map<String, Object> hashMap = new HashMap<>();
		hashMap.put("infoId", infoId);
		hashMap.put("subId", subId);
		hashMap.put("receiverId", userId);
		List<DocXbInfo> docXbInfos = docXbInfoService.queryList(hashMap);
		if (docXbInfos.size()>0) {
			docXbIdea.setUndertakerId(docXbInfos.get(0).getUndertakerId());
			docXbIdea.setUndertakerName(docXbInfos.get(0).getUndertakerName());
		}else {
			//承办人提意见
			docXbIdea.setUndertakerId(CurrentUser.getUserId());
			docXbIdea.setUndertakerName(CurrentUser.getUsername());
		}
		if (subDocInfo != null) {
			subDocInfo.setIdeaCount(subDocInfo.getIdeaCount() == null || subDocInfo.getIdeaCount() == 0 ? 1 : subDocInfo.getIdeaCount()+1);
			subDocInfo.setIdeaAddFlag(1);
			subDocInfoService.update(subDocInfo);
		}
	}
	/**
	 * 设置意见组ID
	 * @param docXbIdea
	 * @param infoId
	 * @param subId
	 */
	/*private void setGroupId(DocXbIdea docXbIdea,String infoId, String subId) {
		DocXbIdea commitIdea = this.isCommitIdea(infoId, subId);
		if (commitIdea != null) {
			docXbIdea.setGroupId(commitIdea.getGroupId());
		}else {
			docXbIdea.setGroupId(UUID.randomUUID().toString());
		}
	}*/
	/**
	 * 展示意见记录(局内所有人，正式发布以后给所有人看，这个跟随本轮反馈是否发布来选择意见按钮的显示)
	 * @param subId
	 */
	@RequestMapping("/showIdeaRecord")
	@ResponseBody
	public void showIdeaRecord(String subId, String ideaGroupId) {
		Map<String, Object> map = new HashMap<String, Object>();
		//登录人是协办人，文不在协办人这里
//		map.put("receiverId", CurrentUser.getUserId());
		try {
			if (StringUtils.isNotBlank(ideaGroupId)) {
				SubDocInfo subDocInfo = subDocInfoService.queryObject(subId);
				if (subDocInfo != null ) {
					String infoId = subDocInfo.getInfoId();
					String undertaker = subDocInfo.getUndertaker();
//				List<DocXbInfo> docXbInfos = docXbInfoService.queryList(map);
//				SubDocTracking subDocTracking = subDocTrackingService.queryLatestRecord(subId);
//				if (docXbInfos == null) {
					//协办人详情页意见展示筛选，只显示本轮意见
//					DocXbIdea docXbIdea = docXbIdeaService.queryLastNewData(subId, infoId);
//					if (docXbIdea != null) {
					Response.json(this.queryDocXbIdeas(infoId, subId, ideaGroupId, map, undertaker));
//					}
//				}
				}
			}else {
				this.showCurrIdeaRecord(subId);
			}
		} catch (Exception e) {
			logger.info("调用意见展示方法异常：{}", e);
		}
	}
	@RequestMapping("/showCurrIdeaRecord")
	@ResponseBody
	public void showCurrIdeaRecord(String subId) {
		Map<String, Object> map = new HashMap<String, Object>();
		SubDocInfo subDocInfo = subDocInfoService.queryObject(subId);
		String infoId = subDocInfo.getInfoId();
		String undertaker = subDocInfo.getUndertaker();
//		subDocInfo.setIdeaAddFlag(null);
//		subDocInfoService.update(subDocInfo);
		SubDocTracking subDocTracking = subDocTrackingService.queryNewRecord(subId);
//		String userId = CurrentUser.getUserId();
		//协办人详情页意见展示筛选，只显示本轮意见
		if (subDocTracking != null /*&& StringUtils.equals(subDocTracking.getReceiverId(), userId)*/) {
//			map.put("receiverId", userId);
//			ReplyExplain replyExplain = replyExplainService.queryLastNewData(infoId,subId);
//			if (replyExplain != null) {
//				String showFlag = replyExplain.getShowFlag();
//				if (StringUtils.equals(showFlag, "1")) {
//					map.clear();
					Response.json(this.queryDocXbIdeas(infoId, subId, subDocTracking.getIdeaGroupId(), map, undertaker));
//				}
//			}
		}else {
			Response.json("result","");
		}
	}
	/**
	 * 查询意见记录
	 * @param infoId
	 * @param subId
	 * @param ideaGroupId
	 * @param map
	 * @param undertaker 
	 * @return
	 */
	private JSONObject queryDocXbIdeas(String infoId, String subId, String ideaGroupId, Map<String, Object> map, String undertaker) {
		JSONObject jsonObject = new JSONObject();
		map.put("subId", subId);
		map.put("infoId", infoId);
		map.put("groupId", ideaGroupId);
		List<DocXbIdea> docXbIdeas = docXbIdeaService.queryList(map);
		map.put("groupId", null);
		map.put("publishFlag", "0");
		List<DocXbInfo> docXbInfos = docXbInfoService.queryList(map);
		Set<String> userNames = new HashSet<>();
		String chenban = null;
		/*if (docXbIdeas.size() < 0) {
			*//*for (DocXbIdea docXbIdea : docXbIdeas) {
				if (!StringUtils.equals(docXbIdea.getUserId(), docXbIdea.getUndertakerId())
						*//**//*&& !StringUtils.equals(docXbIdea.getIsUndertakerFlag(), "1")*//**//*) {
					userNames.add(docXbIdea.getUserName());
				}
			}*//*
			logger.info("根据subId:{}, infoId:{},ideaGroupId:{}查不到承、协办人意见数据",subId,infoId,ideaGroupId);
			jsonObject.put("result", "");
			return jsonObject;
		}*/
		if (docXbInfos.size() > 0) {
			if (this.queryReplyExplain(map, ideaGroupId)) {
				if (docXbIdeas.size() > 0) {
					chenban = docXbIdeas.get(0).getUndertakerName();
					for (DocXbIdea docXbIdea : docXbIdeas) {
						if (!StringUtils.equals(docXbIdea.getUserId(), docXbIdea.getUndertakerId())) {
							userNames.add(docXbIdea.getUserName());
						}
					}
				} else {
					logger.info("根据subId:{}, infoId:{},ideaGroupId:{}查不到承、协办人意见数据",subId,infoId,ideaGroupId);
//					jsonObject.put("result", "");
//					return jsonObject;
				}
			} else {
				chenban = docXbInfos.get(0).getUndertakerName();
                for (DocXbInfo docXbInfo : docXbInfos) {
                    userNames.add(docXbInfo.getReceiverName());
                }
			}
		}else {
			logger.info("根据subId:{}, infoId:{}查不到承、协办人数据",subId,infoId);
			/*jsonObject.put("result", "");
			return jsonObject;*/
		}
		
		/*if (docXbIdeas.size() > 0) {
			chenban = docXbIdeas.get(0).getUndertakerName();
		}else {
			logger.info("根据subId:{}, infoId:{},ideaGroupId:{}查不到承、协办人意见数据",subId,infoId,ideaGroupId);
			jsonObject.put("result", "");
			return jsonObject;
		}*/
		jsonObject.put("chenban", chenban);
		jsonObject.put("xieban", userNames.toString().replace("[", "").replace("]", ""));
		jsonObject.put("docXbIdeas", docXbIdeas);
		return jsonObject;
	}
	/**
	 * 查询本轮反馈是否已发布
	 * @param map
	 * @param ideaGroupId
	 */
	private boolean queryReplyExplain(Map<String, Object> map, String ideaGroupId) {
		map.put("groupId", ideaGroupId);
		List<ReplyExplain> replyExplains = replyExplainService.queryList(map);
		if (replyExplains.size() > 0) {
			ReplyExplain replyExplain = replyExplains.get(0);
			if (StringUtils.equals(replyExplain.getShowFlag(), "1")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 意见数按钮颜色控制
	 * @param subId
	 */
	@RequestMapping("/buttonColor")
	@ResponseBody
	public void buttonColor(String subId) {
		SubDocInfo subDocInfo = subDocInfoService.queryObject(subId);
		if (subDocInfo != null) {
			subDocInfo.setIdeaAddFlag(0);
			subDocInfoService.update(subDocInfo);
			Response.json("result","success");
		}
	}
}
