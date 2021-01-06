package com.css.webservice;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.activemq.filter.function.splitFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.css.addbase.apporgan.entity.BaseAppUser;
import com.css.addbase.apporgan.service.BaseAppUserService;
import com.css.app.db.business.entity.DocumentInfo;
import com.css.app.db.business.entity.SubDocInfo;
import com.css.app.db.business.entity.SubDocTracking;
import com.css.app.db.business.service.DocumentInfoService;
import com.css.app.db.business.service.SubDocInfoService;
import com.css.app.db.business.service.SubDocTrackingService;
import com.css.app.db.config.entity.AdminSet;
import com.css.app.db.config.service.AdminSetService;
import com.css.app.db.util.DbDocStatusDefined;
import com.css.base.utils.CurrentUser;
import com.css.base.utils.Response;
import com.css.base.utils.StringUtils;

@RestController
@RequestMapping("/api")
public class ToDoApiController {
    @Autowired
    private AdminSetService adminSetService;
    @Autowired
    private SubDocInfoService subDocInfoService;
    @Autowired
    private BaseAppUserService baseAppUserService;
    @Autowired
    private SubDocTrackingService subDocTrackingService;
    
    @Autowired
    private DocumentInfoService documentInfoService;

    /**
     * 督办待办数app
     */

    @ResponseBody
    @RequestMapping("/db/todo")
    public void dbNumSum() {
        int dbNumSum = 0;
        int  grdbNum = 0;
        int  jndbNum = 0;
        String loginUserId = CurrentUser.getUserId();
        Map<String, Object> value = new HashMap<String, Object>();
        String userMenuIds = this.getUserMenu(loginUserId);
        if(StringUtils.isBlank(userMenuIds)) {
        	value.put("result", "success");
            value.put("count", dbNumSum);
            Response.json(value);
            return;
        }
        if(userMenuIds.contains("002")) {
        	grdbNum= getPersonTodoCount(loginUserId);
    		dbNumSum = dbNumSum+grdbNum;
        }
        if(userMenuIds.contains("003")) {
        	jndbNum= getUnitTodoCount(loginUserId);
    		dbNumSum = dbNumSum+jndbNum;
        }
       
        value.put("result", "success");
        value.put("count", dbNumSum);
        //value.put("grdbNum",grdbNum);
        //value.put("jndbNum",jndbNum);
        Response.json(value);
    }
    
    // 个人待办数
    private int getPersonTodoCount(String loginUserId) {
        int grdbNum = 0;
        Map<String, Object> personalMap = new HashMap<>();
        if (StringUtils.isNotBlank(loginUserId)) {
            personalMap.put("loginUserId", loginUserId);
        }
        personalMap.put("receiver", "receiver");
        List<SubDocInfo> subDocInfoPersonalList = subDocInfoService.queryPersonList1(personalMap);
        if (subDocInfoPersonalList != null && subDocInfoPersonalList.size() > 0) {
            grdbNum = subDocInfoPersonalList.size();
        }
    	return grdbNum;
    }
    // 局内待办数
    private int getUnitTodoCount(String loginUserId) {
    	 int jndbNum = 0;
         Map<String, Object> jumap = new HashMap<>();
         String orgId = baseAppUserService.getBareauByUserId(loginUserId);
         if (StringUtils.isNotBlank(orgId)) {
             jumap.put("orgId", orgId);
         }
         jumap.put("docStatus", DbDocStatusDefined.DAI_ZHUAN_BAN);
         // 查询列表数据
         List<SubDocInfo> subDocInfoList = subDocInfoService.queryList(jumap);
         if (subDocInfoList != null && subDocInfoList.size() > 0) {
             jndbNum = subDocInfoList.size();
         }
    	return jndbNum;
    }

    //获取当前登录人的菜单
    private String getUserMenu(String loginUserId){
		String menuIds = "002";
		Map<String, Object> map = new HashMap<>();
		map.put("adminType", "2");
		map.put("userId", loginUserId);
		List<AdminSet> list = adminSetService.queryList(map);
		if(!list.isEmpty()) {
			menuIds=menuIds+",003";
		}		
		return menuIds;
    }
    
    
    /**
     * 督办配合负一屏做年度统计
     * @param deptId
     * @return
     * @return
     */
    @ResponseBody
    @RequestMapping("/getAllTaskByDept")
    public void getAllTaskByDept(String deptId) {
        JSONObject jsonObject = new JSONObject();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String currentYear = String.valueOf(year);
        List<BaseAppUser> appUserList = baseAppUserService.queryAllTaskByDept(deptId);
        if (appUserList != null && appUserList.size() > 0) {
            for (int i = 0; i < appUserList.size(); i++) {
            	int bjNum = 0;//办结数
            	int totalNum = 0;//总数
            	double bjPassenger = 0;
            	float day = 0;
                BaseAppUser baseAppUser = appUserList.get(i);
                String userId = baseAppUser.getUserId();
                List<SubDocTracking> list = subDocTrackingService.queryTaskNumByUserId(userId, currentYear);
                totalNum = list.size();
                if(list.size() == 0) {
                	baseAppUser.setFinishRate("0");
                	baseAppUser.setFinishDay("0");
                }else {
                	if(list != null && list.size() > 0) {
                    	for(int j = 0;j<list.size();j++) {
                    		SubDocTracking subDocTracking = list.get(j);
                    		String subId = subDocTracking.getSubId();
                    		SubDocInfo subDocInfo = subDocInfoService.queryObject(subId);
                    		if(subDocInfo != null) {
                    			int status = subDocInfo.getDocStatus();
                        		if(status > 9) {//大于等于9是办结
                        			bjNum +=1;
                        		}
                    		}
                    		
                    	}
                    }
                	DecimalFormat decimalFormat = new DecimalFormat("#.0000");
                	float bjPassengers  = (float) bjNum /(float)totalNum;
                	String format = decimalFormat.format(bjPassengers);
                	Float parseFloat = Float.parseFloat(format);
                	//bjPassenger  = ((new BigDecimal((float) bjNum /(float) totalNum).doubleValue()));
                    //String bjl = String.valueOf(bjPassenger);
                    baseAppUser.setTaskNum(totalNum);
                    baseAppUser.setFinishRate(parseFloat.toString());
                    
                    Date lastTime = list.get(0).getCreatedTime();
                    Date firstTime = list.get(totalNum - 1).getCreatedTime();
                    long millisecond = lastTime.getTime() - firstTime.getTime();
                    day = millisecond / (24 * 60 * 60 * 1000 )/totalNum;
                    if(day < 1){
                        day = 1;
                    }
                    baseAppUser.setFinishDay(String.valueOf(day));
				}
                
                
                
            }
        }
        jsonObject.put("list", appUserList);
        jsonObject.put("result","success");
        Response.json(jsonObject);

    }

}

