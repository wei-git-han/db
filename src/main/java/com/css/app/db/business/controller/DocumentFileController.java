package com.css.app.db.business.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.css.app.db.business.entity.DocumentFile;
import com.css.app.db.business.entity.DocumentInfo;
import com.css.app.db.business.service.DocumentFileService;
import com.css.app.db.business.service.DocumentInfoService;
import com.css.base.utils.Response;
import com.css.base.utils.StringUtils;

import cn.com.css.filestore.impl.HTTPFile;


/**
 * 基本信息表携带文件的关系表
 * 
 * @author 中软信息系统工程有限公司
 * @email 
 * @date 2019-04-18 16:39:11
 */
@Controller
@RequestMapping("/app/db/documentfile")
public class DocumentFileController {
	private final Logger logger = LoggerFactory.getLogger(DocumentFileController.class);

	@Autowired
	private DocumentFileService documentFileService;
	@Autowired
	private HttpServletResponse httpServletResponse;
	@Autowired
	private DocumentInfoService documentInfoService;
	
	/**
	 * 列表
	 */
	@ResponseBody
	@RequestMapping("/list")
	public void list(String infoId){
		List<DocumentFile> fileList=null;
		if(StringUtils.isNotBlank(infoId)) {
			Map<String, Object> map = new HashMap<>();
			map.put("infoId", infoId);
			fileList = documentFileService.queryList(map);
		}
		Response.json(fileList);
	}
	
	/**
	 * 信息
	 */
	@ResponseBody
	@RequestMapping("/getFile")
	public void info(String id){
		System.err.println(id);
		DocumentFile documentFile = documentFileService.queryObject(id);
		JSONObject json= new JSONObject();
		if(documentFile!=null) {
			String formatId=documentFile.getFileServerFormatId();
			if(StringUtils.isNotBlank(formatId)){
				//获取版式文件的下载路径
				HTTPFile httpFiles = new HTTPFile(formatId);
				if(httpFiles!=null) {
					json.put("formatId", formatId);
					json.put("downFormatIdUrl", httpFiles.getAssginDownloadURL(true));
				}
			}
		}
		Response.json(json);
	}
	
	
	/**
	 * 删除
	 */
	@ResponseBody
	@RequestMapping("/delete")
	public void delete(String ids){
		if(StringUtils.isNotBlank(ids)) {
			String[] idArray = ids.split(",");
			documentFileService.deleteBatch(idArray);
			Response.json("result","success");
		}
	}
	/**
	 * 下载公文、暂时支持单个下载；多文件下载自动打成zip
	 * @param ids 文件主键ids
	 * @param infoId 办件主键
	 */
	@ResponseBody
	@RequestMapping("/downLoadFile")
	public void downLoadFile(String ids, String infoId){
		List<HTTPFile> httpFiles = new ArrayList<>();
		HTTPFile httpFile = null;
		if(StringUtils.isNotBlank(infoId)) {
			DocumentInfo docInfo = documentInfoService.queryObject(infoId);
			String[] idArray = ids.split(",");
			if(idArray!=null && idArray.length==1) {
				DocumentFile documentFile = documentFileService.queryObject(ids);
				if(documentFile!=null) {
					String formatId=documentFile.getFileServerFormatId();
					if(StringUtils.isNotBlank(formatId)){
						HTTPFile hf = new HTTPFile(formatId);
						Response.json(hf.getAssginDownloadURL());
					}
				}
			}else {
				try {
					for (String id : idArray) {
						try {
							DocumentFile documentFile = documentFileService.queryObject(id);
							if(documentFile!=null) {
								String formatId=documentFile.getFileServerFormatId();
								if(StringUtils.isNotBlank(formatId)){
									//获取版式文件的下载路径
									httpFile = new HTTPFile(formatId);
									httpFiles.add(httpFile);
								}
							}
						} catch (Exception e) {
							logger.info("下载公文异常：{}", e);
							Response.json("result","fail");
						}
					}
					this.createZip(httpFiles, docInfo.getDocTitle()+".zip");
				} catch (Exception e) {
					logger.info("创建zip包异常：{}", e);
				}
			}
		}else {
			Response.json("result","fail");
		}
	}

	/**
	 * 支持公文批量下载打包zip
	 * @param zipFileName 
	 * @param files
	 * @param httpServletResponse
	 */
	private void createZip(List<HTTPFile> httpFiles, String zipFileName) {
		InputStream inputStream = null;
		ZipOutputStream zipOutputStream = null;
		try {
			//设置zip包名中文乱码问题
			zipFileName = new String(zipFileName.getBytes(),"ISO-8859-1");
			httpServletResponse.setContentType("application/octet-stream");
			httpServletResponse.setHeader("Content-Disposition", "attachment;filename="+zipFileName);
			zipOutputStream = new ZipOutputStream(httpServletResponse.getOutputStream());
			for (HTTPFile httpFile : httpFiles) {
				inputStream = httpFile.getInputSteam();
				zipOutputStream.putNextEntry(new ZipEntry(httpFile.getFileName()));
				System.err.println(httpFile.getFileName());
				int length;
				if (inputStream != null) {
					while ((length = inputStream.read()) != -1) {
						zipOutputStream.write(length);
						zipOutputStream.flush();
					}
				}
			}
		} catch (Exception e) {
			logger.info("创建zip包异常：{}",e);
		}finally {
			if (zipOutputStream != null) {
				try {
					zipOutputStream.close();
				} catch (IOException e) {
					logger.info("关闭zipOutputStream流异常：{}",e);
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.info("关闭fileInputStream流异常：{}",e);
				}
			}
			logger.info("生成：{}包成功！", zipFileName);
		}
	}
}
