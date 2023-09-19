package controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.common.base.Strings;
import com.zjugis.springboot.constant.Constant;
import com.zjugis.springboot.constant.HttpStatusConstants;
import com.zjugis.springboot.domain.BaseResult;
import com.zjugis.springboot.util.FreemarkerParseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class BaseController{

	public static final Logger log = LoggerFactory.getLogger(BaseController.class);

	@Resource
	protected HttpServletRequest request;

	@Resource
	protected HttpServletResponse response;

	public static enum Result {
		success, code, message, data
	}

	/*
	 * 处理action实体参数日期属性映射问题
	 * */
	@InitBinder
	protected void initBinder(ServletRequestDataBinder binder) {
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			CustomDateEditor editor = new CustomDateEditor(df, false);
			binder.registerCustomEditor(Date.class, editor);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	protected String success() {
		return toJson(new BaseResult().ok(), null);
	}

	protected String success(Object data) {
		return toJson(new BaseResult().ok(data), null);
	}

	protected String success(Object data, String timeFormat) {
		return toJson(new BaseResult().ok(data), timeFormat);
	}

	protected String error() {
		return toJson(new BaseResult().error(), null);
	}

	protected String error(HttpStatusConstants constants) {
		return toJson(new BaseResult().error(constants), null);
	}

	protected String error(Exception e) {
		return toJson(new BaseResult().error(e.getMessage()), null);
	}

	protected String error(int code, String message) {
		return toJson(new BaseResult().error(code, message), null);
	}

	/**
	 * @param paramsStr
	 * @return
	 */
	protected Map<String, String> parseParamsToMap(String paramsStr) {
		return JSON.parseObject(paramsStr, Map.class);
	}

	/**
	 * @param obj
	 * @return
	 */
	protected String parseObjToJsonString(Object obj) {
		return JSON.toJSONString(obj);
	}


	private Map getWorkflowMap() {
		long stime =System.currentTimeMillis();
		Map workFlowMap = null;
		if (Constant.getFutrue() != null) {
			try {
				workFlowMap = (Map) Constant.getFutrue().get(10, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
			} finally {
				if (workFlowMap == null) {
					workFlowMap = new HashMap();
				}
			}
		}
		System.out.println("getWorkflowMap:"+ (System.currentTimeMillis()-stime)+"ms");
		return workFlowMap;
	}

	/**
	 * 页面地址按约定，默认 /Users/list  (Users 是文件夹名称 同样是 UsersController的前半部分，必须保持一致，list 既要是方法名 也是 页面名称 list.ftl)
	 * 直接返回 ftl页面（纯字符串）
	 *
	 * @param model （ftl页面的数据  例如${XXX} ）
	 * @return
	 */
	protected String resultPage(Map model) {
		long sTime = System.currentTimeMillis();
//		Map workFlow = (Map) request.getAttribute("WORKFLOW");
		Map workFlowMap = getWorkflowMap();
		if (workFlowMap != null) {
			model.put("WORKFLOW", workFlowMap);
		}
		StackTraceElement elements[] = Thread.currentThread().getStackTrace();
		String className = elements[2].getClassName().split("\\.")[elements[2].getClassName().split("\\.").length - 1];
		className = className.replaceAll("Controller", "");
		String methodName = elements[2].getMethodName();
		String tempPath = "/" + className + "/" + methodName + ".ftl";
		log.info("resultPage" + (System.currentTimeMillis() - sTime));
		return FreemarkerParseUtils.parse(tempPath, model, new HashMap<>(), request);
	}

	/**
	 * 直接返回 ftl页面（纯字符串）
	 *
	 * @param authorityMap
	 * @param model
	 * @return
	 */
	protected String resultPage(Map model, Map authorityMap) {
		//Map workFlow = (Map) request.getAttribute("WORKFLOW");
		Map workFlowMap = getWorkflowMap();
		if (workFlowMap != null) {
			model.put("WORKFLOW", workFlowMap);
		}
		StackTraceElement elements[] = Thread.currentThread().getStackTrace();
		String className = elements[2].getClassName().split("\\.")[elements[2].getClassName().split("\\.").length - 1];
		className = className.replaceAll("Controller", "");
		String methodName = elements[2].getMethodName();
		String tempPath = "/" + className + "/" + methodName + ".ftl";
		return FreemarkerParseUtils.parse(tempPath, model, authorityMap, request);
	}

	/**
	 * 直接返回 ftl页面（纯字符串）
	 *
	 * @param page（templatePath：ftl地址）
	 * @param model
	 * @return
	 */
	protected String resultPage(String page, Map model) {
		Map workFlowMap = getWorkflowMap();
		if (workFlowMap != null) {
			model.put("WORKFLOW", workFlowMap);
		}
		return FreemarkerParseUtils.parse(page, model, new HashMap<>(), request);
	}

	/**
	 * 直接返回 ftl页面（纯字符串）
	 *
	 * @param page（templatePath：ftl地址）
	 * @param model
	 * @return
	 */
	protected String resultPage(String page, Map model, Map authorityMap) {
		//Map workFlow = (Map) request.getAttribute("WORKFLOW");
		Map workFlowMap = getWorkflowMap();
		if (workFlowMap != null) {
			model.put("WORKFLOW", workFlowMap);
		}
		return FreemarkerParseUtils.parse(page, model, authorityMap, request);
	}

	/**
	 * 直接返回 ftl页面（纯字符串）
	 *
	 * @param page（templatePath：ftl地址）
	 * @return
	 */
	protected String resultPage(String page) {
		return FreemarkerParseUtils.parse(page, new HashMap<>(), new HashMap<>(), request);
	}

	/**
	 * 只返回 ftl页面（纯字符串）
	 *
	 * @param model
	 * @return
	 */
	protected String localPage(Map<String, Object> model) {
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		String className = elements[2].getClassName().split("\\.")[elements[2].getClassName().split("\\.").length - 1];
		className = className.replaceAll("Controller", "");
		String methodName = elements[2].getMethodName();
		String tempPath = "/" + className + "/" + methodName + ".ftl";
		return FreemarkerParseUtils.parseLocal(tempPath, model, request);
	}

	/**
	 * 只返回 ftl页面（纯字符串）
	 *
	 * @param model,page
	 * @return
	 */
	protected String localPage(String page, Map<String, Object> model) {
		return FreemarkerParseUtils.parseLocal(page, model, request);
	}

	/**
	 * result ok
	 *
	 * @param data 返回结果
	 * @return
	 */
	protected Map result(Object data) {
		Map<String, Object> result = new HashMap();
		result.put(Result.success.name(), true);
		result.put(Result.data.name(), data);
		return result;
	}

	/**
	 * result ok
	 *
	 * @param data 返回结果
	 * @return
	 */
	protected String ok(Object data) {
		return toJson(data, null);
	}

	/**
	 * result ok
	 *
	 * @param data 返回结果
	 * @return
	 */
	protected String ok(Object data, String timeFormat) {
		return toJson(data, timeFormat);
	}

	/**
	 * error result
	 *
	 * @param message   错误信息
	 * @param errorCode 错误代码
	 * @return
	 */
	protected String error(String message,int errorCode) {
		Map<String, Object> result = new HashMap<>();
		result.put("msg", message);
		result.put("error_code", errorCode);
		return toJson(result, null);
	}

	private String toJson(Object obj, String timeFormat) {
		JSONWriter.Feature[] serializerFeatures = new JSONWriter.Feature[]{
				JSONWriter.Feature.WriteMapNullValue
				, JSONWriter.Feature.WriteNullStringAsEmpty};
		String jsonpCallback = request.getParameter("jsonpcallback");
		return !StringUtils.isBlank(jsonpCallback) ? jsonpCallback + "(" + JSON.toJSONString(obj, serializerFeatures) + ")" : JSON.toJSONString(obj, serializerFeatures);
	}

	/**
	 * send file
	 *
	 * @param file
	 * @param response
	 */
	protected void sendFile(File file, HttpServletResponse response) throws IOException {
		if (file == null || this.response == null) {
            return;
        }
		if (file.exists()) {
			this.response.setDateHeader("Last-Modified", file.lastModified());
			this.response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(file.getName(), Constant.DEFAULT_ENCODING));
			this.response.setContentLength((int) file.length());
			FileCopyUtils.copy(Files.newInputStream(file.toPath()), this.response.getOutputStream());
		} else {
			this.response.sendError(HttpServletResponse.SC_NOT_FOUND, "request file not found");
		}
		this.response.getOutputStream().flush();
	}

	/**
	 * send stream
	 *
	 * @param inputStream
	 * @param fileName
	 * @throws IOException
	 */
	protected void sendStream(InputStream inputStream, String fileName) throws IOException {
		if (inputStream == null || response == null) return;
		if (inputStream.available() > 0) {
			response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, Constant.DEFAULT_ENCODING));
			response.setDateHeader("Last-Modified", new Date().getTime());
			response.setContentLength(inputStream.available());
			FileCopyUtils.copy(inputStream, response.getOutputStream());
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "request file not found");
		}
		response.getOutputStream().flush();
	}

	/**
	 * @param file
	 * @throws IOException
	 */
	protected void sendFileStream(File file) throws IOException {
		InputStream inputStream = Files.newInputStream(file.toPath());
		if (response == null) {
            return;
        }
		if (inputStream.available() > 0) {
			response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(file.getName(), Constant.DEFAULT_ENCODING));
			response.setDateHeader("Last-Modified", new Date().getTime());
			response.setContentLength(inputStream.available());
			FileCopyUtils.copy(inputStream, response.getOutputStream());
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "request file not found");
		}
		response.getOutputStream().flush();
	}

	/**
	 * @param file
	 * @param contentType
	 * @param fileName
	 * @throws IOException
	 */
	protected void sendFileStream(File file, String contentType, String fileName) throws IOException {
		InputStream inputStream = Files.newInputStream(file.toPath());
		if (response == null) {
            return;
        }
		if (inputStream.available() > 0) {
			response.addHeader("Content-Type", contentType);
			response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, Constant.DEFAULT_ENCODING));
			response.setDateHeader("Last-Modified", new Date().getTime());
			response.setContentLength(inputStream.available());
			FileCopyUtils.copy(inputStream, response.getOutputStream());
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "request file not found");
		}
		response.getOutputStream().flush();
	}

	protected void sendFileStream(byte[] data, String contentType, String fileName) throws IOException {
		if (data == null || response == null) {
            return;
        }
		response.addHeader("Content-Type", contentType);
		response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, Constant.DEFAULT_ENCODING));
		response.setDateHeader("Last-Modified", new Date().getTime());
		response.setContentLength(data.length);
		FileCopyUtils.copy(data, response.getOutputStream());
		response.getOutputStream().flush();
	}

}
