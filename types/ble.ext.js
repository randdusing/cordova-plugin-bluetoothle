/**
 * @externs
 * Externs file for cordova-plugin-bluetoothle for use with Closure-Compiler.
 */

'use strict';

var BluetoothlePlugin = {};


/**
 * @interface
 */
BluetoothlePlugin.Bluetoothle = function () { };


/**
 * @param {function(result:[object Object])} initializeResult
 * @param {BluetoothlePlugin.InitParams=} opt_params
 */
BluetoothlePlugin.Bluetoothle.prototype.initialize = function (initializeResult, opt_params) { };


/**
 * @param {function(result:[object Object])} enableSuccess
 * @param {function(error:BluetoothlePlugin.Error)} enableError
 */
BluetoothlePlugin.Bluetoothle.prototype.enable = function (enableSuccess, enableError) { };


/**
 * @param {function(result:BluetoothlePlugin.Error)} disableSuccess
 * @param {function(error:BluetoothlePlugin.Error)} disableError
 */
BluetoothlePlugin.Bluetoothle.prototype.disable = function (disableSuccess, disableError) { };


/**
 * @param {function(status:BluetoothlePlugin.ScanStatus)} startScanSuccess
 * @param {function(error:BluetoothlePlugin.Error)} startScanError
 * @param {BluetoothlePlugin.ScanParams=} opt_params
 */
BluetoothlePlugin.Bluetoothle.prototype.startScan = function (startScanSuccess, startScanError, opt_params) { };


/**
 * @param {function(result:[object Object])} stopScanSuccess
 * @param {function(error:BluetoothlePlugin.Error)} stopScanError
 */
BluetoothlePlugin.Bluetoothle.prototype.stopScan = function (stopScanSuccess, stopScanError) { };


/**
 * @param {function(devices:Array<BluetoothlePlugin.DeviceInfo>)} retrieveConnectedSuccess
 * @param {function(error:BluetoothlePlugin.Error)} retrieveConnectedError
 * @param {{services: Array<string>=}} opt_params
 */
BluetoothlePlugin.Bluetoothle.prototype.retrieveConnected = function (retrieveConnectedSuccess, retrieveConnectedError, opt_params) { };


/**
 * @param {function(status:BluetoothlePlugin.DeviceInfo)} bondSuccess
 * @param {function(error:BluetoothlePlugin.Error)} bondError
 * @param {{address: string}} params
 */
BluetoothlePlugin.Bluetoothle.prototype.bond = function (bondSuccess, bondError, params) { };


/**
 * @param {function(status:BluetoothlePlugin.DeviceInfo)} unbondSuccess
 * @param {function(error:BluetoothlePlugin.Error)} unbondError
 * @param {{address: string}} params
 */
BluetoothlePlugin.Bluetoothle.prototype.unbond = function (unbondSuccess, unbondError, params) { };


/**
 * @param {function(status:BluetoothlePlugin.DeviceInfo)} connectSuccess
 * @param {function(error:BluetoothlePlugin.Error)} connectError
 * @param {BluetoothlePlugin.ConnectionParams} params
 */
BluetoothlePlugin.Bluetoothle.prototype.connect = function (connectSuccess, connectError, params) { };


/**
 * @param {function(status:BluetoothlePlugin.DeviceInfo)} reconnectSuccess
 * @param {function(error:BluetoothlePlugin.Error)} reconnectError
 * @param {{address: string}} params
 */
BluetoothlePlugin.Bluetoothle.prototype.reconnect = function (reconnectSuccess, reconnectError, params) { };


/**
 * @param {function(status:BluetoothlePlugin.DeviceInfo)} disconnectSuccess
 * @param {function(error:BluetoothlePlugin.Error)} disonnectError
 * @param {{address: string}} params
 */
BluetoothlePlugin.Bluetoothle.prototype.disconnect = function (disconnectSuccess, disonnectError, params) { };


/**
 * @param {function(status:BluetoothlePlugin.DeviceInfo)} closeSuccess
 * @param {function(error:BluetoothlePlugin.Error)} closeError
 * @param {{address: string}} params
 */
BluetoothlePlugin.Bluetoothle.prototype.close = function (closeSuccess, closeError, params) { };


