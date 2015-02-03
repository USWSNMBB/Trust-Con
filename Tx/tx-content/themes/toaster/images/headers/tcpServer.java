/** **************************************************************
 * tcpServer.java
 *
 * usage : java tcpServer .
 * default port is 5050.
 * connection to be closed by client.
 * this server handles only 1 connection.
 * this server responds to the action FIND to search an array for a name
 *
 * Copyright (c) 2002-2007 Advanced Applications Total Applications Works.
 * (AATAW)  All Rights Reserved.
 *
 * AATAW grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to AATAW.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. AATAW AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL AATAW OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes or for commercial purposes.
 *
 * Changelog:
 *****************************************************************/

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

/** *****************************************************************
 * The tcpServer class defines the framework for creating a server
 * based multithreaded application. The server responds to request
 * from a Client. The purpose of the tcpServer class is to create
 * a server that
 *  1- Creates a server main frame
 *  2- Establishes a server socker
 *  3- Accepts clients in a multithreaded environment
 ****************************************************************/
public class tcpServer extends JFrame implements  ActionListener {
    private int port = 5050  , trdCnt = 0 ;
    private boolean debug = false , loopCTL = true , loopCTL2 = true ;
    private boolean foundRec = false ;
    private String messageTokens[]  = new String[ 16 ] ,
             addrRecord[][]  = {
                  {"James", "Wilie", "123 Main St.", "919-555-3442"},
                  {"Julie", "Smith", "123 Oak St.", "919-555-3782"},
                  {"Mary", "Easley", "123 Walnut St.", "919-555-5552"},
                  {"Cato", "Willingham", "123 Jones St.", "919-555-3492"},
                  {"Mike", "Jordan", "123 Saunders St.", "919-555-3882"},
                  {"Carole", "Fusemi", "123 Oak Lane", "919-555-3472"},
                  {"Peter", "Wie", "123 Oak Way", "919-555-3642"},
                  {"Donald", "Hill", "123 Oak Blvd.", "919-555-3452"},
                  {"Regina", "Bell", "123 Walnut Lane", "919-555-3342"},
                  {"Alicia", "Keyes", "123 Walnut Way", "919-555-3332"},
                  {"Charles", "Lawrence", "123 Whatis St.", "919-555-3222"},
                  {"Robert", "Urengo", "123 Academy St.", "919-555-3112"},
                  {"Lisa", "Fischer", "123 Willouby St.", "718-555-6754"}, {" ", " ", " ", " "}, {" ", " ", " ", " "},
                  {" ", " ", " ", " "}, {" ", " ", " ", " "}, {" ", " ", " ", " "}
             } ;
    private ServerSocket server_socket;
    private BufferedReader input;
    private PrintWriter output;
    private Container c ;
    private JTextArea display ;
    private JButton cancel , send, exit;
    private JPanel buttonPanel ;
    private StringTokenizer tokens ;
    private Thread thrd[] ;
    private GregorianCalendar cal;
    private String message = "" ;

    /** *********************************************************
     * This is the tcpServer constructor which is used to
     * initialize the tcpServer() object.
     ************************************************************/
    public  tcpServer() {
       super ( "Multithreaded Server II" ) ;

       setupThreads() ;

       setup() ;

       RunServer() ;
    }

    /**  **************************************************************
     * The setThreadcount() method resets the thread count.
     ****************************************************************/
    public  void setThreadcount( int a ) {
       trdCnt = a ;
    }

   /**  ******************************************************************
    * The setupThreads() method creates a Thread array.
    *********************************************************************/
    public void setupThreads() {

      thrd = new Thread[ 15 ] ;
    }

    /** *********************************************************
     * The setUp() method does the intializes the application's
     * 1- JButtons
     * 2- JPanel
     * 3- JTextArea
     * 4- Set the size
     * 5- Set the location of the application on the screen
     * 6- Make the application visiable
     ************************************************************/
    public  void setup() {
      c = getContentPane();

      setUpButtons() ;

      setUpPanels() ;

      setUpTextArea() ;

      setSize( 400, 400 );
      setLocation( 10, 20 ) ;
      show();
    }

    /** *********************************************************
     * The setUpTextArea() method
     * 1- Creates the display JTextArea
     * 2- Adds a ScrollPane( to the display TextArea
     ************************************************************/
    public  void setUpTextArea() {

      display = new JTextArea();
      display.setEditable( false );
      addWindowListener( new WindowHandler( this ) );
      c.add( new JScrollPane( display ),
             BorderLayout.CENTER );
    }

