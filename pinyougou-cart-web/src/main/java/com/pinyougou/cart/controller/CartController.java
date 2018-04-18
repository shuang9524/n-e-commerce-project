package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.common.CookieUtil;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.entity.Result;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference(timeout = 6000)
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    /**
     * 从cookie中查询购物车
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(userName);//从cookie中查找购物车
        String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        //如果cookie中没有,创建一个空的购物车
        if (cartListString == null || cartListString == ""){
            cartListString = "[]";
        }
        List<Cart> cookieCartList = JSON.parseArray(cartListString, Cart.class);
        if (userName.equals("anonymousUser")){//未登录
            return cookieCartList;
        }else {//已登录
            //从Redis中获取购物车
            List<Cart> cartListFromRedis = cartService.findCartListFromRedis(userName);
            if (cookieCartList.size()>0){//如果cookie中有数据
                //将其合并到Redis购物车
                List<Cart> mergeCartList = cartService.mergeCartList(cookieCartList, cartListFromRedis);
                //然后删除cookie中的购物车
                CookieUtil.deleteCookie(request,response,"cartList");
                //登陆后将cookie和Redis合并
                cartService.saveCartListToRedis(userName,mergeCartList);
            }
            return cartListFromRedis;
        }
    }

    /**
     * 添加商品至购物车列表
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:9105")//springMVC4.2版本后支持注解,允许跨域请求,默认缺省值是true允许操作cookie
    public Result addGoodsToCartList(Long itemId,Integer num){
//        response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
//        response.setHeader("Access-Control-Allow-Credentials", "true");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            //从cookie中拿到购物车
            List<Cart> cartList = findCartList();
            //添加商品
            List<Cart> cartListString = cartService.addGoodsToCartList(cartList, itemId, num);
            if (username.equals("anonymousUser")){//如果未登录
                //存入cookie
                CookieUtil.setCookie(request,response,"cartList",JSON.toJSONString(cartListString),3600*24,"UTF-8");
            }else {//已登录
                cartService.saveCartListToRedis(username,cartList);
            }
            return new Result(true,"success");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"failed");
        }
    }
}
