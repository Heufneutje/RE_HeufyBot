package heufybot.utils;

import heufybot.core.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class URLUtils
{
    public static String grab(String urlString, HashMap<String, String> headers)
    {
        try
        {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            for (String requestHeader : headers.keySet())
            {
                connection.setRequestProperty(requestHeader, headers.get(requestHeader));
            }

            BufferedReader bufReader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            String data = "";

            while ((line = bufReader.readLine()) != null)
            {
                data += line + " ";
            }
            bufReader.close();
            return data;
        }
        catch (Exception e)
        {
            Logger.error("URL Utilities", "Couldn't grab URL \"" + urlString + "\"");
            return null;
        }
    }

    public static String grab(String urlString)
    {
        return grab(urlString, new HashMap<String, String>());
    }

    public static String getFullHostname(String urlString)
    {
        try
        {
            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setInstanceFollowRedirects(false);
            while (connection.getResponseCode() / 100 == 3)
            {
                urlString = connection.getHeaderField("location");
                connection = (HttpURLConnection) new URL(urlString).openConnection();
            }
            return urlString;
        }
        catch (Exception e)
        {
            Logger.error("URL Utilities", "Couldn't get full hostname for \"" + urlString + "\"");
            return null;
        }
    }

    public static String getHost(String urlString)
    {
        URL url;
        try
        {
            url = new URL(urlString);
            return url.getHost();
        }
        catch (MalformedURLException e)
        {
            Logger.error("URL Utilities", "Couldn't get the host for \"" + urlString + "\"");
            return null;
        }
    }

    public static String shortenURL(String urlstring)
    {
        try
        {
            String json = "";
            if (urlstring.startsWith("http:"))
            {
                json = "{\"longUrl\": \"" + urlstring + "\"}";
            }
            else
            {
                json = "{\"longUrl\": \"http://" + urlstring + "/\"}";
            }

            URL url = new URL("https://www.googleapis.com/urlshortener/v1/url");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(json);
            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String decodedString;
            String result = "";
            while ((decodedString = in.readLine()) != null)
            {
                result += decodedString;
            }
            in.close();
            return result.substring(result.indexOf("http://goo.gl"),
                    result.indexOf("\"", result.indexOf("http://goo.gl")));
        }
        catch (Exception e)
        {
            Logger.error("URL Utilities", "Couldn't shorten URL \"" + urlstring + "\"");
            return null;
        }
    }

    public static LinkedHashMap<String, String> grabRSSFeed(String url)
    {
        try
        {
            LinkedHashMap<String, String> elements = new LinkedHashMap<String, String>();

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(url);
            doc.getDocumentElement().normalize();

            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile("//rss/channel/item/title/text()");

            Object result = expr.evaluate(doc, XPathConstants.NODESET);
            NodeList nodes = (NodeList) result;

            String[] titles = new String[nodes.getLength()];

            for (int i = 0; i < nodes.getLength(); i++)
            {
                titles[i] = nodes.item(i).getNodeValue();
            }

            expr = xpath.compile("//rss/channel/item/link/text()");
            result = expr.evaluate(doc, XPathConstants.NODESET);
            nodes = (NodeList) result;

            for (int i = 0; i < nodes.getLength(); i++)
            {
                elements.put(titles[i], nodes.item(i).getNodeValue());
            }
            return elements;
        }
        catch (Exception e)
        {
            Logger.error("URL Utilities", "Couldn't grab RSS feed at " + url);
            return null;
        }
    }
}
