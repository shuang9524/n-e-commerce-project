app.controller("brandController",function($scope, $controller, brandService){

    $controller("baseController",{$scope:$scope});

    $scope.findAllBrands = function () {
        brandService.findAllBrands().success(function(data){
            $scope.brands = data;
        });
    }


    $scope.findPage=function (page , rows) {
        brandService.findPage(page, rows).success(function (pageData) {
            $scope.brands = pageData.rows;
            $scope.paginationConf.totalItems = pageData.total;
        });
    };

    $scope.save = function () {
        var object = null;
        if ($scope.entity.id != null){
            object = brandService.update($scope.entity);
        }else {
            object = brandService.save($scope.entity);
        }
        object.success(function (response) {
            if(response.success){
                alert(response.message);
                $scope.reloadList();
            }else {
                alert(response.message);
            }
        })
    }

    $scope.findOne = function (id) {
        brandService.findOne(id).success(function (response) {
            $scope.entity = response;
        })
    }

    $scope.delete = function () {
        brandService.delete($scope.selectIds).success(function (response) {
            if(response.success){
                alert(response.message);
                $scope.reloadList();
            }else {
                alert(response.message);
            }
        })
    }

    $scope.searchEntity = {};

    $scope.search = function (page, rows) {
        brandService.search(page, rows, $scope.searchEntity).success(function (pageData) {
            $scope.brands = pageData.rows;
            $scope.paginationConf.totalItems = pageData.total;
        });
    }

});