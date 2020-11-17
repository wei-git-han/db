//注：修改列表查询的参数一定要对应的修改红点显示leftMenuUrl和统计数numsList
var tableList= {"url":"/app/db/documentinfo/replyList","dataType":"text"};//原table数据
var numsList={"url":"/app/db/documentinfo/replyNums","dataType":"text"};//筛选状态数字统计
var leaderTreeUrl= {"url":"/app/db/roleset/queryLeaderTree","dataType":"text"};//高级搜索首长选择树
var userUrl = {"url":"/app/db/document/grdb/data/userTree.json","dataType":"text"};//人员树
var leftMenuUrl = {"url":"/app/db//documentinfo/getDicByTypet","dataType":"text"};//左侧菜单
var batchReadUrl = {"url":"/app/db/documentinfo/batchRead","dataType":"text"};//批量已读
var getUserAdminTypeUrl = {"url":"/app/db/adminset/getAuthor","dataType":"text"};//获取管理员类型
var getRole = {"url":"/app/db/adminset/getRole","dataType":"text"};//获取当前登录人角色
var fileFrom=getUrlParam("fileFrom")||""; //文件来源
var orgid=getUrlParam("orgid")||""; //统计图传过来的机构
var nowYear = getUrlParam("nowYear");//“统计报表”页面上的“年度”
var month=getUrlParam("month")||""; //统计图传过来的月份
var ytype=getUrlParam("ytype")||""; //统计图传过来的办理状态
if(fileFrom && fileFrom == "jcdb"){
	if(ytype){
		$("input[value="+ytype+"]").attr("checked",true);
	}
}else　if(fileFrom=='blfk'){
    $('#goback').hide()
}

var grid = null;
var grid2 = null;
var grid3 = null;
var total=0;//列表中，数据的总条数

