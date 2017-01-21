# Cordova Bluetooth LE Plugin
This plugin allows you to interact with Bluetooth LE devices on Android, iOS, and partially on Windows.


## Requirements ##

* Cordova 5.0.0 or higher
* Android 4.3 or higher, Android Cordova library 5.0.0 or higher, target Android API 23 or higher
* iOS 7 or higher
* Windows Phone 8.1 (Tested on Nokia Lumia 630)
* Device hardware must be certified for Bluetooth LE. i.e. Nexus 7 (2012) doesn't support Bluetooth LE even after upgrading to 4.3 (or higher) without a modification
* List of devices: http://www.bluetooth.com/Pages/Bluetooth-Smart-Devices-List.aspx


## Limitations / Issues ##

* Disconnect and quickly reconnecting can cause issues on Android. Add a small timeout.
* Indication type subscription hasn't been well .
* OS X is still experimental. I'm experiencing some problems, but may be related to Cordova itself.


## Upgrade 2.x to 3.x ##

* Instead of specifying serviceUuids, serviceUuid, characteristicUuid, etc in the params, use services, service, characteristic, etc. Check out the scan-related, discovery-related and read/write/subscribe operation functions for more info. Discovery related functions will also return uuid properties instead of serviceUuid, characteristicUuid or descriptorUuid.
* The connecting and disconnecting events were removed.

## Upgrade 3.x to 4.x ##

* Background modes aren't added automatically. See Installation Quirks (iOS) for details.


## To Do ##

* Expand Windows support
* Improved notifications on peripheral/server role between Android and iOS
* Code refactoring. It's getting pretty messy.


## Using AngularJS ##

