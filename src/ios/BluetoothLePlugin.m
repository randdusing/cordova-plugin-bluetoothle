#import "BluetoothLePlugin.h"

//TODO Allow for custom IDs, determine how UUIDs are returned

//Object Keys
NSString *const keyStatus = @"status";
NSString *const keyError = @"error";
NSString *const keyRequest = @"request";
NSString *const keyMessage = @"message";
NSString *const keyName = @"name";
NSString *const keyAddress = @"address";
NSString *const keyRssi = @"rssi";
NSString *const keyAdvertisement = @"advertisement";
NSString *const keyServiceUuids = @"serviceUuids";
NSString *const keyCharacteristicUuids = @"characteristicUuids";
NSString *const keyDescriptorUuids = @"descriptorUuids";
NSString *const keyServiceUuid = @"serviceUuid";
NSString *const keyCharacteristicUuid = @"characteristicUuid";
NSString *const keyDescriptorUuid = @"descriptorUuid";
NSString *const keyValue = @"value";
NSString *const keyIsInitialized = @"isInitalized";
NSString *const keyIsScanning = @"isScanning";
NSString *const keyIsConnected = @"isConnected";
NSString *const keyIsDiscovered = @"isDiscovered";

//Status Types
NSString *const statusInitialized = @"initialized";
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

- (void)pluginInitialize
{
    [super pluginInitialize];
    
    activePeripheral = nil;
}

//Actions
- (void)initialize:(CDVInvokedUrlCommand *)command
{
    if (centralManager != nil && centralManager.state == CBCentralManagerStatePoweredOn)
    {
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: statusInitialized, keyStatus, nil];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        return;
    }
    
    NSNumber* request = [NSNumber numberWithBool:NO];
    
    NSDictionary* obj = [self getArgsObject:command.arguments];
    
    if (obj != nil)
    {
        request = [self getRequest:obj];
    }
    
    centralManager = [[CBCentralManager alloc] initWithDelegate:self queue:nil options:@{ CBCentralManagerOptionRestoreIdentifierKey:@"bluetoothleplugin", CBCentralManagerOptionShowPowerAlertKey:request }];
    initCallback = command.callbackId;
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
    
    scanCallback = nil;

    [centralManager stopScan];
    
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
    
    if (activePeripheral != nil)
    {
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorConnect, keyError, logPreviouslyConnected, keyMessage, nil];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        return;
    }
    
    NSDictionary* obj = [self getArgsObject:command.arguments];
    
    if ([self isNotArgsObject:obj :command])
    {
        return;
    }
    
    NSUUID* address = [self getAddress:obj];
    
    if (address == nil)
    {
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorConnect, keyError, logNoAddress, keyMessage, nil];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        return;
    }

    NSArray* peripherals = [centralManager retrievePeripheralsWithIdentifiers:@[address]];
    
    if (peripherals.count == 0)
    {
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorConnect, keyError, logNoDevice, keyMessage, nil];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        return;
    }
    
    activePeripheral = peripherals[0];
    [activePeripheral setDelegate:self];
    
    NSObject* name = [self formatName:activePeripheral.name];
    
    connectCallback = command.callbackId;
    
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: statusConnecting, keyStatus, name, keyName, [activePeripheral.identifier UUIDString], keyAddress, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:true];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:connectCallback];

    [centralManager connectPeripheral:activePeripheral options:nil];
}

- (void)reconnect:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }
    
    if ([self wasNeverConnected:command])
    {
        return;
    }
    
    if ([self isNotDisconnected:command])
    {
        return;
    }
    
    connectCallback = command.callbackId;
    
    NSObject* name = [self formatName:activePeripheral.name];
    
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: statusConnecting, keyStatus, name, keyName, [activePeripheral.identifier UUIDString], keyAddress, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:true];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:connectCallback];
    
    [centralManager connectPeripheral:activePeripheral options:nil];
}

