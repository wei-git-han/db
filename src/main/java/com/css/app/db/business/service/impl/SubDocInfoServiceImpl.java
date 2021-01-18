package com.css.app.db.business.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.css.addbase.appconfig.entity.BaseAppConfig;
import com.css.addbase.appconfig.service.BaseAppConfigService;
import com.css.addbase.apporgan.entity.BaseAppOrgan;
import com.css.addbase.apporgan.service.BaseAppUserService;
import com.css.addbase.apporgmapped.entity.BaseAppOrgMapped;
import com.css.addbase.apporgmapped.service.BaseAppOrgMappedService;
import com.css.addbase.constant.AppConstant;
import com.css.app.db.business.controller.RedisUtil;
import com.css.app.db.config.entity.AdminSet;
import com.css.app.db.config.service.AdminSetService;
import com.css.app.db.util.DbDocStatusDefined;
import com.css.base.utils.CurrentUser;
import com.css.base.utils.Response;
import com.css.base.utils.StringUtils;
import com.css.websocket.WebSocketHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.css.app.db.business.dao.SubDocInfoDao;
import com.css.app.db.business.entity.SubDocInfo;
import com.css.app.db.business.service.SubDocInfoService;
import org.springframework.util.LinkedMultiValueMap;

import javax.sound.sampled.Line;


@Service("subDocInfoService")
public class SubDocInfoServiceImpl implements SubDocInfoService {
	@Autowired
	private SubDocInfoDao subDocInfoDao;

	@Autowired
	private BaseAppConfigService baseAppConfigService;

	@Autowired
	private BaseAppOrgMappedService baseAppOrgMappedService;

	@Autowired
	private AdminSetService adminSetService;
	@Autowired
	private BaseAppUserService baseAppUserService;

	@Autowired
	private RedisUtil redisUtil;

	@Autowired
	private WebSocketHandle webSocketHandle;
	
	@Override
	public SubDocInfo queryObject(String id){
		return subDocInfoDao.queryObject(id);
	}
	
	@Override
	public List<SubDocInfo> queryList(Map<String, Object> map){
		return subDocInfoDao.queryList(map);
	}
	
	@Override
	public void save(SubDocInfo dbSubDocInfo){
		subDocInfoDao.save(dbSubDocInfo);
	}
	
	@Override
	public void update(SubDocInfo dbSubDocInfo){
		subDocInfoDao.update(dbSubDocInfo);
	}
	
	@Override
	public void delete(String id){
		subDocInfoDao.delete(id);
	}
	
	@Override
	public void deleteBatch(String[] ids){
		subDocInfoDao.deleteBatch(ids);
	}

	@Override
	public List<SubDocInfo> queryPersonList(Map<String, Object> map) {
		return subDocInfoDao.queryPersonList(map);
	}

	@Override
	public void updateDocStatus(Integer status,Date updateTime,String infoId) {
		subDocInfoDao.updateDocStatus(status, updateTime,infoId);
	}

	@Override
	public int queryMinDocStatus(String infoId,String subDeptId) {
		return subDocInfoDao.queryMinDocStatus(infoId,subDeptId);
	}

	@Override
	public int queryMaxDocStatus(String infoId, String subDeptId) {
		return subDocInfoDao.queryMaxDocStatus(infoId, subDeptId);
	}

	@Override
	public List<String> queryAllSubDeptIds(String infoId) {
		return subDocInfoDao.queryAllSubDeptIds(infoId);
				
	}

	@Override
	public List<SubDocInfo> queryLastEndSubInfo(String infoId) {
		return subDocInfoDao.queryLastEndSubInfo(infoId);
	}

	@Override
	public List<SubDocInfo> queryAllSubByInfoId(String infoId) {
		return subDocInfoDao.queryAllSubByInfoId(infoId);
	}

	@Override
	public List<SubDocInfo> queryAllSubInfo(Map<String, Object> map) {
		return subDocInfoDao.queryAllSubInfo(map);
	}

	@Override
	public SubDocInfo querySubDocInfoBySubIdAndInfoId(String subId, String infoId) {
		return subDocInfoDao.querySubDocInfoBySubIdAndInfoId(subId, infoId);
	}

