package com.pinyougou.user.service.impl;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.entity.PageResult;
import com.pinyougou.user.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.pojo.TbUserExample;
import com.pinyougou.pojo.TbUserExample.Criteria;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.*;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper userMapper;
	
	/**
	 * 增加
	 */
	@Override
	public void add(TbUser user) {
		user.setCreated(new Date());
		user.setUpdated(new Date());
		user.setPassword(DigestUtils.md5Hex(user.getPassword()));
		userMapper.insert(user);		
	}

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private Destination smsDestination;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Value("${template_code}")
	private String template_code;

	@Value("${sign_name}")
	private String sign_name;

	@Override
	public void createActiveCode(String phone) {
		//生成六位随机验证码
		String code = (long) (Math.random() * 1000000)+"";
		//将验证码存入缓存
		redisTemplate.boundHashOps("smsCode").put(phone,code);
		//将验证码发送到ActiveMQ
		jmsTemplate.send(smsDestination, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				MapMessage mapMessage = session.createMapMessage();
				mapMessage.setString("mobile",phone);//发送的手机号码
				mapMessage.setString("template_code",template_code);//模板
				mapMessage.setString("sign_name",sign_name);//签名
				Map map = new HashMap();
				map.put("code",code);
				mapMessage.setString("param", JSON.toJSONString(map));//验证码
				return mapMessage;
			}
		});
	}

	@Override
	public Boolean checkSmsCode(String phone, String code) {
		if (code == null || code == ""){
			return false;
		}
		String redisCode = (String) redisTemplate.boundHashOps("smsCode").get(phone);
		if (redisCode == null || redisCode == ""){
			return false;
		}
		if (!redisCode.equals(code)){
			return false;
		}
		return true;
	}


}
