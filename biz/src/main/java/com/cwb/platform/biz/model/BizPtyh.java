package com.cwb.platform.biz.model;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "biz_ptyh")
public class BizPtyh implements Serializable {
    /**
     * 用户id  
     */
    @Id
    @Column(name = "ID")
    private String id;

    /**
     * 账号(手机号)
     */
    @Column(name = "YH_ZH")
    private String yhZh;

    /**
     * 密码
     */
    @Column(name = "YH_MM")
    private String yhMm;

    /**
     * 创建人
     */
    @Column(name = "YH_CJR")
    private String yhCjr;

    /**
     * 创建时间
     */
    @Column(name = "CJSJ")
    private String cjsj;

    /**
     * 修改人
     */
    @Column(name = "YH_XGR")
    private String yhXgr;

    /**
     * 修改时间
     */
    @Column(name = "XGSJ")
    private String xgsj;

    /**
     * 姓名
     */
    @Column(name = "YH_XM")
    private String yhXm;

    /**
     * 类型(驾驶员、学员)
     */
    @Column(name = "YH_LX")
    private String yhLx;

    /**
     * 性别(1、男;2、女)
     */
    @Column(name = "YH_XB")
    private String yhXb;

    /**
     * 身份证号码
     */
    @Column(name = "YH_ZJHM")
    private String yhZjhm;

    /**
     * 密码有效期
     */
    @Column(name = "YH_MMYXQ")
    private String yhMmyxq;

    /**
     * 状态(未认证、已认证)
     */
    @Column(name = "YH_ZT")
    private String yhZt;

    /**
     * 是否缴费(0无 1已缴费)
     */
    @Column(name = "DD_SFJX")
    private String ddSfjx;

    /**
     * 微信OPEN_ID
     */
    @Column(name = "YH_OPEN_ID")
    private String yhOpenId;

    /**
     * 支付宝ID
     */
    @Column(name = "YH_ALIPAY_ID")
    private String yhAlipayId;

    /**
     * 用户头像
     */
    @Column(name = "YH_TX")
    private String yhTx;

    /**
     * 用户别名
     */
    @Column(name = "YH_BM")
    private String yhBm;

    /**
     * 用户自身邀请码
     */
    @Column(name = "YH_ZSYQM")
    private String yhZsyqm;

    /**
     * 用户应邀邀请码
     */
    @Column(name = "YH_YYYQM")
    private String yhYyyqm;

    /**
     * 学员是否已分配
     */
    @Column(name = "YH_IXY_SFFP")
    private String yhIxySffp;

    /**
     * 是否有驾照
     */
    @Column(name = "YH_SFYJZ")
    private String yhSfyjz;

    /**
     * 学员分配描述
     */
    @Column(name = "YH_FPMS")
    private String yhFpms;

    /**
     * 用户是否锁定
     */
    @Column(name = "YH_SFSD")
    private String yhSfsd;

    private static final long serialVersionUID = 1L;

    /**
     * 获取用户id  
     *
     * @return ID - 用户id  
     */
    public String getId() {
        return id;
    }

    /**
     * 设置用户id  
     *
     * @param id 用户id  
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取账号(手机号)
     *
     * @return YH_ZH - 账号(手机号)
     */
    public String getYhZh() {
        return yhZh;
    }

    /**
     * 设置账号(手机号)
     *
     * @param yhZh 账号(手机号)
     */
    public void setYhZh(String yhZh) {
        this.yhZh = yhZh;
    }

    /**
     * 获取密码
     *
     * @return YH_MM - 密码
     */
    public String getYhMm() {
        return yhMm;
    }

    /**
     * 设置密码
     *
     * @param yhMm 密码
     */
    public void setYhMm(String yhMm) {
        this.yhMm = yhMm;
    }

    /**
     * 获取创建人
     *
     * @return YH_CJR - 创建人
     */
    public String getYhCjr() {
        return yhCjr;
    }

    /**
     * 设置创建人
     *
     * @param yhCjr 创建人
     */
    public void setYhCjr(String yhCjr) {
        this.yhCjr = yhCjr;
    }

    /**
     * 获取创建时间
     *
     * @return CJSJ - 创建时间
     */
    public String getCjsj() {
        return cjsj;
    }

    /**
     * 设置创建时间
     *
     * @param cjsj 创建时间
     */
    public void setCjsj(String cjsj) {
        this.cjsj = cjsj;
    }

