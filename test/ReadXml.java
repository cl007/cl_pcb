import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.Attribute;

class ReadConf extends ReadXml{
	Map<String, Map<String, String>> map;
	ReadConf(String pathFile){
		super(pathFile);
		map = new HashMap<String, Map<String, String>>();
	}
	public Element proNowNodes(Element node){

		Element e = null;
		if(node.getName() == "DATASET"){
			Map<String, String> map1 = new HashMap<String, String>();
			Attribute nameAttribute = node.attribute("name");
			//	
			Iterator<Element> iterator = node.elementIterator();
			while(iterator.hasNext()){
			    e = iterator.next();
				if(e.getName() == "ITEM"){
					String key = null;
					String value = null;
					List<Attribute> list= e.attributes();
					for(Attribute attribute:list){
						if(attribute.getName() == "key" ){
							key = attribute.getValue();
						}
						if(attribute.getName() == "val" ){
							value = attribute.getValue();
						}
					}
					if(key != null && value != null){
						map1.put(key, value);
					}
				}
		 	}
			System.out.println("good12313");
			map.put(nameAttribute.getValue(), map1);
		    return e;	
		}
      	//	if(null != e) 
		else{
			return node;
		}
		
	}


	public Map<String, String>  getMap(String key){
			
			return  map.get(key) == null ? null:map.get(key);
	}
	public String  getKey(String key, String key1){
			
			return  map.get(key).get(key1) == null ? null:map.get(key).get(key1);
	}
	public Map<String, Map<String, String>> get(){
			return map==null? null:map;
	}
}

public class ReadXml{
	protected Element root;
	ReadXml(String pathFile){
		System.out.println("XML_UTF_8");
		SAXReader  reader = new SAXReader();
		try{
			Document document =  reader.read(new File(pathFile));
			root =  document.getRootElement();	
		}catch(DocumentException e){
			e.printStackTrace();
		}
	}

	
	public void loopNodes(Element node){
		node = proNowNodes(node);
		if(node != null){
			Iterator<Element> iterator = node.elementIterator();
			while(iterator.hasNext()){
				Element e = iterator.next();
				loopNodes(e);
			}
		}
	}

	public Element proNowNodes(Element node){
		System.out.println("now nodes: " + node.getName());
		List<Attribute> list= node.attributes();
		for(Attribute attribute:list){
				System.out.println("attribute: "+attribute.getName() + ":" + attribute.getValue());
		}

		if(!(node.getTextTrim().equals(""))){
			System.out.println("1111" + node.getName()+":" + node.getText());
		}	
		return node;
	}

	/*public static boolean  fileMove(String pathSrc, String pathDest){

		File sFile = new File(pathSrc);
		try{
			if(sFile.rnameTo(new File(pathDest)))
			{
				return true;
			}
			else{
				return false;
			}
		}catch(IOException e){
			e.printStackTrace();
		}

	}*/
	public static void main(String []argv){
		System.out.println("Parse xml File");
		ReadConf readConf = new ReadConf("merger_bcp.xml");
		readConf.loopNodes(readConf.root);
		
		Map<String, String> map1 = readConf.getMap("DATA_EMAIL");
		System.out.println(map1);
		for(Object obj:map1.keySet()){
			//List<String> l2 = map1.get(obj); 
			System.out.println(obj + "  value  " + map1.get(obj));
		}
		Map<String, String> map2 = readConf.getMap("DATA_HTTP");
		System.out.println(map2);
		
		
	}
}



