app.service("contentService",function ($http) {
    //查询首页活动
    this.findByCategoryId = function (categoryId) {
        return $http.get('content/findCategoryById.do?id='+categoryId);
    }
})