    /** *********************************************************
     * The setUpPanels() method
     * 1- Creates the buttonPanel JPanel
     * 2- Adds the exit button to the buttonPanel
     * 3- Adds the buttonPanel southern part of the content pane
     ************************************************************/
    public  void setUpPanels() {
       buttonPanel = new JPanel() ;
       buttonPanel.add( exit ) ;
       c.add( buttonPanel , BorderLayout.SOUTH) ;
    }

    /** *********************************************************
     * The setUpButtons() method
     * 1- Creates the exit JButton
     * 2- Sets the background color
     * 3- Sets the foreground color
     * 4- Add an ActionListener to the exit button
     ************************************************************/
    public  void setUpButtons() {
      exit = new JButton( "Exit" );
      exit.setBackground( Color.red ) ;
      exit.setForeground( Color.white ) ;

      exit.addActionListener( this );
    }

   /** *********************************************************
    * The RunServer() method in the server reads and writes data to
    * the client. This method
    * 1- Sets up a ServerSocket
    * 2- Sets up means to read from the socket
    * 3- Sets up means to write to the socket
    * 4- Sets up a filter to respond to specific request from the
    *    Client
    ************************************************************/
    public void RunServer() {
       try {

          server_socket = new ServerSocket( 5050, 100,
                                  InetAddress.getByName("127.0.0.1"));
          display.setText("This example is presented by Ronald Holland \nat Total Application Works\n" ) ;
          display.append("\nServer waiting for client on port " +
			       server_socket.getLocalPort() + "\n");

          // server infinite loop
          while( loopCTL  ) {
             Socket socket = server_socket.accept();
             display.append("\nNew connection accepted " +
				   socket.getInetAddress() +
				   ": Port " + socket.getPort() + "\n");
             input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             output = new PrintWriter(socket.getOutputStream(),true);
                 // Construct handler to process the Client request message.
             try {
                MyThread request =
                              new MyThread( this , socket , trdCnt );
                thrd[ trdCnt ]  = request ;

                  // Start the thread.
                thrd[ trdCnt ].start() ;
                trdCnt++ ;
             }
             catch(Exception e) {
	        sysPrint( "" + e);
             }
          }
       }
       catch (IOException e) {
          display.append("\n" + e);
       }
    }

   /** *********************************************************
    * This method responds to the exit button being pressed
    * on the tcpServer frame.
    *************************************************************/
   public void actionPerformed( ActionEvent e )    {
      if ( e.getSource() == exit )
         closeConnection() ;
   }

   /** *********************************************************
    * This method closes the socket connect to the server.
    ************************************************************ */
   public  void closeConnection() {
      sysPrint("closeConnection() 1: closing down application.") ;
      try {
         server_socket.close();
         System.exit( 0 );
      }
      catch ( IOException e ) {
         display.append("\n" + e);
         System.exit(  1  ) ;
      }
   }

   /** ***********************************************************
    * The sysExit() method is called in response to a close
    * application event.
    ************************************************************* */
   public void sysExit( int ext ) {
      loopCTL  = false ;
      loopCTL2 = false ;
      closeConnection() ;
      System.exit( ext ) ;
   }

   /** ***********************************************************
    * The sysPrint method prints out debugging messages.
    ************************************************************ */
   public void sysPrint( String str ) {
      if( debug ) {
         System.out.println("" + str ) ;
      }
   }

   /** ********************************************************
    * This method is the main entry point called by the JVM.
    ********************************************************  */
    public static void main(String args[]) {
       final tcpServer server = new tcpServer() ;
       server.addWindowListener(
         new WindowAdapter() {
            public void windowClosing( WindowEvent e )
            {
               server.sysExit( 0 );
               //server.closeConnection() ;
               //System.exit( 0 );
            }
         }
      );
    }


   /** ********************************************************
    * This method closes the socket connect to the server when the
    * application window is closed.
    ********************************************************  */
   public class WindowHandler extends WindowAdapter {
      tcpServer tcpS;

      public WindowHandler( tcpServer t ) { tcpS = t; }

      public void windowClosing( WindowEvent e ) { tcpS.closeConnection(); }
   }

