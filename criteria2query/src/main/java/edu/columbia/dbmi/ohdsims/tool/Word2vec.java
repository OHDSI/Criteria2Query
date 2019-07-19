package edu.columbia.dbmi.ohdsims.tool;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;



import java.util.Set;
import java.util.TreeSet;

import edu.columbia.dbmi.ohdsims.util.FileUtil;

public class Word2vec {
	
//	public static String getSimilarWords(String word){
//		
//	}
	
	
	public Word2vec(){
		
	}
	
	public static void main(String[] args) throws IOException {
		
		
		Word2vec vec = new Word2vec();
		///Documents/pubmed_phrase_word_combo_vectors_win_5.bin
		vec.loadModel("/Users/cy2465/Downloads/vectors-phrase.bin");///Downloads/vectors-phrase.bin
		
		String[] arr={"i2b2","ncbi","ct_gov"};
		
		for(String a:arr){
		System.out.println("word2vec="+vec.words);
		String type=a;
		String c=FileUtil.Readfile("/Users/cy2465/Downloads/RECON_eval/"+type+"_pairs.txt");
		String[] rows=c.split("\n");
		StringBuffer result=new StringBuffer();
		for(String r:rows){
			float sim=0;
			String[] es=r.toLowerCase().split("\t");
			try{
				
				System.out.println(es[0]+"\t"+es[1]);
				sim=vec.calculateDis(es[0].toLowerCase(),es[1].toLowerCase());
			}catch(Exception ex){
				sim=0;
			}
			result.append(es[0]+"\t"+es[1]+"\t"+sim+"\n");
		}
		FileUtil.write2File("/Users/cy2465/Downloads/RECON_eval/dic_"+type+"_phrase2vec.txt", result.toString());
		}
//		System.out.println("One word analysis");
//		Set<WordEntry> result = new TreeSet<WordEntry>();
//		result = vec.distance("diabetes");
//		Iterator iter = result.iterator();
//		while (iter.hasNext()) {
//			WordEntry word = (WordEntry) iter.next();
//			System.out.println(word.name + " " + word.score);
//		}
		/*
		float[] conceptvec=vec.getWordVector("breast-cancer");
		
		for(float f:conceptvec){
			System.out.println(f);
		}
		System.out.println("length="+conceptvec.length);
		*/
		
		List<String> list=new ArrayList<String>();
		
		
		list.add("eyes");
		list.add("nervous-system");
		
		list.add("eyes");
		list.add("nervous");
		
		list.add("gastric");
		list.add("biliary ");
		
		
		list.add("gastric");
		list.add("biliary-system");
		
		for(int i=0;i<list.size()-1;i=i+2){
			try{
				System.out.println("similarity of "+list.get(i)+" & "+ list.get(i+1) + " : "+vec.calculateDis(list.get(i),list.get(i+1)));
			}catch(Exception ex){
				System.out.println("error");
			}
		}
		
//		System.out.println("*******************************");
//		System.out.println("Three word analysis");
//		result = vec.analogy("diabetes", "diabetes", "metformin");
//		iter = result.iterator();
//		while (iter.hasNext()) {
//			WordEntry word = (WordEntry) iter.next();
//			System.out.println(word.name + " " + word.score);
//		}
	}

	private HashMap<String, float[]> wordMap = new HashMap<String, float[]>();

	private int words;
	private int size;
	private int topNSize = 10;

	/**
	 * 加载模型
	 * 
	 * @param path
	 *            模型的路径
	 * @throws IOException
	 */
	public void loadModel(String path) throws IOException {
		DataInputStream dis = null;
		BufferedInputStream bis = null;
		double len = 0;
		float vector = 0;
		try {
			bis = new BufferedInputStream(new FileInputStream(path));
			dis = new DataInputStream(bis);
			// //读取词数
			words = Integer.parseInt(readString(dis));
			// //大小
			size = Integer.parseInt(readString(dis));

			String word;
			float[] vectors = null;
			for (int i = 0; i < words; i++) {
				word = readString(dis);
				vectors = new float[size];
				len = 0;
				for (int j = 0; j < size; j++) {
					vector = readFloat(dis);
					len += vector * vector;
					vectors[j] = (float) vector;
				}
				len = Math.sqrt(len);

				for (int j = 0; j < vectors.length; j++) {
					vectors[j] = (float) (vectors[j] / len);
				}
				wordMap.put(word, vectors);
				dis.read();
			}

		} finally {
			bis.close();
			dis.close();
		}
	}

	private static final int MAX_SIZE = 50;

