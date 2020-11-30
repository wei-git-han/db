var loginUser =  {"url":rootPath +"/documentinfo/getLoginUser","dataType":"text"};//获取当前登录人---录入人
var dicUrl = {"url":rootPath +"/documentdic/getDicByTypet","dataType":"text"}; //返回的下拉框字典值
var saveSzpsUrl = {"url":rootPath +"/documentszps/save","dataType":"text"}; //保存首长批示
var saveSzpsUrl2 = {"url":rootPath +"/documentszps/newSaves","dataType":"text"}; //保存首长批示,，内容中存在多个部长信息
var getSzpsListUrl = {"url":rootPath +"/documentszps/queryList","dataType":"text"}; //获取首长批示
var updateUrl={"url":rootPath +"/documentinfo/update","dataType":"json"}; //表单数据保存
var uploadFileUrl = "/app/db/documentinfo/uploadFile";//文件上传
var fileDataUrl = {"url":rootPath +"/documentfile/list","dataType":"text"}; //相关文件-附件list
var delFileUrl = {"url":"/app/db/documentfile/delete","dataType":"text"}; /*相关文件--删除附件*/
var getFormatFileUrl = {"url":"/app/db/documentfile/getFile","dataType":"text"}; /*相关文件-点击获取对应文件*/
var getPdfPath = {"url":rootPath +"/fileinfo/getFormaFileUrl","dataType":"text"};
var UserTreeUrl = {"url":"/app/base/user/allTree","dataType":"text"}; //登记人树
var deleteSzcqUrl = {"url":"/app/db/documentszps/delete","dataType":"text"};//删除首长批示
var getlastPeriodUrl ={"url":"/app/db/documentinfo/lastInfo","dataType":"json"}; /*查询上一条期数*/
var tablegrid = rootPath +"/document/personList";//审批公文列表
var saveFileUrl =  {"url":rootPath +"/document/saveFile","dataType":"text"};//审批公文列表保存提交
var fileFrom=getUrlParam("fileFrom")||""; //文件来源
var scanFilePath = "";//扫描件路径
var addcqFlag="";//此变量用来标识是不是自动保存的操作，在submit中区分保存回调
var loginUserId = ""//登录人Id
var editFlag = false;   //超清内容是新增还是对原有数据的编辑
var spgwList = [];
var pageModule = function(){
	 /*带入录入人*/
	var makeLoginUser = function(){
		$ajax({
			url:loginUser,  
			success:function(data){
				$("#userId").val(data.userId);
				$("#userName").val(data.userName);
				//登记录入同账号类别记忆功能
				loginUserId = data.userId
				if(getCookie('docTypeArr')){
					var docTypeArr = JSON.parse(getCookie('docTypeArr'))
					docTypeArr.map(function(item){
						if(item.userId == data.userId){
							$("#docTypeId").val(item.docTypeId);
							$("#docTypeName").val(item.docTypeName)
							$("#docTypeId").change();
						}
					});
				}
			}
		})
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
	
	//请求各字典数据
	var initdictionary = function(){
		$ajax({
			url:dicUrl,
			data:{dicType:"all"},
			success:function(data){
				if(data.code!=500){//&&data.security_classification!="" && data.security_classification!=null && typeof(data.security_classification)!=undefined){
					initselect1("docTypeId",data.document_type);
					initselect("securityId",data.security_classification);
					initselect("urgencyId",data.urgency_degree);
				}
			}
		});
	}
	
	//获取抄清list
	var initCqfn = function(){
		$ajax({
			url:getSzpsListUrl,
			data:{infoId:$("#id").val()},
			success:function(data){
				$("#showcq").html("");
				$("#cqcontent").val("");
				$("#editcqId").val("");
				$.each(data,function(i,item){
					$("#showcq").append(
						'<div class="cqline">'+
						'	<div dataId="'+item.id+'" dataName="'+item.leaderComment+'"  dataUserId="'+item.userId+'"  dataUser="'+item.userName+'" dataDate="'+item.createdTime+'"><span>'+item.userName+'</span><span class="cqrq">'+item.createdTime+'</span><span class="pull-right"><a style="margin-right:10px" class="editcq">编辑</a><a class="delcq">删除</a></span></div>'+
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
					//$("#cqDate").val($(this).parent().parent().attr("dataDate"));
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
	
	//相关文件
	var initfilefn = function(){
		$ajax({
			url:fileDataUrl,
			data:{infoId:$("#id").val()},
			success:function(data){
				$("#file_all").html("");
				if(data&&data.length>0){
					$.each(data,function(i,item){
						$("#file_all").append(
							'<li><input type="checkbox" name="fjcheckbox" data="'+item.id+'" /> <a data="'+item.id+'">'+item.fileName+'</a></li>'
			            )
					});
				}else{
					//psLoad('','');
					$("#embedwrap").hide();
				}
				$("#file_all>li>a").click(function(){
					$(this).addClass("liactive");
					$(this).parent().siblings().find("a").removeClass("liactive");
					$("#embedwrap").show();
					var scanId = $(this).attr("data");
					$ajax({
						url:getFormatFileUrl,
						data:{id:scanId},
						success:function(data){
							psLoad(data.formatId,data.downFormatIdUrl);
						}
			    	});
				})
			}
		});	
	}

	var initother = function(){
		//文件类别change事件
		$("#docTypeId").change(function(){
			if($(this).val() == "1" || $(this).val() == "2" ){
				$("#jobContent").attr("disabled",true);
				$("#banjianNumber").removeAttr("disabled");
				$("#chaoqing").show();
				$("#period").attr("disabled",true);
				$("#period").val('');
			}else if($(this).val() == "4"){
				$ajax({
					url:getlastPeriodUrl,
					success:function(data){
						if(data.code!=500){//&&data.document_period!="" && data.document_period!=null && typeof(data.document_period)!=undefined){
							$("#period").val(data.period);
						}
					}
				});
				$("#banjianNumber").attr("disabled",true);
				$("#jobContent").attr("disabled",true);
				$("#period").removeAttr("disabled");
				$("#chaoqing").show();
			}else{
				$("#period").attr("disabled",true);
				$("#period").val('');
				$("#chaoqing").hide();
				$("#jobContent").removeAttr("disabled");
				$("#banjianNumber").attr("disabled",true);
			}
			if($(this).val() == "6"){
				$('#gzfgnr').text('落实事项')
			}else{
				$('#gzfgnr').text('工作分工内容')
			}
			if($(this).val() == "3" || $(this).val() == "5" ){
				$(".seteee").removeAttr("disabled");

				$(".setdis").attr("disabled",true);
				$(".seteee").val("");
				$(".setdis").val("");
			}else{
				$(".seteee").attr("disabled",true);
				$(".setdis").removeAttr("disabled");
			}
			//登记录入同账号类别记忆功能
			var docTypeArr = []
			if(getCookie('docTypeArr')){
				docTypeArr = getCookie('docTypeArr');
				docTypeArr = JSON.parse(docTypeArr).filter(function(item){
					item.userId != loginUserId
				});
			}
			docTypeArr.push({
				userId: loginUserId,docTypeId: $(this).val(),docTypeName: $("#docTypeId option:checked").text()
			})
			document.cookie = 'docTypeArr='+JSON.stringify(docTypeArr);
		});
		
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
		

		var newdate = (new Date()).format("yyyy-MM-dd");
		$("#applyTime").val(newdate);
		
		$("#commentForm").validate({
			ignore:'',
		    submitHandler: function() {
		    	$("#docTypeName").val($("#docTypeId option:checked").text());
		    	$("#securityClassification").val($("#securityId option:checked").text());
		    	$("#urgencyDegree").val($("#urgencyId option:checked").text());
			    var elementarry = ["docTypeId","docTypeName","docTitle","securityId","securityClassification",
			    	"urgencyId","urgencyDegree","docCode","banjianNumber","userId","userName","applyTime","printDate","jobContent","remark","period"];
				var paramdata = getformdata(elementarry);
				paramdata.id = $("#id").val();
                var docTypeId=$("#docTypeId").val();
                if(docTypeId=='0'){
                    newbootbox.alert('请选择类别！');
                    return;
                }
				//newbootbox.alert('正在保存，请稍候...',false);
				$.ajax({
					url:updateUrl.url,
					data:paramdata,
					type:"post",
					async:false,
					success:function(data){
						$("#id").val(data.id);
						var psszName = $("#psszName").val();
						var psszId = $("#psszId").val();
						var leaderComment=$("#cqcontent").val();
						var createdTime=$("#cqDate").val();
						var errArr = new Array();
						if($.trim(leaderComment) != "" && $.trim(leaderComment) != null){
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
                        }
                         var url = saveSzpsUrl;
                        if (!editFlag) {
                            url = saveSzpsUrl2;
                        }
                        if (editFlag) {
                            createdTime = $("#editDate").val();
                        }
						if($.trim(leaderComment) != "" && $.trim(leaderComment) != null){

                            $.ajax({
                                url:url.url,
                                data:{infoId:$("#id").val(),userName:psszName,userId:psszId,leaderComment:leaderComment,createdTime:createdTime,id:$("#editcqId").val()},
                                async:false,
                                success:function(data){
                                    if(data.result == "success"){
                                        newbootbox.alert("保存成功！").done(function(){
                                            editFlag = false;
                                            initCqfn();
                                        });
                                    }
                                }
                            });

						}

						window.top.$(".newclose").click();
						if(addFlag){
							window.location.href="/app/db/document/djlr/html/add.html";
						}else if(returnSave){
							window.location.href = "/app/db/document/djlr/html/djlr.html?fileFrom=djlr";
						}else{
							if(addcqFlag != "0"){
								setTimeout(function(){
									newbootbox.alert("保存成功！").done(function(){
									});
								},200);
							}
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
			    	window.location.href = "/app/db/document/djlr/html/djlr.html?fileFrom=djlr";
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
		
		
		//扫描续传功能
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
	    });
		
		
		//选择首长
		$("#psszName").click(function(){
			newbootbox.newdialog({
				id:"chooseszDialog",
				width:800,
				height:600,
				header:true,
				title:"选择首长",
				classed:"cjDialog",
				url:"/app/db/document/djlr/html/chooseszDialog.html",
			})
		});
		
		//增加批示
		$("#addcq").click(function(){
			addcqFlag="0";
			var psszName = $("#psszName").val();
			var psszId = $("#psszId").val();
			if($("#id").val() == "" || $("#id").val() == null || typeof($("#id").val()) == undefined){
				if($("#docTitle").val() == "" || $("#docTitle").val() == null || typeof($("#docTitle").val()) == undefined){
					newbootbox.alertInfo("请先填写文件标题！"); 
					return;
				} 
				$("#commentForm").submit();
				return;
			} 
			var leaderComment=$("#cqcontent").val();
			var createdTime=$("#cqDate").val();
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
				async:false,
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
			//$("#cqDate").val("");
			$("#cqcontent").val("");
			$("#psszName").val("");
			$("#psszId").val("");
		});
		
		//转办
		$("#zhuanban").click(function(){
			addcqFlag="0";
			if($("#id").val() == "" || $("#id").val() == null || typeof($("#id").val()) == undefined){
				if($("#docTitle").val() == "" || $("#docTitle").val() == null || typeof($("#docTitle").val()) == undefined){
					newbootbox.alertInfo("请先填写文件标题！"); 
					return;
				} else if($("#docTypeId").val() == "0"){
				    newbootbox.alertInfo("请选择类别！");
                    return;
				}
				$("#commentForm").submit();
			} 
			var fileId = $("#id").val();
			newbootbox.newdialog({
				id:"zhuanbanDialog",
				width:800,
				height:600,
				header:true,
				title:"转办",
				classed:"cjDialog",
				url:"/app/db/document/blfk/html/zhuanbanDialog.html?fileIds="+fileId+"&zhuanbanAdd=1"
			})
		});
		
		
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
		 // 审批公文
        $("#spgw").click(function() {
            if($("#id").val() == "" || $("#id").val() == null || typeof($("#id").val()) == undefined){
                newbootbox.alertInfo("请先保存要素信息再选择审批公文文件！");
                return  false;
            }
            $("#showSpgwDialog").modal("show")
        })
		// 保存审批公文
		$("#spgwSave").click(function(){
            if(spgwList.length<1){
                newbootbox.alert('请选择文件后再点击确定按钮！')
                return
            }
             $("#spgwWait").show();
             $("#spgwSave").addClass("disabledBtn");
            $ajax({
                 url:saveFileUrl,
                 type: "POST",
                 data:{
                      fileIds:spgwList.join(","),
                      idpdf: $("#id").val()
                  },
                 success:function(data){
                     $("#spgwWait").hide();
                      if (data.result == 'success') {
                        // 刷新文件列表
                        psLoad('', data.url);
                        initfilefn();
                      }
                     $("#spgwSave").removeClass("disabledBtn");
                 },error:function(data) {
                    $("#spgwWait").hide();
                 }
            });
        })
		// 关闭审批公文窗口
		$("#spgwClose").click(function(){
            $("#showSpgwDialog").modal('hide')
		})
		$("#showSpgwDialog").on("hidden.bs.modal",function(e){
           $("#searchVal").val("")
           if ($("#spgwSave").hasClass("disabledBtn")) {
               $("#spgwSave").removeClass("disabledBtn");
           }
           if ($("#spgwWait").css("display") == 'block') {
               $("#spgwWait").hide();
           }
           spgwList = []
           initSpgwList();
        });
		$(".search").click(function(){
           $('#gridcont').treegrid('reload',{search:$("#searchVal").val()})
        });
	}
	// 审批公文列表查询
	var initSpgwList = function(){
        $('#gridcont').treegrid({
            width : '860px',
            height : '600px',
            rownumbers : false,
            animate : true,
            autoRowHeight : false,
            pagination : true,
            pageSize : 10,
            pageList : [ 5, 10, 20, 30 ],
            fitColumns : true,
            url : tablegrid,
            queryParams:{
                search:$("#searchVal").val()
             },
            async : true,
            idField : 'id',
            loadingMessage : '请等待……',
            loadMsg : '请稍等……',
            columns:[[
                {title:'文件名',field:'title',width:'100%',align:'left',formatter:function(value,rowdata){
                    var html = '';
                    var str = '';
                    var content = `<span class="" title="${rowdata.title}">${rowdata.title}</span>`;
                    if(rowdata.cpj.length>0 || rowdata.fj.length>0 || rowdata.bwly&&rowdata.bwly.length>0 || rowdata.countersignatureFile&&rowdata.countersignatureFile.length>0 || rowdata.szsy&&rowdata.szsy.length>0) {
                        html = '<i class="fa fa-chevron-right faSty" onclick="openCpj(this)"></i>';
                    }

                    str+=fileHtml(rowdata)

                    return html+content+str;
                }}
            ]]
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
				    			$("#embedwrap").show();
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
								$("#embedwrap").show();
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
			initdictionary();
			initfilefn();
			initCqfn();
			initUserTree();
			makeLoginUser();
			initother();
			initPdf();
			initSpgwList();
		},
		getUserData:function(message1,message2){
			$("#psszName").val(message1);
			$("#psszId").val(message2);
		}
	}
	
}();

var psLoad = function(psFileId, psPath){
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

var getCookie = function(name){
	var arr,reg = new RegExp("(^|)"+name+"=([^;]*)(;|$)");
	if(arr = document.cookie.match(reg)){
		return unescape(arr[2]);
	}else{
		return null
	}
}
function openCpj(event){
    var $ul =$($(event).parent().find("ul"));
        if ($ul.hasClass("close")) {
            $(event).addClass("openUl")
            $ul.removeClass("close");
        } else {
            $(event).removeClass("openUl")
             $ul.addClass("close");
        }
}
function openCpj1(event){
    var $sub =$($(event).parent().next());
    if ($sub.hasClass("close")) {
        $(event).addClass("openUl")
        $sub.removeClass("close");
    } else {
        $(event).removeClass("openUl")
         $sub.addClass("close");
    }
}
function checkInput(event){
    var inputValue =  $(event).val()
    if ($(event).prop("checked")) {
        if (inputValue == "") {
            $(event).parent().parent().nextAll().find("input").prop("checked",true)
            $($(event).parent().parent().nextAll().find("input")).each(function(index,obj){
                 spgwList.push(obj.defaultValue);
            })
        } else {
            $(event).parent().parent().addClass("selectLi")
            spgwList.push(inputValue);
        }
    } else {
        if (inputValue == "") {
            $(event).parent().parent().nextAll().find("input").prop("checked",false)
            $($(event).parent().parent().nextAll().find("input")).each(function(index,obj){
                 spgwList.remove(obj.defaultValue);
            })
        } else {
            $(event).parent().parent().removeClass("selectLi")
            spgwList.remove(inputValue)
        }
    }
    console.log(spgwList);
}
function fileHtml(rowdata){
    var str = "";
    var cpj = '',fj="",banwenlaiyuan='',countersignatureFile='',szsy='';
    if (rowdata.cpj&&rowdata.cpj.length>0) {
        $(rowdata.cpj).each(function(i,obj){
            cpj += `<li title="${obj.name}"><label><input type="checkbox" value="${obj.fileId}" class="select" style="margin:0 10px;" onclick="checkInput(this)" />${obj.name}</label></li>`
        })
        cpj = '<li style="height:auto;" class="close"><ul class="subUl">'+cpj+'</ul></li>';
        str += '<ul class="close ulSty"><li class="liSty"><i class="fa fa-chevron-right faSty faSty1" onclick="openCpj1(this)"></i><label><input type="checkbox" value="" style="margin:0 10px;" onclick="checkInput(this)"/>主文件</label></li>'+cpj+'</ul>';
    }
     if (rowdata.fj&&rowdata.fj.length>0) {
        $(rowdata.fj).each(function(i,obj){
            fj += `<li title="${obj.name}"><label><input type="checkbox" value="${obj.fileId}" class="select" style="margin:0 10px;" onclick="checkInput(this)" />${obj.name}</label></li>`
        })
         fj = '<li style="height:auto;" class="close"><ul class="subUl">'+fj+'</ul></li>';
        str += '<ul class="close ulSty"><li class="liSty"><i class="fa fa-chevron-right faSty faSty1" onclick="openCpj1(this)"></i><label><input type="checkbox" value="" style="margin:0 10px;" onclick="checkInput(this)" />附件</label></li>'+fj+'</ul>';

    }
     if (rowdata.bwly&&rowdata.bwly.length>0) {
         $(rowdata.bwly).each(function(i,obj){
                banwenlaiyuan += `<li title="${obj.name}"><label><input type="checkbox" value="${obj.fileId}" class="select" style="margin:0 10px;" onclick="checkInput(this)" />${obj.name}</label></li>`
        })
         banwenlaiyuan = '<li style="height:auto;" class="close"><ul class="subUl">'+banwenlaiyuan+'</ul></li>';
        str += '<ul class="close ulSty"><li class="liSty"><i class="fa fa-chevron-right faSty faSty1" onclick="openCpj1(this)"></i><label><input type="checkbox" value="" style="margin:0 10px;" onclick="checkInput(this)" />办文来源</label></li>'+banwenlaiyuan+'</ul>';
     }
     if (rowdata.countersignatureFile&&rowdata.countersignatureFile.length>0) {
              $(rowdata.countersignatureFile).each(function(i,obj){
                     countersignatureFile += `<li title="${obj.name}"><label><input type="checkbox" value="${obj.fileId}" class="select" style="margin:0 10px;" onclick="checkInput(this)" />${obj.name}</label></li>`
             })
              countersignatureFile = '<li style="height:auto;" class="close"><ul class="subUl">'+countersignatureFile+'</ul></li>';
             str += '<ul class="close ulSty"><li class="liSty"><i class="fa fa-chevron-right faSty faSty1" onclick="openCpj1(this)"></i><label><input type="checkbox" value="" style="margin:0 10px;" onclick="checkInput(this)"/>会签单</label></li>'+countersignatureFile+'</ul>';
     }
     if (rowdata.szsy&&rowdata.szsy.length>0) {
               $(rowdata.szsy).each(function(i,obj){
                      banwenlaiyuan += `<li title="${obj.name}"><label><input type="checkbox" value="${obj.fileId}" class="select" style="margin:0 10px;" onclick="checkInput(this)" />${obj.name}</label></li>`
              })
               szsy = '<li style="height:auto;" class="close"><ul class="subUl">'+szsy+'</ul></li>';
              str += '<ul class="close ulSty"><li class="liSty"><i class="fa fa-chevron-right faSty faSty1" onclick="openCpj1(this)"><label><input type="checkbox" value="" style="margin:0 10px;" onclick="checkInput(this)" />首长首页</label></li>'+szsy+'</ul>';
     }
    return str;
}
