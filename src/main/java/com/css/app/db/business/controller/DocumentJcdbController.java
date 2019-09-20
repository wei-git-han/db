package com.css.app.db.business.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.css.addbase.appconfig.entity.BaseAppConfig;
import com.css.addbase.appconfig.service.BaseAppConfigService;
import com.css.addbase.apporgan.entity.BaseAppUser;
import com.css.addbase.apporgan.service.BaseAppUserService;
import com.css.addbase.constant.AppConstant;
import com.css.app.db.business.entity.BaseTreeObject;
import com.css.app.db.business.entity.DocumentInfo;
import com.css.app.db.business.entity.DocumentSzps;
import com.css.app.db.business.service.DocumentInfoService;
import com.css.app.db.business.service.DocumentSzpsService;
import com.css.app.db.config.entity.DocumentDic;
import com.css.app.db.config.service.AdminSetService;
import com.css.app.db.config.service.DocumentDicService;
import com.css.app.db.config.service.RoleSetService;
import com.css.base.utils.CurrentUser;
import com.css.base.utils.DateUtil;
import com.css.base.utils.GwPageUtils;
import com.css.base.utils.Response;
import com.css.base.utils.StringUtils;
import com.github.pagehelper.PageHelper;

import dm.jdbc.util.StringUtil;



/**
 * 督办基本信息表
 * 
 * @author 中软信息系统工程有限公司
 * @email 
 * @date 2019-04-18 16:34:38
 */
@Controller
@RequestMapping("/app/db/documentjcdb")
public class DocumentJcdbController {
	private final Logger logger = LoggerFactory.getLogger(DocumentJcdbController.class);
	@Autowired
	private DocumentInfoService documentInfoService;
	@Autowired
	private RoleSetService roleSetService;
	@Autowired
	private AdminSetService adminSetService;
	@Autowired
	private BaseAppConfigService baseAppConfigService;
	@Autowired
	private DocumentSzpsService documentSzpsService;
	@Autowired
	private BaseAppUserService baseAppUserService;
	@Autowired
	private DocumentDicService documentDicService;

	
	/**
	 * 数据统计报表-(年度,状态数量)
	 * {
		年度"year":"2019",
		总数"total":100,
		办理中"blz":100,
		办结"bj":100,
		常态落实"ctls":100,
		完成率"wcl":"98%"
}
	 */
	@ResponseBody
	@RequestMapping("/list")
	public void list(String year){
		JSONObject jo=new JSONObject();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy");
		Map<String, Object> map = new HashMap<>();
		if(StringUtil.isEmpty(year)) {
			year=sdf.format(new Date());
		}
		map.put("year", year);
		List<Map<String, Object>> infoList = documentInfoService.queryListByYear(map);
		double  blz=0;
		double  bj=0;
		double  ctls=0;
		double  total=0;
		double  wfk =0;
		// sum(blz+bj+ctls) as total,sum(blz) as blz,sum(bj) as bj,sum(ctls) as ctls
		if (infoList!=null&&infoList.size()>0) {
			Map<String, Object> map2=infoList.get(0);
			if(map2!=null) {
				int wfkCount = this.queryWfkCount(null, year);
				blz= (long) map2.get("blz") - wfkCount;
				bj= (long) map2.get("bj");
				ctls= (long) map2.get("ctls");
				total= (long) map2.get("total");
				wfk= wfkCount;
			}
		}
		jo.put("blz", blz);
		jo.put("bj", bj);
		jo.put("ctls", ctls);
		jo.put("total", total);
		jo.put("wfk", wfk);
		jo.put("year", year);
		if(total>0) {
			jo.put("wcl", (bj+ctls)*100/total);
		}else {
			jo.put("wcl", "0%");
		}
		Response.json(jo);
	}
	
