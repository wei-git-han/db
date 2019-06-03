var tableList= {"url":"/app/db/subdocinfo/list","dataType":"text"};//原table数据
var numsList={"url":"/app/db/subdocinfo/numsList","dataType":"text"};//筛选状态数字统计
var deptUrl= {"url":"/app/db/document/grdb/data/deptTree.json","dataType":"text"};//高级搜索--部门树
var userUrl = {"url":"/app/db/document/grdb/data/userTree.json","dataType":"text"};//高级搜索--人员树
var fileFrom=getUrlParam("fileFrom")||""; //文件来源
var fromMsg=getUrlParam("fromMsg")||false; //是否为消息进入
var grid = null;
var total=0;//列表中，数据的总条数

if(!window.top.memory){
	window.top.memory = {};
}
var o = window.top.memory;

var pageModule = function(){
	var initgrid = function(){
        grid = $("#gridcont").createGrid({
            columns:[
                 {display:"军委办件号",name:"banjianNumber",width:"6%",align:"left",title:true,render:function(rowdata,n){
                	 return rowdata.banjianNumber;
                 }},
                 {display:"局内状态",name:"statusName",width:"8%",align:"center",render:function(rowdata,n){
                 	var statusName="";
               	 	var bgColor="";
               	 	if(rowdata.docStatus==1){
	               	 	statusName="待转办";
	               		bgColor="rgba(240, 96, 0, 1)";
            	 	}else if(rowdata.docStatus==3){
	               	 	statusName="退回修改";
	               		bgColor="rgba(240, 96, 0, 1)";
	           	 		if(rowdata.dealUserName){
	           	 			statusName="待"+rowdata.dealUserName+"修改";
	           	 			bgColor="#FF8C40";
	           	 		}
               	 	}else if(rowdata.docStatus==5){
	               	 	statusName="待落实";
	               	 	bgColor="rgba(240, 96, 0, 1)";
	           	 		if(rowdata.dealUserName){
	           	 			statusName="待"+rowdata.dealUserName+"落实";
	           	 			bgColor="#FF8C40";
	           	 		}
               	 	}else if(rowdata.docStatus==7){
	               	 	statusName="待审批";
	               	 	bgColor="rgba(60, 123, 255, 1)";
	           	 		if(rowdata.dealUserName){
	           	 			statusName="待"+rowdata.dealUserName+"审批";
	           	 			bgColor="#6699FF";
	           	 		}
               	 	}else if(rowdata.docStatus==9){
	               	 	statusName="办理中";
	               	 	bgColor="rgba(43, 170, 129, 1)";
	           	 		if(rowdata.dealUserName){
	           	 			statusName=rowdata.dealUserName+"办理中";
	           	 			bgColor="#33CC99";
	           	 		}
               	 	}else if(rowdata.docStatus==10){
	               	 	statusName="建议办结";
	               	 	bgColor="rgba(153, 153, 153, 1)";
               	 	}else if(rowdata.docStatus==11){
	               	 	statusName="常态落实";
	               	 	bgColor="rgba(153, 153, 153, 1)";
               	 	}   				  	
               	 	return '<div title="'+statusName+'" class="btn btn-xs btn-color" style="background-color:'+bgColor+';">'+statusName+'</div>';
                 }},
                 {display:"办件标题",name:"docTitle",width:"15%",align:"left",title:false,render:function(rowdata){
                	 var cuiban="";
                 	 if(rowdata.cuibanFlag=="1"){
                 		 cuiban = '<label class="cuibanlabel">催办</label>';
                	 }
                	 return '<a title="'+rowdata.docTitle+'" class="tabletitle" href="../../view/html/view.html?fileId='+rowdata.infoId+'&subId='+rowdata.id+'&fileFrom='+fileFrom+'" target="iframe1">'+cuiban+rowdata.docTitle+'</a>'
                 }},
                 {display:"紧急程度",name:"urgencyDegree",width:"5%",align:"center",paixu:false,render:function(rowdata){
                	 return rowdata.urgencyDegree;
                 }},
                 {display:"批示指示内容",name:"",width:"20%",align:"left",paixu:false,title:false,render:function(rowdata){
                	 var szpsCont="";
                	 var leaderTime1="";
                	 if(rowdata.leaderTime!="" && rowdata.leaderTime!=null){
                		 leaderTime1= rowdata.leaderTime.substring(0,16);
                	 }
                	 if(rowdata.leaderName && rowdata.leaderContent){
                		 szpsCont=rowdata.leaderName+" "+leaderTime1+"批示："+rowdata.leaderContent
                	 }
                	 return '<div class="zspsnr" onclick="pszsnrAlert(\''+rowdata.infoId+'\')" title="'+szpsCont+'">'+szpsCont+'</div>';
                 }},
                 {display:"督办落实情况",name:"",width:"20%",align:"left",paixu:false,title:false,render:function(rowdata){
                	 var duban="";
                 	 if(rowdata.updateFlag=="1"){
                 		duban = '<label class="cuibanlabel">已更新</label>';
                	 }
                	 var dbCont="";
                	 if(rowdata.latestReply){
                		dbCont=rowdata.latestReply;
                	 }	 
                	 return '<div class="dblsqk" onclick="dblsqkAlert(\''+rowdata.infoId+'\')" title="'+dbCont+'">'+duban+'<span>'+dbCont+'</span></div>';
                 }},
                 {display:"承办单位/人",name:"",width:"10%",align:"left",paixu:false,title:false,render:function(rowdata){
                	 return '<div class="cbdw" title="'+rowdata.underDepts+'">'+rowdata.underDepts+'</div>'
                 }},
                 {display:"办件分类",name:"docTypeName",width:"7%",align:"left",paixu:false,render:function(rowdata){
                	 return rowdata.docTypeName;
                 }},
                 {display:"转办时间",name:"createdTime",width:"5%",align:"center",render:function(rowdata){
                	 return rowdata.createdTime.substring(0,16);
                 }},
                 {display:"操作",name:"do",width:"4%",align:"center",render:function(rowdata){
                	 var caozuo = '';
                	 if(rowdata.docStatus == "1"){
                		 //待修改
                     	 caozuo +='<a title="转办" class="btn btn-default btn-xs new_button1" href="javascript:;" onclick="zhuanbanDoc(\''+rowdata.id+'\',\''+rowdata.infoId+'\',\''+fromMsg+'\')"><i class="fa fa-external-link"></i></a>';
    				 }
                	 return caozuo;
                 }}
            ],
            width:"100%",
            height:"100%",
            checkbox: true,
            rownumberyon:true,
            overflowx:true,
            pagesize: 10,
            pageyno:true,
            paramobj:{page:o.pagesize,search:$("#searchVal").val(),docStatus:$("input[name='documentStatus']:checked").val()},
            loadafter:function(data){
            	total=data.total;
            	$(".zspsnr").each(function(){
					var maxwidth = 78;
					if($(this).text().length > maxwidth){
						$(this).text($(this).text().substring(0,maxwidth));
						$(this).html($(this).html()+'...');
					}
				});
            	
            	$(".dblsqk span").each(function(){
					var maxwidth = 72;
					if($(this).text().length > maxwidth){
						$(this).text($(this).text().substring(0,maxwidth));
						$(this).html($(this).html()+'...');
					}
				});
            	$(".tabletitle").each(function(){
					var maxwidth = 55;
					if($(this).text().length > maxwidth){
						$(this).text($(this).text().substring(0,maxwidth));
						$(this).html($(this).html()+'...');
					}
				});
            	$(".cbdw").each(function(){
					var maxwidth = 42;
					if($(this).text().length > maxwidth){
						$(this).text($(this).text().substring(0,maxwidth));
						$(this).html($(this).html()+'...');
					}
				});
            },
            url: tableList,
            getpagefn:function(page){
            	return window.top.memory.pagesize = page;   
            }
       });
	}
	
	var numsListfn = function(){
		$ajax({
			url:numsList,
			data:{search:$("#searchVal").val()},
			success:function(data){
				$.each(data,function(i,item){
					var id = "grdb"+i;
					$("#"+id).html(item);
				});
			}
		});	
	}
	
	var initother = function(){
		$(".date-picker").datepicker({
		    language:"zh-CN",
		    rtl: Metronic.isRTL(),
		    orientation: "right",
		    format : "yyyy-mm-dd",
		    autoclose: true
		});
		
		$(".search").hover(function(){
			$(this).attr("src","../../../common/images/u132_mouseOver.png");
		},function(){
			$(this).attr("src","../../../common/images/u132_mouseDown.png");
		});

		$("input[name='documentStatus']").click(function(){
			refreshgrid();
		});
		
		$(".search").click(function(){
			refreshgrid();
		});
		
		$("#searchAll").click(function(){
			$("#searchwrap").toggle();
		});
		
		//筛选功能
		$("#sure").click(function(){
			 $("#searchwrap").slideUp(50);
			 refreshgrid();
		});
		
		//筛选功能
		$("#close").click(function(){
			$("#searchwrap").slideUp(50);
		});
		
		//重置
		$("#reset").click(function(){
			removeInputData(["title","deptid","deptname","username","userid","blstatus","designStart","designEnd","fileType"]);
		});
		
		//导出
		$("#export").click(function(){
			var datas=grid.getcheckrow();
			var ids='';
			var t_count=0;
			$(datas).each(function(i){
				ids+=i!=0?(','+this.id):this.id;
				t_count++;
			});
			t_count=t_count>0?t_count:total;
			if(datas.length>0){
				newbootbox.confirm({
			     	title:"提示",
			     	message: "将导出"+t_count+"条数据！",
			     	callback1:function(){
			     		/*window.location.href=后台地址+'?ids='+ids;*/
			     	}
			    });
			}else{
				newbootbox.alertInfo("请选择要导出的数据！");
			}
		});
	}
	
	var inittree = function(){
		$("#deptname").createcheckboxtree({
			url : deptUrl,
			width : "100%",
			success : function(data, treeobj) {},
			selectnode : function(e, data,treessname,treessid) {
				$("#deptid").val(treessid);
				$("#deptname").val(treessname);
			},
			deselectnode:function(e,data,treessname,treessid){
				$("#deptid").val(treessid);
				$("#deptname").val(treessname);
		   }
		});
		
		$("#username").createUserTree({
			url : userUrl,
			width : "100%",
			success : function(data, treeobj) {
			},
			selectnode : function(e, data,treessname,treessid) {
				$("#userid").val(treessid);
				$("#username").val(treessname);
			},
			deselectnode : function(e, data,treessname,treessid) {
				$("#userid").val(treessid);
				$("#username").val(treessname);
			}
		});
	}

	var initfn = function(){
		$.uniform.update($("input[name='documentStatus']").prop("checked",false));
		if(o.radio!="undefined" && o.radio!=null && o.radio!=""){
			$.uniform.update($("input[value='"+o.radio+"']").prop("checked",true));
		}else{
			$.uniform.update($("input[value='']").prop("checked",true));
		}
		$("#searchVal").val(o.search);
	}
	
	return{
		//加载页面处理程序
		initControl:function(){
			initfn();
			initgrid();
			numsListfn();
			initother();
			inittree();
		},
		initgrid:function(){
			initgrid();
			numsListfn();
		}
	};
}();

