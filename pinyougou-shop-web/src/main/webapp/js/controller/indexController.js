app.controller("indexController",function ($scope, $controller, loginService) {
    $scope.readLoginName = function () {
        loginService.loginName().success(
            function (response) {
                $scope.loginName = response.loginName;
            });
    }
});