var deptTreeUrl = {"url":"/app/base/user/tree","dataType":"text"}; //部门树
var sureUrl = {"url":"/app/db/subdocinfo/batchSendOperation","dataType":"text"}; //保存
var pushTodo={"url":"/api/db/todo","dataType":"text"}; 
var fileId=getUrlParam("ids"); //主文件id
var content=getUrlParam2("content"); //意见内容
var userId="";
var userName="";
var pageModule = function(){
	var initTree = function(){
		$ajax({
			url:deptTreeUrl,
			success:function(data){
				$("#people-tree").jstree({
				    "plugins": ["wholerow", "types"],
				    "core": {
				    "themes" : {
				        "responsive": false
				    },    
				    "data": data,
				    },
				    "types" : {
				    	"default" : {
					        "icon" : "peoples_img"
					    },
					    "file" : {
					        "icon" : "peoples_img"
					    },
					    "1" : {
					        "icon" : "people_img"
					    }
				    }
				});
				$("#people-tree").on("select_node.jstree", function(e,data) {
					userName = "";
					userId = "";
					if(data.node.original.type == 1 || data.node.original.type == "1"){
						 userName = data.node.text;
						 userId = data.node.id;
					}
				});
			}
		})
	};
	
	var initother = function(){
		//确定
		$("#sure").click(function(){
			$ajax({
				url:sureUrl,
				data:{subIds:fileId,replyContent:content,userName:userName,userId:userId},
				success:function(data){
					newbootbox.newdialogClose("plspDialog");
					if(data.result=="success"){
						newbootbox.alert("提交成功！").done(function(){

                            // $ajax({
	    					// 	url:pushTodo,
	    					// 	data:{},
	    					// 	success:function(data){}
	    					// });
                            changToNum2(function () {
                                window.top.iframe1.pageModule.initgrid();
                                window.top.grdbfn()
                            });
						});
					}else{
						newbootbox.alert("送审批失败！");
					}
				}
			})
		});
		
		//关闭
		$("#close").click(function(){
			newbootbox.newdialogClose("plspDialog");
		})
	}
	return{
		//加载页面处理程序
		initControl:function(){
			initTree();
			initother();
		}
	};
}();
