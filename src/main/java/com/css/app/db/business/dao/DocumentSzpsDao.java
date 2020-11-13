package com.css.app.db.business.dao;

import com.css.app.db.business.entity.DocumentSzps;

import org.apache.ibatis.annotations.Mapper;

import com.css.base.dao.BaseDao;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 首长批示内容表
 * 
 * @author 中软信息系统工程有限公司
 * @email 
 * @date 2019-04-19 14:39:12
 */
@Mapper
public interface DocumentSzpsDao extends BaseDao<DocumentSzps> {
	
	void deleteByInfoId(String infoId);
	
	void updateUserNameByUserId(String userName,String userId);

	@Select("select * from DB_DOCUMENT_SZPS where INFO_ID = #{0}")
	List<DocumentSzps> queryByInfo(String infoId);
	
}
