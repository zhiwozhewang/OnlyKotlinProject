package com.enn.ionic.js;

/**
 * 数据接收基类 根据服务端返回数据封装
 * @param <T>
 */
public class HttpResponse<T> {
    public final static int CODE_SUCCESS = 200;//成功
    public final static int CODE_FAIL = 400;//失败
    public final static int CODE_UNAUTHORIZED = 401;//未认证（签名错误）
    public final static int CODE_NOT_FOUND = 404;//接口不存在
    public final static int CODE_NOT_OUT = 403;//需要登录
    public final static int CODE_NOT_407 = 407;//需要登录
    public final static int CODE_INTERNAL_SERVER_ERROR = 500;//服务器内部错误
    public int code;//状态码
    public String msg;//返回消息
    public T data;//返回数据实体

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
