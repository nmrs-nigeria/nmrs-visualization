<% ui.includeJavascript("visualization", "highcharts-3d.js") %>

<div style="border: thin solid #ddd; margin: 5px; border-radius: 25px; padding: 10px;">
    <span style="width:40%">
        <label>Start Date</label>
        <input type="date" id="start_date" placeholder="Start Date">
    </span>&nbsp;&nbsp;
    <span style="width:40%">
        <label>End Date</label>
        <input type="date" id="end_date" placeholder="End Date">
    </span>&nbsp;&nbsp;
    <span class="button confirm" onclick="getEid()"><i class="icon-refresh"></i></span>
</div>

<div id="pmtct_eid" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
<br/>

<hr style="border-bottom: thin solid #ddd; margin-bottom: 45px;"/>
<div>
    <select id="pmtct_year" class="button"></select>
    <select id="pmtct_month" class="button"></select>
    <span class="button confirm" onclick="getAncPmtctPie()"><i class="icon-refresh"></i></span>
</div>
<div id="pmtct_fo" style="min-width: 310px; height: 400px; margin: 0 auto"></div>

<script type="text/javascript">
    jq = jQuery;

    jq(document).ready(function()
    {
        populateDropdown2();
    });

    function getAncPmtctPie()
    {
        var pmtct_year = document.getElementById("pmtct_year").value;
        var pmtct_month = document.getElementById("pmtct_month").value;

        var link = "${ ui.actionLink("visualization", "hts", "getPmtctFo")}";
        jq.ajax({
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: {'pmtct_year':pmtct_year, 'pmtct_month':pmtct_month},
            url: link,
            cache: false,
            timeout: 600000,
            success: function (data)
            {
                var mData = [];
                jq.each(data, function (i, f)
                {
                    var dataData =[];
                    dataData.push(f.name, f.value);
                    mData.push(dataData);
                });
                var colors = ['#000308', '#ff9b2a', '#95c9df',
                    '#ffea62', '#4eabb4', '#5dce8a', '#3643d0'];
                Highcharts.chart('pmtct_fo', {
                    chart: {
                        type: 'pie',
                        options3d: {
                            enabled: true,
                            alpha: 45,
                            beta: 0
                        }
                    },
                    title: {
                        text: 'HIV Exposed Infant Final Outcome'
                    },
                    tooltip: {
                        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
                    },
                    colors:colors,
                    plotOptions: {
                        pie: {
                            allowPointSelect: true,
                            cursor: 'pointer',
                            depth: 35,
                            dataLabels: {
                                enabled: true,
                                formatter: function() {
                                    return '<b>'+ this.point.y +'</b>%';
                                }
                            },
                            showInLegend: true
                        }
                    },
                    series: [{
                        type: 'pie',
                        name: 'HIV Exposed Infant Final Outcome',
                        data: mData
                    }]
                });
            },
            error: function (e)
            {
                console.log(e);
            }
        });
    }

    function getEid() {
        var start_date = document.getElementById("start_date").value;
        var end_date = document.getElementById("end_date").value;

        var link = "${ ui.actionLink("visualization", "hts", "getPmtctEid")}";
        jq.ajax({
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: {'start_date':start_date, 'end_date':end_date},
            url: link,
            cache: false,
            timeout: 600000,
            success: function (data)
            {
                PlotEidChart(data);
            },
            error: function (e)
            {
                console.log(e);
            }
        });
    }

    function populateDropdown2(){
        var monthNames = ["January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        ];
        var qntYears = 6;
        var selectYear = jq("#pmtct_year");
        var selectMonth = jq("#pmtct_month");
        //var selectDay = jq("#day");
        var currentYear = new Date().getFullYear();

        for (var y = 0; y < qntYears; y++){
            var date = new Date(currentYear);
            var yearElem = document.createElement("option");
            yearElem.value = currentYear;
            yearElem.textContent = currentYear;
            selectYear.append(yearElem);
            currentYear--;
        }

        for (var m = 0; m < 12; m++){
            var monthNum = new Date(2018, m).getMonth();
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

    function PlotEidChart(data) {
        var columnData = data;
        var category = [];
        var values = [];
        var valuesName = "Cohort: monthly birth cohorts; HEI that attained 2 months of age";
        for(var i = 0; i < columnData.length; i++){
            category.push(columnData[i].name);
            values.push(columnData[i].y);
        }
        var theSeries = {name: valuesName, data: values};
        var colors = ['#030508', '#7cacc2', '#80699B',
            '#f43713', '#afe5b7'];
        Highcharts.chart('pmtct_eid', {
            chart: {
                type: 'column'
            },
            title: {
                text: 'HIV Exposed Infant Cohort Analysis'
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
    }
</script>
