function toggleEventVisibility()
{
        var link = document.getElementById("eventToggle");
        var newVisibility;
	var newDisplay;
        if (link.eventsVisible === undefined || link.eventsVisible === true) {
                //hide
                newVisibility = 'collapse';
		newDisplay = 'none';
                link.eventsVisible = false;
                link.innerHTML = link.innerHTML.replace('Hide', 'Show');
        }
        else {
                //show
                newVisibility = 'visible';
		newDisplay = '';
                link.eventsVisible = true;
                link.innerHTML = link.innerHTML.replace('Show', 'Hide');
        }
        
        var events = document.getElementsByClassName('other');
        for (var i = 0; i < events.length; ++i) {
                events[i].style.visibility = newVisibility;
		events[i].style.display = newDisplay;
        }
}
