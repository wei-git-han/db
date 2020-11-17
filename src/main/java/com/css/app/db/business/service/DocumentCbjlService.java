package com.css.app.db.business.service;

import com.css.app.db.business.entity.DocumentCbjl;

import java.util.List;
import java.util.Map;

/**
 * 催办记录表
 * 
 * @author 中软信息系统工程有限公司
 * @email 
 * @date 2019-04-28 16:43:15
 */
public interface DocumentCbjlService {
	
	DocumentCbjl queryObject(String id);
	
	List<DocumentCbjl> queryList(Map<String, Object> map);
	
	void save(DocumentCbjl dbDocumentCbjl);
	
	void update(DocumentCbjl dbDocumentCbjl);
	
	void delete(String id);
	
	void deleteBatch(String[] ids);
	
	DocumentCbjl queryLatestCuiBan(String infoId);
	
	void deleteByInfoId(String infoId);

	DocumentCbjl queryByInfoId(String infoId,String type);
}
