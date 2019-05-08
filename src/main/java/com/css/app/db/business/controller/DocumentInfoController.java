package com.css.app.db.business.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.css.addbase.AppConfig;
import com.css.addbase.FileBaseUtil;
import com.css.addbase.apporgan.service.BaseAppUserService;
import com.css.addbase.suwell.OfdTransferUtil;
import com.css.app.db.business.entity.DocumentBjjl;
import com.css.app.db.business.entity.DocumentCbjl;
import com.css.app.db.business.entity.DocumentFile;
import com.css.app.db.business.entity.DocumentInfo;
import com.css.app.db.business.entity.DocumentRead;
import com.css.app.db.business.entity.SubDocInfo;
import com.css.app.db.business.entity.SubDocTracking;
import com.css.app.db.business.service.DocumentBjjlService;
import com.css.app.db.business.service.DocumentCbjlService;
import com.css.app.db.business.service.DocumentFileService;
import com.css.app.db.business.service.DocumentInfoService;
import com.css.app.db.business.service.DocumentReadService;
import com.css.app.db.business.service.SubDocInfoService;
import com.css.app.db.business.service.SubDocTrackingService;
import com.css.app.db.config.entity.AdminSet;
import com.css.app.db.config.entity.RoleSet;
import com.css.app.db.config.service.AdminSetService;
import com.css.app.db.config.service.RoleSetService;
import com.css.app.db.util.DbDefined;
import com.css.app.db.util.DbDocStatusDefined;
import com.css.base.utils.CurrentUser;
import com.css.base.utils.GwPageUtils;
import com.css.base.utils.Response;
import com.css.base.utils.UUIDUtils;
import com.github.pagehelper.PageHelper;

import cn.com.css.filestore.impl.HTTPFile;


/**
 * 督办基本信息表
 * 
 * @author 中软信息系统工程有限公司
 * @email 
 * @date 2019-04-18 16:34:38
 */
