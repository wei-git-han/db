package com.css.addbase.suwell;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.css.addbase.FileBaseUtil;
import com.css.addbase.suwell.include.PictureToOfd;
import com.css.base.utils.EncryptUtils;
import com.css.base.utils.StringUtils;
import com.suwell.ofd.custom.agent.ConvertException;
import com.suwell.ofd.custom.agent.HTTPAgent;
import com.suwell.ofd.custom.wrapper.Const.PackType;
import com.suwell.ofd.custom.wrapper.Const.Target;
import com.suwell.ofd.custom.wrapper.Const;
import com.suwell.ofd.custom.wrapper.Hex;
import com.suwell.ofd.custom.wrapper.PackEntry;
import com.suwell.ofd.custom.wrapper.PackException;
import com.suwell.ofd.custom.wrapper.Packet;
import com.suwell.ofd.custom.wrapper.model.Common;

import cpcns.util.WebMocker;
/**
 * 集成转换服务类；
 * @author mashuwen
 * @version 
 * 1、转换类可以进行的转换类型：
 * 	1.1、将单个或多个图片转换成OFD文件；
 *  1.2、将单个或多个流式文件转换成OFD文件；
 *  1.3、将单个或多个PDF文件转换成OFD文件；
 *  1.4、将多个文件合并转换成OFD文件；
 * 
 * 2、此转换类中包含两种转换方法：
 * 	2.1、将所有需要转换的文件在本地生成ZIP包传递给转换服务；（此种访问暂不使用）
 * 	2.2、将所有需要转换的文件的文件流或者文件以HTTPAgent的接口方式直接传递给转换服务，由转换服务自行处理；
 * 
 */
