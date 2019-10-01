<% ui.decorateWith("appui", "standardEmrPage") %>

<%= ui.resourceLinks() %>
<script type="text/javascript" src="/openmrs/ms/uiframework/resource/uicommons/scripts/datetimepicker/bootstrap-datetimepicker.min.js?cache=1525344062488"></script>
<link rel="stylesheet" href="/openmrs/ms/uiframework/resource/uicommons/styles/datetimepicker.css?cache=1525344062488" type="text/css" />
<% ui.includeJavascript("datamigration", "highcharts.js") %>
<% ui.includeJavascript("datamigration", "sunburst.js") %>
${ui.includeFragment("visualization","header")}
<h1 align="center"> <b>TX_New Indicators</b></h1>

<style>
#container3 {
    height: 400px;
    max-width: 800px;
    margin: 0 auto;
}
.highcharts-background {
    opacity: 0.0;
}
#doit{
    background-image: url('${ ui.resourceLink("datamigration", "img/bulb.png")}');
    background-repeat: no-repeat;
    background-position: center;
    height: 800px;
}
#bulb{
    padding-top: 60px;
}

/* Style the tab */
.tab {
    overflow: hidden;
    border: 1px solid #ccc;
    background-color: #f1f1f1;
}

/* Style the buttons that are used to open the tab content */
.tab button {
    background-color: inherit;
    float: left;
    border: none;
    outline: none;
    cursor: pointer;
    padding: 14px 16px;
    transition: 0.3s;
}

/* Change background color of buttons on hover */
.tab button:hover {
    background-color: #ddd;
}

/* Create an active/current tablink class */
.tab button.active {
    background-color: #ccc;
}

/* Style the tab content */
.tabcontent {
    display: none;
    padding: 6px 12px;
    border: 1px solid #ccc;
    border-top: none;
}
.highcharts-data-labels{
    opacity: 0;
}
</style>

<div class="tab">
    <button class="tablinks" onclick="openCity(event, 't1')">HIV Positive Clients </button>
    <button class="tablinks" onclick="openCity(event, 't2')">Tx_New Achievement</button>
    <button class="tablinks" onclick="openCity(event, 't3')">Tx_New Viral Load Cascade</button>
    <button class="tablinks" onclick="openCity(event, 't4')">Tx_New Patients CD4 Analysis</button>
    <button class="tablinks" onclick="openCity(event, 't5')">Missed Appointments</button>
</div>

<!-- Tab content -->
<div id="t1" class="tabcontent">
    <div style="border: thin solid #ddd; margin: 5px; border-radius: 25px; padding: 10px;">
        <span style="width:40%">
            <label>Start Date</label>
            <input type="date" id="from" placeholder="Start Date">
        </span>&nbsp;&nbsp;
        <span style="width:40%">
            <label>End Date</label>
            <input type="date" id="to" placeholder="End Date">
        </span>&nbsp;&nbsp;
        <span class="button confirm" id="filter1" onclick="return false"><i class="icon-refresh"></i></span>
    </div>
    <div id="container1" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
</div>

<div id="t2" class="tabcontent">
    <div style="border: thin solid #ddd; margin: 5px; border-radius: 25px; padding: 10px;">
        <span style="width:40%">
            <label>Enter Target</label>
            <input type="text" id="target" placeholder="Facility Target" value="1000">
        </span>&nbsp;&nbsp;
        <span class="button confirm"  id="filter2" onclick="return false"><i class="icon-refresh"></i></span>
    </div>
    <div id="container2" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
</div>
<div id="t3" class="tabcontent">
    <div id="container3" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
</div>

<div id="t4" class="tabcontent">
    <div id="container4" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
</div>

<div id="t5" class="tabcontent">
    <div style="border: thin solid #ddd; margin: 5px; border-radius: 25px; padding: 10px;">
        <span class="button confirm"  id="filter3" onclick="return false">Plot Graph<i class="icon-refresh"></i></span>
        <span style="display: none" id="process">Please wait...</span>
    </div>
    <div id="container5" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
</div>


