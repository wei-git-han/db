package com.css.app.db.business.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.css.app.db.business.dao.ReplyExplainDao;
import com.css.app.db.business.entity.ReplyExplain;
import com.css.app.db.business.service.ReplyExplainService;



@Service("replyExplainService")
public class ReplyExplainServiceImpl implements ReplyExplainService {
	@Autowired
	private ReplyExplainDao replyExplainDao;
	
	@Override
	public ReplyExplain queryObject(String id){
		return replyExplainDao.queryObject(id);
	}
	
	@Override
	public List<ReplyExplain> queryList(Map<String, Object> map){
		return replyExplainDao.queryList(map);
	}
	
	@Override
	public void save(ReplyExplain dbReplyExplain){
		replyExplainDao.save(dbReplyExplain);
	}
	
	@Override
	public void update(ReplyExplain dbReplyExplain){
		replyExplainDao.update(dbReplyExplain);
	}
	
	@Override
	public void delete(String id){
		replyExplainDao.delete(id);
	}
	
	@Override
	public void deleteBatch(String[] ids){
		replyExplainDao.deleteBatch(ids);
	}

	@Override
	public ReplyExplain queryLastestTempReplyByTeamId(String subId, String teamId, String userId) {
		return replyExplainDao.queryLastestTempReplyByTeamId(subId, teamId, userId);
	}
	
}
