<style lang="less">
	@import '../../../styles/common.less';
</style>
<template>
	<div class="boxbackborder">
		<Row style="padding-bottom: 16px;">
        	<search-items :parent="v"></search-items>
            <Button type="info" @click="exportData">
                <Icon type="ios-download-outline"></Icon>
            </Button>
            <Tooltip content="批量导入" placement="bottom">
                <Button type="success" @click="componentName='batchImport'">
                    <Icon type="md-arrow-round-up"></Icon>
                </Button>
            </Tooltip>
            <!--<Tooltip content="批量打款" placement="bottom">-->
                <!--<Button type="success" @click="batchDk">-->
                    <!--<Icon type="checkmark-circled"></Icon>-->
                <!--</Button>-->
            <!--</Tooltip>-->
        </Row>
        <Row style="position: relative;">
        	<Table :height="tableHeight" :columns="tableColumns" :data="pageData"></Table>
        </Row>
        <Row class="margin-top-10 pageSty">
            <pager :parent="v"></pager>
        </Row>
        <component :is="componentName"></component>
	</div>
</template>

<script>
	import audit from './audit'
	import confirm from './confirm'
    import batchImport from './batchImport'

    export default {
        name: 'tx',
        components: {confirm,audit,batchImport},
        data() {
            return {
                v:this,
                SpinShow: true,
                apiRoot:this.apis.tx,
                tableHeight: 220,
                componentName: '',
                choosedItem: null,
                tableColumns: [
                    {title: "#", width: 60, type: 'index'},
                    {title:'姓名',key:'yhMc',searchKey:'yhMcLike'},
                    {title:'身份证号',key:'yhMc',searchKey:'yhMcLike'},
                    {title:'联系电话',key:'yhMc',searchKey:'yhMcLike'},
                    // {title:'佣金明细',key:'yjId'},
                    // {title:'提现方式',key:'ttFs',dict:'ZDCLK0047',searchType:'dict'},
                    {title:'提现金额',key:'ttJe',render:(h,p)=>{
                            return h('div',parseFloat(p.row.ttJe/100)+'元')
                        }},
                    {title:'申请时间',key:'ttSj'},
                    // {title:'提现状态',key:'ttZt',dict:'ZDCLK0048',searchType:'dict'},
                    {title:'审核人',key:'ttShr'},
                    {title:'审核时间',key:'shsj'},
                    // {title:'提现审核状态',key:'ttShzt',dict:'ZDCLK0049'},
                    // {title:'银行卡号',key:'ttYhkh'},
                    // {title:'所属银行',key:'ttSsyh'},
                    // {title:'提现姓名',key:'txXm'},
                    {title:'备注',key:'ttBz',width:250},
                    {
                        title: '操作',
                        key: 'action',
                        width: 120,
                        fixed:'right',
                        render: (h, params) => {
                            let buttons = [];
                            if (params.row.ttShzt === '0'){
                                buttons.push(
                                    this.util.buildButton(this,h,'success','md-checkmark','审核',()=>{
                                        this.choosedItem = params.row;
                                        this.componentName = 'audit'
                                    }),
                                )
                            }
                            if (params.row.ttShzt === '1' && params.row.ttZt == '0'){
                                buttons.push(
                                    h('Tooltip',
                                        {props: {placement: 'top',content: '打款',}},
                                        [
                                            h('Button', {
                                                props: {type: 'success',icon: 'md-checkmark-circle',shape: 'circle',size: 'small'},
                                                style: {margin: '0 8px 0 0'},
                                                on: {click:()=> {
                                                        this.dk(params.row.id)
                                                    }
                                                }
                                            }),
                                        ]
                                    ),
                                    this.util.buildButton(this,h,'info','md-checkmark-circle','手动确认',()=>{
                                        this.choosedItem = params.row;
                                        this.componentName = 'confirm'
                                    }),
                                )
                            }
                            return h('div', buttons);
                        }
                    }
                ],
                pageData: [],
                form: {
                    ttZt:'0',
                    total: 0,
                    pageNum: 1,
                    pageSize: 8,
                },
            }
        },
        created() {
            this.util.initTable(this)
        },
        methods: {
            exportData(){
                let params = {
                    exportType:'ptyh',
                    cols:'用户名称,提现方式,提现金额,提现时间,提现状态,银行卡号,开户行,提现姓名',
                    keys:'yhMc,ttFs,ttJe,ttSj,ttZt,ttYhkh,ttKhh,txXm'
                }
                window.open(this.apis.exportData+"?exportType=tx&cols="+params.cols+"&keys="+params.keys);
            },
            batchDk(){
                this.dk();
            },
            dk(id){
                let p = {};
                if (id){
                    p.id = id;
                }
                this.$http.post(this.apis.tx.dk,p).then((res)=>{
                    if (res.code === 200){
                        this.util.getPageData(this);
                    }else{
                        this.$Message.error(res.message);
                    }
                })
            }
        }
    }
</script>