	@Override
	public void updateSubDocInfoById(SubDocInfo subDocInfo) {
		subDocInfoDao.updateSubDocInfoById(subDocInfo);
		
	}

	@Override
	public List<SubDocInfo> queryPersonList1(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return subDocInfoDao.queryPersonList1(map);
	}
	@Override
	public List<SubDocInfo> queryTmingTaskList(Map<String, Object> map){
		return subDocInfoDao.queryTmingTaskList(map);
	}
	@Override
	public List<SubDocInfo> NoFeedbackTmingTaskList(){
		return subDocInfoDao.NoFeedbackTmingTaskList();
	}
	@Override
	public List<SubDocInfo> firstNoFeedbackTmingTaskList(){
		return subDocInfoDao.firstNoFeedbackTmingTaskList();
	}
	@Override
	public List<SubDocInfo> notTransferredTmingTaskList(){
		return subDocInfoDao.notTransferredTmingTaskList();
	}
	@Override
	public int queryNoBanJie(String infoId){
		return subDocInfoDao.queryNoBanJie(infoId);
	}
	@Override
	public int queryNoBjNum(String infoId){
		return subDocInfoDao.queryNoBjNum(infoId);
	}
	@Override
	public int queryTotalNum(String infoId){
		return subDocInfoDao.queryTotalNum(infoId);
	}
	@Override
	public List<SubDocInfo> queryForList(String infoId){
		return subDocInfoDao.queryForList(infoId);
	}

	@Override
	public List<SubDocInfo> queryAllTime(Map<String,Object> map){
		return subDocInfoDao.queryAllTime(map);
	}

	@Override
	public void sendMsgByWebSocket(String userId,int menuType,Boolean isSerf){
		JSONObject jsonObject = new JSONObject();
		//List<String> appConfigList = baseAppConfigService.queryAllJuzhang();
		//if(appConfigList != null && appConfigList.size() > 0){
			//for(int i = 0;i<appConfigList.size();i++){
				//String userId = appConfigList.get(i);
				System.out.println("dddd");
				//String value = redisUtil.getString(userId+"_dbcount");
				//当value等于true的时候才会触发websocket
				//if("true".equals(value)){
					//触发websocket
					int dbNumSum = dbNumSum(userId);//个人待办总数
					int getPersonTodoCount = this.getPersonTodoCount(userId);//个人待办菜单
					int getUnitTodoCount = this.getUnitTodoCount(userId);//局内待办菜单
					jsonObject.put("dbNumSum",dbNumSum);
					jsonObject.put("getPersonTodoCount",getPersonTodoCount);
					jsonObject.put("getUnitTodoCount",getUnitTodoCount);
				//}
				//恢复成默认
				//redisUtil.setString(userId+"_dbcount","false");

			//}
		int numAll = 0;
		if(menuType == 4){
			numAll = getPersonTodoCount;
		}else if(menuType == 5){
			numAll = getUnitTodoCount;
		}

		//}

		webSocketHandle.addSendMap(userId,4,false,String.valueOf(numAll));

		//return jsonObject;
	}

	public int dbNumSum(String loginUserId) {
		int dbNumSum = 0;
		int  grdbNum = 0;
		int  jndbNum = 0;
		//String loginUserId = CurrentUser.getUserId();
		Map<String, Object> value = new HashMap<String, Object>();
		String userMenuIds = this.getUserMenu(loginUserId);
		if(StringUtils.isBlank(userMenuIds)) {
			value.put("result", "success");
			value.put("count", dbNumSum);
			Response.json(value);
			return dbNumSum;
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
		return dbNumSum;
	}

	// 个人待办数
	private int getPersonTodoCount(String loginUserId) {
		int grdbNum = 0;
		Map<String, Object> personalMap = new HashMap<>();
		if (StringUtils.isNotBlank(loginUserId)) {
			personalMap.put("loginUserId", loginUserId);
		}
		personalMap.put("receiver", "receiver");
		List<SubDocInfo> subDocInfoPersonalList = this.queryPersonList1(personalMap);
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
		List<SubDocInfo> subDocInfoList = this.queryList(jumap);
		if (subDocInfoList != null && subDocInfoList.size() > 0) {
			jndbNum = subDocInfoList.size();
		}
		return jndbNum;
	}

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
}
