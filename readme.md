Bluetooth LE Cordova Plugin

====================

## Supported platforms ##

* Cordova 3.0.0 or higher
* Android 4.3 or higher
* iOS 7 or higher
* Windows Phone 8.1 (Tested on Nokia Lumia 630)
* Device hardware must be certified for Bluetooth LE. i.e. Nexus 7 (2012) doesn't support Bluetooth LE even after upgrading to 4.3 (or higher) without a modification
* List of devices: http://www.bluetooth.com/Pages/Bluetooth-Smart-Devices-List.aspx


## Limitations / Issues ##

* Tested mostly with a heart rate monitor, so some scenarios especially those involving writing characteristics / descriptors may not work or are poorly documented
* Windows Phone 8 support is limited for the time being. I'm not sure when I'll begin working on this...
* No queueing support for read/write operations
* Disconnecting and quickly reconnecting causes issues on Android. The device becomes connected again, but then quickly disconnects. Adding a timeout before reconnecting fixed the issue for me. I'm not sure if this is a problem with the plugin or Android's Bluetooth LE implementation.
* For subscribing, indication hasn't been tested since my heart rate monitor doesn't support it.
* Characteristic and descriptor permissions are not returned during discovery. If anyone requests this, I should be able to add it fairly easily, at least for Android.


## To Do ##

* Support for Peripheral role on iOS 6.0+ and Android 5.0+
* Full support for Windows Phone 8.1 C#-based projects. Assuming I can follow @MiBLE's process successfully.
* Support for Windows Phone 8.1 Javascript projects. Currently waiting for better debugging support with Visual Studio.
* Connect and Reconnect should detect existing connection with better error messages
* Better documentation and example for write and writeDescriptor


## Installation ##

If you are using PhoneGap add the plugin to your app by running the command below:

```phonegap local plugin add https://github.com/randdusing/BluetoothLE```

If you are using Apache Cordova use this instead:

```cordova plugin add https://github.com/randdusing/BluetoothLE```

If you are using PhoneGap Build and want to use the PhoneGap Build Plugin (outdated plugin version 2.0.0), add below to config.xml:

```<gap:plugin name="com.randdusing.bluetoothle" />```

