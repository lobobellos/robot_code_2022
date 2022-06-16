# Robot_code_2022

Robot code for the 2022 FRC season

readme updated 6/15/22

# Ports

<h2>PWM Channels</h2>

|motor|port|
|---|---|
|RR Drive|0|
|FR Drive|1|
|Fl Drive|2|
|RL Drive|3|
|Main Yeeter|4|
|Intake|5|
|Top Yeeter|6|
|Climber|7|

<h2>DIO Channels</h2>

|Device|Port|
|---|---|
|Ultrasonic Output|0|
|Ultrasonic Input|1|
|Limit Switch|2|


# To drive

<ol>
    <li>Ensure that FRC game tools is installed</li>
    <li>Ensure that avation joystick is pluged in on port 0</li>
    <li>Turn on the robot (You know what it likes)</li>
    <li>Initiate connection with robot (via WIFI, Ethernet, or USB)</li>
    <li>Open FRC driver station</li>
    <li>Wait for all of the lights to turn green</li>
    <li>If any of them stay red, visit the troubleshooting section </li>
    <li>Press the green enable button, and the robot should be good to go.</li>
</ol>

|Input|What it does|
|---|---|
|Joystick Y-Axis|Moves robot forward|
|Joystick X-Axis|Moves robot sideways|
|Joystick Z-Axis|Rotates robot around it's own axis|
|Joystick trigger|Starts intake|
|Joystick thumb button|Shoots ball|
|Joystick thumb button|Shoots ball|
|Joystick button 4|Deploy Hook|
|Joystick button 5|Retract Hook|

# troubleshooting

<ul>
    <li>Connection error
        <ul>
            <li>Ensure all cords are plugged in</li>
            <li>Try restarting your device</li>
            <li>Try communicating with the robot on 10.72.43.1 and 172.22.11.2</li>
        </ul>
    </li>
    <li>Connection error
        <ul>
            <li>Redeploy robot code</li>
            <li>make sure that firmware is up to date</li>
            <li>Restart robot code in driver station</li>
        </ul>
    </li>
    <li>Joystick error
        <ul>
            <li>Ensure joystick is plugged in</li>
            <li>rescan for joystick in driver station</li>
            <li>Try a different joystick</li>
        </ul>
    </li>
    
</ul>
