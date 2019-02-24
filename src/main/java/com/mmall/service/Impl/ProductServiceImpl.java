package com.mmall.service.Impl;

import com.mmall.common.ServiceResponse;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    public ServiceResponse saveOrUpdateProduct(Product product)
    {
        if(product != null)
        {
            if(StringUtils.isNotBlank(product.getSubImages()))
            {
                String[] strings = product.getSubImages().split(",");
                if(strings.length > 0)
                {
                    product.setMainImage(strings[0]);
                }
            }

            if(product.getId() != null)
            {
                int rowCount;
                rowCount = productMapper.updateByPrimaryKey(product);
                if(rowCount > 0)
                {
                    return ServiceResponse.createBySuccessMessage("更新成功");
                }else{
                    return ServiceResponse.createByErrorMessage("更新失败");
                }
            }else
            {
                int rowCount;
                rowCount = productMapper.insert(product);
                if(rowCount > 0)
                {
                    return ServiceResponse.createBySuccessMessage("添加成功");
                }else{
                    return ServiceResponse.createByErrorMessage("添加失败");
                }
            }
        }
        return ServiceResponse.createByErrorMessage("参数有误");
    }

    public ServiceResponse getSaleStatus(Integer productId, Integer status)
    {
        if(productId == null || status == null)
        {

        }
    }
}
