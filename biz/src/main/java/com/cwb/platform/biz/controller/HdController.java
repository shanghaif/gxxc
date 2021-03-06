package com.cwb.platform.biz.controller;

import com.cwb.platform.util.bean.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cwb.platform.biz.model.BizHd;
import com.cwb.platform.biz.service.HdService;
import com.cwb.platform.sys.base.BaseController;
import com.cwb.platform.sys.base.BaseService;

@RestController
@RequestMapping("/api/hd")
public class HdController extends BaseController<BizHd,java.lang.String>{
    @Autowired
    private HdService service;

    @Override
    protected BaseService<BizHd, java.lang.String> getBaseService() {
        return service;
    }

    /**
     * 新增
     * @param entity
     * @return
     */
    @Override
    @RequestMapping(value="/save", method={RequestMethod.POST})
    public ApiResponse<String> save(BizHd entity){
        return service.saveEntity(entity);
    }

    /**
     * 修改
     * @param entity
     * @return
     */
    @Override
    @RequestMapping(value="/update", method={RequestMethod.POST})
    public ApiResponse<String> update(BizHd entity){
        return service.updateEntity(entity);
    }

    /**
     * 活动推荐
     * @param entity
     * @return
     */
    @RequestMapping(value="/hdtj", method={RequestMethod.POST})
    public ApiResponse<String> activityRecommend(BizHd entity){
        return service.activityRecommend(entity);
    }



}
