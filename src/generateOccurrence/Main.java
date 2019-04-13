package generateOccurrence;


import java.util.List;

import domain.AOccurrence;
import domain.Occurrence;
import domain.Seed;


public class Main {
	
	public static void main(String[] args){
		Preprocessor preprocessor = new Preprocessor();
		APreprocessor apreprocessor = new APreprocessor();
		
		//String pathSeed = "D:/PPPPPPPractice/small_pair.txt"; 
		//String pathSentence = "D:/PPPPPPPractice/sentences.txt";  //gbk
		//String pathSentence = "D:/PPPPPPPractice/bbb.txt";  //gbk
		String pathSeed = "D:/PPPPPPPractice/h.txt"; //gbk
		//String pathSeed = "D:/PPPPPPPractice/big_pair.txt";//utf-8
		String pathSentence = "D:/PPPPPPPractice/ckxx_jieba_seg.txt";//utf-8
		System.out.println("-----------------start------------------");
		List<Seed> seeds = preprocessor.spiltedSeeds(pathSeed);
		List<Occurrence> occurrence = preprocessor.spiltedSentences(pathSentence,seeds);
		
		//System.out.println("#################middle#################");
		
		//List<Seed> aseeds = apreprocessor.spiltedSeeds(pathSeed);
		//List<AOccurrence> aoccurrence = apreprocessor.spiltedSentences(pathSentence, aseeds);
		
		preprocessor.findOccurrence(occurrence,seeds);
		System.out.println("------------------end-------------------");
		
	}
}
