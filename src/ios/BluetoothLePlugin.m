#import "BluetoothLePlugin.h"

//Plugin Name
NSString *const pluginName = @"bluetoothleplugin";

//Object Keys
NSString *const keyStatus = @"status";
NSString *const keyError = @"error";
NSString *const keyRequest = @"request";
NSString *const keyMessage = @"message";
NSString *const keyName = @"name";
NSString *const keyAddress = @"address";
NSString *const keyProperties = @"properties";
NSString *const keyRssi = @"rssi";
NSString *const keyAdvertisement = @"advertisement";
NSString *const keyServiceUuids = @"serviceUuids";
NSString *const keyCharacteristicUuids = @"characteristicUuids";
NSString *const keyCharacteristics = @"characteristics";
NSString *const keyDescriptorUuids = @"descriptorUuids";
NSString *const keyServiceUuid = @"serviceUuid";
NSString *const keyCharacteristicUuid = @"characteristicUuid";
NSString *const keyDescriptorUuid = @"descriptorUuid";
NSString *const keyValue = @"value";
NSString *const keyType = @"type";
NSString *const keyIsInitialized = @"isInitalized";
NSString *const keyIsEnabled = @"isEnabled";
NSString *const keyIsScanning = @"isScanning";
NSString *const keyIsConnected = @"isConnected";
NSString *const keyIsDiscovered = @"isDiscovered";

//Write Type
NSString *const writeTypeNoResponse = @"noResponse";

//Properties
NSString *const propertyBroadcast = @"broadcast";
NSString *const propertyRead = @"read";
NSString *const propertyWriteWithoutResponse = @"writeWithoutResponse";
NSString *const propertyWrite = @"write";
NSString *const propertyNotify = @"notify";
NSString *const propertyIndicate = @"indicate";
NSString *const propertyAuthenticatedSignedWrites = @"authenticatedSignedWrites";
NSString *const propertyExtendedProperties = @"extendedProperties";
NSString *const propertyNotifyEncryptionRequired = @"notifyEncryptionRequired";
NSString *const propertyIndicateEncryptionRequired = @"indicateEncryptionRequired";

//Status Types
NSString *const statusEnabled = @"enabled";
NSString *const statusScanStarted = @"scanStarted";
NSString *const statusScanStopped = @"scanStopped";
NSString *const statusScanResult = @"scanResult";
NSString *const statusConnected = @"connected";
NSString *const statusConnecting = @"connecting";
NSString *const statusDisconnected = @"disconnected";
NSString *const statusDisconnecting = @"disconnecting";
NSString *const statusClosed = @"closed";
NSString *const statusDiscoveredServices = @"discoveredServices";
NSString *const statusDiscoveredCharacteristics = @"discoveredCharacteristics";
NSString *const statusDiscoveredDescriptors = @"discoveredDescriptors";
NSString *const statusRead = @"read";
NSString *const statusSubscribed = @"subscribed";
NSString *const statusSubscribedResult = @"subscribedResult";
NSString *const statusUnsubscribed = @"unsubscribed";
NSString *const statusWritten = @"written";
NSString *const statusReadDescriptor = @"readDescriptor";
NSString *const statusWrittenDescriptor = @"writtenDescriptor";
NSString *const statusRssi = @"rssi";

//Error Types
NSString *const errorInitialize = @"initialize";
NSString *const errorEnable = @"enable";
NSString *const errorArguments = @"arguments";
NSString *const errorStartScan = @"startScan";
NSString *const errorStopScan = @"stopScan";
NSString *const errorConnect = @"connect";
NSString *const errorReconnect = @"reconnect";
NSString *const errorDiscoverServices = @"discoverServices";
NSString *const errorDiscoverCharacteristics = @"discoverCharacteristics";
NSString *const errorDiscoverDescriptors = @"discoverDescriptors";
NSString *const errorRead = @"read";
NSString *const errorSubscription = @"subscription";
NSString *const errorWrite = @"write";
NSString *const errorReadDescriptor = @"readDescriptor";
NSString *const errorWriteDescriptor = @"writeDescriptor";
NSString *const errorRssi = @"rssi";
NSString *const errorNeverConnected = @"neverConnected";
NSString *const errorIsNotDisconnected = @"isNotDisconnected";
NSString *const errorIsNotConnected = @"isNotConnected";
NSString *const errorIsDisconnected = @"isDisconnected";
NSString *const errorService = @"service";
NSString *const errorCharacteristic = @"characteristic";
NSString *const errorDescriptor = @"descriptor";

//Error Messages
//Initialization
NSString *const logPoweredOff = @"Bluetooth powered off";
NSString *const logUnauthorized = @"Bluetooth unauthorized";
NSString *const logUnknown = @"Bluetooth unknown state";
NSString *const logResetting = @"Bluetooth resetting";
NSString *const logUnsupported = @"Bluetooth unsupported";
NSString *const logNotInit = @"Bluetooth not initialized";
NSString *const logNotEnabled = @"Bluetooth not enabled";
//Scanning
NSString *const logAlreadyScanning = @"Scanning already in progress";
NSString *const logNotScanning = @"Not scanning";
//Connection
NSString *const logPreviouslyConnected = @"Device previously connected, reconnect or close for new device";
NSString *const logNeverConnected = @"Never connected to device";
NSString *const logIsNotConnected = @"Device isn't connected";
NSString *const logIsNotDisconnected = @"Device isn't disconnected";
NSString *const logIsDisconnected = @"Device is disconnected";
NSString *const logNoAddress = @"No device address";
NSString *const logNoDevice = @"Device not found";
//Read/write
NSString *const logNoArgObj = @"Argument object not found";
NSString *const logNoService = @"Service not found";
NSString *const logNoCharacteristic = @"Characteristic not found";
NSString *const logNoDescriptor = @"Descriptor not found";
NSString *const logWriteValueNotFound = @"Write value not found";
NSString *const logWriteDescriptorValueNotFound = @"Write descriptor value not found";

NSString *const operationRead = @"read";
NSString *const operationSubscribe = @"subscribe";
NSString *const operationUnsubscribe = @"unsubscribe";
NSString *const operationWrite = @"write";


@implementation BluetoothLePlugin

