using System;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Windows.Forms;

namespace hp
{

	public class hpsocket
	{

		private IPEndPoint iep ;
		private AsyncCallback callbackProc ;
		private int port ;
		private Socket sock ;
		int closed = 0;
		string rec="";
		int tmewait=0;
		Byte[] buff = new Byte[32767];
		ManualResetEvent wait = new ManualResetEvent(false);

		public int connect(string svr,string prt)
		{
			port	= int.Parse(prt);
			IPHostEntry IPHost = Dns.Resolve(svr); 
			string []aliases = IPHost.Aliases; 
			IPAddress[] addr = IPHost.AddressList; 

			try
			{
				sock = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
				iep	= new IPEndPoint(addr[0],port);  
				sock.Blocking = false ;	
				callbackProc = new AsyncCallback(ConnectCallback);
				sock.BeginConnect(iep , callbackProc, sock ) ;				 
			}
			catch(Exception ex)
			{
				//MessageBox.Show(ex.Message , "Application Error!!!" , MessageBoxButtons.OK , MessageBoxIcon.Stop );
				return 0;

			}

			while(true)
			{
				if (sock.Connected)
				{
					return 1;
				}
				tmewait++;
				wait.WaitOne(500,false);
				if (tmewait > 15)
				{
					return 0;
				}
			}
		}
        

		public int close()
		{
			try
			{
				sock.Shutdown( SocketShutdown.Both );
				sock.Close();
				closed = 1;
			}
			catch(Exception ex)
			{
			}
			return 0;
		}

		public string readbuff()
		{
				return rec;
		}


		public int write(string str)
		{
			rec="";
			try
			{
				Byte[] smk = new Byte[str.Length];
				for ( int i=0; i < str.Length ; i++)
				{
					Byte ss = Convert.ToByte(str[i]);
					smk[i] = ss ;
				}

				IAsyncResult ar2 = sock.BeginSend(smk , 0 , smk.Length , SocketFlags.None , callbackProc , sock );
				sock.EndSend(ar2);
			}
			catch(Exception ers)
			{
			return 0;
				//MessageBox.Show("ERROR IN RESPOND OPTIONS");
			}
			return 1;
		}
		
		public void OnRecievedData( IAsyncResult ar )
		{
			int nBytesRec;
			Socket sock = (Socket)ar.AsyncState;
			try
			{
				nBytesRec = sock.EndReceive( ar );	
			}
			catch(Exception er)
			{
				nBytesRec = 0;
               
				
			}

			if( nBytesRec > 0 )
			{
				 rec = Encoding.ASCII.GetString( buff, 0, nBytesRec );
				
			}
			else
			{
				if (closed == 0)
				{
					sock.Shutdown( SocketShutdown.Both );
					sock.Close();
				}
			}
			
		}

		

		
		public void ConnectCallback( IAsyncResult ar )
		{
			try
			{
				Socket sock1 = (Socket)ar.AsyncState;
				if ( sock1.Connected ) 
				{	
					AsyncCallback recieveData = new AsyncCallback( OnRecievedData ); 
					sock1.BeginReceive( buff, 0, buff.Length, SocketFlags.None, recieveData , sock1 );
				}
			}
			catch( Exception ex )
			{
				//MessageBox.Show( this, ex.Message, "Setup Recieve callbackProc failed!" );
			}
		}

	}
}
