package generateOccurrence;

import java.io.*;
import java.util.*;

import domain.Occurrence;
import domain.Pattern;
import domain.Seed;
import domain.Structure;

/**
 * 算法思想
 *   1.提供seed ok 
 *   2.寻找实例
 *   3.通用模式（数据结构）
 *  （prefix，author，middle，book，suffix，order，url）
 *   4.寻找模式匹配 
 *   (order, url, prefix,middle, suffix)
 *   5.找到足够的元组后退出，否则重复2
 * */

public class Preprocessor {

	public Preprocessor() {

	}

	/**
	 * 按行以空格或者Tab分割句子
	 * */
	public String[] lineSpilt(String line) {
		String[] splited = line.split(" |	");
		return splited;
	}

	/**
	 * 分割种子库，将种子对提取出来
	 * */
	@SuppressWarnings("resource")
	public List<Seed> spiltedSeeds(String pathname) {

		String seedf = "";
		String seedl = "";
		String symbol = "";

		Seed seed = new Seed();
		List<Seed> seeds = new ArrayList<>();

		try {
			// String path = "D:/PPPPPPPractice/big_pair.txt";
			File file = new File(pathname);
			if (file.isFile() && file.exists()) {
				InputStream inputStream = new FileInputStream(file);
				InputStreamReader reader = new InputStreamReader(inputStream,"gbk");
				//InputStreamReader reader = new InputStreamReader(inputStream,"utf-8");
				BufferedReader bufferedReader = new BufferedReader(reader);

				String line = ""; // 用来存储按行读取的文本内容
				String[] spilted; // spilted数组来存储分割完毕的种子对

				while ((line = bufferedReader.readLine()) != null) {
					spilted = lineSpilt(line);

					seedf = spilted[1];
					seedl = spilted[2];
					symbol = spilted[3];
					seed = new Seed(symbol,seedf,seedl); 
					seeds.add(seed);

					//System.out.println(seed);
				}
				// System.out.println("ok");

				// for(Seed str : seeds){
				// System.out.println(str);
				// }

			} else {
				System.out.println("this file is not exist");
			}
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return seeds;
	}

	/**
	 * 将句子分割抽取出来的结果存入List中
	 * */
	@SuppressWarnings("resource")
	public List<Occurrence> spiltedSentences(String pathname, List<Seed> seeds) {
		// String pathname="D:/PPPPPPPractice/ckxx_jieba_seg.txt";
		File file = new File(pathname);
		BufferedInputStream fis;

		Occurrence occurrence = new Occurrence();
		List<Occurrence> listOccurrence = new ArrayList<>();

		try {
			fis = new BufferedInputStream(new FileInputStream(file));
			//BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "gbk"), 5 * 1024 * 1024);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "utf-8"), 5 * 1024 * 1024);

			String line = "";
			String[] spilted;
			int tag = 0; // 标记句号的位置


			while ((line = reader.readLine()) != null) {
				// TODO：write your business
				spilted = lineSpilt(line);
				//System.out.println(line);
				for (int i = 0; i < spilted.length; i++) {
					List<String> sentences = new ArrayList<>();

					// System.out.print(spilted[i]+" ");
					if (spilted[i].equals("。")) {
						for (int j = tag; j <= i; j++) {
							sentences.add(spilted[j]);
							// System.out.print(""+spilted[j]+"/");
						}
						// System.out.println();
						tag = i + 1;
						int pos = -1;
						int posa = -1;
						int posb = -1;

						boolean ordera = false;
						boolean orderb = false;
						boolean order = false;

						for (pos = 0; pos < sentences.size(); pos++) {

							List<String> prefix = new ArrayList<>();
							String seedf = "";
							List<String> middle = new ArrayList<>();
							String seedl = "";
							List<String> suffix = new ArrayList<>();
							String symbol ="";

							int num = -1;

							for (int flag = 0; flag < seeds.size(); flag++) {
								// System.out.print(sentences.get(pos).equals(strSeed.getSeedfirst())+"|");
								if (sentences.get(pos).equals(seeds.get(flag).getSeedfirst()) == true) {
									// System.out.print("first\\\\\\\\\\\\\\\\\\\\|"+"posa="+pos+"||");
									posa = pos;
									ordera = true;

									String trait = seeds.get(flag).getSeedlast();

									sign: 
										while (!sentences.get(pos).equals(trait)) {
											pos++;
											if (pos + 1 == sentences.size()) {
												break sign;
											}
										}

									if (sentences.get(pos).equals(trait)) {
										posb = pos;
										orderb = true;
									}

									if (ordera && orderb) {
										order = true;
										for (int a = 0; a < posa; a++) {
											prefix.add(sentences.get(a));
										}
										// System.out.println();
										// for(String str:prefix){
										// System.out.print("**"+str+"**");
										// }
										// System.out.println();

										seedf = sentences.get(posa);

										for (int a = posa + 1; a < posb; a++) {
											middle.add(sentences.get(a));
										}
										// System.out.println();
										// for(String str:middle){
										// System.out.print("**"+str+"**");
										// }
										// System.out.println();

										seedl = sentences.get(posb);
										for (int a = posb + 1; a < sentences
												.size(); a++) {
											suffix.add(sentences.get(a));
										}
										// System.out.println();
										// for(String str:suffix){
										// System.out.print("**"+str+"**");
										// }
										// System.out.println();
										symbol = seeds.get(flag).getSymbol();
										occurrence = new Occurrence(prefix,seedf, middle, seedl, suffix,symbol,order, num);
										listOccurrence.add(occurrence);
										//System.out.println(occurrence.toString());
										//System.out.println();

										order = false;

									} else {
										order = false;
										// System.out.print("failure|");
									}
									ordera = false;
									orderb = false;
								}
								// pos=pos-1;
							}
						}

					}
				}
				// System.out.println("=============");
				// System.out.println("一行句子读完啦");
				// System.out.println("=============");
				//
				tag = 0;


			}
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

		//System.out.println();

		return listOccurrence;
	}



	public  void findOccurrence(List<Occurrence> occurrence,List<Seed> seed) {
		List<Structure> structures = new ArrayList<>();
		Structure structure = new Structure();
		
		List<Structure> AAA = new ArrayList<>();
		Structure aaa = new Structure();
		
		List<Pattern> ssstrs = new ArrayList<>();
		Pattern ssstr = new Pattern();

		String inPrefix="";
		String inMiddle="";
		String inSuffix="";
		boolean inOrder=false;
		int inNum = 1;

		
		structures.add(structure);
		ssstrs.add(ssstr);

		//int flag = 1;
		//int flag1 = 1;
		
		boolean rrr = false;
		
		//boolean trait = false;
		boolean trait1 = false;
		
		for(Occurrence str: occurrence){
			
			System.out.println("------------------------------------------------------");
			System.out.println(str);
			System.out.println("种子：("+str.getSeedf()+","+str.getSeedl()+","+str.getSymbol()+")");
			Iterator<String> it1 = str.getPrefix().iterator();
			while(it1.hasNext()){
				String ss = (String) it1.next();
				if(ss.equals("，")||ss.equals("。")||ss.equals("")||ss.equals("、")||ss.equals("【")||ss.equals("】")||ss.equals("《")||ss.equals("》")||ss.equals("‘")||ss.equals("’")){
					it1.remove();
				}
			}
			System.out.println("前缀："+str.getPrefix());
			Iterator<String> it2 = str.getMiddle().iterator();
			while(it2.hasNext()){
				String ss = (String) it2.next();
				if(ss.equals("，")||ss.equals("。")||ss.equals("")||ss.equals("、")||ss.equals("【")||ss.equals("】")||ss.equals("《")||ss.equals("》")||ss.equals("‘")||ss.equals("’")){
					it2.remove();
				}
			}
			System.out.println("中缀："+str.getMiddle());
			Iterator<String> it3 = str.getSuffix().iterator();
			while(it3.hasNext()){
				String ss = (String) it3.next();
				if(ss.equals("，")||ss.equals("。")||ss.equals("")||ss.equals("、")||ss.equals("【")||ss.equals("】")||ss.equals("《")||ss.equals("》")||ss.equals("‘")||ss.equals("’")){
					it3.remove();
				}
			}
			System.out.println("后缀："+str.getSuffix());
			System.out.println(str);
			System.out.println("ok");
			System.out.println("------------------------------------------------------");
			
			
			
			
			//ListIterator<Structure> listIt = structures.listIterator();
			//ListIterator<Pattern> listIt2 = ssstrs.listIterator();
			
			
			for(int i=0;i<str.getPrefix().size();i++){
				for(int j=0;j<str.getMiddle().size();j++){
					for(int k=0;k<str.getSuffix().size();k++){
						//System.out.println("+++++++++++++++++++++++++++++");
						//System.out.print("i="+i+" j="+j+" k="+k+" ");
						
						
						//System.out.println();
						//System.out.println("("+str.getPrefix().get(i)+","+str.getMiddle().get(j)+","+str.getSuffix().get(k)+")");							
						
						inPrefix = str.getPrefix().get(i);
						inMiddle = str.getMiddle().get(j);
						inSuffix = str.getSuffix().get(k);
						
						aaa = new Structure(str.getSeedf(),str.getSeedl(),str.getSymbol(),inPrefix,inMiddle,inSuffix,inOrder,inNum);
						AAA.add(aaa);
						
						//for(int h=0;h<structures.size();h++){
						//System.out.println("structures.size()="+structures.size());						
						//for(Structure temp :structures){
					
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
						/*System.out.println("'''"+structures.size());
						for(int h =0;h<structures.size();h++){
							System.out.println("======go in  Structure=====");
							if(inPrefix.equals(structures.get(h).getInprefix())&&inMiddle.equals(structures.get(h).getInmiddle())&&inSuffix.equals(structures.get(h).getInsuffix())){	
								System.out.println("begin1111111111111111");		
								trait = true;
								structures.get(h).setInnum(++flag);
								structures.get(h).setInorder(trait);
								System.out.println("end1111111111111111");
								trait = false;
								break;
	
							}
							else{
								if(h+1==structures.size()){
									rrr = true;
								}
								
								if(rrr){
									System.out.println("begin2222222222222222");
									//if()
									structure = new Structure(str.getSeedf(),str.getSeedl(),str.getSymbol(),inPrefix,inMiddle,inSuffix,inOrder,inNum);
									structures.add(structure);
									
									
									for(Structure temp1 :structures){
										System.out.print(temp1);
										System.out.println("|");
									}
									
									System.out.println("end2222222222222222");
									//continue;
									rrr=false;
									break;
								}
								
								else{
									
								}
								
							}
							System.out.println("======go out Structure=====");
						}*/
						
					
						//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
						/*while(listIt2.hasNext()){
							Pattern ssstru = (Pattern)listIt2.next();
							System.out.println("======go in  Pattern======");
							//System.out.println("??"+temp+"??");
							//System.out.print("+");
					

							//if(temp.getInprefix().equals(inPrefix)&&temp.getInmiddle().equals(inMiddle)&&temp.getInsuffix().equals(inSuffix)){
							//if(inPrefix.equals(temp.getInprefix())&&inMiddle.equals(temp.getInmiddle())&&inSuffix.equals(temp.getInsuffix())){
							//if(inPrefix.equals(structures.get(h).getInprefix())&&inMiddle.equals(structures.get(h).getInmiddle())&&inSuffix.equals(structures.get(h).getInsuffix())){
							//if(inPrefix.equals(stru.getInprefix())&&inMiddle.equals(stru.getInmiddle())&&inSuffix.equals(stru.getInsuffix())){
							if(inPrefix.equals(ssstru.getPrefix())&&inMiddle.equals(ssstru.getMiddle())&&inSuffix.equals(ssstru.getSuffix())){
									
								System.out.println("begin1111111111111111");		
								trait1 = true;
								//structures.get(h).setInnum(flag++);
								//stru.setInnum(flag++);
								ssstru.setNum(flag1++);
								//temp.setInnum(flag++);
								//structures.get(h).setInorder(trait);
								//stru.setInorder(trait);
								ssstru.setOrder(trait1);
								//temp.setInorder(trait);
								System.out.println("end1111111111111111");

							}
							else{
								System.out.println("begin2222222222222222");
								
								//structure = new Structure(str.getSeedf(),str.getSeedl(),str.getSymbol(),inPrefix,inMiddle,inSuffix,inOrder,inNum);
								ssstr = new Pattern(str.getSymbol(),inPrefix,inMiddle,inSuffix,inOrder,inNum);
								
								//structures.add(structure);
								//listIt.add(structure);
								listIt2.add(ssstr);

								//for(Structure temp1 :structures){
								//	System.out.print(temp1);
								//	System.out.println("|");
								//}
								for(Pattern temp1 :ssstrs){
									System.out.print(temp1);
									System.out.println("|");
								}
								System.out.println("end2222222222222222");

							}
							System.out.println("======go out Pattern======\n");
						}*/
						
						//System.out.println("'''"+ssstrs.size());
						for(int h =0;h<ssstrs.size();h++){
							//System.out.println("======go in  Pattern=====");
							if(inPrefix.equals(ssstrs.get(h).getPrefix())&&inMiddle.equals(ssstrs.get(h).getMiddle())&&inSuffix.equals(ssstrs.get(h).getSuffix())){	
								//System.out.println("begin1111111111111111");		
								trait1 = true;
								ssstrs.get(h).setNum(ssstrs.get(h).getNum()+1);
								ssstrs.get(h).setOrder(trait1);
								//System.out.println("end1111111111111111");
								trait1 = false;
								break;
	
							}
							else{
								if(h+1==ssstrs.size()){
									rrr = true;
								}
								
								if(rrr){
									//System.out.println("begin2222222222222222");
									//if()
									ssstr = new Pattern(str.getSymbol(),inPrefix,inMiddle,inSuffix,inOrder,inNum);
									ssstrs.add(ssstr);
									
									//for(Pattern temp1 :ssstrs){
									//	System.out.print(temp1);
									//	System.out.println("|");
									//}
									
									//System.out.println("end2222222222222222");
									//continue;
									rrr=false;
									break;
								}
								
								
							}
							//System.out.println("======go out Pattern=====");
							
						}
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
							
					}
					
					
				}
				
			}
			
		}
		//System.out.println("kakakakakakakakakakakakakakakakakakaka");
		//System.out.println();
		//System.out.println("============charpter1=================");
		//for(Structure str:AAA){
		//	System.out.println(str);
		//}
		//System.out.println("************charpter2*****************");
		//for(Structure str:structures){
		//		System.out.println(str);
		//}
		//System.out.println();
		//System.out.println("=============charpter3=================");
		//for(Pattern str:ssstrs){
		//	System.out.println(str);
		//}
		System.out.println("=============charpter4=================");
		for(Pattern str:ssstrs){
			if(str.getOrder()){
			System.out.println(str);
			}
		}
		
	}



	public void matchOccurrence(){

	}

}
