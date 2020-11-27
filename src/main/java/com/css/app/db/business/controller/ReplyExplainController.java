package com.css.app.db.business.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.css.addbase.apporgan.service.BaseAppUserService;
import com.css.app.db.business.entity.*;
import com.css.app.db.business.service.*;
import com.css.app.db.config.service.AdminSetService;
import com.css.app.db.config.service.RoleSetService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.css.base.utils.CurrentUser;
import com.css.base.utils.Response;
import com.css.base.utils.UUIDUtils;

import cn.com.css.filestore.impl.HTTPFile;


/**
 * 办理反馈表
 * 
 * @author 中软信息系统工程有限公司
 * @email 
 * @date 2019-04-25 19:07:17
 */
@Controller
@RequestMapping("/app/db/replyexplain")
public class ReplyExplainController {
	@Autowired
	private ReplyExplainService replyExplainService;
	@Autowired
	private ReplyAttacService replyAttacService;
	@Autowired
	private SubDocInfoService subDocInfoService;
	@Autowired
	private ApprovalOpinionService approvalOpinionService;
	@Autowired
	private SubDocTrackingService subDocTrackingService;
	@Autowired
	private BaseAppUserService baseAppUserService;
    @Autowired
    private AdminSetService adminSetService;
	@Autowired
	private DocumentInfoService documentInfoService;
	@Autowired
	private DocumentReadService documentReadService;
	@Autowired
	private RoleSetService roleSetService;
	/**
	 * 获取某个分支局反馈
	 * @param infoId 主文件id
	 * @param subId 分支局id
	 */
	@ResponseBody
	@RequestMapping("/subReplyList")
	public void subReplyList(String infoId,String subId){
		JSONArray jsonArray = new JSONArray();
		if(StringUtils.isNotBlank(infoId) && StringUtils.isNotBlank(subId)) {
			//承办人
			SubDocInfo subDocInfo = subDocInfoService.queryObject(subId);
			//当前审核人
			SubDocTracking latestRecord = subDocTrackingService.queryLatestRecord(subId);
			boolean isCheckUser=false;
			if(latestRecord != null) {
				String recordId=latestRecord.getReceiverId();
				if(StringUtils.equals(CurrentUser.getUserId(),recordId )) {
					isCheckUser=true;
				}
			}
			if(subDocInfo != null) {
				String cbrId=subDocInfo.getUndertaker();
				//获取某局各组最新的反馈
				List<ReplyExplain> dbReplyExplainList = replyExplainService.querySubLatestReply(infoId, subId);
				for (ReplyExplain replyExplain : dbReplyExplainList) {
					boolean editFlag=false;
					JSONObject json=new JSONObject();
					String teamId=replyExplain.getTeamId();
					Date firstDate = null;
					Map<String, Object> replyMap =new HashMap<>();
					replyMap.put("subId", subId);
					replyMap.put("teamId", teamId);
					replyMap.put("cbrFlag", "1");
					replyMap.put("sort", "asc");
					List<ReplyExplain> list = replyExplainService.queryList(replyMap);
					if(list !=null && list.size()>0) {
						//第一个承办人反馈的时间
						firstDate = list.get(0).getCreatedTime();
						String showFlag = list.get(0).getShowFlag();
						if(StringUtils.isNotBlank(cbrId)) {
							//如果已经正式发布显示这一组最后一个承办人姓名
							if(StringUtils.equals("1", showFlag)) {
								json.put("cbrId", list.get(list.size()-1).getUserId());
								json.put("cbrName", list.get(list.size()-1).getUserName());
							}else {
								//如果未正式发布则显示系统当前承办人姓名（（因为可能会存在流程中转办但下一个承办人承办了但还没做任何反馈的修改的时候））
								json.put("cbrId", cbrId);
								json.put("cbrName", subDocInfo.getUndertakerName());
							}
						}else {
							//如果处在为承办的过程中，则显示当前组的最后一个承办人姓名
							json.put("cbrId", list.get(list.size()-1).getUserId());
							json.put("cbrName", list.get(list.size()-1).getUserName());
						}
					}
					json.put("danwei", subDocInfo.getSubDeptName());
					json.put("firstDate", firstDate);
					json.put("teamId", teamId);
					//意见组ID
					json.put("ideaGroupId", replyExplain.getIdeaGroupId());
					json.put("content",replyExplain.getReplyContent());
					json.put("updateTime",replyExplain.getCreatedTime());
					//编辑的显示条件：1、当前反馈未正式发布2.登录人为当前处理人3.非待落实状态
					if(!StringUtils.equals("1", replyExplain.getShowFlag()) && isCheckUser && subDocInfo.getDocStatus()!=5) {
						editFlag=true;
					}
					json.put("edit",editFlag);
					//附件
					Map<String, Object> map = new HashMap<>();
					map.put("teamId", teamId);
					map.put("subId", subId);
					List<ReplyAttac> attchList = replyAttacService.queryList(map);
					json.put("attchList",attchList);
					Map<String, Object> opMap = new HashMap<>();
					opMap.put("subId", subId);
					opMap.put("teamId", teamId);
					List<ApprovalOpinion> opinionList = approvalOpinionService.queryList(map);
					if(opinionList != null && opinionList.size()>0) {
						json.put("cuowei","1");
						for(ApprovalOpinion opinion : opinionList) {
							if(StringUtils.equals("1", opinion.getYjType())) {
								if(StringUtils.isNotBlank(opinion.getOpinionContent())) {
									HTTPFile httpFile = new HTTPFile(opinion.getOpinionContent());
									opinion.setOpinionContent(httpFile.getAssginDownloadURL());
								}else {
									System.out.print("标识为手写签批，但局长手写签批获取不到，可能原因为标识存错或者链接不到文件服务");
								}
							}
						}
						json.put("opinionList",opinionList);
					}else {
						json.put("cuowei","0");
					}
					//未完成审批的默认展开标识
					if(StringUtils.equals("1", replyExplain.getShowFlag())) {
						json.put("show","0");
					}else {
						json.put("show","1");
						String status = subDocInfo.getChooseStatus();
						String statusName="";
						if(StringUtils.equals("1", status)) {
							statusName="办理中";
						}else if(StringUtils.equals("2", status)){
							statusName="办结";
						}else if(StringUtils.equals("3", status)) {
							statusName="常态落实";
						}
						json.put("checkStatus",status);
						json.put("checkStatusName",statusName);
						
					}
					//当前文件状态
					jsonArray.add(json);
				}
			}
		}
		Response.json(jsonArray);
	}

