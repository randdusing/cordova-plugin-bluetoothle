#import "BluetoothLePlugin.h"

//Plugin Name
NSString *const pluginName = @"bluetoothleplugin";

//Object Keys
NSString *const keyStatus = @"status";
NSString *const keyError = @"error";
NSString *const keyRequest = @"request";
NSString *const keyStatusReceiver = @"statusReceiver";
NSString *const keyMessage = @"message";
NSString *const keyName = @"name";
NSString *const keyAddress = @"address";
NSString *const keyProperties = @"properties";
NSString *const keyRssi = @"rssi";
NSString *const keyAdvertisement = @"advertisement";
NSString *const keyUuid = @"uuid";
NSString *const keyService = @"service";
NSString *const keyServices = @"services";
NSString *const keyCharacteristic = @"characteristic";
NSString *const keyCharacteristics = @"characteristics";
NSString *const keyDescriptor = @"descriptor";
NSString *const keyDescriptors = @"descriptors";
NSString *const keyValue = @"value";
NSString *const keyType = @"type";
NSString *const keyIsInitialized = @"isInitialized";
NSString *const keyIsEnabled = @"isEnabled";
NSString *const keyIsScanning = @"isScanning";
NSString *const keyIsConnected = @"isConnected";
NSString *const keyIsDiscovered = @"isDiscovered";
NSString *const keyIsDiscoveredQueue = @"isDiscoveredQueue";
NSString *const keyPeripheral = @"peripheral";
NSString *const keyAllowDuplicates = @"allowDuplicates";

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
NSString *const statusDisabled = @"disabled";
NSString *const statusScanStarted = @"scanStarted";
NSString *const statusScanStopped = @"scanStopped";
NSString *const statusScanResult = @"scanResult";
NSString *const statusConnected = @"connected";
NSString *const statusDisconnected = @"disconnected";
NSString *const statusClosed = @"closed";
NSString *const statusServices = @"services";
NSString *const statusCharacteristics = @"characteristics";
NSString *const statusDescriptors = @"descriptors";
NSString *const statusRead = @"read";
NSString *const statusSubscribed = @"subscribed";
NSString *const statusSubscribedResult = @"subscribedResult";
NSString *const statusUnsubscribed = @"unsubscribed";
NSString *const statusWritten = @"written";
NSString *const statusReadDescriptor = @"readDescriptor";
NSString *const statusWrittenDescriptor = @"writtenDescriptor";
NSString *const statusRssi = @"rssi";
NSString *const statusDiscovered = @"discovered";

//Error Types
NSString *const errorInitialize = @"initialize";
NSString *const errorEnable = @"enable";
NSString *const errorDisable = @"disable";
NSString *const errorArguments = @"arguments";
NSString *const errorStartScan = @"startScan";
NSString *const errorStopScan = @"stopScan";
NSString *const errorConnect = @"connect";
NSString *const errorReconnect = @"reconnect";
NSString *const errorDiscover = @"discover";
NSString *const errorServices = @"services";
NSString *const errorCharacteristics = @"characteristics";
NSString *const errorDescriptors = @"descriptors";
NSString *const errorRead = @"read";
NSString *const errorSubscription = @"subscription";
NSString *const errorWrite = @"write";
NSString *const errorReadDescriptor = @"readDescriptor";
NSString *const errorWriteDescriptor = @"writeDescriptor";
NSString *const errorRssi = @"rssi";
NSString *const errorMtu = @"mtu";
NSString *const errorRequestConnectionPriority = @"requestConnectionPriority";
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
NSString *const logOperationUnsupported = @"Operation unsupported";
//Scanning
NSString *const logAlreadyScanning = @"Scanning already in progress";
NSString *const logNotScanning = @"Not scanning";
//Connection
NSString *const logPreviouslyConnected = @"Device previously connected, reconnect or close for new connection";
NSString *const logNeverConnected = @"Never connected to connection";
NSString *const logIsNotConnected = @"Device isn't connected";
NSString *const logIsNotDisconnected = @"Device isn't disconnected";
NSString *const logIsDisconnected = @"Device is disconnected";
NSString *const logNoAddress = @"No connection address";
NSString *const logNoDevice = @"Device not found";
//Read/write
NSString *const logNoArgObj = @"Argument object not found";
NSString *const logNoService = @"Service not found";
NSString *const logNoCharacteristic = @"Characteristic not found";
NSString *const logNoDescriptor = @"Descriptor not found";
NSString *const logWriteValueNotFound = @"Write value not found";
NSString *const logWriteDescriptorValueNotFound = @"Write descriptor value not found";
NSString *const logSubscribeAlready = @"Already subscribed";
NSString *const logUnsubscribeAlready = @"Already unsubscribed";
//Discovery
NSString *const logAlreadyDiscovering = @"Already discovering device";

NSString *const operationConnect = @"connect";
NSString *const operationDiscover = @"discover";
NSString *const operationRssi = @"rssi";
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

    //If central manager has been initialized already, return status=>enabled or status=>disabled success
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
            returnObj = [NSDictionary dictionaryWithObjectsAndKeys: statusDisabled, keyStatus, nil];
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

    //Check if status should be returned
    statusReceiver = [self getStatusReceiver:obj];

    //Initialize central manager
    centralManager = [[CBCentralManager alloc] initWithDelegate:self queue:nil options:@{ CBCentralManagerOptionRestoreIdentifierKey:pluginName, CBCentralManagerOptionShowPowerAlertKey:request }];

    //Create dictionary to hold connections and all their callbacks
    connections = [NSMutableDictionary dictionary];
}

- (void)enable:(CDVInvokedUrlCommand *)command
{
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorEnable, keyError, logOperationUnsupported, keyMessage, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)disable:(CDVInvokedUrlCommand *)command
{
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorDisable, keyError, logOperationUnsupported, keyMessage, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
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
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        return;
    }

    //Get an array of service assigned numbers to filter by
    NSDictionary *obj = [self getArgsObject:command.arguments];
    NSMutableArray* serviceUuids = nil;
    if (obj != nil)
    {
        serviceUuids = [self getUuids:obj forType:keyServices];
    }

    NSNumber* allowDuplicates = [NSNumber numberWithBool:NO];
    if (obj != nil) {
        allowDuplicates = [self getAllowDuplicates:obj];
    }

    //Set the callback
    scanCallback = command.callbackId;

    //Send scan started status
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: statusScanStarted, keyStatus, nil];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:true];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:scanCallback];

    //Start the scan
    [centralManager scanForPeripheralsWithServices:serviceUuids options:@{ CBCentralManagerScanOptionAllowDuplicatesKey:allowDuplicates }];
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

- (void)retrieveConnected:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }

    //Get an array of service assigned numbers to filter by
    NSDictionary *obj = [self getArgsObject:command.arguments];
    NSMutableArray* serviceUuids = nil;
    if (obj != nil)
    {
        serviceUuids = [self getUuids:obj forType:keyServices];
    }

    //Retrieve Connected Peripherals doesn't like nil UUID array
    if (serviceUuids == nil)
    {
        serviceUuids = [NSMutableArray array];
    }

    //Get connected connections with specified services
    NSArray* peripherals = [centralManager retrieveConnectedPeripheralsWithServices:serviceUuids];

    //Array to store returned peripherals
    NSMutableArray* peripheralsOut = [[NSMutableArray alloc] init];

    //Create an object from each peripheral containing connection ID and name, and add to array
    for (CBPeripheral* peripheral in peripherals)
    {
        NSMutableDictionary* peripheralOut = [NSMutableDictionary dictionary];
        [self addDevice:peripheral :peripheralOut];
        [peripheralsOut addObject:peripheralOut];
    }

    //Return the array
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:peripheralsOut];
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

    //Get the arguments
    NSDictionary* obj = [self getArgsObject:command.arguments];
    if ([self isNotArgsObject:obj :command])
    {
        return;
    }

    //Get the connection address
    NSUUID* address = [self getAddress:obj];
    if ([self isNotAddress:address :command])
    {
        return;
    }

    //Ensure connection was connected
    if ([self wasConnected:address :command])
    {
        return;
    }

    //Get the peripherals and ensure at least one exists
    NSArray* peripherals = [centralManager retrievePeripheralsWithIdentifiers:@[address]];
    if (peripherals.count == 0)
    {
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorConnect, keyError, logNoDevice, keyMessage, [address UUIDString], keyAddress, nil];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        return;
    }

    //Get the peripheral to connect to
    CBPeripheral* peripheral = peripherals[0];

    //Create connection and set peripheral/connect callback
    NSMutableDictionary* connection = [NSMutableDictionary dictionary];

    //Set periperhal and connect callback and discovered state
    [connection setObject: peripheral forKey:keyPeripheral];
    [connection setObject: command.callbackId forKey:operationConnect];
    [connection setObject: [NSNumber numberWithInt:0] forKey:keyIsDiscovered];

    //Add connection to connections
    [connections setObject:connection forKey:address];

    //Set delegate
    [peripheral setDelegate:self];

    //Attempt the actual connection
    [centralManager connectPeripheral:peripheral options:nil];
}

