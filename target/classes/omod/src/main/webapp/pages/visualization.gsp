<% ui.decorateWith("appui", "standardEmrPage") %>
<div class="base">
    <div class="container">
        <div class="home-container">
            <a class="button big" href="#" onclick="gotoPath('visualization/Pmtct.page')">
                <i class="icon-medical"></i>
                <br>
                PMTCT Charts
            </a>
            &nbsp;
            <a class="button big" href="#" onclick="gotoPath('visualization/Hts.page')">
                <i class="icon-medical"></i>
                <br>
                HTS Charts
            </a>
            <!--{<a class="button big" href="#" onclick="gotoPath('visualization/LineList.page')">
                <i class="icon-medical"></i>
                <br>
                Line List Charts
            </a>}-->
        </div>
    </div>
</div>

<style>
    .base{
        padding: 15px;
    }

    #body-wrapper{
        background-color: #1c463f;
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