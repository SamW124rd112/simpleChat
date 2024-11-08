package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import ocsf.server.*;
import edu.seg2105.client.common.ChatIF;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  final public static String LOGIN_ID = "loginID";

  ChatIF serverUI;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI) 
  {
    super(port);
    this.serverUI = serverUI;
  }
    //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient(Object msg, ConnectionToClient client)
  {
    System.out.println("Message received: " + msg + " |-from " + client.getInfo(LOGIN_ID));
	String messageStr = (String) msg;
	
	if (messageStr.startsWith("#login")) {

		if(client.getInfo(LOGIN_ID) == null) {
			String loginID = messageStr.substring(7);
			client.setInfo(LOGIN_ID, loginID);
			
			String newMessage = loginID + " has logged on";
			serverUI.display(newMessage);
			sendToAllClients(newMessage);
		}
		else {
			try {
				client.sendToClient("Error - Already logged in");
			} catch (IOException e) {}
		}
	}
	else {
		Object loginID = client.getInfo(LOGIN_ID);
		this.sendToAllClients(loginID + "> " + msg);	
	}
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    serverUI.display("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    serverUI.display("Server has stopped listening for connections.");
  }
  
  /**
   * Implements hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   * @param client the connection connected to the client.
   */
  @Override
  protected void clientConnected(ConnectionToClient client) {
	  serverUI.display("A new client has connected to the server");
  }
  /**	
   * Implements hook method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  serverUI.display(client.getInfo(LOGIN_ID)+"Client has disconnected from the server");
  }
  
  
  //Class methods ***************************************************
  
  public void handleMessageFromServerUI(String message) {
	  if(message.startsWith("#")) {
		handleCommand(message);
	}
	
	else if(isListening()){

		String toDisplay = "Server MSG> " + message;
		serverUI.display(toDisplay);
		sendToAllClients(toDisplay);
	}
}
  
  public void quit()
  {
    try
    {
      close();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  /*
   * Function handles user command inputs
   * 
   * @param command input commands from user
   */
  private void handleCommand(String command) {
	  if (command.equals("#quit")) {
		  quit();
	  }
	  else if (command.equals("#stop")) {
		  stopListening();
	  }
	  else if (command.equals("#close")) {
		  try {
			close();
		} catch (IOException e) {}
	  }
	  else if (command.equals("#start")) {
		  if(!isListening()) {
			  try {
				listen();
			} catch (IOException e) {
				serverUI.display("ERROR: Couldn't listen for clients");
			}
		  } else {
			  serverUI.display("ERROR: Server must be stopped");
		  }
	  }
	  else if (command.equals("#getport")) {
		  serverUI.display("Port: " + getPort());
	  }
	  else if (command.startsWith("#setport")) {
		  
		  if(getNumberOfClients() == 0 && !isListening()) {
			  String[] input = command.split(" ");
			  setPort(Integer.parseInt(input[1]));
		  } else {
			  serverUI.display("ERROR: Server must be closed before setting a new port");
		  }
	  }
  }
}
//End of EchoServer class
