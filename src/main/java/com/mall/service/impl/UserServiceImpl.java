package com.mall.service.impl;

import com.mall.common.Const;
import com.mall.common.ServiceResponse;
import com.mall.common.TokenCache;
import com.mall.dao.UserDao;
import com.mall.model.User;
import com.mall.service.IUserService;
import com.mall.utils.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by kevin on 2017/7/13.
 */
@Service("userService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserDao userDao;

    /**
     * 登录
     *
     * @param username
     * @param password
     * @return
     */
    @Override
    public ServiceResponse<User> login(String username, String password) {
        int count = userDao.checkUserName(username);
        if (count == 0) {
            return ServiceResponse.createByErrorMessage("用户名不存在");
        }

        //todo  密码登录MD5
        String md5Passwprd = MD5Util.MD5EncodeUtf8(password);
        User user = userDao.selectLogin(username, md5Passwprd);
        if (null == user) {
            return ServiceResponse.createByErrorMessage("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServiceResponse.createBySucess("登录成功", user);

    }

    /**
     * 注册
     *
     * @param user
     * @return
     */
    @Override
    public ServiceResponse<String> register(User user) {
        ServiceResponse<String> validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }
        validResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        //md5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int count = userDao.insert(user);
        if (count == 0) {
            return ServiceResponse.createByErrorMessage("注册失败");
        }
        return ServiceResponse.createBySucessMessage("注册成功");
    }

    @Override
    public ServiceResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)) {
            //开始校验
            if (Const.USERNAME.equals(type)) {
                int count = userDao.checkUserName(str);
                if (count > 0) {
                    return ServiceResponse.createByErrorMessage("用户名已经存在");
                }
            }
            if (Const.EMAIL.equals(type)) {
                int count = userDao.checkEmail(str);
                if (count > 0) {
                    return ServiceResponse.createByErrorMessage("email已经存在");
                }
            }
        } else {
            return ServiceResponse.createByErrorMessage("参数错误");
        }
        return ServiceResponse.createBySucessMessage("校验成功");
    }


    public ServiceResponse selectQuestion(String username) {
        ServiceResponse<String> validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            //用户不存在
            return ServiceResponse.createByErrorMessage("用户不存在");
        }
        String question = userDao.selectQuestionByUsername(username);
        if (StringUtils.isNotBlank(question)) {
            return ServiceResponse.createBySucessMessage(question);
        }
        return ServiceResponse.createByErrorMessage("找回密码的问题是空的");
    }


    public ServiceResponse<String> checkAnswer(String username, String question, String answer) {
        int count = userDao.checkAnswer(username, question, answer);
        if (count > 0) {
            //说明问题及答案是这个用户的，确实是正确的
            String forgetTakon = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetTakon);
            return ServiceResponse.createBySucessMessage(forgetTakon);
        }
        return ServiceResponse.createByErrorMessage("问题的答案错误");
    }

    public ServiceResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if (StringUtils.isNotBlank(forgetToken)) {
            return ServiceResponse.createByErrorMessage("参数错误，token需要传递");
        }
        ServiceResponse<String> validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            //用户不存在
            return ServiceResponse.createByErrorMessage("用户不存在");
        }

        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)) {
            return ServiceResponse.createByErrorMessage("token无效或者过期");
        }

        if (StringUtils.equals(forgetToken, token)) {
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int count = userDao.updatePasswordByUsername(username, md5Password);
            if (count > 0) {
                return ServiceResponse.createBySucessMessage("修改密码成功");
            }
        } else {
            return ServiceResponse.createByErrorMessage("token错误，请重新获取重置密码的token");
        }
        return ServiceResponse.createByErrorMessage("修改密码失败");
    }

    @Override
    public ServiceResponse<String> resetPassword(String passwordNew, String passwordOld, User user) {
        //防止横向越权，要校验一下这个这个用户的旧密码，一定要指定是这个用户，因为我们会查询一个count,若是不指定id,那么结果是true count>0

        int cound = userDao.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if (cound == 0) {
            return ServiceResponse.createByErrorMessage("旧密码错误");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userDao.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServiceResponse.createBySucessMessage("密码更新成功");
        }
        return ServiceResponse.createByErrorMessage("密码更新失败");
    }


    @Override
    public ServiceResponse<User> updateUserInfo(User user) {
        //username不能被更新
        //email也要进行校验，校验新的email是否已经存在，并且存在的email若是相同，不能是我们当前的用户
        int count = userDao.checkEmailByUserID(user.getEmail(), user.getId());
        if (count > 0) {
            return ServiceResponse.createByErrorMessage("请更换eamil仔尝试更新");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        updateUser.setPhone(user.getPhone());

        int updateCount = userDao.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServiceResponse.createBySucess("个人信息更新成功", updateUser);
        }
        return ServiceResponse.createByErrorMessage("个人信息更新失败");
    }

    @Override
    public ServiceResponse<User> getInformation(Integer userId) {
        User user = userDao.selectByPrimaryKey(userId);
        if (user == null) {
            return ServiceResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServiceResponse.createBySucess(user);
    }


    //backend

    /**
     * 校验是否是管理员
     * @param user
     * @return
     */
    @Override
    public ServiceResponse checkAdminRole(User user) {
        if (user.getRole().intValue() == Const.Role.ROLE_admin) {
            return ServiceResponse.createBySucess();
        }else {
            return ServiceResponse.createByError();
        }

    }
}


