<% ui.decorateWith("appui", "standardEmrPage") %>

<%= ui.resourceLinks() %>
<script type="text/javascript" src="/openmrs/ms/uiframework/resource/uicommons/scripts/datetimepicker/bootstrap-datetimepicker.min.js?cache=1525344062488"></script>
<link rel="stylesheet" href="/openmrs/ms/uiframework/resource/uicommons/styles/datetimepicker.css?cache=1525344062488" type="text/css" />
<% ui.includeJavascript("datamigration", "highcharts.js") %>
<% ui.includeJavascript("datamigration", "sunburst.js") %>
<h1 align="center"> <b>VISUALS DEMO</b></h1>

<style>
#container3 {
    height: 400px;
    max-width: 800px;
    margin: 0 auto;
}
.highcharts-background {
    opacity: 0.0;
}


</style>








<div class="container" style="padding-top: 10px;">
    <form method="post">
        <fieldset>

            <legend> Filter</legend>
            <div class="form-row">

                <input style="width: 10%;font-size: 16px; padding: 12px 20px 12px 40px; border: 1px solid #ddd; margin-bottom: 12px;" class="heading-text pull-left" type="date"    id="from"  placeholder="Form..">
                <input style="width: 10%;font-size: 16px; padding: 12px 20px 12px 40px; border: 1px solid #ddd; margin-bottom: 12px;" class="heading-text pull-left" type="date" id="to"  placeholder="to..">

                <button class="confirm button" id="filter" onclick="return false">Filter
                    <i class="icon-spinner icon-spin icon-2x"
                       style="display: none; margin-left: 10px;">

                    </i>
                </button>
            </div>
        </fieldset>
    </form>
</div>


<style>
#doit{
    background-image: url('${ ui.resourceLink("datamigration", "img/bulb.png")}');
    background-repeat: no-repeat;
    background-position: center;
    height: 800px;
}
#bulb{
    padding-top: 60px;
}
</style>



<div id="container" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
<br><br>
<form method="post">
    <fieldset>

        <legend> Filter</legend>
        <div class="form-row">

            <input style="width: 10%;font-size: 16px; padding: 12px 20px 12px 40px; border: 1px solid #ddd; margin-bottom: 12px;" class="heading-text pull-left" type="text"    id="target"  placeholder="Form.." value="1000">
            <button class="confirm button" id="filter2" onclick="return false">Filter
                <i class="icon-spinner icon-spin icon-2x"
                   style="display: none; margin-left: 10px;">

                </i>
            </button>
        </div>
    </fieldset>
</form>
<div id="container2" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
<br><br>
<div id="container3" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
<br><br>
<div id="container4" style="min-width: 310px; height: 400px; margin: 0 auto"></div>



<script>
    jq('#filter').click(function (e) {
        var startDatePeriod = jq('#from').val();
        var endDatePeriod = jq('#to').val();

        if( !startDatePeriod || !endDatePeriod){
            alert("Please supply valid values for start and End date ");
            return;
        }
        if(Date.parse(endDatePeriod) < Date.parse(startDatePeriod)){
            alert("Start date must be before the end date");
            return;
        }

        jq = jQuery;
        jq(function() {
            jq.ajax({
                url: "${ ui.actionLink("visualization", "visualization", "plotgetHIVPositveClientsGraph") }",
                dataType: "json",
                data: {
                    'startDatePeriod': startDatePeriod,
                    'endDatePeriod': endDatePeriod

                }


            }).success(function(data) {

                var  obj= jq.parseJSON(data);
                console.log(obj);
                plotgetHIVPositveClientsGraph(obj.categories, obj.series)
            }).error(function(xhr, status, err) {
                /* jq('#gen-wait').hide();
                 alert('An error occured');*/

            });

        })

    });

    jq('#filter2').click(function (e) {
        var target = jq('#target').val();
        jq = jQuery;
        jq(function() {
            jq.ajax({
                url: "${ ui.actionLink("visualization", "visualization", "getTxNewAchievments") }",
                dataType: "json",
                data: {
                    'target': target
                }

            }).success(function(data) {

                var  obj= jq.parseJSON(data);
                console.log(obj);
                plotTxNewAchievmentGraph(obj.categories, obj.series)
            }).error(function(xhr, status, err) {

            });

        })

    });

    jq(function() {
        jq.ajax({
            url: "${ ui.actionLink("visualization", "visualization", "getTxNewAchievments") }",
            dataType: "json",
            data: {
                'target': 1000
            }

        }).success(function(data) {

            var  obj= jq.parseJSON(data);
            console.log(obj);
            plotTxNewAchievmentGraph(obj.categories, obj.series)
        }).error(function(xhr, status, err) {

        });

        jq.ajax({
            url: "${ ui.actionLink("visualization", "visualization", "plotNewPatientVLCascade") }",
            dataType: "json",
            data: {
                'target': 1000
            }

        }).success(function(data) {

            var  obj= jq.parseJSON(data);
            console.log(obj);
            plotTxNewVLCascade(obj.categories, obj.series);
        }).error(function(xhr, status, err) {

        });

        jq.ajax({
            url: "${ ui.actionLink("visualization", "visualization", "plotNewPatientsCD4Analysis") }",
            dataType: "json",
            data: {
                'target': 1000
            }

        }).success(function(data) {

            var  obj= jq.parseJSON(data);
            console.log(obj);
            plotNewPatientsCD4Analysis(obj.categories, obj.series);
        }).error(function(xhr, status, err) {

        });

        jq.ajax({
            url: "${ ui.actionLink("visualization", "visualization", "getTxNewAchievments") }",
            dataType: "json",
            data: {
                'target': 1000
            }

        }).success(function(data) {

            var  obj= jq.parseJSON(data);
            console.log(obj);
            plotTxNewAchievmentGraph(obj.categories, obj.series)
        }).error(function(xhr, status, err) {

        });

    })