- (void)disconnect:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }
    
    if ([self wasNeverConnected:command])
    {
        return;
    }
    
    if ([self isDisconnected:command])
    {
        return;
    }
    
    NSObject* name = [self formatName:activePeripheral.name];
    
    if (activePeripheral.state == CBPeripheralStateConnecting)
    {
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: statusDisconnecting, keyStatus, name, keyName, [activePeripheral.identifier UUIDString], keyAddress, nil];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        
        connectCallback = nil;
    }
    else
    {
        connectCallback = command.callbackId;
        
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: statusDisconnecting, keyStatus, name, keyName, [activePeripheral.identifier UUIDString], keyAddress, nil];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:true];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:connectCallback];
    }
    
    [centralManager cancelPeripheralConnection:activePeripheral];
}

- (void)close:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }
    
    if ([self wasNeverConnected:command])
    {
        return;
    }
    
    if ([self isNotDisconnected:command])
    {
        return;
    }
    
    NSObject* name = [self formatName:activePeripheral.name];
    
    //Create dictionary with status message and optionally add device information
    NSMutableDictionary* returnObj = [NSMutableDictionary dictionaryWithObjectsAndKeys: statusClosed, keyStatus, name, keyName, [activePeripheral.identifier UUIDString], keyAddress, nil];

    activePeripheral = nil;
    connectCallback = nil;
    
    [self clearOperationCallbacks];

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)discover:(CDVInvokedUrlCommand *)command
{
    //TODO Whether this returns null
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
    
    if ([self wasNeverConnected:command])
    {
        return;
    }
    
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
    
    NSMutableArray* serviceUuids = [self getUuids:obj forType:keyServiceUuids];
    
    discoverCallback = command.callbackId;
    
    [activePeripheral discoverServices:serviceUuids];
}

- (void)characteristics:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }
    
    if ([self wasNeverConnected:command])
    {
        return;
    }
    
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
    
    CBService* service = [self getService:obj];
    
    if ([self isNotService:service :command])
    {
        return;
    }
    
    NSMutableArray* characteristicUuids = [self getUuids:obj forType:keyCharacteristicUuids];
    
    discoverCallback = command.callbackId;
    
    [activePeripheral discoverCharacteristics:characteristicUuids forService:service];
}

- (void)descriptors:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }
    
    if ([self wasNeverConnected:command])
    {
        return;
    }
    
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
    
    CBService* service = [self getService:obj];
    
    if ([self isNotService:service :command])
    {
        return;
    }
    
    CBCharacteristic* characteristic = [self getCharacteristic:obj forService:service];
    
    if ([self isNotCharacteristic:characteristic :command])
    {
        return;
    }
    
    discoverCallback = command.callbackId;
    
    [activePeripheral discoverDescriptorsForCharacteristic:characteristic];
}

- (void)read:(CDVInvokedUrlCommand *)command
{
    //Normal read
    if ([self isNotInitialized:command])
    {
        return;
    }
    
    if ([self wasNeverConnected:command])
    {
        return;
    }
    
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
    
    CBService* service = [self getService:obj];
    
    if ([self isNotService:service :command])
    {
        return;
    }
    
    CBCharacteristic* characteristic = [self getCharacteristic:obj forService:service];
    
    if ([self isNotCharacteristic:characteristic :command])
    {
        return;
    }
    
    [self addCallback:characteristic.UUID forOperationType:operationRead forCallback:command.callbackId];
    
    [activePeripheral readValueForCharacteristic:characteristic]; 
}

- (void)subscribe:(CDVInvokedUrlCommand *)command
{
    if ([self isNotInitialized:command])
    {
        return;
    }
    
    if ([self wasNeverConnected:command])
    {
        return;
    }
    
    if ([self isNotConnected:command])
    {
        return;
    }
    
    NSDictionary *obj = [self getArgsObject:command.arguments];
    
    if ([self isNotArgsObject:obj :command])
    {
        return;
    }
    
    CBService* service = [self getService:obj];
    
    if ([self isNotService:service :command])
    {
        return;
    }
    
    CBCharacteristic* characteristic = [self getCharacteristic:obj forService:service];
    
    if ([self isNotCharacteristic:characteristic :command])
    {
        return;
    }
    
    [self addCallback:characteristic.UUID forOperationType:operationSubscribe forCallback:command.callbackId];
    
    [activePeripheral setNotifyValue:true forCharacteristic:characteristic];
}

