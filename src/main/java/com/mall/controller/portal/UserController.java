package com.mall.controller.portal;

import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServiceResponse;
import com.mall.model.User;
import com.mall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by kevin on 2017/7/13.
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * 登录
     *
     * @param username
     * @param password
     * @param session
     */
    @PostMapping("login.do")
    @ResponseBody
    public ServiceResponse<User> login(String username, String password, HttpSession session) {
        ServiceResponse<User> response = userService.login(username, password);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    /**
     * 退出登录
     *
     * @param session
     * @return
     */
    @GetMapping("logout.do")
    @ResponseBody
    public ServiceResponse<String> logout(HttpSession session) {
        session.removeAttribute(Const.CURRENT_USER);
        return ServiceResponse.createBySucess();
    }

    /**
     * 注册用户
     *
     * @param user
     * @return
     */
    @PostMapping("register.do")
    @ResponseBody
    public ServiceResponse<String> register(User user) {
        return userService.register(user);
    }


    /**
     * 对username和eamil校验
     *
     * @param str
     * @param type
     * @return
     */
    @PostMapping("checkValid.do")
    @ResponseBody
    public ServiceResponse<String> checkValid(String str, String type) {
        return userService.checkValid(str, type);
    }


    /**
     * 获取当前用户信息
     *
     * @param session
     * @return
     */
    @GetMapping("getUserInfo.do")
    @ResponseBody
    public ServiceResponse<User> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return ServiceResponse.createBySucess(user);
        }
        return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户信息");
    }

    /**
     * 忘记密码获取回答问题
     *
     * @param username
     * @return
     */
    @GetMapping("forgetGetQuestion.do")
    @ResponseBody
    public ServiceResponse<String> forgetGetQuestion(String username) {
        return userService.selectQuestion(username);
    }


    /**
     * 校验回答问题的答案
     *
     * @param username
     * @return
     */
    @GetMapping("forgetCheckAnswer.do")
    @ResponseBody
    public ServiceResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        return userService.checkAnswer(username, question, answer);
    }

    /**
     * 忘记密码修改密码
     *
     * @param username
     * @param passwordNew
     * @param token
     * @return
     */
    @GetMapping("forgetResetPassword.do")
    @ResponseBody
    public ServiceResponse<String> forgetResetPassword(String username, String passwordNew, String token) {
        return userService.forgetResetPassword(username, passwordNew, token);
    }

    /**
     * 重置密码
     *
     * @param passwordNew
     * @param passwordOld
     * @param session
     * @return
     */
    @GetMapping("resetPassword.do")
    @ResponseBody
    public ServiceResponse<String> resetPassword(String passwordNew, String passwordOld, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServiceResponse.createByErrorMessage("用户未登录");
        }

        return userService.resetPassword(passwordNew, passwordOld, user);
    }

    @PostMapping("resetPassword.do")
    @ResponseBody
    public ServiceResponse<User> updateUserInfo(User user, HttpSession session) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServiceResponse.createByErrorMessage("用户未登录");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServiceResponse<User> response = userService.updateUserInfo(user);
        if (response.isSuccess()) {
            response.getData().setUsername(currentUser.getUsername());
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }


    /**
     * 未登录需要强制登录
     *
     * @param session
     * @return
     */
    @PostMapping("getInformation.do")
    @ResponseBody
    public ServiceResponse<User> getInformation(HttpSession session) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登录需要强制登录status=10");
        }
        return userService.getInformation(currentUser.getId());
    }


}
