package fr.umlv.evaluation;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

public class EvaluationWord {
	


	/** Compare if the translateWord is good.
	  * @param code The code to evaluate
	  * @return The String result
	  */
	public static boolean getEval(String word, String translateWord)throws Exception{

		return Translate.execute(word, Language.FRENCH, Language.ENGLISH).toLowerCase().equals(translateWord.toLowerCase());
	}
	
	/** Get the translation of the word.
	 * 
	 * @param word
	 * @return
	 * @throws Exception
	 */
	public static String getTranslateWord(String word) throws Exception{
		
		return Translate.execute(word, Language.FRENCH, Language.ENGLISH);
	}

}