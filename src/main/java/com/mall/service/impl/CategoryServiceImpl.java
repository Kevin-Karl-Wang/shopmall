package com.mall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mall.common.ServiceResponse;
import com.mall.dao.CategoryDao;
import com.mall.model.Category;
import com.mall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by kevin on 2017/7/14.
 */
@Service("categoryService")
public class CategoryServiceImpl implements ICategoryService {

    private static Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryDao categoryDao;

    @Override
    public ServiceResponse addCategory(String categroyName, Integer parentId) {
        if (StringUtils.isBlank(categroyName) || parentId == null) {
            return ServiceResponse.createByErrorMessage("参数错误");
        }
        Category category = new Category();
        category.setName(categroyName);
        category.setParentId(parentId);
        category.getStatus(true);

        int insertRow = categoryDao.insert(category);
        if (insertRow > 0) {
            return ServiceResponse.createBySucessMessage("添加品类成功");
        }
        return ServiceResponse.createByErrorMessage("添加品类失败");
    }

    @Override
    public ServiceResponse updateCategoryName(Integer categroyId, String categoryName) {
        if (categroyId == null || StringUtils.isBlank(categoryName)) {
            return ServiceResponse.createByErrorMessage("更新品类参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setId(categroyId);

        int conut = categoryDao.updateByPrimaryKeySelective(category);
        if (conut > 0) {
            return ServiceResponse.createBySucessMessage("更新品类名字成功");
        }
        return ServiceResponse.createByErrorMessage("更新品类名字失败");
    }

    @Override
    public ServiceResponse<List<Category>> getPrallelChildrenCategory(Integer categroyId) {
        if (categroyId == 0) {
            return ServiceResponse.createByErrorMessage("查询子节点信息参数错误");
        }
        List<Category> categories = categoryDao.selectCategroyChildrenByParentId(categroyId);
        if (CollectionUtils.isEmpty(categories)) {
            logger.info("未找到当前分类的子分类");
        }
        return ServiceResponse.createBySucess(categories);
    }

    @Override
    public ServiceResponse getCategoryAndChidrenById(Integer categroyId) {
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet, categroyId);

        List<Integer> categroyIdList = Lists.newArrayList();
        if (null != categroyId) {
            for (Category category : categorySet) {
                categroyIdList.add(categroyId);
            }
        }

        return ServiceResponse.createBySucess(categroyIdList);
    }

    //递归算法算出子节点
    private Set<Category> findChildCategory(Set<Category> categorySet, Integer categroyId) {
        Category category = categoryDao.selectByPrimaryKey(categroyId);
        if (category != null) {
            categorySet.add(category);
        }
        //查找子节点，递归算法一定要有一个退出条件
        List<Category> categories = categoryDao.selectCategroyChildrenByParentId(categroyId);
        for (Category category1 : categories) {
            findChildCategory(categorySet, category1.getId());
        }
        return categorySet;

    }
}
