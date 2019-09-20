<p>Line List Fragment</p>

<script>
    jq = jQuery;
    jq.getJSON('${ ui.actionLink("getLineList") }')
        .success(function(data) {
            console.log(data);
        }).error(function(xhr, status, err) {
        console.log(err)
    })
</script>