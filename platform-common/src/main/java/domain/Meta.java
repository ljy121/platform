package domain;

/**
 * @author ljy
 * @version 1.0
 * @date 2020/11/2 0:55
 */
public class Meta {

    private int code;
    private String message;

    public Meta(int code){
        this.code = code;
    }

    public Meta(int code,String message) {
        this.message = message;
        this.code = code;
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
}
