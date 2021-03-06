/**
 *  ****************  Message_Central_Child  ****************
 *
 *  Design Usage:
 *  This is the 'Child' app for message automation...
 *
 *
 *  Copyright 2018 Andrew Parker
 *  
 *  This SmartApp is free!
 *  Donations to support development efforts are accepted via: 
 *
 *  Paypal at: https://www.paypal.me/smartcobra
 *  
 *
 *  I'm very happy for you to use this app without a donation, but if you find it useful then it would be nice to get a 'shout out' on the forum! -  @Cobra
 *  Have an idea to make this app better?  - Please let me know :)
 *
 *  Website: http://securendpoint.com/smartthings
 *
 *-------------------------------------------------------------------------------------------------------------------
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *-------------------------------------------------------------------------------------------------------------------
 *
 *
 *  Last Update: 15/02/2018
 *
 *  Changes:
 *
 *  V3.2.0 - added random 'pre' & 'post' message variables - also 'wakeup' variable messages
 *  V3.1.2 - added variable: %greeting% - to say 'good morning' , 'good afternoon' ... etc...
 *  V3.1.1 - UI slight revamp
 *  V3.1.0 - Added a second presence restriction - Useful if you want something to happen when one person is home but NOT another
 *  V3.0.1 - Added additional variables: %device% & %event%
 *  V3.0.0 - Added a new trigger setup 'Appliance Power Monitor' - This uses a second power threshold which must be exceeded before monitoring starts
 *  V2.9.0 - Added Missed message config to 'Time' trigger
 *  V2.8.0 - Added %opencontact% variable to check any open windows/door
 *  V2.7.0 - Added 'Button' as a trigger - either pushed or held
 *  V2.6.1 - Added delay between messages to SMS/Push
 *  V2.6.0 - Added 'Temperature' trigger ability for above or below configured temperature
 *  V2.5.0 - Added 'Motion' trigger ability for motion 'active' or 'inactive'
 *  V2.4.1 - Debug issue with presence restrictions not working correctly
 *  V2.4.0 - Revamped Weather Report - converted it to variable %weather%
 *  V2.3.2 - Changed %day% variable to correct English
 *  V2.3.1 - Added option to use 24hr format
 *  V2.3.0 - Added %time%, %day%, %date%, %year% as variables used in messages
 *  V2.2.0 - Removed requirement for allowed time & days - Now optional
 *  V2.1.0 - GUI revamp - Moved restrictions to their own page
 *  V2.0.1 - Debug
 *  V2.0.0 - Added 'Weather Report' - Trigger with Switch, Water, Contact, & Time
 *  V1.9.0 - Added 'Open Too Long' to speak when a contact (door?) has been open for more than the configured number of minutes
 *  V1.8.0 - Added ability to speak/send message if contact is open at a certain time (Used to check I closed the shed door)
 *  V1.7.0 - Added ability to SMS/Push instead of speaking
 *  V1.6.0 - Added Routines & Mode Change as triggers
 *  V1.5.1 - Debug - Disable switch not always working
 *  V1.5.0 - Added 'Presence' restriction so will only speak if someone is present/not present
 *  V1.4.0 - Added 'Power' trigger and ability to use 'and stays that way' to use with Washer or Dryer applicance
 *  V1.3.2 - Debug
 *  V1.3.1 - Code cleanup & new icon path
 *  V1.3.0 - Added 'quiet' time to allow different volume levels at certain times
 *  V1.2.2 - New Icons
 *  V1.2.1 - Debug - Time did not have day restriction
 *  V1.2.0 - Added switchable logging
 *	V1.1.0 - Added delay between messages
 *  V1.0.2 - Debug
 *  V1.0.1 - Header & Debug
 *  V1.0.0 - POC
 *
 *  If modifying this project, please keep the above header intact and add your comments/credits below - Thank you! -  @Cobra
 *
 *-------------------------------------------------------------------------------------------------------------------
 */

