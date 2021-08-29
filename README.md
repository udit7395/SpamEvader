# SpamEvader

A Simple Android Application to block incoming Spam and marketing calls 

# Motivation

Around 2018 for some unknown reason, I started receiving way too many spam/marketing calls, even though I had enabled "Do Not Call"(DNC) which gives you a choice about whether to receive telemarketing calls. I thought of using apps like [Trucaller](https://play.google.com/store/apps/details?id=com.truecaller) which use a common database with user's flagging spam calls. I wanted something that was local, something that could work on the mobile itself and require no internet connection. Also make it a bit of non-intrusive(not sharing my data) if possible.

# Working

One thing I noticed after blocking a few numbers was that the phone numbers of the spam callers that I was receiving in particular all had the same initial/starting numbers(first three or four digits) and digits after that changed. This meant blocking an individual number won't help for too long. So I exploited the initial same number feature and wrote the code to check if the incoming call started with a particular sequence and if yes I programmatically decline the call and notify the user that the call was blocked.  

# Why is this not on Google Playstore ?

I was planning to submit my app to Google Playstore once I had a basic working but soon the whole Android API scene changed(this is the best way I can put it at least). Quite a few Android API's I used changed or got restricted, privacy issues(like what permissions you can ask or could get) and battery optimizations from Android OEM's. I could work around all the issues by the user going through extra steps and essentially doing more work just to get the app to run(too much hassle). Hence, I dropped this idea.  

# Learnings

- Basic Animations
- Material Design (How to and what are the guidelines in general)  
- Battery Optimization on Android is not implemented well. More info about it [https://dontkillmyapp.com/](here) 


# Credits

- [tfKamran](https://github.com/tfKamran) for his help and guidance
- App Icon made by [Freepik](\"http://www.freepik.com\" "\"Freepik\"") from [www.flaticon.com](\"https://www.flaticon.com/\" "\"Flaticon\"") is licensed by [CC 3.0 BY](\"http://creativecommons.org/licenses/by/3.0/\" "\"Creative")
