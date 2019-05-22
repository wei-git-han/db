var id = getUrlParam2("id");
var saveUrl = {"url":rootPath +"/adminset/saveOrUpdate","dataType":"text"};  //保存
var editInfo = {"url":rootPath +"/adminset/info","dataType":"text"}; //编辑数据
var getUserAdminTypeUrl = {"url":rootPath +"/adminset/getAuthor","dataType":"text"};//那当前用户的类型1：部管理员，2：局管理员
var userTree; //部门树
var pageModule = function(){
	var initrolefn = function(){
		$ajax({
			url:getUserAdminTypeUrl,
			async:false,
			success:function(data){
				if(data=="2"){
					userTree = {"url":"/app/base/user/tree","dataType":"text"}; //部门树
				}else{
					userTree = {"url":"/app/base/user/allTree","dataType":"text"}; //人员选择树
				}
			}
		})
	}
	
	var initdatafn = function(){
		if(id!="" && !!id){
			$ajax({
				url:editInfo,
				data:{id:id},
				success:function(data){
					setformdata(data);
					$("#roleType").val("局管理员");
				}
			})
		}
	}
	
	var initother = function(){
		$("#userName").createUserTree({
			url : userTree,
			width:"100%",
			success : function(data, treeobj) {},
			selectnode : function(e, data) {
				$("#userName").val(data.node.text);
				$("#userId").val(data.node.id);
			}
		});
		
		$("#quxiao,#fanhui").click(function(){
			window.location.href="/app/db/document/ywpz/glysz/html/index.html";
		})
		
		$("#save").click(function(){
			var userName=$("#userName").val();
			var userId=$("#userId").val();
			if(userId == ''){
				newbootbox.alertInfo("请选择用户！");
				return;
			}
			$ajax({
				url:saveUrl,
				data:{id:id,userName:userName,userId:userId,adminType:"2"},
				type: "GET",
				success:function(data){
					if(data.result == "success") {
						newbootbox.alertInfo('保存成功！').done(function(){
							window.location.href = "/app/db/document/ywpz/glysz/html/index.html";
						});
					}else{
						newbootbox.alertInfo("保存失败！");
					}
				}
			});
			
		})
	}
	
	return{
		//加载页面处理程序
		initControl:function(){
			initrolefn();
			initdatafn();
			initother();
		}
	};
	
}();
