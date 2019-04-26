package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.common.util.HttpClientUtils;
import com.pinyougou.mapper.UserMapper;
import com.pinyougou.pojo.User;
import com.pinyougou.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;


/** 用户注册服务接口实现类 */
@Service(interfaceName = "com.pinyougou.service.UserService")
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Value("${sms.url}")
    private String smsUrl;
    @Value("${sms.signName}")
    private String signName;
    @Value("${sms.templateCode}")
    private String templateCode;

    @Override
    public void save(User user) {
        try {
            //创建日期
            user.setCreated(new Date());
            //修改日期
            user.setUpdated(user.getCreated());

            //密码加密
            user.setPassword(DigestUtils.md5Hex(user.getPassword()));
            userMapper.insertSelective(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(User user) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public User findOne(Serializable id) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public List<User> findByPage(User user, int page, int rows) {
        return null;
    }

     /** 发送短信验证码 */
    public boolean sendCode(String phone){
        try {
            /** 生成6位随机数*/
            String code = UUID.randomUUID().toString()
                    .replaceAll("-", "")
                    .replaceAll("[a-zA-Z]","")
                    .substring(0,6);
            System.out.println("验证码: " + code);
            /** 调用短信发送接口 */
            HttpClientUtils httpClientUtils = new HttpClientUtils(false);
            //创建Map集合封装请求参数
            HashMap<String, String> param = new HashMap<>();
            param.put("phone",phone);
            param.put("signName",signName);
            param.put("templateCode",templateCode);
            param.put("templateParam","{\"number\":\"" + code + "\"}");
            //发送Post请求
            String content = httpClientUtils.sendPost(smsUrl, param);
            //把json字符串转化成Map
            Map<String,Object> resMap = JSON.parseObject(content, Map.class);
            /** 存入Redis中*/
            redisTemplate.boundValueOps(phone).set(code, 30, TimeUnit.SECONDS);
            return (boolean) resMap.get("success");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** 检验短信验证码是否正确 */
    public boolean checkSmsCode(String phone, String smsCode){
        /** 获取Redis验证码是否正确 */
        String sysCode = redisTemplate.boundValueOps(phone).get();
        return StringUtils.isNoneBlank(sysCode)&&sysCode.equals(smsCode);
    }
}
