
let now = new Date();
let token = '';
let sign = ''
let code = ''
let openid = ''
let baseUrl = 'http://localhost/';
function getCode(){
    window.location.href = baseUrl+"wechat/getCode";
}
function getOpenid(code){
    $.ajax({
        url:baseUrl+"wechat/getOpenid?code="+code,
        type:'get',
        success:function(res){
            console.log(res);
            if (res.code == 200){
                openid = res.message;
            }
            $.cookie('openid',openid);
        }
    })
}

function getAccessToken(){
    $.ajax({
        url:baseUrl+"wechat/getAccessToken",
        type:'get',
        success:function(res){
            if (res.code == 200){
                this.token = res.message;
                getSign(res.message);
            }
        }
    })
}
function getSign(token){
    let url = location.href.split('#')[0];
    console.log(url);
    $.ajax({
        url:baseUrl+"wechat/getJsApiSign?token="+token+"&timestamp="+now.getTime()+"&url="+url,
        type:'get',
        success:function(res){
            if (res.code == 200){
                sign = res.message;
                config();
            }
        }
    })
}

function config(){
    wx.config({
        debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
        appId: 'wxc79cfaa1daf98915', // 必填，公众号的唯一标识
        timestamp: now.getTime(), // 必填，生成签名的时间戳
        nonceStr: 'wechat123', // 必填，生成签名的随机串
        signature: sign,// 必填，签名
        jsApiList: ['scanQRCode','chooseImage','uploadImage'] // 必填，需要使用的JS接口列表
    });
}
getAccessToken();

wx.ready(function(){
    console.log('ready');
    // chooseImage();
    // qrScan();
    // config信息验证后会执行ready方法，所有接口调用都必须在config接口获得结果之后，config是一个客户端的异步操作，所以如果需要在页面加载时就调用相关接口，则须把相关接口放在ready函数中调用来确保正确执行。对于用户触发时才调用的接口，则可以直接调用，不需要放在ready函数中。
});

wx.error(function(res){
    console.log('error',res);
    // config信息验证失败会执行error函数，如签名过期导致验证失败，具体错误信息可以打开config的debug模式查看，也可以在返回的res参数中查看，对于SPA可以在这里更新签名。
});

function qrScan(){
    wx.scanQRCode({
        needResult: 1, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
        scanType: ["qrCode","barCode"], // 可以指定扫二维码还是一维码，默认二者都有
        success: function (res) {
            var result = res.resultStr; // 当needResult 为 1 时，扫码返回的结果
            console.log(result);
        }
    });
}
function chooseImage(){
    console.log('chooseImage');
    wx.chooseImage({
        count: 1, // 默认9
        sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
        sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
        success: function (res) {
            var localIds = res.localIds; // 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
        }
    });
}
function uploadImage(){
    wx.uploadImage({
        localId: '', // 需要上传的图片的本地ID，由chooseImage接口获得
        isShowProgressTips: 1, // 默认为1，显示进度提示
        success: function (res) {
            var serverId = res.serverId; // 返回图片的服务器端ID
        }
    });
}