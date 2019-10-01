<div id="container" style="min-width: 310px; height: 400px; margin: 0 auto"></div>

<br>
<br>
<hr style="border-bottom: thin solid #ddd; margin-bottom: 45px;"/>
<div>
    <select id="year" class="button"></select>
    <select id="month" class="button"></select>
    <span class="button confirm" onclick="getPmtctCohortRetention()"><i class="icon-refresh"></i></span>
</div>
<br>
<div id="cohort" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
<br>
<br>
<hr style="border-bottom: thin solid #ddd; margin-bottom: 45px;"/>
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
                var ancReg = 0;
                var ancTested = 0;
                var oldPositive = 0
                var newPositive = 0;
                var newTx = 0;
                var oldTx = 0;

                jq.each(r, function (i, f)
                {
                    if(f.name.toLowerCase() === 'anchts')
                    {
                        ancTested = f.value;
                    }
                    if(f.name.toLowerCase() === 'anc')
                    {
                        ancReg = f.value;
                    }
                    if(f.name.toLowerCase() === 'prevpositive')
                    {
                        oldPositive = f.value;
                    }
                    if(f.name.toLowerCase() === 'newpositive')
                    {
                        newPositive = f.value;
                    }
                    if(f.name.toLowerCase() === 'oldontx')
                    {
                        oldTx = f.value;
                    }
                    if(f.name.toLowerCase() === 'newontx')
                    {
                        newTx = f.value;
                    }
                });

                pData.push({name: 'ANC Registration', data: [ancReg, null, null, null, null, null], color: '#000'});
                pData.push({name: 'Tested', data: [null, ancTested, null, null, null, null], color: '#B4C7E7'});
                pData.push({name: 'Known Positive', data: [null, null, oldPositive, null, null, null], color: '#FF00FF', stacking: 'New Positive'});
                pData.push({name: 'New Positive', data: [null, null, newPositive, null, null, null], color: '#7030A0', stacking: 'New Positive'});
                pData.push({name: 'Already on Tx', data: [null, null, null, null, null, oldTx], color: '#FF00FF', stacking: 'New on Tx'});
                pData.push({name: 'New on Tx', data: [null, null, null, null, null, newTx], color: '#7030A0', stacking: 'New on Tx'});

                Highcharts.chart('container',
                {
                    chart: {
                        type: 'column'
                    },
                    title: {
                        text: 'PMTCT ART Coverage'
                    },
                    xAxis: {
                        categories: ['ANC Registration', 'Tested', 'New Positive', 'Known Positive', 'New on Tx', 'Already on Tx'],
                        labels: {enabled:false}
                    },
                    yAxis: {
                        allowDecimals: false,
                        gridLineWidth: 0,
                        minorGridLineWidth: 0,
                        min: 0,
                        title: {
                            text: 'Number of Pregnant Women'
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
                        bar: {
                            borderWidth: 0,
                            colorByPoint: true,
                            stacking: 'normal'
                        },
                        series: {
                            pointWidth: 80
                        }
                    },
                    legend: {
                        align: 'left',
                        x: 50,
                        verticalAlign: 'bottom',
                        y: 22,
                        itemDistance:45,
                        floating: false,
                        backgroundColor: 'white',
                        borderColor: '#fff',
                        borderWidth: 0,
                        shadow: false
                    },
                    series: pData,
                    dataLabels: {
                        enabled: false
                    }
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
                var categories = [];
                var month0 = [];
                var month3 = [];
                var month6 = [];
                var month12 = [];
                var percentageRetention =[];

                jq.each(r, function (i, f)
                {
                    month0.push(f.active_0)
                    month3.push(f.active_3)
                    month6.push(f.active_6)
                    month12.push(f.active_12)
                    categories.push(f.cohort);

                    if(f.active_12 > 0 && f.active_0 > 0)
                    {
                        percentageRetention.push((f.active_12*100)/f.active_0)
                    }
                    else
                    {
                        percentageRetention.push(0)
                    }
                });

                pData.push({name: 'Month 0', data: month0, type: 'column', pointWidth: 45, pointPadding: 0, tooltip: { valueSuffix: '' }});
                pData.push({name: '3 Months retention', data: month3, type: 'column', pointWidth: 40, pointPadding: 0., tooltip: { valueSuffix: '' }});
                pData.push({name: '6 Months retention', data: month6, type: 'column', pointWidth: 35, pointPadding: 0.2, tooltip: { valueSuffix: '' }});
                pData.push({name: '12 Months retention', data: month12, type: 'column', pointWidth: 30, pointPadding: 0.1, tooltip: { valueSuffix: '' }});
                pData.push({name: '% 12 Month retention', data: percentageRetention, yAxis: 1, type: 'scatter', marker: { radius: 4 }, tooltip: { valueSuffix: '%' }, dataLabels: { enabled: true, format: '{point.y}%'}});

                Highcharts.chart('cohort',
                    {
                    chart: {
                        type: 'column'
                    },
                    title: {
                        text: 'PMTCT ART Retention Analysis'
                    },
                    xAxis: {
                        categories: categories,
                        crosshair: true
                    },
                    yAxis: [{ // Primary yAxis
                        gridLineWidth: 0,
                        minorGridLineWidth: 0,
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
                            text: '% Retained',
                            rotation: 270,
                            style: {
                                color: Highcharts.getOptions().colors[0]
                            }
                        },
                        gridLineWidth: 0,
                        minorGridLineWidth: 0,
                        min: 0,
                        max: 100,
                        labels: {
                            format: '{value}%',
                            style: {
                                color: Highcharts.getOptions().colors[0]
                            }
                        },
                        opposite: true,
                        showFirstLabel: false,
                        showLastLabel: true
                    }],
                        legend: {
                            align: 'left',
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
                            headerFormat: '<table style="width: 110px"><tr style="font-size:12px; font-weight: bold; border-bottom: thin solid #ddd"><td colspan="2">{point.key}</td></tr>',
                            pointFormat: '<tr style="font-size:11px;background-color: #fff"><td style="color:{series.color};padding:0">{series.name}: </td>' +
                                '<td style="padding:0"><b>{point.y:,.0f}</b></td></tr>',
                            footerFormat: '</table>',
                            shared: true,
                            useHTML: true
                        },
                    plotOptions: {
                        column: {
                            borderWidth: 0,
                            grouping: false,
                            shadow: false
                        },
                        colorByPoint: true
                    },
                    colors: ['#000', '#B4C7E7', '#7030A0', '#fff', '#4472C4'],
                    series: pData,
                    dataLabels:
                        {
                        enabled: true
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
                var suppressed = [];
                var nonSuppressed = [];
                jq.each(r, function (i, f)
                {
                    suppressed.push(f.suppressed)
                    nonSuppressed.push(f.nonSuppressed);
                    categories.push(f.cohort);
                });

                pData.push({name: 'VL Result by 36 week GA (<1,000)', data: suppressed, color: '#B4C7E7', stacking: 'VL Result by 36 week GA (>= 1,000)'});
                pData.push({name: 'VL Result by 36 week GA (>= 1,000)', data: nonSuppressed, color: '#000', stacking: 'VL Result by 36 week GA (>= 1,000)'});
                Highcharts.chart('cohortViralSupp',
                    {
                        chart: {
                            type: 'column'
                        },
                        title: {
                            text: 'Viral load Result in PMTCT by Gestation Age'
                        },
                        xAxis: {
                            categories: categories,
                            labels: {enabled:true}
                        },
                        yAxis: {
                            allowDecimals: false,
                            gridLineWidth: 0,
                            minorGridLineWidth: 0,
                            min: 0,
                            title: {
                                text: 'Number of positive pregnant women'
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
                            bar: {
                                pointPadding: 0.02,
                                borderWidth: 0,
                                colorByPoint: true,
                                stacking: 'normal'
                            }
                        },
                        legend:
                        {
                            align: 'left',
                            x: 50,
                            verticalAlign: 'bottom',
                            y: 22,
                            floating: false,
                            backgroundColor: 'white',
                            borderColor: '#fff',
                            borderWidth: 0,
                            shadow: false
                        },
                        series: pData,
                        dataLabels:
                        {
                            enabled: true
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
