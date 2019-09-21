<% ui.decorateWith("appui", "standardEmrPage") %>

<%= ui.resourceLinks() %>
<% ui.includeJavascript("visualization", "highcharts.js") %>

<h1 align="center"> <b>HTS</b></h1>

<div id="clients" style="min-width: 310px; height: 400px; max-width: 600px; margin: 0 auto"></div>
<br/>
<div id="facility" style="min-width: 310px; height: 400px; margin: 0 auto"></div>



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
            /*jq("#datatable tbody").empty();
            for (var i = 0; i < data.length; i++){
                jq("#datatable tbody").append("<tr />");
                jq(jq("#datatable tbody tr")[i]).append("<td>"+data[i].name+"</td><td>"+data[i].y+"</td>");
            }*/
            /*Highcharts.chart('clients', {
                chart: {
                    type: 'column'
                },
                title: {
                    text: 'HTS 1'
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
                },
                series :[
                    {data: data.barChartModels}
                    ]
            });*/
            var columnData = data.barChartModels;
            var category = [];
            var values = [];
            var valuesName = "HTS 1";
            for(var i = 0; i < columnData.length; i++){
                category.push(columnData[i].name);
                values.push(columnData[i].y);
            }
            var theSeries = {name: valuesName, data: values}
            Highcharts.chart('clients', {
                chart: {
                    type: 'column'
                },
                title: {
                    text: 'Monthly Average Rainfall'
                },
                subtitle: {
                    text: 'Source: WorldClimate.com'
                },
                xAxis: {
                    categories: category,
                    crosshair: true
                },
                yAxis: {
                    min: 0,
                    title: {
                        text: 'Rainfall (mm)'
                    }
                },
                tooltip: {
                    headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                    pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                        '<td style="padding:0"><b>{point.y:.1f} mm</b></td></tr>',
                    footerFormat: '</table>',
                    shared: true,
                    useHTML: true
                },
                plotOptions: {
                    column: {
                        pointPadding: 0.2,
                        borderWidth: 0
                    }
                },
                series: [ theSeries ]
            });

            var fac_model = data.chartModel;
            Highcharts.chart('facility', {
                chart: {
                    type: 'column'
                },
                title: {
                    text: 'HTS 2'
                },
                xAxis: {
                    categories: [data.pos_name, data.start_art_in, data.start_art_out]
                },
                yAxis: {
                    min: 0,
                    title: {
                        text: ''
                    }
                },
                tooltip: {
                    pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y}</b> ({point.percentage:.0f}%)<br/>',
                    shared: true
                },
                plotOptions: {
                    column: {
                        stacking: 'percent'
                    }
                },
                series: [{
                    name: 'New HIV Positive Client',
                    data: [data.pos_count, 0]
                }, {
                    name: 'Started Art',
                    data: [data.start_art_in, data.start_art_out]
                }]
            });
        }).error(function (err) {
            console.log(err)
        });
    });
</script>
