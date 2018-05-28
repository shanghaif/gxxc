import Vue from 'vue'
import Router from 'vue-router'
import Home from '@/views/home'

Vue.use(Router)
// 路由配置
const router = new Router({
  routes: [
    {
      path: '/',
      name: 'Home',
      component: Home,
      children:[
      ]
    },
    {
      path: '/login',
      name: 'Login',
      component: resolve => { require(['@/views/login.vue'], resolve); }
    }
  ]
});
router.beforeEach((to, from, next) => {
  next();
})

router.afterEach((to) => {
  //Util.openNewPage(router.app, to.name, to.params, to.query);
  window.scrollTo(0, 0);
});
export default router;
