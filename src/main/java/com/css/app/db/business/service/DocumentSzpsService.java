package com.css.app.db.business.service;

import com.css.app.db.business.entity.DocumentSzps;

import java.util.List;
import java.util.Map;

/**
 * 首长批示内容表
 * 
 * @author 中软信息系统工程有限公司
 * @email 
 * @date 2019-04-19 14:39:12
 */
public interface DocumentSzpsService {
	
	DocumentSzps queryObject(String id);
	
	List<DocumentSzps> queryList(Map<String, Object> map);
	
	void save(DocumentSzps dbDocumentSzps);
	
	void update(DocumentSzps dbDocumentSzps);
	
	void delete(String id);
	
	void deleteBatch(String[] ids);
	
	void deleteByInfoId(String infoId);
	
	void updateUserNameByUserId(String userName,String userId);

	List<DocumentSzps> queryByInfo(String infoId);
}
