package com.css.app.db.business.dao;

import com.css.app.db.business.entity.DocumentCbjl;

import org.apache.ibatis.annotations.Mapper;

import com.css.base.dao.BaseDao;

/**
 * 催办记录表
 * 
 * @author 中软信息系统工程有限公司
 * @email 
 * @date 2019-04-28 16:43:15
 */
@Mapper
public interface DocumentCbjlDao extends BaseDao<DocumentCbjl> {
	
	DocumentCbjl queryLatestCuiBan(String infoId);
	
	void deleteByInfoId(String infoId);
	
}