	/**
	 * 个人待办中，局长编辑意见，从后台返回，防止有特殊符号，前台从链接中取不到值
	 * @param infoId
	 * @param subId
	 */
	@ResponseBody
	@RequestMapping("/getReplyInfo")
	public void getReplyInfo(String infoId,String subId){
		JSONArray jsonArray = new JSONArray();
		List<ReplyExplain> dbReplyExplainList = replyExplainService.querySubLatestReply(infoId, subId);
		if(dbReplyExplainList != null && dbReplyExplainList.size() > 0){
			for(int i = 0;i<dbReplyExplainList.size();i++){
				JSONObject json=new JSONObject();
				json.put("content",dbReplyExplainList.get(i).getReplyContent());
				jsonArray.add(json);
			}

		}
		Response.json(jsonArray);
	}


	
	/**
	 * 获取所有分支局最新已发布反馈
	 * @param infoId 主文件id
	 */
	@ResponseBody
	@RequestMapping("/allReplyList")
	public void allReplyList(String infoId){
		JSONArray jsonArray = new JSONArray();
		if(StringUtils.isNotBlank(infoId)) {
			//获取所有分支局已经正式发布的反馈按创建时间倒叙排列
			List<ReplyExplain> dbReplyExplainList = replyExplainService.queryAllLatestReply(infoId);
			for (ReplyExplain replyExplain : dbReplyExplainList) {
				JSONObject json=new JSONObject();
				///////////////////////////////////
				String subId=replyExplain.getSubId();
				SubDocInfo subDocInfo = subDocInfoService.queryObject(subId);
				//当前审核人
				SubDocTracking latestRecord = subDocTrackingService.queryLatestRecord(subId);
				boolean isCheckUser=false;
				if(latestRecord != null) {
					String recordId=latestRecord.getReceiverId();
					if(StringUtils.equals(CurrentUser.getUserId(),recordId )) {
						isCheckUser=true;
					}
				}
				boolean editFlag=false;
				if(!StringUtils.equals("1", replyExplain.getShowFlag()) && isCheckUser && subDocInfo.getDocStatus()!=5) {
					editFlag=true;
				}
				json.put("edit",editFlag);


				///////////////////////////////////




				if(subDocInfo != null) {

					String teamId=replyExplain.getTeamId();
					Date firstDate = null;
					String ideaGroupId = null;
					Map<String, Object> replyMap =new HashMap<>();
					replyMap.put("subId", subId);
					replyMap.put("teamId", teamId);
					replyMap.put("cbrFlag", "1");
					replyMap.put("sort", "asc");
					List<ReplyExplain> list = replyExplainService.queryList(replyMap);
					if(list !=null && list.size()>0) {
						firstDate = list.get(0).getCreatedTime();
						ideaGroupId = list.get(0).getIdeaGroupId();
						json.put("cbrId", list.get(list.size()-1).getUserId());
						json.put("cbrName", list.get(list.size()-1).getUserName());
					}
					json.put("danwei", subDocInfo.getSubDeptName());
					json.put("firstDate", firstDate);
					json.put("subId", subId);
					json.put("teamId", teamId);
					
					//意见组ID
					json.put("ideaGroupId", ideaGroupId);
					json.put("teamId", teamId);
					json.put("content",replyExplain.getReplyContent());
					json.put("updateTime",replyExplain.getCreatedTime());
					json.put("id",replyExplain.getId());
					//附件
					Map<String, Object> map = new HashMap<>();
					map.put("teamId", teamId);
					map.put("subId", subId);
					List<ReplyAttac> attchList = replyAttacService.queryList(map);
					json.put("attchList",attchList);
					Map<String, Object> opMap = new HashMap<>();
					opMap.put("subId", subId);
					opMap.put("teamId", teamId);
					List<ApprovalOpinion> opinionList = approvalOpinionService.queryList(map);
					if(opinionList != null && opinionList.size()>0) {
						json.put("cuowei","1");
						for(ApprovalOpinion opinion : opinionList) {
							if(StringUtils.equals("1", opinion.getYjType())) {
								if(StringUtils.isNotBlank(opinion.getOpinionContent())) {
									HTTPFile httpFile = new HTTPFile(opinion.getOpinionContent());
									opinion.setOpinionContent(httpFile.getAssginDownloadURL());
								}else {
									System.out.print("标识为手写签批，但局长手写签批获取不到，可能原因为标识存错或者链接不到文件服务");
								}
							}
						}
						json.put("opinionList",opinionList);
					}else {
						json.put("cuowei","0");
					}
					String deptId = subDocInfo.getSubDeptId();
					String orgId = baseAppUserService.getBareauByUserId(CurrentUser.getUserId());
                    String loginUserId = CurrentUser.getUserId();
                    //获取当前人的管理员类型（0:超级管理员 ;1：部管理员；2：局管理员；3：即是部管理员又是局管理员）
                    String adminFlag = adminSetService.getAdminTypeByUserId(loginUserId);
					// 当前登录人的角色（1：首长；2：首长秘书；3：局长；4：局秘书；5：处长；6：参谋;）
					String roleType = roleSetService.getRoleTypeByUserId(loginUserId);
                    //如果该局和当前登录人属于同一个部门且该登录人是局管理员或者超级管理员
					if (StringUtils.equals(deptId, orgId) && "2".equals(adminFlag)) {//是局管理员且是同一个局
						json.put("isSameDept", "true");
					} else if ("0".equals(adminFlag)) {//超级管理员
						json.put("isSameDept", "true");
					} else if (StringUtils.equals(deptId, orgId) && "3".equals(roleType)) {//是局长且是同一个局
						json.put("isSameDept", "true");
					} else {
						json.put("isSameDept", "false");
					}
					String status = subDocInfo.getChooseStatus();
					String statusName="";
					if(StringUtils.equals("1", status)) {
						statusName="办理中";
					}else if(StringUtils.equals("2", status)){
						statusName="办结";
					}else if(StringUtils.equals("3", status)) {
						statusName="常态落实";
					}
					json.put("checkStatus",status);
					json.put("checkStatusName",statusName);
					jsonArray.add(json);
				}
			}
		}
		Response.json(jsonArray);
	}
	
