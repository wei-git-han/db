var getUserAdminTypeUrl = {"url":rootPath +"/adminset/getAuthor","dataType":"text"};
var pageModule = function(){
	var initother = function(){
		$ajax({
			url: getUserAdminTypeUrl,
			type: "GET",
			success: function(data) {
				if(data=="0"){//超级管理员
					$("#jssz").show();
					$("#szsz").show();
					$("#zdwh").show();
					$('#departAdmin').show();
					$('#juAdmin').show();
					$("#fkfl").show(); //反馈范例...
				}else if(data=="1"){//部管理员
					$('#departAdmin').show();
					$('#juAdmin').show();
					$("#szsz").show();
					$("#zdwh").show();
					$("#fkfl").show(); //反馈范例...
				}else{ //局管理员
					$('#juAdmin').show();
					$("#jssz").show();
				}
			}
		});
		$(".newpage8").click(function(){
			$(".newpage8").removeClass("active");
			$(this).addClass("active");
		});
	}

	return{
		//加载页面处理程序
		initControl:function(){
			initother();
		}
	};
	
}();