/**
 * @param {function(device:BluetoothlePlugin.Device)} discoverSuccess
 * @param {function(error:BluetoothlePlugin.Error)} discoverError
 * @param {{address: string, clearCache: boolean=}} params
 */
BluetoothlePlugin.Bluetoothle.prototype.discover = function (discoverSuccess, discoverError, params) { };


/**
 * @param {function(services:BluetoothlePlugin.Services)} servicesSuccess
 * @param {function(error:BluetoothlePlugin.Error)} servicesError
 * @param {{address: string, services: Array<string>=}} params
 */
BluetoothlePlugin.Bluetoothle.prototype.services = function (servicesSuccess, servicesError, params) { };


/**
 * @param {function(characteristics:BluetoothlePlugin.Characteristics)} characteristicsSuccess
 * @param {function(error:BluetoothlePlugin.Error)} characteristicsError
 * @param {BluetoothlePlugin.CharacteristicParams} params
 */
BluetoothlePlugin.Bluetoothle.prototype.characteristics = function (characteristicsSuccess, characteristicsError, params) { };


/**
 * @param {function(descriptors:BluetoothlePlugin.Descriptors)} descriptorsSuccess
 * @param {function(error:BluetoothlePlugin.Error)} descriptorsError
 * @param {BluetoothlePlugin.DescriptorParams} params
 */
BluetoothlePlugin.Bluetoothle.prototype.descriptors = function (descriptorsSuccess, descriptorsError, params) { };


/**
 * @param {function(result:BluetoothlePlugin.OperationResult)} readSuccess
 * @param {function(error:BluetoothlePlugin.Error)} readError
 * @param {BluetoothlePlugin.DescriptorParams} params
 */
BluetoothlePlugin.Bluetoothle.prototype.read = function (readSuccess, readError, params) { };


/**
 * @param {function(result:BluetoothlePlugin.OperationResult)} subscribeSuccess
 * @param {function(error:BluetoothlePlugin.Error)} subscribeError
 * @param {BluetoothlePlugin.DescriptorParams} params
 */
BluetoothlePlugin.Bluetoothle.prototype.subscribe = function (subscribeSuccess, subscribeError, params) { };


/**
 * @param {function(result:BluetoothlePlugin.UnsubscribeResult)} unsubscribeSuccess
 * @param {function(error:BluetoothlePlugin.Error)} unsubscribeError
 * @param {BluetoothlePlugin.DescriptorParams} params
 */
BluetoothlePlugin.Bluetoothle.prototype.unsubscribe = function (unsubscribeSuccess, unsubscribeError, params) { };


/**
 * @param {function(result:BluetoothlePlugin.OperationResult)} writeSuccess
 * @param {function(error:BluetoothlePlugin.Error)} writeError
 * @param {BluetoothlePlugin.WriteCharacteristicParams} params
 */
BluetoothlePlugin.Bluetoothle.prototype.write = function (writeSuccess, writeError, params) { };


/**
 * @param {function(result:BluetoothlePlugin.OperationResult)} writeSuccess
 * @param {function(error:BluetoothlePlugin.Error)} writeError
 * @param {BluetoothlePlugin.WriteQCharacteristicParams} params
 */
BluetoothlePlugin.Bluetoothle.prototype.writeQ = function (writeSuccess, writeError, params) { };


/**
 * @param {function(descriptor:BluetoothlePlugin.DescriptorResult)} readDescriptorSuccess
 * @param {function(error:BluetoothlePlugin.Error)} readDescriptorError
 * @param {BluetoothlePlugin.OperationDescriptorParams} params
 */
BluetoothlePlugin.Bluetoothle.prototype.readDescriptor = function (readDescriptorSuccess, readDescriptorError, params) { };


/**
 * @param {function(descriptor:BluetoothlePlugin.DescriptorResult)} writeDescriptorSuccess
 * @param {function(error:BluetoothlePlugin.Error)} writeDescriptorError
 * @param {BluetoothlePlugin.WriteDescriptorParams} params
 */
BluetoothlePlugin.Bluetoothle.prototype.writeDescriptor = function (writeDescriptorSuccess, writeDescriptorError, params) { };


/**
 * @param {function(rssi:BluetoothlePlugin.RSSI)} rssiSuccess
 * @param {function(error:BluetoothlePlugin.Error)} rssiError
 * @param {{address: string}} params
 */
