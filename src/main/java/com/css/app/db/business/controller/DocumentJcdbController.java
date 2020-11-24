package com.css.app.db.business.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.css.addbase.apporgan.entity.BaseAppOrgan;
import com.css.app.db.business.dto.LeaderStatisticsDto;
import com.css.app.db.business.entity.SubDocInfo;
import com.css.app.db.business.service.SubDocInfoService;
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
//import sun.plugin2.message.BestJREAvailableMessage;


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
    @Autowired
    private SubDocInfoService subDocInfoService;


    /**
     * 数据统计报表-(年度,状态数量)
     * {
     * 年度"year":"2019",
     * 总数"total":100,
     * 办理中"blz":100,
     * 办结"bj":100,
     * 常态落实"ctls":100,
     * 完成率"wcl":"98%"
     * }
     */
    @ResponseBody
    @RequestMapping("/list")
    public void list(String year) {
        JSONObject jo = new JSONObject();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Map<String, Object> map = new HashMap<>();
        if (StringUtil.isEmpty(year)) {
            year = sdf.format(new Date());
        }
        map.put("year", year);
        List<Map<String, Object>> infoList = documentInfoService.queryListByYear(map);
        double blz = 0;
        double bj = 0;
        double ctls = 0;
        double total = 0;
        double wfk = 0;
        // sum(blz+bj+ctls) as total,sum(blz) as blz,sum(bj) as bj,sum(ctls) as ctls
        if (infoList != null && infoList.size() > 0) {
            Map<String, Object> map2 = infoList.get(0);
            if (map2 != null) {
                int wfkCount = this.queryWfkCount(null, year);
                blz = (long) map2.get("blz") - wfkCount;
                bj = (long) map2.get("bj");
                ctls = (long) map2.get("ctls");
                total = (long) map2.get("total");
                wfk = wfkCount;
            }
        }
        jo.put("blz", blz < 0 ? 0 : blz);
        jo.put("bj", bj);
        jo.put("ctls", ctls);
        jo.put("total", total);
        jo.put("wfk", wfk);
        jo.put("year", year);
        if (total > 0) {
            jo.put("wcl", (bj + ctls) * 100 / total);
        } else {
            jo.put("wcl", "0%");
        }
        Response.json(jo);
    }


