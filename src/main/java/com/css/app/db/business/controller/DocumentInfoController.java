package com.css.app.db.business.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.css.addbase.AppConfig;
import com.css.addbase.FileBaseUtil;
import com.css.addbase.appconfig.entity.BaseAppConfig;
import com.css.addbase.appconfig.service.BaseAppConfigService;
import com.css.addbase.apporgan.entity.BaseAppOrgan;
import com.css.addbase.apporgan.entity.BaseAppUser;
import com.css.addbase.apporgan.service.BaseAppOrganService;
import com.css.addbase.apporgan.service.BaseAppUserService;
import com.css.addbase.constant.AppConstant;
import com.css.addbase.suwell.OfdTransferUtil;
import com.css.app.db.business.entity.DocumentBjjl;
import com.css.app.db.business.entity.DocumentCbjl;
import com.css.app.db.business.entity.DocumentFile;
import com.css.app.db.business.entity.DocumentInfo;
import com.css.app.db.business.entity.DocumentRead;
import com.css.app.db.business.entity.DocumentSzps;
import com.css.app.db.business.entity.SubDocInfo;
import com.css.app.db.business.entity.SubDocTracking;
import com.css.app.db.business.service.DocumentBjjlService;
import com.css.app.db.business.service.DocumentCbjlService;
import com.css.app.db.business.service.DocumentFileService;
import com.css.app.db.business.service.DocumentInfoService;
import com.css.app.db.business.service.DocumentReadService;
import com.css.app.db.business.service.DocumentSzpsService;
import com.css.app.db.business.service.SubDocInfoService;
import com.css.app.db.business.service.SubDocTrackingService;
import com.css.app.db.config.entity.DocumentDic;
import com.css.app.db.config.service.AdminSetService;
import com.css.app.db.config.service.RoleSetService;
import com.css.app.db.util.DbDefined;
import com.css.app.db.util.DbDocStatusDefined;
import com.css.base.utils.CurrentUser;
import com.css.base.utils.DateUtil;
import com.css.base.utils.GwPageUtils;
import com.css.base.utils.Response;
import com.css.base.utils.UUIDUtils;
import com.github.pagehelper.PageHelper;

