# Username snipe notifier
A tool to track a Minecraft username's availability during its drop window.

Minecraft usernames have a variable drop widow during which it will be released at a random time. The purpose of this tool is to be able to track the username until it becomes available, and notify the user as soon as it becomes available via email.

This is not a sniping tool, it won't automate any part of the name change process apart from calling Minecraft username APIs to fetch the status

## How to use

One day if a miracle happens there will be a UI, but that day is probably gonna be the 30th of February.

### search settings
- change the "username" variable to your username of interest
- change the "freq" variable to how often (in seconds) you want to call Minecraft's APIs (recommended 10-60s)

### session token
- head to https://www.minecraft.net/en-us/msaprofile/mygames/editprofile, press f12 (developer mode), go to the network tab, and search for an operation called "profile". Under request headers you'll find "Authorization": "Bearer xxxxxxx", copy the value and paste it into the bearerToken variable.

### email settings
- from: your email
- to: your email, or whoever you want to notify (they can be the same). You can have multiple recipients, separated by commas without spaces
- subject: put whatever you want, or use the provided one, just make sure not to use too many caps or emojis to avoid it getting filtered as spam
- password: go to https://myaccount.google.com/ -> Security and sign-in, enable 2FA if it's not on (i think it's a requirement for creating an app password), look up "app passwords" in the search bar, give it a name, copy the output (should look like this: "abcd efgh ijkl mnop") and paste it into the password variable

## How to run

compile:
```bash
javac Main.java
```
run:
```bash
java Main
```


This tool is provided for personal use only. By using it, you agree to comply with [Mojang's Terms of Service](https://www.minecraft.net/en-us/eula). The author is not responsible for any misuse, account actions, or consequences resulting from the use of this software. Use at your own risk.
