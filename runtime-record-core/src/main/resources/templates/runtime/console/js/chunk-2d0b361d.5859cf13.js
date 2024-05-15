(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-2d0b361d"],{"27ac":function(e,t,a){"use strict";a.r(t);var l=function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{staticClass:"app-container"},[a("el-form",{staticClass:"demo-form-inline",attrs:{model:e.queryTable,inline:!0}},[a("el-row",[a("el-col",{attrs:{span:6}},[a("el-form-item",{attrs:{label:"用户名","label-width":"100"}},[a("el-input",{staticStyle:{width:"240px"},model:{value:e.queryTable.userName,callback:function(t){e.$set(e.queryTable,"userName",t)},expression:"queryTable.userName"}})],1)],1),a("el-col",{attrs:{span:6}},[a("el-form-item",{attrs:{label:"用户ID"}},[a("el-input",{staticStyle:{width:"240px"},model:{value:e.queryTable.userId,callback:function(t){e.$set(e.queryTable,"userId",t)},expression:"queryTable.userId"}})],1)],1),a("el-col",{attrs:{span:6}},[a("el-form-item",{attrs:{label:"企业名称"}},[a("el-input",{staticStyle:{width:"240px"},model:{value:e.queryTable.companyName,callback:function(t){e.$set(e.queryTable,"companyName",t)},expression:"queryTable.companyName"}})],1)],1),a("el-col",{attrs:{span:6}},[a("el-form-item",{attrs:{label:"企业ID"}},[a("el-input",{staticStyle:{width:"240px"},model:{value:e.queryTable.companyId,callback:function(t){e.$set(e.queryTable,"companyId",t)},expression:"queryTable.companyId"}})],1)],1),a("el-col",{attrs:{span:6}},[a("el-form-item",{attrs:{label:"追踪码"}},[a("el-input",{staticStyle:{width:"240px"},model:{value:e.queryTable.traceId,callback:function(t){e.$set(e.queryTable,"traceId",t)},expression:"queryTable.traceId"}})],1)],1),a("el-col",{attrs:{span:6}},[a("el-form-item",{attrs:{label:"请求方式"}},[a("el-select",{attrs:{clearable:"",placeholder:"请选择请求方式"},model:{value:e.queryTable.httpMethod,callback:function(t){e.$set(e.queryTable,"httpMethod",t)},expression:"queryTable.httpMethod"}},[a("el-option",{attrs:{label:"GET",value:"GET"}}),a("el-option",{attrs:{label:"POST",value:"POST"}}),a("el-option",{attrs:{label:"PUT",value:"PUT"}}),a("el-option",{attrs:{label:"DELETE",value:"DELETE"}})],1)],1)],1),a("el-col",{attrs:{span:6}},[a("el-form-item",{attrs:{label:"请求地址"}},[a("el-input",{staticStyle:{width:"260px"},model:{value:e.queryTable.url,callback:function(t){e.$set(e.queryTable,"url",t)},expression:"queryTable.url"}})],1)],1),a("el-col",{attrs:{span:6}},[a("el-form-item",{attrs:{label:"是否成功"}},[a("el-select",{attrs:{clearable:"",placeholder:"请选择是否成功"},model:{value:e.queryTable.success,callback:function(t){e.$set(e.queryTable,"success",t)},expression:"queryTable.success"}},[a("el-option",{attrs:{label:"成功",value:"YES"}}),a("el-option",{attrs:{label:"失败",value:"NO"}})],1)],1)],1),a("el-col",{attrs:{span:12}},[a("el-form-item",{attrs:{label:"发生时间"}},[a("el-date-picker",{attrs:{"value-format":"timestamp",type:"datetimerange","range-separator":"至","start-placeholder":"开始日期","end-placeholder":"结束日期"},model:{value:e.createTimeRange,callback:function(t){e.createTimeRange=t},expression:"createTimeRange"}})],1)],1),a("el-col",{attrs:{offset:10,span:2}},[a("el-form-item",[a("el-button",{attrs:{type:"primary"},on:{click:e.queryTablePage}},[e._v("查询")])],1)],1)],1)],1),a("el-table",{attrs:{data:e.articleTable,border:"",height:"600"}},[a("el-table-column",{attrs:{type:"index",width:"50",align:"center"}}),a("el-table-column",{attrs:{prop:"traceId",label:"追踪码",width:"200"}}),a("el-table-column",{attrs:{prop:"url",label:"请求地址","min-width":"260"}}),a("el-table-column",{attrs:{label:"请求方式",width:"100",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("el-tag",[e._v(e._s(t.row.httpMethod))])]}}])}),a("el-table-column",{attrs:{prop:"params",label:"请求参数","min-width":"300"}}),a("el-table-column",{attrs:{label:"是否成功",width:"80",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return["YES"===t.row.success?a("el-tag",{attrs:{type:"success"}},[e._v(e._s(t.row.success))]):a("el-tag",{attrs:{type:"danger"}},[e._v(e._s(t.row.success))])]}}])}),a("el-table-column",{attrs:{prop:"createTime",label:"创建时间","min-width":"160"}}),a("el-table-column",{attrs:{prop:"companyName",label:"企业名称",width:"200"}}),a("el-table-column",{attrs:{prop:"userName",label:"用户名称",width:"200"}}),a("el-table-column",{attrs:{prop:"mappingMethod",label:"关联方法",width:"140"}}),a("el-table-column",{attrs:{fixed:"right",label:"操作",width:"120",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("el-button",{attrs:{type:"text",size:"small"},on:{click:function(a){return e.documentDetail(t.row)}}},[e._v("详情")])]}}])})],1),a("el-pagination",{attrs:{"current-page":e.currentPage,"page-sizes":[50,100,200,300,400,500],"page-size":50,layout:"total, sizes, prev, pager, next, jumper",total:e.tableTotal},on:{"size-change":e.handleSizeChange,"current-change":e.handleCurrentChange}}),e.documentDetailDialogVisible?a("el-dialog",{attrs:{title:e.currentRow.url+" 记录详情",visible:e.documentDetailDialogVisible,width:"75%"},on:{"update:visible":function(t){e.documentDetailDialogVisible=t}}},[a("el-descriptions",{staticClass:"margin-top",attrs:{title:"详情",column:3,border:""}},[a("template",{slot:"extra"},[a("el-button",{attrs:{type:"primary",size:"small"},on:{click:function(t){return e.deleteDocument()}}},[e._v("删除文档")])],1),a("el-descriptions-item",{attrs:{label:"文档ID"}},[e._v(e._s(e.currentRow.id))]),a("el-descriptions-item",{attrs:{label:"文档名称"}},[e._v(e._s(e.currentRow.httpMethod))]),a("el-descriptions-item",{attrs:{label:"文档地址"}},[e._v(e._s(e.currentRow.url))]),a("el-descriptions-item",{attrs:{label:"菜单名称"}},[e._v(e._s(e.currentRow.mappingClass))]),a("el-descriptions-item",{attrs:{label:"菜单ID"}},[e._v(e._s(e.currentRow.mappingMethod))]),a("el-descriptions-item",{attrs:{label:"模块"}},[e._v(e._s(e.currentRow.createTime))]),a("el-descriptions-item",{attrs:{label:"追踪码",span:3}},[e._v(e._s(e.currentRow.traceId))]),a("el-descriptions-item",{attrs:{label:"用户ID"}},[e._v(e._s(e.currentRow.userId))]),a("el-descriptions-item",{attrs:{label:"用户"}},[e._v(e._s(e.currentRow.userName))]),a("el-descriptions-item",{attrs:{label:"企业ID"}},[e._v(e._s(e.currentRow.companyId))]),a("el-descriptions-item",{attrs:{label:"企业名称"}},[e._v(e._s(e.currentRow.companyName))])],2),a("el-divider",{attrs:{"content-position":"left"}},[e._v("请求参数")]),a("div",[a("pre",{directives:[{name:"highlight-a",rawName:"v-highlight-a"}]},[e._v("         "),a("code",[e._v(e._s(e.currentRow.params))]),e._v("\n      ")])]),"YES"===e.currentRow.success?a("div",[a("el-divider",{attrs:{"content-position":"left"}},[e._v("返回结果")]),a("div",[a("pre",{directives:[{name:"highlight-a",rawName:"v-highlight-a"}]},[e._v("           "),a("code",[e._v(e._s(e.currentRow.result))]),e._v("\n        ")])])],1):a("div",[a("el-divider",{attrs:{"content-position":"left"}},[e._v("异常信息")]),a("div",[a("h3",[e._v("异常类")]),a("pre",{directives:[{name:"highlight-a",rawName:"v-highlight-a"}]},[e._v("           "),a("code",[e._v(e._s(e.currentRow.throwable))]),e._v("\n        ")]),a("h3",[e._v("异常信息")]),a("pre",{directives:[{name:"highlight-a",rawName:"v-highlight-a"}]},[e._v("           "),a("code",[e._v(e._s(e.currentRow.exceptionMsg))]),e._v("\n        ")]),a("h3",[e._v("堆栈跟踪")]),[a("el-input",{staticClass:"input-with-select",attrs:{placeholder:"请输入内容"},model:{value:e.stackTraceCLass,callback:function(t){e.stackTraceCLass=t},expression:"stackTraceCLass"}},[a("el-button",{attrs:{slot:"append",icon:"el-icon-search"},on:{click:e.filterHandler},slot:"append"})],1),a("el-table",{staticStyle:{width:"90%",margin:"0 auto"},attrs:{data:e.currentStackTrace}},[a("el-table-column",{attrs:{type:"index",width:"50",label:"序号"}}),a("el-table-column",{attrs:{prop:"className",label:"类名","min-width":"200"}}),a("el-table-column",{attrs:{prop:"methodName",label:"方法名",width:"180"}}),a("el-table-column",{attrs:{prop:"lineNumber",label:"所在行",width:"80"}}),a("el-table-column",{attrs:{label:"原生方法",width:"120"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("el-tag",[e._v(e._s(t.row.nativeMethod))])]}}],null,!1,1979842978)})],1)]],2)],1)],1):e._e()],1)},r=[],s=(a("caad"),a("e9c4"),a("2532"),a("498a"),a("b775"));function i(e){return Object(s["a"])({url:"/runtime/log/mapping/record/page",method:"get",params:e})}var n={data:function(){return{queryTable:{httpMethod:"",mappingClass:"",mappingMethod:"",success:"",traceId:"",userId:"",userName:"",companyId:"",companyName:"",throwable:"",createFromTime:"",createToTime:""},createTimeRange:[],stackTraceCLass:"",documentDetailDialogVisible:!1,currentRow:{httpMethod:"",mappingClass:"",mappingMethod:"",success:"",traceId:"",userId:"",userName:"",companyId:"",companyName:"",throwable:"",createTime:""},currentStackTrace:[],articleTable:[],currentPage:1,currentPageSize:50,tableTotal:0}},created:function(){},mounted:function(){this.queryTablePage()},methods:{filterHandler:function(){if(null!==this.stackTraceCLass&&void 0!==this.stackTraceCLass&&""!==this.stackTraceCLass.trim())for(var e in this.currentStackTrace=[],this.currentRow.stackTrace){var t=this.currentRow.stackTrace[e];t.className.includes(this.stackTraceCLass)&&this.currentStackTrace.push(t)}else this.currentStackTrace=this.currentRow.stackTrace},deleteDocument:function(){},documentDetail:function(e){this.currentRow={},this.documentDetailDialogVisible=!0,this.$set(this,"currentRow",e),this.currentStackTrace=this.currentRow.stackTrace},handleSizeChange:function(e){this.currentPageSize=e,this.queryTablePage()},handleCurrentChange:function(e){this.currentPage=e,this.queryTablePage()},queryTablePage:function(){var e=this;this.queryTable.createFromTime=this.createTimeRange[0],this.queryTable.createToTime=this.createTimeRange[1],this.queryTable.pageNum=this.currentPage,this.queryTable.pageSize=this.currentPageSize,console.log("文章列表查询条件 ： "+JSON.stringify(this.queryTable)),i(this.queryTable).then((function(t){e.articleTable=t.data.list,e.tableTotal=t.data.total})).catch((function(t){e.$message.error(t)}))}}},c=n,o=a("2877"),u=Object(o["a"])(c,l,r,!1,null,null,null);t["default"]=u.exports}}]);