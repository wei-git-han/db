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
var cuibanurl = {"url":"/app/db/documentszinfo/press","dataType":"text"};//催办操作
var luoShiUrl = {"url":"/app/db/documentinfo/luoShiOperation","dataType":"text"}; //部管理员强制常态落实操作
var banjieUrl = {"url":"/app/db/documentinfo/banJieOperation","dataType":"text"}; //部管理员强制办结操作
var fileId=getUrlParam("fileId")||""; //主文件id
var fileFrom=getUrlParam("fileFrom")||""; //文件来源
var fromMsg=getUrlParam("fromMsg")||false; //是否为消息进入
var status = getUrlParam("status");//统计图传过来的状态
var leaderId = getUrlParam("leaderId");//统计图传过来的首长ID
var startdate = getUrlParam2("startdate");
var enddate = getUrlParam2("enddate");
var isFromChart = getUrlParam("isFromChart");
var isDepart = getUrlParam("isDepart");
var docTypeName = getUrlParam2("docTypeName");//类别
var jobContent = getUrlParam2("jobContent");//任务分工内容
var backAnchor = getUrlParam2("backAnchor");//当为jndb时，代表是从统计报表进入的，显示办理反馈数据的局内代办页面，所打开的详情页
var orgid0=getUrlParam("orgid")||""; //统计图传过来的机构
var nowYear0 = getUrlParam("nowYear");//“统计报表”页面上的“年度”
var month0=getUrlParam("month")||""; //统计图传过来的月份
var ytype0=getUrlParam("ytype")||""; //统计图传过来的办理状态
var backData = {
	status:status,
	leaderId:leaderId,
	startdate:startdate,
	enddate:enddate
}
$("#id").val(fileId);
var pageModule = function(){
	if(fileFrom=="djlr"){
		$('.xgfileWrap').show()
	}else{
		$('.xgfileWrap').hide()
	}
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
				if("blfk"==fileFrom){
					if(data.cuiBanBtn){
						$(".newbottom").show();
						$("#cuiban").show();
					}
					if(data.quXiaoBtn){
						$(".newbottom").show();
						$("#quxiaobanjie").show();
					}
					if(data.banjieBtn){
						$(".newbottom").show();
						$("#bjandls").show(); //办结和常态落实合并为一个
					}
				}
				
				if(data.zhuanBanBtn && ("blfk"==fileFrom||"djlr"==fileFrom)){
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
					$("#ttcont").html('');
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
						$("#fjList").append('<li num="'+i+'" id="fj_'+item.id+'"  data_id="'+item.id+'" formatId="'+item.fileServerFormatId+'" ><input type="checkbox" name="fjcheckbox" class="fileCheckbox" checkId="'+item.id+'"/><a class="'+(i==0?"fjactive":"")+'">'+item.fileName+'</a></li>');
					});
					//点击附件名称
					$("#fjList li a").click(function(){
						$("#fjList>li>a").removeClass("fjactive");
						$(this).addClass("fjactive");
						var clickfileId = $($(this).parent('li')).attr("data_id");
						getFile(clickfileId);
						$("#tt").tabs("select",parseInt($($(this).parent('li')).attr("num")))
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
   	 if(docTypeName == "重要决策部署分工"||docTypeName == "其他重要工作"||docTypeName == "部内重要工作分工"){
   		 if(jobContent&&jobContent!=''&&jobContent!='undefined'){
   			$(".ps_title").html("任务分工");
			$(".psMain").html("");
			$(".psMain").append(
				'<div class="psrecord">'+
	            '	<div  class="pslist">'+ jobContent+'</div>'+
	            '</div>'
            )
   		 }else{
   			$(".line3").html("");
   		 }
	 }else{
		$ajax({
			url:getSzpsListUrl,
			data:{infoId:fileId},
			success:function(data){
				if(data&&data.length>0){
					$(".psMain").html("");
					$.each(data,function(i,item){
						$(".psMain").append(
							'<div class="psrecord">'+
				            '	<div  class="pslist"><span>'+item.userName+'&nbsp;&nbsp;'+item.createdTime+'&nbsp;&nbsp;批示：</span><span>'+item.leaderComment+'</span></div>'+
				            '</div>'
			            )
					});
				}else{
					$(".line3").html("");
				}
			}
		});	
	 }

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
					var miji="";
					if(data.urgencyDegree && data.urgencyDegree!="" && !!data.urgencyDegree){
						miji = '<font class="miji secretLevelName">'+data.urgencyDegree+'</font>';
					}
					$(".commonHtml").append(
						'<div class="line1"><span class="fileName">'+data.docTitle+'</span>'+miji+'</div>'+
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
					id = o.id;
					teamId = o.teamId;
					cbrId = o.cbrId;
					date = o.updateTime;
					firstDate = o.firstDate;
					danwei = o.danwei||"某单位";
					ld = o.cbrName;
					content = o.content;
					subId = o.subId;
					ideaGroupId = o.ideaGroupId;
					edit = o.isSameDept;
					checkStatus = o.checkStatus;
					if(edit=="true" && fileFrom=="blfk"){
                        // '${id}','${content}','${subId}','${checkStatus}','${teamId}','${ideaGroupId}',this
						edit = `<div class="nrt-cont-top-btn">
							<a class="" onclick="editfn('${id}','${subId}','${checkStatus}','${teamId}','${ideaGroupId}',this)" >编辑</a>
						</div>`;
					}else{
					    edit=``
					};
					
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
											<a style="float: right;" id="yjjl" onclick="showfn('${subId}','${ideaGroupId}')">意见记录</a>
										</div>
									</div>
									
									<div class="newpanel-right-cent" id="${id}">
										<div class="nrt-cont" style="border-color:${color}">
											<div class="nrt-cont-top">
												<div class="nrt-cont-top-left">
													<div class="nrt-cont-top-title" onclick="viewcont('${teamId}','${subId}','${ideaGroupId}')">
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
		//意见记录事件
		
		
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
						if(item.xbOperateFlag == 1){
							$("#zbrecord").append(
									'<div class="record">'+
									'	<label class="zbUser">承办人:</label>'+
									'	<div><span>'+item.orgName+'&nbsp;&nbsp;'+item.userName+'</span><span class="zbDate">'+item.createdTime+'</span></div>'+
									'	<label class="cbdw">增加协办人:</label>'+
									'	<div>'+item.receiverNames+'</div>'+
									'</div>'
							)
						}else if(item.xbOperateFlag == 2 || item.xbOperateFlag == 3){
							$("#zbrecord").append(
									'<div class="record">'+
									'	<label class="zbUser">承办人:</label>'+
									'	<div><span>'+item.orgName+'&nbsp;&nbsp;'+item.userName+'</span><span class="zbDate">'+item.createdTime+'</span></div>'+
									'	<label class="cbdw">修改协办人:</label>'+
									'	<div>'+item.receiverNames+'</div>'+
									'</div>'
							)
						}else{
							$("#zbrecord").append(
									'<div class="record">'+
									'	<label class="zbUser">转办人:</label>'+
									'	<div><span>'+item.orgName+'&nbsp;&nbsp;'+item.userName+'</span><span class="zbDate">'+item.createdTime+'</span></div>'+
									'	<label class="cbdw">承办单位/人:</label>'+
									'	<div>'+item.receiverNames+'</div>'+
									'</div>'
							)
						}
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
		if(fromMsg && fromMsg == "true"){
			$("#goback").hide();
		}
		//扫描设置
		$("#scanSet").click(function(){
			$(".smczcont").toggle();
		});
		//返回
		$("#goback").click(function(){
			if(fromMsg && fromMsg=="true"){
			}else{
				if(np2==0){
					window.top.blfkfn();
				}
				skip();
			}
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
				newbootbox.alert("请填写内容！");
				return;
			}
			$ajax({
				url:cuibanurl,
				data:{textarea:textarea,id:fileId},
				success:function(data){
					if(data.result=="success"){
						$("#viewcont2").modal("hide");
						newbootbox.alert("操作成功！").done(function(){
							window.location.reload();
							if(fromMsg && fromMsg=="true"){
								//windowClose();
							}else{
								if(np2==0){
									window.top.blfkfn();
								}
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
						newbootbox.alert("取消办结成功！").done(function(){
							showButton();
							changToNum2(function(){
                                if(fromMsg && fromMsg=="true"){
                                    windowClose();
                                }else{
                                    if(np2==0){
                                        window.top.blfkfn();
                                    }
                                }
							})
						});
					}
				}
			});
		});
		
		$("#closeviewcont").click(function(){
			$("#viewcont").modal("hide");
		});
		
		
		//办结
		$("#banjie").click(function(){
			newbootbox.oconfirm({
			 	title:"提示",
			 	message: "是否确认要进行文件办结操作？",
			 	callback1:function(){
	 				$ajax({
	 					url:banjieUrl,
	 					data:{infoId:fileId},
	 					type: "GET",
	 					success:function(data){
	 						if(data.result == "success"){
	 							newbootbox.alert("已办结！").done(function(){
	 							    changToNum2(function(){
                                        if(fromMsg && fromMsg=="true"){
                                            windowClose();
                                        }else{
                                            if(np2==0){
                                                window.top.blfkfn();
                                            }
                                            skip();
                                        }
	 								})
	 							});
	 						}else{
	 							newbootbox.alert("办结失败！")
	 						}
	 					}
	 				});
			 	}
			});
		});
		
		//常态落实
		$("#luoshi").click(function(){
			newbootbox.oconfirm({
			 	title:"提示",
			 	message: "是否确认要进行文件常态落实操作？",
			 	callback1:function(){
	 				$ajax({
	 					url:luoShiUrl,
	 					data:{infoId:fileId},
	 					type: "GET",
	 					success:function(data){
	 						if(data.result == "success"){
	 							newbootbox.alert("文件已常态落实！").done(function(){
	 							    changToNum2(function(){
                                        if(fromMsg && fromMsg=="true"){
                                            windowClose();
                                        }else{
                                            if(np2==0){
                                                window.top.blfkfn();
                                            }
                                            skip();
                                        }
	 								})
	 							});
	 						}else{
	 							newbootbox.alert("落实失败！")
	 						}
	 					}
	 				});
			 	}
			});
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
		},
		initblfkList:function(){
            initblfkList();
        },
		takeMenufn:function(){
			takeMenufn();
			$(".scroller").css("height","100%");
			$(".slimScrollDiv").css("height","100%");
		}
	};
}();




//$(function()){
//	$("#checkAll").click(function){
//		$("input:checkbox").prop("checked","checked");
//	};
//	$("#checkNo").click(function){
//		$("input:checkbox").removeAttr("checked","checked");
//	};
//	$("#checkRev").click(function){
//		$("input:checkbox").each(function()){
//			this.checked=!this.checked;
//		}
//	};
//}




//跳转返回事件
function skip(){
	if(frompage==0){
		if(isFromChart=='1'){
			if(isDepart=='0'){
				var status = sessionStorage.getItem('status');
				var leaderId = sessionStorage.getItem('leaderId');
				var startdate = sessionStorage.getItem('startdate');
				var enddate = sessionStorage.getItem('enddate');
				window.location.href="/app/db/document/tjsj/html/tjsj2.html?fileFrom="+fileFrom+"&status="+status+"&leaderId="+leaderId+"&startdate="+startdate+"&enddate="+enddate;
			}else{
				var orgid = sessionStorage.getItem('orgid');
				var month = sessionStorage.getItem('month')
				var ytype = sessionStorage.getItem('ytype')
				window.location.href="/app/db/document/jcdb/html/table2.html?orgid="+orgid+"&month="+month+"&ytype="+ytype;
			}
		}else{
			window.location.href="/app/db/document/jcdb/html/main.html";
		}
//		window.location.href="/app/db/document/jcdb/html/main.html";
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
			if(backAnchor == "jndb"){
				window.location="/app/db/document/jndb/html/jndb.html?fileFrom=jcdb"+"&orgid="+orgid0+"&nowYear="+nowYear0+"&month="+month0+"&ytype="+ytype0;
			}else{
				window.location="/app/db/document/blfk/html/blfk.html?fileFrom="+fileFrom;
			}
		}else if(fileFrom=="jndb"){ 
			window.location.href="/app/db/document/jndb/html/jndb.html?fileFrom="+fileFrom;
		}else if(fileFrom=="sztj"){ 
			window.location.href="/app/db/document/tjsj/html/tjsj.html?fileFrom="+fileFrom+"&status="+backData.status+"&leaderId="+backData.leaderId+"&startdate="+backData.startdate+"&enddate="+backData.enddate;
		}else{ 
			window.location.href="/app/db/document/grdb/html/grdb.html?fileFrom="+fileFrom;
		}
	}
}






/*//批示详情
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
}*/

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
function editfn(id,subId,checkStatus,teamId,ideaGroupId,el){
	$(el).parents(".nrt-cont").find(".nrt-cont-file .remove").show();
	newbootbox.oconfirm({
        title:"提示",
        message: "当前办理反馈内容已正式提交，是否确认修改？",
        callback1:function(){
              newbootbox.newdialog({
                id:"editDialog",
                width:800,
                height:600,
                header:true,
                title:"编辑",
                classed:"cjDialog",
                url:"/app/db/document/view/html/editDialog.html?fileId="+fileId+"&subId="+subId+"&teamId="+teamId+"&fromMsg="+fromMsg+"&checkStatus="+checkStatus+"&ideaGroupId="+ideaGroupId+"&fileFrom="+fileFrom
              })
        }
    });

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

function viewcont(teamId,subId,ideaGroupId){
	$("#viewcont").modal("show");
	$ajax({
		url:replyByTeamIdUrl,
		data:{subId:subId,teamId:teamId,ideaGroupId:ideaGroupId},
	    success:function(data){
	    	if(data && data.length>0){
	    		$(".viewcontent").html("");
				$.each(data,function(i,item){
					var statusName = "";
					if(item.chooseStatus==1){
						statusName='<div class="record_line1">设置状态：办理中</div>';
					}else if(item.chooseStatus==2){
						statusName='<div class="record_line1">设置状态：办结</div>';
					}else if(item.chooseStatus==3){
						statusName='<div class="record_line1">设置状态：常态落实</div>';
					}
					$(".viewcontent").append(
						'<div class="record">'+
			            '	<div class="record_line1"><span>'+item.userName+'&nbsp;&nbsp;'+item.createdTime+'&nbsp;&nbsp;落实情况：</span></div>'+
			            '	<div class="record_line2">'+item.replyContent+'</div>'+statusName+
			            '</div>'
		            )
				});
	    	}else{
	    		$(".viewcontent").html("暂无数据!");
	    	}
	    }
	});
}
//意见记录事件
var showfn = function(subId, ideaGroupId){
	newbootbox.newdialog({
		id:"opinionDialog",
		width:800,
		height:600,
		header:true,
		title:"意见收集",
		classed:"cjDialog",
		style:{"padding":"0px"},
		url:"/app/db/document/view/html/opinion.html?subId="+subId+"&ideaGroupId="+ideaGroupId+"&fileFrom="+fileFrom
	})
	
//	url:"/app/db/document/view/html/psDialog.html?fileId="+id,
}
//下载附件
$("#downLoadfj").click(function(){
	if($("#fjList").find("input[name=fjcheckbox]:checked").length>0){
		var checkId = [];
		$("#fjList").find("input[name=fjcheckbox]").each(function(){
			if($(this).is(":checked")){
				checkId.push($(this).attr("checkId"));
			}
		})
		if(checkId.length==1){
			$.ajax({
				url:'/app/db/documentfile/downLoadFile',
				data:{ids:checkId[0],infoId:fileId},
				success:function(data){
					window.location.href = data
				}
			})
		}else{
			window.location.href="/app/db/documentfile/downLoadFile?ids="+checkId.toString()+"&infoId="+fileId
		}
	}else{
		newbootbox.alert("请选中要下载的附件！"); 
	}
});