	/**
	 * 获取所有局最新一轮最新一条的已发布反馈
	 * @param infoId
	 */
	@ResponseBody
	@RequestMapping("/getAllLatestOneReply")
	public void getAllLatestOneReply(String infoId) {
		List<ReplyExplain> latestOneReply = replyExplainService.queryAllLatestOneReply(infoId);
		for (ReplyExplain replyExplain : latestOneReply) {
			Map<String, Object> replyMap =new HashMap<>();
			replyMap.put("subId", replyExplain.getSubId());
			replyMap.put("teamId", replyExplain.getTeamId());
			replyMap.put("cbrFlag", "1");
			List<ReplyExplain> list = replyExplainService.queryList(replyMap);
			if(list !=null && list.size()>0) {
				replyExplain.setUserName(list.get(0).getUserName());
			}
			Map<String, Object> statusMap =new HashMap<>();
			statusMap.put("subId", replyExplain.getSubId());
			statusMap.put("teamId", replyExplain.getTeamId());
			statusMap.put("tjStatus", "yes");
			List<ReplyExplain> statuslist = replyExplainService.queryList(statusMap);
			if(statuslist !=null && statuslist.size()>0) {
				replyExplain.setChooseStatus(statuslist.get(0).getChooseStatus());
			}
		}
		Response.json(latestOneReply);
	}
	
