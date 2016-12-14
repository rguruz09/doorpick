/* GET users listing. */

var mongo = require("./mongo");
var formidable = require('formidable');
var mongoURL = "mongodb://localhost:27017/doorpick";


exports.list = function(req, res){
  res.send('respond with a resource');
};


//function insertDriver(email, lat, long, callback){
//	
//	mongo.connect(mongoURL, function(){
// 		
// 		console.log('Connected to mongo at: ' + mongoURL);	
// 		var coll = mongo.collection('driverloc');
// 		
// 		coll.insert({"email":email,"lat":lat,"long":long}, function(err, result){
//					if(err){
//						callback(true, false);
//					}else{
//						callback(false, true);
//					}
//				});
// 	});
//}

function updateDriver(email, lat, long, sts, callback){
	
	mongo.connect(mongoURL, function(){
		
		var coll = mongo.collection('users');
		coll.findOne( {"email": email}, function(err, docs) {
			if(docs){
				coll.update({"email":email},{$set : {"lat": lat, "long":long, "driver_status":sts}}, 
							function(err, user){
						if (user) {
							callback(true, false);
						} else {
							callback(false, true);
						}
					});
			}else{	
				callback(false, true);
			}							
		});
	});
}


exports.adduser = function(req, res){
	
	console.log("This is a UpdatePost's status API call");
	
	var results = {};
	
	var form = new formidable.IncomingForm();
	
	form.parse(req, function(err, fields, files) {
	    if(err){
	       console.log(err);
	       res.end("sorry, an error occurred");
	       return;
	    }
	    var ufname = fields.ufname.replace(/[\n\t\r]/g,"");
	 	var ulname = fields.ulname.replace(/[\n\t\r]/g,"");
	 	var email = fields.email.replace(/[\n\t\r]/g,"");
	 	var mob = fields.mob.replace(/[\n\t\r]/g,"");
	 	var st_address = fields.st_address.replace(/[\n\t\r]/g,"");
	 	var city = fields.city.replace(/[\n\t\r]/g,"");
	 	var state = fields.state.replace(/[\n\t\r]/g,"");
	 	var zip = fields.zip.replace(/[\n\t\r]/g,"");
	 	var password = fields.password.replace(/[\n\t\r]/g,"");
	 	var user_type = fields.user_type.replace(/[\n\t\r]/g,"");
	 	var token = fields.token.replace(/[\n\t\r]/g,"");
	 	var lat = fields.lat.replace(/[\n\t\r]/g,"");
	 	var long = fields.long.replace(/[\n\t\r]/g,"");
	 	
	 	console.log("user_type"+user_type);
	 	
	 	mongo.connect(mongoURL, function(){
	 		
	 		console.log('Connected to mongo at: ' + mongoURL);	
	 		var coll = mongo.collection('users');
	 		
	 		coll.findOne( {"email": email}, function(err, docs) {
	 			if(docs){
	 				results.statusCode = 202;
	 				results.message = "User already registered!!";
	 				res.json(results);
	 			}else{	
	 				coll.insert({"ufname":ufname,"ulname":ulname,"email":email,
	 					"st_address":st_address,"city":city,"state":state,"zip":zip,
	 					"mob":mob,"password":password,"user_type":user_type, "token":token}, function(err, result){
	 						if(err){
	 							results.statusCode = 204;
	 							results.message = "Mongo Failed";
	 							res.json(results);
	 						}else{
	 							if(user_type === "driver"){
	 								updateDriver(email, lat, long, "0", function(yes,no) {
		 								results.statusCode = 200;
			 							results.message = "User added successfully!!";
			 							res.json(results);
		 							});
	 							}
	 							else{
	 								results.statusCode = 200;
		 							results.message = "User added successfully!!";
		 							res.json(results);
	 							}
	 						}
	 					});
	 			}							
	 		});
	 	});
	     
	});
	     
};

exports.signInUser = function(req, res){
	
	console.log("This is a UpdatePost's status API call");
	
	var form = new formidable.IncomingForm();
	
	var results = {};
	form.parse(req, function(err, fields, files) {
	     if(err){
	       console.log(err);
	       res.end("sorry, an error occurred");
	       return;
	     }
	 	var email = fields.email.replace(/[\n\t\r]/g,"");
		var password = fields.password.replace(/[\n\t\r]/g,"");
		var token = fields.token.replace(/[\n\t\r]/g,"");
		var lat = fields.lat.replace(/[\n\t\r]/g,"");
	 	var long = fields.long.replace(/[\n\t\r]/g,"");
		
		if(token){
			mongo.connect(mongoURL, function(){
				
				//console.log('Connected to mongo at: ' + mongoURL);	
				var coll = mongo.collection('users');
				console.log("user"+email);
				console.log("password"+password);
				coll.findOne( {"email": email, "password":password}, function(err, docs) {
					if(docs){
						
						coll.update({"email":email},{$set : {"token": token}}, 
									function(err, user){
								if (user) {
									
									updateDriver(email, lat, long, "1", function(yes,no) {
										results.statusCode = 200;
										results.message = "Success";
										results.data = docs;
										res.json(results);
									
									});
								} else {
									results.statusCode = 208;
									results.message = "Failed for token";
									res.json(results);
								}
							});
					}else{	
						results.statusCode = 206;
						results.message = "Failed";
						res.json(results);
					}							
				});
			});
		}
		else{
			results.statusCode = 209;
			results.message = "Token empty";
			res.json(results);
		}
	});	
};

exports.updateDriverLoc = function(req, res){
	
	var form = new formidable.IncomingForm();
	
	var results = {};
	form.parse(req, function(err, fields, files) {
	     if(err){
	       console.log(err);
	       res.end("sorry, an error occurred");
	       return;
	     }
	 	var email = fields.email;
		var lat = fields.lat;
		var long = fields.long;
		
		mongo.connect(mongoURL, function(){
			
			var coll = mongo.collection('users');
			coll.findOne( {"email": email}, function(err, docs) {
				if(docs){
					coll.update({"email":email},{$set : {"lat": lat, "long":long}}, 
								function(err, user){
							if (user) {
								results.statusCode = 200;
								results.message = "Success";
								res.json(results);
							} else {
								results.statusCode = 208;
								results.message = "Failed to update loc";
								res.json(results);
							}
						});
				}else{	
					results.statusCode = 206;
					results.message = "Failed";
					res.json(results);
				}							
			});
		});
		
	});
};

exports.getAvailDrivers = function(req, res){
	
	var results = {};
		
	var form = new formidable.IncomingForm();
		
	form.parse(req, function(err, fields, files) {
		    if(err){
		    	console.log(err);
		    	res.end("sorry, an error occurred");
		    	return;
		    }
		    
		    mongo.connect(mongoURL, function(db){
		 		
		 		if(db){
		 			
			 		var coll = mongo.collection('users');			 			 		

			 		coll.find({"driver_status":"1", "user_type" : "driver"}).toArray(function(err, docs) {
						if(docs){												
							var myArray = [];
							console.log("doc length "+docs.length);
							for(var i=0; i<docs.length; i++){
								myArray.push(docs[i]);
								console.log("doc length "+docs[i]);
							}
							results.statusCode = 200;
							results.message = "request added";
							results.data = myArray;
							res.json(results);	
						}else{						
							results.statusCode = 201;
							results.message = "No schedules";
							res.json(results);
						}	
						db.close();
					});
		 			
		 		}else{
		 			results.statusCode = 210;
					results.message = "Mongo error";
					res.json(results);
		 		}
		 	});
		    
		});
	};
