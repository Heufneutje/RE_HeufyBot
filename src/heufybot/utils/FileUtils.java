package heufybot.utils;

import heufybot.core.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public class FileUtils 
{
	public static String readFile(String filePath) 
	{
		try 
		{
			FileInputStream fstream = new FileInputStream(filePath);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String result = "";
			String read = "";
			while ((read = br.readLine()) != null) 
			{
				result += read += "\n";
			}
			in.close();
			return result;
		} 
		catch (FileNotFoundException e) 
		{
			Logger.error("FileUtils", "The file " + filePath + " does not exist.");
			return null;
		}
		catch (IOException e) 
		{
			Logger.error("FileUtils", "The file " + filePath + " could not be read.");
			return null;
		}
	}
	
	public static boolean writeFile(String filePath, String text) 
	{
		File file = new File(filePath);
		if (file.exists()) 
		{
			file.delete();
		}
		try 
		{
			FileOutputStream fos = new FileOutputStream(filePath, false);
			OutputStreamWriter writer = new OutputStreamWriter(fos, Charset.forName("UTF-8"));
			BufferedWriter bw = new BufferedWriter(writer);
			bw.write(text);
			bw.flush();
			bw.close();
			return true;
		} 
		catch (IOException e) 
		{
			Logger.error("FileUtils", "The file " + filePath + " could not be written.");
			return false;
		}
	}
	
	public static boolean writeFileAppend(String filePath, String text)
	{
		try 
		{
			FileOutputStream fos = new FileOutputStream(filePath, true);
			OutputStreamWriter writer = new OutputStreamWriter(fos, Charset.forName("UTF-8"));
			BufferedWriter bw = new BufferedWriter(writer);
			bw.write(text);
			bw.flush();
			bw.close();
			return true;
		} 
		catch (IOException e) 
		{
			Logger.error("FileUtils", "The file " + filePath + " could not be written.");
			return false;
		}
	}
	
	public static boolean touchFile(String filePath)
	{
		File file = new File(filePath);
		try 
		{
			return file.createNewFile();
		}
		catch (IOException e)
		{
			Logger.error("FileUtils", "The file " + filePath + " could not be written.");
			return false;
		}
	}
	
	public static void touchDir(String filePath)
	{
		File file = new File(filePath);
		if(!file.exists())
		{
			file.mkdirs();
		}
	}
	
	public static boolean deleteFile(String filePath)
	{
		File file = new File(filePath);
		return file.delete();
	}
	
	public static boolean fileExists(String filePath)
	{
		File file = new File(filePath);
		return file.exists();
	}
}