BluetoothlePlugin.Bluetoothle.prototype.rssi = function (rssiSuccess, rssiError, params) { };


/**
 * @param {function(mtu:BluetoothlePlugin.MTU)} mtuSuccess
 * @param {function(error:BluetoothlePlugin.Error)} mtuError
 * @param {{address: string, mtu: number=}} params
 */
BluetoothlePlugin.Bluetoothle.prototype.mtu = function (mtuSuccess, mtuError, params) { };


/**
 * @param {function(result:BluetoothlePlugin.DeviceInfo)} success
 * @param {function(error:BluetoothlePlugin.Error)} error
 * @param {{address: string, connectionPriority: BluetoothlePlugin.ConnectionPriority}} params
 */
BluetoothlePlugin.Bluetoothle.prototype.requestConnectionPriority = function (success, error, params) { };


/**
 * @param {function(result:[object Object])} success
 */
BluetoothlePlugin.Bluetoothle.prototype.isInitialized = function (success) { };


/**
 * @param {function(result:[object Object])} success
 */
BluetoothlePlugin.Bluetoothle.prototype.isEnabled = function (success) { };


/**
 * @param {function(result:[object Object])} success
 */
BluetoothlePlugin.Bluetoothle.prototype.isScanning = function (success) { };


/**
 * @param {function(result:BluetoothlePlugin.BondedStatus)} isBondedSuccess
 * @param {function(error:BluetoothlePlugin.Error)} isBondedError
 * @param {{address: string}} params
 */
BluetoothlePlugin.Bluetoothle.prototype.isBonded = function (isBondedSuccess, isBondedError, params) { };


/**
 * @param {function(result:BluetoothlePlugin.PrevConnectionStatus)} wasConnectedSuccess
 * @param {function(error:BluetoothlePlugin.Error)} wasConnectedError
 * @param {{address: string}} params
 */
BluetoothlePlugin.Bluetoothle.prototype.wasConnected = function (wasConnectedSuccess, wasConnectedError, params) { };


/**
 * @param {function(result:BluetoothlePlugin.CurrConnectionStatus)} isConnectedSuccess
 * @param {function(error:BluetoothlePlugin.Error)} isConnectedError
 * @param {{address: string}} params
 */
BluetoothlePlugin.Bluetoothle.prototype.isConnected = function (isConnectedSuccess, isConnectedError, params) { };


/**
 * @param {function(result:BluetoothlePlugin.DiscoverStatus)} isDiscoveredSuccess
 * @param {function(error:BluetoothlePlugin.Error)} isDiscoveredError
 * @param {{address: string}} params
 */
BluetoothlePlugin.Bluetoothle.prototype.isDiscovered = function (isDiscoveredSuccess, isDiscoveredError, params) { };


/**
 * @param {function(result:[object Object])} success
 */
BluetoothlePlugin.Bluetoothle.prototype.hasPermission = function (success) { };


/**
 * @param {function(result:[object Object])} success
 */
BluetoothlePlugin.Bluetoothle.prototype.requestPermission = function (success) { };


/**
 * @param {function(result:[object Object])} success
 */
BluetoothlePlugin.Bluetoothle.prototype.hasPermissionBtScan = function (success) { };


 /**
  * @param {function(result:[object Object])} success
  */
BluetoothlePlugin.Bluetoothle.prototype.requestPermissionBtScan = function (success) { };


 /**
 * @param {function(result:[object Object])} success
 */
BluetoothlePlugin.Bluetoothle.prototype.hasPermissionBtConnect = function (success) { };


/**
 * @param {function(result:[object Object])} success
 */
BluetoothlePlugin.Bluetoothle.prototype.requestPermissionBtConnect = function (success) { };


/**
 * @param {function(result:[object Object])} success
 */
BluetoothlePlugin.Bluetoothle.prototype.hasPermissionBtAdvertise = function (success) { };


/**
 * @param {function(result:[object Object])} success
 */
BluetoothlePlugin.Bluetoothle.prototype.requestPermissionBtAdvertise = function (success) { };

  
/**
 * @param {function(result:[object Object])} isLocationEnabledSuccess
 * @param {function(error:BluetoothlePlugin.Error)} isLocationEnabledError
 */
BluetoothlePlugin.Bluetoothle.prototype.isLocationEnabled = function (isLocationEnabledSuccess, isLocationEnabledError) { };


