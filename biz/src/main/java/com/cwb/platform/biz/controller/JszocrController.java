package com.cwb.platform.biz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cwb.platform.biz.model.BizJszocr;
import com.cwb.platform.biz.service.JszocrService;
import com.cwb.platform.sys.base.BaseController;
import com.cwb.platform.sys.base.BaseService;

@RestController
@RequestMapping("/api/jszocr")
public class JszocrController extends BaseController<BizJszocr,java.lang.String>{
    @Autowired
    private JszocrService service;

    @Override
    protected BaseService<BizJszocr, java.lang.String> getBaseService() {
        return service;
    }
}
