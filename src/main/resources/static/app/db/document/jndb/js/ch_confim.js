var id = getUrlParam("id");
var infoId = getUrlParam("infoId");
var dealUserId = getUrlParam("dealUserId");
var chehuiUrl = {"url":"/app/db/withdraw/juAdministratorWithdraw","dataType":"text"};//撤回url
var pageModule = function(){
	var initother = function(){
		//借阅保存
		$("#savech").click(function(){
			if($("#isSure").val()=='撤回'||$("#isSure").val()=='是'){
				$ajax({
		 			url:chehuiUrl,
		 			data:{subId:id, infoId:infoId, dealUserId:dealUserId},
		 			success:function(data){
		 				if(data.result=='success'){
		 					newbootbox.newdialogClose("ch_confim");
		 					newbootbox.alertInfo('撤回成功！').done(function(){
		 					    changToNum2(function(){
		 						    window.top.iframe1.pageModule.initgrid();
		 						    window.top.jndbfn();
                                    window.top.grdbfn();
                                    window.top.blfkfn();
		 						})
		 					});
		 				}else{
		 					newbootbox.newdialogClose("ch_confim");
		 					newbootbox.alertInfo('撤回失败！');
		 				}
		 				/*window.top.jndbfn();
						window.top.grdbfn();
						window.top.blfkfn();
						changToNum()*/
		 			}
		 		});	
			}else{
				newbootbox.newdialogClose("ch_confim");
				newbootbox.alertInfo('撤回取消！');
			}
			
		});
		
		//关闭
		$("#close").click(function(){
			newbootbox.newdialogClose("ch_confim");
		})
	}
	
	return{
		//加载页面处理程序
		initControl:function(){
			initother();
		}
	};
	
}();