If you are using PhoneGap Build and want to use the Cordova Plugin Registry (up to date plugin version but PhoneGap Build doesn't support Android API21 yet), add below to config.xml:

```<gap:plugin name="com.randdusing.bluetoothle" source="plugins.cordova.io" />```


## Installation Quirks (iOS) ##
By default, background mode is enabled. If you wish to remove this, follow the steps below:
1. Click your Project
2. Click your Target
3. Click Capabilities
4. Scroll down to Background Modes section, and uncheck Uses Bluetooth LE accessories
5. Open up BluetoothLePlugin.m
6. Remove "CBCentralManagerOptionRestoreIdentifierKey:pluginName," from the initWithDelegate call in the initialize function
7. Remove the willRestoreState function
8. Optionally remove 'NSString *const pluginName = @"bluetoothleplugin";' since it's no longer used


Updating the plugin for iOS causes BluetoothLePlugin.m to be removed from the Compile Sources and CoreBluetooth.framework to be removed from Link Binary with Libraries. To fix:
1. Click your project to open the "properties" window
2. Click your target
3. Click Build Phases
4. Ensure BluetoothLePlugin.m is added to the Compile Sources
5. Ensure CoreBluetooth.framework is added to the Link Binary with Libraries


## Installation Quirks (Android) ##
The latest version of the plugin requires you to set the Android target API to 21.


## PhoneGap Build ##
I'm no longer updating the PhoneGap Build version of this plugin since it costs money and better alternatives like the Cordova Plugin Registry exist. Once PhoneGap Build supports Android API 21 (required for latest version of my plugin), I'll request deactivation of the plugin on PhoneGap Build to remove the out of date version.


## Discovery Quirks (iOS vs Android) ##
Discovery works differently between Android and iOS. In Android, a single function is called to initiate discovery of all services, characteristics and descriptors on the device. In iOS, a single function is called to discover the device's services. Then another function to discover the characteristics of a particular service. And then another function to discover the descriptors of a particular characteristic. The Device plugin (http://docs.phonegap.com/en/edge/cordova_device_device.md.html#Device) should be used to properly determine the device and make the proper calls if necessary. Additionally, if a device is disconnected, it must be rediscovered when running on iOS.


## UUIDs ##
UUIDs can be 16 bits or 128 bits. The "out of the box" UUIDs from the link below are 16 bits.
Since iOS returns the 16 bit version of the "out of the box" UUIDs even if a 128 bit UUID was used in the parameters, the 16 bit version should always be used for the "out of the box" UUIDs for consistency.
Android on the other hand only uses the 128 bit version, but the plugin will automatically convert 16 bit UUIDs to the 128 bit version on input and output. For a list of out of the box UUIDS, see https://developer.bluetooth.org/gatt/services/Pages/ServicesHome.aspx


## Advertisement Data / MAC Address ##
On iOS, the MAC address is hidden from the advertisement packet, and the address returned from the scanResult is a generated, device-specific address. This is a problem when using devices like iBeacons where you need the MAC Address. Fortunately the CLBeacon class can be used for this, but unfortunately it's not supported in this plugin.
One option is to set Manufacturer Specific Data in the advertisement packet if that's possible in your project.
Another option is to connect to the device and use the "Device Information" (0x180A) service, but connecting to each device is much more energy intensive than scanning for advertisement data.
See the following for more info: https://stackoverflow.com/questions/18973098/get-mac-address-of-bluetooth-low-energy-peripheral, https://stackoverflow.com/questions/22833198/get-advertisement-data-for-ble-in-ios


## Methods ##

* [bluetoothle.initialize] (#initialize)
* [bluetoothle.enable] (#enable) (Android)
* [bluetoothle.disable] (#disable) (Android)
* [bluetoothle.startScan] (#startscan)
* [bluetoothle.stopScan] (#stopscan)
* [bluetoothle.retrieveConnected] (#retrieveconnected) (iOS)
* [bluetoothle.connect] (#connect)
* [bluetoothle.reconnect] (#reconnect)
* [bluetoothle.disconnect] (#disconnect)
* [bluetoothle.close] (#close)
* [bluetoothle.discover] (#discover) (Android)
* [bluetoothle.services] (#services) (iOS)
* [bluetoothle.characteristics] (#characteristics) (iOS)
* [bluetoothle.descriptors] (#descriptors)  (iOS)
* [bluetoothle.read] (#read)
* [bluetoothle.subscribe] (#subscribe)
* [bluetoothle.unsubscribe] (#unsubscribe)
* [bluetoothle.write] (#write)
* [bluetoothle.readDescriptor] (#readdescriptor)
* [bluetoothle.writeDescriptor] (#writedescriptor)
* [bluetoothle.rssi] (#rssi)
* [bluetoothle.isInitialized] (#isinitialized)
* [bluetoothle.isEnabled] (#isenabled)
* [bluetoothle.isScanning] (#isscanning)
* [bluetoothle.isConnected] (#isconnected)
* [bluetoothle.isDiscovered] (#isdiscovered)  (Android)
* [bluetoothle.encodedStringToBytes] (#encodedstringtobytes)
* [bluetoothle.bytesToEncodedString] (#bytestoencodedstring)
* [bluetoothle.stringToBytes] (#stringtobytes)
* [bluetoothle.bytesToString] (#bytestostring)


## Errors ##

Whenever the error callback is executed, the return object will contain the error type and a message.
* initialize - Bluetooth isn't initialized (Try initializing Bluetooth)
* enable - Bluetooth isn't enabled (Request user to enable Bluetooth)
* disable - Bluetooth isn't disabled (Can't enabled if already disabled)
* startScan - Scan couldn't be started (Is the scan already running?)
* stopScan - Scan couldn't be stopped (Is the scan already stopped?)
* retrieveConnected - Failed to retrieve connected devices (Is the device iOS?)
* connect - Connection attempt failed (Is the device address correct?)
* reconnect - Reconnection attempt failed (Was the device ever connected?)
* discover - Failed to discover device (Is the device already discovered or discovering? Is the device Android?)
* services - Failed to discover services (Is the device iOS?)
* characteristics - Failed to discover characteristics (Is the device iOS?)
* descriptors - Failed to discover descriptors (Is the device iOS?)
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
* requestConnectionPriority - Failed to request connection priority (Is the device iOS?)

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
5. read/subscribe/write characteristics AND read/write descriptors
6. disconnect
7. close


## initialize ##
Initialize Bluetooth on the device. Must be called before anything else. Callback will continuously be used whenever Bluetooth is enabled or disabled. Note: Although Bluetooth initialization could initially be successful, there's no guarantee whether it will stay enabled. Each call checks whether Bluetooth is disabled. If it becomes disabled, the user must connect to the device, start a read/write operation, etc again. If Bluetooth is disabled, you can request the user to enable it by setting the request property to true. The `request` property in the `params` argument is optional and defaults to false. Also, this function should only be called once. But if it's called subsequent times, it will return either status => enabled or error => enable.

```javascript
bluetoothle.initialize(initializeSuccess, initializeError, params);
```

##### Params #####
* request = true / false (default) - Should user be prompted to enable Bluetooth
* statusReceiver = true (default) / false - Should change in Bluetooth status notifications be sent.

```javascript
{
  "request": true,
  "statusReceiver": false
}
```

##### Success #####
* status => enabled = Bluetooth is enabled

```javascript
{
  "status": "enabled"
}
```



## enable ##
Enable Bluetooth on the device. Android support only.

```javascript
bluetoothle.enable(enableSuccess, enableError);
```

##### Error #####
* errorDisable = Bluetooth isn't disabled, so unable to enable.
* errorEnable = Immediate failure of the internal enable() function due to Bluetooth already on or airplane mode, so unable to enable.

##### Success #####
The successCallback isn't actually used. Listen to initialize callbacks for change in Bluetooth state. A successful enable will return a status => enabled via initialize success callback.



## disable ##
Disable Bluetooth on the device. Android support only.

```javascript
bluetoothle.disable(disableSuccess, disableError);
```

##### Error #####
* errorEnable = Bluetooth isn't enabled, so unable to disable.
* errorDisable = Immediate failure of the internal disable() function due to Bluetooth already off, so unable to enable. This shouldn't occur since the plugin is already checking this condition anyways.

##### Success #####
The successCallback isn't actually used. Listen to initialize callbacks for change in Bluetooth state. A successful disable will return an error => enable via initialize error callback.



### startScan ###
Scan for Bluetooth LE devices. Since scanning is expensive, stop as soon as possible. The Cordova app should use a timer to limit the scan interval. Also, Android uses an AND operator for filtering, while iOS uses an OR operator.

```javascript
bluetoothle.startScan(startScanSuccess, startScanError, params);
```

##### Params #####
* serviceUuids = An array of service IDs to filter the scan or empty array / null

```javascript
{
  "serviceUuids": [
    "180D",
    "180F"
  ]
}
```

##### Success #####
* status => scanStarted = Scan has started
* status => scanResult = Scan has found a device
  * name = the device's display name
  * address = the device's address / identifier for connecting to the object
  * rssi = signal strength
  * advertisement = advertisement data in encoded string of bytes, use bluetoothle.encodedStringToBytes() - Only tested in Android so far!
  
```javascript
{
  "status": "scanStarted"
}

{
  "status": "scanResult",
  "advertisement": "awArG05L",
  "rssi": -58,
  "name": "Polar H7 3B321015",
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63"
}
```



### stopScan ###
Stop scan for Bluetooth LE devices. Since scanning is expensive, stop as soon as possible. The Cordova app should use a timer to limit the scanning time.

```javascript
bluetoothle.stopScan(stopScanSuccess, stopScanError);
```

##### Success #####
* status => scanStop = Scan has stopped

```javascript
{
  "status": "scanStopped"
}
```



### retrieveConnected ###
Retrieved Bluetooth LE devices currently connected. In iOS, devices that are "paired" to will not return during a normal scan. Callback is "instant" compared to a scan. I haven't been able to get UUID filtering working on Android, so it returns all paired devices including non Bluetooth LE ones.

```javascript
bluetoothle.retrieveConnected(retrieveConnectedSuccess, retrieveConnectedError, params);
```

##### Params #####
* serviceUuids = An array of service IDs to filter the retrieval by. If no service IDs are specified, no devices will be returned! Ignored on Android

```javascript
{
  "serviceUuids": [
    "180D",
    "180F"
  ]
}
```

##### Success #####
An array of device objects:
* name = the device's display name
* address = the device's address / identifier for connecting to the object

```javascript
[
  {
    "name": "Polar H7 3B321015",
    "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63"
  }
]
```



### connect ###
Connect to a Bluetooth LE device. The Cordova app should use a timer to limit the connecting time in case connecting is never successful. Once a device is connected, it may disconnect without user intervention. The original connection callback will be called again and receive an object with status => disconnected. To reconnect to the device, use the reconnect method. Before connecting to a new device, the current device must be disconnected and closed. If a timeout occurs, the connection attempt should be canceled using disconnect().

```javascript
bluetoothle.connect(connectSuccess, connectError, params);
```

##### Params #####
* address = The address/identifier provided by the scan's return object

```javascript
{
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63"
}
```

##### Success #####
* status => connecting = Beginning to connect
* status => connected = Device connected
* status => disconnected = Device unexpectedly disconnected

```javascript
{
  "name": "Polar H7 3B321015",
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "status": "connecting"
}

{
  "name": "Polar H7 3B321015",
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "status": "connected"
}

{
  "name": "Polar H7 3B321015",
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "status": "disconnected"
}
```



### reconnect ###
Reconnect to a previously connected Bluetooth device. The Cordova app should use a timer to limit the connecting time. If a timeout occurs, the reconnection attempt should be canceled using disconnect().

```javascript
bluetoothle.reconnect(reconnectSuccess, reconnectError);
```

##### Params #####
* address = The address/identifier provided by the scan's return object

```javascript
{
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63"
}
```

##### Success #####
* status => connecting = Beginning to connect
* status => connected = Device connected
* status => disconnected = Device unexpectedly disconnected

```javascript
{
  "name": "Polar H7 3B321015",
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "status": "connecting"
}

{
  "name": "Polar H7 3B321015",
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "status": "connected"
}

{
  "name": "Polar H7 3B321015",
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "status": "disconnected"
}
```


### disconnect ###
Disconnect from a Bluetooth LE device.

```javascript
bluetoothle.disconnect(disconnectSuccess, disconnectError);
```

##### Params #####
* address = The address/identifier provided by the scan's return object

```javascript
{
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63"
}
```

##### Success #####
* status => disconnecting = Beginning to disconnect
* status => disconnected = Device disconnected

```javascript
{
  "name": "Polar H7 3B321015",
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "status": "disconnecting"
}

{
  "name": "Polar H7 3B321015",
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "status": "disconnected"
}
```


### close ###
Close/dispose a Bluetooth LE device. Must disconnect before closing.

```javascript
bluetoothle.close(closeSuccess, closeError, params);
```

##### Params #####
* address = The address/identifier provided by the scan's return object

```javascript
{
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63"
}
```

##### Success #####
* status => closed = Connection with device completely closed down

```javascript
{
  "name": "Polar H7 3B321015",
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "status": "closed"
}
```



### discover ###
Discover all the devices services, characteristics and descriptors. Doesn't need to be called again after disconnecting and then reconnecting. Android support only.

```javascript
bluetoothle.discover(discoverSuccess, discoverError, params);
```

##### Params #####
* address = The address/identifier provided by the scan's return object

```javascript
{
  "address": "00:22:D0:3B:32:10"
}
```

##### Return #####
Device Object:
* status => discovered = Device was discovered
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
  "address": "00:22:D0:3B:32:10",
  "status": "discovered",
  "services": [
    {
      "characteristics": [
        {
          "descriptors": [
            
          ],
          "characteristicUuid": "2a00",
          "properties": {
            "write": true,
            "writeWithoutResponse": true,
            "read": true
          }
        },
        {
          "descriptors": [
            
          ],
          "characteristicUuid": "2a01",
          "properties": {
            "read": true
          }
        },
        {
          "descriptors": [
            
          ],
          "characteristicUuid": "2a02",
          "properties": {
            "read": true
          }
        },
        {
          "descriptors": [
            
          ],
          "characteristicUuid": "2a03",
          "properties": {
            "write": true
          }
        },
        {
          "descriptors": [
            
          ],
          "characteristicUuid": "2a04",
          "properties": {
            "read": true
          }
        }
      ],
      "serviceUuid": "1800"
    },
    {
      "characteristics": [
        {
          "descriptors": [
            {
              "descriptorUuid": "2902"
            }
          ],
          "characteristicUuid": "2a05",
          "properties": {
            "indicate": true
          }
        }
      ],
      "serviceUuid": "1801"
    },
    {
      "characteristics": [
        {
          "descriptors": [
            {
              "descriptorUuid": "2902"
            }
          ],
          "characteristicUuid": "2a37",
          "properties": {
            "notify": true
          }
        },
        {
          "descriptors": [
            
          ],
          "characteristicUuid": "2a38",
          "properties": {
            "read": true
          }
        }
      ],
      "serviceUuid": "180d"
    },
    {
      "characteristics": [
        {
          "descriptors": [
            
          ],
          "characteristicUuid": "2a23",
          "properties": {
            "read": true
          }
        },
        {
          "descriptors": [
            
          ],
          "characteristicUuid": "2a24",
          "properties": {
            "read": true
          }
        },
        {
          "descriptors": [
            
          ],
          "characteristicUuid": "2a25",
          "properties": {
            "read": true
          }
        },
        {
          "descriptors": [
            
          ],
          "characteristicUuid": "2a26",
          "properties": {
            "read": true
          }
        },
        {
          "descriptors": [
            
          ],
          "characteristicUuid": "2a27",
          "properties": {
            "read": true
          }
        },
        {
          "descriptors": [
            
          ],
          "characteristicUuid": "2a28",
          "properties": {
            "read": true
          }
        },
        {
          "descriptors": [
            
          ],
          "characteristicUuid": "2a29",
          "properties": {
            "read": true
          }
        }
      ],
      "serviceUuid": "180a"
    },
    {
      "characteristics": [
        {
          "descriptors": [
            
          ],
          "characteristicUuid": "2a19",
          "properties": {
            "read": true
          }
        }
      ],
      "serviceUuid": "180f"
    },
    {
      "characteristics": [
        {
          "descriptors": [
            
          ],
          "characteristicUuid": "6217ff4c-c8ec-b1fb-1380-3ad986708e2d",
          "properties": {
            "read": true
          }
        },
        {
          "descriptors": [
            {
              "descriptorUuid": "2902"
            }
          ],
          "characteristicUuid": "6217ff4d-91bb-91d0-7e2a-7cd3bda8a1f3",
          "properties": {
            "write": true,
            "indicate": true
          }
        }
      ],
      "serviceUuid": "6217ff4b-fb31-1140-ad5a-a45545d7ecf3"
    }
  ],
  "name": "Polar H7 3B321015"
}
```



### services ###
Discover the device's services. Not providing an array of services will return all services and take longer to discover. iOS support only.

```javascript
bluetoothle.services(servicesSuccess, servicesError, params);
```

##### Params #####
* address = The address/identifier provided by the scan's return object
* serviceUuids = An array of service IDs to filter the scan or empty array / null

```javascript
{
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "serviceUuids": [
    
  ]
}
```

##### Success #####
* status => services = Services discovered
	* serviceUuids = Array of service UUIDS

```javascript
{
  "status": "services",
  "serviceUuids": [
    "180d",
    "180a",
    "180f",
    "6217ff4b-fb31-1140-ad5a-a45545d7ecf3"
  ],
  "name": "Polar H7 3B321015",
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63"
}
```



### characteristics ###
Discover the service's characteristics. Not providing an array of characteristics will return all characteristics and take longer to discover. iOS support only.

```javascript
bluetoothle.characteristics(characteristicsSuccess, characteristicsError, params);
```

##### Params #####
* address = The address/identifier provided by the scan's return object
* serviceUuid = Service UUID
* characteristicUuids = An array of characteristic IDs to discover or empty array / null

```javascript
{
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "serviceUuid": "180d",
  "characteristicUuids": [
    
  ]
}
```

##### Success #####
* status => characteristics = Characteristics discovered
	* serviceUuid = Service UUID
	* characteristics = Array of characteristics
		* properties = Object of defined properties
		* characteristicUuid = Characteristic UUID

```javascript
{
  "status": "characteristics",
  "characteristics": [
    {
      "properties": {
        "notify": true
      },
      "characteristicUuid": "2a37"
    },
    {
      "properties": {
        "read": true
      },
      "characteristicUuid": "2a38"
    }
  ],
  "name": "Polar H7 3B321015",
  "serviceUuid": "180d",
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63"
}
```



### descriptors ###
Discover the characteristic's descriptors. iOS support only.

```javascript
bluetoothle.descriptors(descriptorsSuccess, descriptorsError, params);
```

##### Params #####
* address = The address/identifier provided by the scan's return object
* serviceUuid = The service's ID
* characteristicUuids = The characteristic's ID

```javascript
{
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "serviceUuid": "180d",
  "characteristicUuid": "2a37"
}
```

##### Success #####
* status => descriptors = Descriptors discovered
	* serviceUuid = Service UUID
	* characteristicUuid = characteristic UUID
	* descriptorUuids = Array of Descriptor UUIDs
	
```javascript
{
  "status": "descriptors",
  "descriptorUuids": [
    "2902"
  ],
  "characteristicUuid": "2a37",
  "name": "Polar H7 3B321015",
  "serviceUuid": "180d",
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63"
}
```



### read ###
Read a particular service's characteristic once.

```javascript
bluetoothle.read(readSuccess, readError, params);
```

##### Params #####
* address = The address/identifier provided by the scan's return object
* serviceUuid = The service's ID
* characteristicUuid = The characteristic's ID

```javascript
{
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "serviceUuid": "180d",
  "characteristicUuid": "2a38"
}
```

##### Success #####
* status => read = Characteristics read
	* serviceUuid = Service UUID
	* characteristicUuid = Characteristic UUID
	* value = Base64 encoded string of bytes. Use bluetoothle.encodedStringToBytes(obj.value) to convert to a unit8Array. See characteristic's specification and example below on how to correctly parse this.

```javascript
{
  "status": "read",
  "value": "AQ==",
  "characteristicUuid": "2a38",
  "name": "Polar H7 3B321015",
  "serviceUuid": "180d",
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63"
}
```


### subscribe ###
Subscribe to a particular service's characteristic. Once a subscription is no longer needed, execute unsubscribe in a similar fashion. The Client Configuration descriptor will automatically be written to enable notification/indication.

```javascript
bluetoothle.subscribe(subscribeSuccess, subscribeError, params);
```

##### Params #####
* address = The address/identifier provided by the scan's return object
* serviceUuid = The service's ID
* characteristicUuid = The characteristic's ID
* isNotification is only required on Android. True (or null) means notification will be enabled. False means indication will be enabled.

```javascript
{
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "serviceUuid": "180d",
  "characteristicUuid": "2a37",
  "isNotification" : true
}
```

##### Success #####
* status => subscribed = Subscription has started
* status => subscribedResult = Subscription result has been received
	* serviceUuid = Service UUID
	* characteristicUuid = Characteristic UUID
	* value = Base64 encoded string of bytes. Use bluetoothle.encodedStringToBytes(obj.value) to convert to a unit8Array. See characteristic's specification and example below on how to correctly parse this.

```javascript
{
  "status": "subscribed",
  "characteristicUuid": "2a37",
  "name": "Polar H7 3B321015",
  "serviceUuid": "180d",
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63"
}

{
  "status": "subscribedResult",
  "value": "BkY=",
  "characteristicUuid": "2a37",
  "name": "Polar H7 3B321015",
  "serviceUuid": "180d",
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63"
}
```



### unsubscribe ###
Unsubscribe to a particular service's characteristic.

```javascript
bluetoothle.unsubscribe(unsubscribeSuccess, unsubscribeError, params);
```

##### Params #####
* address = The address/identifier provided by the scan's return object
* serviceUuid = The service's ID
* characteristicUuid = The characteristic's ID

```javascript
{
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "serviceUuid": "180d",
  "characteristicUuid": "2a37"
}
```

##### Success #####
* status => unsubscribed = Characteristics unsubscribed
	* serviceUuid = Service UUID
	* characteristicUuid = Characteristic UUID

```javascript
{
  "status": "unsubscribed",
  "characteristicUuid": "2a37",
  "name": "Polar H7 3B321015",
  "serviceUuid": "180d",
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63"
}
```



### write ### Needs Update
Write a particular service's characteristic.

```javascript
bluetoothle.write(writeSuccess, writeError, params);
```

##### Params #####
Value is a base64 encoded string of bytes to write. Use bluetoothle.bytesToEncodedString(bytes) to convert to base64 encoded string from a unit8Array.
To write without response, set type to "noResponse". Any other value will default to write with response. Note, no callback will occur on write without response.
```javascript
//Note, this example doesn't actually work since it's read only characteristic
{"value":"","serviceUuid":"180F","characteristicUuid":"2A19","type":"noResponse"}
```

##### Success #####
Value is a base64 encoded string of written bytes. Use bluetoothle.encodedStringToBytes(obj.value) to convert to a unit8Array. See characteristic's specification and example below on how to correctly parse this.

```javascript
//Write
{"status":"written","serviceUuid":"180F","characteristicUuid":"2A19","value":""}
```



### readDescriptor ###
Read a particular characterist's descriptor

```javascript
bluetoothle.read(readDescriptorSuccess, readDescriptorError, params);
```

##### Params #####
* address = The address/identifier provided by the scan's return object
* serviceUuid = The service's ID
* characteristicUuid = The characteristic's ID
* descriptorUuid = The descriptor's ID

```javascript
{
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "serviceUuid": "180d",
  "characteristicUuid": "2a37",
  "descriptorUuid": "2902"
}
```

##### Success #####
* status => readDescriptor = Descriptor was read
	* serviceUuid = Service UUID
	* characteristicUuid = Characteristic UUID
	* descriptorUuid = Descriptor UUID
	* value = Base64 encoded string of bytes. Use bluetoothle.encodedStringToBytes(obj.value) to convert to a unit8Array.

```javascript
{
  "status": "readDescriptor",
  "serviceUuid": "180d",
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "characteristicUuid": "2a37",
  "value": "AQAAAAAAAAA=",
  "name": "Polar H7 3B321015",
  "descriptorUuid": "2902"
}```



### writeDescriptor ### Needs Update
Write a particular characteristic's descriptor. Unable to write characteristic configuration directly to keep in line with iOS implementation. Instead use subscribe/unsubscribe, which will automatically enable/disable notification. ***Note, limited testing and likely needs to be made more generic***

```javascript
bluetoothle.write(writeDescriptorSuccess, writeDescriptorError, params);
```

##### Params #####
Value is a base64 encoded string of bytes to write. Use bluetoothle.bytesToEncodedString(bytes) to convert to base64 encoded string from a unit8Array.

```javascript
{"serviceUuid":"180D","characteristicUuid":"2A37","descriptorUuid":"2902","value":"EnableNotification"}
```

##### Success #####
Value is a base64 encoded string of written bytes. Use bluetoothle.encodedStringToBytes(obj.value) to convert to a unit8Array. 

```javascript
{"status":"writeDescriptor","serviceUuid":"180D","characteristicUuid":"2A37", "descriptorUuid":"2902","value":"EnableNotification"}
```



### rssi ###
Read RSSI of a connected device. RSSI is also returned with scanning.

```javascript
bluetoothle.rssi(rssiSuccess, rssiError, params);
```

#### Params ####
* address = The address/identifier provided by the scan's return object

```javascript
{
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63"
}
```

##### Success #####
* status => rssi = RSSI retrieved
  * rssi = signal strength

```javascript
{
  "status": "rssi",
  "rssi": -50,
  "name": "Polar H7 3B321015",
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63"
}
```


### isInitialized ###
Determine whether the adapter is initialized. No error callback. Returns true or false

```javascript
bluetoothle.isInitialized(isInitialized);
```

##### Success Return #####
* status => isInitialized = true/false

```javascript
{
  "isInitialized": true
}
```


### isEnabled ###
Determine whether the adapter is enabled. No error callback

```javascript
bluetoothle.isEnabled(isEnabled);
```

##### Success #####
* status => isEnabled = true/false

```javascript
{
  "isEnabled": true
}
```



### isScanning ###
Determine whether the adapter is initialized. No error callback. Returns true or false

```javascript
bluetoothle.isScanning(isScanning);
```

##### Return #####
* status => isScanning = true/false

```javascript
{
  "isScanning": false
}
```



### isConnected ###
Determine whether the device is connected. No error callback. Returns true or false

```javascript
bluetoothle.isConnected(isConnected);
```

#### Params ####
* address = The address/identifier provided by the scan's return object

```javascript
{
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63"
}
```

##### Success #####
* status => isConnected = true/false

```javascript
{
  "name": "Polar H7 3B321015",
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "isConnected": false
}
```



### isDiscovered ###
Determine whether the device's characteristics and descriptors have been discovered. No error callback. Android support only. iOS will return false.

```javascript
bluetoothle.isDiscovered(isDiscovered);
```

#### Params ####
* address = The address/identifier provided by the scan's return object

```javascript
{
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63"
}
```

##### Success #####
* status => isDiscovered = true/false

```javascript
{
  "name": "Polar H7 3B321015",
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "isDiscovered": false
}
```



### requestConnectionPriority ###
Request a change in the connection priority to improve throughput when transfer large amounts of data via BLE. Android support only. iOS will return error.

```javascript
bluetoothle.requestConnectionPriority(success, error, params);
```

#### Params ####
* address = The address/identifier provided by the scan's return object
* connectionPriority = low / balanced / high

```javascript
{
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "connectionPriority" : "balanced"
}
```

##### Success #####
* status => connectionPriorityRequested = true

```javascript
{
  "name": "Polar H7 3B321015",
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "status" : "connectionPriorityRequested"
}
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
1. Create an out of the box Cordova application
2. Copy and paste the /www folder in /example to your Cordova application
3. Install the plugin using the steps above
4. Install the console plugin using: cordova plugin add org.apache.cordova.console
5. Modify write and writeDescriptor functions with actual values.

## Data Parsing Example ##
```javascript
if (obj.status == "subscribedResult")
{
  //Turn the base64 string into an array of unsigned 8bit integers
  var bytes = bluetoothle.encodedStringToBytes(obj.value);
  if (bytes.length === 0)
  { 
    return;
  }
  
  //NOTE: Follow along to understand how the parsing works
  //https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml

  //First byte provides instructions on what to do with the remaining bytes
  var flag = bytes[0];

  //Offset from beginning of the array
  var offset = 1;

  //If the first bit of the flag is set, the HR is in 16 bit form
  if ((flag & 0x01) == 1)
  {
      //Extract second and third bytes and convert to 16bit unsigned integer
      var u16bytesHr = bytes.buffer.slice(offset, offset + 2);
      var u16Hr = new Uint16Array(u16bytesHr)[0];
      //16 bits takes up 2 bytes, so increase offset by two
      offset += 2;
  }
  //Else the HR is in 8 bit form
  else
  {
      //Extract second byte and convert to 8bit unsigned integer
      var u8bytesHr = bytes.buffer.slice(offset, offset + 1);
      var u8Hr = new Uint8Array(u8bytesHr)[0];
  
      //Or I believe I could just do this: var u8Hr = u8bytesHr[offset]
  
      //8 bits takes up 1 byte, so increase offset by one
      offset += 1;
  }

  //NOTE: I'm ignoring the second and third bit because I'm not interested in the sensor contact, and it doesn't affect the offset

  //If the fourth bit is set, increase the offset to skip over the energy expended information
  if ((flag & 0x08) == 8)
  {
      offset += 2;
  }

  //If the fifth bit is set, get the RR interval(s)
  if ((flag & 0x10) == 16)
  {
      //Number of rr intervals
      var rrCount = (bytes.length - offset) / 2;
  
      for (var i = rrCount - 1; i >= 0; i--)
      {
          //Cast to 16 bit unsigned int
          var u16bytesRr = bytes.buffer.slice(offset, offset + 2);
          var u16Rr = new Uint16Array(u16bytesRr)[0];
          //Increase offset
          offset += 2;
      }
  }
}
```


## More information ##
* Author: Rand Dusing
* Website: http://www.randdusing.com/
* Email: {firstAndLastName}@gmail.com

## License ##
The source files included in the repository are released under the Apache License, Version 2.0.