- (void)reconnect:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }

    //Get the arguments
    NSDictionary* obj = [self getArgsObject:command.arguments];
    if ([self isNotArgsObject:obj :command])
    {
        return;
    }

    //Get the connection address
    NSUUID* address = [self getAddress:obj];
    if ([self isNotAddress:address :command])
    {
        return;
    }

    //If never connected or attempted connected, reconnect can't be used
    NSMutableDictionary* connection = [self wasNeverConnected:address :command];
    if (connection == nil)
    {
        return;
    }

    //Get the peripheral
    CBPeripheral* peripheral = [connection objectForKey:keyPeripheral];

    //If currently connected, reconnect can't be used
    if ([self isNotDisconnected:peripheral :command])
    {
        return;
    }

    //Set the connect callback and discovered state
    [connection setObject:command.callbackId forKey:operationConnect];
    [connection setObject: [NSNumber numberWithInt:0] forKey:keyIsDiscovered];

    //Try to reconnect
    [centralManager connectPeripheral:peripheral options:nil];
}

- (void)disconnect:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }

    //Get the arguments
    NSDictionary* obj = [self getArgsObject:command.arguments];
    if ([self isNotArgsObject:obj :command])
    {
        return;
    }

    //Get the connection address
    NSUUID* address = [self getAddress:obj];
    if ([self isNotAddress:address :command])
    {
        return;
    }

    //If never connected or attempted connected, reconnect can't be used
    NSMutableDictionary* connection = [self wasNeverConnected:address :command];
    if (connection == nil)
    {
        return;
    }

    //Get the peripheral
    CBPeripheral* peripheral = [connection objectForKey:keyPeripheral];

    //If connection is disconnected alrady, disconnect can't be used
    if ([self isDisconnected:peripheral :command])
    {
        return;
    }

    NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

    [self addDevice:peripheral :returnObj];

    //If currently connecting, just cancel the pending connecting and return disconnected status without saving callback
    if (peripheral.state == CBPeripheralStateConnecting)
    {
        [returnObj setValue:statusDisconnected forKey:keyStatus];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

        [connection removeObjectForKey:operationConnect];
    }
    //Else return disconnecting status and save callback for disconnect status
    else
    {
        [connection setObject: command.callbackId forKey:operationConnect];
    }

    //Disconnect
    [centralManager cancelPeripheralConnection:peripheral];
}

- (void)close:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }

    //Get the arguments
    NSDictionary* obj = [self getArgsObject:command.arguments];
    if ([self isNotArgsObject:obj :command])
    {
        return;
    }

    //Get the connection address
    NSUUID* address = [self getAddress:obj];
    if ([self isNotAddress:address :command])
    {
        return;
    }

    //If never connected or attempted connected, reconnect can't be used
    NSMutableDictionary* connection = [self wasNeverConnected:address :command];
    if (connection == nil)
    {
        return;
    }

    //Get the peripheral
    CBPeripheral* peripheral = [connection objectForKey:keyPeripheral];

    /* Make disconnect/close less annoying
    //If currently connected, reconnect can't be used
    if ([self isNotDisconnected:peripheral :command])
    {
        return;
    }
    */

    //Create dictionary with status message and optionally add connection information
    NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

    [self addDevice:peripheral :returnObj];

    [returnObj setValue:statusClosed forKey:keyStatus];

    //Remove from connections (do I need to clear everything out?)
    [connections removeObjectForKey:address];

    //Return success callback
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)discover:(CDVInvokedUrlCommand *)command
{
    /*NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorDiscover, keyError, logOperationUnsupported, keyMessage, nil];
     CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
     [pluginResult setKeepCallbackAsBool:false];
     [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];*/

    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }

    //Get the arguments
    NSDictionary* obj = [self getArgsObject:command.arguments];
    if ([self isNotArgsObject:obj :command])
    {
        return;
    }

    //Get the connection address
    NSUUID* address = [self getAddress:obj];
    if ([self isNotAddress:address :command])
    {
        return;
    }

    //If never connected or attempted connected, reconnect can't be used
    NSMutableDictionary* connection = [self wasNeverConnected:address :command];
    if (connection == nil)
    {
        return;
    }

    //Get the peripheral
    CBPeripheral* peripheral = [connection objectForKey:keyPeripheral];

    //Ensure connection is connected
    if ([self isNotConnected:peripheral :command])
    {
        return;
    }

    //Check if already discovered
    if ([self isAlreadyDiscovered:connection :command])
    {
        return;
    }

    //Set the discover callback
    [connection setObject:command.callbackId forKey:operationDiscover];

    //Create dictionary to managed discovered services/characteristics/descriptors
    NSMutableDictionary* discoveryQueue = [NSMutableDictionary dictionary];
    [discoveryQueue setValue:[NSMutableDictionary dictionary] forKey:keyServices];
    [discoveryQueue setValue:[NSMutableDictionary dictionary] forKey:keyCharacteristics];
    [connection setObject:discoveryQueue forKey:keyIsDiscoveredQueue];

    //Start a complete discovery
    [connection setObject:[NSNumber numberWithInt:1] forKey:keyIsDiscovered];

    //Get the serviceUuids to discover
    NSMutableArray* serviceUuids = [self getUuids:obj forType:keyServices];

    //Discover the services
    [peripheral discoverServices:serviceUuids];
}

- (void)services:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }

    //Get the arguments
    NSDictionary* obj = [self getArgsObject:command.arguments];
    if ([self isNotArgsObject:obj :command])
    {
        return;
    }

    //Get the connection address
    NSUUID* address = [self getAddress:obj];
    if ([self isNotAddress:address :command])
    {
        return;
    }

    //If never connected or attempted connected, reconnect can't be used
    NSMutableDictionary* connection = [self wasNeverConnected:address :command];
    if (connection == nil)
    {
        return;
    }

    //Get the peripheral
    CBPeripheral* peripheral = [connection objectForKey:keyPeripheral];

    //Ensure connection is connected
    if ([self isNotConnected:peripheral :command])
    {
        return;
    }

    //Set the discover callback
    [connection setObject:command.callbackId forKey:operationDiscover];

    //Get the serviceUuids to discover
    NSMutableArray* serviceUuids = [self getUuids:obj forType:keyServices];

    //Discover the services
    [peripheral discoverServices:serviceUuids];
}

- (void)characteristics:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }

    //Get the arguments
    NSDictionary* obj = [self getArgsObject:command.arguments];
    if ([self isNotArgsObject:obj :command])
    {
        return;
    }

    //Get the connection address
    NSUUID* address = [self getAddress:obj];
    if ([self isNotAddress:address :command])
    {
        return;
    }

    //If never connected or attempted connected, reconnect can't be used
    NSMutableDictionary* connection = [self wasNeverConnected:address :command];
    if (connection == nil)
    {
        return;
    }

    //Get the peripheral
    CBPeripheral* peripheral = [connection objectForKey:keyPeripheral];

    //Ensure connection is connected
    if ([self isNotConnected:peripheral :command])
    {
        return;
    }

    //Get the service
    CBService* service = [self getService:obj forPeripheral:peripheral];
    if ([self isNotService:service forDevice:peripheral :command])
    {
        return;
    }

    //Set the discover callback
    [connection setObject:command.callbackId forKey:operationDiscover];

    //Get the characteristic UUIDs
    NSMutableArray* characteristicUuids = [self getUuids:obj forType:keyCharacteristics];

    //Discover the characteristics for the service
    [peripheral discoverCharacteristics:characteristicUuids forService:service];
}

