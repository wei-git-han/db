package com.css.app.db.business.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.fastjson.JSONObject;
import com.css.addbase.apporgan.entity.BaseAppOrgan;
import com.css.addbase.apporgan.entity.BaseAppUser;
import com.css.addbase.apporgan.service.BaseAppOrganService;
import com.css.addbase.apporgan.service.BaseAppUserService;
import com.css.addbase.apporgmapped.service.BaseAppOrgMappedService;
import com.css.app.db.business.entity.DocumentInfo;
import com.css.app.db.business.entity.DocumentSzps;
import com.css.app.db.business.entity.ReplyExplain;
import com.css.app.db.business.entity.SubDocInfo;
import com.css.app.db.business.entity.SubDocTracking;
import com.css.app.db.business.service.DocumentInfoService;
import com.css.app.db.business.service.DocumentSzpsService;
import com.css.app.db.business.service.ImportExcleService;
import com.css.app.db.business.service.ReplyExplainService;
import com.css.app.db.business.service.SubDocInfoService;
import com.css.app.db.business.service.SubDocTrackingService;
import com.css.app.db.config.entity.DocumentDic;
import com.css.app.db.config.service.DocumentDicService;
import com.css.app.db.util.DbDefined;
import com.css.app.db.util.DbDocStatusDefined;
import com.css.base.utils.CurrentUser;
import com.css.base.utils.Response;
import com.css.base.utils.StringUtils;
import com.css.base.utils.UUIDUtils;

/**
 * 导入wps-excle
 * @author weizy
 *
 */
@RestController
@RequestMapping("/app/db/import")
public class ImportController{
    @Autowired
	DocumentInfoService documentInfoService;
    @Autowired
    DocumentSzpsService documentSzpsService;
    @Autowired
    ReplyExplainService replyExplainService;
    @Autowired
    SubDocInfoService subDocInfoService;
    @Autowired
    BaseAppOrgMappedService baseAppOrgMappedService;
    @Autowired
	BaseAppOrganService baseAppOrganService;
    @Autowired
    ImportExcleService importExcleService;    
    @Autowired
    BaseAppUserService baseAppUserService;    
    @Autowired
	private SubDocTrackingService subDocTrackingService;
	@Autowired
	private DocumentDicService documentDicService;