	/**
	 * 数据统计报表-(年度,状态数量)
	 * {"id":"01","dwname":"办公厅","blz":"100","bj":"101","ctls":"102"},
	{"id":"02","dwname":"办公厅","blz":"100","bj":"101","ctls":"102"},
	{"id":"03","dwname":"办公厅","blz":"100","bj":"101","ctls":"102"},
	{"id":"04","dwname":"办公厅","blz":"100","bj":"101","ctls":"102"},
	{"id":"05","dwname":"办公厅","blz":"100","bj":"101","ctls":"102"}
		 * 
	d.SUB_DEPT_ID,d.SUB_DEPT_NAME,sum(blz) as blz,sum(bj) as bj,sum(ctls) as ctls
	 */
	@ResponseBody
	@RequestMapping("/orglist")
	public void orglist(String year,String month){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy");
		Map<String, Object> map = new HashMap<>();
		if(StringUtil.isEmpty(year)) {
			int mon=Calendar.MONTH+1;
			year=sdf.format(new Date());
			if(StringUtil.isEmpty(month)) {				
				year+="-"+(mon<10?"0"+month:month+"");
			}else if(!StringUtil.equals("all", month)){
				year+="-"+month;
			}
		}
		map.put("year", year);
		//按年统计各局数据
		List<Map<String, Object>> infoList = documentInfoService.queryListByOrgYear(map);
		String orgId="";
		String szorgid=getSzOrgid();//获取首长的部门id
		String role=getNewRoleType();//返回值为1：首长，返回值为2：超级管理员、部管理员、即是部管理员又是局管理员，返回值为3：局管理员或局长，返回值为""：其他人员
		if("3".equals(role)) {//局管理员或局长
			orgId=baseAppUserService.getBareauByUserId(CurrentUser.getUserId());//获取当前人的局id
		}
		JSONArray ja=new JSONArray();
		if (infoList!=null&&infoList.size()>0) {
			for (Map<String, Object> map2 : infoList) {
				String danweiid=(String) map2.get("ID");
				JSONObject jo=new JSONObject();
				jo.put("bj", (long) map2.get("bj"));
				jo.put("ctls", (long) map2.get("ctls"));
				//对结果进行二级过滤：针对办理中拆分：办理中+未反馈
				//未反馈量
				int wfkCount = this.queryWfkCount(danweiid, year);
				//各局完成比率  办结+常态落实/总数
				long total = (long) map2.get("blz") + (long) map2.get("bj") + (long) map2.get("ctls");
				if (total == 0) {
					jo.put("wcl", "0%");
				} else {
					long bjAddCtls = (long) map2.get("bj") + (long) map2.get("ctls");
					if (bjAddCtls == 0) {
						jo.put("wcl", "0%");
					} else {
						double wclDouble =(int)(((double) bjAddCtls / total) * 100 + 0.5);
						String wclStr= wclDouble + "";
						String wcl = wclStr.substring(0, wclStr.lastIndexOf("."));
						jo.put("wcl", wcl + "%");
					}
				}
				//增加未反馈
				jo.put("wfk",wfkCount);
				//原办理中数据-未反馈量 = 现办理中数据
				jo.put("blz", (long) map2.get("blz") - (long)wfkCount);
				jo.put("id",danweiid);
				jo.put("dwname", (String) map2.get("dwname"));
				
				if("1".equals(role)) {//首长
					jo.put("state", "1");//点击所有单位
					jo.put("type", "1");
				}else {
					if("2".equals(role)) {//部管理员
						jo.put("state", "1");//点击所有单位
					}else if("3".equals(role)){//局管理员或局长
						if(orgId.equals(danweiid)) {
							jo.put("state", "2");//点击该局的数据
						}else {
							jo.put("state", "3");//它局的数据不能点击
						}
					}else {//局内其他人员
						jo.put("state", "4");//都不能点击
					}
					jo.put("type", "0");
				}
				if(!szorgid.contains(danweiid)) {
					ja.add(jo);
				}
				
			}
		}
		Response.json(ja);
	}

	/**
	 *  根据每局ID 查出办理中未反馈数据量
	 *
	 * @param year 年
	 * @param danweiid 直接部门ID
	 * @return int
	 */
	private int queryWfkCount(String danweiid, String year) {
		return documentInfoService.queryDocumentWfk(danweiid, year);
	}

