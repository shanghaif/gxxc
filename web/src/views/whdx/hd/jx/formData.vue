<style lang="less">
	@import '../../../../styles/common.less';
</style>
<template>
	<div class="margin-top-20">
		<textarea id="articleEditor"></textarea>
	</div>
</template>

<script>
    import tinymce from 'tinymce';
	export default {
		name: 'byxxForm',
		components:{tinymce},
		data() {
			return {
			    v:this,
                operate:'保养',
                spinShow:true,
				saveUrl:this.apis.maintain.ADD,
				showModal: true,
				readonly: false,
				formItem: {
				},
                formInputs:[
                    {label:'车牌号码',prop:'vHphm',readonly:true},
                    {label:'保养金额',prop:'bydByje'},
                ],
                ruleInline:{
				}
			}
		},
		created(){
		    this.init();
		},
		methods: {
            init () {
                this.$nextTick(() => {
                    let vm = this;
                    let height = document.body.offsetHeight - 300;
                    tinymce.init({
                        selector: '#tinymceEditer',
                        branding: false,
                        elementpath: false,
                        height: height,
                        language: 'zh_CN.GB2312',
                        menubar: 'edit insert view format table tools',
                        plugins: [
                            'advlist autolink lists link image charmap print preview hr anchor pagebreak imagetools',
                            'searchreplace visualblocks visualchars code fullpage',
                            'insertdatetime media nonbreaking save table contextmenu directionality',
                            'emoticons paste textcolor colorpicker textpattern imagetools codesample'
                        ],
                        toolbar1: ' newnote print preview | undo redo | insert | styleselect | forecolor backcolor bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image emoticons media codesample',
                        autosave_interval: '20s',
                        image_advtab: true,
                        table_default_styles: {
                            width: '100%',
                            borderCollapse: 'collapse'
                        },
                        setup: function (editor) {
                            editor.on('init', function (e) {
                                vm.spinShow = false;
                                if (localStorage.editorContent) {
                                    tinymce.get('tinymceEditer').setContent(localStorage.editorContent);
                                }
                            });
                            editor.on('keydown', function (e) {
                                localStorage.editorContent = tinymce.get('tinymceEditer').getContent();
                            });
                        }
                    });
                });
            }
		}
	}
</script>

<style>

</style>
