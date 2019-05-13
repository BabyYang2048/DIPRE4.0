package generateOccurrence;


import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import domain.AOccurrence;
import domain.Occurrence;
import domain.PatternA;
import domain.Seed;


public class Main {
	
	public static void main(String[] args){
		
		Preprocessor preprocessor = new Preprocessor();
		//APreprocessor apreprocessor = new APreprocessor();
		
		String pathSeed = "D:/PPPPPPPractice/small_pair.txt"; 
		//String pathSentence = "D:/PPPPPPPractice/sentences.txt";  //gbk
		//String pathSentence = "D:/PPPPPPPractice/bbb.txt";  //utf-8
		//String pathSentence = "D:/PPPPPPPractice/ccc.txt";  //utf-8
		//String pathSeed = "D:/PPPPPPPractice/m.txt"; //gbk
		//String pathSeed = "D:/PPPPPPPractice/big_pair.txt";//utf-8
		String pathSentence = "D:/PPPPPPPractice/newSentences.txt";
		//String pathSentence = "D:/PPPPPPPractice/ckxx_jieba_seg.txt";//utf-8
		System.out.println("-----------------start------------------");
		List<Seed> seeds = preprocessor.spiltedSeeds(pathSeed);
		List<Occurrence> occurrence = preprocessor.spiltedSentences(pathSentence,seeds);
		
		//System.out.println("#################middle#################");
		
		//List<Seed> aseeds = apreprocessor.spiltedSeeds(pathSeed);
		//List<AOccurrence> aoccurrence = apreprocessor.spiltedSentences(pathSentence, aseeds);
		
		List<PatternA> ssstrs = preprocessor.findOccurrence(occurrence);
		preprocessor.matchOccurrence(ssstrs,pathSentence,seeds);
		
		System.out.println("------------------end-------------------");
	
			
	}
}