	/**
	 * 获取所有局最新一条的已发布反馈
	 * @param infoId
	 */
	@ResponseBody
	@RequestMapping("/queryAllLatestReply")
	public void queryAllLatestReply(String infoId) {
		List<ReplyExplain> latestOneReply = replyExplainService.queryAllLatestReply(infoId);
		for (ReplyExplain replyExplain : latestOneReply) {
			Map<String, Object> replyMap =new HashMap<>();
			replyMap.put("subId", replyExplain.getSubId());
			replyMap.put("teamId", replyExplain.getTeamId());
			replyMap.put("cbrFlag", "1");
			List<ReplyExplain> list = replyExplainService.queryList(replyMap);
			if(list !=null && list.size()>0) {
				replyExplain.setUserName(list.get(0).getUserName());
			}
			Map<String, Object> statusMap =new HashMap<>();
			statusMap.put("subId", replyExplain.getSubId());
			statusMap.put("teamId", replyExplain.getTeamId());
			statusMap.put("tjStatus", "yes");
			List<ReplyExplain> statuslist = replyExplainService.queryList(statusMap);
			if(statuslist !=null && statuslist.size()>0) {
				replyExplain.setChooseStatus(statuslist.get(0).getChooseStatus());
			}
		}
		Response.json(latestOneReply);
	}
	
	/**
	 * 获取某个反馈的审批意见----暂时没有用（本来想点击展开的时候单独获取用）
	 * @param subId 分局主id
	 * @param teamId 某个反馈
	 */
	@ResponseBody
	@RequestMapping("/getOpinion")
	public void getOpinion(String subId,String teamId) {
		Map<String, Object> map = new HashMap<>();
		map.put("subId", subId);
		map.put("teamId", teamId);
		List<ApprovalOpinion> queryList = approvalOpinionService.queryList(map);
		Response.json(queryList);
	}
	
	/**
	 * 获取某个人的反馈---------注：已经废弃不用了
	 * @param subId 分支主文件id
	 * @param teamId 某组反馈的id
	 * @param userId 人的id
	 */
	@ResponseBody
	@RequestMapping("/personReply")
	public void personReply(String subId,String teamId,String userId) {
		ReplyExplain reply=null;
		Map<String, Object> map = new HashMap<>();
		map.put("subId", subId);
		map.put("teamId", teamId);
		map.put("userId", userId);
		List<ReplyExplain> queryList = replyExplainService.queryList(map);
		if(queryList !=null && queryList.size()>0) {
			reply=queryList.get(0);
		}
		Response.json(reply);
	}
	
	/**
	 * 获取某组办理反馈
	 * @param subId
	 * @param teamId
	 */
	@ResponseBody
	@RequestMapping("/getReplyByTeamId")
	public void getReplyByTeamId(String subId,String teamId) {
		Map<String, Object> map = new HashMap<>();
		map.put("subId", subId);
		map.put("teamId", teamId);
		List<ReplyExplain> queryList = replyExplainService.queryList(map);
		Response.json(queryList);
	}
	
