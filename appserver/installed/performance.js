/**
 * App to evaluate Rhino in AppServer
 * Quicksort source: http://en.literateprograms.org/Quicksort_%28JavaScript%29#chunk%20def:partition
 * JS = 10*Java: http://www.pankaj-k.net/spubs/articles/beanshell_rhino_and_java_perf_comparison/index.html
 *
 * Send a POST request with the payload
 * 		algr [input] [tests]
 *	algr: [fib, quick, newton]
 *	input: input for the algorithm (Default depends on algr).
 *	tests: How many tests should be done (Default is 10)
 * 		The fastest test counts.
 *
 * fib = Recursive Fibonacci algorithm
 * quick = Quicksort
 * newton = Newton's square root algorithm
 *
 * Examples:
 * 	fib 20 5
 * 		Computes 5 times the 20. fibonacci number recursively
 *
 * 	quick 10000
 * 		Sorts 10000 numbers 10 times with Quicksort
 *
 * 	newton
 * 		Computes 10 times all square roots from 1 to 999.999
 */ 


var measurements = 10;
var n_fib = 25;
var n_quick = 10000;
var n_newton = 1000000;

app.root.onpost = function(request) {
	request.accept();
	
	try {
		var prep = null; // preparation function
		var func; // function to mesaure
		var arg;
		var m = measurements;
		
		parts = request.getPayloadString().split(" ");
		if (parts.length>0 && parts[0]=="fib") {
			func = fibonacci;
			arg = n_fib;
		} else if (parts.length>0 && parts[0]=="quick") {
			func = quick_sort;
			arg = n_quick;
			prep = prep_quick_sort;
		} else if (parts.length>0 && parts[0]=="newton") {
			func = newton_sqareroot;
			arg = n_newton;
		} else {
			request.respond(CodeRegistry.RESP_BAD_REQUEST, "unknown function");
		}
		
		if (parts.length>1) {
			arg = Integer.parseInt(parts[1]);
		}
		
		if (parts.length>2) {
			m = Integer.parseInt(parts[2]);
		}
		
		app.dump(parts[0],arg,m);
		var dts = measure(func,prep,arg,m);
		respond_measurement(request,dts);
		
	} catch (e if e.javaException instanceof Exception) {
		request.respond(CodeRegistry.RESP_BAD_REQUEST, e.javaException.toString());
	}
}

function respond_measurement(request, dts) {
	var avg = 0;
	var min = Long.MAX_VALUE;
	
	for (var i=0;i<dts.length;i++) {
		avg += dts[i];
		
		if (min>dts[i]) {
			min = dts[i];
		}
	}
	avg = avg/dts.length;
	
	app.dump("min = "+(min/1000000)+" ms, avg = "+(avg/1000000)+" ms");
	request.respond(CodeRegistry.RESP_CONTENT, "min = "+(min/1000000)+" ms, avg = "+(avg/1000000)+" ms");
}

function measure(func, prep, arg, m) {
	var dts = new Array();
	for (var i=0;i<m;i++) {
	
		var arg_temp;
		if (prep!=null) {
			arg_temp = prep(arg);
		} else {
			arg_temp = arg;
		}
	
		var t0 = app.getNanoTime();
		
		func(arg_temp);
		
		var dt = app.getNanoTime()-t0;
		dts[dts.length] = dt;
		
		app.dump("dt: "+(dt/1000000)+" ms");
	}
	return dts;
}

function fibonacci(n) {
	if (n<=1) return 1;
	else return fibonacci(n-1) + fibonacci(n-2);
}

function prep_quick_sort(size) {
	var rand = new Random();
	var array = new Array();
	for (var i=0;i<size; i++) {
		array[i] = rand.nextInt();
	}
	return array;
}

function quick_sort(array) {
	qsort(array, 0, array.length);
}

function qsort(array, begin, end) {
	if(end-1>begin) {
		var pivot=begin+Math.floor((end-begin)/2);

		pivot=partition(array, begin, end, pivot);

		qsort(array, begin, pivot);
		qsort(array, pivot+1, end);
	}
}

function partition(array, begin, end, pivot) {
	var piv=array[pivot];
	temp = array[pivot];
	array[pivot] = array[end-1];
	array[end-1] = temp;
	var store=begin;
	var ix, temp;
	for(ix=begin; ix<end-1; ++ix) {
		if(array[ix]<=piv) {
			temp = array[store];
			array[store] = array[ix];
			array[ix] = temp;
			++store;
		}
	}
	temp = array[store];
	array[store] = array[end-1];
	array[end-1] = temp;

	return store;
}

function newton_sqareroot(count) {
	/*var x = 10;
	var number;*/
	for (var j=1;j<count;j++) {
		var x = 10;
		var number = j;
		for (var i=0;i<8;i++) {
			x = x - (x*x - number)/(2*x);
		}
	}
}
