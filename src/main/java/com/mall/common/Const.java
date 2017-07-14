package com.mall.common;

/**
 * Created by kevin on 2017/7/13.
 */
public class Const {

    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL="email";
    public static final String USERNAME="username";

    public interface Role {
        int ROLE_CUSTOMER = 0;//普通用户
        int ROLE_admin = 1;//管理员
    }
}
