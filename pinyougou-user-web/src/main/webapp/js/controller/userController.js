 //控制层 
app.controller('userController' ,function($scope,$controller   ,userService){	

	//注册
	$scope.reg = function () {
		if ($scope.entity.password != $scope.password){
			alert("两次密码不一致,请重新输入!");
			return;
		}
		userService.add($scope.entity,$scope.smsCode).success(
			function (response) {
				alert(response.message);
        })
    }

    //发送短信
	$scope.sendCode = function () {
        if ($scope.entity.phone == null || $scope.entity.phone == ""){
        	alert("手机号不能为空!");
        	return;
		}
		userService.sendCode($scope.entity.phone).success(
			function (response) {
				alert(response.message);
            }
		);
    }


});
