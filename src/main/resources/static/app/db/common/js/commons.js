/*
 js验证的时候用
 * */
var css={
		filter_html:function(html){
			/*1、本方法的功能：去除HTML标签、空格（&nbsp;）、换行（\n）*/
			var regex=/<[^>]*>/g;
			var txt=html.replace(regex,'');
			txt=txt.replace(/&nbsp;/g,'');
			txt=txt.replace(/\n/g,'');
			return txt;
		},
		limit_rich_text:function(){
			/*1、本方法的功能：限制富文本框的最大字符长度（去除掉：HTML标签、空格（&nbsp;）、换行（\n））*/
			var _obj=css;
			$('textarea.limit').each(function(){
				var $this=$(this);
				var id=$this.attr('id');
				var editor=CKEDITOR.instances[id];
				editor.on('change',function(event){
					var data_html=editor.getData();
					var txt=_obj.filter_html(data_html);
					var len=txt.length;
					$this.val(txt).blur();
				});

				var cancel=setInterval(function(){
					var $cke=$('#cke_'+id);
					if($cke.size()>0){
						clearInterval(cancel);
						$('#cke_'+id).after($this);
					}
				},300);
			});
		},
		get_checkbox_val:function(name,split){
			/*获取指定name的已被选择的复选框的所有值*/
			var vals='';
			split=split||',';
			$('input[name='+name+']:checked').each(function(){
				var $this=$(this);
				vals+=split+$this.val();
			});
			return vals.substring(split.length);
		},
		get_query_val:function(name){
			/*1、获取当前url中，指定参数的值*/
			var reg=new RegExp('(^|&)'+name+'=([^&]*)(&|$)');//构建一个含有目标参数的正则表达式对象
			var r=window.location.search.substr(1).match(reg);//匹配目标参数
			if(!!r){
				return unescape(r[2]);
			}else{
				return '';
			}

		},
		cut:function(selector){
			val=$(selector).val();
			val=val||'';
			$(selector).val(val.substring(0,10));
		},
		input_trim:function(obj){
			var txt=$(obj).val()||'';
			 txt=txt.replace(/^[ \t]*|[ \t]*$/g,'');
			 $(obj).val(txt);
		},
		disabled:function(){
			$('input,textarea,select').attr('disabled','disabled');
		}
};
var np2 = 0;
try{window.top.document}catch(e){
	np2=1;
	var str = e.message;
	if(str.indexOf("Permission denied to")!=-1){
		np2=1;
	}
}

var gettop2 = function(){
	if(np2==0){
		return window.top;
	}else{
		return window.parent;
	}
}
jQuery(document).ready(function() {
    Metronic.init(); // init metronic core componets
//    Layout.init(); // init layout
//    QuickSidebar.init(); // init quick sidebar
//    Demo.init(); // init demo features
//    initcomonother();
//    initinclude();

    var pagedate = new Date();
	var month = pagedate.getMonth()+1;
	if(month<10){
		month = "0"+month;
	}
	var day = pagedate.getDate();
	if(day<10){
		day = "0"+day;
	}
	var year = pagedate.getFullYear();
	$(".year").val(year);
	$(".datee").val(year+"-"+month+"-"+day);
	$(".datee2").val(year+"年"+month+"月"+day+"日");
    $(".datee3").val(year+"年");


	var hh = pagedate.getHours();
	if(hh<10){
		hh = "0"+hh;
	}
	var mm = pagedate.getMinutes();
	if(mm<10){
		mm = "0"+mm;
	}

	$(".datesfm").val(year+"-"+month+"-"+day+" "+hh+":"+mm);

});


/*start设置bootstrap滚动条自适应浏览器高度*/
var c2 = {};
$(window).resize(function(){
	clearTimeout(c2);
	c2 = setTimeout(function(){
		/*
        <div style="height:100%;">高度通过此处来设置,scroller外面需要包一层容器
        	<div  style="height:100%;" class="scroller">
        	</div>
        </div>
		 */
		$(".slimScrollDiv,.scroller").each(function(){
			$(this).css({"height":"100%"});
		});
	},500)
});
/*end*/

function setformdata(data){
	for(key in data){
		if($("[name="+key+"]").attr("type")=="checkbox"){
			var checkbox = document.getElementsByName(key);
			for(var j = 0;j<checkbox.length;j++){
				if(checkbox[j].value==data[key]){
					checkbox[j].checked=true;
					$(checkbox[j]).parent().addClass("checked");
				}
			};
		}else if($("[name="+key+"]").attr("type")=="radio"){
			var radio = document.getElementsByName(key);
			for(var j = 0;j<radio.length;j++){
				if(radio[j].value==data[key]){
					radio[j].checked=true;
					$(radio[j]).parent().addClass("checked");
				}else{
					radio[j].checked=false;
					$(radio[j]).parent().removeClass("checked");
				}
			};
		}else{
			$("#"+key).val(data[key]);
			//alert(data[key])
		}
	}

}

/*	var elementarry = ["username","email","phone","password1","password2","ifduty","role"];
	返回表单值{username:"username",...}
	var paramdata = getformdata(elementarry);
*/

function getformdata(arry){
	var paramdata = {};
	$.each(arry,function(i){
		if($("[name="+arry[i]+"]").attr("type")=="checkbox"){
			var valuearry = [];
			var checkboxvalue = "";
			var checkbox = document.getElementsByName(arry[i]);
			for(var j = 0;j<checkbox.length;j++){
				if(checkbox[j].checked==true){
					valuearry.push(checkbox[j].value);
				}
			};
			checkboxvalue = valuearry.join(",");
			paramdata[arry[i]] = checkboxvalue;
		}else if($("[name="+arry[i]+"]").attr("type")=="radio"){
			var radiovalue = "";
			var radio = document.getElementsByName(arry[i]);
			for(var j = 0;j<radio.length;j++){
				if(radio[j].checked==true){
					radiovalue = radio[j].value;
				}
			};
			paramdata[arry[i]] = radiovalue;
		}else{
			paramdata[arry[i]] = $("#"+arry[i]).val();
		}
	})
	return paramdata;
}

/*
 传入一个表单name数组，清空表单数据（重置按钮使用）-------这个方法可以用在列表的重置按钮上，传入对应的表单元素的name值即可
 * */
	function removeInputData(arry){
		$.each(arry,function(i){
			$("[name="+arry[i]+"]").val("");
		})
	}

/*	var jsonarry =  [{
						text:"是否转维修",
						value:"01"
					},{
						text:"已转",
						value:"02"
					},{
						text:"未转",
						value:"03"
					}]
	用来对select进行初始化
	initselect("selectelement",jsonarry);
	*/
function initselect(id,arry){
	$("#"+id).html("");
	var html = "";
	$.each(arry,function(i){
		if(($.trim(arry[i].text)).indexOf("请选择")==-1){
			html+='<option value='+arry[i].value+'>'+arry[i].text+'</option>';
		}
	});
	$("#"+id).append(html);
}
function initselect1(id,arry){
	$("#"+id).html("");
	var html = "";
	$.each(arry,function(i){
		if(($.trim(arry[i].text)).indexOf("请选择")==-1){
			html+='<option value='+arry[i].value+'>'+arry[i].text+'</option>';
		}
	});
	html+='<option selected="selected" style="display:none" value="0">请选择</option>';
	$("#"+id).append(html);
}

