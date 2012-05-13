importPackage(Packages.ch.ethz.inf.vs.actinium.test);

var child = new JavaScriptResource("child");
	child.onpost = function(request) {
	app.dump("request text: "+request.requestText);
	app.dump("request headers: "+request.getAllRequestHeaders());
	
	request.setResponseHeader("Max-Age", 55);
	request.setResponseHeader("Content-Type", 3);
	request.setLocationPath("my_location_path");
	request.respond(2.05, "response blabla");
}

app.root.add(child);

var req = new CoAPRequest();
req.open("POST", "coap://localhost:5683/apps/running/aaa/child", true); // asynchronous
req.setRequestHeader("Accept", "application/json");
req.setRequestHeader("Max-Age", 77);
req.setRequestHeader(app.Uri_Host, "My_Uri_Host");
req.onload = function(response) {
	app.dump("response text: "+this.responseText);
	app.dump("response headers: "+this.getAllResponseHeaders());
}

req.send("payload blabla");


/*var r = new MyRequest();
r.nix = "haha"
r.getPayloadString = function(){app.dump("js fun"); return "js fun ret";}
app.dump(r.payloadText);
app.dump(r.getPayloadString());
app.dump(r.nix);

app.fun = function(arg) {
	app.dump("fun is called");
	app.dump("arg.payloadText = "+arg.payloadText);
	app.dump("arg.getPayloadString() = "+arg.getPayloadString());
	app.dump(arg.respond(2.05))
}/**/