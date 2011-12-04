$(document).ready(function() {
    $("input:checkbox").click(function() {
        var checkedSeverities = [];

        $.each($("input:checkbox:checked"), function(index, value) {
            checkedSeverities.push(value.name);
        });

        $.post("#", {"action": "applyFilter", "checkedSeverities": checkedSeverities}, function(response) {
            location.reload();
        }, "json");
    });
});
