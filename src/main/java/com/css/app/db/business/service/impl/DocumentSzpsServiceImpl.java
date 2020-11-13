package com.css.app.db.business.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.css.app.db.business.dao.DocumentSzpsDao;
import com.css.app.db.business.entity.DocumentSzps;
import com.css.app.db.business.service.DocumentSzpsService;



@Service("documentSzpsService")
public class DocumentSzpsServiceImpl implements DocumentSzpsService {
	@Autowired
	private DocumentSzpsDao documentSzpsDao;
	
	@Override
	public DocumentSzps queryObject(String id){
		return documentSzpsDao.queryObject(id);
	}
	
	@Override
	public List<DocumentSzps> queryList(Map<String, Object> map){
		return documentSzpsDao.queryList(map);
	}
	
	@Override
	public void save(DocumentSzps dbDocumentSzps){
		documentSzpsDao.save(dbDocumentSzps);
	}
	
	@Override
	public void update(DocumentSzps dbDocumentSzps){
		documentSzpsDao.update(dbDocumentSzps);
	}
	
	@Override
	public void delete(String id){
		documentSzpsDao.delete(id);
	}
	
	@Override
	public void deleteBatch(String[] ids){
		documentSzpsDao.deleteBatch(ids);
	}

	@Override
	public void deleteByInfoId(String infoId) {
		documentSzpsDao.deleteByInfoId(infoId);
		
	}

	@Override
	public void updateUserNameByUserId(String userName, String userId) {
		documentSzpsDao.updateUserNameByUserId(userName, userId);
	}

	@Override
	public List<DocumentSzps> queryByInfo(String infoId){
		return  documentSzpsDao.queryByInfo(infoId);
	}
	
}