	/**
	 * 保存反馈意见
	 */
	@ResponseBody
	@RequestMapping("/save")
	public void save(String subId,String infoId,String teamId,String replyContent,@RequestParam(value = "file", required = false) MultipartFile[] files){
		String loginUserId=CurrentUser.getUserId();
		String loginUserName=CurrentUser.getUsername();
		String cbrFlag="0";
		JSONObject json=new JSONObject();
		if(StringUtils.isNotBlank(infoId) && StringUtils.isNotBlank(subId)) {
			SubDocInfo subDocInfo = subDocInfoService.queryObject(subId);
			if(StringUtils.equals(loginUserId, subDocInfo.getUndertaker())) {
				cbrFlag="1";
			}
			if(StringUtils.isBlank(teamId)) {
				String uuid=UUIDUtils.random();
				//新增反馈及附件
				replyExplainService.saveReply(subId, infoId, loginUserId, loginUserName, uuid, replyContent, subDocInfo.getSubDeptId(), subDocInfo.getSubDeptName(),cbrFlag,null);
				if(files!=null){
					replyAttacService.saveAttacs(files, subId, uuid);
				}
			}else {
				Map<String, Object> map =new HashMap<>();
				map.put("subId", subId);
				map.put("userId", loginUserId);
				map.put("teamId", teamId);
				map.put("showFlag", "0");
				ReplyExplain tempReply = replyExplainService.queryLastestTempReply(map);
				if(tempReply != null) {
					tempReply.setReplyContent(replyContent);
					replyExplainService.update(tempReply);
					
				}else {
					Map<String, Object> cbrMap =new HashMap<>();
					cbrMap.put("subId", subId);
					cbrMap.put("teamId", teamId);
					cbrMap.put("showFlag", "0");
					ReplyExplain zbTempReply = replyExplainService.queryLastestTempReply(cbrMap);
					if(zbTempReply != null) {
						if(StringUtils.equals("1", zbTempReply.getCbrFlag()) && StringUtils.equals("1", cbrFlag)) {
							zbTempReply.setCbrFlag("0");
						}
						zbTempReply.setReVersion("1");
						zbTempReply.setVersionTime(new Date());
						replyExplainService.update(zbTempReply);
					}
					replyExplainService.saveReply(subId, infoId, loginUserId, loginUserName, teamId, replyContent, subDocInfo.getSubDeptId(), subDocInfo.getSubDeptName(),cbrFlag,null);
				}
				if(files!=null && files.length>0){
					replyAttacService.saveAttacs(files, subId, teamId);
				}
			}
			
			json.put("result", "success");
		}else {
			json.put("result", "fail");
		}
		Response.json(json);
	}
	/**
	 * 编辑反馈意见
	 */
	@ResponseBody
	@RequestMapping("/edit")
	public void edit(String subId,String infoId,String teamId,String replyContent,String checkStatus){
		String loginUserId=CurrentUser.getUserId();
		String loginUserName=CurrentUser.getUsername();
		String cbrFlag="0";
		JSONObject json=new JSONObject();
		if(StringUtils.isNotBlank(infoId) && StringUtils.isNotBlank(subId)) {
			SubDocInfo subDocInfo = subDocInfoService.queryObject(subId);
			if(StringUtils.equals(loginUserId, subDocInfo.getUndertaker())) {
				cbrFlag="1";
			}
			if(StringUtils.isBlank(teamId)) {
				String uuid=UUIDUtils.random();
				//新增反馈及附件
				replyExplainService.saveReply(subId, infoId, loginUserId, loginUserName, uuid, replyContent, subDocInfo.getSubDeptId(), subDocInfo.getSubDeptName(),cbrFlag,checkStatus);
			}else {
				Map<String, Object> map =new HashMap<>();
				map.put("subId", subId);
				map.put("userId", loginUserId);
				map.put("teamId", teamId);
				map.put("showFlag", "0");
				ReplyExplain tempReply = replyExplainService.queryLastestTempReply(map);
				SubDocTracking subDocTracking = subDocTrackingService.queryLatestRecord(subId);
				if(StringUtils.isNotBlank(checkStatus)) {
					subDocTracking.setPreviousStatus(Integer.parseInt(subDocInfo.getChooseStatus()));
					subDocTrackingService.update(subDocTracking);
				}
				if(tempReply != null) {
					if(StringUtils.isNotBlank(checkStatus)) {
						subDocInfo.setChooseStatus(checkStatus);
						subDocInfoService.update(subDocInfo);
						tempReply.setChooseStatus(checkStatus);
					}
					tempReply.setReplyContent(replyContent);
					replyExplainService.update(tempReply);
				}else {
					if(StringUtils.isNotBlank(checkStatus)) {
						subDocInfo.setChooseStatus(checkStatus);
						subDocInfoService.update(subDocInfo);
					}
					replyExplainService.saveReply(subId, infoId, loginUserId, loginUserName, teamId, replyContent, subDocInfo.getSubDeptId(), subDocInfo.getSubDeptName(),cbrFlag,checkStatus);
				}
			}
			json.put("result", "success");
		}else {
			json.put("result", "fail");
		}
		Response.json(json);
	}

