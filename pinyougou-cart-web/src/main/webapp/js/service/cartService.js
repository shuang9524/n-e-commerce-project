//购物车服务层
app.service('cartService',function($http){
    //购物车列表
    this.findCartList=function(){
        return $http.get('cart/findCartList.do');
    }

    //添加商品到购物车
    this.addGoodsToCartList=function(itemId,num){
        return $http.get('cart/addGoodsToCartList.do?itemId='+itemId+'&num='+num);
    }

    //商品属性合计和商品总计
    this.num = function (cartList) {
        var totalValue = {totalNum:0,totalPrice:0.00};
        for (var i=0;i<cartList.length;i++){
            var cart = cartList[i];
            for (var j=0;j<cart.orderItemsList.length;j++){
                var orderItem = cart.orderItemsList[j];
                totalValue.totalNum += orderItem.num;
                totalValue.totalPrice += orderItem.totalFee;
            }
        }
        return totalValue;
    }
});