    /**
     * 获取修改人
     *
     * @return YH_XGR - 修改人
     */
    public String getYhXgr() {
        return yhXgr;
    }

    /**
     * 设置修改人
     *
     * @param yhXgr 修改人
     */
    public void setYhXgr(String yhXgr) {
        this.yhXgr = yhXgr;
    }

    /**
     * 获取修改时间
     *
     * @return XGSJ - 修改时间
     */
    public String getXgsj() {
        return xgsj;
    }

    /**
     * 设置修改时间
     *
     * @param xgsj 修改时间
     */
    public void setXgsj(String xgsj) {
        this.xgsj = xgsj;
    }

    /**
     * 获取姓名
     *
     * @return YH_XM - 姓名
     */
    public String getYhXm() {
        return yhXm;
    }

    /**
     * 设置姓名
     *
     * @param yhXm 姓名
     */
    public void setYhXm(String yhXm) {
        this.yhXm = yhXm;
    }

    /**
     * 获取类型(驾驶员、学员)
     *
     * @return YH_LX - 类型(驾驶员、学员)
     */
    public String getYhLx() {
        return yhLx;
    }

    /**
     * 设置类型(驾驶员、学员)
     *
     * @param yhLx 类型(驾驶员、学员)
     */
    public void setYhLx(String yhLx) {
        this.yhLx = yhLx;
    }

    /**
     * 获取性别(1、男;2、女)
     *
     * @return YH_XB - 性别(1、男;2、女)
     */
    public String getYhXb() {
        return yhXb;
    }

    /**
     * 设置性别(1、男;2、女)
     *
     * @param yhXb 性别(1、男;2、女)
     */
    public void setYhXb(String yhXb) {
        this.yhXb = yhXb;
    }

    /**
     * 获取身份证号码
     *
     * @return YH_ZJHM - 身份证号码
     */
    public String getYhZjhm() {
        return yhZjhm;
    }

    /**
     * 设置身份证号码
     *
     * @param yhZjhm 身份证号码
     */
    public void setYhZjhm(String yhZjhm) {
        this.yhZjhm = yhZjhm;
    }

    /**
     * 获取密码有效期
     *
     * @return YH_MMYXQ - 密码有效期
     */
    public String getYhMmyxq() {
        return yhMmyxq;
    }

    /**
     * 设置密码有效期
     *
     * @param yhMmyxq 密码有效期
     */
    public void setYhMmyxq(String yhMmyxq) {
        this.yhMmyxq = yhMmyxq;
    }

    /**
     * 获取状态(未认证、已认证)
     *
     * @return YH_ZT - 状态(未认证、已认证)
     */
    public String getYhZt() {
        return yhZt;
    }

    /**
     * 设置状态(未认证、已认证)
     *
     * @param yhZt 状态(未认证、已认证)
     */
    public void setYhZt(String yhZt) {
        this.yhZt = yhZt;
    }

    /**
     * 获取是否缴费(0无 1已缴费)
     *
     * @return DD_SFJX - 是否缴费(0无 1已缴费)
     */
    public String getDdSfjx() {
        return ddSfjx;
    }

    /**
     * 设置是否缴费(0无 1已缴费)
     *
     * @param ddSfjx 是否缴费(0无 1已缴费)
     */
    public void setDdSfjx(String ddSfjx) {
        this.ddSfjx = ddSfjx;
    }

    /**
     * 获取微信OPEN_ID
     *
     * @return YH_OPEN_ID - 微信OPEN_ID
     */
    public String getYhOpenId() {
        return yhOpenId;
    }

    /**
     * 设置微信OPEN_ID
     *
     * @param yhOpenId 微信OPEN_ID
     */
    public void setYhOpenId(String yhOpenId) {
        this.yhOpenId = yhOpenId;
    }

    /**
     * 获取支付宝ID
     *
     * @return YH_ALIPAY_ID - 支付宝ID
     */
    public String getYhAlipayId() {
        return yhAlipayId;
    }

    /**
     * 设置支付宝ID
     *
     * @param yhAlipayId 支付宝ID
     */
    public void setYhAlipayId(String yhAlipayId) {
        this.yhAlipayId = yhAlipayId;
    }