	/**
	 * 办理反馈编辑反馈意见
	 */
	@ResponseBody
	@RequestMapping("/editOpinion")
	public void editOpinion(String subId,String infoId,String teamId,String replyContent,String checkStatus,String ideaGroupId){
		String loginUserId=CurrentUser.getUserId();
		String loginUserName=CurrentUser.getUsername();
		String cbrFlag="0";
		JSONObject json=new JSONObject();
		if(StringUtils.isNotBlank(infoId) && StringUtils.isNotBlank(subId)) {
			SubDocInfo subDocInfo = subDocInfoService.queryObject(subId);
			if(StringUtils.equals(loginUserId, subDocInfo.getUndertaker())) {
				cbrFlag="1";
			}
			if(StringUtils.isBlank(teamId)) {
				String uuid=UUIDUtils.random();
				//新增反馈及附件
				replyExplainService.saveNewReply(subId, infoId, loginUserId, loginUserName, uuid, replyContent, subDocInfo.getSubDeptId(), subDocInfo.getSubDeptName(),cbrFlag,checkStatus,ideaGroupId);
			}else {
				Map<String, Object> map =new HashMap<>();
				map.put("subId", subId);
				map.put("userId", loginUserId);
				map.put("teamId", teamId);
				map.put("showFlag", "0");
				ReplyExplain tempReply = replyExplainService.queryLastestTempReply(map);
				//ReplyExplain tempReply = replyExplainService.queryReplyExplain(opinionId);
				SubDocTracking subDocTracking = subDocTrackingService.queryLatestRecord(subId);
				if(StringUtils.isNotBlank(checkStatus)) {
					subDocTracking.setPreviousStatus(Integer.parseInt(subDocInfo.getChooseStatus()));
					subDocTrackingService.update(subDocTracking);
				}
				if(tempReply != null) {
					if(StringUtils.isNotBlank(checkStatus)) {
						subDocInfo.setChooseStatus(checkStatus);
						subDocInfoService.update(subDocInfo);
						tempReply.setChooseStatus(checkStatus);
					}
					tempReply.setReplyContent(replyContent);
					replyExplainService.update(tempReply);
				}else {
					if(StringUtils.isNotBlank(checkStatus)) {
						subDocInfo.setChooseStatus(checkStatus);
						subDocInfoService.update(subDocInfo);
					}
					replyExplainService.saveNewReply(subId, infoId, loginUserId, loginUserName, teamId, replyContent, subDocInfo.getSubDeptId(), subDocInfo.getSubDeptName(),cbrFlag,checkStatus,ideaGroupId);
				}
			}
			//列表督办落实情况也得更新
			DocumentInfo info = documentInfoService.queryObject(infoId);
			info.setLatestReply(replyContent);
			info.setLatestReplyTime(new Date());
			documentInfoService.update(info);
			// 清理除首长外的本文件已读
			documentReadService.deleteByInfoId(infoId);
			json.put("result", "success");
		}else {
			json.put("result", "fail");
		}
		Response.json(json);
	}
	
	/**
	 * 删除附件
	 * @param id 附件id
	 */
	@ResponseBody
	@RequestMapping("/deleteAttch")
	public void delete(String id){
		replyAttacService.delete(id);
		Response.json("result", "success");
	}
	
    /**
     * 下载附件
     * @param fileId
     */
	@ResponseBody
	@RequestMapping("/downLoad")
	public void downLoad(String fileId){
		String url="";
		if(StringUtils.isNotBlank(fileId)){
			HTTPFile hf = new HTTPFile(fileId);
		    url = hf.getAssginDownloadURL();
		}
		Response.json("url", url);
	}
}
