package ca.concordia.soen;


import java.io.*;
import java.util.*;

public class MergeCSVBSAP {
    public static void main(String[] args) throws Exception {
        // Read the first file
        BufferedReader reader1 = new BufferedReader(new FileReader("C:\\Users\\Admin\\eclipse-workspace\\Assignment\\src\\Assignment\\src\\Files\\Final\\AntiPatterns\\BASE.csv"));
        Map<String, String[]> map1 = new HashMap<String, String[]>();
        String line1 = null;
        while ((line1 = reader1.readLine()) != null) {
            String[] values1 = line1.split(",");
            map1.put(values1[0], values1);
        }
        reader1.close();
        
        // Read the second file
        BufferedReader reader2 = new BufferedReader(new FileReader("C:\\Users\\Admin\\eclipse-workspace\\Assignment\\src\\Assignment\\src\\Files\\Final\\AntiPatterns\\Antipatters_logs.csv"));
        Map<String, String[]> map2 = new HashMap<String, String[]>();
        String line2=null;
        while ((line2 = reader2.readLine()) != null) {
            String[] values2 = line2.split(",");
            map2.put(values2[0], values2);
        }
        reader2.close();
        
        /*
        // Read the third file
        BufferedReader reader3 = new BufferedReader(new FileReader("C:\\Users\\Admin\\eclipse-workspace\\Assignment\\src\\Assignment\\src\\Files\\Final\\pre_release_bugs.csv"));
        Map<String, String[]> map3 = new HashMap<String, String[]>();
        String line3 = null;
        while ((line3 = reader3.readLine()) != null) {
            String[] values3 = line3.split(",");
            map3.put(values3[0], values3);
        }
        reader3.close();
        // Read the fourth file
        BufferedReader reader4 = new BufferedReader(new FileReader("C:\\Users\\Admin\\eclipse-workspace\\Assignment\\src\\Assignment\\src\\Files\\Final\\post_release_bugs.csv"));
        Map<String, String[]> map4 = new HashMap<String, String[]>();
        String line4 = null;
        while ((line4 = reader4.readLine()) != null) {
            String[] value4 = line4.split(",");
            map4.put(value4[0], value4);
        }
        reader4.close();
        
        // Read the fifth file
        BufferedReader reader5 = new BufferedReader(new FileReader("C:\\Users\\Admin\\eclipse-workspace\\Assignment\\src\\Assignment\\src\\Files\\Final\\Antipatters_logs.csv"));
        Map<String, String[]> map5 = new HashMap<String, String[]>();
        String line5 = null;
        while ((line5 = reader5.readLine()) != null) {
            String[] value5 = line5.split(",");
            map5.put(value5[0], value5);
        }
        reader5.close();*/
        
        // Merge the files
        Set<String> keys = new HashSet<String>(map1.keySet());
        keys.addAll(map2.keySet());
        /*keys.addAll(map3.keySet());       
        keys.addAll(map4.keySet());
        keys.addAll(map5.keySet());*/

        List<String[]> mergedList = new ArrayList<String[]>();
        for (String key : keys) {
        	

            String[] values1 = map1.get(key);
            String[] values2 = map2.get(key);
            /*String[] values3 = map3.get(key);
            String[] values4 = map4.get(key);
            String[] values5 = map5.get(key);*/
            if(values1==null) {
            	values1=new String[] {key, "0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0",
            			"0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0"};
            }
            if (values2 == null) {
                values2 = new String[] { key, "0","0","0","0" };
            }
            
            String[] mergedValues = new String[51];
            
            mergedValues[0]=key;
            mergedValues[1] = values1[1];
            mergedValues[2] = values1[2];
            mergedValues[3] = values1[3];
            mergedValues[4] = values1[4];
            mergedValues[5] = values1[5];
            mergedValues[6] = values1[6];
            mergedValues[7] = values1[7];
            mergedValues[8] = values1[8];
            mergedValues[9] = values1[9];
            mergedValues[10] = values1[10];
            mergedValues[11] = values1[11];
            mergedValues[12] = values1[12];
            mergedValues[13] = values1[13];
            mergedValues[14] = values1[14];
            mergedValues[15] = values1[15];
            mergedValues[16] = values1[16];
            mergedValues[17] = values1[17];
            mergedValues[18] = values1[18];
            mergedValues[19] = values1[19];
            mergedValues[20] = values1[20];
            mergedValues[21] = values1[21];
            mergedValues[22] = values1[22];
            mergedValues[23] = values1[23];
            mergedValues[24] = values1[24];
            mergedValues[25] = values1[25];
            mergedValues[26] = values1[26];
            mergedValues[27] = values1[27];
            mergedValues[28] = values1[28];
            mergedValues[29] = values1[29];
            mergedValues[30] = values1[30];
            mergedValues[31] = values1[31];
            mergedValues[32] = values1[32];
            mergedValues[33] = values1[33];
            mergedValues[34] = values1[34];
            mergedValues[35] = values1[35];
            mergedValues[36] = values1[36];
            mergedValues[37] = values1[37];
            mergedValues[38] = values1[38];
            mergedValues[39] = values1[39];
            mergedValues[40] = values1[40];
            mergedValues[41] = values1[41];
            mergedValues[42] = values1[42];
            mergedValues[43] = values1[1];
            mergedValues[44] = values1[2];
            mergedValues[45] = values1[1];
            mergedValues[46] = values1[1];
            mergedValues[47] = values2[1];
            mergedValues[48] = values2[2];
            mergedValues[49] = values2[3];
            mergedValues[50] = values2[4];
            
            /*System.arraycopy(values1, 0, mergedValues, 0, values1.length);
            System.arraycopy(values2, 1, mergedValues, 43, 2);
            System.arraycopy(values3, 1, mergedValues, 45, 1);
            System.arraycopy(values4, 1, mergedValues, 46, 1);*/
            mergedList.add(mergedValues);
        }

        // Write the merged file
        BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\Admin\\eclipse-workspace\\Assignment\\src\\Assignment\\src\\Files\\BSAP.csv"));

        writer.write("File,AvgCyclomaticModified,AvgCyclomatic,AvgLine,AvgLineCode,AvgCyclomaticStrict,AvgCyclomaticStrictModified,AvgEssential,AvgLineBlank,AvgLineComment,CountDeclClass,CountDeclClassMethod,CountDeclClassVariable,CountDeclExecutableUnit,CountDeclFunction,CountDeclInstanceMethod,CountDeclInstanceVariable,CountDeclMethod,CountDeclMethodDefault,CountDeclMethodPrivate,CountDeclMethodPublic,CountLine,CountLineBlank,CountLineCode,CountLineCodeDecl,CountLineCodeExe,CountLineComment,CountSemicolon,CountStmt,CountStmtDecl,CountStmtExe,MaxCyclomatic,MaxCyclomaticModified,MaxCyclomaticStrict,MaxCyclomaticStrictModified,MaxEssential,MaxNesting,RatioCommentToCode,SumCyclomatic,SumCyclomaticModified,SumCyclomaticStrict,SumCyclomaticStrictModified,SumEssential,Total Changes,Code Churn,Pre-release Bugs,Post-release Bugs,Log and Throw,Nested Try,Destructive Wrapping,Throws Kitchen Sink");
        for (String[] values : mergedList) {
            writer.write(String.join(",", values) + "\n");
        }
        writer.close();
    }
}