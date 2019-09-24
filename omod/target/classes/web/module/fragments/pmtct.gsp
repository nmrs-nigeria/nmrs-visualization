<%=  ui.resourceLinks() %>

<div id="container" style="min-width: 310px; height: 400px; margin: 0 auto"></div>

<br>
<br>

<div>
    <select id="year" class="button"></select>
    <select id="month" class="button"></select>
    <span class="button confirm" onclick="getPmtctCohortRetention()"><i class="icon-refresh"></i></span>
</div>
<br>
<div id="cohort" style="min-width: 310px; height: 400px; margin: 0 auto"></div>

<br>
<br>
<div id="cohortViralSupp" style="min-width: 310px; height: 400px; margin: 0 auto"></div>

<br>
<br>

<script type="text/javascript">
    jq = jQuery;

    jq(document).ready(function()
    {
        populateDropdown();
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
                        text: 'PMTCT ART Covergae'
                    },
                    xAxis: {
                        categories: categories
                    },
                    yAxis: {
                        allowDecimals: false,
                        min: 0,
                        title: {
                            text: 'Number of pregnant women'
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
            },
            error: function (e)
            {

            }
        });
    }

    function getPmtctCohortRetention()
    {
        var month = document.getElementById("month").value;
        var year = document.getElementById("year").value;

        var link = "${ ui.actionLink("visualization", "pmtct", "getPmtctCohortRetention")}";
        jq.ajax({
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            url: link,
            data: {'month':parseInt(month)+1, 'year':parseInt(year)},
            cache: false,
            timeout: 600000,
            success: function (r)
            {
                var pData =[];
                var percentageRetention =[];
                var categories = [];
                var pointPadding = 0;
                jq.each(r, function (i, f)
                {
                    var dx = [ f.active_12, f.active_6, f.active_3, f.active_0 ];
                    categories.push(f.cohort);
                    pData.push({name: f.cohort, data: dx, type: 'column', pointPadding: pointPadding, pointPlacement: -0.1, tooltip: { valueSuffix: '' }});
                    pointPadding += 0.02;

                    if(f.active_12 > 0 && f.active_0 > 0)
                    {
                        percentageRetention.push((f.active_12*100)/f.active_0)
                    }
                    else
                     {
                        percentageRetention.push(0)
                    }
                    pData.push({name: f.cohort, data: percentageRetention, type: 'scatter', marker: { radius: 4 }, tooltip: { valueSuffix: '%' }});
                });
                Highcharts.chart('cohort',
                    {
                    chart: {
                        type: 'column'
                    },
                    title: {
                        text: 'PMTCT ART Retention Analysis'
                    },
                    xAxis: {
                        categories: categories
                    },
                    yAxis: [{ // Primary yAxis
                        labels: {
                            format: '{value}',
                            style: {
                                color: Highcharts.getOptions().colors[1]
                            }
                        },
                        title: {
                            text: 'Number of positive pregnant women',
                            style: {
                                color: Highcharts.getOptions().colors[1]
                            }
                        }
                    }, { // Secondary yAxis
                        title: {
                            text: '%Retained',
                            style: {
                                color: Highcharts.getOptions().colors[0]
                            }
                        },
                        labels: {
                            format: '{value}%',
                            style: {
                                color: Highcharts.getOptions().colors[0]
                            }
                        },
                        opposite: true
                    }],
                        legend: {
                            enabled: false
                        },
                    tooltip: {
                        shared: true
                    },
                    plotOptions: {
                        column:
                         {
                            // dataLabels: { enabled: true },
                            grouping: false,
                             pointPadding: 0.02,
                             borderWidth: 0,
                             colorByPoint: true
                        }
                    },
                    series: pData,
                        dataLabels: {
                            enabled: true,
                            rotation: -90,
                            color: '#FFFFFF',
                            align: 'right',
                            format: '{point.y:.0f}', // one decimal
                            y: 10, // 10 pixels down from the top
                            style: {
                                fontSize: '13px',
                                fontFamily: 'Verdana, sans-serif'
                            }
                        }
                });

                getPmtctCohortViralSuppression(month, year);
            },
            error: function (e)
            {

            }
        });
    }

    function getPmtctCohortViralSuppression(month, year)
    {
        var link = "${ ui.actionLink("visualization", "pmtct", "getPmtctCohortViralSuppression")}";
        jq.ajax({
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            url: link,
            data: {'month':parseInt(month)+1, 'year':parseInt(year)},
            cache: false,
            timeout: 600000,
            success: function (r)
            {
                var pData =[];
                var categories = [];
                jq.each(r, function (i, f)
                {
                    var dx = [ f.suppressed, f.nonSuppressed ];
                    categories.push(f.cohort);
                    pData.push({name: f.cohort, data: dx});
                });
                Highcharts.chart('cohortViralSupp',
                    {
                        chart: {
                            type: 'column'
                        },
                        title: {
                            text: 'Viral load Result in PMTCT by gestation age'
                        },
                        xAxis: {
                            categories: categories
                        },
                        yAxis: {
                            min: 0,
                            title: {
                                text: 'Number of positive pregnant women'
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
                            enabled: false
                        },
                        tooltip: {
                            headerFormat: '<b>{point.x}</b><br/>',
                            pointFormat: '{series.name}: {point.y}<br/>+ve mothers: {point.y}'
                        },
                        plotOptions: {
                            column: {
                                stacking: 'normal',
                                dataLabels: {
                                    enabled: true
                                }
                            }
                        },
                        series: pData,
                        dataLabels: {
                            enabled: true,
                            rotation: -90,
                            color: '#FFFFFF',
                            align: 'right',
                            format: '{point.y:.0f}', // one decimal
                            y: 10, // 10 pixels down from the top
                            style: {
                                fontSize: '13px',
                                fontFamily: 'Verdana, sans-serif'
                            }
                        }
                    });
            },
            error: function (e)
            {

            }
        });
    }


    function populateDropdown(){
        var monthNames = ["January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        ];
        var qntYears = 6;
        var selectYear = jq("#year");
        var selectMonth = jq("#month");
        //var selectDay = jq("#day");
        var currentYear = new Date().getFullYear();

        for (var y = 0; y < qntYears; y++){
            var date = new Date(currentYear);
            var yearElem = document.createElement("option");
            yearElem.value = currentYear
            yearElem.textContent = currentYear;
            selectYear.append(yearElem);
            currentYear--;
        }

        for (var m = 0; m < 12; m++){
            var monthNum = new Date(2018, m).getMonth()
            var month = monthNames[monthNum];
            var monthElem = document.createElement("option");
            monthElem.value = monthNum;
            monthElem.textContent = month;
            selectMonth.append(monthElem);
        }

        var d = new Date();
        var month = d.getMonth();
        var year = d.getFullYear();
        var day = d.getDate();

        selectYear.val(year);
        //selectYear.on("change", AdjustDays);
        selectMonth.val(month);
        //selectMonth.on("change", AdjustDays);

        //AdjustDays();
        //selectDay.val(day);
    }

    function AdjustDays(){
        var year = selectYear.val();
        var month = parseInt(selectMonth.val());
        selectDay.empty();

        //get the last day, so the number of days in that month
        var days = new Date(year, month, 0).getDate();

        //lets create the days of that month
        for (var d = 1; d <= days; d++){
            var dayElem = document.createElement("option");
            dayElem.value = d;
            dayElem.textContent = d;
            selectDay.append(dayElem);
        }
    }
</script>
