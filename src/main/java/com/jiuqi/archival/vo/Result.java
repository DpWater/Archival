package com.jiuqi.archival.vo;//


import java.io.Serializable;

public class Result<T> implements Serializable {
    private Integer code;
    private String msg;
    private T data;

    public Result() {
        code = 200;
    }
    public Result(String msg) {
        code = 200;
        this.msg = msg;
    }
    public Result(T data) {
        code = 200;
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{code=" + this.code + ", msg='" + this.msg + '\'' + ", data=" + this.data + '}';
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
