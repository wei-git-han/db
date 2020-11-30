package com.css.app.db.business.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.css.addbase.apporgmapped.entity.BaseAppOrgMapped;
import com.css.addbase.apporgmapped.service.BaseAppOrgMappedService;
import com.css.addbase.constant.AppConstant;
import com.css.app.db.business.entity.DocumentFile;
import com.css.app.db.business.service.DocumentFileService;
import com.css.app.db.business.service.DocumentInfoService;
import com.css.base.utils.CrossDomainUtil;
import com.css.base.utils.Response;
import com.css.base.utils.UUIDUtils;
import com.github.pagehelper.PageHelper;

@RestController
@RequestMapping("/app/db/document")
public class PersonDocumentController {

	@Autowired
	private BaseAppOrgMappedService baseAppOrgMappedService;
	@Autowired
	private DocumentInfoService documentInfoService;
	@Autowired
	private DocumentFileService documentFileService;

	/**
	 * 列表 呈报列表
	 * 
	 * @param page，第几页
	 * @param pagesize，每页条数
	 */
	@ResponseBody
	@RequestMapping("/personList")
	public void personList(Integer page, Integer pagesize, String search, String orderField, String orderSeq, String status) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderField", orderField);
		map.put("orderSeq", orderSeq);
		map.put("status", status);

		map.put("search", search);

		PageHelper.startPage(page, pagesize);
		BaseAppOrgMapped mapped = (BaseAppOrgMapped) baseAppOrgMappedService.orgMappedByOrgId("", "",
				AppConstant.APP_GWZB);
		if (mapped == null) {
			return;
		}
		LinkedMultiValueMap<String, Object> infoMap = new LinkedMultiValueMap<String, Object>();
		String url = mapped.getUrl() + "/app/zhms/gwzb/dbList";
		infoMap.add("page", String.valueOf(page));
		infoMap.add("pagesize", String.valueOf(pagesize));
		infoMap.add("search", search);
		infoMap.add("orderField", orderField);
		infoMap.add("orderSeq", orderSeq);
		infoMap.add("status", status);
		JSONObject retInfo = CrossDomainUtil.getJsonData(url, infoMap);
		Response.json(retInfo);
	}
	
	/**
	 * 列表 呈报列表
	 * 
	 * @param page，第几页
	 * @param pagesize，每页条数
	 */
	@ResponseBody
	@RequestMapping("/saveFile")
	public void saveFile(String[] fileIds) {
		JSONObject result=new JSONObject();
		DocumentFile file = null;
		for(String oldId : fileIds){
			if (StringUtils.isNotEmpty(oldId)) {
				
				file = documentFileService.queryObject(oldId);
				file.setId(UUIDUtils.random());
				documentFileService.save(file);
			}
		}
		result.put("id", file.getId());
		result.put("result", "success");
		
		Response.json(result);
	}

}
