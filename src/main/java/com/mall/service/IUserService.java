package com.mall.service;

import com.mall.common.ServiceResponse;
import com.mall.model.User;

/**
 * Created by kevin on 2017/7/13.
 */
public interface IUserService {

    ServiceResponse<User> login(String username, String password);

    ServiceResponse<String> register(User user);

    ServiceResponse<String> checkValid(String str, String type);

    ServiceResponse selectQuestion(String username);

    ServiceResponse<String> checkAnswer(String username, String question, String answer);

    ServiceResponse<String> forgetResetPassword(String username, String passwordNew, String token);

    ServiceResponse<String> resetPassword(String passwordNew, String passwordOld, User user);

    ServiceResponse<User> updateUserInfo(User user);

    ServiceResponse<User> getInformation(Integer userId);

    ServiceResponse checkAdminRole(User user);
}
