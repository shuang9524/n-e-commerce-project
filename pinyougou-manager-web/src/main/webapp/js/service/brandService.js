app.service("brandService",function ($http) {
    this.findAllBrands = function () {
        return $http.get("../findAllBrands.do");
    }

    this.findPage = function (page, rows) {
        return $http.get("../findPage.do?page="+page+"&rows="+rows);
    }

    this.save = function (entity) {
        return $http.post("../save.do",entity);
    }

    this.update = function (entity) {
        return $http.post("../update.do",entity);
    }

    this.findOne = function (id) {
        return $http.get("../findOne.do?id="+id);
    }

    this.delete = function (ids) {
        return  $http.get("../delete.do?ids="+ids);
    }

    this.search = function (page,rows,searchEntity) {
        return $http.post("../search.do?page="+page+"&rows="+rows,searchEntity);
    }

    this.selectOptionList = function () {
        return $http.get("../selectOptionList.do");
    }
});