- (void)unsubscribe:(CDVInvokedUrlCommand *)command
{
    if ([self isNotInitialized:command])
    {
        return;
    }
    
    if ([self wasNeverConnected:command])
    {
        return;
    }
    
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
    
    CBService* service = [self getService:obj];
    
    if ([self isNotService:service :command])
    {
        return;
    }
    
    CBCharacteristic* characteristic = [self getCharacteristic:obj forService:service];
    
    if ([self isNotCharacteristic:characteristic :command])
    {
        return;
    }
    
    [self addCallback:characteristic.UUID forOperationType:operationUnsubscribe forCallback:command.callbackId];
    
    [activePeripheral setNotifyValue:false forCharacteristic:characteristic]; 
}

- (void)write:(CDVInvokedUrlCommand *)command
{
    if ([self isNotInitialized:command])
    {
        return;
    }
    
    if ([self wasNeverConnected:command])
    {
        return;
    }
    
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
    
    CBService* service = [self getService:obj];
    
    if ([self isNotService:service :command])
    {
        return;
    }
    
    CBCharacteristic* characteristic = [self getCharacteristic:obj forService:service];
    
    if ([self isNotCharacteristic:characteristic :command])
    {
        return;
    }
    
    NSData* value = [self getValue:obj];
    
    if (value == nil)
    {
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorWrite, keyError, logWriteValueNotFound, keyMessage, nil];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        return;
    }
    
    [self addCallback:characteristic.UUID forOperationType:operationWrite forCallback:command.callbackId];
    
    [activePeripheral writeValue:value forCharacteristic:characteristic type:CBCharacteristicWriteWithResponse];
}

- (void)readDescriptor:(CDVInvokedUrlCommand *)command
{
    if ([self isNotInitialized:command])
    {
        return;
    }
    
    if ([self wasNeverConnected:command])
    {
        return;
    }
    
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
    
    CBService* service = [self getService:obj];
    
    if ([self isNotService:service :command])
    {
        return;
    }
    
    CBCharacteristic* characteristic = [self getCharacteristic:obj forService:service];
    
    if ([self isNotCharacteristic:characteristic :command])
    {
        return;
    }
    
    CBDescriptor* descriptor = [self getDescriptor:obj forCharacteristic:characteristic];
    
    if ([self isNotDescriptor:descriptor :command])
    {
        return;
    }
    
    descriptorCallback = command.callbackId;
    
    [activePeripheral readValueForDescriptor:descriptor];
}

- (void)writeDescriptor:(CDVInvokedUrlCommand *)command
{
    if ([self isNotInitialized:command])
    {
        return;
    }
    
    if ([self wasNeverConnected:command])
    {
        return;
    }
    
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
    
    CBService* service = [self getService:obj];
    
    if ([self isNotService:service :command])
    {
        return;
    }
    
    CBCharacteristic* characteristic = [self getCharacteristic:obj forService:service];
    
    if ([self isNotCharacteristic:characteristic :command])
    {
        return;
    }
    
    CBDescriptor* descriptor = [self getDescriptor:obj forCharacteristic:characteristic];
    
    if ([self isNotDescriptor:descriptor :command])
    {
        return;
    }
    
    NSData* value = [self getValue:obj];
    
    if (value == nil)
    {
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorWriteDescriptor, keyError, logWriteDescriptorValueNotFound, keyMessage, nil];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        return;
    }
    
    descriptorCallback = command.callbackId;
    
    [activePeripheral writeValue:value forDescriptor:descriptor];
}

