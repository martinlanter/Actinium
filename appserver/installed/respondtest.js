importPackage(Packages.ch.ethz.inf.vs.actinium.test);

app.dump("hello aaa");


app.root.onget = function(request) {
	request.respond("Content", "responded Content");
};


app.root.onpost = function(request) {
	app.dump("get "+request.payloadText);
	request.respond(68, "responded 68 (Changed)");
};


app.root.onput = function(request) {
	request.respond(2.05, "responded 2.05 (Content)");
};


app.root.ondelete = function(request) {
	request.respond(this.Created, "responded Created");
};

var first = new JavaScriptResource("first");
first.onget = function(request) {
	request.respond(2.05);
}

first.onpost = function(request) {
	app.dump("get "+request.payloadText);
	request.respond(2.05, "String");
}

first.onput = function(request) {
	request.respond(2.05, 42);
}

first.ondelete = function(request) {
	request.respond(2.05, 5.5);
}

var second = new JavaScriptResource("second");
second.onget = function(request) {
	request.respond(2.05, "Content text/plain", "text/plain");
}

second.onpost = function(request) {
	request.respond(2.05, "Content TEXT_XML", 1);
}

second.onput = function(request) {
	request.respond(2.05, null);
}

second.ondelete = function(request) {
	request.accept();
	request.respond(2.05);
}



app.root.add(first);
app.root.add(second);

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