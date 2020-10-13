var loginUser =  {"url":rootPath +"/documentinfo/getLoginUser","dataType":"text"};//获取当前登录人---录入人
var dicUrl = {"url":rootPath +"/documentdic/getDicByTypet","dataType":"text"}; //返回的下拉框字典值
var saveSzpsUrl = {"url":rootPath +"/documentszps/save","dataType":"text"}; //保存首长批示
var getSzpsListUrl = {"url":rootPath +"/documentszps/queryList","dataType":"text"}; //保存首长批示
var updateUrl={"url":rootPath +"/documentinfo/update","dataType":"json"}; //表单数据保存
var uploadFileUrl = "/app/db/documentinfo/uploadFile";//文件上传
var fileDataUrl = {"url":rootPath +"/documentfile/list","dataType":"text"}; //相关文件-附件list
var delFileUrl = {"url":"/app/db/documentfile/delete","dataType":"text"}; /*相关文件--删除附件*/
var zbjlDataUrl = {"url":"/app/db/documentzbjl/list","dataType":"json"}; //文件转办-转办记录list
var getFormatFileUrl = {"url":"/app/db/documentfile/getFile","dataType":"text"}; /*相关文件-点击获取对应文件*/
var getData ={"url":"/app/db/documentinfo/info","dataType":"json"}; /*编辑返回的数据*/
var getPdfPath = {"url":rootPath +"/fileinfo/getFormaFileUrl","dataType":"text"};
var UserTreeUrl = {"url":"/app/base/user/allTree","dataType":"text"}; //登记人树
var deleteSzcqUrl = {"url":"/app/db/documentszps/delete","dataType":"text"};//删除首长批示
var newSaveSzpsUrl = {"url":rootPath +"/documentszps/newSave","dataType":"text"}; //保存首长批示新版
var saveSzpsUrl2 = {"url":rootPath +"/documentszps/newSaves","dataType":"text"}; //保存首长批示,，内容中存在多个部长信息
var fileFrom=getUrlParam("fileFrom")||""; //文件来源
var fileId=getUrlParam("fileId")||""; //主文件id
var currPage=getUrlParam("currPage")||1; //数据所在页码
var totalPage=getUrlParam("totalPage")||15; //数据每页条数
var documentStatus=(getUrlParam("documentStatus")==0)?0:1;//数据状态0：待处理，1：已处理
var preId = "";//上一页id
var sufId = "";//下一页id
var nowId = "";
var turnSave = false;
var zhuanbanSave = false;
var editFlag = false;   //超清内容是新增还是对原有数据的编辑
$("#id").val(fileId);
var scanFilePath = "";//扫描件路径
var pageModule = function(){
	if(fileFrom=='blfk'){
		$('#newTitle').text('办理反馈')
	}
	//按钮显示
	var initBtn = function(){
		if(fileFrom=="blfk"){
			$('#saveAndAdd').hide();
			$('#return').hide();
			$('#zhuanban').hide();
		}
	}
	//请求各字典数据
	var initdictionary = function(){
		$ajax({
			url:dicUrl,
			data:{dicType:"all"},
			async:false,
			success:function(data){
				if(data.code!=500){//&&data.security_classification!="" && data.security_classification!=null && typeof(data.security_classification)!=undefined){
					initselect("docTypeId",data.document_type);
					initselect("securityId",data.security_classification);
					initselect("urgencyId",data.urgency_degree);
				}
			}
		});
	}
	
	
	var initdatafn = function(){
		$ajax({
			url:getData,
			async:false,
			data:{id:fileId,page:currPage,pagesize:totalPage,documentStatus:documentStatus},
			success:function(data){
				preId = data.preId;//上一页id
				sufId = data.sufId;//下一页id
				if(preId == "noPredId"){
					$("#prevFile").css("pointer-events","none")
					$("#prevFile").css("color","#ccc")
					$("#prevFile .fa").css("color","#ccc")
				}
				if(sufId == "noSufId"){
					$("#nextFile").css("pointer-events","none")
					$("#nextFile").css("color","#ccc")
					$("#nextFile .fa").css("color","#ccc")
				}
				setformdata(data);
				if(data.docTypeId == "1" || data.docTypeId == "2"){
					$("#jobContent").attr("disabled",true);
					$("#banjianNumber").removeAttr("disabled");
					$("#chaoqing").show();
				}else if(data.docTypeId == "4"){
					$("#banjianNumber").attr("disabled",true);
					$("#jobContent").attr("disabled",true);
					$("#period").removeAttr("disabled");
					$("#chaoqing").show();
				}else{
					$("#chaoqing").hide();
					$("#jobContent").removeAttr("disabled");
					$("#banjianNumber").attr("disabled",true);
				}
				if(data.docTypeId == "3" || data.docTypeId == "5" ){
					$(".seteee").removeAttr("disabled");
				}else{
					$(".seteee").attr("disabled",true);
				}
				if(data.docTypeId== "6"){
					$('#gzfgnr').text('落实事项')
				}else{
					$('#gzfgnr').text('工作分工内容')
				}
			}
		});
	}
	
	
	var getdatefn = function(date){
		if(date!=""&&date!=null&&typeof(date)!=undefined){
			return date.substr(0,10);
		}
		return "";
	}
	
	var initUserTree = function(){
		$("#userName").createSelecttree({
			url :UserTreeUrl,
			width : '100%',
			success : function(data, treeobj) {},
			selectnode : function(e, data) {
				$("#userName").val(data.node.text);
				$("#userId").val(data.node.id);
			}
		});
	}
	
	//获取抄清list
	var initCqfn = function(){
		$ajax({
			url:getSzpsListUrl,
			data:{infoId:fileId},
			success:function(data){
				$("#showcq").html("");
				$("#cqcontent").val("");
				$("#editcqId").val("");
				$.each(data,function(i,item){
					$("#showcq").append(
						'<div class="cqline">'+
						'	<div dataId="'+item.id+'" dataUserId="'+item.userId+'" dataName="'+item.leaderComment+'" dataUser="'+item.userName+'" dataDate="'+item.createdTime+'"><span>'+item.userName+'</span><span class="cqrq">'+item.createdTime+'</span><span class="pull-right"><a style="margin-right:10px" class="editcq">编辑</a><a class="delcq">删除</a></span></div>'+
						'	<div>'+item.leaderComment+'</div>'+
						'</div>'
					);

				});
				
				
				$(".editcq").click(function(){//编辑抄清
				    editFlag = true;
					$("#cqcontent").val($(this).parent().parent().attr("dataName"));
					$("#editcqId").val($(this).parent().parent().attr("dataId"));
					
					$("#psszName").val($(this).parent().parent().attr("dataUser"));
					$("#psszId").val($(this).parent().parent().attr("dataUserId"));
					var date = $(this).parent().parent().attr("dataDate");
					$("#cqDate").val(date.substring(0,5));
					$("#editDate").val(date);
				});
				
				$(".delcq").click(function(){
					$ajax({
						url:deleteSzcqUrl,//删除抄清
						data:{id:$(this).parent().parent().attr("dataId")},
						success:function(data){
							if(data.result == "success"){
								newbootbox.alert("删除成功！").done(function(){
									initCqfn();
								});
							}else{
								newbootbox.alert(data.result+"！");
							}
						}
			    	});
				});
			}
		});
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
	
	//相关文件
	var initfilefn = function(){
		$ajax({
			url:fileDataUrl,
			data:{infoId:fileId},
			success:function(data){
				$("#file_all").html("");
				var scanId ="";
				if(data&&data.length>0){
					$.each(data,function(i,item){
						if(i==0){
							scanId = item.id;
                            $ajax({
                                url:getFormatFileUrl,
                                data:{id:scanId},
                                success:function(data){
                                    psLoad(data.formatId,data.downFormatIdUrl);
                                }
                            });
						}
						$("#file_all").append(
							'<li><input type="checkbox" name="fjcheckbox" data="'+item.id+'" /> <a data="'+item.id+'">'+item.fileName+'</a></li>'
			            )
					});
				}
				

				
				//相关文件点击事件
				$("#file_all>li>a").click(function(){
					$(this).addClass("liactive");
					$(this).parent().siblings().find("a").removeClass("liactive");
					var scanId = $(this).attr("data");
					$ajax({
						url:getFormatFileUrl,
						data:{id:scanId},
						success:function(data){
							psLoad(data.formatId,data.downFormatIdUrl);
						}
			    	});
				});
			}
		});	
	}

	var initother = function(){
		$("#docTypeId").attr("disabled",true);
		
		//扫描设置
		$("#scanSet").click(function(){
			$(".smczcont").toggle();
		});
		
		$(".date-picker").datepicker({
		    language:"zh-CN",
		    rtl: Metronic.isRTL(),
		    orientation: "right",
		    autoclose: true
		});
        $("#cqDate").datepicker({
            language:"zh-CN",
            rtl: Metronic.isRTL(),
            orientation: "right",
            startView:2,
            format: "yyyy年",
            minViewMode:2,
            maxViewMode:2,
            autoclose: true
        });
		$(".form_datetime").datetimepicker({
		    language:"zh-CN",
		    autoclose: true,
		    isRTL: Metronic.isRTL(),
		    format: "yyyy-mm-dd   hh:ii",
		    pickerPosition: (Metronic.isRTL() ? "bottom-right" : "bottom-left")
		});
		
		$("#commentForm").validate({
			ignore:'',
		    submitHandler: function() {
		    	$("#docTypeName").val($("#docTypeId option:checked").text());
		    	$("#securityClassification").val($("#securityId option:checked").text());
		    	$("#urgencyDegree").val($("#urgencyId option:checked").text());
			    var elementarry = ["docTypeId","docTypeName","docTitle","securityId","securityClassification",
			    	"urgencyId","urgencyDegree","docCode","banjianNumber","userId","userName","applyTime","printDate","jobContent","remark","period","workableMatter"];
				var paramdata = getformdata(elementarry);
				paramdata.id = fileId;
				//newbootbox.alert('正在保存，请稍候...',false);
				$.ajax({
					url:updateUrl.url,
					data:paramdata,
					type:"post",
					async:false,
					success:function(data){
						var psszName = $("#psszName").val();
						var psszId = $("#psszId").val();
						var leaderComment=$.trim($("#cqcontent").val());
						var createdTime=$("#cqDate").val();
						var infoId = $("#id").val();
                        var id = $("#editcqId").val();
                        var errArr = new Array();
                       /* if(leaderComment == "" || leaderComment == null){
                            newbootbox.alert('请输入抄清内容！');
                            return;
                        }*/
                       if (leaderComment != "" && leaderComment != null) {
                        if (leaderComment.indexOf("\n")) {
                            var reg = /\n|\r\n/g;
                            var arr = leaderComment.split(reg);
                            $(arr).each(function(i){
                                arr[i] = arr[i].replace(/^\s+|\s+$/g,"");
                                if (!editFlag) {
                                    var index = arr[i].indexOf(":")!=-1?arr[i].indexOf(":")+1:arr[i].indexOf("：")+1;
                                    var temp = $.trim(arr[i].substring(index,arr[i].length));
                                    var temp1 = $.trim(arr[i].charAt(arr[i].length-1));
                                    //批示内容前后同时存在引号，则不追加引号
                                    if ((temp.substring(0,1) == '"'||temp.substring(0,1) == '”'||temp.substring(0,1) == '“')&&(temp1 == '"'||temp1 == '”'||temp1.substring(0,1) == '“')) {
                                            console.log("批示前后都有双引号");
                                    } else {
                                        if (!(temp.substring(0,1) == '"'||temp.substring(0,1) == '”'||temp.substring(0,1) == '“')) {
                                            temp = '"'+temp;
                                        }
                                        if (!(temp1 == '"'||temp1 == '”'||temp1.substring(0,1) == '“')) {
                                             temp = temp+'"';
                                        }
                                        arr[i] = arr[i].substring(0,index) +temp;
                                    }
                                }
                            })
                            //校验输入格式  xxx部长X月X日批示：”xxx内容”  注意冒号中英文
                             if (!editFlag) {
                                 $(arr).each(function(i){
                                    var obj = new Object();
                                    var temp = new Array();
                                    var reg1 = new RegExp("^[\u4E00-\u9FA5]+[0-9]{1,2}月[0-9]{1,2}日批示$");
                                    if (arr[i].indexOf(":") != -1){
                                        temp = arr[i].split(":");
                                    }
                                    if (arr[i].indexOf("：") != -1){
                                        temp = arr[i].split("：");
                                    }
                                    if (temp.length <=1 ){
                                       obj.index = i+1;
                                       obj.msg = "批示后面无冒号";
                                      errArr.push(obj);
                                    } else {
                                          if (!reg1.test(temp[0])) {
                                             obj.index = i+1;
                                             obj.msg = "日期格式xx月xx日";
                                             errArr.push(obj);
                                          }
                                    }
                                })
                            }
                            leaderComment = arr.join("&");
                            console.log("leaderComment"+leaderComment);
                        }

                           if (errArr.length > 0) {
                                  var str = "";
                                 $(errArr).each(function(i){
                                    str += "<p>第<span style='color:red;'>"+errArr[i].index+"</span>条内容，不合法。"+"原因："+errArr[i].msg+"</p>"
                                 })
                                newbootbox.alert1(str);
                                return ;
                            }
                        }
                         var url = saveSzpsUrl;
                        if (!editFlag) {
                            url = saveSzpsUrl2;
                        }
                        if (editFlag) {
                            createdTime = $("#editDate").val();
                        }
						if(leaderComment != "" && leaderComment != null){
                            $ajax({
                                url:url,
                                data:{infoId:$("#id").val(),userName:psszName,userId:psszId,leaderComment:leaderComment,createdTime:createdTime,id:$("#editcqId").val()},
                                async:false,
                                success:function(data){
                                    if(data.result == "success"){
                                        if(!turnSave){
                                            newbootbox.alert("保存成功！").done(function(){
                                                //清空之前选中和复制的参数
                                                $("#cqcontent").val("");
                                                $("#psszName").val("");
                                                $("#psszId").val("");
                                                 editFlag = false;
                                                initCqfn();
                                            });
                                        }

                                    }
                                }
                            });
						}
						if(!turnSave){
							window.top.$(".newclose").click();
							if(addFlag){
								window.location.href="/app/db/document/djlr/html/add.html";
							}else if(returnSave){
						    	if(fileFrom=='blfk'){
							    	window.location.href = "/app/db/document/blfk/html/blfk.html?fileFrom=blfk";
						    	}else{
							    	window.location.href = "/app/db/document/djlr/html/djlr.html?fileFrom=djlr";
						    	}
							}else{
								setTimeout(function(){
								    if(!zhuanbanSave){

                                        newbootbox.alert("保存成功！").done(function(){
                                            //清空之前选中和复制的参数
                                            $("#cqcontent").val("");
                                            $("#psszName").val("");
                                            $("#psszId").val("");
                                        });
								    }
								},200);
							}
						}else{
							window.location.href ='/app/db/document/djlr/html/edit.html?fileId='+nowId+'&currPage='+currPage+'&totalPage='+totalPage+'&documentStatus='+documentStatus
						}
						
					}
				})
		    },
		    errorPlacement: function(error, element) {
	    	 	if($(element).parent().hasClass("selecttree")){
	    	 		error.appendTo(element.parent().parent().parent()); 
	    	 	}else{
	    	 		error.appendTo(element.parent());  
	    	 	}
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
		//取消并退出
		var returnSave =false;//此变量用来标识是不是取消并退出按钮中保存的操作，在submit中区分保存回调成功的跳转
		$("#return").click(function(){
			newbootbox.confirm100({
			    title: "提示",
			    message: "是否保存当前更改？不保存将丢失编辑数据",
			    callback1:function(){
			    	returnSave=true;
			    	$("#commentForm").submit();
			    },
			    callback2:function(){
			    	if(fileFrom=='blfk'){
				    	window.location.href = "/app/db/document/blfk/html/blfk.html?fileFrom=blfk";
			    	}else{
				    	window.location.href = "/app/db/document/djlr/html/djlr.html?fileFrom=djlr";
			    	}
			    }
			});
		})
		//返回按钮
		$("#goback").click(function(){
			//$("#commentForm").submit();
			/*newbootbox.confirm100({
			    title: "提示",
			    message: "是否保存当前更改？不保存将丢失编辑数据",
			    callback1:function(){
			    	returnSave=true;
			    	$("#commentForm").submit();
			    },
			    callback2:function(){
			    	window.location.href = "/app/db/document/djlr/html/djlr.html?fileFrom=djlr";
			    }
			});*/
			returnSave=true;
	    	$("#commentForm").submit();
		});
		//转办
		$("#zhuanban").click(function(){
		    zhuanbanSave = true;
		    $("#commentForm").submit();
		    setTimeout(function(){
                newbootbox.newdialog({
                    id:"zhuanbanDialog",
                    width:800,
                    height:600,
                    header:true,
                    title:"转办",
                    classed:"cjDialog",
                    url:"/app/db/document/blfk/html/zhuanbanDialog.html?fileIds="+fileId+"&fileFrom="+fileFrom
                })
		    },1000)
		});
		
		/*//扫描续传功能
		$("#scanKeepTransfer").click(function() {   	
	    	$.ajax({
	    		type: "post",
				url:"/app/dzbms/fileinfo/checkScanpdf",//默认是form action
				success:function(data){
					if(data.result != "success"){
						newbootbox.alert(data.result);
					}
				}
	    	});
	    });*/
		
		
		//选择首长
		$("#psszName").click(function(){
			newbootbox.newdialog({
				id:"chooseszDialog",
				width:800,
				height:600,
				header:true,
				title:"选择首长",
				classed:"cjDialog",
				//url:"/app/db/document/djlr/html/chooseszDialog.html",
				url:"/app/db/document/djlr/html/chooseszDialogNew.html"
			})
		});
		
		//增加批示   选择多人
		$("#addcq").click(function(){
			var psszName = $("#psszName").val();
			var psszId = $("#psszId").val();
			var leaderComment=$.trim($("#cqcontent").val());
			var createdTime=$("#cqDate").val();
			var infoId = $("#id").val();
			var id = $("#editcqId").val();
			 var errArr = new Array();
			if($.trim(leaderComment) == "" || $.trim(leaderComment) == null){
				newbootbox.alert('请输入抄清内容！');
				return;
			}
			if (leaderComment.indexOf("\n")) {
			    var reg = /\n|\r\n/g;
                var arr = leaderComment.split(reg);
                $(arr).each(function(i){
                       arr[i] = arr[i].replace(/^\s+|\s+$/g,"");
                         if (!editFlag) {
                            var index = arr[i].indexOf(":")!=-1?arr[i].indexOf(":")+1:arr[i].indexOf("：")+1;
                           var temp = $.trim(arr[i].substring(index,arr[i].length));
                           var temp1 = $.trim(arr[i].charAt(arr[i].length-1));
                            if ((temp.substring(0,1) == '"'||temp.substring(0,1) == '”'||temp.substring(0,1) == '“')&&(temp1 == '"'||temp1 == '”'||temp1.substring(0,1) == '“')) {
                                console.log("批示前后都有双引号");
                           } else {
                               if (!(temp.substring(0,1) == '"'||temp.substring(0,1) == '”'||temp.substring(0,1) == '“')) {
                                   temp = '"'+temp;
                               }
                               if (!(temp1 == '"'||temp1 == '”'||temp1.substring(0,1) == '“')) {
                                    temp = temp+'"';
                               }
                               arr[i] = arr[i].substring(0,index) +temp;
                           }
                       }
                   })
                   //校验输入格式  xxx部长X月X日批示：”xxx内容”  注意冒号中英文
                     if (!editFlag) {
                        $(arr).each(function(i){
                           var obj = new Object();
                           var temp = new Array();
                           var reg1 = new RegExp("^[\u4E00-\u9FA5]+[0-9]{1,2}月[0-9]{1,2}日批示$");
                           if (arr[i].indexOf(":") != -1){
                               temp = arr[i].split(":");
                           }
                           if (arr[i].indexOf("：") != -1){
                               temp = arr[i].split("：");
                           }
                           if (temp.length <=1 ){
                                obj.index = i+1;
                                obj.msg = "批示后面无冒号";
                               errArr.push(obj);
                           } else {
                               if (!reg1.test(temp[0])) {
                                  obj.index = i+1;
                                  obj.msg = "日期格式xx月xx日";
                                  errArr.push(obj);
                               }
                           }
                     })
                   }
            leaderComment = arr.join("&");
			console.log("leaderComment"+leaderComment);
			}
			if (errArr.length > 0) {
			      var str = "";
			     $(errArr).each(function(i){
			        str += "<p>第<span style='color:red;'>"+errArr[i].index+"</span>条内容，不合法。"+"原因："+errArr[i].msg+"</p>"
			     })
                newbootbox.alert1(str);
                return ;
            }
			 var url = saveSzpsUrl;
            if (!editFlag) {
                url = saveSzpsUrl2;
            }
            if (editFlag) {
                createdTime = $("#editDate").val();
            }
			$ajax({
				url:url,
				data:{infoId:$("#id").val(),userName:psszName,userId:psszId,leaderComment:leaderComment,createdTime:createdTime,id:$("#editcqId").val()},
				success:function(data){
					if(data.result == "success"){
						newbootbox.alert("保存成功！").done(function(){
						    editFlag = false;
							initCqfn();
						});
					}
				}
			});
			//清空之前选中和复制的参数
			$("#cqcontent").val("");
            $("#psszName").val("");
            $("#psszId").val("");
		});
		
		/*//转办
		$("#zhuanban").click(function(){
			newbootbox.newdialog({
				id:"zhuanbanDialog",
				width:800,
				height:600,
				header:true,
				title:"转办",
				classed:"cjDialog",
				url:"/app/db/document/blfk/html/zhuanbanDialog.html?fileIds="+fileId
			})
		});*/
		
		
		//删除附件
		$("#delfj").click(function(){
			if($("#file_all").find("input[name=fjcheckbox]:checked").length>0){
				var checkId = [];
				$("#file_all").find("input[name=fjcheckbox]").each(function(){
					if($(this).is(":checked")){
						checkId.push($(this).attr("data"));
					}
				})
				$ajax({
					url:delFileUrl,
					data:{ids:checkId.toString()},
					success:function(data){
						if(data.result == "success" && data.url != ""){
							newbootbox.alert("删除成功！").done(function(){
								initfilefn();
							}); 
						}
					}
				})
			}else{
				newbootbox.alert("请选中要删除的附件！"); 
			}
		});
	}
	
	var initPdf = function(){
		$("#uploadPdf").click(function(){
			$("#FireFoxOFDDIV").hide();
		})
		
		$("#showupload").on("hidden.bs.modal",function(e){
			$("#FireFoxOFDDIV").show();
		});

		$(".close.fileinput-exists").live("click",function(){
			$(this).parent().parent().parent().remove();
		})

		$("#addfile2").click(function(){
			$(this).parent().parent().before(
	                '<div class="form-group has-success new_success">'+
	                '    <div class="col-xs-12">'+
	                '        <div class="fileinput fileinput-new" data-provides="fileinput">'+
	                '            <span class="btn default btn-file" style="padding-left:50px;padding-right:50px;">'+
	                '            <span class="fileinput-new">'+
	                '            上传 </span>'+
	                '            <span class="fileinput-exists">'+
	                '            修改 </span>'+
	                '            <input type="file" name="...">'+
	                '            </span>'+
	                '            <span class="fileinput-filename" style="padding-left:20px;">'+
	                '            </span>'+
	                '            &nbsp; <a href="javascript:;" class="close fileinput-exists" data-dismiss="fileinput">'+
	                '            </a>'+
	                '        </div>'+
	                '    </div>'+
	                '</div>'
			)
		})
		
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
			        			psLoad('', data.smjFilePath);
			        			initfilefn();
		    				});
						}else{
							newbootbox.alert("上传失败！"); 
						}
					}
				}
				$('#form3').ajaxSubmit(ajax_option);
		   }
		});
		
		$("#addFj").click(function(){
			if($("#id").val() == "" || $("#id").val() == null || typeof($("#id").val()) == undefined){
				newbootbox.alertInfo("请先保存要素信息再开始扫描！"); 
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
								psLoad(data.scanId, data.scanFilePath);
							});
						} 
					}
				}
				$('#smjForm').ajaxSubmit(ajax_option);
			}
		});
	}
	
	return{
		//加载页面处理程序
		initControl:function(){
			initBtn();
			initdictionary();
			initdatafn();
			initCqfn();
			initzbjlfn();
			initfilefn();
			initUserTree();
			initother();
			initPdf();
		},
		getUserData:function(message1,message2){
		    $("#usersDiv").html("");
		    $("#psszName").val(message1);
            $("#psszId").val(message2);
		    if (message1.length > 0) {
                var arrName = message1.split(",");
                 var arrId = message2.split(",");
                var str = "";
                for (var i =0;i<arrId.length;i++) {
                   str += '<div><label data_id="'+arrId[i]+'" data_name="'+arrName[i]+'">'+arrName[i]+'</label><textarea class="form-control cqcontent" placeholder="输入抄清内容..." maxlength="500"></textarea></div>'
                }
                $("#usersDiv").html(str);
		    }
			//$("#psszName").val(message1);
			//$("#psszId").val(message2);
		}
	}
	
}();

