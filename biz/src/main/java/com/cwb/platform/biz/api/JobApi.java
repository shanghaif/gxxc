package com.cwb.platform.biz.api;

import com.cwb.platform.biz.model.BizOrder;
import com.cwb.platform.biz.service.JobService;
import com.cwb.platform.util.bean.ApiResponse;
import com.cwb.platform.util.commonUtil.DateUtils;
import com.cwb.platform.util.commonUtil.MD5Util;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * JOB定时任务处理接口
 */
@RestController
@RequestMapping("/job/")
public class JobApi {
    @Autowired
    private JobService jobService;
    @Value("${JOB.KEY}")
    private String jobKey;
    @Value("${JOB.TOKEN}")
    private String jobToken;
    @Value("${JOB.HOST}")
    private String jobHost;

    @Resource(name = "wxPayService")
    private WxPayService wxService;

    @Autowired
    private StringRedisTemplate redisDao;

    /**
     * 订单处理成功
     * 1、查询所有完成的订单。
     * 2、给用户明细表，下发佣金。
     * 3、下发完佣金后，需要更新账户表
     * 4、给支付成功的用户生成邀请码，并生成二维码orderFulfil
     * @param key
     * @return
     */
    @RequestMapping(value="/orderFulfil", method={RequestMethod.POST})
    public ApiResponse<String> orderFulfil(@RequestParam(value = "key",required = false) String key,@RequestParam(value = "token",required = false) String token){
        //1、报文验证 IP、时间戳、业务编号、md5校证值。

        if(StringUtils.isBlank(token)){
            if(StringUtils.isBlank(key)){
                return ApiResponse.fail("密钥为空");
            }
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String host = request.getRemoteHost();
            if(StringUtils.indexOf(jobHost,host) < 0 ){
                return ApiResponse.fail("ip异常");
            }
            //MD5Util.MD5Encode(jobKey + DateUtils.getDateStr(new Date(), "yyyy-MM-dd HH:mm"),null);
            DateTime dateTime = DateTime.now();
            DateTime minusMinutes = dateTime.minusMinutes(1);
            String encode = MD5Util.MD5Encode(jobKey + DateUtils.getDateStr(dateTime.toDate(), "yyyy-MM-dd HH:mm"), null);
            String encode1 = MD5Util.MD5Encode(jobKey + DateUtils.getDateStr(minusMinutes.toDate(), "yyyy-MM-dd HH:mm"), null);
            if(!StringUtils.equals(key,encode) && !StringUtils.equals(key,encode1)){
                return ApiResponse.fail("不是当前的任务");
            }
        }else {
            if(!StringUtils.equals(jobToken,token)){
                return ApiResponse.fail("token错误");
            }
        }


        //2、验证是否成功 ，如果失败就直接失败

        //3、

        List<BizOrder> list=jobService.orderFulfil();

        String messaget="";
        if(CollectionUtils.isEmpty(list)){
            messaget = "未查询到订单";
        }
        for(BizOrder l:list){
            //防止定时任务被并发处理，增加的业务锁处理。
            String value = redisDao.boundValueOps("order_"+l.getDdId()).get();
            if(StringUtils.isEmpty(value)){
                try {
                    ApiResponse<String> ret=jobService.updateOrderFulfilDispose(l);
                    if(!ret.isSuccess()){
                        messaget+=ret.getMessage()+"\n";
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    messaget+="订单："+l.getDdId()+"处理异常\n"; // TODO: 2018/6/8 数据库异常，需要回写到定时任务中
                }
                finally {
                    redisDao.delete("order_"+l.getDdId());
                }

            }else{
                messaget+="订单编号："+l.getDdId()+"已被其它应用于"+value+"处理。系统跳过处理\n";
            }
        }

        return ApiResponse.success(messaget);

//        return null;
    }

//    /**
//     * 定时任务 定时对账
//     * 该方法已经放弃了。不用了
//     */
//    @PostMapping("/downloadBill")
//    public ApiResponse<List<String>> downloadBill(@RequestParam String billDate,@RequestParam(required = false) String key, @RequestParam(value = "token",required = false) String token) throws WxPayException {
//        List<String> retMessage=new ArrayList<String>();
//        ApiResponse<List<String>> res = new ApiResponse<List<String>>();
//        if(StringUtils.isBlank(token)){
//            if(StringUtils.isBlank(key)){
//                res.setMessage("密钥为空");
//                return res;
//            }
//            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//            String host = request.getRemoteHost();
//            if(StringUtils.indexOf(jobHost,host) < 0 ){
//                res.setMessage("ip异常");
//                return res;
//            }
//            //MD5Util.MD5Encode(jobKey + DateUtils.getDateStr(new Date(), "yyyy-MM-dd HH:mm"),null);
//            DateTime dateTime = DateTime.now();
//            DateTime minusMinutes = dateTime.minusMinutes(1);
//            String encode = MD5Util.MD5Encode(jobKey + DateUtils.getDateStr(dateTime.toDate(), "yyyy-MM-dd HH:mm"), null);
//            String encode1 = MD5Util.MD5Encode(jobKey + DateUtils.getDateStr(minusMinutes.toDate(), "yyyy-MM-dd HH:mm"), null);
//            if(!StringUtils.equals(key,encode) && !StringUtils.equals(key,encode1)){
//                res.setMessage("不是当前的任务");
//                return res;
//            }
//        }else {
//            if(!StringUtils.equals(jobToken,token)){
//                res.setMessage("token错误");
//                return res;
//            }
//        }
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
//        if(StringUtils.isBlank(billDate)) {
//
//            LocalDateTime time = LocalDateTime.now();
//            LocalDateTime dateTime = time.minusDays(13);
//            billDate = dateTime.format(dateTimeFormatter);
//        }else{
//            try {
//                LocalDate.parse(billDate,dateTimeFormatter);
//            }catch (DateTimeParseException e){
//                res.setMessage("所传日期格式不对");
//                return res;
//            }
//        }
//        String billType = "ALL";
//        String deviceInfo = "";
//
//        try {
//            WxPayBillResult billResult=wxService.downloadBill(billDate, billType, "", deviceInfo);
//            if(billResult!=null&&billResult.getWxPayBillBaseResultLst().size()>0){
//                retMessage= jobService.billContrast(billResult,billDate);
//            }
//        }catch (WxPayException e){
////            e.
//        }
//
//        res.setMessage("操作成功");
//        res.setResult(retMessage);
//        return res;
//    }

    /**
     * 微信端下载对账文件
     * @param payTpye 1、支付宝 2、微信
     * @param billDate
     * @param key
     * @param token
     * @return
     * @throws WxPayException
     */
    @PostMapping("/wxDownloadBill")
    public ApiResponse<String> wxDownloadBill(@RequestParam String payTpye,@RequestParam String billDate,@RequestParam(required = false) String key, @RequestParam(value = "token",required = false) String token) throws WxPayException {
        List<String> retMessage=new ArrayList<String>();
        if(StringUtils.isBlank(token)){
            if(StringUtils.isBlank(key)){
                return ApiResponse.fail("密钥为空");
            }
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String host = request.getRemoteHost();
            if(StringUtils.indexOf(jobHost,host) < 0 ){
                return ApiResponse.fail("ip异常");
            }
            //MD5Util.MD5Encode(jobKey + DateUtils.getDateStr(new Date(), "yyyy-MM-dd HH:mm"),null);
            DateTime dateTime = DateTime.now();
            DateTime minusMinutes = dateTime.minusMinutes(1);
            String encode = MD5Util.MD5Encode(jobKey + DateUtils.getDateStr(dateTime.toDate(), "yyyy-MM-dd HH:mm"), null);
            String encode1 = MD5Util.MD5Encode(jobKey + DateUtils.getDateStr(minusMinutes.toDate(), "yyyy-MM-dd HH:mm"), null);
            if(!StringUtils.equals(key,encode) && !StringUtils.equals(key,encode1)){
                return ApiResponse.fail("不是当前的任务");
            }
        }else {
            if(!StringUtils.equals(jobToken,token)){
                return ApiResponse.fail("token错误");
            }
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        if(StringUtils.isBlank(billDate)) {

            LocalDateTime time = LocalDateTime.now();
            LocalDateTime dateTime = time.minusDays(13);
            billDate = dateTime.format(dateTimeFormatter);
        }else{
            try {
                LocalDate queryDate = LocalDate.parse(billDate, dateTimeFormatter);
                queryDate.atStartOfDay();
            }catch (DateTimeParseException e){
                return ApiResponse.fail("所传日期格式不对");
            }
        }
        if(!(StringUtils.equals(payTpye,"2")||StringUtils.equals(payTpye,"1"))){
            return ApiResponse.fail("平台暂未开通:"+payTpye+" 类型的支付通道");
        }
        //是否手工对账  如果是手工对账，就要删除 对账日期的账单
        return jobService.balanceBillAccount(billDate,false,payTpye);
    }

    /**
     * 对账统计
     * @param tjsj
     * @return
     */
    @RequestMapping("/tj")
    public ApiResponse<String> tj(@RequestParam(value = "tjsj",required = false)String tjsj){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if(StringUtils.isBlank(tjsj)){ // 默认为前一天的时间
            LocalDateTime localDateTime = LocalDateTime.now();
            LocalDateTime minusDays = localDateTime.minusDays(1);
            tjsj =  minusDays.format(dateTimeFormatter);
        }else{
            try {
                LocalDateTime.parse(tjsj,dateTimeFormatter);
            }catch (DateTimeParseException e){
                return ApiResponse.fail("所传格式有误");
            }
        }
        //tjsj
        // 插入数据至统计表中 todo
        jobService.orderStatistics(tjsj);


        return ApiResponse.success();

    }
}
