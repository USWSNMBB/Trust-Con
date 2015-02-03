using System;
using hp;
using System.Windows.Forms;
using System.Threading;
using System.IO;
using System.Diagnostics;
using System.Collections;


namespace HPsmtp
{
	public class smtp
	{
		ManualResetEvent wait = new ManualResetEvent(false);
		public string to;
		public string from;
		public string body;
		public string subj;
		public bool servermode;
		public string srv;
		public string name;
		public string lastserver;
		public int tmeout=0;
		public int amount=0;
		public hpsocket sck = new hpsocket();


		public string MXLookup(string dmn)
		{
			
			string nl = "\r\n";
			if (dmn.IndexOf("@") > -1)
			{
				dmn = dmn.Substring(dmn.IndexOf("@")+1,(dmn.Length-dmn.IndexOf("@")-1));
			}
			ProcessStartInfo psi = new ProcessStartInfo();
			psi.FileName = "nslookup.exe";
			psi.Arguments = "-type=mx "+dmn;
			psi.RedirectStandardOutput = true;
			psi.UseShellExecute = false;
			Process p = Process.Start(psi);
			StreamReader stmrdr = p.StandardOutput;
			string s = stmrdr.ReadToEnd();
			s = s.Substring(s.IndexOf("anger")+8,s.Length-(s.IndexOf("anger")+8));
			s = s.Substring(0,s.IndexOf(nl));
		    
			return s;

		}
        


		public bool conn()
		{
			int ret;
			int done=0;
			string rec="";
			string[] receitps;
			char[] sep;


			ret=sck.connect(srv,"25");
			if (ret == 1)
			{
				return true;
			}
			else
			{
				return false;
			}
		}


		public bool close()
		{
		int ret;
		ret=sck.write("quit\r\n");
		ret=sck.close();
		return true;
		}


		public bool Mail() 
		{

			int ret;
			int done=0;
			string rec="";
			string[] receitps;
			char[] sep;
			
			rec="";
			tmeout=0;

			if (to.IndexOf(";") < 1)
			{
				amount=1;
			}
			else
			{
				// to do for multiple email address's
				//receitps = (to.Split(sep,50));
			}

			if(servermode == true)
			{
				srv = MXLookup(to.Substring(to.IndexOf("@"),to.Length-to.IndexOf("@")));
			}




			ret=sck.write("ehlo "+srv+"\r\n");
			while(true)
			{
				rec=sck.readbuff();
				if (rec.Length > 0)
					break;
				wait.WaitOne(1000,false);
				tmeout++;
				if (tmeout > 5)
				{
					return false;
				}
			}

			if (rec.Substring(0,1) == "2")
			{
				rec="";
				tmeout=0;
				ret=sck.write("mail from: <"+from+">\r\n");
				while(true)
				{
					rec=sck.readbuff();
					if (rec.Length > 0)
						break;
					tmeout++;
					if (tmeout > 5)
					{
						return false;
					}
					wait.WaitOne(1000,false);
				}





				while(done < amount)
				{
					if (rec.Substring(0,1) == "2")
					{
						rec="";
						tmeout=0;
						ret=sck.write("rcpt to: <"+to+">\r\n");
						while(true)
						{
							rec=sck.readbuff();
							if (rec.Length > 0)
								break;

							wait.WaitOne(1000,false);
							tmeout++;
							if (tmeout > 5)
							{
								return false;
							}
											
						}
					}
					done++;
				}






				if (rec.Substring(0,1) == "2")
				{
					rec="";
					tmeout=0;
					ret=sck.write("data\r\n");
					while(true)
					{
						rec=sck.readbuff();
						if (rec.Length > 0)
							break;

						wait.WaitOne(1000,false);
						tmeout++;
						if (tmeout > 5)
						{
							return false;
						}
					}
					if (rec.Substring(0,1) == "3")
					{
						rec="";
						tmeout=0;
						ret=sck.write("From: "+name+" <"+from+">\r\nSubject: "+subj+"\r\n\r\n"+body+"\r\n.\r\n");
						while(true)
						{
							rec=sck.readbuff();
							if (rec.Length > 0)
								break;

							wait.WaitOne(1000,false);
							tmeout++;
							if (tmeout > 5)
							{
								return false;
							}
						}

						if (rec.Substring(0,1) != "2")
						{
							amount=-1;
							return false;
						}

					}
					else
					{ 
						amount=-1; 
						return false;
					}
				}
				else 
				{ 
					amount=-1; 
					return false;
				}

			}
			else 
			{ 
				amount=-1;
				return false;
			}
			return true;
		}
	}
}

