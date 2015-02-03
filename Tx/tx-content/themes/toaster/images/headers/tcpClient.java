/*************************************************************
 * tcpClient.java
 *
 *  *       Version 1.1
 *
 * Set up a Server that will receive
 *    - a connection from a client
 *      - requests for services
 *        - Add
 *        - Delete
 *        - Find
 *        - Update
 *        - List
 *    - respond to requests by sending a string(s) back to the client
 *    - and close the connection when the Client is finished.
 *
 *
 * Copyright (c) 2002-2006 Advanced Applications Total Applications Works.
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
 * default port is 5050
 *************************************************************/

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import java.util.*;
import java.net.*;

/** ***************************************************************
  * The tcpClient class creates an object for the Client frame.
  * Set up a client that will receive
  *    - a connection from a server
  *      - sends requests for services to the Server
  *        - Send
  *
  *    - process server responses to string(s) sent back to the client
  *    - and close the connection when the Client is finished.
  *
 ******************************************************************/
public class tcpClient extends JFrame implements  ActionListener {
   private int port = 5050;
   private String server = "localhost";
   private Socket socket = null;
   private BufferedReader input;
   private PrintWriter output;
   private int ERROR = 1;
   private Container c ;
   private JTextArea display ;
   private JButton clear , find, list, add, update, delete, exit;
   private JPanel buttonPanel, textPanel ;
   private JTextField enterBox;
   private JLabel enterLabel;
   private StringTokenizer tokens ;
   private String messageTokens[]  = new String[ 16 ] ;


   /** ***************************************************************
    *  The tcpClient constructor initializes the tcpClient object.
    ******************************************************************/
   public tcpClient() {
      super( "Client" ) ;

      setUp() ;

      connect() ;

      RunClient() ;

      closeConnection() ;
   }

   /** ***************************************************************
    * The setUp() method does the intialization for the application.
    * The setUp() method
    * 1- Creates JButtons
    * 2- Creates JPanels
    * 3- Creates JLabels
    * 4- Adds Action Listeners to the JButtons
    * 5- Sets the size for the JFrame
    * 6- Sets the location for the JFrame
    * 7- Makes the JFrame visible
    ******************************************************************/
   public  void setUp() {

      c = getContentPane();

      /** Create JButtons */
      find   = new JButton( "Find" );
      list   = new JButton( "List" );
      add    = new JButton( "Add" );
      update = new JButton( "Update" );
      delete = new JButton( "Delete" );
      clear  = new JButton( "Clear Message" );
      exit   = new JButton( "Exit" );

      /** ******************************************************
       * Set up the Background and Foreground Colors
       *  for JButtons
       ******************************************************* */
      list.setBackground( Color.blue ) ;
      list.setForeground( Color.white ) ;
      find.setBackground( Color.blue ) ;
      find.setForeground( Color.white ) ;
      add.setBackground( Color.blue ) ;
      add.setForeground( Color.white ) ;
      update.setBackground( Color.blue ) ;
      update.setForeground( Color.white ) ;
      delete.setBackground( Color.blue ) ;
      delete.setForeground( Color.white ) ;
      exit.setBackground( Color.red ) ;
      exit.setForeground( Color.white ) ;
      clear.setBackground( Color.white ) ;
      clear.setForeground( Color.blue ) ;
      buttonPanel = new JPanel() ;

      /** Add the JButtons to the buttonPanel */
      buttonPanel.add( list ) ;
      buttonPanel.add( find ) ;
      //buttonPanel.add( add ) ;
      //buttonPanel.add( update ) ;
      buttonPanel.add( delete ) ;
      buttonPanel.add( clear ) ;
      buttonPanel.add( exit ) ;
      c.add( buttonPanel , BorderLayout.SOUTH) ;

      /** Create JLabels */
      enterLabel = new JLabel("Enter a last name below and then press a button." ) ;
      enterLabel.setFont(new Font( "Serif", Font.BOLD, 14) );
      enterLabel.setForeground( Color.black );
      enterBox = new JTextField( 100 );
      enterBox.setEditable( true );

      /** Create JPanel */
      textPanel = new JPanel() ;
      textPanel.setLayout( new GridLayout( 2, 1 ) );
      textPanel.add( enterLabel ) ;
      textPanel.add( enterBox ) ;
      c.add( textPanel , BorderLayout.NORTH) ;


      /** *************************************************************
       * Add an Action Listener for the send, exit,
       *   and clear JButtons
       ************************************************************** */
      find.addActionListener( this );
      list.addActionListener( this );
      add.addActionListener( this );
      update.addActionListener( this );
      delete.addActionListener( this );
      exit.addActionListener( this );
      clear.addActionListener( this );

      /** Create JTextArea */
      display = new JTextArea();
      display.setEditable( false );

      /** Create JScrollPane for the main area of the JFrame  */
      c.add( new JScrollPane( display ),
             BorderLayout.CENTER );

      addWindowListener( new WindowHandler( this ) );
      setSize( 500, 400 );
      setLocation( 450, 20 ) ;
      show();

   }

