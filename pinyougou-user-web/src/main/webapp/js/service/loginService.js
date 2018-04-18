//服务层
app.service('loginService',function($http){

    //查找用户名
    this.findUserName = function () {
        return $http.get('../login/showUserName.do');
    }
});