if(!window.top.memory){
	window.top.memory = {};
}
var o = window.top.memory;
var pageModule = function(){
	//左侧菜单树
	var leftMenufn = function(){
		$ajax({
			url:leftMenuUrl,
			data:{orgid:orgid,month:month},
			async:false,
			success:function(data){
				$("#classType").html("");
				$.each(data.document_type,function(i,item){
					var dot ='';
					if(item.hasUpdateNum!=0){
						dot = '<span class="dot"></span>'
					}
					$("#classType").append('<li class="'+(i==0?"active":"")+'" data_flag="'+item.gridFlag+'" value="'+item.value+'"><span>'+item.text+dot+'</span><font id="Menu_num'+i+'"></font><i class="fa fa-angle-right"></i></li>');
				});
				
				$("#classType li").unbind("click");
				$("#classType li").click(function(){
					window.top.memory.value = $(this).attr("value");
					$(this).siblings().removeClass("active");
					$(this).addClass("active");
					if($(this).attr("data_flag") == "3"){ //部领导批示指示 
						window.top.memory.tree = "3";
						$("#gridcont3").show();
						$("#gridcont2").hide();
						$("#gridcont").hide();
						$("#gridcont4").hide();
						$("#period").removeAttr("disabled");
						$("#leaderName").removeAttr("disabled");
						$("#leaderName").parents(".form-control").css("background","#fff");
						$("#startDate").removeAttr("disabled");
						$("#endDate").removeAttr("disabled");
						refreshgrid3();
					}else if($(this).attr("data_flag") == "2"){//分工
						window.top.memory.tree = "2";
						$("#gridcont3").hide();
						$("#gridcont").hide();
						$("#gridcont4").hide();
						$("#gridcont2").show();
						$("#period").attr("disabled",true).val("");
						$("#leaderName").attr("disabled",true).val("");
						$("#leaderId").val("");
						$("#leaderName").parents(".form-control").css("background","#eee");
						$("#startDate").attr("disabled",true).val("");
						$("#endDate").attr("disabled",true).val("");
						refreshgrid2();
					}else if($(this).attr("data_flag") == "4"){//批示
						window.top.memory.tree = "4";
						$("#gridcont3").hide();
						$("#gridcont2").hide();
						$("#gridcont").hide();
						$("#gridcont4").show();
						$("#period").attr("disabled",true).val("");
						$("#leaderName").removeAttr("disabled");
						$("#leaderName").parents(".form-control").css("background","#fff");
						$("#startDate").removeAttr("disabled");
						$("#endDate").removeAttr("disabled");
						refreshgrid4();
					}else{
						window.top.memory.tree = "1";
						$("#gridcont3").hide();
						$("#gridcont2").hide();
						$("#gridcont").show();
						$("#period").attr("disabled",true).val("");
						$("#leaderName").removeAttr("disabled");
						$("#leaderName").parents(".form-control").css("background","#fff");
						$("#startDate").removeAttr("disabled");
						$("#endDate").removeAttr("disabled");
						refreshgrid1();
					}
				});
				
				if(o.value!="" && o.value!=null &&　o.value!="null" && o.value!="undefined"){
					$("#classType li").removeClass("active");
					$("#classType li[value="+o.value+"]").addClass("active");
					$("#classType li[value="+o.value+"]").click()
				}				
			}
		});	
	}
	
	var initgrid = function(){
        grid = $("#gridcont").createGrid({
            columns:[
            	{display:"军委办件号",name:"",width:"8%",align:"left",title:true,render:function(rowdata,n){
               	 	return rowdata.banjianNumber;
                }},
                {display:"办理状态",name:"",width:"8%",align:"center",render:function(rowdata,n){
                	var statusName="";
               	 	var bgColor="";
               	 	if(rowdata.status==1){
	               	 	if(rowdata.latestReply != '' && rowdata.latestReply != null){
	           	 			statusName="办理中";
	           	 		}else{
	           	 			statusName="未反馈";
	           	 		}  
	               		bgColor="#2BAA81";
            	 	}else if(rowdata.status==2){
	               	 	statusName="已办结";
	               		bgColor="#BCBCBC";
               	 	}else if(rowdata.status==3){
	               	 	statusName="常态落实";
	               	 	bgColor="#BCBCBC";
               	 	}
  				  	return '<div title="'+statusName+'" class="btn btn-xs btn-color" style="background-color:'+bgColor+';">'+statusName+'</div>';
                 }},
                 {display:"文件标题",name:"",width:"15%",align:"left",title:false,render:function(rowdata,n){
                	 var cuiban="";
                 	 if(rowdata.cuibanFlag=="1"){
                 		 cuiban = '<label class="cuibanlabel">催办</label>';
                	 }
                 	//isOverTreeMonth
                 	var csFlag = "";
                	if(rowdata.isOverTreeMonth==1){
                		csFlag = '<img src="../../../common/images/u301.png" class="titleimg"/>';
                	}
                	 return '<a title="'+rowdata.docTitle+'" class="tabletitle addimg" href="../../djlr/html/djlr_view.html?fileId='+rowdata.id+'&fileFrom='+fileFrom+'" target="iframe1">'+cuiban+'<span class="tabletitle2">'+rowdata.docTitle+csFlag+'</span></a>'
                 }},
                 {display:"批示指示内容",name:"",width:"26%",align:"left",paixu:false,title:false,render:function(rowdata){
                	 var html1="";
                	 $.each(rowdata.szpslist,function(i,item){
                		 var createdTime="";
                		 if(item.createdTime!="" && item.createdTime!=null){
                			 createdTime= item.createdTime.substring(0,16);
                		 }
                		 html1+=item.userName+'&nbsp;&nbsp;'+createdTime+'批示：'+item.leaderComment+'&nbsp;&nbsp;&nbsp;'
     				 });
                	 return '<div class="zspsnr" onclick="pszsnrAlert(\''+rowdata.id+'\')" title="'+html1+'">'+html1+'</div>';
                 }},
                 {display:"督办落实情况",name:"",width:"21%",align:"left",paixu:false,title:false,render:function(rowdata){
                	 var duban="";
                 	 if(rowdata.updateFlag=="1"){
                 		duban = '<label class="cuibanlabel">已更新</label>';
                	 }
                	 var dbCont="";
                	 if(rowdata.latestReply){
                		dbCont=rowdata.latestReply;
                	 }
                	 return '<div class="dblsqk" onclick="dblsqkAlert(\''+rowdata.id+'\')" title="'+dbCont+'">'+duban+'<span>'+dbCont+'</span></div>';
                 }},
                 {display:"承办单位/人",name:"",width:"10%",align:"left",paixu:false,title:false,render:function(rowdata){
                	 return '<div class="cbdw" title="'+rowdata.underDepts+'">'+rowdata.underDepts+'</div>'
                 }},
                 {display:"转办时间",name:"",width:"6%",align:"center",render:function(rowdata){
                	 if(rowdata.firstZbTime && !!rowdata.firstZbTime){
                		 return rowdata.firstZbTime.substring(0,16);
                	 }
                	 return '';
                 }},
                 {display:"反馈时间",name:"",width:"6%",align:"center",paixu:false,render:function(rowdata){
                	 if(rowdata.latestReplyTime && !!rowdata.latestReplyTime){
                		 return rowdata.latestReplyTime.substring(0,16);
                	 }
                	 return '';
                 }}
            ],
            width:"100%",
            height:"100%",
            checkbox: true,
            rownumberyon:true,
            overflowx:false,
            pagesize: 10,
            pageyno:true,
            paramobj:{page:o.pagesize1,search:$("#searchVal").val(),status:$("input[name='documentStatus']:checked").val(),typeId:$("#classType li.active").attr("value"),orgid:orgid,month:month,title:$("#title").val(),leaderId:$("#leaderId").val(),psStartDate:$("#startDate").val(),psEndDate:$("#endDate").val(),year:nowYear},
            loadafter:function(data){
            	total=data.total;
            	$(".zspsnr").each(function(){
					var maxwidth = 90;
					if($(this).text().length > maxwidth){
						$(this).text($(this).text().substring(0,maxwidth));
						$(this).html($(this).html()+'...');
					}
				});
            	$(".dblsqk span").each(function(){
					var maxwidth = 68;
					if($(this).text().length > maxwidth){
						$(this).text($(this).text().substring(0,maxwidth));
						$(this).html($(this).html()+'...');
					}
				});
            	$(".tabletitle2").each(function(){
					var maxwidth = 46;
					if($(this).text().length > maxwidth){
						$(this).text($(this).text().substring(0,maxwidth));
						$(this).html($(this).html()+'...');
					}
				});
            	$(".cbdw").each(function(){
					var maxwidth = 34;
					if($(this).text().length > maxwidth){
						$(this).text($(this).text().substring(0,maxwidth));
						$(this).html($(this).html()+'...');
					}
				});
            },
            url: tableList,
            getpagefn:function(page){
            	return window.top.memory.pagesize1 = page;   
            }
       });
	}
	
	var initgrid2 = function(){
        grid2 = $("#gridcont2").createGrid({
            columns:[
            	{display:"印发时间",name:"",width:"6%",align:"center",paixu:false,render:function(rowdata){
            		if(rowdata.printDate && rowdata.printDate!="" && rowdata.printDate!="null" && rowdata.printDate!=null){
            			return rowdata.printDate.substring(0,16);
	               	}
	               	return '';
                }},
                {display:"办理状态",name:"",width:"8%",align:"center",render:function(rowdata,n){
                	var statusName="";
               	 	var bgColor="";
               	 	if(rowdata.status==1){
               	 	if(rowdata.latestReply != '' && rowdata.latestReply != null){
               	 			statusName="办理中";
               	 		}else{
               	 			statusName="未反馈";
               	 		}               	 	
	               		bgColor="#2BAA81";
            	 	}else if(rowdata.status==2){
	               	 	statusName="已办结";
	               		bgColor="#BCBCBC";
               	 	}else if(rowdata.status==3){
	               	 	statusName="常态落实";
	               	 	bgColor="#BCBCBC";
               	 	}
  				  	return '<div title="'+statusName+'" class="btn btn-xs btn-color" style="background-color:'+bgColor+';">'+statusName+'</div>';
                }},
                {display:"文件标题",name:"",width:"15%",align:"left",render:function(rowdata){
                	 var cuiban="";
                 	 if(rowdata.cuibanFlag=="1"){
                 		 cuiban = '<label class="cuibanlabel">催办</label>';
                	 }
                  	var csFlag = "";
                 	if(rowdata.isOverTreeMonth==1){
                 		csFlag = '<img src="../../../common/images/u301.png" class="titleimg" />';
                 	}
                	 return '<a title="'+rowdata.docTitle+'" class="tabletitle addimg" href="../../djlr/html/djlr_view.html?fileId='+rowdata.id+'&fileFrom='+fileFrom+'&docTypeName='+rowdata.docTypeName+'&jobContent='+rowdata.jobContent+'" target="iframe1">'+cuiban+'<span class="tabletitle2">'+rowdata.docTitle+csFlag+'</span></a>'
                 }},
                 {display:"工作分工内容",name:"",width:"21%",align:"left",paixu:false,title:false,render:function(rowdata){
                	 return '<div class="gzfgnr" title="'+rowdata.jobContent+'">'+rowdata.jobContent+'</div>';
                 }},
                 {display:"督办落实情况",name:"",width:"28%",align:"left",paixu:false,title:false,render:function(rowdata){
                	 var duban="";
                 	 if(rowdata.updateFlag=="1"){
                 		duban = '<label class="cuibanlabel">已更新</label>';
                	 }
                	 var dbCont="";
                	 if(rowdata.latestReply){
                		dbCont=rowdata.latestReply;
                	 }	 
                	 return '<div class="dblsqk"  onclick="dblsqkAlert(\''+rowdata.id+'\')" title="'+dbCont+'">'+duban+'<span>'+dbCont+'</span></div>';
                 }},
                 {display:"承办单位/人",name:"",width:"10%",align:"left",paixu:false,title:false,render:function(rowdata){
                	 return '<div class="cbdw" title="'+rowdata.underDepts+'">'+rowdata.underDepts+'</div>'
                 }},
                 {display:"转办时间",name:"",width:"6%",align:"center",render:function(rowdata){
                	 if(rowdata.firstZbTime && !!rowdata.firstZbTime){
                		 return rowdata.firstZbTime.substring(0,16);
                	 }
                	 return '';
                 }},
                 {display:"反馈时间",name:"",width:"6%",align:"center",paixu:false,render:function(rowdata){
                	 if(rowdata.latestReplyTime && !!rowdata.latestReplyTime){
                		 return rowdata.latestReplyTime.substring(0,16);
                	 }
                	 return '';
                 }}
            ],
            width:"100%",
            height:"100%",
            checkbox: true,
            rownumberyon:true,
            overflowx:false,
            pagesize: 10,
            pageyno:true,
            paramobj:{page:o.pagesize2,search:$("#searchVal").val(),status:$("input[name='documentStatus']:checked").val(),typeId:$("#classType li.active").attr("value"),orgid:orgid,month:month,title:$("#title").val(),year:nowYear},
            loadafter:function(data){
            	total=data.total;
            	$(".dblsqk span").each(function(){
					var maxwidth = 72;
					if($(this).text().length > maxwidth){
						$(this).text($(this).text().substring(0,maxwidth));
						$(this).html($(this).html()+'...');
					}
				});
            	$(".gzfgnr").each(function(){
					var maxwidth = 68;
					if($(this).text().length > maxwidth){
						$(this).text($(this).text().substring(0,maxwidth));
						$(this).html($(this).html()+'...');
					}
				});
            	$(".tabletitle2").each(function(){
					var maxwidth = 46;
					if($(this).text().length > maxwidth){
						$(this).text($(this).text().substring(0,maxwidth));
						$(this).html($(this).html()+'...');
					}
				});
            	$(".cbdw").each(function(){
					var maxwidth = 32;
					if($(this).text().length > maxwidth){
						$(this).text($(this).text().substring(0,maxwidth));
						$(this).html($(this).html()+'...');
					}
				});
            },
            url: tableList,
            getpagefn:function(page){
            	return window.top.memory.pagesize2 = page;   
            }
       });
	}

	var initgrid3 = function(){
        grid3 = $("#gridcont3").createGrid({
            columns:[
            	{display:"期数",name:"",width:"9%",align:"left",title:true,render:function(rowdata,n){
               	 	return rowdata.period;
                }},
                {display:"办理状态",name:"",width:"7%",align:"center",render:function(rowdata,n){
                	var statusName="";
               	 	var bgColor="";
               	 	if(rowdata.status==1){
               	 	if(rowdata.latestReply != '' && rowdata.latestReply != null){
	           	 			statusName="办理中";
	           	 		}else{
	           	 			statusName="未反馈";
	           	 		}  
	               		bgColor="#2BAA81";
            	 	}else if(rowdata.status==2){
	               	 	statusName="已办结";
	               		bgColor="#BCBCBC";
               	 	}else if(rowdata.status==3){
	               	 	statusName="常态落实";
	               	 	bgColor="#BCBCBC";
               	 	}
  				  	return '<div title="'+statusName+'" class="btn btn-xs btn-color" style="background-color:'+bgColor+';">'+statusName+'</div>';
                 }},
                 {display:"文件标题",name:"",width:"15%",align:"left",title:false,render:function(rowdata){
                	 var cuiban="";
                 	 if(rowdata.cuibanFlag=="1"){
                 		 cuiban = '<label class="cuibanlabel">催办</label>';
                	 }
                   	var csFlag = "";
                  	if(rowdata.isOverTreeMonth==1){
                  		csFlag = '<img src="../../../common/images/u301.png" class="titleimg"/>';
                  	}
                	 return '<a title="'+rowdata.docTitle+'" class="tabletitle addimg" href="../../djlr/html/djlr_view.html?fileId='+rowdata.id+'&fileFrom='+fileFrom+'" target="iframe1">'+cuiban+'<span class="tabletitle2">'+rowdata.docTitle+csFlag+'</span></a>'
                 }},
                 {display:"批示指示内容",name:"",width:"26%",align:"left",paixu:false,title:false,render:function(rowdata){
                	 var html1="";
                	 $.each(rowdata.szpslist,function(i,item){
                		 var createdTime="";
                		 if(item.createdTime!="" && item.createdTime!=null){
                			 createdTime= item.createdTime.substring(0,16);
                		 }
                		 html1+=item.userName+'&nbsp;&nbsp;'+createdTime+'批示：'+item.leaderComment+'&nbsp;&nbsp;&nbsp;'
     				 });
                	 return '<div class="zspsnr" onclick="pszsnrAlert(\''+rowdata.id+'\')" title="'+html1+'">'+html1+'</div>';
                 }},
                 {display:"督办落实情况",name:"",width:"21%",align:"left",paixu:false,title:false,render:function(rowdata){
                	 var duban="";
                 	 if(rowdata.updateFlag=="1"){
                 		duban = '<label class="cuibanlabel">已更新</label>';
                	 }
                	 var dbCont="";
                	 if(rowdata.latestReply){
                		dbCont=rowdata.latestReply;
                	 }
                	 return '<div class="dblsqk" onclick="dblsqkAlert(\''+rowdata.id+'\')" title="'+dbCont+'">'+duban+'<span>'+dbCont+'</span></div>';
                 }},
                 {display:"承办单位/人",name:"",width:"10%",align:"left",paixu:false,title:false,render:function(rowdata){
                	 return '<div class="cbdw" title="'+rowdata.underDepts+'">'+rowdata.underDepts+'</div>'
                 }},
                 {display:"转办时间",name:"",width:"6%",align:"center",render:function(rowdata){
                	 if(rowdata.firstZbTime && !!rowdata.firstZbTime){
                		 return rowdata.firstZbTime.substring(0,16);
                	 }
                	 return '';
                 }},
                 {display:"反馈时间",name:"",width:"6%",align:"center",paixu:false,render:function(rowdata){
                	 if(rowdata.latestReplyTime && !!rowdata.latestReplyTime){
                		 return rowdata.latestReplyTime.substring(0,16);
                	 }
                	 return '';
                 }}
            ],
            width:"100%",
            height:"100%",
            checkbox: true,
            rownumberyon:true,
            overflowx:false,
            pagesize: 10,
            pageyno:true,
            paramobj:{page:o.pagesize3,search:$("#searchVal").val(),status:$("input[name='documentStatus']:checked").val(),typeId:$("#classType li.active").attr("value"),orgid:orgid,month:month,title:$("#title").val(),leaderId:$("#leaderId").val(),psStartDate:$("#startDate").val(),psEndDate:$("#endDate").val(),period:$("#period").val(),year:nowYear},
            loadafter:function(data){
            	total=data.total;
            	$(".zspsnr").each(function(){
					var maxwidth = 85;
					if($(this).text().length > maxwidth){
						$(this).text($(this).text().substring(0,maxwidth));
						$(this).html($(this).html()+'...');
					}
				});
            	$(".dblsqk span").each(function(){
					var maxwidth = 64;
					if($(this).text().length > maxwidth){
						$(this).text($(this).text().substring(0,maxwidth));
						$(this).html($(this).html()+'...');
					}
				});
            	$(".tabletitle2").each(function(){
					var maxwidth = 55;
					if($(this).text().length > maxwidth){
						$(this).text($(this).text().substring(0,maxwidth));
						$(this).html($(this).html()+'...');
					}
				});
            	$(".cbdw").each(function(){
					var maxwidth = 32;
					if($(this).text().length > maxwidth){
						$(this).text($(this).text().substring(0,maxwidth));
						$(this).html($(this).html()+'...');
					}
				});
            },
            url: tableList,
            getpagefn:function(page){
            	return window.top.memory.pagesize3 = page;   
            }
       });
	}
	
	var initgrid4 = function(){
        grid4 = $("#gridcont4").createGrid({
            columns:[
            	{display:"印发时间",name:"",width:"6%",align:"center",paixu:false,render:function(rowdata){
            		if(rowdata.printDate && rowdata.printDate!="" && rowdata.printDate!="null" && rowdata.printDate!=null){
            			return rowdata.printDate.substring(0,16);
	               	}
	               	return '';
                }},
                {display:"办理状态",name:"",width:"8%",align:"center",render:function(rowdata,n){
                	var statusName="";
               	 	var bgColor="";
               	 	if(rowdata.status==1){
               	 	if(rowdata.latestReply != '' && rowdata.latestReply != null){
	           	 			statusName="办理中";
	           	 		}else{
	           	 			statusName="未反馈";
	           	 		}  
	               		bgColor="#2BAA81";
            	 	}else if(rowdata.status==2){
	               	 	statusName="已办结";
	               		bgColor="#BCBCBC";
               	 	}else if(rowdata.status==3){
	               	 	statusName="常态落实";
	               	 	bgColor="#BCBCBC";
               	 	}
  				  	return '<div title="'+statusName+'" class="btn btn-xs btn-color" style="background-color:'+bgColor+';">'+statusName+'</div>';
                }},
                {display:"文件标题",name:"",width:"15%",align:"left",render:function(rowdata){
                	 var cuiban="";
                 	 if(rowdata.cuibanFlag=="1"){
                 		 cuiban = '<label class="cuibanlabel">催办</label>';
                	 }
                  	var csFlag = "";
                 	if(rowdata.isOverTreeMonth==1){
                 		csFlag = '<img src="../../../common/images/u301.png" class="titleimg" />';
                 	}
                	 return '<a title="'+rowdata.docTitle+'" class="tabletitle addimg" href="../../djlr/html/djlr_view.html?fileId='+rowdata.id+'&fileFrom='+fileFrom+'&docTypeName='+rowdata.docTypeName+'&jobContent='+rowdata.jobContent+'" target="iframe1">'+cuiban+'<span class="tabletitle2">'+rowdata.docTitle+csFlag+'</span></a>'
                 }},
                 {display:"落实事项",name:"",width:"21%",align:"left",paixu:false,title:false,render:function(rowdata){
                	 return '<div class="gzfgnr" title="'+rowdata.jobContent+'">'+rowdata.jobContent+'</div>';
                 }},
                 {display:"督办落实情况",name:"",width:"28%",align:"left",paixu:false,title:false,render:function(rowdata){
                	 var duban="";
                 	 if(rowdata.updateFlag=="1"){
                 		duban = '<label class="cuibanlabel">已更新</label>';
                	 }
                	 var dbCont="";
                	 if(rowdata.latestReply){
                		dbCont=rowdata.latestReply;
                	 }	 
                	 return '<div class="dblsqk"  onclick="dblsqkAlert(\''+rowdata.id+'\')" title="'+dbCont+'">'+duban+'<span>'+dbCont+'</span></div>';
                 }},
                 {display:"承办单位/人",name:"",width:"10%",align:"left",paixu:false,title:false,render:function(rowdata){
                	 return '<div class="cbdw" title="'+rowdata.underDepts+'">'+rowdata.underDepts+'</div>'
                 }},
                 {display:"转办时间",name:"",width:"6%",align:"center",render:function(rowdata){
                	 if(rowdata.firstZbTime && !!rowdata.firstZbTime){
                		 return rowdata.firstZbTime.substring(0,16);
                	 }
                	 return '';
                 }},
                 {display:"反馈时间",name:"",width:"6%",align:"center",paixu:false,render:function(rowdata){
                	 if(rowdata.latestReplyTime && !!rowdata.latestReplyTime){
                		 return rowdata.latestReplyTime.substring(0,16);
                	 }
                	 return '';
                 }}
            ],
            width:"100%",
            height:"100%",
            checkbox: true,
            rownumberyon:true,
            overflowx:false,
            pagesize: 10,
            pageyno:true,
            paramobj:{page:o.pagesize4,search:$("#searchVal").val(),status:$("input[name='documentStatus']:checked").val(),typeId:$("#classType li.active").attr("value"),orgid:orgid,month:month,title:$("#title").val(),leaderId:$("#leaderId").val(),psStartDate:$("#startDate").val(),psEndDate:$("#endDate").val(),year:nowYear},
            loadafter:function(data){
            	total=data.total;
            	$(".zspsnr").each(function(){
					var maxwidth = 90;
					if($(this).text().length > maxwidth){
						$(this).text($(this).text().substring(0,maxwidth));
						$(this).html($(this).html()+'...');
					}
				});
            	$(".gzfgnr").each(function(){
					var maxwidth = 68;
					if($(this).text().length > maxwidth){
						$(this).text($(this).text().substring(0,maxwidth));
						$(this).html($(this).html()+'...');
					}
				});
            	$(".dblsqk span").each(function(){
					var maxwidth = 68;
					if($(this).text().length > maxwidth){
						$(this).text($(this).text().substring(0,maxwidth));
						$(this).html($(this).html()+'...');
					}
				});
            	$(".tabletitle2").each(function(){
					var maxwidth = 46;
					if($(this).text().length > maxwidth){
						$(this).text($(this).text().substring(0,maxwidth));
						$(this).html($(this).html()+'...');
					}
				});
            	$(".cbdw").each(function(){
					var maxwidth = 34;
					if($(this).text().length > maxwidth){
						$(this).text($(this).text().substring(0,maxwidth));
						$(this).html($(this).html()+'...');
					}
				});
            },
            url: tableList,
            getpagefn:function(page){
            	return window.top.memory.pagesize4 = page;   
            }
       });
	}
	
	var numsListfn = function(){
		$ajax({
			url:numsList,
			data:{search:$("#searchVal").val(),typeId:$("#classType li.active").attr("value"),orgid:orgid,month:month,title:$("#title").val(),leaderId:$("#leaderId").val(),psStartDate:$("#startDate").val(),psEndDate:$("#endDate").val(),period:$("#period").val(),status:$("input[name='documentStatus']:checked").val(),year:nowYear},
			success:function(data){
				$.each(data,function(i,item){
					var id = "grdb"+i;
					$("#"+id).html(item);
				});
			}
		});	
	}
	
	var initBtn = function(){
		$ajax({
			url: getUserAdminTypeUrl,
			type: "GET",
			success: function(data) {
				if(data=="0"||data=="1"||data=="3"){//超级管理员或部管理员				
					$("#plcb").show(); //批量催办
					if(fileFrom=="blfk"){
						$("#edit").show(); //补录
					}
				}
			}
		});
		$ajax({
			url: getRole,
			type: "GET",
			success: function(data) {
				if(data=="2"||data=="5"||data=="3"){
					$("#plcb").show(); //批量催办
				}		
			}
		});
		$(".newpage8").click(function(){
			$(".newpage8").removeClass("active");
			$(this).addClass("active");
		});
	}
	
	var initother = function(){
		$(".date-picker").datepicker({
		    language:"zh-CN",
		    rtl: Metronic.isRTL(),
		    orientation: "right",
		    format : "yyyy年mm月dd日",
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
			if(!!$("#searchVal").val()){
				removeInputData(["title", "leaderId", "leaderName", "startDate", "endDate", "period", "status"]);
			}
			refreshgrid();
		});
		
		$("#searchAll").click(function(){
			$("#searchwrap").toggle();
		});
		
		$("#status option").click(function(){
			var checkedVal = $(this).val();
			$.uniform.update($(".radio-inline input").prop("checked",false));
			$.uniform.update($(".radio-inline input[value="+checkedVal+"]").prop("checked",true));
		});
		
		//筛选功能
		$("#sure").click(function(){
			 $("#searchwrap").slideUp(50);
				if(!!$("#title").val()){
					$("#searchVal").val('')
				}
			 refreshgrid();
		});
		
		//筛选功能
		$("#close").click(function(){
			$("#searchwrap").slideUp(50);
		});
		
		//重置
		$("#reset").click(function(){
			removeInputData(["title","leaderId","leaderName","startDate","endDate","period","status"]);
		});
		
		$("#plyd").click(function(){
			var datas;
			var ids=[];
			if($("#gridcont3").is(":visible")){
				datas=grid3.getcheckrow();
				$(datas).each(function(i){
					ids[i]=this.id;
				});
			}else if($("#gridcont2").is(":visible")){
				datas=grid2.getcheckrow();
				$(datas).each(function(i){
					ids[i]=this.id;
				});
			}else if($("#gridcont4").is(":visible")){
				datas=grid4.getcheckrow();
				$(datas).each(function(i){
					ids[i]=this.id;
				});
			}else{
				datas=grid.getcheckrow();
				$(datas).each(function(i){
					ids[i]=this.id;
				});
			}
			if(datas.length>0){
				newbootbox.confirm({
				    title: "提示",
				    message: "是否要进行确认已读操作？",
				    callback1:function(){
				    	$ajax({
							url:batchReadUrl,
							data:{ids:ids.toString()},
							success:function(data){
								if(data.result == "success"){
									newbootbox.alertInfo("已读成功！").done(function(){
										leftMenufn();
										refreshgrid();
										window.top.blfkfn();
									});
								}
							}
						});	
				    }
				});
			}else{
				newbootbox.alertInfo("请选择要确认已读的数据！");
			}
		});
		// 批量催办
		$("#plcb").click(function(){
			var datas;
			var ids=[];
			var docStatus=[];
			var count = 0;
			if($("#gridcont3").is(":visible")){
				datas=grid3.getcheckrow();
				$(datas).each(function(i){
					ids[i]=this.id;
					docStatus[i]=this.status;
					if(this.status != "1" || this.cuibanFlag=="1"){
						count++;
					}
				});
			}else if($("#gridcont2").is(":visible")){
				datas=grid2.getcheckrow();
				$(datas).each(function(i){
					ids[i]=this.id;
					docStatus[i]=this.status;
					if(this.status != "1" || this.cuibanFlag=="1"){
						count++;
					}
				});
			}else if($("#gridcont4").is(":visible")){
				datas=grid4.getcheckrow();
				$(datas).each(function(i){
					ids[i]=this.id;
					docStatus[i]=this.status;
					if(this.status != "1" || this.cuibanFlag=="1"){
						count++;
					}
				});
			}else{
				datas=grid.getcheckrow();
				$(datas).each(function(i){
					ids[i]=this.id;
					docStatus[i]=this.status;
					if(this.status != "1" || this.cuibanFlag=="1"){
						count++;
					}
				});
			}
			if(datas.length>0){
				if(count > 0){
					newbootbox.alertInfo("所选文件有"+count+"个文件不支持批量催办，请单独处理");
					return;
				}else{
					newbootbox.newdialog({
					    id: "cuibanDialog",
					    title: "催办",
					    header:true,
					    width: 600,
					    height:400,
					    url: rootPath + "/document/blfk/html/cuibanDialog.html?fileIds="+ids.toString()
					});
				}
			}else{
				newbootbox.alertInfo("请选择要催办的数据！");
			}
		});
		//导出
		$("#export").click(function(){
			var tableNum;
			if($("#gridcont3").is(":visible")){
				tableNum= '3';
			}else if($("#gridcont2").is(":visible")){
				tableNum= '2';
			}else if($("#gridcont4").is(":visible")){
				tableNum= '4';
			}else{
				tableNum= '1';
			};
			newbootbox.newdialog({
			    id: "importTable",
			    title: "导出",
			    style: {
			      "padding": "1px"
			    },
			    width: 1600,
			    height:850,
			    url: rootPath + "/document/blfk/html/exportTable.html?tableNum="+tableNum+'&typeId='+$("#classType li.active").attr("value")+'&fileFrom='+fileFrom
			  });
		});
		
		
		//菜单左缩进
		$("#suo").click(function(){
			if($(this).find("i").hasClass("fa-chevron-right")){
				$(this).find("i").removeClass("fa-chevron-right").addClass("fa-chevron-left");
				$("#right_content").css("left","256px");
				$(this).parent().css("left","256px");
				$("#left_content").show();
			}else{
				$(this).find("i").removeClass("fa-chevron-left").addClass("fa-chevron-right");
				$("#left_content").hide();
				$(this).parent().css("left","0px");
				$("#right_content").css("left","0px");
			}
			if($("#gridcont3").is(":visible")){
				pageModule.initgrid3();
			}else if($("#gridcont2").is(":visible")){
				pageModule.initgrid2();
			}else if($("#gridcont4").is(":visible")){
				pageModule.initgrid4();
			}else {
				pageModule.initgrid();
			}
		});
		
		//补录
		$("#edit").click(function(){
			if($("#gridcont3").is(":visible")){
				var datas=grid3.getcheckrow();
			}else if($("#gridcont2").is(":visible")){
				var datas=grid2.getcheckrow();
			}else if($("#gridcont4").is(":visible")){
				var datas=grid4.getcheckrow();
			}else{
				var datas=grid.getcheckrow();
			}
			var ids=[];
			if(datas.length==1){
				$(datas).each(function(i){
					ids[i]=this.id;
				});
				window.location.href="/app/db/document/djlr/html/edit.html?fileId="+ids.toString()+'&fileFrom='+fileFrom;
			}else{
				newbootbox.alertInfo("请选择一条数据进行补录！");
			}
		})
		
	}
	
	var inittree = function(){
		$("#leaderName").createUserTree({
			url : leaderTreeUrl,
			width : "100%",
			success : function(data, treeobj) {},
			selectnode : function(e, data,treessname,treessid) {
				$("#leaderId").val(treessid);
				$("#leaderName").val(treessname);
			},
			deselectnode:function(e,data,treessname,treessid){
				$("#leaderId").val(treessid);
				$("#leaderName").val(treessname);
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
			initBtn();
			initfn();
			leftMenufn();
			initgrid();
			numsListfn();
			initother();
			inittree();
		},
		initgrid:function(){
			initgrid();
			numsListfn();
		},
		initgrid2:function(){
			initgrid2();
			numsListfn();
		},
		initgrid3:function(){
			initgrid3();
			numsListfn();
		},
		initgrid4:function(){
			initgrid4();
			numsListfn();
		}
	};
}();

function refreshgrid(){
	if(o.tree == "3"){
		$("#gridcont3").show();
		$("#gridcont2").hide();
		$("#gridcont").hide();
		$("#gridcont4").hide();
	}else if(o.tree == "2"){
		$("#gridcont2").show();
		$("#gridcont3").hide();
		$("#gridcont").hide();
		$("#gridcont4").hide();
	}else if(o.tree == "4"){
		$("#gridcont4").show();
		$("#gridcont2").hide();
		$("#gridcont3").hide();
		$("#gridcont").hide();
	}else{
		$("#gridcont").show();
		$("#gridcont2").hide();
		$("#gridcont3").hide();
		$("#gridcont4").hide();
	}
	if($("#gridcont3").is(":visible")){
		pageModule.initgrid3();
	}else if($("#gridcont2").is(":visible")){
		pageModule.initgrid2();
	}else if($("#gridcont4").is(":visible")){
		pageModule.initgrid4();
	}else{
		pageModule.initgrid();
	}
	var search = $("#searchVal").val();
	var documentStatus= $("input[name='documentStatus']:checked").val();
	window.top.memory.radio = documentStatus;
	window.top.memory.search = search;
	
	removeInputData(["title","leaderId","leaderName","startDate","endDate","period","status"]);
}

//查询
function refreshgrid1(){
	pageModule.initgrid();
}
function refreshgrid2(){
	pageModule.initgrid2();
}
function refreshgrid3(){
	pageModule.initgrid3();
}
function refreshgrid4(){
	pageModule.initgrid4();
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
		url:"/app/db/document/view/html/dblsqk.html?fileId="+id+"&fileFrom="+fileFrom
	})
}
$("#goback").click(function(){
    window.location.href = "../../jcdb/html/index.html"
});
