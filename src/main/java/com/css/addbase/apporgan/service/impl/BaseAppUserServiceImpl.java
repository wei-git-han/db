package com.css.addbase.apporgan.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.css.addbase.apporgan.dao.BaseAppOrganDao;
import com.css.addbase.apporgan.dao.BaseAppUserDao;
import com.css.addbase.apporgan.entity.BaseAppOrgan;
import com.css.addbase.apporgan.entity.BaseAppUser;
import com.css.addbase.apporgan.service.BaseAppUserService;
@Service("baseAppUserService")
public class BaseAppUserServiceImpl implements BaseAppUserService {
	@Autowired
	private BaseAppUserDao baseAppUserDao;
	@Autowired
	private BaseAppOrganDao baseAppOrganDao;
	
	@Override
	public int queryTotal(Map<String, Object> map) {
		return baseAppUserDao.queryTotal(map);
	}
	
	@Override
	public BaseAppUser queryObject(String id){
		return baseAppUserDao.queryObject(id);
	}
	
	@Override
	public List<BaseAppUser> queryList(Map<String, Object> map){
		return baseAppUserDao.queryList(map);
	}
	
	@Override
	public void save(BaseAppUser baseAppUser){
		baseAppUserDao.save(baseAppUser);
	}
	
	@Override
	public void update(BaseAppUser baseAppUser){
		baseAppUserDao.update(baseAppUser);
	}
	
	@Override
	public void delete(String id){
		baseAppUserDao.delete(id);
	}
	
	@Override
	public void deleteBatch(String[] ids){
		baseAppUserDao.deleteBatch(ids);
	}

	@Override
	public List<BaseAppUser> findByUserId(String userId) {
		return baseAppUserDao.findByUserId(userId);
	}

	@Override
	public List<BaseAppUser> findByOrganid(String organid) {
		return baseAppUserDao.findByOrganid(organid);
	}

	@Override
	public List<BaseAppUser> queryObjectByUserIds(String[] userIds) {
		return baseAppUserDao.queryObjectByUserIds(userIds);
	}

	@Override
	public List<BaseAppUser> queryObjectByDeptIds(String[] deptIds) {
		return baseAppUserDao.queryObjectByDeptIds(deptIds);
	}

	@Override
	public void clearUser() {
		baseAppUserDao.clearUser();
	}

	@Override
	public List<BaseAppUser> deleteByUserId(String userId) {
		return baseAppUserDao.deleteByUserId(userId);
	}

	@Override
	public List<BaseAppUser> findByDepartmentId(String organid) {
		return baseAppUserDao.findByDepartmentId(organid);
	}
	
	@Override
	public String getBareauByUserId(String userId){
		if(StringUtils.isNotBlank(userId)){
			BaseAppUser user = baseAppUserDao.queryObject(userId);
			if(user != null){
				BaseAppOrgan org = baseAppOrganDao.queryObject(user.getOrganid());
				if(org != null){
					String[] pathArr = org.getTreePath().split(",");
					if(pathArr.length > 2){
						return pathArr[2];
					}
				}
			}
		}
		
		return "";
	}
	
	@Override
	public boolean queryCountUser(String deptId) {
		int count = baseAppUserDao.queryCountUser(deptId);
		if (count > 0) {
			return true;
		}
		return false;
	}

	@Override
	public List<BaseAppUser> queryListBySet(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return baseAppUserDao.queryListBySet(map);
	}

	@Override
	public List<BaseAppUser> queryUserByName(String name) {
		return baseAppUserDao.queryUserByName(name);
	}

	@Override
	public List<BaseAppUser> selectUserByNameAndUnitId(String name, String unitId) {
		return baseAppUserDao.selectUserByNameAndUnitId(name, unitId);
	}
	
	@Override
	public List<BaseAppUser> queryAllTaskByDept(String deptId){
		return baseAppUserDao.queryAllTaskByDept(deptId);
	}
	
}
