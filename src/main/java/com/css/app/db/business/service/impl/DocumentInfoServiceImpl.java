package com.css.app.db.business.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.css.app.db.business.dao.ApprovalOpinionDao;
import com.css.app.db.business.dao.DocumentBjjlDao;
import com.css.app.db.business.dao.DocumentCbjlDao;
import com.css.app.db.business.dao.DocumentInfoDao;
import com.css.app.db.business.dao.DocumentReadDao;
import com.css.app.db.business.dao.DocumentZbjlDao;
import com.css.app.db.business.dao.ReplyAttacDao;
import com.css.app.db.business.dao.ReplyExplainDao;
import com.css.app.db.business.dao.SubDocInfoDao;
import com.css.app.db.business.dao.SubDocTrackingDao;
import com.css.app.db.business.dto.LeaderStatisticsDto;
import com.css.app.db.business.entity.DocumentInfo;
import com.css.app.db.business.entity.SubDocInfo;
import com.css.app.db.business.service.DocumentInfoService;
import com.css.app.db.config.entity.DocumentDic;

@Service("documentInfoService")
public class DocumentInfoServiceImpl implements DocumentInfoService {
	@Autowired
	private DocumentInfoDao documentInfoDao;
	@Autowired
	private ApprovalOpinionDao approvalOpinionDao;
	@Autowired
	private DocumentBjjlDao documentBjjlDao;
	@Autowired
	private DocumentCbjlDao documentCbjlDao;
	@Autowired
	private DocumentReadDao documentReadDao;
	@Autowired
	private DocumentZbjlDao documentZbjlDao;
	@Autowired
	private ReplyAttacDao replyAttacDao;
	@Autowired
	private ReplyExplainDao replyExplainDao;
	@Autowired
	private SubDocTrackingDao subDocTrackingDao;
	@Autowired
	private SubDocInfoDao subDocInfoDao;

	@Override
	public DocumentInfo queryObject(String id) {
		return documentInfoDao.queryObject(id);
	}

	@Override
	public List<DocumentInfo> queryList(Map<String, Object> map) {
		return documentInfoDao.queryList(map);
	}

	@Override
	public void save(DocumentInfo documentInfo) {
		documentInfo.setCuibanFlag("0");
		documentInfo.setCreatedTime(new Date());
		documentInfoDao.save(documentInfo);
	}

	@Override
	public void update(DocumentInfo dbDocumentInfo) {
		documentInfoDao.update(dbDocumentInfo);
	}

	@Override
	public void delete(String id) {
		documentInfoDao.delete(id);
	}

	@Override
	public void deleteBatch(String[] ids) {
		documentInfoDao.deleteBatch(ids);
	}

	@Override
	public List<Map<String, Object>> queryListByYear(Map<String, Object> map) {
		return documentInfoDao.queryListByYear(map);
	}

	@Override
	public List<DocumentInfo> queryAllBjList(String year){
		return documentInfoDao.queryAllBjList(year);
	}

	@Override
	public List<Map<String,Object>> queryListByOrgAndYear(Map<String,Object> map){
		return documentInfoDao.queryListByOrgAndYear(map);
	}


	@Override
	public List<Map<String, Object>> queryListByOrgYear(Map<String, Object> map) {
		return documentInfoDao.queryListByOrgYear(map);
	}

	@Override
	public List<Map<String, Object>> queryListByDicType(Map<String, Object> map) {
		return documentInfoDao.queryListByDicType(map);
	}

	@Override
	public List<Map<String, Object>> queryListByDicStu(Map<String, Object> map) {
		return documentInfoDao.queryListByDicStu(map);
	}

	@Override
	public List<DocumentInfo> queryPersonList(Map<String, Object> map) {
		return documentInfoDao.queryPersonList(map);
	}

	@Override
	public List<Map<String, Object>> queryListByDicStutas(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return documentInfoDao.queryListByDicStutas(map);
	}

