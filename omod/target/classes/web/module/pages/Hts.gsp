<% ui.decorateWith("appui", "standardEmrPage") %>

<%= ui.resourceLinks() %>
<% ui.includeJavascript("visualization", "highcharts.js") %>

<h1 align="center"> <b>VISUALS DEMO</b></h1>

<div id="container2" style="min-width: 310px; height: 400px; max-width: 600px; margin: 0 auto"></div>
<br/>
<div id="container" style="min-width: 310px; height: 400px; margin: 0 auto"></div>

<style>
#container3 {
    height: 400px;
    max-width: 800px;
    margin: 0 auto;
}
/* Link the series colors to axis colors */
.highcharts-color-0 {
    fill: #7cb5ec;
    stroke: #7cb5ec;
}
.highcharts-axis.highcharts-color-0 .highcharts-axis-line {
    stroke: #7cb5ec;
}
.highcharts-axis.highcharts-color-0 text {
    fill: #7cb5ec;
}
.highcharts-color-1 {
    fill: #90ed7d;
    stroke: #90ed7d;
}
.highcharts-axis.highcharts-color-1 .highcharts-axis-line {
    stroke: #90ed7d;
}
.highcharts-axis.highcharts-color-1 text {
    fill: #90ed7d;
}
.highcharts-yaxis .highcharts-axis-line {
    stroke-width: 2px;
}
</style>
<script type="text/javascript">
    // Create the chart
    Highcharts.chart('container', {
        chart: {
            type: 'column'
        },
        title: {
            text: 'Disaggregation by Program area'
        },
        subtitle: {
            text: 'Program areas'
        },
        xAxis: {
            type: 'category'
        },
        yAxis: {
            title: {
                text: 'Total percent affected'
            }
        },
        legend: {
            enabled: false
        },
        plotOptions: {
            series: {
                borderWidth: 0,
                dataLabels: {
                    enabled: true,
                    format: '{point.y:.1f}%'
                }
            }
        },
        tooltip: {
            headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
            pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y:.2f}%</b> of total<br/>'
        },
        series: [
            {
                name: "Program Area",
                colorByPoint: true,
                data: [
                    {
                        name: "PMTCT",
                        y: 62.74,
                        drilldown: "PMTCT"
                    },
                    {
                        name: "ART",
                        y: 40.57,
                        drilldown: "ART"
                    },
                    {
                        name: "HTS",
                        y: 17.23,
                        drilldown: "HTS"
                    },
                    {
                        name: "Others",
                        y: 7.62,
                        drilldown: null
                    }
                ]
            }
        ],
        drilldown: {
            series: [
                {
                    name: "PMTCT",
                    id: "PMTCT",
                    data: [
                        [
                            "v65.0",
                            0.1
                        ],
                        [
                            "v64.0",
                            1.3
                        ],
                        [
                            "v63.0",
                            53.02
                        ],
                        [
                            "v62.0",
                            1.4
                        ],
                        [
                            "v61.0",
                            0.88
                        ],
                        [
                            "v60.0",
                            0.56
                        ],
                        [
                            "v59.0",
                            0.45
                        ],
                        [
                            "v58.0",
                            0.49
                        ],
                        [
                            "v57.0",
                            0.32
                        ],
                        [
                            "v56.0",
                            0.29
                        ],
                        [
                            "v55.0",
                            0.79
                        ],
                        [
                            "v54.0",
                            0.18
                        ],
                        [
                            "v51.0",
                            0.13
                        ],
                        [
                            "v49.0",
                            2.16
                        ],
                        [
                            "v48.0",
                            0.13
                        ],
                        [
                            "v47.0",
                            0.11
                        ],
                        [
                            "v43.0",
                            0.17
                        ],
                        [
                            "v29.0",
                            0.26
                        ]
                    ]
                },
                {
                    name: "ART",
                    id: "ART",
                    data: [
                        [
                            "v58.0",
                            1.02
                        ],
                        [
                            "v57.0",
                            7.36
                        ],
                        [
                            "v56.0",
                            0.35
                        ],
                        [
                            "v55.0",
                            0.11
                        ],
                        [
                            "v54.0",
                            0.1
                        ],
                        [
                            "v52.0",
                            0.95
                        ],
                        [
                            "v51.0",
                            0.15
                        ],
                        [
                            "v50.0",
                            0.1
                        ],
                        [
                            "v48.0",
                            0.31
                        ],
                        [
                            "v47.0",
                            0.12
                        ]
                    ]
                },
                {
                    name: "HTS",
                    id: "HTS",
                    data: [
                        [
                            "v16",
                            2.6
                        ],
                        [
                            "v15",
                            0.92
                        ],
                        [
                            "v14",
                            0.4
                        ],
                        [
                            "v13",
                            0.1
                        ]
                    ]
                }
            ]
        }
    });
    Highcharts.chart('container2', {
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false,
            type: 'pie'
        },
        title: {
            text: 'Disaggregation by Sex'
        },
        tooltip: {
            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: true,
                    format: '<b>{point.name}</b>: {point.percentage:.1f} %'
                }
            }
        },
        series: [{
            name: 'Percentage Affected',
            colorByPoint: true,
            data: [{
                name: 'Male',
                y: 61.41,
                sliced: true,
                selected: true
            }, {
                name: 'Female',
                y: 39.59
            }]
        }]
    });
</script>

<div id="container3"></div>


<script type="text/javascript">
    Highcharts.chart('container3', {
        chart: {
            type: 'column',
            styledMode: true
        },
        title: {
            text: 'Styling axes and columns'
        },
        yAxis: [{
            className: 'highcharts-color-0',
            title: {
                text: 'Primary axis'
            }
        }, {
            className: 'highcharts-color-1',
            opposite: true,
            title: {
                text: 'Secondary axis'
            }
        }],
        plotOptions: {
            column: {
                borderRadius: 5
            }
        },
        series: [{
            data: [1, 3, 2, 4]
        }, {
            data: [324, 124, 547, 221],
            yAxis: 1
        }]
    });
</script>