	/**
	 * 得到近义词
	 * 
	 * @param word
	 * @return
	 */
	public Set<WordEntry> distance(String word) {
		float[] wordVector = getWordVector(word);
		if (wordVector == null) {
			return null;
		}
		Set<Entry<String, float[]>> entrySet = wordMap.entrySet();
		float[] tempVector = null;
		List<WordEntry> wordEntrys = new ArrayList<WordEntry>(topNSize);
		String name = null;
		for (Entry<String, float[]> entry : entrySet) {
			name = entry.getKey();
			if (name.equals(word)) {
				continue;
			}
			float dist = 0;
			tempVector = entry.getValue();
			for (int i = 0; i < wordVector.length; i++) {
				dist += wordVector[i] * tempVector[i];
			}
			insertTopN(name, dist, wordEntrys);
		}
		return new TreeSet<WordEntry>(wordEntrys);
	}

	
	/**
	 * 近义词
	 * 
	 * @return
	 */
	public TreeSet<WordEntry> analogy(String word0, String word1, String word2) {
		float[] wv0 = getWordVector(word0);
		float[] wv1 = getWordVector(word1);
		float[] wv2 = getWordVector(word2);

		if (wv1 == null || wv2 == null || wv0 == null) {
			return null;
		}
		float[] wordVector = new float[size];
		for (int i = 0; i < size; i++) {
			wordVector[i] = wv1[i] - wv0[i] + wv2[i];
		}
		float[] tempVector;
		String name;
		List<WordEntry> wordEntrys = new ArrayList<WordEntry>(topNSize);
		for (Entry<String, float[]> entry : wordMap.entrySet()) {
			name = entry.getKey();
			if (name.equals(word0) || name.equals(word1) || name.equals(word2)) {
				continue;
			}
			float dist = 0;
			tempVector = entry.getValue();
			for (int i = 0; i < wordVector.length; i++) {
				dist += wordVector[i] * tempVector[i];
			}
			insertTopN(name, dist, wordEntrys);
		}
		return new TreeSet<WordEntry>(wordEntrys);
	}

	private void insertTopN(String name, float score,
			List<WordEntry> wordsEntrys) {
		if (wordsEntrys.size() < topNSize) {
			wordsEntrys.add(new WordEntry(name, score));
			return;
		}
		float min = Float.MAX_VALUE;
		int minOffe = 0;
		for (int i = 0; i < topNSize; i++) {
			WordEntry wordEntry = wordsEntrys.get(i);
			if (min > wordEntry.score) {
				min = wordEntry.score;
				minOffe = i;
			}
		}

		if (score > min) {
			wordsEntrys.set(minOffe, new WordEntry(name, score));
		}

	}

	public class WordEntry implements Comparable<WordEntry> {
		public String name;
		public float score;

		public WordEntry(String name, float score) {
			this.name = name;
			this.score = score;
		}

		@Override
		public String toString() {
			return this.name + "\t" + score;
		}

		public int compareTo(WordEntry o) {
			if (this.score > o.score) {
				return -1;
			} else {
				return 1;
			}
		}

	}

	public float calculateDis(String word1,String word2){
		float dist=0;
		float[] wordVector = getWordVector(word1);
		float[] wordVector2=getWordVector(word2);
		for(int i=0;i<wordVector.length;i++){
			dist+=wordVector[i] * wordVector2[i];
		}
		return dist;
	}
	/**
	 * 得到词向量
	 * 
	 * @param word
	 * @return
	 */
	public float[] getWordVector(String word) {
		return wordMap.get(word);
	}

	public static float readFloat(InputStream is) throws IOException {
		byte[] bytes = new byte[4];
		is.read(bytes);
		return getFloat(bytes);
	}

	/**
	 * 读取一个float
	 * 
	 * @param b
	 * @return
	 */
	public static float getFloat(byte[] b) {
		int accum = 0;
		accum = accum | (b[0] & 0xff) << 0;
		accum = accum | (b[1] & 0xff) << 8;
		accum = accum | (b[2] & 0xff) << 16;
		accum = accum | (b[3] & 0xff) << 24;
		return Float.intBitsToFloat(accum);
	}

	/**
	 * 读取一个字符串
	 * 
	 * @param dis
	 * @return
	 * @throws IOException
	 */
	private static String readString(DataInputStream dis) throws IOException {
		byte[] bytes = new byte[MAX_SIZE];
		byte b = dis.readByte();
		int i = -1;
		StringBuilder sb = new StringBuilder();
		while (b != 32 && b != 10) {
			i++;
			bytes[i] = b;
			b = dis.readByte();
			if (i == 49) {
				sb.append(new String(bytes));
				i = -1;
				bytes = new byte[MAX_SIZE];
			}
		}
		sb.append(new String(bytes, 0, i + 1));
		return sb.toString();
	}

	public int getTopNSize() {
		return topNSize;
	}

	public void setTopNSize(int topNSize) {
		this.topNSize = topNSize;
	}

	public HashMap<String, float[]> getWordMap() {
		return wordMap;
	}

	public int getWords() {
		return words;
	}

	public int getSize() {
		return size;
	}
}