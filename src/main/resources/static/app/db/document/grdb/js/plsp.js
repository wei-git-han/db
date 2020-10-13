var passUrl = {"url":"/app/db/subdocinfo/batchFinishOperation","dataType":"text"};//审批通过   ---静态地址、待修改
var pushTodo={"url":"/api/db/todo","dataType":"text"}; 
var ids=getUrlParam("ids")||""; //主文件id
var curRole=getUrlParam("curRole")||"6"; //主文件id
var pageModule = function(){
	var initother = function(){
		if(curRole != "3"){
			$("#pass").hide();
		}
		//审批通过
		$("#pass").click(function(){
			newbootbox.oconfirm({
			 	title:"提示",
			 	message: "审批完成，确认后将正式发布此办件落实情况？",
			 	callback1:function(){
			 		$ajax({
						url:passUrl,
						data:{subIds:ids,content:$("#content").val()},
						success:function(data){
							newbootbox.newdialogClose("plspDialog");
							if(data.result=='success'){
								newbootbox.alertInfo('公文已审批通过！').done(function(){
									window.top.iframe1.pageModule.initgrid();
									$ajax({
			    						url:pushTodo,
			    						data:{},
			    						success:function(data){}
			    					});	
									window.top.grdbfn()
								});
							}else{
								newbootbox.alertInfo('审批未通过！');
							}
						}
					});	
			 	}
			});
		});
		
		//送审批
		$("#send").click(function(){
			window.location.href="/app/db/document/grdb/html/send.html?ids="+ids+"&content="+$("#content").val()
		})
	}
	return{
		//加载页面处理程序
		initControl:function(){
			initother();
		}
	};
}();
