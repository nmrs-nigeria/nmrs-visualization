<% ui.decorateWith("appui", "standardEmrPage") %>

<%= ui.resourceLinks() %>
<% ui.includeJavascript("visualization", "highcharts.js") %>

<!-- Tab links -->
<div class="tab">
    <button class="tablinks" onclick="openCity(event, 'Pmtct1')">PMTCT 1</button>
    <button class="tablinks" onclick="openCity(event, 'Pmtct2')">PMTCT 2</button>

</div>

<!-- Tab content -->
<div id="Pmtct1" class="tabcontent">
    ${ui.includeFragment("visualization","pmtct")}
</div>

<div id="Pmtct2" class="tabcontent">
    ${ui.includeFragment("visualization","child")}
</div>

<style>
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
</style>

<script>
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