   /** *****************************************************************
    *  The connect() method does the intialization of the client socket
    *  on localhost and port 5050
    ***************************************************************** */
   public void connect() {
      // connect to server
      try {
         socket = new Socket(server, port);
         display.setText("Connected with server " +
              socket.getInetAddress() +
              ": Port " + socket.getPort());
      }
      catch (UnknownHostException e) {
         display.setText("" + e);
         System.exit(ERROR);
      }
      catch (IOException e) {
         display.setText("\n" + e);
         System.exit(ERROR);
      }
   }

   /** *****************************************************************
    * The sendData() method in the client sends data to the server
    ***************************************************************** */
   public void sendData(String str) {
      output.println( str );
   }


   /** *****************************************************************
    * The RunClient() method in the client reads and writes data to the server.
    ***************************************************************** */
   public void RunClient() {
      try {
         input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         output = new PrintWriter(socket.getOutputStream(),true);
      enterBox.requestFocus();

         while(true) {

            display.append( "\nThe names in the addresss book are: ") ;
            sendData( "LISTALL NAMES"  );

            String message = input.readLine();
            // stop if input line equals "QUIT"
            while ( !message.toUpperCase().equals( "FROM SERVER==> QUIT" ) ) {

               tokens = new StringTokenizer( message, ";" ) ;
               if ( tokens.countTokens() >= 1 )   {
                  int ii = 0 ;
                  while( tokens.hasMoreTokens() )  {
                     messageTokens[ ii ] = tokens.nextToken().toString() ;

                     ii++ ;
                  }

                  if ( messageTokens[ 0 ].toUpperCase().equals( "RECORDFOUND" ) ) {
                     showName() ;
                  }
                  else if ( messageTokens[ 0 ].toUpperCase().equals( "LISTRECORD" ) ) {
                     listNames() ;
                  }
                  else if ( messageTokens[ 0 ].toUpperCase().equals( "RECORDDELETED" ) ) {
                     display.append( "\n" +  message ) ;
                  }
                  else if ( messageTokens[ 0 ].toUpperCase().equals( "NOTFOUND" ) ) {
                     display.append( "\n" +  message ) ;
                  }
               }
               message = input.readLine();
            }
         }
      }
      catch (IOException e) {
         display.append("\n" + e);
      }

   }

   /** *****************************************************************
    * This method responds to the find, delete, clear or exit button
    *  being pressed on the tcpClient frame.
    ***************************************************************** */
   public void actionPerformed( ActionEvent e )    {

      if ( e.getSource() == exit ) {
            closeConnection() ;
      }
      // list all names in the array
      else if ( e.getSource() == list ) {
            sendData( "LISTALL NAMES"  );
            display.append( "\n LISTALL"  ) ;
      }
      else  if ( !enterBox.getText().equals( "" ) ) {
         if ( e.getSource() == clear ) {
            enterBox.setText( "" );
         }
         // get user input and send it to server
         else if ( e.getSource() == find ) {
            sendData( "FIND " + enterBox.getText() );
            display.append( "\n" +  enterBox.getText() ) ;
         }
         else if ( e.getSource() == add ) {
            sendData( "ADD " + enterBox.getText() );
            display.append( "\n" +  enterBox.getText() ) ;
         }
         else if ( e.getSource() == update ) {
            sendData( "UPDATE " + enterBox.getText() );
            display.append( "\n" +  enterBox.getText() ) ;
         }
         else if ( e.getSource() == delete ) {
            sendData( "DELETE " + enterBox.getText() );
            display.append( "\n" +  enterBox.getText() ) ;
         }
      }
      else {
          JOptionPane.showMessageDialog(null,
                 "Please enter a last name" + "\n",
                 "No last name",
                 JOptionPane.INFORMATION_MESSAGE ) ;
      }
   }

   /*******************************************************************
    * This method shows the name found by the server.
    ***************************************************************** */
   public  void showName() {
      display.append( "\n Name:         " + messageTokens[ 1 ] + " " + messageTokens[ 2 ] ) ;
      display.append( "\n Address:     " + messageTokens[ 3 ]  ) ;
      display.append( "\n Telephone: " + messageTokens[ 4 ]  ) ;
   }

   /** *****************************************************************
    * This method lists all of the name found by the server.
    ***************************************************************** */
   public  void listNames() {
      if ( !messageTokens[ 1 ].equals( "" ) && !messageTokens[ 1 ].equals( " " )) {
         display.append( "\n Last Name: " + messageTokens[ 1 ]  ) ;
      }
   }

   /** *****************************************************************
    * This method closes the socket connect to the server.
    ***************************************************************** */
   public  void closeConnection() {
      sendData( "QUIT" ) ;
      try {
         socket.close();
         input.close();
         output.close();
      }
      catch (IOException e) {
         display.append("\n" + e);
      }

      setVisible( false );
      System.exit( 0 );
   }

   /** *****************************************************************
    * This method is the main entry point called by the JVM.
    ****************************************************************** */
   public static void main(String[] args) {
      final tcpClient client = new tcpClient() ;

      client.addWindowListener(
         new WindowAdapter() {
            public void windowClosing( WindowEvent e )
            {
               client.closeConnection() ;
            }
         }
      );
   }

   /** *****************************************************************
    * This method closes the socket connect to the server when the
    * application window is closed.
    ***************************************************************** */
   public class WindowHandler extends WindowAdapter {
      tcpClient tcpC;

      public WindowHandler( tcpClient t ) { tcpC = t; }

      public void windowClosing( WindowEvent e ) { tcpC.closeConnection(); }
   }

}