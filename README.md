RE_HeufyBot
===========

A modular Java IRC bot and a from scratch rewrite of my original [HeufyBot](https://github.com/Heufneutje/HeufyBot) project, which was based on PircBotX. By default it just runs the core, which makes the bot idle in one or more channels on a server. It will also log all events in those channels to log files. The bot depends on modules for its actual functionality.

Modules
=======

The following recommended modules come with the bot:

- ModuleLoader
  - Implements the `load`, `unload` and `reload` commands that are needed to interact with the module interface on runtime.

- Help
  - Implements the `help` command, which will allow you to get a list of all loaded modules and to call the help method for a module if that module's name is passed as parameter.

- Ignore
  - Implements the `ignore` and `unignore` commands, which allow you to add certain users to the bot's ignore list. Any commands from any user on this list will be ignored by the bot.

The following modules are also included, but are fully optional:

- Choose
- Do
- Join
- Log
- LogDB (DesertBus)
- Nick
- OutOfContext
- Part
- Quit
- RandomCuteness
- Say
- Shorten
- Source
- Tell
- Time
- TimeDB (DesertBus)
- Translate
- TranslationParty
- URLFollow
- Uptime
- UserLocation
- Weather
- WeatherDB (DesertBus)
- YouTube

Running
=======

To use the bot, copy the "settings.yml.example" file into "settings.yml" and edit it. Then open a terminal and run `java -jar RE_HeufyBot.jar`
