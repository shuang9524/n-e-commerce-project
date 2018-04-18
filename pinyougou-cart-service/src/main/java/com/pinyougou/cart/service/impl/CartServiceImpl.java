package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车实现
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    /**
     * 添加至购物车
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.根据商品SKU ID查询SKU商品信息
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item == null){
            //说明商品不存在,防止系统抛空指针异常
            throw new RuntimeException("商品不存在");
        }
        if (!item.getStatus().equals("1")){
            //商品状态不为1,无效商品
            throw new RuntimeException("商品无效");
        }
        //2.获取商家ID
        String sellerId = item.getSellerId();
        //3.根据商家ID判断购物车列表中是否存在该商家的购物车
        Cart cart = searchCartByCartList(cartList, sellerId);
        //4.如果购物车列表中不存在该商家的购物车
        if (cart == null){
            //4.1 新建购物车对象
            cart = new Cart();
            cart.setSellerId(sellerId);//给新的购物车设置商家id
            cart.setSellerName(item.getSeller());//设置商家名称
            List<TbOrderItem> orderItemList = new ArrayList<>();
            TbOrderItem orderItem = createOrderItem(item, num);
            orderItemList.add(orderItem);
            cart.setOrderItemsList(orderItemList);//设置订单详情
            //4.2 将新建的购物车对象添加到购物车列表
            cartList.add(cart);
        }else {
            //5.如果购物车列表中存在该商家的购物车
            TbOrderItem orderItem = searchOrderItem(cart.getOrderItemsList(), itemId);
            // 查询购物车明细列表中是否存在该商品
            if (orderItem == null){
                //5.1. 如果没有，新增购物车明细
                orderItem = createOrderItem(item,num);
                cart.getOrderItemsList().add(orderItem);
            }else {
                //5.2. 如果有，在原购物车明细上添加数量，更改金额
                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setTotalFee( new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));
                //如果商品数量为0
                if (orderItem.getNum() < 0){
                    cart.getOrderItemsList().remove(orderItem);
                }
                //如果购物车为空
                if (cart.getOrderItemsList().size() < 0){
                    cartList.remove(cart);
                }
            }
        }
        return cartList;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 从缓存中取出购物车,如果为空new一个空的购物车
     * @param username
     * @return
     */
    @Override
    public List<Cart> findCartListFromRedis(String username) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if (cartList == null){
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    /**
     * 保存购物车至缓存
     * @param username
     * @param cartList
     */
    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(username,cartList);
    }

    /**
     * 合并
     * @param cookieCartList
     * @param redisCartList
     * @return
     */
    @Override
    public List<Cart> mergeCartList(List<Cart> cookieCartList, List<Cart> redisCartList) {
        for (Cart cart:cookieCartList){
            for (TbOrderItem orderItem:cart.getOrderItemsList()){
                redisCartList = addGoodsToCartList(redisCartList,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return redisCartList;
    }

    /**
     * 搜索SKU
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem searchOrderItem(List<TbOrderItem> orderItemList,Long itemId){
        for (TbOrderItem orderItem:orderItemList){
            if (orderItem.getItemId().longValue()==itemId){
                return orderItem;
            }
        }
        return null;
    }


    /**
     * 根据商家ID查询购物车列表是否存在该商家
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart searchCartByCartList(List<Cart> cartList, String sellerId){
        //购物车为空
        if (cartList == null){
            return null;
        }
        for (Cart cart:cartList){
            //购物车有该商家
            if (cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }

    /**
     * 创建订单详情
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item,Integer num){
        if(item == null ){
            return null;
        }
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setTitle(item.getTitle());
        //设置总金额 商品单价 * 商品数量
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
        return orderItem;
    }
}
