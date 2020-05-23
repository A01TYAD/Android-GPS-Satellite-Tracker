# Android-GPS-Satellite-Tracker  
----------------------------------

GPS Satellite Tracker is an android app that tracks the position of GPS Satellites(PRN, SNR, Elevation and Azimuth) around you 
that are used to get your current location. It uses the gps $GPGSV NMEA data to determine the position of GPS satellites.
  
The NMEA 0183 standard uses a simple ASCII, serial communications protocol that defines how data are transmitted in a "sentence" from one "talker" to multiple "listeners" at a time. 
  
  
Example of $GPGSC sentence from https://www.gpsinformation.org/dale/nmea.htm:  
 ``` 
$GPGSV,2,1,04,01,40,083,46,02,17,308,41,12,07,344,39,14,22,228,45*75
Where:   
      GSV          Satellites in view 
      2            Number of sentences for full data
      1            sentence 1 of 3
      04           Number of satellites in view
      01           Satellite PRN number
      40           Elevation, degrees
      083          Azimuth, degrees
      46           SNR - higher is better
      *75          the checksum data, always begins with *
  ```
Screenshot  
------------------------------------
![](http://adhungana.com.np/img/GPSTrack.png)