//Actions
- (void)initialize:(CDVInvokedUrlCommand *)command
{
    //Save the callback
    initCallback = command.callbackId;
    
    //If central manager has been initialized already, return status=>enabled success or enable error
    if (centralManager != nil)
    {
        NSDictionary* returnObj = nil;
        CDVPluginResult* pluginResult = nil;
        if ([centralManager state] == CBCentralManagerStatePoweredOn)
        {
            
            returnObj = [NSDictionary dictionaryWithObjectsAndKeys: statusEnabled, keyStatus, nil];
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
        }
        else
        {
            returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorEnable, keyError, nil];
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        }
        
        [pluginResult setKeepCallbackAsBool:true];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:initCallback];
    
        return;
    }
    
    //By default, dont request the user to enable Bluetooth
    NSNumber* request = [NSNumber numberWithBool:NO];
    
    //Check arguments to see if default value is overwritten
    NSDictionary* obj = [self getArgsObject:command.arguments];
    if (obj != nil)
    {
        request = [self getRequest:obj];
    }
    
    //Initialize central manager
    centralManager = [[CBCentralManager alloc] initWithDelegate:self queue:nil options:@{ CBCentralManagerOptionRestoreIdentifierKey:pluginName, CBCentralManagerOptionShowPowerAlertKey:request }];
}

- (void)startScan:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }
    
    //Ensure scan isn't already running
    if (scanCallback != nil)
    {
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorStartScan, keyError, logAlreadyScanning, keyMessage, nil];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        return;
    }
    
    //Get an array of service assigned numbers to filter by
    NSDictionary *obj = [self getArgsObject:command.arguments];
    NSMutableArray* serviceUuids = nil;
    if (obj != nil)
    {
        serviceUuids = [self getUuids:obj forType:keyServiceUuids];
    }

    //Set the callback
    scanCallback = command.callbackId;
    
    //Send scan started status
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: statusScanStarted, keyStatus, nil];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:true];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:scanCallback];
    
    //Start the scan
    [centralManager scanForPeripheralsWithServices:serviceUuids options:nil];
}

- (void)stopScan:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }
    
    //Ensure scan is running
    if (scanCallback == nil)
    {
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorStartScan, keyError, logNotScanning, keyMessage, nil];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        return;
    }
    
    //Remove the scan callback
    scanCallback = nil;

    //Stop the scan
    [centralManager stopScan];
    
    //Return a callback
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: statusScanStopped, keyStatus, nil];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)connect:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }
    
    //If device previously connected or attempted connection, use reconnect or disconnect/close
    if (activePeripheral != nil)
    {
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorConnect, keyError, logPreviouslyConnected, keyMessage, nil];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        return;
    }
    
    //Get the arguments
    NSDictionary* obj = [self getArgsObject:command.arguments];
    if ([self isNotArgsObject:obj :command])
    {
        return;
    }
    
    //Get the device address
    NSUUID* address = [self getAddress:obj];
    if (address == nil)
    {
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorConnect, keyError, logNoAddress, keyMessage, nil];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        return;
    }

    //Get the peripherals and ensure at least one exists
    NSArray* peripherals = [centralManager retrievePeripheralsWithIdentifiers:@[address]];
    if (peripherals.count == 0)
    {
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorConnect, keyError, logNoDevice, keyMessage, nil];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        return;
    }
    
    //Set the active peripheral
    activePeripheral = peripherals[0];
    [activePeripheral setDelegate:self];
    
    //Get the name, which could be null
    NSObject* name = [self formatName:activePeripheral.name];
    
    //Set the connect callback
    connectCallback = command.callbackId;
    
    //Send back connecting callback
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: statusConnecting, keyStatus, name, keyName, [activePeripheral.identifier UUIDString], keyAddress, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:true];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:connectCallback];

    //Attempt the actual connection
    [centralManager connectPeripheral:activePeripheral options:nil];
}

- (void)reconnect:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }
    
    //If never connected or attempted connected, reconnect can't be used
    if ([self wasNeverConnected:command])
    {
        return;
    }
    
    //If currently connected, reconnect can't be used
    if ([self isNotDisconnected:command])
    {
        return;
    }
    
    //Set the connect callback
    connectCallback = command.callbackId;
    
    //Get the name, which could be null
    NSObject* name = [self formatName:activePeripheral.name];
    
    //Return the connecting status callback
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: statusConnecting, keyStatus, name, keyName, [activePeripheral.identifier UUIDString], keyAddress, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:true];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:connectCallback];
    
    //Try to reconnect
    [centralManager connectPeripheral:activePeripheral options:nil];
}

- (void)disconnect:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }
    
    //If device was never connected or attempted connected, disconnect can't be used
    if ([self wasNeverConnected:command])
    {
        return;
    }
    
    //If device is disconnected alrady, disconnect can't be used
    if ([self isDisconnected:command])
    {
        return;
    }
    
    //Get the name, which could be null
    NSObject* name = [self formatName:activePeripheral.name];
    
    //If currently connecting, just cancel the pending connecting and return disconnecting status without saving callback
    if (activePeripheral.state == CBPeripheralStateConnecting)
    {
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: statusDisconnecting, keyStatus, name, keyName, [activePeripheral.identifier UUIDString], keyAddress, nil];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        
        connectCallback = nil;
    }
    //Else return disconnecting status and save callback for disconnect status
    else
    {
        connectCallback = command.callbackId;
        
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: statusDisconnecting, keyStatus, name, keyName, [activePeripheral.identifier UUIDString], keyAddress, nil];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:true];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:connectCallback];
    }
    
    //Disconnect
    [centralManager cancelPeripheralConnection:activePeripheral];
}

- (void)close:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }
    
    //If a device was never connected or attempted connected, close can't be called
    if ([self wasNeverConnected:command])
    {
        return;
    }
    
    //If device isn't disconnected, close can't be called
    if ([self isNotDisconnected:command])
    {
        return;
    }
    
    //Get the name, which could be null
    NSObject* name = [self formatName:activePeripheral.name];
    
    //Create dictionary with status message and optionally add device information
    NSMutableDictionary* returnObj = [NSMutableDictionary dictionaryWithObjectsAndKeys: statusClosed, keyStatus, name, keyName, [activePeripheral.identifier UUIDString], keyAddress, nil];

    //Set peripheral and callback to null
    activePeripheral = nil;
    connectCallback = nil;
    
    //Clear all the operation callbacks
    [self clearOperationCallbacks];

    //Return success callback
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)discover:(CDVInvokedUrlCommand *)command
{
    //Do nothing if discover is called on iOS
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)services:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }
    
    //Ensure device was connected
    if ([self wasNeverConnected:command])
    {
        return;
    }
    
    //Ensure device is connected
    if ([self isNotConnected:command])
    {
        return;
    }
    
    //Get an array of service assigned numbers to filter by
    NSDictionary *obj = [self getArgsObject:command.arguments];
    if ([self isNotArgsObject:obj :command])
    {
        return;
    }
    
    //Get the serviceUuids to discover
    NSMutableArray* serviceUuids = [self getUuids:obj forType:keyServiceUuids];
    
    //Set the discover callback
    discoverCallback = command.callbackId;
    
    //Discover the services
    [activePeripheral discoverServices:serviceUuids];
}

