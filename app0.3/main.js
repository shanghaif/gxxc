import app from './app.ui'
import '#/static/css/box.less'
let options = {
  app: app,
  beforeEach(to,from,next) {
    let um = localStorage.getItem('usermess')
    try{
      if(!um){
        ui.getApp().userMess = JSON.parse(localStorage.getItem('usermess'))
      }
    }catch(e){ }
    
    if(to.path=='/pages/login' || to.path=='/pages/reg' || to.path=='/pages/retrieveworld' || to.path=='/pages/Welcome' ){
      next()
      return
    }else if(!um){
      ui.showToast({ title: '用户信息丢失，请重新登录！' })
      next({
        path:'/pages/login'
      })
    }else{
      next()
    }
    console.log('去',to)
    console.log('来',from)
  }
}

ui.extend({
    // 微信js方法
    getWxJs(){
        // 微信js初始化 
        var script = document.createElement("script")
        script.type = "text/javascript"
        script.src="./static/utils/jweixin-1.2.0.js"
        document.body.appendChild(script)

        script.onload = function(){ // 微信js初始化 回调函数
            console.log('*****wx',wx)
            
            // alert("wx1")
            // 微信js初始化成功后引用 微信功能方法
            ui.getApp().wxUtil = require('./static/ajax/wechatUtil.js').default
            // alert("wx2")
            //获取Code 直
            let authCode = ui.getApp().wxUtil.getQueryString("code");
            console.log('获取code',authCode)
            // alert('获取code=      '+authCode);
            if(authCode){
              
              // 获取Openid
              ui.getApp().wxUtil.vueParent = this;
              ui.getApp().wxUtil.getOpenid(authCode,(res)=>{
                  console.log('openid-------',res)
                  // alert("wx3+"+res);
                  localStorage.setItem("openid",res);//存储openid
                  ui.getApp().wxUtil.initConfig();//执行 微信 config
              });
            }else{
              return 
            }
        }
    },
    projectType(){//获取项目类型 true 微信  false app
      return localStorage.getItem('projectType')
    },
    getUser(){//获取用户信息
      return JSON.parse(localStorage.getItem('usermess'))
    },
    getUserMess(callback){//网络数据请求 获取用户信息
      this.$http('POST',ui.getApp().apis.USERMESS,{},(res)=>{
        if(res.code==200 && res.result){
            console.log('用户信息',res)
            if(res.result.yhTx == ''){
              res.result.yhTx ='static/img/login/logo.png'
            }
          }
          localStorage.setItem('usermess',JSON.stringify(res.result))
          ui.getApp().userMess = res.result
          callback && callback(res.result)
        }
      )
    },

    $http(method,url,data,callback){//网路数据请求
      let accessTokenStr = localStorage.getItem("token");
      if(accessTokenStr != null && accessTokenStr != ''&& accessTokenStr!=undefined){
        let tokMess = JSON.parse(accessTokenStr)
        ui.getApp().Ajax.header.token = tokMess.token
        ui.getApp().Ajax.header.userId = tokMess.userId
      }
      let openid = localStorage.getItem("openid");
      if(openid){
        ui.getApp().Ajax.header.openid = openid
      }

      ui.request({
        // ui.getApp().Ajax.url+':'+ui.getApp().Ajax.port+
        url: url, //仅为示例，并非真实的接口地址
        data: data,
        header: ui.getApp().Ajax.header,
        method:method,
        success: function (res) {
          console.log('请求成功')
          if(res.data.code ==403){
            ui.showToast({ title: res.data.message})
            ui.redirectTo({
              url: '/pages/login'
            })
          }else{
            callback && callback(res.data);
          }
        },
        fail:function(err){
          console.log('请求失败')
          callback && callback(err.data);
        },
        complete:function(mess){
          console.log('请求结果')
          // callback && callback(mess.data);
        }
      })
    },
    getDateTime(){
			var NowDate = new Date
			let Year = NowDate.getFullYear()
			let Month = NowDate.getMonth()+1
			let Day = NowDate.getDate()
			let Hours = NowDate.getHours()
			let Minutes = NowDate.getMinutes()
			let Seconds = NowDate.getSeconds()
			let time = Year+'-'+Month+'-'+Day+' '+Hours+':'+Minutes+':'+Seconds
			return time
		},



  sayHello(){
    console.log('全局方法参数',ui.getApp().Ajax)
  },
  goBack(){
    ui.navigateBack()
  },
  pageHeight(val){
    return  ui.DEFAULT_CONTENT_HEIGHT + val
            // default content height
  },
  getdateStr(Y){
    var NowDate = new Date
    let Year = NowDate.getFullYear()+Y
    let Month = NowDate.getMonth()+1
    let Day = NowDate.getDate()
    let Hours = NowDate.getHours()
    let Minutes = NowDate.getMinutes()
    let Seconds = NowDate.getSeconds()
    // +Seconds+'秒'
    let time = Year+'年'+Month+'月'+Day+'日'+Hours+'时'+Minutes+'分'
    return time
  },
})

ui.start(options)