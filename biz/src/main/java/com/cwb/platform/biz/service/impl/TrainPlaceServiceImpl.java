package com.cwb.platform.biz.service.impl;

import com.cwb.platform.biz.mapper.BizTrainPlaceMapper;
import com.cwb.platform.biz.model.BizDriversSchool;
import com.cwb.platform.biz.model.BizSubSchool;
import com.cwb.platform.biz.model.BizTrainPlace;
import com.cwb.platform.biz.service.SchoolService;
import com.cwb.platform.biz.service.SubSchoolService;
import com.cwb.platform.biz.service.TrainPlaceService;
import com.cwb.platform.biz.util.GpsUtil;
import com.cwb.platform.sys.base.BaseServiceImpl;
import com.cwb.platform.sys.base.LimitedCondition;
import com.cwb.platform.util.bean.ApiResponse;
import com.cwb.platform.util.commonUtil.DateUtils;
import com.cwb.platform.util.exception.RuntimeCheck;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.mapper.common.Mapper;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TrainPlaceServiceImpl extends BaseServiceImpl<BizTrainPlace,String> implements TrainPlaceService {
    @Autowired
    private BizTrainPlaceMapper trainPlaceMapper;
    @Autowired
    private SchoolService schoolService;
    @Autowired
    private SubSchoolService subSchoolService;

    @Override
    protected Mapper<BizTrainPlace> getBaseMapper() {
        return trainPlaceMapper;
    }


    @Override
    public boolean fillPagerCondition(LimitedCondition condition){

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()) .getRequest();
        String regionCode = request.getParameter("inputRegionCode");//区县行政区划代码
        if(StringUtils.isNotEmpty(regionCode)){
            condition.startWith(BizTrainPlace.InnerColumn.regionCode.name(), regionCode + "%");
        }
        condition.setOrderByClause("create_time desc");
        return true;
    }

    @Override
    protected void afterPager(PageInfo<BizTrainPlace> resultPage) {
        List<BizTrainPlace> placeList = resultPage.getList();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String gps = request.getParameter("gps");
        if(StringUtils.isNotBlank(gps)){
            String[] gpses = gps.split(",");
            double latitude = Double.parseDouble(gpses[1]);  // 纬度
            double longitude = Double.parseDouble(gpses[0]); // 经度


            placeList.stream().forEach(bizTrainPlace -> {
                if(bizTrainPlace.getLongitude()!=null&&bizTrainPlace.getLatitude()!=null){
                    double distance = GpsUtil.getDistance(longitude, latitude, bizTrainPlace.getLongitude(), bizTrainPlace.getLatitude());
                    bizTrainPlace.setDistance(distance);
                }else{
                    bizTrainPlace.setDistance(null);
                }
//                double distance = GpsUtil.getDistance(longitude, latitude, bizTrainPlace.getLongitude(), bizTrainPlace.getLatitude());
//                bizTrainPlace.setDistance(distance);
            });
//            GpsUtil.getDistance(longitude,latitude)

        }else{
           placeList.stream().forEach(bizTrainPlace -> {
               bizTrainPlace.setDistance(null);
           });
        }


        super.afterPager(resultPage);
    }

    @Override
    public ApiResponse<String> validAndSave(BizTrainPlace entity){
        RuntimeCheck.ifBlank(entity.getSchoolCode(),"请选择驾校");
        BizDriversSchool school = schoolService.findById(entity.getSchoolCode());
        RuntimeCheck.ifNull(school,"未找到驾校");

        RuntimeCheck.ifBlank(entity.getSubCode(), "请选择代培点");
        BizSubSchool subSchool = subSchoolService.findById(entity.getSubCode());
        RuntimeCheck.ifNull(subSchool, "未找到代培点信息");
        entity.setSubName(subSchool.getSubName());
        entity.setSchoolName(school.getSchoolName());
        entity.setPlaceId(genId());
        entity.setCreateTime(DateUtils.getNowTime());
        trainPlaceMapper.insertSelective(entity);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse<String> validAndUpdate(BizTrainPlace trainPlace){
        if(StringUtils.isNotBlank(trainPlace.getSchoolCode())){
            BizDriversSchool school = schoolService.findById(trainPlace.getSchoolCode());
            if(school != null){
                trainPlace.setSchoolName(school.getSchoolName());
            }
        }
        if(StringUtils.isNotBlank(trainPlace.getSubCode())){
            BizSubSchool sub = subSchoolService.findById(trainPlace.getSubCode());
            if(sub != null){
                trainPlace.setSubName(sub.getSubName());
            }
        }
        int i = update(trainPlace);
        return ApiResponse.success();
    }

    public void afterQuery(List<BizTrainPlace> re){
        if(CollectionUtils.isNotEmpty(re)){
            List<String> list = re.stream().map(BizTrainPlace::getSubCode).collect(Collectors.toList());
            List<BizSubSchool> schools = subSchoolService.findByIds(list);
            if(CollectionUtils.isNotEmpty(schools)){
                Map<String, BizSubSchool> schoolMap = schools.stream().collect(Collectors.toMap(BizSubSchool::getSubCode, p -> p));
                re.forEach(bizTrainPlace -> {
                    BizSubSchool school = schoolMap.get(bizTrainPlace.getSubCode());
                    bizTrainPlace.setYhXm(school.getSubFz());
                    bizTrainPlace.setYhhm(school.getSubPhone());
                });
            }
        }
    }


}
