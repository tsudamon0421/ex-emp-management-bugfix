$(function() {
	// [住所取得ボタンクリックで非同期処理開始]
	$("#get_address_btn").on("click", function() {
		$.ajax({
			url : "https://zipcoda.net/api",
			dataType : "jsonp",
			data : {
				zipcode : $("#zipcode").val()
			},
			async : true
		}).done(function(data) {
			console.dir(JSON.stringify(data));
			$("#address").val(data.items[0].address);// 住所欄に住所をセット
		}).fail(function(XMLHttpRequest, textStatus, errorThrown) {
			alert("正しい結果を得られませんでした");
			console.log("XMLHttpRequest:" + XMLHttpRequest.status);
			console.log("textStatus    :" + textStatus);
			console.log("errorThrown   :" + errorThrown.message);
		});
	});
	/**
	 * 画像を表示するためのjs
	 */

	$('#image').on('change', function(e) {
		var reader = new FileReader();
		reader.onload = function(e) {
			$("#preview").attr('src', e.target.result);
		}
		reader.readAsDataURL(e.target.files[0]);
	});
});