- (void)descriptors:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }

    //Get the arguments
    NSDictionary* obj = [self getArgsObject:command.arguments];
    if ([self isNotArgsObject:obj :command])
    {
        return;
    }

    //Get the connection address
    NSUUID* address = [self getAddress:obj];
    if ([self isNotAddress:address :command])
    {
        return;
    }

    //If never connected or attempted connected, reconnect can't be used
    NSMutableDictionary* connection = [self wasNeverConnected:address :command];
    if (connection == nil)
    {
        return;
    }

    //Get the peripheral
    CBPeripheral* peripheral = [connection objectForKey:keyPeripheral];

    //Ensure connection is connected
    if ([self isNotConnected:peripheral :command])
    {
        return;
    }

    //Get the service
    CBService* service = [self getService:obj forPeripheral:peripheral];
    if ([self isNotService:service forDevice:peripheral :command])
    {
        return;
    }

    //Get the characteristic
    CBCharacteristic* characteristic = [self getCharacteristic:obj forService:service];
    if ([self isNotCharacteristic:characteristic forDevice:peripheral :command])
    {
        return;
    }

    //Set the discover callback
    [connection setObject:command.callbackId forKey:operationDiscover];

    //Discover the descriptors
    [peripheral discoverDescriptorsForCharacteristic:characteristic];
}

- (void)read:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }

    //Get the arguments
    NSDictionary* obj = [self getArgsObject:command.arguments];
    if ([self isNotArgsObject:obj :command])
    {
        return;
    }

    //Get the connection address
    NSUUID* address = [self getAddress:obj];
    if ([self isNotAddress:address :command])
    {
        return;
    }

    //If never connected or attempted connected, reconnect can't be used
    NSMutableDictionary* connection = [self wasNeverConnected:address :command];
    if (connection == nil)
    {
        return;
    }

    //Get the peripheral
    CBPeripheral* peripheral = [connection objectForKey:keyPeripheral];

    //Ensure connection is connected
    if ([self isNotConnected:peripheral :command])
    {
        return;
    }

    //Get service
    CBService* service = [self getService:obj forPeripheral:peripheral];
    if ([self isNotService:service forDevice:peripheral :command])
    {
        return;
    }

    //Get characteristic
    CBCharacteristic* characteristic = [self getCharacteristic:obj forService:service];
    if ([self isNotCharacteristic:characteristic forDevice:peripheral :command])
    {
        return;
    }

    //Set the callback
    [self addCallback:characteristic.UUID forConnection:connection forOperationType:operationRead forCallback:command.callbackId];

    //Read the value
    [peripheral readValueForCharacteristic:characteristic];
}

- (void)subscribe:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }

    //Get the arguments
    NSDictionary* obj = [self getArgsObject:command.arguments];
    if ([self isNotArgsObject:obj :command])
    {
        return;
    }

    //Get the connection address
    NSUUID* address = [self getAddress:obj];
    if ([self isNotAddress:address :command])
    {
        return;
    }

    //If never connected or attempted connected, reconnect can't be used
    NSMutableDictionary* connection = [self wasNeverConnected:address :command];
    if (connection == nil)
    {
        return;
    }

    //Get the peripheral
    CBPeripheral* peripheral = [connection objectForKey:keyPeripheral];

    //Ensure connection is connected
    if ([self isNotConnected:peripheral :command])
    {
        return;
    }

    //Get the service
    CBService* service = [self getService:obj forPeripheral:peripheral];
    if ([self isNotService:service forDevice:peripheral :command])
    {
        return;
    }

    //Get the characteristic
    CBCharacteristic* characteristic = [self getCharacteristic:obj forService:service];
    if ([self isNotCharacteristic:characteristic forDevice:peripheral :command])
    {
        return;
    }
  
    if (characteristic.isNotifying) {
      NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorSubscription, keyError, logSubscribeAlready, keyMessage, nil];
      CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
      [pluginResult setKeepCallbackAsBool:false];
      [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
      return;
    }

    //Set the callback
    [self addCallback:characteristic.UUID forConnection:connection forOperationType:operationSubscribe forCallback:command.callbackId];

    //Start the subscription
    [peripheral setNotifyValue:true forCharacteristic:characteristic];
}

- (void)unsubscribe:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }

    //Get the arguments
    NSDictionary* obj = [self getArgsObject:command.arguments];
    if ([self isNotArgsObject:obj :command])
    {
        return;
    }

    //Get the connection address
    NSUUID* address = [self getAddress:obj];
    if ([self isNotAddress:address :command])
    {
        return;
    }

    //If never connected or attempted connected, reconnect can't be used
    NSMutableDictionary* connection = [self wasNeverConnected:address :command];
    if (connection == nil)
    {
        return;
    }

    //Get the peripheral
    CBPeripheral* peripheral = [connection objectForKey:keyPeripheral];

    //Ensure connection is connected
    if ([self isNotConnected:peripheral :command])
    {
        return;
    }

    //Get the service
    CBService* service = [self getService:obj forPeripheral:peripheral];
    if ([self isNotService:service forDevice:peripheral :command])
    {
        return;
    }

    //Get the characteristic
    CBCharacteristic* characteristic = [self getCharacteristic:obj forService:service];
    if ([self isNotCharacteristic:characteristic forDevice:peripheral :command])
    {
        return;
    }
  
    if (!characteristic.isNotifying) {
      NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorSubscription, keyError, logUnsubscribeAlready, keyMessage, nil];
      CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
      [pluginResult setKeepCallbackAsBool:false];
      [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
      return;
    }

    //Set the callback
    [self addCallback:characteristic.UUID forConnection:connection forOperationType:operationUnsubscribe forCallback:command.callbackId];

    //Unsubscribe the characteristic
    [peripheral setNotifyValue:false forCharacteristic:characteristic];
}

- (void)write:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }

    //Get the arguments
    NSDictionary* obj = [self getArgsObject:command.arguments];
    if ([self isNotArgsObject:obj :command])
    {
        return;
    }

    //Get the connection address
    NSUUID* address = [self getAddress:obj];
    if ([self isNotAddress:address :command])
    {
        return;
    }

    //If never connected or attempted connected, reconnect can't be used
    NSMutableDictionary* connection = [self wasNeverConnected:address :command];
    if (connection == nil)
    {
        return;
    }

    //Get the peripheral
    CBPeripheral* peripheral = [connection objectForKey:keyPeripheral];

    //Ensure connection is connected
    if ([self isNotConnected:peripheral :command])
    {
        return;
    }

    //Get service
    CBService* service = [self getService:obj forPeripheral:peripheral];

    if ([self isNotService:service forDevice:peripheral :command])
    {
        return;
    }

    //Get characteristic
    CBCharacteristic* characteristic = [self getCharacteristic:obj forService:service];
    if ([self isNotCharacteristic:characteristic forDevice:peripheral :command])
    {
        return;
    }

    //Get the value to write
    NSData* value = [self getValue:obj];
    //And ensure it's not empty
    if (value == nil)
    {
        NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

        [self addDevice:peripheral :returnObj];

        [returnObj setValue:errorWrite forKey:keyError];
        [returnObj setValue:logWriteValueNotFound forKey:keyMessage];

        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        return;
    }

    //Set the callback
    [self addCallback:characteristic.UUID forConnection:connection forOperationType:operationWrite forCallback:command.callbackId];

    //Get the write type (response or no response)
    int writeType = [self getWriteType:obj];

    //Try to write value
    [peripheral writeValue:value forCharacteristic:characteristic type:writeType];
}

- (void)readDescriptor:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }

    //Get the arguments
    NSDictionary* obj = [self getArgsObject:command.arguments];
    if ([self isNotArgsObject:obj :command])
    {
        return;
    }

    //Get the connection address
    NSUUID* address = [self getAddress:obj];
    if ([self isNotAddress:address :command])
    {
        return;
    }

    //If never connected or attempted connected, reconnect can't be used
    NSMutableDictionary* connection = [self wasNeverConnected:address :command];
    if (connection == nil)
    {
        return;
    }

    //Get the peripheral
    CBPeripheral* peripheral = [connection objectForKey:keyPeripheral];

    //Ensure connection is connected
    if ([self isNotConnected:peripheral :command])
    {
        return;
    }

    //Get service
    CBService* service = [self getService:obj forPeripheral:peripheral];
    if ([self isNotService:service forDevice:peripheral :command])
    {
        return;
    }

    //Get characteristic
    CBCharacteristic* characteristic = [self getCharacteristic:obj forService:service];
    if ([self isNotCharacteristic:characteristic forDevice:peripheral :command])
    {
        return;
    }

    //Get descriptor
    CBDescriptor* descriptor = [self getDescriptor:obj forCharacteristic:characteristic];
    if ([self isNotDescriptor:descriptor forDevice:peripheral :command])
    {
        return;
    }

    //Set callback
    [self addDescriptorCallback:descriptor.UUID forCharacteristic:characteristic.UUID forConnection:connection forOperationType:operationRead forCallback:command.callbackId];

    //Try to read descriptor value
    [peripheral readValueForDescriptor:descriptor];
}