Check out ng-cordova-bluetoothle [here!](https://github.com/randdusing/ng-cordova-bluetoothle)

If timeouts are needed, please check out the Angular wrapper and its example.


## Installation ##

Cordova

```cordova plugin add cordova-plugin-bluetoothle```

PhoneGap Build

```<gap:plugin name="cordova-plugin-bluetoothle" source="npm" />```


## Debugging ##
Check out these guides for lower level debugging on Android and iOS:

* [iOS](https://developer.apple.com/bug-reporting/profiles-and-logs/) - Scroll down to Bluetooth for iOS.
* [Android](http://blog.bluetooth.com/debugging-bluetooth-with-an-android-app/)

Apps like [LightBlue](https://itunes.apple.com/us/app/lightblue-explorer-bluetooth/id557428110?mt=8) are great for verifying Bluetooth LE behavior.


## Installation Quirks (Android) ##
The latest version of the plugin requires you to set the Android target API to a minimum of 23 to support permission requirements for scanning. If you can't target 23, please use plugin version 2.4.0 or below.


## Background Modes (iOS) ##
Background mode(s) are no longer added to your project's plist file by default. They can be added manually by editing the plist file, or you can use the following plugins: [cordova-plugin-background-mode-bluetooth-central](https://github.com/randdusing/cordova-plugin-background-mode-bluetooth-central) and/or [cordova-plugin-background-mode-bluetooth-peripheral](https://github.com/randdusing/cordova-plugin-background-mode-bluetooth-peripheral).

Scanning works differently in the background. There seem to be three different states:

1. Foreground - Service UUID doesn't need to be specified and allowDuplicates isn't ignored.
2. Background (Screen On) - Service UUID must be specified. allowDuplicates isn't ignored. Scanning is very slow!
3. Background (Screen Off) - Service UUID must be specified. allowDuplicates is ignored, so a device will only be returned once per scan. One possible work around is to start and stop the scan while in background. Unfortunately, there's a slim chance that the app may fall asleep again in between starting and stopping the scan.


## Discovery Quirks (iOS vs Android) ##
Discovery works differently between Android and iOS. In Android, a single function is called to initiate discovery of all services, characteristics and descriptors on the device. In iOS, a single function is called to discover the device's services. Then another function to discover the characteristics of a particular service. And then another function to discover the descriptors of a particular characteristic. The [Device plugin](https://github.com/apache/cordova-plugin-device) should be used to properly determine the device and make the proper calls if necessary. Additionally, if a device is disconnected, it must be rediscovered when running on iOS. **iOS now supports Android style discovery, but use with caution. It's a bit buggy on iOS8, but seems to work fine on iOS9.**


## Queuing (Android) ##
Read, write, subscribe, unsubscribe, readDescriptor and writeDescriptor queueing has been added to the master branch and will be part of the 4.1.0 release. If you'd like to try it out, install the plugin directly from GitHub using: ```cordova plugin https://github.com/randdusing/cordova-plugin-bluetoothle```


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
* [bluetoothle.retrieveConnected] (#retrieveconnected)
* [bluetoothle.bond] (#bond) (Android)
* [bluetoothle.unbond] (#unbond) (Android)
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
* [bluetoothle.writeQ] (#write)
* [bluetoothle.readDescriptor] (#readdescriptor)
* [bluetoothle.writeDescriptor] (#writedescriptor)
* [bluetoothle.rssi] (#rssi)
* [bluetoothle.mtu] (#mtu) (Android 5+)
* [bluetoothle.requestConnectionPriority] (#requestconnectionpriority) (Android 5+)
* [bluetoothle.isInitialized] (#isinitialized)
* [bluetoothle.isEnabled] (#isenabled)
* [bluetoothle.isScanning] (#isscanning)
* [bluetoothle.isBonded] (#isbonded) (Android)
* [bluetoothle.wasConnected] (#wasconnected)
* [bluetoothle.isConnected] (#isconnected)
* [bluetoothle.isDiscovered] (#isdiscovered)
* [bluetoothle.hasPermission] (#haspermission) (Android 6+)
* [bluetoothle.requestPermission] (#requestpermission) (Android 6+)
* [bluetoothle.isLocationEnabled] (#islocationenabled) (Android 6+)
* [bluetoothle.requestLocation] (#requestlocation) (Android 6+)
* [bluetoothle.initializePeripheral] (#initializeperipheral)
* [bluetoothle.addService] (#addservice)
* [bluetoothle.removeService] (#removeservice)
* [bluetoothle.removeAllServices] (#removeallservices)
* [bluetoothle.startAdvertising] (#startadvertising)
* [bluetoothle.stopAdvertising] (#stopadvertising)
* [bluetoothle.respond] (#respond)
* [bluetoothle.notify] (#notify)
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
* bond - Bond couldn't be formed (Is it already bonding? Is the device Android?)
* unbond - Bond couldn' be broken (Is it already unbonding? Is the device Android?)
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
* isBonded - Operation is unsupported. (Is the device Android?)

For example:
```javascript
{"error":"startScan", "message":"Scanning already started"}
```



## Permissions (Android) ##
Characteristics can have the following different permissions: read, readEncrypted, readEncryptedMITM, write, writeEncrypted, writeEncryptedMITM, writeSigned, writeSignedMITM. Unfortuately, the getProperties() call always seems to return 0, which means no properties are set. Not sure if this is an issue with my mobile device or that all the Bluetooth devices just don't have the properties set. If the characteristic has a permission, it will exist as a key in the characteristic's permissions object. See discovery().

[Android Docs](https://developer.android.com/reference/android/bluetooth/BluetoothGattCharacteristic.html)


## Properties ##
Characteristics can have the following different properties: broadcast, read, writeWithoutResponse, write, notify, indicate, authenticatedSignedWrites, extendedProperties, notifyEncryptionRequired, indicateEncryptionRequired. If the characteristic has a property, it will exist as a key in the characteristic's properties object. See discovery() or characteristics()

[Android Docs](https://developer.android.com/reference/android/bluetooth/BluetoothGattCharacteristic.html) and
[iOS Docs](https://developer.apple.com/library/mac/documentation/CoreBluetooth/Reference/CBCharacteristic_Class/translated_content/CBCharacteristic.html#//apple_ref/c/tdef/CBCharacteristicProperties)



## Central Life Cycle ##

1. initialize
2. scan (if device address is unknown)
3. connect
4. discover OR services/characteristics/descriptors (iOS)
5. read/subscribe/write characteristics AND read/write descriptors
6. disconnect
7. close



### initialize ###
Initialize Bluetooth on the device. Must be called before anything else. Callback will continuously be used whenever Bluetooth is enabled or disabled. Note: Although Bluetooth initialization could initially be successful, there's no guarantee whether it will stay enabled. Each call checks whether Bluetooth is disabled. If it becomes disabled, the user must connect to the device, start a read/write operation, etc again. If Bluetooth is disabled, you can request the user to enable it by setting the request property to true. The `request` property in the `params` argument is optional and defaults to false. The `restoreKey` property requires using the Bluetooth Central background mode. This function should only be called once.

```javascript
bluetoothle.initialize(initializeResult, params);
```

##### Params #####
* request = true / false (default) - Should user be prompted to enable Bluetooth
* statusReceiver = true (default) / false - Should change in Bluetooth status notifications be sent.
* restoreKey = A unique string to identify your app. Bluetooth Central background mode is required to use this, but background mode doesn't seem to require specifying the restoreKey.

```javascript
{
  "request": true,
  "statusReceiver": false,
  "restoreKey" : "bluetoothleplugin"
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
Scan for Bluetooth LE devices. Since scanning is expensive, stop as soon as possible. The Cordova app should use a timer to limit the scan interval. Also, Android uses an AND operator for filtering, while iOS uses an OR operator. Android API >= 23 requires ACCESS_COARSE_LOCATION permissions to find unpaired devices. Permissions can be requested by using the hasPermission and requestPermission functions. Android API >= 23 also requires location services to be enabled. Use ```isLocationEnabled``` to determine whether location services are enabled. If not enabled, use ```requestLocation``` to prompt the location services settings page.

```javascript
bluetoothle.startScan(startScanSuccess, startScanError, params);
```

##### Params #####
* services = An array of service IDs to filter the scan or empty array / null. This parameter is not supported on Windows platform yet.
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
  * advertisement = empty (Windows)

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



### bond ###
Bond with a device. The first success callback should always return with ```status == bonding```. If the bond is created, the callback will return again with ```status == bonded```. If the bonding popup is canceled or the wrong code is entered, the callback will return again with ```status == unbonded```. The device doesn't need to be connected to initiate bonding. Android support only.

```javascript
bluetoothle.bond(bondSuccess, bondError, params);
```

##### Params #####
* address = The address/identifier provided by the scan's return object

```javascript
{
  "address": "5A:94:4B:38:B3:FD"
}
```

##### Success #####
* status => bonded = Device is bonded
* status => bonding = Device is bonding
* status => unbonded = Device is unbonded

```javascript
{
  "name": "Hello World",
  "address": "5A:94:4B:38:B3:FD",
  "status": "bonded"
}

{
  "name": "Hello World",
  "address": "5A:94:4B:38:B3:FD",
  "status": "bonding"
}

{
  "name": "Hello World",
  "address": "5A:94:4B:38:B3:FD",
  "status": "unbonded"
}
```



### unbond ###
Unbond with a device. The success callback should always return with ```status == unbonded```. The device doesn't need to be connected to initiate bonding. Android support only.

```javascript
bluetoothle.unbond(unbondSuccess, unbondError, params);
```

##### Params #####
* address = The address/identifier provided by the scan's return object

```javascript
{
  "address": "5A:94:4B:38:B3:FD"
}
```

##### Success #####
* status => unbonded = Device is unbonded

```javascript
{
  "name": "Hello World",
  "address": "5A:94:4B:38:B3:FD",
  "status": "unbonded"
}
```



### connect ###
Connect to a Bluetooth LE device. The app should use a timer to limit the connecting time in case connecting is never successful. Once a device is connected, it may disconnect without user intervention. The original connection callback will be called again and receive an object with status => disconnected. To reconnect to the device, use the reconnect method. If a timeout occurs, the connection attempt should be canceled using disconnect(). For simplicity, I recommend just using connect() and close(), don't use reconnect() or disconnect().

```javascript
bluetoothle.connect(connectSuccess, connectError, params);
```

##### Params #####
* address = The address/identifier provided by the scan's return object
* autoConnect = Automatically connect as soon as the remote device becomes available (Android)

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
**Starting with iOS 10, disconnecting before closing seems required!**

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
**Starting with iOS 10, disconnecting before closing seems required!**

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
Discover all the devices services, characteristics and descriptors. Doesn't need to be called again after disconnecting and then reconnecting. If using iOS, you shouldn't use discover and services/characteristics/descriptors on the same device. There seems to be an issue with calling discover on iOS8 devices, so use with caution. On some Android versions, the discovered services may be cached for a device.  Subsequent discover events will make use of this cache.  If your device's services change, set the clearCache parameter to force Android to re-discover services.

```javascript
bluetoothle.discover(discoverSuccess, discoverError, params);
```

##### Params #####
* address = The address/identifier provided by the scan's return object
* clearCache = true / false (default) Force the device to re-discover services, instead of relying on cache from previous discovery (Android only)

```javascript
{
  "address": "00:22:D0:3B:32:10",
  "clearCache": true
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
  "value": "UmVhZCBIZWxsbyBXb3JsZA==", //Read Hello World
  "characteristic": "2a38",
  "name": "Polar H7 3B321015",
  "service": "180d",
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63"
}
```


### subscribe ###
Subscribe to a particular service's characteristic. Once a subscription is no longer needed, execute unsubscribe in a similar fashion. The Client Configuration descriptor will automatically be written to enable notification/indication based on the characteristic's properties.

```javascript
bluetoothle.subscribe(subscribeSuccess, subscribeError, params);
```

##### Params #####
* address = The address/identifier provided by the scan's return object
* service = The service's UUID
* characteristic = The characteristic's UUID

```javascript
{
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "service": "180d",
  "characteristic": "2a37",
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
  "value": "U3Vic2NyaWJlIEhlbGxvIFdvcmxk", //Subscribe Hello World
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
* type = Set to "noResponse" to enable write without response, all other values will write normally.

Value is a base64 encoded string of bytes to write. Use bluetoothle.bytesToEncodedString(bytes) to convert to base64 encoded string from a unit8Array.
To write without response, set type to "noResponse". Any other value will default to write with response. Note, no callback will occur on write without response on iOS.
```javascript
var string = "Write Hello World";
var bytes = bluetoothle.stringToBytes(string);
var encodedString = bluetoothle.bytesToEncodedString(bytes);

//Note, this example doesn't actually work since it's read only characteristic
{"value":"V3JpdGUgSGVsbG8gV29ybGQ=","service":"180F","characteristic":"2A19","type":"noResponse","address":"ABC123"}
```

##### Success #####
Value is a base64 encoded string of written bytes. Use bluetoothle.encodedStringToBytes(obj.value) to convert to a unit8Array. See characteristic's specification and example below on how to correctly parse this.

```javascript
var returnObj = {"status":"written","service":"180F","characteristic":"2A19","value":"V3JpdGUgSGVsbG8gV29ybGQ=","address":"ABC123"}
var bytes = bluetoothle.encodedStringToBytes(returnObj.value);
var string = bluetoothle.bytesToString(bytes); //This should equal Write Hello World
```



### writeQ ###
Write Quick / Queue, use this method to quickly execute write without response commands when writing more than 20 bytes at a time. The data will automatically be split up into 20 bytes packets. On iOS, these packets are written immediately since iOS uses queues. You probably won't see much of a performance increase using writeQ. On Android, a queue isn't used internally. Instead another call shouldn't be made until onCharacteristicWrite is called. This could be done at the Javascript layer, but the Javascript to plugin "bridge" must be crossed twice, which leads to some significant slow downs when milliseconds make a difference. For even better write throughput, use requestConnectionPriority('high') as well. Note, no callback will occur on write without response on iOS.

Warnings
* This is experimental. Test heavily before using in any production code.
* iOS won't see much performance gain, but Android should.
* Only supports one call at a time. Don't execute back to back, use on multiple devices, or multiple characteristics.

```javascript
bluetoothle.writeQ(writeSuccess, writeError, params);
```

##### Params #####
See write() above.

##### Success #####
See write() above.



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
Write a particular characteristic's descriptor. Unable to write characteristic configuration directly to keep in line with iOS implementation. Instead use subscribe/unsubscribe, which will automatically enable/disable notification.

```javascript
bluetoothle.writeDescriptor(writeDescriptorSuccess, writeDescriptorError, params);
```

##### Params #####
* address = The address/identifier provided by the scan's return object
* service = The service's ID
* characteristic = The characteristic's ID
* descriptor = The descriptor's ID
* value - Base64 encoded string, number or string.
* type - Specifies type (data, number or string). Default is base64. (iOS Only, use base64 encoded string for Android)

Value is a base64 encoded string of bytes to write. Use bluetoothle.bytesToEncodedString(bytes) to convert to base64 encoded string from a unit8Array.

```javascript
var string = "Hello World";
var bytes = bluetoothle.stringToBytes(string);
var encodedString = bluetoothle.bytesToEncodedString(bytes);

{"service":"180D","characteristic":"2A37","descriptor":"2902","value":"AQAAAAAAAAA=","address":"ABC123"}
```

##### Success #####
Value is a base64 encoded string of written bytes. Use bluetoothle.encodedStringToBytes(obj.value) to convert to a unit8Array.

```javascript
{"status":"writeDescriptor","service":"180D","characteristic":"2A37", "descriptor":"2902","value":"AQAAAAAAAAA=","address":"ABC123"}
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



### isBonded ###
Determine whether the device is bonded or not, or error if not initialized. Android support only.

```javascript
bluetoothle.isBonded(isBondedSuccess, isBondedError, params);
```

#### Params ####
* address = The address/identifier provided by the scan's return object

```javascript
{
  "address": "5A:94:4B:38:B3:FD"
}
```

##### Success #####
* status => isBonded = true/false

```javascript
{
  "name": "Polar H7 3B321015",
  "address": "5A:94:4B:38:B3:FD",
  "isBonded": false
}
```



### wasConnected ###
Determine whether the device was connected, or error if not initialized.

```javascript
bluetoothle.wasConnected(wasConnectedSuccess, wasConnectedError, params);
```

#### Params ####
* address = The address/identifier provided by the scan's return object

```javascript
{
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63"
}
```

##### Success #####
* status => wasConnected = true/false

```javascript
{
  "name": "Polar H7 3B321015",
  "address": "ECC037FD-72AE-AFC5-9213-CA785B3B5C63",
  "wasConnected": false
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
Determine whether the device's characteristics and descriptors have been discovered, or error if not initialized or not connected to device. Note, on Android, you can connect, discover and then disconnect. isDiscovered will return an error due to the device not being connected. But if you call reconnect and call isDiscovered again, it will return isDiscovered => true since the device stays discovered until calling close().

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
Request coarse location privileges since scanning for unpaired devices requires it in Android API 23. Will return an error if called on iOS or Android versions prior to 6.0.

```javascript
bluetoothle.requestPermission(requestPermissionSuccess, requestPermissionError);
```

##### Success #####
* status => requestPermission = true/false

```javascript
{
  "requestPermission": true
}
```



### isLocationEnabled ###
Determine if location services are enabled or not. Location Services are required to find devices in Android API 23.

```javascript
bluetoothle.isLocationEnabled(isLocationEnabledSuccess, isLocationEnabledError);
```

##### Success #####
* status => isLocationEnabled = true/false

```javascript
{
  "isLocationEnabled": true
}
```



### requestLocation ###
Prompt location services settings pages. ```requestLocation``` property returns whether location services are enabled or disabled. Location Services are required to find devices in Android API 23.

```javascript
bluetoothle.requestLocation(requestLocationSuccess, requestLocationError);
```

##### Success #####
* status => requestLocation = true/false

```javascript
{
  "requestLocation": true
}
```



## Peripheral Life Cycle ##

1. initializePeripheral
2. addService
3. startAdvertising
4. Listen for events on initializePeripheral callback
5. Respond to events using respond or notify
6. stopAdvertising
7. removeService / removeAllServices


### Initilization ###
Initialization works slightly different between Android and iOS. On iOS, you don't need to call intialize() if only acting as a peripheral, just initializePeripheral. On Android, you must always call initialize() before calling initializePeripheral().


### Notifications ###
Notifications work slightly differently between Android and iOS. On Android, you should wait for the ```notificationSent``` event before calling notify() again. On iOS, you need to check the notify() callback for the sent property. If the sent property is set to false, you should wait until receiving the ```peripheralManagerIsReadyToUpdateSubscribers``` event to resend the notification. In future versions, I hope to standardize the functionality between platforms.


### Descriptors ###
iOS doesn't allow you to respond to read and write descriptor requests. Instead it only provides methods for when a client subscribes or unsubscribes. On Android, read and write descriptor requests are provided. If the write descriptor request is made on the Client Configuration Descriptor (used for subscriptions), a subscribe or unsubscribe event will be received instead of writeDescriptorRequested.


### initializePeripheral ###
Initialize Bluetooth on the device. Must be called before anything else. Callback will continuously be used whenever Bluetooth is enabled or disabled. Note: Although Bluetooth initialization could initially be successful, there's no guarantee whether it will stay enabled. Each call checks whether Bluetooth is disabled. If it becomes disabled, the user must readd services, start advertising, etc again. If Bluetooth is disabled, you can request the user to enable it by setting the request property to true. The `request` property in the `params` argument is optional and defaults to false. The `restoreKey` property requires using the Bluetooth Peripheral background mode. This function should only be called once.

Additionally this where new events are delivered for read, write, and subscription requests. See the success section for more details.

```javascript
bluetoothle.initializePeripheral(success, error, params);
```

##### Params #####
* request = true / false (default) - Should user be prompted to enable Bluetooth
* restoreKey = A unique string to identify your app.  Bluetooth Peripheral background mode is required to use this, but background mode doesn't seem to require specifying the restoreKey.

```javascript
{
  "request": true
  "restoreKey": "bluetoothleplugin"
}
```


##### Success #####
* status => enabled = Bluetooth is enabled
* status => disabled = Bluetooth is disabled
* status => readRequested = Respond to a read request with respond(). Characteristic (Android/iOS) or Descriptor (Android)
* status => writeRequested = Respond to a write request with respond(). Characteristic (Android/iOS) or Descriptor (Android)
* status => subscribed = Subscription started request, use notify() to send new data
* status => unsubscribed = Subscription ended request, stop sending data
* status => notificationReady = Resume sending subscription updates (iOS)
* status => notificationSent = Notification has been sent (Android)
* status => connected = A device has connected
* status => disconnected = A device has disconnected
* status => mtuChanged = MTU has changed for device

###### Enabled/Disabled ######
```javascript
{
  "status": "enabled"
}
```

###### readRequested ######
```javascript
{
  "status":"readRequested",
  "address":"5163F1E0-5341-AF9B-9F67-613E15EC83F7",
  "service":"1234",
  "characteristic":"ABCD",
  "requestId":0, //This integer value will be incremented every read/writeRequested
  "offset":0
}
```

###### writeRequested ######
```javascript
{
  "status":"writeRequested",
  "address":"5163F1E0-5341-AF9B-9F67-613E15EC83F7",
  "service":"1234",
  "characteristic":"ABCD",
  "requestId":1, //This integer value will be incremented every read/writeRequested
  "value":"V3JpdGUgSGVsbG8gV29ybGQ=", //Write Hello World
  "offset":0
}
```

###### subscribed ######
```javascript
{
  "status":"subscribed",
  "address":"5163F1E0-5341-AF9B-9F67-613E15EC83F7",
  "service":"1234",
  "characteristic":"ABCD"
}
```

###### unsubscribed ######
```javascript
{
  "status":"unsubscribed",
  "address":"5163F1E0-5341-AF9B-9F67-613E15EC83F7",
  "service":"1234",
  "characteristic":"ABCD"
}
```

###### notificationReady ######
```javascript
{
  "status":"notificationReady"
}
```

###### connected ######
```javascript
{
  "status":"connected",
  "address":"5163F1E0-5341-AF9B-9F67-613E15EC83F7",
}
```

###### disconnected ######
```javascript
{
  "status":"disconnected",
  "address":"5163F1E0-5341-AF9B-9F67-613E15EC83F7",
}
```

###### mtuChanged ######
```javascript
{
  "status":"mtuChanged",
  "address":"5163F1E0-5341-AF9B-9F67-613E15EC83F7",
  "mtu":20,
}
```



### addService ###
Add a service with characteristics and descriptors. If more than one service is added, add them sequentially.

```javascript
bluetoothle.addService(success, error, params);
```

##### Params #####
* service = A service UUID.
* characteristics = An object of characteristics data as shown below.

```javascript
var params = {
  service: "1234",
  characteristics: [
    {
      uuid: "ABCD",
      permissions: {
        read: true,
        write: true,
        //readEncryptionRequired: true,
        //writeEncryptionRequired: true,
      },
      properties : {
        read: true,
        writeWithoutResponse: true,
        write: true,
        notify: true,
        indicate: true,
        //authenticatedSignedWrites: true,
        //notifyEncryptionRequired: true,
        //indicateEncryptionRequired: true,
      }
    }
  ]
};
```


##### Return #####
```javascript
{
  "service":"1234",
  "status":"serviceAdded"
}
```


### removeService ###
Remove a service.

```javascript
bluetoothle.removeService(success, error, params);
```

##### Params #####
* service = A service UUID.

```javascript
var params = {
  service: "1234",
};
```


##### Return #####
```javascript
{
  "service":"1234",
  "status":"serviceRemoved"
}
```


### removeAllServices ###
Remove all services

```javascript
bluetoothle.removeAllServices(success, error);
```

##### Return #####
```javascript
{
  "status":"allServicesRemoved"
}
```


### startAdvertising ###
Start advertising as a BLE device. Note: This needs to be improved so services can be used for both Android and iOS.
On iOS, the advertising devices likes to rename itself back to the name of the device, i.e. Rand' iPhone

```javascript
bluetoothle.startAdvertising(success, error, params);
```

##### Params #####
```javascript
var params = {
  "services":["1234"], //iOS
  "service":"1234", //Android
  "name":"Hello World",
};
```


##### Return #####
```javascript
{
  "status":"advertisingStarted"
}
```


### stopAdvertising ###
Stop advertising

```javascript
bluetoothle.stopAdvertising(success, error);
```


##### Return #####
```javascript
{
  "status":"advertisingStopped"
}
```


### isAdvertising ###
Determine if app is advertising or not.

```javascript
bluetoothle.isAdvertising(success, error);
```


##### Return #####
```javascript
{
  "isAdvertising":true
}
```



### respond ###
Respond to a read or write request

```javascript
bluetoothle.respond(success, error, params);
```

##### Params #####
```javascript
//This was a read
var params = {
  "requestId":0,
  "value":"UmVhZCBIZWxsbyBXb3JsZA==" //Read Hello World
};
```

```javascript
//This was a write
var params = {
  "requestId":1,
  "value":"V3JpdGUgSGVsbG8gV29ybGQ=" //Write Hello World
};
```

##### Return #####
```javascript
{
  "status":"responded"
}
```


### notify ###
Update a value for a subscription. Currently all subscribed devices will receive update. Device specific updates will be added in the future. If ```sent``` equals false in the return value, you must wait for the ```peripheralManagerIsReadyToUpdateSubscribers``` event before sending more updates.

```javascript
bluetoothle.notify(success, error, params);
```

##### Params #####
```javascript
var params = {
  "service":"1234",
  "characteristic":"ABCD",
  "value":"U3Vic2NyaWJlIEhlbGxvIFdvcmxk" //Subscribe Hello World
};
```


##### Return #####
```javascript
{
  "status":"notified",
  "sent":true
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

## Sample: Discover and interact with Bluetooth LE devices ##

We'll build an app that lets you discover Bluetooth Low Energy (LE) devices that are around you, connect to a one, and then look at all of the information that you can obtain from that device such as signal strength, supported services, battery level and more.

You could use an app like this to find a lost device or to debug a device that isn't behaving as expected.

Our code performs these tasks.

* Initialize the BluetoothLE adapter.
* Scan for devices.
* Connect to a device.
* Show device services.
* Show service characteristics.

We'll build this app for Android and Windows devices.

Also, we'll use a **Promise** object for each of the Bluetooth LE functions. If you're unfamiliar with promises, it's just a cleaner way to organize asynchronous functions. You can read more about them [here](https://www.promisejs.org/).

If you're ready to go, let's start.

## Initialize the Bluetooth adapter

The BluetoothLE plugin uses an adapter to interact with each device's Bluetooth LE capability so you'll have to initialize it. To do that, call the [initialize](#initialize) function.

```javascript

document.addEventListener('deviceready', function () {

    new Promise(function (resolve) {

        bluetoothle.initialize(resolve, { request: true, statusReceiver: false });

    }).then(initializeSuccess, handleError);

});

```

If your call succeeds, use ``result.status`` property to find out if Bluetooth is enabled on their device.

```javascript

function initializeSuccess(result) {

    if (result.status === "enabled") {

        log("Bluetooth is enabled.");
        log(result);
    }

    else {

        document.getElementById("start-scan").disabled = true;

        log("Bluetooth is not enabled:", "status");
        log(result, "status");
    }
}

```

If your call fails, you can find out why by using the ``error`` object. This code shows one way to do that. We'll re-use this function throughout this example.

```javascript

function handleError(error) {

    var msg;

    if (error.error && error.message) {

        var errorItems = [];

        if (error.service) {

            errorItems.push("service: " + (uuids[error.service] || error.service));
        }

        if (error.characteristic) {

            errorItems.push("characteristic: " + (uuids[error.characteristic] || error.characteristic));
        }

        msg = "Error on " + error.error + ": " + error.message + (errorItems.length && (" (" + errorItems.join(", ") + ")"));
    }

    else {

        msg = error;
    }

    log(msg, "error");

    if (error.error === "read" && error.service && error.characteristic) {

        reportValue(error.service, error.characteristic, "Error: " + error.message);
    }
}

```

The block of code above calls a function named ``log``. It's just a helper function that shows one of many ways to show output to the users.

```javascript

function log(msg, level) {

    level = level || "log";

    if (typeof msg === "object") {

        msg = JSON.stringify(msg, null, "  ");
    }

    console.log(msg);

    if (level === "status" || level === "error") {

        var msgDiv = document.createElement("div");
        msgDiv.textContent = msg;

        if (level === "error") {

            msgDiv.style.color = "red";
        }

        msgDiv.style.padding = "5px 0";
        msgDiv.style.borderBottom = "rgb(192,192,192) solid 1px";
        document.getElementById("output").appendChild(msgDiv);
    }
}

```

## Scan for devices

Call the [startScan](#startscan) function to find Bluetooth LE devices that are around you. This function succeeds for iOS and Android devices but not for Windows. So why is that?

It turns out that Windows devices detect only those Bluetooth LE devices that are paired to it. For Windows devices, call the [retrieveConnected](#retrieveconnected) function.

```javascript

var foundDevices = [];

function startScan() {

    log("Starting scan for devices...", "status");

    foundDevices = [];

    document.getElementById("devices").innerHTML = "";
    document.getElementById("services").innerHTML = "";
    document.getElementById("output").innerHTML = "";

    if (window.cordova.platformId === "windows") {

        bluetoothle.retrieveConnected(retrieveConnectedSuccess, handleError, {});
    }
    else {

        bluetoothle.startScan(startScanSuccess, handleError, { services: [] });
    }
}

```

Every time that a Bluetooth LE device is detected, the ``startScanSuccess`` callback function is called. In that function, use the ``result`` object to get information about the device.

In this example, we'll add each ``result`` object to an array. We use this array to detect duplicates. We'll compare the MAC address of the current ``result`` to all ``result`` objects in the array before we add it.

After we've determined that the detected device is unique, we'll call a helper function named ``addDevice`` to show that device as a button on the screen. We'll take a look at that function shortly.

```javascript

function startScanSuccess(result) {

    log("startScanSuccess(" + result.status + ")");

    if (result.status === "scanStarted") {

        log("Scanning for devices (will continue to scan until you select a device)...", "status");
    }
    else if (result.status === "scanResult") {

        if (!foundDevices.some(function (device) {

            return device.address === result.address;

        })) {

            log('FOUND DEVICE:');
            log(result);
            foundDevices.push(result);
            addDevice(result.name, result.address);
        }
    }
}

```

Remember that Windows devices detect only those Bluetooth LE devices that are paired to it, so we called the [retrieveConnected](#retrieveconnected) function to get paired devices.

If the function succeeds, we get an array of ``result`` objects.

In this example, we iterate through that array and then call a helper function named ``addDevice`` to show that device as a button on the screen.

```javascript

function retrieveConnectedSuccess(result) {

    log("retrieveConnectedSuccess()");
    log(result);

    result.forEach(function (device) {

        addDevice(device.name, device.address);

    });
}

```

This helper function adds a button for each available device. The ``click`` event of each button calls a helper function named ``connect``. We'll define that function in the next section.

```javascript

function addDevice(name, address) {

    var button = document.createElement("button");
    button.style.width = "100%";
    button.style.padding = "10px";
    button.style.fontSize = "16px";
    button.textContent = name + ": " + address;

    button.addEventListener("click", function () {

        document.getElementById("services").innerHTML = "";
        connect(address);
    });

    document.getElementById("devices").appendChild(button);
}

```

## Connect to a device

If the user clicks a button for any of the devices, the ``connect`` helper function is called. In that function, we'll call the [connect](#connect) function of the Bluetooth LE plugin.

If the user has a Windows device, we'll call a helper function named ``getDeviceServices`` to get information about that device's services. We don't have to connect to it first because we know that if it appears in the list, that device is already paired.

```javascript

function connect(address) {

    log('Connecting to device: ' + address + "...", "status");

    if (cordova.platformId === "windows") {

        getDeviceServices(address);

    }
    else {

        stopScan();

        new Promise(function (resolve, reject) {

            bluetoothle.connect(resolve, reject, { address: address });

        }).then(connectSuccess, handleError);

    }
}

function stopScan() {

    new Promise(function (resolve, reject) {

        bluetoothle.stopScan(resolve, reject);

    }).then(stopScanSuccess, handleError);
}

function stopScanSuccess() {

    if (!foundDevices.length) {

        log("NO DEVICES FOUND");
    }
    else {

        log("Found " + foundDevices.length + " devices.", "status");
    }
}

```

If the call to the [connect](#connect) function succeeds, use the ``result.status`` property to find out if you've managed to connect.

In this example, if we're connected to the Bluethooth LE device, we'll call a helper function named ``getDeviceServices`` to get information about that device's services

```javascript

function connectSuccess(result) {

    log("- " + result.status);

    if (result.status === "connected") {

        getDeviceServices(result.address);
    }
    else if (result.status === "disconnected") {

        log("Disconnected from device: " + result.address, "status");
    }
}

```

## Get device services

Now we'll take a look at that helper function named ``getDeviceServices`` that we referred to above. In this method we'll call either the [discover](#discover) function or the [services](#function) depending on the platform of users device.

For Android devices, call the [discover](#discover) function to find the services that are available on the Bluetooth LE device.

For Windows devices, you can use the [services](#services) function to get straight to the services available on the paired device.


```javascript

function getDeviceServices(address) {

    log("Getting device services...", "status");

    var platform = window.cordova.platformId;

    if (platform === "android") {

        new Promise(function (resolve, reject) {

            bluetoothle.discover(resolve, reject,
                { address: address });

        }).then(discoverSuccess, handleError);

    }
    else if (platform === "windows") {

        new Promise(function (resolve, reject) {

            bluetoothle.services(resolve, reject,
                { address: address });

        }).then(servicesSuccess, handleError);

    }
    else {

        log("Unsupported platform: '" + window.cordova.platformId + "'", "error");
    }
}

```

### Get services on an Android device

If the call to the [discover](#discover) function succeeds, we'll get an array of services.

In this example, we'll call a helper function named ``addService`` for each service in that array.

That function will show all of the characteristics of the service. We'll look at that function a bit later.

```javascript

function discoverSuccess(result) {

    log("Discover returned with status: " + result.status);

    if (result.status === "discovered") {

    // Create a chain of read promises so we don't try to read a property until we've finished
        // reading the previous property.

    var readSequence = result.services.reduce(function (sequence, service) {

        return sequence.then(function () {

            return addService(result.address, service.uuid, service.characteristics);
        });

    }, Promise.resolve());

    // Once we're done reading all the values, disconnect
    readSequence.then(function () {

        new Promise(function (resolve, reject) {

            bluetoothle.disconnect(resolve, reject,
                { address: result.address });

        }).then(connectSuccess, handleError);

    });

    }
}

```

### Get services on a Windows device

If the call to the [services](#services) function succeeds, we'll get an array of services.

we'll call the [Characteristics](#characteristics) function to get all of the characteristics of the service.

```javascript

function servicesSuccess(result) {

    log("servicesSuccess()");
    log(result);

    if (result.status === "services") {

        var readSequence = result.services.reduce(function (sequence, service) {

            return sequence.then(function () {

                console.log('Executing promise for service: ' + service);

                new Promise(function (resolve, reject) {

                    bluetoothle.characteristics(resolve, reject,
                        { address: result.address, service: service });

                }).then(characteristicsSuccess, handleError);

            }, handleError);

        }, Promise.resolve());

        // Once we're done reading all the values, disconnect
        readSequence.then(function () {

            new Promise(function (resolve, reject) {

                bluetoothle.disconnect(resolve, reject,
                    { address: result.address });

            }).then(connectSuccess, handleError);

        });
    }

    if (result.status === "services") {

        result.services.forEach(function (service) {

            new Promise(function (resolve, reject) {

                bluetoothle.characteristics(resolve, reject,
                    { address: result.address, service: service });

            }).then(characteristicsSuccess, handleError);

        });

    }
}

```

If the call to the [characteristics](#characteristics) function succeeds, we'll call a helper function named ``addService``.

That function will show all of the characteristics of the service. We'll look at that function in the next section.

```javascript

function characteristicsSuccess(result) {

    log("characteristicsSuccess()");
    log(result);

    if (result.status === "characteristics") {

        return addService(result.address, result.service, result.characteristics);
    }
}


```

## Show services and characteristics in an app page

The ``addService`` helper method shows the details of each service and their characteristics. To show each characteristic, this function calls the [read](#read) function.

The array of uuid values that is used in this example comes from a helper js file that contains the unique identifiers of all known characteristics. That file does not appear in this example, but the values in it come from this page on the Bluetooth web site: [Characteristics](https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicsHome.aspx?_ga=1.50248486.1214727029.1456966579).

```javascript

function addService(address, serviceUuid, characteristics) {

    log('Adding service ' + serviceUuid + '; characteristics:');
    log(characteristics);

    var readSequence = Promise.resolve();

    var wrapperDiv = document.createElement("div");
    wrapperDiv.className = "service-wrapper";

    var serviceDiv = document.createElement("div");
    serviceDiv.className = "service";
    serviceDiv.textContent = uuids[serviceUuid] || serviceUuid;
    wrapperDiv.appendChild(serviceDiv);

    characteristics.forEach(function (characteristic) {

        var characteristicDiv = document.createElement("div");
        characteristicDiv.className = "characteristic";

        var characteristicNameSpan = document.createElement("span");
        characteristicNameSpan.textContent = (uuids[characteristic.uuid] || characteristic.uuid) + ":";
        characteristicDiv.appendChild(characteristicNameSpan);

        characteristicDiv.appendChild(document.createElement("br"));

        var characteristicValueSpan = document.createElement("span");
        characteristicValueSpan.id = serviceUuid + "." + characteristic.uuid;
        characteristicValueSpan.style.color = "blue";
        characteristicDiv.appendChild(characteristicValueSpan);

        wrapperDiv.appendChild(characteristicDiv);

        readSequence = readSequence.then(function () {

            return new Promise(function (resolve, reject) {

                bluetoothle.read(resolve, reject,
                    { address: address, service: serviceUuid, characteristic: characteristic.uuid });

            }).then(readSuccess, handleError);

        });
    });

    document.getElementById("services").appendChild(wrapperDiv);

    return readSequence;
}

```

If the call to the [read](#function) function succeeds, we'll write the value of that characteristic to the app page.

```javascript

function readSuccess(result) {

    log("readSuccess():");
    log(result);

    if (result.status === "read") {

        reportValue(result.service, result.characteristic, window.atob(result.value));
    }
}

function reportValue(serviceUuid, characteristicUuid, value) {

    document.getElementById(serviceUuid + "." + characteristicUuid).textContent = value;
}

```

That's it! Find the complete sample here: https://github.com/Microsoft/cordova-samples/tree/master/cordova-plugin-bluetoothle.


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
