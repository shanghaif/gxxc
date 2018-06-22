import Vue from 'vue'
import Router from 'vue-router'
import routers from './routers'
import Util from '../libs/apis';
import {Toast } from 'mint-ui';
Vue.use(Router)

const router = new Router({
  routes: routers
})
router.beforeEach((to, from, next) => {
  Util.title(to.meta.title);
  let openid = localStorage.getItem("openid");
  // let openid = "123123";
  let ISLOGIN = sessionStorage.getItem("ISLOGIN");
  //   console.log(openicd);
  // 如果没有openid，则需要获取
  // openid == null ||
  // ISLOGIN == null&&
  if (ISLOGIN == null &&  to.path != '/index') {
    next({
      name: 'index'
    });
    return;
  }else if (ISLOGIN == null && to.path == '/index') {
    next();
  }else if (ISLOGIN != null){
    if(to.name=='Login'||to.name=='reg'||to.name=='forget'){
      next()
    }else if(to.name!='Login' && localStorage.getItem('userMess')){
      next()
    }else{
      Toast('用户信息丢失，请重新登录！')
      next({
        name: 'Login'
      });
    }
    //next();
  }
  // next();
})



export default router
