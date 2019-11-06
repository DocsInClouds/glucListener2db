# Overview
This application implements a sensorlistener for the Dexcom G5 and G6 and stores the data in a local database. It is based on the Nightscout xDrip+ App (https://github.com/NightscoutFoundation/xDrip), which also holds the GPL3 copyright.

This is optimized to run on newer Samsung devices and might not work correctly on devices from different distributors.

This is no medical software in any way. Do not base medical decisions on the values reported by this tool. We do not take any responsibility for any harm done. 

# Changes
* reduced the xDrip app to necessary classes for data receival and transmitter pairing
* removed all UI from xDrip
* removed calibration related code
* added new MainActivity to start/stop service
* added simple settings screen to configure transmitter id and type
* added own notification
* added an internal database to store the recorded values
* set xDrip listener to always use the buggy-samsung mode

# Install Instruction
To install this application, download Android Studio here: https://developer.android.com/studio/. Connect an Android smartphone to your computer and install the app via Android Studio.