function refreshgrid(){
	var search = $("#searchVal").val();
	var documentStatus= $("input[name='documentStatus']:checked").val();
	grid.setparams({search:search,docStatus:documentStatus});
	grid.loadtable();
	
	window.top.memory.radio = documentStatus;
	window.top.memory.search = search;
}


function zhuanbanDoc(subId,infoId,fromMsg){
	newbootbox.newdialog({
		id:"zhuanbanDialog",
		width:800,
		height:600,
		header:true,
		title:"转办",
		url:"/app/db/document/jndb/html/zhuanbandx.html?subId="+subId+"&infoId="+infoId+"&fileFrom="+fileFrom+"&fromMsg="+fromMsg
	})
}

//批示指示内容弹出框
function pszsnrAlert(id){
	newbootbox.newdialog({
		id:"psDialog",
		width:800,
		height:600,
		header:true,
		title:"批示详情",
		classed:"cjDialog",
		url:"/app/db/document/view/html/psDialog.html?fileId="+id
	})
}

//督办落实情况弹出框
function dblsqkAlert(id){
	newbootbox.newdialog({
		id:"dblsqkDialog",
		width:800,
		height:600,
		header:true,
		title:"督办详情",
		classed:"cjDialog",
		url:"/app/db/document/view/html/dblsqk.html?fileId="+id
	})
}