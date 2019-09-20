<%=  ui.resourceLinks() %>

<h1 align="center"> <b>VISUALS DEMO</b></h1>

<div id="container2" style="min-width: 310px; height: 400px; max-width: 600px; margin: 0 auto"></div>
<br/>
<div id="container" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
<div id="container3"></div>

<script type="text/javascript">
    jq = jQuery;
    jq(document).ready(function(){
        jq.ajax({
            url: "${ ui.actionLink("visualization", "pmtct", "getPmtctData")}",
            dataType:"json"
        }).success(function (data) {
            debugger;
            console.log(data)
        }).error(function (err) {
            debugger;
            console.log(err)
        });
    });
</script>
