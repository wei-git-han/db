var saveUrl = {"url":rootPath +"/roleset/saveOrUpdate","dataType":"text"};  //保存
var editInfo = {"url":rootPath +"/roleset/info","dataType":"text"}; //编辑数据
var treeurl = {"url":"/app/base/user/tree","dataType":"text"}; //人员树
var id = getUrlParam("id");
var pageModule = function(){
	var initdatafn = function(){
		if(id && id != "null" && id != ""){
			$ajax({
				url:editInfo,
				data:{id:id},
				type: "GET",
				success:function(data){
					setformdata(data);
				}
			});
		}
	}
	
	var initother = function(){
		//人员选择
		$("#userName").createUserTree({
			url : treeurl,
			width : $("#userName").parent().width()+"px",
			success : function(data, treeobj) {
			},
			selectnode : function(e, data,treessname,treessid) {
				$("#userName").val(treessname);
				$("#userId").val(treessid);
			},
			deselectnode : function(e, data,treessname,treessid) {
				$("#userName").val(treessname);
				$("#userId").val(treessid);
			}
		});
		
		$("#quxiao").click(function(){
			window.location.href="/app/db/document/ywpz/jssz/html/jssz.html";
		});
		$("#fanhui").click(function(){
			window.location.href="/app/db/document/ywpz/jssz/html/jssz.html";
		})
		
		$("#commentForm").validate({
		    submitHandler: function() {
		    	var adminName = $("#userName").val();
				var adminId = $("#userId").val();
				if(adminName == null || adminName == ''){
					newbootbox.alert("请选择人员！");
					return ;
				}
				$ajax({
					url:saveUrl,
					data:{id:id,userName:adminName,userId:adminId,roleFlag:$("#roleFlag option:selected").val()},
					type:'GET',
					success:function(data){
						if(data.result=="success"){
							newbootbox.alertInfo("保存成功！").done(function(){
								window.location.href="/app/db/document/ywpz/jssz/html/jssz.html";
							}); 
						}else{
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
