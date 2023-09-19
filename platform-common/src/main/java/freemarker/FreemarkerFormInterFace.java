package freemarker;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface FreemarkerFormInterFace {

	String changeReadonly(String value, HttpServletRequest request);

	Map changeResultPageMap(Map map);
}
