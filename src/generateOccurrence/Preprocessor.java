package generateOccurrence;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import domain.Occurrence;
import domain.PatternA;
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
	 * 
	 * */
	public boolean check(String string){
		// 字符串验证规则
		String regEx = "^[\u0391-\uFFE5]*$";
		// 编译正则表达式
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(string);
		// 字符串是否与正则表达式相匹配
		boolean rs = matcher.matches();
		
		return rs;
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

				System.out.println("-----------------oldseeds------------------");
				System.out.println(seeds.size());
				for(Seed str : seeds){
				System.out.println(str);
				}
				System.out.println("-----------------oldseeds------------------");

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
		
		File file = new File(pathname);
		BufferedInputStream fis;

		Occurrence occurrence = new Occurrence();
		List<Occurrence> listOccurrence = new ArrayList<>();

		try {
			fis = new BufferedInputStream(new FileInputStream(file));
			//BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "gbk"), 5 * 1024 * 1024);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "utf-8"), 5 * 1024 * 1024);

			String line = "";
			int tag = 0; // 标记句号的位置，以句号分隔按行读取的句子。
			String[] spilted;//存放以句号分割的每一条句子

			while ((line = reader.readLine()) != null) {
				
				spilted = lineSpilt(line);
				//System.out.println("按行读取的句子："+line);
				
				for (int i = 0; i < spilted.length; i++) {
					List<String> sentences = new ArrayList<>();  //存放句子的list

					// System.out.print(spilted[i]+" ");
					if (spilted[i].equals("。")) {
						//System.out.print("[");
						for (int j = tag; j <= i; j++) {
							sentences.add(spilted[j]);
							//System.out.print(""+spilted[j]+",");
						}
						//System.out.println("]");
						// System.out.println();
						
						tag = i + 1;  	//句号位置
						int pos = -1;	//当前位置
						int posa = -1;	//seedf的位置
						int posb = -1;	//seedl的位置

						boolean ordera = false;		//是否找到seedf
						boolean orderb = false;		//是否找到seedl
						boolean order = false;		//是否(seedf,seedl)两个都有

						
						/**两层循环嵌套：先遍历句子，再遍历种子
						 * （对句子遍历种子）
						 * for（挨个读句子里的词看是不是和seedf匹配）     
						 * 		如果匹配{
						 * 					标记posa，ordera
						 * 					for（挨个读posa之后的词，看是不是和seedl匹配）
						 * 						如果匹配{
						 * 									标记posb，orderb
						 * 						}
						 * 						否则下一个句子
						 * 				} 
						 * 		否则下一个句子
						 * */
						for (pos = 0; pos < sentences.size(); pos++) {

							String prefix = "";
							String seedf = "";
							String middle = "";
							String seedl = "";
							String suffix = "";
							String symbol = "";

							int num = -1;

							for (int flag = 0; flag < seeds.size(); flag++) {
								
								if (sentences.get(pos).equals(seeds.get(flag).getSeedfirst()) == true) {
									//System.out.print("first\\\\\\\\\\\\\\\\\\\\|"+"posa="+pos+"||");
									
									posa = pos;
									ordera = true;

									String trait = seeds.get(flag).getSeedlast(); //与种子seedf相对应的seedl
									
									//sign: 
										while (!sentences.get(pos).equals(trait)) {
											pos++;
											if (pos + 1 == sentences.size()) {
												break /*sign*/;
											}
										}

									if (sentences.get(pos).equals(trait)) {
										posb = pos;
										orderb = true;
									}

									
									if (ordera && orderb) {
										
										order = true;
										//System.out.println("true|");
										if(posa!=0){
											int a=posa;
											String ss=sentences.get(posa-1);
											//System.out.println(ss+" ");
//											while(ss.equals("")||ss.equals(" ")||ss.equals("，")||ss.equals("。")||
//													ss.equals("！")||ss.equals("、")||ss.equals("【")||ss.equals("●")||
//													ss.equals("】")||ss.equals("《")||ss.equals("》")||ss.equals("—")||
//													ss.equals("‘")||ss.equals("’")||ss.equals("“")||ss.equals("％")||
//													ss.equals("”")||ss.equals("\"")||ss.equals("（")||ss.equals("…")||
//													ss.equals("）")||ss.equals("？")||ss.equals("；")||ss.equals("：")||
//													ss.equals("「")||ss.equals("」")){
//												a--;
//												//System.out.println(ss+"	"+a);
//												//System.out.println();
//												if(a>0){
//													ss=sentences.get(a-1);
//												}
//												else{
//													break;
//												}
//											}
											//System.out.println();
											//System.out.println(ss+"	"+a);
											if(a>0){
												prefix = sentences.get(a-1);
											}
											else{
												//prefix = null;
												ordera=false;
												orderb=false;
												pos=posa;
												posa=-1;
												posb=-1;
												break;
											}
										}
										else{
											//prefix = null;
											ordera=false;
											orderb=false;
											pos=posa;
											posa=-1;
											posb=-1;
											break;
										}
										seedf = sentences.get(posa);
										if(posa+1==posb){
											break;
										}
										else{
//											if(posa+2==posb){
//												String ss=sentences.get(posa+1);
//												if(ss.equals("")||ss.equals(" ")||ss.equals("，")||ss.equals("。")||
//												ss.equals("！")||ss.equals("、")||ss.equals("【")||ss.equals("●")||
//												ss.equals("】")||ss.equals("《")||ss.equals("》")||ss.equals("—")||
//												ss.equals("‘")||ss.equals("’")||ss.equals("“")||ss.equals("％")||
//												ss.equals("”")||ss.equals("\"")||ss.equals("（")||ss.equals("…")||
//												ss.equals("）")||ss.equals("？")||ss.equals("；")||ss.equals("：")||
//												ss.equals("「")||ss.equals("」")){
//													break;
//												}
//												else{
//													for (int a = posa + 1; a < posb; a++) {
//														//middle.add(sentences.get(a));
//														middle +=sentences.get(a);
//														//System.out.println(sentences.get(a));
//													}
//												}
//											}
//											else{
												for (int a = posa + 1; a < posb; a++) {
													//middle.add(sentences.get(a));
													middle +=sentences.get(a)+" ";
													//System.out.println(sentences.get(a));
												}
//											}
										}
										//System.out.println(middle);
										seedl = sentences.get(posb);
										if(posb!=sentences.size()-1){
											int b=posb;
											String ss=sentences.get(posb+1);
											//System.out.println(ss);
//											while(ss.equals("")||ss.equals(" ")||ss.equals("，")||ss.equals("。")||
//													ss.equals("！")||ss.equals("、")||ss.equals("【")||ss.equals("●")||
//													ss.equals("】")||ss.equals("《")||ss.equals("》")||ss.equals("—")||
//													ss.equals("‘")||ss.equals("’")||ss.equals("“")||ss.equals("％")||
//													ss.equals("”")||ss.equals("\"")||ss.equals("（")||ss.equals("…")||
//													ss.equals("）")||ss.equals("？")||ss.equals("；")||ss.equals("：")||
//													ss.equals("「")||ss.equals("」")||ss.equals("'")){
//												b++;
//												if(b<sentences.size()-1){
//													ss=sentences.get(b+1);
//												}
//												else{
//													break;
//												}
//												
//											}
											if(b<sentences.size()-1){
												suffix = sentences.get(b+1);
											}
											else{
												//suffix=null;
												ordera=false;
												orderb=false;
												pos=posa;
												posa=-1;
												posb=-1;
												break;
											}
//											suffix = sentences.get(posb+1);
										}
										else{
											//suffix = null;
											ordera=false;
											orderb=false;
											pos=posa;
											posa=-1;
											posb=-1;
											break;
										}
										symbol = seeds.get(flag).getSymbol();
										
										if(prefix==null&&middle==null&&suffix==null){
											
										}
										else{
											occurrence = new Occurrence(prefix,seedf, middle, seedl, suffix,symbol,order, num);
											listOccurrence.add(occurrence);
											}
										
										//System.out.println("occurrence.toString()=");
										//System.out.println(occurrence.toString());
										//System.out.println();

										order = false;

									} 
									else {
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



	public  List<PatternA> findOccurrence(List<Occurrence> occurrence) {
		List<PatternA> ssstrs = new ArrayList<>();
		//PatternA ssstr = new PatternA();
		PatternA ssstr = new PatternA("Test...","Test...","Test...","Test...",false,0);

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
						ssstr = new PatternA(str.getSymbol(),inPrefix,inMiddle,inSuffix,inOrder,inNum);
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
		System.out.println("=============charpter3=================");
		for(PatternA str:ssstrs){
			System.out.println(str);
		}
		System.out.println(ssstrs.size());
//		System.out.println("=============charpter4=================");
//		for(PatternA str:ssstrs){
//			if(str.getOrder())
//			System.out.println(str);
//			
//		}
//		System.out.println();
		
		return ssstrs;
		
	}
	
	public static StringBuffer buffer(String s){
        StringBuffer sb=new StringBuffer();
        sb.append(s);       
        return sb.reverse();
    }
	
	public static boolean isLetterDigitOrChinese(String str) {
		  String regex = "^[a-z0-9A-Z\u4e00-\u9fa5]+[/]*[a-z0-9A-Z\u4e00-\u9fa5]*$";//其他需要，直接修改正则表达式就好
		  return str.matches(regex);
		 }
	/**
	 * MatchOccurrence(list<patternA>,pathSentence,List<Seeds> )
	 * 按行读入要检测的大文本语料，并用	tab和。分隔存入spilted中。
	 * 双重循环对句子遍历模式，用正则匹配找出符合模式的句子。
	 * */
	public void matchOccurrence(List<PatternA> ssstrs, String pathSentence, List<Seed> seeds){

		System.out.println("-----------------sentences--------------");
		File file = new File(pathSentence);
		BufferedInputStream fis;
		try {
			fis = new BufferedInputStream(new FileInputStream(file));
			//BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "gbk"), 5 * 1024 * 1024);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"), 5 * 1024 * 1024);

			String line = "";
			String[] spilted = null;
			

			int i=0;  //标记是第几个句子，打印时看句子比较方便
			while ((line = reader.readLine()) != null) {
				//System.out.println(line);
				i++;
				spilted = line.split("。|	");
				//a是要检测的每一个句子
				sign:for(String a:spilted){
					//System.out.println("first loop   spilted.size="+spilted.length);
					//System.out.println("--"+i+"----"+a);
					//str是要匹配的每一个模式（PatternA是自定义的模式类）
					//PatternA(symbol，prefix，middle，suffix，order，num)
					for(PatternA str:ssstrs){
						//System.out.println("second loop   i="+i+"  ssstrs.size()="+ssstrs.size());
						//System.out.println(str);
						
						String prefix = str.getPrefix();
						String middle = str.getMiddle();
						String suffix = str.getSuffix();
						
//					    System.out.println("----------");
//						System.out.println(prefix);
//						System.out.println(middle);
//						System.out.println(suffix);
//						System.out.println("----------");
						
						
						//正则匹配句子模式
						// 字符串验证规则
						//String regEx = ".*?"+prefix+".*?（ 拉丁 名 ： Carpodacus mexicanus ） ， 属于.*?"+suffix+".*";
						String regEx = ".*?"+prefix+".*?"+middle+".*?"+suffix+".*?";
						//String regEx = "[\\s\\S]*"+prefix+"[\\s\\S+\\s]{1}"+middle+"[\\s\\S+\\s]{1}"+suffix+"[\\s\\S]*";
						//String regEx = "[\\s\\S]*?"+prefix+"[\\s\\S]*?"+middle+"[\\s\\S]*?"+suffix+"[\\s\\S]*?";
						//String regEx = "[.*?]["+prefix+"][.*?]["+middle+"][.*?]["+suffix+"][.*?]";
						//String regEx = "[^[\u0391-\uFFE5]*|[ ]*|[0-9]*|[\\p{P}]*$]*["+prefix+"][^[\u0391-\uFFE5]*|[ ]*|[0-9]*|[\\p{P}]*$]*["+middle+"][^[\u0391-\uFFE5]*|[ ]*|[0-9]*|[\\p{P}]*$]*["+suffix+"][^[\u0391-\uFFE5]*|[ ]*|[0-9]*|[\\p{P}]*$]*";
						// 编译正则表达式
						Pattern pattern = Pattern.compile(regEx);
						Matcher matcher = pattern.matcher(a);
						// 字符串是否与正则表达式相匹配
						boolean rs = matcher.matches();
						
						
//						System.out.println(regEx);
//						System.out.println("line="+line+"\n=============rs="+rs);
						
						if(rs){
//							System.out.println("\n************************************");
//							System.out.println("--"+i+"----"+a);
//							System.out.println(str);
//							String[] m = a.split(" ");
//							String string="";
//							for(String mm:m){
//								string +=mm;
//							}
							//System.out.println("[去空格后：]"+string);
							
//							String[] mmid = str.getMiddle().split(" ");
//							String string1 = "";
//							for(String mm:mmid){
//								string1 +=mm;
//							}
							
							int posa=a.indexOf(str.getPrefix());//前缀开始的位置
							int dista = str.getPrefix().length();//前缀的长度
							int posb = a.indexOf(str.getMiddle(),posa+dista);//中缀开始的位置
							int distb = str.getMiddle().length();//中缀的长度
							int posc=a.indexOf(str.getSuffix(),posb+distb);//后缀开始的位置
							//System.out.println("【"+posa+"|"+posb+"|"+posc+"|"+dista+"|"+distb+"】");
							
							
							//！！！如果要测试打开这一段输出！！！
							System.out.println("+++++++++++++++++++++++++++++++++++");
							System.out.println("--"+i+"----"+a);

							System.out.println(str);
//							System.out.println("[去空格后：]"+string);
							System.out.println(str.getPrefix());
							System.out.println(str.getMiddle());
							System.out.println(str.getSuffix());
							System.out.println("【"+posa+"|"+posb+"|"+posc+"|"+dista+"|"+distb+"】");
	
							//为什么会有这个判断呢？是因为正则出问题了，以后再说吧正则太难了。
							if(posa==-1||posb==-1||posc==-1){
								break;
							}

//							
//							String fseedf="",fseedl="";
//							for(int f=posa+dista+1;f<posb-1;f++){
//								fseedf+=a.charAt(f);
//								if(a.charAt(f)==32){
//									System.out.println("seedf这里有空格！！"+"f=="+f);
//									//break sign;
//									//break;
//								}
//							}
//							for(int f=posb+distb;f<posc-1;f++){
//								fseedl+=a.charAt(f);
//								if(a.charAt(f)==32){
//									System.out.println("seedl这里有空格！！"+"f=="+f);
//									//break sign;
//									//break;
//								}
//
//							}
							

							String fseedf="",fseedl="";
							for(int f=posb-2;f>=posa+dista+1;f--){
								fseedf+=a.charAt(f);
								if(a.charAt(f)==32){
									System.out.println("seedf这里有空格！！"+"f=="+f);
									//break sign;
									break;
								}
							}
							System.out.println("buffer(fseedf)==="+buffer(fseedf));
						
							for(int f=posc-2;f>=posb+distb;f--){
								fseedl+=a.charAt(f);
								if(a.charAt(f)==32){
									System.out.println("seedl这里有空格！！"+"f=="+f);
									//break sign;
									break;
								}

							}
							System.out.println("buffer(fseedl)==="+buffer(fseedl));
							fseedf = buffer(fseedf).toString().trim();
							fseedl = buffer(fseedl).toString().trim(); 
							boolean aaaa = isLetterDigitOrChinese(fseedf.trim());
							boolean bbbb = isLetterDigitOrChinese(fseedl.trim());
							System.out.println(aaaa+" "+bbbb);
							boolean flag=false;
							//System.out.println(seeds.size());
							if(fseedf!=" "&&fseedl!=" "&&aaaa&&bbbb){
								for(int p=0;p<seeds.size();p++){
									//System.out.println(seeds.get(p));
									if(seeds.get(p).getSeedfirst().equals(fseedf)&&seeds.get(p).getSeedlast().equals(fseedl)){
										//System.out.println("error");
										break;
									}
									else{
										if(p+1==seeds.size()){
											flag=true;
										}
										//System.out.println("ok");
										if(flag){
											seeds.add(new Seed(str.getSymbol(),fseedf,fseedl));
											flag=false;
											break;
										}
									}
								}
							}

//							System.out.println();
//							System.out.println("+++++++++++++++++++++++++++++++++++");
//							System.out.println("************************************\n");							
							
						}
						 
					}
				}
			}
			
		}catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		System.out.println("-----------------sentences--------------");

		System.out.println("-----------------newseeds------------------");
		
		for(Seed str:seeds){
			System.out.println(str);
		}
		System.out.println(seeds.size());
		System.out.println("-----------------newseeds------------------");
		

		
		
	}

}
