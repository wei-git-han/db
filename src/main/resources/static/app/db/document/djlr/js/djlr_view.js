var menuUrl = {"url":rootPath +"/documentfile/list","dataType":"text"};;//左侧文件menulist（文件列表-附件list）
var getFileUrl = {"url":"/app/db/documentfile/getFile","dataType":"text"};;//左侧页签获取文件信息
var getDataUrl = {"url":"/app/db/documentinfo/info","dataType":"json"};//右侧获取主文件信息
var getSzpsListUrl = {"url":rootPath +"/documentszps/queryList","dataType":"text"}; //获取首长批示
var zbjlDataUrl = {"url":"/app/db/documentzbjl/list","dataType":"json"}; //文件转办-转办记录list
var bjDataUrl = {"url":"/app/db/documentinfo/getBanJieList","dataType":"text"}; //办结记录
//var delfjUrl = {"url":"/app/db/replyexplain/deleteAttch","dataType":"text"}; //删除办理反馈中的附件
var downLoadUrl= {"url":"/app/db/replyexplain/downLoad","dataType":"text"}; //下载办理反馈中的附件
var allReplyListUrl = {"url":"/app/db/replyexplain/allReplyList","dataType":"text"}; //各局办理反馈list
var cbDataUrl = {"url":"/app/db/documentinfo/getCuiBanlist","dataType":"text"}; //催办记录list
var replyByTeamIdUrl = {"url":"/app/db/replyexplain/getReplyByTeamId","dataType":"text"}; //获取某组办理反馈
var latestCuiBanUrl = {"url":"/app/db/documentinfo/getLatestCuiBan","dataType":"text"}; //获取最新的催办
var getButtonParamUrl = {"url":"/app/db/documentinfo/buttonParam","dataType":"json"}; //获取按钮显示控制参数
var cancleOperationUrl = {"url":"/app/db/documentinfo/cancleOperation","dataType":"json"}; //取消办结操作
var batchReadUrl = {"url":"/app/db/documentinfo/batchRead","dataType":"text"};//标识已读
var cuibanurl = {"url":"/app/db/documentszinfo/press","dataType":"text"};
var fileId=getUrlParam("fileId")||""; //主文件id
var fileFrom=getUrlParam("fileFrom")||""; //文件来源
var fromMsg=getUrlParam("fromMsg")||false; //是否为消息进入
var pageModule = function(){
	//打开页面标识已读
	var initReadfn = function(){
		$ajax({
			url:batchReadUrl,
			data:{ids:fileId},
			success:function(data){
			}
		});	
	}
	//判断是否催办
	var ifcuibanfn = function(){
		$ajax({
			url:latestCuiBanUrl,
			data:{infoId:fileId},
			success:function(data){
				if(data && !!data){
					$(".blfkchangeH").attr("style","position:absolute;top:34px;left:0;bottom:0;width:100%;right:0;")
					$("#ifcuibanContent").text(data.userName+"催办："+data.urgeContent).attr("title",data.userName+"催办："+data.urgeContent).show();
				}
			}
		})
	}
	
	/* 按钮权限控制 */
	var showButton = function(){
		$ajax({
			url:getButtonParamUrl,
			data:{id:fileId},
			success:function(data){
				$(".ifShow").hide();
				if(data.cuiBanBtn && ("blfk"==fileFrom)){
					$(".newbottom").show();
					$("#cuiban").show();
				}
				if(data.quXiaoBtn && "blfk"==fileFrom){
					$(".newbottom").show();
					$("#quxiaobanjie").show();
				}
				if(data.zhuanBanBtn && ("blfk"==fileFrom)){
					$(".newbottom").show();
					$("#zhuanban").show();
				}
			}
		});	
	}
	
	//文件menu
	var takeMenufn = function(){
		$ajax({
			url:menuUrl,
			data:{infoId:fileId},
			success:function(data){
				var html1 = "";
				var firstFileId="";
				if(data &&　data.length>0){
					$("#ttcont").append(`
						<div id="tt" class="easyui-tabs" data-options="tabHeight:40,tabWidth:180" style="width:100%;"></div>	
					`);
					$.each(data,function(i,o){
						if(i==0){
							firstFileId = o.id;
						}
						var id = o.id;
						var fileName = o.fileName;
						var fileServerFormatId = o.fileServerFormatId;
						$("#tt").append(`
							<div title="<font title='${fileName}'>${fileName}</font>" id="${id}" fileServerFormatId="${fileServerFormatId}">
							</div>
						`);
					}); 
					$('#tt').tabs({
						onSelect:function(title){
							var tab = $('#tt').tabs('getSelected');
							var id = tab.attr("id");
							var fileServerFormatId = tab.attr("fileServerFormatId");
							$("#fjList>li>a").removeClass("fjactive");
							$("#fj_"+id).find("a").addClass("fjactive");
							getFile(id);
						},
						onClose:function(title){
						}
					});
					
					//文件列表——附件
					$("#fjList").html("");
					$.each(data,function(i,item){
						$("#fjList").append('<li num="'+i+'" id="fj_'+item.id+'"  data_id="'+item.id+'" formatId="'+item.fileServerFormatId+'" ><a class="'+(i==0?"fjactive":"")+'">'+item.fileName+'</a></li>');
					});
					//点击附件名称
					$("#fjList li").click(function(){
						$("#fjList>li>a").removeClass("fjactive");
						$(this).find("a").addClass("fjactive");
						var clickfileId = $(this).attr("data_id");
						getFile(clickfileId);
						$("#tt").tabs("select",parseInt($(this).attr("num")))
					}); 
				}
			}
		});	
	}
	
	var getFile = function(clickfileId){
		$ajax({
			url:getFileUrl,
			data:{id:clickfileId},
			success:function(data){
				var downFormatIdUrl = data.downFormatIdUrl;
				if(downFormatIdUrl != null && downFormatIdUrl != ''){
					openOFDFile(downFormatIdUrl, "suwell",$("#suwell").width(),$("#suwell").height(), "showTablet");
				}else{//没有文件仅初始化插件
					suwell.ofdReaderInit("suwell",$("#suwell").width(),$("#suwell").height());
				}
			}
		});	
	}
	
	//批示信息
	var initps = function(){
		$ajax({
			url:getSzpsListUrl,
			data:{infoId:fileId},
			success:function(data){
				var psxqBtn="";
				if(data.length>0){
					psxqBtn='<a href="javascript:;" class="psxqBtn" onclick="showXQ(\''+fileId+'\')">详情</a>'
				}
				if(data&&data.length>0){
					$(".line3").html('<i class="fa fa-info-circle" style="color:#33CC99"></i> <span class="option"><font class="psstyle">'+data[0].userName+'批示：'+data[0].leaderComment+'</font>'+psxqBtn+'</span>');
				}
			}
		});	
	}
	
	/*公文信息*/
	var initdata = function(){
		$ajax({
			url:getDataUrl,
			data:{id:fileId},
			success:function(data){
				if(data && !!data){
					$("#gwName").text(data.docTitle);
					$(".commonHtml").html("");
					$(".commonHtml").append(
						'<div class="line1"><span class="fileName">'+data.docTitle+'</span><font class="miji secretLevelName">'+data.urgencyDegree+'</font></div>'+
		            	'<div class="line2 fileNum">'+data.banjianNumber+'</div>'
					)
				}
			}
		});	
	}
	
	
	//办理反馈记录
	var initblfkList = function(){
		
		var eachfn = function(array,el,n){
			
			$.each(array,function(i,o){
				
				var id = ``;
				var cbrId = ``;
				var date = ``;
				var danwei = ``;
				var ld = ``;
				var content = ``;
				var state = ``;
				var file = ``;
				var edit = ``;
				var teamId = ``;
				var subId = ``;
				if(n==1){
					id = o.teamId;
					teamId = o.teamId;
					cbrId = o.cbrId;
					date = o.updateTime;
					firstDate = o.firstDate;
					danwei = o.danwei||"某单位";
					ld = o.cbrName;
					content = o.content;
					subId = o.subId;
					//edit = o.edit;
					//if(edit==true){
					//	edit = `<div class="nrt-cont-top-btn">
					//		<a class="" onclick="editfn('${id}','${content}',this)" >编辑</a>
					//	</div>`;
					//}else{edit=``};
					
					var attchList = o.attchList;
					if(typeof(attchList)!="undefined"&&attchList!=null&&$.trim(attchList)!=""){
						var remove = ``;
						$.each(attchList,function(){
							var fileid = this.id;
							var fileName = this.fileName;
							var fileServerId = this.fileServerId;
							var replyTeamId = this.replyTeamId;
							//if(isCbr==1){
							//	remove = `<a class="remove" onclick="removefn('${fileid}',this)" >删除</a>`
							//}
							file+=`<div class="">${remove}<a id="${fileid}" onclick="downloadfn('${fileServerId}')">${fileName}</a></div>`;
						})
					}
					
				}else if(n==2){
					id = o.id;
					teamId = o.replyTeamId;
					cbrId = o.userId;
					date = o.createdTime;
					firstDate = o.firstDate;
					danwei = o.danwei||"某单位";
					ld = o.userName;
					content = o.opinionContent;
					state = o.trackingType;
					
					if(state<3){
						state = "审批通过";
					}else{
						state = "返回修改";
					}
				};
				var child = o.opinionList;
				var zkgb = `<br>`;
				if(typeof(child)!="undefined"&&child!=null&&$.trim(child)!=""){
					if(child.length>0){
						zkgb = `
									<button class="btn btn-link">
										<font class="zhankai">展开<i class="fa fa-chevron-down" ></i></font>
										<font class="guanbi">关闭<i class="fa fa-chevron-up" ></i></font>
									</button>
								`;
					}
				}
				
				var pl = 20;
				var li = ``;
				
				if(n==1){
					var active=""
					//active = o.show;
					//if(active==1){active="active"}
					var color = "#00CCCC";
					
					if((i+3)%3==0){
						color = "#00CCCC";
					}else if((i+2)%3==0){
						color = "#FFCC33";
					}else if((i+1)%3==0){
						color = "#CCCCCC";
					}
					
					li = `
						<div class="newpanel-cont ${active}" color="${color}" subId=${subId};>
							<div class="newpanel-inner">
								<div class="newpanel-left">
									<div class="wh100">
										<i class="fa fa-circle-o"></i>
										<div class="newoanel-left-line"></div>
									</div>
								</div>
								<div class="newpanel-right">
									<div class="newpanel-right-top">
										<div class="nrt-date">
											<font>${firstDate}</font>
										</div>
									</div>
									
									<div class="newpanel-right-cent" id="${id}">
										<div class="nrt-cont" style="border-color:${color}">
											<div class="nrt-cont-top">
												<div class="nrt-cont-top-left">
													<div class="nrt-cont-top-title" onclick="viewcont('${teamId}','${subId}')">
														<i class="fa fa-user"></i>
														<font>${danwei}-${ld}</font>
													</div>
												</div>
												<div class="nrt-cont-top-left">
													<div class="nrt-cont-top-title2">
														<font>${date}</font>
													</div>
												</div>
												<div class="nrt-cont-top-right">
													${edit}
												</div>
											</div>
											<div class="nrt-cont-cent">
												<div class="wh100 scroller">
													<font class="nrt-cont-cent-font" >${content}</font>
												</div>
											</div>
											<div class="nrt-cont-bottom">
												<div class="nrt-cont-file">
													${file}
												</div>
											</div>
										</div>
									</div>
									
									<div class="newpanel-right-cent2" style="padding-left:${pl*n}px;">
										
									</div>
									
									<div class="newpanel-right-bottom">
										${zkgb}
									</div>
								</div>
							</div>
						</div>
					`;
				}else{
					var color = el.parents(".newpanel-cont").attr("color");
					subId = el.parents(".newpanel-cont").attr("subId");
					var newText = '				<font class="nrt-cont-cent-font" >';
					if(o.yjType == "1"){
						newText +='<div class="" title="" style="width:100%;">'+
							 '	<img src="'+content+'" style="max-height: 100px;"/>'+
							 '</div>';
					}else{
						newText +='<div class="" title="">'+
							 '	<span class="message">'+content+'</span>'+
							 '</div>';
					}
					newText+='				</font>';
					li = `
									<div class="newpanel-right-cent" id="${id}">
										<div class="nrt-cont" style="border-color:${color}">
											<div class="nrt-cont-top">
												<div class="nrt-cont-top-left">
													<div class="nrt-cont-top-title">
														<i class="fa fa-user"></i>
														<font>${ld}</font>
													</div>
												</div>
												<div class="nrt-cont-top-left">
													<div class="nrt-cont-top-state">
														<font>${state}</font>
													</div>
												</div>
												<div class="nrt-cont-top-left">
													<div class="nrt-cont-top-title2">
														<font>${date}</font>
													</div>
												</div>
											</div>
											<div class="nrt-cont-cent">
												<div class="wh100 scroller">
													<font class="nrt-cont-cent-font" >${newText}</font>
												</div>
											</div>
										</div>
									</div>
									
									<div class="newpanel-right-cent2" style="padding-left:${pl*n}px;">
										
									</div>
					`;
				}
				li = $(li);
				el.append(li);
				if(typeof(child)!="undefined"&&child!=null&&$.trim(child)!=""){
					if(child.length>0){
						var ul = $(li).find(".newpanel-right-cent2");
						eachfn(child,ul,n+1);
					}
				}
			})
		}
		
		 
		
		$ajax({
			url:allReplyListUrl,
 			data:{infoId:fileId},
			success:function(data){
				if(data&&data.length>0){
					$(".pagemenu").html("");
					eachfn(data,$(".pagemenu"),1);
					Metronic.initSlimScroll('.scroller');
					$(".newpanel-right-bottom .btn-link").unbind("click");
					$(".newpanel-right-bottom .btn-link").click(function(){
						$(this).parents(".newpanel-cont").toggleClass("active");
					});
				}else{
					$(".pagemenu").html("<span class='szqf zwCss2'>暂无内容！</span>");
				}
			}
		})
	}
	
	//文件转办——转办记录
	var initzbjlfn = function(){
		$ajax({
			url:zbjlDataUrl,
			data:{infoId:fileId},
			success:function(data){
				if(data&&data.length>0){
					$("#zbrecord").html("");
					$.each(data,function(i,item){
						$("#zbrecord").append(
							'<div class="record">'+
				            '	<label class="zbUser">转办人:</label>'+
				            '	<div><span>'+item.orgName+'&nbsp;&nbsp;'+item.userName+'</span><span class="zbDate">'+item.createdTime+'</span></div>'+
				            '	<label class="cbdw">承办单位/人:</label>'+
				            '	<div>'+item.receiverNames+'</div>'+
				            '</div>'
			            )
					});
				}else{
					$("#zbrecord").html('<div class="szqf zwCss">暂无转办记录！</div>');
				}
			}
		});	
	}
	
	//文件转办——催办记录
	var initcbfn = function(){
		$ajax({
			url:cbDataUrl,
			data:{infoId:fileId},
			success:function(data){
				if(data&&data.length>0){
					var html1= "";
					$.each(data,function(i,item){
						html1= '<div class="record">'+
					            '	<label class="zbUser">催办人:</label>'+
					            '	<div><span>'+item.userName+'</span><span class="zbDate">'+item.createdTime+'</span></div>'+
					            '	<label class="cbdw">催办留言:</label>'+
					            '	<div>'+item.urgeContent+'</div>';
								if(item.cbrName && !!item.cbrName){
									html1+= '	<label class="cbdw">承办人响应:</label>';
									html1+=	'	<div><span>'+item.cbrName+'</span><span class="dateStyle">'+item.cbTime+'</span><span>发布本次督办落实情况</span></div>';
								}
								html1+='</div>'
			            $("#cbrecord").append(html1);
					});
				}else{
					$("#cbrecord").html('<div class="szqf zwCss">暂无催办记录！</div>');
				}
			}
		});	
	}
	
	//文件转办—办结记录
	var initbjfn = function(){
		$ajax({
			url:bjDataUrl,
			data:{infoId:fileId},
			success:function(data){
				if(data&&data.length>0){
					$("#jybjrecord").html("");
					$.each(data,function(i,item){
						$("#jybjrecord").append(
							'<div class="record">'+
				            '	<label class="zbUser">'+item.subDeptName+' '+ item.userName+'</label>'+
				            '	<div><span>'+item.content+'</span><span class="zbDate">'+item.createdTime+'</span></div>'+
				            '</div>'
			            )
					});
				}else{
					$("#jybjrecord").html('<div class="szqf zwCss">暂无办结记录！</div>');
				}
			}
		});	
	}

	var initother = function(){
		if(fromMsg && fromMsg == true){
			$("#goback").hide();
		}
		
		//返回
		$("#goback").click(function(){
			if(!fromMsg){
				window.top.blfkfn();
			}
			skip();
		});
		//办理反馈-添加附件
		var o1 = $("#file1").createfile({
			//initdata:filedata1,
			yn:true,//true 多个  false 单个
			view:"view1",//view1 列表视图  view2 图片视图
			name:"file",// 后端获取文件的名称
			type:["ofd","pdf","doc","docx","wps"],
			maxsize:500*1024*1024,
			cssStyle:"width:0px;height:0px"
		});
		//查看附件
		$("#showfj").click(function(){
			$(".filelist").toggle();
		});
		$("body").click(function(e){
			if($(e.target).hasClass("showfj") || $(e.target).parents("div").hasClass("filelist")){
				return;
			};
			$(".filelist").hide();
		});
		
		$("#cuiban").click(function(){
			$("#viewcont2").modal("show");
		})
		
		$("#fasong").click(function(){
			var textarea = $("#textarea").val();
			if($.trim(textarea)==""){
				newbootbox.alert("请填写内容!");
				return;
			}
			$ajax({
				url:cuibanurl,
				data:{textarea:textarea,id:fileId},
				success:function(data){
					if(data.result=="success"){
						$("#viewcont2").modal("hide");
						newbootbox.alert("操作成功!").done(function(){
							window.location.reload();
							if(!fromMsg){
								window.top.blfkfn();
							}
						});
					}
				}
			});
		})
		$("#quxiao").click(function(){
			$("#viewcont2").modal("hide");
		})
		//转办
		$("#zhuanban").click(function(){
			newbootbox.newdialog({
				id:"zhuanbanDialog",
				width:800,
				height:600,
				header:true,
				title:"转办",
				classed:"cjDialog",
				url:"/app/db/document/blfk/html/zhuanbanDialog.html?fileIds="+fileId+"&fileFrom="+fileFrom+"&fromMsg="+fromMsg
			})
		});
		
		//取消办结
		$("#quxiaobanjie").click(function(){
			$ajax({
				url:cancleOperationUrl,
				data:{id:fileId},
				success:function(data){
					if(data.result=="success"){
						newbootbox.alert("取消办结成功!").done(function(){
							showButton();
							if(!fromMsg){
								window.top.blfkfn();
							}
						});
					}
				}
			});
		});
		
		$("#closeviewcont").click(function(){
			$("#viewcont").modal("hide");
		});
	}
		
	return{
		//加载页面处理程序
		initControl:function(){
			initReadfn();
			ifcuibanfn();
			showButton();
			takeMenufn();
			initdata();
			initblfkList();
			initps();
			initzbjlfn();
			initcbfn();
			initbjfn();
			initother();
			$(".scroller").css("height","100%");
			$(".slimScrollDiv").css("height","100%");
		}
	};
}();