	/**
	 * {
	"legend":["办理中","办结","常态落实"],
	"xdata":["办公厅","计划局","科定局","系统局","鉴定局","监管局","装备合作","政工","921","项目管理中心"],
	"blzdata":[2, 4, 7, 23, 25, 76, 135, 162, 32, 20],
	"bjdata":[2, 5, 9, 26, 28, 70, 175, 182, 48, 18],
	"ctlsdata":[2, 5, 9, 26, 28, 70, 175, 182, 48, 18]
	
	
	
	"otherdata":{
		"部首长":{
			"blz":{"type":"","id":"","month":""},
			"bj":{"type":"","id":"","month":""},
			"ctls":{"type":"","id":"","month":""}
		},
		"办公厅":{
			"blz":{"type":"","id":"","month":""},
			"bj":{"type":"","id":"","month":""},
			"ctls":{"type":"","id":"","month":""}
		}
	
	
	
	
	
	
	
}
	 */
	@ResponseBody
	@RequestMapping("/orglist2")
	public void orglist2(String year){
		JSONObject jo2=new JSONObject();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy");
		Map<String, Object> map = new HashMap<>();
		if(StringUtil.isEmpty(year)) {
			year=sdf.format(new Date());
		}
		map.put("year", year);
		List<Map<String, Object>> infoList = documentInfoService.queryListByOrgYear(map);
		JSONArray xdata=new JSONArray();
		JSONArray blzdata=new JSONArray();
		JSONArray bjdata=new JSONArray();
		JSONArray ctlsdata=new JSONArray();
		JSONArray wfkdata=new JSONArray();
		JSONArray legend=new JSONArray();
		//JSONArray otherdata=new JSONArray();
		JSONObject jo=new JSONObject();
		JSONObject jo3=new JSONObject();
		String orgId="";
		String month="all";
		String szorgid=getSzOrgid();//首长部门id
		String role=getNewRoleType();//返回值为1：首长，返回值为2：超级管理员、部管理员、即是部管理员又是局管理员，返回值为3：局管理员或局长，返回值为""：其他人员
		if("3".equals(role)) {//局管理员或局长
			orgId=baseAppUserService.getBareauByUserId(CurrentUser.getUserId());//获取当前人的局id
		}
		if (infoList!=null&&infoList.size()>0) {
			for (Map<String, Object> map2 : infoList) {
				String danweiid=(String) map2.get("ID");
				//授权--------strat
				jo=new JSONObject();
				jo.put("id",danweiid);
				jo.put("month",month);
				if("1".equals(role)) {//首长
					jo.put("state", "1");//点击所有单位
					jo.put("type", "1");
				}else {
					if("2".equals(role)) {//部管理员
						jo.put("state", "1");//点击所有单位
					}else if("3".equals(role)){//局管理员或局长
						if(orgId.equals(danweiid)) {
							jo.put("state", "2");//点击该局的数据
						}else {
							jo.put("state", "3");//它局的数据不能点击
						}
					}else {//局内其他人员
						jo.put("state", "4");//都不能点击
					}
					jo.put("type", "0");
				}
				if(!szorgid.contains(danweiid)) {
					jo3.put((String) map2.get("dwname"), jo);
					//授权--------end
					//otherdata.add(jo);
					xdata.add((String) map2.get("dwname"));
					bjdata.add((long) map2.get("bj"));
					ctlsdata.add((long) map2.get("ctls"));
					int wfkCount = queryWfkCount(danweiid, year);
					blzdata.add((long) map2.get("blz") - wfkCount);
					wfkdata.add(wfkCount);
				}
				
			}
		}
		legend.add("办理中");
		legend.add("已办结");
		legend.add("常态落实");
		jo2.put("legend",legend);
		jo2.put("xdata", xdata);
		jo2.put("blzdata", blzdata);
		jo2.put("bjdata", bjdata);
		jo2.put("ctlsdata", ctlsdata);
		jo2.put("wfkdata", wfkdata);
		jo2.put("otherdata", jo3);
		Response.json(jo2);
	}
	//documentDicService.queryDicByType(DbDicTypeDefined.DOCUMENT_TYPE)
	/**
	 * {
	{
	"legend":["JW主席批示指示","党,军,国务院决策部署","其他重要工作","JW首长批示指示","ZBFZB重要工作分工","ZBFZB领导批示指示"],
	"valdata":[
	    {"value":10, "name":"JW主席批示指示"},
	    {"value":20, "name":"党,军,国务院决策部署"},
	    {"value":30, "name":"其他重要工作"},
	    {"value":20, "name":"JW首长批示指示"},
	    {"value":10, "name":"ZBFZB重要工作分工"},
	    {"value":10, "name":"ZBFZB领导批示指示"}
	]
}
}
	 */
	@ResponseBody
	@RequestMapping("/orglist3")
	public void orglist3(String year){
		JSONObject jo2=new JSONObject();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy");
		Map<String, Object> map = new HashMap<>();
		if(StringUtil.isEmpty(year)) {
			year=sdf.format(new Date());
		}
		map.put("year", year);
		List<Map<String, Object>> infoList = documentInfoService.queryListByDicType(map);
		JSONArray valdata=new JSONArray();
		JSONArray legend=new JSONArray();
		if (infoList!=null&&infoList.size()>0) {
			for (Map<String, Object> map2 : infoList) {
				JSONObject jo=new JSONObject();
				String name=(String) map2.get("name");
				jo.put("name", name);
				jo.put("value", (long) map2.get("num"));
				legend.add(name);
				valdata.add(jo);
				
			}
		}
		jo2.put("legend", legend);
		jo2.put("valdata", valdata);
		Response.json(jo2);
	}
	/**
	 * {
	"xdata":["JW主席批示指示","JW首长批示指示","党,军,国务院决策部署","ZBFZB重要工作分工","其他重要工作","ZBFZB领导批示指示"],
	"wcldata":[10,4,7,23,25,76]
}
	 * @param year
	 */
	@ResponseBody
	@RequestMapping("/orglist4")
	public void orglist4(String year){
		JSONObject jo2=new JSONObject();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy");
		Map<String, Object> map = new HashMap<>();
		if(StringUtil.isEmpty(year)) {
			year=sdf.format(new Date());
		}
		map.put("year", year);
		List<Map<String, Object>> infoList = documentInfoService.queryListByDicStu(map);
		JSONArray wcldata=new JSONArray();
		JSONArray xdata=new JSONArray();
		if (infoList!=null&&infoList.size()>0) {
			for (Map<String, Object> map2 : infoList) {
				String name=(String) map2.get("name");
				double bj=(long) map2.get("bj");
				//double hh=bj;
				double total=(long) map2.get("total");
				xdata.add(name);
				if(total>0) {					
					wcldata.add((bj*100/total));
				}else {					
					wcldata.add(0);
				}
				
			}
		}
		jo2.put("xdata", xdata);
		jo2.put("wcldata", wcldata);
		Response.json(jo2);
	}
	/**
	 * @description:统计图当前首长批示落实统计查询
	 * @param year 年份
	 * @param startDate 开始截取时间
	 * @param endDate 结束截取时间
	 * @author:zhangyw
	 * @date:2019年6月23日
	 * @Version v1.0
	 */
	@ResponseBody
	@RequestMapping("/orglist5")
	public void orglist5(String year,String startDate,String endDate){
		JSONObject json =new JSONObject();
		String role=getNewRoleType();
		if(StringUtils.equals("1", role)) {
			json.put("clickFlag", "true");
			json.put("type", "1");
		}else if (StringUtils.equals("2", role)){
			json.put("clickFlag", "true");
			json.put("type", "0");
		}else {
			json.put("clickFlag", "false");
			json.put("type", "0");
		}
		Map<String, Object> map = new HashMap<>();
		/*if(StringUtil.isEmpty(year)) {
			year=DateUtil.getCurrentYear()+"";
		}*/
		//初始化走默认时间配置
		try {
			if (StringUtils.isBlank(startDate) && StringUtils.isBlank(endDate)) {
				DocumentDic documentDic = this.queryDocumentDic();
				if (documentDic != null) {
					String text = documentDic.getText();
					if (StringUtils.equals("3", documentDic.getValue())) {
						//传时间默认到至当前日期
						map.clear();
						map.put("startDate",text);
						map.put("endDate",this.acquireCurrDate(LocalDate.now()));
					}else if(StringUtils.equals("1", documentDic.getValue())) {
						map.clear();
						map.put("yearOrMonth", this.acquireCurrDate(LocalDate.now()).substring(0, 5));//2019年
					}else if(StringUtils.equals("2", documentDic.getValue())){
						map.clear();
						map.put("yearOrMonth", this.acquireCurrDate(LocalDate.now()).substring(0, 8));//2019年07月
					}else {
						logger.info("配置表配置项value值不符合约定，value：{}", documentDic.getValue());
						return;
					}
				}
			}else {
				map.clear();
				map.put("startDate",startDate);
				map.put("endDate",endDate);
			}
			json.put("list", documentInfoService.queryLeaderStatistics(map));
			Response.json(json);
		} catch (Exception e) {
			logger.info("调用统计图当前首长批示落实统计查询方法异常：{}", e);
		}
	}
	
