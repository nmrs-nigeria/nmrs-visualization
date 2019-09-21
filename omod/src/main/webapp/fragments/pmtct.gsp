<%=  ui.resourceLinks() %>

<div id="container" style="min-width: 310px; height: 400px; margin: 0 auto"></div>

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
                            type: 'bar'
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
                                borderWidth: 0
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

</script>