- (void)writeDescriptor:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }

    //Get the arguments
    NSDictionary* obj = [self getArgsObject:command.arguments];
    if ([self isNotArgsObject:obj :command])
    {
        return;
    }

    //Get the connection address
    NSUUID* address = [self getAddress:obj];
    if ([self isNotAddress:address :command])
    {
        return;
    }

    //If never connected or attempted connected, reconnect can't be used
    NSMutableDictionary* connection = [self wasNeverConnected:address :command];
    if (connection == nil)
    {
        return;
    }

    //Get the peripheral
    CBPeripheral* peripheral = [connection objectForKey:keyPeripheral];

    //Ensure connection is connected
    if ([self isNotConnected:peripheral :command])
    {
        return;
    }

    //Get service
    CBService* service = [self getService:obj forPeripheral:peripheral];
    if ([self isNotService:service forDevice:peripheral :command])
    {
        return;
    }

    //Get characteristic
    CBCharacteristic* characteristic = [self getCharacteristic:obj forService:service];
    if ([self isNotCharacteristic:characteristic forDevice:peripheral :command])
    {
        return;
    }

    //Get descriptor
    CBDescriptor* descriptor = [self getDescriptor:obj forCharacteristic:characteristic];
    if ([self isNotDescriptor:descriptor forDevice:peripheral :command])
    {
        return;
    }

    //Get value to write
    NSData* value = [self getValue:obj];
    //And ensure it's not null
    if (value == nil)
    {
        NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

        [self addDevice:peripheral :returnObj];

        [returnObj setValue:errorWriteDescriptor forKey:keyError];
        [returnObj setValue:logWriteDescriptorValueNotFound forKey:keyMessage];

        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        return;
    }

    //Set callback
    [self addDescriptorCallback:descriptor.UUID forCharacteristic:characteristic.UUID forConnection:connection forOperationType:operationWrite forCallback:command.callbackId];

    //Try to write the descriptor
    [peripheral writeValue:value forDescriptor:descriptor];
}

- (void)rssi:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }

    //Get the arguments
    NSDictionary* obj = [self getArgsObject:command.arguments];
    if ([self isNotArgsObject:obj :command])
    {
        return;
    }

    //Get the connection address
    NSUUID* address = [self getAddress:obj];
    if ([self isNotAddress:address :command])
    {
        return;
    }

    //If never connected or attempted connected, reconnect can't be used
    NSMutableDictionary* connection = [self wasNeverConnected:address :command];
    if (connection == nil)
    {
        return;
    }

    //Get the peripheral
    CBPeripheral* peripheral = [connection objectForKey:keyPeripheral];

    //Ensure connection is connected
    if ([self isNotConnected:peripheral :command])
    {
        return;
    }

    //Set the callback
    [connection setObject: command.callbackId forKey:operationRssi];

    //Try to read RSSI
    [peripheral readRSSI];
}

- (void)mtu:(CDVInvokedUrlCommand *)command
{
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorMtu, keyError, logOperationUnsupported, keyMessage, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)requestConnectionPriority:(CDVInvokedUrlCommand *)command
{
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorRequestConnectionPriority, keyError, logOperationUnsupported, keyMessage, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
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
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }

    //Get the arguments
    NSDictionary* obj = [self getArgsObject:command.arguments];
    if ([self isNotArgsObject:obj :command])
    {
        return;
    }

    //Get the connection address
    NSUUID* address = [self getAddress:obj];
    if ([self isNotAddress:address :command])
    {
        return;
    }

    //If never connected or attempted connected, reconnect can't be used
    NSMutableDictionary* connection = [self wasNeverConnected:address :command];
    if (connection == nil)
    {
        return;
    }

    //Get the peripheral
    CBPeripheral* peripheral = [connection objectForKey:keyPeripheral];

    //Return whether isConnected or not
    NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

    [self addDevice:peripheral :returnObj];

    [returnObj setValue:[NSNumber numberWithBool:(peripheral.state == CBPeripheralStateConnected)] forKey:keyIsConnected];

    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)isDiscovered:(CDVInvokedUrlCommand *)command
{
    //Ensure Bluetooth is enabled
    if ([self isNotInitialized:command])
    {
        return;
    }

    //Get the arguments
    NSDictionary* obj = [self getArgsObject:command.arguments];
    if ([self isNotArgsObject:obj :command])
    {
        return;
    }

    //Get the connection address
    NSUUID* address = [self getAddress:obj];
    if ([self isNotAddress:address :command])
    {
        return;
    }

    //If never connected or attempted connected, reconnect can't be used
    NSMutableDictionary* connection = [self wasNeverConnected:address :command];
    if (connection == nil)
    {
        return;
    }

    //Get the peripheral
    CBPeripheral* peripheral = [connection objectForKey:keyPeripheral];

    //Ensure connection is connected
    if ([self isNotConnected:peripheral :command])
    {
        return;
    }

    //Return whether isDiscovered or not
    NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

    [self addDevice:peripheral :returnObj];

    if ([[connection objectForKey:keyIsDiscovered] intValue] == 2) {
        [returnObj setValue:[NSNumber numberWithBool:true] forKey:keyIsDiscovered];
    } else {
        [returnObj setValue:[NSNumber numberWithBool:false] forKey:keyIsDiscovered];
    }

    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)hasPermission:(CDVInvokedUrlCommand *)command
{
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: @"hasPermission", keyError, logOperationUnsupported, keyMessage, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)requestPermission:(CDVInvokedUrlCommand *)command
{
    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: @"requestPermission", keyError, logOperationUnsupported, keyMessage, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

//Central Manager Delegates
- (void) centralManagerDidUpdateState:(CBCentralManager *)central
{
    //If status notifications shouldn't be sent and first status after initialization has been sent, ignore
    if ([statusReceiver boolValue] == FALSE)
    {
        return;
    }

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
        returnObj = [NSDictionary dictionaryWithObjectsAndKeys: statusDisabled, keyStatus, error, keyMessage, nil];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];

        //Clear out any connections
        connections = [NSMutableDictionary dictionary];
        scanCallback = nil;
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
    //Needed to support background mode
}

- (void)centralManager:(CBCentralManager *)central didDiscoverPeripheral:(CBPeripheral *)peripheral advertisementData:(NSDictionary *)advertisementData RSSI:(NSNumber *)RSSI
{
    //If no scan callback, nothing can be returned
    if (scanCallback == nil)
    {
        return;
    }
  
    NSMutableDictionary* advertisement = [NSMutableDictionary dictionary];

    [advertisement setValue:[advertisementData valueForKey:CBAdvertisementDataLocalNameKey] forKey:@"localName"];

    NSData* data = [advertisementData valueForKey:CBAdvertisementDataManufacturerDataKey];
    NSString* dataString = [data base64EncodedStringWithOptions:0];
    [advertisement setValue:dataString forKey:@"manufacturerData"];

    NSDictionary* serviceData = [advertisementData valueForKey:CBAdvertisementDataServiceDataKey];
    NSMutableDictionary* serviceDataOut = [NSMutableDictionary dictionary];
    NSArray* keys = [serviceData allKeys];
    for (CBUUID* uuid in keys) {
      NSData* dataOut = [serviceData objectForKey: uuid];
      NSString* dataOutString = [dataOut base64EncodedStringWithOptions:0];
      [serviceDataOut setValue:dataOutString forKey:uuid.UUIDString];
    }
    [advertisement setValue:serviceDataOut forKey:@"serviceData"];

    NSMutableArray* serviceUuidsOut = [[NSMutableArray alloc] init];
    NSArray* serviceUuids = [advertisementData valueForKey:CBAdvertisementDataServiceUUIDsKey];
    for (CBUUID* uuid in serviceUuids) {
      [serviceUuidsOut addObject:uuid.UUIDString];
    }
    [advertisement setValue:serviceUuidsOut forKey:@"serviceUuids"];

    NSMutableArray* overflowServiceUuidsOut = [[NSMutableArray alloc] init];
    NSArray* overflowServiceUuids = [advertisementData valueForKey:CBAdvertisementDataOverflowServiceUUIDsKey];
    for (CBUUID* uuid in overflowServiceUuids) {
      [overflowServiceUuidsOut addObject:uuid.UUIDString];
    }
    [advertisement setValue:overflowServiceUuidsOut forKey:@"overflowServiceUuids"];

    [advertisement setValue:[advertisementData valueForKey:CBAdvertisementDataTxPowerLevelKey] forKey:@"txPowerLevel"];
    [advertisement setValue:[advertisementData valueForKey:CBAdvertisementDataIsConnectable] forKey:@"isConnectable"];

    NSMutableArray* solicitedServiceUuidsOut = [[NSMutableArray alloc] init];
    NSArray* solicitedServiceUuids = [advertisementData valueForKey:CBAdvertisementDataSolicitedServiceUUIDsKey];
    for (CBUUID* uuid in solicitedServiceUuids) {
      [solicitedServiceUuidsOut addObject:uuid.UUIDString];
    }
    [advertisement setValue:solicitedServiceUuidsOut forKey:@"solicitedServiceUuids"];

    NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

    [self addDevice:peripheral :returnObj];

    [returnObj setValue:statusScanResult forKey:keyStatus];
    [returnObj setValue:RSSI forKey:keyRssi];
    [returnObj setValue:advertisement forKey:keyAdvertisement];

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:true];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:scanCallback];
}

