package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manege/product")
public class ProductManegeController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;

    @RequestMapping("save.do")
    @ResponseBody
    public ServiceResponse productSave(HttpSession session, Product product)
    {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null)
        {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登录, 请先登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess())
        {
            return iProductService.saveOrUpdateProduct(product);
        }else {
            return ServiceResponse.createByErrorMessage("无权限");
        }
    }

    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServiceResponse SetSaleStatus(HttpSession session, Product product)
    {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null)
        {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登录, 请先登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess())
        {
            return iProductService.saveOrUpdateProduct(product);
        }else {
            return ServiceResponse.createByErrorMessage("无权限");
        }
    }
}
