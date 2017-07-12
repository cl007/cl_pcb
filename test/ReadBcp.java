//cl007
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
	* auth  cl
	* date  2017.6.1
	* 
*/

public class ReadBcp extends Merger_Bcp{

	public void  proOneBcp(String path){		
		try{
			String ecnoding = "utf-8";
			File file = new File(path);
			if(file.exists() && file.isFile()){	 
			  BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), ecnoding));
			  read(reader);
			  reader.close();
			}	
		}catch(IOException e){
			e.printStackTrace();
		}
	}
    
	private void read(BufferedReader reader) throws IOException{
		String line;
		while((line = reader.readLine()) != null){
			//line = line.trim();
			proLine(line);
		}	
	}
    public void  proOneBcp(String path, boolean flag){		
		if(!flag){
			File file = new File(path);
			Long filelen = file.length();
			byte[] filecontent = new byte[filelen.intValue()];

			try{
				FileInputStream in = new FileInputStream(file);
				in.read(filecontent);
				in.close();
			}catch(FileNotFoundException e){
				e.printStackTrace();
			}catch(IOException e){
				e.printStackTrace();
			}
			String []fileCountArry = new String(filecontent).split("\r\n");
			System.out.println(read_ini.get("DATA_EMAIL"));
			//Map<String, List<String>> map = new HashMap<String, List<String>>
			//procLines(fileCountArry, map);
		}
	}
	private void procLines(String []fileLineArry,  Map<String, List<String>> map){

		List<String> list = map.get("idx");
		int idx  = Integer.parseInt(list.get(0));
		System.out.println(idx);
		jedis.select(idx);
		for(int i = 0; i <fileLineArry.length; i++){
			proLine(fileLineArry[i]);		
		}
	}

	public void proLine(String line){
		//System.out.println(line + "good");	
		//fen lei ruke
		if(line.startsWith("CLUE_ID")) return ;
		String []arryFileds = line.split("\t");

		for(int i = 0; i < arryFileds.length; i++){	
			jedis.set("good", "134");
			System.out.println("123");
		}
		
	}
	public static void main(String []arg){
		System.out.println("pro BCP File");

		ReadBcp bcpFile = new ReadBcp();
		bcpFile.proOneBcp("124-330000-1490363235-22538-DATA_SHOP-0.sjz_pretreat.01303.124_3.1490186785.bcp");
		ReadBcp bcpFile1 = new ReadBcp();
		bcpFile.proOneBcp("124-330000-1490363235-22538-DATA_SHOP-0.sjz_pretreat.01303.124_3.1490186785.bcp", false);

	}
}
