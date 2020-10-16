/**
 * 
 */
package com.css.addbase;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.css.base.utils.UUIDUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import cn.com.css.filestore.impl.HTTPFile;

/**
 * 
 * @author gengds
 *
 *         2017-5-30
 */
public class FileBaseUtil {
	
	
	
	public FileBaseUtil() {

	}

	/**
	 * 保存文件
	 * 
	 * @author 2017-5-30
	 */
	public static String uploadFile(MultipartFile file, String path) {
		String fileName = file.getOriginalFilename();
		if (StringUtils.isNotEmpty(fileName)) {
			// 读取文件内容
			InputStream is;
			try {
				is = file.getInputStream();
				fileName = new Date().getTime() + "--" + fileName;
				path = path + fileName;
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
				// 复制文件内容
				byte[] b = new byte[1024 * 5];
				int len;
				while ((len = is.read(b)) != -1) {
					bos.write(b, 0, len);
				}
				bos.flush();
				is.close();
				bos.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return path;
	}

	/**
	 * 下载文件
	 * 
	 * @author 2017-5-30
	 */
	public static HttpServletResponse download(String path, HttpServletResponse response) {
		File file = new File(path);
		String fileName = file.getName();
		try {
			InputStream fis = new BufferedInputStream(new FileInputStream(path));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			response.reset();
			response.addHeader("Content-Disposition",
					"attachment;filename=" + new String(fileName.getBytes(), "ISO-8859-1"));
			response.addHeader("Content-Length", "" + file.length());
			OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/octet-stream");
			toClient.write(buffer);
			toClient.flush();
			toClient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public static HttpServletResponse downloadLocal(String path, HttpServletResponse response) {
		File file = new File(path);
		String fileName = file.getName();
		try {
			InputStream fis = new BufferedInputStream(new FileInputStream(path));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			response.reset();
			response.setContentType("bin");
			response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes()));
			// 复制文件内容
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = fis.read(b)) != -1) {
				response.getOutputStream().write(b, 0, len);
			}
			response.getOutputStream().flush();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * 将文件保存到字节数组中
	 * 
	 * @param is
	 *            读取了文件的输入流
	 * @author gengds 2017-5-30
	 */
	public static byte[] inputStreamToByte(InputStream is) throws IOException {
		ByteArrayOutputStream bAOutputStream = new ByteArrayOutputStream();
		// 复制文件内容
		byte[] b = new byte[1024 * 5];
		int len;
		while ((len = is.read(b)) != -1) {
			bAOutputStream.write(b, 0, len);
		}
		byte data[] = bAOutputStream.toByteArray();
		bAOutputStream.close();
		return data;
	}

	/**
	 * 将字节数组写到文件中
	 * 
	 * @param data
	 *            字节数组
	 * @param path
	 *            文件存储路径
	 * @author gengds 2017-5-30
	 */
	@SuppressWarnings("null")
	public static String ByteToFile(byte[] data, String path) throws IOException {
		InputStream in = null;
		File file = new File(path);
		if (!file.exists()) {
			file.createNewFile(); // 如果文件不存在，则创建
		}
		FileOutputStream fos = new FileOutputStream(file);
		int size = 0;
		if (data.length > 0) {
			fos.write(data, 0, data.length);
		} else {
			while ((size = in.read(data)) != -1) {
				fos.write(data, 0, size);
			}
			in.close();
		}
		fos.flush();
		fos.close();
		return path;
	}

	/**
	 * 使用文件服务上传文件
	 * 
	 * @param fileStream
	 *            文件流
	 * @author fileId 文件在文件服务中的唯一标识
	 * @author gengds
	 */
	public static String fileServiceUpload(MultipartFile fileStream) {

		if (fileStream == null || StringUtils.isEmpty(fileStream.getOriginalFilename())) {
			return "";
		}
		try {
			HTTPFile httpFile = HTTPFile.save(fileStream.getInputStream(), fileStream.getOriginalFilename());
			String httpFileId = httpFile.getFileId();
			return httpFileId;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 使用文件服务上传wps文件
	 * 
	 * @param is
	 *            文件流
	 * @author fileName 文件在文件服务中的唯一标识
	 * @author gengds
	 */
	public static String wpsServiceUpload(InputStream is, String fileName) {

		if (is == null || StringUtils.isEmpty(fileName)) {
			return "";
		}
		HTTPFile httpFile = HTTPFile.save(is, fileName);
		String httpFileId = httpFile.getFileId();
		return httpFileId;
	}

	/**
	 * 使用文件服务上传文件
	 * 
	 * @param filePath
	 *            文件路径
	 * @author gengds
	 */
	public static String uploadFileToService(String filePath, String fileName) {
		if (StringUtils.isNotBlank(filePath)) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(new File(filePath));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			HTTPFile httpFile = HTTPFile.save(fis, fileName);
			String httpFileId = httpFile.getFileId();
			return httpFileId;
		}
		return "";
	}

	public byte[] convertToPrimitiveArray(Byte[] objects) {
		byte[] bytes = new byte[1024 * 5];
		if (objects != null && objects.length > 0) {
			bytes = new byte[objects.length];
			for (int i = 0; i < objects.length; i++) {
				bytes[i] = objects[i].byteValue();
			}
		}
		return bytes;
	}

	public static String download(String fileId) {
		HTTPFile httpFile = new HTTPFile(fileId);
		// getAssginDownloadURL方法将返回当前文件的远程下载URL，有效期一次。
		// 返回的UTL地址可在浏览器中直接使用无权限验证。
		String downloadURL = httpFile.getAssginDownloadURL(true);
		return downloadURL;
	}

	/**
	 * @Title: fileuploade.流版文件统一的上传方法
	 * @Description: --------------------------------------
	 * @Param: [request, fileName:文件名称, streamOrFormatFileType:流式版式类型, response]
	 * @return: java.lang.String : 文件服务的文件ID
	 * @Author: jekyll14(zhang xiaoming)
	 * @CreateTime: 2019/1/14 10:58
	 */
	public static String fileuploadtoFileServerReturnID(HttpServletRequest request, String fileName,
			String streamOrFormatFileType) throws IOException {
		InputStream inputStream = request.getInputStream();
		String httpFileId = "";
		// 版式文件的上传方法||csseoa控件传过来的文件（都是包含http头的数据流）
		if (StringUtils.equals("format", streamOrFormatFileType)
				|| StringUtils.equals("stream.fromcsseoa", streamOrFormatFileType)) {
			httpFileId = ofdfileupload(request);
		} else {// 其他文件的上传方法
			HTTPFile httpFile = HTTPFile.save(inputStream, fileName);
			httpFileId = httpFile.getFileId();
		}
		return httpFileId;
	}

	/**
	 * @Title: fileuploadtoFileServerRuturnHTTPFile.流版文件统一的上传方法
	 * @Description: --------------------------------------
	 * @Param: [request, fileName:文件名称, streamOrFormatFileType:流式版式类型, response]
	 * @return: cn.com.css.filestore.impl.HTTPFile : 文件服务的文件对象
	 * @Author: jekyll14(zhang xiaoming)
	 * @CreateTime: 2019/1/14 12:39
	 */
	public static HTTPFile fileuploadtoFileServerRuturnHTTPFile(HttpServletRequest request, String fileName,
			String streamOrFormatFileType) throws IOException {
		return new HTTPFile(fileuploadtoFileServerReturnID(request, fileName, streamOrFormatFileType));
	}

	// 接受上传的文件的大小4G
	private static final long MAX_FILE_SIZE = 4L * 1024 * 1024 * 1024;
	// 缓存大小1M
	private static final int BUFFER_SIZE = 1 * 1024 * 1024;

	/**
	 * @Title: ofdfileupload.版式文件特殊的上传
	 * @Description: 由于版式控件从前台传递过来的文件流中带有http的标识，需要落地应用做文件流的处理
	 *               --------------------------------------
	 * @Param: [request, response]
	 * @return: java.lang.String : 文件服务的文件ID
	 * @Author: jekyll14(zhang xiaoming)
	 * @CreateTime: 2019/1/14 11:14
	 */
	private static String ofdfileupload(HttpServletRequest request) throws IOException {
		try {
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			if (!isMultipart) {
				return "";
			}
			// 利用apache的类完成落地，识别文件流中的http文件头，如果有有丢弃
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(BUFFER_SIZE);// max memory cache
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setFileSizeMax(MAX_FILE_SIZE);
			String charset = request.getCharacterEncoding();
			if (charset == null) {
				charset = "UTF-8";
			}
			upload.setHeaderEncoding(charset);
			List<FileItem> items = upload.parseRequest(request);
			if ((items == null) || items.isEmpty()) {
				System.out.println("没有任何文件上传!");
				return "";
			}
			FileItem fi = null;
			for (FileItem item : items) {
				if (!item.isFormField()) {
					fi = item;
					break;

				}
			}
			if (fi != null) {
				String name = fi.getName();
				HTTPFile httpFile = HTTPFile.save(fi.getInputStream(), name);
				return httpFile.getFileId();
			} else
				System.out.println("commons-fileupload- 1.3.1.jar未发现任何上传文件");
			return "";
		} catch (Exception e) {
			System.out.println("commons-fileupload- 1.3.1.jar中写出FileItem.write()发生异常");
			// response.sendError(500, e.getMessage());
		}
		return "";
	}

	/**
	 * 不使用文件服务上传，上传文件保存本地
	 * 
	 * @param fileStream
	 *            文件流
	 * @author fileId 文件在文件服务中的唯一标识
	 * @author gengds
	 */
	public static Map<String,Object> fileServiceUploadByFilePath(MultipartFile fileStream, String filePath,String localAddress) {
		Map<String,Object> map = new HashMap<>();
		if (fileStream == null || StringUtils.isEmpty(fileStream.getOriginalFilename())) {
			return map;
		}
		filePath = findPath() + filePath;
		String fileName = fileStream.getOriginalFilename();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String date = format.format(new Date());
		String substring = fileName.substring(fileName.indexOf(".")); // 文件后缀.ofd
		String substring2 = fileName.substring(0, fileName.indexOf("."));// 文件名称无后缀
		String fileName1 = UUIDUtils.random();
		fileName = substring2 + date;
		File targetFile = new File(filePath);
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		FileOutputStream out = null;
		try {
			String lastFilePath = filePath + fileName1 + substring;
			out = new FileOutputStream(lastFilePath);
			out.write(fileStream.getBytes());
			String fileUrl =localAddress+ "app/db/documentinfo/download?path=" + lastFilePath;
			map.put("filePath",filePath);
			map.put("fileName1",fileName1);
			map.put("substring",substring);
			map.put("fileUrl",fileUrl);
			map.put("filePath1",lastFilePath);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return map;
	}

	public static String findPath() {
		String url = System.getProperty("user.dir");
		return url;
	}
	
	
	/**
	 * 不使用文件服务上传，上传文件保存本地
	 * 
	 * @param fileStream
	 *            文件流
	 * @author fileId 文件在文件服务中的唯一标识
	 * @author gengds
	 */
	public static String fileServiceUploadByFilePathLiuShi(MultipartFile fileStream, String filePath,String localAddress) {
		if (fileStream == null || StringUtils.isEmpty(fileStream.getOriginalFilename())) {
			return "";
		}
		filePath = findPath() + filePath;
		String fileName = fileStream.getOriginalFilename();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String date = format.format(new Date());
		String substring = fileName.substring(fileName.indexOf(".")); // 文件后缀.ofd
		String substring2 = fileName.substring(0, fileName.indexOf("."));// 文件名称无后缀
		fileName = substring2 + date;
		File targetFile = new File(filePath);
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		FileOutputStream out = null;
		try {
			String lastFilePath = filePath + fileName + substring;
			out = new FileOutputStream(lastFilePath);
			out.write(fileStream.getBytes());
			//String fileUrl =localAddress+ "app/db/documentinfo/download?path=" + targetFile.getAbsolutePath();
			return lastFilePath;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "";
	}

	public static Map<String,Object> fileServiceUploadByFilePath1(MultipartFile fileStream, String filePath, String localAddress) {
		Map<String,Object> map = new HashMap<>();
		if (fileStream == null || StringUtils.isEmpty(fileStream.getOriginalFilename())) {
			return map;
		}
		filePath = findPath() + filePath;
		String fileName = fileStream.getOriginalFilename();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String date = format.format(new Date());
		String substring = fileName.substring(fileName.indexOf(".")); // 文件后缀.ofd
		String substring2 = fileName.substring(0, fileName.indexOf("."));// 文件名称无后缀
		String fileName1 = UUIDUtils.random();
		File targetFile = new File(filePath);
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		FileOutputStream out = null;
		try {
			String lastFilePath = filePath + fileName1 + substring;
			out = new FileOutputStream(lastFilePath);
			out.write(fileStream.getBytes());
			String fileUrl =localAddress+ "app/db/documentinfo/download?id=" + lastFilePath;
			map.put("filePath",filePath);
			map.put("fileName1",fileName1);
			map.put("substring",substring);
			map.put("fileUrl",fileUrl);
			map.put("filePath1",lastFilePath);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return map;
	}

}
