package com.cwb.platform.biz.service.impl;

import com.cwb.platform.biz.mapper.BizCpMapper;
import com.cwb.platform.biz.model.BizCp;
import com.cwb.platform.biz.service.CpService;
import com.cwb.platform.sys.base.BaseServiceImpl;
import com.cwb.platform.sys.model.SysYh;
import com.cwb.platform.util.bean.ApiResponse;
import com.cwb.platform.util.bean.SimpleCondition;
import com.cwb.platform.util.commonUtil.DateUtils;
import com.cwb.platform.util.commonUtil.MathUtil;
import com.cwb.platform.util.commonUtil.StringDivUtils;
import com.cwb.platform.util.exception.RuntimeCheck;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CpServiceImpl extends BaseServiceImpl<BizCp,String> implements CpService {
    @Value("${order_money}")
    private String orderMoney;

    @Autowired
    private BizCpMapper entityMapper;
    @Autowired
    private StringRedisTemplate redisDao;


    @Override
    protected Mapper<BizCp> getBaseMapper() {
        return entityMapper;
    }

    /**
     * 查询一个类型的产品
     * @param cpType    产品类型（1、学费  2、补考费）必填
     * @return
     */
    public ApiResponse<BizCp> getCpTyet(String cpType){
        BizCp bizCp=entityMapper.getCpTyetList(cpType);
        return ApiResponse.success(bizCp);
    }
    /**
     * 查询一个产品列表
     * @return
     */
    public ApiResponse<List<BizCp>> getCpTyetList(){
        BizCp queryBizCp=new BizCp();
        queryBizCp.setCpYx("1");

        SimpleCondition condition = new SimpleCondition(BizCp.class);
        condition.eq(BizCp.InnerColumn.cpYx, "1");
        condition.setOrderByClause(BizCp.InnerColumn.cpType.asc());
        List<BizCp> list=this.findByCondition(condition);
        return ApiResponse.success(list);
    }
    /**
     * 后台-新增产品
     * @param entity
     * @return
     */
    public ApiResponse<String> saveCp(BizCp entity){
    //1、参数验证
        SysYh sysYh = getCurrentUser();
        RuntimeCheck.ifBlank(entity.getCpMc(),"费用名称不能为空");
        RuntimeCheck.ifBlank(entity.getCpType(),"收费类型不能为空");
        RuntimeCheck.ifBlank(entity.getCpYj(),"请确定该产品分佣状态");
        Double cpJl=  MathUtil.stringToDouble(entity.getCpJl());//产品金额
        RuntimeCheck.ifFalse(cpJl>0,"收费金额不能小于0");
        if(entity.getCpType().equals("1")) {
            RuntimeCheck.ifFalse((cpJl / 100) >= MathUtil.stringToDouble(orderMoney), "收费金额不能低于系统配置的最低价：" + orderMoney);
        }
        Double cpYjyjs=0.00;
        Double cpRjyjs=0.00;
        if(StringUtils.equals(entity.getCpYj(),"1")){
            Double cpYjyj= entity.getCpYjyj(); //new BigDecimal(entity.getCpYjyj()).doubleValue();//设置一级佣金
            Double cpRjyj=entity.getCpRjyj();//设置二级佣金
            RuntimeCheck.ifTrue(cpYjyj+cpRjyj>cpJl,"分佣金额不能大于总金额");//
            cpYjyjs=cpYjyj;
            cpRjyjs=cpRjyj;
        }
//    //2、将该类型所有产品设置为无效
//        entityMapper.updateTypeDelete(entity.getCpType());//更新类型为无效
    //3、将新记录插入表中
        BizCp newBizCp=new BizCp();
        newBizCp.setId(genId());
        newBizCp.setCpMc(entity.getCpMc());//设置产品名称
        newBizCp.setCpType(entity.getCpType());//设置产品类型（1、学费  2、补考费）必填
        newBizCp.setCpJl(cpJl.toString());//设置产品总金额
        newBizCp.setCpYj(entity.getCpYj());//设置是否分佣(0不分 1分佣)
        newBizCp.setCpYjyj(cpYjyjs);//设置一级佣金
        newBizCp.setCpRjyj(cpRjyjs);//设置二级佣金
        newBizCp.setCpYx("0");//设置产品是否有效(0、无效 1、生效)
        newBizCp.setCjsj(DateUtils.getNowTime());//设置创建时间
        newBizCp.setCjr(sysYh.getYhid());//设置创建人


       int i= entityMapper.insert(newBizCp);
        if(i == 1){ // 保存成功 ， 生成验证码
            String code1 =  StringDivUtils.getSix();
            String code2 = StringDivUtils.getSix();
            redisDao.boundValueOps("cp-SMS-"+newBizCp.getId()).set(code1 + "," + code2,1,TimeUnit.DAYS);
            // 调用短信发送接口
           /* SendSmsUtil.sendSmsMap();*/
        }else{
            return ApiResponse.fail("修改失败，请重新操作");
        }
        ApiResponse<String> res = new ApiResponse<>();
        res.setMessage("操作成功");
        res.setResult(newBizCp.getId());
        return res;
    }

    /**
     * 验证短信验证号码
     * @param cpId
     * @return
     */
    @Override
    public ApiResponse<String> validateCSMS(String cpId, String code1, String code2) {
        SysYh user = getCurrentUser();
//        1、非空验证
        RuntimeCheck.ifBlank(cpId , "产品id不能为空");
        RuntimeCheck.ifNull(code1,"李总验证码不能为空");
        RuntimeCheck.ifNull(code2, "刘总验证码不能为空");
//2、验证号码验证
        String codes=redisDao.boundValueOps("cp-SMS-"+cpId).get();//获取验证号码。
        RuntimeCheck.ifTrue(StringUtils.isBlank(codes),"获取验证码失败，请重新尝试");//这里是没有获取到reis中的验证码
        String [] codeArray=codes.split(",");
        RuntimeCheck.ifTrue(codeArray==null||codeArray.length<1,"获取验证码失败，请重新尝试");//这里是没有获取到reis中的验证码

        RuntimeCheck.ifTrue(!StringUtils.equals(codeArray[0],code1) , "李总验证码不正确");
        RuntimeCheck.ifTrue(!StringUtils.equals(codeArray[1],code2) , "刘总验证码不正确");
//        3、该产品的验证
        BizCp cp = this.findById(cpId);
        RuntimeCheck.ifTrue(cp==null , "产品id不存在");
        RuntimeCheck.ifFalse(!StringUtils.equals(cp.getCpYx(),"1") , "该产品已经验证通过，暂不需要验证");//获取产品是否有效(0、无效 1、生效)



        entityMapper.updateTypeDelete(cp.getCpType());//更新类型为无效

        BizCp updateCp=new BizCp();
        updateCp.setId(cpId);
        updateCp.setCpYx("1");
        int i=this.update(updateCp);

        if(i == 1){ // 保存成功 ，清除redis
            redisDao.delete("cp-SMS-"+cpId);
        }else{
            return ApiResponse.fail("修改失败，请重新操作");
        }
        return ApiResponse.success();
    }

}
