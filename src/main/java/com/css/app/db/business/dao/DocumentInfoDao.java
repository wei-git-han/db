package com.css.app.db.business.dao;

import com.css.app.db.business.dto.LeaderStatisticsDto;
import com.css.app.db.business.entity.DocumentInfo;
import com.css.app.db.config.entity.DocumentDic;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.css.base.dao.BaseDao;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.security.access.method.P;

/**
 * 督办基本信息表
 * 
 * @author 中软信息系统工程有限公司
 * @email
 * @date 2019-04-18 16:34:38
 */
@Mapper
public interface DocumentInfoDao extends BaseDao<DocumentInfo> {

	List<Map<String, Object>> queryListByYear(Map<String, Object> map);

	@Select("select * from DB_DOCUMENT_INFO where status = '2' and CREATED_TIME like '%'||#{0}||'%' order by CREATED_TIME desc")
	List<DocumentInfo> queryAllBjList(String year);

	List<Map<String, Object>> queryListByOrgAndYear(Map<String, Object> map);

	int queryChaoShiByYear(Map<String, Object> map);
	
	List<Map<String, Object>> queryListByOrgYear(Map<String, Object> map);

	List<Map<String, Object>> queryListByDicType(Map<String, Object> map);

	List<Map<String, Object>> queryListByDicStu(Map<String, Object> map);

	List<DocumentInfo> queryPersonList(Map<String, Object> map);

	List<Map<String, Object>> queryListByDicStutas(Map<String, Object> map);

	List<DocumentDic> queryDicByType(Map<String, Object> map);

	String getMaxSecurity(String[] id);

	List<DocumentInfo> queryInfoByParam(Map<String, Object> map);

	void updateDocumentInfoById(DocumentInfo documentInfo);

	List<LeaderStatisticsDto> queryLeaderStatistics(Map<String, Object> map);

	List<DocumentInfo> queryStatisticsList(Map<String, Object> map);

	List<DocumentInfo> queryReplyList(Map<String, Object> map);

	List<DocumentInfo> queryReplyListPlus(Map<String, Object> map);
	
	
	List<DocumentInfo> queryReplyListForWfk(Map<String, Object> map);

	int queryDocumentWfk(@Param("danweiid") String danweiid, @Param("year") String year);

	int queryWfkLeaderStatistics(Map<String, Object> map);

	int queryDocumentWfk2(@Param("danweiid") String danweiid, @Param("year") String year);

	List<DocumentInfo> queryNewList(Map<String, Object> map);
	
	List<DocumentInfo> queryNewListSort(Map<String, Object> map);

	List<Map<String, Object>> queryListByOrgIdAndYear(Map<String,Object> map);
	
	List<Map<String, Object>> queryListByOrgYear1(Map<String, Object> map);
	
	int queryWfkCount3(Map<String,Object> map);

}
