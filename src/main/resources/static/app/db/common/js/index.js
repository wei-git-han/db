var menulist={"url":"/app/db/menu/menuList","dataType":"text"};
var grdbUrl = {"url":"/app/db/subdocinfo/grdbMenuNums","dataType":"text"};
var jndbUrl = {"url":"/app/db/subdocinfo//jndbMenuNums","dataType":"text"};
var blfkUrl = {"url":"/app/db/documentinfo/getDicByTypet","dataType":"text"};
var show = getUrlParam("show")||"";
var pageModule = function(){
	//动态加载菜单
	var initMenuList =function(){
		$ajax({
			url:menulist,
			success:function(data){
				var lis = '';
				if(data.length == 0) {
					window.location.href="error.html";
				}else{
					$.each(data, function(i, item) {
						if(i==0){
							//var str = item.defaultPage.indexOf('?')>-1?"&":"?";
							$("#iframe1").attr("src","/app/db/document/jcdb/html/index.html?menuId=005");
						}
						lis += '<li id="'+item.id+'"><a href="'+item.defaultPage+'" target="iframe1">'+item.menuName;
						if(item.id=='002'){
							lis += '<i class="grdb_num" style="display:none"></i>';
						}else if(item.id=='003'){
							lis += '<i class="jndb_num" style="display:none"></i>';
						}else if(item.id=='004'){
							lis += '<i class="blfk_num" style="display:none"></i>';
						}
						lis +='</a></li>';
					});

					$('#menulist').html(lis);  //追加到页面
//					$(".menuli li").eq(0).addClass("active");
					$(".menuli li").click(function(){
						$(this).siblings().removeClass("active");
						$(this).addClass("active");
						window.top.memory = {};
					});
					$('#005').click()
					grdbfn();
					jndbfn();
					blfkfn();
				}
			}
		});
	}

	return{
		//加载页面处理程序
		initControl:function(){
			initMenuList();
			getUserId();
		}
	};

}();

var showModal = function(obj){
	$("#"+obj).modal("show");
}
var hideModal = function(obj){
	$("#"+obj).modal("hide");
}

//个人待办气泡
function grdbfn(){
	$ajax({
		url:grdbUrl,
		async:false,
		success:function(data){
			if(data.grdbNum > 0 && data.grdbNum != null && data.grdbNum != "" && typeof(data.grdbNum) != undefined){
				$(".grdb_num").show();
				$('.grdb_num').text(data.grdbNum);
			}else{
				$(".grdb_num").hide();
				$('.grdb_num').text("");
			}
		}
	});
}

//局内待办气泡
function jndbfn(){
	$ajax({
		url:jndbUrl,
		success:function(data){
			if(data.jndbNum > 0 && data.jndbNum != null && data.jndbNum != "" && typeof(data.jndbNum) != undefined){
				$(".jndb_num").show();
				$('.jndb_num').text(data.jndbNum);
			}else{
				$(".jndb_num").hide();
				$('.jndb_num').text("");
			}
		}
	});
}

//意见反馈气泡
function blfkfn(){
	$ajax({
		url:blfkUrl,
		data:{menuFlag:true},
		success:function(data){
			if(data.blfkNum > 0 && data.blfkNum != null && data.total != "" && typeof(data.blfkNum) != undefined){
				$(".blfk_num").show();
//				$('.blfk_num').text(data.blfkNum);
				$('.blfk_num').text("");
			}else{
				$(".blfk_num").hide();
				$('.blfk_num').text("");
			}
		}
	});
}
//loading弹出框
function lodaingControl(status){
	$("#qjDialog").modal(status)
}
var lockReconnect = false;// 避免重复链接
var wsObj = null;// websocket
var tW = null; // 重连定时器
var hasMesssage = true;
var reloadRedPoint = true; // 刷新未关闭
var timeLoadMs = 5; // 加载时间
var timeLoadTime = null;
var messageUserId = ''
function initWebSocket() {
	if(!messageUserId){
		setTimeout(function () {
			initWebSocket()
		},2000)
		return
	}
//    alert(location.host)
	wsObj = new WebSocket(`ws://${location.host}/webSocket/${messageUserId}`);

	wsObj.onopen = function (e) {
		console.log('建立链接成功')
		console.log(e)
		heartCheck.start()
	}
	wsObj.onerror = function (e) {
		console.log('链接出现异常');
		console.log(e)
		reconnectWebsocket()
	}
	wsObj.onclose = function (e) {
		console.log('链接关闭');
		console.log(e)
		reconnectWebsocket()
	}
	wsObj.onmessage = function (e) {
		console.log('收到新的消息');
		console.log(e.data);
		heartCheck.start()
		var str2 = e.data.split('--->>')[1];
		if(str2.indexOf('checkOnline')>-1){
			var str3 = str2.split('checkOnline,')[1];
			if(!str3){
				return
			}else{
				isAllNum = 0;// 收到刷新角标后，重置为0，重新计次
				var jsonMessage = eval("("+str3+")");
				setRedPoint(jsonMessage);
			}
		}else{
			isAllNum = 0;// 收到刷新角标后，重置为0，重新计次
			var jsonMessage = eval("("+str2+")");
			if(jsonMessage.data.bureau&&!jsonMessage.data.bureauIsSerf){ // 刷新办件
				refrashPageName='bureau'
			}else if(jsonMessage.data.feedback&&!jsonMessage.data.feedbackIsSerf){ // 刷新阅件
				refrashPageName='feedback'
			}else if(jsonMessage.data.unit&&!jsonMessage.data.unitIsSerf){// 刷新公文
				refrashPageName= 'unit'
			}
			setRedPoint(jsonMessage.count);
		}
	}
}
var refrashPageName = null
// 设置角标
var changNumData = {
	bureau:false,
	unit:false,
	feedback:false
}
function setRedPoint(data){
	changNumData = {
		bureau:false,
		unit:false,
		feedback:false
	}
	if(($(".grdb_num").length>0&&$(".grdb_num").text()!=data.getPersonTodoCount)){
		changNumData.bureau = true
	}
	if($(".jndb_num").length>0&&$(".jndb_num").text()!=data.getUnitTodoCount){
		changNumData.unit = true
	}
	if(data.blfkNum>0&&$('.blfk_num').length>0&&$('.blfk_num').is(':hidden')){
		changNumData.feedback = true
	}else if(data.blfkNum==0&&!$('.blfk_num').is(':hidden')){
		changNumData.feedback = true
	}

	if(data.getUnitTodoCount > 0 && data.getUnitTodoCount != null && data.getUnitTodoCount != "" && typeof(data.getUnitTodoCount) != undefined){
		$(".jndb_num").show();
		$('.jndb_num').text(data.getUnitTodoCount);
	}else{
		$(".jndb_num").hide();
		$('.jndb_num').text("");
	}
	if(data.getPersonTodoCount > 0 && data.getPersonTodoCount != null && data.getPersonTodoCount != "" && typeof(data.getPersonTodoCount) != undefined){
		$(".grdb_num").show();
		$('.grdb_num').text(data.getPersonTodoCount);
	}else{
		$(".grdb_num").hide();
		$('.grdb_num').text("");
	}
	if(data.blfkNum > 0 && data.blfkNum != null && data.total != "" && typeof(data.blfkNum) != undefined){
		$(".blfk_num").show();
//				$('.blfk_num').text(data.blfkNum);
		$('.blfk_num').text("");
	}else{
		$(".blfk_num").hide();
		$('.blfk_num').text("");
	}
	refrashPage();
	if(navigator.userAgent.indexOf('OfficeBrowser')>=0){
		gettop2().__set_todo_count__(parseInt(data.getPersonTodoCount)+(data.getUnitTodoCount));
	}
}

