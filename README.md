# EducationEdition

A repo containing findings surrounding Education Edition's lack of documentation

## manifest.json

Authors:

- [Causeway Digital](https://github.com/CausewayDigital)
- [Minecraft](https://github.com/Mojang)
- [Pathway Studios](https://github.com/PathwayStudios)
- [ReWrite Media](https://github.com/ReWrite-Media)

## education.json

```json
{
  "codebuilder" : {
      "defaulturi" : "https://minecraft.makecode.com/?ipc=1&inGame=1#tutorial:github:User/Repo/path/to/markdownfile",
      "disableLegacyTitleBar": true,
      "canResize":true,
      "capabilities":{
        "agent":{
          "permissions":{
            "canModifyBlocks": true
          }
        }
      }
    },
  "commands": {
    "hiddenFromPlayer": [
      "*",
      "!kick"
    ],
    "hiddenFromAutomation": [
      "!setblock",
      "!agent",
      "!replaceitem",
      "!give",
      "!execute",
      "!function"
    ]
  }
}
```

### codebuilder.defaulturi

The default URI for the in-game code editor, formatted in the form of:

```url
https://minecraft.makecode.com/?ipc=1&lockedEditor=1&inGame=1#tutorial:https://github.com/User/Repo/path/to/markdownfile
```

or

```url
https://minecraft.makecode.com/?ipc=1&inGame=1#tutorial:github:User/Repo/path/to/markdownfile
```

Other examples:

- <https://notebooks.minecrafteduservices.com/everglade/ci/index.html?lesson=https://rewrite-media.github.io/aznb/lessons/artemis/noCode.json>
- <https://minecraft.makecode.com/hoc2023?ipc=1&inGame=1&noRunOnX=1#tutorial:/hour-of-code/2023/no_code>
- <https://notebooks.minecrafteduservices.com/prod/index.html?lesson=https://notebooks.minecrafteduservices.com/everglade/content/row/master/hoc2020/no_code.json>

Example Repos:

- <https://github.com/CausewayDigital/Minecraft-EE-MakeCode>
- <https://github.com/Mojang/EducationContent>
- <https://github.com/ReWrite-Media/hoc2023-ts>
- <https://github.com/ReWrite-Media/hoc2023-md>

The tutorial link must be formatted without a file extension and with the `/blob/branch` segment removed.

### codebuilder.capabilities.agent.permissions

List of valid permissions:

- canModifyBlocks: boolean

### commands.hiddenFromPlayer and commands.hiddenFromAutomation

Hide commands from either a player character or command blocks/other automated systems. Useful within both map building and group world contexts. (eg when you want players to have the permission to change their gamemode and nothing else)

Formatted as an array of strings, with the possible values:

- "*" -- just means all commands
- "commandName" -- hide a specific command
- "!commandName" -- unhide a specific command name, usually used with "*" to only allow specific commands

Example:

```json
{
    ...
    "commands": {
      "hiddenFromPlayer": [ # Hides all commands except `/gamemode`
            "*",
            "!gamemode"
        ],
        "hiddenFromAutomation": [
            "!setblock",
            "!agent",
            "!replaceitem",
            "!give",
            "!execute",
            "!function"
        ]
    }
}
```
