/**
 * http://usejsdoc.org/
 */
var gcm = require('node-gcm');
var mongo = require("./mongo");
var mongoURL = "mongodb://localhost:27017/doorpick";

function constructNotification (myarr, type, id){
	 
	var uid;
	mongo.connect(function(err, db){
		if(err){
			console.log('unable to connect to mongo');
			return;
		}else{
			
			var coll = mongo.collection('rental_posting');
			
			for(var i=0; i< myarr.length; i++ ){
				
				
				//console.log(myarr[i].user_id+' uid');
				coll.find( { $and : [ { "description" : { $regex: myarr[i].description } }, 
				                      { "address.City" : { $regex: myarr[i].City } }, 
				                      { "address.Zip" : { $regex: myarr[i].Zip } }, 
				                      { "property_type" : { $regex: myarr[i].property_type } },
				                      { "rent" : { $lt : myarr[i].max_rent, $gt : myarr[i].min_rent } }] } )
				                      .toArray(function(err, docs) {	
					if(docs){	
						
						if(type == 1 && id != null){
							for(var j = 0 ; j<docs.length; j++ ){
								if(docs[j]._id == id){
									console.log('send notification to him');
									if(myarr[i]){
										uid = myarr[i].user_id;
										console.log("hey..");
										console.log(uid);
									}
								}
							}
						}else if(type ==2 || type ==3){
							console.log('weekly and monthly notifications');
						}
//						result.data = docs;
//						result.code = 200; 
//						result.status = "Successful";
						
					}else{						
						console.log('No updates');
					}							
				});
				//console.log(myarr[i].user_id);
			}		
		}
	});
}


function constructNotification1 (myarr, type, id){
	 
	mongo.connect(function(err, db){
		if(err){
			console.log('unable to connect to mongo');
			return;
		}else{
			
			var coll = mongo.collection('rental_posting');
			const uid = myarr.user_id;
			console.log('in constructNotif1 - ' + uid);
			coll.find( { $and : [ { "description" : { $regex: myarr.description } }, 
			                      { "address.City" : { $regex: myarr.City } }, 
			                      { "address.Zip" : { $regex: myarr.Zip } }, 
			                      { "property_type" : { $regex: myarr.property_type } },
			                      { "rent" : { $lt : myarr.max_rent, $gt : myarr.min_rent } }] } )
			                      .toArray(function(err, docs) {	
			                    	  if(docs){
			                    		  //const docs1 = docs;
			                    		  if(type == 1 && id != null){
			                    			  ddoc = docs;
			                    			  for(var j = 0 ; j<ddoc.length; j++ ){
			                    				  if(ddoc[j]._id == id){
			                    					  results = ddoc[j];
			                    					  console.log('send notification to him'+results.user_id);
			                    					  var coll1 = mongo.collection('users');
			                    					  coll1.findOne({ "uid" : uid },function(err, doc){
			                    						  if(err){
			                    							  console.log('error');
			                    						  }else if(doc){
			                    							  console.log('user details');
			                    							  console.log("device " + doc.deviceID);
			                    							 // console.log(results);
			                    							  push(results, 'New Postngs', doc.deviceID);
			                    						  }
			                    					  });
			                    				  }
			                    			  }
			                    		  }else if(type ==2 || type ==3){
			                    			  console.log('weekly and monthly notifications');
			                    			  const resul = docs;
			                    			  console.log(resul);
			                    			  for(var k = 0 ; k < resul.length; k++ ){
		                    					  console.log('send notification to him');
		                    					  var coll1 = mongo.collection('users');
		                    					  coll1.findOne({ "uid" : uid },function(err, doc){
		                    						  if(err){
		                    							  console.log('error');
		                    						  }else{
		                    							  console.log('user details');
		                    							  console.log(doc);
		                    							  console.log(results);
		                    							  push(resul, 'New Postngs', doc.deviceID , function(){
		                    								  console.log('notified users');
		                    							  });
		                    						  }
		                    					  });
			                    				  
			                    			  }
			                    		  }
			                    	  }else{						
			                    		  console.log('No updates');
			                    	  }							
			                      });				
			}
		});
}


