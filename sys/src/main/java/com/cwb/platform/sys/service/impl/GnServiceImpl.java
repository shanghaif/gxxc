package com.cwb.platform.sys.service.impl;

import com.cwb.platform.sys.base.BaseServiceImpl;
import com.cwb.platform.sys.base.LimitedCondition;
import com.cwb.platform.sys.bean.Menu;
import com.cwb.platform.sys.service.*;
import com.cwb.platform.util.commonUtil.DateUtils;
import com.cwb.platform.util.commonUtil.JsonUtil;
import com.cwb.platform.util.exception.RuntimeCheck;
import com.cwb.platform.sys.mapper.*;
import com.cwb.platform.sys.model.*;
import com.cwb.platform.util.bean.ApiResponse;
import com.cwb.platform.util.bean.SimpleCondition;
import javafx.beans.property.SimpleListProperty;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GnServiceImpl extends BaseServiceImpl<SysGn, String> implements GnService {

    @Autowired
    private SysYhJsMapper yhJsMapper;
    @Autowired
    private SysJsGnMapper jsGnMapper;
    @Autowired
    private JgService jgService;
    @Autowired
    private JsService jsService;
    @Autowired
    private GnService gnService;
    @Autowired
    private SysPtjgMapper ptjgMapper;
    @Autowired
    private SysJgsqlbMapper jgsqMapper;
    @Autowired
    private SysFwgnMapper gnMapper;
    @Autowired
    private SysJgsqlbMapper jgsqlbMapper;
    @Autowired
    private FwService fwService;
    @Autowired
    private YhService yhService;
    @Autowired
    private SysYhGnMapper yhGnMapper;
    @Override
    protected Mapper<SysGn> getBaseMapper() {
        return gnMapper;
    }

    @Override
    public ApiResponse<String> saveEntity(SysGn entity) {
    	SysGn selectByPrimaryKey = gnMapper.selectByPrimaryKey(entity.getGndm());
    	if(selectByPrimaryKey!=null) {
    		return ApiResponse.fail("功能代码已存在");
    	}
    	String creator = getOperateUser();
        entity.setCjr(creator);
        entity.setCjsj(DateUtils.getNowTime());
        gnMapper.insert(entity);

        SysJgsq jgsq = new SysJgsq();
        jgsq.setFwdm(entity.getFwdm());
        jgsq.setId(genId());
        jgsq.setJgdm("100");
        jgsq.setGndm(entity.getGndm());
        jgsq.setCjsj(DateUtils.getNowTime());
        jgsq.setCjr(getOperateUser());
        jgsqlbMapper.insertSelective(jgsq);
        return ApiResponse.success();
    }

    @Override
    public List<SysYhGn> getYhGnList(String yhId) {
        SimpleCondition condition = new SimpleCondition(SysYhGn.class);
        condition.eq(SysYhGn.InnerColumn.yhid,yhId);
        List<SysYhGn> userFunctions = yhGnMapper.selectByExample(condition);
        if (userFunctions.size() == 0){
            updateUserFunctins(yhId);
        }
        return userFunctions;
    }

    @Override
    public void updateUserFunctins(String yhdId) {
        RuntimeCheck.ifBlank(yhdId,"请选择用户");
        SysYh user = yhService.findById(yhdId);
        RuntimeCheck.ifNull(user,"用户不存在");
        updateUserFunctins(user);
    }

    @Override
    public void updateUserFunctins(SysYh user) {
        RuntimeCheck.ifNull(user,"用户不存在");
        List<SysGn> functions = getUserFunctions(user);

        SimpleCondition condition = new SimpleCondition(SysYhGn.class);
        condition.eq(SysYhGn.InnerColumn.yhid,user.getYhid());
        yhGnMapper.deleteByExample(condition);

        for (SysGn function : functions) {
            SysYhGn yhGn = new SysYhGn();
            yhGn.setYhid(user.getYhid());
            yhGn.setGndm(function.getGndm());
            yhGn.setApiQz(function.getApiQz());
            yhGnMapper.insertSelective(yhGn);
        }
    }

    @Override
    public boolean fillCondition(LimitedCondition condition){
        condition.setOrderByClause("px asc");
        return true;
    }


    @Override
    public ApiResponse<String> updateEntity(SysGn gn) {
    	gn.setXgr(getOperateUser());
    	gn.setXgsj(DateUtils.getNowTime());
    	SysGn gndm = gnMapper.selectByPrimaryKey(gn.getGndm());
    	if (StringUtils.isEmpty(gn.getFjd())){
    	    gn.setFjd(null);
        }
    	if (gndm==null) {
    		save(gn);
		}else {
			update(gn);
		}
        return ApiResponse.success();
    }

    @Override
    public ApiResponse<String> setRoleFunctions(String jsdm, List<String> gndms) {
        RuntimeCheck.ifBlank(jsdm,"角色代码不能为空");
        List<SysJs> roles = jsService.findEq(SysJs.InnerColumn.jsId,jsdm);
        RuntimeCheck.ifTrue(roles.size() == 0,"未找到记录");

        SimpleCondition condition = new SimpleCondition(SysJsGn.class);
        condition.eq(SysJsGn.InnerColumn.jsdm,jsdm);
        jsGnMapper.deleteByExample(condition);

        if (gndms == null || gndms.size() == 0)return ApiResponse.success();
        List<SysGn> functionList = findIn(SysGn.InnerColumn.gndm,gndms);

        String createUser = getOperateUser();

        List<SysJsGn> jsGns = new ArrayList<>();
        for (SysGn function : functionList) {
            SysJsGn jsGn = new SysJsGn();
            jsGn.setGndm(function.getGndm());
            jsGn.setJsdm(jsdm);
            jsGn.setCjr(createUser);
            jsGn.setCjsj(DateUtils.getNowTime());
            jsGn.setFwdm(function.getFwdm());
            jsGn.setFgndm(function.getFjd());
            jsGn.setId(genId());
            jsGns.add(jsGn);
        }
        jsGnMapper.insertList(jsGns);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse<List<SysGn>> getRoleFunctions(String jsdm) {
        SimpleCondition condition = new SimpleCondition(SysJsGn.class);
        condition.eq(SysJsGn.InnerColumn.jsdm,jsdm);
        condition.eq(SysJsGn.InnerColumn.zt,"00");
        List<SysJsGn> roleFunctions = jsGnMapper.selectByExample(condition);
        if (roleFunctions.size() == 0){
            List<SysGn> gnList = new ArrayList<>();
            return ApiResponse.success(gnList);
        }
        List<String> gndms = roleFunctions.stream().map(SysJsGn::getGndm).collect(Collectors.toList());
        List<SysGn> functions = gnService.findIn(SysGn.InnerColumn.gndm,gndms);
        return ApiResponse.success(functions);
    }

    private List<Menu> convertToMenus(List<SysGn> functions){
        List<Menu> menuList = new ArrayList<>();
        for (SysGn function : functions) {
            Menu menu = new Menu(function);
            menuList.add(menu);
        }
        return menuList;
    }


    @Override
    public List<Menu> getMenuTree(SysYh user) {
        List<SysGn> functions = getUserFunctions(user);
        if (functions == null || functions.size() == 0)return new ArrayList<>();
        Set<String> functionCodes = functions.stream().map(SysGn::getGndm).collect(Collectors.toSet());
        findParent(functions,functionCodes);
        functions.sort(Comparator.comparing(SysGn::getPx));
        List<Menu> menuList = convertToMenus(functions);
        return buildMenuTree(menuList);
    }


    private void findParent(List<SysGn> functions,Set<String> functionCodes){
        Set<String> addCodes = new HashSet<>();
        Set<String> fatherCodes = new HashSet<>();
        for (SysGn function : functions) {
            if (StringUtils.isEmpty(function.getFjd()))continue;
            if (functionCodes.contains(function.getFjd()))continue;
            fatherCodes.add(function.getFjd());
            addCodes.add(function.getFjd());
        }
        if (fatherCodes.size() != 0){
            List<SysGn> fathers = findIn(SysGn.InnerColumn.gndm,fatherCodes);
            for (SysGn father : fathers) {
                if (StringUtils.isEmpty(father.getFjd()))continue;
                if (functionCodes.contains(father.getFjd()))continue;
                addCodes.add(father.getFjd());
            }
        }
        if (addCodes.size() != 0){
            List<SysGn> addFunctions = findIn(SysGn.InnerColumn.gndm,addCodes);
            functions.addAll(addFunctions);
        }
    }

    @Override
    public ApiResponse<String> initMenu(List<Menu> menus) {
        List<SysGn> functions = new ArrayList<>();
        addToMenuList(menus,functions);
        for (SysGn function : functions) {
            SysGn exist = gnMapper.selectByPrimaryKey(function.getGndm());
            if (exist == null){
                gnService.save(function);
            }else{
                gnService.update(function);
            }
        }
        return ApiResponse.success();
    }

    @Override
    public ApiResponse<String> setOrgFunctions(String jgdm, List<String> gndmList) {
        RuntimeCheck.ifBlank(jgdm,"请选择机构");
        if ("100".equals(jgdm))return ApiResponse.success();
        SimpleCondition condition = new SimpleCondition(SysJgsq.class);
        condition.eq(SysJgsq.InnerColumn.jgdm,jgdm);
        jgsqlbMapper.deleteByExample(condition);

        String creator = getOperateUser();

        for (String s : gndmList) {
            SysJgsq jgsq = new SysJgsq();
            jgsq.setCjr(creator);
            jgsq.setCjsj(DateUtils.getNowTime());
            jgsq.setGndm(s);
            jgsq.setJgdm(jgdm);
            jgsq.setId(genId());
            // todo 服务代码
//            jgsq.setFwdm();
            jgsqlbMapper.insertSelective(jgsq);
        }
        return ApiResponse.success();
    }

    private void addToMenuList(List<Menu> menuList,List<SysGn> functionList){
        List<SysGn> functions = convertToFunctionList(menuList);
        functionList.addAll(functions);
        for (Menu menu : menuList) {
            if (menu.getChildren() != null){
                addToMenuList(menu.getChildren(),functionList);
            }
        }
    }

    private List<SysGn> convertToFunctionList(List<Menu> menus){
        List<SysGn> list = new ArrayList<>();
        String creartor = "初始导入";

        for (Menu menu : menus) {
            SysGn function = new SysGn();
            function.setFwdm("1");
            function.setZt("00");
            function.setCjr(creartor);
            function.setCjsj(DateUtils.getNowTime());
            function.setFjd(menu.getPid());
            function.setGndm(menu.getName());
            function.setUrl(menu.getName());
            function.setGnmc(menu.getTitle());
            function.setTb(menu.getIcon());
            list.add(function);
        }
        return list;
    }
    private List<Menu> buildMenuTree(List<Menu> menuList){

        Map<String, List<Menu>> menuIdMap = menuList.stream().collect(Collectors.groupingBy(Menu::getId));
        List<Menu> root = new ArrayList<>();
        for (Menu menu : menuList) {
            if (StringUtils.isEmpty(menu.getPid())){
                root.add(menu);
                continue;
            }
            List<Menu> menus = menuIdMap.get(menu.getPid());
            Menu father = null;
            if(CollectionUtils.isNotEmpty(menus)){
                father = menus.get(0);
            }
            if (father == null)continue;
            if (father.getChildren() == null){
                List<Menu> children = new ArrayList<>();
                children.add(menu);
                father.setChildren(children);
            }else{
                father.getChildren().add(menu);
            }
        }
        return root;
    }


    /**
     * 查找某个服务的所有功能
     *
     * @param serviceCode 服务代码
     * @return 功能列表
     */
    @Override
    public List<SysGn> findByServiceCode(String serviceCode) {
        RuntimeCheck.ifBlank(serviceCode,"请输入服务代码");
        return findEq(SysGn.InnerColumn.fwdm,serviceCode);
    }

    /**
     * 设置机构功能
     *
     * @param orgCode      机构代码
     * @param functionCode 功能代码
     * @return 执行结果
     */
    @Override
    public ApiResponse<String> setOrgFunction(String orgCode, List<String> functionCode) {
        SysYh user = getCurrentUser();
        RuntimeCheck.ifBlank(orgCode,"请选择机构");
        SysJg org = jgService.findByOrgCode(orgCode);
        RuntimeCheck.ifNull(org,"机构不存在");

        if ("100".equals(orgCode))return ApiResponse.success();

        // 删除旧数据
        SimpleCondition condition = new SimpleCondition(SysJg.class);
        condition.eq(SysJg.InnerColumn.jgdm,orgCode);
        jgsqMapper.deleteByExample(condition);

        // 插入新数据
        List<SysGn> functions = findIn(SysGn.InnerColumn.gndm,functionCode);

        for (SysGn function : functions) {
            SysJgsq jgsq = new SysJgsq();
            jgsq.setCjr(user.getYhid());
            jgsq.setCjsj(DateUtils.getNowTime());
            jgsq.setFwdm(function.getFwdm());
            jgsq.setGndm(function.getGndm());
            jgsq.setJgdm(orgCode);
            jgsqMapper.insertSelective(jgsq);
        }
        return ApiResponse.success();
    }

    /**
     * 查询机构拥有的功能列表
     *
     * @param gndm 机构代码
     * @return 功能列表
     */
    @Override
    public List<SysGn> findByGndm(String gndm) {
        return findEq(SysGn.InnerColumn.gndm,gndm);
    }

    @Override
    public List<SysGn> getUserFunctions(SysYh user) {
        if ("su".equals(user.getLx())){
            SimpleCondition condition = new SimpleCondition(SysGn.class);
            condition.eq(SysGn.InnerColumn.zt,"00");
            return findByCondition(condition);
        }
        List<String> functionCodes = getUserFunctionCodes(user);
        List<String> orgFunctionCodes = getOrgFunctionCodes(user.getJgdm());
        functionCodes.retainAll(orgFunctionCodes);
        if (functionCodes.size() == 0)return new ArrayList<>();
        SimpleCondition condition = new SimpleCondition(SysGn.class);
        condition.in(SysGn.InnerColumn.gndm,functionCodes);
        condition.eq(SysGn.InnerColumn.zt,"00");
        return findByCondition(condition);
    }

    @Override
    public List<SysGn> getOrgFunctions(String orgCode) {
        List<String> functionCodes = getOrgFunctionCodes(orgCode);
        return gnService.findIn(SysGn.InnerColumn.gndm,functionCodes);
    }
    @Override
    public List<String> getOrgFunctionCodes(String orgCode) {
        SimpleCondition condition = new SimpleCondition(SysJgsq.class);
        condition.eq(SysJgsq.InnerColumn.jgdm,orgCode);
        List<SysJgsq> jgsqs = jgsqMapper.selectByExample(condition);
        if (jgsqs.size() == 0)return new ArrayList<>();
        List<String> functionCodes = jgsqs.stream().map(SysJgsq::getGndm).collect(Collectors.toList());
        return functionCodes;
    }

    private void initjgsq(){
        List<SysGn> functions = findAll();

        String creator = getOperateUser();
        for (SysGn function : functions) {
            SysJgsq jgsq = new SysJgsq();
            jgsq.setFwdm(function.getFwdm());
            jgsq.setGndm(function.getGndm());
            jgsq.setId(genId());
            jgsq.setJgdm("100");
            jgsq.setCjsj(DateUtils.getNowTime());
            jgsq.setCjr(creator);
            jgsqlbMapper.insertSelective(jgsq);
        }
    }

    @Override
    public List<String> getUserFunctionCodes(SysYh user) {
        // 获取用户角色
        SimpleCondition condition = new SimpleCondition(SysYhJs.class);
        condition.eq(SysYhJs.InnerColumn.yhId,user.getYhid());
        List<SysYhJs> yhJs = yhJsMapper.selectByExample(condition);
        if (yhJs.size() == 0)  return new ArrayList<>();

        // 获取角色功能
        List<String> jsdms = yhJs.stream().map(SysYhJs::getJsId).collect(Collectors.toList());
        return getRolesFunctionCodes(jsdms);
    }

    @Override
    public List<SysGn> getRolesFunctions(List<String> jsdms) {
        if (jsdms == null || jsdms.size() == 0)return new ArrayList<>();
        List<String> functionCodes = getRolesFunctionCodes(jsdms);
        if (functionCodes.size() == 0)return new ArrayList<>();

//        SysYh user = getCurrentUser();
//        List<String> orgFunctionCodes = getOrgFunctionCodes(user.getJgdm());
//        functionCodes.retainAll(orgFunctionCodes);
        return findIn(SysGn.InnerColumn.gndm,functionCodes);
    }

    @Override
    public List<String> getRolesFunctionCodes(List<String> jsdms) {
        if (jsdms == null || jsdms.size() == 0)return new ArrayList<>();
        SysJs role = jsService.findById(jsdms.get(0));
        if (role == null)return new ArrayList<>();
        SimpleCondition condition = new SimpleCondition(SysJsGn.class);
        condition.in(SysJsGn.InnerColumn.jsdm,jsdms);
        List<SysJsGn> roleFunctions = jsGnMapper.selectByExample(condition);
        if (roleFunctions.size() == 0)return new ArrayList<>();
        return roleFunctions.stream().map(SysJsGn::getGndm).collect(Collectors.toList());
    }

    private List<SysFw> getAllPermissionTreeWithChecked(List<SysFw> services,List<SysGn> functions){
        SysYh user = getCurrentUser();
        List<SysGn> allFunctions = getOrgFunctions(user.getJgdm());
        List<SysFw> allServices = fwService.findAll();

        List<String> functionCodes = functions.stream().map(SysGn::getGndm).collect(Collectors.toList());

        Map<String,SysFw> serviceMap = allServices.stream().collect(Collectors.toMap(SysFw::getFwdm,p->p));
        Map<String,SysGn> functionMap = allFunctions.stream().collect(Collectors.toMap(SysGn::getGndm, p->p));

//        List<String> serviceCodes = new ArrayList<>();
//        for (SysGn function : functions) {
//            String serviceCode = function.getFwdm();
//            if (StringUtils.isEmpty(serviceCode))continue;
//            if (serviceMap.containsKey(serviceCode))continue;
//            serviceCodes.add(serviceCode);
//        }
//        if (serviceCodes.size() != 0){
//            List<SysFw> addServices = fwService.findIn(SysFw.InnerColumn.fwdm,serviceCodes);
//            services.addAll(addServices);
//        }
        for (SysGn function : allFunctions) {
            if (functionCodes.contains(function.getGndm())){
                function.setChecked("true");
            }
            String fatherCode = function.getFjd();
            // 如果没有父节点，则代码这是个一级功能
            if (StringUtils.isEmpty(fatherCode)){
                SysFw father = serviceMap.get(function.getFwdm());
                if (father == null)continue;
                if (father.getFunctions() == null){
                    List<SysGn> children = new ArrayList<>();
                    children.add(function);
                    father.setFunctions(children);
                }else{
                    if (father.getFunctions().contains(function))continue;
                    father.getFunctions().add(function);
                }
            }else{
                SysGn father = functionMap.get(fatherCode);
                if (father == null)continue;
                if (father.getChildren() == null){
                    List<SysGn> children = new ArrayList<>();
                    children.add(function);
                    father.setChildren(children);
                }else{
                    if (father.getChildren().contains(function))continue;
                    father.getChildren().add(function);
                }
            }
        }
        return allServices;

    }
    private List<SysFw> getPermissionTree(List<SysFw> services,List<SysGn> functions){
        return getPermissionTree(services,functions,null);
    }
    private List<SysFw> getPermissionTree(List<SysFw> services,List<SysGn> functions,List<String> hasFunctionCodes){
        Map<String,SysFw> serviceMap = services.stream().collect(Collectors.toMap(SysFw::getFwdm,p->p));
        Map<String,SysGn> functionMap = functions.stream().collect(Collectors.toMap(SysGn::getGndm, p->p));

        List<String> serviceCodes = new ArrayList<>();
        for (SysGn function : functions) {
            String serviceCode = function.getFwdm();
            if (StringUtils.isEmpty(serviceCode))continue;
            if (serviceMap.containsKey(serviceCode))continue;
            serviceCodes.add(serviceCode);
        }
        if (serviceCodes.size() != 0){
            List<SysFw> addServices = fwService.findIn(SysFw.InnerColumn.fwdm,serviceCodes);
            services.addAll(addServices);
            for (SysFw service : addServices) {
                serviceMap.put(service.getFwdm(),service);
            }
        }
        for (SysGn function : functions) {
            String fatherCode = function.getFjd();
            if (hasFunctionCodes != null && hasFunctionCodes.contains(function.getGndm())){
                function.setChecked("checked");
            }
            // 如果没有父节点，则代码这是个一级功能
            if (StringUtils.isEmpty(fatherCode)){
                SysFw father = serviceMap.get(function.getFwdm());
                if (father == null)continue;
                if (father.getFunctions() == null){
                    List<SysGn> children = new ArrayList<>();
                    children.add(function);
                    father.setFunctions(children);
                }else{
                    father.getFunctions().add(function);
                }
            }else{
                SysGn father = functionMap.get(fatherCode);
                if (father == null)continue;
                if (father.getChildren() == null){
                    List<SysGn> children = new ArrayList<>();
                    children.add(function);
                    father.setChildren(children);
                }else{
                    father.getChildren().add(function);
                }
            }
        }
        return services;
    }

    @Override
    public List<SysFw> getAllPermissionTree() {
        // todo  机构分权
        return getPermissionTree(fwService.findAll(),gnMapper.selectAll());
    }

    @Override
    public List<SysFw> getOrgPermissionTree(String jgdm) {
        List<String> hasFunctionCodes = getOrgFunctionCodes(jgdm);
        SysJg org = jgService.findByOrgCode(jgdm);
        if (org == null)return new ArrayList<>();
        return getPermissionTree(fwService.findByJgdm(jgdm),getOrgFunctions(org.getFjgdm()),hasFunctionCodes);
    }

    @Override
    public List<SysFw> getUserPermissionTree(SysYh user) {
        List<SysGn> functions = getUserFunctions(user);
        List<String> serviceCodes = functions.stream().map(SysGn::getFwdm).collect(Collectors.toList());
        List<SysFw> services = fwService.findIn(SysFw.InnerColumn.fwdm,serviceCodes);
        return getPermissionTree(services,functions);
    }

    @Override
    public List<SysFw> getRolePermissionTree(String jsdm) {
        List<SysGn> functions = getRolesFunctions(Collections.singletonList(jsdm));
        List<SysFw> services;
        if (functions.size() != 0){
            Set<String> serviceCodes = functions.stream().map(SysGn::getFwdm).collect(Collectors.toSet());
            services = fwService.findIn(SysFw.InnerColumn.fwdm,serviceCodes);
        }else{
            services = new ArrayList<>();
        }
        return getAllPermissionTreeWithChecked(services,functions);
    }


}
