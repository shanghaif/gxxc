<style lang="less">
  @import "../../styles/box";
</style>
<template>
  <div class="box">
    <div class="box-row"style="background-color: #2d8cf0;height: 1.5rem;;line-height: 1.5rem;text-align: center">
          <div style="height: 1.5rem;width: 1.2rem;text-align: center;color: #ededed"
          @click="$router.back()">
            <i class="iconfont icon-left1"></i>
          </div>
          <div class="body-O" style="font-weight: 700;font-size: 0.5rem;color: #fff">
            缴费

          </div>
        <div style="height: 1.5rem;width: 1.2rem;text-align: center;">
        </div>
    </div>
    <div class="body md-example-child md-example-child-cashier">
      <md-field
        v-show="false"
        title="支付结果"
      >
        <md-radio
          v-model="cashierResult"
          :options="cashierResults"
        ></md-radio>
      </md-field>
      <md-field
      >
        <!--title="支付配置"-->
        <md-input-item
          title="支付金额"
          align="right"
          type="money"
          v-model="cashierAmount"
        >
        </md-input-item>
        <md-field-item
          v-show="false"
          title="发送验证码"
          align="right"
        >
          <md-switch v-model="isCashierCaptcha"></md-switch>
        </md-field-item>
      </md-field>
      <md-button @click="payMoney">{{ isCashierhow ? '立即支付' : '立即支付' }}</md-button>
      <md-cashier
        ref="cashier"
        v-model="isCashierhow"
        :channels="cashierChannels"
        :payment-amount="cashierAmount"
        payment-describe="共享时代"
        @select="onCashierSelect"
        @pay="onCashierPay"
        @cancel="onCashierCancel"
      ></md-cashier>
    </div>
  </div>
</template>

