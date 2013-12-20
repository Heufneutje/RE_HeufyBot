RE_HeufyBot
===========

A from scratch rewrite of my original HeufyBot project, which was based on PircBotX. By default it just runs the core, which makes the bot idle in one or more channels on a server. It will also log all events in those channels to log files. The bot depends on modules for its actual functionality.

Currently the following modules come with RE_HeufyBot:

Recommended modules:

- ModuleLoader
- Help
- Ignore

Optional modules:

- Choose
- Do
- Join
- Log
- Nick
- OutOfContext
- Part
- Quit
- Say
- Shorten
- Source
- Tell
- Translate
- TranslationParty
- YouTube

Detials about how to write your own module can be found here: https://github.com/Heufneutje/RE_HeufyBot/blob/master/src/heufybot/modules/Module.java

To use the bot, copy the settings.yml.example file into settings.yml and edit it. Then run java -jar RE_HeufyBot.jar to run the bot.