- (void)rssi:(CDVInvokedUrlCommand *)command
{
    if ([self isNotInitialized:command])
    {
        return;
    }
    
    if ([self wasNeverConnected:command])
    {
        return;
    }
    
    if ([self isNotConnected:command])
    {
        return;
    }
    
    rssiCallback = command.callbackId;
    
    [activePeripheral readRSSI];
}

- (void)isInitialized:(CDVInvokedUrlCommand *)command
{
    BOOL result = (centralManager != nil && centralManager.state == CBCentralManagerStatePoweredOn);
    
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:result];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)isScanning:(CDVInvokedUrlCommand *)command
{
    BOOL result = (scanCallback != nil);
    
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:result];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)isConnected:(CDVInvokedUrlCommand *)command
{   
    BOOL result = (activePeripheral != nil && activePeripheral.state == CBPeripheralStateConnected);
    
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:result];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)isDiscovered:(CDVInvokedUrlCommand *)command
{
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:false];
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

    if (error != nil)
    {
        returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorInitialize, keyError, error, keyMessage, nil];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
    }
    else
    {
        returnObj = [NSDictionary dictionaryWithObjectsAndKeys: statusInitialized, keyStatus, nil];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    }
    
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:initCallback];
    
    initCallback = nil;
}

- (void)centralManager:(CBCentralManager *)central willRestoreState:(NSDictionary *)dict
{
    
}

- (void)centralManager:(CBCentralManager *)central didDiscoverPeripheral:(CBPeripheral *)peripheral advertisementData:(NSDictionary *)advertisementData RSSI:(NSNumber *)RSSI
{
    if (scanCallback == nil)
    {
        return;
    }
    
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
    operationCallbacks = [NSMutableDictionary dictionary];
    
    //Successfully connected, call back to end user
    if (connectCallback == nil)
    {
        return;
    }
    
    NSObject* name = [self formatName:peripheral.name];
    
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: statusConnected, keyStatus, name, keyName, [peripheral.identifier UUIDString], keyAddress, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    //Keep in case device gets disconnected without user initiation
    [pluginResult setKeepCallbackAsBool:true];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:connectCallback];
}

- (void)centralManager:(CBCentralManager *)central didFailToConnectPeripheral:(CBPeripheral *)peripheral error:(NSError *)error
{
    if (connectCallback == nil)
    {
        return;
    }
    
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorConnect, keyError, error.description, keyMessage, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:connectCallback];
    
    connectCallback = nil;
}

- (void)centralManager:(CBCentralManager *)central didDisconnectPeripheral:(CBPeripheral *)peripheral error:(NSError *)error
{
    [self clearOperationCallbacks];
    
    if (connectCallback == nil)
    {
        return;
    }
    
    NSObject* name = [self formatName:peripheral.name];
    
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: statusDisconnected, keyStatus, name, keyName, [peripheral.identifier UUIDString], keyAddress, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:connectCallback];
    
    connectCallback = nil;
}

//Peripheral Delegates
- (void)peripheral:(CBPeripheral *)peripheral didDiscoverServices:(NSError *)error
{
    if (discoverCallback == nil)
    {
        return;
    }
    
    NSObject* name = [self formatName:peripheral.name];
    
    if (error != nil)
    {
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorDiscoverServices, keyError, name, keyName, [peripheral.identifier UUIDString], keyAddress, error.description, keyMessage, nil];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:discoverCallback];
        discoverCallback = nil;
        return;
    }
    
    NSMutableArray* services = [[NSMutableArray alloc] init];
    
    for (CBService* service in peripheral.services)
    {
        [services addObject:[service.UUID representativeString]];
    }
    
    
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: statusDiscoveredServices, keyStatus, name, keyName, [peripheral.identifier UUIDString], keyAddress, services, keyServiceUuids, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:discoverCallback];
    
    discoverCallback = nil;
}

