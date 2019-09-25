<% ui.decorateWith("appui", "standardEmrPage") %>

<div id="content" class="container">
    <div id="home-container">
        <div id="apps">

            <a id="demoapp-homepageLink-demoapp-homepageLink-extension" href="#" onclick="gotoPath('reportingui/reportsapp/home.page')" class="button app big">
                <i class="icon-list-alt"></i>
                MSF
            </a>

            <a id="demoapp-homepageLink-demoapp-homepageLink-extension" href="#" onclick="gotoPath('visualization/visualization.page')" class="button app big">
                <i class="icon-bar-chart"></i>
                Enhanced Monitoring
            </a>
        </div>

    </div>

</div>

<style>
    .base{
        background-color: #000003;
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
        console.log(baseUrl);
        window.location.replace(baseUrl);
    }
</script>