<% ui.decorateWith("appui", "standardEmrPage") %>

<%= ui.resourceLinks() %>
<% ui.includeJavascript("visualization", "highcharts.js") %>

<h1 align="center"> <b>HTS</b></h1>

<div id="clients" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
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
            PlotHtsChart(data);
        }).error(function (err) {
            console.log(err)
        });
    });

    function PlotHtsChart(data) {
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
                text: 'HTS 1'
            }/*,
                subtitle: {
                    text: 'Source: WorldClimate.com'
                }*/,
            xAxis: {
                categories: category,
                crosshair: true
            },
            yAxis: {
                min: 0,
                title: {
                    text: '(mm)'
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
                categories: [fac_model.pos_name, "Started Art"]
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
                name: fac_model.pos_name,
                data: [fac_model.pos_count,0]
            }, {
                name: fac_model.start_art_in,
                data: [0, fac_model.in_count]
            },{
                name:fac_model.start_art_out,
                data:[0, fac_model.out_count]
            }]
        });
    }
</script>
