app.service("uploadService",function ($http) {
    this.uploadFile = function () {
        //HTML5新增的表单数据对象
        var formData = new FormData();
        formData.append("file",file.files[0]);//file 文件上传框的name,files[0]取第一个文件
        return $http({
            method : 'post',
            url : "../upload.do",
            data : formData,
            //angularJs默认提交方式是application/json,设置为未定义浏览器会设置成multiPart/formData
            headers : {'Content-Type':undefined},
            //对表单进行二进制序列化
            transformRequest : angular.identity
        });
    }
    
})