	@Override
	public void deleteByCheHui(String id) {
		List<SubDocInfo> list = subDocInfoDao.queryAllSubByInfoId(id);
		if (list != null && list.size() > 0) {
			for (SubDocInfo subInfo : list) {
				String subId = subInfo.getId();
				// 删除审批意见
				approvalOpinionDao.deleteBySubId(subId);
				// 删除附件
				replyAttacDao.deleteBySubId(subId);
				// 删除反馈
				Map<String, Object> map = new HashMap<>();
				map.put("subId", subId);
				replyExplainDao.deleteByParam(map);
				// 删除流转记录
				subDocTrackingDao.deleteBySubId(subId);
				// 删除子分支
				subDocInfoDao.delete(subId);
			}
		}
		// 删除办结记录
		documentBjjlDao.deleteByInfoId(id);
		// 删除催办记录
		documentCbjlDao.deleteByInfoId(id);
		// 删除阅读记录
		documentReadDao.deleteByInfoId(id);
		// 删除转办记录
		documentZbjlDao.deleteByInfoId(id);
		// 更新主文件状态
		DocumentInfo info = documentInfoDao.queryObject(id);
		info.setStatus(0);
		info.setCuibanFlag("0");
		info.setFinishTime(null);
		info.setLatestReply("");
		info.setLatestReplyTime(null);
		info.setLatestSubDept("");
		info.setLatestUndertaker("");
		documentInfoDao.update(info);
	}

	@Override
	public List<DocumentDic> queryDicByType(Map<String, Object> map) {
		return documentInfoDao.queryDicByType(map);
	}

	@Override
	public String getMaxSecurity(String[] stringIds) {
		return documentInfoDao.getMaxSecurity(stringIds);
	}

	@Override
	public List<DocumentInfo> queryInfoByParam(Map<String, Object> map) {
		return documentInfoDao.queryInfoByParam(map);
	}

	@Override
	public List<LeaderStatisticsDto> queryLeaderStatistics(Map<String, Object> map) {
		return documentInfoDao.queryLeaderStatistics(map);
	}

	@Override
	public List<DocumentInfo> queryStatisticsList(Map<String, Object> map) {
		return documentInfoDao.queryStatisticsList(map);
	}

	@Override
	public void updateDocumentInfoById(DocumentInfo documentInfo) {
		documentInfoDao.updateDocumentInfoById(documentInfo);
	}

	@Override
	public List<DocumentInfo> queryReplyList(Map<String, Object> map) {
		return documentInfoDao.queryReplyList(map);
	}

	@Override
	public List<DocumentInfo> queryReplyListPlus(Map<String, Object> map) {
		return documentInfoDao.queryReplyListPlus(map);
	}
	
	@Override
	public List<DocumentInfo> queryReplyListForWfk(Map<String, Object> map) {
		return documentInfoDao.queryReplyListForWfk(map);
	}

	@Override
	public int queryDocumentWfk(String danweiid, String year) {
		return documentInfoDao.queryDocumentWfk(danweiid, year);
	}

	@Override
	public int queryDocumentWfk2(String danweiid, String year) {
		return documentInfoDao.queryDocumentWfk2(danweiid, year);
	}

	@Override
	public int queryWfkLeaderStatistics(Map<String, Object> map) {
		return documentInfoDao.queryWfkLeaderStatistics(map);
	}

	@Override
	public List<DocumentInfo> queryNewList(Map<String, Object> map) {
		return documentInfoDao.queryNewList(map);
	}
	
	@Override
	public List<DocumentInfo> queryNewListSort(Map<String, Object> map) {
		return documentInfoDao.queryNewListSort(map);
	}

	@Override
	public int queryChaoShiByYear(Map<String, Object> map) {
		return documentInfoDao.queryChaoShiByYear(map);
	}

	@Override
	public List<Map<String, Object>> queryListByOrgIdAndYear(Map<String,Object> map){
		return documentInfoDao.queryListByOrgIdAndYear(map);
	}
	
	@Override
	public List<Map<String, Object>> queryListByOrgYear1(Map<String, Object> map) {
		return documentInfoDao.queryListByOrgYear1(map);
	}
	
	@Override
	public int queryWfkCount3(Map<String,Object> map){
		return documentInfoDao.queryWfkCount3(map);
	}
}