- (void)characteristics:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }
    
    //Ensure device was connected
    if ([self wasNeverConnected:command])
    {
        return;
    }
    
    //Ensure device is connected
    if ([self isNotConnected:command])
    {
        return;
    }
    
    //Get arguments
    NSDictionary *obj = [self getArgsObject:command.arguments];
    
    if ([self isNotArgsObject:obj :command])
    {
        return;
    }
    
    //Get the service
    CBService* service = [self getService:obj];
    if ([self isNotService:service :command])
    {
        return;
    }
    
    //Get the characteristic UUIDs
    NSMutableArray* characteristicUuids = [self getUuids:obj forType:keyCharacteristicUuids];
    
    //Set the discover callback
    discoverCallback = command.callbackId;
    
    //Discover the characteristics for the service
    [activePeripheral discoverCharacteristics:characteristicUuids forService:service];
}

- (void)descriptors:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }
    
    //Ensure device was connected
    if ([self wasNeverConnected:command])
    {
        return;
    }
    
    //Ensure device is connected
    if ([self isNotConnected:command])
    {
        return;
    }
    
    //Get arguments
    NSDictionary *obj = [self getArgsObject:command.arguments];
    if ([self isNotArgsObject:obj :command])
    {
        return;
    }
    
    //Get the service
    CBService* service = [self getService:obj];
    if ([self isNotService:service :command])
    {
        return;
    }
    
    //Get the characteristic
    CBCharacteristic* characteristic = [self getCharacteristic:obj forService:service];
    if ([self isNotCharacteristic:characteristic :command])
    {
        return;
    }
    
    //Set the discovery callback
    discoverCallback = command.callbackId;
    
    //Discover the descriptors
    [activePeripheral discoverDescriptorsForCharacteristic:characteristic];
}

- (void)read:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }
    
    //Ensure device was connected
    if ([self wasNeverConnected:command])
    {
        return;
    }
    
    //Ensure device is connected
    if ([self isNotConnected:command])
    {
        return;
    }
    
    //Get arguments
    NSDictionary *obj = [self getArgsObject:command.arguments];
    if ([self isNotArgsObject:obj :command])
    {
        return;
    }
    
    //Get service
    CBService* service = [self getService:obj];
    if ([self isNotService:service :command])
    {
        return;
    }
    
    //Get characteristic
    CBCharacteristic* characteristic = [self getCharacteristic:obj forService:service];
    if ([self isNotCharacteristic:characteristic :command])
    {
        return;
    }
    
    //Set the callback
    [self addCallback:characteristic.UUID forOperationType:operationRead forCallback:command.callbackId];
    
    //Read the value
    [activePeripheral readValueForCharacteristic:characteristic]; 
}

- (void)subscribe:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }
    
    //Ensure device was connected
    if ([self wasNeverConnected:command])
    {
        return;
    }
    
    //Ensure device is connected
    if ([self isNotConnected:command])
    {
        return;
    }
    
    //Get arguments
    NSDictionary *obj = [self getArgsObject:command.arguments];
    if ([self isNotArgsObject:obj :command])
    {
        return;
    }
    
    //Get the service
    CBService* service = [self getService:obj];
    if ([self isNotService:service :command])
    {
        return;
    }
    
    //Get the characteristic
    CBCharacteristic* characteristic = [self getCharacteristic:obj forService:service];
    if ([self isNotCharacteristic:characteristic :command])
    {
        return;
    }
    
    //Set the subscribe callback
    [self addCallback:characteristic.UUID forOperationType:operationSubscribe forCallback:command.callbackId];
    
    //Start the subscription
    [activePeripheral setNotifyValue:true forCharacteristic:characteristic];
}

- (void)unsubscribe:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }
    
    //Ensure device was connected
    if ([self wasNeverConnected:command])
    {
        return;
    }
    
    //Ensure device is connected
    if ([self isNotConnected:command])
    {
        return;
    }
    
    //Get arguments
    NSDictionary *obj = [self getArgsObject:command.arguments];
    if ([self isNotArgsObject:obj :command])
    {
        return;
    }
    
    //Get the service
    CBService* service = [self getService:obj];
    if ([self isNotService:service :command])
    {
        return;
    }
    
    //Get the characteristic
    CBCharacteristic* characteristic = [self getCharacteristic:obj forService:service];
    if ([self isNotCharacteristic:characteristic :command])
    {
        return;
    }
    
    //Add the callback
    [self addCallback:characteristic.UUID forOperationType:operationUnsubscribe forCallback:command.callbackId];
    
    //Unsubscribe the characteristic
    [activePeripheral setNotifyValue:false forCharacteristic:characteristic]; 
}

- (void)write:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }
    
    //Ensure device was connected
    if ([self wasNeverConnected:command])
    {
        return;
    }
    
    //Ensure device is connected
    if ([self isNotConnected:command])
    {
        return;
    }
    
    //Get arguments
    NSDictionary *obj = [self getArgsObject:command.arguments];
    if ([self isNotArgsObject:obj :command])
    {
        return;
    }
    
    //Get service
    CBService* service = [self getService:obj];
    
    if ([self isNotService:service :command])
    {
        return;
    }
    
    //Get characteristic
    CBCharacteristic* characteristic = [self getCharacteristic:obj forService:service];
    if ([self isNotCharacteristic:characteristic :command])
    {
        return;
    }
    
    //Get the value to write
    NSData* value = [self getValue:obj];
    //And ensure it's not empty
    if (value == nil)
    {
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorWrite, keyError, logWriteValueNotFound, keyMessage, nil];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        return;
    }
    
    //Add callback
    [self addCallback:characteristic.UUID forOperationType:operationWrite forCallback:command.callbackId];
    
    //Get the write type (response or no response)
    int writeType = [self getWriteType:obj];
    
    //Try to write value
    [activePeripheral writeValue:value forCharacteristic:characteristic type:writeType];
}

