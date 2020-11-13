package com.css.app.db.business.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.css.app.db.business.entity.SubDocInfo;
import com.css.base.dao.BaseDao;

/**
 * 各子分支主记录表
 * 
 * @author 中软信息系统工程有限公司
 * @email 
 * @date 2019-04-18 16:40:43
 */
@Mapper
public interface SubDocInfoDao extends BaseDao<SubDocInfo> {
	
	List<SubDocInfo> queryPersonList(Map<String, Object> map);
	List<SubDocInfo> queryPersonList1(Map<String, Object> map);
	
	void updateDocStatus(Integer status,Date updateTime,String infoId);
	
	int queryMinDocStatus(String infoId,String subDeptId);
	
	int queryMaxDocStatus(String infoId,String subDeptId);
	
	List<String> queryAllSubDeptIds(String infoId);
	
	List<SubDocInfo> queryLastEndSubInfo(String infoId);
	
	List<SubDocInfo> queryAllSubByInfoId(String infoId);
	
	List<SubDocInfo> queryAllSubInfo(Map<String, Object> map);

	SubDocInfo querySubDocInfoBySubIdAndInfoId(String subId, String infoId);

	void updateSubDocInfoById(SubDocInfo subDocInfo);
	List<SubDocInfo> queryTmingTaskList(Map<String, Object> map);
	List<SubDocInfo> NoFeedbackTmingTaskList();
	List<SubDocInfo> firstNoFeedbackTmingTaskList();
	List<SubDocInfo> notTransferredTmingTaskList();
	
	@Select("select * from DB_SUB_DOC_INFO where INFO_ID = #{infoId}")
	List<SubDocInfo> queryForList(String infoId);

	@Select("select count(*) from DB_SUB_DOC_INFO where INFO_ID = #{0} and DOC_STATUS= 13")
	int queryNoBanJie(String infoId);

	@Select("select count(*) from DB_SUB_DOC_INFO where INFO_ID = #{0} and DOC_STATUS= 12")
	int queryNoBjNum(String infoId);

	@Select("select count(*) from DB_SUB_DOC_INFO where INFO_ID = #{0}")
	int queryTotalNum(String infoId);

	@Select("select * from DB_SUB_DOC_INFO where  UPDATE_TIME is not null and UPDATE_TIME like '%'||#{0}||'%' and SUB_DEPT_ID = #{1} and DOC_STATUS = '12' order by UPDATE_TIME desc")
	List<SubDocInfo> queryAllTime(String year,String orgId);
	
}
