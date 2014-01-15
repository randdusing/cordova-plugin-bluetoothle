Bluetooth LE PhoneGap Plugin
====================
## Supported platforms ##

* PhoneGap 3.0.0 or higher
* Android 4.3 or higher

## Current Limitations ##

* Can only connect to a single device at a time, don't plan to support multiple devices in the near future
* All read, write and subscribe operations must be done sequentially, don't plan to support parallel operations in the near future
* Lacks support for writing descriptors unless using Heart Rate Services, changes coming soon
* No iOS support, but coming soon

## Installation ##

Add the plugin to your app by running the command below:
```phonegap local plugin add https://github.com/randdusing/BluetoothLE```

## Methods ##

* bluetoothle.init
* bluetoothle.startScan
* bluetoothle.stopScan
* bluetoothle.connect
* bluetoothle.disconnect
* bluetoothle.close
* bluetoothle.discover
* bluetoothle.subscribe
* bluetoothle.unsubscribe
* bluetoothle.read
* bluetoothle.write
* bluetoothle.characteristics
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
bluetoothle.startScan(successCallback, errorCallback, uuids, scanLimit);
```

##### Params #####
* uuids = An array of service UUIDs in string format to filter the scan by
* scanLimit = How long to run the scan in milliseconds. Internal plugin default is 10,000 milliseconds.

##### Success Return #####
An object containing the following field(s):
* status = stopScan | device

Additionally if the status is "device", the object will contain:
* name = the friendly name of the device
* address = the MAC address of the device, which is needed to connect to the object
* class = the class of the Device, see http://developer.android.com/reference/android/bluetooth/BluetoothClass.Device.html


### stopScan ###
Stop scan for Bluetooth LE devices. Since scanning is expensive, stop as soon as possible.

```javascript
bluetoothle.stopScan(successCallback, errorCallback);
```


### connect ###
Connect to a Bluetooth LE device

```javascript
bluetoothle.connect(successCallback, errorCallback, address, autoDiscover);
```

#### Params ####
* address = The address provided by the scan's return object
* autoDiscover = Boolean to automatically discover the devices services or not

##### Success Return #####
An object containing the following field(s):
* status = connected | disconnected


### disconnect ###
Disconnect from a Bluetooth LE device

```javascript
bluetoothle.disconnect(successCallback, errorCallback);
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


### subscribe ###
Subscribe to a particular service's characteristic. Once a subscription is no longer needed, execute unsubscribe in a similar fashion.

```javascript
bluetoothle.subscribe(successCallback, errorCallback, serviceUuid, characteristicUuid);
```

#### Params ####
* serviceUuid = See Bluetooth LE UUIDs section.
* characteristicUuid = See Bluetooth LE UUIDs section.

##### Success Return #####
Returns an array of bytes. See characteristic's specification on how to correctly parse this.


### unsubscribe ###
Unsubscribe to a particular service's characteristic.

```javascript
bluetoothle.unsubscribe(successCallback, errorCallback, serviceUuid, characteristicUuid);
```

#### Params ####
* serviceUuid = See Bluetooth LE UUIDs section.
* characteristicUuid = See Bluetooth LE UUIDs section.


### read ###
Read a particular service's characteristic (once).

```javascript
bluetoothle.read(successCallback, errorCallback, serviceUuid, characteristicUuid);
```

#### Params ####
* serviceUuid = See Bluetooth LE UUIDs section.
* characteristicUuid = See Bluetooth LE UUIDs section.

##### Success Return #####
Returns an array of bytes. See characteristic's specification on how to correctly parse this.


### write ###
Write a particular service's characteristic. ***Note, this hasn't been tested yet***

```javascript
bluetoothle.write(successCallback, errorCallback, serviceUuid, characteristicUuid, write);
```

#### Params ####
* serviceUuid = See Bluetooth LE UUIDs section.
* characteristicUuid = See Bluetooth LE UUIDs section.
* write = the value to write to the device

##### Success Return #####
Returns an array of bytes that were written.


### characteristics ###
Get a service's characteristics

```javascript
bluetoothle.characteristics(successCallback, errorCallback, serviceUuid);
```

#### Params ####
* serviceUuid = See Bluetooth LE UUIDs section.

##### Success Return #####
An array of characteristic UUIDs as strings


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
The following example demonstrates how to connect to a heart rate monitor and subscribe to the heart rate value. Caution: no clean up is done like manually stopping the scan, unsubscribing, disconnecting or closing the device, which should all be done in real scenarios.

```javascript
//Service and Characteristic UUIDs
var heartRateServiceUuid = "0000180d-0000-1000-8000-00805f9b34fb";
var heartRateMeasurementCharacteristicUuid = "00002a37-0000-1000-8000-00805f9b34fb";
var batteryServiceUuid = "0000180f-0000-1000-8000-00805f9b34fb";
var batteryLevelCharacteristicUuid = "00002a19-0000-1000-8000-00805f9b34fb";

//Initialize Bluetooth LE
bluetoothle.init(initSuccess, initError);

function initSuccess()
{
  //Scan for devices that have the Heart Rate Service for up to 10 seconds
  bluetoothle.startScan(startScanSuccess, startScanError, [heartRateServiceUuid], 10000);
}

function initError(msg)
{
  console.log("Bluetooth unable to initialize:" + msg);
}

function startScanSuccess(obj)
{
  //Scanning found a device
  if (obj.name != undefined && obj.address != undefined)
  {
    //Stop the scan
    bluetoothle.stopScan(stopScanSuccess, stopScanError);
    
    //Connect to recently scanned device and automatically discover available services
    //Note, if there are multiple devices, multiple connection attempts will be made, which isn't supported at the moment.
    bluetoothle.connect(connectSuccess, connectError, obj.address, true);
  }
}

function startScanError(msg)
{
  console.log("Bluetooth unable to start scan: " + msg);
}

function stopScanSuccess()
{
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
    //Timeouts were added since discovery takes a while. No subscribe or read operations can begin until discovery is complete
    //Subscribe to Heart Rate Measurement
    setTimeout(function() {bluetoothle.subscribe(subscribeSuccess, subscribeError, heartRateServiceUuid, heartRateMeasurementCharacteristicUuid);}, 2000);
    
    //Read the Battery Level
    //setTimeout(function() {bluetoothle.read(readSuccess, readError, batteryServiceUuid, batteryLevelCharacteristicUuid);}, 2000);
  }
}

function connectError(msg)
{
  console.log("Bluetooth unable to connect to device: " + msg);
}

function subscribeSuccess(obj)
{
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

```

## More information ##
* Author: Rand Dusing
* Website: http://www.randdusing.com/

## License ##
The source files included in the repository are released under the Apache License, Version 2.0.