<script>
  import {Button, Radio, Field, FieldItem, InputItem, Switch, Cashier} from 'mand-mobile'
  import { Toast } from 'mint-ui';
  export default {
    name: 'cashier-demo',
    /* DELETE */
    height: 100,
    /* DELETE */
    components: {
      [Button.name]: Button,
      [Radio.name]: Radio,
      [Field.name]: Field,
      [FieldItem.name]: FieldItem,
      [InputItem.name]: InputItem,
      [Switch.name]: Switch,
      [Cashier.name]: Cashier,
    },
    data() {
      return {
        isCashierhow: false,
        isCashierCaptcha: false,//非否发送验证码
        cp:{},
        cashierAmount: '0.00',
        money:0,
        cashierResult: 'success',
        cashierResults: [
          {
            text: '支付成功',
            value: 'success',
          },
          {
            text: '支付失败',
            value: 'fail',
          },
        ],
        cashierChannels: [
          // {
          //   icon: 'cashier-icon-1',
          //   text: '招商银行储蓄卡(0056)支付',
          //   value: '001',
          // },
          // {
          //   icon: 'cashier-icon-2',
          //   text: '支付宝支付',
          //   value: '002',
          // },
          {
            icon: 'cashier-icon-3',
            text: '微信支付',
            value: '2',
          },
          // {
          //   icon: 'cashier-icon-4',
          //   text: 'QQ钱包支付',
          //   value: '004',
          // },
          // {
          //   icon: 'cashier-icon-5',
          //   text: '一网通支付',
          //   value: '005',
          // },
        ],
        payMess:{}
      }
    },
    watch:{
      money:function (n,o) {
      }
    },
    created(){
      this.getCPlist()
    },
    computed: {
      cashier() {
        return this.$refs.cashier
      },
    },
    methods: {
      doPay() {
        var v = this
        if (this.isCashierCaptcha) {
          this.cashier.next('captcha', {
            text: '验证码发送至 156 **** 8965',
            onSend: countdown => {
              console.log('[Mand Mobile] Send Captcha')
              this.sendCaptcha().then(() => {
                countdown()
              })
            },
            onSubmit: code => {
              console.log(`[Mand Mobile] Send Submit ${code}`)
              this.checkCaptcha(code).then(res => {
                if (res) {
                  this.createPay().then(() => {
                    this.cashier.next(this.cashierResult)
                  })
                }
              })
            },
          })
        } else {
          this.createPay().then((res) => {
            console.log('**********',res)
            this.cashier.next(this.cashierResult)

            //  *****************************
            this.$store.commit('CHjf', true);
            setTimeout(function () {
              v.$router.back()
            },1000*2.7)
          })
        }
      },
      // Create a pay request & check pay result
      createPay() {
        this.cashier.next('loading')
        return new Promise(resolve => {
          this.timer = setTimeout(() => {
            resolve()
          }, 3000)
        })
      },
      // Create a captcha sending request
      sendCaptcha() {
        return new Promise(resolve => {
          this.timer = setTimeout(() => {
            resolve()
          }, 200)
        })
      },
      // Create a captcha checking request
      checkCaptcha(code) {
        return new Promise(resolve => {
          this.timer = setTimeout(() => {
            resolve(!!code)
          }, 200)
        })
      },
      onCashierSelect(item) {
        console.log('支付渠道选中')
        console.log(`[Mand Mobile] Select ${JSON.stringify(item)}`)
      },
      onCashierPay(item) {
        var  v = this;
        WeixinJSBridge.invoke(
          'getBrandWCPayRequest', {
            "appId":v.payMess.appId,     //公众号名称，由商户传入
            "timeStamp":v.payMess.timeStamp,         //时间戳，自1970年以来的秒数
            "nonceStr":v.payMess.nonceStr, //随机串
            "package":v.payMess.package,
            "signType":v.payMess.signType,         //微信签名方式：
            "paySign":v.payMess.paySign //微信签名
          },
          function(res){
            console.log(res)
            if(res.err_msg=='get_brand_wcpay_request:ok'){
              v.cashierResult = 'success'
              v.doPay()
            }else if(res.err_msg=='get_brand_wcpay_request::fail'){
              v.cashierResult = 'fail'
              v.doPay()
            }else if(res.err_msg=='get_brand_wcpay_request:cancel'){
              v.isCashierhow = !v.isCashierhow
              Toast('支付取消')
            }
            // if(res.err_msg == "get_brand_wcpay_request:ok" ) {
            //
            // }     // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。
          }
        );
        console.log('支付确认')
        console.log(item)
      },
      onCashierCancel() {
        console.log('取消')
        // Abort pay request or checking request
        this.timer && clearTimeout(this.timer)
      },
      getCPlist(){//获取缴费
        var v = this
        this.$http.post(this.apis.CPTYPE,{cpType:1}).then((res)=>{
          console.log(res)
          if(res.code==200){
            v.cp = res.result
            v.cashierAmount ="'" + parseInt(res.result.cpJl)/100 +"'"
            // v.payMoney(res.result.id)
          }

        }).catch((err)=>{

        })
      },
      payMoney(){
        var v = this
        console.log(v.cp)
        this.$http.post(this.apis.CPPAY,{ddZftd:2,cpId:v.cp.id}).then((res)=>{
          console.log(res)
          if(res.code==200){
            console.log(res.result)
            v.payMess = res.result
            this.isCashierhow = !this.isCashierhow
          }else {
            Toast(res.message)
          }
        }).catch((err)=>{

        })
      },
    }
  }

</script>
<!--<style lang="less">-->
  <!--.md-button.primary.large, .md-button.primary.small{-->
    <!--height: 1rem;-->
    <!--line-height: 1rem;-->
  <!--}-->
