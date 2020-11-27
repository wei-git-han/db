var sureUrl = {"url":"/app/db/replyexplain/edit","dataType":"text"}; //保存
var subId=getUrlParam("subId")||""; //子分支主id
var fileId=getUrlParam("fileId")||""; 
var teamId=getUrlParam("teamId")||""; 
var fromMsg=getUrlParam("fromMsg")|| false; 
// var replyContent=getUrlParam2("replyContent")||""; //文件来源
var checkStatus = getUrlParam("checkStatus");
var fileFrom = getUrlParam("fileFrom");
var ideaGroupId = getUrlParam("ideaGroupId");
var sureUrl1 = {"url":"/app/db/replyexplain/editOpinion","dataType":"text"}; //保存
$("#button1").find("font").text($.trim($("#button1 [data="+checkStatus+"]").text()));
var getReplyInfoUrl = {"url":"/app/db/replyexplain/getReplyInfo","dataType":"text"}; // 个人待办返回意见
var pageModule = function(){
	var initother = function(){
		// $("#replyContent").val(replyContent);
		//确定
		$("#sure").click(function(){
            var data = {subId:subId,infoId:fileId,teamId:teamId,replyContent:$("#replyContent").val(),checkStatus:checkStatus};
            var url = sureUrl;
            //办理反馈中编辑
            if ( fileFrom=="blfk" ) {
                data.ideaGroupId = ideaGroupId;
                url = sureUrl1;
            }
			$.ajax({
				url:url.url,
				data:data,
				type:'post',
				success:function(data){
					newbootbox.newdialogClose("editDialog");
					if(data.result=="success"){
						if(fromMsg && fromMsg=="true"){
							newbootbox.alert("修改成功！").done(function(){
								window.parent.pageModule.initblfkList();
							});
						}else{
							newbootbox.alert("修改成功！").done(function(){
								window.top.iframe1.window.pageModule.initblfkList();
							});
						}
					}else{
						newbootbox.alert("修改失败！");
					}
				}
			})
		});
		
		//关闭
		$("#close").click(function(){
			newbootbox.newdialogClose("editDialog");
		});
		
		$("#button1 a").click(function(){
			var data = $(this).attr("data");
			checkStatus = data;
			var name = $.trim($(this).text());
			$("#button1").find("font").text(name);
		})

	}
	function initData(){
        $ajax({
            url:getReplyInfoUrl,
            data:{infoId:fileId,subId:subId},
            success:function(data){
                if(data.content != null && data.content != '') {
                    // var content = data.content;
                    $("#replyContent").val(data.content);
                }
            }
        })
	}
	return{
		//加载页面处理程序
		initControl:function(){
			initData();
			initother();
		}
	};
}();
