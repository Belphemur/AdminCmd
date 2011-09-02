Admin Command 
=============
Version : ${project.version} (BUILD${timestamp})
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

Files and Folder :
==================
Folders : 
---------
Folder /home/ -> Contain all information about the players, there home, there last location before and if it's there first connection. Each file have the name of the player.
Folder /locale/ -> Contain the locales files.
Folder /scripts/ -> Contain a single file scripts.yml where you can configure your own bash/batch script to be executed by the server (only for experienced admins)
Folder /spawn/ -> Contain a single file spawnLocations.yml containing all the spawn location that your setted in-game with the command /setspawn.
Folder /warp/ -> Pretty same as Folder /spawn/ but for warpoint.
Folder /HelpFiles/ -> Contain directory that contain the file to explain each command of the plugin. (Look on /HelpFiles/AdminCmd/ to see what I mean)

Files :
-------
Alias.yml -> Contain every Item alias you create inGame.
banned.yml -> Contain the name of the banned player and the reason of the ban
blacklist.yml -> Contain blacklisted items
config.yml -> Config file
items.csv -> Contain the default alias of Essentials
kits.yml -> Contain the kits.
muted.yml -> Like banned, but for muted players.
commands.yml -> Contain the command you want to disable, prioritize and your alias for each commands.