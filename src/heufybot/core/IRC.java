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
	private Socket socket;
	private BufferedReader inputReader;
	private OutputStreamWriter outputWriter;
	
	public void connect(String server, int port)
	{
		Logger.log(String.format("*** Trying to connect to %s on port %d", server , port));
		
		try
		{
			this.socket = new Socket(server, port);
			this.inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.defaultCharset()));
	        this.outputWriter = new OutputStreamWriter(socket.getOutputStream(), Charset.defaultCharset());
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void startProcessing()
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
	
	public void sendRaw(String line)
	{
		try
		{
			outputWriter.write(line + "\r\n");
			outputWriter.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
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
}