- (void)readDescriptor:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }
    
    //Ensure device was connected
    if ([self wasNeverConnected:command])
    {
        return;
    }
    
    //Ensure device is connected
    if ([self isNotConnected:command])
    {
        return;
    }
    
    //Get arguments
    NSDictionary *obj = [self getArgsObject:command.arguments];
    if ([self isNotArgsObject:obj :command])
    {
        return;
    }
    
    //Get service
    CBService* service = [self getService:obj];
    if ([self isNotService:service :command])
    {
        return;
    }
    
    //Get characteristic
    CBCharacteristic* characteristic = [self getCharacteristic:obj forService:service];
    if ([self isNotCharacteristic:characteristic :command])
    {
        return;
    }
    
    //Get descriptor
    CBDescriptor* descriptor = [self getDescriptor:obj forCharacteristic:characteristic];
    if ([self isNotDescriptor:descriptor :command])
    {
        return;
    }
    
    //Set callback
    //TODO add this to callback mapping
    descriptorCallback = command.callbackId;
    
    //Try to read descriptor value
    [activePeripheral readValueForDescriptor:descriptor];
}

- (void)writeDescriptor:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }
    
    //Ensure device was connected
    if ([self wasNeverConnected:command])
    {
        return;
    }
    
    //Ensure device is connected
    if ([self isNotConnected:command])
    {
        return;
    }
    
    //Get arguments
    NSDictionary *obj = [self getArgsObject:command.arguments];
    if ([self isNotArgsObject:obj :command])
    {
        return;
    }
    
    //Get service
    CBService* service = [self getService:obj];
    if ([self isNotService:service :command])
    {
        return;
    }
    
    //Get characteristic
    CBCharacteristic* characteristic = [self getCharacteristic:obj forService:service];
    if ([self isNotCharacteristic:characteristic :command])
    {
        return;
    }
    
    //Get descriptor
    CBDescriptor* descriptor = [self getDescriptor:obj forCharacteristic:characteristic];
    if ([self isNotDescriptor:descriptor :command])
    {
        return;
    }
    
    //Get value to write
    NSData* value = [self getValue:obj];
    //And ensure it's not null
    if (value == nil)
    {
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorWriteDescriptor, keyError, logWriteDescriptorValueNotFound, keyMessage, nil];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        return;
    }
    
    //Set descriptor callback
    //TODO Change to callback mapping
    descriptorCallback = command.callbackId;
    
    //Try to write the descriptor
    [activePeripheral writeValue:value forDescriptor:descriptor];
}

- (void)rssi:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }
    
    //Ensure device was connected
    if ([self wasNeverConnected:command])
    {
        return;
    }
    
    //Ensure device is connected
    if ([self isNotConnected:command])
    {
        return;
    }
    
    //Set the callback
    rssiCallback = command.callbackId;
    
    //Try to read RSSI
    [activePeripheral readRSSI];
}

- (void)isInitialized:(CDVInvokedUrlCommand *)command
{
    //See if Bluetooth has been initialized
    NSNumber* result = [NSNumber numberWithBool:(centralManager != nil)];
    
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: result, keyIsInitialized, nil];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)isEnabled:(CDVInvokedUrlCommand *)command
{
    //See if Bluetooth is currently enabled
    NSNumber* result = [NSNumber numberWithBool:(centralManager != nil && centralManager.state == CBCentralManagerStatePoweredOn)];
    
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: result, keyIsEnabled, nil];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)isScanning:(CDVInvokedUrlCommand *)command
{
    //See if Bluetooth is scanning
    NSNumber* result = [NSNumber numberWithBool:(scanCallback != nil)];
    
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: result, keyIsScanning, nil];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)isConnected:(CDVInvokedUrlCommand *)command
{
    //See if device is connected
    NSNumber* result = [NSNumber numberWithBool:(activePeripheral != nil && activePeripheral.state == CBPeripheralStateConnected)];
    
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: result, keyIsConnected, nil];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)isDiscovered:(CDVInvokedUrlCommand *)command
{
    //See if device is discovered
    NSNumber* result = [NSNumber numberWithBool:(false)];
    
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: result, keyIsDiscovered, nil];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

//Central Manager Delegates
- (void) centralManagerDidUpdateState:(CBCentralManager *)central
{
    //If no callback, don't return anything
    if (initCallback == nil)
    {
        return;
    }
    
    //Decide on error message
    NSString* error = nil;
    switch ([centralManager state])
    {
        case CBCentralManagerStatePoweredOff:
        {
            error = logPoweredOff;
            break;
        }
            
        case CBCentralManagerStateUnauthorized:
        {
            error = logUnauthorized;
            break;
        }
            
        case CBCentralManagerStateUnknown:
        {
            error = logUnknown;
            break;
        }
            
        case CBCentralManagerStateResetting:
        {
            error = logResetting;
            break;
        }
            
        case CBCentralManagerStateUnsupported:
        {
            error = logUnsupported;
            break;
        }
            
        case CBCentralManagerStatePoweredOn:
        {
            //Bluetooth on!
            break;
        }
    }
    
    NSDictionary* returnObj = nil;
    CDVPluginResult* pluginResult = nil;

    //If error message exists, send error
    if (error != nil)
    {
        returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorEnable, keyError, error, keyMessage, nil];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        
        //Clear out the callbacks cause user will need to connect again after Bluetooth is back on
        scanCallback = nil;
        connectCallback = nil;
        [self clearOperationCallbacks];
        activePeripheral = nil;
    }
    //Else enabling was successful
    else
    {
        returnObj = [NSDictionary dictionaryWithObjectsAndKeys: statusEnabled, keyStatus, nil];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    }
    
    [pluginResult setKeepCallbackAsBool:true];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:initCallback];
}

- (void)centralManager:(CBCentralManager *)central willRestoreState:(NSDictionary *)dict
{
    //Trying to remember why this was here...
}

- (void)centralManager:(CBCentralManager *)central didDiscoverPeripheral:(CBPeripheral *)peripheral advertisementData:(NSDictionary *)advertisementData RSSI:(NSNumber *)RSSI
{
    //If no scan callback, nothing can be returned
    if (scanCallback == nil)
    {
        return;
    }
    
    //Return all the device details
    NSObject* name = [self formatName:peripheral.name];
    NSData* data = [advertisementData valueForKey:CBAdvertisementDataManufacturerDataKey];
    NSString* dataString = [data base64EncodedStringWithOptions:0];
    NSMutableDictionary* returnObj = [NSMutableDictionary dictionaryWithObjectsAndKeys: statusScanResult, keyStatus, name, keyName, peripheral.identifier.UUIDString, keyAddress, RSSI, keyRssi, dataString, keyAdvertisement, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:true];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:scanCallback];
}

