app.controller('itemController',function($scope,$http){
	//定义用户选择规格
	$scope.specItems = {};
	
	//商品数量+-
	$scope.addNum = function(x){
		$scope.num *= 1;
		$scope.num+=x;
		if($scope.num<1){
			$scope.num=1;
		}
	}
	
	//获取当前被选中的值
	$scope.selectedSpec = function(key,value){
		$scope.specItems[key] = value;
		findSku();
	}
	
	//判断是否被选中
	$scope.isSelected = function(key,value){
		if($scope.specItems[key] == value){
			return true;
		}else{
			return false;
		}
	}
	
	//定义默认sku
	$scope.sku = {};
	//加载默认SKU信息
	$scope.loadSku = function(){
		$scope.sku = skuList[0];
		$scope.specItems = JSON.parse(JSON.stringify($scope.sku.spec));
	}
	
	//选中的规格匹配SKU
	matchObject = function(object1,object2){
		for(var k in object1){
			if(object1[k] != object2[k]){
				return false;
			}
		}
		for(var k in object2){
			if(object2[k] != object1[k]){
				return false;
			}
		}
		return true;
	}
	
	//查询选中的规格
	findSku = function(){
		for(var i=0;i<skuList.length;i++){
			if(matchObject(skuList[i].spec,$scope.specItems)){
				$scope.sku = skuList[i];
				return;
			}
		}
		$scope.sku = {id:0,title:'-----',price:0};
	}

	$scope.addToCart = function () {
        //跨域请求
        $http.get('http://localhost:9107/cart/addGoodsToCartList.do?itemId='+$scope.sku.id+'&num='+$scope.num,{'withCredentials':true}).success(
        	function (response) {
				if (response.success){
					location.href='http://localhost:9107/cart.html';
				}
            }
		);
    }
	
});