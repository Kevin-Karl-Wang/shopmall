package com.mall.controller.backend;

import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServiceResponse;
import com.mall.model.User;
import com.mall.service.ICategoryService;
import com.mall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by kevin on 2017/7/14.
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService userService;

    @Autowired
    private ICategoryService categoryService;

    /**
     * 添加品类
     *
     * @param categoryName
     * @param parentId
     * @param session
     * @return
     */
    @RequestMapping("addCategory.do")
    @ResponseBody
    public ServiceResponse addCategory(String categoryName, @RequestParam(value = "parentId", defaultValue = "0") int parentId, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        //校验是否是管理员
        if (userService.checkAdminRole(user).isSuccess()) {
            return categoryService.addCategory(categoryName, parentId);
        } else {
            return ServiceResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    /**
     * 更新品类名字
     *
     * @param categroyId
     * @param categoryName
     * @param session
     * @return
     */
    @RequestMapping("setCategoryName.do")
    @ResponseBody
    public ServiceResponse setCategoryName(Integer categroyId, String categoryName, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        if (userService.checkAdminRole(user).isSuccess()) {
            //更新categoryName
            return categoryService.updateCategoryName(categroyId, categoryName);
        } else {
            return ServiceResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    /**
     * 获取子节点品牌
     *
     * @param categroyId
     * @param session
     * @return
     */
    @GetMapping("getPrallelChildrenCategory.do")
    @ResponseBody
    public ServiceResponse getPrallelChildrenCategory(@RequestParam(value = "categroyId", defaultValue = "0") Integer categroyId, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        if (userService.checkAdminRole(user).isSuccess()) {
            //查询子节点的categroy的信息，并且不递归，保持平级
            return categoryService.getPrallelChildrenCategory(categroyId);
        } else {
            return ServiceResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }


    /**
     * 获取子节点品牌和其本事的节点信息
     *
     * @param categroyId
     * @param session
     * @return
     */
    @GetMapping("getCategoryAndChidrenCategory.do")
    @ResponseBody
    public ServiceResponse getCategoryAndChidrenCategory(@RequestParam(value = "categroyId", defaultValue = "0") Integer categroyId, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        if (userService.checkAdminRole(user).isSuccess()) {
            //查询当前节点的id和递归子节点的id
            return categoryService.getCategoryAndChidrenById(categroyId);
        } else {
            return ServiceResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

}
