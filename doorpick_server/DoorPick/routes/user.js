/* GET users listing. */

var mongo = require("./mongo");
var formidable = require('formidable');
var mongoURL = "mongodb://localhost:27017/doorpick";


exports.list = function(req, res){
  res.send('respond with a resource');
};


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
	     var ufname = fields.ufname;
		 	var ulname = fields.ulname;
		 	var email = fields.email;
		 	var mob = fields.mob;
		 	var st_address = fields.st_address;
		 	var city = fields.city;
		 	var state = fields.state;
		 	var zip = fields.zip;
		 	var password = fields.password;
		 	var user_type = fields.user_type;
		 	var token = fields.token;
	
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
		 							results.statusCode = 200;
		 							results.message = "User added successfully!!";
		 							res.json(results);
		 						}
		 					});
		 			}							
		 		});
		 	});
	     
	});
	     
};


exports.signInUser = function(req, res){
	
	console.log("This is a UpdatePost's status API call");
	
//	var form = new formidable.IncomingForm();
//	
//	var results = {};
//	form.parse(req, function(err, fields, files) {
//	     if(err){
//	       console.log(err);
//	       res.end("sorry, an error occurred");
//	       return;
//	     }
//	 	var email = fields.email;
//		var password = fields.password;
//		var token = fields.token;
//		
//		if(token){
//			mongo.connect(mongoURL, function(){
//				
//				//console.log('Connected to mongo at: ' + mongoURL);	
//				var coll = mongo.collection('users');
//				console.log("user"+email);
//				console.log("password"+password);
//				coll.find( {"email": email, "password":password}, function(err, docs) {
//					if(docs){
//						coll.update({"email":email},{$set : {"token": token}}, 
//									function(err, user){
//								if (user) {
//									results.statusCode = 200;
//									results.message = "Success";
//									results.data = docs;
//									res.json(results);
//								} else {
//									results.statusCode = 208;
//									results.message = "Failed for token";
//									res.json(results);
//								}
//							});
//					}else{	
//						results.statusCode = 206;
//						results.message = "Failed";
//						res.json(results);
//					}							
//				});
//			});
//		}
//		else{
//			results.statusCode = 209;
//			results.message = "Token empty";
//			res.json(results);
//		}
//	     
//	});
	

	var results = {};
	var email = req.param("email");
	var password = req.param("password");
	var token = req.param("token");
	
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
								results.statusCode = 200;
								results.message = "Success";
								results.data = docs;
								res.json(results);
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
	
};