import cn.com.css.filestore.impl.HTTPFile;
import dm.jdbc.util.StringUtil;

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
	@Autowired
	private DocumentSzpsService documentSzpsService;
	@Autowired
	private BaseAppConfigService baseAppConfigService;
	@Autowired
	private BaseAppOrganService baseAppOrganService;
	
	@Value("${uploadFile.path}")
	private  String filePath;
	
	@Value("${localAddress}")
	private  String localAddress;

	/**
	 * 文件上传保存
	 * 
	 * @param idpdf
	 *            主记录id(documentInfo的id)
	 * @param pdf
	 *            文件
	 */
	@ResponseBody
	@RequestMapping("/uploadFile")
	public void savePDF(String idpdf, @RequestParam(required = false) MultipartFile[] pdf) {
		//String path1 = appConfig.getLocalFilePath();
		String formatDownPath = "";// 版式文件下载路径
		String retFormatId = null;// 返回的版式文件id
		String streamId = null;// 流式文件id
		String formatId = null;// 版式文件id
		JSONObject json = new JSONObject();
		String type ="1";
		String showStreamDownload = "1";
		if (StringUtils.isNotBlank(idpdf)) {
			if (pdf != null && pdf.length > 0) {
				for (int i = 0; i < pdf.length; i++) {
					String fileName = pdf[i].getOriginalFilename();
					// 获取文件后缀
					String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
					// 如果文件是流式则流式转换为版式
					if (!StringUtils.equals("ofd", fileType)) {
						streamId = FileBaseUtil.fileServiceUploadByFilePathLiuShi(pdf[i],filePath,localAddress);
						try {
							if (StringUtils.isNotBlank(streamId)) {
								formatId = OfdTransferUtil.convertLocalFileToOFDPath(streamId,localAddress);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						formatId = FileBaseUtil.fileServiceUploadByFilePath(pdf[i],filePath,localAddress);
						showStreamDownload ="2";
						type = "2";
					}
					if (StringUtils.isNotBlank(formatId)) {
						// 保存文件相关数据
						DocumentFile file = new DocumentFile();
						file.setId(UUIDUtils.random());
						file.setDocInfoId(idpdf);
						file.setSort(documentFileService.queryMinSort(idpdf));
						file.setFileName(fileName);
						file.setCreatedTime(new Date());
						if (StringUtils.isNotBlank(streamId)) {
							file.setFileServerStreamId(streamId);
						}
						file.setFileServerFormatId(formatId);
						documentFileService.save(file);
					}
				}
				json.put("smjId", formatId);
				json.put("smjFilePath", formatId);
				json.put("result", "success");
	/*			json.put("url", formatId);
				json.put("type", type);
				json.put("showStreamDownload", showStreamDownload);*/
			}
		} else {
			json.put("result", "fail");
		}
		Response.json(json);
	}

	/**
	 * 登记录入列表查询
	 */
	@ResponseBody
	@RequestMapping("/list")
	public void list(Integer page, Integer pagesize, String search, String documentStatus) {
		Map<String, Object> map = new HashMap<>();
		map.put("search", search);
		map.put("docStatus", documentStatus);
		PageHelper.startPage(page, pagesize);
		List<DocumentInfo> infoList = documentInfoService.queryNewList(map);
		for (DocumentInfo documentInfo : infoList) {
			Map<String, Object> szpsMap = new HashMap<>();
			szpsMap.put("infoId", documentInfo.getId());
			List<DocumentSzps> list = documentSzpsService.queryList(szpsMap);
			documentInfo.setSzpslist(list);
		}
		GwPageUtils pageUtil = new GwPageUtils(infoList);
		Response.json(pageUtil);
	}

	/**
	 * 录入界面列表筛选数量统计
	 * 
	 * @param search
	 *            搜索参数
	 */
	@ResponseBody
	@RequestMapping("/numsList")
	public void numsList(String search) {
		int[] arr = { 0, 0, 0 };
		Map<String, Object> map = new HashMap<>();
		map.put("search", search);
		List<DocumentInfo> infoList = documentInfoService.queryList(map);
		if (infoList != null && infoList.size() > 0) {
			arr[0] = infoList.size();
			for (DocumentInfo documentInfo : infoList) {
				if (documentInfo.getFirstZbTime() != null) {
					arr[2] += 1;
				} else {
					arr[1] += 1;
				}
			}
		}
		Response.json(arr);
	}

	/**
	 * 注：修改列表查询的参数一定要对应的修改红点显示/getDicByTypet和统计数/replyNums
	 * 
	 * @description:办理反馈列表查询
	 * @param search
	 *            搜索参数,status 状态参数,typeId 文件类型,orgid 统计图传的局id,统计图传的月,
	 * @param startDate
	 *            导出的首长（第一个最大的首长）批示（或转办）开始时间,endDate
	 *            导出的首长（第一个最大的首长）批示（或转办）结束时间,initFlag 导出液的初始化加载标识
	 * @param psStartDate
	 *            办理反馈高级搜索的指示开始时间,psEndDate 办理反馈高级搜索的指示开始时间
	 * @author:zhangyw
	 * @date:2019年6月19日
	 * @Version v1.0
	 */
	@ResponseBody
	@RequestMapping("/replyList")
	public void replyList(Integer page, Integer pagesize, String search, String status, String typeId, String orgid,
			String month, String startDate, String endDate, String initFlag, String title, String leaderId,
			String period, String psStartDate, String psEndDate, String year, String jnType) {
		List<DocumentInfo> infoList = null;
		if (!StringUtils.equals("1", initFlag)) {// initFlag为1 为导出页的初始化加载
			String loginUserId = CurrentUser.getUserId();
			String dateStr = null;
			if (StringUtils.isNotBlank(year)) {
				dateStr = year;
			} else if (!StringUtils.isEmpty(month) && StringUtil.equals("all", month)) {
				dateStr = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE).substring(0, 4);// 2020
			} else if (!StringUtils.isEmpty(month)) {
				dateStr = LocalDate.now().withMonth(Integer.parseInt(month)).format(DateTimeFormatter.ISO_LOCAL_DATE)
						.substring(0, 7);// 2020-01
			}
			// 当前登录人的管理员类型(0:超级管理员 ;1：部管理员；2：局管理员；3：即是部管理员又是局管理员)
			String adminType = adminSetService.getAdminTypeByUserId(loginUserId);
			// 当前登录人的角色（1：首长；2：首长秘书；3：局长；4：局秘书；5：处长；6：参谋;）
			String roleType = roleSetService.getRoleTypeByUserId(loginUserId);
			Map<String, Object> map = new HashMap<>();
			map.put("type", typeId);
			map.put("search", search);
			map.put("orgId", orgid);
			map.put("year", dateStr);
			map.put("title", title);
			map.put("jnType", jnType);
			if (StringUtils.isNotBlank(status)) {
				if (StringUtils.equals("update", status)) {
					map.put("loginUserId", loginUserId);
					map.put("hasUpdate", status);
				} else {
					map.put("status", status);
				}
			}

			if (StringUtils.equals("3", typeId) || StringUtils.equals("5", typeId) || StringUtils.equals("6", typeId)) {
				// 导出的时间段搜索转办时间
				if (StringUtils.isNotBlank(startDate) || StringUtils.isNotBlank(endDate)) {
					if (StringUtils.isNotBlank(startDate)) {
						map.put("zbStartDate", startDate + " 00:00");
					}
					if (StringUtils.isNotBlank(endDate)) {
						map.put("zbEndDate", endDate + " 23:59");
					}
				}
			} else {
				// 导出的时间段搜索批示指示时间
				if (StringUtils.isNotBlank(startDate) || StringUtils.isNotBlank(endDate)) {
					if (StringUtils.isNotBlank(startDate)) {
						map.put("startDate", startDate);
					}
					if (StringUtils.isNotBlank(endDate)) {
						map.put("endDate", endDate);
					}
				}
				// 办理反馈高级搜索批示指示时间:
				// 1.如果高级搜索中选择了首长，则按所选首长的批示时间筛选
				if (StringUtils.isNotBlank(leaderId)) {
					map.put("leaderId", leaderId);
					if (StringUtils.isNotBlank(psStartDate) || StringUtils.isNotBlank(psEndDate)) {
						if (StringUtils.isNotBlank(psStartDate)) {
							map.put("psStartDate", psStartDate);
						}
						if (StringUtils.isNotBlank(psEndDate)) {
							map.put("psEndDate", psEndDate);
						}
					}
				} else {
					// 1.如果高级搜索中没有选择首长，则按这条文最大的首长的批示时间筛选
					if (StringUtils.isNotBlank(psStartDate)) {
						map.put("startDate", psStartDate);
					}
					if (StringUtils.isNotBlank(psEndDate)) {
						map.put("endDate", psEndDate);
					}
				}
				if (StringUtils.isNotBlank(period) && StringUtils.equals("4", typeId)) {
					map.put("period", period);
				}
			}

			if (!StringUtils.equals("0", adminType) && !StringUtils.equals("1", adminType)
					&& !StringUtils.equals("2", adminType) && !StringUtils.equals("3", adminType)
					&& !StringUtils.equals(DbDefined.ROLE_1, roleType)
					&& !StringUtils.equals(DbDefined.ROLE_3, roleType)) {
				if (StringUtils.equals(DbDefined.ROLE_5, roleType)) {
					map.put("deptId", CurrentUser.getDepartmentId());
				} else {
					map.put("loginUserId", loginUserId);
				}
				PageHelper.startPage(page, pagesize);
				infoList = documentInfoService.queryPersonList(map);
			} else {
				if (!StringUtils.equals("3", adminType) && !StringUtils.equals("0", adminType)
						&& (StringUtils.equals("2", adminType) || StringUtils.equals(DbDefined.ROLE_3, roleType))) {
					String orgId = baseAppUserService.getBareauByUserId(loginUserId);
					if (StringUtils.isNotBlank(orgId)) {
						map.put("orgId", orgId);
					}
				}
				if (StringUtils.isNotBlank(jnType)) {// 从统计报表点进去--本年度办理情况--点击某一个状态触发的查询
					if ("4".equals(jnType)) {// 未反馈的用这个查询
						PageHelper.startPage(page, pagesize);
						infoList = documentInfoService.queryReplyListForWfk(map);
					} else {
						PageHelper.startPage(page, pagesize);
						infoList = documentInfoService.queryReplyListPlus(map);
					}
				} else {// 直接点击办理反馈触发的查询
					PageHelper.startPage(page, pagesize);
					infoList = documentInfoService.queryReplyList(map);
				}
			}
			for (DocumentInfo info : infoList) {
				// 是否已读
				Map<String, Object> readMap = new HashMap<>();
				readMap.put("userId", loginUserId);
				readMap.put("infoId", info.getId());
				List<DocumentRead> list = documentReadService.queryList(readMap);

				// 未读，最新反馈字段有值则标识为已更新
				if (list.size() == 0 && StringUtils.isNotEmpty(info.getLatestReply())) {
					info.setUpdateFlag("1");
				}
				// 是否批示超过3个月
				this.isOverTreeMonth(info);
				// 首长批示
				Map<String, Object> szpsMap = new HashMap<>();
				szpsMap.put("infoId", info.getId());
				List<DocumentSzps> szpsList = documentSzpsService.queryList(szpsMap);
				info.setSzpslist(szpsList);
				// info.setUnderDepts(info.getLatestSubDept()+"/"+info.getLatestUndertaker());
			}
		}
		GwPageUtils pageUtil = new GwPageUtils(infoList);
		Response.json(pageUtil);
	}

	/**
	 * 计算首长批示时间到现在是否查过三月；
	 * 
	 * @param info
	 *            文流转主表数据
	 */
	private void isOverTreeMonth(DocumentInfo info) {
		String leaderTime = info.getLeaderTime();
		// 2019年05月08日
		if (StringUtils.isNotBlank(leaderTime)) {
			LocalDate currdate = LocalDate.now();
			LocalDate leaderDate = LocalDate.parse(leaderTime, DateTimeFormatter.ofPattern("yyyy年MM月dd日"));
			// 如果该文没有办结且超过批示时间三个月的，显示超期提示；
			if (info.getStatus() < 2) {
				if ((int) ChronoUnit.YEARS.between(leaderDate, currdate) == 0) {
					if ((int) ChronoUnit.MONTHS.between(leaderDate, currdate) == 3) {
						// 提取天数
						if (currdate.getDayOfMonth() > leaderDate.getDayOfMonth()) {
							info.setIsOverTreeMonth(1);
						}
					} else if ((int) ChronoUnit.MONTHS.between(leaderDate, currdate) > 3) {
						info.setIsOverTreeMonth(1);
					}
				} else if ((int) ChronoUnit.YEARS.between(leaderDate, currdate) > 0) {
					info.setIsOverTreeMonth(1);
				}
			}
		}
	}

	/**
	 * 办理反馈数量统计
	 * 
	 * @param search
	 *            搜索
	 * @param typeId
	 *            文件类型
	 */
	@ResponseBody
	@RequestMapping("/replyNums")
	public void replyNums(String search, String typeId, String orgid, String month, String title, String leaderId,
			String period, String psStartDate, String psEndDate, String year) {
		String dateStr = null;
		if (StringUtils.isNotBlank(year)) {
			dateStr = year;
		} else if (!StringUtils.isEmpty(month) && StringUtil.equals("all", month)) {
			dateStr = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE).substring(0, 4);
		} else if (!StringUtils.isEmpty(month)) {
			dateStr = LocalDate.now().withMonth(Integer.parseInt(month)).format(DateTimeFormatter.ISO_LOCAL_DATE)
					.substring(0, 7);
		}
		int[] arr = { 0, 0, 0, 0, 0, 0 };
		String loginUserId = CurrentUser.getUserId();
		// 当前登录人的管理员类型(0:超级管理员 ;1：部管理员；2：局管理员；3：即是部管理员又是局管理员)
		String adminType = adminSetService.getAdminTypeByUserId(loginUserId);
		// 当前登录人的角色（1：首长；2：首长秘书；3：局长；4：局秘书；5：处长；6：参谋;）
		String roleType = roleSetService.getRoleTypeByUserId(loginUserId);
		List<DocumentInfo> infoList = null;
		Map<String, Object> map = new HashMap<>();
		map.put("search", search);
		map.put("type", typeId);
		map.put("orgId", orgid);
		map.put("year", dateStr);
		map.put("title", title);
		if (StringUtils.equals("1", typeId) || StringUtils.equals("2", typeId) || StringUtils.equals("4", typeId)) {
			// 办理反馈高级搜索批示指示时间:
			// 1.如果高级搜索中选择了首长，则按所选首长的批示时间筛选
			if (StringUtils.isNotBlank(leaderId)) {
				map.put("leaderId", leaderId);
				if (StringUtils.isNotBlank(psStartDate) || StringUtils.isNotBlank(psEndDate)) {
					if (StringUtils.isNotBlank(psStartDate)) {
						map.put("psStartDate", psStartDate);
					}
					if (StringUtils.isNotBlank(psEndDate)) {
						map.put("psEndDate", psEndDate);
					}
				}
			} else {
				// 1.如果高级搜索中没有选择首长，则按这条文最大的首长的批示时间筛选
				if (StringUtils.isNotBlank(psStartDate)) {
					map.put("startDate", psStartDate);
				}
				if (StringUtils.isNotBlank(psEndDate)) {
					map.put("endDate", psEndDate);
				}
			}
			if (StringUtils.isNotBlank(period) && StringUtils.equals("4", typeId)) {
				map.put("period", period);
			}
		}
		if (!StringUtils.equals("0", adminType) && !StringUtils.equals("1", adminType)
				&& !StringUtils.equals("2", adminType) && !StringUtils.equals("3", adminType)
				&& !StringUtils.equals(DbDefined.ROLE_1, roleType) && !StringUtils.equals(DbDefined.ROLE_3, roleType)) {
			if (StringUtils.equals(DbDefined.ROLE_5, roleType)) {
				map.put("deptId", CurrentUser.getDepartmentId());
			} else {
				map.put("loginUserId", loginUserId);
			}
			infoList = documentInfoService.queryPersonList(map);
		} else {
			if (!StringUtils.equals("0", adminType) && !StringUtils.equals("3", adminType)
					&& (StringUtils.equals("2", adminType) || StringUtils.equals(DbDefined.ROLE_3, roleType))) {
				String orgId = baseAppUserService.getBareauByUserId(loginUserId);
				if (StringUtils.isNotBlank(orgId)) {
					map.put("orgId", orgId);
				}
			}
			infoList = documentInfoService.queryReplyList(map);
		}
		arr[0] = infoList.size();
		for (DocumentInfo info : infoList) {
			Integer restatus = info.getStatus();
			String latestReply = info.getLatestReply();
			if (1 == restatus) {
				if (StringUtils.isNotEmpty(latestReply)) {
					arr[1] += 1;
				} else {
					arr[4] += 1;
				}
			}
			if (2 == restatus) {
				arr[2] += 1;
			}
			if (3 == restatus) {
				arr[3] += 1;
			}
			// 是否已读
			Map<String, Object> readMap = new HashMap<>();
			readMap.put("userId", loginUserId);
			readMap.put("infoId", info.getId());
			List<DocumentRead> list = documentReadService.queryList(readMap);
			if (list.size() == 0 && StringUtils.isNotBlank(info.getLatestReply())) {
				arr[5] += 1;
			}
		}
		Response.json(arr);
	}

	/**
	 * 获取文件信息
	 * 
	 * @param id
	 *            文件id
	 */
	@ResponseBody
	@RequestMapping("/info")
	public void info(Integer page, Integer pagesize, String id,String documentStatus) {
		String preId="";
		String sufId="";
		Map<String, Object> map = new HashMap<>();
		map.put("docStatus", documentStatus);
		//PageHelper.startPage(page, pagesize);
		//对列表数据进行排序
		List<DocumentInfo> infoList = documentInfoService.queryNewListSort(map);
		if (infoList != null && infoList.size() > 0) {
			if (infoList.size() == 1) {
				preId = "noPredId";
				sufId = "noSufId";
			} else {
				for (int i = 0; i < infoList.size(); i++) {
					if (StringUtils.equals(id, infoList.get(i).getId())) {
						if (i == 0) {
							preId = "noPredId";
							sufId = infoList.get(i + 1).getId();
						} else if (i == infoList.size() - 1) {
							preId = infoList.get(i - 1).getId();
							sufId = "noSufId";
						} else {
							preId = infoList.get(i - 1).getId();
							sufId = infoList.get(i + 1).getId();
						}
					}
				}
			}

		}
		DocumentInfo documentInfo = documentInfoService.queryObject(id);
		String roleid = getNewRoleType();
		if (!"".equals(roleid) && StringUtils.equals("1", roleid)) {// 首长
			if (StringUtils.isNotBlank(documentInfo.getLatestReply())) {// 局长意见有更新并且待办件
				String szreadids = documentInfo.getSzReadIds();
				if (StringUtils.isBlank(szreadids)) {
					documentInfo.setSzReadIds(CurrentUser.getUserId());
				} else if (!szreadids.contains(CurrentUser.getUserId())) {
					documentInfo.setSzReadIds(szreadids + "," + CurrentUser.getUserId());
				}
				documentInfoService.update(documentInfo);
			}

		}
		documentInfo.setPreId(preId);
		documentInfo.setSufId(sufId);
		Response.json(documentInfo);
	}

	@ResponseBody
	@RequestMapping("/lastInfo")
	public void lastInfo() {
		Map<String, Object> map = new HashMap<>();
		map.put("typeId", "4");
		List<DocumentInfo> list = documentInfoService.queryInfoByParam(map);
		String period = "〔" + DateUtil.getCurrentYear() + "〕第期";
		if (list != null && list.size() > 0) {
			String lastPeriod = list.get(0).getPeriod();
			if (StringUtils.isNotBlank(lastPeriod)) {
				period = lastPeriod;
			}
		}
		Response.json("period", period);
	}

	// 返回值为1：首长，返回值为2：超级管理员、部管理员、即是部管理员又是局管理员，返回值为3：局管理员或局长，返回值为""：其他人员
	public String getNewRoleType() {
		String newRoleTpe = "";
		String loginUserId = CurrentUser.getUserId();
		String loginDeptId = CurrentUser.getDepartmentId();
		// 首长单位id
		BaseAppConfig mapped = baseAppConfigService.queryObject(AppConstant.LEAD_TEAM);
		// 当前登录人的管理员类型(0:超级管理员 ;1：部管理员；2：局管理员；3：即是部管理员又是局管理员)
		String adminType = adminSetService.getAdminTypeByUserId(loginUserId);
		// 当前登录人的角色（3：局长；5：处长；6：其他）
		String roleType = roleSetService.getRoleTypeByUserId(loginUserId);
		if (StringUtils.equals("2", adminType) || StringUtils.equals("3", roleType)) {
			newRoleTpe = "3";
		}
		if (("1").equals(adminType) || ("3").equals(adminType) || ("0").equals(adminType)) {
			newRoleTpe = "2";
		}
		if (mapped != null && StringUtils.equals(loginDeptId, mapped.getValue())) {
			newRoleTpe = "1";
		}
		return newRoleTpe;
	}

	/**
	 * 新增或修改
	 */
	@ResponseBody
	@RequestMapping("/update")
	public void update(DocumentInfo documentInfo) {
		String uuid = "";
		JSONObject jo = new JSONObject();
		String id = documentInfo.getId();
		if (StringUtils.isNotBlank(id)) {
			jo.put("id", documentInfo.getId());
			// 更新操作
			documentInfoService.update(this.organizeDocumentInfo(documentInfo));
		} else {
			uuid = UUIDUtils.random();
			documentInfo.setId(uuid);
			documentInfo.setStatus(0);
			documentInfoService.save(documentInfo);
			jo.put("id", uuid);
		}
		Response.json(jo);
	}

	/**
	 *
	 * @param documentInfo
	 *            主文数据
	 * @return DocumentInfo
	 */
	private DocumentInfo organizeDocumentInfo(DocumentInfo documentInfo) {
		DocumentInfo documentInfo1 = documentInfoService.queryObject(documentInfo.getId());
		String docTypeId = documentInfo.getDocTypeId();
		String docTypeName = documentInfo.getDocTypeName();
		String docTitle = documentInfo.getDocTitle();
		String securityId = documentInfo.getSecurityId();
		String securityClassification = documentInfo.getSecurityClassification();
		String urgencyId = documentInfo.getUrgencyId();
		String urgencyDegree = documentInfo.getUrgencyDegree();
		String docCode = documentInfo.getDocCode();
		String banjianNumber = documentInfo.getBanjianNumber();
		String userId = documentInfo.getUserId();
		String userName = documentInfo.getUserName();
		String applyTime = documentInfo.getApplyTime();
		String printDate = documentInfo.getPrintDate();
		String jobContent = documentInfo.getJobContent();
		String remark = documentInfo.getRemark();
		String period = documentInfo.getPeriod();
		if (StringUtils.isNotBlank(docTypeId)) {
			documentInfo1.setDocTypeId(docTypeId);
		}
		if (StringUtils.isNotBlank(docTypeName)) {
			documentInfo1.setDocTypeName(docTypeName);
		}
		if (StringUtils.isNotBlank(docTitle)) {
			documentInfo1.setDocTitle(docTitle);
		}
		if (StringUtils.isNotBlank(securityId)) {
			documentInfo1.setSecurityId(securityId);
		}
		if (StringUtils.isNotBlank(securityClassification)) {
			documentInfo1.setSecurityClassification(securityClassification);
		}
		if (StringUtils.isNotBlank(urgencyId)) {
			documentInfo1.setUrgencyId(urgencyId);
		}
		if (StringUtils.isNotBlank(urgencyDegree)) {
			documentInfo1.setUrgencyDegree(urgencyDegree);
		}
		if (StringUtils.isNotBlank(docCode)) {
			documentInfo1.setDocCode(docCode);
		}
		if (StringUtils.isNotBlank(banjianNumber)) {
			documentInfo1.setBanjianNumber(banjianNumber);
		}
		if (StringUtils.isNotBlank(userId)) {
			documentInfo1.setUserId(userId);
		}
		if (StringUtils.isNotBlank(userName)) {
			documentInfo1.setUserName(userName);
		}
		if (StringUtils.isNotBlank(applyTime)) {
			documentInfo1.setApplyTime(applyTime);
		}
		if (StringUtils.isNotBlank(printDate)) {
			documentInfo1.setPrintDate(printDate);
		}
		if (StringUtils.isNotBlank(jobContent)) {
			documentInfo1.setJobContent(jobContent);
		}
		if (StringUtils.isNotBlank(remark)) {
			documentInfo1.setRemark(remark);
		}
		if (StringUtils.isNotBlank(period)) {
			documentInfo1.setPeriod(period);
		}
		return documentInfo1;
	}

	/**
	 * 按钮显示参数
	 * 
	 * @param id
	 *            主文件id
	 */
	@ResponseBody
	@RequestMapping("/buttonParam")
	public void buttonParam(String id) {
		String loginUserId = CurrentUser.getUserId();
		boolean cuiBanBtn = false;
		boolean zhuanBanBtn = false;
		boolean quXiaoBtn = false;
		boolean banjieBtn = false;

		// 当前登录人的管理员类型(0:超级管理员 ;1：部管理员；2：局管理员；3：即是部管理员又是局管理员)
		String adminType = adminSetService.getAdminTypeByUserId(loginUserId);
		// 获取文件信息
		DocumentInfo documentInfo = documentInfoService.queryObject(id);
		if (documentInfo != null) {
			Integer status = documentInfo.getStatus();
			String cuibanFlag = documentInfo.getCuibanFlag();
			if (StringUtils.equals("0", adminType) || StringUtils.equals("1", adminType)
					|| StringUtils.equals("3", adminType)) {
				if (status < 2) {
					zhuanBanBtn = true;
					banjieBtn = true;
					if (!StringUtils.equals("1", cuibanFlag)) {
						cuiBanBtn = true;
					}
				} else {
					quXiaoBtn = true;
				}
			}
		}
		JSONObject json = new JSONObject();
		json.put("cuiBanBtn", cuiBanBtn);
		json.put("quXiaoBtn", quXiaoBtn);
		json.put("zhuanBanBtn", zhuanBanBtn);
		json.put("banjieBtn", banjieBtn);
		Response.json(json);
	}

	/**
	 * @description:部管理员强制常态落实
	 * @author:zhangyw
	 * @date:2019年5月24日
	 * @Version v1.0
	 * @param infoId
	 *            主文件id
	 */
	@ResponseBody
	@RequestMapping("/luoShiOperation")
	public void luoShiOperation(String infoId) {
		JSONObject json = new JSONObject();
		if (StringUtils.isNotBlank(infoId)) {
			// 主文件状态变为常态落实
			DocumentInfo info = documentInfoService.queryObject(infoId);
			info.setSzReadIds("");
			info.setStatus(3);
			documentInfoService.update(info);
			//主文件办结，各分支文件强制更新为常态落实
			List<SubDocInfo> subDocInfosList = subDocInfoService.queryForList(infoId);
			if (subDocInfosList != null && subDocInfosList.size() > 0) {
				for (SubDocInfo subDocInfo : subDocInfosList) {
					subDocInfo.setDocStatus(13);
					subDocInfo.setUpdateTime(new Date());
					subDocInfoService.update(subDocInfo);
				}
			}
			// 办结落实记录
			DocumentBjjl bjjl = new DocumentBjjl();
			bjjl.setInfoId(infoId);
			bjjl.setContent("常态落实");
			documentBjjlService.save(bjjl);
			json.put("result", "success");
		} else {
			json.put("result", "fail");
		}
		Response.json(json);
	}

	/**
	 * @description:部管理员强制办结
	 * @author:zhangyw
	 * @date:2019年5月24日
	 * @Version v1.0
	 * @param infoId
	 *            主文件id
	 */
	@ResponseBody
	@RequestMapping("/banJieOperation")
	public void banJieOperation(String infoId) {
		JSONObject json = new JSONObject();
		if (StringUtils.isNotBlank(infoId)) {
			// 主文件状态变为办结
			DocumentInfo info = documentInfoService.queryObject(infoId);
			info.setSzReadIds("");
			info.setStatus(2);
			documentInfoService.update(info);
			//主文件办结，各分支文件强制更新为办结
			List<SubDocInfo> subDocInfosList = subDocInfoService.queryForList(infoId);
			if (subDocInfosList != null && subDocInfosList.size() > 0) {
				for (SubDocInfo subDocInfo : subDocInfosList) {
					subDocInfo.setDocStatus(12);
					subDocInfo.setUpdateTime(new Date());
					subDocInfoService.update(subDocInfo);
				}
			}
			// 办结落实记录
			DocumentBjjl bjjl = new DocumentBjjl();
			bjjl.setInfoId(infoId);
			bjjl.setContent("办结");
			documentBjjlService.save(bjjl);
			json.put("result", "success");
		} else {
			json.put("result", "fail");
		}
		Response.json(json);
	}

	/**
	 * 取消办结操作
	 * 
	 * @param id
	 *            主文件id
	 */
	@ResponseBody
	@RequestMapping("/cancleOperation")
	public void cancleOperation(String id) {
		JSONObject json = new JSONObject();
		DocumentInfo info = documentInfoService.queryObject(id);
		// 取最后办结或落实的局的主文件(正常系统中只会有一个分支文件的状态大于11，导入的会有多个)
		// 文件局内状态（1:待转办；3：退回修改；5：待落实；7：待审批；9：办理中；10：建议办结；11：建议落实；12：办结；13：常态落实）
		List<SubDocInfo> lastEndSubInfos = subDocInfoService.queryLastEndSubInfo(id);
		// 添加办结记录
		DocumentBjjl newBjjl = new DocumentBjjl();
		newBjjl.setContent("取消办结");
		newBjjl.setInfoId(id);
		documentBjjlService.save(newBjjl);
		if (lastEndSubInfos != null && lastEndSubInfos.size() > 0) {
			// lastEndSubInfos存在说明是承办人选择办结的文或导入的已办结的文
			for (SubDocInfo lastEndSubInfo : lastEndSubInfos) {
				String subId = lastEndSubInfo.getId();
				if (StringUtils.isNotBlank(subId)) {
					String undertaker = lastEndSubInfo.getUndertaker();
					String undertakerName = lastEndSubInfo.getUndertakerName();
					Integer subStatus = DbDocStatusDefined.BAN_LI_ZHONG;
					// 取最后一条流转记录
					SubDocTracking lastTracking = subDocTrackingService.queryLatestRecord(subId);
					// trackingType（1：转办；2：审批流转；3：退回;4:新一轮反馈的开始，即承办人的办理）
					/*
					 * String trackingType = lastTracking.getTrackingType();
					 * //如果最后一条流转为审批，接收人为承办人则文件为办理中，否则文件为待审批
					 * if(StringUtils.equals("2",trackingType)) {
					 * if(!StringUtils.equals(lastEndSubInfo.getUndertaker(),
					 * lastTracking.getReceiverId())) { subStatus = DbDocStatusDefined.DAI_SHEN_PI;
					 * } //如果最后一条流转为退回修改，则文件状态为退回修改 }else if(StringUtils.equals("3",trackingType)){
					 * subStatus = DbDocStatusDefined.TUIHUI_XIUGAI; }
					 */
					// 获取当前登录人信息
					String deptId = null;
					String deptName = null;
					if (StringUtils.isNotBlank(undertaker)) {
						List<BaseAppUser> list = baseAppUserService.findByUserId(undertaker);
						if (list != null && list.size() > 0) {
							deptId = list.get(0).getOrganid();
							if (StringUtils.isNotBlank(deptId)) {
								BaseAppOrgan organ = baseAppOrganService.queryObject(deptId);
								deptName = organ.getName();
							}
						}
					}
					// 添加流转记录
					SubDocTracking tracking = new SubDocTracking();
					tracking.setSenderId(lastTracking.getReceiverId());
					tracking.setSenderName(lastTracking.getReceiverName());
					tracking.setSenDeptId(lastTracking.getRecDeptId());
					tracking.setSenDeptName(lastTracking.getRecDeptName());
					tracking.setReceiverId(undertaker);
					tracking.setReceiverName(undertakerName);
					tracking.setRecDeptId(deptId);
					tracking.setRecDeptName(deptName);
					tracking.setSubId(subId);
					tracking.setTrackingType("4");
					subDocTrackingService.save(tracking);
					// 修改最后一个局的文件状态
					lastEndSubInfo.setDocStatus(subStatus);
					subDocInfoService.update(lastEndSubInfo);
				} else {
					json.put("result", "fail");
				}
			}
		}
		// lastEndSubInfos不存在说明是部管理员强制办结的文，只需要将主文件变为办理中
		info.setStatus(1);
		documentInfoService.update(info);
		json.put("result", "success");
		Response.json(json);
	}

	/**
	 * 撤回操作
	 * 
	 * @param id
	 */
	@ResponseBody
	@RequestMapping("/cheHuiOperation")
	public void cheHuiOperation(String id) {
		documentInfoService.deleteByCheHui(id);
		Response.json("result", "success");
	}

	/**
	 * 批量已读
	 * 
	 * @param ids
	 *            主文件id
	 */
	@ResponseBody
	@RequestMapping("/batchRead")
	public void batchRead(String ids) {
		String[] idArry = ids.split(",");
		for (String id : idArry) {
			Map<String, Object> map = new HashMap<>();
			map.put("userId", CurrentUser.getUserId());
			map.put("infoId", id);
			map.put("readFlag", "1");
			List<DocumentRead> list = documentReadService.queryList(map);
			if (list.size() == 0 || list == null) {
				DocumentRead read = new DocumentRead();
				read.setInfoId(id);
				documentReadService.save(read);
			}
		}
		Response.json("result", "success");
	}

	/**
	 * 删除文件
	 * 
	 * @param id
	 *            主文件id
	 */
	@ResponseBody
	@RequestMapping("/delete")
	public void delete(String id) {
		documentSzpsService.deleteByInfoId(id);
		documentFileService.deleteByInfoId(id);
		documentInfoService.delete(id);
		Response.json("result", "success");
	}

	/**
	 * 获取当前登录人信息
	 */
	@ResponseBody
	@RequestMapping("/getLoginUser")
	public void getLoginUser() {
		JSONObject jo = new JSONObject();
		jo.put("userId", CurrentUser.getUserId());
		jo.put("userName", CurrentUser.getUsername());
		Response.json(jo);
	}

	/**
	 * 部管理员添加催办
	 * 
	 * @param textarea
	 *            催办内容
	 * @param infoId
	 *            主文件id
	 */
	@ResponseBody
	@RequestMapping("/saveCuiBan")
	public void saveCuiBan(String textarea, String infoId) {
		// 保存催办历史
		DocumentCbjl cb = new DocumentCbjl();
		cb.setUrgeContent(textarea);
		cb.setInfoId(infoId);
		documentCbjlService.save(cb);
		// 主文件标识催办
		DocumentInfo info = documentInfoService.queryObject(infoId);
		info.setCuibanFlag("1");
		documentInfoService.update(info);
		Response.json("result", "success");
	}

	/**
	 * 获取最新催办数据
	 * 
	 * @param infoId
	 *            主文件id
	 */
	@ResponseBody
	@RequestMapping("/getLatestCuiBan")
	public void getLatestCuiBan(String infoId) {
		DocumentCbjl cuiBan = null;
		DocumentInfo info = documentInfoService.queryObject(infoId);
		if (StringUtils.equals("1", info.getCuibanFlag())) {
			cuiBan = documentCbjlService.queryLatestCuiBan(infoId);
		}
		Response.json(cuiBan);
	}

	/**
	 * 催办记录列表
	 * 
	 * @param infoId
	 *            主文件id
	 */
	@ResponseBody
	@RequestMapping("/getCuiBanlist")
	public void getCuiBanlist(String infoId) {
		List<DocumentCbjl> list = null;
		if (StringUtils.isNotBlank(infoId)) {
			Map<String, Object> map = new HashMap<>();
			map.put("infoId", infoId);
			// 查询列表数据
			list = documentCbjlService.queryList(map);
		}
		Response.json(list);
	}

	/**
	 * 办结记录列表
	 * 
	 * @param infoId
	 */
	@ResponseBody
	@RequestMapping("/getBanJieList")
	public void getBanJieList(String infoId) {
		List<DocumentBjjl> bjjlList = null;
		if (StringUtils.isNotBlank(infoId)) {
			Map<String, Object> map = new HashMap<>();
			map.put("infoId", infoId);
			// 查询列表数据
			bjjlList = documentBjjlService.queryList(map);
		}
		Response.json(bjjlList);
	}

	/**
	 * 获取办理反馈左侧菜单及更新数量
	 */
	@ResponseBody
	@RequestMapping("/getDicByTypet")
	public void getDicByTypet(String orgid, String month, boolean menuFlag) {
		String dateStr = null;
		String orgId = orgid;
		if (!StringUtils.isEmpty(month) && StringUtil.equals("all", month)) {
			dateStr = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE).substring(0, 4);
		} else if (!StringUtils.isEmpty(month)) {
			dateStr = LocalDate.now().withMonth(Integer.parseInt(month)).format(DateTimeFormatter.ISO_LOCAL_DATE)
					.substring(0, 7);
		}
		String loginUserId = CurrentUser.getUserId();
		// 当前登录人的管理员类型(0:超级管理员 ;1：部管理员；2：局管理员；3：即是部管理员又是局管理员)
		String adminType = adminSetService.getAdminTypeByUserId(loginUserId);
		// 当前登录人的角色（1：首长；2：首长秘书；3：局长；4：局秘书；5：处长；6：参谋;）
		String roleType = roleSetService.getRoleTypeByUserId(loginUserId);
		Map<String, Object> map = new HashMap<>();
		map.put("userId", loginUserId);
		map.put("docType", DbDefined.DOCUMENT_TYPE);
		map.put("year", dateStr);
		if (!StringUtils.equals("0", adminType) && !StringUtils.equals("1", adminType)
				&& !StringUtils.equals("2", adminType) && !StringUtils.equals("3", adminType)
				&& !StringUtils.equals(DbDefined.ROLE_1, roleType) && !StringUtils.equals(DbDefined.ROLE_3, roleType)) {
			orgId = null;
			if (StringUtils.equals(DbDefined.ROLE_5, roleType)) {// 处长
				map.put("deptId", CurrentUser.getDepartmentId());
			} else {
				map.put("loginUserId", loginUserId);// 个人（普通参谋）
			}
		} else {
			if (!StringUtils.equals("0", adminType) && !StringUtils.equals("3", adminType)
					&& (StringUtils.equals("2", adminType) || StringUtils.equals(DbDefined.ROLE_3, roleType))) {
				String loginOrgId = baseAppUserService.getBareauByUserId(loginUserId);// 局管理员或局长获取局id
				if (StringUtils.isNotBlank(loginOrgId)) {
					orgId = loginOrgId;
				}
			}
		}
		map.put("orgId", orgId);
		List<DocumentDic> dicByType = documentInfoService.queryDicByType(map);
		int blfkNum = 0;
		if (menuFlag) {
			for (DocumentDic dic : dicByType) {
				blfkNum += dic.getHasUpdateNum();
			}
			Response.json("blfkNum", blfkNum);
			return;
		}
		Response.json(DbDefined.DOCUMENT_TYPE, dicByType);
	}

	/**
	 * 将扫描筛选后的图片，保存成PDF文件
	 * 
	 * @param smwj
	 *            文件
	 * @param infoId
	 *            公文ID
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/saveSmjPsFile")
	public void saveSmjToFile(@RequestParam(value = "smwj", required = false) String smwj,
			@RequestParam(value = "infoId", required = true) String infoId) throws Exception {
		DocumentInfo fileInfo = documentInfoService.queryObject(infoId);
		if (fileInfo != null) {
			// 获得批示首页文件的绝对路径
			String scanFilePath = "";
			// 将扫描件上传到电子数据中心
			scanFilePath = getFilePath(smwj, fileInfo, CurrentUser.getUsername());
			
			/*if (StringUtils.isNotBlank(fileId)) {
				HTTPFile httpFile = new HTTPFile(fileId);
				scanFilePath = httpFile.getAssginDownloadURL(true);
			}*/
			JSONObject result = new JSONObject();
			result.put("result", "success");
			//result.put("scanId", StringUtils.isNotBlank(fileId) ? fileId : "");
			result.put("scanFilePath", scanFilePath);
			Response.json(result);
		}
	}

	private String getFilePath(String smwj, DocumentInfo model, String loginUserName) {
		String fileIdPath = "";
		// 将扫描件上传到电子数据中心
		if (StringUtils.isNotBlank(smwj)) {
			fileIdPath = OfdTransferUtil.createdOFDFile(smwj, model.getId());
			if (StringUtils.isNotBlank(fileIdPath)) {
				File file2 = new File(fileIdPath);
				// 保存文件相关数据
				DocumentFile file = new DocumentFile();
				file.setId(UUIDUtils.random());
				file.setDocInfoId(model.getId());
				file.setSort(documentFileService.queryMinSort(model.getId()));
//				HTTPFile httpFile = new HTTPFile(fileId);
				file.setFileName(file2.getName());
				file.setCreatedTime(new Date());
				file.setFileServerFormatId(fileIdPath);
				documentFileService.save(file);
			}
		}
		return fileIdPath;
	}

	@ResponseBody
	@RequestMapping("/uploadFiles")
	public void saveMoreFiles(String period,String securityId,String securityClassification,String urgencyId,String urgencyDegree,String docTypeId,String docTypeName, @RequestParam(required = false) MultipartFile[] pdf) {
		String formatDownPath = "";// 版式文件下载路径
		String retFormatId = null;// 返回的版式文件id
		String streamId = null;// 流式文件id
		String formatId = null;// 版式文件id
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		JSONObject json = new JSONObject();
		if (pdf != null && pdf.length > 0) {
			for (int i = 0; i < pdf.length; i++) {
				// 获取文件名字
				String fileName = pdf[i].getOriginalFilename();
				// 保存
				DocumentInfo documentInfo = new DocumentInfo();
				String uuid = UUIDUtils.random();
				documentInfo.setStatus(0);
				documentInfo.setId(uuid);
				documentInfo.setDocTitle(fileName.substring(0,fileName.lastIndexOf(".")));
				documentInfo.setDocTypeId(docTypeId);
				documentInfo.setDocTypeName(docTypeName);
				documentInfo.setCreatedTime(new Date());
				documentInfo.setSecurityId(securityId);
				documentInfo.setSecurityClassification(securityClassification);
				documentInfo.setUrgencyId(urgencyId);
				documentInfo.setUrgencyDegree(urgencyDegree);
				documentInfo.setPeriod(period);
				documentInfo.setUserId(CurrentUser.getUserId());
				documentInfo.setUserName(CurrentUser.getUsername());
				documentInfo.setApplyTime(simpleDateFormat.format(new Date()));
				documentInfoService.save(documentInfo);
				// 获取文件后缀
				String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
				// 如果文件是流式则流式转换为版式
				if (!StringUtils.equals("ofd", fileType)) {
					streamId = FileBaseUtil.fileServiceUploadByFilePathLiuShi(pdf[i],filePath,localAddress);
					try {
						if (StringUtils.isNotBlank(streamId)) {
							formatId = OfdTransferUtil.convertLocalFileToOFDPath(streamId,localAddress);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					formatId = FileBaseUtil.fileServiceUploadByFilePath(pdf[i],filePath,localAddress);
				}
				if (StringUtils.isNotBlank(formatId)) {
					// 保存文件相关数据
					DocumentFile file = new DocumentFile();
					file.setId(UUIDUtils.random());
					file.setDocInfoId(uuid);
					file.setSort(documentFileService.queryMinSort(uuid));
					file.setFileName(fileName);
					file.setCreatedTime(new Date());
					if (StringUtils.isNotBlank(streamId)) {
						file.setFileServerStreamId(streamId);
					}
					file.setFileServerFormatId(formatId);
					documentFileService.save(file);
				}
			}
			json.put("smjId", formatId);
			json.put("smjFilePath", formatId);
			json.put("result", "success");
		}
		Response.json(json);
	}

	@ResponseBody
	@RequestMapping("/download")
	public HttpServletResponse download(HttpServletResponse response,String path) {
		File file = new File(path);
		try {
			FileInputStream fis = new FileInputStream(file);
            response.setContentType("application/force-download");
            response.setCharacterEncoding("UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename="+new String(file.getName().getBytes(),"ISO-8859-1"));
            ServletOutputStream os = response.getOutputStream();
            byte[] buf = new byte[1024];
            int len = 0;
            while((len = fis.read(buf)) !=-1) {
            	os.write(buf,0,len);
            }
            fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response;
	}
	
}
