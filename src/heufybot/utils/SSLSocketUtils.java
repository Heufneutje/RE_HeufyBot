package heufybot.utils;

import heufybot.core.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SSLSocketUtils extends SSLSocketFactory 
{
	protected SSLSocketFactory wrappedFactory;
	protected boolean trustingAllCertificates = false;
	protected boolean diffieHellmanDisabled = false;

	public SSLSocketUtils() 
	{
		wrappedFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
	}

	public SSLSocketUtils(SSLSocketFactory providedFactory) 
	{
		wrappedFactory = providedFactory;
	}

	public SSLSocketUtils trustAllCertificates() 
	{
		if (trustingAllCertificates) 
		//Already doing this, no need to do it again
		return this;
		trustingAllCertificates = true;
		try 
		{
			TrustManager[] tm = new TrustManager[]{new TrustingX509TrustManager()};
			SSLContext context = SSLContext.getInstance("SSL");
			context.init(new KeyManager[0], tm, new SecureRandom());
			wrappedFactory = (SSLSocketFactory)context.getSocketFactory();
		}
		catch (Exception e)
		{
			Logger.error("SSL Utilities", "Could not trust all certificates.");
			return null;
		}
		return this;
	}

	public SSLSocketUtils disableDiffieHellman() 
	{
		diffieHellmanDisabled = true;
		return this;
	}
	
	protected Socket prepare(Socket socket)
	{
		SSLSocket sslSocket = (SSLSocket)socket;
		if (diffieHellmanDisabled)
		{
			List<String> limited = new LinkedList<String>();
			for (String suite : sslSocket.getEnabledCipherSuites())
			{
				if (!suite.contains("_DHE_"))
				{
					limited.add(suite);
				}
			}
			sslSocket.setEnabledCipherSuites(limited.toArray(new String[limited.size()]));
		}
		return sslSocket;
	}
	
	@Override
	public Socket createSocket(String host, int port) throws IOException, UnknownHostException 
	{
		return prepare(wrappedFactory.createSocket(host, port));
	}
	
	@Override
	public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException
	{
		return prepare(wrappedFactory.createSocket(host, port, localHost, localPort));
	}
	
	@Override
	public Socket createSocket(InetAddress address, int port) throws IOException 
	{
		return prepare(wrappedFactory.createSocket(address, port));
	}
	
	@Override
	public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException 
	{
		return prepare(wrappedFactory.createSocket(address, port, localAddress, localPort));
	}
	
	@Override
	public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException
	{
		return prepare(wrappedFactory.createSocket(s, host, port, autoClose));
	}

	public static class TrustingX509TrustManager implements X509TrustManager 
	{
		public void checkClientTrusted(X509Certificate[] cert, String authType) throws CertificateException 
		{
		}

		public void checkServerTrusted(X509Certificate[] cert, String authType) throws CertificateException 
		{
		}

		public X509Certificate[] getAcceptedIssuers() 
		{
			return null;
		}
	}
	
	protected interface SSLSocketFactoryDelegateExclude 
	{	
		Socket createSocket(String host, int port) throws IOException, UnknownHostException;
		
		Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException;
		
		Socket createSocket(InetAddress address, int port) throws IOException;
		
		Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException;
		
		Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException;
	}
	
	public boolean isTrustingAllCertificates()
	{
		return this.trustingAllCertificates;
	}
	
	public boolean isDiffieHellmanDisabled()
	{
		return this.diffieHellmanDisabled;
	}
	
	public java.lang.String[] getDefaultCipherSuites() 
	{
		return this.wrappedFactory.getDefaultCipherSuites();
	}
	
	public java.lang.String[] getSupportedCipherSuites()
	{
		return this.wrappedFactory.getSupportedCipherSuites();
	}
	
	public java.net.Socket createSocket() throws java.io.IOException
	{
		return this.wrappedFactory.createSocket();
	}
}