- (void)centralManager:(CBCentralManager *)central didConnectPeripheral:(CBPeripheral *)peripheral
{
    //Get connection
    NSMutableDictionary* connection = [connections objectForKey:peripheral.identifier];
    if (connection == nil)
    {
        return;
    }

    //Get connect callback
    NSString* connectCallback = [connection objectForKey:operationConnect];

    //If no connect callback, can't continue
    if (connectCallback == nil)
    {
        return;
    }

    //Return connection information of what was connected
    NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

    [self addDevice:peripheral :returnObj];

    [returnObj setValue:statusConnected forKey:keyStatus];

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    //Keep in case connection gets disconnected without user initiation
    [pluginResult setKeepCallbackAsBool:true];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:connectCallback];
}

- (void)centralManager:(CBCentralManager *)central didFailToConnectPeripheral:(CBPeripheral *)peripheral error:(NSError *)error
{
    //Get connection
    NSMutableDictionary* connection = [connections objectForKey:peripheral.identifier];
    if (connection == nil)
    {
        return;
    }

    //Get connect callback
    NSString* connectCallback = [connection objectForKey:operationConnect];
    [connection removeObjectForKey:operationConnect];

    //If no connect callback, can't continue
    if (connectCallback == nil)
    {
        return;
    }

    //Return the error message
    NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

    [self addDevice:peripheral :returnObj];

    [returnObj setValue:errorConnect forKey:keyError];
    [returnObj setValue:error.description forKey:keyMessage];

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:connectCallback];
}

- (void)centralManager:(CBCentralManager *)central didDisconnectPeripheral:(CBPeripheral *)peripheral error:(NSError *)error
{
    //Get connection
    NSMutableDictionary* connection = [connections objectForKey:peripheral.identifier];
    if (connection == nil)
    {
        return;
    }

    NSArray* callbacks = [self getCallbacks:connection];
    for (NSString* callback in callbacks) {
      NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

      [self addDevice:peripheral :returnObj];
      [returnObj setValue:errorIsDisconnected forKey:keyError];
      [returnObj setValue:logIsDisconnected forKey:keyMessage];

      CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
      [pluginResult setKeepCallbackAsBool:false];
      [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];
    }

    //Get connect callback
    NSString* callback = [connection objectForKey:operationConnect];

    //Reset all callbacks
    connection = [NSMutableDictionary dictionary];
    [connection setObject:peripheral forKey:keyPeripheral];

    [connections setObject:connection forKey:peripheral.identifier];

    //If no connect callback, can't continue
    if (callback == nil)
    {
        return;
    }
  
    //Return disconnected connection information
    NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

    [self addDevice:peripheral :returnObj];

    [returnObj setValue:statusDisconnected forKey:keyStatus];

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];
}

//Peripheral Delegates
- (void)peripheral:(CBPeripheral *)peripheral didDiscoverServices:(NSError *)error
{
    //Get connection
    NSMutableDictionary* connection = [connections objectForKey:peripheral.identifier];
    if (connection == nil)
    {
        return;
    }

    //See if complete discovery is enabled
    if ([[connection objectForKey:keyIsDiscovered] intValue] == 1) {
        if ([self checkDiscoveryError:connection :error])
        {
            return;
        }

        //Get discovery queue
        NSMutableDictionary* discoveryQueue = [connection objectForKey:keyIsDiscoveredQueue];
        if (discoveryQueue == nil) {
            return;
        }

        //Get services queue
        NSMutableDictionary* services = [discoveryQueue objectForKey:keyServices];
        if (services == nil) {
            return;
        }

        //Add to queue and discover characteristics
        for (CBService* service in peripheral.services)
        {
            [services setObject:service.UUID forKey: service.UUID];
            [peripheral discoverCharacteristics:nil forService:service];
        }
    } else {
        //Get discover callback
        NSString* callback = [connection objectForKey:operationDiscover];
        [connection removeObjectForKey:operationDiscover];

        //Return if callback is null
        if (callback == nil)
        {
            return;
        }

        NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

        [self addDevice:peripheral :returnObj];

        //If error is set, send back error
        if (error != nil)
        {
            [returnObj setValue:errorServices forKey:keyError];
            [returnObj setValue:error.description forKey:keyMessage];

            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
            [pluginResult setKeepCallbackAsBool:false];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];
            return;
        }

        //Get array of service UUIDs
        NSMutableArray* services = [[NSMutableArray alloc] init];
        for (CBService* service in peripheral.services)
        {
            [services addObject:[service.UUID representativeString]];
        }

        //Return service UUIDs
        [returnObj setValue:statusServices forKey:keyStatus];
        [returnObj setValue:services forKey:keyServices];

        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];
    }
}

- (void)peripheral:(CBPeripheral *)peripheral didDiscoverCharacteristicsForService:(CBService *)service error:(NSError *)error
{
    //Get connection
    NSMutableDictionary* connection = [connections objectForKey:peripheral.identifier];
    if (connection == nil)
    {
        return;
    }

    //See if complete discovery is enabled
    if ([[connection objectForKey:keyIsDiscovered] intValue] == 1) {
        if ([self checkDiscoveryError:connection :error])
        {
            return;
        }

        //Get discovery queue
        NSMutableDictionary* discoveryQueue = [connection objectForKey:keyIsDiscoveredQueue];
        if (discoveryQueue == nil) {
            return;
        }

        //Get services queue
        NSMutableDictionary* services = [discoveryQueue objectForKey:keyServices];
        if (services == nil) {
            return;
        }

        //Get characteristics queue
        NSMutableDictionary* characteristics = [discoveryQueue objectForKey:keyCharacteristics];
        if (services == nil) {
            return;
        }

        //Remove service from undiscovered
        [services removeObjectForKey:service.UUID];

        //Add to queue and discover descriptors
        for (CBCharacteristic* characteristic in service.characteristics)
        {
            [characteristics setObject:characteristic.UUID forKey: characteristic.UUID];
            [peripheral discoverDescriptorsForCharacteristic:characteristic];
        }
    } else {
        //Get discover callback
        NSString* callback = [connection objectForKey:operationDiscover];
        [connection removeObjectForKey:operationDiscover];

        //Return if callback is null
        if (callback == nil)
        {
            return;
        }

        NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

        [self addDevice:peripheral :returnObj];

        [self addService:service :returnObj];

        //Return error if necessary
        if (error != nil)
        {
            [returnObj setValue:errorCharacteristics forKey:keyError];
            [returnObj setValue:error.description forKey:keyMessage];

            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
            [pluginResult setKeepCallbackAsBool:false];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];
            return;
        }

        //Get array of characteristics with their UUIDs and properties
        NSMutableArray* characteristics = [[NSMutableArray alloc] init];
        for (CBCharacteristic* characteristic in service.characteristics)
        {
            NSMutableDictionary* properties = [self getProperties:characteristic];

            NSDictionary* characteristicObject = [NSDictionary dictionaryWithObjectsAndKeys: [characteristic.UUID representativeString], keyUuid, properties, keyProperties, nil];

            [characteristics addObject:characteristicObject];
        }

        //Return characteristics
        [returnObj setValue:statusCharacteristics forKey:keyStatus];
        [returnObj setValue:characteristics forKey:keyCharacteristics];

        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];
    }
}

