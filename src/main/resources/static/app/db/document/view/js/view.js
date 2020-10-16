var menuUrl = {"url":rootPath +"/documentfile/list","dataType":"text"};;//左侧文件menulist（文件列表-附件list）
var getFileUrl = {"url":"/app/db/documentfile/getFile","dataType":"text"};;//左侧页签获取文件信息
var getDataUrl = {"url":"/app/db/documentinfo/info","dataType":"json"};//右侧获取主文件信息
var getSzpsListUrl = {"url":rootPath +"/documentszps/queryList","dataType":"text"}; //获取首长批示
var zbjlDataUrl = {"url":"/app/db/documentzbjl/list","dataType":"json"}; //文件转办-转办记录list
var getButtonParamUrl = {"url":"/app/db/subdocinfo/buttonParam","dataType":"json"}; //获取按钮显示控制参数
var chengbanUrl = {"url":"/app/db/subdocinfo/undertakeOperation","dataType":"text"}; //承办地址
var saveUrl = {"url":"/app/db/replyexplain/save","dataType":"text"}; //办理反馈保存
var finishUrl = {"url":"/app/db/subdocinfo/finishOperation","dataType":"text"}; //完成审批操作
var returnUrl = {"url":"/app/db/subdocinfo/returnOperation","dataType":"text"}; //返回修改操作
var luoShiUrl = {"url":"/app/db/subdocinfo/luoShiOperation","dataType":"text"}; //常态落实操作
var banjieUrl = {"url":"/app/db/subdocinfo/banJieOperation","dataType":"text"}; //办结操作
var bjDataUrl = {"url":"/app/db/documentinfo/getBanJieList","dataType":"text"}; //办结记录
var delfjUrl = {"url":"/app/db/replyexplain/deleteAttch","dataType":"text"}; //删除办理反馈中的附件
var downLoadUrl= {"url":"/app/db/replyexplain/downLoad","dataType":"text"}; //下载办理反馈中的附件
var subReplyListUrl = {"url":"/app/db/replyexplain/subReplyList","dataType":"text"}; //局内办理反馈list
var replyByTeamIdUrl = {"url":"/app/db/replyexplain/getReplyByTeamId","dataType":"text"}; //获取某组办理反馈
var cbDataUrl = {"url":"/app/db/documentinfo/getCuiBanlist","dataType":"text"}; //催办记录list
var latestCuiBanUrl = {"url":"/app/db/documentinfo/getLatestCuiBan","dataType":"text"}; //获取最新的催办
var uploadFileUrl = "/app/db/documentinfo/uploadFile";//文件上传
var delFileUrl = {"url":"/app/db/documentfile/delete","dataType":"text"}; /*相关文件--删除附件*/
var fileId=getUrlParam("fileId")||""; //主文件id
var subId=getUrlParam("subId")||""; //主文件id
var fileFrom=getUrlParam("fileFrom")||""; //文件来源
var fromMsg=getUrlParam("fromMsg")||false; //是否为消息进入
var showFileButton = getUrlParam("showFileButton")||false
var docStatus = getUrlParam("docStatus")||"" //文件办理状态
var docTypeName = getUrlParam2("docTypeName");//类别
var jobContent = getUrlParam2("jobContent");//任务分工内容
var isCbr = 0;//承办人标识
var isSave = 0;//保存成功提示标识
var ifShowEditBtn="0";//是否有编辑按钮
var scanFilePath = "";//扫描件路径
var showCollectIdeaButtonUrl={"url":"/app/db/addXbDeal/showCollectIdeaButton","dataType":"text"}; //是否显示意见收集
var commitIdeaUrl={"url":"/app/db/addXbDeal/commitIdea","dataType":"text"}; //发送意见url



//var listUrl = {"url":"/app/db/document/view/data/opinion.json","dataType":"text"}; //意见记录list
var showCurrIdeaRecordUrl = {"url":"/app/db/addXbDeal/showCurrIdeaRecord","dataType":"text"}; //意见记录
var teamId=getUrlParam("teamId");//
var subId=getUrlParam("subId");//
var fileFrom=getUrlParam("fileFrom")||""; //文件来源

