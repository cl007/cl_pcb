import redis.clients.jedis.Jedis;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Iterator;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.lang.InterruptedException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.lang.Runnable;
import java.lang.Thread;


class ReadFile{
	
	BufferedReader breader;
	Long length;
	protected String []lines;
	
	ReadFile(String path){	
		File file = new File(path);
		try{
			if(file.exists()){	
				length = file.length();
			    breader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8") );
				char []bufContent = new char[length.intValue()];
				breader.read(bufContent);
				breader.close();
				lines = new String(bufContent).split("\n|\r\n");
			}
		}catch(IOException e){
			e.printStackTrace();
		}		
	}

}
class ReadBcp  extends  ReadFile{	 
	 ReadBcp(String path){
		super(path);
	 }  
	 public void procLines(Jedis jedis, Map<String, List<String>> map, Long deadTime){
		 List<String> list = map.get("idx");
		 int idx  = Integer.parseInt(list.get(0));
		 jedis.select(idx);

		 for(int i = 0; i <lines.length; i++){
			 proLine(idx,jedis, lines[i], map, deadTime); 	 
		 }
	 }
	 private void proLine(int idx, Jedis jedis, String line, Map<String, List<String>> map, Long deadTime){	 
		 //fen lei ruke
		 if(line.startsWith("#|CLUE_ID|\r\n|\n")) return ;
		 String []arryFileds = line.split("\t");
		 List<String> list = map.get("key_data");
		 String str = list.get(0).trim();
		 int index = Integer.parseInt(str);
		 Map<String, String> mapline = new HashMap<String, String>();	 
		// System.out.println(line);
		 if(arryFileds.length >=  index){
			 String key = arryFileds[index];
			 if(jedis.exists(key)){
				 /* modefine data*/
				 System.out.println("Have already");
			 }else{
				 /* get now time   LONG*/
				 Date date = new Date();
				 Long seconds = date.getTime()/1000;
				 /*time + deadtime is key, value is data line's key, in zero table */
				 jedis.select(2);
				 jedis.lpush(Long.toString(seconds+deadTime), key);
				 jedis.select(1);
				 /*time list for second find if the time had gone  can find again, in one table*/
				 jedis.lpush("Time_List", Long.toString(seconds+deadTime));
				 jedis.set(key, Integer.toString(idx));
				 jedis.select(idx);
			     mapline.put("bcpline", line);
			 	 jedis.hmset(key, mapline);
			 }
			 //System.out.println(jedis.get(key));
		 }
	 }
}
class ReadConf extends ReadFile{
	private String serverIP;
	private int port;
	int time_capture;
	Long deadTime;
	Long checkTime;
	 
	ReadConf(String path){
		super(path);
	}
	public void setKeys(){
		for(int i = 0; i <lines.length; i++){
			 String line = lines[i];
			 line = line.trim();
			 if(line.matches("^#*.$|^\\r|^\\n")) return;
			 else if(line.matches(".*=.*$")){
				 int j = line.indexOf("=");
				 String key = line.substring(0, j).trim();
				 String value = line.substring(j+1).trim();
				 if(-1 != key.indexOf("server_ip")) serverIP = value;
				 if(-1 != key.indexOf("port")) port = Integer.parseInt(value);
				 if(-1 != key.indexOf("deadtime")) deadTime = Long.parseLong(value);
				 if(-1 != key.indexOf("checktime")) checkTime = Long.parseLong(value);
				 System.out.println(serverIP);
				 System.out.println(port);
				 System.out.println(deadTime);
				 System.out.println(checkTime);
			 }
		}
	}
}

