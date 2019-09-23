<%=  ui.resourceLinks() %>

<div id="container" style="min-width: 310px; height: 400px; margin: 0 auto"></div>

<br>
<br>
<div id="cohort" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
<script type="text/javascript">
    jq = jQuery;

    jq(document).ready(function()
    {
        getAncPmtctArt()
    });

    function getAncPmtctArt()
    {
        var link = "${ ui.actionLink("visualization", "pmtct", "getAncPmtctArt")}";
        jq.ajax({
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            url: link,
            cache: false,
            timeout: 600000,
            success: function (r)
            {
                var pData =[];
                var categories = [];
                var colors = ['#95CEFF', '#50B432', '#24CBE5','#ff7043', '#DDDF00', '#24CBE5',
                    '#64E572', '#FF9655', '#FFF263', '#6AF9C4'];
                jq.each(r, function (i, f)
                {
                    pData.push(f.value);
                    categories.push(f.name);
                });
                Highcharts.chart('container',
                {
                    chart: {
                        type: 'column'
                    },
                    title: {
                        text: 'ANC with prev/new HIV+ status and on ART '
                    },
                    xAxis: {
                        categories: categories
                    },
                    yAxis: {
                        allowDecimals: false,
                        min: 0,
                        title: {
                            text: 'Count'
                        }
                    },
                    tooltip: {
                        headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                        pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                            '<td style="padding:0"><b>{point.y:.0f}</b></td></tr>',
                        footerFormat: '</table>',
                        shared: true,
                        useHTML: true
                    },
                    plotOptions: {
                        column: {
                            pointPadding: 0.02,
                            borderWidth: 0,
                            colorByPoint: true
                        }
                    },
                    colors: colors
                    ,
                    series: [{
                        name: 'Patients',
                        data: pData

                    }]
                });

                getPmtctCohortRetention();
            },
            error: function (e)
            {

            }
        });
    }

    function getPmtctCohortRetention()
    {
        var link = "${ ui.actionLink("visualization", "pmtct", "getPmtctCohortRetention")}";
        jq.ajax({
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            url: link,
            cache: false,
            timeout: 600000,
            success: function (r)
            {
                var pData =[];
                var categories = [];
                jq.each(r, function (i, f)
                {
                    var dx = [];
                    pData.push([f.active_0, f.active_3, f.active_6, f.active_12]);
                    categories.push({name: f.cohort, data: dx});
                });
                Highcharts.chart('cohort',
                    {
                    chart: {
                        type: 'column'
                    },
                    title: {
                        text: 'Current PMTCT Cohort Retention'
                    },
                    xAxis: {
                        categories: categories
                    },
                    yAxis: {
                        min: 0,
                        title: {
                            text: 'Clients'
                        },
                        stackLabels: {
                            enabled: true,
                            style: {
                                fontWeight: 'bold',
                                color: ( // theme
                                    Highcharts.defaultOptions.title.style &&
                                    Highcharts.defaultOptions.title.style.color
                                ) || 'gray'
                            }
                        }
                    },
                    legend: {
                        align: 'right',
                        x: -30,
                        verticalAlign: 'top',
                        y: 25,
                        floating: true,
                        backgroundColor:
                            Highcharts.defaultOptions.legend.backgroundColor || 'white',
                        borderColor: '#CCC',
                        borderWidth: 1,
                        shadow: false
                    },
                    tooltip: {
                        headerFormat: '<b>{point.x}</b><br/>',
                        pointFormat: '{series.name}: {point.y}<br/>Total: {point.stackTotal}'
                    },
                    plotOptions: {
                        column: {
                            stacking: 'normal',
                            dataLabels: {
                                enabled: true
                            }
                        }
                    },
                    series: pData
                });
            },
            error: function (e)
            {

            }
        });
    }
</script>