</script>

<script type="text/javascript">

    function plotgetHIVPositveClientsGraph(categories, series) {
        Highcharts.chart('container', {
            chart: {
                type: 'column'
            },
            title: {
                text: 'Test and Start Implementation'
            },
            xAxis: {
                categories: categories
            },
            yAxis: [{
                min: 0,
                title: {
                    text: '% of New ART Patients'
                },
                stackLabels: {
                    enabled: true,
                    style: {
                        fontWeight: 'bold',
                        color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
                    }
                }
            },
                { // Secondary yAxis
                    gridLineWidth: 0,
                    title: {
                        text: '',
                        style: {
                            color: Highcharts.getOptions().colors[0]
                        }
                    },
                    labels: {
                        format: '{value} %',
                        style: {
                            color: Highcharts.getOptions().colors[0]
                        }
                    }

                }
            ],
            legend: {
                align: 'center',
                x: 50,
                verticalAlign: 'bottom',
                y: 22,
                floating: false,
                backgroundColor: 'white',
                borderColor: '#fff',
                borderWidth: 0,
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
                        enabled: true,
                        color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white'
                    }
                }
            },
            series:[ {
                name: '>=8 Days',
                data: series[2].data,
                color:"#7030a0"
            }, {
                name: '1-7 Days',
                data: series[1].data,
                color:"#4472c4"
            }, {
                name: 'Same Day',
                data: series[0].data,
                color:"#000000"
            }]
        })
    }
    function plotTxNewVLCascade(categories, series) {
        Highcharts.chart('container3', {
            chart: {
                type: 'column'
            },
            title: {
                text: 'New Patients Viral Load Cascade'
            },
            subtitle: {
                text: ''
            },
            xAxis: {
                categories: categories,
                crosshair: true
            },
            yAxis: [
                {
                    min: 0,
                    tickWidth: 0,
                    crosshair: false,
                    lineWidth: 0,
                    gridLineWidth:0,//Set this to zero
                    title: {
                        text: 'Number of Patients '
                    },
                    stackLabels: {
                        enabled: false,
                        style: {
                            fontWeight: 'bold',
                            color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
                        }
                    }
                },
                {
                    title: {
                        text: '%Percentage'
                    },
                    minPadding: 0,
                    maxPadding: 0,
                    max: 100,
                    min: 0,
                    opposite: true,
                    labels: {
                        format: "{value}%"
                    }
                }

            ],
            tooltip: {
                headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                '<td style="padding:0"><b>{point.y:.1f} </b></td></tr>',
                footerFormat: '</table>',
                shared: true,
                useHTML: true
            },
            plotOptions: {
                column: {
                    // stacking: 'normal',
                    dataLabels: {
                        enabled: false,
                        color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white'
                    }
                }

                /* column: {
                     pointPadding: 0.2,
                     borderWidth: 0
                 }*/
            },
            series: [{
                name: 'New on ART',
                data: series[0].data,
                color: '#000',

            },
                {
                    name: 'Result <1,000 copies/ml',
                    data: series[2].data,
                    color: '#b4c7e7', stacking: 'New Positive'

                }, {
                    name: 'Results ≥1,000 copies/ml',
                    data: series[3].data,
                    color: '#e905bf', stacking: 'New Positive'

                },
                {
                    type: 'scatter',
                    yAxis: 1,
                    name: 'Viral Coverage',
                    data: series[4].data,
                    marker: {
                        radius: 4
                    }
                },{
                    type: 'scatter',
                    yAxis: 1,
                    name: 'Viral Load Suppression',
                    data: series[5].data,
                    marker: {
                        radius: 4
                    }
                }
            ]
        });
    }

    function plotNewPatientsCD4Analysis(categories, series) {
        Highcharts.chart('container4', {
            chart: {
                type: 'column'
            },
            title: {
                text: 'New Patients CD4 Analysis'
            },
            subtitle: {
                text: ''
            },
            xAxis: {
                categories: categories,
                crosshair: true
            },
            yAxis: [
                {
                    min: 0,
                    title: {
                        text: 'Number of Patients '
                    },
                    stackLabels: {
                        enabled: false,
                        style: {
                            fontWeight: 'bold',
                            color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
                        }
                    }
                },
                {
                    title: {
                        text: '%Percentage'
                    },
                    minPadding: 0,
                    maxPadding: 0,
                    max: 100,
                    min: 0,
                    opposite: true,
                    labels: {
                        format: "{value}%"
                    }
                }

            ],
            tooltip: {
                headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                '<td style="padding:0"><b>{point.y:.1f} </b></td></tr>',
                footerFormat: '</table>',
                shared: true,
                useHTML: true
            },
            plotOptions: {
                column: {
                    stacking: 'normal',
                    dataLabels: {
                        enabled: false,
                        color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white'
                    }
                }

                /* column: {
                     pointPadding: 0.2,
                     borderWidth: 0
                 }*/
            },
            series: [{
                name: 'New on ART',
                color: '#000',
                data: series[0].data

            },
                {
                    name: 'CD4 count <200 cells/ml',
                    color: '#b4c7e7', stacking: 'New Positive',
                    data: series[1].data

                },
                {
                    name: 'CD4 count ≥200 cells/ml',
                    data: series[2].data,
                    color: '#e905bf', stacking: 'New Positive'

                },
                {
                    type: 'scatter',
                    yAxis: 1,
                    name: 'Proportion',
                    data: series[3].data,
                    marker: {
                        radius: 4
                    }
                },{
                    type: 'scatter',
                    yAxis: 1,
                    name: '%<200',
                    color: '#b4c7e7',
                    data: series[4].data,
                    marker: {
                        radius: 4
                    }
                }
            ]
        });
    }

    function plotTxNewAchievmentGraph(categories, series) {
        console.log(series[0].data)
        Highcharts.chart('container2', {
            chart: {
                // zoomType: 'xy'
            },
            title: {
                text: 'Number of Patients Newly Started on ART'
            },
            subtitle: {
                text: ''
            },
            xAxis: [{
                categories: categories,
                crosshair: true
            }],
            yAxis: [{ // Primary yAxis
                max: 1000,
                tickWidth: 0,
                crosshair: false,
                lineWidth: 0,
                gridLineWidth:0,//Set this to zero
                labels: {
                    format: '{value}',
                    enabled: true,
                    style: {
                        color: Highcharts.getOptions().colors[1]
                    }
                },
                title: {
                    text: 'Number of pateints',
                    style: {
                        color: Highcharts.getOptions().colors[1]
                    }
                }
            },
                {
                    title: {
                        text: '%Percentage'
                    },
                    minPadding: 0,
                    maxPadding: 0,
                    max: 100,
                    min: 0,
                    opposite: true,
                    labels: {
                        format: "{value}%"
                    }
                }
            ],
            tooltip: {
                shared: true
            },
            plotOptions: {
                column: {
                    grouping: false,
                    shadow: false,
                    dataLabels: {
                        enabled: true,
                        color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white'
                    }
                }
            },
            legend: {
                align: 'center',
                x: 50,
                verticalAlign: 'bottom',
                y: 22,
                floating: false,
                backgroundColor: 'white',
                borderColor: '#fff',
                borderWidth: 0,
                shadow: false
                // backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || 'rgba(255,255,255,0.25)'
            },
            series: [{
                name: 'Cumulative Expected Monthly Result',
                type: 'column',
                data: series[1].data,
                pointPadding: 0,
                color: '#000'

            },

                {
                    name: 'Cumulative Actual Monthly Result',
                    type: 'column',
                    data: series[0].data,
                    pointPadding: 0.1,
                    color: '#b4c7e7'

                }, {
                    name: 'Annual Facility Targe',
                    type: 'spline',
                    dashStyle: 'shortdot',
                    data: series[3].data,
                    marker: {
                        enabled: false
                    },
                    tooltip: {
                        valueSuffix: ''
                    }

                }, {
                    name: '% Cumulative Achievement',
                    type: 'spline',
                    yAxis: 1,
                    data: series[2].data,
                    color: '#df7f7f',
                    tooltip: {
                        valueSuffix: '%'
                    }

                }

            ]
        });
    }

</script>
