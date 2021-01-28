package com.css.addbase.appconfig.dao;

import java.util.List;
import java.util.Map;

import com.css.addbase.apporgan.entity.BaseAppOrgan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.css.addbase.appconfig.entity.BaseAppConfig;
import com.css.base.dao.BaseDao;

/**
 * 部门表
 * 
 * @author 中软信息系统工程有限公司
 * @email 
 * @date 2017-11-29 15:10:13
 */
@Mapper
public interface BaseAppConfigDao extends BaseDao<BaseAppConfig> {
	
	int queryTotal(Map<String,Object> map);
	
	List<BaseAppConfig> typeList(String type);
	
	@Update("update BASE_APP_CONFIG set value=#{0} where type=#{1}")	
	int UpdateValueByType(String value, String type);

	@Select("select * from BASE_APP_ORGAN where PARENT_ID = 'root'")
	List<BaseAppOrgan> queryAllDept();

	@Select("select value from BASE_APP_CONFIG where type = 'web_socket_jiaobiao'")
	List<String> queryAllJuzhang();

	@Select("select * from BASE_APP_CONFIG where type = 'web_socket_jiaobiao' and value = #{0}")
	BaseAppConfig queryByUserId(String userId);
	
	
}
