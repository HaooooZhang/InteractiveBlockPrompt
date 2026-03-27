
Source installation information for modders
-------------------------------------------
This code follows the Minecraft Forge installation methodology. It will apply
some small patches to the vanilla MCP source code, giving you and it access 
to some of the data and functions you need to build a successful mod.

Note also that the patches are built against "un-renamed" MCP source code (aka
SRG Names) - this means that you will not be able to read them directly against
normal code.

Setup Process:
==============================

Step 1: Open your command-line and browse to the folder where you extracted the zip file.

Step 2: You're left with a choice.
If you prefer to use Eclipse:
1. Run the following command: `./gradlew genEclipseRuns`
2. Open Eclipse, Import > Existing Gradle Project > Select Folder 
   or run `gradlew eclipse` to generate the project.

If you prefer to use IntelliJ:
1. Open IDEA, and import project.
2. Select your build.gradle file and have it import.
3. Run the following command: `./gradlew genIntellijRuns`
4. Refresh the Gradle Project in IDEA if required.

If at any point you are missing libraries in your IDE, or you've run into problems you can 
run `gradlew --refresh-dependencies` to refresh the local cache. `gradlew clean` to reset everything 
(this does not affect your code) and then start the process again.

Mapping Names:
=============================
By default, the MDK is configured to use the official mapping names from Mojang for methods and fields 
in the Minecraft codebase. These names are covered by a specific license. All modders should be aware of this
license, if you do not agree with it you can change your mapping names to other crowdsourced names in your 
build.gradle. For the latest license text, refer to the mapping file itself, or the reference copy here:
https://github.com/MinecraftForge/MCPConfig/blob/master/Mojang.md

Additional Resources: 
=========================
Community Documentation: https://docs.minecraftforge.net/en/1.20.1/gettingstarted/
LexManos' Install Video: https://youtu.be/8VEdtQLuLO0
Forge Forums: https://forums.minecraftforge.net/
Forge Discord: https://discord.minecraftforge.net/

Indicator JSON Config
=========================
This mod supports per-indicator JSON files under your config directory.

Default path:
- `config/ibp/indicators/book.json`
- `config/ibp/indicators/button.json`
- `config/ibp/indicators/click.json`
- `config/ibp/indicators/interest.json`
- `config/ibp/indicators/notice.json`
- `config/ibp/indicators/search.json`
- `config/ibp/indicators/toggle.json`
- `config/ibp/indicators/wrench.json`

When these files are missing, empty templates are created automatically.

`ibp-common.toml` options:
- `indicatorRules.enableJsonRules` (default `true`)
- `indicatorRules.jsonDirectory` (default `ibp/indicators`)

JSON format (`rules` can also be the root array):

{
  "rules": [
    {
      "block": "minecraft:barrel"
    },
    {
      "block_tag": "minecraft:signs"
    },
    {
      "block": "minecraft:chest",
      "nbt": "{CustomName:'\"Shop\"'}"
    }
  ]
}

Rule fields:
- `block`: exact block id.
- `block_tag`: block tag id.
- `nbt`: block entity NBT SNBT string.

Matching behavior:
- JSON rules are checked first for each indicator.
- If no JSON rule matches, existing `data/ibp/tags/blocks/*.json` behavior still works.