    /**
     * 获取用户头像
     *
     * @return YH_TX - 用户头像
     */
    public String getYhTx() {
        return yhTx;
    }

    /**
     * 设置用户头像
     *
     * @param yhTx 用户头像
     */
    public void setYhTx(String yhTx) {
        this.yhTx = yhTx;
    }

    /**
     * 获取用户别名
     *
     * @return YH_BM - 用户别名
     */
    public String getYhBm() {
        return yhBm;
    }

    /**
     * 设置用户别名
     *
     * @param yhBm 用户别名
     */
    public void setYhBm(String yhBm) {
        this.yhBm = yhBm;
    }

    /**
     * 获取用户自身邀请码
     *
     * @return YH_ZSYQM - 用户自身邀请码
     */
    public String getYhZsyqm() {
        return yhZsyqm;
    }

    /**
     * 设置用户自身邀请码
     *
     * @param yhZsyqm 用户自身邀请码
     */
    public void setYhZsyqm(String yhZsyqm) {
        this.yhZsyqm = yhZsyqm;
    }

    /**
     * 获取用户应邀邀请码
     *
     * @return YH_YYYQM - 用户应邀邀请码
     */
    public String getYhYyyqm() {
        return yhYyyqm;
    }

    /**
     * 设置用户应邀邀请码
     *
     * @param yhYyyqm 用户应邀邀请码
     */
    public void setYhYyyqm(String yhYyyqm) {
        this.yhYyyqm = yhYyyqm;
    }

    /**
     * 获取学员是否已分配
     *
     * @return YH_IXY_SFFP - 学员是否已分配
     */
    public String getYhIxySffp() {
        return yhIxySffp;
    }

    /**
     * 设置学员是否已分配
     *
     * @param yhIxySffp 学员是否已分配
     */
    public void setYhIxySffp(String yhIxySffp) {
        this.yhIxySffp = yhIxySffp;
    }

    /**
     * 获取是否有驾照
     *
     * @return YH_SFYJZ - 是否有驾照
     */
    public String getYhSfyjz() {
        return yhSfyjz;
    }

    /**
     * 设置是否有驾照
     *
     * @param yhSfyjz 是否有驾照
     */
    public void setYhSfyjz(String yhSfyjz) {
        this.yhSfyjz = yhSfyjz;
    }

    /**
     * 获取学员分配描述
     *
     * @return YH_FPMS - 学员分配描述
     */
    public String getYhFpms() {
        return yhFpms;
    }

    /**
     * 设置学员分配描述
     *
     * @param yhFpms 学员分配描述
     */
    public void setYhFpms(String yhFpms) {
        this.yhFpms = yhFpms;
    }

    /**
     * 获取用户是否锁定
     *
     * @return YH_SFSD - 用户是否锁定
     */
    public String getYhSfsd() {
        return yhSfsd;
    }

    /**
     * 设置用户是否锁定
     *
     * @param yhSfsd 用户是否锁定
     */
    public void setYhSfsd(String yhSfsd) {
        this.yhSfsd = yhSfsd;
    }

    public enum InnerColumn {
        id("ID"),
        yhZh("YH_ZH"),
        yhMm("YH_MM"),
        yhCjr("YH_CJR"),
        cjsj("CJSJ"),
        yhXgr("YH_XGR"),
        xgsj("XGSJ"),
        yhXm("YH_XM"),
        yhLx("YH_LX"),
        yhXb("YH_XB"),
        yhZjhm("YH_ZJHM"),
        yhMmyxq("YH_MMYXQ"),
        yhZt("YH_ZT"),
        ddSfjx("DD_SFJX"),
        yhOpenId("YH_OPEN_ID"),
        yhAlipayId("YH_ALIPAY_ID"),
        yhTx("YH_TX"),
        yhBm("YH_BM"),
        yhZsyqm("YH_ZSYQM"),
        yhYyyqm("YH_YYYQM"),
        yhIxySffp("YH_IXY_SFFP"),
        yhSfyjz("YH_SFYJZ"),
        yhFpms("YH_FPMS"),
        yhSfsd("YH_SFSD");

        private final String column;

        public String value() {
            return this.column;
        }

        public String getValue() {
            return this.column;
        }

        InnerColumn(String column) {
            this.column = column;
        }

        public String desc() {
            return this.column + " DESC";
        }

        public String asc() {
            return this.column + " ASC";
        }
    }
}