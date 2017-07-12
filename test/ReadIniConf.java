import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
 
/**
	* ini parse
	* @auth cl
	* date
*/
public class ReadIniConf{	
	//	
	private Map<String, Map<String, List<String>>> map = null;
	//
	private String currentSection = null;
	
	public ReadIniConf(String path){
		//System.out.println("default");

		map = new HashMap<String, Map<String, List<String>>>();

		try{
			BufferedReader reader = new BufferedReader( new FileReader(path));
			read(reader);
			reader.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	private void read(BufferedReader reader) throws IOException{
		//System.out.println("read one line");
		String line;
		while((line = reader.readLine())!= null){
			praseLine(line);
		}
		reader.close();
	}
	private void  praseLine(String line){
		//System.out.println("parse one line");

		line = line.trim();

		if(line.matches("^#*.$|^*.$\n")) return;
		else if(line.matches("^\\[\\S+\\]$"))
		{
			this.currentSection = line.replaceFirst("^\\[(\\S+)\\]$", "$1");
			setSection();
		}
		else if(line.matches(".*=.*$"))
		{
			int i = line.indexOf("=");
			String key = line.substring(0, i).trim();
			String value = line.substring(i+1).trim();
			setKeyValue(key, value);
		}
	}
	public void setSection(){
		//System.out.println("set section "+ this.currentSection);

		if(!map.containsKey(currentSection)){
			Map<String, List<String>> childMap = new HashMap<String, List<String>>();
			this.map.put(currentSection, childMap);
		}	
	}
	private void setKeyValue(String key, String value){
		//System.out.println(key + " = " + value);
		if(!this.map.containsKey(currentSection)){
			return;
		}
		Map<String, List<String>> childMap = this.map.get(currentSection);
		if(!childMap.containsKey(key))
	    {
			List<String> list = new ArrayList<String> ();
			list.add(value);
			childMap.put(key, list);
	    }else{

			childMap.get(key).add(value);
		}

	}
	public List<String> get(String section, String key){
		//System.out.println("the value");
		if(this.map.containsKey(section)){
			return map.get(section).containsKey(key)?
				   map.get(section).get(key):null;
		}
		return null;
	}
	public Map<String, List<String>> get(String section){
		//System.out.println("one section map");

		return map.containsKey(section)?map.get(section):null;
	}

	public Map<String, Map<String, List<String>>> get(){
		return map;
	}
	
	/*public static void main(String []argv){
		System.out.println("INI_PRASE");
		ReadIniConf read_ini = new ReadIniConf("Merger_table_bcp.ini");
		List<String> l1 = read_ini.get("DATA_EMAIL","datatype");
        //test
		Iterator it = l1.iterator();
		while(it.hasNext())
	    {
	    	String str = (String)it.next();
			System.out.println(str+ "ini key value");
	    }
		Object []arry = l1.toArray();
		for(int i = 0; i < arry.length; i++){
			System.out.println("good idear" + (String)arry[i]);
		}	
		System.out.println(l1.get(0));

		Map<String, List<String>> map1 = read_ini.get("DATA_EMAIL");

		for(Object obj:map1.keySet()){
			List<String> l2 = map1.get(obj); 
			System.out.println("l2" + l2.get(0));
		}
		
		Map<String, Map<String, List<String>>> map3 = read_ini.get();

		for(Object obj1:map3.keySet()){
			Map<String, List<String>> map4 = map3.get(obj1);
			for(Object obj2:map4.keySet()){
				List<String> l3 = map1.get(obj2); 
				System.out.println("l3" + l3.get(0));
			}
		}
	}*/
}