<!--</style>-->
<style lang="stylus">
  .md-example-child-cashier
    .md-field
      margin-bottom 30px
    .choose-channel-item
      .item-icon.cashier-icon-1
        background url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAYAAAByDd+UAAABHklEQVR4Ae2WgUYEURSGh70oQIB6gh6gHmBsqhfqHdq2hAJTUYAACMCytqQISUDAVpsEsWi09TX/XFcxYeJ0E/vzMf4z7uc6cJMQWi6l1egU5AUYkfszXZp8TVFmBfwyWbhZU0UcXKrbdWMJ5ZLwNaIwl5CY1BPen8HgAtoT1dn6pJ/dnRoKn64p01mpztQpj1eGwsNlyuTPsDX92W/P+A70j6FQ3BxR5nI3dP4bNDPeochmYZTD+xvsz8HBvP8evWhmLvScb1Cm3/Mo6sLcXLg5BcMHQhgO1BkL+8fUjP6NLLw9MRB+R0h19h+F4x3WYSystVe/rx/zJ0+MXtxHVNstxBO6ZqKw2tiLINxJQrzULenKxjvVWV3W3GLwfAD9KR4TBA12SgAAAABJRU5ErkJggg==') center no-repeat
        background-size 26px
      .item-icon.cashier-icon-2
        background url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAYAAAByDd+UAAAB7ElEQVR4Ac2WA6weQRSFb62gtuPajetGtW0FNcO6cVK7XdRu2Ia149R2Z6bP9r6T+yb5bZ7k/Bh9gzt3l1hXfzUiS+4iU36GK2AnTq6AP/PYJ/42JhZ+oPAp7CTWYDDUVHu4IDneCaD8kkTgV4r4zA5+akBuivhMI56llyLv768wXkpfYNK3NGXAaHQhpxlHY9KAlpwc6Qojt6W2urZYnYoFWA5XhWhTSaf/dmHYDqc2/v+NFPiZDLmKzv7pxAPAZIvWZPwbjrqN8B242LU6edUtgMZGGjS3XI8R6IRTj/yI2xhiPNofA7CPCyiuRAL8wM9FUy6AH8I5urwE/gwb8FSdS311+ldz1KsIgGIpmWpNGEGSQYbaC7f1gWIyPClD3uaJBgWeld3x/S3MyCwkWwz1AfqsWCzV8EIfIAeJqd6GAcuHx7oFylz4AVlqGl116gQ/c3HABTTkFH1pywPD1Esy//V0wdQMlJe6Rex3MtQ2OvO/fRgZXbziGVqqP+CmXm0m/AED2WT9G0GOU4sg/sYVCZLCyngrLTHRJ9K9Gl4jWzShYDoj+5Ih7keQiSQWcwgTHqKBPrP8wanK/D+QLzwOH4DeOgDu+maeiPwmBS9RuFNJA1pyV9JfhF33BHS9vZVxhFTWjCn2kYZVAyRlGm3AoxGeAAAAAElFTkSuQmCC') center no-repeat
        background-size 26px
      .item-icon.cashier-icon-3
        background url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAYCAYAAADpnJ2CAAABiklEQVR4AbWVAWRCURSGf8gIGQyYgUEAgyAAbCCAAAQQGBAYIGAwAAgIwAMJGIsqRTAgQPAQEsKDu//kXHLddV+v+w6f0HnvP/e8/5yLYPzgAXN0yICsSUoyZUeW+l/nlFs45miSRF9scpKRBDM0rhGqacXmRobhE8/wwsQtMZHYyjv/E2swYU9MZPZW1DVGWoLYEQt88ffR/W7DyEIHCn3ab4gVKpjhzbayHlOI9K2QHqZFNsSctKSSCEI78oE17h1PTJy8PnRwjYdNjlanpEdqsLHC04XnltA2+KuWiudoe9y7xQLvWKF6JlSVE5DjxZZf3CRs91nV3zqjXYxw55iuk9PlGQKJGaZ4hoR123lM8cqc3yu+dSqC40BS4lkSdf9zQcY4tSic2LQLQgc5K+jmrl3Wu0DiUkxy4+o72LEJnDISUrDjtKREwTHc0DkalSA2sfPqE62oKWKJDWRmgzd+BKENaUGjLMGjzmbbLoiigmvSk/tMnKa3y0DQ9vdIM9S6gKCK6FqLGX+Ik2Cgy7oRZQAAAABJRU5ErkJggg==') center no-repeat
        background-size 26px
      .item-icon.cashier-icon-4
        background url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAcCAYAAAB75n/uAAADaElEQVR4AWIgADiAOAaI1wDxUxZAadUA5GoTBCd59Wzbtm3btm3btm3btm3btt/5+v/6uLXRl/q7qu+SRc/OzKbXIhAh5ZXB1QZLyf8ANz+noAseNZhc3ETb4NOaocUiX+JFlYJiEuU1cdNBzGQSjbVWN3bu3BkHDx5EgQIFQsb69u2LjRs3IkWKFHqgE+ICndUNFCB27drFIBxDoUKFcP/+fZLj9rIpL05wVV1co0YNEBQdP3484sWLx9OjXbt2JD5//mwvwBaHV1KvPctCsEyLFy9GxIgRQeErV67g2rVrePz4sf1eOEBGLtB56tQpEPPmzQO/x4gRAx8+fADRvXt3R02PJnaQw97inKnCYkiLTBjf2IJ5bQTkxJax0KdpdiSPK44CxBcdk5LE7qqWaGxDwZv5AmxyzjtTLKiVP3QfNfakTVBZdDzOHHPDhESREBxkQA3Br4Vh8WlyJAr568Leq634MC4yPs63onz2UPEVySL7vswcc6LoeJIx+vPXmWNgW8ooSBs+DDcgY2LBoYEWvBoaDVeaJMSNlvFD+G5sZJYLsaMGlTJiGOxPFRXUuJ8x+nnREO16+ui+nAzm4qSRUT5qWES2Bpbg7OCwuNcjJok1nawMzjlUix4Oq5NHhrr3WJqonnqj5/HUJ9IEnkAnx5n6rMSRQFLQ0VpmkSSslVlNkyAkD647/y8pkx9vC6XiYrf4tnh6dEocHapWsDdNVK/Y/Pnz8XvdYlOiH6rlx7fh3eFx8iDg54uyZcvq13W8jT1s3boVPyYPVYXg/+8vfJ4/htedayT42d/TAzoaNmyoB7gquj3QJXkqNQBPZwZ6gCBtgcpp06bZBPD79QMmQFNUhYMpj4M+dOYEHdT3y0f8nDsBX3u3wpdujU1l4OHhgQgRIsBqld+K7VNbBgU1mlhNx6SZuYtVq1ZRUL2e04TaGuLTbmnP7uDv379Ily4dxZ/bOqkGPt5Mk802i+bNm4MHy5ZccokzYJOMN/jn6WxBscxhwVfM29sbjsBSsmclMoc47x9q2BffLP00x/TfM0DQvkYqjB45BHyT+Q7zNeNvpWuntmhUIgq4RtsHaokObJS2nHRAf2a1s5/s3NFXtvOzauE6qeWoRHUMHjT4mQJBfGpws8HKyrrKHOOcso57DlJDFPwHtUxGlWBNgLkAAAAASUVORK5CYII=') center no-repeat
        background-size 26px
      .item-icon.cashier-icon-5
        background url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAMAAABF0y+mAAABPlBMVEUAAAC/AEDIFS3GFS3HFS3HFS3HFS3MADPGFC7GFS3FFSvHFS3GFS3HFS3HFS3KIjnooKnmmKPJHDTMLELqqLHmlaDIGjHONEnssLjkjJjHFi7////hgI3rqrLdcoDwwsjUS17KITjed4T44+XIGC/LJz3++fr119vQPFD//v7NMEXgf43ca3r78PLZX3DVUWPpoavut77IGTD44+bxxszmlqHxw8nHFy/44OPttr3LJjz99/jaZXXXWWrhgY7+/P3//f3XV2jzzdPgf4zut7/nmqTxxMrllJ/yyM7dcH/++vvMK0HWUmTQO0/ZYXHba3r77vDJHTTts7v22Nzxxcv55OffdoXed4XRP1PedIPaZHThg5D11dnVTmDJHzb77e/55+rLJj377/Hqp7DkjprTRlnlkZzRPlLNL0TXVmdZnHwTAAAAD3RSTlMABEqUzPH/BXDmMNZV+P145yZGAAABMklEQVR4AXTQ02KcARCG4Z/v2t5vVdv2to1t2/d/BcnEfE7H45xxPT8IIQx8z3WuikRjnItFI5dj8QRXJOIXsWSKa1LJ81iaG9Kn0XiKW6TiFoskuFXCtopyh+jRfTFMJpvLA4ViqQxUqrU6MdfxwJSkBtCUWkBb6oDn+Jh796UHD3n0WHoCT6Vnz8F3AsyLl6+k1zQk6Q1vpXdA4IQWe//h4yfp85evFvz2/YdlQOhgfkq/nkm/pT9S96/1NifBf9L/HpnePql/QIMYa0tHGhoekRkdk16NT0yCtQ2AKalIfVrSDO9npbl5TGCnPO9KC7AoaQmWpRUwvj1hVWoDa+vaKMPmlrbBePa+LzvVCsDu3j7AweEyxSAJghFfwOONMnyRjS+Z4Etg+JIm4USNNzsAAPHdK2mIKv4bAAAAAElFTkSuQmCC') center no-repeat
        background-size 26px
</style>
