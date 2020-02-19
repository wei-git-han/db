var grid = null;
var grid1 = null;
var grid2 = null;
var grid3 = null;
var tableUrl = {"url":rootPath +"/dbexpdeedbackset/list","dataType":"text"}; //表格list
var delUrl = {"url":rootPath +"/dbexpdeedbackset/delete","dataType":"text"}; //删除---待完善
var pageModule = function() {
	var initgrid = function() {
		grid = $("#gridcont").createGrid({
			columns: [
				  {display: "范例名称",name:"orgName",width: "15%",align: "left",render: function(rowdata,n){
					  return rowdata.expName;   
				  }},
				  {display: "范例内容",name:"deptName",width: "75%",align: "left",render: function(rowdata,n){
					  return rowdata.expContent;   
				  }}, 
				  {display: "操作",name: "do",width: "10%",align: "center",render: function(rowdata,n){
					  var btns="";
					  //if(限制条件){
						  btns= '<i class="fa fa-pencil btns"  onclick="editfn(\''+rowdata.id+'\')" title="编辑"></i>'+
						  		'<i class="fa fa-trash-o btns"  onclick="delfn(\''+rowdata.id+'\')" title="删除"></i>';
					  //}
					  return btns;
				  }}
				 ],
			width: "100%",
			height: "100%",
			checkbox: true,
			rownumberyon: true,
			overflowx: false,
            pagesize: 15,
			url: tableUrl
			
		});
		grid1 = $("#gridcont1").createGrid({
			columns: [
				  {display: "提醒角色",name:"orgName",width: "15%",align: "left",render: function(rowdata,n){
					  return rowdata.expName;   
				  }},
				  {display: "提醒时间",name:"deptName",width: "15%",align: "left",render: function(rowdata,n){
					  return rowdata.expName;   
				  }}, 
				  {display: "提醒内容",name:"deptName",width: "30%",align: "left",render: function(rowdata,n){
					  return rowdata.expName;   
				  }}, 
//				  {display:"状态",name:"flag",width:"15%",align:"center",paixu:false,render:function(rowdata,n){
//                  	var checkedMark = ""
//                   	return '<div class="switch"><input class="leaveSwitch" data-clickid="'+rowdata.id+'" name="status" type="checkbox" '+checkedMark+'></div>'; 
//                   }},
//                   {display:"操作",name:"",width:"15%",align:"center",paixu:false,render:function(rowdata,n){
//                    	return '<button type="button" class="btn btn-xs btn-icon QBlackFont" onclick="editfn(\''+rowdata.id+'\')"  title="编辑">编辑</button>';
//                    }},
				 ],
			width: "100%",
			height: "100%",
			checkbox: true,
			rownumberyon: true,
			overflowx: false,
			pageyno:false,
            pagesize: 15,
			url: tableUrl
			
		});
		grid2 = $("#gridcont2").createGrid({
			columns: [
				  {display: "提醒角色",name:"orgName",width: "15%",align: "left",render: function(rowdata,n){
					  return rowdata.expName;   
				  }},
				  {display: "提醒时间",name:"deptName",width: "15%",align: "left",render: function(rowdata,n){
					  return rowdata.expContent;   
				  }}, 
				  {display: "提醒内容",name:"deptName",width: "30%",align: "left",render: function(rowdata,n){
					  return rowdata.expContent;   
				  }}, 
				  {display:"状态",name:"flag",width:"15%",align:"center",paixu:false,render:function(rowdata,n){
                	var checkedMark = (rowdata.deductionVacationDay==1)?"":"checked"
                 	return '<div class="switch"><input class="leaveSwitch" data-clickid="'+rowdata.id+'" name="status" type="checkbox" '+checkedMark+'></div>'; 
                 }},
                 {display:"操作",name:"",width:"15%",align:"center",paixu:false,render:function(rowdata,n){
                  	return '<button type="button" class="btn btn-xs btn-icon QBlackFont" onclick="editfn(\''+rowdata.id+'\')"  title="编辑">编辑</button>';
                  }}
				 ],
			width: "100%",
			height: "100%",
			checkbox: true,
			rownumberyon: true,
			overflowx: false,
			pageyno:false,
            pagesize: 15,
			url: tableUrl
			
		});
		grid3 = $("#gridcont3").createGrid({
			columns: [
				  {display: "提醒角色",name:"orgName",width: "15%",align: "left",render: function(rowdata,n){
					  return rowdata.expName;   
				  }},
				  {display: "提醒时间",name:"deptName",width: "15%",align: "left",render: function(rowdata,n){
					  return rowdata.expContent;   
				  }}, 
				  {display: "提醒内容",name:"deptName",width: "30%",align: "left",render: function(rowdata,n){
					  return rowdata.expContent;   
				  }}, 
				  {display:"状态",name:"flag",width:"15%",align:"center",paixu:false,render:function(rowdata,n){
                	var checkedMark = (rowdata.deductionVacationDay==1)?"":"checked"
                 	return '<div class="switch"><input class="leaveSwitch" data-clickid="'+rowdata.id+'" name="status" type="checkbox" '+checkedMark+'></div>'; 
                 }},
                 {display:"操作",name:"",width:"15%",align:"center",paixu:false,render:function(rowdata,n){
                  	return '<button type="button" class="btn btn-xs btn-icon QBlackFont" onclick="editfn(\''+rowdata.id+'\')"  title="编辑">编辑</button>';
                  }}
				 ],
			width: "100%",
			height: "100%",
			checkbox: true,
			rownumberyon: true,
			overflowx: false,
			pageyno:false,
            pagesize: 15,
			url: tableUrl
			
		});
	}

	var initother = function() {
		$("#add").click(function() {
			newbootbox.newdialog({
				id:"addDialog",
				width:800,
				height:600,
				header:true,
				title:"新增范例",
				classed:"cjDialog",
				url:"/app/db/document/ywpz/fkfl/html/edit.html"
			})
		});
		
		$("#edit").click(function() {
			var datas = grid.getcheckrow();
			var ids=[];
			if(datas.length < 1 || datas.length > 1) {
				newbootbox.alertInfo("请选择一条数据进行编辑！");
			} else {
				$(datas).each(function(i){
					ids[i]=this.id;
				});
				newbootbox.newdialog({
					id:"addDialog",
					width:800,
					height:600,
					header:true,
					title:"新增范例",
					classed:"cjDialog",
					url:"/app/db/document/ywpz/fkfl/html/edit.html?id="+ids[0]
				})
			}
		});
		
		//删除
		$("#del").click(function() {
			var datas = grid.getcheckrow();
			var ids=[];
			if(datas.length < 1) {
				newbootbox.alertInfo("请选择要删除的数据！");
			} else {
				$(datas).each(function(i){
					ids[i]=this.id;
				});
				delfn(ids.toString());
			}
		});
		
	}

	return {
		//加载页面处理程序
		initControl: function() {
			initgrid();
			initother();
		},
		initgrid:function(){
			initgrid();
		}
	}

}();

//编辑
var editfn = function(id){
	newbootbox.newdialog({
		id:"addDialog",
		width:800,
		height:600,
		header:true,
		title:"范例设置",
		classed:"cjDialog",
		url:"/app/db/document/ywpz/fkfl/html/edit.html?id="+id
	})
}
//删除
var delfn = function(id){
	newbootbox.confirm({
		 title: "提示",
	     message: "是否要进行删除操作？",
	     callback1:function(){
	    	 $ajax({
				url: delUrl,
				type: "GET",
				data: {"ids":id},
				success: function(data) {
					if(data.result == "success") {
						newbootbox.alertInfo('删除成功！').done(function(){
							grid.refresh();
						});
					}else{
						newbootbox.alertInfo("删除失败！");
					}
				}
			})
	     }
	});
}