public class OfdTransferUtil{
	private static Logger log = Logger.getLogger(OfdTransferUtil.class);
	private static String tmpFilePath = ConvertServerConnect.initTmpFilePath();
	private static HTTPAgent ha = ConvertServerConnect.initConvertServer();
	/**
	 * 将从扫描仪获取的文件流转换成OFD文件；（使用HTTPAgent的接口进行集成）
	 * @param smwj	扫描件流数据
	 * @param dataId	记录ID
	 * @return	返回上传到文件服务后的ID
	 */
	public static String createdOFDFile(String smwj, String dataId) {
		try {
			// 初始化转换类
			if (ha == null) {
				System.out.println("****（扫描件）未连接到转换服务地址，无法进行转换操作！****");
				return null;
			}
			if (StringUtils.isNotBlank(smwj)) {
				System.out.println("**********【扫描件】开始转换（createdOFDFile）***********" + System.currentTimeMillis());
				// 扫描件是以流的形似从前台传递到后台，是以&符合进行分割每个扫描件的，这里需要进行解析
				String[] s = smwj.split("&");
				if (s != null && s.length > 0) {
					List<String> imgPath = PictureToOfd.getImageFilePath(s, dataId, tmpFilePath);
					if (imgPath != null && imgPath.size() > 0) {
						List<File> imgFileList = new ArrayList<File>();
						for(String img : imgPath) {
							imgFileList.add(new File(img));
						}
						if (imgFileList != null && imgFileList.size() > 0) {
							// 生成的OFD文件存放路径
							String ofdPath = tmpFilePath + "/" + dataId + ".ofd";
							ha.imagesToOFD(imgFileList, new FileOutputStream(ofdPath), -1);
							ha.close();
							System.out.println("**********【扫描件】结束转换（createdOFDFile）***********" + System.currentTimeMillis());
							// 将生成的ofd文件上传到文件服务
							return FileBaseUtil.uploadFileToService(ofdPath, dataId + ".ofd");
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (PackException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ConvertException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 将本地单个文件转换成OFD文件；
	 * 支持的文件格式：doc、docx、wps、uof、pdf等；
	 * @param filePath	文件的路径；此路径只能是一个实体文件的路径；
	 * @return	返回上传到文件服务后的ID
	 */
	public static String convertLocalFileToOFD(String filePath) {
		// 初始化转换类
		if (ha == null) {
			System.out.println("****（文件本地路径）未连接到转换服务地址，无法进行转换操作！****");
			return null;
		}
		Packet packet = new Packet(Const.PackType.COMMON, Const.Target.OFD);
		try {
			if (StringUtils.isNotBlank(filePath)) {
				System.out.println("**********【单文件转换】开始转换（convertLocalFileToOFD）***********" + System.currentTimeMillis());
				String ofdName = System.currentTimeMillis() + ".ofd";
				String ofdPath = tmpFilePath + "/" + ofdName;
//				ha.officeToOFD(new File(filePath), new FileOutputStream(ofdPath));
//				ha.close();
				PackEntry filePack = PackEntry.wrap(new FileInputStream(filePath), MessageDigest.getInstance("MD5"));
				filePack.setHash(Hex.decodeHex(EncryptUtils.fileMD5(filePath).toCharArray()));
				// 获取文件的后缀
				File file = new File(filePath);
				String filename = file.getName();
				String suffix = filename.substring(filename.lastIndexOf(".") + 1);
	            // 2018-12-26 数科的转版服务要求知晓待转换文件的格式，将suffix传递过去
				packet.file(new Common("流式文件转版式文件", suffix, 0, filePack));
	            ha.convert(packet, new FileOutputStream(ofdPath));
	            ha.close();
				packet.close();
				System.out.println("**********【单文件转换】结束转换（convertLocalFileToOFD）***********" + System.currentTimeMillis());
				// 将生成的ofd文件上传到文件服务
				return ofdPath;
			}
		} catch (IOException | ConvertException | PackException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} 
		return null;
	}
	/**
	 * 将本地单个文件转换成OFD文件(不经MD5)；
	 * 支持的文件格式：doc、docx、wps、uof、pdf等；
	 * @param filePath	文件的路径；此路径只能是一个实体文件的路径；
	 * @return	返回上传到文件服务后的ID
	 */
	public static String convertLocalFileNotByMD5ToOFD(String filePath) {
		// 初始化转换类
		if (ha == null) {
			System.out.println("****（文件本地路径）未连接到转换服务地址，无法进行转换操作！****");
			return null;
		}
		//Packet packet = new Packet(Const.PackType.COMMON, Const.Target.OFD);
		try {
			if (StringUtils.isNotBlank(filePath)) {
				System.out.println("**********开始转换（convertLocalFileToOFD）***********" + System.currentTimeMillis());
				String ofdName = System.currentTimeMillis() + ".ofd";
				String ofdPath = tmpFilePath + "/" + ofdName;
				ha.officeToOFD(new File(filePath), new FileOutputStream(ofdPath));
				ha.close();
			/*	PackEntry filePack = PackEntry.wrap(new FileInputStream(filePath), MessageDigest.getInstance("MD5"));
				filePack.setHash(Hex.decodeHex(EncryptUtils.fileMD5(filePath).toCharArray()));
	            packet.file(new Common("流式文件转版式文件", null, 0, filePack));
	            ha.convert(packet, new FileOutputStream(ofdPath));
	            ha.close();
				packet.close();*/
				System.out.println("**********结束转换（convertLocalFileToOFD）***********" + System.currentTimeMillis());
				// 将生成的ofd文件上传到文件服务
				return FileBaseUtil.uploadFileToService(ofdPath, ofdName); 
			}
		} catch (IOException | ConvertException | PackException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	/**
	 * 将单个文件对象转换成OFD文件；
	 * 支持的文件格式：doc、docx、wps、uof、pdf等；
	 * @param file	文件对象；
	 * @return
	 */
	public static String convertFileToOFD(File file) {
		// 初始化转换类
		if (ha == null) {
			System.out.println("****（文件对象）未连接到转换服务地址，无法进行转换操作！****");
			return null;
		}
		Packet packet = new Packet(Const.PackType.COMMON, Const.Target.OFD);
		try {
			if (file != null) {
				System.out.println("**********开始转换（convertFileToOFD）***********" + System.currentTimeMillis());
				String ofdName = System.currentTimeMillis() + ".ofd";
				String ofdPath = tmpFilePath + "/" + ofdName;
//				ha.officeToOFD(file, new FileOutputStream(ofdPath));
//				ha.close();
				PackEntry filePack = PackEntry.wrap(new FileInputStream(file), MessageDigest.getInstance("MD5"));
				filePack.setHash(Hex.decodeHex(EncryptUtils.fileMD5(file).toCharArray()));
	            packet.file(new Common("流式文件转版式文件", null, 0, filePack));
	            ha.convert(packet, new FileOutputStream(ofdPath));
	            ha.close();
				packet.close();
				System.out.println("**********结束转换（convertFileToOFD）***********" + System.currentTimeMillis());
				// 将生成的ofd文件上传到文件服务
				return FileBaseUtil.uploadFileToService(ofdPath, ofdName); 
			}
		} catch (IOException | ConvertException | PackException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 将单个远程文件转换成OFD文件；
	 * 支持的文件格式：doc、docx、wps、uof、pdf等；
	 * @param remotePath	远程文件地址；
	 * @dept Common中的参数说明：参数1为title；
	 * 						参数2为文件类型；（非必填）
	 * 						参数3为文件流，远程地址；
	 * @return
	 */
	public static String convertRemoteFileToOFD(String remotePath) {
		// 初始化转换类
		if (ha == null) {
			System.out.println("****（远程文件）未连接到转换服务地址，无法进行转换操作！****");
			return null;
		}
		try {
			if (StringUtils.isNotBlank(remotePath)) {
				System.out.println("**********开始转换（convertRemoteFileToOFD）***********" + System.currentTimeMillis());
				Packet packet =new Packet(PackType.COMMON, Target.OFD); 
				//可以支持ftp文件路径、html文件路径等、ceb文件等。
				URI uri = new URI(remotePath);
				packet.file(new Common("OFD", null, uri));
				String ofdName = System.currentTimeMillis() + ".ofd";
				String ofdPath = tmpFilePath + "/" + ofdName;
				ha.convert(packet, new FileOutputStream(ofdPath));
				ha.close();
				System.out.println("**********结束转换（convertRemoteFileToOFD）***********" + System.currentTimeMillis());
				// 将生成的ofd文件上传到文件服务
				return FileBaseUtil.uploadFileToService(ofdPath, ofdName); 
			}
		} catch (IOException | ConvertException | PackException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 将单个文件流转换成OFD文件；
	 * @param ins	文件流
	 * @return	返回上传文件服务后的ID
	 */
	public static String convertInstreamToOFD(InputStream ins) {
		// 初始化转换类
		if (ha == null) {
			System.out.println("****（远程文件）未连接到转换服务地址，无法进行转换操作！****");
			return null;
		}
		Packet packet = new Packet(Const.PackType.COMMON, Const.Target.OFD);
		try {
			if (ins != null) {
				System.out.println("**********开始转换（convertInstreamToOFD）***********" + System.currentTimeMillis());
				String ofdName = System.currentTimeMillis() + ".ofd";
				String ofdPath = tmpFilePath + "/" + ofdName;
//				ha.convert(ins, new FileOutputStream(ofdPath));
//				ha.close();
				PackEntry filePack = PackEntry.wrap(ins, MessageDigest.getInstance("MD5"));
				filePack.setHash(Hex.decodeHex(EncryptUtils.fileMD5(ins).toCharArray()));
	            packet.file(new Common("流式文件转版式文件", null, 0, filePack));
	            ha.convert(packet, new FileOutputStream(ofdPath));
	            ha.close();
				packet.close();
				System.out.println("**********结束转换（convertInstreamToOFD）***********" + System.currentTimeMillis());
				// 将生成的ofd文件上传到文件服务
				return FileBaseUtil.uploadFileToService(ofdPath, ofdName);
			}
		} catch (ConvertException | IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (PackException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 将多个文件合并生成一个OFD文件；注此处地址集合和文件名称及合顺序一一对应，如果二者大小不相等取默认文件名称参数title
	 * @param filePath	本地文件地址集合
	 * @param fileTitleList	本地文件名称集合
	 * @param title 主文件标题（或默认标题）
	 * @return	返回上传文件服务后的ID
	 */
	public static String mergeConvertLocalToOFDWithTitle(List<String> filePath,List<String> fileTitleList,String title) {
		// 初始化转换类
		if (ha == null) {
			System.out.println("****（合并本地文件）未连接到转换服务地址，无法进行转换操作！****");
			return null;
		}
		Packet packet = new Packet(Const.PackType.COMMON, Const.Target.OFD);
		String draftTitle="";
		int pathSize = 0;
		if(filePath != null ){pathSize = filePath.size();}
		int titleSize = 0 ;
		if(fileTitleList != null){ titleSize = fileTitleList.size();}
		if (filePath != null && pathSize > 0) {
			try {
				System.out.println("**********【有大纲】开始转换（mergeConvertLocalToOFD）***********" + System.currentTimeMillis());
				List<File> fileList = new ArrayList<File>();
				for (int i = 0; i < pathSize; i++) {
					if(pathSize==titleSize) {
						draftTitle=fileTitleList.get(i);
					}else {
						draftTitle=title;
					}
					String path=filePath.get(i);
					if (StringUtils.isNotBlank(path) && StringUtils.isNotBlank(EncryptUtils.md5(path))) {
						// 获取文件的后缀
						File file = new File(path);
						String filename = file.getName();
						String suffix = filename.substring(filename.lastIndexOf(".") + 1);
						fileList.add(file);
						PackEntry filePack = PackEntry.wrap(new FileInputStream(path), MessageDigest.getInstance("MD5"));
						filePack.setHash(Hex.decodeHex(EncryptUtils.fileMD5(path).toCharArray()));
						// 2018-12-26 数科的转版服务要求知晓待转换文件的格式，将suffix传递过去
						packet.file(new Common(draftTitle, suffix, 0, filePack));
					}
				}				
				if (fileList != null && fileList.size() > 0) {
					String ofdName = System.currentTimeMillis() + ".ofd";
					String ofdPath = tmpFilePath + "/" + ofdName;
//					ha.officesToOFD(fileList, new FileOutputStream(ofdPath));
//					ha.close();
					ha.convert(packet, new FileOutputStream(ofdPath));
					ha.close();
					packet.close();
					System.out.println("**********【有大纲】结束转换（mergeConvertLocalToOFD）***********" + System.currentTimeMillis());
					// 将生成的ofd文件上传到文件服务
					return FileBaseUtil.uploadFileToService(ofdPath, ofdName);
				}
			} catch (PackException | IOException | ConvertException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 将多个文件合并生成一个OFD文件；
	 * @param filePath	文件地址集合；
	 * @return	返回上传文件服务后的ID
	 */
	public static String mergeConvertLocalToOFD(List<String> filePath) {
		// 初始化转换类
		if (ha == null) {
			System.out.println("****（合并本地文件）未连接到转换服务地址，无法进行转换操作！****");
			return null;
		}
		Packet packet = new Packet(Const.PackType.COMMON, Const.Target.OFD);
		if (filePath != null && filePath.size() > 0) {
			try {
				System.out.println("**********【无大纲】开始转换（mergeConvertLocalToOFD）***********" + System.currentTimeMillis());
				List<File> fileList = new ArrayList<File>();
				for(String fp : filePath) {
					if (StringUtils.isNotBlank(fp) && StringUtils.isNotBlank(EncryptUtils.md5(fp))) {
						// 获取文件的后缀
						File file = new File(fp);
						String filename = file.getName();
						String suffix = filename.substring(filename.lastIndexOf(".") + 1);
						fileList.add(file);
						PackEntry filePack = PackEntry.wrap(new FileInputStream(fp), MessageDigest.getInstance("MD5"));
						filePack.setHash(Hex.decodeHex(EncryptUtils.fileMD5(fp).toCharArray()));
						// 2018-12-26 数科的转版服务要求知晓待转换文件的格式，将suffix传递过去
						packet.file(new Common("OFD", suffix, 0, filePack));
					}
				}
				if (fileList != null && fileList.size() > 0) {
					String ofdName = System.currentTimeMillis() + ".ofd";
					String ofdPath = tmpFilePath + "/" + ofdName;
//					ha.officesToOFD(fileList, new FileOutputStream(ofdPath));
//					ha.close();
					ha.convert(packet, new FileOutputStream(ofdPath));
					ha.close();
					packet.close();
					System.out.println("**********【无大纲】结束转换（mergeConvertLocalToOFD）***********" + System.currentTimeMillis());
					// 将生成的ofd文件上传到文件服务
					return FileBaseUtil.uploadFileToService(ofdPath, ofdName);
				}
			} catch (PackException | IOException | ConvertException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 将多个文件合并生成一个OFD文件；
	 * @param  fileList	文件对象集合；
	 * @return	返回上传文件服务后的ID
	 */
	public static String mergeConvertFileToOFD(List<File> fileList) {
		// 初始化转换类
		if (ha == null) {
			System.out.println("****（合并文件对象）未连接到转换服务地址，无法进行转换操作！****");
			return null;
		}
		Packet packet = new Packet(Const.PackType.COMMON, Const.Target.OFD);
		try {
			if (fileList != null && fileList.size() > 0) {
				System.out.println("**********开始转换（mergeConvertFileToOFD）***********" + System.currentTimeMillis());
				for(File file : fileList) {
					PackEntry filePack = PackEntry.wrap(new FileInputStream(file), MessageDigest.getInstance("MD5"));
					filePack.setHash(Hex.decodeHex(EncryptUtils.fileMD5(file).toCharArray()));
					// 获取文件的后缀
					String filename = file.getName();
					String suffix = filename.substring(filename.lastIndexOf(".") + 1);
					// 2018-12-26 数科的转版服务要求知晓待转换文件的格式，将suffix传递过去
					packet.file(new Common("OFD", suffix, 0, filePack));
				}
				String ofdName = System.currentTimeMillis() + ".ofd";
				String ofdPath = tmpFilePath + "/" + ofdName;
//				ha.officesToOFD(fileList, new FileOutputStream(ofdPath));
//				ha.close();
				ha.convert(packet, new FileOutputStream(ofdPath));
				ha.close();
				packet.close();
				System.out.println("**********结束转换（mergeConvertFileToOFD）***********" + System.currentTimeMillis());
				// 将生成的ofd文件上传到文件服务
				return FileBaseUtil.uploadFileToService(ofdPath, ofdName);
			}
		} catch (PackException | IOException | ConvertException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * TODO: 旧版方法，使用ZIP打包的形式进行转换；
	 * 文件转换服务
	 * @param list	扫描图片集合
	 * @param fileTitle	ofd文件的title设置
	 * @param server_url	转换服务的url
	 * @param dataId	生成文档的id,以当前数据的id作为文档的id
	 * @param userName	当前登陆者，设定文档的作者
	 * @throws Exception
	 */
	public static String convert(List<String> list, String fileTitle,
			String server_url, String dataId, String userName){
		//返回上传文件服务的ID
		String fileId = "";
		//存放生成OFD文件的元数据
		Map<String, String> meta = new HashMap<String, String>();
		
		// 设置将要嵌入到最终生成OFD中的元数据;文档属性的地方
		meta.put("DocID", dataId + Math.round((Math.random()*100)));
		try {
			log.debug("接收的Title值为"+fileTitle);
			meta.put("Title", new String((fileTitle + Math.round((Math.random()*100))).getBytes(),"utf-8"));
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		Map<String, String> custems = new HashMap<String, String>();
		// 设置将要嵌入到最终生成OFD中的元数据
		custems.put("attachID", userName);
		// 将元数据集合及待转文档按照规范打成数据源zip包
		File zip = null;
		try {
			zip = ZipUtil.zipFile(meta, custems, list, null, "xml");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		// WebMocker是一个模拟http请求的封装类
		WebMocker http = new WebMocker(server_url);
		// 调用上传接口将数据源包上传并获得任务“票据”
		String ticket = null;
		try {
			if (zip != null) {
				ticket = http.upload(zip);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		String result = null;
		while (true) {
			try {
				Thread.sleep(5 * 100);
			} catch (InterruptedException e) {
				e.printStackTrace();
				//System.out.println(e.getMessage());
			}
			// 每500毫秒依据任务“票据”查询转换状态
			try {
				result = http.query(ticket);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (result == null) {
				System.out.println("转换因为未知原因失败");
				break;
			}
			if (!result.matches("-?\\d+")) {//成功标志
				break;
			}
			if("6000".equals(result) || "7000".equals(result)){
				break;
			}
		}
		if("6000".equals(result) || "7000".equals(result)){//处理错误
			return fileId;
		}
		// 转换完成后得到下载链接，将其保存为指定路径
		try {
			InputStream is = http.down(result);
			//调用文件上传服务
			fileId = FileBaseUtil.wpsServiceUpload(is, fileTitle);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileId;
	}
	
	/**
	 * TODO: 旧版方法，使用ZIP打包的形式进行文件合并操作；
	 * 合并文件，并生成OFD文件
	 * 
	 */
	public static String ofdMerge(List<String> filePathList, String dataTitle, String docId, String userName, String convertServiceUrl){
		String fileFlag = OfdTransferUtil.convert(filePathList, dataTitle, convertServiceUrl, docId, userName);
		return fileFlag;
	}
}
