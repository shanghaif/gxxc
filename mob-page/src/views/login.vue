<template>
  <div id="login">
    <!--<md-button @click="imgCS" type="ghost-primary" style="width:100%;font-size: 26px;color:white;border-bottom-color:#19be6b">测试</md-button>-->
    <!--<div>-->
      <!--{{wxmess}}-->
      <!--<img :src="wxmess.serverId" alt="">-->
    <!--</div>-->

      <!-- logo区域 -->
      <div id="logo">
        <img src="static/icon/LOGO.png" width="120">
        <dt style="font-size: 28px;color: white">
          学 车 联 盟
        </dt>
      </div>
      <!-- 登录输入项区域 -->
      <div>
          <md-input-item
            ref="name"
            title="手机号"
            v-model="from.username"
            placeholder="请输入手机号"
            is-title-latent
            clearable
            style="border-bottom: 1px gray solid;margin: 20px;;margin-bottom: 5px"
          >
            <i class="iconfont icon-mobile" slot="left" style="font-size: 26px"></i>
          </md-input-item>
          <md-input-item
            ref="id"
            title="密码"
            v-model="from.password"
            placeholder="请输入登录密码"
            is-title-latent
            clearable
            type="password"
            style="border-bottom: 1px gray solid;margin: 20px;margin-top: 0px"
          >
            <i class="iconfont icon-lock" slot="left" style="font-size: 26px"></i>
          </md-input-item>
          <!--<md-input-item-->
            <!--ref="id"-->
            <!--title="邀请码"-->
            <!--v-model="yqm"-->
            <!--placeholder="邀请码"-->
            <!--is-title-latent-->
            <!--clearable-->
            <!--type="text"-->
            <!--style="border-bottom: 1px gray solid;margin: 20px;margin-top: 0px"-->
          <!--&gt;-->
            <!--<i class="iconfont icon-lock" slot="left" style="font-size: 26px"></i>-->
          <!--</md-input-item>-->
      </div>
      <!-- 操作按钮区域 -->
      <div class="box-row">
        <div class="body-O"></div>
        <div style="margin-right: 20px;margin-bottom: 20px">
            <a style="color: #dddee1;font-size: 20px;vertical-align: top" @click="handleClick">忘记密码！</a>
        </div>
      </div>
      <div class="box-row">
        <div class="body-O" style="margin-right: 20px;margin-left: 20px">
          <md-button @click="login" type="ghost-primary" style="width:100%;font-size: 26px;color:white;border-bottom-color:#19be6b">登&nbsp;&nbsp;&nbsp;&nbsp;录</md-button>
        </div>
      </div>
      <div class="box-row">
        <div class="body-O" style="text-align: center;margin-top: 20px">
          <a style="color: #FFCC66;font-size: 24px;" @click="reg">创 建 账 号</a>
        </div>
      </div>
  </div>
</template>

<script>
  import { Button, InputItem, Icon, Toast } from 'mand-mobile'

  export default {
    name: 'home-view',
    components: {
      [Icon.name]: Icon,
      [Button.name]: Button,
      [InputItem.name]: InputItem
    },
    data(){
      return{
        wxmess:'',
        from:{
          username:'',
          password:''
        },
        yqm:''
      }
    },
    created(){
        let ISLOGIN = sessionStorage.getItem("ISLOGIN");
        if(ISLOGIN == null){
          let openid = localStorage.getItem("openid");
          this.wechatUtil.getAccessToken(openid);
          sessionStorage.setItem("ISLOGIN",true);
        }
        this.$store.commit('M_tabId', 'tab-home')
        // this.getUrlCode()
    },
    methods: {
      fet(){
        // this.wechatUtil.getCode()
        let authCode = this.wechatUtil.getQueryString("code");
        if (!authCode){
          this.wechatUtil.getCode();
        }else{
          this.wechatUtil.getOpenid(authCode,(res)=>{
            localStorage.setItem("openid",res);

            this.wechatUtil.getAccessToken();
          });
        }
      },

      handleClick() {
        Toast.succeed('操作成功');
      },
      login(){
        var v = this
        this.$http.post(this.apis.LOGIN,this.from).then((res)=>{
          if(res.code==200){
              localStorage.setItem('token',JSON.stringify(res.result.accessToken))
              this.util.userMess(v,()=>{
                v.$router.push({name:'Home'})
              })
          }else {
            Toast.failed(res.message)
          }
        }).catch((err)=>{
          console.log(err)
          console.log('登录出错了！！！')
        })
      },
      reg(){
        var v = this
        this.wechatUtil.qrScan((messtoback)=>{
          // alert('微信'+messtoback)
          // Toast.succeed('微信'+messtoback);
          v.codeyz(messtoback)
            // v.codeyz(v.yqm)
        })
      },
      getUrlCode(){
        // let urlCode = this.$route.jquery.wxCode
        alert(urlCode)
      },
      codeyz(val){
        var v = this
        // alert(val)
        this.$http.post(this.apis.CODEYZ,{'code':val}).then((res)=>{
          if(res.code==200){
            localStorage.setItem('yqm',val)
            v.$router.push("/reg");
          }else {
            Toast.info(res.message)
          }
        }).catch((err)=>{
          alert('失败'+err)
        })
      },
      imgCS(){
        var v = this
        this.wechatUtil.chooseImage((res)=>{
          console.log('本地图片获取参数返回',res)
          // v.wechatUtil.getLocalImgData(res,(val)=>{
          //   alert(val)
          //   v.wxmess=val
          //   console.log(val)
          // })
          v.wechatUtil.uploadImage(res,(val)=>{
            alert('uploadImage')
            alert(val)
            v.wxmess=val
          })
        })
      }
    }
  }
</script>

<style lang="less">
  #login{
    background-color: #2b85e4;
    width: 100%;
    padding: 0;
    margin: 0;
    height: 100%;

    #logo{
      text-align: center;
    }
    #logo img{
      position: relative;
      padding-top: 80px;
      left: 0;
      right: 0;
    }
    .md-input-item.is-title-latent .md-input-item-title{
      color: white;
    }
  }
</style>
