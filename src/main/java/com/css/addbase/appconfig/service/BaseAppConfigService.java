package com.css.addbase.appconfig.service;

import java.util.List;
import java.util.Map;

import com.css.addbase.appconfig.entity.BaseAppConfig;
import com.css.addbase.apporgan.entity.BaseAppOrgan;

/**
 * 部门表
 * 
 * @author 中软信息系统工程有限公司
 * @email 
 * @date 2017-11-29 15:10:13
 */
public interface BaseAppConfigService {
	
	int queryTotal(Map<String,Object> map);
	
	BaseAppConfig queryObject(String type);
	
	String getValue(String type);
	
	String getDlrFormat(String userName,String dlrName);
	
	List<BaseAppConfig> typeList(String type);
	
	int UpdateValueByType(String value, String type);
	
	String objectValue(String type);

	List<BaseAppOrgan> queryAllDept();

	List<String> queryAllJuzhang();
}
