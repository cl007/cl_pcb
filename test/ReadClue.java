/*
 *   read xml get clue expression
 *   auth cunli
 *   date 2017/6/27
 */
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


class Clue{
	private  String controlType;
	private  String clueID;
	private  String	messageType;
	private  String alertType;
	private  String commitServer;
	private  String controlAreaType;
    private   TreeNode   head;  

	Clue(){
		System.out.println("now clue node");
	}
	public void setHead(Element e){
		 this.head = setBtree(e, null);
	}
	public TreeNode getHead(){
		 return this.head;
	} 
	public String getControlType(){
		return this.controlType;
	}
	
	public String getClueID(){
		return this.clueID;
	}

	public String getMessageType(){
		return this.messageType;
	}
	
	public String getAlertType(){
		return this.alertType;
	}
	public String getCommitServer(){
		return this.commitServer;
	}
	
	public String getControlAreaType(){
		return this.controlAreaType;
	}

	
	public void setPublicAttr(Element node){

		String key   = null;
		String value = null;
		List<Attribute> list= node.attributes();
		for(Attribute attribute:list){
			if(attribute.getName() == "key" ){
				key = attribute.getValue();
			}
			if(attribute.getName() == "val" ){
				value = attribute.getValue();
			}
		}

		if(key == "I010029"){
			this.controlType = value;
		}	
		if(key == "H010005"){
			this.clueID = value;
		}
		if(key == "I010013"){
			this.messageType = value;
		}
		if(key == "TJ90000"){
			this.alertType = value;
		}
	    if(key == "TJ90002"){
			this.controlAreaType = value;
		}
		if(key == "J010016"){
			this.commitServer = value;
		}
	}

	public void setNodeAttrPtr(Element node, TreeNode treeNode){
		List<Attribute> list= node.attributes();
			for(Attribute attribute:list){
				if(attribute.getName() == "key" ){
					treeNode.setCode(attribute.getValue());
				}
				if(attribute.getName() == "val" ){
					treeNode.setClueValue(attribute.getValue());
				}
				if(attribute.getName() == "rel" ){
					treeNode.setLogical(attribute.getValue());
				}
				if(attribute.getName() == "match_mode" ){
					treeNode.setMatchType(attribute.getValue());
				}
				if(attribute.getName() == "filed_type" ){
					treeNode.setCluetype(attribute.getValue());
				}
			}
	}
	public TreeNode setBtree(Element node, Element node1 ){

		if(node == null)  return null;
		TreeNode treeNode = new TreeNode();
		Element e;
		Element e1;
		// backfill bt attr
		setNodeAttrPtr(node, treeNode);

		if(node1 != null){
			String Identity_type_val; 
			String Identity_val;
			String Match_mode;
			if(treeNode.getCode()== "I010056") Identity_type_val = treeNode.getClueValue();
			if(treeNode.getCode() == "I010043"){ Identity_val = treeNode.getClueValue(); Match_mode = treeNode.getMatchType();}
			System.out.println(treeNode);
			treeNode.delNode();
			setNodeAttrPtr(node1, treeNode);
			if(treeNode.getCode() == "I010056") Identity_type_val = treeNode.getClueValue();
			if(treeNode.getCode() == "I010043"){ Identity_val = treeNode.getClueValue(); Match_mode = treeNode.getMatchType();}
			System.out.println(treeNode);
		}else
		{
			System.out.println(treeNode);
			//normal clue
		}
		//traversers all the child node under now node
		Iterator<Element> iterator = node.elementIterator();
		//if the first node is child
		if(iterator.hasNext()){
			e = iterator.next();
			e1 = null;
			Attribute value = e.attribute("key");	
			if(value != null && (value.getValue().equals("I010056") || value.equals("I010043"))){
				if(iterator.hasNext()){e1 = iterator.next();}
			}
			treeNode.leftBtNode = setBtree(e, e1);
		}
		//other is brothre node
		if(iterator.hasNext()){
			e = iterator.next();
			e1 = null;
			Attribute value1 = e.attribute("key");
			if(value1 != null && (value1.getValue().equals("I010056") || value1.getValue().equals("I010043"))){
				if(iterator.hasNext()){e1 = iterator.next();}
			}
			treeNode.rightBtNode = setBtree(e, e1);
			TreeNode treeNode2 = treeNode.rightBtNode;
			while(iterator.hasNext()){
				if(iterator.hasNext()){
					e = iterator.next();
					e1 = null;
					Attribute value2 = e.attribute("key");
					if(value2 != null && (value2.getValue().equals("I010056") || value2.getValue().equals("I010043"))){
						if(iterator.hasNext()){ e1 = iterator.next();}
					}
					treeNode2.rightBtNode = setBtree(e, e1);
					treeNode2 = treeNode2.rightBtNode;
				}
			}
		}	
		return treeNode;
	}
			
}


 
public class ReadClue extends ReadXml{


