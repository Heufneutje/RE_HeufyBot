RE_HeufyBot
===========

A from scratch rewrite of my original HeufyBot project, which was based on PircBotX. By default it just runs the core, which makes the bot idle in one or more channels on a server. It will also log all events in those channels to log files. The bot depends on modules for its actual functionality.

Currently the following modules come with RE_HeufyBot:
- ModuleLoader
- Help
- Ignore

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
- Time
- Translate
- TranslationParty
- Weather
- YouTube

Detials about how to write your own module can be found here: https://github.com/Heufneutje/RE_HeufyBot/blob/master/src/heufybot/modules/Module.java