- (void)peripheral:(CBPeripheral *)peripheral didDiscoverCharacteristicsForService:(CBService *)service error:(NSError *)error
{
    if (discoverCallback == nil)
    {
        return;
    }
    
    NSObject* name = [self formatName:peripheral.name];
    
    if (error != nil)
    {
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorDiscoverCharacteristics, keyError, name, keyName, [peripheral.identifier UUIDString], keyAddress, error.description, keyMessage, nil];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:discoverCallback];
        discoverCallback = nil;
        return;
    }
    
    NSMutableArray* characteristics = [[NSMutableArray alloc] init];
    
    for (CBCharacteristic* characteristic in service.characteristics)
    {
        [characteristics addObject:[characteristic.UUID representativeString]];
    }
    
    
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: statusDiscoveredCharacteristics, keyStatus, name, keyName, [peripheral.identifier UUIDString], keyAddress, characteristics, keyCharacteristicUuids, [service.UUID representativeString], keyServiceUuid, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:discoverCallback];
    
    discoverCallback = nil;
}

- (void)peripheral:(CBPeripheral *)peripheral didDiscoverDescriptorsForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error
{
    if (discoverCallback == nil)
    {
        return;
    }
    
    NSObject* name = [self formatName:peripheral.name];
    
    if (error != nil)
    {
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorDiscoverDescriptors, keyError, name, keyName, [peripheral.identifier UUIDString], keyAddress, error.description, keyMessage, nil];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:discoverCallback];
        discoverCallback = nil;
        return;
    }
    
    NSMutableArray* descriptors = [[NSMutableArray alloc] init];
    
    for (CBDescriptor* descriptor in characteristic.descriptors)
    {
        [descriptors addObject:[descriptor.UUID representativeString]];
    }
    
    
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: statusDiscoveredDescriptors, keyStatus, name, keyName, [peripheral.identifier UUIDString], keyAddress, descriptors, keyDescriptorUuids, [characteristic.UUID representativeString], keyCharacteristicUuid, [characteristic.service.UUID representativeString], keyServiceUuid, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:discoverCallback];
    
    discoverCallback = nil;
}

- (void)peripheral:(CBPeripheral *)peripheral didUpdateValueForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error
{
    NSMutableDictionary* returnObj = [NSMutableDictionary dictionaryWithObjectsAndKeys: [characteristic.service.UUID representativeString], keyServiceUuid, [characteristic.UUID representativeString], keyCharacteristicUuid, nil];
    
    if (error != nil)
    {
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
        
        if (callback == nil)
        {
            return;
        }
        
        [returnObj setValue:error.description forKey:keyMessage];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];
        
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
    
    [self addValue:characteristic.value toDictionary:returnObj];
    
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
    if (descriptorCallback == nil)
    {
        return;
    }
    
    CBCharacteristic* characteristic = descriptor.characteristic;
    
    NSMutableDictionary* returnObj = [NSMutableDictionary dictionaryWithObjectsAndKeys: [characteristic.service.UUID representativeString], keyServiceUuid, [characteristic.UUID representativeString], keyCharacteristicUuid, [descriptor.UUID representativeString], keyDescriptorUuid, nil];
    
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

    NSUInteger value = [descriptor.value integerValue];
    NSData *data = [NSData dataWithBytes:&value length:sizeof(value)];
    [self addValue:data toDictionary:returnObj];
    
    [returnObj setValue:statusReadDescriptor forKey:keyStatus];
    
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:descriptorCallback];
    
    descriptorCallback = nil;
}

- (void)peripheral:(CBPeripheral *)peripheral didWriteValueForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error
{
    NSString* callback = [self getCallback:characteristic.UUID forOperationType:operationWrite];
    
    if (callback == nil)
    {
        return;
    }
    
    NSMutableDictionary* returnObj = [NSMutableDictionary dictionaryWithObjectsAndKeys: [characteristic.service.UUID representativeString], keyServiceUuid, [characteristic.UUID representativeString], keyCharacteristicUuid, nil];
    
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
    
    [self addValue:characteristic.value toDictionary:returnObj];
    
    [returnObj setValue:statusWritten forKey:keyStatus];
    
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];
    
    [self removeCallback:characteristic.UUID forOperationType:operationWrite];
}