   /** ***********************************************************
    *  The purpose of the MyThread class is to create a thread of
    *  execution to respond to client requests.  A thread is a
    *  thread of execution in a program. The Java Virtual Machine
    *  allows an application to have multiple threads of execution
    *  running concurrently.
    **************************************************************/
   public class MyThread extends Thread {
      Socket socket;
      private BufferedReader input2;
      private PrintWriter output2;
      private BufferedReader br ;
      private PrintWriter outp ;
      private int trdCnt , ar ;
      private tcpServer tcpS ;
      private String gNames = "GET /servlet/HTTPGetNames? HTTP/1.1" ;
      private String hWorld = "GET /servlet/HelloWorld? HTTP/1.1" ;
      private String gServ = "GET /servlet/HTTPGetServlet? HTTP/1.1" ;

      /** *********************************************************
       * The purpose of the MyThread() constructor is to used the
       * passed parameters to initialize MyThread class level
       * variables.
       **************************************************************/
      public MyThread( tcpServer tps , Socket soc_ket , int trd_Cnt  ) throws Exception {
         trdCnt =  trd_Cnt ;
         tcpS = tps ;
         socket  = soc_ket;
         outp         = new PrintWriter(socket.getOutputStream(),true);
         this.input2  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         this.output2 = new PrintWriter(socket.getOutputStream(),true);
         this.br      =  new BufferedReader(
                       new InputStreamReader( socket.getInputStream() ) ) ;
      }

