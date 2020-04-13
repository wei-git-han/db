package com.css.app.db.business.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.css.base.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.css.app.db.business.entity.DocumentSzps;
import com.css.app.db.business.service.DocumentSzpsService;
import com.css.base.utils.Response;
import com.css.base.utils.StringUtils;
import com.css.base.utils.UUIDUtils;


/**
 * 首长批示内容表
 * 
 * @author 中软信息系统工程有限公司
 * @email 
 * @date 2019-04-19 14:39:12
 */
@Controller
@RequestMapping("/app/db/documentszps")
public class DocumentSzpsController {
	@Autowired
	private DocumentSzpsService documentSzpsService;
	
	/**
	 * 列表
	 */
	@ResponseBody
	@RequestMapping("/queryList")
	public void list(String infoId){
		List<DocumentSzps> documentSzpsList = null;
		if(StringUtils.isNotBlank(infoId)) {
			Map<String, Object> map = new HashMap<>();
			map.put("infoId", infoId);
			documentSzpsList = documentSzpsService.queryList(map);
		}
		Response.json(documentSzpsList);
	}
	
	/**
	 * 保存
	 */
	@ResponseBody
	@RequestMapping("/save")
	public void save(DocumentSzps documentSzps){
		JSONObject json=new JSONObject();
		if(StringUtils.isBlank(documentSzps.getId())) {
			documentSzps.setId(UUIDUtils.random());
			documentSzpsService.save(documentSzps);
		}else {
			documentSzpsService.update(documentSzps);
		}
		json.put("result", "success");
		Response.json(json);
	}

	/**
	 * 针对批量导入新的保存接口
	 * 新的保存
	 *
	 */
	@ResponseBody
	@RequestMapping("/newSave")
	public void saveInfo(String infos) {
		JSONObject jsonObject = new JSONObject();
		if (StringUtils.isNotBlank(infos)) {
			String[] info = infos.split(",");
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					String[] infomention = info[i].split("_");
					if (infomention != null) {
						//如果用户选了多个首长，只给一个首长填了批示，那只保存填了批示的首长，也就是通过批示内容那个字段是否有值来判断。
						if (StringUtils.isNotBlank(infomention[3])) {
							DocumentSzps documentSzps = new DocumentSzps();
							documentSzps.setUserId(infomention[1]);
							documentSzps.setUserName(infomention[2]);
							documentSzps.setLeaderComment(infomention[3]);
							documentSzps.setCreatedTime(infomention[4]+"01日");
							documentSzps.setInfoId(infomention[5]);
							if (StringUtils.isBlank(infomention[0])) {
								documentSzps.setId(UUIDUtils.random());
								documentSzpsService.save(documentSzps);
							} else {
								documentSzps.setId(infomention[0]);
								documentSzpsService.update(documentSzps);
							}
						}
					}
				}
			}
		}
		jsonObject.put("result", "success");
		Response.json(jsonObject);
	}


	/**
	 * 删除
	 */
	@ResponseBody
	@RequestMapping("/delete")
	public void delete(String id){
		documentSzpsService.delete(id);
		Response.json("result", "success");
	}
	
}
