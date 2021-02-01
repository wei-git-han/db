package com.css.app.db.business.dao;

import com.css.app.db.business.entity.SubDocInfo;
import com.css.app.db.business.entity.SubDocTracking;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.css.base.dao.BaseDao;

/**
 * 局内流转记录表
 * 
 * @author 中软信息系统工程有限公司
 * @email 
 * @date 2019-04-24 13:40:54
 */
@Mapper
public interface SubDocTrackingDao extends BaseDao<SubDocTracking> {
	
	SubDocTracking  queryLatestRecord(String subId);
	void deleteBySubId(String subId);
	List<SubDocTracking> queryListBySubId(String subId);


	@Select("select * from DB_SUB_DOC_TRACKING where SUB_ID = #{0}")
	List<SubDocTracking> queryAllListBySubId(String subId);
	SubDocTracking queryNewRecord(String subId);
	String findDealUserName(String subId);
	@Select("select * from DB_SUB_DOC_TRACKING where RECEIVER_ID = #{0} and CREATED_TIME like '%'||#{1}||'%' order by CREATED_TIME desc")
	List<SubDocTracking> queryTaskNumByUserId(String userId,String year);
}
