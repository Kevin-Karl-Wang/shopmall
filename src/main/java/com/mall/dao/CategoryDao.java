package com.mall.dao;

import com.mall.model.Category;

import java.util.List;

public interface CategoryDao {

    int insert(Category record);

    int updateByPrimaryKeySelective(Category record);

    List<Category> selectCategroyChildrenByParentId(Integer parentId);

    Category selectByPrimaryKey(Integer id);




    int insertSelective(Category record);


    int updateByPrimaryKey(Category record);

    int deleteByPrimaryKey(Integer id);


}