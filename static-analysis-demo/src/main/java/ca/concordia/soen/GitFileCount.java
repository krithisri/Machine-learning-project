package ca.concordia.soen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitFileCount {
	
	private static int beginindex;

	public static void main(String args[]) {
		String input="C:\\Users\\Admin\\eclipse-workspace\\Assignment\\src\\Assignment\\src\\pre_keys.txt";
		String output="C:\\Users\\Admin\\eclipse-workspace\\Assignment\\src\\Assignment\\src\\finally_.txt";
		List<String> keys=readKeys(input);
		Map<String,Integer> fileCounts=findFileChanges(keys,output);
		writeOutputToFile(fileCounts);
		
	}

	private static void writeOutputToFile(Map<String, Integer> fileCounts) {
		try (PrintWriter writer= new PrintWriter(new File("C:\\Users\\Admin\\eclipse-workspace\\Assignment\\src\\Assignment\\src\\Files\\pre_release_bugs.csv"))){
			StringBuilder sb= new StringBuilder();
			sb.append("File,pre_release_bugs\n");
			for(String file: fileCounts.keySet()) {
				sb.append(file);
				sb.append(",");
				sb.append(fileCounts.get(file));
				sb.append("\n");
			}
			writer.write(sb.toString());
			System.out.println("Output written to PreRea.csv");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static Map<String, Integer> findFileChanges(List<String> keys, String output) {
		Map<String,Integer> fileCounts=new HashMap<>();
		try(BufferedReader br=new BufferedReader(new FileReader(output))){
			String line;
			String commit="";
			while((line=br.readLine())!=null) {
				if(line.startsWith("commit")) {
					commit=line.substring(7);
				}else if (line.startsWith("KAFKA-")){
					boolean found=false;
					for(String key: keys) {
						if(line.contains(key)) {
							found=true;
							break;
						}
					}
					if(found) {
						while((line=br.readLine())!=null) {
							if(line.startsWith("kAFKA-")) {
								int count=fileCounts.getOrDefault(line, 0);
								fileCounts.put(line, count+1);
							}else if(line.startsWith("commit ")) {
								break;
							}
						}
					}
				}
			}
		}  catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileCounts;
	}

	private static List<String> readKeys(String input) {
		List<String> keys= new ArrayList<>();
		try 
			(BufferedReader br=new BufferedReader(new FileReader(input))){
				String line;
				while((line=br.readLine())!=null) {
					String pattern="KAFKA-\\d+";
					Pattern r= Pattern.compile(pattern);
					Matcher m=r.matcher(line);
					if(m.find()) {
						keys.add(m.group(0));
					}
				}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return keys;
	}
}

