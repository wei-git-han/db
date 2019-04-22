package com.css.app.db.business.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;

import com.css.base.utils.PageUtils;
import com.css.base.utils.UUIDUtils;
import com.github.pagehelper.PageHelper;
import com.css.base.utils.Response;
import com.css.app.db.business.entity.DocumentRead;
import com.css.app.db.business.service.DocumentReadService;


/**
 * 确认已读表
 * 
 * @author 中软信息系统工程有限公司
 * @email 
 * @date 2019-04-18 16:39:53
 */
@Controller
@RequestMapping("/documentread")
public class DocumentReadController {
	@Autowired
	private DocumentReadService documentReadService;
	
	/**
	 * 列表
	 */
	@ResponseBody
	@RequestMapping("/list")
	@RequiresPermissions("dbdocumentread:list")
	public void list(Integer page, Integer limit){
		Map<String, Object> map = new HashMap<>();
		PageHelper.startPage(page, limit);
		
		//查询列表数据
		List<DocumentRead> dbDocumentReadList = documentReadService.queryList(map);
		
		PageUtils pageUtil = new PageUtils(dbDocumentReadList);
		Response.json("page",pageUtil);
	}
	
	
	/**
	 * 信息
	 */
	@ResponseBody
	@RequestMapping("/info/{id}")
	@RequiresPermissions("dbdocumentread:info")
	public void info(@PathVariable("id") String id){
		DocumentRead dbDocumentRead = documentReadService.queryObject(id);
		Response.json("dbDocumentRead", dbDocumentRead);
	}
	
	/**
	 * 保存
	 */
	@ResponseBody
	@RequestMapping("/save")
	@RequiresPermissions("dbdocumentread:save")
	public void save(@RequestBody DocumentRead dbDocumentRead){
		dbDocumentRead.setId(UUIDUtils.random());
		documentReadService.save(dbDocumentRead);
		
		Response.ok();
	}
	
	/**
	 * 修改
	 */
	@ResponseBody
	@RequestMapping("/update")
	@RequiresPermissions("dbdocumentread:update")
	public void update(@RequestBody DocumentRead dbDocumentRead){
		documentReadService.update(dbDocumentRead);
		
		Response.ok();
	}
	
	/**
	 * 删除
	 */
	@ResponseBody
	@RequestMapping("/delete")
	@RequiresPermissions("dbdocumentread:delete")
	public void delete(@RequestBody String[] ids){
		documentReadService.deleteBatch(ids);
		
		Response.ok();
	}
	
}
