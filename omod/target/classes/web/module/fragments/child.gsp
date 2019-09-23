<%=  ui.resourceLinks() %>

<div id="pmtct_fo" style="min-width: 310px; height: 400px; margin: 0 auto"></div>

<div>
    <div>
        <select id="year" class="button"></select>
        <select id="month" class="button"></select>
        <span class="button confirm" onclick="getEid()"><i class="icon-refresh"></i></span>
    </div>
    <div id="pmtct_eid" style="min-width: 310px; height: 400px; margin: 0 auto">
</div>
    
</div>

<script type="text/javascript">
    jq = jQuery;

    jq(document).ready(function()
    {
        populateDropdown()
        getAncPmtctArt()
    });

    function getAncPmtctArt()
    {
        var link = "${ ui.actionLink("visualization", "hts", "getPmtctFo")}";
        jq.ajax({
            contentType: "application/json; charset=utf-8",
            dataType: "json",
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
                        text: 'PMTCT Follow up'
                    },
                    tooltip: {
                        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
                    },
                    plotOptions: {
                        pie: {
                            allowPointSelect: true,
                            cursor: 'pointer',
                            depth: 35,
                            dataLabels: {
                                enabled: true,
                                format: '{point.name}'
                            }
                        }
                    },
                    series: [{
                        type: 'pie',
                        name: 'Browser share',
                        data: mData
                    }]
                });
            },
            error: function (e)
            {

            }
        });
    }

    function getEid() {
        var month = document.getElementById("month").value;
        var year = document.getElementById("year").value;

        var link = "${ ui.actionLink("visualization", "hts", "getPmtctEid")}";
        jq.ajax({
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: {'month':month, 'year':year},
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
        var month = parseInt(selectMonth.val()) + 1;
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
        Highcharts.chart('pmtct_eid', {
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
    }
</script>
