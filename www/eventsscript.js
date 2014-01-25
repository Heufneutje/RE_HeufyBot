function toggleEventVisibility(adjustScroll)
 {
 	//Check if a parameter was passed. Use it if so, use default 'true' if not;
 	adjustScroll = typeof adjustScroll !== 'undefined' ? adjustScroll : true;
 	 
	var rowToScrollTo = 0;
	var scrollDifference = 0;
	if (adjustScroll) {
		//Get the line we're currently looking at, so we can scroll back to it
		var scrollHeight = document.documentElement.scrollTop || window.pageYOffset;
		var table = document.getElementById('log');
		var rowHeight = Math.ceil(table.rows[0].cells[0].offsetHeight * 1.5);
		var estimatedRow = Math.max(0, Math.floor(scrollHeight / rowHeight));

		var rowToScrollTo = Math.min(table.rows.length - 1, estimatedRow);
		while (rowToScrollTo < table.rows.length - 1 && (table.rows[rowToScrollTo].className != 'message' || table.rows[rowToScrollTo].offsetTop + table.offsetTop < scrollHeight)) {
			++rowToScrollTo;
		}
		var scrollDifference = table.rows[rowToScrollTo].getBoundingClientRect().top;

		table.rows[rowToScrollTo].style.border = '1px dashed lightgrey';
		setTimeout(function() {removeBorder(table.rows[rowToScrollTo]); }, 2000);
	}
	
	
	var link = document.getElementById("eventToggle");
	var newDisplay;
	if (link.eventsVisible === undefined || link.eventsVisible === true) {
		//hide
		newDisplay = 'none';
		link.eventsVisible = false;
		link.innerHTML = link.innerHTML.replace('Hide', 'Show');
	}
	else {
		//show
		newDisplay = '';
		link.eventsVisible = true;
		link.innerHTML = link.innerHTML.replace('Show', 'Hide');
	}
	
	var events = document.getElementsByClassName('other');
	for (var i = 0; i < events.length; ++i) {
		events[i].style.display = newDisplay;
	}
	
	//Scroll to the row we were previously at
	if (adjustScroll) {
		setTimeout(function() {scrollToRow(rowToScrollTo, scrollDifference); }, 1);
	}

	return false;
}

function scrollToRow(rowToScrollTo, scrollDifference) {
	var table = document.getElementById('log');
	var targetScroll = table.rows[rowToScrollTo].offsetTop + table.offsetTop - scrollDifference;
	if (document.documentElement.scrollTop) {
		document.documentElement.scrollTop = targetScroll;
	}
	else if (window.pageYOffset) {
		window.pageYOffset = targetScroll;
	}
	else {
		document.documentElement.scrollTop = 0;
		window.pageYOffset = 0;
	}
}

function removeBorder(rowElement) {
	rowElement.style.border = '';
}
