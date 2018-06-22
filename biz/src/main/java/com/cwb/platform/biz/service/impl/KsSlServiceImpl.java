package com.cwb.platform.biz.service.impl;

import com.cwb.platform.biz.mapper.BizKsSlMapper;
import com.cwb.platform.biz.model.BizKsSl;
import com.cwb.platform.biz.service.KsSlService;
import com.cwb.platform.biz.service.PtyhService;
import com.cwb.platform.biz.wxpkg.service.WechatService;
import com.cwb.platform.sys.base.BaseServiceImpl;
import com.cwb.platform.sys.model.BizPtyh;
import com.cwb.platform.sys.model.SysYh;
import com.cwb.platform.sys.model.SysZdxm;
import com.cwb.platform.sys.service.ZdxmService;
import com.cwb.platform.util.bean.ApiResponse;
import com.cwb.platform.util.bean.SimpleCondition;
import com.cwb.platform.util.commonUtil.DateUtils;
import com.cwb.platform.util.exception.RuntimeCheck;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.text.ParseException;
import java.util.*;

/**
 * 学员考试受理信息表
 * Created by Administrator on 2018/6/19.
 */
@Slf4j
@Service
public class KsSlServiceImpl extends BaseServiceImpl<BizKsSl,String> implements KsSlService {
    @Autowired
    private BizKsSlMapper entityMapper;

    @Autowired
    private PtyhService ptyhService;

    @Autowired
    private WechatService wechatService;

    @Autowired
    private ZdxmService zdxmService;

    @Value("${wxMsgTemplate.handle}")
    private String handleMsgId;

    @Value("${wxDomain}")
    private String wxDomain;

    @Override
    protected Mapper<BizKsSl> getBaseMapper() {
        return entityMapper;
    }

    @Override
    protected Class<?> getEntityCls(){
        return BizKsSl.class;
    }

    @Override
    public ApiResponse<String> validAndSave(BizKsSl entity) {
        int i=save(entity);
        return i==1?ApiResponse.success():ApiResponse.fail();
    }
    @Override
    public int save(BizKsSl entity) {
//        RuntimeCheck.ifBlank(entity.getCode(), "请选择机构");
        RuntimeCheck.ifBlank(entity.getName(), "请确定机构名称");

        RuntimeCheck.ifBlank(entity.getSlType(), "审核状态不能为空");
        if (org.apache.commons.lang.StringUtils.containsNone(entity.getSlType(), new char[]{'1', '2', '3', '4'})) {
            RuntimeCheck.ifTrue(true,"请输入正确审核状态");
        }

        SysYh user=getCurrentUser();
        entity.setId(genId());
        entity.setCjr(user.getYhid());//操作人ID
        entity.setCjsj(DateUtils.getNowTime());//创建时间
        BizPtyh ptyh=ptyhService.findById(entity.getYhId());
        RuntimeCheck.ifTrue(ptyh == null, "用户资料有误！");
        entity.setYhZjhm(ptyh.getYhZjhm());//用户证件号码
        entity.setYhXm(ptyh.getYhXm());//用户姓名

        // 向微信用户发送消息
        sendMsg(entity,ptyh);
        return entityMapper.insertSelective(entity);
    }

    private String getSlType(String code){
        if (StringUtils.isEmpty(code))return "";
        List<SysZdxm> zdxmList = zdxmService.findEq(SysZdxm.InnerColumn.zdlmdm,"ZDCLK0071");
        for (SysZdxm zdxm : zdxmList) {
            if (code.equals(zdxm.getZddm())){
                return zdxm.getZdmc();
            }
        }
        return "";
    }