- (void)peripheral:(CBPeripheral *)peripheral didWriteValueForDescriptor:(CBDescriptor *)descriptor error:(NSError *)error
{
    if (descriptorCallback == nil)
    {
        return;
    }
    
    CBCharacteristic* characteristic = descriptor.characteristic;
    
    NSMutableDictionary* returnObj = [NSMutableDictionary dictionaryWithObjectsAndKeys: [characteristic.service.UUID representativeString], keyServiceUuid, [characteristic.UUID representativeString], keyCharacteristicUuid, [descriptor.UUID representativeString], keyDescriptorUuid, nil];
    
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
    
    [self addValue:descriptor.value toDictionary:returnObj];
    
    [returnObj setValue:statusWrittenDescriptor forKey:keyStatus];
    
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:descriptorCallback];
    
    descriptorCallback = nil;
}

- (void)peripheral:(CBPeripheral *)peripheral didUpdateNotificationStateForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error
{
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
        
        [returnObj setValue:errorSubscription forKey:keyError];
        [returnObj setValue:error.description forKey:keyMessage];
        
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];
        
        [self removeCallback:characteristic.UUID forOperationType:operationSubscribe];

        return;
    }
    
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
    if (rssiCallback == nil)
    {
        return;
    }
    
    if (error != nil)
    {
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorRssi, keyError, error.description, keyMessage, nil];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:rssiCallback];
        rssiCallback = nil;
        return;
    }
    
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: peripheral.RSSI, keyRssi, statusRssi, keyStatus, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:rssiCallback];
    
    rssiCallback = nil;
}

//Helpers for Callbacks
- (NSMutableDictionary*) ensureCallback: (CBUUID *) characteristicUuid
{
    NSMutableDictionary* characteristicCallbacks = [operationCallbacks objectForKey:characteristicUuid];
    
    if (characteristicCallbacks != nil)
    {
        return characteristicCallbacks;
    }
    
    NSMutableDictionary* newCharacteristicCallbacks = [NSMutableDictionary dictionary];
    [operationCallbacks setObject:newCharacteristicCallbacks forKey:characteristicUuid];
    return newCharacteristicCallbacks;
}

- (void) addCallback: (CBUUID *) characteristicUuid forOperationType:(NSString*) operationType forCallback:(NSString*) callback
{
    NSMutableDictionary* characteristicCallbacks = [self ensureCallback:characteristicUuid];
    
    [characteristicCallbacks setObject:callback forKey:operationType];
}

- (NSString*) getCallback: (CBUUID *) characteristicUuid forOperationType:(NSString*) operationType
{
    NSMutableDictionary* characteristicCallbacks = [operationCallbacks objectForKey:characteristicUuid];

  	if (characteristicCallbacks == nil)
  	{
  		return nil;
  	}
  	
  	//This may return nil
    return [characteristicCallbacks objectForKey:operationType];
}

- (void) removeCallback: (CBUUID *) characteristicUuid forOperationType:(NSString*) operationType
{
    NSMutableDictionary* characteristicCallbacks = [operationCallbacks objectForKey:characteristicUuid];
    
  	if (characteristicCallbacks == nil)
  	{
  		return;
  	}
    
    [characteristicCallbacks removeObjectForKey:operationType];
}

- (void) clearOperationCallbacks
{
    operationCallbacks = [NSMutableDictionary dictionary];
    discoverCallback = nil;
    descriptorCallback = nil;
    rssiCallback = nil;
}

//Helpers to check conditions and send callbacks
- (BOOL) isNotInitialized:(CDVInvokedUrlCommand *)command
{
    if (centralManager == nil || centralManager.state != CBCentralManagerStatePoweredOn)
    {
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorInitialize, keyError, logNotInit, keyMessage, nil];
        
        CDVPluginResult *pluginResult = nil;
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        
        initCallback = nil;
        scanCallback = nil;
        connectCallback = nil;
        [self clearOperationCallbacks];
        
        activePeripheral = nil;
        
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
    //TODO Not sure how cast typing works in objective c
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

@end