/**
 * @param {function(result:[object Object])} requestLocationSuccess
 * @param {function(error:BluetoothlePlugin.Error)} requestLocationError
 */
BluetoothlePlugin.Bluetoothle.prototype.requestLocation = function (requestLocationSuccess, requestLocationError) { };


/**
 * @param {function(result:BluetoothlePlugin.InitializeResult)} success
 * @param {function(error:BluetoothlePlugin.Error)} error
 * @param {BluetoothlePlugin.InitPeripheralParams=} opt_params
 */
BluetoothlePlugin.Bluetoothle.prototype.initializePeripheral = function (success, error, opt_params) { };


/**
 * @param {function(result:[object Object])} success
 * @param {function(error:BluetoothlePlugin.Error)} error
 * @param {{service: string, characteristics: Array<BluetoothlePlugin.Characteristic>}} params
 */
BluetoothlePlugin.Bluetoothle.prototype.addService = function (success, error, params) { };


/**
 * @param {function(result:[object Object])} success
 * @param {function(error:BluetoothlePlugin.Error)} error
 * @param {{service: string}} params
 */
BluetoothlePlugin.Bluetoothle.prototype.removeService = function (success, error, params) { };


/**
 * @param {function(result:[object Object])} success
 * @param {function(error:BluetoothlePlugin.Error)} error
 */
BluetoothlePlugin.Bluetoothle.prototype.removeAllServices = function (success, error) { };


/**
 * @param {function(result:[object Object])} success
 * @param {function(error:BluetoothlePlugin.Error)} error
 * @param {BluetoothlePlugin.AdvertisingParams} params
 */
BluetoothlePlugin.Bluetoothle.prototype.startAdvertising = function (success, error, params) { };


/**
 * @param {function(status:BluetoothlePlugin.Status)} success
 * @param {function(error:BluetoothlePlugin.Error)} error
 */
BluetoothlePlugin.Bluetoothle.prototype.stopAdvertising = function (success, error) { };


/**
 * @param {function(result:[object Object])} success
 * @param {function(error:BluetoothlePlugin.Error)} error
 */
BluetoothlePlugin.Bluetoothle.prototype.isAdvertising = function (success, error) { };


/**
 * @param {function(result:[object Object])} success
 * @param {function(error:BluetoothlePlugin.Error)} error
 * @param {BluetoothlePlugin.RespondParams} params
 */
BluetoothlePlugin.Bluetoothle.prototype.respond = function (success, error, params) { };


/**
 * @param {function(result:[object Object])} success
 * @param {function(error:BluetoothlePlugin.Error)} error
 * @param {BluetoothlePlugin.NotifyParams} params
 */
BluetoothlePlugin.Bluetoothle.prototype.notify = function (success, error, params) { };


/**
 * @param {string} value
 * @return {BluetoothlePlugin.Uint8Array}
 */
BluetoothlePlugin.Bluetoothle.prototype.encodedStringToBytes = function (value) { };


/**
 * @param {BluetoothlePlugin.Uint8Array} value
 * @return {string}
 */
BluetoothlePlugin.Bluetoothle.prototype.bytesToEncodedString = function (value) { };


/**
 * @param {string} value
 * @return {BluetoothlePlugin.Uint8Array}
 */
BluetoothlePlugin.Bluetoothle.prototype.stringToBytes = function (value) { };


/**
 * @param {BluetoothlePlugin.Uint8Array} value
 * @return {string}
 */
BluetoothlePlugin.Bluetoothle.prototype.bytesToString = function (value) { };


/**
 * @interface
 */
BluetoothlePlugin.Params = function () { };
BluetoothlePlugin.Params.prototype.address = '';
BluetoothlePlugin.Params.prototype.service = '';

/**
 * @interface
 */
BluetoothlePlugin.InitPeripheralParams = function () { };
BluetoothlePlugin.InitPeripheralParams.prototype.request = false;
BluetoothlePlugin.InitPeripheralParams.prototype.restoreKey = "";

/**
 * @interface
 */
BluetoothlePlugin.InitParams = new BluetoothlePlugin.InitPeripheralParams();
/** Should change in Bluetooth status notifications be sent */
BluetoothlePlugin.InitParams.prototype.statusReceiver = false;


/**
 * @interface
 */
