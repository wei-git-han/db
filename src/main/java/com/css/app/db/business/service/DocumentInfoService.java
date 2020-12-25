package com.css.app.db.business.service;

import com.css.app.db.business.dto.LeaderStatisticsDto;
import com.css.app.db.business.entity.DocumentInfo;
import com.css.app.db.config.entity.DocumentDic;

import java.util.List;
import java.util.Map;

/**
 * 督办基本信息表
 * 
 * @author 中软信息系统工程有限公司
 * @email
 * @date 2019-04-18 16:34:38
 */
public interface DocumentInfoService {

	DocumentInfo queryObject(String id);

	List<DocumentInfo> queryList(Map<String, Object> map);

	void save(DocumentInfo dbDocumentInfo);

	void update(DocumentInfo dbDocumentInfo);

	void delete(String id);

	void deleteBatch(String[] ids);

	List<Map<String, Object>> queryListByYear(Map<String, Object> map);


	List<DocumentInfo> queryAllBjList(String year);

	List<Map<String,Object>> queryListByOrgAndYear(Map<String,Object> map);
	
	int queryChaoShiByYear(Map<String, Object> map);

	List<Map<String, Object>> queryListByOrgYear(Map<String, Object> map);

	List<Map<String, Object>> queryListByDicType(Map<String, Object> map);

	List<Map<String, Object>> queryListByDicStu(Map<String, Object> map);

	List<DocumentInfo> queryPersonList(Map<String, Object> map);

	List<Map<String, Object>> queryListByDicStutas(Map<String, Object> map);

	void deleteByCheHui(String id);

	List<DocumentDic> queryDicByType(Map<String, Object> map);

	String getMaxSecurity(String[] stringIds);

	List<DocumentInfo> queryInfoByParam(Map<String, Object> map);

	// 统计图首长批示落实统计查询
	List<LeaderStatisticsDto> queryLeaderStatistics(Map<String, Object> map);

	List<DocumentInfo> queryStatisticsList(Map<String, Object> map);

	void updateDocumentInfoById(DocumentInfo documentInfo);

	List<DocumentInfo> queryReplyList(Map<String, Object> map);

	List<DocumentInfo> queryReplyListPlus(Map<String, Object> map);
	
	
	List<DocumentInfo> queryReplyListForWfk(Map<String, Object> map);

	int queryDocumentWfk(String danweiid, String year);

	int queryWfkLeaderStatistics(Map<String, Object> map);

	int queryDocumentWfk2(String danweiid, String year);

	List<DocumentInfo> queryNewList(Map<String, Object> map);
	
	List<DocumentInfo> queryNewListSort(Map<String, Object> map);

	List<Map<String, Object>> queryListByOrgIdAndYear(Map<String,Object> map);
	
	List<Map<String, Object>> queryListByOrgYear1(Map<String, Object> map);
	
	int queryWfkCount3(Map<String,Object> map);
}
