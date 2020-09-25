package com.css.app.db.config.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.css.app.db.config.dao.AdminSetDao;
import com.css.app.db.config.entity.AdminSet;
import com.css.app.db.config.service.AdminSetService;
import com.css.base.utils.CurrentUser;
import com.css.base.utils.StringUtils;



@Service("adminSetService")
public class AdminSetServiceImpl implements AdminSetService {
	@Autowired
	private AdminSetDao adminSetDao;
	
	//@Value("${csse.dccb.appId}")
	private  String appId;
	
	//@Value("${csse.dccb.appSecret}")
	private  String clientSecret;
	
	@Override
	public AdminSet queryObject(String id){
		return adminSetDao.queryObject(id);
	}
	
	@Override
	public List<AdminSet> queryList(Map<String, Object> map){
		return adminSetDao.queryList(map);
	}
	
	@Override
	public void save(AdminSet dbAdminSet){
		adminSetDao.save(dbAdminSet);
	}
	
	@Override
	public void update(AdminSet dbAdminSet){
		adminSetDao.update(dbAdminSet);
	}
	
	@Override
	public void delete(String id){
		adminSetDao.delete(id);
	}
	
	@Override
	public void deleteBatch(String[] ids){
		adminSetDao.deleteBatch(ids);
	}

	@Override
	public List<AdminSet> queryJuAdminList(String userId) {
		return adminSetDao.queryJuAdminList(userId);
	}

	@Override
	public List<String> queryUserIdByOrgId(String orgId) {
		return adminSetDao.queryUserIdByOrgId(orgId);
	}

	@Override
	public String getAdminTypeByUserId(String userId) {
		//管理员类型（0:超级管理员 ;1：部管理员；2：局管理员；3：即是部管理员又是局管理员）
		String adminFlag = "";
		boolean admin = CurrentUser.getIsManager(appId, clientSecret);
		if(admin) {
			adminFlag="0";
		}else {
			//当前登录人的管理员类型
			Map<String, Object> adminMap = new HashMap<>();
			adminMap.put("userId",userId);
			List<AdminSet> adminList = adminSetDao.queryList(adminMap);
			if(adminList != null && adminList.size()>0) {
				if(adminList.size()==1) {
					String adminType = adminList.get(0).getAdminType();
					adminFlag=adminType;
				}if(adminList.size()>1) {
					adminFlag="3";
				}
			}
		}
		return adminFlag;
	}

	@Override
	public String getAgentLeagerId(String userId) {
		String agentLeaderId="";
		Map<String, Object> map = new HashMap<>();
		map.put("userId", userId);
		map.put("adminType", "1");
		List<AdminSet> adminSets = adminSetDao.queryList(map);
		if (adminSets != null && adminSets.size() > 0) {
			AdminSet adminSet = adminSets.get(0);
			if (StringUtils.isNotBlank(adminSet.getSeniorOfficial())) {
				agentLeaderId=adminSet.getSeniorOfficialId();				
			}
		}
		return agentLeaderId;
	}
	
}