BluetoothlePlugin.ScanParams = function () { };
BluetoothlePlugin.ScanParams.prototype.services = [""];
BluetoothlePlugin.ScanParams.prototype.allowDuplicates = false;
BluetoothlePlugin.ScanParams.prototype.scanMode;
BluetoothlePlugin.ScanParams.prototype.matchMode;
BluetoothlePlugin.ScanParams.prototype.matchNum;
BluetoothlePlugin.ScanParams.prototype.callbackType;
BluetoothlePlugin.ScanParams.prototype.isConnectable = false;

/**
 * @interface
 */
BluetoothlePlugin.ConnectionParams = function () { };
BluetoothlePlugin.ConnectionParams.prototype.address = "";
BluetoothlePlugin.ConnectionParams.prototype.autoConnect = false;
BluetoothlePlugin.ConnectionParams.prototype.transport = 0;


/**

*/
BluetoothlePlugin.AndroidGattTransportMode = {};


/**

*/
BluetoothlePlugin.AndroidGattTransportMode.TRANSPORT_AUTO = {};


/**

*/
BluetoothlePlugin.AndroidGattTransportMode.TRANSPORT_BREDR = {};


/**

*/
BluetoothlePlugin.AndroidGattTransportMode.TRANSPORT_LE = {};


/**
 * @interface
 */
BluetoothlePlugin.NotifyParams = function () { };
BluetoothlePlugin.NotifyParams.prototype.service = "";
BluetoothlePlugin.NotifyParams.prototype.characteristic = "";
BluetoothlePlugin.NotifyParams.prototype.value = "";
BluetoothlePlugin.NotifyParams.prototype.address = "";

/**
 * @interface
 */
BluetoothlePlugin.RespondParams = function () { };
/** This integer value will be incremented every read/writeRequested */
BluetoothlePlugin.RespondParams.prototype.requestId = 0;
/** base64 string */
BluetoothlePlugin.RespondParams.prototype.value = "";
/** not documented */
BluetoothlePlugin.RespondParams.prototype.offset = 0;

/**
 * @interface
 */
BluetoothlePlugin.CharacteristicParams = new BluetoothlePlugin.Params();
BluetoothlePlugin.CharacteristicParams.prototype.characteristics = [''];

/**
 * @interface
 */
BluetoothlePlugin.DescriptorParams = new BluetoothlePlugin.Params();
BluetoothlePlugin.DescriptorParams.prototype.characteristic = '';


/**
 * @interface
 */
BluetoothlePlugin.OperationDescriptorParams = new BluetoothlePlugin.DescriptorParams();
/** The descriptor's ID */
BluetoothlePlugin.OperationDescriptorParams.prototype.descriptor = '';

/**
 * @interface
 */
BluetoothlePlugin.WriteCharacteristicParams = new BluetoothlePlugin.DescriptorParams();
/* Base64 encoded string */
BluetoothlePlugin.WriteCharacteristicParams.prototype.value = '';
/* Set to "noResponse" to enable write without response, all other values will write normally. */
BluetoothlePlugin.WriteCharacteristicParams.prototype.type = '';

/**
 * @interface
 */
BluetoothlePlugin.WriteQCharacteristicParams = new BluetoothlePlugin.WriteCharacteristicParams();
/* Define the size of packets. This should be according to MTU value */
BluetoothlePlugin.WriteQCharacteristicParams.prototype.chunkSize = 0;

/**
 * @interface
 */
BluetoothlePlugin.WriteDescriptorParams = new BluetoothlePlugin.DescriptorParams();
/** The descriptor's ID */
BluetoothlePlugin.WriteDescriptorParams.prototype.descriptor = '';
/** Base64 encoded string, number or string */
BluetoothlePlugin.WriteDescriptorParams.prototype.value = '';

/**
 * @interface
 */
