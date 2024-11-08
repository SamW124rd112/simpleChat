package edu.seg2105.client.ui;

import java.io.IOException;
import java.util.Scanner;

import edu.seg2105.client.common.ChatIF;
import edu.seg2105.edu.server.backend.EchoServer;

public class ServerConsole implements ChatIF{
	final public static int DEFAULT_PORT = 5555;

	 EchoServer server;
	 Scanner fromConsole;
	
	 public ServerConsole(int port) {
		  try {
			  server = new EchoServer(port, this);
			  server.listen();
		  } 
		  
		  catch(IOException exception) 
		  {
			  System.out.println("ERROR: Could not listen for clients! - Terminating connection");
			  System.exit(1);
		  }
	    
		  // Create scanner object to read from console
		  fromConsole = new Scanner(System.in); 
	  }
	 
	  public void accept() 
	  {
	    try
	    {
	      String message;

	      while (true) 
	      {
	        message = fromConsole.nextLine();
	        server.handleMessageFromServerUI(message);
	      }
	    } 
	    catch (Exception ex) 
	    {
	      System.out.println
	        ("Unexpected error while reading from console!");
	    }
	  }
	 
	 
	  public void display(String message) 
	  {
	    System.out.println(message);
	  }
	  
	  public static void main(String[] args) {
		  int port = 0;
		  
		  try {
			  port = Integer.parseInt(args[0]);
		  } catch(Throwable t) {
			  port = DEFAULT_PORT;
		  }
		  
		  ServerConsole chat = new ServerConsole(port);
		  chat.accept();	
	  }
	  

}
