package fr.umlv.exercices.exercice;
import fr.umlv.evaluation.EvaluationWord;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Random;

public class Exercice {
	private List<String> words;
	private final Random rand = new Random();
	private final Object lock = new Object();


	Exercice(List<String> words) {
		this.words = words;  // null already checked in create factory

	}
	
	private String getRandomWord(){
		int index = rand.nextInt(words.size());
		return words.get(index);
	}

		
	/**
	 * 
	 * @return The String that represent the Object
	 */
	public String toJSon() {

		synchronized(this.lock){
			String word = getRandomWord();
			JsonObject json = new JsonObject();
			json.put("word", word);
			//Put clue for client
			System.out.println(word);
			try{
			String translateWord = EvaluationWord.getTranslateWord(word);
			System.out.println(translateWord);
			
			json.put("firstLetter", String.valueOf(translateWord.charAt(0)));
			json.put("lengthRes", translateWord.length());
			}catch(Exception e){
				e.printStackTrace();
				//Check later
			}
			return json.encode();
		}
	}



}