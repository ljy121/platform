package utils;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import freemarker.FreemarkerFormInterFace;
import freemarker.FreemarkerInterface;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zl
 * @ClassName FreemarkerParseUtils
 * @Date 2019/7/24 10:44 AM
 * @Version 1.0
 **/
@Configuration
public class FreemarkerParseUtils implements FreemarkerInterface, FreemarkerFormInterFace {

	public static final Logger log = LoggerFactory.getLogger(FreemarkerParseUtils.class);

	private static FreemarkerFormInterFace freemarkerFormInterFace;

	@Autowired  //①  注入上下文
	private ApplicationContext context;

	/**
	 * 把ftl文件解析成html字符串内容
	 *
	 * @param templatePath ftl文件路径
	 * @param model        模型数据
	 * @return 解析后的html文件内容
	 */
	public static String parse(String templatePath, Map<String, Object> model, Map<String, Object> eleMap, HttpServletRequest request) {
		StringWriter stringWriter = null;
		BufferedWriter writer = null;
		ServletOutputStream out = null;
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
		try {
			freemarkerFormInterFace.changeResultPageMap(model);
			String ymlName = com.zjugis.springboot.constant.Constant.YNL_NAME;
			String uiUrl = YamlConfig.getValue(ymlName, "serviceUrl.ui_version");
			if (StringUtils.isBlank(uiUrl)) {
				uiUrl = YamlConfig.getValue(ymlName, "serviceUrl.UI");
			}
			if (null != templatePath && templatePath.contains("$original$")) {
				String[] tempArr = templatePath.split("\\$original\\$");
				templatePath = tempArr[0] + ".ftl";
			}
			freemarker.template.Template template = SpringUtils.getBean(FreeMarkerConfigurer.class).getConfiguration().getTemplate(templatePath);
			stringWriter = new StringWriter();
			writer = new BufferedWriter(stringWriter);
			//添加all.js版本
			String allJsVserion = RequestPostUtil.httpURLConnectionGet(uiUrl + "/static/version.js", "");
			if (!StringUtils.isBlank(allJsVserion)) {
				allJsVserion = allJsVserion.trim();
				Map versionMap = JSONObject.parseObject(allJsVserion, Map.class);
				allJsVserion = (String) versionMap.get("version");
				model.put("allJsVserion", allJsVserion);
			} else {
				model.put("allJsVserion", "1");
			}
			template.process(model, writer);
			writer.flush();
			String content = stringWriter.toString();
			String jsonpCallback = request.getParameter("jsonpcallback");
			//long stime = System.currentTimeMillis();
			content = freemarkerFormInterFace.changeReadonly(content, request);
			//System.out.println(" freemarkerFormInterFace.changeReadonly:" + (System.currentTimeMillis() - stime) + "ms");
			if (!StringUtils.isBlank(jsonpCallback)) {
				if (null != content) {
					content = content.replaceAll("\\r", "\\\\r");
					content = content.replaceAll("\\n", "\\\\n");
					content = content.replaceAll("\"", "\\\\\"");
					content = content.replaceAll("\'", "\\\\\'");
				}
				content = jsonpCallback + "(\"" + content + "\")";
			}
			//替换extranet_entrance_ip处理
			if (!StringUtils.isBlank(request.getParameter("extranet"))) {
				String convertedUrl;
				String entranceIp = YamlConfig.getValue(ymlName, "extranet_entrance_ip");
				String siteIp = YamlConfig.getValue(ymlName, "serviceUrl." + YamlConfig.getValue(ymlName, "SysID"));
				if(!StringUtils.isBlank(entranceIp) && !StringUtils.isBlank(siteIp)) {
					Pattern p = Pattern.compile("(?<= )(src|href)=['\\\"](.*?)['\\\"]", Pattern.CASE_INSENSITIVE);
					Matcher m = p.matcher(content);
					while(m.find()) {
						convertedUrl = convertEntranceIp(m.group(2), entranceIp, siteIp);
						if(!StringUtils.isBlank(convertedUrl)) {
							content = content.replace(m.group(2), convertedUrl);
						}
					}
				}
			}
			response.setContentType("text/html;charset=utf-8");
			out = response.getOutputStream();
			byte[] data = content.getBytes();
			out.write(data);
			out.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(writer);
			IOUtils.closeQuietly(stringWriter);
		}
		return null;
	}

	/**
	 * 只返回 字符串html
	 *
	 * @param templatePath ftl文件路径
	 * @param model        模型数据
	 * @return
	 */
	public static String parseLocal(String templatePath, Map<String, Object> model, HttpServletRequest request) {
		StringWriter stringWriter = null;
		BufferedWriter writer = null;
		try {
			//处理 skywalking agent代理带来的 temple路径问题
			if (null != templatePath && templatePath.contains("$original$")) {
				String[] tempArr = templatePath.split("\\$original\\$");
				templatePath = tempArr[0] + ".ftl";
			}
			String ymlName = com.zjugis.springboot.constant.Constant.YNL_NAME;
			String uiUrl = YamlConfig.getValue(ymlName, "serviceUrl.ui_version");
			if (StringUtils.isBlank(uiUrl)) {
				uiUrl = YamlConfig.getValue(ymlName, "serviceUrl.UI");
			}
			freemarker.template.Template template = ((FreeMarkerConfigurer) SpringUtils.getBean(FreeMarkerConfigurer.class)).getConfiguration().getTemplate(templatePath);

			stringWriter = new StringWriter();
			writer = new BufferedWriter(stringWriter);
			String allJsVserion = RequestPostUtil.httpURLConnectionGet(uiUrl + "/static/version.js", "");
			if (null != allJsVserion) {
				allJsVserion = allJsVserion.trim();
				Map versionMap = (Map) JSONObject.parseObject(allJsVserion, Map.class);
				allJsVserion = (String) versionMap.get("version");
				model.put("allJsVserion", allJsVserion);
			} else {
				model.put("allJsVserion", "1");
			}
			template.process(model, writer);
			writer.flush();
			return stringWriter.toString();

		} catch (Exception var20) {
			log.error(var20.getMessage());
		} finally {
			IOUtils.closeQuietly(writer);
			IOUtils.closeQuietly(stringWriter);
		}
		return "";
	}

	//获得自身对象
	@PostConstruct
	public void setSelf() {
		this.freemarkerFormInterFace = context.getBean(FreemarkerFormInterFace.class);
	}

	@Override
	public String changeReadonly(String content, HttpServletRequest request) {
		return content;
	}

	@Override
	public Map changeResultPageMap(Map map) {
		return map;
	}

	/**
	 * 转换EntranceIp
	 * @param url
	 * @param entranceIp
	 * @param siteIp
	 * @return
	 */
	private static String convertEntranceIp(String url, String entranceIp, String siteIp) {
		if(!StringUtils.isBlank(url) && url.contains("http")) {
			String siteUrl = siteIp.substring(siteIp.indexOf("://") + 3);
			if (url.contains(siteUrl.substring(0, siteUrl.indexOf("/")))) {
				int index = url.indexOf("://") + 3;
				String noHttp = url.substring(index);
				return url.substring(0, index) + entranceIp + noHttp.substring(noHttp.indexOf("/"));
			}
		}
		return null;
	}
}
