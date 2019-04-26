/** 定义控制器层 */
app.controller('goodsController', function($scope, $controller, baseService){

    /** 指定继承baseController */
    $controller('baseController',{$scope:$scope});

    /** 添加或修改 */
    $scope.saveOrUpdate = function(){
        //获取富文本编辑器中的内容
        $scope.goods.goodsDesc.introduction = editor.html();

        /** 发送post请求 */
        baseService.sendPost("/goods/save", $scope.goods)
            .then(function(response){
                if (response.data){
                    /** 清空数据 */
                    $scope.goods = {};
                    /** 清空富文本编辑器中的内容 */
                    editor.html('');
                }else{
                    alert("操作失败！");
                }
            });
    };

    //定义文件上传
    $scope.uploadFile = function () {
        //调用服务层上传文件
        baseService.uploadFile().then(function (response) {
           //获取响应数据 (status :200, url : 'http://192.168.12.131/group/xxx/xx.jpg')
            if (response.data.status == 200){
                //获取图片url
                $scope.picEntity.url = response.data.url;
            }else {
                alert("文件上传失败!");
            }
        });
    };

    //$scope.goods.goodsDesc.itemImages = [{color:'',url:''},{}];
    //不能直接点出来, 要变成该格式需要先定义出来
    //定义数据格式
    $scope.goods = {goodsDesc : { itemImages : [], specificationItems : []}};

    //添加商品的单张图片到数组中
    $scope.addPic = function () {
        $scope.goods.goodsDesc.itemImages.push($scope.picEntity)
    };

    //从数组中删除图片
    $scope.removePic = function (idx) {
        $scope.goods.goodsDesc.itemImages.splice(idx,1);
    };


    //根据父级id查询商品分类
    $scope.findItemCatByParentId = function (parentId,name) {
      baseService.sendGet("/itemCat/findItemCatByParentId?parentId="
          + parentId).then(function (response) {
              //获取响应数据
          $scope[name] = response.data;
      });
    };

    // $scope.$watch: 它可以监控$scope中的变量发生改变,就会调用一个函数
    // $scope.$watch: 监控一级分类id,发生改变,查询二级分类
    // 可以使用ng-change ,效果相同
    $scope.$watch("goods.category1Id",function (newVal, oldVal) {
        //alert("新值: "+newVal+ "旧值: " + oldVal);
       if (newVal){  //newVal 值不是undefined、null 即为true
           //查询二级分类
           $scope.findItemCatByParentId(newVal,"itemCatList2");
       }else {
           $scope.itemCatList2=[];
       }
    });

    // $scope.$watch: 监控二级分类id,发生改变,查询三级分类
    $scope.$watch("goods.category2Id",function (newVal, oldVal) {
        //alert("新值: "+newVal+ "旧值: " + oldVal);
        if (newVal){  //newVal 值不是undefined、null 即为true
            //查询三级分类
            $scope.findItemCatByParentId(newVal,"itemCatList3");
        }else {
            $scope.itemCatList3=[];
        }
    });

    // $scope.$watch: 监控三级分类id,发生改变,查询类型模板id
    $scope.$watch("goods.category3Id",function (newVal, oldVal) {
        //alert("新值: "+newVal+ "旧值: " + oldVal);
        if (newVal){   //newVal 值不是undefined、null 即为true
            //迭代三级分类数组
            for (var i = 0; i < $scope.itemCatList3.length; i++){
                var itemCat = $scope.itemCatList3[i];
                if (itemCat.id == newVal){
                    //获取类型模板id
                    $scope.goods.typeTemplateId = itemCat.typeId;
                    break;
                }
            }

        }else {
            $scope.goods.typeTemplateId = null;
        }
    });

    // $scope.$watch: 监控类型模板id, 发生改变, 根据类型模板id, 查询类型模板对象
    $scope.$watch("goods.typeTemplateId",function (newVal, oldVal) {
        if (newVal){   //newVal 值不是undefined、null 即为true
            baseService.sendGet("/typeTemplate/findOne?id="+newVal).then(function (response) {
                //获取品牌数据
                $scope.brandIds =JSON.parse(response.data.brandIds);

                //获取扩展属性
                $scope.goods.goodsDesc.customAttributeItems = JSON.parse(response.data.customAttributeItems);
            });

            //根据类型模板id,查询规格选项数据
            baseService.sendGet("/typeTemplate/findSpecByTemplateId?id="
            + newVal).then(function (response) {
                //获取响应数据
                /**
                 * [{"id":27,"text":"网络","options" : [{},{}]},
                   {"id":32,"text":"机身内存","options" : [{},{}]}]
                 */
                $scope.specList = response.data;
            });

        }else {
            $scope.brandIds = [];
        }
    });

    //保存用户选中的规格选项
    $scope.updateSpecAttr = function ($event, specName, optionName) {
        //取$scope.goods.goodsDesc.specificationItems 这一列数组, specName界面传的参数值   obj 一个对象
        var obj = $scope.searchJsonFromArr($scope.goods.goodsDesc.specificationItems,specName);

        if (obj){  //如果obj不为空,则把这个数据添加进数组

            //判断checkbox是否选中
            if ($event.target.checked){  //选中
                obj.attributeValue.push(optionName);
            }else {   //没选中
                //获取该元素在数组中的索引号
               var idx = obj.attributeValue.indexOf(optionName);
               //从数组中删除一个元素
                obj.attributeValue.splice(idx,1);

                //判断数组长度是否为0
                if (obj.attributeValue.length == 0){
                    var idx = $scope.goods.goodsDesc.specificationItems.indexOf(obj);
                    //从specificationItems数组中删除元素
                    $scope.goods.goodsDesc.specificationItems.splice(idx,1);
                }
            }
        }else { //如果没有这个数组,则创建一个新的数组,并添加一个元素进去
            $scope.goods.goodsDesc.specificationItems.
            push({attributeValue : [optionName],attributeName:specName});
        }


    };

    /** 从数组中搜索一个json对象*/
    $scope.searchJsonFromArr = function (jsonArr, specName) {
        /**
         *  $scope.goods.goodsDesc.specificationItems =
         *  [{"attributeValue":["联通4G","移动4G","电信4G"],"attributeName":"网络"},
         *  {"attributeValue":["64G","128G"],"attributeName":"机身内存"}];
         */
        for (var i=0; i < jsonArr.length; i++){
            //{"attributeValue":["联通4G","移动4G","电信4G"],"attributeName":"网络"}
            var json = jsonArr[i];
            //json.attributeName == specName  判断数组中是否有界面传来的元素
            if (json.attributeName == specName){
                return json;
            }
        }
        return null;
    };

    //根据用户选中的规格选项生成SKU数组
    $scope.createItems = function () {

        /** 定义SKU数组变量,并初始化 */
        $scope.goods.items = [{spec:{},price:0,num:9999,status:'0',isDefault:'0'}];

        //获取用户选中的规格选项数组
        //[{"attributeValue":["联通4G","移动4G","电信4G"],"attributeName":"网络"}]
        var specItems = $scope.goods.goodsDesc.specificationItems;
        //迭代用户选中的规格选项数组,生成SKU数组
        for (var i = 0; i<specItems.length; i++){
            //获取一个数组元素
            //{"attributeValue":["联通4G","移动4G","电信4G"],"attributeName":"网络"}
            var json = specItems[i];

            //转化生成新的SKU数组
            $scope.goods.items = $scope.swapItems($scope.goods.items,
                json.attributeValue,json.attributeName);
        }
    };

    /** 对原来的SKU数组不断克隆,扩充,返回新的数组 */
    $scope.swapItems = function (items,attributeValue,attributeName) {
        //items: [{spec:{},price:0,num:9999,status:'0',isDefault:'0'}]
        //attributeValue:  {"联通4G","移动4G","电信4G"}
        //attributeName:  "网络"
        //定义新的SKU数组
        var newItems = [];
        for (var i = 0; i<items.length; i++){
            //获取数组中的元素SKU
            //{spec:{},price:0,num:9999,status:'0',isDefault:'0'}
            var item = items[i];

            //迭代 ["联通4G","移动4G","电信4G"]
            for (var j = 0 ; j < attributeValue.length; j++){
                // 对item克隆产生新的SKU   JSON.stringify(item)   转成字符串    然后在转成json对象
                var newItem = JSON.parse(JSON.stringify(item));
                //设置规格选项
                // spec: {"网络" : "电信4G" , "机身内存" : "64G" }
                newItem.spec[attributeName] = attributeValue[j];
                //添加到数组中
                newItems.push(newItem);
            }
        }

        return newItems;
    };


    /** 定义商品状态数组 */
    $scope.status = ['未审核','已审核','审核未通过','关闭'];




    /** 查询条件对象 */
    $scope.searchEntity = {};
    /** 分页查询(查询条件) */
    $scope.search = function(page, rows){
        baseService.findByPage("/goods/findByPage", page,
			rows, $scope.searchEntity)
            .then(function(response){
                /** 获取分页查询结果 */
                $scope.dataList = response.data.rows;
                /** 更新分页总记录数 */
                $scope.paginationConf.totalItems = response.data.total;
            });
    };



    /** 商品上下架 */
    $scope.updateMarketable = function(status){
        if ($scope.ids.length > 0){

                baseService.sendGet("/goods/updateMarketable?ids="+$scope.ids +"&status="+status)
                    .then(function(response){
                        if (response.data){
                            /** 重新加载数据 */
                            $scope.reload();
                            // 清空ids数组
                            $scope.ids = [];
                        }else{
                            alert("上下架失败！");
                        }
                    });


        }else{
            alert("请选择要上下架的商品！");
        }
    };
});