//	@ResponseBody
//	@RequestMapping("/szCount")
//	public void szCount(String year,String month,String organId){
//		JSONObject jo=new JSONObject();
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyy");
//		Map<String, Object> map = new HashMap<>();
//		Map<String,Object> objectMap = new HashMap<>();
//		if(StringUtil.isEmpty(year)) {
//			year=sdf.format(new Date());
//		}
//		if(StringUtils.isBlank(organId)){
//			organId = baseAppUserService.getBareauByUserId(CurrentUser.getUserId());
//		}
//		long  blz=0;
//		double wcl=0;
//		long bj = 0;
//		map.put("year", year);
//		map.put("month", month);
//		map.put("organId", organId);
//		objectMap.put("year",year);
//		//List<Map<String, Object>> infoList = documentInfoService.queryListByOrgIdAndYear(map);
//		List<Map<String, Object>> infoList = documentInfoService.queryListByYear(objectMap);
//		List<DocumentInfo> documentInfoList = documentInfoService.queryAllBjList(year);
//		//List<Map<String, Object>> infoListAll = documentInfoService.queryListByYear(map);
//		//List<Map<String, Object>> infoListAll = documentInfoService.queryListByOrgYear(map);
//		double  ctls=0;
//		double  total=0;
//		double days = 0;
//		int onTimebj = 0;
//		float day = 0;
//		double  wfk =0;
//		if(infoList != null && infoList.size() > 0){
//			Map<String, Object> map2=infoList.get(0);
//			if(map2!=null) {
//				int wfkCount = this.queryWfkCount(null, year);
//				blz= (long) map2.get("blz") - wfkCount;
//				bj= (long) map2.get("bj");
//				ctls= (long) map2.get("ctls");
//				total= (long) map2.get("total");
//				wcl = (bj+ctls)*100/total;
//				wfk= wfkCount;
//			}
//		}
//		//List<Map<String, Object>> infoListAll = documentInfoService.queryListByOrgAndYear(map);
//		//List<Map<String, Object>> infoList = documentInfoService.queryListByOrgYear(map);
//		map.put("status", 1);
//		int overTimewbj = 0;//超时未结
////		int overTimewbj = documentInfoService.queryChaoShiByYear(map);//超时未结
//		map.put("status", 2);
//		int overTimebj = 0;//超时办结
////		int overTimebj = documentInfoService.queryChaoShiByYear(map);//超时办结
//
//
//		//double  bj=0;
//
//		List<SubDocInfo> docInfoList = subDocInfoService.queryAllTime(year,organId);
//		if(docInfoList != null && docInfoList.size() > 0){
//			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//			int t = docInfoList.size();
//			Date lastTime = docInfoList.get(0).getUpdateTime();
//			Date firstTime = docInfoList.get(t-1).getUpdateTime();
//			long millisecond = lastTime.getTime() - firstTime.getTime();
//			day = millisecond/(24*60*60*1000*60);
//		}
//
//		if (documentInfoList!=null && documentInfoList.size()>0) {
////			Map<String, Object> map2=infoList.get(0);
////			if(map2!=null) {
////				int wfkCount = this.queryWfkCount(null, year);
////				blz= (long) map2.get("blz") - wfkCount;
////				bj= (long) map2.get("bj");
////				ctls= (long) map2.get("ctls");
////				total= (long) map2.get("total");
////				days=(double) map2.get("days");
////			}
//			total = documentInfoList.size();
//			String leaderTime = "";
//			for(int i = 0;i<documentInfoList.size();i++){
//				DocumentInfo documentInfo = documentInfoList.get(i);
//				String infoId = documentInfo.getInfoId();
//				Integer docStatus = documentInfo.getStatus();
//				List<DocumentSzps> documentSzps = documentSzpsService.queryByInfo(infoId);
//				if(documentSzps != null && documentSzps.size() > 0){
//					for(DocumentSzps documentSzps1 : documentSzps){
//						if(StringUtils.isNotBlank(documentSzps1.getCreatedTime())){
//							leaderTime = documentSzps1.getCreatedTime();
//						}
//					}
//				}
//
//				boolean t = false;
//				if(documentSzps != null && documentSzps.size() > 0){
//					t = isOverTreeMonth(leaderTime,docStatus);//是否超3个月
//				}else {
//					System.out.println("dddddddddddddd");
//				}
//				if(docStatus == 2){//办结
//					if(t){
//						overTimebj += 1;//超时办结
//					}else{
//						onTimebj += 1;//按时办结
//					}
//				}else{//没有办结
//					if(t){
//						overTimewbj += 1;//超时未结
//					}
//
//
//
//				}
//			}
//
//		}
//		jo.put("overTimewbj", overTimewbj);//超时未结
//		jo.put("overTimebj", overTimebj);//超时办结
//		jo.put("onTimeblz", blz < 0 ? 0 : blz);//按时在办
//		jo.put("onTimebj", onTimebj);//按时办结
//		jo.put("aveDays", day);//已办结事项平均天数
//		jo.put("zsl", total);//总数量
//		jo.put("year", year);//今年
//		jo.put("month", month);//月份
//		jo.put("organId", organId);//部门id
//		if(total>0) {
//			DecimalFormat df = new DecimalFormat("#.00");
//			String format = df.format((bj+ctls)*100/total);
//			//	long round = Math.round((bj+ctls)*100/total);
//			if(format.equals(".00")) {
//				jo.put("wcl", 0);
//			}else {
//				jo.put("wcl", wcl);
//			}
//
//		}else {
//			jo.put("wcl", "0%");
//		}
//		Response.json(jo);
//	}


    /**
     * 数据统计报表-(年度,状态数量)
     * {"id":"01","dwname":"办公厅","blz":"100","bj":"101","ctls":"102"},
     * {"id":"02","dwname":"办公厅","blz":"100","bj":"101","ctls":"102"},
     * {"id":"03","dwname":"办公厅","blz":"100","bj":"101","ctls":"102"},
     * {"id":"04","dwname":"办公厅","blz":"100","bj":"101","ctls":"102"},
     * {"id":"05","dwname":"办公厅","blz":"100","bj":"101","ctls":"102"}
     * <p>
     * d.SUB_DEPT_ID,d.SUB_DEPT_NAME,sum(blz) as blz,sum(bj) as bj,sum(ctls) as ctls
     */
    @ResponseBody
    @RequestMapping("/orglist")
    public void orglist(String year, String month) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Map<String, Object> map = new HashMap<>();
        if (StringUtil.isEmpty(year)) {
            int mon = Calendar.MONTH + 1;
            year = sdf.format(new Date());
            if (StringUtil.isEmpty(month)) {
                year += "-" + (mon < 10 ? "0" + month : month + "");
            } else if (!StringUtil.equals("all", month)) {
                year += "-" + month;
            }
        }
        map.put("year", year);
        //按年统计各局数据
        long startTime = System.currentTimeMillis();
        List<Map<String, Object>> infoList = documentInfoService.queryListByOrgYear(map);
        long endTime = System.currentTimeMillis();
        System.out.println("----------------按年统计各局数据:" + (endTime - startTime));
        String orgId = "";
        String szorgid = getSzOrgid();//获取首长的部门id
        String role = getNewRoleType();//返回值为1：首长，返回值为2：超级管理员、部管理员、即是部管理员又是局管理员，返回值为3：局管理员或局长，返回值为""：其他人员
        if ("3".equals(role)) {//局管理员或局长
            orgId = baseAppUserService.getBareauByUserId(CurrentUser.getUserId());//获取当前人的局id
        }
        JSONArray ja = new JSONArray();
        if (infoList != null && infoList.size() > 0) {
            for (Map<String, Object> map2 : infoList) {
                String danweiid = (String) map2.get("ID");
                JSONObject jo = new JSONObject();
                jo.put("bj", (long) map2.get("bj"));
                jo.put("ctls", (long) map2.get("ctls"));
                //对结果进行二级过滤：针对办理中拆分：办理中+未反馈
                //未反馈量
                long start = System.currentTimeMillis();
                int wfkCount = this.queryWfkCount2(danweiid, year);
                long end = System.currentTimeMillis();
                System.out.println("----------------未反馈:" + (end - start));
                //各局完成比率  办结+常态落实/总数
                long total = (long) map2.get("blz") + (long) map2.get("bj") + (long) map2.get("ctls") + wfkCount;
                if (total == 0) {
                    jo.put("wcl", "0%");
                } else {
                    long bjAddCtls = (long) map2.get("bj") + (long) map2.get("ctls");
                    if (bjAddCtls == 0) {
                        jo.put("wcl", "0%");
                    } else {
                        //完成率>=99.5,就不再加0.5了，取整为99
                        double wclv = ((double) bjAddCtls / total);
                        //double wclv = (double) bjAddCtls/total;
                        double wclDouble = 0.00;
                        if (wclv >= 0.995 && wclv < 1.000) {
                            wclDouble = (int) (wclv * 100);
                        } else {
                            wclDouble = (int) (wclv * 100 + 0.5);
                        }
                        //double wclDouble =(int)(((double) bjAddCtls / total) * 100 + 0.5);
                        String wclStr = wclDouble + "";
                        String wcl = wclStr.substring(0, wclStr.lastIndexOf("."));
                        jo.put("wcl", wcl + "%");
                    }
                }
                //增加未反馈
                jo.put("wfk", wfkCount);
                //原办理中数据-未反馈量 = 现办理中数据
                long blz = (long) map2.get("blz");
                jo.put("blz", blz < 0 ? 0 : blz);
                jo.put("id", danweiid);
                jo.put("dwname", (String) map2.get("dwname"));

                if ("1".equals(role)) {//首长
                    jo.put("state", "1");//点击所有单位
                    jo.put("type", "1");
                } else {
                    if ("2".equals(role)) {//部管理员
                        jo.put("state", "1");//点击所有单位
                    } else if ("3".equals(role)) {//局管理员或局长
                        if (orgId.equals(danweiid)) {
                            jo.put("state", "2");//点击该局的数据
                        } else {
                            jo.put("state", "3");//它局的数据不能点击
                        }
                    } else {//局内其他人员
                        jo.put("state", "4");//都不能点击
                    }
                    jo.put("type", "0");
                }
                if (!szorgid.contains(danweiid)) {
                    ja.add(jo);
                }

            }
        }
        Response.json(ja);
    }

    /**
     * 根据每局ID 查出办理中未反馈数据量
     *
     * @param year     年
     * @param danweiid 直接部门ID
     * @return int
     */
    private int queryWfkCount(String danweiid, String year) {
        return documentInfoService.queryDocumentWfk(danweiid, year);
    }

    /**
     * 根据每局ID 查出办理中未反馈数据量
     *
     * @param year     年
     * @param danweiid 直接部门ID
     * @return int
     */
    private int queryWfkCount2(String danweiid, String year) {
        return documentInfoService.queryDocumentWfk2(danweiid, year);
    }

    /**
     * {
     * "legend":["办理中","办结","常态落实"],
     * "xdata":["办公厅","计划局","科定局","系统局","鉴定局","监管局","装备合作","政工","921","项目管理中心"],
     * "blzdata":[2, 4, 7, 23, 25, 76, 135, 162, 32, 20],
     * "bjdata":[2, 5, 9, 26, 28, 70, 175, 182, 48, 18],
     * "ctlsdata":[2, 5, 9, 26, 28, 70, 175, 182, 48, 18]
     * <p>
     * <p>
     * <p>
     * "otherdata":{
     * "部首长":{
     * "blz":{"type":"","id":"","month":""},
     * "bj":{"type":"","id":"","month":""},
     * "ctls":{"type":"","id":"","month":""}
     * },
     * "办公厅":{
     * "blz":{"type":"","id":"","month":""},
     * "bj":{"type":"","id":"","month":""},
     * "ctls":{"type":"","id":"","month":""}
     * }
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * }
     */
    @ResponseBody
    @RequestMapping("/orglist2")
    public void orglist2(String year) {
        JSONObject jo2 = new JSONObject();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Map<String, Object> map = new HashMap<>();
        if (StringUtil.isEmpty(year)) {
            year = sdf.format(new Date());
        }
        map.put("year", year);
        List<Map<String, Object>> infoList = documentInfoService.queryListByOrgYear(map);
        JSONArray xdata = new JSONArray();
        JSONArray blzdata = new JSONArray();
        JSONArray bjdata = new JSONArray();
        JSONArray ctlsdata = new JSONArray();
        JSONArray wfkdata = new JSONArray();
        JSONArray legend = new JSONArray();
        //JSONArray otherdata=new JSONArray();
        JSONObject jo = new JSONObject();
        JSONObject jo3 = new JSONObject();
        String orgId = "";
        String month = "all";
        String szorgid = getSzOrgid();//首长部门id
        String role = getNewRoleType();//返回值为1：首长，返回值为2：超级管理员、部管理员、即是部管理员又是局管理员，返回值为3：局管理员或局长，返回值为""：其他人员
        if ("3".equals(role)) {//局管理员或局长
            orgId = baseAppUserService.getBareauByUserId(CurrentUser.getUserId());//获取当前人的局id
        }
        if (infoList != null && infoList.size() > 0) {
            for (Map<String, Object> map2 : infoList) {
                String danweiid = (String) map2.get("ID");
                //授权--------strat
                jo = new JSONObject();
                jo.put("id", danweiid);
                jo.put("month", month);
                if ("1".equals(role)) {//首长
                    jo.put("state", "1");//点击所有单位
                    jo.put("type", "1");
                } else {
                    if ("2".equals(role)) {//部管理员
                        jo.put("state", "1");//点击所有单位
                    } else if ("3".equals(role)) {//局管理员或局长
                        if (orgId.equals(danweiid)) {
                            jo.put("state", "2");//点击该局的数据
                        } else {
                            jo.put("state", "3");//它局的数据不能点击
                        }
                    } else {//局内其他人员
                        jo.put("state", "4");//都不能点击
                    }
                    jo.put("type", "0");
                }
                if (!szorgid.contains(danweiid)) {
                    jo3.put((String) map2.get("dwname"), jo);
                    //授权--------end
                    //otherdata.add(jo);
                    xdata.add((String) map2.get("dwname"));
                    bjdata.add((long) map2.get("bj"));
                    ctlsdata.add((long) map2.get("ctls"));
                    int wfkCount = queryWfkCount2(danweiid, year);
                    long blz = (long) map2.get("blz");
                    blzdata.add(blz < 0 ? 0 : blz);
                    wfkdata.add(wfkCount);
                }

            }
        }
        legend.add("办理中");
        legend.add("已办结");
        legend.add("常态落实");
        legend.add("未反馈");
        jo2.put("legend", legend);
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
     * {
     * "legend":["JW主席批示指示","党,军,国务院决策部署","其他重要工作","JW首长批示指示","ZBFZB重要工作分工","ZBFZB领导批示指示"],
     * "valdata":[
     * {"value":10, "name":"JW主席批示指示"},
     * {"value":20, "name":"党,军,国务院决策部署"},
     * {"value":30, "name":"其他重要工作"},
     * {"value":20, "name":"JW首长批示指示"},
     * {"value":10, "name":"ZBFZB重要工作分工"},
     * {"value":10, "name":"ZBFZB领导批示指示"}
     * ]
     * }
     * }
     */
    @ResponseBody
    @RequestMapping("/orglist3")
    public void orglist3(String year) {
        JSONObject jo2 = new JSONObject();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Map<String, Object> map = new HashMap<>();
        if (StringUtil.isEmpty(year)) {
            year = sdf.format(new Date());
        }
        map.put("year", year);
        List<Map<String, Object>> infoList = documentInfoService.queryListByDicType(map);
        JSONArray valdata = new JSONArray();
        JSONArray legend = new JSONArray();
        if (infoList != null && infoList.size() > 0) {
            for (Map<String, Object> map2 : infoList) {
                JSONObject jo = new JSONObject();
                String name = (String) map2.get("name");
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
     * "xdata":["JW主席批示指示","JW首长批示指示","党,军,国务院决策部署","ZBFZB重要工作分工","其他重要工作","ZBFZB领导批示指示"],
     * "wcldata":[10,4,7,23,25,76]
     * }
     *
     * @param year
     */
    @ResponseBody
    @RequestMapping("/orglist4")
    public void orglist4(String year) {
        JSONObject jo2 = new JSONObject();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Map<String, Object> map = new HashMap<>();
        if (StringUtil.isEmpty(year)) {
            year = sdf.format(new Date());
        }
        map.put("year", year);
        List<Map<String, Object>> infoList = documentInfoService.queryListByDicStu(map);
        JSONArray wcldata = new JSONArray();
        JSONArray xdata = new JSONArray();
        if (infoList != null && infoList.size() > 0) {
            for (Map<String, Object> map2 : infoList) {
                String name = (String) map2.get("name");
                double bj = (long) map2.get("bj");
                //double hh=bj;
                double total = (long) map2.get("total");
                xdata.add(name);
                if (total > 0) {
                    wcldata.add((bj * 100 / total));
                } else {
                    wcldata.add(0);
                }

            }
        }
        jo2.put("xdata", xdata);
        jo2.put("wcldata", wcldata);
        Response.json(jo2);
    }

    /**
     * @param year      年份
     * @param startDate 开始截取时间
     * @param endDate   结束截取时间
     * @description:统计图当前首长批示落实统计查询
     * @author:zhangyw
     * @date:2019年6月23日
     * @Version v1.0
     */
    @ResponseBody
    @RequestMapping("/orglist5")
    public void orglist5(String year, String startDate, String endDate) {
        JSONObject json = new JSONObject();
        String role = getNewRoleType();
        if (StringUtils.equals("1", role)) {
            json.put("clickFlag", "true");
            json.put("type", "1");
        } else if (StringUtils.equals("2", role)) {
            json.put("clickFlag", "true");
            json.put("type", "0");
        } else {
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
                // 需求更改：不再从配置表里获取时间段，直接从前端获取年份
				/*DocumentDic documentDic = this.queryDocumentDic();
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
				}*/
                map.clear();
                map.put("yearOrMonth", year);//2019年
            } else {
                map.clear();
                map.put("startDate", startDate);
                map.put("endDate", endDate);
            }


            List<LeaderStatisticsDto> leaderStatisticsDtos = documentInfoService.queryLeaderStatistics(map);

            leaderStatisticsDtos.forEach(leaderStatisticsDto -> {
                String leaderId = leaderStatisticsDto.getLeaderId();
                map.put("leaderId", leaderId);
                //现办理中 = 原办理中 - 未反馈
                int wfkLeaderStatistics = this.queryWfkLeaderStatistics(map);
                Integer blzCount1 = leaderStatisticsDto.getBlzCount();
                int blzCount = leaderStatisticsDto.getBlzCount() - wfkLeaderStatistics;
                leaderStatisticsDto.setBlzCount(blzCount < 0 ? 0 : blzCount);
                leaderStatisticsDto.setWfkCount(wfkLeaderStatistics);
            });
            json.put("list", leaderStatisticsDtos);
            Response.json(json);
        } catch (Exception e) {
            logger.info("调用统计图当前首长批示落实统计查询方法异常：{}", e);
        }
    }

    /**
     * 首长批示统计  增加未反馈数据统计
     *
     * @param map 参数
     * @return 统计数
     */
    private int queryWfkLeaderStatistics(Map<String, Object> map) {
        return documentInfoService.queryWfkLeaderStatistics(map);
    }

    /**
     * @description:首长端统计报表点击本年度办理情况和全年督办落实情况跳转页查询
     * @author:zhangyw
     * @date:2019年7月4日
     * @Version v1.0
     */
    @ResponseBody
    @RequestMapping("/leaderStatisticsPage")
    public void leaderStatisticsPage(Integer page, Integer pagesize, String year, String docStatus, String orgId, String typeId) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtil.isEmpty(year)) {
            year = DateUtil.getCurrentYear() + "";
        }
        map.put("year", year);
        map.put("orgid", orgId);
        if (StringUtils.isNotBlank(typeId)) {
            map.put("type", typeId);
        }
        if (StringUtils.isNotBlank(docStatus)) {
            if (!StringUtils.equals("all", docStatus)) {
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
     * @param year      年份
     * @param startDate 开始截取时间
     * @param endDate   结束截取时间
     * @description:统计图首长批示落实统计详情页查询
     * @author:zhangyw
     * @date:2019年6月24日
     * @Version v1.0
     */
    @ResponseBody
    @RequestMapping("/leaderStatisticsList")
    public void leaderStatisticsList(Integer page, Integer pagesize, String year, String startDate, String endDate, String docStatus, String leaderId, String typeId) {
        Map<String, Object> map = new HashMap<>();
		/*if(StringUtil.isEmpty(year)) {
			year=DateUtil.getCurrentYear()+"";
		}
		map.put("year", year);
		map.put("startDate",startDate);
		map.put("endDate",endDate);*/
        try {
            if (StringUtils.isBlank(startDate) && StringUtils.isBlank(endDate)) {
                // 需求更改：不再从配置表里获取时间段，直接从前端获取年份
                /*DocumentDic documentDic = this.queryDocumentDic();
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
                }*/
                map.clear();
                map.put("yearOrMonth", year + "年");//2019年
            } else {
                map.clear();
                map.put("startDate", startDate);
                map.put("endDate", endDate);
            }
            if (StringUtils.isNotBlank(docStatus)) {
                if (!StringUtils.equals("all", docStatus)) {
                    map.put("status", docStatus);
                }
            }
            if (StringUtils.isNotBlank(typeId)) {
                map.put("type", typeId);
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
        String newRoleTpe = "";
        String loginUserId = CurrentUser.getUserId();
        String loginDeptId = CurrentUser.getDepartmentId();
        //首长单位id
        BaseAppConfig mapped = baseAppConfigService.queryObject(AppConstant.LEAD_TEAM);
        //当前登录人的管理员类型(0:超级管理员 ;1：部管理员；2：局管理员；3：即是部管理员又是局管理员)
        String adminType = adminSetService.getAdminTypeByUserId(loginUserId);
        //当前登录人的角色（3：局长；5：处长；6：其他）
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

    public String getSzOrgid() {
        BaseAppConfig mapped = baseAppConfigService.queryObject(AppConstant.LEAD_TEAM);
        BaseAppConfig mapped2 = baseAppConfigService.queryObject(AppConstant.NOTDUBANTJ);
        String szids = "";
        if (mapped != null && StringUtils.isNotBlank(mapped.getValue())) {
            szids = mapped.getValue();
        }
        if (mapped2 != null && StringUtils.isNotBlank(mapped2.getValue())) {
            szids += "," + mapped2.getValue();
        }
        return szids;
    }

    /**
     * 当前用户是否为首长或者部管理员下挂接首长
     */
    @ResponseBody
    @RequestMapping("/isShouZhang")
    public void isShouZhang() {
        String szFlag = "";
        JSONObject jsonObject = new JSONObject();
        String userId = CurrentUser.getUserId();
        String deptId = CurrentUser.getDepartmentId();
        String agentLeagerId = adminSetService.getAgentLeagerId(userId);
        if (StringUtils.isNotBlank(agentLeagerId)) {
            szFlag = "2";
        }
        BaseAppConfig mapped = baseAppConfigService.queryObject(AppConstant.LEAD_TEAM);//首长单位id
        if (mapped != null && deptId.equals(mapped.getValue())) {
            szFlag = "1";
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
        map.put("organid", mapped.getValue());
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
     *
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
            } else if (StringUtils.equals("2", operateFlag)) {
                //月
                defaultTime = this.acquireCurrDate(null).substring(0, 8);
            } else {
                defaultTime = setTime;
            }
            if (StringUtils.isNotBlank(defaultTime)) {
                //保存时间配置
                DocumentDic documentDic = this.queryDocumentDic();
                if (documentDic != null) {
                    documentDic.setText(defaultTime);
                    documentDic.setValue(operateFlag);//操作类型：1-年；2-月；3-年月日
                    documentDicService.update(documentDic);
                    Response.json("result", "success");
                } else {
                    logger.info("根据ID：{}，dicType：{}查不到相关批示时间配置。", "014", "leader_idea_time_conf");
                    Response.json("result", "fail");
                }
            } else {
                Response.json("result", "fail");
            }
        } catch (Exception e) {
            logger.info("调用首长批示时间设置方法异常：{}", e);
            Response.json("result", "fail");
        }
    }

    /**
     * 查询首长批示已经设置好的时间，返回给前端
     */
    @ResponseBody
    @RequestMapping("/getDefaultApprovelTime")
    public void getDefaultApprovelTime() {
        DocumentDic documentDic = this.queryDocumentDic();
        if (documentDic != null) {
            Response.json(documentDic);
        }
    }

    /**
     * 查询首长批示时间设置值
     *
     * @return
     */
    private DocumentDic queryDocumentDic() {
        return documentDicService.queryIdAndDicType("014", "leader_idea_time_conf");
    }

    /**
     * 获取当前日期的字符串类型yyyy年xx月zz日
     *
     * @return
     * @throws Exception
     */
    private String acquireCurrDate(LocalDate currDate) throws Exception {
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
            monthStr = month + "";
        }

        if (day < 10) {
            dayStr = "0" + day;
        } else {
            dayStr = day + "";
        }
        currDate = LocalDate.parse(year + "年" + monthStr + "月" + dayStr + "日", dateTimeFormatter);
		/*}else {
			dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
		}*/
        return currDate.format(dateTimeFormatter);
    }


    /**
     * 数据统计报表-(年度,状态数量)
     * {
     * 年度"year":"2019",
     * 总数"total":100,
     * 办理中"blz":100,
     * 办结"bj":100,
     * 常态落实"ctls":100,
     * 完成率"wcl":"98%"
     * }
     */
    @ResponseBody
    @RequestMapping("/szCount1")
    public void szCount1(String year, String month, String organId) {
        JSONObject jo = new JSONObject();
        List<BaseAppOrgan> baseAppOrganList = baseAppConfigService.queryAllDept();
        int daySum = baseAppOrganList.size();
        int overTimewbj = 0;//超时未结
        int overTimebj = 0;//超时办结
        long blz = 0;
        int onTimebj = 0;
        float day = 0;
        double total = 0;
        double wcl = 0;
        long sum = 0;
        long bj = 0;
        double ctls = 0;
        if (baseAppOrganList != null && baseAppOrganList.size() > 0) {
            for (int m = 0; m < baseAppOrganList.size(); m++) {
                BaseAppOrgan baseAppOrgan = baseAppOrganList.get(m);
                if(StringUtils.equals("首长",baseAppOrgan.getName()) || StringUtils.equals("部首长",baseAppOrgan.getName())){
                    continue;
                }
                String orgId = baseAppOrgan.getId();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                Map<String, Object> map = new HashMap<>();
                if (StringUtil.isEmpty(year)) {
                    year = sdf.format(new Date());
                }
                map.put("year", year);
                map.put("month", month);
                map.put("organId", orgId);
                List<Map<String, Object>> infoList = documentInfoService.queryListByOrgIdAndYear(map);
                List<Map<String, Object>> infoListAll = documentInfoService.queryListByOrgYear(map);
                if (infoListAll != null && infoListAll.size() > 0) {
                    for (int j = 0; j < infoListAll.size(); j++) {
                        String deptId = (String) infoListAll.get(j).get("ID");
                        if (orgId.equals(deptId)) {
                            int wfkCount = this.queryWfkCount2(deptId, year);
                            blz = (long) infoListAll.get(j).get("blz");
                            bj = (long) infoListAll.get(j).get("bj");
                            long ctlss = (long) infoListAll.get(j).get("ctls");
                            sum = blz + bj + ctlss + wfkCount;
                            long bjSum = bj + ctlss;
                            wcl = ((new BigDecimal((float) bjSum / sum).doubleValue()) * 100);
                            //break;
                        }
                    }
                }
                List<SubDocInfo> docInfoList = subDocInfoService.queryAllTime(year, orgId);
                if (docInfoList != null && docInfoList.size() > 0) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    int t = docInfoList.size();
                    Date lastTime = docInfoList.get(0).getUpdateTime();
                    Date firstTime = docInfoList.get(t - 1).getUpdateTime();
                    long millisecond = lastTime.getTime() - firstTime.getTime();
                    day = millisecond / (24 * 60 * 60 * 1000 * 60);
                }
//                if (infoList != null && infoList.size() > 0) {
//                    total = infoList.size();
//                    String leaderTime = "";
//                    for (int i = 0; i < infoList.size(); i++) {
//                        Map<String, Object> map1 = infoList.get(i);
//                        int docStatus = (Integer) map1.get("DOC_STATUS");
//                        String infoId = (String) map1.get("INFO_ID");
//                        List<DocumentSzps> documentSzps = documentSzpsService.queryByInfo(infoId);
//                        if (documentSzps != null && documentSzps.size() > 0) {
//                            for (DocumentSzps documentSzps1 : documentSzps) {
//                                if (StringUtils.isNotBlank(documentSzps1.getCreatedTime())) {
//                                    leaderTime = documentSzps1.getCreatedTime();
//                                    break;
//                                }
//                            }
//                        }
//                        boolean t = false;
//                        if (documentSzps != null && documentSzps.size() > 0) {
//                            t = isOverTreeMonth(leaderTime, docStatus);//是否超3个月
//                            if (docStatus == 12) {//办结
//                                if (t) {
//                                    overTimebj += 1;//超时办结
//                                } else {
//                                    onTimebj += 1;//按时办结
//                                }
//                            } else {//没有办结
//                                if (t) {
//                                    overTimewbj += 1;//超时未结
//                                }
//                            }
//                        } else {
//                            onTimebj += 1;//按时办结
//                        }
//
//                    }
//                }
                bj += bj;
                ctls += ctls;
                blz += blz;
                day += day;
                total += sum;
            }
        }
        if (daySum > 0) {
            day = day / daySum;
        }
        wcl = ((new BigDecimal((float) onTimebj / total).doubleValue()) * 100);
//        long bjSum = overTimebj + onTimebj;
//        wcl = ((new BigDecimal((float) bjSum / total).doubleValue()) * 100);
        int t = (int) Math.round(wcl);
        jo.put("overTimewbj", overTimewbj);//超时未结
        jo.put("overTimebj", ctls);//办理反馈
        jo.put("onTimeblz", blz < 0 ? 0 : blz);//按时在办
        jo.put("onTimebj", bj);//按时办结
        jo.put("aveDays", day);//已办结事项平均天数
        jo.put("zsl", total);//总数量
        if (total > 0) {
            jo.put("wcl", wcl);

        } else {
            jo.put("wcl", "0%");
        }
        //jo.put("year", year);//今年
        //jo.put("month", month);//月份
        //jo.put("organId", organId);//部门id
        Response.json(jo);

    }

    @ResponseBody
    @RequestMapping("/szCount")
    public void countSum(String year, String month, String organId){
        JSONObject jsonObject = new JSONObject();
        Map<String, Object> map = new HashMap<>();
        map.put("year",year);
        double  blz=0;
        double  bj=0;
        double  ctls=0;
        double  total=0;
        double  wfk =0;
        int wfkCount = 0;
        float day = 0;
        int t = 0;
        List<DocumentInfo> documentInfoList = documentInfoService.queryAllBjList(year);
        if(documentInfoList != null && documentInfoList.size() > 0){
            t = documentInfoList.size();
            Date lastTime = documentInfoList.get(0).getCreatedTime();
            Date firstTime = documentInfoList.get(t - 1).getCreatedTime();
            long millisecond = lastTime.getTime() - firstTime.getTime();
            day = millisecond / (24 * 60 * 60 * 1000 )/t;
        }
        List<Map<String, Object>> infoList = documentInfoService.queryListByOrgYear(map);
        if (infoList != null && infoList.size() > 0) {
            for (Map<String, Object> map2 : infoList) {
                String danweiid = (String) map2.get("ID");
                JSONObject jo = new JSONObject();
                jo.put("bj", (long) map2.get("bj"));
                bj = bj + (long) map2.get("bj");
                jo.put("ctls", (long) map2.get("ctls"));
                ctls = ctls + (long) map2.get("ctls");
                //对结果进行二级过滤：针对办理中拆分：办理中+未反馈
                //未反馈量
                long start = System.currentTimeMillis();
                int wfkCount1 = 0;
                wfkCount1 = this.queryWfkCount2(danweiid, year);
                wfkCount = wfkCount + this.queryWfkCount2(danweiid, year);
                long end = System.currentTimeMillis();
                System.out.println("----------------未反馈:" + (end - start));
                //各局完成比率  办结+常态落实/总数
                total = total + (long) map2.get("blz") + (long) map2.get("bj") + (long) map2.get("ctls") + wfkCount1;
                if (total == 0) {
                    jo.put("wcl", "0%");
                } else {
                    long bjAddCtls = (long) map2.get("bj") + (long) map2.get("ctls");
                    if (bjAddCtls == 0) {
                        jo.put("wcl", "0%");
                    } else {
                        double wclv = ((double) bjAddCtls / total);
                        double wclDouble = 0.00;
                        if (wclv >= 0.995 && wclv < 1.000) {
                            wclDouble = (int) (wclv * 100);
                        } else {
                            wclDouble = (int) (wclv * 100 + 0.5);
                        }
                        String wclStr = wclDouble + "";
                        String wcl = wclStr.substring(0, wclStr.lastIndexOf("."));
                        jo.put("wcl", wcl + "%");
                    }
                }
                blz = blz + (long) map2.get("blz");
            }
        }
        double bjAddCtls = bj + ctls;
        if (bjAddCtls == 0) {
            jsonObject.put("wcl", "0%");
        } else {
            double wclv = bjAddCtls / total;
            double wclDouble = 0.00;
            if (wclv >= 0.995 && wclv < 1.000) {
                wclDouble = (int) (wclv * 100);
            } else {
                wclDouble = (int) (wclv * 100 + 0.5);
            }
            String wclStr = wclDouble + "";
            String wcl = wclStr.substring(0, wclStr.lastIndexOf("."));
            jsonObject.put("wcl", wcl + "%");
        }

        jsonObject.put("overTimewbj",wfkCount);//未反馈
        jsonObject.put("onTimeblz",blz);//时限内在办
        jsonObject.put("onTimebj",bj);//按时办结
        jsonObject.put("overTimebj",ctls);//常态落实
        jsonObject.put("zsl",total);//总量
        jsonObject.put("aveDays",day);//平均办理天数
        Response.json(jsonObject);
    }

    @ResponseBody
    @RequestMapping("/count")
    public void count(String year, String month, String organId) {
        JSONObject jo = new JSONObject();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Map<String, Object> map = new HashMap<>();
        if (StringUtil.isEmpty(year)) {
            year = sdf.format(new Date());
        }
        if (StringUtils.isBlank(organId)) {
            organId = baseAppUserService.getBareauByUserId(CurrentUser.getUserId());
        }
        long blz = 0;
        double wcl = 0;
        long bj = 0;
        int wfkCount = 0;
        long ctls = 0;
        map.put("year", year);
        map.put("month", month);
        map.put("organId", organId);
        long sum = 0;
        List<Map<String, Object>> infoList = documentInfoService.queryListByOrgIdAndYear(map);
        //List<Map<String, Object>> infoListAll = documentInfoService.queryListByYear(map);
        List<Map<String, Object>> infoListAll = documentInfoService.queryListByOrgYear(map);
        if (infoListAll != null && infoListAll.size() > 0) {
            for (int j = 0; j < infoListAll.size(); j++) {
                String deptId = (String) infoListAll.get(j).get("ID");
                if (organId.equals(deptId)) {
                    wfkCount = this.queryWfkCount2(deptId, year);
                    blz = (long) infoListAll.get(j).get("blz");
                    bj = (long) infoListAll.get(j).get("bj");
                    ctls = (long) infoListAll.get(j).get("ctls");
                    sum = blz + bj + ctls + wfkCount;
                    long bjSum = bj + ctls;
                    wcl = ((new BigDecimal((float) bjSum / sum).doubleValue()) * 100);
                    break;
                }
            }
        }
        //List<Map<String, Object>> infoListAll = documentInfoService.queryListByOrgAndYear(map);
        //List<Map<String, Object>> infoList = documentInfoService.queryListByOrgYear(map);
        map.put("status", 1);
        int overTimewbj = 0;//超时未结
//		int overTimewbj = documentInfoService.queryChaoShiByYear(map);//超时未结
        map.put("status", 2);
        int overTimebj = 0;//超时办结
//		int overTimebj = documentInfoService.queryChaoShiByYear(map);//超时办结


        //double  bj=0;
        //double ctls = 0;
        double total = 0;
        double days = 0;
        int onTimebj = 0;
        float day = 0;
        List<SubDocInfo> docInfoList = subDocInfoService.queryAllTime(year, organId);
        if (docInfoList != null && docInfoList.size() > 0) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            int t = docInfoList.size();
            Date lastTime = docInfoList.get(0).getUpdateTime();
            Date firstTime = docInfoList.get(t - 1).getUpdateTime();
            long millisecond = lastTime.getTime() - firstTime.getTime();
            day = millisecond / (24 * 60 * 60 * 1000 * 60)/t;
        }

        if (infoList != null && infoList.size() > 0) {
//			Map<String, Object> map2=infoList.get(0);
//			if(map2!=null) {
//				int wfkCount = this.queryWfkCount(null, year);
//				blz= (long) map2.get("blz") - wfkCount;
//				bj= (long) map2.get("bj");
//				ctls= (long) map2.get("ctls");
//				total= (long) map2.get("total");
//				days=(double) map2.get("days");
//			}
            total = infoList.size();
            String leaderTime = "";
            for (int i = 0; i < infoList.size(); i++) {
                Map<String, Object> map1 = infoList.get(i);
                int docStatus = (Integer) map1.get("DOC_STATUS");
                String infoId = (String) map1.get("INFO_ID");
                List<DocumentSzps> documentSzps = documentSzpsService.queryByInfo(infoId);
                if (documentSzps != null && documentSzps.size() > 0) {
                    for (DocumentSzps documentSzps1 : documentSzps) {
                        if (StringUtils.isNotBlank(documentSzps1.getCreatedTime())) {
                            leaderTime = documentSzps1.getCreatedTime();
                            break;
                        }
                    }
                }

                boolean t = false;
                if (documentSzps != null && documentSzps.size() > 0) {
                    t = isOverTreeMonth(leaderTime, docStatus);//是否超3个月
                    if (docStatus == 12) {//办结
                        if (t) {
                            overTimebj += 1;//超时办结
                        } else {
                            onTimebj += 1;//按时办结
                        }
                    } else {//没有办结
                        if (t) {
                            overTimewbj += 1;//超时未结
                        }
                    }
                } else {
                    onTimebj += 1;//按时办结
                }

            }

        }
        //long bjSum = overTimebj + onTimebj;
        //wcl = ((new BigDecimal((float) bjSum / total).doubleValue()) * 100);

        int t = (int) Math.round(wcl);
        jo.put("overTimewbj", wfkCount);//超时未结
        jo.put("overTimebj", ctls);//超时办结
        jo.put("onTimeblz", blz < 0 ? 0 : blz);//按时在办
        jo.put("onTimebj", bj);//按时办结
        jo.put("aveDays", day);//已办结事项平均天数
        jo.put("zsl", sum);//总数量
        jo.put("year", year);//今年
        jo.put("month", month);//月份
        jo.put("organId", organId);//部门id
        if (total > 0) {
            DecimalFormat df = new DecimalFormat("#.00");
            String format = df.format((bj + ctls) * 100 / total);
            //	long round = Math.round((bj+ctls)*100/total);
            if (format.equals(".00")) {
                jo.put("wcl", 0);
            } else {
                jo.put("wcl", t+ "%");
            }

        } else {
            jo.put("wcl", "0%");
        }
        Response.json(jo);
    }

    private boolean isOverTreeMonth(String leaderTime, int status) {
        boolean t = false;
        // 2019年05月08日
        if (StringUtils.isNotBlank(leaderTime)) {
            LocalDate currdate = LocalDate.now();
            LocalDate leaderDate = LocalDate.parse(leaderTime, DateTimeFormatter.ofPattern("yyyy年MM月dd日"));
            int docStatus = status;
            // 如果该文没有办结且超过批示时间三个月的，显示超期提示；
            if (docStatus < 12) {
                if ((int) ChronoUnit.YEARS.between(leaderDate, currdate) == 0) {
                    if ((int) ChronoUnit.MONTHS.between(leaderDate, currdate) == 3) {
                        // 提取天数
                        if (currdate.getDayOfMonth() > leaderDate.getDayOfMonth()) {
                            t = true;
                        }
                    } else if ((int) ChronoUnit.MONTHS.between(leaderDate, currdate) > 3) {
                        t = true;
                    }
                } else if ((int) ChronoUnit.YEARS.between(leaderDate, currdate) > 0) {
                    t = true;
                }
            } else if (docStatus == 12) {
                if ((int) ChronoUnit.YEARS.between(leaderDate, currdate) == 0) {
                    if ((int) ChronoUnit.MONTHS.between(leaderDate, currdate) == 3) {
                        // 提取天数
                        if (currdate.getDayOfMonth() > leaderDate.getDayOfMonth()) {
                            t = true;
                        }
                    } else if ((int) ChronoUnit.MONTHS.between(leaderDate, currdate) > 3) {
                        t = true;
                    }
                } else if ((int) ChronoUnit.YEARS.between(leaderDate, currdate) > 0) {
                    t = true;
                }
            }
        }
        return t;
    }


}