- (void)peripheral:(CBPeripheral *)peripheral didDiscoverDescriptorsForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error
{
    //Get connection
    NSMutableDictionary* connection = [connections objectForKey:peripheral.identifier];
    if (connection == nil)
    {
        return;
    }

    //See if complete discovery is enabled
    if ([[connection objectForKey:keyIsDiscovered] intValue] == 1) {
        if ([self checkDiscoveryError:connection :error])
        {
            return;
        }

        //Get discovery queue
        NSMutableDictionary* discoveryQueue = [connection objectForKey:keyIsDiscoveredQueue];
        if (discoveryQueue == nil) {
            return;
        }

        //Get services queue
        NSMutableDictionary* servicesCheck = [discoveryQueue objectForKey:keyServices];
        if (servicesCheck == nil) {
            return;
        }

        //Get characteristics queue
        NSMutableDictionary* characteristicsCheck = [discoveryQueue objectForKey:keyCharacteristics];
        if (characteristicsCheck == nil) {
            return;
        }

        //Remove service from undiscovered
        [characteristicsCheck removeObjectForKey:characteristic.UUID];

        //See if services and characteristics are empty
        if ([servicesCheck count] > 0 || [characteristicsCheck count] > 0) {
            return;
        }

        //Set discovered to true
        [connection setObject:[NSNumber numberWithInt:2] forKey:keyIsDiscovered];

        //Get discover callback
        NSString* callback = [connection objectForKey:operationDiscover];
        [connection removeObjectForKey:operationDiscover];

        //Return if callback is null
        if (callback == nil)
        {
            return;
        }

        NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

        [self addDevice:peripheral :returnObj];

        NSMutableArray* services = [[NSMutableArray alloc] init];
        for (CBService* service in peripheral.services)
        {
            NSMutableDictionary* serviceObj = [NSMutableDictionary dictionary];
            [serviceObj setValue:[service.UUID representativeString] forKey:keyUuid];

            NSMutableArray* characteristics = [[NSMutableArray alloc] init];
            for (CBCharacteristic* characteristic in service.characteristics)
            {
                NSMutableDictionary* characteristicObj = [NSMutableDictionary dictionary];
                [characteristicObj setValue:[characteristic.UUID representativeString] forKey:keyUuid];
                [characteristicObj setValue:[self getProperties:characteristic] forKey:keyProperties];

                NSMutableArray* descriptors = [[NSMutableArray alloc] init];
                for (CBDescriptor* descriptor in characteristic.descriptors)
                {
                    NSMutableDictionary* descriptorObj = [NSMutableDictionary dictionary];
                    [descriptorObj setValue:[descriptor.UUID representativeString] forKey:keyUuid];

                    [descriptors addObject:descriptorObj];
                }

                [characteristicObj setValue:descriptors forKey:keyDescriptors];


                [characteristics addObject:characteristicObj];
            }

            [serviceObj setValue:characteristics forKey:keyCharacteristics];

            [services addObject:serviceObj];
        }

        [returnObj setValue:services forKey:keyServices];

        [returnObj setValue:statusDiscovered forKey:keyStatus];

        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];
    } else {
        //Get discover callback
        NSString* callback = [connection objectForKey:operationDiscover];
        [connection removeObjectForKey:operationDiscover];

        //Return if callback is null
        if (callback == nil)
        {
            return;
        }

        NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

        [self addDevice:peripheral :returnObj];
        [self addCharacteristic:characteristic :returnObj];

        //Return error if necessary
        if (error != nil)
        {
            [returnObj setValue:errorDescriptors forKey:keyError];
            [returnObj setValue:error.description forKey:keyMessage];

            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
            [pluginResult setKeepCallbackAsBool:false];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];
            return;
        }

        //Get list of descriptors
        NSMutableArray* descriptors = [[NSMutableArray alloc] init];
        for (CBDescriptor* descriptor in characteristic.descriptors)
        {
            [descriptors addObject:[descriptor.UUID representativeString]];
        }

        [returnObj setValue:statusDescriptors forKey:keyStatus];
        [returnObj setValue:descriptors forKey:keyDescriptors];

        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];
    }
}

- (void)peripheral:(CBPeripheral *)peripheral didUpdateValueForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error
{
    //Get connection
    NSMutableDictionary* connection = [connections objectForKey:peripheral.identifier];
    if (connection == nil)
    {
        return;
    }

    //Create the initial return object
    NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

    [self addDevice:peripheral :returnObj];
    [self addCharacteristic:characteristic :returnObj];

    //If an error exists...
    if (error != nil)
    {
        //Get the callback based on whether subscription or read
        NSString* callback = nil;
        if (characteristic.isNotifying)
        {
            callback = [self getCallback:characteristic.UUID forConnection:connection forOperationType:operationSubscribe];
            [returnObj setValue:errorSubscription forKey:keyError];
        }
        else
        {
            callback = [self getCallback:characteristic.UUID forConnection:connection forOperationType:operationRead];
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
            [self removeCallback:characteristic.UUID forConnection:connection forOperationType:operationSubscribe];
        }
        else
        {
            [self removeCallback:characteristic.UUID forConnection:connection forOperationType:operationRead];
        }

        return;
    }

    //Add the read value to return object
    [self addValue:characteristic.value toDictionary:returnObj];

    //Get the correct callback and return value
    if (characteristic.isNotifying)
    {
        NSString* callback = [self getCallback:characteristic.UUID forConnection:connection forOperationType:operationSubscribe];

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
        NSString* callback = [self getCallback:characteristic.UUID forConnection:connection forOperationType:operationRead];

        if (callback == nil)
        {
            return;
        }

        [returnObj setValue:statusRead forKey:keyStatus];

        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];

        [self removeCallback:characteristic.UUID forConnection:connection forOperationType:operationRead];
    }
}

- (void)peripheral:(CBPeripheral *)peripheral didUpdateValueForDescriptor:(CBDescriptor *)descriptor error:(NSError *)error
{
    //Get connection
    NSMutableDictionary* connection = [connections objectForKey:peripheral.identifier];
    if (connection == nil)
    {
        return;
    }

    //Get the descriptors parent characteristic
    CBCharacteristic* characteristic = descriptor.characteristic;

    //Get the callback and immediately delete it
    NSString* callback = [self getDescriptorCallback:descriptor.UUID forCharacteristic:characteristic.UUID forConnection:connection forOperationType:operationRead];
    [self removeDescriptorCallback:descriptor.UUID forCharacteristic:characteristic.UUID forConnection:connection forOperationType:operationRead];

    //Return if callback is null
    if (callback == nil)
    {
        return;
    }

    //Create initial return object
    NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

    [self addDevice:peripheral :returnObj];
    [self addDescriptor:descriptor :returnObj];

    //If error isn't null, return an error
    if (error != nil)
    {
        [returnObj setValue:errorReadDescriptor forKey:keyError];
        [returnObj setValue:error.description forKey:keyMessage];

        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];
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
    [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];
}

- (void)peripheral:(CBPeripheral *)peripheral didWriteValueForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error
{
    //Get connection
    NSMutableDictionary* connection = [connections objectForKey:peripheral.identifier];
    if (connection == nil)
    {
        return;
    }

    //Get the proper callback for write operation
    NSString* callback = [self getCallback:characteristic.UUID forConnection:connection forOperationType:operationWrite];
    [self removeCallback:characteristic.UUID forConnection:connection forOperationType:operationWrite];

    //Return if callback is null
    if (callback == nil)
    {
        return;
    }

    NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

    [self addDevice:peripheral :returnObj];
    [self addCharacteristic:characteristic :returnObj];

    //If error exists, return error
    if (error != nil)
    {
        [returnObj setValue:errorWrite forKey:keyError];
        [returnObj setValue:error.description forKey:keyMessage];

        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];
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
}