//跳转返回事件
function skip(){
	if(frompage==0){
		window.location.href="/app/db/document/jcdb/html/main.html";
	}else if(frompage==1){
		var indexobject = gettop2().indexobject;
		
		var ifmenu = indexobject.ifmenu;
		var orgid = indexobject.orgid;
		var month = indexobject.month;
		var ytype = indexobject.ytype;
		window.location.href = "/app/db/document/jcdb/html/table.html?ifmenu=false&orgid="+orgid+"&month="+month+"&ytype="+ytype;
	}else{
		if(fileFrom == "djlr"){//文件来源于登记录入
			window.location.href="/app/db/document/djlr/html/djlr.html?fileFrom="+fileFrom;
		}else if(fileFrom == "grdb"){//文件来源于个人待办
			window.location.href="/app/db/document/grdb/html/grdb.html?fileFrom="+fileFrom;
		}else if(fileFrom=="blfk" || fileFrom=="jcdb"){  //文件来源于办理反馈
			window.location="/app/db/document/blfk/html/blfk.html?fileFrom=blfk";
		}else if(fileFrom=="jndb"){ 
			window.location.href="/app/db/document/jndb/html/jndb.html?fileFrom="+fileFrom;
		}else{ 
			window.location.href="/app/db/document/grdb/html/grdb.html?fileFrom="+fileFrom;
		}
	}

}

