package heufybot.modules;

import heufybot.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class DailySquee extends Module
{
	public DailySquee()
	{
		this.authType = Module.AuthType.Anyone;
		this.trigger = "^" + commandPrefix + "(squee)($| .*)";
	}
	
	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params) 
	{
		try
		{
			List<String> titles = new ArrayList<String>();
			List<String> urls = new ArrayList<String>();
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse("http://feeds.feedburner.com/DailySquee?format=xml");
			doc.getDocumentElement().normalize();
	
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile("//rss/channel/item/title/text()");
			
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++) 
			{
			    titles.add(nodes.item(i).getNodeValue());
			    System.out.println(nodes.item(i).getNodeValue());
			}
			
			expr = xpath.compile("//rss/channel/item/link/text()");
			result = expr.evaluate(doc, XPathConstants.NODESET);
			nodes = (NodeList) result;

			for (int i = 0; i < nodes.getLength(); i++) 
			{
			    urls.add(nodes.item(i).getNodeValue());
			    System.out.println(nodes.item(i).getNodeValue());
			}
			
			int id = -1;
			if(params.size() > 1)
			{
				StringUtils.tryParseInt(params.get(1));
			}
			
			if(id == -1 || id > titles.size() - 1)
			{
				id = (int) (Math.random() * titles.size());
			}
			
			bot.getIRC().cmdPRIVMSG(source, titles.get(id) + " | " + urls.get(id));
		}
		catch(Exception e)
		{
			bot.getIRC().cmdPRIVMSG(source, "Something went wrong while trying to read the DailySquee RSS feed.");
		}
	}

	public String getHelp(String message)
	{
		return "Commands: " + commandPrefix + "squee (<id>) | Returns random cuteness from the DailySquee RSS feed. An ID can be specified to get a certain one.";
	}

	@Override
	public void onLoad() 
	{
	}

	@Override
	public void onUnload()
	{
	}
}
