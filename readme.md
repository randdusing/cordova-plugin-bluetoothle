# Cordova Bluetooth LE Plugin
This plugin allows you to interact with Bluetooth LE devices on Android, iOS, and partially on Windows.


## Available for Hire ##
I'm available for part time contracting work. This would really help keep the project alive and up to date. You can contact me via: <randdusing@gmail.com>, [Facebook](https://www.facebook.com/randdusing), [LinkedIn](https://www.linkedin.com/in/randdusing) or [Twitter](https://twitter.com/randdusing) for more information.


## Requirements ##

* Cordova 3.0.0 or higher
* Android 4.3 or higher, Android Cordova library 5.0.0 or higher, target Android API 23 or higher
* iOS 7 or higher
* Windows Phone 8.1 (Tested on Nokia Lumia 630)
* Device hardware must be certified for Bluetooth LE. i.e. Nexus 7 (2012) doesn't support Bluetooth LE even after upgrading to 4.3 (or higher) without a modification
* List of devices: http://www.bluetooth.com/Pages/Bluetooth-Smart-Devices-List.aspx


## Limitations / Issues ##

* Windows support is limited
* Disconnecting and quickly reconnecting causes issues on Android. The device becomes connected again, but then quickly disconnects. Adding a timeout before reconnecting fixed the issue for me. I'm not sure if this is a problem with the plugin, Android's Bluetooth LE implementation or Android itself.
* For subscribing, indication hasn't been tested.


## Upgrade 2.x to 3.x ##

* Instead of specifying serviceUuids, serviceUuid, characteristicUuid, etc in the params, use services, service, characteristic, etc. Check out the scan-related, discovery-related and read/write/subscribe operation functions for more info. Discovery related functions will also return uuid properties instead of serviceUuid, characteristicUuid or descriptorUuid.
* The connecting and disconnecting events were removed.


## To Do ##

* Support for Peripheral/Server role
* Full support for Windows
* Resolve all callbacks on disconnected event. Currently it's only partially supported.


## Using AngularJS ##

