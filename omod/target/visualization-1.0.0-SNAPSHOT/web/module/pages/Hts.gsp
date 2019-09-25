<% ui.decorateWith("appui", "standardEmrPage") %>

<%= ui.resourceLinks() %>
<% ui.includeJavascript("visualization", "highcharts.js") %>

<h1 align="center"> <b>HTS</b></h1>
<div>
    <input type="date" id="start_date" placeholder="Start Date">&nbsp;<input type="date" id="end_date" placeholder="End Date">
    <span class="button confirm" onclick="getChartsByDate()"><i class="icon-refresh"></i></span>
</div>
<div id="clients" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
<br/>
<div id="facility" style="min-width: 310px; height: 400px; margin: 0 auto"></div>

<script type="text/javascript">
    jq = jQuery;
    jq(document).ready(function(){

    });

    function getChartsByDate(){
        var start_date = document.getElementById("start_date").value;
        var end_date = document.getElementById("end_date").value;
        if(start_date === "" || end_date === ""){
            alert("Please Ensure you pick a date");
            return false;
        }
        console.log(start_date + " => " + end_date);
        jq.ajax({
            url: "${ ui.actionLink("visualization", "Hts", "getClientData")}",
            dataType:"json",
            data: {'start_date':start_date, 'end_date':end_date}
        }).success(function (data) {
            //plot the chart here
            PlotHtsChart(data);
        }).error(function (err) {
            console.log(err)
        });
    }

    function PlotHtsChart(data) {
        var colors = ['#030508', '#7cacc2', '#80699B', '#ff4344', '#ff741e', '#cc7a34', '#34eada'];
        var columnData = data.barChartModels;
        var category = [];
        var values = [];
        var valuesName = "HIV Testing Services Cascade";
        for(var i = 0; i < columnData.length; i++){
            category.push(columnData[i].name);
            values.push(columnData[i].y);
        }
        var theSeries = {name: valuesName, data: values};
        Highcharts.chart('clients', {
            chart: {
                type: 'column'
            },
            title: {
                text: 'HIV Testing Services Cascade'
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
                    text: 'Number of Clients'
                }
            },
            colors: colors,
            tooltip: {
                headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                    '<td style="padding:0"><b>{point.y:.1f}</b></td></tr>',
                footerFormat: '</table>',
                shared: true,
                useHTML: true
            },
            plotOptions: {
                column: {
                    pointPadding: 0.2,
                    borderWidth: 0,
                    colorByPoint: true
                }
            },
            series: [ theSeries ]
        });
        var fac_model = data.chartModel;
        var colors = ['#030508', '#7cacc2', '#80699B'];
        Highcharts.chart('facility', {
            chart: {
                type: 'column'
            },
            title: {
                text: 'Linkage to Treatment'
            },
            xAxis: {
                categories: [fac_model.pos_name, "Started Art"]
            },
            yAxis: {
                min: 0,
                title: {
                    text: 'Number of CLients'
                }
            },
            tooltip: {
                pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y}</b> ({point.percentage:.0f}%)<br/>',
                shared: true
            },
            plotOptions: {
                column: {
                    stacking: 'percent',
                    colorByPoint: true
                }
            },
            colors: colors,
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
