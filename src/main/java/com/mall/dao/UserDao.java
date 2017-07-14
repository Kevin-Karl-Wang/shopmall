package com.mall.dao;

import com.mall.model.User;
import org.apache.ibatis.annotations.Param;

public interface UserDao {


    int checkUserName(String username);

    User selectLogin(@Param("username") String username, @Param("password") String password);

    int checkEmail(String email);

    int insert(User record);

    String selectQuestionByUsername(String username);

    int checkAnswer(@Param("username") String username, @Param("question") String question, @Param("answer") String answer);

    int updatePasswordByUsername(@Param("username") String username, @Param("md5Password") String md5Password);

    int checkPassword(@Param("passwordOld") String passwordOld, @Param("userId") Integer userId);

    int updateByPrimaryKeySelective(User record);

    int checkEmailByUserID(@Param("email") String email, @Param("id") Integer id);

    User selectByPrimaryKey(Integer id);


//    int deleteByPrimaryKey(Integer id);
//
//
//    int insertSelective(User record);
//
//
//
//
//    int updateByPrimaryKey(User record);



}