package com.pinyougou.cart.service;

import com.pinyougou.pojogroup.Cart;

import java.util.List;

/**
 * 购物车接口
 */
public interface CartService {
    /**
     * 添加商品到购物车列表
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);

    /**
     * 根据用户名从Redis中查询购物车列表
     * @param username
     * @return
     */
    List<Cart> findCartListFromRedis(String username);

    /**
     * 保存购物车到Redis
     * @param username
     * @param cartList
     */
    void saveCartListToRedis(String username,List<Cart> cartList);

    /**
     * 合并购物车
     * @param cookieCartList
     * @param redisCartList
     * @return
     */
    List<Cart> mergeCartList(List<Cart> cookieCartList,List<Cart> redisCartList);
}
