Admin Command 
=============
Version : ${project.version} (BUILD${timestamp})
Url : http://forums.bukkit.org/threads/admincmd.10770/

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

Configuration Explanation :
===========================
#Display for all new player the MOTD and the news
MessageOfTheDay: true

#Default value for each power
DefaultVulcanPower: 4.0
DefaultFireBallPower: 1.0
DefaultFlyPower: 1.75

#Language of messages displayed in the plugin.
locale: en_US
#Disable every message of the plugin
noMessage: false

#Activate the autoAfk, meaning the player will be set AFK after the given time
autoAfk: true
afkTimeInSecond: 60
#Activate the auto kick AFK people after the given time
autoKickAfkPlayer: false
afkKickInMinutes: 3
#After how many seconds the plugin will check if the player didn't move for the AFK
#Also used to send update about the Invisible status.
statutCheckInSec: 20

#How many home a user can have. 0 = Infinite
maxHomeByUser: 0

#Only useful when using bridge with SuperPerm, to avoid the use of the bridge.
forceOfficialBukkitPerm: false

#If true and if it's the first time that the player connect, he will be spawn at the spawn point you set
firstConnectionToSpawnPoint: false

#Fly,God,vulcan, etc ... are power. If true, these power are lost when tp to another world.
resetPowerWhenTpAnotherWorld: true

#Range check for update the invisible status.
invisibleRangeInBlock: 312
#Fake quit when you become invisible
fakeQuitWhenInvisible: true

#Activate the parachute when falling in fly mode
glideWhenFallingInFlyMode: true
#Tweak the parachute value.
glinding:
    newYvelocity: -0.5
    YvelocityCheckToGlide: -0.2
    multiplicator: 0.1
#Activate the color sign, using & to select the color.
ColoredSign: true

#Command that will have all priority under other plugins.
prioritizedCommands:
- reload
- ex
- undo
#Disabled commands
disabledCommands:
- blah
- thor

#Disallow muted player to do private message
mutedPlayerCantPm: false

#Max Range in block for the tp at see.
maxRangeForTpAtSee: 312

#Mean, when you want to tp to a player or tp the player, he'll receive a request that he can ignore or accept.
#By setting this option to true, the tp request will be activated by default.
tpRequestActivatedByDefault: true
#How much minute before a tp request become invalid.
tpRequestTimeOutInMinutes: 1

#Disable some "debug" message when launching the plugin (change it to true only if you have a good reason to do it.)
verboseLog: false

#Private message send with command /msg are logged in the server.log
logPrivateMessages: true

#Broadcast a message to every player when reloading the server.
broadcastServerReload: true