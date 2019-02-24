package com.mmall.service.Impl;

import com.mmall.common.Const;
import com.mmall.common.ServiceResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import jdk.nashorn.internal.parser.Token;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServiceResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUserName(username);
        if(resultCount == 0)
        {
            return ServiceResponse.createByErrorMessage("用户名不存在");
        }
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        //todo 密码登录MD5

        User user = userMapper.selectLogin(username, md5Password);
        if(user == null)
        {
            return ServiceResponse.createByErrorMessage("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServiceResponse.createBySuccess("登录成功", user);
    }

    public ServiceResponse<String> register(User user)
    {
        ServiceResponse<String> Vaild = this.checkValid(user.getUsername(), Const.USERNAME);
        if(!Vaild.isSuccess())
        {
            return Vaild;
        }
        Vaild = this.checkValid(user.getEmail(), Const.EMAiL);
        if(!Vaild.isSuccess())
        {
            return Vaild;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);

        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        System.out.println(user);
        int resultCount = userMapper.insert(user);

        if(resultCount == 0)
        {
            return  ServiceResponse.createByErrorMessage("注册失败");
        }
        return ServiceResponse.createBySuccessMessage("注册成功");
    }

    public ServiceResponse<String> checkValid(String str, String type)
    {
        if (StringUtils.isNotBlank(type))
        {
            if(Const.USERNAME.equals(type))
            {
                int resultCount = userMapper.checkUserName(str);
                if(resultCount > 0)
                {
                    return ServiceResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(Const.EMAiL.equals(type))
            {
                int resultCount = userMapper.checkEmail(str);
                if(resultCount > 0)
                {
                    return ServiceResponse.createByErrorMessage("email已存在");
                }
            }
        }else
        {
            return ServiceResponse.createByErrorMessage("参数错误");
        }
        return ServiceResponse.createBySuccessMessage("校验成功");
    }

    public ServiceResponse<String> selectQuestion(String username)
    {
        ServiceResponse ValidResponse = this.checkValid(username, Const.USERNAME);
        if(ValidResponse.isSuccess())
        {
            //用户不存在
            return ServiceResponse.createByErrorMessage("用户不存在");
        }

        String question = userMapper.selectQuestionByUsername(username);

        if(StringUtils.isNotBlank(username))
        {
            return ServiceResponse.createBySuccess(question);
        }

        return ServiceResponse.createByErrorMessage("找回密码的问题是空的");
    }

    public ServiceResponse<String> checkAnswer(String username, String question, String answer)
    {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if(resultCount > 0)
        {
            String forgetToken = UUID.randomUUID().toString();
            System.out.println(forgetToken);
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            System.out.println(TokenCache.getKey(TokenCache.TOKEN_PREFIX + username));
            return ServiceResponse.createBySuccess(forgetToken);
        }
        return ServiceResponse.createByErrorMessage("问题答案错误");
    }

    public ServiceResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken)
    {
        if(StringUtils.isBlank(forgetToken))
        {
            return ServiceResponse.createByErrorMessage("参数错误, token需要传递");
        }
        ServiceResponse ValidResponse = this.checkValid(username, Const.USERNAME);
        if(ValidResponse.isSuccess())
        {
            //用户不存在
            return ServiceResponse.createByErrorMessage("用户不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        System.out.println("token=" + token);
        if(StringUtils.isBlank(token))
        {
            return ServiceResponse.createBySuccessMessage("token无效或者过期");
        }

        if(StringUtils.equals(forgetToken, token))
        {
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username, md5Password);

            if(rowCount > 0){
                return  ServiceResponse.createBySuccessMessage("修改密码成功");
            }
        }else {
            return ServiceResponse.createByErrorMessage("token错误, 请重新获取重置密码的token");
        }
        return ServiceResponse.createByErrorMessage("修改密码失败");
    }

    public ServiceResponse resetPassword(String passwordOld, String passwordNew, User user)
    {
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if(resultCount == 0){
            return ServiceResponse.createByErrorMessage("旧密码错误");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0)
        {
            return ServiceResponse.createBySuccessMessage("密码更改成功");
        }
        return ServiceResponse.createByErrorMessage("密码修改失败");
    }

    public ServiceResponse<User> updateInformation(User user)
    {
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if(resultCount > 0)
        {
            return ServiceResponse.createByErrorMessage("email已存在");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0)
        {
            return ServiceResponse.createBySuccess("更新个人信息成功", updateUser);
        }

        return ServiceResponse.createByErrorMessage("更新失败");
    }

    public ServiceResponse<User> getInformation(Integer userId)
    {
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null)
        {
            return ServiceResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServiceResponse.createBySuccess(user);
    }

    //backnd

    public ServiceResponse checkAdminRole(User user)
    {
        if(user != null && user.getRole() == Const.Role.ROLE_ADMIN)
        {
            return ServiceResponse.createBySuccess();
        }else
        {
            return ServiceResponse.createByError();
        }
    }

}
