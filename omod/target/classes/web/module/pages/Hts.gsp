<% ui.decorateWith("appui", "standardEmrPage") %>

<%= ui.resourceLinks() %>
<% ui.includeJavascript("visualization", "highcharts.js") %>

<h1 align="center"> <b>HTS</b></h1>

<div id="clients" style="min-width: 310px; height: 400px; max-width: 600px; margin: 0 auto"></div>
<br/>
<div id="facility" style="min-width: 310px; height: 400px; margin: 0 auto"></div>

<table id="datatable">
    <thead>
    <tr>
        <th>Indicator</th>
        <th>Count</th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <th>Apples</th>
        <td>3</td>
    </tr>

    </tbody>
</table>


<script type="text/javascript">

</script>
<script type="text/javascript">
    jq = jQuery;
    jq(document).ready(function(){
        jq.ajax({
            url: "${ ui.actionLink("visualization", "Hts", "getClientData")}",
            dataType:"json"
        }).success(function (data) {
            //plot the chart here
            jq("#datatable tbody").empty();
            for (var i = 0; i < data.length; i++){
                jq("#datatable tbody").append("<tr />");
                jq(jq("#datatable tbody tr")[i]).append("<td>"+data[i].name+"</td><td>"+data[i].y+"</td>");
            }
            Highcharts.chart('clients', {
                data: {
                    table: 'datatable'
                },
                chart: {
                    type: 'column'
                },
                title: {
                    text: 'Data extracted from a HTML table in the page'
                },
                yAxis: {
                    allowDecimals: false,
                    title: {
                        text: 'Units'
                    }
                },
                tooltip: {
                    formatter: function () {
                        return '<b>' + this.series.name + '</b><br/>' +
                            this.point.y + ' ' + this.point.name.toLowerCase();
                    }
                }
            });
        }).error(function (err) {
            console.log(err)
        });
    });
</script>
