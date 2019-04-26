/** 定义控制器层 */
app.controller('userController', function($scope, $timeout, baseService){

    /** 定义用户对象 */
    $scope.user = {};

    /** 定义保存用户方法 */
    $scope.save =function () {
        if ($scope.user.password != $scope.password){
            alert("密码不一致,请重新输入! ");
            return;
        }
        baseService.sendPost("user/save?smsCode="+$scope.smsCode,$scope.user).then(function (response) {
            if (response.data){
                alert("注册成功!!");
                $scope.user = {};
                $scope.password = "";
                $scope.smsCode = "";
            }else {
                alert("注册失败!");
            }
        });
    };




    /** 发送短信验证码 */
    $scope.sendCode = function () {

        //alert($scope.user.phone);
        //发送异步请求
        if ($scope.user.phone && /^1[3|4|5|7|8]\d{9}$/.test($scope.user.phone)){
            baseService.sendGet("/user/sendCode?phone=" + $scope.user.phone)
                .then(function (response) {
                if (response.data){
                    //开启倒计时
                    //调用倒计时方法
                    $scope.downcount(30);
                }else {
                    alert("发送失败!");
                }
            });
        }else {
            alert("手机号码格式不正确!");
        }
    };

    $scope.smsTip = "获取短信验证码";
    $scope.disabled = false;
    //倒计时方法
    $scope.downcount =function (seconds) {

        seconds--;
        if (seconds>=0){
            $scope.smsTip = seconds+"秒后,重新获取!";
            $scope.disabled = true;

            //第一个参数: 回调函数
            //第二个参数: 间隔的时间毫秒数
            $timeout(function () {
                $scope.downcount(seconds);
            },1000);
        }else {
            $scope.smsTip = "获取短信验证码";
            $scope.disabled = false;
        }

    }
});