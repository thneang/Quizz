package fr.umlv.exercices.exercice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExerciceFactory {
	
	/**
	 * Factory that create an Exercise with the path
	 * @param path Path to the file
	 * @return an Exercice
	 * @throws IOException If path doesn't exist
	 */
	public static Exercice createExercice(Path path) throws IOException {


		try (Stream<String> lines = Files.lines(path)) {
			List<String[]> wordsLine = lines.map(string -> string.split(" "))
					.collect(Collectors.toList());
			List<String> words = new ArrayList<>();
			for(String[] wordsL : wordsLine)
				for(String word : wordsL)
					words.add(word);
			return new Exercice(words);
		}

	}

}