	/**
	 * @description:首长端统计报表点击本年度办理情况和全年督办落实情况跳转页查询
	 * @author:zhangyw
	 * @date:2019年7月4日
	 * @Version v1.0
	 */
	@ResponseBody
	@RequestMapping("/leaderStatisticsPage")
	public void leaderStatisticsPage(Integer page, Integer pagesize,String year,String docStatus,String orgId,String typeId) {
		Map<String, Object> map = new HashMap<>();
		if(StringUtil.isEmpty(year)) {
			year=DateUtil.getCurrentYear()+"";
		}
		map.put("year", year);
		map.put("orgid",orgId);
		if(StringUtils.isNotBlank(typeId)) {
			map.put("type",typeId);
		}
		if(StringUtils.isNotBlank(docStatus)) {
			if(!StringUtils.equals("all", docStatus)) {
				map.put("state", docStatus);
			}
		}
		map.put("state", docStatus);
		PageHelper.startPage(page, pagesize);
		List<DocumentInfo> list = documentInfoService.queryList(map);
		GwPageUtils pageUtil = new GwPageUtils(list);
		for (DocumentInfo info : list) {
			//首长批示
			Map<String, Object> szpsMap = new HashMap<>();
			szpsMap.put("infoId", info.getId());
			List<DocumentSzps> szpsList = documentSzpsService.queryList(szpsMap);
			info.setSzpslist(szpsList);
		}
		Response.json(pageUtil);
	}
	
