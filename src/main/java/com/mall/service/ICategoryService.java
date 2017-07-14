package com.mall.service;

import com.mall.common.ServiceResponse;

/**
 * Created by kevin on 2017/7/14.
 */
public interface ICategoryService {

    ServiceResponse addCategory(String categroyName,Integer parentId);

    ServiceResponse updateCategoryName(Integer categroyId, String categoryName);

    ServiceResponse getPrallelChildrenCategory(Integer categroyId);

    ServiceResponse getCategoryAndChidrenById(Integer categroyId);
}
