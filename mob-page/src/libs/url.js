module.exports = {
  wechat:{
      getCode:'/wechat/getCode',
      getOpenid:'/wechat/getOpenid',
      getAccessToken:'/wechat/getAccessToken',
      getJsApiSign:'/wechat/getJsApiSign',
  },
  getImgUrl:'http://47.98.39.45:9088/',
  LOGIN:'/app/login',//登录接口
  LOGOUT:'/app/logout',//退出用戶
  USERMESS:'/app/ptyh/get',//用户信息
  USERZH:'/app/zh/get',//账户余额
  CODEYZ:'/app/yzyym',//邀请码验证
  PHINECODE:'/app/sendSMSzc',//获取短信验证码
  YZDX:'/app/validateSms',//短信验证
  USERSAVE:'/app/ptyh/save',//用户注册
  SWIPER:'/app/hd/pager ',//轮播图
  TX:'/app/tx/save',//资金提现
  TEAM:'/app/user/pager',//我的团队
  IDRZ:'/app/ptyh/updatesm',//身份认证
  CHUSERMESS:'/app/ptyh/update',//昵称 头像修改
  UPWORLD:'app/ptyh/mdfPwd',//密码修改
  CPTYPE:'/app/cp/getcplx',//产品类别
  CPPAY:'/app/order/save',//产品支付
}