      /** *********************************************************
       * The run() method responds to the client's request.
       **************************************************************/
      public void run() {
         int IndexOfEquals = 0 ;
         String strName = "" ;

         sysPrint( "Thread run() 1: running Thread" + (trdCnt+1) ) ;
         display.append("\nThread run() 1: running Thread" + (trdCnt+1) );

             // print received data
         try {
            while( !message.toUpperCase().equals( "QUIT" )) {
               message = (String)input2.readLine();
               sysPrint("Thread run() 2: The message is " + message ) ;
               tokens = new StringTokenizer( message ) ;


               if( message.equals( hWorld ) ) {
               // create and send HTML page to client
                  StringBuffer buf = new StringBuffer();
                  buf.append( "<HTML><HEAD><TITLE>\n" );
                  buf.append( "Hello World Servlets with the RonHoll Server Example\n" );
                  buf.append( "</TITLE></HEAD><BODY>\n" );
                  buf.append( "<H1>Hello World!</H1>\n\n" );
                  buf.append( "<H1>from Ronald S. Holland</H1>\n\n" );
                  buf.append( "<H1>at Total Application Works</H1>\n" );
                  buf.append( "</BODY></HTML>" );
                  output2.println( buf.toString() );
                  //output2.close();    // close PrintWriter stream
               }
	       else if( message.equals( gServ ) ) {
                  sysPrint( "Thread run() 3: The GETHW request is being processed." ) ;

                  //output = response.getWriter();           // get writer

                  // create and send HTML page to client
                  StringBuffer buf = new StringBuffer();
                  buf.append( "<HTML><HEAD><TITLE>\n" );
                  buf.append( "Using Servlets with the RonHoll Server Example II\n" );
                  buf.append( "</TITLE></HEAD><BODY>\n" );
                  buf.append( "<H1>Welcome to Servlets and the RonHoll Server!</H1>\n" );
                  buf.append( "<p><b><font size=5>from Ronald S. Holland</p>" );
                  buf.append( "<p><b><font size=5>at Total Application Works</p>" );
                  buf.append( "</b></font></BODY></HTML>" );
                  output2.println( buf.toString() );
                  //output2.close();    // close PrintWriter stream
               }
               else if( message.equals( gNames ) ) {
                  sysPrint( "Thread run() 4: The GETNames request is being processed." ) ;
                  String servName = "http://localhost:5050/servlet/HTTPGetAName" ;

                  // create and send HTML page to client
                  ar = addrRecord.length ;
                  sysPrint( "Thread run() 5: The length of addrRecord is " + ar ) ;
                  StringBuffer buf = new StringBuffer();
                  buf.append( "<HTML><HEAD><TITLE><br>" );
                  buf.append( "Using Servlets with the RonHoll Server Example II<br>" );
                  buf.append( "</TITLE></HEAD><BODY>\n" );
                  buf.append( "<FORM METHOD=\"GET\" ACTION=<br>" );

                  buf.append( "<H1>Welcome to Servlets and the RonHoll Server!</H1>" );
                  buf.append( "<p><b><font size=5>from Ronald S. Holland</p>" );
                  buf.append( "<p><b><font size=5>at Total Application Works</p></b>" );
                  buf.append( "<p>Select a name from the list <br>  <select name=\"getaName\">" );
                  for ( int ii = 0 ; ii < ar ; ii++ ) {
                     buf.append( "<option value=\"" + addrRecord[ ii ][ 1 ] + "\">" +
                                  " " + addrRecord[ ii ][ 1 ] + "</option>" ) ;
                     sysPrint("<option value=\"" + addrRecord[ ii ][ 1 ] + "\">" +
                                  addrRecord[ ii ][ 1 ] + "</option>" ) ;
                     sysPrint( "Thread run() 6: The value of ii is " + ii ) ;
                  }
                  buf.append( "</select><br><br><br>" ) ;
                  buf.append( "<p>Press the button to retrieve an address <br><input type=\"submit\" value=\"Get Name\">" ) ;
                  buf.append( "</FORM>" ) ;
                  buf.append( "</font></BODY></HTML>" );
                  output2.println( buf.toString() );
               }
               else   if ( tokens.countTokens() >= 1 )   {
                  int ii = 0 , recLength = addrRecord.length ;
                  while( tokens.hasMoreTokens() )  {
                     messageTokens[ ii ] = tokens.nextToken().toString() ;
                     sysPrint("Thread run() 7: " + messageTokens[ ii ] ) ;
                     ii++ ;
                  }

                  sysPrint("Thread run() 8a: " + messageTokens[ 0 ] ) ;

                  if ( messageTokens[ 0 ].toUpperCase().equals( "GET" ) ) {
                     if ( messageTokens[ 1 ].substring(0 , 4).equals( "/ser" ) ) {
                        IndexOfEquals = messageTokens[ 1 ].lastIndexOf('=') ;
                        strName = "" + messageTokens[ 1 ].substring( IndexOfEquals+1 ,
                                     messageTokens[ 1 ].length() ) ;
                        sysPrint("Thread run() 8b: " + strName + " IndexOfEquals " + IndexOfEquals) ;

                        if ( IndexOfEquals > 0 )  {
                           sysPrint("Thread run() 9: " + strName ) ;

                           ii = 0  ;
                           StringBuffer buf = new StringBuffer();

                           //foundRec =  false ;

                           while ( ii < recLength ) {
                              if ( addrRecord[ ii ][ 1 ].toUpperCase().equals( strName.toUpperCase() ) ) {
                                 display.append("\n" + "RecordFound" ) ;
                                 buf.append( "<HTML><HEAD><TITLE><br>" );
                                 buf.append( "Using Servlets with the RonHoll Server Example II<br>" );
                                 buf.append( "</TITLE></HEAD><BODY>\n" );
                                 buf.append( "" + addrRecord[ ii ][ 0 ] + "<br>" ) ;
                                 buf.append( "" + addrRecord[ ii ][ 1 ] + "<br>" ) ;
                                 buf.append( "" + addrRecord[ ii ][ 2 ] + "<br>" ) ;
                                 buf.append( "" + addrRecord[ ii ][ 3 ] + "<br>" ) ;
                                 buf.append( "</BODY></HTML>" );
                                 output2.println( buf.toString() );
                                 //foundRec =  true ;
                                 //break floop;
                              }

                              ii++ ;
                           }  // End of while loop

                        }  // End of outer if
                     }
                  }
                  else if ( messageTokens[ 0 ].toUpperCase().equals( "FIND" ) ) {
                     findName() ;
                  }
                  else if ( messageTokens[ 0 ].toUpperCase().equals( "LISTALL" ) ) {
                     listNames() ;
                  }
                  else if ( messageTokens[ 0 ].toUpperCase().equals( "ADD" ) ) {
                     addName() ;
                  }
                  else if ( messageTokens[ 0 ].toUpperCase().equals( "UPDATE" ) ) {
                     updateName() ;
                  }
                  else if ( messageTokens[ 0 ].toUpperCase().equals( "DELETE" ) ) {
                     deleteName() ;
                  }
                  ii = 0 ;
               }
               else  {
                  display.append( message );
                 // message = null; //so the loop will terminate the server
                  break;
               }
            }  // End of outer while
         }
         catch (IOException e) {
               display.append("\n" + e);
         }

         // connection closed by client
         try {
            socket.close();
            display.append("\n Connection closed by client");
         }
         catch (IOException e) {
            display.append("\n" + e);
         }
      }  // End of run() method

