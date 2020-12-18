package com.css.app.db.business.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.css.app.db.business.dao.SubDocInfoDao;
import com.css.app.db.business.entity.SubDocInfo;
import com.css.app.db.business.service.SubDocInfoService;

import javax.sound.sampled.Line;


@Service("subDocInfoService")
public class SubDocInfoServiceImpl implements SubDocInfoService {
	@Autowired
	private SubDocInfoDao subDocInfoDao;
	
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
}
