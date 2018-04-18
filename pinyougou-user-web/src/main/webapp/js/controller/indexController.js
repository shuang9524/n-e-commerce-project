app.controller('indexController',function($scope,loginService){
//用户名
$scope.findUserName = function () {
    loginService.findUserName().success(
        function (response) {
            $scope.loginUserName = response.userName;
        }
    );
}
});