BluetoothlePlugin.AdvertisingParamsAndroid = function () { };
/** Service UUID on Android */
BluetoothlePlugin.AdvertisingParamsAndroid.prototype.service = '';
/** not documented */
BluetoothlePlugin.AdvertisingParamsAndroid.prototype.mode = '';
/** not documented */
BluetoothlePlugin.AdvertisingParamsAndroid.prototype.connectable = false;
/** not documented */
BluetoothlePlugin.AdvertisingParamsAndroid.prototype.timeout = 0;
/** not documented */
BluetoothlePlugin.AdvertisingParamsAndroid.prototype.txPowerLevel = '';
/** not documented */
BluetoothlePlugin.AdvertisingParamsAndroid.prototype.manufacturerId = 0;
/** not documented */
BluetoothlePlugin.AdvertisingParamsAndroid.prototype.manufacturerSpecificData;
/** not documented */
BluetoothlePlugin.AdvertisingParamsAndroid.prototype.includeDeviceName = false;
/** not documented */
BluetoothlePlugin.AdvertisingParamsAndroid.prototype.includeTxPowerLevel = false;

/**
 * @interface
 */
BluetoothlePlugin.AdvertisingParamsIOS = function () { };
/** Array of service UUIDs on iOS */
BluetoothlePlugin.AdvertisingParamsIOS.prototype.services = [""];
/** device's name */
BluetoothlePlugin.AdvertisingParamsIOS.prototype.name = "";

/**
 * @interface
 */
BluetoothlePlugin.CommonInfo = function () { };
/** The device's display name */
BluetoothlePlugin.CommonInfo.prototype.name = "";
/** The device's address / identifier for connecting to the object */
BluetoothlePlugin.CommonInfo.prototype.address = "";

/**
 * @interface
 */
BluetoothlePlugin.DeviceInfo = new BluetoothlePlugin.CommonInfo();
/** Device's status */
BluetoothlePlugin.DeviceInfo.prototype.status = "";

/**
 * @interface
 */
BluetoothlePlugin.RSSI = new BluetoothlePlugin.DeviceInfo();
/** signal strength */
BluetoothlePlugin.RSSI.prototype.rssi = 0;

/**
 * @interface
 */
BluetoothlePlugin.MTU = new BluetoothlePlugin.DeviceInfo();
/* mtu value */
BluetoothlePlugin.MTU.prototype.mtu = 0;

/**
 * @interface
 */
BluetoothlePlugin.BondedStatus = new BluetoothlePlugin.CommonInfo();
/** Bonded status*/
BluetoothlePlugin.BondedStatus.prototype.isBonded = false;

/**
 * @interface
 */
BluetoothlePlugin.PrevConnectionStatus = new BluetoothlePlugin.CommonInfo();
/** Determine whether the device was connected */
BluetoothlePlugin.PrevConnectionStatus.prototype.wasConnected = false;

/**
 * @interface
 */
BluetoothlePlugin.CurrConnectionStatus = new BluetoothlePlugin.CommonInfo();
/** Determine whether the device is connected */
BluetoothlePlugin.CurrConnectionStatus.prototype.isConnected = false;

/**
 * @interface
 */
BluetoothlePlugin.DiscoverStatus = new BluetoothlePlugin.CommonInfo();
/** Determine whether the device's characteristics and descriptors have been discovered */
BluetoothlePlugin.DiscoverStatus.prototype.isDiscovered = false;

/**
 * @interface
 */
BluetoothlePlugin.advertisement = function () { };
/** An array of service UUIDs */
BluetoothlePlugin.advertisement.prototype.serviceUuids = [""];
/** A string representing the name of the manufacturer of the device */
BluetoothlePlugin.advertisement.prototype.manufacturerData = "";
/** A number containing the transmit power of a peripheral */
BluetoothlePlugin.advertisement.prototype.txPowerLevel = 0;
/** An array of one or more CBUUID objects, representing CBService UUIDs that were found in the “overflow” area of the advertisement data */
BluetoothlePlugin.advertisement.prototype.overflowServiceUuids = [""];
/** A boolean value that indicates whether the advertising event type is connectable */
BluetoothlePlugin.advertisement.prototype.isConnectable = false;
/** An array of one or more CBUUID objects, representing CBService UUIDs */
BluetoothlePlugin.advertisement.prototype.solicitedServiceUuids = [""];
/* A dictionary containing service-specific advertisement data */
BluetoothlePlugin.advertisement.prototype.serviceData;
/* A string containing the local name of a peripheral */
BluetoothlePlugin.advertisement.prototype.localName = "";

/**
 * @interface
 */