class WriteBcp{
		protected BufferedWriter bwrite;
		protected int counts;
	    WriteBcp(String path){ 	
		//System.out.println("End");
		File file = new File(path);
		try{
			 if(!file.exists()){
			 file.createNewFile();
			}
		    bwrite = new BufferedWriter(new FileWriter (file, true));
		}catch(IOException e){
			e.printStackTrace();
		}
		counts = 0;
	}
	public void writelines(String line){
		try{
			bwrite.write(line);
			System.out.println(line);
			bwrite.newLine();
			bwrite.flush();
			counts++;
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}


public class Merger_Bcp {
	protected Jedis jedisS;
	protected Jedis jedisE;
	protected Jedis jedisC;
	protected ReadIniConf file_ini;
	protected ReadConf fileConf;
	protected WriteBcp  bcpEmail;
	protected boolean flagEmail  = true;
	
	Merger_Bcp(String ip, int port, int time, String iniFile){
		fileConf = new ReadConf("merger_bcp.conf");
		fileConf.setKeys();
		jedisS = new Jedis(ip, port, time);
		jedisE = new Jedis(ip, port, time);
		jedisC = new Jedis(ip, port, time);
	    file_ini = new ReadIniConf(iniFile);
	}
	public void mergerStart(String path){ 
		while(true){
			File root = new File(path);
			File []fList = root.listFiles();
			if(null == fList || fList.length == 0) {
				System.out.println("wait^-^");
				try{
					Thread.sleep(1000);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
			else{
				for(File f:fList){
					if(f.isDirectory()){
						mergerStart(f.getAbsolutePath());
					}
					else{
						
						if((f.getAbsolutePath()).indexOf(".bcp") != -1){					
							ReadBcp bcpFile = new ReadBcp(f.getAbsolutePath());
							String fileName;
							
							System.out.println(f.getAbsolutePath());
							if((f.getAbsolutePath()).indexOf("/") != -1)
							{
								fileName = (f.getAbsolutePath()).substring((f.getAbsolutePath()).lastIndexOf("/")+1);
							}
							else
							{
								fileName =f.getAbsolutePath();
							}
							System.out.println(fileName);
							if(fileName.indexOf("DATA") != -1){
								String dateTyeptemp = fileName.substring(fileName.indexOf("DATA"));
								if(dateTyeptemp.indexOf("-") != -1){
									String dateType = dateTyeptemp.substring(0, dateTyeptemp.indexOf("-"));
									System.out.println(dateType);
									Map<String, List<String>> map = file_ini.get(dateType);
									if(map != null){
										bcpFile.procLines(jedisS, map, fileConf.deadTime);
									}
								}
							}
				
						}
					}
				}


			}

		}
	}
	private boolean createNewFile(boolean flag, String path, WriteBcp bcpFile, String bcpHead){

		if(flag){
			bcpFile = new WriteBcp(path);
			List<String> bcphead= file_ini.get(bcpHead, "bcp_head");	
			bcpFile.writelines(bcphead.get(0));
			flag = false;
			//bcpFile.counts = 0;
		}
		if(bcpFile.counts == 4999){
			flag = true;
			try{
				bcpFile.bwrite.close();
			}catch (IOException e){
				e.printStackTrace();
			}
		}
		return flag;

	}
	private void writeToBcp(String path, int idx, String line){
		System.out.println(idx);
		switch(idx){
			case 3:
					if(flagEmail){
						bcpEmail = new WriteBcp(path);
						List<String> bcphead= file_ini.get("DATA_EMAIL", "bcp_head");	
						bcpEmail.writelines(bcphead.get(0));
						flagEmail = false;
						bcpEmail.counts = 0;
					}
					if(bcpEmail.counts == 4999){
						flagEmail = true;
						try{
							bcpEmail.bwrite.close();
						}catch (IOException e){
							e.printStackTrace();
						}
					}

					if(null != line)
					{
						bcpEmail.writelines(line);
					}

			case 4:
				;
			case 5:
				;
			default:
				System.out.println("error");

		}
	}
	
	public void mergerEnd (String path) { 
	    while(true){
			jedisE.select(2);
			Long seconds = System.currentTimeMillis()/1000;
			String key = seconds.toString();
			System.out.println(key);
			List<String> values = jedisE.lrange(key, 0 ,-1);
			System.out.println(values);
			if(values.size() != 0){
				jedisE.del(key);
				Iterator it = values.iterator();
				while(it.hasNext())
				{
					String keynext = (String)it.next();
					jedisE.select(1);
					String idx = jedisE.get(keynext);
					jedisE.del(keynext);
					if(idx != null){
						jedisE.select(Integer.parseInt(idx));
						if(jedisE.exists(keynext)){
							writeToBcp(path, Integer.parseInt(idx), jedisE.hmget(keynext, "bcpline").get(0));	
						}
					}
				}
			}
			else{
				System.out.println("wait^-^");
				try{
					Thread.sleep(1000);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}	
	}

	public void everyDayCheckAll(){
		Long firstTime = System.currentTimeMillis()/1000;
		
		while(true){
			Long nowTime = System.currentTimeMillis()/1000;
			if(nowTime - firstTime < fileConf.checkTime)
			{
				System.out.println("wait^-^");
				try{
					Thread.sleep(1000);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
				
			}
			else{
					Long seconds = System.currentTimeMillis()/1000;
					jedisC.select(1);
				
					String time = jedisC.lpop("Time_List");
					if(time == null){System.out.println("over"); return;}
					Long times = Long.parseLong(time);
					while(times < seconds){
						jedisC.select(2);
						if(jedisC.exists(time)){
							System.out.println("good");
						}
						jedisC.select(1);
						time = jedisC.lpop("Time_List");
						if(time == null){return;}
						times = Long.parseLong(time);
					}
				}
			}
	}
	public static void main(String []argv) throws IOException{
		System.out.println("MergerBcp");	
	
		Merger_Bcp mergerBcp = new Merger_Bcp("localhost", 6379, 15000, "Merger_table_bcp.ini");

		RunnableMergerS runnablemergerS = new RunnableMergerS(mergerBcp);
		Thread t = new Thread(runnablemergerS);	
		t.start();	
        
		
		RunnableMergerE runnablemergerE = new RunnableMergerE(mergerBcp);
		Thread t1 = new Thread(runnablemergerE);	
		t1.start();
		/*try{
			t.join();
		}catch (InterruptedException e)
		{
			e.printStackTrace();
		}*/
		mergerBcp.everyDayCheckAll();
	}
}

// 
class RunnableMergerS implements Runnable{

	Merger_Bcp mergerbcp;
	
	RunnableMergerS(Merger_Bcp mergerbcp){
		this.mergerbcp = mergerbcp;
	}

	public void run(){
		mergerbcp.mergerStart("./");

	}
}
class RunnableMergerE implements Runnable{

	Merger_Bcp mergerbcp;
	
	RunnableMergerE(Merger_Bcp mergerbcp){
		this.mergerbcp = mergerbcp;
	}
	public void run(){
		mergerbcp.mergerEnd("1.bcp");

	}
}


