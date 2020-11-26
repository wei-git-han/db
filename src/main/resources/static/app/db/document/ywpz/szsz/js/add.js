var saveUrl = {"url":rootPath +"/roleset/saveOrUpdate","dataType":"text"};  //保存
var editInfo = {"url":rootPath +"/roleset/info","dataType":"text"}; //编辑数据
var treeurl = {"url":"/app/base/user/allTree","dataType":"text"}; //人员树
var id = getUrlParam("id");
var userId = getUrlParam("userId");
var pageModule = function(){
	var initdatafn = function(){
		if(id && id != "null" && id != ""){
			$ajax({
				url:editInfo,
				data:{id:id},
				type: "GET",
				success:function(data){
					setformdata(data);
					$("#roleFlag").val("首长");
				}
			});
		}
	}
	
	var initother = function(){
		$("#quxiao").click(function(){
			window.location.href="/app/db/document/ywpz/szsz/html/szsz.html";
		});
		$("#fanhui").click(function(){
			window.location.href="/app/db/document/ywpz/szsz/html/szsz.html";
		})
		
		$("#commentForm").validate({
		    submitHandler: function() {
		    	var adminName = $("#userName").val();
		    	//var deptName = $("#deptName").val();
		    	var sort = $("#sort").val();
				if(adminName == null || adminName == ''){
					newbootbox.alert("请输入姓名！");
					return ;
				}
				$ajax({
					url:saveUrl,
					data:{id:id,userName:adminName,roleFlag:"1",sort:sort,userId:userId},
					type:'GET',
					success:function(data){
						if(data.code == 0){
							newbootbox.alertInfo("保存成功！").done(function(){
								window.location.href="/app/db/document/ywpz/szsz/html/szsz.html";
							}); 
						}else if(data.code == 1){
							newbootbox.alertInfo(data.result);
						}
					}
				})
		    }
		 });
		
		 $("#save").click(function(){
			$("#commentForm").submit();
		 });
	}
	
	return{
		//加载页面处理程序
		initControl:function(){
			initdatafn();
			initother();
		}
	};
}();
