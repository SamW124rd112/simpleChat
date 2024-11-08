// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  String loginID;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = loginID;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
    	if(message.startsWith("#")) {
    		handleCommand(message);
    	}
    	
    	else if(isConnected()) {
    		sendToServer(message);
    	}
    }
    catch(IOException e)
    {
      clientUI.display("Could not send message to server.Terminating client.");
      quit();
    }
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
	  else if (command.equals("#logoff")) {
		  try {
			closeConnection();
		} catch (IOException e) {
			clientUI.display("ERROR: Failed to disconnect from server");
		}
	  }

	  else if (command.equals("#login")) {
		  if(!isConnected()) {
			  try {
				  openConnection();
			  } catch (IOException e) {
				  clientUI.display("ERROR: Failed to connect to server");
			  }
		  }else {
			  clientUI.display("ERROR - Already connected to server");
		  }

	  }
	  else if (command.equals("#gethost")) {
		  clientUI.display("Host: " + getHost());
	  }
	  else if (command.equals("#getport")) {
		  clientUI.display("Port: " + getPort());
	  }
	  else if (command.startsWith("#sethost")) {
		  if (!isConnected()) {
			  String[] input = command.split(" ");
			  setHost(input[1]);
		  }
		  else {
			  clientUI.display("ERROR: User must be logged off before setting host");
		  } 
	  }
	  else if (command.startsWith("#setport")) {
		  if(!isConnected()) 	{
			  String[] input = command.split(" ");
			  setPort(Integer.parseInt(input[1]));
		  }
		  else {
			  clientUI.display("ERROR: User must be logged off before setting port");
		  }
	  }

	  else if (command.startsWith("#login")) {
		  connectionEstablished();
	  }
  }
  
  /**
   * This method terminates the client.
   */	
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
	/**
	 * Hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method may be
	 * overridden by subclasses.
	 * 
	 * @param exception
	 *            the exception raised.
	 */
  	@Override
	protected void connectionException(Exception exception) {
  		clientUI.display("The server has shut down");
  		System.exit(0);
	}
  	
	/**
	 * Hook method called after the connection has been closed. The default
	 * implementation does nothing. The method may be overriden by subclasses to
	 * perform special processing such as cleaning up and terminating, or
	 * attempting to reconnect.
	 */
  	@Override
	protected void connectionClosed() {
		clientUI.display("Connection Closed");
	}
  	
	/**
	 * Hook method called after a connection has been established. The default
	 * implementation does nothing. It may be overridden by subclasses to do
	 * anything they wish.
	 */
	protected void connectionEstablished() {
		try {
			sendToServer("#login " + loginID);
		} catch (IOException e) {
		      clientUI.display("Could not send message to server.  Terminating client.");
		      quit();
		}
	}
}
//End of ChatClient class