//批示详情
function showXQ(id){
	newbootbox.newdialog({
		id:"psDialog",
		width:800,
		height:600,
		header:true,
		title:"批示详情",
		classed:"cjDialog",
		url:"/app/db/document/view/html/psDialog.html?fileId="+id,
	})
}

//下载
function downloadfn(fileServerId){
	$ajax({
		url:downLoadUrl,
		data:{fileId:fileServerId},
	    success:function(data){
	    	if(data.url != null && data.url != ''){
	    		window.location.href = data.url;
	    	}
	    }
	});
}
function editfn(id,content,el){
	$(el).parents(".nrt-cont").find(".nrt-cont-file .remove").show();
	$("#editTeamId").attr("id",id);
	$("#replyContent").val(content);
}

function removefn(id,el){
	$ajax({
		url:delfjUrl,
		data:{id:id},
	    success:function(data){
	    	if(data.result=="success"){
	    		$(el).parent().remove();
	    		newbootbox.alert("附件已删除！");
	    	}
	    }
	});
}

function viewcont(teamId,subId){
	$("#viewcont").modal("show");
	$ajax({
		url:replyByTeamIdUrl,
		data:{subId:subId,teamId:teamId},
	    success:function(data){
	    	if(data && data.length>0){
	    		$(".viewcontent").html("");
				$.each(data,function(i,item){
					$(".viewcontent").append(
						'<div class="record">'+
			            '	<div class="record_line1"><span>'+item.userName+'&nbsp;&nbsp;'+item.createdTime+'&nbsp;&nbsp;落实情况：</span></div>'+
			            '	<div class="record_line2">'+item.replyContent+'</div>'+
			            '</div>'
		            )
				});
	    	}
	    }
	});
}