      /** ************************************************
       * The addName() is just a stub in this version.
       *************************************************** */
      public void addName() {

      }

      /** *********************************************************
       * This method searches for the a name matching the
       * name sent by the client. The name to serach for is in
       * messageTokens[ 1 ]; therefore, each record's second
       * field in the addrRecord array is compared to the entity in
       * messageTokens[ 1 ]. If a match is found, an indication of
       * a found record is returned; otherwise, an indication of
       * a not-found record is returned.
       *********************************************************** */
      public void findName() {
         int ii = 0 , recLength = addrRecord.length ;

         foundRec =  false ;

floop:   while ( ii < recLength ) {
            if ( addrRecord[ ii ][ 1 ].toUpperCase().equals( messageTokens[ 1 ].toUpperCase() ) ) {
               display.append("\n" + "RecordFound" ) ;
               sendData( "RECORDFOUND; " + addrRecord[ ii ][ 0 ] + "; " +
                   addrRecord[ ii ][ 1 ] + "; " +
                   addrRecord[ ii ][ 2 ] + "; " +
                   addrRecord[ ii ][ 3 ] + ";;"  ) ;
               foundRec =  true ;
               break floop;
            }

            ii++ ;
         }

         if ( !foundRec ) {
            sendData( "NOTFOUND; " + messageTokens[ 1 ] ) ;
            display.append("\n" + "NOTFOUND; " + messageTokens[ 1 ] ) ;
         }
      }

      /** *********************************************************
       * This method lists the names in the array and sends them to
       * the client.
       *********************************************************** */
      public void listNames() {
         int ii = 0 , recLength = addrRecord.length ;

         foundRec =  false ;

floop:   while ( ii < recLength ) {
            if ( !addrRecord[ ii ][ 1 ].equals( " " ) ) {
               sendData( "LISTRECORD; " + addrRecord[ ii ][ 1 ] + "; "   ) ;
               display.append("\nLISTRECORD; "  + addrRecord[ ii ][ 1 ] + "; ") ;
            }
            else
               break floop;

            ii++ ;
         }
      }

      /** ***********************************************************
       * The updateName() method is just a stub in this version.
       ***********************************************************  */

      public void updateName() {

      }

      /** *******************************************************
       * This method deletes the name matching the name sent by
       * the client. The record to be deleted is passed to the
       * Server and is contained in messageTokens[ 1 ]. The
       * addrRecord array is searched for a match to the field
       * in messageTokens[ 1 ]. If a match is found, that record
       * is deleted.
       ********************************************************* */
      public void deleteName() {
         int ii = 0 , recLength = addrRecord.length ;

         foundRec =  false ;

floop:   while ( ii < recLength ) {
            if ( addrRecord[ ii ][ 1 ].toUpperCase().equals( messageTokens[ 1 ].toUpperCase() ) ) {
               /** The record was  found */
               display.append("\n" + "RecordFound" ) ;
               sendData( "RECORDDELETED; " + addrRecord[ ii ][ 0 ] + "; " +
                   addrRecord[ ii ][ 1 ] + "; " +
                   addrRecord[ ii ][ 2 ] + "; " +
                   addrRecord[ ii ][ 3 ] + ";;"  ) ;
               foundRec =  true ;

               addrRecord[ ii ][ 0 ] = "" ;
               addrRecord[ ii ][ 1 ] = "" ;
               addrRecord[ ii ][ 2 ] = "" ;
               addrRecord[ ii ][ 3 ] = "" ;

               break floop;
            }

            ii++ ;
         }

         /** The record was not found */
         if ( !foundRec ) {
            sendData( "NOTFOUND; " + messageTokens[ 1 ] ) ;
            display.append("\n" + "NOTFOUND; " + messageTokens[ 1 ] ) ;
         }
      }

     /** ********************************************************
       * Send the found record back to the client
       ******************************************************** */
      public  void sendData(String str) {
         output2.println( str );
         output2.flush() ;
      }
   }
}