- (void)peripheral:(CBPeripheral *)peripheral didWriteValueForDescriptor:(CBDescriptor *)descriptor error:(NSError *)error
{
    //Get connection
    NSMutableDictionary* connection = [connections objectForKey:peripheral.identifier];
    if (connection == nil)
    {
        return;
    }

    //Get descriptor's parent characteristic
    CBCharacteristic* characteristic = descriptor.characteristic;

    //Get the callback and immediately delete it
    NSString* callback = [self getDescriptorCallback:descriptor.UUID forCharacteristic:characteristic.UUID forConnection:connection forOperationType:operationRead];
    [self removeDescriptorCallback:descriptor.UUID forCharacteristic:characteristic.UUID forConnection:connection forOperationType:operationWrite];

    //Return if callback is null
    if (callback == nil)
    {
        return;
    }

    NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

    [self addDevice:peripheral :returnObj];
    [self addDescriptor:descriptor :returnObj];

    //If error exists, return an error
    if (error != nil)
    {
        [returnObj setValue:errorWriteDescriptor forKey:keyError];
        [returnObj setValue:error.description forKey:keyMessage];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];
        return;
    }

    //Add descriptor value to return obj
    [self addValue:descriptor.value toDictionary:returnObj];

    //Add status
    [returnObj setValue:statusWrittenDescriptor forKey:keyStatus];

    //Send the result
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];
}

- (void)peripheral:(CBPeripheral *)peripheral didUpdateNotificationStateForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error
{
    //Get connection
    NSMutableDictionary* connection = [connections objectForKey:peripheral.identifier];
    if (connection == nil)
    {
        return;
    }

    //Create the initial return object
    NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

    [self addDevice:peripheral :returnObj];
    [self addCharacteristic:characteristic :returnObj];

    if (error != nil)
    {
        //Usually I would use characteristic.isNotifying to determine which callback to use
        //But that probably isn't accurate if there's an error, so just use subscribe
        NSString* callback = [self getCallback:characteristic.UUID forConnection:connection forOperationType:operationSubscribe];
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
        [self removeCallback:characteristic.UUID forConnection:connection forOperationType:operationSubscribe];

        return;
    }

    //If notifying, send result via subscribe operation
    if (characteristic.isNotifying)
    {
        NSString* callback = [self getCallback:characteristic.UUID forConnection:connection forOperationType:operationSubscribe];

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
        NSString* callback = [self getCallback:characteristic.UUID forConnection:connection forOperationType:operationUnsubscribe];

        if (callback == nil)
        {
            return;
        }

        [returnObj setValue:statusUnsubscribed forKey:keyStatus];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];

        [self removeCallback:characteristic.UUID forConnection:connection forOperationType:operationUnsubscribe];
    }
}

- (void)peripheralDidUpdateRSSI:(CBPeripheral *)peripheral error:(NSError *)error
{
    //Get connection
    NSMutableDictionary* connection = [connections objectForKey:peripheral.identifier];
    if (connection == nil)
    {
        return;
    }

    //Get the proper callback for write operation
    NSString* callback = [connection objectForKey:operationRssi];
    [connection removeObjectForKey:operationRssi];

    //Return if callback is null
    if (callback == nil)
    {
        return;
    }

    //Create the initial return object
    NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

    [self addDevice:peripheral :returnObj];

    //If error exists, return error
    if (error != nil)
    {
        [returnObj setValue:errorRssi forKey:keyError];
        [returnObj setValue:error.description forKey:keyMessage];

        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];
        return;
    }

    //Return RSSI value
    [returnObj setValue:peripheral.RSSI forKey:keyRssi];
    [returnObj setValue:statusRssi forKey:keyStatus];

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];
}

//Helpers for Callbacks
- (NSMutableDictionary*) ensureCallback: (CBUUID *) characteristicUuid forConnection:(NSMutableDictionary*) connection
{
    //See if callback map exists for characteristic
    NSMutableDictionary* characteristicCallbacks = [connection objectForKey:characteristicUuid];

    //If it does, return it
    if (characteristicCallbacks != nil)
    {
        return characteristicCallbacks;
    }

    //Or create a new map for characteristic
    NSMutableDictionary* newCharacteristicCallbacks = [NSMutableDictionary dictionary];
    [connection setObject:newCharacteristicCallbacks forKey:characteristicUuid];
    return newCharacteristicCallbacks;
}

- (void) addCallback: (CBUUID *) characteristicUuid forConnection:(NSMutableDictionary*) connection forOperationType:(NSString*) operationType forCallback:(NSString*) callback
{
    //Get the characteristic's callback map
    NSMutableDictionary* characteristicCallbacks = [self ensureCallback:characteristicUuid forConnection:connection];
    //And add a callback for a particular operation
    [characteristicCallbacks setObject:callback forKey:operationType];
}

- (NSString*) getCallback: (CBUUID *) characteristicUuid forConnection:(NSMutableDictionary*)connection forOperationType:(NSString*) operationType
{
    //Get the characteristic's callback
    NSMutableDictionary* characteristicCallbacks = [connection objectForKey:characteristicUuid];

    if (characteristicCallbacks == nil)
    {
        return nil;
    }

    //Return the callback for a particular operation, which may be null
    return [characteristicCallbacks objectForKey:operationType];
}

- (NSArray*) getCallbacks: (NSMutableDictionary*)connection
{
  //TODO need to store this in a cleaner format, support for other actions like descriptors, rssi, etc
  NSMutableArray* callbacks = [[NSMutableArray alloc] init];
  NSArray* keys = [connection allKeys];
  NSUInteger count = [keys count];
  for (int i = 0; i < count; i++) {
    id key = [keys objectAtIndex: i];
    if (![key isKindOfClass:[CBUUID class]]) {
      continue;
    }
    id characteristic = [connection objectForKey: key];
    NSArray* keysCallback = [characteristic allKeys];
    NSUInteger countCallback = [keysCallback count];
    for (int j = 0; j < countCallback; j++) {
      id keyCallback = [keysCallback objectAtIndex: j];
      if (![keyCallback isKindOfClass:[NSString class]]) {
        continue;
      }
      id callback = [characteristic objectForKey:keyCallback];
      if (!callback) {
        continue;
      }
      [callbacks addObject:callback];
    }
  }

  return callbacks;
}

- (void) removeCallback: (CBUUID *) characteristicUuid forConnection: (NSMutableDictionary*)connection forOperationType:(NSString*) operationType
{
    //Get the characteristic's callback
    NSMutableDictionary* characteristicCallbacks = [connection objectForKey:characteristicUuid];

    if (characteristicCallbacks == nil)
    {
        return;
    }

    //Remove the callback for the particular operation
    [characteristicCallbacks removeObjectForKey:operationType];
}

- (NSMutableDictionary*) ensureDescriptorCallback: (CBUUID*) descriptorUuid forCharacteristic:(CBUUID*) characteristicUuid forConnection:(NSMutableDictionary*) connection
{
    //Get characteristic callbacks
    NSMutableDictionary* characteristicCallbacks = [self ensureCallback:characteristicUuid forConnection:connection];

    //See if callback map exists for descriptor
    NSMutableDictionary* descriptorCallbacks = [characteristicCallbacks objectForKey:descriptorUuid];

    //If it does, return it
    if (descriptorCallbacks != nil)
    {
        return descriptorCallbacks;
    }

    //Or create a new map for descriptor
    NSMutableDictionary* newDescriptorCallbacks = [NSMutableDictionary dictionary];
    [characteristicCallbacks setObject:newDescriptorCallbacks forKey:descriptorUuid];
    return newDescriptorCallbacks;
}

- (void) addDescriptorCallback:(CBUUID*)descriptorUuid forCharacteristic:(CBUUID*)characteristicUuid forConnection:(NSMutableDictionary*)connection forOperationType:(NSString*)operationType forCallback:(NSString*)callback
{
    //Get the descriptor's callback map
    NSMutableDictionary* descriptorCallbacks = [self ensureDescriptorCallback:descriptorUuid forCharacteristic:characteristicUuid forConnection:connection];
    //And add a callback for a particular operation
    [descriptorCallbacks setObject:callback forKey:operationType];
}

- (NSString*) getDescriptorCallback:(CBUUID*)descriptorUuid forCharacteristic:(CBUUID*)characteristicUuid forConnection:(NSMutableDictionary*)connection forOperationType:(NSString*) operationType
{
    //Get the characteristic's callback
    NSMutableDictionary* characteristicCallbacks = [connection objectForKey:characteristicUuid];

    if (characteristicCallbacks == nil)
    {
        return nil;
    }

    //Get the descriptor callbacks
    NSMutableDictionary* descriptorCallbacks = [characteristicCallbacks objectForKey:descriptorUuid];

    if (descriptorCallbacks == nil)
    {
        return nil;
    }

    //Return the callback for a particular operation, which may be null
    return [descriptorCallbacks objectForKey:operationType];
}