function initselect_DOC(id,arry,checkedId){
	var html = "";
	if(checkedId == "" || checkedId == null || typeof(checkedId) == "undefined" || checkedId == "undefined"){
		$.each(arry,function(i){
			html+='<option value='+arry[i].id+'>'+arry[i].dictionaryValue+'</option>';
		});
	}else{
		$.each(arry,function(i){
			if(arry[i].id == checkedId){
				html+='<option value='+arry[i].id+' selected="true">'+arry[i].dictionaryValue+'</option>';
			}else{
				html+='<option value='+arry[i].id+' >'+arry[i].dictionaryValue+'</option>';
			}
		});
	}
	$("#"+id).append(html);
}

function $ajax(obj){
	var urlobj = obj.url;
	if(urlobj==null||typeof(urlobj)=="undefined"){
		return;
	}
	var url = urlobj.url;
	var async = obj.async;
	var dataType = urlobj.dataType;
	var data = obj.data;
	var success = obj.success;
	var error = obj.error;
	var type = obj.type;
	if(url==null||typeof(url)=="undefined"){
		return
	}
	if(async==null||typeof(async)=="undefined"){
		async = true;
	}
	if(dataType==null||typeof(dataType)=="undefined"){
		dataType = 'json';
	}
	if(data==null||typeof(data)=="undefined"){
		data = {};
	}
	if(success==null||typeof(success)=="undefined"){
		success = function(data){
			//alert();
		}
	}
	if(error==null||typeof(error)=="undefined"){
		error = function(msg) {
			//alert("系统故障!");
		}
	}
	if(type==null||typeof(type)=="undefined"){
		type = "GET";
	}else{
		if(dataType=='text'){
			type = "GET";
		}
	}
	$.ajax({
		url : url,
		dataType : dataType,
		type:type,
		async: async,
		data:data,
		success:function(data){
			if(dataType=="text"){
				data = eval("("+data+")");
			}
			return success(data);
		},
		error : error
	});
}

function getUrlParam(name){
	var reg=new RegExp("(^|&)"+name+"=([^&]*)(&|$)");
	var r=window.location.search.substr(1).match(reg);
	if(r!=null)return unescape(r[2]);
	return null;
}
/*升级版，可以带中文自动解码*/
function getUrlParam2(name){
	var reg=new RegExp("(^|&)"+name+"=([^&]*)(&|$)","i");
	var r=window.location.search.substr(1).match(reg);
	//if(r!=null)return unescape(r[2]);
	if(r!=null)return decodeURI(r[2]);
	return null;
}

function initinclude(){
	var include = $("[include]");
	if(include!=null&&typeof(include)!="undefined"){
		include.each(function(i){
			var obj = $(include[i]);
			var url = obj.attr("include");
			$ajax({
				url:{"url":url,"dataType":"html"},
				async:false,
				success:function(html){
					obj.append(html);
				}
			})
		})
	}
}

//用来日期格式化
Date.prototype.format =function(format) {
	var o = {
		"M+" : this.getMonth()+1, //month
		"d+" : this.getDate(), //day
		"h+" : this.getHours(), //hour
		"m+" : this.getMinutes(), //minute
		"s+" : this.getSeconds(), //second
		"q+" : Math.floor((this.getMonth()+3)/3), //quarter
		"S" : this.getMilliseconds() //millisecond
	}
	if(/(y+)/.test(format)) format=format.replace(RegExp.$1,
	(this.getFullYear()+"").substr(4- RegExp.$1.length));
	for(var k in o)if(new RegExp("("+ k +")").test(format))
	format = format.replace(RegExp.$1,
	RegExp.$1.length==1? o[k] :
	("00"+ o[k]).substr((""+ o[k]).length));
	return format;
}

jQuery.fn.extend({
	createSelecttree: function(obj) {
		obj.target = $(this).attr("id");
		var gridobj = new createSelecttree(obj);
		return gridobj;
	}
});

jQuery.fn.extend({
	createcheckboxtree: function(obj) {
		obj.target = $(this).attr("id");
		var gridobj = new createcheckboxtree(obj);
		return gridobj;
	}
});

