Bluetooth LE PhoneGap Plugin
====================
## Supported platforms ##

* PhoneGap 3.0.0 or higher
* Android 4.3 or higher
* Device hardware must be certified for Bluetooth LE. i.e. Nexus 7 2012 doesn't support Bluetooth LE after upgrading to 4.3 or higher without a modification.

## Limitations ##

* Can only connect to a single device at a time. Eventually I'll test out the feasibility of multiple devices.
* All read, write and subscribe operations must be done sequentially. Based on this thread (http://stackoverflow.com/questions/18011816/has-native-android-ble-gatt-implementation-synchronous-nature), Android Bluetooth LE support seems sychronous.
* No iOS support, but coming soon

## Installation ##

Add the plugin to your app by running the command below:
```phonegap local plugin add https://github.com/randdusing/BluetoothLE```

## Methods ##

* bluetoothle.init
* bluetoothle.startScan
* bluetoothle.stopScan
* bluetoothle.connect
* bluetoothle.reconnect
* bluetoothle.disconnect
* bluetoothle.close
* bluetoothle.discover
* bluetoothle.subscribe
* bluetoothle.unsubscribe
* bluetoothle.read
* bluetoothle.write
* bluetoothle.readDescriptor
* bluetoothle.writeDescriptor
* bluetoothle.isConnected
* bluetoothle.isDiscovered


## init ##
Initialize Bluetooth on the device. Must be called before anything else. If Bluetooth is disabled, the user will be prompted to enable it.

```javascript
bluetoothle.init(successCallback, errorCallback);
```

### startScan ###
Scan for Bluetooth LE devices. Since scanning is expensive, stop as soon as possible.

```javascript
bluetoothle.startScan(successCallback, errorCallback, paramsObj);
```

##### Params #####
An object containing the follow field(s):
* serviceUuids = An array of service UUIDs in string format to filter the scan by
* scanLimit = How long to run the scan in milliseconds. Plugin default is 10,000 milliseconds.

##### Success Return #####
An object containing the following field(s):
* status = scanStart, scanStop, scanResult
**scanStart = Scan has started
**scanStop = Scan has automatically stopped
**scanResult = Scan has found a device

Additionally if the status is "scanResult", the object will contain device information:
* name = the friendly name of the device
* address = the MAC address of the device, which is needed to connect to the object
* class = the class of the Device, see http://developer.android.com/reference/android/bluetooth/BluetoothClass.Device.html

```javascript
//Scan Start
{"status":"scanStart"}
//Scan Result
{"status":"scanResult","class":7936,"address":"xx:xx:xx:xx:xx:xx","name":"Polar H7"}
//Scan Stop
{"status":"scanStop"}
```



### stopScan ###
Stop scan for Bluetooth LE devices. Since scanning is expensive, stop as soon as possible.

```javascript
bluetoothle.stopScan(successCallback, errorCallback);
```


### connect ###
Connect to a Bluetooth LE device

```javascript
bluetoothle.connect(successCallback, errorCallback, paramsObj);
```

##### Params #####
An object containing the follow field(s):
* address = The address provided by the scan's return object

##### Success Return #####
An object containing the following field(s):
* status = connected | disconnected

```javascript
//Connected
{"status":"connected","address":"xx:xx:xx:xx:xx:xx","name":"Polar H7"}
//Disconnected without user initiation
{"status":"disconnected","address":"xx:xx:xx:xx:xx:xx","name":"Polar H7"}
```



### reconnect ###
Reconnect to a disconnected Bluetooth device

```javascript
bluetoothle.reconnect(successCallback, errorCallback);
```

##### Success Return #####
An object containing the following field(s):
* status = connected | disconnected

```javascript
//Connected
{"status":"connected","address":"xx:xx:xx:xx:xx:xx","name":"Polar H7"}
//Disconnected without user initiation
{"status":"disconnected","address":"xx:xx:xx:xx:xx:xx","name":"Polar H7"}
```



### disconnect ###
Disconnect from a Bluetooth LE device

```javascript
bluetoothle.disconnect(successCallback, errorCallback);
```

##### Success Return #####
An object containing the following field(s):
* status = disconnected

```javascript
//Disconnected with user initiation
{"status":"disconnected","address":"xx:xx:xx:xx:xx:xx","name":"Polar H7"}
```



### close ###
Close/dispose a Bluetooth LE device

```javascript
bluetoothle.close(successCallback, errorCallback);
```



### discover ###
Discover the available characteristics on a Bluetooth LE device

```javascript
bluetoothle.discover(successCallback, errorCallback);
```

##### Success Return #####
An object containing the follow field(s):
* address = Device address
* name = Device name
* services = Array of service objects below

Service Object:
* uuid = Service's uuid
* characteristics = Array of characteristic objects below

Characteristic Object:
* uuid = Characteristic's uuid
* descriptors = Array of descriptor objects below

Descriptor Object:
* uuid = Descriptor's uuid

```javascript
{
   "address":"xx:xx:xx:xx:xx:xx",
   "services":[
      {
         "characteristics":[
            {
               "descriptors":[
                  {
                     "uuid":"00002902-0000-1000-8000-00805f9b34fb"
                  }
               ],
               "uuid":"00002a37-0000-1000-8000-00805f9b34fb"
            },
            {
               "descriptors":[

               ],
               "uuid":"00002a38-0000-1000-8000-00805f9b34fb"
            }
         ],
         "uuid":"0000180d-0000-1000-8000-00805f9b34fb"
      }
   ],
   "name":"Polar H7"
}
```



### subscribe ###
Subscribe to a particular service's characteristic. Once a subscription is no longer needed, execute unsubscribe in a similar fashion.

```javascript
bluetoothle.subscribe(successCallback, errorCallback, paramsObj);
```

##### Params #####
An object containing the follow field(s):
* serviceUuid = See Bluetooth LE UUIDs section.
* characteristicUuid = See Bluetooth LE UUIDs section.

##### Success Return #####
Continously returns an array of bytes until unsubscription. See characteristic's specification on how to correctly parse this.



### unsubscribe ###
Unsubscribe to a particular service's characteristic.

```javascript
bluetoothle.unsubscribe(successCallback, errorCallback, paramsObj);
```

##### Params #####
An object containing the follow field(s):
* serviceUuid = See Bluetooth LE UUIDs section.
* characteristicUuid = See Bluetooth LE UUIDs section.



### read ###
Read a particular service's characteristic once.

```javascript
bluetoothle.read(successCallback, errorCallback, paramsObj);
```

##### Params #####
An object containing the follow field(s):
* serviceUuid = See Bluetooth LE UUIDs section.
* characteristicUuid = See Bluetooth LE UUIDs section.

##### Success Return #####
Returns an array of bytes. See characteristic's specification on how to correctly parse this.



### write ###
Write a particular service's characteristic. ***Note, this hasn't been tested yet***

```javascript
bluetoothle.write(successCallback, errorCallback, paramsObj);
```

##### Params #####
An object containing the follow field(s):
* serviceUuid = See Bluetooth LE UUIDs section.
* characteristicUuid = See Bluetooth LE UUIDs section.
* value = the value to write to the device

##### Success Return #####
Returns an array of bytes that were written.



### readDescriptor ###
Read a particular characterist's descriptor ***Note, tested with limited scenarios***

```javascript
bluetoothle.read(successCallback, errorCallback, paramsObj);
```

##### Params #####
An object containing the follow field(s):
* serviceUuid = See Bluetooth LE UUIDs section.
* characteristicUuid = See Bluetooth LE UUIDs section.
* descriptorUuid = See Bluetooth LE UUIDs section.

##### Success Return #####
Returns an array of bytes. See descriptor's specification on how to correctly parse this.



### writeDescriptor ###
Write a particular characteristic's descriptor. ***Note, limited testing and likely needs to be made more generic***

```javascript
bluetoothle.write(successCallback, errorCallback, paramsObj);
```

##### Params #####
An object containing the follow field(s):
* serviceUuid = See Bluetooth LE UUIDs section.
* characteristicUuid = See Bluetooth LE UUIDs section.
* descriptorUuid = See Bluetooth LE UUIDs section.
* value = the value to write to the device. Currently limited to the following strings: EnableNotification, EnableIndication, DisableNotification.

##### Success Return #####
Returns an array of bytes that were written.



### isConnected ###
Determine whether the device is connected

```javascript
bluetoothle.isConnected(successCallback, errorCallback);
```

##### Success Return #####
Device is connected

##### Error Return #####
Device isn't connected



### isDiscovered ###
Determine whether the device's characteristics have been discovered

```javascript
bluetoothle.isDiscovered(successCallback, errorCallback);
```

##### Success Return #####
Device is discovered

##### Error Return #####
Device isn't discovered



## Bluetooth LE UUIDs ##
* A list of Bluetooth LE Services can be found here: https://developer.bluetooth.org/gatt/services/Pages/ServicesHome.aspx. And within each service, the list of characteristics can be viewed.
* All Bluetooth LE UUIDs have a base of 0000xxxx-0000-1000-8000-00805F9B34FB where xxxx is replaced by the service's assigned number. For example, the Heart Rate Service has a UUID of 0000180d-0000-1000-8000-00805f9b34fb. The Heart Rate Measurement charactersitic has a UUID of 00002a37-0000-1000-8000-00805f9b34fb.



## Example ##
The following example demonstrates how to connect to a heart rate monitor and subscribe to the heart rate value. Caution: no clean up is done like unsubscribing, disconnecting or closing the device, which should all be done in real scenarios.

```javascript
//Service and Characteristic UUIDs
var heartRateServiceUuid = "0000180d-0000-1000-8000-00805f9b34fb";
var heartRateMeasurementCharacteristicUuid = "00002a37-0000-1000-8000-00805f9b34fb";
var clientCharacteristicConfigDescriptorUuid = "00002902-0000-1000-8000-00805f9b34fb";
var batteryServiceUuid = "0000180f-0000-1000-8000-00805f9b34fb";
var batteryLevelCharacteristicUuid = "00002a19-0000-1000-8000-00805f9b34fb";

//Initialize Bluetooth LE
bluetoothle.init(initSuccess, initError);

function initSuccess()
{
  console.log("Bluetooth initialized");
  
  //Scan for devices that have the Heart Rate Service for up to 10 seconds
  var paramsObj = {"serviceUuids":[heartRateServiceUuid], "scanLimit":10000};
  bluetoothle.startScan(startScanSuccess, startScanError, paramsObj);
}

function initError(msg)
{
  console.log("Bluetooth unable to initialize:" + msg);
}

function startScanSuccess(obj)
{
  //Scanning found a device
  if (obj.status == "scanResult")
  {
    console.log("Device found");
    
    //Stop the scan
    bluetoothle.stopScan(stopScanSuccess, stopScanError);
    
    //Connect to recently scanned device
    //Note, if there are multiple devices, multiple connection attempts will be made, which isn't supported at the moment.
    var paramsObj = {"address":obj.address};
    bluetoothle.connect(connectSuccess, connectError, paramsObj);
  }
  else if (obj.status == "scanStart")
  {
    console.log("Scan was started successfully");
  }
  else if (obj.status == "scanStop")
  {
    console.log("Scan was automatically stopped");
  }
}

function startScanError(msg)
{
  console.log("Bluetooth unable to start scan: " + msg);
}

function stopScanSuccess()
{
  console.log("Scan was manually stopped");
}

function stopScanError(msg)
{
  console.log("Unable to stop scan: " + msg);
}

function connectSuccess(obj)
{
  //Connected to a device
  if (obj.status == "connected")
  {
    bluetoothle.discover(discoverSuccess, discoverError); 
  }
}

function connectError(msg)
{
  console.log("Bluetooth unable to connect to device: " + msg);
}

function discoverSuccess(obj)
{
  console.log("Discovery completed");
  
  //Set Heart Rate Measurement Descriptor prior to Subscription
  var paramsObj = {"serviceUuid":heartRateServiceUuid, "characteristicUuid":heartRateMeasurementCharacteristicUuid, "descriptorUuid":clientCharacteristicConfigDescriptorUuid, "value":"EnableNotification"}
  bluetoothle.writeDescriptor(writeDescriptorSuccess, writeDescriptorError, paramsObj);
    
  //Read the Battery Level
  //var paramsObj = {"serviceUuid":batteryServiceUuid, "characteristicUuid":batteryLevelCharacteristicUuid};
  //bluetoothle.read(readSuccess, readError, batteryServiceUuid, batteryLevelCharacteristicUuid);
}

function discoverError(msg)
{
  console.log("Discover error: " + msg);
}

function writeDescriptorSuccess(obj)
{
  console.log("Descriptor written");
  
  //Subscribe to Heart Rate Measurement
  var paramsObj = {"serviceUuid":heartRateServiceUuid, "characteristicUuid":heartRateMeasurementCharacteristicUuid};
  bluetoothle.subscribe(subscribeSuccess, subscribeError, paramsObj);
}

function writeDescriptorError(msg)
{
  console.log("Descriptor not written: " + msg);
}

function subscribeSuccess(obj)
{
  console.log("Subscription data received");
  
  //Parse array of int32 into uint8
  var bytes = new Uint8Array(obj);

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

function subscribeError(msg)
{
  console.log("Unable to initiate subscribe: " + msg);
}

function readSuccess(obj)
{
  //Read the heart rate
  var value = new Uint8Array(obj);
  console.log("Read value: " + value[0]);
}

function readError(msg)
{
  console.log("Read error: " + msg);
}

//Eventually you want to disconnect or close the device

function disconnectDevice()
{
  bluetoothle.disconnect(disconnectSuccess, disconnectError);
}

function disconnectSuccess()
{
  console.log("Device disconnected");
}

function disconnectError()
{
}

function closeDevice()
{
  bluetoothle.close(closeSuccess, closeError);
}

function closeSuccess()
{
  console.log("Device closed");
}

function closeError()
{
}

```

## More information ##
* Author: Rand Dusing
* Website: http://www.randdusing.com/

## License ##
The source files included in the repository are released under the Apache License, Version 2.0.
