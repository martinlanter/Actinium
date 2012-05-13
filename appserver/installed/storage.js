/**
 * Storage is a test app for the AppServer.
 * Send a POST request with a name as payload to create a new subresource with the specified name.
 * Send a PUT request with a text as payload to store the specified text inside the resource.
 * Send a GET request to retrieve the text from the resource.
 * Same semantics applys to all created subresources.
 */

var subress = new Array();
var paths = new Array();

var content = ""; //java.lang.reflect.Array.newInstance(java.lang.Character.TYPE, 0);

app.root.onpost = function(request) {
	var name = request.getPayloadString();
	
	if (contains(subress,name)) {
		var response = new Response(CodeRegistry.RESP_BAD_REQUEST)
		var path = findPathForName(subress, paths, name);
		response.setPayload("Storage "+name+" is already created at "+path);
		response.setLocationPath(path);
		request.respond(response);
	} else {
		var storage = new Storage(name);
		app.root.add(storage.res);
		var path = storage.res.getPath();
		subress[subress.length] = name;
		paths[paths.length] = path;
		
		var response = new Response(CodeRegistry.RESP_CREATED);
		response.setPayload("Storage "+name+" created at location "+path);
		response.setLocationPath(path);
		request.respond(response);
	}
}

app.root.onput = function(request) {
	content = request.getPayloadString();
	app.root.changed();
	request.respond(CodeRegistry.RESP_CHANGED);
}

app.root.onget = function(request) {
	var response = new Response(CodeRegistry.RESP_CONTENT);
	response.setPayload(content);
	request.respond(response);
}

app.root.ondelete = function(request) {
	request.respond(CodeRegistry.RESP_FORBIDDEN, "Storage root cannot be deleted");
}

function Storage(name) {
	this.res = new JavaScriptResource(name);
	this.subress = new Array();
	this.paths = new Array();
	this.content = "";
	var mythis = this;
	
	this.res.onget = function(request) {
		var response = new Response(CodeRegistry.RESP_CONTENT);
		response.setPayload(mythis.content);
		request.respond(response);
	}
	
	this.res.onpost = function(request) {
		var name = request.getPayloadString();

		if (contains(mythis.subress, name)) {
			var response = new Response(CodeRegistry.RESP_BAD_REQUEST)
			request.setPayload("Storage "+name+" is already created");
			response.setLocationPath(findPathForName(mythis.subress, mythis.paths, name));
			request.respond(response);
		} else {
			var storage = new Storage(name);
			mythis.res.add(storage.res);
			var path = storage.res.getPath();
			mythis.subress[mythis.subress.length] = name;
			mythis.paths[mythis.paths.length] = path;


			var response = new Response(CodeRegistry.RESP_CREATED);
			response.setPayload("Storage "+name+" created at location "+path);
			response.setLocationPath(path);
			request.respond(response);
		}
	}
	
	this.res.onput = function(request) {
		mythis.content = request.getPayloadString();
		mythis.res.changed();
		request.respond(CodeRegistry.RESP_CHANGED);
	}
	
	this.res.ondelete = function(request) {
		mythis.res.remove();
		request.respond(CodeRegistry.RESP_DELETED, "deleted");
	}
}

function contains(array, name) {
	for (var i=0;i<subress.length;i++) {
		if (name==array[i]) {
			return true;
		}
	}
	return false;
}

function findPathForName(ress, ps, n) {
	for (var i=0;i<ress.length;i++) {
		if (ress[i] == n) {
			return ps[i];
		}
	}
	return null;
}
