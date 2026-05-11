$(() => {
    const searchTrackingModal = $("#modalContainer")
    // 배송추적 모달 열기
    $(".searchTracking").on("click", () => {
        console.log("버튼 눌림");
        searchTrackingModal.removeClass("hidden");
        document.body.style.overflow = 'hidden';
        var t_key = "xpzQhmiulhxaYdHfd4ml0g";
        var t_code = "04";
        var t_invoice = document.querySelector('#trackingNumber').value;
        console.log(t_invoice);
        $.ajax({
            type : "GET",
            dataType : "json",
            url : "https://info.sweettracker.co.kr/api/v1/trackingInfo?t_key="+t_key+"&t_code="+t_code+"&t_invoice="+t_invoice,
            success:function (data){
                console.log(data);
                var header = "";
                var trackingData = "";
                var trackingDetails = data.trackingDetails;
                if(data.status == false){
                    $("#msg").html('<p>'+data.msg+'</p>');
                } else{
                    header += ('<tr>')
                    header += ('<th>배송시간</th>');
                    header += ('<th>현재위치</th>');
                    header += ('<th>배송상태</th>');
                    header += ('</tr>')

                    $.each(trackingDetails, function (key,value){
                        trackingData += ('<tr>')
                        trackingData += ('<td>'+value.timeString+'</td>');
                        trackingData += ('<td>'+value.where+'</td>');
                        trackingData += ('<td>'+value.kind+'</td>');
                        trackingData += ('</tr>')
                    })
                    $("#deliveryCompany").text("택배사 CJ대한통운")
                    $("#trackingData").html(header + trackingData);
                }
            }
        })
    });
    $(".modalCloseBtn").on("click", () => {
        searchTrackingModal.addClass("hidden");
        document.body.style.overflow = "auto";
    });

})