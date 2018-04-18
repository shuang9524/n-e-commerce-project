package com.pinyougou.user.controller;
import java.util.List;

import com.common.PhoneFormatCheckUtils;
import com.pinyougou.entity.PageResult;
import com.pinyougou.entity.Result;
import com.pinyougou.user.service.UserService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbUser;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference
	private UserService userService;

	/**
	 * 增加
	 * @param user
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbUser user,String smsCode){
		try {
			Boolean checkResult = userService.checkSmsCode(user.getPhone(), smsCode);
			if (checkResult == false){
				return new Result(false,"验证码错误");
			}
			userService.add(user);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}

	@RequestMapping("/sendCode")
	public Result sendCode(String phone){
		if (!PhoneFormatCheckUtils.isPhoneLegal(phone)){
			return new Result(false,"手机号错误,请重新输入!");
		}
		userService.createActiveCode(phone);
		return new Result(true,"验证短信已发送!");
	}
}
