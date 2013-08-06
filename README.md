#version: ${git.commit.id}
Admin Command 
=============
Version : ${project.version} (BUILD ${git.build.time})
Thread : http://forums.bukkit.org/threads/admincmd.10770/
Ticket (for bug/feature request) : http://dev.bukkit.org/server-mods/admincmd/tickets/
Wiki : http://dev.bukkit.org/server-mods/admincmd/

Features :
==========
    Time Commands
    Items Commands
    Player Commands
    Teleport Commands
    Weather Commands
    Warp Commands
    Mob Commands
    Spawn Commands
    Home Commands (in Teleport Commands)
    Permissions support (see below)
    Multi-World Support
    Auto-Afk with Auto-Kick (can be configured in confg File)
    Support Permissions Plugins (Official Bukkit, TheYeti version, PermissionsEX)
    Support for OddItem
    Support for mChat
    Configurable with a config file
    Always overridden by other plugin (Example : you have multihome. My /home command will be disabled automatically)
    Colored Sign : see below with Color.
    Multi-lingual : English and German
    Prioritize system for the command, meaning you can choose if some of my command will override other plugin commands.
    Disable command : you can choose witch command you want to disable.

Immunity
========
You can now set immunityLvl (like maxHomePerUser (explained http://dev.bukkit.org/server-mods/admincmd/pages/features/ )), 
an immunityLvl is a level representing the power of the user.

A user with a power of 0 can't do command against a user of a power of 1 or above.
And the user of 150 (the max) can do everything to the lower levels.

Only one exception : admincmd.immunityLvl.samelvl if a user have this node, he can only issue 

Files and Folder :
==================
Folders : 
---------
Folder /userData/ -> Contain all informations about the players, there home, there last location before and if it's there first connection. Each file have the name of the player.
Folder /locale/ -> Contain the locales files.
Folder /scripts/ -> Contain a single file scripts.yml where you can configure your own bash/batch script to be executed by the server (only for experienced admins)
Folder /worldData/ -> Contain all the informations about the worlds, spawn point, warp, time frozen and weather frozen.
Folder /HelpFiles/ -> Contain directory that contain the file to explain each command of the plugin. (Look on /HelpFiles/AdminCmd/ to see what I mean)

Files :
-------
Alias.yml -> Contain every Item alias you create inGame.
blacklist.yml -> Contain blacklisted items
config.yml -> Config file
items.csv -> Contain the default alias of Essentials
kits.yml -> Contain the kits.
commands.yml -> Contain the command you want to disable, prioritize and your alias for each commands.
motd.txt -> file to set the Message of the Day
rules.txt -> file to set the rules
news.txt -> file to set the news.