	/**
	 * 导入
	 * 创建list来存储读取wps键值对对应情况
	 *  0：军委办件号；1：文件标题；2、批示指示内容；3、督办落实情况；4、办理状态
	 *  5、承办单位/人员；6、docTypeId；7、印发时间；8、文件号； 9、工作分工内容；
	 */
	@Transactional
	@RequestMapping("/importExcle")
	public void importExcle(MultipartFile file) {
		JSONObject json=new JSONObject();
		try {
			List<List<Object>> excleDates = importExcleService.getExcleDate(file);
			for (List<Object> excleDate : excleDates) {
				//添加主文件信息
				String infoId = UUIDUtils.random();
				Integer status = (Integer) excleDate.get(4);
				String docTypeId = String.valueOf(excleDate.get(6));
				DocumentInfo docInfo = new DocumentInfo();
				docInfo.setId(infoId);
				docInfo.setBanjianNumber(String.valueOf(excleDate.get(0)));// 0：军委办件号
				docInfo.setDocTitle(String.valueOf(excleDate.get(1)));// 1：文件标题
				docInfo.setStatus(status);// 8、办理状态
				if(StringUtils.equals("3", docTypeId)||StringUtils.equals("5", docTypeId)||StringUtils.equals("6", docTypeId)) {
					docInfo.setPrintDate(String.valueOf(excleDate.get(7)));
					docInfo.setDocCode(String.valueOf(excleDate.get(8)));
					Object object = excleDate.get(9);
					String valueOf = String.valueOf(object);
					docInfo.setJobContent(valueOf);
				}
				docInfo.setDocTypeId(docTypeId);
				Map<String, Object> dicMap= new HashMap<>();
				dicMap.put("dicType", DbDefined.DOCUMENT_TYPE);
				dicMap.put("value", docTypeId);
				List<DocumentDic> dicList = documentDicService.queryList(dicMap);
				if(dicList != null && dicList.size()>0) {
					docInfo.setDocTypeName(dicList.get(0).getText());
				}
				docInfo.setFirstZbTime(new Date());
				docInfo.setFinishTime(new Date());
				// 为主文件创建批示指示内容
				if((StringUtils.equals("1", docTypeId)||StringUtils.equals("2", docTypeId)||StringUtils.equals("4", docTypeId))&&!"".equals(excleDate.get(2))) {
					List<Map<String, String>> pszsnrList = (List<Map<String, String>>) excleDate.get(2);// 批示指示内容
					for (Map<String, String> pszsnrMap : pszsnrList) {
						DocumentSzps docSzps = new DocumentSzps();
						String pszssj = pszsnrMap.get("pszssj");
						String pizsnr = pszsnrMap.get("pizsnr");
						docSzps.setId(UUIDUtils.random());
						docSzps.setInfoId(infoId);
						docSzps.setUserName(pszsnrMap.get("szxm"));
						docSzps.setCreatedTime(pszssj);
						docSzps.setLeaderComment(pizsnr);
						documentSzpsService.save(docSzps);
					}
				}
				//创建分支局主文件记录
				List<Map<String, String>> undertakeLis = (List<Map<String, String>>) excleDate.get(5);// 承办单位和人员
				for (Map<String, String> undertakerMap : undertakeLis) {
					SubDocInfo subDocInfo = new SubDocInfo();
					String subId=UUIDUtils.random();
					subDocInfo.setId(subId);
					subDocInfo.setInfoId(infoId);
					String cbdw = undertakerMap.get("cbdw");// 承办单位；
					String cbrName = undertakerMap.get("cbr"); // 承办人；
					String cbrdh = undertakerMap.get("cbrdh");
					//分局名称和分局id
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("name", cbdw);
					List<BaseAppOrgan> list = baseAppOrganService.queryList(map);
					String subDeptId="";
					if(list != null && list.size()>0) {
						subDeptId=list.get(0).getId();
					}
					subDocInfo.setSubDeptId(subDeptId);
					subDocInfo.setSubDeptName(cbdw);
					subDocInfo.setCreatedTime(new Date());
					//文件状态（多局情况需求要求各局状态一样，但对于办结和常态落实的文件取消办结会有问题：因为取消办结会回滚到最后一个局的状态，但状态都一样无法辨别最后一个局，故会随便回滚到一个局）
					if(1 == status) {
						subDocInfo.setDocStatus(DbDocStatusDefined.BAN_LI_ZHONG);
					}else if(2 == status) {
						subDocInfo.setDocStatus(DbDocStatusDefined.BAN_JIE);
					}else if(3 == status){
						subDocInfo.setDocStatus(DbDocStatusDefined.CHANG_TAI_LUO_SHI);
					}
					if(StringUtils.isNotBlank(cbdw)) {
						if(StringUtils.isNotBlank(cbrName)) {
							//承办人名称和承办人id(要查某个局下的人)
							List<BaseAppUser> users = baseAppUserService.selectUserByNameAndUnitId(cbrName, subDeptId);
							String userId = "";
							if (users != null && users.size() > 0) {
								if(users.size()>1) {
									System.out.println("局内存在重名人员:"+cbrName+",此局数据未录入");
									json.put("msg", "局内存在重名人员:"+cbrName+",此局数据未录入");
									continue;
								}else {
									userId = users.get(0).getUserId();
								}
							}
							subDocInfo.setUndertaker(userId);
							subDocInfo.setUndertakerName(cbrName);
							subDocInfo.setUndertakerPhone(cbrdh);
							if(!"".equals(excleDate.get(3))) {
							//添加主分支的落实情况
							List<Map<String, String>> dblsqkLis = (List<Map<String, String>>) excleDate.get(3);//督办落实情况
							for (int i = 0; i < dblsqkLis.size(); i++) {
								String dblsqkrydw = dblsqkLis.get(i).get("dblsqkrydw");
								if(StringUtils.equals(cbdw, dblsqkrydw)) {
									String replyUserId="";
									ReplyExplain replyExplain = new ReplyExplain();
									String dblsqkryxm = dblsqkLis.get(i).get("dblsqkryxm");
									String dblsqksj = dblsqkLis.get(i).get("dblsqksj");// 7、督办落实情况时间；
									String dblsqknr = dblsqkLis.get(i).get("dblsqknr");// 8、督办落实情况内容
									List<BaseAppUser> queryUserByName = baseAppUserService.queryUserByName(dblsqkryxm);
									if (queryUserByName != null && queryUserByName.size() > 0) {
										replyUserId = queryUserByName.get(0).getUserId();
										replyExplain.setUserId(replyUserId);
									}
									replyExplain.setId(UUIDUtils.random());
									replyExplain.setInfoId(infoId);
									replyExplain.setSubId(subId);
									replyExplain.setUserName(dblsqkryxm);
									if(StringUtils.isNotBlank(dblsqksj)) {
										replyExplain.setCreatedTime(new SimpleDateFormat("yyyy-MM-dd").parse(dblsqksj));
									}
									replyExplain.setReplyContent(dblsqknr);
									if(StringUtils.equals(cbrName, dblsqkryxm)) {
										replyExplain.setCbrFlag("1");
									}
									replyExplain.setReVersion("1");
									replyExplain.setShowFlag("1");
									replyExplain.setSubDeptId(subDeptId);
									replyExplain.setSubDeptName(cbdw);
									replyExplain.setTeamId(UUIDUtils.random());
									replyExplainService.save(replyExplain);
									//添加流转记录
									SubDocTracking tracking = new SubDocTracking();
									tracking.setSubId(subId);
									String loginUserId=CurrentUser.getUserId();
									String loginUserName=CurrentUser.getUsername();
									String loginUserDeptId=CurrentUser.getDepartmentId();
									String loginUserDeptName=CurrentUser.getOrgName();
									tracking.setSenderId(loginUserId);
									tracking.setSenderName(loginUserName);
									tracking.setSenDeptId(loginUserDeptId);
									tracking.setSenDeptName(loginUserDeptName);
									tracking.setReceiverId(replyUserId);
									BaseAppUser user = baseAppUserService.queryObject(replyUserId);
									if(user != null) {
										BaseAppOrgan organ = baseAppOrganService.queryObject(user.getOrganid());
										if(organ != null) {
											tracking.setRecDeptId(user.getOrganid());
											tracking.setRecDeptName(organ.getName());
										}
									}
									tracking.setReceiverName(cbrName);
									tracking.setTrackingType("4");
									subDocTrackingService.save(tracking);
								}
								if(i==(dblsqkLis.size()-1)) {
									docInfo.setLatestReply(dblsqkLis.get(i).get("dblsqknr"));
									docInfo.setLatestSubDept(cbdw);
									docInfo.setLatestUndertaker(cbrName);
									docInfo.setLatestReplyTime(new SimpleDateFormat("yyyy-MM-dd").parse(dblsqkLis.get(i).get("dblsqksj")));
								}
							}
						}
						}else {
							subDocInfo.setDocStatus(DbDocStatusDefined.DAI_ZHUAN_BAN);
						}
						subDocInfoService.save(subDocInfo);
					}					
				}
				documentInfoService.save(docInfo);
			}
			json.put("result","success");
		   } catch (Exception e) {
			e.printStackTrace();
			json.put("result","fail");
		}
	    Response.json(json);
	}
}
