import java.io.*;
import java.net.*;
import java.sql.*;


public class Server{

public static void main ( String args[] )

{

ServerSocket ss=new ServerSocket(4445);
Socket s;
Socket ds;
ObjectOutputStream s1;
ObjectInputStream is;

try

{

            s=ss.accept();

	    System.out.println ( "Connection requested from " + s.getInetAddress().getLocalHost() );
   	    s1 = new ObjectOutputStream(s.getOutputStream());


	    Socket cs1= new Socket("localhost",5000);
	    InetAddress a=InetAddress.getLocalHost();

	    ObjectInputStream in1=new ObjectInputStream(cs1.getInputStream());
	    ObjectOutputStream out1=new ObjectOutputStream(cs1.getOutputStream());

	    System.out.println("Connection Establish wid DataServer");


	    String msg="";
	      do
	      {
			  try
			  {
				  msg=(String)in1.readObject();
				  System.out.println(msg);
			  }
			  catch(ClassNotFoundException cnfe){}
		  }
		  while(!msg.equals("DataServer ==>> Hi Server"));



	s1.writeObject ("Server ==>> Im Server");
	s1.flush();

	s1.writeObject("Server ==>> Hi Client");
	s1.flush();

	is = new ObjectInputStream(s.getInputStream());
	int j=0;
	String ms="",name="";
	do
	{
		try
		{
			ms=(String)is.readObject();
            if(j==0)
            {
				name=ms;
				System.out.println("Client says>> Student name is "+ms);

			}
			else
			{

				System.out.println(ms);
			}
			j++;

		}
		catch(ClassNotFoundException clnf){}
     }
     while(!ms.equals("Client ==>> Thanks"));




			  out1.writeObject(name);
			  out1.flush();

			
			  out1.writeObject("Server ==>> Thanks");
			  out1.flush();

			msg="";
			  do
			      {
			  		  try
			  		  {
			  			  msg=(String)in1.readObject();
			  			  System.out.println(msg);
			  		  }
			  		  catch(ClassNotFoundException cnfe){}
			  	  }
			  while(!msg.equals("DataServer ==>>BYE"));



	s1.writeObject("Server ==>>BYE");
	s1.flush();

	s.close();

}

catch ( Exception e) { }

}



}