	/**
	 * @description:统计图首长批示落实统计详情页查询
	 * @param year 年份
	 * @param startDate 开始截取时间
	 * @param endDate 结束截取时间
	 * @author:zhangyw
	 * @date:2019年6月24日
	 * @Version v1.0
	 */
	@ResponseBody
	@RequestMapping("/leaderStatisticsList")
	public void leaderStatisticsList(Integer page, Integer pagesize,String year,String startDate,String endDate,String docStatus,String leaderId,String typeId) {
		Map<String, Object> map = new HashMap<>();
		/*if(StringUtil.isEmpty(year)) {
			year=DateUtil.getCurrentYear()+"";
		}
		map.put("year", year);
		map.put("startDate",startDate);
		map.put("endDate",endDate);*/
		try {
			if (StringUtils.isBlank(startDate) && StringUtils.isBlank(endDate)) {
                DocumentDic documentDic = this.queryDocumentDic();
                if (documentDic != null) {
                    String text = documentDic.getText();
                    if (StringUtils.equals("3", documentDic.getValue())) {
                        //传时间默认到至当前日期
                        map.clear();
                        map.put("startDate",text);
                        map.put("endDate",this.acquireCurrDate(LocalDate.now()));
                    }else if(StringUtils.equals("1", documentDic.getValue())) {
                        map.clear();
                        map.put("yearOrMonth", this.acquireCurrDate(LocalDate.now()).substring(0, 5));//2019年
                    }else if(StringUtils.equals("2", documentDic.getValue())){
                        map.clear();
                        map.put("yearOrMonth", this.acquireCurrDate(LocalDate.now()).substring(0, 8));//2019年07月
                    }else {
                        logger.info("配置表配置项value值不符合约定，value：{}", documentDic.getValue());
                        return;
                    }
                }
            }else {
                map.clear();
                map.put("startDate",startDate);
                map.put("endDate",endDate);
            }
			if(StringUtils.isNotBlank(docStatus)) {
				if(!StringUtils.equals("all", docStatus)) {
					map.put("status", docStatus);
				}
			}
			if(StringUtils.isNotBlank(typeId)) {
				map.put("type",typeId);
			}
			map.put("leaderId", leaderId);
			PageHelper.startPage(page, pagesize);
			List<DocumentInfo> list = documentInfoService.queryStatisticsList(map);
			GwPageUtils pageUtil = new GwPageUtils(list);
			for (DocumentInfo info : list) {
				/*
				//未读，最新反馈字段有值则标识为已更新
				if(StringUtils.isNotBlank(info.getLatestReply())) {
					info.setUpdateFlag("1");
				}*/
				//首长批示
				Map<String, Object> szpsMap = new HashMap<>();
				szpsMap.put("infoId", info.getId());
				List<DocumentSzps> szpsList = documentSzpsService.queryList(szpsMap);
				info.setSzpslist(szpsList);
			}
			Response.json(pageUtil);
		} catch (Exception e) {
			logger.info("调用统计图首长批示落实统计详情页查询方法异常：{}", e);
		}
	}
	