BluetoothlePlugin.ScanStatus = new BluetoothlePlugin.DeviceInfo();
/** signal strength */
BluetoothlePlugin.ScanStatus.prototype.rssi = 0;
/**
 * advertisement data in encoded string of bytes, use bluetoothle.encodedStringToBytes() (Android)
 * advertisement hash with the keys (iOS)
 * empty (Windows)
 * @type {string|BluetoothlePlugin.advertisement}
 */
BluetoothlePlugin.ScanStatus.prototype.advertisement = "";

/**
 * @interface
 */
BluetoothlePlugin.Service = function () { };
/** Service's uuid */
BluetoothlePlugin.Service.prototype.uuid = "";
/** Array of characteristics */
BluetoothlePlugin.Service.prototype.characteristics = new BluetoothlePlugin.Characteristic();

/**
 * @interface
 */
BluetoothlePlugin.Characteristic = function () { };
/* Array of descriptors */
BluetoothlePlugin.Characteristic.prototype.descriptors = [new BluetoothlePlugin.Descriptor()];
/**  Characteristic's uuid */
BluetoothlePlugin.Characteristic.prototype.uuid = "";
BluetoothlePlugin.Characteristic.prototype.properties = new BluetoothlePlugin.CharProperties();
BluetoothlePlugin.Characteristic.prototype.permissions = BluetoothlePlugin.CharPermissions();

/**
 *  Characteristi's properties
 *  If the property is defined as a key, the characteristic has that property
 */
BluetoothlePlugin.CharProperties = function () { };
BluetoothlePlugin.CharProperties.prototype.write = false;
BluetoothlePlugin.CharProperties.prototype.broadcast = false;
BluetoothlePlugin.CharProperties.prototype.extendedProps = false;
BluetoothlePlugin.CharProperties.prototype.writeWithoutResponse = false;
BluetoothlePlugin.CharProperties.prototype.writeNoResponse = false;
BluetoothlePlugin.CharProperties.prototype.signedWrite = false;
BluetoothlePlugin.CharProperties.prototype.read = false;
BluetoothlePlugin.CharProperties.prototype.notify = false;
BluetoothlePlugin.CharProperties.prototype.indicate = false;
BluetoothlePlugin.CharProperties.prototype.authenticatedSignedWrites = false;
BluetoothlePlugin.CharProperties.prototype.notifyEncryptionRequired = false;
BluetoothlePlugin.CharProperties.prototype.indicateEncryptionRequired = false;

/**
 *  If the permission is defined as a key, the character has that permission
 */
BluetoothlePlugin.CharPermissions = function () { };
BluetoothlePlugin.CharPermissions.prototype.read = false;
BluetoothlePlugin.CharPermissions.prototype.readEncrypted = false;
BluetoothlePlugin.CharPermissions.prototype.readEncryptedMITM = false;
BluetoothlePlugin.CharPermissions.prototype.write = false;
BluetoothlePlugin.CharPermissions.prototype.writeSigned = false;
BluetoothlePlugin.CharPermissions.prototype.writeSignedMITM = false;
BluetoothlePlugin.CharPermissions.prototype.writeEncryptedMITM = false;
BluetoothlePlugin.CharPermissions.prototype.readEncryptionRequired = false;
BluetoothlePlugin.CharPermissions.prototype.writeEncryptionRequired = false;


/**
 * @interface
 */
BluetoothlePlugin.Descriptor = function () { };
BluetoothlePlugin.Descriptor.prototype.uuid = "";

/**
 * @interface
 */
BluetoothlePlugin.Device = new BluetoothlePlugin.DeviceInfo();
/** Device's services */
BluetoothlePlugin.Device.prototype.services = [new BluetoothlePlugin.Service()];

/**
 * @interface
 */
BluetoothlePlugin.Services = new BluetoothlePlugin.DeviceInfo();
/** Array of service UUIDS */
BluetoothlePlugin.Services.prototype.services = [""];

/**
 * @interface
 */
BluetoothlePlugin.Descriptors = new BluetoothlePlugin.DeviceInfo();
/** Characteristic's UUID */
BluetoothlePlugin.Descriptors.prototype.characteristic = "";
/** Service's UUID */
BluetoothlePlugin.Descriptors.prototype.service = "";
/* Array of descriptor UUIDs */
BluetoothlePlugin.Descriptors.prototype.descriptors = [""];

/**
 * @interface
 */