- (void) removeDescriptorCallback:(CBUUID*)descriptorUuid forCharacteristic:(CBUUID*)characteristicUuid forConnection: (NSMutableDictionary*)connection forOperationType:(NSString*) operationType
{
    //Get the characteristic's callback
    NSMutableDictionary* characteristicCallbacks = [connection objectForKey:characteristicUuid];

    if (characteristicCallbacks == nil)
    {
        return;
    }

    //Get the descriptor callbacks
    NSMutableDictionary* descriptorCallbacks = [characteristicCallbacks objectForKey:descriptorUuid];
    if (descriptorCallbacks == nil)
    {
        return;
    }

    //Remove the callback for the particular operation
    [descriptorCallbacks removeObjectForKey:operationType];
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

- (BOOL) isNotService:(CBService*) service forDevice:(CBPeripheral*)peripheral :(CDVInvokedUrlCommand *)command
{
    if (service != nil)
    {
        return false;
    }

    NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

    [self addDevice:peripheral :returnObj];

    [returnObj setValue:errorService forKey:keyError];
    [returnObj setValue:logNoService forKey:keyMessage];

    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

    return true;
}

- (BOOL) isNotCharacteristic:(CBCharacteristic*)characteristic forDevice:(CBPeripheral*)peripheral :(CDVInvokedUrlCommand *)command
{
    if (characteristic != nil)
    {
        return false;
    }

    NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

    [self addDevice:peripheral :returnObj];

    [returnObj setValue:errorCharacteristic forKey:keyError];
    [returnObj setValue:logNoCharacteristic forKey:keyMessage];

    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

    return true;
}

- (BOOL) isNotDescriptor:(CBDescriptor*) descriptor forDevice:(CBPeripheral*)peripheral :(CDVInvokedUrlCommand *)command
{
    if (descriptor != nil)
    {
        return false;
    }

    NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

    [self addDevice:peripheral :returnObj];

    [returnObj setValue:errorDescriptor forKey:keyError];
    [returnObj setValue:logNoDescriptor forKey:keyMessage];

    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

    return true;
}

- (BOOL) wasConnected:(NSUUID *)address :(CDVInvokedUrlCommand *)command
{
    //If connection previously connected or attempted connection, use reconnect or disconnect/close
    NSMutableDictionary* connection = [connections objectForKey:address];
    if (connection != nil)
    {
        CBPeripheral* peripheral = [connection objectForKey:keyPeripheral];

        NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

        [self addDevice:peripheral :returnObj];

        [returnObj setValue:errorConnect forKey:keyError];
        [returnObj setValue:logPreviouslyConnected forKey:keyMessage];

        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        return true;
    }

    return false;
}

- (NSMutableDictionary *) wasNeverConnected:(NSUUID *)address :(CDVInvokedUrlCommand *)command
{
    NSMutableDictionary* connection = [connections objectForKey:address];
    if (connection != nil)
    {
        return connection;
    }

    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorNeverConnected, keyError, logNeverConnected, keyMessage, [address UUIDString], keyAddress, nil];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

    return nil;
}

- (BOOL) isNotDisconnected:(CBPeripheral *)peripheral :(CDVInvokedUrlCommand *)command
{
    if (peripheral.state == CBPeripheralStateDisconnected)
    {
        return false;
    }

    NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

    [self addDevice:peripheral :returnObj];

    [returnObj setValue:errorIsNotDisconnected forKey:keyError];
    [returnObj setValue:logIsNotDisconnected forKey:keyMessage];

    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

    return true;
}

- (BOOL) isDisconnected:(CBPeripheral*)peripheral :(CDVInvokedUrlCommand *)command
{
    if (peripheral.state != CBPeripheralStateDisconnected)
    {
        return false;
    }

    NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

    [self addDevice:peripheral :returnObj];

    [returnObj setValue:errorIsDisconnected forKey:keyError];
    [returnObj setValue:logIsDisconnected forKey:keyMessage];

    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

    return true;
}

- (BOOL) isNotConnected:(CBPeripheral *)peripheral :(CDVInvokedUrlCommand *)command
{
    if (peripheral.state == CBPeripheralStateConnected)
    {
        return false;
    }

    NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

    [self addDevice:peripheral :returnObj];

    [returnObj setValue:errorIsNotConnected forKey:keyError];
    [returnObj setValue:logIsNotConnected forKey:keyMessage];

    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

    return true;
}

- (BOOL) isNotAddress:(NSUUID *)address :(CDVInvokedUrlCommand *)command
{
    if (address == nil)
    {
        NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorConnect, keyError, logNoAddress, keyMessage, nil];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
        [pluginResult setKeepCallbackAsBool:false];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        return true;
    }

    return false;
}

- (BOOL) isAlreadyDiscovered:(NSMutableDictionary*) connection :(CDVInvokedUrlCommand *)command
{
    if ([[connection objectForKey:keyIsDiscovered] intValue] == 0) {
        return false;
    }

    NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

    CBPeripheral* peripheral = [connection objectForKey:keyPeripheral];
    [self addDevice:peripheral :returnObj];

    [returnObj setValue:errorDiscover forKey:keyError];
    [returnObj setValue:logAlreadyDiscovering forKey:keyMessage];

    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

    return true;
}

- (BOOL) checkDiscoveryError:(NSMutableDictionary*) connection :(NSError *)error
{
    if (error == nil) {
        return false;
    }

    //Get discover callback
    NSString* callback = [connection objectForKey:operationDiscover];
    [connection removeObjectForKey:operationDiscover];

    //Reset discovery state
    [connection setObject: [NSNumber numberWithInt:0] forKey:keyIsDiscovered];

    //Return if callback is null
    if (callback == nil)
    {
        return true;
    }

    NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];

    CBPeripheral* peripheral = [connection objectForKey:keyPeripheral];
    [self addDevice:peripheral :returnObj];

    [returnObj setValue:errorDiscover forKey:keyError];
    [returnObj setValue:error.description forKey:keyMessage];

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:callback];

    return true;
}

-(void) addDevice:(CBPeripheral*)peripheral :(NSDictionary*)returnObj
{
    NSObject* name = [self formatName:peripheral.name];
    [returnObj setValue:name forKey:keyName];
    [returnObj setValue:peripheral.identifier.UUIDString forKey:keyAddress];
}

-(void) addService:(CBService*)service :(NSDictionary*)returnObj
{
    [returnObj setValue:service.UUID.representativeString forKey:keyService];
}

-(void) addCharacteristic:(CBCharacteristic*)characteristic :(NSDictionary*)returnObj
{
    [self addService:characteristic.service :returnObj];
    [returnObj setValue:characteristic.UUID.representativeString forKey:keyCharacteristic];
}

-(void) addDescriptor:(CBDescriptor*)descriptor :(NSDictionary*)returnObj
{
    [self addCharacteristic:descriptor.characteristic :returnObj];
    [returnObj setValue:descriptor.UUID.representativeString forKey:keyDescriptor];
}

//General Helpers
-(NSDictionary*) getArgsObject:(NSArray *)args
{
    if (args == nil)
    {
        return nil;
    }

    if (args.count != 1)
    {
        return nil;
    }

    NSObject* arg = [args objectAtIndex:0];

    if (![arg isKindOfClass:[NSDictionary class]])
    {
        return nil;
    }

    return (NSDictionary *)[args objectAtIndex:0];
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

-(NSNumber*) getAllowDuplicates:(NSDictionary *)obj
{
    NSNumber* allowDuplicates = [obj valueForKey:keyAllowDuplicates];

    if (allowDuplicates == nil)
    {
        return [NSNumber numberWithBool:NO];
    }

    if (![allowDuplicates isKindOfClass:[NSNumber class]])
    {
        return [NSNumber numberWithBool:NO];
    }

    return allowDuplicates;
}

-(NSNumber*) getStatusReceiver:(NSDictionary *)obj
{
    NSNumber* checkStatusReceiver = [obj valueForKey:keyStatusReceiver];

    if (checkStatusReceiver == nil)
    {
        return [NSNumber numberWithBool:YES];
    }

    if (![checkStatusReceiver isKindOfClass:[NSNumber class]])
    {
        return [NSNumber numberWithBool:YES];
    }

    return checkStatusReceiver;
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

-(CBService*) getService:(NSDictionary *)obj forPeripheral:(CBPeripheral*)peripheral
{
    if (peripheral.services == nil)
    {
        return nil;
    }

    NSString* uuidString = [obj valueForKey:keyService];

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

    for (CBService* item in peripheral.services)
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

    NSString* uuidString = [obj valueForKey:keyCharacteristic];

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

    NSString* uuidString = [obj valueForKey:keyDescriptor];

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