- (void)centralManager:(CBCentralManager *)central didConnectPeripheral:(CBPeripheral *)peripheral
{
    //On new connection, reset the operation callbacks
    operationCallbacks = [NSMutableDictionary dictionary];
    
    //If no connect callback, can't continue
    if (connectCallback == nil)
    {
        return;
    }
    
    //Return device information of what was connected
    NSObject* name = [self formatName:peripheral.name];
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: statusConnected, keyStatus, name, keyName, [peripheral.identifier UUIDString], keyAddress, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    //Keep in case device gets disconnected without user initiation
    [pluginResult setKeepCallbackAsBool:true];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:connectCallback];
}

- (void)centralManager:(CBCentralManager *)central didFailToConnectPeripheral:(CBPeripheral *)peripheral error:(NSError *)error
{
    //If no connect callback, can't continue
    if (connectCallback == nil)
    {
        return;
    }
    
    //Return the error message
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorConnect, keyError, error.description, keyMessage, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:connectCallback];
    
    //And clear callback
    connectCallback = nil;
}

- (void)centralManager:(CBCentralManager *)central didDisconnectPeripheral:(CBPeripheral *)peripheral error:(NSError *)error
{
    //Clear out the operation callbacks
    [self clearOperationCallbacks];
    
    //If no connect callback, can't continue
    if (connectCallback == nil)
    {
        return;
    }
    
    //Return disconnected device information
    NSObject* name = [self formatName:peripheral.name];
    
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: statusDisconnected, keyStatus, name, keyName, [peripheral.identifier UUIDString], keyAddress, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:connectCallback];
    
    //Clear callback
    connectCallback = nil;
}

//Peripheral Delegates
- (void)peripheral:(CBPeripheral *)peripheral didDiscoverServices:(NSError *)error
{
    //Return if callback is null
    if (discoverCallback == nil)
    {
        return;
    }
    
    //Get name which could be null
    NSObject* name = [self formatName:peripheral.name];
    
    //If error is set, send back error
    if (error != nil)
    {
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorDiscoverServices, keyError, name, keyName, [peripheral.identifier UUIDString], keyAddress, error.description, keyMessage, nil];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:discoverCallback];
        discoverCallback = nil;
        return;
    }
    
    //Get array of service UUIDs
    NSMutableArray* services = [[NSMutableArray alloc] init];
    for (CBService* service in peripheral.services)
    {
        [services addObject:[service.UUID representativeString]];
    }
    
    //Return service UUIDs
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: statusDiscoveredServices, keyStatus, name, keyName, [peripheral.identifier UUIDString], keyAddress, services, keyServiceUuids, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:discoverCallback];
    
    //Set callback to null
    discoverCallback = nil;
}

- (void)peripheral:(CBPeripheral *)peripheral didDiscoverCharacteristicsForService:(CBService *)service error:(NSError *)error
{
    //Return if callback is null
    if (discoverCallback == nil)
    {
        return;
    }
    
    //Get name which could be null
    NSObject* name = [self formatName:peripheral.name];
    
    //Return error if necessary
    if (error != nil)
    {
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorDiscoverCharacteristics, keyError, name, keyName, [peripheral.identifier UUIDString], keyAddress, error.description, keyMessage, nil];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:discoverCallback];
        discoverCallback = nil;
        return;
    }
    
    //Get array of characteristics with their UUIDs and properties
    NSMutableArray* characteristics = [[NSMutableArray alloc] init];
    for (CBCharacteristic* characteristic in service.characteristics)
    {
        NSMutableDictionary* properties = [self getProperties:characteristic];
        
        NSDictionary* characteristicObject = [NSDictionary dictionaryWithObjectsAndKeys: [characteristic.UUID representativeString], keyCharacteristicUuid, properties, keyProperties, nil];
        
        [characteristics addObject:characteristicObject];
    }
    
    //Retur characteristics
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: statusDiscoveredCharacteristics, keyStatus, name, keyName, [peripheral.identifier UUIDString], keyAddress, characteristics, keyCharacteristics, [service.UUID representativeString], keyServiceUuid, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:discoverCallback];
    
    //Set callback to null
    discoverCallback = nil;
}

- (void)peripheral:(CBPeripheral *)peripheral didDiscoverDescriptorsForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error
{
    //Return if callback is null
    if (discoverCallback == nil)
    {
        return;
    }
    
    //Get name which may be nul
    NSObject* name = [self formatName:peripheral.name];
    
    //Return error if necessary
    if (error != nil)
    {
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorDiscoverDescriptors, keyError, name, keyName, [peripheral.identifier UUIDString], keyAddress, error.description, keyMessage, nil];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:discoverCallback];
        discoverCallback = nil;
        return;
    }
    
    //Get list of descriptors
    NSMutableArray* descriptors = [[NSMutableArray alloc] init];
    for (CBDescriptor* descriptor in characteristic.descriptors)
    {
        [descriptors addObject:[descriptor.UUID representativeString]];
    }
    
    //Return descriptors
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: statusDiscoveredDescriptors, keyStatus, name, keyName, [peripheral.identifier UUIDString], keyAddress, descriptors, keyDescriptorUuids, [characteristic.UUID representativeString], keyCharacteristicUuid, [characteristic.service.UUID representativeString], keyServiceUuid, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:discoverCallback];
    
    //Set callback to null
    discoverCallback = nil;
}

- (void)peripheral:(CBPeripheral *)peripheral didUpdateValueForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error
{
    //Create the initial return object
    NSMutableDictionary* returnObj = [NSMutableDictionary dictionaryWithObjectsAndKeys: [characteristic.service.UUID representativeString], keyServiceUuid, [characteristic.UUID representativeString], keyCharacteristicUuid, nil];
    
    //If an error exists...
    if (error != nil)
    {
        //Get the callback based on whether subscription or read
        NSString* callback = nil;
        if (characteristic.isNotifying)
        {
            callback = [self getCallback:characteristic.UUID forOperationType:operationSubscribe];
            [returnObj setValue:errorSubscription forKey:keyError];
        }
        else
        {
            callback = [self getCallback:characteristic.UUID forOperationType:operationRead];
            [returnObj setValue:errorRead forKey:keyError];
        }
        
        //Return if callback is null
        if (callback == nil)
        {
            return;
        }
        
        //Return the error message
        [returnObj setValue:error.description forKey:keyMessage];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];
        
        //Clear out the correct callback
        if (characteristic.isNotifying)
        {
            [self removeCallback:characteristic.UUID forOperationType:operationSubscribe];
        }
        else
        {
            [self removeCallback:characteristic.UUID forOperationType:operationRead];
        }
        
        return;
    }
    
    //Add the read value to return object
    [self addValue:characteristic.value toDictionary:returnObj];
    
    //Get the correct callback and return value
    if (characteristic.isNotifying)
    {
        NSString* callback = [self getCallback:characteristic.UUID forOperationType:operationSubscribe];
        
        if (callback == nil)
        {
            return;
        }
        
        [returnObj setValue:statusSubscribedResult forKey:keyStatus];
        
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:true];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];
    }
    else
    {
        NSString* callback = [self getCallback:characteristic.UUID forOperationType:operationRead];
       
        if (callback == nil)
        {
            return;
        }
        
        [returnObj setValue:statusRead forKey:keyStatus];
        
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];
        
        [self removeCallback:characteristic.UUID forOperationType:operationRead];
    }
}

