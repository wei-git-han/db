package com.css.app.db.business.service;

import com.css.app.db.business.entity.SubDocTracking;

import java.util.List;
import java.util.Map;

/**
 * 局内流转记录表
 * 
 * @author 中软信息系统工程有限公司
 * @email 
 * @date 2019-04-24 13:40:54
 */
public interface SubDocTrackingService {
	
	SubDocTracking queryObject(String id);
	
	List<SubDocTracking> queryList(Map<String, Object> map);
	
	void save(SubDocTracking dbSubDocTracking);
	
	void update(SubDocTracking dbSubDocTracking);
	
	void delete(String id);
	
	void deleteBatch(String[] ids);
	
	SubDocTracking  queryLatestRecord(String subId);
	
	void deleteBySubId(String subId);

	List<SubDocTracking> queryListBySubId(String subId);

	SubDocTracking queryNewRecord(String subId);

	String findDealUserName(String subId);
	
	int queryTaskNumByUserId(String userId,String year);

}