var pageModule = function(){
	
	//意见记录
	var initList = function(){
		$ajax({
			url:showCurrIdeaRecordUrl,
 			data:{subId:subId},
			success:function(data){
				var html1= "";
				var xbUser = [];
				var texts = "承办人："+data.chenban+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;协办人："+data.xieban;

//				$("#chenban").text(data.chenban);
				$("#xieban").html(texts);
				$("#xieban").attr('title',data.xieban);
				datas = data.docXbIdeas;
				$("#opinionList").html('');
				$.each(datas,function(i,o){
					var createdTime = o.createdTime;
//					var cbrList = o.cbrList;
					html1=	'<div class="timelinesheys">'+
							'	<div class="timeline-icon">'+
							'		<i class="icontime"></i>'+
							'	</div>'+
							'	<div class="timeline-user">'+
							'		<span class="createTime">'+createdTime+'</span>'+
							'	</div>'+
							'	<div class="timeline-body">';
//							$.each(cbrList,function(i,item){
//								xbUser.push(o.userName);
								html1 += '<div class="timeline-content">'+
									        '	<div class="userName"><i class="fa fa-user"></i>&nbsp;'+o.userName+'</div>';
								html1 += '	<div class="content">'+o.feedBackIdea+'</div>';
								html1 += '</div>';
//							})
							
							html1 +='	</div>'+
									'</div>'
					$("#opinionList").append(html1);
				})
				$("#xbUser").html(xbUser.toString());
			}
		})
	}
	
	
	$('#id').val(fileId);
	if(showFileButton=="true"){
		$('.xgfileWrap').show()
	}else{
		$('.xgfileWrap').hide()
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
   //文件办理状态 判断显示
//	var isShowDocStatus=function(){
//		if(isCbr == 0){
//			alert(docStatus)
//			if(docStatus=="9" || docStatus==9){
//				$("#shuru").show();
//				$("#clear").show();
//				$("#fasong").show();
//				$("#sryj_center").show();
//				$(".blfk_bottom").show(); //意见外大框
//				$(".newbottom").show(); //所有按钮的容器
//				$(".blfk_top").css({"bottom":"40%","height":"58%"});   //意见框上方元素样式控制
//				$("#clear").show();
//				$("#fanli").hide();
//			}else{
//				$("#shuru").hide();
//				$("#clear").hide();
//				$("#fasong").hide();
//				$("#sryj_center").hide();
//				$(".newbottom").hide(); //所有按钮的容器
//			}
//		}
//	}
	
	
	//是否显示意见收集按钮
	var showCollectIdeaButton=function(){
		$ajax({
			url:showCollectIdeaButtonUrl,
			data:{infoId:fileId,subId:subId},
			success:function(data){
				if(data.result=="success"){
					$("#yjsj").show();
				}else{
					$("#yjsj").hide();
				}
			}
		});
	}
	
	/* 按钮权限控制 */
	var showButton = function(){
		$ajax({
			url:getButtonParamUrl,
			data:{subId:subId},
			success:function(data){
				$(".ifShow").hide();
				/*Integer docStatus=0;//1:待转办；3：退回修改；5：待落实；7：待审批；9：办理中；11：建议办结;
				String roleType = DbDefined.ROLE_6;//角色标识（1：首长；2：首长秘书；3：局长；4：局秘书；5：处长；6：参谋;）
				boolean isCheckUser=false;//是否是当前办理人
				boolean isUndertaken=false;//是否已承办
				boolean isUndertaker=false;//是否承办人*/
				if(data.docStatus<10){//文件为办理中
					if(data.docStatus==1 && "jndb"==fileFrom){
						$(".newbottom").show(); 
						$("#zhuanban").show();
					}else{
						if(data.docStatus==5 && data.isCheckUser){//承办、转办按钮显示，输入框相关不显示
							$(".newbottom").show(); //所有按钮的容器
							$("#chengban").show();
							$("#zhuanban").show();
						}else{
							if(data.isUndertaker && data.docStatus!=5){
								$(".newbottom").show(); //所有按钮的容器
								$("#bjandls").show(); //办结和常态落实合并为一个
							}
							if(data.isCheckUser){//显示办结、常态落实,输入框
								$(".blfk_bottom").show(); //意见外大框
								$(".newbottom").show(); //所有按钮的容器
								$(".blfk_top").css({"bottom":"50%","height":"50%"});   //意见框上方元素样式控制
								$("#clear").show();
								if(data.roleType=='3'){//是局长显示审批完成否则显示提交
									$("#sptg").show();
									$("#send").show();
									if(!data.isUndertaker){
										$("#changewrite").show();
									}
								}else{
									$("#tijiao").show();
								}
								if(!data.isUndertaker){//当前处理人是承办人则不显示返回修改
									$("#fhxg").show();
								}else{
									$("#save").show();
									$("#ifaddfj").show();
									$("#showfj").show();
									$("#zhuanban").show();
									isCbr = 1;
								}
							}else{
								if(data.isXBPerson && data.docStatus==9){
									$(".blfk_bottom").show(); //意见外大框
									$(".newbottom").show(); //所有按钮的容器
									$(".blfk_top").css({"bottom":"50%","height":"50%"});   //意见框上方元素样式控制
									$("#clear").show();
									$("#fasong").show();
								}
							}
						}
					}
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
						            '	<div class="pslist"><span>'+item.userName+'&nbsp;&nbsp;'+item.createdTime+'&nbsp;&nbsp;批示：</span><span>'+item.leaderComment+'</span></div>'+
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
				var id = '';
				var cbrId = '';
				var date = '';
				var danwei = '';
				var ld = '';
				var content = '';
				var state = '';
				var file = '';
				var edit = '';
				var otherhtml = '';
				var teamId = '';
				var checkStatus = '';
				var checkStatusname = '';
				if(n==1){
					id = o.teamId;
					teamId = o.teamId;
					cbrId = o.cbrId;
					date = o.updateTime;
					firstDate = o.firstDate;
					danwei = o.danwei||"某单位";
					ld = o.cbrName;
					content = o.content;
					checkStatus = o.checkStatus;
					checkStatusname = o.checkStatusName;
					ideaGroupId = o.ideaGroupId;
					if(checkStatus){
						otherhtml = '<div class="nrt-cont-top-right">'+
									'	<div class="nrt-cont-top-btn">'+
									'		<span class="nrt-cont-top-content">本次提交状态：'+checkStatusname+'</span>'+
									'	</div>'+
									'</div>';
					}else{
						otherhtml='';
					}
					edit = o.edit;
					if(edit==true){
						ifShowEditBtn="1";
						edit =  '<div class="nrt-cont-top-btn">'+
								'	<a class="isEditbtn" data="'+id+'" dataContent="'+content+'"  onclick="editfn(\''+id+'\',this,\''+checkStatus+'\')" >编辑</a>'+
								'</div>';
					}else{
						edit=''
					};
					var attchList = o.attchList;
					if(typeof(attchList)!="undefined"&&attchList!=null&&$.trim(attchList)!=""){
						var remove = '';
						$.each(attchList,function(){
							var fileid = this.id;
							var fileName = this.fileName;
							var fileServerId = this.fileServerId;
							var replyTeamId = this.replyTeamId;
							if(isCbr==1){
								remove = '<a class="remove" onclick="removefn(\''+fileid+'\',this)" >删除</a>'
							}
							file+='<div class="">'+remove+'<a id="'+fileid+'" onclick="downloadfn(\''+fileServerId+'\')">'+fileName+'</a></div>';
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
					ideaGroupId = o.ideaGroupId;
					if(state<3){
						state = "审批通过";
					}else{
						state = "返回修改";
					}
				};
				var child = o.opinionList;
				var zkgb = '<br>';
				if(typeof(child)!="undefined"&&child!=null&&$.trim(child)!=""){
					if(child.length>0){
						zkgb =  ' <button type="button" class="btn btn-link">'+
								'	<font class="zhankai">展开<i class="fa fa-chevron-down"></i></font>'+
								'	<font class="guanbi">关闭<i class="fa fa-chevron-up"></i></font>'+
								'</button>';
					}
				}
				
				var pl = 20;
				var li = '';
				
				if(n==1){
					var active = o.show;
					if(active==1){active="active"}
					var color = "#00CCCC";
					
					if((i+3)%3==0){
						color = "#00CCCC";
					}else if((i+2)%3==0){
						color = "#FFCC33";
					}else if((i+1)%3==0){
						color = "#CCCCCC";
					}
					
					li ='<div class="newpanel-cont '+active+'" color="'+color+'">'+
					'	<div class="newpanel-inner">'+
					'		<div class="newpanel-left">'+
					'			<div class="wh100">'+
					'				<i class="fa fa-circle-o"></i>'+
					'				<div class="newoanel-left-line"></div>'+
					'			</div>'+
					'		</div>'+
					'		<div class="newpanel-right">'+
					'			<div class="newpanel-right-top">'+
					'				<div class="nrt-date">'+
					'					<font>'+firstDate+'</font>'+
					'					<font class="pull-right yjjl" onclick="opinionView(\''+teamId+'\',\''+subId+'\',\''+ideaGroupId+'\')">意见记录</font>'+
					'				</div>'+
					'			</div>'+
					'			<div class="newpanel-right-cent" id="'+id+'">'+
					'				<div class="nrt-cont" style="border-color:'+color+'">'+
					'					<div class="nrt-cont-top">'+
					'						<div class="nrt-cont-top-left">'+
					'							<div class="nrt-cont-top-title" onclick="viewcont(\''+teamId+'\',\''+subId+'\',\''+ideaGroupId+'\')">'+
					'								<i class="fa fa-user"></i>'+
					'								<font>'+danwei+'-'+ld+'</font>'+
					'							</div>'+
					'						</div>'+
					'						<div class="nrt-cont-top-left">'+
					'							<div class="nrt-cont-top-title2">'+
					'								<font>'+date+'</font>'+
					'							</div>'+
					'						</div>'+
					'						<div class="nrt-cont-top-right">'+edit+
					'						</div>'+otherhtml+
					'					</div>'+
					'					<div class="nrt-cont-cent">'+
					'						<div class="wh100 scroller">'+
					'							<font class="nrt-cont-cent-font" >'+content+'</font>'+
					'						</div>'+
					'					</div>'+
					'					<div class="nrt-cont-bottom">'+
					'						<div class="nrt-cont-file">'+
												file+
					'						</div>'+
					'					</div>'+
					'				</div>'+
					'			</div>'+
					'			<div class="newpanel-right-cent2" style="padding-left:'+pl*n+'px">'+
					'			</div>'+
					'			<div class="newpanel-right-bottom">'+
									zkgb+
					'			</div>'+
					'		</div>'+
					'	</div>'+
					'</div>';
				}else{
					var color = el.parents(".newpanel-cont").attr("color");
					li ='<div class="newpanel-right-cent" id="'+id+'">'+
					'	<div class="nrt-cont" style="border-color:'+color+'">'+
					'		<div class="nrt-cont-top">'+
					'			<div class="nrt-cont-top-left">'+
					'				<div class="nrt-cont-top-title">'+
					'					<i class="fa fa-user"></i>'+
					'					<font>'+ld+'</font>'+
					'				</div>'+
					'			</div>'+
					'			<div class="nrt-cont-top-left">'+
					'				<div class="nrt-cont-top-state">'+
					'					<font>'+state+'</font>'+
					'				</div>'+
					'			</div>'+
					'			<div class="nrt-cont-top-left">'+
					'				<div class="nrt-cont-top-title2">'+
					'					<font>'+date+'</font>'+
					'				</div>'+
					'			</div>'+
					'		</div>'+
					'		<div class="nrt-cont-cent">'+
					'			<div class="wh100 scroller">'+
					'				<font class="nrt-cont-cent-font" >';
										if(o.yjType == "1"){
											li +='<div class="" title="" style="width:100%;">'+
												 '	<img src="'+content+'" style="max-height: 100px;"/>'+
												 '</div>';
										}else{
											li +='<div class="" title="">'+
												 '	<span class="message">'+content+'</span>'+
												 '</div>';
										}
					li +=   '				</font>'+
					'			</div>'+
					'		</div>'+
					'	</div>'+
					'</div>'+
					'<div class="newpanel-right-cent2" style="padding-left:'+pl*n+'px">'+
					'</div>';
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
			url:subReplyListUrl,
 			data:{infoId:fileId,subId:subId},
			success:function(data){
				if(data&&data.length>0){
					$(".pagemenu").html("");
					eachfn(data,$(".pagemenu"),1);
					$(".newpanel-right-bottom .btn-link").unbind("click");
					$(".newpanel-right-bottom .btn-link").click(function(){
						$(this).parents(".newpanel-cont").toggleClass("active");
					})
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
		if(fromMsg && (fromMsg == true || fromMsg == "true")){
			$("#goback").hide();
		}
				
		//返回
		$("#goback").click(function(){
			if(fromMsg && fromMsg=="true"){
			}else{
				window.top.grdbfn();
				skip();
			}
		});
		
		
		//办理反馈清屏
		$("#clear").click(function(){
			if($("span.css3").attr("data") =="1"){
            	clearsign();
        	}else{
            	$("#replyContent").val("");
        	}
		});
		
		//送审
		$("#tijiao").click(function(){
			var cbrFlag="";  
			if(isCbr && isCbr == 1){ //承办人
				cbrFlag="1";
				if($("#replyContent").val() !="" && !!$("#replyContent").val()){
					isSave=1;
					$("#save").click();
				}else{
					if(ifShowEditBtn&&ifShowEditBtn=="0"){//如果没有编辑按钮：说明没有任何反馈，则必须填写反馈
						newbootbox.alert("反馈不能为空！");
						return;
					}
				}
				
				newbootbox.newdialog({
					id:"statusDialog",
					width:550,
					height:400,
					header:true,
					title:"选择办件状态",
					classed:"cjDialog",
					url:"/app/db/document/view/html/statusDialog.html?subId="+subId+"&infoId="+fileId+"&cbrFlag="+cbrFlag+"&fromMsg="+fromMsg
				});
			}else{
				newbootbox.newdialog({
					id:"tijiaoDialog",
					width:800,
					height:600,
					header:true,
					title:"提交",
					classed:"cjDialog",
					url:"/app/db/document/view/html/tijiaoDialog.html?subId="+subId+"&infoId="+fileId+"&replyContent="+$("#replyContent").val()+"&cbrFlag="+cbrFlag+"&fromMsg="+fromMsg
				})
			}
		});
		
		//承办
		$("#chengban").click(function(){
			$("#yjsj").show();
			$ajax({
				url:chengbanUrl,
				data:{subId:subId},
				type: "GET",
				success:function(data){
					if(data.result == "success"){
						showButton();
						initblfkList();
					}
				}
			});
		});
		
		//返回修改
		$("#fhxg").click(function(){
			var replyContent = $("#replyContent").val();
			var imgFileId="";
			var saveFlag="0"; //文字
			if($("span.css3").attr("data") =="1"){
				if(checkIsModified()){
					saveFlag="1"; //图片
					$.ajax({
						url : "/app/base/user/getToken",
						type : "GET",
						async : false,
						success : function(data) {//插件读取文件
							end();	//签批确定
							var surl = location.protocol+ "//"+ location.host+ "/servlet/suwell/savePictureYj?access_token="+data.result;
						    document.getElementById("signtool").SetUploadURL(surl);
							var result = document.getElementById("signtool").UploadImageStream();
							if(result && result!="" && result!=null){
								imgFileId = result.replace(/^\"|\"$/g,'');
								replyContent = imgFileId;
							}else{
								saveFlag="0"; //图片
							}
						}
					})
				}else{
					newbootbox.alert('保存失败！请填写您的意见！')
				}
			}
			$ajax({
				url:returnUrl,
				data:{infoId:fileId,subId:subId,replyContent:replyContent,saveFlag:saveFlag},
				type: "GET",
				success:function(data){
					if(data.result == "success"){
						newbootbox.alert("已返回承办人！").done(function(){
							changToNum2(function(){
                                window.location.reload();
                                if(fromMsg && fromMsg=="true"){
                                }else{
                                    window.top.jndbfn();
                                    window.top.grdbfn();
                                    window.top.blfkfn();
                                    //changToNum()
                                }
							})
						});
					}
				}
			});
		});
		
		/*局长送审 */
		$("#send").click(function(){
			var cbrFlag="";
			var replyContent = $("#replyContent").val();
			if(isCbr && isCbr == 1){ //承办人
				cbrFlag="1";
				if(replyContent !="" && !!replyContent){
					isSave=1;
					$("#save").click();
				}else{
					if(ifShowEditBtn&&ifShowEditBtn=="0"){//如果没有编辑按钮：说明没有任何反馈，则必须填写反馈
						newbootbox.alert("反馈不能为空！");
						return;
					}
				}
				newbootbox.newdialog({
					id:"statusDialog",
					width:550,
					height:400,
					header:true,
					title:"选择办件状态",
					classed:"cjDialog",
					url:"/app/db/document/view/html/statusDialog.html?subId="+subId+"&infoId="+fileId+"&cbrFlag="+cbrFlag+"&fromMsg="+fromMsg
				});
			}else{
				var imgFileId="";
				var saveFlag="0"; //文字
				if($("span.css3").attr("data") =="1"){
					if(checkIsModified()){
						saveFlag="1"; //图片
						$.ajax({
							url : "/app/base/user/getToken",
							type : "GET",
							async : false,
							success : function(data) {//插件读取文件
								end();	//签批确定
								var surl = location.protocol+ "//"+ location.host+ "/servlet/suwell/savePictureYj?access_token="+data.result;
							    document.getElementById("signtool").SetUploadURL(surl);
								var result = document.getElementById("signtool").UploadImageStream();
								if(result && result!="" && result!=null){
									imgFileId = result.replace(/^\"|\"$/g,'');
									replyContent = imgFileId;
								}else{
									saveFlag="0"; //图片
								}
							}
						})
					}else{
						newbootbox.alert('保存失败！请填写您的意见！')
					}
				}
				newbootbox.newdialog({
					id:"tijiaoDialog",
					width:800,
					height:600,
					header:true,
					title:"提交",
					classed:"cjDialog",
					url:"/app/db/document/view/html/tijiaoDialog.html?subId="+subId+"&infoId="+fileId+"&replyContent="+replyContent+"&cbrFlag="+cbrFlag+"&saveFlag="+saveFlag+"&fromMsg="+fromMsg
				})
			}
		});
		
		
		//审批通过
		$("#sptg").click(function(){
			newbootbox.oconfirm({
			 	title:"提示",
			 	message: "审批完成，确认后将正式发布此办件落实情况？",
			 	callback1:function(){
			 		var replyContent = $("#replyContent").val();
					var imgFileId="";
					var saveFlag="0"; //文字
					if($("span.css3").attr("data") =="1"){
						if(checkIsModified()){
							saveFlag="1"; //图片
							$.ajax({
								url : "/app/base/user/getToken",
								type : "GET",
								async : false,
								success : function(data) {//插件读取文件
									end();	//签批确定
									var surl = location.protocol+ "//"+ location.host+ "/servlet/suwell/savePictureYj?access_token="+data.result;
								    document.getElementById("signtool").SetUploadURL(surl);
									var result = document.getElementById("signtool").UploadImageStream();
									if(result && result!="" && result!=null){
										imgFileId = result.replace(/^\"|\"$/g,'');
										replyContent = imgFileId;
									}else{
										saveFlag="0"; //图片
									}
								}
							})
						}else{
							newbootbox.alert('保存失败！请填写您的意见！')
						}
					}			
					$ajax({
						url:finishUrl,
						data:{infoId:fileId,subId:subId,replyContent:replyContent,saveFlag:saveFlag},
						type: "GET",
						success:function(data){
							if(data.result == "success"){
								newbootbox.alert("审批完成！").done(function(){

									changToNum2(function(){
                                        window.location.reload();
                                        if(fromMsg && fromMsg=="true"){
                                        }else{
                                            window.top.jndbfn();
                                            window.top.grdbfn();
                                            window.top.blfkfn();
                                            //changToNum()
                                        }
									})
								});
							}
						}
					});
			 	}
			});
		});
		
		//转办
		$("#zhuanban").click(function(){
			newbootbox.newdialog({
				id:"zhuanbanDialog",
				width:800,
				height:600,
				header:true,
				title:"转办",
				classed:"cjDialog",
				url:"/app/db/document/jndb/html/zhuanbandx.html?subId="+subId+"&infoId="+fileId+"&fileFrom="+fileFrom+"&fromMsg="+fromMsg
			})
		});
		
		//范例
		$("#fanli").click(function(){
			newbootbox.newdialog({
				id:"fanliDialog",
				width:800,
				height:600,
				header:true,
				title:"反馈范例",
				classed:"cjDialog",
				url:"/app/db/document/view/html/fanli.html?subId="+subId+"&infoId="+fileId+"&fileFrom="+fileFrom+"&fromMsg="+fromMsg
			})
		});
		/*//办结
		$("#banjie").click(function(){
			newbootbox.oconfirm({
			 	title:"提示",
			 	message: "是否确认要进行文件办结操作？",
			 	callback1:function(){
	 				$ajax({
	 					url:banjieUrl,
	 					data:{infoId:fileId,subId:subId},
	 					type: "GET",
	 					success:function(data){
	 						if(data.result == "success"){
	 							if(!fromMsg || fromMsg == false){
									window.top.grdbfn();
								}
	 							window.location.reload();
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
	 					data:{infoId:fileId,subId:subId},
	 					type: "GET",
	 					success:function(data){
	 						if(data.result == "success"){
	 							if(!fromMsg || fromMsg == false){
									window.top.grdbfn();
								}
	 							window.location.reload();
	 						}
	 					}
	 				});
			 	}
			});
		});
		*/
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
		
		//表单
		$("#commentForm").validate({
		    submitHandler: function() {
		    	var replyContent=$("#replyContent").val();
		    	if(replyContent == "" || replyContent== null || replyContent == "undefined"){
					newbootbox.alert("反馈不能为空！");
					return;
				}
		    	$("#editTeamId").val($(".isEditbtn").attr("data"));
		    	var ajax_option = {
					url : saveUrl.url,// 默认是form action
					data:{subId:subId,infoId:fileId,teamId:$("#editTeamId").val(),replyContent:$("#replyContent").val()},
					type:'post',
					success : function(data) {
						if (data.result == "success") {
							if(isSave && isSave!=1){
								newbootbox.alert("保存成功！").done(function(){
									$("#replyContent").val("");
									initblfkList();
									$(".filename").text("");
									$(".filelist").text("附件列表:");
								}); 
							}else{
								$("#replyContent").val("");
								initblfkList();
								$(".filename").text("");
								$(".filelist").text("附件列表:");
							}
						} else {
							newbootbox.alert("提交失败，请稍后重试！"); 
						}
					}
				}
				$('#commentForm').ajaxSubmit(ajax_option); 
		    }
		});
		
		//办理反馈保存
		$("#save").click(function(){
			$("#commentForm").submit();
		});
		
		//催办
		$("#cuiban").click(function(){
			newbootbox.newdialog({
				id:"cuibanDialog",
				width:800,
				height:600,
				header:true,
				title:"催办",
				classed:"cjDialog",
				url:"/app/db/document/view/html/cuibanDialog.html?fileId="+fileId+"&fromMsg="+fromMsg
			})
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
		
		$("#closeviewcont").click(function(){
			$("#viewcont").modal("hide");
		});
		
		
		//切换签批方式
		$("#changewrite").unbind("click"); //手写
        $("#changewrite").click(function(){ //手写
        	$(this).hide();
        	$("#changejianpan").show();
	        $(".css3").attr("data","1");
        	var penNum = $("#penNum").text();
    		if($.trim(penNum) == "0.5"){
    			penNum="signpen_05mm";
    		}else if($.trim(penNum) == "1"){
    			penNum="signpen_1mm";
    		}else if($.trim(penNum) == "2"){
    			penNum="softpen_2mm";
    		}
    		else if($.trim(penNum) == "3"){
    			penNum="softpen_3mm";
    		}else{
    			penNum="signpen_05mm";
    		}	
    		
    		$("#replyContent").hide();
    		$("#write").show();
    		$(".setpen").show();
    		/*try{*/
    			tablet();
            	document.getElementById("signtool").SetPenColor("#000");//设置笔的颜色
            	/*$ajax({
    				url:myHabitUrl,
    				data:{tempIndex:"2",penWidth:penNum},
    				type: "GET",
    				async:false,
    				success:function(data){
    					initmemory();
    				}
    			});*/
    		/*}catch(e){
    			newbootbox.alert("请安装插件!");
    		}*/
        });
        
        $("#changejianpan").unbind("click");
        $("#changejianpan").click(function(){ //键盘
        	$(this).hide();
        	$("#changewrite").show();
    		$("#write").hide();
    		$("#replyContent").show();
    		$("#replyContent").val("");
    		$(".setpen").hide();
    		$(".css3").attr("data","0");
    		/*$ajax({
				url:myHabitUrl,
				data:{tempIndex:"1",penWidth:''},
				type: "GET",
				async:false,
				success:function(data){
					initmemory();
				}
			});*/
        });
        
        //设置笔粗
    	$("body").delegate(".setpen","click",function(){
			$("#setpenchoose").toggle();
		});
		$(document).click(function(e){
			if($(e.target).hasClass("setpen")){
				return;
			}else if($(e.target).parents().hasClass("setpen")){
				return;
			}
			$("#setpenchoose").hide();
		});

        
        $("#setpenchoose li").click(function(){
        	$("#penNum").text($(this).find("font").text());
        	var value = $(this).attr("data");
            document.getElementById("signtool").SetPenWidth(value);
            var penwidth = document.getElementById("signtool").GetPenWidth();
            $ajax({
				url:myHabitUrl,
				data:{penWidth:value,tempIndex:"2"},
				type: "GET",
				success:function(data){
				}
			});
        });
        
        //意见收集
        $("#yjsj").click(function(){
        	newbootbox.newdialog({
        		id:"yijianDialog",
        		width:800,
        		height:600,
        		header:true,
        		title:"意见记录",
        		classed:"cjDialog",
        		style:{"padding":"0px"},
        		url:"/app/db/document/djlr/html/yjjl.html?infoId="+fileId+"&subId="+subId
        	})
        });

        $("#form3").validate({
            submitHandler: function() {
            	$("#dialogzz").show();
            	$("#dialogzz").css("display","table");
        		var ajax_option ={
        			type: "post",
        			url:uploadFileUrl,//默认是form action
        			success:function(data){
        				$("#dialogzz").hide();
        				if(data.result == "success"){
        					newbootbox.alert('上传成功！').done(function(){
        		        		$(".fileinput-filename").text("");
        		    			$("#pdf").val("");
        		    			$("#scanId").val(data.smjId);
        		    			getFile(data.smjId);
        		    			pageModule.takeMenufn()
            				});
        				}else{
        					newbootbox.alert("上传失败！"); 
        				}
        			}
        		}
        		$('#form3').ajaxSubmit(ajax_option);
           }
        });
        //扫描设置
        $("#scanSet").click(function(){
        	$(".smczcont").toggle();
        });
        //扫描新增
        $("#addFj").click(function(){
        	if($("#id").val() == "" || $("#id").val() == null || typeof($("#id").val()) == undefined){
        		newbootbox.alert("请先保存要素信息再开始扫描！"); 
        		return  false;
        	} 
        	$("#pdf").unbind("click");
        	$("#pdf").unbind("change");
        	$("#pdf").click();
        	$("#pdf").change(function(){
        		var fileNameArry = $(this).val().split("\\");
        		var fileName;
        		if(fileNameArry.length==1){
        			fileName=fileNameArry[0];
        		}else{
        			fileName=fileNameArry[fileNameArry.length-1];
        		}
        		$(".fileinput-filename").text(fileName);
        		var id=$("#id").val();
        		$("#idpdf").val(id);
        		$("#form3").submit();
        	});
        })

        //扫描件表单提交
        $("#smjForm").validate({
        	submitHandler : function() {
        		$("#infoId").val($("#id").val());
        		var ajax_option = {
        			type: "post",
        			url : "/app/db/documentinfo/saveSmjPsFile",
        			success : function(data) {
        				if (data.result == "success") {
        					newbootbox.alert("保存成功！").done(function(){
        						getFile(data.scanId);
        		    			pageModule.takeMenufn()
        					});
        				} 
        			}
        		}
        		$('#smjForm').ajaxSubmit(ajax_option);
        	}
        });
        //保存
        $("#save").click(function(){
        	$("#commentForm").submit();
        })
        //保存并新增
        var addFlag =false;//此变量用来标识是不是保存并新增的操作，在submit中区分保存保存回调成功的跳转
        $("#saveAndAdd").click(function(){
        	addFlag=true;
        	$("#commentForm").submit();
        })
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
        //删除附件
        $("#delfj").click(function(){
        	if($("#fjList").find("input[name=fjcheckbox]:checked").length>0){
        		var checkId = [];
        		$("#fjList").find("input[name=fjcheckbox]").each(function(){
        			if($(this).is(":checked")){
        				checkId.push($(this).attr("checkId"));
        			}
        		})
        		$ajax({
        			url:delFileUrl,
        			data:{ids:checkId.toString()},
        			success:function(data){
        				if(data.result == "success" && data.url != ""){
        					newbootbox.alert("删除成功！").done(function(){
        		    			pageModule.takeMenufn()
        					}); 
        				}
        			}
        		})
        	}else{
        		newbootbox.alert("请选中要删除的附件！"); 
        	}
        });


        $("#fasong").click(function(){
        	if($("#replyContent").val() == ''){
        		newbootbox.alert("发送内容不允许为空！");
        	}else{
        		$ajax({
        			url:commitIdeaUrl,
        			data:{infoId:fileId,subId:subId,feedBackIdea:$("#replyContent").val()},
        			type: "GET",
        			success:function(data){
        				if(data.result == "success"){
        					newbootbox.alert("发送成功！").done(function(){
        						initList();
        						//pageModule.takeMenufn();
        					});
        					$("#replyContent").val('');
        				}else{
        					newbootbox.alert("发送失败！");
        				}
        			}
        		});
        	}
        });
        
	}
		
	return{
		//加载页面处理程序
		initControl:function(){
			initList();
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
//			isShowDocStatus();
			showCollectIdeaButton();
			$(".scroller").css("height","100%");
			$(".slimScrollDiv").css("height","100%");
		},
		initblfkList:function(){
			initblfkList();
		},
		takeMenufn:function(){
			takeMenufn();
		},
		reload:function(){
			window.location.reload();
		}
	};
}();


//跳转返回事件
function skip(){
	if(fromMsg && fromMsg=="true"){
		windowClose();
	}else{
		if(fileFrom == "djlr"){//文件来源于登记录入
			window.location.href="/app/db/document/djlr/html/djlr.html?fileFrom=djlr";
		}else if(fileFrom == "grdb"){//文件来源于个人待办
			window.location.href="/app/db/document/grdb/html/grdb.html?fileFrom=grdb";
		}else if(fileFrom=="blfk"){  //文件来源于办理反馈
			window.location="/app/db/document/blfk/html/blfk.html?fileFrom=blfk";
		}else if(fileFrom=="jndb"){  //文件来源于局内待办
			window.location="/app/db/document/jndb/html/jndb.html?fileFrom=jndb";
		}else{ 
			window.location.href="/app/db/document/grdb/html/grdb.html?fileFrom=grdb";
		}
	}
}

//批示详情
/*function showXQ(id){
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
function editfn(id,el,checkStatus){
	var content=$(el).attr("dataContent");
	$(el).parents(".nrt-cont").find(".nrt-cont-file .remove").show();
	if(isCbr==1){
		$("#editTeamId").val(id);
		$("#replyContent").val(content);
		return;
	}else{
		newbootbox.newdialog({
			id:"editDialog",
			width:800,
			height:600,
			header:true,
			title:"编辑",
			classed:"cjDialog",
			url:"/app/db/document/view/html/editDialog.html?fileId="+fileId+"&replyContent="+content+"&subId="+subId+"&teamId="+id+"&fromMsg="+fromMsg+"&checkStatus="+checkStatus
		})
	}
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


//意见收集
function opinionView(teamId,subId,ideaGroupId){ 
	newbootbox.newdialog({
		id:"opinionDialog",
		width:800,
		height:600,
		header:true,
		title:"意见收集",
		classed:"cjDialog",
		style:{"padding":"0px"},
		url:"/app/db/document/view/html/opinion.html?teamId="+teamId+"&subId="+subId+"&ideaGroupId="+ideaGroupId+"&fileFrom="+fileFrom
	})
}