BluetoothlePlugin.OperationResult = new BluetoothlePlugin.DeviceInfo();
/** Characteristic UUID */
BluetoothlePlugin.OperationResult.prototype.characteristic = "";
/** Service's UUID */
BluetoothlePlugin.OperationResult.prototype.service = "";
/** Base64 encoded string of bytes */
BluetoothlePlugin.OperationResult.prototype.value = "";

/**
 * @interface
 */
BluetoothlePlugin.UnsubscribeResult = new BluetoothlePlugin.DeviceInfo();
/** Characteristic UUID */
BluetoothlePlugin.UnsubscribeResult.prototype.characteristic = "";
/** Service's UUID */
BluetoothlePlugin.UnsubscribeResult.prototype.service = "";

/**
 * @interface
 */
BluetoothlePlugin.DescriptorResult = new BluetoothlePlugin.OperationResult();
BluetoothlePlugin.DescriptorResult.prototype.descriptor = "";

/**
 * @interface
 */
BluetoothlePlugin.Characteristics = new BluetoothlePlugin.DeviceInfo();
/** Service's id */
BluetoothlePlugin.Characteristics.prototype.service = "";
/** Array of characteristic objects*/
BluetoothlePlugin.Characteristics.prototype.characteristics = [new BluetoothlePlugin.Characteristic];

/**
 * @interface
 */
BluetoothlePlugin.InitializeResult = function () { };
/** Device's status */
BluetoothlePlugin.InitializeResult.prototype.status = new BluetoothlePlugin.Status();
/** The address/identifier provided by the scan's return object */
BluetoothlePlugin.InitializeResult.prototype.address = "";
/** Service's UUID */
BluetoothlePlugin.InitializeResult.prototype.service = "";
/** Characteristic UUID */
BluetoothlePlugin.InitializeResult.prototype.characteristic = "";
/** This integer value will be incremented every read/writeRequested */
BluetoothlePlugin.InitializeResult.prototype.requestId = 0;
/** Offset value */
BluetoothlePlugin.InitializeResult.prototype.offset = 0;
/** mtu value */
BluetoothlePlugin.InitializeResult.prototype.mtu = 0;
/** Base64 encoded string of bytes */
BluetoothlePlugin.InitializeResult.prototype.value = "";

/**

*/
BluetoothlePlugin.BluetoothScanMode = {};


/**

*/
BluetoothlePlugin.BluetoothScanMode.SCAN_MODE_OPPORTUNISTIC = {};


/**

*/
BluetoothlePlugin.BluetoothScanMode.SCAN_MODE_LOW_POWER = {};


/**

*/
BluetoothlePlugin.BluetoothScanMode.SCAN_MODE_BALANCED = {};


/**

*/
BluetoothlePlugin.BluetoothScanMode.SCAN_MODE_LOW_LATENCY = {};


/**

*/
BluetoothlePlugin.BluetoothMatchMode = {};


/**

*/
BluetoothlePlugin.BluetoothMatchMode.MATCH_MODE_AGRESSIVE = {};


/**

*/
BluetoothlePlugin.BluetoothMatchMode.MATCH_MODE_STICKY = {};


/**

*/
BluetoothlePlugin.BluetoothMatchNum = {};


/**

*/
BluetoothlePlugin.BluetoothMatchNum.MATCH_NUM_ONE_ADVERTISEMENT = {};


/**

*/
BluetoothlePlugin.BluetoothMatchNum.MATCH_NUM_FEW_ADVERTISEMENT = {};


/**

*/
BluetoothlePlugin.BluetoothMatchNum.MATCH_NUM_MAX_ADVERTISEMENT = {};


/**

*/
BluetoothlePlugin.BluetoothCallbackType = {};


/**

*/
BluetoothlePlugin.BluetoothCallbackType.CALLBACK_TYPE_ALL_MATCHES = {};


/**

*/
BluetoothlePlugin.BluetoothCallbackType.CALLBACK_TYPE_FIRST_MATCH = {};


/**

*/
BluetoothlePlugin.BluetoothCallbackType.CALLBACK_TYPE_MATCH_LOST = {};


/**
 * @interface
 */
BluetoothlePlugin.Error = function () { };
BluetoothlePlugin.Error.prototype.error="";
BluetoothlePlugin.Error.prototype.message="";

window.bluetoothle = BluetoothlePlugin;