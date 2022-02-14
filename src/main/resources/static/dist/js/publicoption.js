// jQuery

$(function() {
    "use strict";

    var mail = $('.email-table .max-texts a');

    // Highlight row when checkbox is checked
    $('.publicoption-list-box').find('.publicoption-chb').find('input[type=checkbox]').on('change', function() {
        if ($(this).is(':checked')) {
            $(this).parents('.publicoption-list-box').addClass('selected');
        } else {
            $(this).parents('.publicoption-list-box').removeClass('selected');
        }
    });

    $(".sl-all").on('click', function() {
        $('.publicoption-chb input:checkbox').not(this).prop('checked', this.checked);
        if ($('.publicoption-chb input:checkbox').is(':checked')) {
            $('.publicoption-chb input:checkbox').parents('.publicoption-list-box').addClass('selected');
        } else {
            $('.publicoption-chb input:checkbox').parents('.publicoption-list-box').removeClass('selected');
        }
    });

});
function adddata(){
	let eventname =$("#publicoption_eventname").val();
 	console.info(eventname);
 	let eventkeywords =$("#publicoption_eventkeywords").val();
 	let eventstarttime =$("#publicoption_eventstarttime").val();
 	let eventendtime =$("#publicoption_eventendtime").val();
 	let eventstopwords =$("#publicoption_eventstopwords").val();
 	if(eventname==null||eventname==""){
 		showtips("请输入舆情研判任务名称！");
    	return;
 	}
 	if(eventstarttime==null||eventstarttime==""){
 		showtips("请选择任务开始时间！");
    	return;
 	}
 	if(eventendtime==null||eventendtime==""){
 		showtips("请选择任务结束时间！");
    	return;
 	}
 	if(eventkeywords==null||eventkeywords==""){
 		showtips("请输入事件涉及词！");
    	return;
 	}
 	
 	
 	$.ajax({
		url : ctxPath + 'publicoption/addpublicoptiondata',
		type : 'post',
		dataType : 'json',
		data : {
			eventname:eventname,
			eventkeywords:eventkeywords,
			eventstarttime:eventstarttime,
			eventendtime:eventendtime,
			eventstopwords:eventstopwords
			},
		success : function(res) {
			let status = res.status;
			if(status==200){
				$("#confirmsure").remove()
				$.blockUI({
		            message: "创建成功",
		            fadeIn: 700,
		            fadeOut: 700,
		            timeout: 3000,
		            showOverlay: false,
		            centerY: false,
		            css: {
		                width: '250px',
		                top: '20px',
		                left: '',
		                right: '20px',
		                border: 'none',
		                padding: '15px 5px',
		                backgroundColor: '#000',
		                '-webkit-border-radius': '10px',
		                '-moz-border-radius': '10px',
		                opacity: 0.9,
		                color: '#fff'
		            }
		        });
    setTimeout(() => {
    	window.location.href = ctxPath + 'publicoption';
    }, 1000);
				
			}
			
		},
		error : function(xhr, ajaxOptions, thrownError) {
			console.log(xhr);
			if (xhr.status == 403) {
				window.location.href = ctxPath + "login";
			}
		}
});
   
}

function createEvent(param) { 
    var eventHtml =
       '<div class="shadebox" id="confirmsure">'+
       '    <div class="showCreateEvent"> '+
       '        <div class="event-box">'+
       '            <div class="event-box-header"> '+
       '                <h5 class="modal-title">创建事件分析任务</h5>'+
       '                <i class="mdi mdi-close-circle-outline font-18 cursor-po" id="closethis"></i>'+
       '            </div>'+
       '            <div class="event-box-body">'+
       '                <div class="event-line">'+
       '                    <div><span>*</span>事件分析任务名称</div>'+
       '                    <div class="input-group">'+
       '                        <input type="text" class="form-control" id="publicoption_eventname" placeholder="请设置导出任务名称" maxlength="15"/>'+
       '                    </div>'+
       '                </div>'+
       '                <div class="event-line">'+
       '                    <div><span>*</span>任务时间段</div>'+
       '                    <div class="input-group event-time-box">'+
       '                        <input type="date" id="publicoption_eventstarttime" class="form-control">'+
       '                        <input type="date" id="publicoption_eventendtime" class="form-control">'+
       '                    </div>'+
       '                </div>'+
       '                <div class="event-line">'+
       '                    <div><span>*</span>事件涉及词</div>'+
       '                    <div class="input-group">'+
       '                        <textarea class="form-control" name="keywords" id="publicoption_eventkeywords" cols="" rows="4" placeholder="关键词之间请用以下“+”、“|”、“（”、“）"隔开"></textarea>'+
       '                    </div>'+
       '                </div>'+
       '                <div class="event-line">'+
       '                    <div>事件屏蔽词</div>'+
       '                    <div class="input-group">'+
       '                        <textarea class="form-control" name="keywords" id="publicoption_eventstopwords" cols="" rows="4" placeholder="屏蔽词之间请用以下“+”、“|”、“（”、“）"隔开"></textarea>'+
       '                    </div>'+
       '                </div>'+
       '            </div>'+
       '            <div class="modal-footer no-border">'+
       '                <button type="button" class="btn btn-info" id="confirm"> 确定</button> '+
       '                <button type="button" class="btn btn-secondary" id="cancel">取消</button>'+
       '            </div>'+
       '        </div>'+
       '    </div>'+
       '</div>'

   $("body").append(eventHtml)
   $("#closethis").click(function (param) { 
       $("#confirmsure").remove()
    })
    $("#confirm").click(function (param) {
   	 adddata();
    })
    $("#cancel").click(function (param) { 
       $("#confirmsure").remove()
    })  
 }