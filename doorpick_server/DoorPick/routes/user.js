/* GET users listing. */

var mongo = require("./mongo");
var formidable = require('formidable');
var mongoURL = "mongodb://localhost:27017/doorpick";


exports.list = function(req, res){
  res.send('respond with a resource');
};


exports.adduser = function(req, res){
	
	console.log("This is a UpdatePost's status API call");
	
	results = {}
	
	var form = new formidable.IncomingForm();
	
	form.parse(req, function(err, fields, files) {
	     if(err){
	       console.log(err);
	       res.end("sorry, an error occurred");
	       return;
	     }
	     var ufname = fields.param("ufname");
		 	var ulname = fields.param("ulname");
		 	var email = fields.param("email");
		 	var mob = fields.param("mob");
		 	var st_address = fields.param("st_address");
		 	var city = fields.param("city");
		 	var state = fields.param("state");
		 	var zip = fields.param("zip");
		 	var password = fields.param("password");
		 	var user_type = fields.param("user_type");
		 	var token = fields.param("token");
	
		 	console.log("user_type"+user_type)
		 	
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
	
var form = new formidable.IncomingForm();
	
	results = {}
	form.parse(req, function(err, fields, files) {
	     if(err){
	       console.log(err);
	       res.end("sorry, an error occurred");
	       return;
	     }
	 	var email = req.param("email");
		var password = req.param("password");
		var token = req.param("token");
		
		if(token){
			mongo.connect(mongoURL, function(){
				
				console.log('Connected to mongo at: ' + mongoURL);	
				var coll = mongo.collection('users');
				
				coll.findOne( {"email": email, "password":password}, function(err, docs) {
					if(docs){
						coll.update({"email":email},{$set : {"token": token}}, 
									function(err, user){
								if (user) {
									results.statusCode = 200;
									results.message = "Success";
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
	     
	});
	
};
