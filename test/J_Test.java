import redis.clients.jedis.Jedis; 
public class J_Test{
	public void J_Test(){
		System.out.println("start");
	}
	public void J_end(){
		System.out.println("j_Test");
	}
	public static void main(String []argv){
		J_Test jtest = new J_Test();
		Jedis jedis = new Jedis("localhost", 6340, 15000);
		jedis.set("good", "134");
		System.out.println(jedis.get("good"));
		//jedis.close();	
		jtest.J_end();
	}

}

