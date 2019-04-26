/** 定义搜索控制器 */
app.controller("searchController" ,function ($scope, $sce, $location, baseService) {

    //搜索参数
    $scope.searchParam = {keywords : '', category : '', brand : '', price : '',
        spec : {}, page : 1, rows : 10, sortField : '', sort : ''};
   //定义搜索方法
    $scope.search = function () {
        //发送异步请求
        baseService.sendPost("/Search", $scope.searchParam).then(function (response) {
            /** 获取搜索结果 */
            $scope.resultMap = response.data;

            //页面显示的关键字
            $scope.keyword = $scope.searchParam.keywords;

            //调用生成页码的方法
            initPageNum();
        });
    };

    /** 生成页码的方法 */
    var initPageNum = function () {
        /** 定义页码数组*/
        $scope.pageNums = [];
        /** 获取总页数 */
        var totalPages = $scope.resultMap.totalPages;
        /** 开始页码 */
        var firstPage = 1;
        /** 结束页码 */
        var lastPage = totalPages;

        /** 前面有点 */
        $scope.firstDot = true;
        /** 后面有点 */
        $scope.lastDot = true;

        /** 如果总页数大于5,显示部分页码 */
        if (totalPages > 5){
            /** 如果当前页码处于前面位置 */
            if ($scope.searchParam.page <= 3){
                lastPage = 5;  //生成5页页码
                $scope.firstDot = false; //前面没点
            }
            /** 如果当前页码处于后面位置 */
            else if ( $scope.searchParam.page >= totalPages - 3){
                firstPage = totalPages - 4; //生成5页页码
                $scope.lastDot = false;  //后面没点
            }else {  /** 当前页码处于中间位置 */

            firstPage = $scope.searchParam.page -2;
            lastPage = $scope.searchParam.page +2;
            }
        }else {
            /** 前面没点 */
            $scope.firstDot = false;
            /** 后面没点 */
            $scope.lastDot = false;
        }
        /** 循环产生页码 */
        for (var i = firstPage; i<=lastPage; i++){
            $scope.pageNums.push(i);
        }
    };

    //把HTML格式的字符串转化成html标签
    $scope.trustHtml = function (html) {
        return $sce.trustAsHtml(html);
    };

    //添加过滤条件
    $scope.addSearchItem =function (key,value) {
        //判断key是商品分类、品牌、价格
        if (key=='category' || key=='brand' || key=='price'){
            $scope.searchParam[key] = value;
        }else {
            $scope.searchParam.spec[key] = value;
        }
        //执行搜索方法
        $scope.search();
    };

    //删除过滤条件
    $scope.removeSearchItem = function (key) {
        //判断key是商品分类、品牌、价格
        if (key=='category' || key=='brand' || key=='price'){
            $scope.searchParam[key] = '';
        }else {
            /** 删除规格选项 */
            // delete 删除json对象中的key value对
           delete $scope.searchParam.spec[key];
        }
        //执行搜索方法
        $scope.search();
    };

    /** 根据分页搜索方法 */
    $scope.pageSearch = function (page) {
        page = parseInt(page);
        /** 页码验证 */
        if (page >= 1 && page <= $scope.resultMap.totalPages &&
            page != $scope.searchParam.page){
            //当前页码
            $scope.searchParam.page = page;
            //跳转的页码
            $scope.jumpPage = page;
            //执行搜索
            $scope.search();
        };
    };

    /** 定义排序搜索方法 */
    $scope.sortSearch = function (sortField , sort) {
        $scope.searchParam.sortField = sortField;
        $scope.searchParam.sort = sort;
        //当前页码
        $scope.searchParam.page = 1;
        $scope.search();
    };

    /** 获取检索关键字 */
    $scope.getkeywords = function () {
        $scope.searchParam.keywords = $location.search().keywords;
        $scope.search();
    };
});
