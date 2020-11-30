var tableList= {"url":"/app/db/subdocinfo/personList","dataType":"text"};//原table数据
var numsList={"url":"/app/db/subdocinfo/presonNumsList","dataType":"text"};//筛选状态数字统计
var deptUrl= {"url":"/app/db/document/grdb/data/deptTree.json","dataType":"text"};//高级搜索--部门树
var userUrl = {"url":"/app/db/document/grdb/data/userTree.json","dataType":"text"};//高级搜索--人员树
var chehuiUrl = {"url":"/app/db/withdraw/juInnnerWithdraw","dataType":"text"};//撤回url
var currUserRoleTypeUrl = {"url":"/app/db/subdocinfo/currUserRoleType","dataType":"text"};//获取当前用户的角色类型
var buttonColorUrl={"url":"/app/db/addXbDeal/buttonColor","dataType":"text"}; //发送意见url
var pushTodo={"url":"/api/db/todo","dataType":"text"};

var grid = null;
var total=0;//列表中，数据的总条数
var currentUserRole = "6";
var fileFrom=getUrlParam("fileFrom")||""; //文件来源
if(!window.top.memory){
	window.top.memory = {};
}
var o = window.top.memory;

var docStatus=0;

var pageModule = function(){
	var initgrid = function(){
        grid = $("#gridcont").createGrid({
            columns:[
            	{display:"军委办件号",name:"banjianNumber",width:"6%",align:"left",render:function(rowdata,n){
               	 	return rowdata.banjianNumber;
                }},
                {display:"局内状态",name:"statusName",width:"7%",align:"center",render:function(rowdata,n){
                	var statusName="";
               	 	var bgColor="#FF6600";
                	docStatus=rowdata.docStatus;
               	 	if(rowdata.docStatus==3){
	               	 	statusName="退回修改";
	               		bgColor="rgba(240, 96, 0, 1)";
	           	 		if(1 != rowdata.receiverIsMe){
	           	 			statusName="待"+rowdata.dealUserName+"修改";
	           	 			bgColor="#FF8C40";
	           	 		}
               	 	}else if(rowdata.docStatus==5){
	               	 	statusName="待落实";
	               	 	bgColor="rgba(240, 96, 0, 1)";
	           	 		if(1 != rowdata.receiverIsMe){
	           	 			statusName="待"+rowdata.dealUserName+"落实";
	           	 			bgColor="#FF8C40";
	           	 		}
               	 	}else if(rowdata.docStatus==7){
	               	 	statusName="待审批";
	               	 	bgColor="rgba(60, 123, 255, 1)";
	           	 		if(1 != rowdata.receiverIsMe){
	           	 			statusName="待"+rowdata.dealUserName+"审批";
	           	 			bgColor="#6699FF";
	           	 		}
               	 	}else if(rowdata.docStatus==9){
	               	 	statusName="办理中";
	               	 	bgColor="rgba(43, 170, 129, 1)";
	           	 		if(1 != rowdata.receiverIsMe && rowdata.isXBPerson != 1 ){
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
                {display:"文件标题",name:"docTitle",width:"10%",align:"left",render:function(rowdata){
                	var cuiban="";
                	if(rowdata.cuibanFlag=="1"){
                		cuiban = '<label class="cuibanlabel">催办</label>';
               	    }
                	var csFlag = "";
                	if(rowdata.isOverTreeMonth==1){
                		csFlag = '<img src="../../../common/images/u301.png" class="titleimg" />';
                	}
                	if(rowdata.isXBPerson == 1 ){
                		return '<a title="'+rowdata.docTitle+'" class="tabletitle addimg" href="../../view/html/view.html?fileId='+rowdata.infoId+'&subId='+rowdata.id+'&docStatus='+docStatus+'&fileFrom=grdb'+'&docTypeName='+rowdata.docTypeName+'&jobContent='+rowdata.jobContent+'" target="iframe1">'+cuiban+'<span class="tabletitle2">'+rowdata.docTitle+csFlag+'</span></a>'
                	}else{
                		return '<a title="'+rowdata.docTitle+'" class="tabletitle addimg" href="../../view/html/view2.html?fileId='+rowdata.infoId+'&subId='+rowdata.id+'&docStatus='+docStatus+'&fileFrom=grdb'+'&docTypeName='+rowdata.docTypeName+'&jobContent='+rowdata.jobContent+'" target="iframe1">'+cuiban+'<span class="tabletitle2">'+rowdata.docTitle+csFlag+'</span></a>'
                	}
                }},
                {display:"紧急程度",name:"urgencyDegree",width:"5%",align:"center",paixu:false,render:function(rowdata){
               	 	return rowdata.urgencyDegree;
                }},
                {display:"批示指示/任务分工",name:"",width:"20%",align:"left",paixu:false,render:function(rowdata){
	               	 var contentText = '';
	            	 if(rowdata.docTypeName == "重要决策部署分工"||rowdata.docTypeName == "其他重要工作"||rowdata.docTypeName == "部内重要工作分工"){
	            		 //contentText = rowdata.jobContent?rowdata.jobContent:"";
	            		 contentText = rowdata.jobContent?('<div style="cursor: initial;" class="zspsnr" title="' + rowdata.jobContent + '">'+rowdata.jobContent+'</div>'):"";
	            	 }else{
	                	 var html1="";
	               	 	 $.each(rowdata.szpslist,function(i,item){
		               		 var createdTime="";
		               		 if(item.createdTime!="" && item.createdTime!=null){
		               			 createdTime= item.createdTime.substring(0,16);
		               		 }
		               		html1+=item.userName+'&nbsp;&nbsp;'+createdTime+'批示：'+item.leaderComment+'&nbsp;&nbsp;&nbsp;'
		    		     });
	                	 contentText = '<div class="zspsnr" onclick="pszsnrAlert(\''+rowdata.infoId+'\')" title="'+html1+'">'+html1+'</div>';
	            	 }

	            	 return contentText;
                }},
                {display:"本期局内反馈",name:"",width:"19%",align:"left",paixu:false,render:function(rowdata){
		           	var dbCont="";
		           	if(rowdata.latestReply){
		           		dbCont=rowdata.latestReply;
		           	}
		           	return '<div class="dblsqk" onclick="dblsqkAlert(\''+rowdata.infoId+'\')"  title="'+dbCont+'"><span>'+dbCont+'</span></div>';
		        }},
                {display:"承办单位/人",name:"",width:"10%",align:"left",paixu:false,title:false,render:function(rowdata){
                	return '<div class="cbdw" title="'+rowdata.underDepts+'">'+rowdata.underDepts+'</div>'
                }},
                {display:"类别",name:"docTypeName",width:"5%",align:"center",paixu:false,render:function(rowdata){
               	 	return rowdata.docTypeName;
                }},
                {display:"转办时间",name:"createdTime",width:"5%",align:"center",render:function(rowdata){
               	 	return rowdata.createdTime.substring(0,16);
                }},
                {display:"更新时间",name:"",width:"5%",align:"center",paixu:false,render:function(rowdata){
                	 var updateTime="";
                	 if(rowdata.updateTime){
                		 updateTime = rowdata.updateTime.substring(0,16);
                	 }
                	 return updateTime;
                }},
                {display:"意见收集",name:"",width:"4%",align:"center",paixu:false,render:function(rowdata){
               	 var ideaCount="";
               	 var fontColor="";
               	 if(rowdata.ideaAddFlag==1 && rowdata.isCBPerson == 1){
               		fontColor = "color:#f00;"
               	 }
               	 if(rowdata.ideaCount){
               		ideaCount = '<font style="cursor:pointer;'+fontColor+'" id="'+rowdata.id+'" onclick="opinionView(\''+rowdata.infoId+'\',\''+rowdata.id+'\',\''+rowdata.isCBPerson+'\')">'+rowdata.ideaCount+'</font>';
               	 }else{
               		ideaCount = '-';
               	 }
               	 return ideaCount;
               }},
                {display:"操作",name:"",width:"4%",align:"center",paixu:false,render:function(rowdata){
	               	 var btnHtml="";
	               	 if(rowdata.withdrawFlag == "1"){
	               		btnHtml+='<a title="撤回" class="btn btn-default btn-xs new_button1" href="javascript:;" onclick="chehuiDoc(\''+rowdata.id+'\',\''+rowdata.infoId+'\',\''+rowdata.dealUserId+'\')"><i class="fa fa-mail-reply"></i></a>';
	               	 }
	               	 return btnHtml;
               }}
            ],
            width:"100%",
            height:"100%",
            checkbox: true,
            rownumberyon:true,
            overflowx:false,
            pagesize: 10,
            pageyno:true,
            paramobj:{page:o.pagesize,search:$("#searchVal").val(),docStatus:$("input[name='documentStatus']:checked").val()},
            loadafter:function(data){
            	total=data.total;
            	$(".zspsnr").each(function(){
					var maxwidth = 88;
					if($(this).text().length > maxwidth){
						$(this).text($(this).text().substring(0,maxwidth));
						$(this).html($(this).html()+'...');
					}
				});
            	$(".dblsqk span").each(function(){
					var maxwidth = 79;
					if($(this).text().length > maxwidth){
						$(this).text($(this).text().substring(0,maxwidth));
						$(this).html($(this).html()+'...');
					}
				});
            	$(".tabletitle2").each(function(){
					var maxwidth = 47;
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

		/*$("body").click(function(e){
			if($(e.target).hasClass("searchAll") || $(e.target).hasClass("form-group") || $(e.target).parents("div").hasClass("searchwrap")){
				return;
			};
			$(".searchwrap").slideUp(50);
		});*/

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

		//批量审批
		$("#plsp").click(function(){
			var ids=[];
			var datas=grid.getcheckrow();
			var count = 0;//统计不是本人待审批的数据条数
			$(datas).each(function(i){
				ids[i]=this.id;
				if(this.docStatus != "7"){
					count++;
				}else{
					if(this.receiverIsMe != "1"){
						count++;
					}
				}
			});
			if(datas.length>0){
				if(count > 0){
					newbootbox.alertInfo("所选文件有"+count+"个文件不支持批量审批，请单独处理");
					return;
				}else{
					plspFn(ids.toString(),currentUserRole);
				}
			}else{
				newbootbox.alertInfo("请选择待审批的数据！");
				return;
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

	var initUserRole = function(){
		$ajax({
			url:currUserRoleTypeUrl,
//			data:{search:$("#searchVal").val()},
			success:function(data){
				//是局长
				if(data.result == "success"){
					currentUserRole = data.currUserRoleType;
				}
			}
		});
	}

	return{
		//加载页面处理程序
		initControl:function(){
			initfn();
			initgrid();
			numsListfn();
			initother();
			inittree();
			initUserRole();
			changToNum();
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
	numsListClickfn();

	window.top.memory.radio = documentStatus;
	window.top.memory.search = search;
}
function numsListClickfn(){
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

//撤回，添加dealUserId这个字段，处理角标问题
function chehuiDoc(id, infoId, dealUserId){
	newbootbox.confirm({
	    title: "提示",
	    message: "是否确认要进行撤回操作？",
	    callback1:function(){
	    	$ajax({
	    		url:chehuiUrl,
	    		data:{subId:id,infoId:infoId,dealUserId:dealUserId},
	    		success:function(data){
	    			if(data.result=='success'){
	    				newbootbox.alertInfo('撤回成功！').done(function(){
	    					pageModule.initgrid();
	    					// $ajax({
	    					// 	url:pushTodo,
	    					// 	data:{},
	    					// 	success:function(data){}
	    					// });
                            changToNum();
	    					window.top.grdbfn()
	    				});
	    			}else if(data.result=='deal'){
	    				newbootbox.alertInfo('当前文件已被处理，不能撤回！').done(function(){
	    					pageModule.initgrid();
	    				});
	    			}else{
	    				newbootbox.alertInfo('撤回失败！');
	    			}
	    		}
	    	});
	    }
	})
}


//批量审批
function plspFn(ids, curRole){
	newbootbox.newdialog({
		id:"plspDialog",
		width:850,
		height:500,
		header:true,
		title:"审批",
		classed:"cjDialog",
		url:"/app/db/document/grdb/html/plsp.html?ids="+ids+"&curRole="+curRole
	})
}

//意见收集
function opinionView(infoId, subId, isCBPerson){
	if(isCBPerson == 1){
		$ajax({
			url:buttonColorUrl,
			data:{subId:subId,infoId:infoId},
			success:function(data){

			}
		});
		$("#"+subId).css("color","#333");
	}
	newbootbox.newdialog({
		id:"opinionDialog",
		width:800,
		height:600,
		header:true,
		title:"意见收集",
		classed:"cjDialog",
		style:{"padding":"0px"},
		url:"/app/db/document/view/html/opinion.html?infoId="+infoId+"&subId="+subId+"&opinionFlag=table"
	})
}