@Controller
@RequestMapping("/app/db/documentinfo")
public class DocumentInfoController {
	@Autowired
	private AppConfig appConfig;
	@Autowired
	private DocumentInfoService documentInfoService;
	@Autowired
	private DocumentFileService documentFileService;
	@Autowired
	private AdminSetService adminSetService;
	@Autowired
	private RoleSetService roleSetService;
	@Autowired
	private BaseAppUserService baseAppUserService;
	@Autowired
	private DocumentCbjlService documentCbjlService;
	@Autowired
	private DocumentBjjlService documentBjjlService;
	@Autowired
	private SubDocTrackingService subDocTrackingService;
	@Autowired
	private SubDocInfoService subDocInfoService;
	@Autowired
	private DocumentReadService documentReadService;
	
	
	/**
	 * 文件上传保存
	 * @param idpdf 主记录id(documentInfo的id)
	 * @param pdf 文件
	 */
	@ResponseBody
	@RequestMapping("/uploadFile")
	public void savePDF(String idpdf,@RequestParam(value = "pdf", required = false) MultipartFile pdf){
			String formatDownPath = "";//版式文件下载路径
			String streamId = null;//流式文件id
			String formatId  = null;//版式文件id
			JSONObject json = new JSONObject();
			if(StringUtils.isNotBlank(idpdf)) {				
				if (pdf != null && pdf.getSize() > 0) {
					String fileName=pdf.getOriginalFilename();
					//获取文件后缀
					String fileType =fileName.substring(fileName.lastIndexOf(".")+1);
					//如果文件是流式则流式转换为版式
					if(!StringUtils.equals("ofd", fileType)){
						streamId = FileBaseUtil.fileServiceUpload(pdf);
						HTTPFile hf = new HTTPFile(streamId);
						try {
							String path = appConfig.getLocalFilePath() + UUIDUtils.random() + "." + hf.getSuffix();
							try {
								FileUtils.moveFile(new File(hf.getFilePath()) , new File(path));
							} catch (IOException e) {
								e.printStackTrace();
							}
							if(StringUtils.isNotBlank(path)){
								formatId = OfdTransferUtil.convertLocalFileToOFD(path);
							}
							//删除本地的临时流式文件
							if(new File(path).exists()){
								new File(path).delete();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else{
						formatId=FileBaseUtil.fileServiceUpload(pdf);	
					}
					if(StringUtils.isNotBlank(formatId)) {
						//获取版式文件的下载路径
						HTTPFile httpFiles = new HTTPFile(formatId);
						if(httpFiles!=null) {
							formatDownPath = httpFiles.getAssginDownloadURL(true);
						}
						//保存文件相关数据
						DocumentFile file=new DocumentFile();
						file.setId(UUIDUtils.random());
						file.setDocInfoId(idpdf);
						file.setSort(documentFileService.queryMinSort(idpdf));
						file.setFileName(fileName);
						file.setCreatedTime(new Date());
						if(StringUtils.isNotBlank(streamId)) {
							file.setFileServerStreamId(streamId);
						}
						file.setFileServerFormatId(formatId);
						documentFileService.save(file);
					}
					json.put("smjId", formatId);
					json.put("smjFilePath", formatDownPath);
					json.put("result", "success");
				}
			}else {
				json.put("result", "fail");
			}
			Response.json(json);
	}
	
	
	/**
	 * 登记录入列表查询
	 */
	@ResponseBody
	@RequestMapping("/list")
	public void list(Integer page, Integer pagesize,String search,String documentStatus){
		Map<String, Object> map = new HashMap<>();
		map.put("search", search);
		map.put("docStatus", documentStatus);
		PageHelper.startPage(page, pagesize);
		List<DocumentInfo> infoList = documentInfoService.queryList(map);
		GwPageUtils pageUtil = new GwPageUtils(infoList);
		Response.json(pageUtil);
	}
	
	/**
	 * 录入界面列表筛选数量统计
	 * @param search 搜索参数
	 */
	@ResponseBody
	@RequestMapping("/numsList")
	public void numsList(String search) {
		int [] arr = {0,0,0};
		Map<String, Object> map = new HashMap<>();
		map.put("search", search);
		List<DocumentInfo> infoList = documentInfoService.queryList(map);
		if(infoList != null && infoList.size()>0) {
			arr[0]=infoList.size();
			for (DocumentInfo documentInfo : infoList) {
				if(documentInfo.getFirstZbTime() != null) {
					arr[2] += 1;
				}else {
					arr[1] += 1;
				}
			}
		}
		Response.json(arr);
	}
	
	/**
	 * 办理反馈列表查询
	 */
	@ResponseBody
	@RequestMapping("/replyList")
	public void replyList(Integer page, Integer pagesize,String search,String status,String typeId){
		String loginUserId=CurrentUser.getUserId();
		String adminType = null;//管理员类型（1：部管理员；2：局管理员）
		String roleType = DbDefined.ROLE_6;//角色标识（1：首长；2：首长秘书；3：局长；4：局秘书；5：处长；6：参谋;）
		//当前登录人的管理员类型
		Map<String, Object> adminMap = new HashMap<>();
		adminMap.put("userId", loginUserId);
		List<AdminSet> adminList = adminSetService.queryList(adminMap);
		if(adminList != null && adminList.size()>0) {
			adminType = adminList.get(0).getAdminType();
		}
		//当前登录人的角色
		Map<String, Object> roleMap = new HashMap<>();
		roleMap.put("userId", loginUserId);
		List<RoleSet> roleList = roleSetService.queryList(roleMap);
		if(roleList != null && roleList.size()>0) {
			roleType = roleList.get(0).getRoleFlag();
		}
		List<DocumentInfo> infoList =null;
		Map<String, Object> map = new HashMap<>();
		map.put("docStatus", "2");
		map.put("type", typeId);
		map.put("search", search);
		if(StringUtils.isNotBlank(status)) {
			if(StringUtils.equals("update", status)) {
				map.put("loginUserId", loginUserId);
				map.put("hasUpdate", status);
			}else {
				map.put("state", status);
			}
		}
		if (!StringUtils.equals("1", adminType) && !StringUtils.equals("2", adminType)
				&& !StringUtils.equals("1", roleType) && !StringUtils.equals("3", roleType)) {
			map.put("loginUserId", loginUserId);
			PageHelper.startPage(page, pagesize);
			infoList = documentInfoService.queryPersonList(map);
		} else {
			if(StringUtils.equals("2", adminType)|| StringUtils.equals("3", roleType)) {
				String orgId = baseAppUserService.getBareauByUserId(loginUserId);
				if(StringUtils.isNotBlank(orgId)) {
					map.put("orgid", orgId);
				}
			}
			PageHelper.startPage(page, pagesize);
			infoList = documentInfoService.queryList(map);
		}
		for (DocumentInfo info : infoList) {
			//是否已读
			Map<String, Object> readMap = new HashMap<>();
			readMap.put("userId", loginUserId);
			readMap.put("infoId", info.getId());
			List<DocumentRead> list = documentReadService.queryList(readMap);
			
			//催办为0，未读，最新反馈字段有值则标识为已更新
			if(list.size()==0 && StringUtils.equals("0", info.getCuibanFlag()) && StringUtils.isNotBlank(info.getLatestReply())) {
				info.setUpdateFlag("1");
			}
		}
		GwPageUtils pageUtil = new GwPageUtils(infoList);
		Response.json(pageUtil);
	}
	
	/**
	 * 办理反馈数量统计
	 * @param search 搜索
	 * @param typeId 文件类型
	 */
	@ResponseBody
	@RequestMapping("/replyNums")
	public void replyNums(String search,String typeId) {
		int [] arr = {0,0,0,0,0};
		String loginUserId=CurrentUser.getUserId();
		String adminType = null;//管理员类型（1：部管理员；2：局管理员）
		String roleType = DbDefined.ROLE_6;//角色标识（1：首长；2：首长秘书；3：局长；4：局秘书；5：处长；6：参谋;）
		//当前登录人的管理员类型
		Map<String, Object> adminMap = new HashMap<>();
		adminMap.put("userId", loginUserId);
		List<AdminSet> adminList = adminSetService.queryList(adminMap);
		if(adminList != null && adminList.size()>0) {
			adminType = adminList.get(0).getAdminType();
		}
		//当前登录人的角色
		Map<String, Object> roleMap = new HashMap<>();
		roleMap.put("userId", loginUserId);
		List<RoleSet> roleList = roleSetService.queryList(roleMap);
		if(roleList != null && roleList.size()>0) {
			roleType = roleList.get(0).getRoleFlag();
		}
		List<DocumentInfo> infoList =null;
		Map<String, Object> map = new HashMap<>();
		map.put("docStatus", "2");
		map.put("search", search);
		map.put("type", typeId);
		if (!StringUtils.equals("1", adminType) && !StringUtils.equals("2", adminType)
				&& !StringUtils.equals("1", roleType) && !StringUtils.equals("3", roleType)) {
			map.put("loginUserId", loginUserId);
			infoList = documentInfoService.queryPersonList(map);
		} else {
			if(StringUtils.equals("2", adminType)|| StringUtils.equals("3", roleType)) {
				String orgId = baseAppUserService.getBareauByUserId(loginUserId);
				if(StringUtils.isNotBlank(orgId)) {
					map.put("orgid", orgId);
				}
			}
			infoList = documentInfoService.queryList(map);
		}
		arr[0]=infoList.size();
		for (DocumentInfo info : infoList) {
			Integer restatus = info.getStatus();
			if(1==restatus) {
				arr[1]+=1;
			}
			if(2==restatus){
				arr[2]+=1;
			}
			if(3==restatus){
				arr[3]+=1;
			}
			//是否已读
			Map<String, Object> readMap = new HashMap<>();
			readMap.put("userId", loginUserId);
			readMap.put("infoId", info.getId());
			List<DocumentRead> list = documentReadService.queryList(readMap);
			
			//是否有完成的催办
			Map<String, Object> cuiBanMap = new HashMap<>();
			cuiBanMap.put("infoId", info.getId());
			cuiBanMap.put("finishFlag", 1);
			List<DocumentCbjl> cuiBanList = documentCbjlService.queryList(cuiBanMap);
			if(list.size()==0 && StringUtils.equals("0", info.getCuibanFlag()) && cuiBanList.size()>0) {
				arr[4]+=1;
			}
		}
		Response.json(arr);
	}	
	
	/**
	 * 获取文件信息
	 * @param id 文件id
	 */
	@ResponseBody
	@RequestMapping("/info")
	public void info(String id){
		DocumentInfo documentInfo = documentInfoService.queryObject(id);
		Response.json(documentInfo);
	}
	
	/**
	 * 新增或修改
	 */
	@ResponseBody
	@RequestMapping("/update")
	public void update(DocumentInfo documentInfo){
		String uuid="";
		JSONObject jo=new JSONObject();
		if (StringUtils.isNotBlank(documentInfo.getId())) {
			jo.put("id",documentInfo.getId());
			documentInfoService.update(documentInfo);
		} else {
			uuid=UUIDUtils.random();
			documentInfo.setId(uuid);
			documentInfo.setStatus(0);
			documentInfo.setCreatedTime(new Date());
			documentInfoService.save(documentInfo);
			jo.put("id",uuid);
		}
		Response.json(jo);
	}
	
	/**
	 * 按钮显示参数
	 * @param id 主文件id
	 */
	@ResponseBody
	@RequestMapping("/buttonParam")
	public void buttonParam(String id){
		String loginUserId=CurrentUser.getUserId();
		String adminType = "0";//管理员类型（1：部管理员；2：局管理员）
		String roleType = DbDefined.ROLE_6;//角色标识（1：首长；2：首长秘书；3：局长；4：局秘书；5：处长；6：参谋;）
		boolean cuiBanBtn =false;
		boolean zhuanBanBtn =false;
		boolean quXiaoBtn =false;
		
		//当前登录人的管理员类型
		Map<String, Object> adminMap = new HashMap<>();
		adminMap.put("userId", loginUserId);
		List<AdminSet> adminList = adminSetService.queryList(adminMap);
		if(adminList != null && adminList.size()>0) {
			adminType = adminList.get(0).getAdminType();
		}
		//当前登录人的角色
		Map<String, Object> roleMap = new HashMap<>();
		roleMap.put("userId", loginUserId);
		List<RoleSet> roleList = roleSetService.queryList(roleMap);
		if(roleList != null && roleList.size()>0) {
			roleType = roleList.get(0).getRoleFlag();
		}
		//获取文件信息
		DocumentInfo documentInfo = documentInfoService.queryObject(id);
		if(documentInfo != null) {
			Integer status = documentInfo.getStatus();
			String cuibanFlag = documentInfo.getCuibanFlag();
			if(StringUtils.equals("1", adminType)) {
				zhuanBanBtn=true;
				if(status>1) {
					quXiaoBtn=true;
				}
			}
			if(!StringUtils.equals("1", cuibanFlag) && (StringUtils.equals("1", adminType) || StringUtils.equals("1", roleType))) {
				cuiBanBtn=true;
			}
		}
		if(quXiaoBtn) {
			 cuiBanBtn =false;
			 zhuanBanBtn =false;
		}
		JSONObject json= new JSONObject();
		json.put("cuiBanBtn", cuiBanBtn);
		json.put("quXiaoBtn", quXiaoBtn);
		json.put("zhuanBanBtn", zhuanBanBtn);
		Response.json(json);
	}
	
	/**
	 * 取消办结操作
	 * @param id 主文件id
	 */
	@ResponseBody
	@RequestMapping("/cancleOperation")
	public void cancleOperation(String id){
		JSONObject json= new JSONObject();
		DocumentInfo info = documentInfoService.queryObject(id);
		Integer subStatus = DbDocStatusDefined.BAN_LI_ZHONG;
		//取最后一个办结或落实的局的主文件
		//文件局内状态（1:待转办；3：退回修改；5：待落实；7：待审批；9：办理中；10：建议办结；11：建议落实；12：办结；1:3：常态落实）
		SubDocInfo lastEndSubInfo = subDocInfoService.queryLastEndSubInfo(id);
		String subId =lastEndSubInfo.getId();
		if(StringUtils.isNotBlank(subId)) {
			//添加办结记录
			DocumentBjjl newBjjl=new DocumentBjjl();
			newBjjl.setContent("取消办结");
			newBjjl.setInfoId(id);
			newBjjl.setSubId(subId);
			documentBjjlService.save(newBjjl);				
			//取最后一条流转记录
			SubDocTracking tracking = subDocTrackingService.queryLatestRecord(subId);
			//trackingType（1：转办；2：审批流转；3：退回）
			String trackingType = tracking.getTrackingType();
			//如果最后一条流转为审批，接收人为承办人则文件为办理中，否则文件为待审批
			if(StringUtils.equals("2",trackingType)) {
				if(!StringUtils.equals(lastEndSubInfo.getUndertaker(), tracking.getReceiverId())) {
					subStatus = DbDocStatusDefined.DAI_SHEN_PI;
				}
			//如果最后一条流转为退回修改，则文件状态为退回修改
			}else if(StringUtils.equals("3",trackingType)){
				subStatus = DbDocStatusDefined.TUIHUI_XIUGAI;
			}
			//修改最后一个局的文件状态
			lastEndSubInfo.setDocStatus(subStatus);
			subDocInfoService.update(lastEndSubInfo);
			//主文件变为办理中
			info.setStatus(1);
			documentInfoService.update(info);
			json.put("result", "success");
		}else {
			json.put("result", "fail");
		}
		Response.json(json);
	}
	
	@ResponseBody
	@RequestMapping("/cheHuiOperation")
	public void cheHuiOperation(String id){
		
	}
	
	/**
	 * 批量已读
	 * @param ids 主文件id
	 */
	@ResponseBody
	@RequestMapping("/batchRead")
	public void batchRead(String ids){
		String[] idArry = ids.split(",");
		for (String id : idArry) {
			DocumentRead read=new DocumentRead();
			read.setInfoId(id);
			documentReadService.save(read);
		}
		Response.json("result", "success");
	}
	
	/**
	 * 删除文件
	 * @param id 主文件id
	 */
	@ResponseBody
	@RequestMapping("/delete")
	public void delete(String id){
		documentInfoService.delete(id);
		Response.json("result", "success");
	}
	/**
	 * 获取当前登录人信息
	 */
	@ResponseBody
	@RequestMapping("/getLoginUser")
	public void getLoginUser() {
		JSONObject jo=new JSONObject();
		jo.put("userId", CurrentUser.getUserId());
		jo.put("userName", CurrentUser.getUsername());
		Response.json(jo);
	}
	
	/**
	 * 部管理员添加催办
	 * @param textarea 催办内容
	 * @param infoId 主文件id
	 */
	@ResponseBody
	@RequestMapping("/saveCuiBan")
	public void saveCuiBan(String textarea,String infoId){
		//保存催办历史
		DocumentCbjl cb=new DocumentCbjl();
		cb.setUrgeContent(textarea);
		cb.setInfoId(infoId);
		documentCbjlService.save(cb);
		//主文件标识催办
		DocumentInfo info=documentInfoService.queryObject(infoId);
		info.setCuibanFlag("1");
		documentInfoService.update(info);
		Response.json("result", "success");
	}
	
	/**
	 * 获取最新催办数据
	 * @param infoId 主文件id
	 */
	@ResponseBody
	@RequestMapping("/getLatestCuiBan")
	public void getLatestCuiBan(String infoId){
		DocumentCbjl cuiBan =null;
		DocumentInfo info=documentInfoService.queryObject(infoId);
		if(StringUtils.equals("1", info.getCuibanFlag())) {
			cuiBan= documentCbjlService.queryLatestCuiBan(infoId);
		}
		Response.json(cuiBan);
	}
	
	/**
	 * 催办记录列表
	 * @param infoId 主文件id
	 */
	@ResponseBody
	@RequestMapping("/getCuiBanlist")
	public void getCuiBanlist(String infoId){
		List<DocumentCbjl> list=null;
		if(StringUtils.isNotBlank(infoId)) {
			Map<String, Object> map = new HashMap<>();
			map.put("infoId", infoId);
			//查询列表数据
			list = documentCbjlService.queryList(map);
		}
		Response.json(list);
	}
	
	/**
	 * 办结记录列表
	 * @param infoId
	 */
	@ResponseBody
	@RequestMapping("/getBanJieList")
	public void getBanJieList(String infoId){
		List<DocumentBjjl> bjjlList=null;
		if(StringUtils.isNotBlank(infoId)) {
			Map<String, Object> map = new HashMap<>();
			map.put("infoId", infoId);
			//查询列表数据
			bjjlList = documentBjjlService.queryList(map);
		}
		Response.json(bjjlList);
	}
}
