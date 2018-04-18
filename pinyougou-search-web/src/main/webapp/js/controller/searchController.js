app.controller("searchController",function ($scope, $location, searchService) {
    $scope.search = function () {
        $scope.searchMap.pageNum = parseInt($scope.searchMap.pageNum);
        searchService.search($scope.searchMap).success(
            function (response) {
            $scope.resultMap = response;
            buildPageLabel();
        })
    }

    //接收首页跳转过来的搜索
    $scope.indexSearch = function () {
        $scope.searchMap.keywords = $location.search()['keywords'];
        $scope.search();
    }


    //搜索包含品牌隐藏品牌列表
    $scope.keywordsIsBrand = function () {
        for (var i = 0 ;i < $scope.resultMap.brandList.length; i++){
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text) >= 0 ){
                return true;
            }
        }
        return false;
    }

    //价格排序
    $scope.sortSearch = function (sortFiled,sort) {
        $scope.searchMap.sortFiled = sortFiled;
        $scope.searchMap.sort = sort;
        $scope.search();
    }

    //分页数组
    buildPageLabel = function () {
        $scope.pageLabel = [];
        var firstPage = 1;//首页
        var lastPage = $scope.resultMap.totalPages;//尾页

        $scope.firstDot = true;
        $scope.lastDot = true;

        // if ($scope.pageLabel[0] == 1){
        //     $scope.firstDot = false;
        // }
        // if ($scope.pageLabel[4] == $scope.resultMap.totalPages){
        //     $scope.lastDot = false;
        // }

        if ($scope.resultMap.totalPages > 5){

            if ($scope.searchMap.pageNum <= 3){//前五页
                lastPage = 5;
                $scope.firstDot=false;//页码数组包含1,不需要显示点
            }else if ($scope.searchMap.pageNum >= $scope.resultMap.totalPages - 2){//后五页
                firstPage = $scope.resultMap.totalPages - 4;
                $scope.lastDot=false;//当前页面等于总记录数,不需要点
            }else {//当前页为中心的五页
                firstPage = $scope.searchMap.pageNum - 2;
                lastPage = $scope.searchMap.pageNum + 2;
            }

        }else {
            //总记录数小于5,不需要点
            $scope.firstDot=false;
            $scope.lastDot=false;
        }
        for (var i = firstPage;i <= lastPage;i++){
            $scope.pageLabel.push(i);
        }
    }
    
    //点击上一页下一页翻页
    $scope.queryPage = function (pageNum) {
        if (pageNum < 1 || pageNum > $scope.resultMap.totalPages){
            return;
        }
        $scope.searchMap.pageNum = pageNum;
        $scope.search();
    }
    
    //定义搜索分类
    $scope.searchMap = {'keywords':'','category':'','brand':'','spec':{},'price':'','pageNum':1,'pageRows':20,'sortFiled':'','sort':'','updatetime':''};
    $scope.addSearchItem = function (key,value) {
        if (key == 'category' || key == 'brand' || key == 'price'){
            $scope.searchMap[key] = value;
        }else {
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();//选择条件后查询
    }


    //移除搜索条件
    $scope.removeSearchItem = function (key) {
        if (key == 'category' || key == 'brand' || key == 'price'){
            $scope.searchMap[key] = "";//置为空
        }else {
           delete $scope.searchMap.spec[key];
        }
        $scope.search();//移除条件后条件后查询
    }

    //判断是否为第一页
    $scope.isStartPage = function () {
        if ($scope.searchMap.pageNum == 1){
            return true;
        } else {
            return false;
        }
    }

    //判断是否为最后一页
    $scope.isEndPage = function () {
        if ($scope.searchMap.pageNum == $scope.resultMap.totalPages){
            return true;
        } else {
            return false;
        }
    }

});