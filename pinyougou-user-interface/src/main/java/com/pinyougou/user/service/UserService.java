package com.pinyougou.user.service;
import java.util.List;

import com.pinyougou.entity.PageResult;
import com.pinyougou.pojo.TbUser;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface UserService {

	/**
	 * 增加
	*/
	public void add(TbUser user);

	/**
	 * 生成短信验证码
	 * @param phone
	 */
	void createActiveCode(String phone);

	/**
	 * 校验验证码是否正确
	 * @param phone
	 * @param code
	 * @return
	 */
	Boolean checkSmsCode(String phone, String code);
	
}
