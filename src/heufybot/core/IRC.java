package heufybot.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

public class IRC 
{
	private static final IRC instance = new IRC();
	
	private Socket socket;
	private BufferedReader inputReader;
	private OutputStreamWriter outputWriter;
	private Thread inputThread;
	
	private IRC()
	{
		this.socket = new Socket();
	}
	
	public static IRC getInstance()
	{
		return instance;
	}
	
	public boolean connect(String server, int port)
	{
		if(socket.isConnected())
		{
			Logger.error("IRC Connect", "Already connected to a server. Connection failed.");
			return false;
		}
		Logger.log(String.format("*** Trying to connect to %s on port %d", server , port));
		
		try
		{
			this.socket = new Socket(server, port);
			this.inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.defaultCharset()));
	        this.outputWriter = new OutputStreamWriter(socket.getOutputStream(), Charset.defaultCharset());
	        
	        Logger.log("*** Connected to the server.");
	        
	        return true;
		}
		catch (UnknownHostException e)
		{
			Logger.error("IRC Connect", "Host could not be resolved. Connection failed.");
			return false;
		}
		catch (IOException e)
		{
			Logger.error("IRC Connect", "Unkown connection error. Connection failed.");
			return false;
		}
	}
	
	public void disconnect()
	{
		try 
		{
			this.inputReader.close();
			this.outputWriter.flush();
			this.outputWriter.close();
			this.socket.close();
		} 
		catch (IOException e) 
		{
			Logger.error("IRC Disconnect", "Error closing connection");
		}
	}
	
	public void startProcessing()
	{
		inputThread = new Thread(new Runnable()
		{
			public void run()
			{
				while(true)
				{
					 String line;
		             try
		             {
		            	 line = inputReader.readLine();
		             }
		             catch (InterruptedIOException iioe) 
		             {
		            	 cmdPING("" + System.currentTimeMillis() / 1000);
		            	 continue;
		             }
		             catch (Exception e)
		             {
		            	 e.printStackTrace();
		            	 line = null;
		             }
	
		             if (line == null)
		                     break;
	
		             Logger.log(line);
		             
		             if (Thread.interrupted())
		                     return;
	
				}
			}
		});
		inputThread.start();
	}
	
	public void sendRaw(String line)
	{
		try
		{
			outputWriter.write(line + "\r\n");
			outputWriter.flush();
		}
		catch (IOException e)
		{
			Logger.error("IRC Output", "Error sending line");
		}
	}
	
	public void cmdNICK(String nick)
	{
		sendRaw("NICK " + nick);
	}
	
	public void cmdUSER(String user, String realname)
	{
		sendRaw("USER " + user + " 8 * :" + realname);
	}
	
	public void cmdPING(String ping)
	{
		sendRaw("PING"  + ping);
	}
	
	public void cmdQUIT(String message)
	{
		sendRaw("QUIT: " + message);
	}
	
	public void cmdPRIVMSG(String target, String message)
	{
		sendRaw("PRIVMSG " + target + ": " + message);
	}
	
	public void cmdJOIN(String channel, String key)
	{
		sendRaw("JOIN " + channel + " " + key);
	}
	
	public void cmdPART(String channel, String message)
	{
		sendRaw("PART " + channel + ": " + message);
	}
}