definition(
    name: "Message_Central_Child",
    namespace: "Cobra",
    author: "Andrew Parker",
    description: "Child App for Message Automation",
     category: "Fun & Social",

parent: "Cobra:Message Central",

    iconUrl: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/voice.png",
    iconX2Url: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/voice.png",
    iconX3Url: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/voice.png")

preferences {
    page name: "mainPage", title: "", install: false, uninstall: true, nextPage: "restrictionsPage"
    page(name: "pageHelpVariables")
    page name: "restrictionsPage", title: "", install: false, uninstall: true, nextPage: "variablesPage"
    page name: "variablesPage", title: "", install: false, uninstall: true, nextPage: "namePage"
    page name: "namePage", title: "", install: true, uninstall: true
    
}

def installed() {
    initialize()
}

def updated() {
    unschedule()
    initialize()
}

def initialize() {
	  log.info "Initialised with settings: ${settings}"
      setAppVersion()
      logCheck()
      
      switchRunCheck()
      state.timer1 = true
      state.timer2 = true
      if (!restrictPresenceSensor){
      state.presenceRestriction = true
      }
      if (!restrictPresenceSensor1){
      state.presenceRestriction1 = true
      }
      state.contact1SW = 'closed' 
     if(state.msgType == "Voice Message"){ 
     checkVolume()
     }
      
// Subscriptions    

subscribe(enableSwitch, "switch", switchEnable)





if(trigger == 'Time'){
   LOGDEBUG("Trigger is $trigger")
   schedule(runTime,timeTalkNow)
  if (missedPresenceSensor1){subscribe(missedPresenceSensor1, "presence", missedPresenceCheckNow)}
    }
    
if(trigger == 'Time if Contact Open'){
   LOGDEBUG("Trigger is $trigger")
   schedule(runTime,timeTalkNow1)
   subscribe(contact1, "contact", contact1Handler)
    }   
    
else if(trigger == 'Button'){
     LOGDEBUG( "Trigger is $trigger")
subscribe(button1, "button", buttonEvent)
    }
    
else if(trigger == 'Switch'){
     LOGDEBUG( "Trigger is $trigger")
subscribe(switch1, "switch", switchTalkNow)
    }
else if(trigger == 'Water'){
    LOGDEBUG( "trigger is $trigger")
subscribe(water1, "water.wet", waterTalkNow) 
subscribe(water1, "water.dry", waterTalkNow) 
	}
else if(trigger == 'Contact'){
    LOGDEBUG( "trigger is $trigger")
subscribe(contactSensor, "contact", contactTalkNow) 
	}
else if(trigger == 'Presence'){
    LOGDEBUG("trigger is $trigger")
subscribe(presenceSensor1, "presence", presenceTalkNow) 
     
	}
else if(trigger == 'Power'){
    LOGDEBUG("trigger is $trigger")
subscribe(powerSensor, "power", powerTalkNow) 
     
	}
    
else if(trigger == 'Appliance Power Monitor'){
    LOGDEBUG("trigger is $trigger")
subscribe(powerSensor, "power", powerApplianceNow) 
     
	}    
    
else if(trigger == 'Motion'){
    LOGDEBUG("trigger is $trigger")
subscribe(motionSensor, "motion" , motionTalkNow) 
     
	}    
    
else if(trigger == 'Temperature'){
    LOGDEBUG("trigger is $trigger")
subscribe(tempSensor, "temperature" , tempTalkNow) 
     
	}    
else if(trigger == 'Routine'){
    LOGDEBUG("trigger is $trigger")
 subscribe(location, "routineExecuted", routineChanged)
    
    }
else if(trigger == 'Mode Change'){
    LOGDEBUG("trigger is $trigger")
subscribe(location, "mode", modeChangeHandler)

	}
    
else if(trigger == 'Open Too Long'){
    LOGDEBUG("trigger is $trigger")
subscribe(openSensor, "contact", tooLongOpen)

	}
    
if (restrictPresenceSensor){
subscribe(restrictPresenceSensor, "presence", restrictPresenceSensorHandler)
}    

if (restrictPresenceSensor1){
subscribe(restrictPresenceSensor1, "presence", restrictPresence1SensorHandler)
}    



}


// main page *************************************************************************
def mainPage() {
    dynamicPage(name: "mainPage") {
      
        section {
        paragraph image: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/voice.png",
                  title: "Message Central Child",
                  required: false,
                  "This child app allows you use different triggers to create different voice or text messages"
                  }
     section() {
   
        paragraph image: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/cobra3.png",
                         " Child Version: $state.appversion - Copyright © 2017 - 2018 Cobra" 
                      	
   
            href "pageHelpVariables", title:"Message Variables", description:"Tap here for a list of 'variables' you can use in your messages (and what they do)"
        }

    
      section() {
        	speakerInputs()
            triggerInput()
            actionInputs()
        }
         }
}


def variablesPage() {
       dynamicPage(name: "variablesPage") {
      restrictionInputs()            
      }  
    }
    
    
def pageHelpVariables(){
	

    dynamicPage(name: "pageHelpVariables", title: "Message Variables", install: false, uninstall:false){

       section("The following variables can be used in your event messages and will be replaced with the details listed below"){

	def AvailableVariables = ""

	AvailableVariables += " %time% 			-		Replaced with current time in 12 or 24 hour format (Switchable)\n\n"
	AvailableVariables += " %day% 			- 		Replaced with current day of the week\n\n"
	AvailableVariables += " %date% 			- 		Replaced with current day number & month\n\n"
	AvailableVariables += " %year% 			- 		Replaced with the current year\n\n"
    AvailableVariables += " %greeting% 		- 		Replaced with 'Good Morning', 'Good Afternoon' or 'Good Evening' (evening starts at 6pm)\n\n"
    AvailableVariables += " %pre%			- 		Replaced with the a random 'prefix' message\n\n"
    AvailableVariables += " %post%			- 		Replaced with the a random 'post' message\n\n"
    AvailableVariables += " %wakeup%		- 		Replaced with the a random 'wake up' message\n\n"
	AvailableVariables += " %weather% 		- 		Replaced with the current weather forcast\n\n"
	AvailableVariables += " %opencontact% 	- 		Replaced with a list of configured contacts if they are open\n\n"
	AvailableVariables += " %device% 		- 		Replaced with the name of the triggering device\n\n"
	AvailableVariables +=  " %event% 			- 		Replaced with what triggered the action (e.g. On/Off, Wet/Dry)" 	

	paragraph(AvailableVariables)


           
       }
   }
}    
    

def restrictionsPage() {
       dynamicPage(name: "restrictionsPage") {
       
         section("Time Format") { 
      input "hour24", "bool", title: "If using the %time% variable, On = 24hr format - Off = 12Hr format", required: true, defaultValue: false
       }
       section("Check Open Contacts") { 
       input "sensors", "capability.contactSensor", title: "If using the %opencontact% variable, choose window/door contact", required: false, multiple: true, submitOnChange: true
       
       } 
       
      }  
    }


def namePage() {
       dynamicPage(name: "namePage") {
       
     
            section("Automation name") {
                label title: "Enter a name for this message automation", required: false
            }
            section("Logging") {
            input "debugMode", "bool", title: "Enable logging", required: true, defaultValue: false
  	        }
      }  
    }



// defaults
def speakerInputs(){	
	input "enableSwitch", "capability.switch", title: "Select switch Enable/Disable this message (Optional)", required: false, multiple: false 
    input "messageAction", "enum", title: "Select Message Type", required: true, submitOnChange: true,  options: ["Voice Message", "SMS/Push Message"]

 if (messageAction){
 state.msgType = messageAction
    if(state.msgType == "Voice Message"){
	input "speaker", "capability.musicPlayer", title: "Choose speaker(s)", required: false, multiple: true
	input "volume1", "number", title: "Normal Speaker volume", description: "0-100%", defaultValue: "85",  required: true
    
 
	}

	
 }
}




// inputs
def triggerInput() {
   input "trigger", "enum", title: "How to trigger message?",required: true, submitOnChange: true, options: ["Appliance Power Monitor", "Button", "Contact", "Contact - Open Too Long", "Switch", "Mode Change", "Motion", "Power", "Presence", "Routine", "Temperature", "Time", "Time if Contact Open", "Water"]
  
}

def restrictionInputs(){

		section() {
           		mode title: "Run only when in specific mode(s) ", required: false
            }
        

		section() {
        
    input "restrictions1", "bool", title: "Restrict by Time & Day", required: true, defaultValue: false, submitOnChange: true
    input "restrictions2", "bool", title: "Restrict Volume by Time", required: true, defaultValue: false, submitOnChange: true
    input "restrictions3", "bool", title: "Restrict by Presence Sensor", required: true, defaultValue: false, submitOnChange: true
     }

    
    
     if(restrictions1){    
     	section("Time/Day") {
    input "fromTime", "time", title: "Allow messages from", required: false
    input "toTime", "time", title: "Allow messages until", required: false
    input "days", "enum", title: "Select Days of the Week", required: false, multiple: true, options: ["Monday": "Monday", "Tuesday": "Tuesday", "Wednesday": "Wednesday", "Thursday": "Thursday", "Friday": "Friday", "Saturday": "Saturday", "Sunday": "Sunday"]
    		}
    }
    if(restrictions2){
		section("'Quiet Time' - This is to reduce volume during a specified time") {
    input "volume2", "number", title: "Quiet Time Speaker volume", description: "0-100%", required: false, submitOnChange: true
    if(volume2){
    input "fromTime2", "time", title: "Quiet Time Start", required: false
    input "toTime2", "time", title: "Quiet Time End", required: false  
    		}
    	}
    }
    if(restrictions3){
    section("This is to restrict on 1 or 2 presence sensor(s)") {
    input "restrictPresenceSensor", "capability.presenceSensor", title: "Select presence sensor 1 to restrict action", required: false, multiple: false, submitOnChange: true
    if(restrictPresenceSensor){
   	input "restrictPresenceAction", "bool", title: "   On = Action only when someone is 'Present'  \r\n   Off = Action only when someone is 'NOT Present'  ", required: true, defaultValue: false    
	}
     input "restrictPresenceSensor1", "capability.presenceSensor", title: "Select presence sensor 2 to restrict action", required: false, multiple: false, submitOnChange: true
    if(restrictPresenceSensor1){
   	input "restrictPresenceAction1", "bool", title: "   On = Action only when someone is 'Present'  \r\n   Off = Action only when someone is 'NOT Present'  ", required: true, defaultValue: false    
	}
    
    }
            }
		
  
}

def actionInputs() {
    if (trigger) {
    state.selection = trigger
    
if(state.selection == 'Button'){
   input "button1", "capability.button", title: "Button", multiple: false, required: false

    
    if(state.msgType == "Voice Message"){
	input "message1", "text", title: "Message to play when button pressed",  required: false, submitOnChange: true
	input "message2", "text", title: "Message to play when button held",  required: false, submitOnChange: true
    input "triggerDelay", "number", title: "Delay after trigger before speaking (Enter 0 for no delay)", defaultValue: '0', description: "Seconds", required: true
    input "msgDelay", "number", title: "Delay between messages (Enter 0 for no delay)", defaultValue: '0', description: "Minutes", required: true
    
    }
    if(state.msgType == "SMS/Push Message"){
     input "message1", "text", title: "Message to send when button pressed",  required: false, submitOnChange: true
	 input "message2", "text", title: "Message to send when button held",  required: false, submitOnChange: true
     input "triggerDelay", "number", title: "Delay after trigger before sending (Enter 0 for no delay)", defaultValue: '0', description: "Seconds", required: true
	 input "msgDelay", "number", title: "Delay between messages (Enter 0 for no delay)", defaultValue: '0', description: "Minutes", required: true
     input("recipients", "contact", title: "Send notifications to") {
     input(name: "sms", type: "phone", title: "Send A Text To", description: null, required: false)
     input(name: "pushNotification", type: "bool", title: "Send a push notification to", description: null, defaultValue: true)
    
    }
    }
	
    

}     
    
    
if(state.selection == 'Switch'){
    input "switch1", "capability.switch", title: "Select switch to trigger message/report", required: false, multiple: false 

    
    if(state.msgType == "Voice Message"){
	input "message1", "text", title: "Message to play when switched on",  required: false, submitOnChange: true
	input "message2", "text", title: "Message to play when switched off",  required: false, submitOnChange: true
    input "triggerDelay", "number", title: "Delay after trigger before speaking (Enter 0 for no delay)", defaultValue: '0', description: "Seconds", required: true
    input "msgDelay", "number", title: "Delay between messages (Enter 0 for no delay)", defaultValue: '0', description: "Minutes", required: true
    
    }
    if(state.msgType == "SMS/Push Message"){
     input "message1", "text", title: "Message to send when switched On",  required: false, submitOnChange: true
	 input "message2", "text", title: "Message to send when switched Off",  required: false, submitOnChange: true
     input "triggerDelay", "number", title: "Delay after trigger before sending (Enter 0 for no delay)", defaultValue: '0', description: "Seconds", required: true
	 input "msgDelay", "number", title: "Delay between messages (Enter 0 for no delay)", defaultValue: '0', description: "Minutes", required: true
     input("recipients", "contact", title: "Send notifications to") {
     input(name: "sms", type: "phone", title: "Send A Text To", description: null, required: false)
     input(name: "pushNotification", type: "bool", title: "Send a push notification to", description: null, defaultValue: true)
    
    }
    }
	
    

}    
 

else if(state.selection == 'Water'){
	
     
    if(state.msgType == "Voice Message"){
    input "water1", "capability.waterSensor", title: "Select water sensor to trigger message", required: false, multiple: false 
	input "message1", "text", title: "Message to play when WET",  required: false
	input "message2", "text", title: "Message to play when DRY",  required: false
    input "triggerDelay", "number", title: "Delay after trigger before speaking (Enter 0 for no delay)", description: "Seconds", required: true
   	input "msgDelay", "number", title: "Delay between messages (Enter 0 for no delay)", defaultValue: '0', description: "Minutes", required: true
	}
    
    if(state.msgType == "SMS/Push Message"){
     input "water1", "capability.waterSensor", title: "Select water sensor to trigger message", required: false, multiple: false 
     input "message1", "text", title: "Message to send when Wet",  required: false
	 input "message2", "text", title: "Message to send when Dry",  required: false
     input "triggerDelay", "number", title: "Delay after trigger before sending (Enter 0 for no delay)", defaultValue: '0', description: "Seconds", required: true
	 input "msgDelay", "number", title: "Delay between messages (Enter 0 for no delay)", defaultValue: '0', description: "Minutes", required: true
     input("recipients", "contact", title: "Send notifications to") {
     input(name: "sms", type: "phone", title: "Send A Text To", description: null, required: false)
     input(name: "pushNotification", type: "bool", title: "Send a push notification to", description: null, defaultValue: true)
     
    	}
    }
   
    }   

else if(state.selection == 'Presence'){
	input "presenceSensor1", "capability.presenceSensor", title: "Select presence sensor to trigger message", required: false, multiple: false 
   
    if(state.msgType == "Voice Message"){
	input "message1", "text", title: "Message to play when sensor arrives",  required: false
	input "message2", "text", title: "Message to play when sensor leaves",  required: false
    input "triggerDelay", "number", title: "Delay after trigger before speaking (Enter 0 for no delay)", defaultValue: "0", description: "Seconds", required: true
    input "msgDelay", "number", title: "Delay between messages (Enter 0 for no delay)", defaultValue: '0', description: "Minutes", required: true
	}
    
     if(state.msgType == "SMS/Push Message"){
     input "message1", "text", title: "Message to send when sensor arrives",  required: false
	 input "message2", "text", title: "Message to send when sensor leaves",  required: false
     input "triggerDelay", "number", title: "Delay after trigger before sending (Enter 0 for no delay)", defaultValue: '0', description: "Seconds", required: true
	 input "msgDelay", "number", title: "Delay between messages (Enter 0 for no delay)", defaultValue: '0', description: "Minutes", required: true
     input("recipients", "contact", title: "Send notifications to") {
     input(name: "sms", type: "phone", title: "Send A Text To", description: null, required: false)
     input(name: "pushNotification", type: "bool", title: "Send a push notification to", description: null, defaultValue: true)
     
    	}
    }
} 

else if(state.selection == 'Contact'){
	input "contactSensor", "capability.contactSensor", title: "Select contact sensor to trigger message", required: false, multiple: false 
   
     
    if(state.msgType == "Voice Message"){
	input "message1", "text", title: "Message to play when sensor opens",  required: false
	input "message2", "text", title: "Message to play when sensor closes",  required: false
    input "triggerDelay", "number", title: "Delay after trigger before speaking (Enter 0 for no delay)", description: "Seconds", required: true
    input "msgDelay", "number", title: "Delay between messages (Enter 0 for no delay)", defaultValue: '0', description: "Minutes", required: true
	    }
     if(state.msgType == "SMS/Push Message"){
     input "message1", "text", title: "Message to send when sensor opens",  required: false
	 input "message2", "text", title: "Message to send when sensor closes",  required: false
     input "triggerDelay", "number", title: "Delay after trigger before sending (Enter 0 for no delay)", defaultValue: '0', description: "Seconds", required: true
	 input "msgDelay", "number", title: "Delay between messages (Enter 0 for no delay)", defaultValue: '0', description: "Minutes", required: true
     input("recipients", "contact", title: "Send notifications to") {
     input(name: "sms", type: "phone", title: "Send A Text To", description: null, required: false)
     input(name: "pushNotification", type: "bool", title: "Send a push notification to", description: null, defaultValue: true)
     
    	}
    }   
   
   
} 

else if(state.selection == 'Power'){
	input "powerSensor", "capability.powerMeter", title: "Select power sensor to trigger message", required: false, multiple: false 
    input(name: "belowThreshold", type: "number", title: "Power Threshold (Watts)", required: true, description: "this number of watts")
    input "actionType1", "bool", title: "Select Power Sensor action type: \r\n \r\n On = Alert when power goes ABOVE configured threshold  \r\n Off = Alert when power goes BELOW configured threshold", required: true, defaultValue: false
	input(name: "delay1", type: "number", title: "Only if it stays that way for this number of minutes...", required: true, description: "this number of minutes", defaultValue: '0')
    
    
  if(state.msgType == "Voice Message"){
    input "message1", "text", title: "Message to play ...",  required: false
    input "triggerDelay", "number", title: "Delay after trigger before speaking (Enter 0 for no delay - Seconds)", description: "Seconds", required: true, defaultValue: '0'
    input "msgDelay", "number", title: "Delay between messages (Enter 0 for no delay)", defaultValue: '0', description: "Minutes", required: true
    }
  if(state.msgType == "SMS/Push Message"){
     input "message1", "text", title: "Message to send...",  required: false
     input "triggerDelay", "number", title: "Delay after trigger before sending (Enter 0 for no delay)", defaultValue: '0', description: "Seconds", required: true
	 input "msgDelay", "number", title: "Delay between messages (Enter 0 for no delay)", defaultValue: '0', description: "Minutes", required: true
	 input("recipients", "contact", title: "Send notifications to") {
     input(name: "sms", type: "phone", title: "Send A Text To", description: null, required: false)
     input(name: "pushNotification", type: "bool", title: "Send a push notification to", description: null, defaultValue: true)
    
    	}
    }    
} 


else if(state.selection == 'Appliance Power Monitor'){
	input "powerSensor", "capability.powerMeter", title: "Select power sensor to trigger message", required: true, multiple: false 
    input(name: "belowThreshold", type: "number", title: "Below Power Threshold (Watts)", required: true, description: "Trigger below this number of watts", defaultValue: '0')
    input(name: "delay2", type: "number", title: "Only if it stays that way for this number of minutes...", required: true, description: "this number of minutes", defaultValue: '0')
    input(name: "aboveThreshold", type: "number", title: "Activate Power Threshold (Watts)", required: true, description: "Start monitoring above this number of watts", defaultValue: '0')
	
    
    
  if(state.msgType == "Voice Message"){
    input "message1", "text", title: "Message to play ...",  required: false
    input "triggerDelay", "number", title: "Delay after trigger before speaking (Enter 0 for no delay - Seconds)", description: "Seconds", required: true, defaultValue: '0'
    input "msgDelay", "number", title: "Delay between messages (Enter 0 for no delay)", defaultValue: '0', description: "Minutes", required: true
    }
  if(state.msgType == "SMS/Push Message"){
     input "message1", "text", title: "Message to send...",  required: false
     input "triggerDelay", "number", title: "Delay after trigger before sending (Enter 0 for no delay)", defaultValue: '0', description: "Seconds", required: true
	 input "msgDelay", "number", title: "Delay between messages (Enter 0 for no delay)", defaultValue: '0', description: "Minutes", required: true
	 input("recipients", "contact", title: "Send notifications to") {
     input(name: "sms", type: "phone", title: "Send A Text To", description: null, required: false)
     input(name: "pushNotification", type: "bool", title: "Send a push notification to", description: null, defaultValue: true)
    
    	}
    }    
} 

else if(state.selection == 'Motion'){
	input "motionSensor",  "capability.motionSensor", title: "Select Motion Sensor", required: false, multiple: false 
    input "motionActionType", "bool", title: "Select Motion Sensor action type: \r\n \r\n On = Alert when motion 'Active'  \r\n Off = Alert when motion 'Inactive'", required: true, defaultValue: false
	
    
  if(state.msgType == "Voice Message"){
    input "message1", "text", title: "Message to play ...",  required: false
    input "triggerDelay", "number", title: "Delay after trigger before speaking (Enter 0 for no delay - Seconds)", description: "Seconds", required: true, defaultValue: '0'
    input "msgDelay", "number", title: "Delay between messages (Enter 0 for no delay)", defaultValue: '0', description: "Minutes", required: true
    }
  if(state.msgType == "SMS/Push Message"){
     input "message1", "text", title: "Message to send...",  required: false
     input "triggerDelay", "number", title: "Delay after trigger before sending (Enter 0 for no delay)", defaultValue: '0', description: "Seconds", required: true
	 input "msgDelay", "number", title: "Delay between messages (Enter 0 for no delay)", defaultValue: '0', description: "Minutes", required: true
	 input("recipients", "contact", title: "Send notifications to") {
     input(name: "sms", type: "phone", title: "Send A Text To", description: null, required: false)
     input(name: "pushNotification", type: "bool", title: "Send a push notification to", description: null, defaultValue: true)
   
    	}
    }    
}
else if(state.selection == 'Temperature'){
	input "tempSensor",  "capability.temperatureMeasurement" , title: "Select Temperature Sensor", required: false, multiple: false 
    input "temperature1", "number", title: "Set Temperature", required: true
    input "tempActionType", "bool", title: "Select Temperature Sensor action type: \r\n \r\n On = Alert when above set temperature  \r\n Off = Alert when below set temperature", required: true, defaultValue: false
	
    
  if(state.msgType == "Voice Message"){
    input "message1", "text", title: "Message to play ...",  required: false
    input "triggerDelay", "number", title: "Delay after trigger before speaking (Enter 0 for no delay - Seconds)", description: "Seconds", required: true, defaultValue: '0'
    input "msgDelay", "number", title: "Delay between messages (Enter 0 for no delay)", defaultValue: '0', description: "Minutes", required: true
    }
  if(state.msgType == "SMS/Push Message"){
     input "message1", "text", title: "Message to send...",  required: false
     input "triggerDelay", "number", title: "Delay after trigger before sending (Enter 0 for no delay)", defaultValue: '0', description: "Seconds", required: true
	 input "msgDelay", "number", title: "Delay between messages (Enter 0 for no delay)", defaultValue: '0', description: "Minutes", required: true
	 input("recipients", "contact", title: "Send notifications to") {
     input(name: "sms", type: "phone", title: "Send A Text To", description: null, required: false)
     input(name: "pushNotification", type: "bool", title: "Send a push notification to", description: null, defaultValue: true)
     
    	}
    }    
}

else if(state.selection == 'Time'){
	input (name: "runTime", title: "Time to run", type: "time",  required: true) 
   
    
   if(state.msgType == "Voice Message"){
	input "messageTime", "text", title: "Message to play",  required: true
    input "missedMessageAction", "bool", title: "Let me know if I miss this message while away ", required: true, defaultValue: false, submitOnChange: true 
   
   if(missedMessageAction == true){
    input "missedPresenceSensor1", "capability.presenceSensor", title: "Select presence sensor", required: true, multiple: false
    input "missedMessage", "text", title: "Message reminder to play when presence sensor arrives if original message missed",  required: true
    input "missedMsgDelay", "number", title: "Delay after arriving before reminder message", defaultValue: '0', description: "Seconds", required: true
   }
    
   		}
  if(state.msgType == "SMS/Push Message"){
     input "messageTime", "text", title: "Message to send...",  required: false
   	 input("recipients", "contact", title: "Send notifications to") {
     input(name: "sms", type: "phone", title: "Send A Text To", description: null, required: false)
     input(name: "pushNotification", type: "bool", title: "Send a push notification to", description: null, defaultValue: true)
    
    	}
    }  
   
    
     
}   

else if(state.selection == 'Time if Contact Open'){
	input (name: "runTime", title: "Time to run", type: "time",  required: true) 
    input "contact1", "capability.contactSensor", title: "Select contact sensor to check", required: false, multiple: false 
   
  if(state.msgType == "Voice Message"){
	input "messageTime", "text", title: "Message to play if contact open",  required: true
   		}
  if(state.msgType == "SMS/Push Message"){
     input "messageTime", "text", title: "Message to send if contact open",  required: false
	 input("recipients", "contact", title: "Send notifications to") {
     input(name: "sms", type: "phone", title: "Send A Text To", description: null, required: false)
     input(name: "pushNotification", type: "bool", title: "Send a push notification to", description: null, defaultValue: true)
    	}
    }  
      
}   

else if(state.selection == 'Mode Change'){
	input "newMode1", "mode", title: "Action when changing to this mode",  required: false
    
     
  if(state.msgType == "Voice Message"){
	input "message1", "text", title: "Message to play",  required: true
    input "triggerDelay", "number", title: "Delay after trigger before speaking (Enter 0 for no delay - Seconds)", description: "Seconds", required: true, defaultValue: '0'
    input "msgDelay", "number", title: "Delay between messages (Enter 0 for no delay)", defaultValue: '0', description: "Minutes", required: true
       }
    
   if(state.msgType == "SMS/Push Message"){
     input "message1", "text", title: "Message to send...",  required: false
     input "triggerDelay", "number", title: "Delay after trigger before sending (Enter 0 for no delay)", defaultValue: '0', description: "Seconds", required: true
	 input "msgDelay", "number", title: "Delay between messages (Enter 0 for no delay)", defaultValue: '0', description: "Minutes", required: true
	 input("recipients", "contact", title: "Send notifications to") {
     input(name: "sms", type: "phone", title: "Send A Text To", description: null, required: false)
     input(name: "pushNotification", type: "bool", title: "Send a push notification to", description: null, defaultValue: true)
    
    	}
    } 
   
	} 
    
else if(state.selection == 'Routine'){
	  def actions = location.helloHome?.getPhrases()*.label
            if (actions) {
            input "routine1", "enum", title: "Action when this routine runs", required: false, options: actions
            }
           
            
   if(state.msgType == "Voice Message"){
	input "message1", "text", title: "Message to play",  required: true
    input "triggerDelay", "number", title: "Delay after trigger before speaking (Enter 0 for no delay - Seconds)", description: "Seconds", required: true, defaultValue: '0'
    input "msgDelay", "number", title: "Delay between messages (Enter 0 for no delay)", defaultValue: '0', description: "Minutes", required: true
    }
  if(state.msgType == "SMS/Push Message"){
    input "message1", "text", title: "Message to send...",  required: false
    input "triggerDelay", "number", title: "Delay after trigger before sending (Enter 0 for no delay)", defaultValue: '0', description: "Seconds", required: true
	input "msgDelay", "number", title: "Delay between messages (Enter 0 for no delay)", defaultValue: '0', description: "Minutes", required: true
	input("recipients", "contact", title: "Send notifications to") {
    input(name: "sms", type: "phone", title: "Send A Text To", description: null, required: false)
    input(name: "pushNotification", type: "bool", title: "Send a push notification to", description: null, defaultValue: true)
    
    	}
    } 
    
} 
if(state.selection == 'Contact - Open Too Long'){
	input "openSensor", "capability.contactSensor", title: "Select contact sensor to trigger message", required: false, multiple: false 
   	input(name: "opendelay1", type: "number", title: "Only if it stays open for this number of minutes...", required: true, description: "this number of minutes", defaultValue: '0')
   
    
  if(state.msgType == "Voice Message"){
    input "message1", "text", title: "Message to play ...",  required: false
    input "msgDelay", "number", title: "Delay between messages (Enter 0 for no delay)", defaultValue: '0', description: "Minutes", required: true
  	
    }
  if(state.msgType == "SMS/Push Message"){
     input "message1", "text", title: "Message to send...",  required: false
     input "msgDelay", "number", title: "Delay between messages (Enter 0 for no delay)", defaultValue: '0', description: "Minutes", required: true
	 input("recipients", "contact", title: "Send notifications to") {
     input(name: "sms", type: "phone", title: "Send A Text To", description: null, required: false)
     input(name: "pushNotification", type: "bool", title: "Send a push notification to", description: null, defaultValue: true)
    
    	}
    }    
}
    

}
}





// Handlers




// Appliance Power Monitor
def powerApplianceNow(evt){
state.meterValue = evt.value as double
state.activateThreshold = aboveThreshold
state.nameOfDevice = evt.displayName
state.actionEvent = evt.value as double
LOGDEBUG( "Power reported $state.meterValue watts")
if(state.meterValue > state.activateThreshold){
state.activate = true
LOGDEBUG( "Activate threshold reached or exceeded setting state.activate to: $state.activate")

}



 if(state.appgo == true && state.activate == true){
LOGDEBUG( "powerApplianceNow -  Power is: $state.meterValue")
    state.belowValue = belowThreshold as int
    if (state.meterValue < state.belowValue) {
   def mydelay = 60 * delay2 
   LOGDEBUG( "Checking again after delay: $delay2 minutes... Power is: $state.meterValue")
       runIn(mydelay, checkApplianceAgain1, [overwrite: false])     
       
      }
}

	 if(state.activate == false){
     LOGDEBUG( "Not reached threshold yet to activate monitoring")
     
     }
     
     
 if(state.appgo == false){
    LOGDEBUG("App disabled by $enableswitch being off")

}

}


def checkApplianceAgain1() {
   
     if (state.meterValue < state.belowValue) {
      LOGDEBUG( " checkApplianceAgain1 - Checking again now... Power is: $state.meterValue")
    
      speakNow()
      state.activate = false  
			}
     else  if (state.meterValue > state.belowValue) {
     LOGDEBUG( "checkApplianceAgain1 -  Power is: $state.meterValue so cannot run yet...")
	}	
}	






// Missed message presence handler
def missedPresenceCheckNow(evt){
	state.missedPresencestatus1 = evt.value
LOGDEBUG("state.missedPresencestatus1 = $evt.value")

	def	myMissedDelay = missedMsgDelay

	if(state.missedPresencestatus1 == "present" && state.missedEvent == true){
   
LOGDEBUG("Telling you about missed events in $missedMsgDelay seconds (If there are any, and I haven't already told you about them)")
    
    runIn(myMissedDelay, speakMissedNow, [overwrite: false])
    
    }
if(state.missedPresencestatus1 == "present" && state.missedEvent == false){
LOGDEBUG("No missed messages yet")
	}
}


// check if any timed messages have been missed
def checkTimeMissedNow(){
LOGDEBUG("Checking missed events now...")
	if(state.missedPresencestatus1 == 'not present'){
    state.missedEvent = true
    state.alreadyDone = false
LOGDEBUG("Missed a time event")
	}

	if(state.missedPresencestatus1 == 'present'){
	state.missedEvent = false
LOGDEBUG("No missed timed events")
	}
}

// speak any missed message
def speakMissedNow(){

LOGDEBUG("SpeakMissedNow called...")
	state.myMsg = missedMessage
	    
  if (state.alreadyDone == false){  
LOGDEBUG("Message = $state.myMsg")
	
	speaker.speak(state.myMsg)
	state.alreadyDone = true
	}

  if (state.alreadyDone == true){ 
LOGDEBUG("Already told you, so won't tell you again!")
	}
}







// Button
def buttonEvent(evt){
state.buttonStatus1 = evt.value
state.nameOfDevice = evt.displayName
state.actionEvent = evt.value
LOGDEBUG("Button is $state.buttonStatus1 state.presenceRestriction = $state.presenceRestriction")
state.msg1 = message1
state.msg2 = message2
def mydelay = triggerDelay



if(state.msgType == "Voice Message"){
LOGDEBUG("Button - Voice Message")

	if(state.buttonStatus1 == 'pushed'){
state.msgNow = 'oneNow'

    }

	else if (state.buttonStatus1 == 'held'){
state.msgNow = 'twoNow'
	}

LOGDEBUG( "$button1 is $state.buttonStatus1")

checkVolume()
LOGDEBUG("Speaker(s) in use: $speaker set at: $state.volume% - waiting $mydelay seconds before continuing..."  )

runIn(mydelay, talkSwitch)
}

if(state.msgType == "SMS/Push Message"){
LOGDEBUG("Button - SMS/Push Message")

	if(state.buttonStatus1 == 'pushed' && state.msg1 != null){
def msg = message1
LOGDEBUG("Button - SMS/Push Message - Sending Message: $msg")
  sendMessage(msg)
    }
    
    
    if(state.buttonStatus1 == 'held' && state.msg2 != null){
def msg = message2
LOGDEBUG("Button - SMS/Push Message - Sending Message: $msg")
  sendMessage(msg)
    }

}

}


// Temperature
def tempTalkNow(evt){
state.tempStatus1 = evt.doubleValue
state.nameOfDevice = evt.displayName
state.actionEvent = evt.doubleValue
state.msg1 = message1
state.msgNow = 'oneNow'
def myTemp = temperature1

if(tempActionType == true && state.tempStatus1 > myTemp){

 if(state.msgType == "Voice Message"){
    talkSwitch()
   }          
  
      
  else if(state.msgType == "SMS/Push Message"){
	def msg = message1
LOGDEBUG("TempTalkNow - SMS/Push Message - Sending Message: $msg")
  sendMessage(msg)
	}


	}
if(tempActionType == false && state.tempStatus1 < myTemp){
 if(state.msgType == "Voice Message"){
    talkSwitch()
   }          
  
      
  else if(state.msgType == "SMS/Push Message"){
	def msg = message1
LOGDEBUG("TempTalkNow - SMS/Push Message - Sending Message: $msg")
  sendMessage(msg)
	}

	}


}





// Motion

def motionTalkNow(evt){
state.motionStatus1 = evt.value
state.nameOfDevice = evt.displayName
state.actionEvent = evt.value
state.msg1 = message1
state.msgNow = 'oneNow'

if(motionActionType == true && state.motionStatus1 == 'active'){
 LOGDEBUG( "MotionTalkNow... Sensor Active - Configured to alert on active motion sensor")
    
    if(state.msgType == "Voice Message"){
    talkSwitch()
   }          
  
      
  else if(state.msgType == "SMS/Push Message"){
	def msg = message1
LOGDEBUG("MotionTalkNow - SMS/Push Message - Sending Message: $msg")
  sendMessage(msg)
	}
}

if(motionActionType == false && state.motionStatus1 == 'inactive'){
 LOGDEBUG( "MotionTalkNow... Sensor Inactive - Configured to alert on inactive motion sensor")
    
    if(state.msgType == "Voice Message"){
    talkSwitch()
   }          
  
      
  else if(state.msgType == "SMS/Push Message"){
	def msg = message1
LOGDEBUG("MotionTalkNow - SMS/Push Message - Sending Message: $msg")
  sendMessage(msg)
	}
}



}


// Open Too Long
def tooLongOpen(evt){
state.deviceVar = openSensor
state.openContact = evt.value
state.nameOfDevice = evt.displayName
state.actionEvent = evt.value
if (state.openContact == 'open' && state.appgo == true && state.presenceRestriction == true && state.presenceRestriction1 == true){
LOGDEBUG("tooLongOpen - Contact is open")
openContactTimer1()
}

else if (state.openContact == 'closed'){
LOGDEBUG("tooLongOpen - Contact is closed")
}
 else if(state.appgo == false){
    LOGDEBUG("App disabled by $enableswitch being off")
}

}


def openContactTimer1(){

LOGDEBUG( "tooLongOpen - openContactTimer1 -  Contact is: $state.openContact")
   def mydelayOpen = 60 * opendelay1
   LOGDEBUG( "openContactTimer1 - Checking again after delay: $opendelay1 minute(s)... ")
       runIn(mydelayOpen, openContactSpeak)     
      }
      
      
def openContactSpeak(){
LOGDEBUG( "openContactSpeak -  Contact is: $state.openContact")
state.msg1 = message1
state.msgNow = 'oneNow'


if (state.openContact == 'open'){
     LOGDEBUG( "openContactSpeak -  Still open...")
    
    if(state.msgType == "Voice Message"){
    talkSwitch()
   }          
  
      
  else if(state.msgType == "SMS/Push Message"){
	def msg = message1
LOGDEBUG("tooLongOpen - SMS/Push Message - Sending Message: $msg")
  sendMessage(msg)
	}
  
   
  }
 }

// Mode Change

def modeChangeHandler(evt){
state.modeNow = evt.value
state.actionEvent = evt.value
LOGDEBUG("state.modeNow = $state.modeNow")
 state.msg1 = message1
 LOGDEBUG("state.msg1 = $state.msg1")
	
 
 state.msgNow = 'oneNow'
 
		if (evt.isStateChange){
LOGDEBUG(" State Change - The value of this event is different from its previous value: ${evt.isStateChange()}")
def modeChangedTo = newMode1
		if(state.modeNow == modeChangedTo){
   	LOGDEBUG( "Mode is now $modeChangedTo")
    
 	if(state.msgType == "Voice Message"){    
def mydelay = triggerDelay
checkVolume()
LOGDEBUG("Speaker(s) in use: $speaker set at: $state.volume% - waiting $mydelay seconds before continuing..."  )
runIn(mydelay, talkSwitch)
}
	

if(state.msgType == "SMS/Push Message"){
def msg = message1
LOGDEBUG("Mode Change - SMS/Push Message - Sending Message: $msg")
  sendMessage(msg)
	} 
    

   }
}
}

// Routines
def routineChanged(evt) {
state.newRoutine = evt.displayName
state.nameOfDevice = evt.displayName
state.msg1 = message1
state.msgNow = 'oneNow'
def routineToCheckRun = routine1
LOGDEBUG("state.newRoutine = $state.newRoutine")
  
 LOGDEBUG("state.msg1 = $state.msg1") 
 
 if(state.newRoutine == routineToCheckRun){
 
 	LOGDEBUG( "Routine running: $state.newRoutine")
	if(state.msgType == "Voice Message"){     
def mydelay = triggerDelay
checkVolume()
LOGDEBUG("Speaker(s) in use: $speaker set at: $state.volume% - waiting $mydelay seconds before continuing..."  )
runIn(mydelay, talkSwitch)
}

if(state.msgType == "SMS/Push Message"){
def msg = message1
LOGDEBUG("Routine running - SMS/Push Message - Sending Message: $msg")
  sendMessage(msg)
	} 

 }


   
}


// Define restrictPresenceSensor actions
def restrictPresenceSensorHandler(evt){
state.presencestatus1 = evt.value
LOGDEBUG("state.presencestatus1 = $evt.value")
checkPresence()
checkPresence1()

}


def checkPresence(){
LOGDEBUG("running checkPresence - restrictPresenceSensor = $restrictPresenceSensor")

if(restrictPresenceSensor){
LOGDEBUG("Presence = $state.presencestatus1")
def actionPresenceRestrict = restrictPresenceAction


if (state.presencestatus1 == "present" && actionPresenceRestrict == true){
LOGDEBUG("Presence ok")
state.presenceRestriction = true
}
else if (state.presencestatus1 == "not present" && actionPresenceRestrict == true){
LOGDEBUG("Presence not ok")
state.presenceRestriction = false
}

if (state.presencestatus1 == "not present" && actionPresenceRestrict == false){
LOGDEBUG("Presence ok")
state.presenceRestriction = true
}
else if (state.presencestatus1 == "present" && actionPresenceRestrict == false){
LOGDEBUG("Presence not ok")
state.presenceRestriction = false
}
}
else if(!restrictPresenceSensor){
state.presenceRestriction = true
LOGDEBUG("Presence sensor restriction not used")
}
}


def restrictPresence1SensorHandler(evt){
state.presencestatus2 = evt.value
LOGDEBUG("state.presencestatus2 = $evt.value")
checkPresence1()


}


def checkPresence1(){
LOGDEBUG("running checkPresence1 - restrictPresenceSensor1 = $restrictPresenceSensor1")

if(restrictPresenceSensor1){
LOGDEBUG("Presence = $state.presencestatus1")
def actionPresenceRestrict1 = restrictPresenceAction1


if (state.presencestatus2 == "present" && actionPresenceRestrict1 == true){
LOGDEBUG("Presence 2 ok")
state.presenceRestriction1 = true
}
else if (state.presencestatus2 == "not present" && actionPresenceRestrict1 == true){
LOGDEBUG("Presence 2 not ok")
state.presenceRestriction1 = false
}

if (state.presencestatus2 == "not present" && actionPresenceRestrict1 == false){
LOGDEBUG("Presence 2 ok")
state.presenceRestriction1 = true
}
else if (state.presencestatus2 == "present" && actionPresenceRestrict1 == false){
LOGDEBUG("Presence 2 not ok")
state.presenceRestriction1 = false
}
}
else if(!restrictPresenceSensor1){
state.presenceRestriction1 = true
LOGDEBUG("Presence sensor 2 restriction not used")
}
}





// define debug action
def logCheck(){
state.checkLog = debugMode
if(state.checkLog == true){
log.info "All Logging Enabled"
}
else if(state.checkLog == false){
log.info "Further Logging Disabled"
}

}
def LOGDEBUG(txt){
    try {
    	if (settings.debugMode) { log.debug("${app.label.replace(" ","_").toUpperCase()}  (Childapp Version: ${state.appversion}) - ${txt}") }
    } catch(ex) {
    	log.error("LOGDEBUG unable to output requested data!")
    }
}



// Enable Switch

def switchRunCheck(){
if(enableSwitch){
def switchstate = enableSwitch.currentValue('switch')
LOGDEBUG("Enable switch is used. Switch is: $enableSwitch ")

if(switchstate == 'on'){
state.appgo = true
LOGDEBUG("$enableSwitch - Switch State = $switchstate - Appgo = $state.appgo")
}
else if(switchstate == 'off'){
state.appgo = false
LOGDEBUG("$enableSwitch - Switch State = $switchstate - Appgo = $state.appgo")
}
}


if(!enableSwitch){
LOGDEBUG("Enable switch is NOT used. Switch is: $enableSwitch ")
state.appgo = true
LOGDEBUG("AppGo = $state.appgo")
}
}

def switchEnable(evt){
state.sEnable = evt.value
LOGDEBUG("$enableSwitch = $state.sEnable")
if(state.sEnable == 'on'){
state.appgo = true
LOGDEBUG("AppGo = $state.appgo")
}
else if(state.sEnable == 'off'){
state.appgo = false
LOGDEBUG("AppGo = $state.appgo")
}
}


// Time
def timeTalkNow(evt){
checkTimeMissedNow()
checkPresence()
checkPresence1()
checkDay()
state.timeOK = true

LOGDEBUG("state.appgo = $state.appgo - state.dayCheck = $state.dayCheck - state.volume = $state.volume - runTime = $runTime")
if(state.appgo == true && state.dayCheck == true && state.presenceRestriction == true && state.presenceRestriction1 == true){
LOGDEBUG("Time trigger -  Activating now! ")

if(state.msgType == "Voice Message"){ 
def msg = messageTime
checkVolume()
LOGDEBUG( "Speaker(s) in use: $speaker set at: $state.volume% - Message to play: $msg"  )
LOGDEBUG("Calling.. CompileMsg")
compileMsg(msg)
LOGDEBUG("All OK! - Playing message: '$state.fullPhrase'")
speaker.speak(state.fullPhrase)
}

if(state.msgType == "SMS/Push Message"){
def msg = messageTime
LOGDEBUG("Time - SMS/Push Message - Sending Message: $msg")
  sendMessage(msg)
	} 
    

}

else if(state.appgo == false){
LOGDEBUG( "$enableSwitch is off so cannot continue")
}
else if(state.dayCheck == false){
LOGDEBUG( "Cannot continue - Daycheck failed")
}
else if(state.presenceRestriction ==  false){
LOGDEBUG( "Cannot continue - Presence failed")
}

}


// Time if Contact Open
def contact1Handler (evt) {
state.contact1SW = evt.value 
state.nameOfDevice = evt.displayName
state.actionEvent = evt.value
LOGDEBUG( "$contact1 = $evt.value")
						 }



def timeTalkNow1(evt){
checkDay()
checkPresence()
checkPresence1()

LOGDEBUG("state.appgo = $state.appgo - state.dayCheck = $state.dayCheck - state.volume = $state.volume - runTime = $runTime")
if(state.appgo == true && state.dayCheck == true && state.presenceRestriction == true && state.presenceRestriction1 == true && state.contact1SW == 'open' ){
LOGDEBUG("Time trigger -  Activating now! ")

if(state.msgType == "Voice Message"){ 
def msg = messageTime
checkVolume()
LOGDEBUG( "Speaker(s) in use: $speaker set at: $state.volume% - Message to play: $msg"  )
LOGDEBUG("Calling.. CompileMsg")
compileMsg(msg)
LOGDEBUG("All OK! - Playing message: '$state.fullPhrase'")
speaker.speak(state.fullPhrase)

}

if(state.msgType == "SMS/Push Message"){
def msg = messageTime
LOGDEBUG("Time - SMS/Push Message - Sending Message: $msg")
  sendMessage(msg)
	} 

}

else if(state.appgo == false){
LOGDEBUG( "$enableSwitch is off so cannot continue")
}
else if(state.dayCheck == false){
LOGDEBUG( "Cannot continue - Daycheck failed")
}
else if(state.presenceRestriction ==  false){
LOGDEBUG( "Cannot continue - Presence failed")
}

else if(state.contact1SW != 'open'){
LOGDEBUG( "Cannot continue - $contact1 is Closed")
}


}




// Switch
def switchTalkNow(evt){
state.talkswitch1 = evt.value
state.nameOfDevice = evt.displayName
state.actionEvent = evt.value
state.msg1 = message1
state.msg2 = message2
def mydelay = triggerDelay

if(state.msgType == "Voice Message"){
LOGDEBUG("Switch - Voice Message - $state.nameOfDevice")

	if(state.talkswitch1 == 'on'){
state.msgNow = 'oneNow'
    }

	else if (state.talkswitch1 == 'off'){
state.msgNow = 'twoNow'
	}

LOGDEBUG( "$switch1 is $state.talkswitch1")

checkVolume()
LOGDEBUG("Speaker(s) in use: $speaker set at: $state.volume% - waiting $mydelay seconds before continuing..."  )

runIn(mydelay, talkSwitch)
}

if(state.msgType == "SMS/Push Message"){
LOGDEBUG("Switch - SMS/Push Message")
	if(state.talkswitch1 == 'on' && state.msg1 != null){
def msg = message1
LOGDEBUG("Switch - SMS/Push Message - Sending Message: $msg - $state.nameOfDevice")
  sendMessage(msg)
    }
    
    
    if(state.talkswitch1 == 'off' && state.msg2 != null){
def msg = message2
LOGDEBUG("Switch - SMS/Push Message - Sending Message: $msg")
  sendMessage(msg)
    }

}

}



// Contact
def contactTalkNow(evt){
state.talkcontact = evt.value
state.nameOfDevice = evt.displayName
state.actionEvent = evt.value
state.msg1 = message1
state.msg2 = message2

if(state.msgType == "Voice Message"){
	if(state.talkcontact == 'open'){
state.msgNow = 'oneNow'
}
	else if (state.talkcontact == 'closed'){
state.msgNow = 'twoNow'
}

LOGDEBUG("$contactSensor is $state.talkcontact")
def mydelay = triggerDelay
checkVolume()
LOGDEBUG( "Speaker(s) in use: $speaker set at: $state.volume% - waiting $mydelay seconds before continuing..."  )

runIn(mydelay, talkSwitch)
}

if(state.msgType == "SMS/Push Message"){
	if(state.talkcontact == 'open' && state.msg1 != null){
def msg = message1
LOGDEBUG("Contact - SMS/Push Message - Sending Message: $msg")
  sendMessage(msg)

}

	else if (state.talkcontact == 'closed' && state.msg2 != null){
def msg = message2
LOGDEBUG("Contact - SMS/Push Message - Sending Message: $msg")
  sendMessage(msg)

}


	}
}






// Water
def waterTalkNow(evt){
state.talkwater = evt.value
state.nameOfDevice = evt.displayName
state.actionEvent = evt.value
state.msg1 = message1
state.msg2 = message2


if(state.msgType == "Voice Message"){
        
	if(state.talkwater == 'wet'){
state.msgNow = 'oneNow'
	}
	else if (state.talkwater == 'dry'){
state.msgNow = 'twoNow'
	}

LOGDEBUG( "$water1 is $state.talkwater")
def mydelay = triggerDelay
checkVolume()
LOGDEBUG( "Speaker(s) in use: $speaker set at: $state.volume% - waiting $mydelay seconds before continuing..."  )
runIn(mydelay, talkSwitch)
	}
    
if(state.msgType == "SMS/Push Message"){
	if(state.talkwater == 'wet' && state.msg1 != null){
def msg = message1
LOGDEBUG("Water - SMS/Push Message - Sending Message: $msg")
  sendMessage(msg)

}

	else if(state.talkwater == 'dry' && state.msg2 != null){
def msg = message2
LOGDEBUG("Water - SMS/Push Message - Sending Message: $msg")
  sendMessage(msg)

}    
}    
    
    
}

// Presence
def presenceTalkNow(evt){
state.talkpresence = evt.value
state.nameOfDevice = evt.displayName
state.actionEvent = evt.value
state.msg1 = message1
state.msg2 = message2

if(state.msgType == "Voice Message"){

	if(state.talkpresence == 'present'){
state.msgNow = 'oneNow'
	}

	else if (state.talkpresence == 'not present'){
state.msgNow = 'twoNow'
	}

LOGDEBUG( "$presenceSensor1 is $state.talkpresence")
def mydelay = triggerDelay
checkVolume()
LOGDEBUG("Speaker(s) in use: $speaker set at: $state.volume% - waiting $mydelay seconds before continuing..."  )
runIn(mydelay, talkSwitch)
}


if(state.msgType == "SMS/Push Message"){
	if(state.talkpresence == 'present' && state.msg1 != null){
def msg = message1
LOGDEBUG("Presence - SMS/Push Message - Sending Message: $msg")
  sendMessage(msg)

}

	else if (state.talkpresence == 'not present' && state.msg2 != null){
def msg = message2
LOGDEBUG("Presence - SMS/Push Message - Sending Message: $msg")
  sendMessage(msg)

}    
} 





}


// Power 
def powerTalkNow (evt){
state.meterValue = evt.value as double
state.nameOfDevice = evt.displayName
state.actionEvent = evt.value as double
	LOGDEBUG("$powerSensor shows $state.meterValue Watts")
    if(state.appgo == true){
	checkNow1()  
	}
    else if(state.appgo == false){
    LOGDEBUG("App disabled by $enableswitch being off")

}
}
def checkNow1(){
if( actionType1 == false){
LOGDEBUG( "checkNow1 -  Power is: $state.meterValue")
    state.belowValue = belowThreshold as int
    if (state.meterValue < state.belowValue) {
   def mydelay = 60 * delay1 
   LOGDEBUG( "Checking again after delay: $delay1 minutes... Power is: $state.meterValue")
       runIn(mydelay, checkAgain1, [overwrite: false])     
      }
      }
      
else if( actionType1 == true){
LOGDEBUG( "checkNow1 -  Power is: $state.meterValue")
    state.belowValue = belowThreshold as int
    if (state.meterValue > state.belowValue) {
   def mydelay = 60 * delay1
   LOGDEBUG( "Checking again after delay: $delay1 minutes... Power is: $state.meterValue")
       runIn(mydelay, checkAgain2, [overwrite: false])     
      }
      }
  }

 

def checkAgain1() {
   
     if (state.meterValue < state.belowValue) {
      LOGDEBUG( " checkAgain1 - Checking again now... Power is: $state.meterValue")
    
      speakNow()
        
			}
     else  if (state.meterValue > state.belowValue) {
     LOGDEBUG( "checkAgain1 -  Power is: $state.meterValue so cannot run yet...")
	}	
}		

def checkAgain2() {
   
     if (state.meterValue > state.belowValue) {
      LOGDEBUG( "checkAgain2 - Checking again now... Power is: $state.meterValue")
    
      speakNow()
        
			}
     else  if (state.meterValue < state.belowValue) {
     LOGDEBUG( "checkAgain2 -  Power is: $state.meterValue so cannot run yet...")
	}	
}		



def speakNow(){
LOGDEBUG("Power - speakNow...")
checkPresence()
checkPresence1()
state.msg1 = message1
    if ( state.timer1 == true && state.presenceRestriction == true && state.presenceRestriction1 == true){
  if(state.msgType == "Voice Message"){
  checkVolume()
	LOGDEBUG("Calling.. CompileMsg")
compileMsg(state.msg1)
LOGDEBUG("All OK! - Playing message: '$state.fullPhrase'")
speaker.speak(state.fullPhrase)
   	startTimerPower()  
    }
   if(state.msgType == "SMS/Push Message" && state.msg1 != null){
def msg = message1
LOGDEBUG("Power - SMS/Push Message - Sending Message: $msg")
  sendMessage(msg)
startTimerPower()
 
} 
}    
  if(state.presenceRestriction ==  false || state.presenceRestriction1 ==  false){
LOGDEBUG( "Cannot continue - Presence failed")
}
}


def startTimerPower(){
state.timer1 = false
state.timeDelay = 60 * msgDelay
LOGDEBUG("Waiting for $msgDelay minutes before resetting timer to allow further messages")
runIn(state.timeDelay, resetTimerPower)
}

def resetTimerPower() {
state.timer1 = true
LOGDEBUG( "Timer reset - Messages allowed")
}











// Talk now....

def talkSwitch(){
LOGDEBUG("Calling.. talkSwitch")
if(state.appgo == true){
LOGDEBUG("Calling.. CheckTime")
checkTime()
LOGDEBUG("Calling.. CheckDay")
checkDay()
LOGDEBUG("Calling.. CheckPresence")
checkPresence()
LOGDEBUG("Calling.. CheckPresence1")
checkPresence1()

LOGDEBUG("state.appgo = $state.appgo - state.timeOK = $state.timeOK - state.dayCheck = $state.dayCheck - state.timer1 = $state.timer1 - state.timer2 = $state.timer2 - state.volume = $state.volume state.presenceRestriction = $state.presenceRestriction")
if(state.timeOK == true && state.dayCheck == true && state.presenceRestriction == true && state.presenceRestriction1 == true){

LOGDEBUG( " Continue... Check delay...")

if(state.msgNow == 'oneNow' && state.timer1 == true && state.msg1 != null){
LOGDEBUG("Calling.. CompileMsg")
compileMsg(state.msg1)
LOGDEBUG("All OK! - Playing message 1: '$state.fullPhrase'")
speaker.speak(state.fullPhrase)
startTimer1()
}
else if(state.msgNow == 'twoNow'  && state.msg2 != null && state.timer2 == true){
LOGDEBUG("Calling.. CompileMsg")
compileMsg(state.msg2)
LOGDEBUG("All OK! - Playing message 2: '$state.fullPhrase'")
speaker.speak(state.fullPhrase)
startTimer2()
}


else if(state.timeOK == false){
LOGDEBUG("Not enabled for this time so cannot continue")
}
else if(state.presenceRestriction ==  false || state.presenceRestriction1 ==  false){
LOGDEBUG( "Cannot continue - Presence failed")
}

else if(state.msgNow == 'oneNow' && state.msg1 == null){
LOGDEBUG( "Message 1 is empty so nothing to say")
}
else if(state.msgNow == 'twoNow' && state.msg2 == null){
LOGDEBUG( "Message 2 is empty so nothing to say")
}
}
}
else if(state.appgo == false){
LOGDEBUG("$enableSwitch is off so cannot continue")
}

}

def checkVolume(){
def timecheck = fromTime2
if (timecheck != null){
def between2 = timeOfDayIsBetween(fromTime2, toTime2, new Date(), location.timeZone)
    if (between2) {
    
    state.volume = volume2
   speaker.setLevel(state.volume)
    
   LOGDEBUG("Quiet Time = Yes - Setting Quiet time volume")
    
}
else if (!between2) {
state.volume = volume1
LOGDEBUG("Quiet Time = No - Setting Normal time volume")

speaker.setLevel(state.volume)
 
	}
}
else if (timecheck == null){

state.volume = volume1
speaker.setLevel(state.volume)

	}
 
}
// Message Actions ==================================


def sendMessage(msg) {

LOGDEBUG("Calling.. sendMessage")
compileMsg(msg)
if(state.appgo == true){
LOGDEBUG("Calling.. CheckTime")
checkTime()
LOGDEBUG("Calling.. CheckDay")
checkDay()
LOGDEBUG("Calling.. CheckPresence")
checkPresence()
LOGDEBUG("Calling.. CheckPresence1")
checkPresence1()

def mydelay = triggerDelay
LOGDEBUG("Waiting $mydelay seconds before sending")
runIn(mydelay, pushNow)
}
}

def pushNow(){

LOGDEBUG("state.appgo = $state.appgo - state.timeOK = $state.timeOK - state.dayCheck = $state.dayCheck - state.timer1 = $state.timer1")
if(state.timeOK == true && state.dayCheck == true && state.presenceRestriction == true && state.presenceRestriction1 == true && state.timer1 == true){

log.trace "SendMessage - $state.fullPhrase"
    if (location.contactBookEnabled) {
        sendNotificationToContacts(state.fullPhrase, recipients)
        startTimer1()
    }
    else {
        if (sms) {
            sendSms(sms, state.fullPhrase)
            startTimer1()
        }
        if (pushNotification) {
            sendPush(state.fullPhrase)
            startTimer1()
        }
    }
}
}







// Check time allowed to run...

def checkTime(){
def timecheckNow = fromTime
if (timecheckNow != null){
def between = timeOfDayIsBetween(fromTime, toTime, new Date(), location.timeZone)
    if (between) {
    state.timeOK = true
   LOGDEBUG("Time is ok so can continue...")
    
}
else if (!between) {
state.timeOK = false
LOGDEBUG("Time is NOT ok so cannot continue...")
	}
  }
else if (timecheckNow == null){  
state.timeOK = true
  LOGDEBUG("Time restrictions have not been configured -  Continue...")
  }
}


// check days allowed to run
def checkDay(){
def daycheckNow = days
if (daycheckNow != null){
 def df = new java.text.SimpleDateFormat("EEEE")
    
    df.setTimeZone(location.timeZone)
    def day = df.format(new Date())
    def dayCheck1 = days.contains(day)
    if (dayCheck1) {

  state.dayCheck = true
LOGDEBUG( " Day ok so can continue...")
 }       
 else {
LOGDEBUG( " Not today!")
 state.dayCheck = false
 }
 }
if (daycheckNow == null){ 
 LOGDEBUG("Day restrictions have not been configured -  Continue...")
 state.dayCheck = true 
} 
}


 // Delay between messages...

def startTimer1(){
state.timer1 = false
state.timeDelay = 60 * msgDelay
LOGDEBUG("Waiting for $state.timeDelay seconds before resetting timer1 to allow further messages")
runIn(state.timeDelay, resetTimer1)
}

def startTimer2(){
state.timer2 = false
state.timeDelay = 60 * msgDelay
LOGDEBUG( "Waiting for $state.timeDelay seconds before resetting timer2 to allow further messages")
runIn(state.timeDelay, resetTimer2)
}

def resetTimer1() {
state.timer1 = true
LOGDEBUG( "Timer 1 reset - Messages allowed")
}
def resetTimer2() {
state.timer2 = true
LOGDEBUG("Timer 2 reset - Messages allowed")
}


private getWeatherReport() {
	if (location.timeZone || zipCode) {
		def isMetric = location.temperatureScale == "C"
        def sb = new StringBuilder()
      	def weather = getWeatherFeature("forecast", zipCode)

			if (isMetric) {
        		sb << weather.forecast.txt_forecast.forecastday[0].fcttext_metric 
        	}
        	else {
          		sb << weather.forecast.txt_forecast.forecastday[0].fcttext
        	}
        
        
		def msgWeather = sb.toString()
        msgWeather = msgWeather.replaceAll(/([0-9]+)C/,'$1 degrees')
        msgWeather = msgWeather.replaceAll(/([0-9]+)F/,'$1 degrees')
        
    return msgWeather
   
   
	}
	else {
		msgWeather = "Please set the location of your hub with the SmartThings mobile app, or enter a zip code to receive weather forecasts."
	
    }
}

private compileMsg(msg) {
	LOGDEBUG("compileMsg - msg = ${msg}")
    def msgComp = ""
    msgComp = msg.toUpperCase()
    LOGDEBUG("msgComp = $msgComp")
    
    if (msgComp.contains("%TIME%")) {msgComp = msgComp.toUpperCase().replace('%TIME%', getTime(false,true))}  
    if (msgComp.contains("%DAY%")) {msgComp = msgComp.toUpperCase().replace('%DAY%', getDay() )}  
	if (msgComp.contains("%DATE%")) {msgComp = msgComp.toUpperCase().replace('%DATE%', getdate() )}  
    if (msgComp.contains("%YEAR%")) {msgComp = msgComp.toUpperCase().replace('%YEAR%', getyear() )}  
    if (msgComp.contains("%WAKEUP%")) {msgComp = msgComp.toUpperCase().replace('%WAKEUP%', getWakeUp() )}
    if (msgComp.contains("%WEATHER%")) {msgComp = msgComp.toUpperCase().replace('%WEATHER%', getWeatherReport() )}  
	if (msgComp.contains("%OPENCONTACT%")) {msgComp = msgComp.toUpperCase().replace('%OPENCONTACT%', getContactReport() )}  
	if (msgComp.contains("%DEVICE%")) {msgComp = msgComp.toUpperCase().replace('%DEVICE%', getNameofDevice() )}  
	if (msgComp.contains("%EVENT%")) {msgComp = msgComp.toUpperCase().replace('%EVENT%', getWhatHappened() )}  
    if (msgComp.contains("%GREETING%")) {msgComp = msgComp.toUpperCase().replace('%GREETING%', getGreeting() )}      
    if (msgComp.contains("%PRE%")) {msgComp = msgComp.toUpperCase().replace('%PRE%', getPre() )}
    if (msgComp.contains("%POST%")) {msgComp = msgComp.toUpperCase().replace('%POST%', getPost() )}


    
    convertWeatherMessage(msgComp)
  
    
}


// Message variables ***************************************************





// Random message processing ************************************


// 'Pre' random message processing
private getPre(){

def preAnswer = [ 
'1': "Hey!",
'2': "I thought you might like to know ,,, ",
'3': "I'm sorry to disturb you. ,,, but  ",
'4': "Please excuse me! ,,, but I thought you might like to know ,,, ",
'5': " I'm sorry to disturb you. ,,, but I thought you might like to know ,,, ",
'6': "Hey! ,,, I thought you might like to know ,,, ",
'7': "Information ,,, "
]

def random1 = new Random()
def randomKey1 = (preAnswer.keySet() as List)[random1.nextInt(preAnswer.size())]
 
def msgPre =  "${preAnswer[randomKey1]}"
LOGDEBUG("msgPre = $msgPre")

return msgPre
}

// 'Post' random message processing
private getPost(){
def postAnswer = [
'1': "I'm telling you this ,,, because I just thought you might like to know!",
'2': "I just thought you might like to know this",
'3': "I just thought you might like to know this, that's all!"
]

def random2 = new Random()
def randomKey2 = (postAnswer.keySet() as List)[random2.nextInt(postAnswer.size())]
 
def msgPost =  "${postAnswer[randomKey2]}"
LOGDEBUG("msgPost = $msgPost")

return msgPost
}

// 'WakeUp' random message processing
private getWakeUp(){
def wakeAnswer = [
'1': "It's time to wake up! ,,, ",
'2': "Please Wake Up! ,,,",
'3': "You don't want to waste the day. ,,, do you? ,,, ",
'4': "You don't want to sleep all day. ,,, do you? ,,, ",
'5': "Get out of bed! ,,, NOW! ,,, ",
'6': "Come on! it's time to get up! ,,, "
]

def random3 = new Random()
def randomKey3 = (wakeAnswer.keySet() as List)[random3.nextInt(wakeAnswer.size())]
 
def msgWake =  "${wakeAnswer[randomKey3]}"
LOGDEBUG("msgWake = $msgWake")

return msgWake


}

// End random message processing ************************************



// 'Greeting' message processing
private getGreeting(){
    def calendar = Calendar.getInstance()
	calendar.setTimeZone(location.timeZone)
	def timeHH = calendar.get(Calendar.HOUR) toInteger() // changed from toString() to toInteger()  ***********************************
    def timeampm = calendar.get(Calendar.AM_PM) ? "pm" : "am" 
    
LOGDEBUG("timeHH = $timeHH")
if(timeampm == 'am'){
state.greeting = "GOOD MORNING"
}

else if(timeampm == 'pm' && timeHH < 6){
state.greeting = "GOOD AFTERNOON"
LOGDEBUG("timeampm = $timeampm - timehh = $timeHH")
}

else if(timeampm == 'pm' && timeHH >= 6){
LOGDEBUG("timehh = $timeHH - timeampm = $timeampm")

state.greeting = "GOOD EVENING"
} 

LOGDEBUG("Greeting = $state.greeting")
return state.greeting
}





private getWhatHappened(){
LOGDEBUG("Event = $state.actionEvent")
return state.actionEvent

}

private getNameofDevice(){
LOGDEBUG("Device = $state.nameOfDevice")
return state.nameOfDevice

}

private getContactReport(){
LOGDEBUG("Calling getContactReport")

def open = sensors.findAll { it?.latestValue("contact") == 'open' }
		if (open) { 
LOGDEBUG("Open windows or doors: ${open.join(',,, ')}")
           def anyOpen = "${open.join(',,, ')}"
            
return anyOpen
	}
}


private convertWeatherMessage(msgIn){

LOGDEBUG("Running convertWeatherMessage... Converting weather message to English (If weather requested)...")

    def msgOut = ""
    msgOut = msgIn.toUpperCase()
    
// Weather Variables    

    msgOut = msgOut.replace(" N ", " North ")
    msgOut = msgOut.replace(" S ", " South ")
    msgOut = msgOut.replace(" E ", " East ")
    msgOut = msgOut.replace(" W ", " West ")
    msgOut = msgOut.replace(" NE ", " Northeast ")
    msgOut = msgOut.replace(" NW ", " Northwest ")
    msgOut = msgOut.replace(" SE ", " Southeast ")
    msgOut = msgOut.replace(" SW ", " Southwest ")
    msgOut = msgOut.replace(" NNE ", " North Northeast ")
    msgOut = msgOut.replace(" NNW ", " North Northwest ")
    msgOut = msgOut.replace(" SSE ", " South Southeast ")
    msgOut = msgOut.replace(" SSW ", " South Southwest ")
    msgOut = msgOut.replace(" ENE ", " East Northeast ")
    msgOut = msgOut.replace(" ESE ", " East Southeast ")
    msgOut = msgOut.replace(" WNW ", " West Northeast ")
    msgOut = msgOut.replace(" WSW ", " West Southwest ")
    msgOut = msgOut.replace(" MPH", " Miles Per Hour")
    msgOut = msgOut.replace(" PRECIP", " PRECIPITATION")
    
   state.fullPhrase = msgOut

  return state.fullPhrase
  
  
}




private getTime(includeSeconds, includeAmPm){
    def calendar = Calendar.getInstance()
	calendar.setTimeZone(location.timeZone)
	def timeHH = calendar.get(Calendar.HOUR) toString()
    def timemm = calendar.get(Calendar.MINUTE) toString()
    def timess = calendar.get(Calendar.SECOND)
    def timeampm = calendar.get(Calendar.AM_PM) ? "pm" : "am" 
    
LOGDEBUG("timeHH = $timeHH")
 
 if (timeHH == "0") {timeHH = timeHH.replace("0", "12")}   //  Changes hours so it doesn't say 0 for 12 midday/midnight
 if (timeHH == "10") {timeHH = timeHH.replace("10", "TEN")}   //  Changes 10 to TEN as there seems to be an issue with it saying 100 for 10 o'clock
  
 if (hour24 == true){ // Convert to 24hr clock if selected
LOGDEBUG("hour24 = $hour24 -  So converting hours to 24hr format")
     
 if (timeHH == "1" && timeampm.contains ("pm")){timeHH = timeHH.replace("1", "13")}
 if (timeHH == "2" && timeampm.contains ("pm")){timeHH = timeHH.replace("2", "14")}
 if (timeHH == "3" && timeampm.contains ("pm")){timeHH = timeHH.replace("3", "15")}
 if (timeHH == "4" && timeampm.contains ("pm")){timeHH = timeHH.replace("4", "16")}
 if (timeHH == "5" && timeampm.contains ("pm")){timeHH = timeHH.replace("5", "17")}
 if (timeHH == "6" && timeampm.contains ("pm")){timeHH = timeHH.replace("6", "18")}
 if (timeHH == "7" && timeampm.contains ("pm")){timeHH = timeHH.replace("7", "19")}
 if (timeHH == "8" && timeampm.contains ("pm")){timeHH = timeHH.replace("8", "20")}
 if (timeHH == "9" && timeampm.contains ("pm")){timeHH = timeHH.replace("9", "21")}
 if (timeHH == "10" && timeampm.contains ("pm")){timeHH = timeHH.replace("10", "22")}
 if (timeHH == "11" && timeampm.contains ("pm")){timeHH = timeHH.replace("11", "23")}
 timeampm = timeampm.replace("pm", " ")
  if (timemm == "0") {
     LOGDEBUG("timemm = 0  - So changing to 'hundred hours")
     timemm = timemm.replace("0", " hundred hours")
    	  if(timeampm.contains ("pm")){timeampm = timeampm.replace("pm", " ")}
     else if(timeampm.contains ("am")){timeampm = timeampm.replace("am", " ")}
      }
 }
 
     if (timemm == "0" && hour24 == false) {
     LOGDEBUG("timemm = 0  - So changing to o'clock")
     timemm = timemm.replace("0", "o'clock")
    	  if(timeampm.contains ("pm")){timeampm = timeampm.replace("pm", " ")}
     else if(timeampm.contains ("am")){timeampm = timeampm.replace("am", " ")}
      }
      
else if (timemm == "1") {timemm = timemm.replace("1", "01")}
else if (timemm == "2") {timemm = timemm.replace("2", "02")}
else if (timemm == "3") {timemm = timemm.replace("3", "03")}
else if (timemm == "4") {timemm = timemm.replace("4", "04")}  
else if (timemm == "5") {timemm = timemm.replace("5", "05")}  
else if (timemm == "6") {timemm = timemm.replace("6", "06")}  
else if (timemm == "7") {timemm = timemm.replace("7", "07")}  
else if (timemm == "8") {timemm = timemm.replace("8", "08")}  
else if (timemm == "9") {timemm = timemm.replace("9", "09")}  

         
 
 
 
 
 
 LOGDEBUG("timeHH Now = $timeHH")
    def timestring = "${timeHH} ${timemm}"
    if (includeSeconds) { timestring += ":${timess}" }
    if (includeAmPm) { timestring += " ${timeampm}" }
   LOGDEBUG("timestring = $timestring")
    
    return timestring
}

private getDay(){
	def df = new java.text.SimpleDateFormat("EEEE")
	if (location.timeZone) {
		df.setTimeZone(location.timeZone)
	}
	else {
		df.setTimeZone(TimeZone.getTimeZone("America/New_York"))
	}
	def day = df.format(new Date())
    
    return day
}

private parseDate(date, epoch, type){
    def parseDate = ""
    if (epoch){
    	long longDate = Long.valueOf(epoch).longValue()
        parseDate = new Date(longDate).format("yyyy-MM-dd'T'HH:mm:ss.SSSZ", location.timeZone)
    }
    else {
    	parseDate = date
    }
    new Date().parse("yyyy-MM-dd'T'HH:mm:ss.SSSZ", parseDate).format("${type}", timeZone(parseDate))
}
private getdate() {
    def month = parseDate("", now(), "MMMM")
    def dayNum = parseDate("", now(), "dd")
  LOGDEBUG("Date:  $dayNum $month")
    
    LOGDEBUG("dayNum = $dayNum - Converting into 'proper' English")
    if(dayNum == "01"){dayNum = dayNum.replace("01","THE FIRST OF")}
	if(dayNum == "02"){dayNum = dayNum.replace("02","THE SECOND OF")}
    if(dayNum == "03"){dayNum = dayNum.replace("03","THE THIRD OF")}
    if(dayNum == "04"){dayNum = dayNum.replace("04","THE FOURTH OF")}
    if(dayNum == "05"){dayNum = dayNum.replace("05","THE FIFTH OF")}
    if(dayNum == "06"){dayNum = dayNum.replace("06","THE SIXTH OF")}
    if(dayNum == "07"){dayNum = dayNum.replace("07","THE SEVENTH OF")}
    if(dayNum == "08"){dayNum = dayNum.replace("08","THE EIGHTH OF")}
    if(dayNum == "09"){dayNum = dayNum.replace("09","THE NINTH OF")}
    if(dayNum == "10"){dayNum = dayNum.replace("10","THE TENTH OF")}
    if(dayNum == "11"){dayNum = dayNum.replace("11","THE ELEVENTH OF")}
    if(dayNum == "12"){dayNum = dayNum.replace("12","THE TWELTH OF")}
    if(dayNum == "13"){dayNum = dayNum.replace("13","THE THIRTEENTH OF")}
    if(dayNum == "14"){dayNum = dayNum.replace("14","THE FOURTEENTH OF")}
    if(dayNum == "15"){dayNum = dayNum.replace("15","THE FIFTEENTH OF")}
    if(dayNum == "16"){dayNum = dayNum.replace("16","THE SIXTEENTH OF")}
    if(dayNum == "17"){dayNum = dayNum.replace("17","THE SEVENTEENTH OF")}
    if(dayNum == "18"){dayNum = dayNum.replace("18","THE EIGHTEENTH OF")}
    if(dayNum == "19"){dayNum = dayNum.replace("19","THE NINETEENTH OF")}
    if(dayNum == "20"){dayNum = dayNum.replace("20","THE TWENTIETH OF")}
    if(dayNum == "21"){dayNum = dayNum.replace("21","THE TWENTY FIRST OF")}
    if(dayNum == "22"){dayNum = dayNum.replace("22","THE TWENTY SECOND OF")} 
    if(dayNum == "23"){dayNum = dayNum.replace("23","THE TWENTY THIRD OF")}
    if(dayNum == "24"){dayNum = dayNum.replace("24","THE TWENTY FOURTH OF")}
    if(dayNum == "25"){dayNum = dayNum.replace("21","THE TWENTY FIFTH OF")}
    if(dayNum == "26"){dayNum = dayNum.replace("26","THE TWENTY SIXTH OF")}
    if(dayNum == "27"){dayNum = dayNum.replace("27","THE TWENTY SEVENTH OF")}
    if(dayNum == "28"){dayNum = dayNum.replace("28","THE TWENTY EIGHTH OF")}
    if(dayNum == "29"){dayNum = dayNum.replace("29","THE TWENTY NINTH OF")}
    if(dayNum == "30"){dayNum = dayNum.replace("30","THE THIRTIETH OF")}
    if(dayNum == "31"){dayNum = dayNum.replace("21","THE THIRTY FIRST OF")}
     LOGDEBUG("Day number has been converted to: '$dayNum'")  
    
    return dayNum + " " + month + " "
}
private getyear() {
    def year = parseDate("", now(), "yyyy")
	
   LOGDEBUG("Year =  $year")
         
    return year
}





// App Version   *********************************************************************************
def setAppVersion(){
    state.appversion = "3.2.0"
}