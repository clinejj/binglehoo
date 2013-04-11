// JavaScript Document

function doSearch(form) {
    //document.getElementById("gobtn").disabled = true;
    query = $.trim(form.query.value);
		query = "&query=" +  encodeURIComponent(query)
		$.ajax({
				type: 'post',
				url: '/binglehoo',
				data: query,
				dataType: 'text',
				success: function (data) {
					$('#results').html(data);
				},
				error: function (data) {
						errfunc(data);
				}
		});
}

function errfunc(data) {
    console.log(data);
}