    private String sendMsg(BizKsSl sl,BizPtyh ptyh){
        if (StringUtils.isEmpty(ptyh.getYhOpenId())){
            log.error("发送微信消息失败，用户openid为空");
            return "openid is empty";
        }
        List<WxMpTemplateData> data = new ArrayList<>();
        String slType = getSlType(sl.getSlType());
        String time = "";
        try {
            Date date = DateUtils.getDate(sl.getSlSj(),"yyyy-MM-dd");
            time = DateUtils.getDateStr(date,"yyyy年MM月dd日");
        } catch (ParseException e) {
            log.error("受理时间转换异常",e);
        }

        String first = "您"+time+"，在"+sl.getName();
        switch (slType){
            case "医院体验":
                first += "体检采集完成，请携带本人有效身份证件前往报名驾校进行入网面签";
                break;
            case "入网面签":
                first += "入网面签完成，请携带本人有效身份证件前往报名驾校进行档案采集";
                break;
            case "档案采集":
                first += "档案采集完成，请等待工作人员受理";
                break;
            case "受理成功":
                first += "受理成功，请前往报名驾校进行培训";
                break;
            default:

        }
        data.add(new WxMpTemplateData("first",first));
        data.add(new WxMpTemplateData("keyword1",slType,"#ff0000"));
        data.add(new WxMpTemplateData("keyword2",time));
        data.add(new WxMpTemplateData("remark","点击查看"));
        WxMpTemplateMessage msg = new WxMpTemplateMessage();
        msg.setToUser(ptyh.getYhOpenId());
        msg.setTemplateId(handleMsgId);
        msg.setUrl(wxDomain);
        msg.setData(data);
        try {
            String res = wechatService.sendTemplateMsg(msg);
            log.info("sendMsg result :",res);
            return res;
        } catch (WxErrorException e) {
            log.error("发送微信模板消息异常",e);
        }
        return "未知错误";
    }

    @Override
    public ApiResponse<Map<String,BizKsSl>> getHandleStatus(String yhId) {
        RuntimeCheck.ifBlank(yhId,"请选择学员");
        SimpleCondition condition1 = new SimpleCondition(BizKsSl.class);
        condition1.setOrderByClause("SL_SJ desc");
        condition1.eq(BizKsSl.InnerColumn.yhId,yhId);
        condition1.eq(BizKsSl.InnerColumn.slType,"1");

        SimpleCondition condition2 = new SimpleCondition(BizKsSl.class);
        condition2.setOrderByClause("SL_SJ desc");
        condition2.eq(BizKsSl.InnerColumn.yhId,yhId);
        condition2.eq(BizKsSl.InnerColumn.slType,"2");

        SimpleCondition condition3 = new SimpleCondition(BizKsSl.class);
        condition3.setOrderByClause("SL_SJ desc");
        condition3.eq(BizKsSl.InnerColumn.yhId,yhId);
        condition3.eq(BizKsSl.InnerColumn.slType,"3");

        SimpleCondition condition4 = new SimpleCondition(BizKsSl.class);
        condition4.setOrderByClause("SL_SJ desc");
        condition4.eq(BizKsSl.InnerColumn.yhId,yhId);
        condition4.eq(BizKsSl.InnerColumn.slType,"4");

        List<BizKsSl> list1 = entityMapper.selectByExampleAndRowBounds(condition1,new RowBounds(0,1));
        List<BizKsSl> list2 = entityMapper.selectByExampleAndRowBounds(condition2,new RowBounds(0,1));
        List<BizKsSl> list3 = entityMapper.selectByExampleAndRowBounds(condition3,new RowBounds(0,1));
        List<BizKsSl> list4 = entityMapper.selectByExampleAndRowBounds(condition4,new RowBounds(0,1));

        Map<String,BizKsSl> map = new HashMap<>(4);
        if (list1.size() != 0){
            map.put("1",list1.get(0));
        }
        if (map.size() != 0){
            map.put("2",list2.get(0));
        }
        if (list3.size() != 0){
            map.put("3",list3.get(0));
        }
        if (list4.size() != 0){
            map.put("4",list4.get(0));
        }
        ApiResponse<Map<String,BizKsSl>> result = new ApiResponse<>();
        result.setResult(map);
        result.setMessage(""+map.size());
        return result;
    }
}