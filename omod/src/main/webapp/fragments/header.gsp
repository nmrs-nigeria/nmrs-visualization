<div class="topnav">
    <a class="active" href="#" onclick="gotoPath('visualization/datavisualization.page')">TXCURR</a>
    <a href="#" onclick="gotoPath('visualization/datavisualization.page')">TXNEW</a>
    <a href="#" onclick="gotoPath('visualization/Hts.page')">HTS</a>
    <a href="#" onclick="gotoPath('visualization/Pmtct.page')">PMTCT</a>
</div>

<style>
/* Add a black background color to the top navigation */
.topnav {
    background-color: #1c463f;
    overflow: hidden;
}

/* Style the links inside the navigation bar */
.topnav a {
    float: left;
    color: #f2f2f2;
    text-align: center;
    padding: 14px 16px;
    text-decoration: none;
    font-size: 17px;
}

/* Change the color of links on hover */
.topnav a:hover {
    background-color: #ddd;
    color: black;
}

/* Add a color to the active/current link */
.topnav a.active {
    background-color: #4CAF50;
    color: white;
}
    .confirm{
        background-color: #5aaf50 !important;
    }
</style>

<script>
    function getPath(){
        var path = jQuery(location).attr('pathname').split('/')[1];
        return path;
    }

    function gotoPath(value) {
        var baseApp = jQuery(location).attr('pathname').split('/')[0];
        var baseUrl = baseApp + "/" + getPath() + "/" + value;

        window.location.replace(baseUrl);
    }
</script>