	//返回值为1：首长，返回值为2：超级管理员、部管理员、即是部管理员又是局管理员，返回值为3：局管理员或局长，返回值为""：其他人员
	public String getNewRoleType() {
		String newRoleTpe="";
		String loginUserId=CurrentUser.getUserId();
		String loginDeptId=CurrentUser.getDepartmentId();
		//首长单位id
		BaseAppConfig mapped = baseAppConfigService.queryObject(AppConstant.LEAD_TEAM);
		//当前登录人的管理员类型(0:超级管理员 ;1：部管理员；2：局管理员；3：即是部管理员又是局管理员)
		String adminType = adminSetService.getAdminTypeByUserId(loginUserId);
		//当前登录人的角色（3：局长；5：处长；6：其他）
		String roleType = roleSetService.getRoleTypeByUserId(loginUserId);
		if(StringUtils.equals("2", adminType)||StringUtils.equals("3", roleType)) {
			newRoleTpe="3";
		}
		if(("1").equals(adminType)||("3").equals(adminType)||("0").equals(adminType)) {
			newRoleTpe="2";
		}
		if(mapped!=null&&StringUtils.equals(loginDeptId,mapped.getValue())) {
			newRoleTpe="1";
		}
		return newRoleTpe;
	}
	
	public String getSzOrgid() {
		BaseAppConfig mapped = baseAppConfigService.queryObject(AppConstant.LEAD_TEAM);
		BaseAppConfig mapped2 = baseAppConfigService.queryObject(AppConstant.NOTDUBANTJ);
		String szids="";
		if(mapped != null && StringUtils.isNotBlank(mapped.getValue())){
			szids= mapped.getValue();
		}
		if(mapped2 != null && StringUtils.isNotBlank(mapped2.getValue())){
			szids+=","+ mapped2.getValue();
		}
		return szids;
	}
	/**
	 * 当前用户是否为首长或者部管理员下挂接首长
	 */
	@ResponseBody
	@RequestMapping("/isShouZhang")
	public void isShouZhang() {
		String szFlag="";
		JSONObject jsonObject = new JSONObject();
		String userId = CurrentUser.getUserId();
		String deptId = CurrentUser.getDepartmentId();
		String agentLeagerId = adminSetService.getAgentLeagerId(userId);
		if (StringUtils.isNotBlank(agentLeagerId)) {
			szFlag="2";				
		}
		BaseAppConfig mapped = baseAppConfigService.queryObject(AppConstant.LEAD_TEAM);//首长单位id
		if (mapped!=null&&deptId.equals(mapped.getValue())) {
			szFlag="1";
		}
		jsonObject.put("szFlag", szFlag);
		Response.json(jsonObject);
	}
	/**
	 * 查询所有首长
	 */
	@ResponseBody
	@RequestMapping("/allShouZhang")
	public void allShouZhang() {
		BaseAppConfig mapped = baseAppConfigService.queryObject(AppConstant.LEAD_TEAM);//首长单位id
		Map<String, Object> map = new HashMap<>();
		map.put("organid",mapped.getValue());
		List<BaseAppUser> baseAppUsers = baseAppUserService.queryList(map);
		List<BaseTreeObject> baseTreeObjects = new ArrayList<BaseTreeObject>();
		BaseTreeObject baseTreeObject = null;
		for (BaseAppUser baseAppUser : baseAppUsers) {
			baseTreeObject = new BaseTreeObject();
			baseTreeObject.setId(baseAppUser.getUserId());
			baseTreeObject.setText(baseAppUser.getTruename());
			baseTreeObject.setType("1");
			baseTreeObjects.add(baseTreeObject);
		}
		Response.json(baseTreeObjects);
	}
	/**
	 * 设置首长批示默认查询时间
	 * @param operateFlag
	 * @param setTime
	 */
	@ResponseBody
	@RequestMapping("/setDefaultApprovelTime")
	public void setDefaultApprovelTime(String operateFlag, String setTime) {
		String defaultTime = null;
		try {
			//年
			if (StringUtils.equals("1", operateFlag)) {
				defaultTime = this.acquireCurrDate(null).substring(0, 5);
			}else if (StringUtils.equals("2", operateFlag)){
				//月
				defaultTime = this.acquireCurrDate(null).substring(0, 8);
			}else {
				defaultTime = setTime;
			}
			if (StringUtils.isNotBlank(defaultTime)) {
				//保存时间配置
				DocumentDic documentDic = this.queryDocumentDic();
				if (documentDic != null) {
					documentDic.setText(defaultTime);
					documentDic.setValue(operateFlag);//操作类型：1-年；2-月；3-年月日
					documentDicService.update(documentDic);
					Response.json("result","success");
				}else {
					logger.info("根据ID：{}，dicType：{}查不到相关批示时间配置。","014","leader_idea_time_conf");
					Response.json("result","fail");
				}
			}else {
				Response.json("result","fail");
			}
		} catch (Exception e) {
			logger.info("调用首长批示时间设置方法异常：{}",e);
			Response.json("result","fail");
		}
	}
	/**
	 * 查询首长批示已经设置好的时间，返回给前端
	 */
	@ResponseBody
	@RequestMapping("/getDefaultApprovelTime")
	public void getDefaultApprovelTime() {
		DocumentDic documentDic =  this.queryDocumentDic();
		if (documentDic != null) {
			Response.json(documentDic);
		}
	}
	/**
	 * 查询首长批示时间设置值
	 * @return
	 */
	private DocumentDic queryDocumentDic() {
		return documentDicService.queryIdAndDicType("014","leader_idea_time_conf");
	}
	/**
	 * 获取当前日期的字符串类型yyyy年xx月zz日
	 * @return
	 * @throws Exception
	 */
	private String acquireCurrDate(LocalDate currDate) throws Exception{
		LocalDate currdate = LocalDate.now();
		int year = currdate.getYear();
		int month = currdate.getMonthValue();
		int day = currdate.getDayOfMonth();
		DateTimeFormatter dateTimeFormatter = null;
//		if (currDate == null) {
		dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
		String monthStr = null;
		String dayStr = null;
		if (month < 10) {
			monthStr = "0" + month;
		} else {
			monthStr = month+"";
		}

		if (day < 10) {
			dayStr = "0" + day;
		} else {
			dayStr = day+"";
		}
		currDate = LocalDate.parse(year+"年"+monthStr+"月"+dayStr+"日", dateTimeFormatter);
		/*}else {
			dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
		}*/
		return currDate.format(dateTimeFormatter);
	}
}