	ReadClue(String pathFile){
		super(pathFile);
		System.out.println("readClue");		
	}	

	public boolean travelXml(Element node){
		if(node.getName() == "MESSAGE"){
			Iterator<Element> iterator = node.elementIterator();
			while(iterator.hasNext()){
				Element e = iterator.next();
				if(e.getName() == "DATASET"){
					proXmlNodes(e);// get one of clue // loop single 
					return true;
				} 
			}	
		}
		return false;
	}
   
	public void proXmlNodes(Element node){
			Clue  clue = new Clue();
			Iterator<Element> iterator = node.elementIterator();
			while(iterator.hasNext()){
				Element e = iterator.next();
				if(e.getName() == "ITEM"){
					clue.setPublicAttr(e);
				} 
				if(e.getName() == "CONDITION"){
					//handle head node
					clue.setHead(e);
					System.out.println(clue);
				}
			}
			traverseBt(clue.getHead());
			//;			
	}
	public void traverseBt(TreeNode  node){
		TreeNode temp = node;
			
		if(null == temp){
			System.out.println("null");
			return ;
		}
		if(temp.getCode() != null){System.out.print(temp.getCode());}
		if(temp.getClueValue() != null){System.out.print("  "+temp.getClueValue());}
		if(temp.getCluetype() != null){System.out.print(" "+temp.getCluetype());}
		if(temp.getLogical() != null){System.out.print("  "+temp.getLogical());}	
		System.out.println("  now node  ");
		if(temp.leftBtNode != null){
			System.out.println("left");
			traverseBt(temp.leftBtNode);
		}
		if(temp.rightBtNode != null){
			System.out.println("right");
			traverseBt(temp.rightBtNode);
		}
	}
	public static void main(String []args){
		System.out.println("Parse xml File of clue");
		ReadClue readClue= new ReadClue("cmp_01_xml_clue_increment_0007_1497496803.xml.new");
		readClue.travelXml(readClue.root);
	}

}


class TreeNode{	
	private String code;
	private String clueValue;
	private String cluetype;
	private String matchType;
	private String logical;
	private boolean  compresult;
	public TreeNode leftBtNode;
	public TreeNode rightBtNode;

	TreeNode(){
		this.code       = null;
		this.clueValue  = null;
		this.cluetype   = null;
		this.matchType  = null;
		this.logical    = null;
		this.compresult = false;
		this.leftBtNode = null;
		this.rightBtNode= null;

	}
	public void setLogical(String logical){
		this.logical = logical;
	}
	public void setCode(String code){
		this.code       = code;
	}
	public void setClueValue(String clueValue){
		this.clueValue  = clueValue;
	}
	public void setCluetype(String clueType){
		this.cluetype   = clueType;
	}
	public void setMatchType(String matchType){
		this.matchType  = matchType;
	}
	public void setCompResult(boolean  compResult){
		this.compresult = compResult;	
	}

	public String getLogical(){
		return this.logical;
	}
	public String getCode(){
		return this.code ;
	}
	public String getClueValue(){
		return this.clueValue;
	}
	public String getCluetype(){
		return this.cluetype;
	}
	public String getMatchType(){
		return this.matchType ;
	}
	public boolean getCompResult(){
		return this.compresult;	
	}
	public void delNode(){
		this.code       = null;
		this.clueValue  = null;
		this.cluetype   = null;
		this.matchType  = null;
		this.logical    = null;
		this.compresult = false;
		this.leftBtNode = null;
		this.rightBtNode= null;	
	}
}


//end 