function createSelecttree(obj){
	var create = function(){
		$(".selecttree").css({
			width:"100%",
			height:"100%",
			overflow:"visible"
		});
		$("#"+obj.target).css({
			width:"100%",
			height:"100%",
			"padding-left":"10px",
			border:"none"
		});
		if(obj.readonly!=false){
			$("#"+obj.target)[0].readOnly=true;
		}
		var width = obj.width;
		if(width==null||typeof(width)=="undefined"){
			width = "";
		}else{
			width = "width:"+obj.width;
		}
		var data = obj.data;
		if(data==null||typeof(data)=="undefined"){
			data = {};
		}
		$("#"+obj.target).parent().append(
			'<div class="'+obj.target+'tree1 trees" style="max-height:300px;overflow: auto;display:none;background:#ffffff;border:1px solid #cccccc;'+width+';padding:10px;position:absolute;z-index: 100;">'+
    		'	<div id="'+obj.target+'tree2" class="tree-demo">'+
			'	</div>'+
    		'</div>'
		);
		$("#"+obj.target).click(function(){
			var objClass = obj.target+"tree1";
			$(".trees").each(function(){
				if(!$(this).hasClass(objClass)){
					$(this).hide();
				}
			});
			if($("."+obj.target+"tree1").is(":hidden")){
				$("."+obj.target+"tree1").show();
			}else{
				$("."+obj.target+"tree1").hide();
			}
			return false;
		})

		//增加判断，当点击展开和收起加减号时不隐藏树。
		$("body").click(function(e){
			if($(e.target).hasClass("jstree-ocl")){
				return;
			}
			$("."+obj.target+"tree1").slideUp(50);
		})

		if(!obj.data){
			$ajax({
				url:obj.url,
				async:false,
				success:function(data){
					initdata(data);
				}
			})
		}else{
			initdata(obj.data);
		}

	}

	var initdata = function(data){
		$("#"+obj.target+"tree2").jstree({
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
		$("#"+obj.target+"tree2").on("ready.jstree", function(e,o) {
			obj.success(data,$("#"+obj.target+"tree2"));
		});
		$("#"+obj.target+"tree2").on("select_node.jstree", function(e,data) {
			var id = $("#" + data.selected).attr("id");
			//liuhq:当同一页面中有两个或多个相同树时赋值有重复问题，
			//$("#"+obj.target).val($("#"+id+">a").text());
			$("#"+obj.target).val($("#"+obj.target+"tree2").find("#"+id+">a").eq(0).text());
		    obj.selectnode(e,data);
		});
	}

	create();
}


var isclose0 = true;
function createcheckboxtree(obj){
	var create = function(){
		$(".selecttree").css({
			width:"100%",
			height:"100%",
			overflow:"visible"
		});
		$("#"+obj.target).css({
			width:"100%",
			height:"100%",
			"padding-left":"10px",
			border:"none"
		});
		if(obj.readonly!=false){
			$("#"+obj.target)[0].readOnly=true;
		}

		var width = obj.width;
		if(width==null||typeof(width)=="undefined"){
			width = "";
		}else{
			width = "width:"+obj.width;
		}
		var data = obj.data;
		if(data==null||typeof(data)=="undefined"){
			data = {};
		}
		$("#"+obj.target).parent().append(
			'<div class="'+obj.target+'tree1 positionTree trees" style="max-height:300px;overflow-y:auto;overflow-x: auto;display:none;background:#ffffff;border:1px solid #cccccc;'+width+';padding:10px;position:relative;z-index: 100;">'+
    		'	<div id="'+obj.target+'tree2" class="tree-demo" style="width:100%;">'+
			'	</div>'+
    		'</div>'
		);

		$("#"+obj.target).click(function(){
			var objClass = obj.target+"tree1";
			$(".trees").each(function(){
				if(!$(this).hasClass(objClass)){
					$(this).hide();
				}
			});
			if($("."+obj.target+"tree1").is(":hidden")){
				$("."+obj.target+"tree1").show();
			}else{
				$("."+obj.target+"tree1").hide();
			}
			return false;
		})

		//增加判断，当点击展开和收起加减号时不隐藏树。
		$("body").click(function(e){
			if($(e.target).hasClass("jstree-ocl")){
				return;
			};
			var objClass = obj.target+"tree1";
			if($(e.target).parents("div").hasClass(objClass)){
				if(!isclose){
					return;
				}
			}
			$("."+objClass).slideUp(50);
		})

		if(!obj.data){
			$ajax({
				url:obj.url,
				async:false,
				success:function(data){
					initdata(data);
				}
			})
		}else{
			initdata(obj.data);
		}
	}

	var initdata = function(data){
		$("#"+obj.target+"tree2").jstree({
		    "plugins": ["wholerow", "types","checkbox"],
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
		$("#"+obj.target+"tree2").on("ready.jstree", function(e,o) {
			obj.success(data,$("#"+obj.target+"tree2"));
		});

		$("#"+obj.target+"tree2").on("select_node.jstree", function(e,data) {
			var id = $("#" + data.selected).attr("id");
			var nodes2 = $("#"+obj.target+"tree2").jstree("get_bottom_checked",true);
			var treessid = [];
			var treessname = [];
			$.each(nodes2, function(i,obj) {
				treessid.push(obj.id);
				treessname.push(obj.text);
			});
			if(treessid.length==0){
				isclose = false;
			}else{
				isclose = false;
				obj.selectnode(e,data,treessname,treessid);
			};
		});

		$("#"+obj.target+"tree2").on("deselect_node.jstree", function(e,data) {
			var id = $("#" + data.selected).attr("id");
			var nodes2 = $("#"+obj.target+"tree2").jstree("get_bottom_checked",true);
			var treessid = [];
			var treessname = [];
			$.each(nodes2, function(i,obj) {
				treessid.push(obj.id)
				treessname.push(obj.text)
			});
		    obj.deselectnode(e,data,treessname,treessid);
		});
	}


	create();
}

jQuery.fn.extend({
	createUserTree: function(obj) {
		obj.target = $(this).attr("id");
		var gridobj = new createUserTree(obj);
		return gridobj;
	}
});

var isclose = true;
function createUserTree(obj){
	var create = function(){
		$(".selecttree").css({
			width:"100%",
			height:"100%",
			overflow:"visible"
		});
		$("#"+obj.target).css({
			width:"100%",
			height:"100%",
			"padding-left":"10px",
			border:"none"
		});
		$("#"+obj.target)[0].readOnly=true;

		var width = obj.width;
		if(width==null||typeof(width)=="undefined"){
			width = "";
		}else{
			width = "width:"+obj.width;
		}
		var data = obj.data;
		if(data==null||typeof(data)=="undefined"){
			data = {};
		}
		$("#"+obj.target).parent().append(
			'<div class="'+obj.target+'tree1 trees"  style="max-height:300px;overflow:auto;display:none;background:#ffffff;border:1px solid #cccccc;'+width+';padding:10px;position:absolute;z-index: 100;">'+
    		'	<div id="'+obj.target+'tree2" class="tree-demo">'+
			'	</div>'+
    		'</div>'
		);

		$("#"+obj.target).click(function(){
			var objClass = obj.target+"tree1";
			$(".trees").each(function(){
				if(!$(this).hasClass(objClass)){
					$(this).hide();
				}
			});
			if($("."+obj.target+"tree1").is(":hidden")){
				$("."+obj.target+"tree1").show();
			}else{
				$("."+obj.target+"tree1").hide();
			}
			return false;
		})

		//增加判断，当点击展开和收起加减号时不隐藏树。
		$("body").click(function(e){
			if($(e.target).hasClass("jstree-ocl")){

				return;
			};
			var objClass = obj.target+"tree1";
			if($(e.target).parents("div").hasClass(objClass)){
				if(!isclose){
					return;
				}
			}
			$("."+objClass).slideUp(50);
		})


		if(!obj.data){
			$ajax({
				url:obj.url,
				async:false,
				success:function(data){
					initdata(data);
				}
			})
		}else{
			initdata(obj.data);
		}
	}

	var initdata = function(data){
		var plugins = obj.plugins;
		if(plugins==null||typeof(plugins)=="undefined"){
			plugins = "";
		}

		$("#"+obj.target+"tree2").jstree({
		    "plugins": ["wholerow", "types",plugins],
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
		$("#"+obj.target+"tree2").on("ready.jstree", function(e,o) {
			obj.success(data,$("#"+obj.target+"tree2"));
		});
		$("#"+obj.target+"tree2").on("before_open.jstree", function(e,o) {
			setTimeout(function(){
				$("#filter").parent().addClass("open");
			})
		});
		$("#"+obj.target+"tree2").on("select_node.jstree", function(e,data) {
			var nodes2 = $("#"+obj.target+"tree2").jstree("get_bottom_selected",true);
			var treessid = [];
			var treessname = [];
			$.each(nodes2, function(i,obj) {
				if(obj.original.type == 1){
					treessid.push(obj.id);
					treessname.push(obj.text);
				}
			});
			if(treessid.length==0){
				isclose = false;
			}else{
				if(plugins == "checkbox"){
					isclose = false;
				}else{
					isclose = true;
				}
				obj.selectnode(e,data,treessname,treessid);
			};
		});
		$("#"+obj.target+"tree2").on("deselect_node.jstree", function(e,data) {
			var nodes2 = $("#"+obj.target+"tree2").jstree("get_bottom_selected",true);
			var treessid = [];
			var treessname = [];
			$.each(nodes2, function(i,obj) {
				if(obj.original.type == 1){
					treessid.push(obj.id);
					treessname.push(obj.text);
				}
			});
		    obj.selectnode(e,data,treessname,treessid);
		});
	}

	create();
}

jQuery.fn.extend({
	createGwzltree: function(obj) {
		obj.target = $(this).attr("id");
		var gridobj = new createGwzltree(obj);
		return gridobj;
	}
});

var isclose2 = true;
function createGwzltree(obj){
	var create = function(){
		$(".selecttree").css({
			width:"100%",
			height:"100%",
			overflow:"visible"
		});
		$("#"+obj.target).css({
			width:"100%",
			height:"100%",
			"padding-left":"10px",
			border:"none"
		});
		$("#"+obj.target)[0].readOnly=true;

		var width = obj.width;
		if(width==null||typeof(width)=="undefined"){
			width = "";
		}else{
			width = "width:"+obj.width;
		}
		var data = obj.data;
		if(data==null||typeof(data)=="undefined"){
			data = {};
		}
		$("#"+obj.target).parent().append(
			'<div class="'+obj.target+'tree1 trees" style="max-height:300px;overflow: auto;display:none;background:#ffffff;border:1px solid #cccccc;'+width+';padding:10px;position:absolute;z-index: 100;">'+
    		'	<div id="'+obj.target+'tree2" class="tree-demo" style="width:100%;">'+
			'	</div>'+
    		'</div>'
		);
		$("#"+obj.target).click(function(){
			var objClass = obj.target+"tree1";
			$(".trees").each(function(){
				if(!$(this).hasClass(objClass)){
					$(this).hide();
				}
			});
			if($("."+obj.target+"tree1").is(":hidden")){
				$("."+obj.target+"tree1").show();
			}else{
				$("."+obj.target+"tree1").hide();
			}
			return false;
		})
/*		$("body").click(function(){
			$("."+obj.target+"tree1").slideUp(50)
		})*/
		//增加判断，当点击展开和收起加减号时不隐藏树。
		$("body").click(function(e){
			if($(e.target).hasClass("jstree-ocl")){
				return;
			};
			var objClass = obj.target+"tree1";
			if($(e.target).parents("div").hasClass(objClass)){
				if(!isclose2){
					return;
				}
			}
			$("."+objClass).slideUp(50);
		})
		$ajax({
			url:obj.url,
			async:false,
			success:function(data){

				$("#"+obj.target+"tree2").jstree({
				    "plugins": ["wholerow", "types"],
				    "core": {
				    "themes" : {
				        "responsive": false
				    },
				    "data": data,
				    },
				    "types" : {
				    	"default" : {
					        "icon" : "fa fa-folder icon-state-warning icon-lg"
					    },
					    "file" : {
					        "icon" : "fa fa-file icon-state-warning icon-lg"
					    },
					    "1" : {
					        "icon" : "fa fa-folder icon-state-warning icon-lg"
					    }
				    	/*"default" : {
					        "icon" : "tree_img"
					    },
					    "file" : {
					        "icon" : "tree_img"
					    },
					    "1" : {
					        "icon" : "tree_img"
					    }*/
				    }
				});
				$("#"+obj.target+"tree2").on("ready.jstree", function(e,o) {
					obj.success(data,$("#"+obj.target+"tree2"));
				});
				$("#"+obj.target+"tree2").on("select_node.jstree", function(e,data) {
					//var id = $("#" + data.selected).attr("id");
					//liuhq:当同一页面中有两个或多个相同树时赋值有重复问题，
					//$("#"+obj.target).val($("#"+id+">a").text());
					//$("#"+obj.target).val($("#"+obj.target+"tree2").find("#"+id+">a").eq(0).text());
				    obj.selectnode(e,data);
				});

			}
		})

	}
	create();
}


/**
 * 判断OS，如果是Window系统并且是IE浏览器，则需要初始化；如果是Linux系统并且是FireFox浏览器，则需要初始化；
 */
function getWebEquipmentOS(){
	// 判断OS
	var sUserAgent = navigator.userAgent;
	var isWin = (navigator.platform == 'Win32') || (navigator.platform == 'Windows');
	var isLinux = (String(navigator.platform).indexOf("Linux") > -1);
	var isUnix = (navigator.platform == 'X11') && !isWin;
	// 判断浏览器
	var explorere = navigator.userAgent.toLowerCase();
	var isFireFox = (explorere.indexOf("firefox") > 0);
	var isIE = (explorere.indexOf("msie") > 0);
	if ((isLinux || isUnix) && isFireFox) {
		return 'firefox';
	} else if (isWin && isIE) {
		return 'ie';
	} else {
		return 'no_init';
	}

}

var path = "/gwgl/";
var newbootbox = {
	confirm:function(obj){
		gettop2().bootbox.dialog({
	        title: obj.title,
	        message: obj.message,
	        buttons: {
	          success: {
	            label: "确定",
	            className: "btn-primary",
	            callback: function() {
					obj.callback1();
	            }
	          },
	          danger: {
	            label: "取消",
	            className: "btn-default",
	            callback: function() {
	            	if(obj.callback2 != null && typeof(obj.callback2)!="undefined"){
						obj.callback2();
	            	}
	            }
	          },
	        }
	    });
	},
	//插件的确认框
	oconfirm:function(obj){
		gettop2().bootbox.dialog({
	        title: obj.title,
	        message: obj.message,
            className:"cjDialog",
	        buttons: {
	          success: {
	            label: "确定",
	            className: "btn-primary",
	            callback: function() {
					obj.callback1();
	            }
	          },
	          danger: {
	            label: "取消",
	            className: "btn-default",
	            callback: function() {
	            	if(obj.callback2 != null && typeof(obj.callback2)!="undefined"){
						obj.callback2();
	            	}
	            }
	          }
	        }
	    });
	},
	confirm100:function(obj){
		gettop2().bootbox.dialog({
	        title: obj.title,
	        message: obj.message,
            className:"cjDialog",
	        buttons: {
	          success: {
	            label: "保存",
	            className: "btn-primary",
	            callback: function() {
					obj.callback1();
	            }
	          },
	          danger1: {
	            label: "不保存",
	            className: "btn-primary",
	            callback: function() {
	            	obj.callback2();
	            }
	          },
	          danger: {
	            label: "取消",
	            className: "btn-default",
	            callback: function() {
	            	//obj.callback3();
	            }
	          },
	        }
	    });
	},
	//插件的确认框
	Tconfirm:function(obj){
		gettop2().bootbox.dialog({
	        title: obj.title,
	        message: obj.message,
            className:"cjDialog",
	        buttons: {
	        	danger: {
		            label: "确定",
		            className: "btn-primary",
		            callback: function() {
		            	if(obj.callback2 != null && typeof(obj.callback2)!="undefined"){
							obj.callback2();
		            	}
		            }
		          }
	        }
	    });
	},
	//插件的提示框
	alerttitle:function(text,fn){
		var dtd=$.Deferred();
		var obj=gettop2().bootbox.dialog({
	            message: text,
	            title: "提示",
	            className:"cjDialog chajian"
	        });
    	var $alert=gettop2().$(".cjDialog");
    	obj.on("shown.bs.modal",function(e){
			if(fn){fn()}
		});
	},
	//插件的提示框
	alert:function(text,shi){
		var dtd=$.Deferred();
		var obj=gettop2().bootbox.dialog({
	            message: text,
	            title: "提示",
	            className:"cjDialog chajian"
	        });
    	var $alert=gettop2().$(".cjDialog");
        var cancel=setTimeout(function(){
        	 if(shi || shi == null || typeof(shi)=="undefined"){
        		 gettop2().$(".chajian .newclose").click();
        	 }
        	 dtd.resolve();
		},1000);
        obj.on("hidden.bs.modal",function(e){
			clearTimeout(cancel);
			dtd.resolve();
		});
		return dtd;
	},
	//插件的提示框，手动关闭提示框
    alert1:function(text,shi){
        var dtd=$.Deferred();
        var obj=gettop2().bootbox.dialog({
                message: text,
                title: "提示",
                className:"cjDialog chajian"
            });
        var $alert=gettop2().$(".cjDialog");

        obj.on("hidden.bs.modal",function(e){
            if(shi || shi == null || typeof(shi)=="undefined"){
                 gettop2().$(".chajian .newclose").click();
             }
            dtd.resolve();
        });
        return dtd;
    },
	alertInfo:function(text,shi){
		var dtd=$.Deferred();
		var objInfo=gettop2().bootbox.dialog({
            message: text,
            title: "提示",
            className:"alertInfo"
        });
        var $alert=gettop2().$(".alertInfo");
        var cancel=setTimeout(function(){
        	 if(shi || shi == null || typeof(shi)=="undefined"){
        		 //gettop2().$(".newclose").click();
        		 $(".newclose",$(objInfo)).click();
        	 }
			//gettop2().$(".alertInfo").find(".newclose").click();
			dtd.resolve();
		},1000);
        objInfo.on("hidden.bs.modal",function(e){
			clearTimeout(cancel);
			dtd.resolve();
		});
		return dtd;
	},
	newdialog:function(obj){
		var id = obj.id;
		var width = obj.width+"px";
		var height = obj.height+"px";
		var title = obj.title;
		var header = obj.header;
		var style = obj.style;
		var url = obj.url;
		var classed = obj.classed;
		var theme = obj.theme;
		var theme_color = '';
		$(gettop2().document.body).find("#"+obj.id).remove();
		var html="";
		var styleHtml="";
		if(!header){
			html = "style='display:none'";
		}
		if(style!=null||typeof(style)!="undefined"){
			for(key in style){
				styleHtml+=";"+key+":"+style[key]
			}
		};
		if(classed==null||typeof(classed)=="undefined"){
			classed="";
		};
		if(theme==null||typeof(theme)=="undefined"){
			theme="";
		}else if(theme=='black'){
			url+=url.indexOf('?')>-1?'&theme=black':'?theme=black'
		};
		$(gettop2().document.body).append(
			'<div class="modal fade in newmodal '+classed+' '+theme+'" id="'+obj.id+'" tabindex="-1" aria-hidden="true" >'+
			'    <div class="modal-dialog" style="width:'+width+'">'+
			'        <div class="modal-content">'+
			'            <div class="modal-header"'+html+'>'+
			'                <div class="newclose" data-dismiss="modal" aria-hidden="true"><i class="fa fa-times"></i></div>'+
			'                <h4 class="modal-title">'+title+'</h4>'+
			'            </div>'+
			'            <div class="modal-body" style="height:'+height+styleHtml+';overflow:hidden">'+
			'				<iframe src="'+url+'" style="width:100%;height:100%;" frameborder="0" marginheight="0px" marginwidth="0px"  height="100%" width="100%"></iframe>'+
			'            </div>'+
			'        </div>'+
			'    </div>'+
			'</div>'
		);
		gettop2().showModal(obj.id);
		if(classed){
			if(classed.indexOf('dragDialog')>-1){
				setTimeout(function () {
					gettop2().DragEl(gettop2().document)
				},500)
			}
		}
	},
	newdialogClose:function(obj){
		gettop2().hideModal(obj);
	},
	newclose:function(modalId){
		//alert(modalId)
		$("#"+modalId).modal("hide");
	}
}


$("body").keydown(function(event){
	var event = event || window.event;
	if(event.keyCode == "13"){
		$(".search").click();
	}
})

//给表格添加鼠标经过显示按钮的功能
var initbtn = function(){
	$(".table-title").each(function(){
		var Tr = $(this).parents("tr");
		var TdLength = Tr.find("td").size();
		var btnTd = Tr.find("td:eq("+(TdLength-2)+")");
		var btnHtml = btnTd.html();
		var overDiv = '<div class="overDiv"></div>';
		$(this).wrap("<div class='titleDiv' style='width:100%;white-space:nowrap;text-overflow:ellipsis;overflow:hidden'></div>");
		$(this).parent().append($(overDiv).html(btnHtml))
	});
	var o = {};
	$(".titleDiv").hover(function(e){
		var $obj = $(this);
		clearTimeout(o);
		o = setTimeout(function(){
			//var xx = e.originalEvent.x || e.originalEvent.layerX || 0;
    		//var yy = $obj.offset();
			var xx = e.clientX;
			var yy = $obj.position();
    		$obj.find(".overDiv").fadeIn().css({'top':yy.top-30,"left":xx});
		},500)
	},function(){
		$(this).find(".overDiv").fadeOut();
		clearTimeout(o);
	})
}


jQuery.fn.extend({
	createTablist: function(obj) {
		obj.target = $(this).attr("id");
		var tablist = new createTablist(obj);
		return tablist;
	}
});
function createTablist(obj){
	var create = function(){
		var url = obj.url;
		var success = obj.success;
		var delfn1 = obj.delfn1;
		var clickfn = obj.click;
		var target = obj.target;
		var render = obj.render;
		$ajax({
			url:url,
			success:function(data){
				if(render){
					data = render(data);
				}
				initdata(data);
				success(data);
			}
		})
		$(".newtab-ul1>li").unbind("click");
		$(".newtab-ul1>li").live("click",function(){
			var o = $(this).attr("data");
			if(o!=0){
				clickfn(
						$(this).attr("data"),
						$(this).attr("dataType"),
						function(){
							$(".newtab-ul1>li").removeClass("active");
							$(this).addClass("active");
						}
				);
			}
		});
		$(".newtab-ul1 ul li").unbind("click");
		$(".newtab-ul1 ul li").live("click",function(){
			clickfn(
					$(this).attr("data"),
					$(this).attr("dataType"),
					function(){
						$(".newtab-ul1>li").removeClass("active");
						$(o).parent().parent().addClass("active");

						$(o).parent().find("li").removeClass("liactive");
						$(o).addClass("liactive");

						var text = $(o).text();
						$(o).parents("li").eq(0).find("font").text(text);
					}
			);
			return false;
		});

		$(".newtab-ul1>li .o1").unbind("click");
		$(".newtab-ul1>li .o1").live("click",function(){
			delfn1();
			return false;
		});
	}

	var initdata = function(data){
		target = obj.target;
		$("#"+target).html("");
		var cpj = data.cpj;
		if(cpj.length>0){
			var html = "";
			$.each(cpj,function(i,o){
					html += ''+
							'	<li class="'+(i==0?"liactive":"")+'" data="'+o.id+'" dataType="cpj">'+
							'		<a href="javascript:;">'+o.name+'</a>'+
							'	</li>';
			});
			$("#"+target).append(
				'<li class="'+target+'-cpj active" data="'+cpj[0].id+'" dataType="cpj">'+
				'	<a href="javascript:;">'+
				'		<span><i class="fa fa-angle-down"></i></span>'+
				'		主文件:<font>'+cpj[0].name+'</font>'+
				'	</a>'+
				'	<ul>'+html+
				'	</ul>'+
				'</li>'
			);
			//clickfn($(this).attr(cpj[0].id));
		}else{
			$("#"+target).append(
					'<li class="'+target+'-cpj active" data="cpj" dataType="cpj">'+
					'	<a href="javascript:;">'+
					'		<span><i class="fa fa-angle-down"></i></span>'+
					'		主文件:<font>空</font>'+
					'	</a>'+
					'</li>'
				);
		}
		var fj = data.fj;
		if(fj.length>0){
			var html = "";
			$.each(fj,function(i,o){
					html += ''+
							'	<li class="'+(i==0?"liactive":"")+'" data="'+o.id+'" dataType="fj">'+
							'		<a href="javascript:;">'+o.name+'</a>'+
							'	</li>';
			});
			$("#"+target).append(
				'<li class="'+target+'-fj"  data="'+fj[0].id+'" dataType="fj">'+
				'	<a href="javascript:;">'+
				'		<span><i class="fa fa-angle-down"></i></span>'+
				'		附件:<font>'+fj[0].name+'</font>'+
				'	</a>'+
				'	<ul>'+html+
				'	</ul>'+
				'</li>'
			);
		}else{
			$("#"+target).append(
					'<li class="'+target+'-fj"  data="fj" dataType="fj">'+
					'	<a href="javascript:;">'+
					'		<span><i class="fa fa-angle-down"></i></span>'+
					'		附件:<font>空</font>'+
					'	</a>'+
					'</li>'
				);
		}
		var bwly = data.bwly;
		if(bwly && bwly.length>0){
			var html = "";
			$.each(bwly,function(i,o){
					html += ''+
							'	<li class="'+(i==0?"liactive":"")+'" data="'+o.id+'" dataType="bwly">'+
							'		<a href="javascript:;">'+o.name+'</a>'+
							'	</li>';
			});
			$("#"+target).append(
				'<li class="'+target+'-wjly"  data="'+bwly[0].id+'" dataType="bwly">'+
				/*'<li class="'+target+'-wjly"  data="'+wjly[0].id+'" dataType="bwly">'+*/
				'	<a href="javascript:;">'+
				'		<span><i class="fa fa-angle-down"></i></span>'+
				'		文件来源:<font>'+bwly[0].name+'</font>'+
				'	</a>'+
				'	<ul>'+html+
				'	</ul>'+
				'</li>'
			);
		}else{
			$("#"+target).append(
					'<li class="'+target+'-wjly"  data="bwly" dataType="bwly">'+
					'	<a href="javascript:;">'+
					'		<span><i class="fa fa-angle-down"></i></span>'+
					'		文件来源:<font>空</font>'+
					'	</a>'+
					'</li>'
				);
		}
	}

	this.addfn = function(str){
		$("."+obj.target+"-"+str).attr("o","0");

		$(".newtab-ul1>li").removeClass("active");
		$("."+obj.target+"-"+str).addClass("active");

		$("."+obj.target+"-"+str).parent().find("li").removeClass("liactive");

		if(str=="cpj"){
			$("."+obj.target+"-"+str+">a font").html('[新增主文件]&nbsp;&nbsp;<i class="fa fa-trash-o o1"></i>')
		}else if(str=="fj"){
			$("."+obj.target+"-"+str+">a font").html('[新增主文件]&nbsp;&nbsp;<i class="fa fa-trash-o o1"></i>')
		}else if(str=="bwly"){
			$("."+obj.target+"-"+str+">a font").html('[新增主文件]&nbsp;&nbsp;<i class="fa fa-trash-o o1"></i>')
		}
	}
	this.render = function(data){
		initdata(data);
	}
	this.refreshfn = function(){
		create();
	}
	this.selectfn = function(str){
		var o = $('.newtab-ul1 ul>li[data='+str+']');
		$(".newtab-ul1>li").removeClass("active");

		o.parent().parent().addClass("active");

		o.parent().find("li").removeClass("liactive");
		o.addClass("liactive");

		var text = o.text();
		o.parents("li").eq(0).find("font").text(text);
	}
	create();
}

jQuery.fn.extend({
	createTitlecontent: function(obj) {
		obj.target = $(this);
		var titlecontent = new createTitlecontent(obj);
		return titlecontent;
	}
});
function createTitlecontent(obj){
	var create = function(){
		var target = obj.target;
		var html = obj.html;
		var afterfn = obj.afterfn;
		var position = obj.position;

		target.click(function(){
			if($('#titlecontent').length==0){

				var x =$(this).offset().left;
				var y = $(this).offset().top;

				var o = $('<div id="titlecontent" style="padding-top:10px;position:relative;padding-bottom:10px;"></div>');

				var p = {position:"absolute"};

				if(position(this)=="top"){
					var h = $("body").height();
					p.left = x+"px";
					p.bottom = (h-y)+"px";
					o.append('<div style="position:absolute;bottom:0;border-top:10px solid #ccc;border-left:10px solid transparent;border-right:10px solid transparent;width:10px;"></div>')
				}else if(position(this)=="bottom"){
					p.top = (y+30)+"px";
					p.left = x+"px";
					o.append('<div style="position:absolute;top:0;border-bottom:10px solid #ccc;border-left:10px solid transparent;border-right:10px solid transparent;width:10px;"></div>')
				}else{
					p.top = (y+30)+"px";
					p.left = x+"px";
					o.append('<div style="position:absolute;top:0;border-bottom:10px solid #ccc;border-left:10px solid transparent;border-right:10px solid transparent;width:10px;"></div>')
				}
				o.css(p);

				o.append(html(this));

				$("body").append(o);
				$('#titlecontent').unbind("click");
				$('#titlecontent').click(function(){
					return false;
				});
				afterfn(this);
			}else{
				$('#titlecontent').remove();
			}
			return false;
		})
		$("body").click(function(){
			$('#titlecontent').remove();
		})
	}

	create();
}
var changToNumUrl = {"url":"/api/db/todo"};
//更新桌面代办数量
function changToNum(){
		$ajax({
			url:changToNumUrl,
			success:function(data){
			if(navigator.userAgent.indexOf('OfficeBrowser')>=0){
				gettop2().__set_todo_count__(data.count);
			   }
			}
		});
}
//带回调 更新桌面待办数量
function changToNum2(callback){
         		$ajax({
         			url:changToNumUrl,
         			success:function(data){
         			if(navigator.userAgent.indexOf('OfficeBrowser')>=0){
         				gettop2().__set_todo_count__(data.count);
         			   }
         			if (callback) {
                        callback();
         			}
         			}
         		});
         }
function GetRootPath(){
	var pathName = window.location.pathname.substring(1);
	var webName = pathName == '' ? '' : pathName.substring(0, pathName.lastIndexOf('/'));
	//rootPath = webName == '' ? '/' : '/' + webName;
	rootPath = "/"+webName.split('/')[0]+"/"+webName.split('/')[1];
	return rootPath;
}

var rootPath = GetRootPath();

//关闭桌面浏览器
function windowClose() {
    if (navigator.userAgent.indexOf('OfficeBrowser') >= 0) {
        if(window.top.__close__){
            try{
                window.top.__close__()
            }
            catch(e){
                console.log(e)
            }
        }else{
            window.location = 'cmd:close';
        }
    } else {
        window.close();
    }
}



//加载进度条
var openloading = function(newval,total){
	var value1 = (newval/total)*100;
	if(value1>=100){
		value1=100;
	}
	var width = value1+"%";
	if(gettop2().$(".jdt").length>0){
		gettop2().$(".jctcont").css("width",width);
	}else{
		gettop2().$("body").append(
			`
				<div style="width:100%;height:5px;position:absolute;top:0;z-index:10000;" class ="jdt">
					<div style="height:100%;width:${width};background:rgb(0, 192, 255);" class="jctcont" ></div>
				</div>
			`
		)
	}
	if(value1>=100){
		setTimeout(function(){
			gettop2().$(".jdt").remove();
		},200);
	}
}

// 表格创建功能
jQuery.fn.extend({

	creatTable: function(obj) {

		var sortcolumns = obj.sortcolumns;
		var columns = obj.columns;
		var pobj = obj.pobj;
		$.each(columns,function(){
			var array1 = this;
			$.each(array1,function(){
				var field = this.field;
				if(field=="ck"){return;}
				this.title = pobj[field].title;
				this.width = pobj[field].width;
				this.hidden = pobj[field].hidden;
				this.number = sortcolumns[field];
			});
		});

		columns[0].sort(function(o1,o2){return o1.number-o2.number;});
		obj.onResizeColumn = function(a1,a2){initdgfn();};



		var o1 = null;
		var target = $(this).attr("id");
		var pcont = obj.pcont;
		$(window).resize(function(){
			clearTimeout(o1);
			o1 = setTimeout(function(){
				$('#'+target).datagrid('resize',{
					width:$(pcont).width(),
					height: $(pcont).height()
				});
			},500);
		});
		var gridobj = $('#'+$(this).attr("id")).datagrid(obj);
		return gridobj;
	}
});
// 表格生成
var initcheckgroup = function(pobj){
	var data1 = pobj.data1;
	if(typeof(data1)=="undefined"||data1==null||$.trim(data1)==""){
		data1={};
	}
	$ajax({
		url:pobj.url1,
		data:data1,
		success:function(data){
			var columns = data.columns
			var n1 = columns.length;
			if(n1>1){
				if(!!pobj.loadfn){

					var object = {};
					$.each(columns,function(i,item){
						object[item.field] = item;
					});
					if(!object['index']){
						object['index'] = {
							field: "index",
							hidden: false,
							number: 0,
							width: 50
						};
					}
					object.auto = data.auto;
					var sobj = {};
					$.each(columns,function(){
						sobj[this.field] = this.number;
					})
					if(!sobj['index']){
						sobj['index'] = -1;
					}
					object.auto = data.auto;
					pobj.loadfn(columns,object,sobj);

				}
			};
			$(".checkcont").html("");

			var auto = data.auto;
			$(".checkcont").append(
				`
					<div class="checkgroup auto">
						<!--
						<div class="checkgroup-title">
							表格宽度配置
						</div>
						-->
						<div class="checkgroup-cont">
							<div class="checkgroup-inner">
								<label >
									<input type="radio" name="auto" ${auto?"checked":""} value=0 > 自适应宽度
								</label>
								<label >
									<input type="radio" name="auto" ${!auto?"checked":""} value=1 > 固定宽度 
								</label>					
							</div>
						</div>
					</div>
				`
			);



			$(".checkcont").append(
				`
					<div class="checkgroup columns" style="border-bottom:none;">
						<!--
						<div class="checkgroup-title">
							列显示配置
						</div>
						-->
						<div class="checkgroup-cont">
							<div class="checkgroup-inner">
							</div>
						</div>
					</div>
				`
			);
			$.each(columns,function(i,o){
				if(o.field=='index'){
					return
				}
				var title = o.title;
				var field = o.field;
				var checked = o.hidden;
				var width = o.width;
				if(checked==false){checked="checked"};
				$(".columns .checkgroup-inner").append(
					`
						<label>
							<i class="fa fa-crosshairs " title="拖动排序" ></i>
							<input type="checkbox" value="${field}" ${checked} data1="${width}"> ${title} 
						</label>
					`
				);
			});


			$(".columns").sortable({
				items:"label",
				cursor:"move",
				opacity:0.5,
				stop:function(event,ui){

					var object = {};
					$(".columns input").each(function(){
						var item = {};
						var field = this.value;
						item.field = field;
						item.hidden = !this.checked;
						item.width = parseInt($(this).attr("data1"),10);
						//if($(".auto input:checked").val()==0){
						//	item.width = parseInt($(this).attr("data1"),10);
						//}else{
						//	item.width = parseInt($(".datagrid [field="+field+"]").width(),10);
						//}
						object[item.field] = item;
					});
					if(!object['index']){
						object['index'] = {
							field: "index",
							hidden: false,
							number: 0,
							width: 50
						};
					}
					if($(".auto input:checked").val()==0){
						object.auto = true;
					}else{
						object.auto = false;
					}

					var sobj = {};
					$(".columns input").each(function(i){
						sobj[this.value] = i;
					})
					if(!sobj['index']){
						sobj['index'] = -1;
					}
					pobj.loadfn(columns,object,sobj);

				}
			});

			getdatafn = function(){
				var object = {};
				var array = [];
				$(".columns input").each(function(i){
					var obj = {};
					var field = this.value;
					obj.field = field;
					obj.hidden = !this.checked;
					if($(".auto input:checked").val()==0){
						obj.width = parseInt($(this).attr("data1"),10);
					}else{
						obj.width = parseInt($(".datagrid [field="+field+"]").width(),10);
					}
					obj.number = i;
					array.push(obj);
				})
				object.columns = array;
				var auto = $(".auto input:checked").val();
				if(auto == 0){auto = true;
				}else{auto = false;}
				object.auto = auto;

				return object;
			}
			var beforobject = JSON.stringify(getdatafn());

			$(".columns input").unbind("click");
			$(".columns input").click(function(){
				var field = this.value;
				if(!this.checked){
					$(pobj.target).datagrid('hideColumn', field);
				}else{
					$(pobj.target).datagrid('showColumn', field);
				}
			})

			$(".auto input").unbind("click");
			$(".auto input").click(function(){
				var object = {};
				var value = this.value;
				$(".columns input").each(function(){
					var item = {};
					var field = this.value;
					item.field =field;
					item.hidden = !this.checked;
					item.width = parseInt($(this).attr("data1"),10);
					//if(value==0){
					//	item.width = parseInt($(this).attr("data1"),10);
					//}else{
					//	item.width = parseInt($(".datagrid [field="+field+"]").width(),10);
					//}
					object[item.field] = item;
				})
				if(!object['index']){
					object['index'] = {
						field: "index",
						hidden: false,
						number: -1,
						width: 50
					};
				}
				if(value==0){
					object.auto = true;
				}else{
					object.auto = false;
				}

				var sobj = {};
				$(".columns input").each(function(i){
					sobj[this.value] = i;
				})
				if(!sobj['index']){
					sobj['index'] = -1;
				}
				pobj.loadfn(columns,object,sobj);
			})

			initdgfn = function(){

				var object = getdatafn();

				if(beforobject==object){
					return;
				}

				var data2 = pobj.data2
				if(typeof(data2)=="undefined"||data2==null||$.trim(data2)==""){
					data2={};
				}
				object.columns.unshift({
					field: "index",
					hidden: false,
					number: -1,
					width: 50
				})
				beforobject = JSON.stringify(object);
				data2.json = JSON.stringify(object);
				$ajax({
					url:pobj.url2,
					data:data2,
					success:function(data){
						if(data.result=="success"){

							var columns = object.columns;
							$.each(columns,function(){
								var field = this.field;
								var width = this.width;
								$(".columns input[value="+field+"]").attr("data1",width);

							})

						}
					}
				});

			};
		}
	})
}
// 菜单点击展示下级
var initudgroup = function(){
	$("body").click(function(e){
		var n1 = $(e.target).parents(".newbtngroup100").length;
		if(n1>0){return;}else{
			if(!$(".newbtngroup100-cont").is(":hidden")){
				$(".newbtngroup100-cont").hide();
			}
		}
	})
	$(".newbtngroup100-cont").mouseleave(function(){
		initdgfn();
	});
	$(".newbtngroup100>button").click(function(){
		if($(this).parent().find(".newbtngroup100-cont").is(":hidden")){
			$(this).parent().find(".newbtngroup100-cont").show();
		}else{
			$(this).parent().find(".newbtngroup100-cont").hide();
			initdgfn();
		}
	})
}

//上传图片（支持同时上传多个图片）
jQuery.fn.extend({ //成员函数
	createfile:function(obj){
		var targetid = $(this).attr("id");
		var filen = 0;
		var deletefilekey = [];
		var name = obj.name;
		var yn = obj.yn;
		var view = obj.view;
		var cssStyle = obj.cssStyle;
		var delbtn = obj.delbtn;
		var type = obj.type;
		if(obj.initdata){
			var filelist = obj.initdata;
			$.each(filelist,function(i,o){
				if(view=="view1"){
					$(".filelist[name="+targetid+"]").append(
						'<div style="display:block;position:relative;text-align: left;margin-right:20px;margin-bottom: 10px;" id="filecont_'+targetid+'_'+o.fileServerId+'" class="fileview_'+targetid+'" data="'+o.fileServerId+'">'+
						'	<i class="fa fa-file-o"></i><a onclick="downloadfn(\''+o.fileServerId+'\')" title="下载" style="margin-left:3px;margin-right:8px;">'+o.fileName+'</a>'+
						'	<a id="'+o.id+'" class="delfj">删除</a>'+
						'</div>'
					);

				}else if(view=="view2"){
					$(".filelist[name="+targetid+"]").append(
						'<div style="display:inline-block;'+cssStyle+';position:relative;float:left;margin-right:20px;margin-bottom:10px;" id="filecont_'+targetid+'_'+o.code+'" class="fileview_'+targetid+'" data="'+o.code+'">'+
						'	<div style="width:20px;height:20px;position:absolute;top:0;right:0;text-align:center;background:#cccccc;cursor:pointer;color:#fff;" id="'+o.code+'">x</div>'+
						'	<div style="width:100%;height:100%;">'+
						'		<img src="'+o.load+'"  style="width:100%;height:100%;"/>'+
						'	</div>'+
						'</div>'
					);

				}
				$("#"+o.code).click(function(){
					$(this).parent().remove();
					deletefilekey.push(this.id);
				});
			})
		}
		$("#"+targetid).click(function(){
			filen+=1;
			if(view=="view1"){
				$(".filelist[name="+targetid+"]").append(
					'<div style="display:block;position:relative;display:none;text-align: left;margin-right:20px;margin-bottom:10px;" id="filecont_'+targetid+'_'+filen+'">'+
					'	<input type="file" accept=".pdf,.ofd,.doc,.docx,.wps" name="'+name+'" style="display:none;" id="file_'+targetid+'_'+filen+'"/>'+
					'</div>'
				);
			}else if(view=="view2"){
				$(".filelist[name="+targetid+"]").append(
					'<div style="display:inline-block;'+cssStyle+';position:relative;display:none;float:left;margin-right:20px;margin-bottom:10px;" id="filecont_'+targetid+'_'+filen+'">'+
					'	<input type="file"  name="'+name+'" style="display:none;" id="file_'+targetid+'_'+filen+'"/>'+
					'</div>'
				);
			}
			$("#file_"+targetid+"_"+filen).change(function(){
				var filename = this.value;
				var array = filename.split("\\");
				filename = array[array.length-1];
				var typearray = filename.split(".");
				var filetype = typearray[typearray.length-1];
				var type = obj.type;
				type = type.join(",");
				if(type.indexOf(filetype)==-1){
					$(this).val("");
					$('.filemessage[name='+targetid+']').html('不支持.'+filetype+'文件类型');
					$('.filemessage[name='+targetid+']').show();
					return;
				}
				var filesize = this.files[0].size;
				var maxsize = obj.maxsize;
				if(filesize > maxsize){
					$(this).val("");
					$('.filemessage[name='+targetid+']').html('您上传的文件超过'+(maxsize/1024/1024).toFixed(2)+'M，请重新上传!');
					$('.filemessage[name='+targetid+']').show();
					return;
				}
				if(view=="view1"){

					$("#filecont_"+targetid+"_"+filen).append(
						'	<i class="fa fa-file-o" style="margin-right:3px;"></i><font class="filename">'+filename+'</font>'+
						'	<a id="close_'+targetid+'_'+filen+'">删除</a>'
					);
				}else if(view=="view2"){
					$("#filecont_"+targetid+"_"+filen).append(
						'<div style="width:20px;height:20px;position:absolute;top:0;right:0;text-align:center;background:#cccccc;cursor:pointer;color:#fff;" class="close_btn" id="close_'+targetid+'_'+filen+'">x</div>'+
						'<div style="width:100%;height:100%;">'+
						'	<img id="img_'+targetid+'_'+filen+'" class="pic" style="width:100%;height:100%;"/>'+
						'</div>'
					);
					$(".filemessage").html("");
				}
				if(view=="view2"){
					var otype="jpg,png,jpeg,JPG,JPEG,PNG"
					if(otype.indexOf(filetype)==-1){
						$(this).val("");
						$('.filemessage[name='+targetid+']').html('当前视图不支持.'+filetype+'文件类型！');
						$('.filemessage[name='+targetid+']').show();
						return;
					}

					var file = this.files[0];
					var reader = new FileReader();
					reader.onload = function(re){
						$("#img_"+targetid+"_"+filen).attr("src",re.target.result);
					}
					reader.readAsDataURL(file);
				}
				if(yn==false){
					if($(".fileview_"+targetid).length!=0){
						deletefilekey.push($(".fileview_"+targetid).attr("data"));
						$(".fileview_"+targetid).remove();
					};

					for(var i=0;i<filen;i++){
						if(i!=(filen-1)){
							$("#filecont_"+targetid+"_"+(i+1)).remove();
						}
					}

				}
				if(view=="view2"){
					$("#filecont_"+targetid+"_"+filen).css("display","inline-block");
				}else{
					$("#filecont_"+targetid+"_"+filen).css("display","block");
				}
				$("#close_"+targetid+"_"+filen).click(function(){
					$(this).parent().remove();
				});
			})
			$("#file_"+targetid+"_"+filen).click()
		});
		return {
			getdeletefilekey:function(){
				return deletefilekey;
			}
		}
	}
})


//这个方法是解决浏览器中input框焦点丢失问题的调用方法
//建议是在所有的input框中都调用这个方法
function fixForcesBug(){
	  var obj = document.getElementById("fixfocusbug");
	  obj.ResetFocus();
}

var showModal = function(obj){
	$("#"+obj).modal("show");
}
var hideModal = function(obj){
	$("#"+obj).modal("hide");
}
var theme = getUrlParam('theme')||'';
if(theme =='black'){
	$('body').addClass('body-black');
}
function DragEl(el) {
	var dragDom = el.querySelector('.dragDialog')
	var dialogHeaderEl = dragDom.querySelector(' .dragDialog .modal-header')
	dialogHeaderEl.style.cssText += ';cursor:move;'
	dragDom.style.cssText += ';'

	// 获取原有属性 ie dom元素.currentStyle 火狐谷歌 window.getComputedStyle(dom元素, null);
	var getStyle = (function() {
		if (window.document.currentStyle) {
			return (dom, attr) => dom.currentStyle[attr]
		} else {
			return (dom, attr) => getComputedStyle(dom, false)[attr]
		}
	})()

	dialogHeaderEl.onmousedown = (e) => {
		// 鼠标按下，计算当前元素距离可视区的距离
		var disX = e.clientX - dialogHeaderEl.offsetLeft
		var disY = e.clientY - dialogHeaderEl.offsetTop

		var dragDomWidth = dragDom.offsetWidth
		var dragDomHeight = dragDom.offsetHeight

		var screenWidth = document.body.clientWidth
		var screenHeight = document.body.clientHeight

		var minDragDomLeft = dragDom.offsetLeft
		var maxDragDomLeft = screenWidth - dragDom.offsetLeft - dragDomWidth

		var minDragDomTop = dragDom.offsetTop
		var maxDragDomTop = screenHeight - dragDom.offsetTop - dragDomHeight

		// 获取到的值带px 正则匹配替换
		var styL = getStyle(dragDom, 'left')
		var styT = getStyle(dragDom, 'top')

		if (styL.includes('%')) {
			styL = +document.body.clientWidth * (+styL.replace(/\%/g, '') / 100)
			styT = +document.body.clientHeight * (+styT.replace(/\%/g, '') / 100)
		} else {
			styL = +styL.replace(/\px/g, '')
			styT = +styT.replace(/\px/g, '')
		}

		document.onmousemove = function(e) {
			// 通过事件委托，计算移动的距离
			var left = e.clientX - disX
			var top = e.clientY - disY

			// 边界处理
			if (-(left) > minDragDomLeft) {
				left = -minDragDomLeft
			} else if (left > maxDragDomLeft) {
				left = maxDragDomLeft
			}

			if (-(top) > minDragDomTop) {
				top = -minDragDomTop
			} else if (top > maxDragDomTop) {
				top = maxDragDomTop
			}

			// 移动当前元素
			dragDom.style.cssText += `;left:${left + styL}px;top:${top + styT}px;`
		}

		document.onmouseup = function(e) {
			document.onmousemove = null
			document.onmouseup = null
		}
	}
}
