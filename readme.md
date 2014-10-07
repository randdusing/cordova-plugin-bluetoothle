Bluetooth LE PhoneGap Plugin

====================

## Supported platforms ##

* PhoneGap 3.0.0 or higher
* Android 4.3 or higher
* iOS 7 or higher
* Windows Phone 8.1 (Tested on Nokia Lumia 630)
* Device hardware must be certified for Bluetooth LE. i.e. Nexus 7 (2012) doesn't support Bluetooth LE even after upgrading to 4.3 (or higher) without a modification
* List of devices: http://www.bluetooth.com/Pages/Bluetooth-Smart-Devices-List.aspx


## Limitations / Issues ##

* Warning: Phonegap, Android, iOS and Objective C are all very new to me.
* <del>iOS doesn't prompt user to enable Bluetooth if disabled like Android does. It's probably possible, but I just forgot until right before comitting the latest changes.</del> This is now configurable using the "request" property in initialize.
* Tested with a heart rate monitor, so some scenarios especially those involving writing characteristics may not work as I was unable to test it. If you run into an issue, log it and I'll try to fix it. If you let me borrow a device, I can probably fix it even quicker. :)
* Limited to connecting to a single device at a time (Pretty sure it's feasible and not too difficult to implement, but a low priorty for my original project) ** Hope to begin working on this starting in July
* <del>All discovery, read and write operations must be done sequentially. i.e read characteristic x1234, wait for read result, read characteristic x5678, wait for read result, etc. More info on http://stackoverflow.com/questions/18011816/has-native-android-ble-gatt-implementation-synchronous-nature (Eventually queuing could be added, but a low priority for my original project)</del> There's now support for multiple operations. For example, you can write characteristic A or read characteristic B while subscribed to characteristic C.
* <del>No support for Windows Phone currently. **Update: Windows Phone 8.1 supports Bluetooth LE and devices are pretty cheap, so this will be a priority as soon as it's released. Originally planned to buy a Windows 8.1 Phone before moving to Korea, but the one I wanted did come out in time. Still deciding what to do.
* Disconnecting and quickly reconnecting causes issues on Android. The device becomes connected again, but then quickly disconnects. Adding a timeout before reconnecting fixed the issue for me. I'm not sure if this is a problem with the plugin or Android's Bluetooth LE implementation.
* For subscribing, indication hasn't been tested since my heart rate monitor doesn't support it.
* <del>Characteristic properties are not returned during discovery. If anyone requests this, I should be able to add it fairly easily.</del>Charactertistic properties are now returned. See discovery/characteristics method documentation for more info.
* Characteristic and descriptor permissions are not returned during discovery. If anyone requests this, I should be able to add it fairly easily, at least for Android. iOS doesn't appear to use permissions.

## Discovery Android vs iOS ##

Discovery works differently between Android and iOS. In Android, a single function is called to initiate discovery of all services, characteristics and descriptors on the device. In iOS, a single function is called to discover the device's services. Then another function to discover the characteristics of a particular service. And then another function to discover the descriptors of a particular characteristic. The Device plugin (http://docs.phonegap.com/en/edge/cordova_device_device.md.html#Device) should be used to properly determine the device and make the proper calls if necessary. Additionally, if a device is disconnected, it must be rediscovered when running on iOS.

## UUIDs ##
UUIDs can be 16 bits or 128 bits. The "out of the box" UUIDs from the link below are 16 bits.
Since iOS returns the 16 bit version of the "out of the box" UUIDs even if a 128 bit UUID was used in the parameters, the 16 bit version should always be used for the "out of the box" UUIDs for consistency.
Android on the other hand only uses the 128 bit version, but the plugin will automatically convert 16 bit UUIDs to the 128 bit version on input and output.

https://developer.bluetooth.org/gatt/services/Pages/ServicesHome.aspx

## Advertisement Data / MAC Address ##
On iOS, the MAC address is hidden from the advertisement packet, and the address returned from the scanResult is a generated, device-specific address. This is a problem when using devices like iBeacons where you need the MAC Address. Fortunately the CLBeacon class can be used for this, but unfortunately it's not supported in this plugin.
One option is to set Manufacturer Specific Data in the advertisement packet if that's possible in your project.
Another option is to connect to the device and use the "Device Information" (0x180A) service, but connecting to each device is much more energy intensive than scanning for advertisement data.

Some related links:
https://stackoverflow.com/questions/18973098/get-mac-address-of-bluetooth-low-energy-peripheral
https://stackoverflow.com/questions/22833198/get-advertisement-data-for-ble-in-ios


## Installation ##

If you are using phonegap add the plugin to your app by running the command below:

```phonegap local plugin add https://github.com/randdusing/BluetoothLE```

If you are using apache cordova use this instead:

```cordova plugin add https://github.com/randdusing/BluetoothLE```


Read the documentation below.


## Updating ##

Updating the plugin for iOS causes BluetoothLePlugin.m to be removed from the Compile Sources and CoreBluetooth.framework to be removed from Link Binary with Libraries.
To fix:
- Click your project to open the "properties" window
- Click your target
- Click Build Phases
- Ensure BluetoothLePlugin.m is added to the Compile Sources
- Ensure CoreBluetooth.framework is added to the Link Binary with Libraries


## Methods ##

* bluetoothle.initialize
* bluetoothle.startScan
* bluetoothle.stopScan
* bluetoothle.connect
* bluetoothle.reconnect
* bluetoothle.disconnect
* bluetoothle.close
* bluetoothle.discover (Android only)
* bluetoothle.services (iOS only)
* bluetoothle.characteristics (iOS only)
* bluetoothle.descriptors (iOS only)
* bluetoothle.read
* bluetoothle.subscribe
* bluetoothle.unsubscribe
* bluetoothle.write
* bluetoothle.readDescriptor
* bluetoothle.writeDescriptor
* bluetoothle.rssi
* bluetoothle.isInitialized
* bluetoothle.isEnabled
* bluetoothle.isScanning
* bluetoothle.isConnected
* bluetoothle.isDiscovered (Android only)
* bluetoothle.encodedStringToBytes
* bluetoothle.bytesToEncodedString
* bluetoothle.stringToBytes
* bluetoothle.bytesToString


## Errors ##

Whenever the error callback is executed, the return object will contain the error type and a message.
* initialize - Bluetooth isn't initialized (Try initializing Bluetooth)
* enable - Bluetooth isn't enabled (Request user to enable Bluetooth)
* startScan - Scan couldn't be started (Is the scan already running?)
* stopScan - Scan couldn't be stopped (Is the scan already stopped?)
* connect - Connection attempt failed (Is the device address correct?)
* reconnect - Reconnection attempt failed (Was the device ever connected?)
* discover - Failed to discover device (Is the device already discovered or discovering? Is the device Android?)
* service - Service doesn't exist (Was it discovered? Correct uuid? Is the device iOS?)
* characteristic - Characteristic doesn't exist (Was it discovered? Correct uuid? Is the device iOS?)
* descriptor - Descriptor doesn't exist (Was it discovered? Correct uuid? Is the device iOS?)
* read - Failed to read (Not sure what would cause this)
* subscription - Failed to subscribe or unsubscribe (Does the characteristic have the Client Configuration descriptor?)
* write - Failed to write (Was a write value provided?)
* readDescriptor - Failed to read descriptor (Not sure what would cause this)
* writeDescriptor - Failed to write descriptor (Was a write value provided?)
* rssiError - Failed to read RSSI (Not sure what would cause this)
* arguments - Invalid arguments (Check arguments)
* neverConnected - Device never connected (Call connect, not reconnect)
* isNotDisconnected - Device is not disconnected (Don't call connect, reconnect or close while connected)
* isNotConnected - Device isn't connected (Don't call discover or any read/write operations)
* isDisconnected - Device is disconnected (Don't call disconnect)

For example:
```javascript
{"error":"startScan", "message":"Scanning already started"}
```



## Properties ##
Characteristics can have the following different properties: broadcast, read, writeWithoutResponse, write, notify, indicate, authenticatedSignedWrites, extendedProperties, notifyEncryptionRequired, indicateEncryptionRequired
If the characteristic has a property, it will exist as a key in the characteristic's properties object. See discovery() or characteristics()

https://developer.android.com/reference/android/bluetooth/BluetoothGattCharacteristic.html
https://developer.apple.com/library/mac/documentation/CoreBluetooth/Reference/CBCharacteristic_Class/translated_content/CBCharacteristic.html#//apple_ref/c/tdef/CBCharacteristicProperties



## Life Cycle ##

1. initialize
2. scan (if device address is unknown)
3. connect
4. discover (Android) OR services/characteristics/descriptors (iOS)
5. read/subscribe/write characteristics/descriptors
6. disconnect
7. close


## initialize ##
Initialize Bluetooth on the device. Must be called before anything else. Callback will continuously be used whenever Bluetooth is enabled or disabled. Note: Although Bluetooth initialization could initially be successful, there's no guarantee whether it will stay enabled. Each call checks whether Bluetooth is disabled. If it becomes disabled, the user must connect to the device, start a read/write operation, etc again. If Bluetooth is disabled, you can request the user to enable it by setting the request property to true. The `request` property in the `params` argument is optional and defaults to false. Also, this function should only be called once. But if it's called subsequent times, it will return either status => enabled or error => enable.

```javascript
bluetoothle.initialize(initializeSuccessCallback, initializeErrorCallback, params);
```

##### Params #####
* request = true/false

```javascript
{"request":true};
```

##### Success Return #####
```javascript
{"status":"enabled"};
```



### startScan ###
Scan for Bluetooth LE devices. Since scanning is expensive, stop as soon as possible. The Phonegap app should use a timer to limit the scan interval. Also Android uses an AND operator for filtering, while iOS uses an OR operator for filtering.

```javascript
bluetoothle.startScan(startScanSuccessCallback, startScanErrorCallback, params);
```

##### Params #####
* serviceUuids = An array of service IDs to filter the scan or empty array / null

```javascript
{"serviceUuids":["180D", "180F"]}
```

##### Success Return #####
* scanStarted = Scan has started
* scanResult = Scan has found a device
  * name = the device's display name
  * address = the device's address / identifier for connecting to the object
  * rssi = signal strength
  * advertisement = advertisement data in encoded string of bytes, use bluetoothle.encodedStringToBytes() - Only tested in Android so far!
```javascript
{"status":"scanStarted"};
{"status":"scanResult","address":"01:23:45:67:89:AB","name":"Polar H7","rssi":-5}; /* Android */
{"status":"scanResult","address":"XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX","name":"Polar H7","rssi":-5}; /* iOS */
{"status":"scanResult","address":"0123456789AB","name":"Polar H7"}; /* WP8.1 */
```



### stopScan ###
Stop scan for Bluetooth LE devices. Since scanning is expensive, stop as soon as possible. The Phonegap app should use a timer to limit the scanning time.

```javascript
bluetoothle.stopScan(stopScanSuccessCallback, stopScanErrorCallback);
```

##### Return #####
* scanStop = Scan has stopped
```javascript
{"status":"scanStopped"}
```



### connect ###
Connect to a Bluetooth LE device. The Phonegap app should use a timer to limit the connecting time in case connecting is never successful. Once a device is connected, it may disconnect without user intervention. The original connection callback will be called again and receive an object with status => disconnected. To reconnect to the device, use the reconnect method. Before connecting to a new device, the current device must be disconnected and closed. If a timeout occurs, the connection attempt should be canceled using disconnect().

```javascript
bluetoothle.connect(connectSuccessCallback, connectErrorCallback, params);
```

##### Params #####
* address = The address/identifier provided by the scan's return object

```javascript
{"address":"01:23:45:67:89:AB"} /* Android */
{"address":"XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX"} /* iOS */
```

##### Success Return #####
```javascript
{"status":"connecting","address":"01:23:45:67:89:AB","name":"Polar H7"}
{"status":"connected","address":"01:23:45:67:89:AB","name":"Polar H7"}
{"status":"disconnecting","address":"01:23:45:67:89:AB","name":"Polar H7"}
{"status":"disconnected","address":"01:23:45:67:89:AB","name":"Polar H7"}
```



### reconnect ###
Reconnect to a previously connected Bluetooth device. The Phonegap app should use a timer to limit the connecting time. If a timeout occurs, the reconnection attempt should be canceled using disconnect().

```javascript
bluetoothle.reconnect(reconnectSuccessCallback, reconnectErrorCallback);
```

##### Success Return #####
See return object for connect



### disconnect ###
Disconnect from a Bluetooth LE device.

```javascript
bluetoothle.disconnect(disconnectSuccessCallback, disconnectErrorCallback);
```

##### Return #####
See return object for connect, specifically disconnecting and disconnected.



### close ###
Close/dispose a Bluetooth LE device. Must disconnect before closing.

```javascript
bluetoothle.close(closeSuccessCallback, closeErrorCallback);
```

##### Success Return #####
```javascript
{"status":"closed","address":"01:23:45:67:89:AB","name":"Polar H7"}
```



### discover ###
Discover all the devices services, characteristics and descriptors. Doesn't need to be called again after disconnecting and then reconnecting. Android support only. Calling on iOS will return void.

```javascript
bluetoothle.discover(discoverSuccessCallback, discoverErrorCallback);
```

##### Return #####
Device Object:
* address = Device address
* name = Device name
* services = Array of service objects below

Service Object:
* serviceUuid = Service's uuid
* characteristics = Array of characteristic objects below

Characteristic Object:
* characteristicUuid = Characteristic's uuid
* properties = If the property is defined as a key, the characteristic has that property
* descriptors = Array of descriptor objects below

Descriptor Object:
* descriptorUuid = Descriptor's uuid

```javascript
{
  "status" : "discovered",
  "address":"xx:xx:xx:xx:xx:xx",
  "name":"Polar H7",
  "services":[
    {
      "serviceUuid":"180d",
      "characteristics":[
        {
          "characteristicUuid":"2a37",
          "descriptors":[
            {
              "descriptorUuid":"2902"
            }
          ],
          "properties":
          {
          	"read":true,
          } 
        },
        {
          "characteristicUuid":"2a38",
          "descriptors":[]
        }
      ]
    }
  ]
}
```



### services ###
Discover the device's services. Not providing an array of services will return all services and take longer to discover. iOS support only. Calling on Android will return void.

```javascript
bluetoothle.services(servicesSuccessCallback, servicesErrorCallback, params);
```

##### Params #####
* serviceUuids = An array of service IDs to filter the scan or empty array / null

```javascript
{"serviceUuids":["180D","180F"]};
```

##### Success Return #####
```javascript
{"status":"discoveredServices","serviceUuids":["180d"],"name":"Polar H7 259536","address":"6A267C59-3364-544C-F2AE-1616AE34F2C3"}
```



### characteristics ###
Discover the service's characteristics. Not providing an array of characteristics will return all characteristics and take longer to discover. iOS support only. Calling on Android will return void.

```javascript
bluetoothle.characteristics(characteristicsSuccessCallback, characteristicsErrorCallback, params);
```

##### Params #####
```javascript
{"serviceUuid":"180D","characteristicUuids":["2A37","2A38"]}
```

##### Success Return #####
```javascript
{"status":"discoveredCharacteristics","characteristics":[{"properties":{"notify":true},"characteristicUuid":"2a37"}],"name":"Polar H7 259536","address":"6A267C59-3364-544C-F2AE-1616AE34F2C3"}
```



### descriptors ###
Discover the characteristic's descriptors. iOS support only. Calling on Android will return void.

```javascript
bluetoothle.characteristics(descriptorsSuccessCallback, descriptorsErrorCallback, params);
```

##### Params #####
```javascript
{"serviceUuid":"180D","characteristicUuid":"2A37"};
```

##### Success Return #####
```javascript
{"status":"discoveredDescriptors","descriptorUuids":["2902"],"characteristicUuid":"2a37","name":"Polar H7 259536","serviceUuid":"180d","address":"6A267C59-3364-544C-F2AE-1616AE34F2C3"}
```



### read ###
Read a particular service's characteristic once.

```javascript
bluetoothle.read(readSuccessCallback, readErrorCallback, params);
```

##### Params #####
```javascript
{"serviceUuid":"180F","characteristicUuid":"2A19"}
```

##### Success Return #####
Value is a base64 encoded string of read bytes. Use bluetoothle.encodedStringToBytes(obj.value) to convert to a unit8Array. See characteristic's specification and example below on how to correctly parse this.

```javascript
{"status":"read","serviceUuid":"180F","characteristicUuid":"2A19","value":""}
```


### subscribe ###
Subscribe to a particular service's characteristic. Once a subscription is no longer needed, execute unsubscribe in a similar fashion. The Client Configuration descriptor will automatically be written to enable notification/indication.

```javascript
bluetoothle.subscribe(subscribeSuccessCallback, subscribeErrorCallback, params);
```

##### Params #####
```javascript
{"serviceUuid":"180D","characteristicUuid":"2A37","isNotification":true}
```
* isNotification is only required on Android. True (or null) means notification will be enabled. False means indication will be enabled.

##### Success Return #####
Value is a base64 encoded string of read bytes. Use bluetoothle.encodedStringToBytes(obj.value) to convert to a unit8Array. See characteristic's specification and example below on how to correctly parse this.

```javascript
{"status":"subscribed","serviceUuid":"180D","characteristicUuid":"2A37"}
{"status":"subscribedResult","serviceUuid":"180D","characteristicUuid":"2A37","value":""}
```



### unsubscribe ###
Unsubscribe to a particular service's characteristic.

```javascript
bluetoothle.unsubscribe(unsubscribeSuccessCallback, unsubscribeErrorCallback, params);
```

##### Params #####
```javascript
{"serviceUuid":"180D","characteristicUuid":"2A37"}
```

##### Success Return #####
```javascript
{"status":"unsubscribed","serviceUuid":"180D","characteristicUuid":"2A37"}
```



### write ###
Write a particular service's characteristic. ***Note, this hasn't been well tested***

```javascript
bluetoothle.write(writeSuccessCallback, writeErrorCallback, params);
```

##### Params #####
Value is a base64 encoded string of bytes to write. Use bluetoothle.bytesToEncodedString(bytes) to convert to base64 encoded string from a unit8Array.
To write without response, set type to "noResponse". Any other value will default to write with response. Note, no callback will occur on write without response.
```javascript
//Note, this example doesn't actually work since it's read only characteristic
{"value":"","serviceUuid":"180F","characteristicUuid":"2A19","type":"noResponse"}
```

##### Success Return #####
Value is a base64 encoded string of written bytes. Use bluetoothle.encodedStringToBytes(obj.value) to convert to a unit8Array. See characteristic's specification and example below on how to correctly parse this.

```javascript
//Write
{"status":"written","serviceUuid":"180F","characteristicUuid":"2A19","value":""}
```



### readDescriptor ###
Read a particular characterist's descriptor

```javascript
bluetoothle.read(readDescriptorSuccessCallback, readDescriptorErrorCallback, params);
```

##### Params #####
```javascript
{"serviceUuid":"180D","characteristicUuid":"2A37","descriptorUuid":"2902"}
```

##### Success Return #####
Value is a base64 encoded string of read bytes. Use bluetoothle.encodedStringToBytes(obj.value) to convert to a unit8Array.

```javascript
{"status":"readDescriptor","serviceUuid":"180D","characteristicUuid":"2A37", "descriptorUuid":"2902","value":""}
```



### writeDescriptor ###
Write a particular characteristic's descriptor. Unable to write characteristic configuration directly to keep in line with iOS implementation. Instead use subscribe/unsubscribe, which will automatically enable/disable notification. ***Note, limited testing and likely needs to be made more generic***

```javascript
bluetoothle.write(writeDescriptorSuccessCallback, writeDescriptorErrorCallback, params);
```

##### Params #####
Value is a base64 encoded string of bytes to write. Use bluetoothle.bytesToEncodedString(bytes) to convert to base64 encoded string from a unit8Array.

```javascript
{"serviceUuid":"180D","characteristicUuid":"2A37","descriptorUuid":"2902","value":"EnableNotification"}
```

##### Success Return #####
Value is a base64 encoded string of written bytes. Use bluetoothle.encodedStringToBytes(obj.value) to convert to a unit8Array. 

```javascript
{"status":"writeDescriptor","serviceUuid":"180D","characteristicUuid":"2A37", "descriptorUuid":"2902","value":"EnableNotification"}
```



### rssi ###
Read RSSI of a connected device. RSSI is also returned with scanning.

```javascript
bluetoothle.rssi(rssiSuccessCallback, rssiErrorCallback);
```

##### Success Return #####
```javascript
{"status":"rssi","rssi":-5};
```


### isInitialized ###
Determine whether the adapter is initialized. No error callback. Returns true or false

```javascript
bluetoothle.isInitialized(isInitializedCallback);
```

##### Success Return #####
```javascript
{"isInitialized" : true }
```


### isEnabled ###
Determine whether the adapter is enabled. No error callback

```javascript
bluetoothle.isEnabled(isEnabledCallback);
```

##### Success Return #####
True or false



### isScanning ###
Determine whether the adapter is initialized. No error callback. Returns true or false

```javascript
bluetoothle.isScanning(isScanningCallback);
```

##### Return #####
```javascript
{"isScanning" : true }
```


### isConnected ###
Determine whether the device is connected. No error callback. Returns true or false

```javascript
bluetoothle.isConnected(isConnectedCallback);
```

##### Return #####
```javascript
{"isConnected" : true }
```


### isDiscovered ###
Determine whether the device's characteristics and descriptors have been discovered. No error callback. Android support only. Calling on iOS will return false.

```javascript
bluetoothle.isDiscovered(isDiscoveredCallback);
```

##### Success Return #####
```javascript
{"isDiscovered" : true }
```




### encodedStringToBytes ###
Helper function to convert a base64 encoded string from a characteristic or descriptor value into a uint8Array object.

```javascript
bluetoothle.encodedStringToBytes(string);
```



### bytesToEncodedString ###
Helper function to convert a unit8Array to a base64 encoded string for a characteric or descriptor write.

```javascript
bluetoothle.bytesToEncodedString(bytes);
```



### stringToBytes ###
Helper function to convert a string to bytes.

```javascript
bluetoothle.stringToBytes(string);
```



### bytesToString ###
Helper function to convert bytes to a string.

```javascript
bluetoothle.bytesToString(bytes);
```



## Example ##
The following example demonstrates how to connect to a heart rate monitor, read the battery level and subscribe to the heart rate. The first execution will automatically scan and connect to the first device. The second execution will use the saved device address rather than scanning for devices.

***Dependencies***: Example depends on the device plugin to detect whether Phonegap is running on Android or iOS. Run "cordova plugin add org.apache.cordova.device" from the CLI to install the device plugin. 

***Life Cycle***: Initialize -> Scan -> Connect -> Disconnect -> Reconnect -> Discover -> Read Battery -> Subscribe Heart Rate -> Wait -> Unsubscribe -> Disconnect -> Close

***Timeouts***: scan, connect and reconnect.

```javascript
var addressKey = "address";

var heartRateServiceUuid = "180d";
var heartRateMeasurementCharacteristicUuid = "2a37";
var clientCharacteristicConfigDescriptorUuid = "2902";
var batteryServiceUuid = "180f";
var batteryLevelCharacteristicUuid = "2a19";

var scanTimer = null;
var connectTimer = null;
var reconnectTimer = null;

var iOSPlatform = "iOS";
var androidPlatform = "Android";

bluetoothle.initialize(initializeSuccess, initializeError);

function initializeSuccess(obj)
{
  if (obj.status == "enabled")
  {
  	var address = window.localStorage.getItem(addressKey);
  	if (address == null)
  	{
   		console.log("Bluetooth initialized successfully, starting scan for heart rate devices.");
   		var paramsObj = {"serviceUuids":[heartRateServiceUuid]};
  		bluetoothle.startScan(startScanSuccess, startScanError, paramsObj);
  	}
  	else
  	{
  		connectDevice(address);
  	}
  }
  else
  {
  	console.log("Unexpected initialize status: " + obj.status);
  }
}

function initializeError(obj)
{
  console.log("Initialize error: " + obj.error + " - " + obj.message);
}

function startScanSuccess(obj)
{
  if (obj.status == "scanResult")
  {
    console.log("Stopping scan..");
    bluetoothle.stopScan(stopScanSuccess, stopScanError);
    clearScanTimeout();
    
    window.localStorage.setItem(addressKey, obj.address);
		connectDevice(obj.address);
  }
  else if (obj.status == "scanStarted")
  {
    console.log("Scan was started successfully, stopping in 10");
    scanTimer = setTimeout(scanTimeout, 10000);
  }
  else
  {
  	console.log("Unexpected start scan status: " + obj.status);
  }
}

function startScanError(obj)
{
  console.log("Start scan error: " + obj.error + " - " + obj.message);
}

function scanTimeout()
{
  console.log("Scanning time out, stopping");
  bluetoothle.stopScan(stopScanSuccess, stopScanError);
}

function clearScanTimeout()
{ 
	console.log("Clearing scanning timeout");
  if (scanTimer != null)
  {
    clearTimeout(scanTimer);
  }
}

function stopScanSuccess(obj)
{
  if (obj.status == "scanStopped")
  {
    console.log("Scan was stopped successfully");
  }
  else
  {
  	console.log("Unexpected stop scan status: " + obj.status);
  }
}

function stopScanError(obj)
{
  console.log("Stop scan error: " + obj.error + " - " + obj.message);
}

function connectDevice(address)
{
  console.log("Begining connection to: " + address + " with 5 second timeout");
 	var paramsObj = {"address":address};
  bluetoothle.connect(connectSuccess, connectError, paramsObj);
  connectTimer = setTimeout(connectTimeout, 5000);
}

function connectSuccess(obj)
{
  if (obj.status == "connected")
  {
  	console.log("Connected to : " + obj.name + " - " + obj.address);

    clearConnectTimeout();
    
    tempDisconnectDevice();
  }
  else if (obj.status == "connecting")
  {
  	console.log("Connecting to : " + obj.name + " - " + obj.address);
  }
	else
  {
  	console.log("Unexpected connect status: " + obj.status);
    clearConnectTimeout();
  }
}

function connectError(obj)
{
  console.log("Connect error: " + obj.error + " - " + obj.message);
  clearConnectTimeout();
}

function connectTimeout()
{
  console.log("Connection timed out");
}

function clearConnectTimeout()
{ 
	console.log("Clearing connect timeout");
  if (connectTimer != null)
  {
    clearTimeout(connectTimer);
  }
}

function tempDisconnectDevice()
{
  console.log("Disconnecting from device to test reconnect");
	bluetoothle.disconnect(tempDisconnectSuccess, tempDisconnectError);
}

function tempDisconnectSuccess(obj)
{
	if (obj.status == "disconnected")
	{
		console.log("Temp disconnect device and reconnecting in 1 second. Instantly reconnecting can cause issues");
		setTimeout(reconnect, 1000);
	}
	else if (obj.status == "disconnecting")
	{
		console.log("Temp disconnecting device");
	}
	else
  {
  	console.log("Unexpected temp disconnect status: " + obj.status);
  }
}

function tempDisconnectError(obj)
{
  console.log("Temp disconnect error: " + obj.error + " - " + obj.message);
}

function reconnect()
{
  console.log("Reconnecting with 5 second timeout");
  bluetoothle.reconnect(reconnectSuccess, reconnectError);
  reconnectTimer = setTimeout(reconnectTimeout, 5000);
}

function reconnectSuccess(obj)
{
  if (obj.status == "connected")
  {
  	console.log("Reconnected to : " + obj.name + " - " + obj.address);
    
    clearReconnectTimeout();
    
    if (window.device.platform == iOSPlatform)
    {
      console.log("Discovering heart rate service");
      var paramsObj = {"serviceUuids":[heartRateServiceUuid]};
      bluetoothle.services(servicesHeartSuccess, servicesHeartError, paramsObj);
    }
    else if (window.device.platform == androidPlatform)
    {
      console.log("Beginning discovery");
      bluetoothle.discover(discoverSuccess, discoverError);
    }
  }
  else if (obj.status == "connecting")
  {
  	console.log("Reconnecting to : " + obj.name + " - " + obj.address);
  }
  else
  {
  	console.log("Unexpected reconnect status: " + obj.status);
    disconnectDevice();
  }
}

function reconnectError(obj)
{
  console.log("Reconnect error: " + obj.error + " - " + obj.message);
  disconnectDevice();
}

function reconnectTimeout()
{
  console.log("Reconnection timed out");
}

function clearReconnectTimeout()
{ 
	console.log("Clearing reconnect timeout");
  if (reconnectTimer != null)
  {
    clearTimeout(reconnectTimer);
  }
}

function servicesHeartSuccess(obj)
{
  if (obj.status == "discoveredServices")
  {
    var serviceUuids = obj.serviceUuids;
    for (var i = 0; i < serviceUuids.length; i++)
    {
      var serviceUuid = serviceUuids[i];
      
      if (serviceUuid == heartRateServiceUuid)
      {
        console.log("Finding heart rate characteristics");
        var paramsObj = {"serviceUuid":heartRateServiceUuid, "characteristicUuids":[heartRateMeasurementCharacteristicUuid]};
        bluetoothle.characteristics(characteristicsHeartSuccess, characteristicsHeartError, paramsObj);
        return;
      }
    }
    console.log("Error: heart rate service not found");
  }
	else
  {
  	console.log("Unexpected services heart status: " + obj.status);
  }
  disconnectDevice();
}

function servicesHeartError(obj)
{
  console.log("Services heart error: " + obj.error + " - " + obj.message);
  disconnectDevice();
}

function characteristicsHeartSuccess(obj)
{
  if (obj.status == "discoveredCharacteristics")
  {
    var characteristics = obj.characteristics;
    for (var i = 0; i < characteristics.length; i++)
    {
      console.log("Heart characteristics found, now discovering descriptor");
      var characteristicUuid = characteristics[i].characteristicUuid;
      
      if (characteristicUuid == heartRateMeasurementCharacteristicUuid)
      {
        var paramsObj = {"serviceUuid":heartRateServiceUuid, "characteristicUuid":heartRateMeasurementCharacteristicUuid};
        bluetoothle.descriptors(descriptorsHeartSuccess, descriptorsHeartError, paramsObj);
        return;
      }
    }
    console.log("Error: Heart rate measurement characteristic not found.");
  }
	else
  {
  	console.log("Unexpected characteristics heart status: " + obj.status);
  }
  disconnectDevice();
}

function characteristicsHeartError(obj)
{
  console.log("Characteristics heart error: " + obj.error + " - " + obj.message);
  disconnectDevice();
}

function descriptorsHeartSuccess(obj)
{
  if (obj.status == "discoveredDescriptors")
  {
  	console.log("Discovered heart descriptors, now discovering battery service");
    var paramsObj = {"serviceUuids":[batteryServiceUuid]};
    bluetoothle.services(servicesBatterySuccess, servicesBatteryError, paramsObj);
  }
	else
  {
  	console.log("Unexpected descriptors heart status: " + obj.status);
  	disconnectDevice();
  }
}

function descriptorsHeartError(obj)
{
  console.log("Descriptors heart error: " + obj.error + " - " + obj.message);
  disconnectDevice();
}

function servicesBatterySuccess(obj)
{
  if (obj.status == "discoveredServices")
  {
    var serviceUuids = obj.serviceUuids;
    for (var i = 0; i < serviceUuids.length; i++)
    {
      var serviceUuid = serviceUuids[i];
      
      if (serviceUuid == batteryServiceUuid)
      {
        console.log("Found battery service, now finding characteristic");
        var paramsObj = {"serviceUuid":batteryServiceUuid, "characteristicUuids":[batteryLevelCharacteristicUuid]};
        bluetoothle.characteristics(characteristicsBatterySuccess, characteristicsBatteryError, paramsObj);
        return;
      }
    }
    console.log("Error: battery service not found");
  }
	else
  {
  	console.log("Unexpected services battery status: " + obj.status);
  }
  disconnectDevice();
}

function servicesBatteryError(obj)
{
  console.log("Services battery error: " + obj.error + " - " + obj.message);
  disconnectDevice();
}

function characteristicsBatterySuccess(obj)
{
  if (obj.status == "discoveredCharacteristics")
  {
    var characteristics = obj.characteristics;
    for (var i = 0; i < characteristics.length; i++)
    {
      var characteristicUuid = characteristics[i].characteristicUuid;
      
      if (characteristicUuid == batteryLevelCharacteristicUuid)
      {
        readBatteryLevel();
        return;
      }
    }
    console.log("Error: Battery characteristic not found.");
  }
	else
  {
  	console.log("Unexpected characteristics battery status: " + obj.status);
  }
  disconnectDevice();
}

function characteristicsBatteryError(obj)
{
  console.log("Characteristics battery error: " + obj.error + " - " + obj.message);
  disconnectDevice();
}

function discoverSuccess(obj)
{
	if (obj.status == "discovered")
	{
		console.log("Discovery completed");
		
    readBatteryLevel();
  }
  else
  {
  	console.log("Unexpected discover status: " + obj.status);
  	disconnectDevice();
  }
}

function discoverError(obj)
{
  console.log("Discover error: " + obj.error + " - " + obj.message);
  disconnectDevice();
}

function readBatteryLevel()
{
  console.log("Reading battery level");
  var paramsObj = {"serviceUuid":batteryServiceUuid, "characteristicUuid":batteryLevelCharacteristicUuid};
  bluetoothle.read(readSuccess, readError, paramsObj);
}

function readSuccess(obj)
{
	if (obj.status == "read")
	{
		var bytes = bluetoothle.encodedStringToBytes(obj.value);
		console.log("Battery level: " + bytes[0]);
		  
		console.log("Subscribing to heart rate for 5 seconds");
		var paramsObj = {"serviceUuid":heartRateServiceUuid, "characteristicUuid":heartRateMeasurementCharacteristicUuid};
		bluetoothle.subscribe(subscribeSuccess, subscribeError, paramsObj);
		setTimeout(unsubscribeDevice, 5000);
	}
	else
  {
  	console.log("Unexpected read status: " + obj.status);
    disconnectDevice();
  }
}

function readError(obj)
{
  console.log("Read error: " + obj.error + " - " + obj.message);
  disconnectDevice();
}

function subscribeSuccess(obj)
{	
	if (obj.status == "subscribedResult")
	{
		console.log("Subscription data received");
	
		//Parse array of int32 into uint8
		var bytes = bluetoothle.encodedStringToBytes(obj.value);

		//Check for data
		if (bytes.length == 0)
		{
			console.log("Subscription result had zero length data");
			return;
		}

		//Get the first byte that contains flags
		var flag = bytes[0];

		//Check if u8 or u16 and get heart rate
		var hr;
		if ((flag & 0x01) == 1)
		{
			var u16bytes = bytes.buffer.slice(1, 3);
			var u16 = new Uint16Array(u16bytes)[0];
			hr = u16;
		}
		else
		{
			var u8bytes = bytes.buffer.slice(1, 2);
			var u8 = new Uint8Array(u8bytes)[0];
			hr = u8;
		}
		console.log("Heart Rate: " + hr);
	}
	else if (obj.status == "subscribed")
	{
		console.log("Subscription started");
	}
	else
  {
  	console.log("Unexpected subscribe status: " + obj.status);
    disconnectDevice();
  }
}

function subscribeError(msg)
{
  console.log("Subscribe error: " + obj.error + " - " + obj.message);
  disconnectDevice();
}

function unsubscribeDevice()
{
  console.log("Unsubscribing heart service");
  var paramsObj = {"serviceUuid":heartRateServiceUuid, "characteristicUuid":heartRateMeasurementCharacteristicUuid};
  bluetoothle.unsubscribe(unsubscribeSuccess, unsubscribeError, paramsObj);
}

function unsubscribeSuccess(obj)
{
	if (obj.status == "unsubscribed")
	{
		console.log("Unsubscribed device");
		
		console.log("Reading client configuration descriptor");
		var paramsObj = {"serviceUuid":heartRateServiceUuid, "characteristicUuid":heartRateMeasurementCharacteristicUuid, "descriptorUuid":clientCharacteristicConfigDescriptorUuid};
		bluetoothle.readDescriptor(readDescriptorSuccess, readDescriptorError, paramsObj);
	}
	else
  {
  	console.log("Unexpected unsubscribe status: " + obj.status);
    disconnectDevice();
  }
}

function unsubscribeError(obj)
{
  console.log("Unsubscribe error: " + obj.error + " - " + obj.message);
  disconnectDevice();
}

function readDescriptorSuccess(obj)
{
	if (obj.status == "readDescriptor")
	{
		var bytes = bluetoothle.encodedStringToBytes(obj.value);
		var u16Bytes = new Uint16Array(bytes.buffer);
		console.log("Read descriptor value: " + u16Bytes[0]);
		disconnectDevice();
	}
	else
  {
  	console.log("Unexpected read descriptor status: " + obj.status);
    disconnectDevice();
  }
}

function readDescriptorError(obj)
{
  console.log("Read Descriptor error: " + obj.error + " - " + obj.message);
  disconnectDevice();
}

function disconnectDevice()
{
  bluetoothle.disconnect(disconnectSuccess, disconnectError);
}

function disconnectSuccess(obj)
{
	if (obj.status == "disconnected")
	{
		console.log("Disconnect device");
		closeDevice();
	}
	else if (obj.status == "disconnecting")
	{
		console.log("Disconnecting device");
	}
	else
  {
  	console.log("Unexpected disconnect status: " + obj.status);
  }
}

function disconnectError(obj)
{
  console.log("Disconnect error: " + obj.error + " - " + obj.message);
}

function closeDevice()
{
  bluetoothle.close(closeSuccess, closeError);
}

function closeSuccess(obj)
{
	if (obj.status == "closed")
	{
		console.log("Closed device");
	}
	else
  {
  	console.log("Unexpected close status: " + obj.status);
  }
}

function closeError(obj)
{
  console.log("Close error: " + obj.error + " - " + obj.message);
}

```


## More information ##
* Author: Rand Dusing
* Website: http://www.randdusing.com/

## License ##
The source files included in the repository are released under the Apache License, Version 2.0.