- (void)peripheral:(CBPeripheral *)peripheral didUpdateValueForDescriptor:(CBDescriptor *)descriptor error:(NSError *)error
{
    //Return if callback is null
    if (descriptorCallback == nil)
    {
        return;
    }
    
    //Get the descriptors parent characteristic
    CBCharacteristic* characteristic = descriptor.characteristic;
    
    //Create initial return object
    NSMutableDictionary* returnObj = [NSMutableDictionary dictionaryWithObjectsAndKeys: [characteristic.service.UUID representativeString], keyServiceUuid, [characteristic.UUID representativeString], keyCharacteristicUuid, [descriptor.UUID representativeString], keyDescriptorUuid, nil];
    
    //If error isn't null, return an error
    if (error != nil)
    {
        [returnObj setValue:errorReadDescriptor forKey:keyError];
        [returnObj setValue:error.description forKey:keyMessage];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:descriptorCallback];
        descriptorCallback = nil;
        return;
    }

    //Get the descriptor value and add to return object
    NSUInteger value = [descriptor.value integerValue];
    NSData *data = [NSData dataWithBytes:&value length:sizeof(value)];
    [self addValue:data toDictionary:returnObj];
    
    //Add the correct status
    [returnObj setValue:statusReadDescriptor forKey:keyStatus];
    
    //Return the callback
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:descriptorCallback];
    
    //Set descriptor callback to null
    descriptorCallback = nil;
}

- (void)peripheral:(CBPeripheral *)peripheral didWriteValueForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error
{
    //Get the proper callback for write operation
    NSString* callback = [self getCallback:characteristic.UUID forOperationType:operationWrite];
    
    //Return if callback is null
    if (callback == nil)
    {
        return;
    }
    
    //Create initial return object
    NSMutableDictionary* returnObj = [NSMutableDictionary dictionaryWithObjectsAndKeys: [characteristic.service.UUID representativeString], keyServiceUuid, [characteristic.UUID representativeString], keyCharacteristicUuid, nil];
    
    //If error exists, return error
    if (error != nil)
    {
        [returnObj setValue:errorWrite forKey:keyError];
        [returnObj setValue:error.description forKey:keyMessage];
        
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];
        [self removeCallback:characteristic.UUID forOperationType:operationWrite];
        return;
    }
    
    //Add characteristic value to object
    [self addValue:characteristic.value toDictionary:returnObj];
    
    //Update status
    [returnObj setValue:statusWritten forKey:keyStatus];
    
    //Return data
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];
    
    //Remove callback
    [self removeCallback:characteristic.UUID forOperationType:operationWrite];
}

- (void)peripheral:(CBPeripheral *)peripheral didWriteValueForDescriptor:(CBDescriptor *)descriptor error:(NSError *)error
{
    //Return if callback is null
    if (descriptorCallback == nil)
    {
        return;
    }
    
    //Get descriptor's parent characteristic
    CBCharacteristic* characteristic = descriptor.characteristic;
    
    //Create initial return object
    NSMutableDictionary* returnObj = [NSMutableDictionary dictionaryWithObjectsAndKeys: [characteristic.service.UUID representativeString], keyServiceUuid, [characteristic.UUID representativeString], keyCharacteristicUuid, [descriptor.UUID representativeString], keyDescriptorUuid, nil];
    
    //If error exists, return an error
    if (error != nil)
    {
        [returnObj setValue:errorWriteDescriptor forKey:keyError];
        [returnObj setValue:error.description forKey:keyMessage];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:descriptorCallback];
        descriptorCallback = nil;
        return;
    }
    
    //Add descriptor value to return obj
    [self addValue:descriptor.value toDictionary:returnObj];
    
    //Add status
    [returnObj setValue:statusWrittenDescriptor forKey:keyStatus];
    
    //Send the result
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:descriptorCallback];
    
    //Clear the descriptor callback
    descriptorCallback = nil;
}

- (void)peripheral:(CBPeripheral *)peripheral didUpdateNotificationStateForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error
{
    //Create the initial return object
    NSMutableDictionary* returnObj = [NSMutableDictionary dictionaryWithObjectsAndKeys: [characteristic.service.UUID representativeString], keyServiceUuid, [characteristic.UUID representativeString], keyCharacteristicUuid, nil];
    
    if (error != nil)
    {
        //Usually I would use characteristic.isNotifying to determine which callback to use
        //But that probably isn't accurate if there's an error, so just use subscribe
        NSString* callback = [self getCallback:characteristic.UUID forOperationType:operationSubscribe];
        if (callback == nil)
        {
            return;
        }
        
        //Add error information
        [returnObj setValue:errorSubscription forKey:keyError];
        [returnObj setValue:error.description forKey:keyMessage];
        
        //Send error
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];
        
        //Remove callback
        [self removeCallback:characteristic.UUID forOperationType:operationSubscribe];

        return;
    }
    
    //If notifying, send result via subscribe operation
    if (characteristic.isNotifying)
    {
        NSString* callback = [self getCallback:characteristic.UUID forOperationType:operationSubscribe];
        
        if (callback == nil)
        {
            return;
        }
        
        [returnObj setValue:statusSubscribed forKey:keyStatus];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:true];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];
    }
    //Or send result via read operation
    else
    {
        NSString* callback = [self getCallback:characteristic.UUID forOperationType:operationUnsubscribe];
        
        if (callback == nil)
        {
            return;
        }
        
        [returnObj setValue:statusUnsubscribed forKey:keyStatus];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];
        
        [self removeCallback:characteristic.UUID forOperationType:operationUnsubscribe];
    }
}

- (void)peripheralDidUpdateRSSI:(CBPeripheral *)peripheral error:(NSError *)error
{
    //Return if callback is null
    if (rssiCallback == nil)
    {
        return;
    }
    
    //If error exists, return error
    if (error != nil)
    {
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorRssi, keyError, error.description, keyMessage, nil];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:rssiCallback];
        rssiCallback = nil;
        return;
    }
    
    //Return RSSI value
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: peripheral.RSSI, keyRssi, statusRssi, keyStatus, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:rssiCallback];
    
    //Clear callbac
    rssiCallback = nil;
}

