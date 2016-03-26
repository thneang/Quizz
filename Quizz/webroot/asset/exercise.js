 
 $(document).ready(function() {


		function putImputToEvaluateCode(theStart,theEnd){
		
			for (var j = theStart; j < theEnd ; j++) {
				console.log('myForm'+j);

				const t = j;
				
				document.getElementById('myForm'+t).addEventListener('submit', function(e) {
				
					var $form = $( this )
					var term =$form.find( "textarea[name='translateWord']" ).val();
					console.log("term : %s",term);
					$.ajax({ url : "/api/reponse", 
						type: "POST", 
							crossDomain: true,
							data : JSON.stringify({translateWord: term}), 
							dataType : 'json', 
							success:function(data) {
								console.log("items : %s",data.bidule);
								console.log( '"#myAnswerEvaluated'+t+'"' ); 
								
								$( '#myAnswerEvaluated'+t ).empty().append(data.answer);
							}, 
							error: function( error ) { 
								alert( "Error"+error );
							} 
						});
						e.preventDefault();

					}, true);


				} 
			}






    var element = $("#listExercise");
    var url = window.location.search;
  	var exerciseName = url.substring(url.lastIndexOf("=")+1);
	

	var exerciceRequest = '/exercise/'+exerciseName;
		
 
 // Get the URL of the page
 var str = document.location.href ;
 var prefixeUrl = str.substring(0,str.indexOf('exer'));

var eb = new vertx.EventBus(prefixeUrl+'eventbus');


 eb.onopen = function() {

 

     eb.registerHandler('server-client', function(message) {
		 	console.log("info recu pour changement 1");
      console.log('received a message: ' + JSON.stringify(message));
      var jsonDataNew = JSON.parse(message);
      console.log(jsonDataNew.type);
      console.log(jsonDataNew.name);
      
      // Check if the message is for us
       if( ( jsonDataNew.name !==  exerciseName ) || ( jsonDataNew.type == 'ENTRY_CREATE' ) ) return ;
       // we don"'t check if is it a created exercices because is impossible that a browser on an exercice not yet added.
       
       
       
       
       console.log(jsonDataNew.type);
       //If the exercise is deleted, we can't continue.
       if( jsonDataNew.type === 'ENTRY_DELETE') window.close();
       
		
       

    });
    
 };
 // --------------
 
 
 
 
 
	
window.onbeforeunload = function(){
		var closeExerciceRequest = '/closeexercise/'+exerciseName;
		$.get(closeExerciceRequest,function(list) {
		});

  return null;
};
	function getHandler(exercise) {


			console.log(exercise);

			var jsonData = JSON.parse(exercise);
			
			console.log(point);

			console.log(jsonData.word);
			word = jsonData.word;
			console.log(word);
			element.empty();

			element.append('<li id = "Points">'+ 'Vous avez  ' + point +'/20 points !</li>' );
			element.append('<li >'+ 'Traduisez le mot : ' + jsonData.word +'</li>' );
			element.append('<li >'+ 'Voici la première lettre : ' + jsonData.firstLetter +'</li>' );
			element.append('<li >'+ 'Il y a : ' + jsonData.lengthRes + ' lettres </li>' );
			

			

				
	}

	
	var point = 10;
	//Word to translate
	var word;
	var form = $("#form");
	form.append('<form id="myForm'+ '"><br /><input type="submit" value="Submit !" /><br /> <input id="answer" type="text" name="translateWord" value=""><br><p id ="myAnswerEvaluated' + '"></p> </form>');
	
	document.getElementById('myForm').addEventListener('submit', function(e) {	
		var $form = $( this )
		var term =$form.find( "input[name='translateWord']" ).val();

		console.log("term : %s",term);
		$.ajax({ url : "/api/reponse", 
				type: "POST", 
				crossDomain: true,
				data : JSON.stringify({word: word,translateWord: term}), 
				dataType : 'json', 
				success:function(data) {
					console.log("items : %s",data.answer);
					console.log( '"#myAnswerEvaluated' );

					if(data.answer == true){
						$( '#myAnswerEvaluated').empty().append("Trouvé !")
						point++;
						if(point >= 20){
							$( '#myAnswerEvaluated').empty().append("Bravo ! Vous avez gagné !");
							point = 10;
						}
						$.get(exerciceRequest,function(exercise){
							getHandler(exercise);
						});
					}
					else{
						point--;
						var points = $("#Points");
						points.empty().append('Vous avez  ' + point +'/20 points !');
						$( '#myAnswerEvaluated').empty().append("Raté ! Vous perdez 1 point ! Réeesayer !");
						if(point <= 0){
							$( '#myAnswerEvaluated').empty().append("Désolé ! Vous avez perdu !");
							point = 10;
							$.get(exerciceRequest,function(exercise){
								getHandler(exercise);
							});
						}
						
					}

				}, 
				error: function( error ) { 
					alert( "Error"+error );
				} 
			});
			e.preventDefault();

		}, true);

		
	
	$.get(exerciceRequest,function(exercise){
		getHandler(exercise);
	});
	
	
});
   
   
   
   
   
   




