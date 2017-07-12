import java.io.*;
public class OneInstance_3 {
  public static void main(String[] args) throws Exception {
    File flagFile = new File("C:\\flagFile");
    if(false == flagFile.createNewFile()) {
      System.out.println("A previous instance is already running....");
      System.exit(1);
    }
    flagFile.deleteOnExit();
    System.out.println("This is the first instance of this program...");
    // Do some work here.....
  }
}