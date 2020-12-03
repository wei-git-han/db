package com.css.app.db.business.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.css.addbase.apporgmapped.entity.BaseAppOrgMapped;
import com.css.addbase.apporgmapped.service.BaseAppOrgMappedService;
import com.css.addbase.constant.AppConstant;
import com.css.app.db.business.service.DocumentFileService;
import com.css.base.utils.CrossDomainUtil;
import com.css.base.utils.Response;
import com.github.pagehelper.PageHelper;

@RestController
@RequestMapping("/app/db/document")
public class PersonDocumentController {

	@Autowired
	private BaseAppOrgMappedService baseAppOrgMappedService;
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
	public void personList(Integer page,Integer rows,  String search, String orderField, String orderSeq, String status) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderField", orderField);
		map.put("orderSeq", orderSeq);
		map.put("status", status);

		map.put("search", search);

		PageHelper.startPage(page, rows);
		BaseAppOrgMapped mapped = (BaseAppOrgMapped) baseAppOrgMappedService.orgMappedByOrgId("", "root",
				AppConstant.APP_GWZB);
		if (mapped == null) {
			return;
		}
		LinkedMultiValueMap<String, Object> infoMap = new LinkedMultiValueMap<String, Object>();
		String url = mapped.getUrl() + "/app/zhms/gwzb/dbList";
		infoMap.add("page", String.valueOf(page));
		infoMap.add("pagesize", String.valueOf(rows));
		infoMap.add("search", search);
		infoMap.add("orderField", orderField);
		infoMap.add("orderSeq", orderSeq);
		infoMap.add("status", status);
		JSONObject retInfo = CrossDomainUtil.getJsonData(url, infoMap);
		Response.json(retInfo);
	}
	
	/**
	 * 保存文件
	 * 
	 */
	@ResponseBody
	@RequestMapping("/saveFile")
	public void saveFile(String fileIds,String idpdf) {
		LinkedMultiValueMap<String, Object> infoMap = new LinkedMultiValueMap<String, Object>();
		BaseAppOrgMapped mapped = (BaseAppOrgMapped) baseAppOrgMappedService.orgMappedByOrgId("", "root",
				AppConstant.APP_GWZB);
		String url = mapped.getUrl() + "/app/zhms/gwzb/saveFile";
		infoMap.add("fileIds", fileIds);
		infoMap.add("idpdf", idpdf);
		infoMap.add("sort", String.valueOf(documentFileService.queryMinSort(idpdf)));
		JSONObject retInfo = CrossDomainUtil.getJsonData(url, infoMap);
		if(retInfo!=null) {
			Response.json(retInfo);
		}else {
			Response.json("result", "fail");
		}
	}

}
