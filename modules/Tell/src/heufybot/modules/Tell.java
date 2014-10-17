package heufybot.modules;

import heufybot.core.Logger;
import heufybot.utils.FileUtils;
import heufybot.utils.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Tell extends Module
{
    private final int MESSAGE_COOLDOWN = 20;
    private LinkedHashMap<String, ArrayList<Message>> tellsMap;
    private HashMap<String, Date> tellers;
    private String databasePath;

    public Tell(String server)
    {
        super(server);

        this.authType = AuthType.Anyone;
        this.apiVersion = 60;
        this.triggerTypes = new TriggerType[] { TriggerType.Message, TriggerType.Action };
        this.trigger = "^" + this.commandPrefix + "(tell|r(emove)?tell|s(ent)?tells)($| .*)";
        this.triggerOnEveryMessage = true;

        this.tellsMap = new LinkedHashMap<String, ArrayList<Message>>();
        this.tellers = new HashMap<String, Date>();
        this.databasePath = "data/" + server + "/tells.json";
    }

    @Override
    public String getHelp(String message)
    {
        return "Commands: "
                + this.commandPrefix
                + "tell <user> <message>, "
                + this.commandPrefix
                + "rtell <message>, "
                + this.commandPrefix
                + "senttells | Tells the specified user a message the next time they speak, " +
                "removes a message sent by you from the database or lists your pending messages.";
    }

    @Override
    public void processEvent(String source, String message, String triggerUser, List<String> params)
    {
        if (message.toLowerCase().matches("^" + this.commandPrefix + "tell.*"))
        {
            if (params.size() == 1)
            {
                this.bot.getServer(this.server).cmdPRIVMSG(source, "Tell what?");
                return;
            }
            params.remove(0);
            if (params.size() == 1)
            {
                this.bot.getServer(this.server).cmdPRIVMSG(source,
                        "What do you want me to tell them?");
                return;
            }

            Date timestamp = this.tellers.get(triggerUser);
            if (timestamp != null)
            {
                long timeStampDifference = (new Date().getTime() - timestamp.getTime()) / 1000;
                System.out.println(timeStampDifference);
                if (timeStampDifference < this.MESSAGE_COOLDOWN)
                {
                    this.bot.getServer(this.server).cmdPRIVMSG(
                            source,
                            "Calm down, " + triggerUser + "! You just sent a message "
                                    + timeStampDifference + " seconds ago! You have to wait "
                                    + (this.MESSAGE_COOLDOWN - timeStampDifference)
                                    + " more seconds.");
                    return;
                }
            }

            String[] recepients;
            if (params.get(0).contains("&"))
            {
                recepients = params.get(0).split("&");
            }
            else
            {
                recepients = new String[] { params.get(0) };
            }
            params.remove(0);
            for (String recepient2 : recepients)
            {
                String recepient = this.fixRegex(recepient2.replaceAll(":", ""));
                if (triggerUser.toLowerCase().matches(recepient.toLowerCase()))
                {
                    this.bot.getServer(this.server).cmdPRIVMSG(source,
                            "Why are you telling yourself that?");
                    return;
                }
                else if (recepient2.equalsIgnoreCase(this.bot.getServer(this.server).getNickname()))
                {
                    this.bot.getServer(this.server).cmdPRIVMSG(source,
                            "Thanks for telling me that, " + triggerUser + ".");
                    return;
                }

                String messageToSend = StringUtils.join(params, " ");
                Date date = new Date();
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss (z)");
                String dateString = format.format(date);
                String messageSource = "";
                if (source.equals(triggerUser))
                {
                    messageSource = "PM";
                }
                else
                {
                    messageSource = "Channel";
                }

                Message tellMessage = new Message(triggerUser, messageToSend, dateString,
                        messageSource);

                if (!this.tellsMap.containsKey(recepient))
                {
                    this.tellsMap.put(recepient, new ArrayList<Message>());
                }

                this.tellsMap.get(recepient).add(tellMessage);
                this.tellers.put(triggerUser, new Date());

                this.writeMessages();
                this.bot.getServer(this.server).cmdPRIVMSG(source,
                        "Okay, I'll tell " + recepient2 + " that next time they speak.");
            }
        }
        else if (message.toLowerCase().matches("^" + this.commandPrefix + "r(emove)?tell.*"))
        {
            if (params.size() == 1)
            {
                this.bot.getServer(this.server).cmdPRIVMSG(source, "Remove what?");
                return;
            }

            params.remove(0);
            String searchString = ".*" + StringUtils.join(params, " ") + ".*";
            boolean matchFound = false;

            for (Iterator<String> iter = this.tellsMap.keySet().iterator(); iter.hasNext()
                    && !matchFound; )
            {
                String user = iter.next();
                ArrayList<Message> sentMessages = this.tellsMap.get(user);
                for (Iterator<Message> iter2 = sentMessages.iterator(); iter2.hasNext()
                        && !matchFound; )
                {
                    Message sentMessage = iter2.next();
                    if (sentMessage.getFrom().equalsIgnoreCase(triggerUser)
                            && sentMessage.getText().toLowerCase()
                            .matches(searchString.toLowerCase()))
                    {
                        String messageString = "Message \"" + sentMessage.getText() + "\" sent to "
                                + user + " on " + sentMessage.getDateSent()
                                + " was removed from the message database!";
                        if (sentMessage.getMessageSource().equals("PM"))
                        {
                            this.bot.getServer(this.server).cmdNOTICE(triggerUser, messageString);
                        }
                        else
                        {
                            this.bot.getServer(this.server).cmdPRIVMSG(source, messageString);
                        }
                        iter2.remove();
                        this.writeMessages();
                        matchFound = true;
                    }
                }
                if (sentMessages.size() == 0)
                {
                    this.tellsMap.remove(user);
                }
            }
            if (!matchFound)
            {
                this.bot.getServer(this.server).cmdPRIVMSG(source,
                        "No message matching \"" + searchString + "\" was found.");
            }
        }
        else if (message.toLowerCase().matches("^" + this.commandPrefix + "s(ent)?tells.*"))
        {
            if (params.size() > 1)
            {
                return;
            }

            boolean foundMessage = false;
            for (String user : this.tellsMap.keySet())
            {
                ArrayList<Message> sentMessages = this.tellsMap.get(user);
                for (Message sentMessage : sentMessages)
                {
                    if (sentMessage.getFrom().equalsIgnoreCase(triggerUser))
                    {
                        foundMessage = true;
                        this.bot.getServer(this.server).cmdNOTICE(
                                triggerUser,
                                sentMessage.getText() + " < Sent to " + user + " on "
                                        + sentMessage.getDateSent());
                    }
                }
            }

            if (!foundMessage)
            {
                this.bot.getServer(this.server).cmdNOTICE(triggerUser,
                        "There are no messages sent by you that have not been received yet.");
            }
        }

        // Automatic stuff
        int messageCount = 0;
        for (Iterator<String> iter = this.tellsMap.keySet().iterator(); iter.hasNext()
                && messageCount < 3; )
        {
            String user = iter.next();
            if (triggerUser.toLowerCase().matches(user.toLowerCase()))
            {
                ArrayList<Message> sentMessages = this.tellsMap.get(user);
                for (Iterator<Message> iter2 = sentMessages.iterator(); iter2.hasNext()
                        && messageCount < 3; messageCount++)
                {
                    Message sentMessage = iter2.next();
                    String messageString = triggerUser + ": " + sentMessage.getText() + " < From "
                            + sentMessage.getFrom() + " on " + sentMessage.getDateSent();
                    if (sentMessage.getMessageSource().equals("PM"))
                    {
                        this.bot.getServer(this.server).cmdPRIVMSG(triggerUser, messageString);
                        iter2.remove();
                    }
                    else if (source.startsWith("#"))
                    {
                        this.bot.getServer(this.server).cmdPRIVMSG(source, messageString);
                        iter2.remove();
                    }
                }
                if (sentMessages.size() == 0)
                {
                    iter.remove();
                }
            }
        }
        this.writeMessages();
    }

    @Override
    public void onLoad()
    {
        if (FileUtils.touchFile(this.databasePath))
        {
            FileUtils.writeFile(this.databasePath, "[]");
        }
        this.readMessages();
    }

    @Override
    public void onUnload()
    {
        this.writeMessages();
    }

    public void readMessages()
    {
        try
        {
            JSONArray recepients = (JSONArray) new JSONParser().parse(FileUtils
                    .readFile(this.databasePath));
            for (int i = 0; i < recepients.size(); i++)
            {
                JSONObject recepient = (JSONObject) recepients.get(i);
                String name = recepient.get("name").toString();
                JSONArray messages = (JSONArray) recepient.get("messages");

                ArrayList<Message> messageList = new ArrayList<Message>();
                for (int j = 0; j < messages.size(); j++)
                {
                    JSONObject messageObject = (JSONObject) messages.get(j);
                    Message message = new Message(messageObject.get("from").toString(),
                            messageObject.get("text").toString(), messageObject.get("dateSent")
                            .toString(), messageObject.get("messageSource").toString());
                    messageList.add(message);
                }
                this.tellsMap.put(name, messageList);
            }
        }
        catch (ParseException e)
        {
            Logger.error("Module: Tell", "The tells database could not be read.");
        }
    }

    @SuppressWarnings("unchecked")
    public void writeMessages()
    {
        JSONArray recepients = new JSONArray();
        for (String recepient : this.tellsMap.keySet())
        {
            JSONArray messages = new JSONArray();
            JSONObject recepientObject = new JSONObject();

            for (Message message : this.tellsMap.get(recepient))
            {
                JSONObject messageObject = new JSONObject();
                messageObject.put("from", message.getFrom());
                messageObject.put("text", message.getText());
                messageObject.put("dateSent", message.getDateSent());
                messageObject.put("messageSource", message.getMessageSource());

                messages.add(messageObject);
            }

            recepientObject.put("name", recepient);
            recepientObject.put("messages", messages);
            recepients.add(recepientObject);
        }
        FileUtils.writeFile(this.databasePath, recepients.toJSONString());
    }

    private String fixRegex(String regex)
    {
        return "^"
                + StringUtils.escapeRegex(regex).replaceAll("\\*", ".*").replaceAll("\\?", ".")
                .replaceAll("\\(", "(").replaceAll("\\)", ")").replaceAll(",", "|")
                .replaceAll("/", "|") + "$";
    }
}
