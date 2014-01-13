Bluetooth LE PhoneGap Plugin
====================
### Supported platforms ###

PhoneGap 3.0.0 or higher
Android 4.3 or higher
iOS support coming soon

### Current Limitations ###

Can only connect to a single device at a time
All read, write and subscribe operations must be done sequentially

### Installation ###

Add the plugin to your app by running the command below:
```phonegap local plugin add https://github.com/randdusing/BluetoothLE```

### Methods ###

bluetoothle.init
bluetoothle.startScan
bluetoothle.stopScan
bluetoothle.connect
bluetoothle.disconnect
bluetoothle.close
bluetoothle.discover
bluetoothle.subscribe
bluetoothle.unsubscribe
bluetoothle.read
bluetoothle.write
bluetoothle.characteristics
bluetoothle.isConnected
bluetoothle.isDiscovered

### init ###
Initialize Bluetooth on the device. Must be called before anything else.

```bluetoothle.init(successCallback, errorCallback);```

### startScan ###
Scan for Bluetooth LE devices

```bluetoothle.startScan(successCallback, errorCallback, uuids, scanLimit);```

## Params ##
uuids = An array of characteristic UUIDs in string format to filter the scan by
scanLimit = How long to run the scan in milliseconds. Internal plugin default is 10,000 milliseconds.
  
### stopScan ###
Stop scan for Bluetooth LE devices. Since scanning is expensive, stop as soon as possible.

```bluetoothle.stopScan(successCallback, errorCallback);```

### connect ###
Connect to a Bluetooth LE device

```bluetoothle.connect(successCallback, errorCallback, address, autoDiscover);```

## Params ##
address = The address provided by the scan's return object
autoDiscover = Boolean to automatically discover the devices services or not
 
### disconnect ###
Disconnect from a Bluetooth LE device

```bluetoothle.disconnect(successCallback, errorCallback);```

### close ###
Close/dispose a Bluetooth LE device

```bluetoothle.close(successCallback, errorCallback);```

### discover ###
Discover the available characteristics on a Bluetooth LE device

```bluetoothle.discover(successCallback, errorCallback);```

### subscribe ###
Subscribe to a particular service's characteristic.

```bluetoothle.subscribe(successCallback, errorCallback, serviceUuid, characteristicUuid);```

## Params ##
serviceUuid = the service uuid like "0000180d-0000-1000-8000-00805f9b34fb" (Heart Rate Service)
characteristicUuid = the characteristic uuid like "00002a37-0000-1000-8000-00805f9b34fb" (Heart Rate Measurement Characteristic)

### unsubscribe ###
Unsubscribe to a particular service's characteristic.

```bluetoothle.unsubscribe(successCallback, errorCallback, serviceUuid, characteristicUuid);```

## Params ##
serviceUuid = the service uuid like "0000180d-0000-1000-8000-00805f9b34fb" (Heart Rate Service)
characteristicUuid = the characteristic uuid like "00002a37-0000-1000-8000-00805f9b34fb" (Heart Rate Measurement Characteristic)

### read ###
Read a particular service's characteristic (once).

```bluetoothle.read(successCallback, errorCallback, serviceUuid, characteristicUuid);```

## Params ##
serviceUuid = the service uuid like "0000180d-0000-1000-8000-00805f9b34fb" (Heart Rate Service)
characteristicUuid = the characteristic uuid like "00002a37-0000-1000-8000-00805f9b34fb" (Heart Rate Measurement Characteristic)

### write ### 
Write a particular service's characteristic.

```bluetoothle.write(successCallback, errorCallback, serviceUuid, characteristicUuid, write);```

## Params ###
serviceUuid = the service uuid like "0000180d-0000-1000-8000-00805f9b34fb" (Heart Rate Service) https://developer.bluetooth.org/gatt/services/Pages/ServiceViewer.aspx?u=org.bluetooth.service.heart_rate.xml
characteristicUuid = the characteristic uuid like "00002a37-0000-1000-8000-00805f9b34fb" (Heart Rate Measurement Characteristic)
https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
write = the value to write to the device

### characteristics ###
Get a service's characteristics

```bluetoothle.characteristics(successCallback, errorCallback, serviceUuid);```

## Params ##
serviceUuid = the service uuid like "0000180d-0000-1000-8000-00805f9b34fb" (Heart Rate Service)

### isConnected ###
Determine whether the device is connected

```bluetoothle.isConnected(successCallback, errorCallback);```

### isDiscovered ###
Determiene whether the device's characteristics have been discovered

```bluetoothle.isDiscovered(successCallback, errorCallback);```

### Example ###

### More information ###
Author: Rand Dusing
Website: http://www.randdusing.com/

### License ###
The source files included in the repository are released under the Apache License, Version 2.0.