//Helpers for Callbacks
- (NSMutableDictionary*) ensureCallback: (CBUUID *) characteristicUuid
{
    //See if callback map exists for characteristic
    NSMutableDictionary* characteristicCallbacks = [operationCallbacks objectForKey:characteristicUuid];
    
    //If it does, return it
    if (characteristicCallbacks != nil)
    {
        return characteristicCallbacks;
    }
    
    //Or create a new map for characteristic
    NSMutableDictionary* newCharacteristicCallbacks = [NSMutableDictionary dictionary];
    [operationCallbacks setObject:newCharacteristicCallbacks forKey:characteristicUuid];
    return newCharacteristicCallbacks;
}

- (void) addCallback: (CBUUID *) characteristicUuid forOperationType:(NSString*) operationType forCallback:(NSString*) callback
{
    //Get the characteristic's callback map
    NSMutableDictionary* characteristicCallbacks = [self ensureCallback:characteristicUuid];
    //And add a callback for a particular operation
    [characteristicCallbacks setObject:callback forKey:operationType];
}

- (NSString*) getCallback: (CBUUID *) characteristicUuid forOperationType:(NSString*) operationType
{
    //Get the characteristic's callback
    NSMutableDictionary* characteristicCallbacks = [operationCallbacks objectForKey:characteristicUuid];

  	if (characteristicCallbacks == nil)
  	{
  		return nil;
  	}
  	
  	//Return the callback for a particular operation, which may be null
    return [characteristicCallbacks objectForKey:operationType];
}

- (void) removeCallback: (CBUUID *) characteristicUuid forOperationType:(NSString*) operationType
{
    //Get the characteristic's callback
    NSMutableDictionary* characteristicCallbacks = [operationCallbacks objectForKey:characteristicUuid];
    
  	if (characteristicCallbacks == nil)
  	{
  		return;
  	}
    
    //Remove the callback for the particular operation
    [characteristicCallbacks removeObjectForKey:operationType];
}

- (void) clearOperationCallbacks
{
    //Clear all the device specific callbacks
    operationCallbacks = [NSMutableDictionary dictionary];
    discoverCallback = nil;
    descriptorCallback = nil;
    rssiCallback = nil;
}

//Helpers to check conditions and send callbacks
- (BOOL) isNotInitialized:(CDVInvokedUrlCommand *)command
{
    if (centralManager == nil)
    {
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorInitialize, keyError, logNotInit, keyMessage, nil];
        
        CDVPluginResult *pluginResult = nil;
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        
        return true;
    }
    
    return [self isNotEnabled:command];
}

- (BOOL) isNotEnabled:(CDVInvokedUrlCommand *)command
{
    if (centralManager.state != CBCentralManagerStatePoweredOn)
    {
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorEnable, keyError, logNotEnabled, keyMessage, nil];
        
        CDVPluginResult *pluginResult = nil;
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        
        return true;
    }
    
    return false;
}

- (BOOL) isNotArgsObject:(NSDictionary*) obj :(CDVInvokedUrlCommand *)command
{
    if (obj != nil)
    {
        return false;
    }
    
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorArguments, keyError, logNoArgObj, keyMessage, nil];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    
    return true;
}

- (BOOL) isNotService:(CBService*) service :(CDVInvokedUrlCommand *)command
{
    if (service != nil)
    {
      return false;
    }
    
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorService, keyError, logNoService, keyMessage, nil];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    
    return true;
}

- (BOOL) isNotCharacteristic:(CBCharacteristic*) characteristic :(CDVInvokedUrlCommand *)command
{
    if (characteristic != nil)
    {
      return false;
    }

    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorCharacteristic, keyError, logNoCharacteristic, keyMessage, nil];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    
    return true;
}

- (BOOL) isNotDescriptor:(CBDescriptor*) descriptor :(CDVInvokedUrlCommand *)command
{
    if (descriptor != nil)
    {
      return false;
    }

    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorDescriptor, keyError, logNoDescriptor, keyMessage, nil];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    
    return true;
}

- (BOOL) wasNeverConnected:(CDVInvokedUrlCommand *)command
{
    if (activePeripheral != nil)
    {
        return false;
    }
    
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorNeverConnected, keyError, logNeverConnected, keyMessage, nil];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    
    return true;
}

- (BOOL) isNotDisconnected:(CDVInvokedUrlCommand *)command
{
    if (activePeripheral.state == CBPeripheralStateDisconnected)
    {
        return false;
    }
    
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorIsNotDisconnected, keyError, logIsNotDisconnected, keyMessage, nil];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    
    return true;
}

- (BOOL) isDisconnected:(CDVInvokedUrlCommand *)command
{
    if (activePeripheral.state != CBPeripheralStateDisconnected)
    {
        return false;
    }
    
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorIsDisconnected, keyError, logIsDisconnected, keyMessage, nil];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    
    return true;
}

- (BOOL) isNotConnected:(CDVInvokedUrlCommand *)command
{
    if (activePeripheral.state == CBPeripheralStateConnected)
    {
        return false;
    }
    
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorIsNotConnected, keyError, logIsNotConnected, keyMessage, nil];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    
    return true;
}

//General Helpers
-(NSDictionary*) getArgsObject:(NSArray *)args
{
    if (args == nil)
    {
        return nil;
    }
    
    if (args.count == 1)
    {
        return (NSDictionary *)[args objectAtIndex:0];
    }
    return nil;
}

-(NSData*) getValue:(NSDictionary *) obj
{
    NSString* string = [obj valueForKey:keyValue];
    
    if (string == nil)
    {
      return nil;
    }
    
    if (![string isKindOfClass:[NSString class]])
    {
        return nil;
    }
    
    NSData *data = [[NSData alloc] initWithBase64EncodedString:string options:0];
    
    if (data == nil || data.length == 0)
    {
      return nil;
    }
    
    return data;
}

-(void) addValue:(NSData *) bytes toDictionary:(NSMutableDictionary *) obj
{
    NSString *string = [bytes base64EncodedStringWithOptions:0];
    
    if (string == nil || string.length == 0)
    {
      return;
    }
    
    [obj setValue:string forKey:keyValue];
}