// 是否是需要刷新的页面
function isReloadHtml(){
	var htmlUrl = gettop2().iframe1.location.href;
	if((htmlUrl.indexOf('app/db/document/grdb/html/grdb.html')>-1&&(refrashPageName=='unit'||changNumData.unit))||
		(htmlUrl.indexOf('app/db/document/blfk/html/blfk.html')>-1&&(refrashPageName=='feedback'||changNumData.feedback))||
		(htmlUrl.indexOf('app/db/document/jndb/html/jndb.html')>-1&&(refrashPageName=='bureau'||changNumData.bureau))){
		return true
	}else{
		return false
	}
}
function getUserId() {
	$.ajax({
		url:'/app/db/adminset/getUserId',
		success:function(data){
			messageUserId = data.userId
			initWebSocket()
		}
	})
}
//刷新角标
function refrashPage(){
	if(isReloadHtml()&&reloadRedPoint){
		$('#timeLoading').html(timeLoadMs)
		clearInterval(timeLoadTime);
		$(".refreshTip").show();
		timeLoadTime = setInterval(function () {
			if(!reloadRedPoint){ // 在提示刷新流程中，点击了关闭
				clearInterval(timeLoadTime);
				timeLoadMs = 5;
				$(".refreshTip").hide()
				return
			}
			if(timeLoadMs < 2){
				clearInterval(timeLoadTime);
				timeLoadMs = 5;
				$(".refreshTip").hide();
				if(gettop2().iframe1){
					gettop2().iframe1.refreshgrid()
				}else{
					gettop2().refreshgrid()
				}
			}else{
				timeLoadMs--;
				$('#timeLoading').html(timeLoadMs)
			}
		},1000)
	}else{
		clearInterval(timeLoadTime);
		timeLoadMs = 5;
		$('#timeLoading').html(timeLoadMs)
		$(".refreshTip").hide();
	}
}
// 初始化websocket
// initWebSocket()
// initWebSocket()
function reconnectWebsocket() {
	if(lockReconnect){
		return;
	}
	lockReconnect = true;
	tW && clearTimeout(tW);
	tW = setTimeout(function(){
		initWebSocket();
		lockReconnect = false
	},4000)
}
var isAllNum = 0; // 心跳验证次数，每收到消息累加一次，收到6次，则清空为0
// 心跳检测机制
var heartCheck = {
	timeout: 50000, // 等待时间
	timeoutObj: null, //  发送时间
	serverTimeOutObj: null,
	start: function () {
		console.log('心跳检测开始！');
		var selfW = this;
		this.timeoutObj && clearTimeout(this.timeoutObj); // 清空定时器
		this.serverTimeOutObj && clearTimeout(this.serverTimeOutObj);//清空定时器
		hasMesssage = true
		this.timeoutObj = setTimeout(function(){ // 发送心跳检测
			isAllNum++;
			if(isAllNum == 6){
				isAllNum = 0
			}
			wsObj.send(`checkOnline,${messageUserId},${isAllNum==0?true:false}`);
			hasMesssage = false;
			this.serverTimeOutObj = setTimeout(function () { // 无反应后10s,关闭websocket 进行重连
				if(!hasMesssage){
					wsObj.close()
				}
			},selfW.timeout)
		},this.timeout)
	}
}

function resetRoad() {
	clearInterval(timeLoadTime);
	timeLoadMs = 5;
	$(".refreshTip").hide();
}
