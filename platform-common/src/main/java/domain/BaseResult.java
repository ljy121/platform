package domain;


import javax.servlet.http.HttpServletResponse;

/**
 * @author ljy
 * @version 1.0
 * @date 2020/11/2 0:53
 */
public class BaseResult {

    private static final String OK = "OK";
    private static final String ERROR = "ERROR";

    private int code;
    private String message;
    private Object data;

    public BaseResult ok(){
        this.code = 200;
        this.message = OK;
        return this;
    }

    public BaseResult ok(String message){
        this.code = 200;
        this.message = message;
        return this;
    }

    public BaseResult ok(Object data){
        this.code = 200;
        this.message = OK;
        this.data = data;
        return this;
    }

    public BaseResult ok(String message,Object data){
        this.code = 200;
        this.message = message;
        this.data = data;
        return this;
    }

    public BaseResult error(){
        this.code = 500;
        this.message = "error!";
        return this;
    }

    public BaseResult error(int code){
        this.code = code;
        this.message = "error!";
        return this;
    }

    public BaseResult error(String message){
        this.code = 500;
        this.message = message;
        return this;
    }

    public BaseResult error(int code, String message){
        this.code = code;
        this.message = message;
        return this;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
