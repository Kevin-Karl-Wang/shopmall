package com.mall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * Created by kevin on 2017/7/13.
 */

//保证序列化json时，如果是null的对象，key也会消失
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServiceResponse<T> implements Serializable {

    private int status;
    private String msg;
    private T data;

    private ServiceResponse(int status) {
        this.status = status;
    }

    private ServiceResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServiceResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    private ServiceResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    //@JsonIgnore 不会出现在json序列化中
    @JsonIgnore
    public boolean isSuccess() {
        return this.status == ResponseCode.SUCCESS.getCode();
    }


    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }


    public static <T> ServiceResponse<T> createBySucess() {
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServiceResponse<T> createBySucessMessage(String msg) {
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode(), msg);
    }

    public static <T> ServiceResponse<T> createBySucess(T data) {
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode(), data);
    }

    public static <T> ServiceResponse<T> createBySucess(String msg, T data) {
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode(), msg, data);
    }

    public static <T> ServiceResponse<T> createByError() {
        return new ServiceResponse<T>(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getDesc());
    }

    public static <T> ServiceResponse<T> createByErrorMessage(String errorMessage) {
        return new ServiceResponse<T>(ResponseCode.ERROR.getCode(), errorMessage);
    }

    public static <T> ServiceResponse<T> createByErrorCodeMessage(int errorCode, String errorMessage) {
        return new ServiceResponse<T>(errorCode, errorMessage);
    }


}
