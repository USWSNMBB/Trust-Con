import java.io.*;
import java.net.*;

class Client
{

  public static void main(String[] args) throws Exception
  {
    try
    {
		
      Socket ss= new Socket("localhost",4445);
      InetAddress a=InetAddress.getLocalHost();
      System.out.println("Connection Establish wid Server");


      ObjectInputStream is=new ObjectInputStream (ss.getInputStream());
      ObjectOutputStream os=new ObjectOutputStream(ss.getOutputStream());

      String msg="";
      do
      {
		  try
		  {
			  msg=(String)is.readObject();
			  System.out.println(msg);
		  }
		  catch(ClassNotFoundException cnfe){}
	  }
	  while(!msg.equals("Server ==>> Hi Client"));


      BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
      System.out.println("Please enter name you want to search in the table:");
      String str=br.readLine();
      os.writeObject(str);
      os.flush();


	  os.writeObject("Client ==>> Thanks");
	  os.flush();

	msg="";
	  do
	      {
	  		  try
	  		  {
	  			  msg=(String)is.readObject();
	  			  System.out.println(msg);
	  		  }
	  		  catch(ClassNotFoundException cnfe){}
	  	  }
	  while(!msg.equals("Server ==>> BYE"));




    }
    catch (SocketException se)
    {
      System.out.println(se);
    }
  }
}



