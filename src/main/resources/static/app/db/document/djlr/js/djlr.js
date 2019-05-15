var tableList= {"url":"/app/db/documentinfo/list","dataType":"text"};//table数据
var delDocUrl = {"url":"/app/db/documentinfo/delete","dataType":"text"};//表格数据删除
var numsList={"url":"/app/db/documentinfo/numsList","dataType":"text"};//筛选状态数字统计
var chehuiDocUrl = {"url":"/app/db/documentinfo/cheHuiOperation","dataType":"text"};//表格数据撤回

var deptUrl= {"url":"/app/db/document/grdb/data/deptTree.json","dataType":"text"};//部门树
var userUrl = {"url":"/app/db/document/grdb/data/userTree.json","dataType":"text"};//人员树

var fileFrom=getUrlParam("fileFrom")||""; //文件来源
var grid = null;
var total=0;//列表中，数据的总条数
var pageModule = function(){
	var initgrid = function(){
        grid = $("#gridcont").createGrid({
            columns:[
                 {display:"军委办件号",name:"",width:"8%",align:"left",title:true,render:function(rowdata,n){
                	 return rowdata.banjianNumber;
                 }},
                 {display:"办理状态",name:"",width:"6%",align:"center",render:function(rowdata,n){
                 	 var bgColor="#FF6600";
                 	 var documentStatusName = "";
             		 if(rowdata.status<1){
             			 documentStatusName="待处理";
             			 bgColor="#FF6600";
    				 }else{
    					 documentStatusName="已处理";
    					 bgColor="#999999";
    				 }
   				  	 return '<div title="'+documentStatusName+'" class="btn btn-xs btn-color" style="background-color:'+bgColor+';">'+documentStatusName+'</div>';
                 }},
                 {display:"办件标题",name:"",width:"15%",align:"left",render:function(rowdata){
                	 return '<a title="'+rowdata.docTitle+'" class="tabletitle" href="djlr_view.html?fileId='+rowdata.id+'&fileFrom='+fileFrom+'" target="iframe1">'+rowdata.docTitle+'</a>'
                 }},
                 {display:"紧急程度",name:"",width:"4%",align:"center",paixu:false,render:function(rowdata){
                	 return rowdata.urgencyDegree;
                 }},
                 {display:"批示指示内容",name:"",width:"25%",align:"left",paixu:false,title:false,render:function(rowdata){
                	 var szpsCont="";
                	 var leaderTime1="";
                	 if(rowdata.leaderTime!="" && rowdata.leaderTime!=null){
                		 leaderTime1= rowdata.leaderTime.substring(0,16);
                	 }
                	 if(rowdata.leaderName && rowdata.leaderContent){
                		 szpsCont=rowdata.leaderName+":"+rowdata.leaderContent+" "+leaderTime1
                	 }
                	 return '<div class="zspsnr"  onclick="pszsnrAlert(\''+rowdata.id+'\')" title="'+szpsCont+'">'+szpsCont+'</div>';
                 }},
                 {display:"承办单位/人",name:"",width:"20%",align:"left",paixu:false,title:false,render:function(rowdata){
                	 return '<div class="cbdw" title="'+rowdata.underDepts+'">'+rowdata.underDepts+'</div>'
                 }},
                 {display:"办件分类",name:"",width:"10%",align:"left",paixu:false,title:true,render:function(rowdata){
                	 return rowdata.docTypeName;
                 }},
                 {display:"创建时间",name:"",width:"9%",align:"center",title:true,render:function(rowdata){
                	 return rowdata.createdTime.substring(0,16);
                 }},
                 {display:"操作",name:"do",width:"3%",align:"center",render:function(rowdata){
                	 var caozuo = '';
                	 if(rowdata.status==1){
                		 caozuo +='<a title="撤回" class="btn btn-default btn-xs new_button1" href="javascript:;" onclick="chehuiDoc(\''+rowdata.id+'\')"><i class="fa fa-mail-reply"></i></a>';
                	 }
                	 if(rowdata.status==0){
                     	 caozuo +='<a title="删除" class="btn btn-default btn-xs new_button1" href="javascript:;" onclick="deleteDoc(\''+rowdata.id+'\')"><i class="fa fa-trash-o"></i></a>';
    				 }
                	 return caozuo;
                 }}
            ],
            width:"100%",
            height:"100%",
            checkbox: true,
            rownumberyon:true,
            overflowx:false,
            pagesize: 10,
            pageyno:true,
            paramobj:{search:$("#searchVal").val(),documentStatus:$("input[name='documentStatus']:checked").val()},
            loadafter:function(data){
            	total=data.total;
            	$(".zspsnr").each(function(){
					var maxwidth = 100;
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
					var maxwidth = 60;
					if($(this).text().length > maxwidth){
						$(this).text($(this).text().substring(0,maxwidth));
						$(this).html($(this).html()+'...');
					}
				});
            },
            url: tableList
       });
	}
	
	var numsListfn = function(){
		$ajax({
			url:numsList,
			data:{search:$("#searchVal").val()},
			success:function(data){
				$.each(data,function(i,item){
					var id = "djlr"+i;
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
		
		//新增
		$("#add").click(function(){
			window.location.href="/app/db/document/djlr/html/add.html";
		});
		
		//编辑
		$("#edit").click(function(){
			var datas=grid.getcheckrow();
			var ids=[];
			if(datas.length==1){
				$(datas).each(function(i){
					ids[i]=this.id;
				});
				window.location.href="/app/db/document/djlr/html/edit.html?fileId="+ids.toString();
			}else{
				newbootbox.alertInfo("请选择一条数据进行补录！");
			}
		})
		
		$("#zhuanban").click(function(){
			var datas=grid.getcheckrow();
			var ids=[];
			if(datas.length>0){
				$(datas).each(function(i){
					ids[i]=this.id;
				});
				newbootbox.newdialog({
					id:"zhuanbanDialog",
					width:800,
					height:600,
					header:true,
					title:"转办",
					classed:"cjDialog",
					url:"/app/db/document/blfk/html/zhuanbanDialog.html?fileIds="+ids.toString()
				})
			}else{
				newbootbox.alertInfo("请勾选要转办的数据！");
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

	return{
		//加载页面处理程序
		initControl:function(){
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
	grid.setparams({search:search,documentStatus:$("input[name='documentStatus']:checked").val()});
	grid.loadtable();
}

//删除数据
function deleteDoc(docId) {
	newbootbox.confirm({
	    title: "提示",
	    message: "是否要进行删除操作？",
	    callback1:function(){
			$ajax({
				url:delDocUrl,
				data:{id:docId},
				type: "GET",
				success:function(data){
					if(data.result=='success'){
						newbootbox.alertInfo('删除成功！').done(function(){
							pageModule.initgrid();
						});
					}else{
						newbootbox.alertInfo('删除失败！')
					}
				}
			});
	    }
	});
}

//撤回数据
function chehuiDoc(docId) {
	newbootbox.confirm({
	    title: "提示",
	    message: "是否要进行撤回操作？",
	    callback1:function(){
			$ajax({
				url:chehuiDocUrl,
				data:{id:docId},
				type: "GET",
				success:function(data){
					if(data.result=='success'){
						newbootbox.alertInfo('撤回成功！').done(function(){
							pageModule.initgrid();
						});
					}else{
						newbootbox.alertInfo('撤回失败！')
					}
				}
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