-(NSMutableArray*) getUuids:(NSDictionary *) dictionary forType:(NSString*) type
{
    NSMutableArray* uuids = [[NSMutableArray alloc] init];
    
    NSArray* checkUuids = [dictionary valueForKey:type];
    
    if (checkUuids == nil)
    {
        return nil;
    }
    
    if (![checkUuids isKindOfClass:[NSArray class]])
    {
        return nil;
    }
    
    for (NSString* checkUuid in checkUuids)
    {
        CBUUID* uuid = [CBUUID UUIDWithString:checkUuid];
        
        if (uuid != nil)
        {
            [uuids addObject:uuid];
        }
    }
    
    if (uuids.count == 0)
    {
        return nil;
    }
    
    return uuids;
}

-(NSUUID*) getAddress:(NSDictionary *)obj
{
    NSString* addressString = [obj valueForKey:keyAddress];
    
    if (addressString == nil)
    {
        return nil;
    }
    
    if (![addressString isKindOfClass:[NSString class]])
    {
        return nil;
    }
    
    return [[NSUUID UUID] initWithUUIDString:addressString];
}

-(NSNumber*) getRequest:(NSDictionary *)obj
{
    NSNumber* request = [obj valueForKey:keyRequest];
    
    if (request == nil)
    {
        return [NSNumber numberWithBool:NO];
    }
    
    if (![request isKindOfClass:[NSNumber class]])
    {
        return [NSNumber numberWithBool:NO];
    }
    
    return request;
}

-(int) getWriteType:(NSDictionary *)obj
{
    NSString* writeType = [obj valueForKey:keyType];

    if (writeType == nil || [writeType compare:writeTypeNoResponse])
    {
        return CBCharacteristicWriteWithResponse;
    }
    return CBCharacteristicWriteWithoutResponse;
}

-(NSObject*) formatName:(NSString*)name
{
    if (name != nil)
    {
        return name;
    }
    
    return [NSNull null];
}

-(CBService*) getService:(NSDictionary *)obj
{
    if (activePeripheral.services == nil)
    {
      return nil;
    }
    
    NSString* uuidString = [obj valueForKey:keyServiceUuid];
    
    if (uuidString == nil)
    {
      return nil;
    }
    
    if (![uuidString isKindOfClass:[NSString class]])
    {
        return nil;
    }
    
    CBUUID* uuid = [CBUUID UUIDWithString:uuidString];
    
    if (uuid == nil)
    {
      return nil;
    }
    
    CBService* service = nil;
    
    for (CBService* item in activePeripheral.services)
    {
      if ([item.UUID isEqual: uuid])
      {
        service = item;
      }
    }
    
    return service;
}

-(CBCharacteristic*) getCharacteristic:(NSDictionary *) obj forService:(CBService*) service
{
    if (service.characteristics == nil)
    {
      return nil;
    }
    
    NSString* uuidString = [obj valueForKey:keyCharacteristicUuid];
    
    if (uuidString == nil)
    {
      return nil;
    }
    
    if (![uuidString isKindOfClass:[NSString class]])
    {
        return nil;
    }
    
    CBUUID* uuid = [CBUUID UUIDWithString:uuidString];
    
    if (uuid == nil)
    {
      return nil;
    }
    
    CBCharacteristic* characteristic = nil;
    
    for (CBCharacteristic* item in service.characteristics)
    {
      if ([item.UUID isEqual: uuid])
      {
        characteristic = item;
      }
    }
    
    return characteristic;
}

-(CBDescriptor*) getDescriptor:(NSDictionary *) obj forCharacteristic:(CBCharacteristic*) characteristic
{
    if (characteristic.descriptors == nil)
    {
      return nil;
    }
    
    NSString* uuidString = [obj valueForKey:keyDescriptorUuid];
    
    if (uuidString == nil)
    {
      return nil;
    }
    
    if (![uuidString isKindOfClass:[NSString class]])
    {
        return nil;
    }
    
    CBUUID* uuid = [CBUUID UUIDWithString:uuidString];
    
    if (uuid == nil)
    {
      return nil;
    }
    
    CBDescriptor* descriptor = nil;
    
    for (CBDescriptor* item in characteristic.descriptors)
    {
      if ([item.UUID isEqual: uuid])
      {
        descriptor = item;
      }
    }
    
    return descriptor;
}

-(NSMutableDictionary*) getProperties:(CBCharacteristic*) characteristic
{
    NSMutableDictionary* propertiesObject = [NSMutableDictionary dictionary];
    
    CBCharacteristicProperties properties = characteristic.properties;
    
    if ((properties & CBCharacteristicPropertyBroadcast) == CBCharacteristicPropertyBroadcast)
    {
        [propertiesObject setValue:@YES forKey:propertyBroadcast];
    }
    
    if ((properties & CBCharacteristicPropertyRead) == CBCharacteristicPropertyRead)
    {
        [propertiesObject setValue:@YES forKey:propertyRead];
    }
    
    if ((properties & CBCharacteristicPropertyWriteWithoutResponse) == CBCharacteristicPropertyWriteWithoutResponse)
    {
        [propertiesObject setValue:@YES forKey:propertyWriteWithoutResponse];
    }
    
    if ((properties & CBCharacteristicPropertyWrite) == CBCharacteristicPropertyWrite)
    {
        [propertiesObject setValue:@YES forKey:propertyWrite];
    }
    
    if ((properties & CBCharacteristicPropertyNotify) == CBCharacteristicPropertyNotify)
    {
        [propertiesObject setValue:@YES forKey:propertyNotify];
    }
    
    if ((properties & CBCharacteristicPropertyIndicate) == CBCharacteristicPropertyIndicate)
    {
        [propertiesObject setValue:@YES forKey:propertyIndicate];
    }
    
    if ((properties & CBCharacteristicPropertyAuthenticatedSignedWrites) == CBCharacteristicPropertyAuthenticatedSignedWrites)
    {
        [propertiesObject setValue:@YES forKey:propertyAuthenticatedSignedWrites];
    }
    
    if ((properties & CBCharacteristicPropertyExtendedProperties) == CBCharacteristicPropertyExtendedProperties)
    {
        [propertiesObject setValue:@YES forKey:propertyExtendedProperties];
    }
    
    if ((properties & CBCharacteristicPropertyNotifyEncryptionRequired) == CBCharacteristicPropertyNotifyEncryptionRequired)
    {
        [propertiesObject setValue:@YES forKey:propertyNotifyEncryptionRequired];
    }
    
    if ((properties & CBCharacteristicPropertyIndicateEncryptionRequired) == CBCharacteristicPropertyIndicateEncryptionRequired)
    {
        [propertiesObject setValue:@YES forKey:propertyIndicateEncryptionRequired];
    }
    
    return propertiesObject;
}

@end