<script>
    jq('#filter1').click(function (e) {
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
    jq('#filter3').click(function (e) {
        var target = jq('#target').val();
        jq('#process').show();
        jq = jQuery;
        jq(function() {
            jq.ajax({
                url: "${ ui.actionLink("visualization", "visualization", "plotMissedAppointments") }",
                dataType: "json",
                data: {
                    'target': target
                }

            }).success(function(data) {

                var  obj= jq.parseJSON(data);
                console.log(obj);
                plotMissedApointment(obj.categories, obj.series)
                jq('#process').hide();
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
        Highcharts.chart('container1', {
            chart: {
                type: 'column'
            },
            title: {
                text: 'Test and Start Implementation'
            },
            xAxis: {
                gridLineWidth: 0,
                minorGridLineWidth: 0,
                categories: categories,
                crosshair: true,
                enabled:false
            },
            yAxis: [{
                min: 0,
                max: 100,
                title: {
                    text: '% of New ART Patients'
                },
                stackLabels: {
                    format: '',
                    // enabled: true,
                    style: {
                        //fontWeight: 'bold',
                        color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
                    }
                },
                gridLineWidth: 0,
                minorGridLineWidth: 0,
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
                        enabled: true,
                        format: '',
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
                showInLegend: false,
                name: '>7 Days',
                data: series[2].dataDouble,
                color:"#7030a0"
            }, {
                showInLegend: false,
                name: '1-7 Days',
                data: series[1].dataDouble,
                color:"#4472c4"
            }, {
                showInLegend: false,
                name: 'Same Day',
                data: series[0].dataDouble,
                color:"#000000"
            }, {
                showInLegend: false,
                name: 'HIV Diagnostic  Date Unknown',
                data: series[3].dataDouble,
                color:"#ff0000"
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
                gridLineWidth: 0,
                minorGridLineWidth: 0,
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
                    },
                    gridLineWidth: 0,
                    minorGridLineWidth: 0,
                },
                {
                    title: {
                        text: '% of Patients'
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
                    name: 'Results ≥1,000 copies/ml',
                    data: series[2].data,
                    color: '#e905bf', stacking: 'New Positive'

                },{
                    name: 'Result <1,000 copies/ml',
                    data: series[3].data,
                    color: '#b4c7e7', stacking: 'New Positive'

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
                gridLineWidth: 0,
                categories: categories,
                crosshair: true
            },
            yAxis: [
                {
                    tickInterval: 1,
                    minRange: 1,
                    allowDecimals: false,
                    startOnTick: true,
                    endOnTick: true,
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
                    },
                    gridLineWidth: 0,
                    minorGridLineWidth: 0
                },
                {
                    title: {
                        text: '% of Patients'
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
                    //stacking: 'normal',
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
                    data: series[2].data

                },
                {
                    name: 'CD4 count ≥200 cells/ml',
                    data: series[1].data,
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
            xAxis: {
                gridLineWidth: 0,
                minorGridLineWidth: 0,
                categories: categories,
                crosshair: true
            },
            yAxis: [{ // Primary yAxis
                //  max: 1000,
                tickInterval: 1,
                minRange: 1,
                allowDecimals: false,
                startOnTick: true,
                endOnTick: true,
                tickWidth: 0,
                crosshair: false,
                lineWidth: 0,
                labels: {
                    format: '{value}',
                    enabled: true,
                    style: {
                        color: Highcharts.getOptions().colors[1]
                    },
                    gridLineWidth: 0,
                    minorGridLineWidth: 0
                },
                gridLineWidth: 0,
                minorGridLineWidth: 0,
                title: {
                    text: 'Number of pateints',
                    style: {
                        color: Highcharts.getOptions().colors[1]
                    }
                }
            },
                {
                    title: {
                        text: '% Performance'
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
                    data: series[2].dataDouble,
                    color: '#df7f7f',
                    tooltip: {
                        valueSuffix: '%'
                    }

                }

            ]
        });
    }

    function plotMissedApointment(categories, series) {
        Highcharts.chart('container5', {
            chart: {
                // zoomType: 'xy'
            },
            title: {
                text: 'Missed Appointment Analysis'
            },
            subtitle: {
                text: ''
            },
            xAxis: {
                gridLineWidth: 0,
                minorGridLineWidth: 0,
                categories: categories,
                crosshair: true
            },
            yAxis: [{ // Primary yAxis
                //  max: 1000,
                tickWidth: 0,
                crosshair: false,
                lineWidth: 0,
                labels: {
                    format: '{value}',
                    enabled: true,
                    style: {
                        color: Highcharts.getOptions().colors[1]
                    },
                    gridLineWidth: 0,
                    minorGridLineWidth: 0
                },
                gridLineWidth: 0,
                minorGridLineWidth: 0,
                title: {
                    text: 'Number of pateints',
                    style: {
                        color: Highcharts.getOptions().colors[1]
                    }
                }
            },
                {
                    title: {
                        text: '% of Patients'
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
                name: 'Patients with appointmentt',
                type: 'column',
                data: series[0].data,
                pointPadding: 0,
                color: '#000'

            },

                {
                    name: 'Patients that missed appointment',
                    type: 'column',
                    data: series[1].data,
                    pointPadding: 0.3,
                    color: '#b4c7e7'

                }

            ]
        });
    }

    function openCity(evt, cityName) {
        // Declare all variables
        var i, tabcontent, tablinks;

        // Get all elements with class="tabcontent" and hide them
        tabcontent = document.getElementsByClassName("tabcontent");
        for (i = 0; i < tabcontent.length; i++) {
            tabcontent[i].style.display = "none";
        }

        // Get all elements with class="tablinks" and remove the class "active"
        tablinks = document.getElementsByClassName("tablinks");
        for (i = 0; i < tablinks.length; i++) {
            tablinks[i].className = tablinks[i].className.replace(" active", "");
        }

        // Show the current tab, and add an "active" class to the button that opened the tab
        document.getElementById(cityName).style.display = "block";
        evt.currentTarget.className += " active";
    }
</script>