Check out ng-cordova-bluetoothle [here!](https://github.com/randdusing/ng-cordova-bluetoothle)  
If timeouts or queueing is needed, please check out the Angular wrapper and its example. I don't plan to implement queuing within the plugin itself anymore.


## Installation ##

Cordova  
```cordova plugin add cordova-plugin-bluetoothle```

PhoneGap Build  
```<gap:plugin name="cordova-plugin-bluetoothle" source="npm" />```


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


Updating the plugin for iOS sometimes causes BluetoothLePlugin.m to be removed from the Compile Sources and CoreBluetooth.framework to be removed from Link Binary with Libraries. To fix:
1. Click your project to open the "properties" window  
2. Click your target  
3. Click Build Phases  
4. Ensure BluetoothLePlugin.m is added to the Compile Sources  
5. Ensure CoreBluetooth.framework is added to the Link Binary with Libraries  


## Installation Quirks (Android) ##
The latest version of the plugin requires you to set the Android target API to a minimum of 23 to support permission requirements for scanning. If you can't target 23, please use plugin version 2.4.0 or below.


## Discovery Quirks (iOS vs Android) ##
Discovery works differently between Android and iOS. In Android, a single function is called to initiate discovery of all services, characteristics and descriptors on the device. In iOS, a single function is called to discover the device's services. Then another function to discover the characteristics of a particular service. And then another function to discover the descriptors of a particular characteristic. The [Device plugin](http://docs.phonegap.com/en/edge/cordova_device_device.md.html#Device) should be used to properly determine the device and make the proper calls if necessary. Additionally, if a device is disconnected, it must be rediscovered when running on iOS. **iOS now supports Android style discovery, but use with caution. It's a bit buggy on iOS8, but seems to work fine on iOS9.**


## UUIDs ##
UUIDs can be 16 bits or 128 bits. The "out of the box" UUIDs from the link below are 16 bits.
Since iOS returns the 16 bit version of the "out of the box" UUIDs even if a 128 bit UUID was used in the parameters, the 16 bit version should always be used for the "out of the box" UUIDs for consistency.
Android on the other hand only uses the 128 bit version, but the plugin will automatically convert 16 bit UUIDs to the 128 bit version on input and output. For a list of out of the box UUIDS, see [Bluetooth Developer Portal](https://developer.bluetooth.org/gatt/services/Pages/ServicesHome.aspx)


## Advertisement Data / MAC Address ##
On iOS, the MAC address is hidden from the advertisement packet, and the address returned from the scanResult is a generated, device-specific address. This is a problem when using devices like iBeacons where you need the MAC Address. Fortunately the CLBeacon class can be used for this, but unfortunately it's not supported in this plugin.
One option is to set Manufacturer Specific Data in the advertisement packet if that's possible in your project.
Another option is to connect to the device and use the "Device Information" (0x180A) service, but connecting to each device is much more energy intensive than scanning for advertisement data.
See the following StackOverflow posts for more info: [here](https://stackoverflow.com/questions/18973098/get-mac-address-of-bluetooth-low-energy-peripheral) and [here](https://stackoverflow.com/questions/22833198/get-advertisement-data-for-ble-in-ios)


## Emulator ##
Neither Android nor iOS support Bluetooth on emulators, so you'll need to test on a real device.


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
* [bluetoothle.discover] (#discover)
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
* [bluetoothle.isDiscovered] (#isdiscovered)
* [bluetoothle.hasPermission] (#haspermission) (Android)
* [bluetoothle.requestPermission] (#requestpermission) (Android)
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
* connect - Connection attempt failed (Is the device address correct?)
* reconnect - Reconnection attempt failed (Was the device ever connected?)
* discover - Failed to discover device (Is the device already discovered or discovering?)
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
* rssi - Failed to read RSSI (Not sure what would cause this)
* mtu - Failed to set MTU (Is device Android?)
* requestConnectionPriority - Failed to request connection priority (Is the device iOS?)
* arguments - Invalid arguments (Check arguments)
* neverConnected - Device never connected (Call connect, not reconnect)
* isNotDisconnected - Device is not disconnected (Don't call connect or reconnect while connected)
* isNotConnected - Device isn't connected (Don't call discover or any read/write operations)
* isDisconnected - Device is disconnected (Don't call disconnect)

For example:
```javascript
{"error":"startScan", "message":"Scanning already started"}
```



## Permissions (Android) ##
Characteristics can have the following different permissions: read, readEncrypted, readEncryptedMITM, write, writeEncrypted, writeEncryptedMITM, writeSigned, writeSignedMITM. Unfortuately, the getProperties() call always seems to return 0, which means no properties are set. Not sure if this is an issue with my mobile device or that all the Bluetooth devices just don't have the properties set. If the characteristic has a permission, it will exist as a key in the characteristic's permissions object. See discovery().

[Android Docs](https://developer.android.com/reference/android/bluetooth/BluetoothGattCharacteristic.html)


## Properties ##
Characteristics can have the following different properties: broadcast, read, writeWithoutResponse, write, notify, indicate, authenticatedSignedWrites, extendedProperties, notifyEncryptionRequired, indicateEncryptionRequired. If the characteristic has a property, it will exist as a key in the characteristic's properties object. See discovery() or characteristics()

[iOS Docs](https://developer.android.com/reference/android/bluetooth/BluetoothGattCharacteristic.html) and 
[Android Docs](https://developer.apple.com/library/mac/documentation/CoreBluetooth/Reference/CBCharacteristic_Class/translated_content/CBCharacteristic.html#//apple_ref/c/tdef/CBCharacteristicProperties)



## Life Cycle ##

1. initialize
2. scan (if device address is unknown)
3. connect
4. discover OR services/characteristics/descriptors (iOS)
5. read/subscribe/write characteristics AND read/write descriptors
6. disconnect
7. close


## Functions ##

### initialize ###
Initialize Bluetooth on the device. Must be called before anything else. Callback will continuously be used whenever Bluetooth is enabled or disabled. Note: Although Bluetooth initialization could initially be successful, there's no guarantee whether it will stay enabled. Each call checks whether Bluetooth is disabled. If it becomes disabled, the user must connect to the device, start a read/write operation, etc again. If Bluetooth is disabled, you can request the user to enable it by setting the request property to true. The `request` property in the `params` argument is optional and defaults to false. Also, this function should only be called once.

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
* status => disabled = Bluetooth is disabled

```javascript
{
  "status": "enabled"
}
```



### enable ###
Enable Bluetooth on the device. Android support only.

```javascript
bluetoothle.enable(enableSuccess, enableError);
```

##### Error #####
* errorDisable = Bluetooth isn't disabled, so unable to enable.
* errorEnable = Immediate failure of the internal enable() function due to Bluetooth already on or airplane mode, so unable to enable.

##### Success #####
The successCallback isn't actually used. Listen to initialize callbacks for change in Bluetooth state. A successful enable will return a status => enabled via initialize success callback.



### disable ###
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
Scan for Bluetooth LE devices. Since scanning is expensive, stop as soon as possible. The Cordova app should use a timer to limit the scan interval. Also, Android uses an AND operator for filtering, while iOS uses an OR operator. Android API >= 23 requires ACCESS_COARSE_LOCATION permissions to find unpaired devices. Permissions can be requested by using the hasPermission and requestPermission functions.

```javascript
bluetoothle.startScan(startScanSuccess, startScanError, params);
```

##### Params #####
* services = An array of service IDs to filter the scan or empty array / null
* iOS - See [iOS Docs](https://developer.apple.com/library/ios/documentation/CoreBluetooth/Reference/CBCentralManager_Class/#//apple_ref/doc/constant_group/Peripheral_Scanning_Options)
  * allowDuplicates = True/false to allow duplicate advertisement packets, defaults to false.
* Android - See [Android Docs](http://developer.android.com/reference/android/bluetooth/le/ScanSettings.html)
  * scanMode - Defaults to Low Power. Available from API21 / API 23.
  * matchMode - Defaults to Aggressive. Available from API23.
  * matchNum - Defaults to One Advertisement. Available from API23.
  * callbackType - Defaults to All Matches. Available from API21 / API 23. *Note: Careful using this one. When using CALLBACK_TYPE_FIRST_MATCH on a Nexus 7 on API 21, I received a Feature Unsupported error when starting the scan.

```javascript
{
  "services": [
    "180D",
    "180F"
  ],
  "allowDuplicates": true
  "scanMode": bluetoothle.SCAN_MODE_LOW_LATENCY,
  "matchMode": bluetoothle.MATCH_MODE_AGGRESSIVE,
  "matchNum": bluetoothle.MATCH_NUM_MAX_ADVERTISEMENT,
  "callbackType": bluetoothle.CALLBACK_TYPE_ALL_MATCHES,
}
```

##### Success #####
* status => scanStarted = Scan has started
* status => scanResult = Scan has found a device
  * name = the device's display name
  * address = the device's address / identifier for connecting to the object
  * rssi = signal strength
  * advertisement = advertisement data in encoded string of bytes, use bluetoothle.encodedStringToBytes() (Android)
  * advertisement = advertisement hash with the keys specified [here](https://developer.apple.com/library/ios/documentation/CoreBluetooth/Reference/CBCentralManagerDelegate_Protocol/#//apple_ref/doc/constant_group/Advertisement_Data_Retrieval_Keys) (iOS)

```javascript
{
  "status": "scanStarted"
}

{
  "status": "scanResult",
  "advertisement": "awArG05L", //Android
  "advertisement": { //iOS
    "serviceUuids": [
      "180D"
    ],
    "manufacturerData": "awAvFFZY",
    "txPowerLevel": 0,
    "overflowServiceUuids": [
    ],
    "isConnectable": true,
    "solicitedServiceUuids": [
    ],
    "serviceData": {
    },
    "localName": "Polar H7 3B321015"
  },
  "rssi": -58,
  "name": "Polar H7 3B321015",
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63"
}
```



### stopScan ###
Stop scan for Bluetooth LE devices. Since scanning is expensive, stop as soon as possible. The app should use a timer to limit the scanning time.

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
Retrieved paired Bluetooth LE devices. Yes, this function should be renamed, but I went with iOS's naming. In iOS, devices that are "paired" to will not return during a normal scan. Callback is "instant" compared to a scan. I haven't been able to get UUID filtering working on Android, so it returns all paired BLE devices.

```javascript
bluetoothle.retrieveConnected(retrieveConnectedSuccess, retrieveConnectedError, params);
```

##### Params #####
* services = An array of service IDs to filter the retrieval by. If no service IDs are specified, no devices will be returned. Ignored on Android

```javascript
{
  "services": [
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
Connect to a Bluetooth LE device. The app should use a timer to limit the connecting time in case connecting is never successful. Once a device is connected, it may disconnect without user intervention. The original connection callback will be called again and receive an object with status => disconnected. To reconnect to the device, use the reconnect method. If a timeout occurs, the connection attempt should be canceled using disconnect(). For simplicity, I recommend just using connect() and close(), don't use reconnect() or disconnect().

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
* status => connected = Device connected
* status => disconnected = Device unexpectedly disconnected

```javascript
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
Reconnect to a previously connected Bluetooth device. The app should use a timer to limit the connecting time. If a timeout occurs, the reconnection attempt should be canceled using disconnect() or close().

```javascript
bluetoothle.reconnect(reconnectSuccess, reconnectError, params);
```

##### Params #####
* address = The address/identifier provided by the scan's return object

```javascript
{
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63"
}
```

##### Success #####
* status => connected = Device connected
* status => disconnected = Device unexpectedly disconnected

```javascript
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
Disconnect from a Bluetooth LE device. It's simpler to just call close().

```javascript
bluetoothle.disconnect(disconnectSuccess, disconnectError, params);
```

##### Params #####
* address = The address/identifier provided by the scan's return object

```javascript
{
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63"
}
```

##### Success #####
* status => disconnected = Device disconnected

```javascript
{
  "name": "Polar H7 3B321015",
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "status": "disconnected"
}
```


### close ###
Close/dispose a Bluetooth LE device. Prior to 2.7.0, you needed to disconnect to the device before closing, but this is no longer the case.

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
Discover all the devices services, characteristics and descriptors. Doesn't need to be called again after disconnecting and then reconnecting. If using iOS, you shouldn't use discover and services/characteristics/descriptors on the same device. There seems to be an issue with calling discover on iOS8 devices, so use with caution.

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
* uuid = Service's uuid
* characteristics = Array of characteristic objects below

Characteristic Object:
* uuid = Characteristic's uuid
* properties = If the property is defined as a key, the characteristic has that property
* permissions = If the permission is defined as a key, the character has that permission
* descriptors = Array of descriptor objects below

Descriptor Object:
* uuid = Descriptor's uuid

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
          "uuid": "2a00", // [Device Name](https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.gap.device_name.xml)
          "properties": {
            "write": true,
            "writeWithoutResponse": true,
            "read": true
          }
        },
        {
          "descriptors": [

          ],
          "uuid": "2a01", // [Appearance](https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.gap.appearance.xml)
          "properties": {
            "read": true
          }
        },
        {
          "descriptors": [

          ],
          "uuid": "2a02", // [Peripheral Privacy Flag](https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.gap.peripheral_privacy_flag.xml)
          "properties": {
            "read": true
          }
        },
        {
          "descriptors": [

          ],
          "uuid": "2a03", // [Reconnection Address](https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.gap.reconnection_address.xml)
          "properties": {
            "write": true
          }
        },
        {
          "descriptors": [

          ],
          "uuid": "2a04", // [Pheripheral Preferred Connection Parameters](https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.gap.peripheral_preferred_connection_parameters.xml)
          "properties": {
            "read": true
          }
        }
      ],
      "uuid": "1800" // [Generic Access](https://developer.bluetooth.org/gatt/services/Pages/ServiceViewer.aspx?u=org.bluetooth.service.generic_access.xml)
    },
    {
      "characteristics": [
        {
          "descriptors": [
            {
              "uuid": "2902"
            }
          ],
          "uuid": "2a05", // [Service Changed](https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.gatt.service_changed.xml)
          "properties": {
            "indicate": true
          }
        }
      ],
      "uuid": "1801" // [Generic Attribute](https://developer.bluetooth.org/gatt/services/Pages/ServiceViewer.aspx?u=org.bluetooth.service.generic_attribute.xml)
    },
    {
      "characteristics": [
        {
          "descriptors": [
            {
              "uuid": "2902"
            }
          ],
          "uuid": "2a37", // [Heart Rate Measurement](https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml)
          "properties": {
            "notify": true
          }
        },
        {
          "descriptors": [

          ],
          "uuid": "2a38", // [Body Sensor Location](https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.body_sensor_location.xml)
          "properties": {
            "read": true
          }
        }
      ],
      "uuid": "180d" // [Heart Rate](https://developer.bluetooth.org/gatt/services/Pages/ServiceViewer.aspx?u=org.bluetooth.service.heart_rate.xml)
    },
    {
      "characteristics": [
        {
          "descriptors": [

          ],
          "uuid": "2a23", // [System ID](https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.system_id.xml)
          "properties": {
            "read": true
          }
        },
        {
          "descriptors": [

          ],
          "uuid": "2a24", // [Model Number String](https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.model_number_string.xml)
          "properties": {
            "read": true
          }
        },
        {
          "descriptors": [

          ],
          "uuid": "2a25", // [Serial Number String](https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.serial_number_string.xml)
          "properties": {
            "read": true
          }
        },
        {
          "descriptors": [

          ],
          "uuid": "2a26", // [Firmware Revision String](https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.firmware_revision_string.xml)
          "properties": {
            "read": true
          }
        },
        {
          "descriptors": [

          ],
          "uuid": "2a27", // [hardware Revision String](https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.hardware_revision_string.xml)
          "properties": {
            "read": true
          }
        },
        {
          "descriptors": [

          ],
          "uuid": "2a28", // [Software Revision String](https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.software_revision_string.xml)
          "properties": {
            "read": true
          }
        },
        {
          "descriptors": [

          ],
          "uuid": "2a29", // [Manufacturer Name String](https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.manufacturer_name_string.xml)
          "properties": {
            "read": true
          }
        }
      ],
      "uuid": "180a" // [Device Information](https://developer.bluetooth.org/gatt/services/Pages/ServiceViewer.aspx?u=org.bluetooth.service.device_information.xml)
    },
    {
      "characteristics": [
        {
          "descriptors": [

          ],
          "uuid": "2a19", // [Battery Level](https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.battery_level.xml)
          "properties": {
            "read": true
          }
        }
      ],
      "uuid": "180f" // [Battery Service](https://developer.bluetooth.org/gatt/services/Pages/ServiceViewer.aspx?u=org.bluetooth.service.battery_service.xml)
    },
    {
      "characteristics": [
        {
          "descriptors": [

          ],
          "uuid": "6217ff4c-c8ec-b1fb-1380-3ad986708e2d",
          "properties": {
            "read": true
          }
        },
        {
          "descriptors": [
            {
              "uuid": "2902"
            }
          ],
          "uuid": "6217ff4d-91bb-91d0-7e2a-7cd3bda8a1f3",
          "properties": {
            "write": true,
            "indicate": true
          }
        }
      ],
      "uuid": "6217ff4b-fb31-1140-ad5a-a45545d7ecf3"
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
* services = An array of service IDs to filter the scan or empty array / null

```javascript
{
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "services": [

  ]
}
```

##### Success #####
* status => services = Services discovered
  * services = Array of service UUIDS

```javascript
{
  "status": "services",
  "services": [
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
* service = Service UUID
* characteristics = An array of characteristic IDs to discover or empty array / null

```javascript
{
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "service": "180d",
  "characteristics": [

  ]
}
```

##### Success #####
* status => characteristics = Characteristics discovered
  * uuid = Service UUID
  * characteristics = Array of characteristics
    * properties = Object of defined properties
    * uuid = Characteristic UUID

```javascript
{
  "status": "characteristics",
  "characteristics": [
    {
      "properties": {
        "notify": true
      },
      "uuid": "2a37"
    },
    {
      "properties": {
        "read": true
      },
      "uuid": "2a38"
    }
  ],
  "name": "Polar H7 3B321015",
  "service": "180d",
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
* service = The service's ID
* characteristic = The characteristic's ID

```javascript
{
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "service": "180d",
  "characteristic": "2a37"
}
```

##### Success #####
* status => descriptors = Descriptors discovered
  * service = Service UUID
  * characteristic = characteristic UUID
  * descriptors = Array of Descriptor UUIDs

```javascript
{
  "status": "descriptors",
  "descriptors": [
    "2902"
  ],
  "characteristic": "2a37",
  "name": "Polar H7 3B321015",
  "service": "180d",
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
* service = The service's UUID
* characteristic = The characteristic's UUID

```javascript
{
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "service": "180d",
  "characteristic": "2a38"
}
```

##### Success #####
* status => read = Characteristics read
  * service = Service UUID
  * characteristic = Characteristic UUID
  * value = Base64 encoded string of bytes. Use bluetoothle.encodedStringToBytes(obj.value) to convert to a unit8Array. See characteristic's specification and example below on how to correctly parse this.

```javascript
{
  "status": "read",
  "value": "AQ==",
  "characteristic": "2a38",
  "name": "Polar H7 3B321015",
  "service": "180d",
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
* service = The service's UUID
* characteristic = The characteristic's UUID
* isNotification is only required on Android. True (or null) means notification will be enabled. False means indication will be enabled.

```javascript
{
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "service": "180d",
  "characteristic": "2a37",
  "isNotification" : true
}
```

##### Success #####
* status => subscribed = Subscription has started
* status => subscribedResult = Subscription result has been received
  * service = Service UUID
  * characteristic = Characteristic UUID
  * value = Base64 encoded string of bytes. Use bluetoothle.encodedStringToBytes(obj.value) to convert to a unit8Array. See characteristic's specification and example below on how to correctly parse this.

```javascript
{
  "status": "subscribed",
  "characteristic": "2a37",
  "name": "Polar H7 3B321015",
  "service": "180d",
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63"
}

{
  "status": "subscribedResult",
  "value": "BkY=",
  "characteristic": "2a37",
  "name": "Polar H7 3B321015",
  "service": "180d",
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
* service = The service's UUID
* characteristic = The characteristic's UUID

```javascript
{
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "service": "180d",
  "characteristic": "2a37"
}
```

##### Success #####
* status => unsubscribed = Characteristics unsubscribed
  * service = Service UUID
  * characteristic = Characteristic UUID

```javascript
{
  "status": "unsubscribed",
  "characteristic": "2a37",
  "name": "Polar H7 3B321015",
  "service": "180d",
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63"
}
```



### write ###
Write a particular service's characteristic.

```javascript
bluetoothle.write(writeSuccess, writeError, params);
```

##### Params #####
* address = The address/identifier provided by the scan's return object
* service = The service's UUID
* characteristic = The characteristic's UUID
* value = Base64 encoded string

Value is a base64 encoded string of bytes to write. Use bluetoothle.bytesToEncodedString(bytes) to convert to base64 encoded string from a unit8Array.
To write without response, set type to "noResponse". Any other value will default to write with response. Note, no callback will occur on write without response.
```javascript
var string = "Hello World";
var bytes = bluetoothle.stringToBytes(string);
var encodedString = bluetoothle.bytesToEncodedString(encodedString);

//Note, this example doesn't actually work since it's read only characteristic
{"value":encodedString,"service":"180F","characteristic":"2A19","type":"noResponse","address":"ABC123"}
```

##### Success #####
Value is a base64 encoded string of written bytes. Use bluetoothle.encodedStringToBytes(obj.value) to convert to a unit8Array. See characteristic's specification and example below on how to correctly parse this.

```javascript
var returnObj = {"status":"written","service":"180F","characteristic":"2A19","value":"SGVsbG8gV29ybGQ=","address":"ABC123"}
var bytes = bluetoothle.encodedStringToBytes(returnObj.value);
var string = bluetoothle.bytesToString(bytes); //This should equal Hello World!
```



### readDescriptor ###
Read a particular characterist's descriptor

```javascript
bluetoothle.read(readDescriptorSuccess, readDescriptorError, params);
```

##### Params #####
* address = The address/identifier provided by the scan's return object
* service = The service's ID
* characteristic = The characteristic's ID
* descriptor = The descriptor's ID

```javascript
{
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "service": "180d",
  "characteristic": "2a37",
  "descriptor": "2902"
}
```

##### Success #####
* status => readDescriptor = Descriptor was read
  * service = Service UUID
  * characteristic = Characteristic UUID
  * descriptor = Descriptor UUID
  * value = Base64 encoded string of bytes. Use bluetoothle.encodedStringToBytes(obj.value) to convert to a unit8Array.

```javascript
{
  "status": "readDescriptor",
  "service": "180d",
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "characteristic": "2a37",
  "value": "AQAAAAAAAAA=",
  "name": "Polar H7 3B321015",
  "descriptor": "2902"
}
```



### writeDescriptor ###
Write a particular characteristic's descriptor. Unable to write characteristic configuration directly to keep in line with iOS implementation. Instead use subscribe/unsubscribe, which will automatically enable/disable notification. ***Note, limited testing and likely needs to be made more generic***

```javascript
bluetoothle.writeDescriptor(writeDescriptorSuccess, writeDescriptorError, params);
```

##### Params #####
* address = The address/identifier provided by the scan's return object
* service = The service's ID
* characteristic = The characteristic's ID
* descriptor = The descriptor's ID
* value - Base64 encoded string

Value is a base64 encoded string of bytes to write. Use bluetoothle.bytesToEncodedString(bytes) to convert to base64 encoded string from a unit8Array.

```javascript
var string = "Hello World";
var bytes = bluetoothle.stringToBytes(string);
var encodedString = bluetoothle.bytesToEncodedString(encodedString);

{"service":"180D","characteristic":"2A37","descriptor":"2902","value":encodedString,"address":"ABC123"}
```

##### Success #####
Value is a base64 encoded string of written bytes. Use bluetoothle.encodedStringToBytes(obj.value) to convert to a unit8Array.

```javascript
{"status":"writeDescriptor","service":"180D","characteristic":"2A37", "descriptor":"2902","value":"SGVsbG8gV29ybGQ","address":"ABC123"}
var bytes = bluetoothle.encodedStringToBytes(returnObj.value);
var string = bluetoothle.bytesToString(bytes); //This should equal Hello World!
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


### mtu ###
Set MTU of a connected device. Android only.

```javascript
bluetoothle.mtu(mtuSuccess, mtuError, params);
```

#### Params ####
* address = The address/identifier provided by the scan's return object
* mtu - Integer value mtu should be set to

```javascript
{
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "mtu" : 50
}
```

##### Success #####
* status => mtu = MTU set
  * mtu = mtu value

```javascript
{
  "status": "mtu",
  "mtu": 50,
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
Determine whether the device is connected, or error if not initialized or never connected to device.

```javascript
bluetoothle.isConnected(isConnectedSuccess, isConnectedError, params);
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
Determine whether the device's characteristics and descriptors have been discovered, or error if not initialized or never connected to device.

```javascript
bluetoothle.isDiscovered(isDiscoveredSuccess, isDiscoveredError, params);
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



### hasPermission ###
Determine whether coarse location privileges are granted since scanning for unpaired devices requies it in Android API 23

```javascript
bluetoothle.hasPermission(hasPermissionSuccess);
```

##### Success #####
* status => hasPermission = true/false

```javascript
{
  "hasPermission": true
}
```



### requestPermission ###
Request coarse location privileges since scanning for unpaired devices requies it in Android API 23.

```javascript
bluetoothle.requestPermission(requestPermissionSuccess);
```

##### Success #####
* status => requestPermission = true/false

```javascript
{
  "requestPermission": true
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



## Example #
See the example provided with the [Angular Wrapper](https://github.com/randdusing/ng-cordova-bluetoothle)


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
* Email: <randdusing@gmail.com>
* Facebook: https://www.facebook.com/randdusing
* LinkedIn: https://www.linkedin.com/in/randdusing
* Twitter: https://twitter.com/randdusing

## License ##
The MIT License (MIT)

Copyright (c) 2016 Rand Dusing and contributors.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.