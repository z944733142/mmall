package com.mmall.service.Impl;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServiceResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    private Logger logger = LoggerFactory.getLogger(Category.class);
    public ServiceResponse addCategory(String categoryName, Integer parentId)
    {
        if(parentId == null || StringUtils.isBlank(categoryName))
        {
            return ServiceResponse.createByErrorMessage("参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);

        int rowCount = categoryMapper.insert(category);
        if(rowCount > 0)
        {
            return ServiceResponse.createBySuccessMessage("添加品类成功");
        }
        return ServiceResponse.createByErrorMessage("添加品类成功");
    }

    public ServiceResponse setCategoryName(Integer categoryId, String categoryName)
    {
        if(categoryId == null || StringUtils.isBlank(categoryName))
        {
            return ServiceResponse.createByErrorMessage("参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setId(categoryId);

        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount > 0)
        {
            return ServiceResponse.createBySuccessMessage("更新成功");
        }

        return ServiceResponse.createByErrorMessage("更新失败");
    }

    public ServiceResponse<List<Category>> getChildrenParallelCategory(Integer categoryId)
    {
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if(CollectionUtils.isEmpty(categoryList))
        {
            logger.info("未找到子分类");
        }
        return ServiceResponse.createBySuccess(categoryList);
    }

    public ServiceResponse selectCategoryAndChildrenById(Integer categoryId)
    {
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet, categoryId);

        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId != null)
        {
            for (Category category : categorySet) {
                categoryIdList.add(category.getId());
            }
        }
        return ServiceResponse.createBySuccess(categoryIdList);
    }

    public Set<Category> findChildCategory(Set<Category> categorySet, Integer categoryId)
    {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null)
        {
            categorySet.add(category);
        }

        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for (Category category1 : categoryList) {
            findChildCategory(categorySet, category1.getId());
        }
        return categorySet;
    }
}