var psLoad = function(psFileId, psPath){
	if(psPath == "" || psPath == null || psPath=="undefined"){
		$("#embedwrap").hide();
	}else{
		$("#embedwrap").show();
	}
	var embedWidth = "100%";
	var embedHeight = "100%";
	var id=$("#scanId").val();
	if(psFileId != null && psFileId != ""){
		$("#scanId").val(psFileId);
	}
	if(psPath != null && psPath != ""){
		scanFilePath = psPath;
		openOFDFile(scanFilePath, "FireFoxOFDDIV",embedWidth,embedHeight);
	}else{
		//初始化版式文件
		$ajax({
			type:"post",
			async:false,
			url:getPdfPath,//文件请求地址
			data:{id:id},
			success:function(data){
				if(data.result == "success" && data.url != ""){
					scanFilePath = data.url;
				}
			}
		})
		// 判断隐藏域中是否有值，如果有则从隐藏域取，或者初始化打开
		if ($("#cloneDiv").html() != null && $("#cloneDiv").html() != "") {
			fromCloneDivOpenFile('FireFoxOFDDIV', 'cloneDiv', 'showPsAreaDiv',embedHeight);
		} else {
			if(scanFilePath != null && scanFilePath != ""){
				openOFDFile(scanFilePath, "FireFoxOFDDIV",embedWidth,embedHeight);
			}
		}
	}
}
function pagefn(obj){
	turnSave = true
	var turn = $(obj).attr("data");
	nowId = "";
	if(turn == "prev"){
		nowId = preId
	}
	if(turn == "next"){
		nowId = sufId
	}
	$("#commentForm").submit();
//	window.location.href ='/app/db/document/djlr/html/edit.html?fileId='+nowId+'&currPage='+currPage+'&totalPage='+totalPage+'&documentStatus='+documentStatus
}
