# LFunctions

This plugin is a replacement for /function that doesn't require being a senior datapack developer to edit config.yml

Here we have 4 concepts:

## Action
Action is `command` (player executes /command), `[SERVER] command` (console executes /command), `#[SERVER] command` (console executes /command but player sees the command output), `!!sleep <seconds>` (delay before next command)

## Function
Each function is a list of Actions which will be executed in right order. You can run them: /<function name> (only if you have lfunctions.function.<function name> permission).

## Event
You can bind some functions to in-game events: for example, when the player joins, run the greeting function.

## Arguments
You can add arguments to your function using /<function name> <argument1> <argument2>. If the argument has spaces /<function name> """<argument1>""" """<argument2>""" (surround them with triple quotes). Then %arg<number> is replaced with <argument number> in each action (example: /say %arg1). Also some events pass arguments:

- join: %arg1 = player real name, %arg2 = player display name (with colors, maybe prefixes and other stylizations)
