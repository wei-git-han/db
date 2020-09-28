package com.css.app.db.business.controller;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.css.base.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.css.addbase.apporgmapped.service.BaseAppOrgMappedService;
import com.css.app.db.business.entity.DocumentInfo;
import com.css.app.db.business.entity.DocumentSzps;
import com.css.app.db.business.entity.ReplyExplain;
import com.css.app.db.business.entity.SubDocInfo;
import com.css.app.db.business.service.DocumentInfoService;
import com.css.app.db.business.service.DocumentSzpsService;
import com.css.app.db.business.service.ExportService;
import com.css.app.db.business.service.ExportWPSservice;
import com.css.app.db.business.service.ReplyExplainService;
import com.css.app.db.business.service.SubDocInfoService;
import com.css.app.db.business.service.impl.ExportBLDServiceImpl;
import com.css.app.db.business.service.impl.ExportBLDServiceImpl2;
import com.css.app.db.business.service.impl.ExportInvoke;
import com.css.app.db.business.service.impl.ExportJWZYServiceImpl;
import com.css.app.db.business.service.impl.ExportJWZYServiceImpl2;
import com.css.app.db.business.service.impl.ExportWPSserviceImpl;
import com.css.app.db.business.service.impl.ExportZYJCServiceImpl;
import com.css.app.db.business.service.impl.ExportZYJCServiceImpl2;
import com.css.app.db.config.service.DocumentDicService;
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
    BaseAppOrgMappedService baseAppOrgMappedService;
    @Autowired
    ExportInvoke exportInvoke;    
    @Autowired
    DocumentDicService documentDicService;    
	@Value("${uploadFile.path}")
	private String filePath;

	
	@RequestMapping("/exportDocx")
	public void exprotWPS(String stringIds) {
		String[] ids = stringIds.split(",");
		String statusName = "";
		String exportFileName = "";
		File tempFile = null;
		DocumentInfo documentInfo = null;
		int banjieNum = 0;//办结数量
		int weibanjieNum = 0;//未办结数量
		List<Map<String, String>> exportDataLis = new ArrayList<Map<String, String>>();	
		String security=documentInfoService.getMaxSecurity(ids);
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
				weibanjieNum++;
				break;
			case 2:
				statusName = "办结";
				banjieNum++;
				break;
			case 3:
				statusName = "常态落实";
				banjieNum++;
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
//				String newForMatCreatedTime = "xxxx年xx月xx日";
//				String createdTime = docSzps.getCreatedTime();
//				if(createdTime !=null) {
//					String[] split = createdTime.split("-");
//					if(split.length<2) {
//						System.out.println("首长批示的创建时间（createdTime）:"+createdTime+"格式有误！");
//						throw new RuntimeException("首长批示的创建时间（createdTime）:"+createdTime+"格式有误！");
//					}else {
//						newForMatCreatedTime=split[0]+"年"+split[1]+"月"+split[2]+"日";
//					}					
//				}				
				commentBuilder.append(
						docSzps.getUserName() + docSzps.getCreatedTime() + "批示：" + docSzps.getLeaderComment()
								+ "                                                                        ");
			}
			for (ReplyExplain reply : latestOneReply) {
				replyBuilder.append("	"+reply.getReplyContent()
								+ "                                                                        ");
			}
			for (SubDocInfo subInfo : subByInfoId) {
				String telephone = subInfo.getUndertakerPhone() == null ? "" :subInfo.getUndertakerPhone();
				String deptName = subInfo.getSubDeptName() == null ? "" : subInfo.getSubDeptName();
				String subInfoName = subInfo.getUndertakerName() == null ? "" : subInfo.getUndertakerName();				
				// 查询承办单位/人电话情况
//				BaseAppOrgMapped orgMapped = (BaseAppOrgMapped) baseAppOrgMappedService.orgMapped("", "",AppType.APP_TXL);
//				if (orgMapped != null) {
//					LinkedMultiValueMap<String, Object> paraMap = new LinkedMultiValueMap<String, Object>();
//					paraMap.add("id", subInfo.getUndertaker());
//					String url = "http://172.16.3.13:64001/txluser/getUser";
//					JSONObject jsonData = CrossDomainUtil.getJsonData(url, paraMap);
//					if (jsonData != null && jsonData.get("txlOrgtel") != null) {
//						Map<String, Object> txlOrgtel = (Map<String, Object>) jsonData.get("txlOrgtel");
//						telephone = txlOrgtel.get("telephone").toString();
//					}
//				}				
				subInfoBuilder.append(deptName + "   "
						+ "                                               " + subInfoName + "                                                  " + telephone);
			}											
			exportDataMap.put("banjianNumber", documentInfo.getBanjianNumber()==null?"":documentInfo.getBanjianNumber());// 军 委办件号：
			exportDataMap.put("docCode", documentInfo.getDocCode()==null?"":documentInfo.getDocCode());//文件号：
			exportDataMap.put("docTitle", documentInfo.getDocTitle().replace("<","&lt;").replace(">","&gt;"));// 文件标题
			exportDataMap.put("printDate", documentInfo.getPrintDate()==null?"":documentInfo.getPrintDate());// 印发时间
			exportDataMap.put("jobContent", documentInfo.getJobContent()==null?"":documentInfo.getJobContent());// 工作分工内容
			exportDataMap.put("status", statusName);// 办理状态 (0:还未转办1：办理中；2：办结：3：常态落实）
			exportDataMap.put("leaderComment", StringUtils.isNotBlank(commentBuilder.toString().replace("\tnull","")) ? commentBuilder.toString().replace("<","&lt;").replace(">","&gt;") : "");// 批示指示内容
			exportDataMap.put("replyComment",  StringUtils.isNotBlank(replyBuilder.toString().replace("\tnull","")) ? replyBuilder.toString().replace("<","&lt;").replace(">","&gt;") : "");// 督办落实情况
			exportDataMap.put("subInfoComment",  StringUtils.isNotBlank(subInfoBuilder.toString().replace("\tnull","")) ? subInfoBuilder.toString().replace("<","&lt;").replace(">","&gt;") : "");// 承办单位人员
			exportDataMap.put("period", documentInfo.getPeriod());// 期数
			exportDataMap.put("security",security);//秘籍
			exportDataLis.add(exportDataMap);
		}

		InputStream is = null; 
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			String docTypeId = documentInfo.getDocTypeId();
			switch (docTypeId) {
			case "1":
				exportFileName = "军委主席批示指示督办落实情况表.doc";
				tempFile = creatFile(exportFileName);
				ExportService exportJWZYServiceImpl = new ExportJWZYServiceImpl2();
				ExportWPSservice exportWPSserviceJWZY = new ExportWPSserviceImpl(exportJWZYServiceImpl, exportDataLis,
						tempFile.getAbsolutePath(),banjieNum,weibanjieNum,docTypeId);
				exportInvoke.setExportWPSservice(exportWPSserviceJWZY);												
				is = exportInvoke.export();
				break;
			case "2":
				exportFileName = "军委首长批示指示督办落实情况表.doc";
				tempFile = creatFile(exportFileName);
				ExportService exportJWSZServiceImpl = new ExportJWZYServiceImpl2();
				ExportWPSservice exportWPSserviceJWSZ = new ExportWPSserviceImpl(exportJWSZServiceImpl, exportDataLis,
						tempFile.getAbsolutePath(),banjieNum,weibanjieNum,docTypeId);
				exportInvoke.setExportWPSservice(exportWPSserviceJWSZ);
				is = exportInvoke.export();
				break;
			case "3":
				exportFileName = "党中央、中央军委、国务院重要决策部署分工落实情况表.doc";
				tempFile = creatFile(exportFileName);
				ExportService exportZYJCServiceImpl = new ExportZYJCServiceImpl2();
				ExportWPSservice exportWPSserviceZYJC = new ExportWPSserviceImpl(exportZYJCServiceImpl, exportDataLis,
						tempFile.getAbsolutePath(),banjieNum,weibanjieNum,docTypeId);
				exportInvoke.setExportWPSservice(exportWPSserviceZYJC);
				is = exportInvoke.export();
				break;
			case "4":
				exportFileName = "装备发展部领导批示指示督办落实情况表.doc";
				tempFile = creatFile(exportFileName);
				ExportService exportBLDServiceImpl = new ExportBLDServiceImpl2();
				ExportWPSservice exportWPSserviceBLD = new ExportWPSserviceImpl(exportBLDServiceImpl, exportDataLis,
						tempFile.getAbsolutePath(),banjieNum,weibanjieNum,docTypeId);
				exportInvoke.setExportWPSservice(exportWPSserviceBLD);
				is = exportInvoke.export();
				break;
			case "5":
				exportFileName = "装备发展部重要工作分工落实情况表.doc";
				tempFile = creatFile(exportFileName);
				ExportService exportBNZYGZServiceImpl = new ExportZYJCServiceImpl2();
				ExportWPSservice exportWPSserviceBNZYGZ = new ExportWPSserviceImpl(exportBNZYGZServiceImpl,
						exportDataLis, tempFile.getAbsolutePath(),banjieNum,weibanjieNum,docTypeId);
				exportInvoke.setExportWPSservice(exportWPSserviceBNZYGZ);
				is = exportInvoke.export();
				break;
			case "6":
				exportFileName = "其他重要工作落实情况表.doc";
				tempFile = creatFile(exportFileName);
				ExportService exportQTServiceImpl = new ExportZYJCServiceImpl2();
				ExportWPSservice exportWPSserviceQT = new ExportWPSserviceImpl(exportQTServiceImpl, exportDataLis,
						tempFile.getAbsolutePath(),banjieNum,weibanjieNum,docTypeId);
				exportInvoke.setExportWPSservice(exportWPSserviceQT);
				is = exportInvoke.export();
				break;
			}
			resultMap.put("fileUrl", tempFile.getAbsoluteFile());
			resultMap.put("fileName", tempFile.getName());
			resultMap.put("result", "success");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response.download(exportFileName, is);

	}

   /**
    * 创建file
    * @param exportFileName
    * @return
    */
	private File creatFile(String exportFileName) {
		File tempFile;
		tempFile = new File(filePath, exportFileName);
		if (tempFile.exists()) {
			tempFile.delete();
		} else {
			tempFile.getParentFile().mkdirs();
		}
		return tempFile;
	}

}
