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
		//System.out.println("=========go in  spiltedSentences========");
		//String pathname="D:/PPPPPPPractice/ckxx_jieba_seg.txt";
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
				//System.out.println("按行读取的句子："+line);
				for (int i = 0; i < spilted.length; i++) {
					List<String> sentences = new ArrayList<>();

					// System.out.print(spilted[i]+" ");
					if (spilted[i].equals("。")) {
						//System.out.print("[");
						for (int j = tag; j <= i; j++) {
							sentences.add(spilted[j]);
							//System.out.print(""+spilted[j]+",");
						}
						//System.out.println("]");
						// System.out.println();
						tag = i + 1;
						int pos = -1;
						int posa = -1;
						int posb = -1;

						boolean ordera = false;
						boolean orderb = false;
						boolean order = false;

						for (pos = 0; pos < sentences.size(); pos++) {

							String prefix = "";
							String seedf = "";
							String middle = "";
							String seedl = "";
							String suffix = "";
							String symbol = "";

							int num = -1;

							for (int flag = 0; flag < seeds.size(); flag++) {
								//System.out.println("flag="+flag);
								//System.out.println(sentences.get(pos));
								//System.out.println(seeds.get(flag).getSeedfirst());
								//System.out.print(sentences.get(pos).equals(seeds.get(flag).getSeedfirst())+"|");
								if (sentences.get(pos).equals(seeds.get(flag).getSeedfirst()) == true) {
									//System.out.print("first\\\\\\\\\\\\\\\\\\\\|"+"posa="+pos+"||");
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
										//System.out.println();
										//System.out.println(sentences);
										order = true;
										//System.out.println("true|");
										if(posa!=0){
											int a=posa;
											String ss=sentences.get(posa-1);
											//System.out.println(ss+"");
											while(ss.equals(" ")||ss.equals("，")||ss.equals("。")||
													ss.equals("！")||ss.equals("、")||ss.equals("【")||
													ss.equals("】")||ss.equals("《")||ss.equals("》")||
													ss.equals("‘")||ss.equals("’")||ss.equals("“")||
													ss.equals("”")||ss.equals("\"")||ss.equals("（")||
													ss.equals("）")||ss.equals("：")){
												a--;
												//System.out.println(ss+"	"+a);
												//System.out.println();
												if(a>0){
													ss=sentences.get(a-1);
												}
												else{
													break;
												}
											}
											//System.out.println();
											//System.out.println(ss+"	"+a);
											if(a>0){
												prefix = sentences.get(a-1);
											}
											else{
												prefix = "";
											}
										}
										else{
											prefix = "";
										}
										seedf = sentences.get(posa);
										for (int a = posa + 1; a < posb; a++) {
											//middle.add(sentences.get(a));
											middle +=sentences.get(a);
											//System.out.println(sentences.get(a));
										}
										//System.out.println(middle);
										seedl = sentences.get(posb);
										if(posb!=sentences.size()-1){
											int b=posb;
											String ss=sentences.get(posb+1);
											//System.out.println(ss);
											while(ss.equals(" ")||ss.equals("，")||ss.equals("。")||
													ss.equals("！")||ss.equals("、")||ss.equals("【")||
													ss.equals("】")||ss.equals("《")||ss.equals("》")||
													ss.equals("‘")||ss.equals("’")||ss.equals("“")||
													ss.equals("”")||ss.equals("\"")||ss.equals("（")||
													ss.equals("）")||ss.equals("：")){
												b++;
												if(b<sentences.size()-1){
													ss=sentences.get(b+1);
												}
												else{
													break;
												}
												
											}
											if(b<sentences.size()-1){
												suffix = sentences.get(b+1);
											}
											else{
												suffix="";
											}
//											suffix = sentences.get(posb+1);
										}
										else{
											suffix = "";
										}
										symbol = seeds.get(flag).getSymbol();
										
										occurrence = new Occurrence(prefix,seedf, middle, seedl, suffix,symbol,order, num);
										listOccurrence.add(occurrence);
										
										//System.out.println("occurrence.toString()=");
										//System.out.println(occurrence.toString());
										//System.out.println();

										order = false;

									} else {
										order = false;
										//System.out.print("failure|");
									}
									ordera = false;
									orderb = false;
								}
								// pos=pos-1;
							}
						}

					}
				}
				//System.out.println();
				//System.out.println("一行句子读完啦");
				tag = 0;
				
				//System.out.println("=========go out spiltedSentences========");


			}
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		System.out.println("\n************listOccurence***************");
		for(Occurrence str:listOccurrence){
			System.out.println(str);
		}
		System.out.println("************listOccurence***************\n");

		return listOccurrence;
	}



	public  void findOccurrence(List<Occurrence> occurrence,List<Seed> seed) {
		List<Pattern> ssstrs = new ArrayList<>();
		Pattern ssstr = new Pattern();

		String symbol = "";
		String inPrefix = "";
		String inMiddle = "";
		String inSuffix = "";
		boolean inOrder = false;
		int inNum = 1;

		ssstrs.add(ssstr);
		
		boolean trait1 = false;
		boolean rrr = false;
		
		for(Occurrence str: occurrence){
			
//			System.out.println("------------------------------------------------------");
//			System.out.println(str);
//			System.out.println("种子：("+str.getSeedf()+","+str.getSeedl()+","+str.getSymbol()+")");
//			System.out.println("前缀："+str.getPrefix());
//			System.out.println("中缀："+str.getMiddle());
//			System.out.println("后缀："+str.getSuffix());
//			System.out.println("ok");
//			System.out.println("------------------------------------------------------s\n");
			
			symbol = str.getSymbol();
			inPrefix = str.getPrefix();
			inMiddle = str.getMiddle();
			inSuffix = str.getSuffix();
			
			
			
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
						ssstr = new Pattern(str.getSymbol(),inPrefix,inMiddle,inSuffix,inOrder,inNum);
						ssstrs.add(ssstr);
										
						//for(Pattern temp1 :ssstrs){
						//	System.out.print(temp1);
						//	System.out.println("|");
						//}
						//System.out.println("end2222222222222222");
						
						rrr=false;
						break;
					}			
				}
				//System.out.println("======go out Pattern=====");
							
			}
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//	
		}
//		System.out.println("kakakakakakakakakakakakakakakakakakaka");
//		System.out.println();
//		System.out.println("=============charpter3=================");
//		for(Pattern str:ssstrs){
//			System.out.println(str);
//		}
		System.out.println("=============charpter4=================");
		for(Pattern str:ssstrs){
			if(str.getOrder()){
			System.out.println(str);
			}
		}
		System.out.println();
		
	}
	
	
	public void matchOccurrence(){

	}

}
