package com.css.app.db.business.controller;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.css.addbase.apporgmapped.constant.AppType;
import com.css.addbase.apporgmapped.entity.BaseAppOrgMapped;
import com.css.addbase.apporgmapped.service.BaseAppOrgMappedService;
import com.css.app.db.business.entity.DocumentInfo;
import com.css.app.db.business.entity.DocumentSzps;
import com.css.app.db.business.entity.ReplyExplain;
import com.css.app.db.business.entity.SubDocInfo;
import com.css.app.db.business.service.DocumentInfoService;
import com.css.app.db.business.service.DocumentSzpsService;
import com.css.app.db.business.service.ExportService;
import com.css.app.db.business.service.ReplyExplainService;
import com.css.app.db.business.service.SubDocInfoService;
import com.css.base.entity.SSOUser;
import com.css.base.utils.CrossDomainUtil;
import com.css.base.utils.CurrentUser;
import com.css.base.utils.Response;

/**
 * 导出wps
 * @author weizy
 *
 */
@RestController
@RequestMapping("/app/db/export")
public class ExportController{
    @Autowired
	DocumentInfoService documentInfoService;
    @Autowired
    DocumentSzpsService documentSzpsService;
    @Autowired
    ReplyExplainService replyExplainService;
    @Autowired
    SubDocInfoService subDocInfoService;
    @Autowired
    ExportService exportService;
    @Autowired
    BaseAppOrgMappedService baseAppOrgMappedService;
    @Value("filePath")
	String filePath;

	
	@RequestMapping("/exportDocx")
	public void exprotWPS(String stringIds) {
		String[] ids = stringIds.split(",");
		String statusName = "";
		String exportFileName = "";
		DocumentInfo documentInfo = null;
		List<Map<String, String>> exportDataLis = new ArrayList<Map<String, String>>();
		for (String id : ids) {
			StringBuilder commentBuilder = new StringBuilder();
			StringBuilder replyBuilder = new StringBuilder();
			StringBuilder subInfoBuilder = new StringBuilder();
			Map<String, String> exportDataMap = new HashMap<String, String>();
			// 军 委办件号 文件标题 办理状态
			documentInfo = documentInfoService.queryObject(id);
			switch (documentInfo.getStatus()) {
			case 1:
				statusName = "办理中";
				break;
			case 2:
				statusName = "办结";
				break;
			case 3:
				statusName = "常态落实";
				break;
			}

			Map<String, Object> map = new HashMap<>();
			map.put("infoId", id);
			// 批示指示内容
			List<DocumentSzps> documentSzpsList = documentSzpsService.queryList(map);

			// 督办落实情况
			List<ReplyExplain> latestOneReply = replyExplainService.queryAllLatestOneReply(id);
			// 承办单位人员
			List<SubDocInfo> subByInfoId = subDocInfoService.queryAllSubByInfoId(id);
			for (DocumentSzps docSzps : documentSzpsList) {
				commentBuilder.append(
						docSzps.getUserName() + " 于" + docSzps.getCreatedTime() + "发表批示内容：" + docSzps.getLeaderComment()
								+ "                                                                        ");
			}
			for (ReplyExplain reply : latestOneReply) {
				replyBuilder.append(
						reply.getUserName() + " 于" + new SimpleDateFormat("yyyy-MM-dd").format(reply.getCreatedTime())
								+ "发表办理反馈内容：" + reply.getReplyContent()
								+ "                                                                        ");
			}
			for (SubDocInfo subInfo : subByInfoId) {
				String telephone ="";
				String deptName=subInfo.getSubDeptName()== null ? "":subInfo.getSubDeptName();
				String subInfoName=subInfo.getUndertakerName()== null ? "":subInfo.getUndertakerName();
				BaseAppOrgMapped orgMapped = (BaseAppOrgMapped) baseAppOrgMappedService.orgMapped("", "", AppType.APP_TXL);
				if (orgMapped != null) {
					LinkedMultiValueMap<String, Object> paraMap = new LinkedMultiValueMap<String, Object>();
					paraMap.add("id", subInfo.getUndertaker());
					String url = orgMapped.getUrl() + "/txluser/getUser";
					JSONObject jsonData = CrossDomainUtil.getJsonData(url, paraMap);
					if (jsonData != null && jsonData.get("txlOrgtel") != null) {
						Map<String, Object> txlOrgtel = (Map<String, Object>) jsonData.get("txlOrgtel");
						telephone = txlOrgtel.get("telephone").toString();
					}
				}
				subInfoBuilder.append(deptName+ "        " + subInfoName+ "         "+telephone);
			}
			exportDataMap.put("banjianNumber", documentInfo.getBanjianNumber());// 军 委办件号：
			exportDataMap.put("docTitle", documentInfo.getDocTitle());// 文件标题
			exportDataMap.put("printDate", documentInfo.getPrintDate());// 文件标题
			exportDataMap.put("jobContent", documentInfo.getJobContent());// 工作分工内容
			exportDataMap.put("status", statusName);// 办理状态 (0:还未转办1：办理中；2：办结：3：常态落实）
			exportDataMap.put("leaderComment", commentBuilder.toString());// 批示指示内容
			exportDataMap.put("replyComment", replyBuilder.toString());// 督办落实情况
			exportDataMap.put("subInfoComment", subInfoBuilder.toString());// 承办单位人员
			exportDataLis.add(exportDataMap);
		}
		String docTypeId = documentInfo.getDocTypeId();
		switch (docTypeId) {
		case "1":
			exportFileName="军委主席批示指示督办落实情况表.docx";
			break;
		case "2":
			exportFileName="军委首长批示指示督办落实情况表.docx";
			break;
		case "3":
			exportFileName="党中央、中央军委、国务院重要决策部署分工落实情况表.docx";
			break;
		case "4":
			exportFileName="装备发展部领导批示指示督办落实情况表.docx";
			break;
		case "5":
			exportFileName="装备发展部重要工作分工落实情况表.docx";
			break;
		case "6":
			exportFileName="其他重要工作落实情况表.docx";
			break;
		}
		
		// 本地文件路径（应用中下载操作的默认文件保存路径）
		InputStream is = null;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		File tempFile = new File(filePath, exportFileName);
		if (tempFile.exists()) {
			tempFile.delete();
		} else {
			tempFile.getParentFile().mkdirs();
		}
		try {
			is = exportService.exportWPSdoc(exportDataLis, tempFile.getAbsolutePath(), documentInfo.getDocTypeId());
			resultMap.put("fileUrl", tempFile.getAbsoluteFile());
			resultMap.put("fileName", tempFile.getName());
			resultMap.put("result", "success");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Response.download(exportFileName, is);

	}

}
