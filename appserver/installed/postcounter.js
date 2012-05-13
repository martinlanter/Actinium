/**
 * This app counts the POST Request it gets. On a PUT Request the counter is
 * resetted to 0. This app is to test whether performGET/POST/PUT/DELETE are
 * overridable and what happens if they aren't.
 */

app.root.onput = function(request) {
	count = 0;
	app.dump("reset counter to 0");
	request.respond(66, "counter: "+count);
}

app.root.onpost = function(request) {
	count = count + 1;
	request.respond(68, "counter: "+count);
}

var count = 0;