function getAllTokens(callback){
	
	mongo.connect(mongoURL, function(){
 		
 		console.log('Connected to mongo at: ' + mongoURL);	
 		var coll = mongo.collection('users');
 		tokens = []
 		coll.find({"driver_status":"1", "user_type" : "driver"}).toArray(function(err, docs) {
 			var myArray = [];
			for(var i=0; i<docs.length; i++){
				myArray.push(docs[i].token);
				console.log("doc length "+docs[i].token);
			}
			callback(myArray);
		});	
 	});	
}

function getScheduleInfo(scid, callback){
	
	mongo.connect(mongoURL, function(){
	 		
		console.log('Connected to mongo at: ' + mongoURL);	
	 	var coll = mongo.collection('users');
	 	
	 	coll.find({"schedules":scid}).toArray(function(err, docs) {
	 		var myArray = [];
			for(var i=0; i<docs.length; i++){
				myArray.push(docs[i]);
				console.log("doc length "+docs[i]);
			}
			callback(myArray);
	 	});	
	});
}

exports.notifyDrivers = function(scid){
	
	getScheduleInfo(scid, function(sinfo){
		console.log('Get sid details '+scid);
		getAllTokens(function(tokens){
			console.log('Tokens '+ tokens);
			push(sinfo, "New request", tokens);
		});
		
	});
	
	
};


function push( msg , header, device_tokens){	
	
    var retry_times = 4; //the number of times to retry sending the message if it fails

    var sender = new gcm.Sender('AIzaSyDORN5DzBzC_mNFTzV9gtWhFnOX8nNqVDc'); //create a new sender
    var message = new gcm.Message(); //create a new message

    message.addData('title', header );
    message.addData('message', msg);
    message.addData('sound', 'notification');

    message.collapseKey = 'testing'; //grouping messages
    message.delayWhileIdle = true; //delay sending while receiving device is offline
    message.timeToLive = 3; //the number of seconds to keep the message on the server if the device is offline

    console.log('in push msg & token : '+device_tokens);
//    console.log(message);
 //   console.log(message + " " + device_tokens  );
    sender.send(message, device_tokens, retry_times, function(result){
        console.log(result);
        console.log('push sent to: ' + device_tokens);
    });
}
	
exports.notify = function(id, type, callback){
	
	console.log("In notify method");
	
	if(type == 1 || type == 2 || type == 3){
		
		console.log(' notify type ' + type);
		console.log(' ID ' + id);
		if(type == 1 && id == null){
			console.log('in callback');
			callback();
		}else{
			mongo.connect(function(err, db){
				if(err){
					console.log('unable to connect to mongo');
					callback();
				}else{
					var searchcol = mongo.collection('search_queries');

					searchcol.find( {  "rate" : type } ).toArray(function(err, docs) {
						if(docs){	
							const dc = docs;
							var myArray = [];
							for(var i=0; i<dc.length; i++){
								//myArray.push({ "user_id":docs[i].user_id, "rate": docs[i].rate, "description":docs[i].description, "City":docs[i].City,"Zip": docs[i].Zip, "Make": docs[i].Make, "property_type":docs[i].property_type, "max_rent":docs[i].max_rent, "min_rent":docs[i].min_rent});
								var ma = dc[i];
								console.log('ma is ');
								console.log(ma)
								constructNotification1(ma, type, id);
							}
//							console.log('length is '+ myArray.length);
//							constructNotification1(myArray, type, id);
							callback();
						}else{						
							console.log('No Docs');
							callback();
						}							
						
					});
					
				}
			});
		}	
	}else{
		console.log('Invalid');
		callback();
	}
};