package com.cwb.platform.biz.service;


import com.cwb.platform.biz.model.BizWj;
import com.cwb.platform.sys.base.BaseService;

import java.util.Map;

public interface WjService extends BaseService<BizWj,java.lang.String>{

    boolean ocrRecognition(Map<String, String> retMap, String fileType,String filePath,String path);

    void tailorSubjectImg(String s);
}
