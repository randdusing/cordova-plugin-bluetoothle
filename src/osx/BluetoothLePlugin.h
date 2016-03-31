#import <Cordova/CDV.h>
#import <CoreBluetooth/CoreBluetooth.h>

@interface BluetoothLePlugin : CDVPlugin <CBCentralManagerDelegate, CBPeripheralDelegate, CBPeripheralManagerDelegate> {
  CBCentralManager *centralManager;
  NSNumber* statusReceiver;
  NSString* initCallback;
  NSString* scanCallback;
  NSMutableDictionary* connections;

  CBPeripheralManager* peripheralManager;
  NSString* initPeripheralCallback;
  NSString* addServiceCallback;
  NSString* advertisingCallback;
  int requestId;
  NSMutableDictionary* requestsHash;
  NSMutableDictionary* servicesHash;
}

- (void)initialize:(CDVInvokedUrlCommand *)command;
- (void)enable:(CDVInvokedUrlCommand *)command;
- (void)disable:(CDVInvokedUrlCommand *)command;
- (void)startScan:(CDVInvokedUrlCommand *)command;
- (void)stopScan:(CDVInvokedUrlCommand *)command;
- (void)retrieveConnected:(CDVInvokedUrlCommand *)command;
- (void)connect:(CDVInvokedUrlCommand *)command;
- (void)reconnect:(CDVInvokedUrlCommand *)command;
- (void)disconnect:(CDVInvokedUrlCommand *)command;
- (void)close:(CDVInvokedUrlCommand *)command;
- (void)discover:(CDVInvokedUrlCommand *)command;
- (void)services:(CDVInvokedUrlCommand *)command;
- (void)characteristics:(CDVInvokedUrlCommand *)command;
- (void)descriptors:(CDVInvokedUrlCommand *)command;
- (void)read:(CDVInvokedUrlCommand *)command;
- (void)subscribe:(CDVInvokedUrlCommand *)command;
- (void)unsubscribe:(CDVInvokedUrlCommand *)command;
- (void)write:(CDVInvokedUrlCommand *)command;
- (void)writeQ:(CDVInvokedUrlCommand *)command;
- (void)readDescriptor:(CDVInvokedUrlCommand *)command;
- (void)writeDescriptor:(CDVInvokedUrlCommand *)command;
- (void)rssi:(CDVInvokedUrlCommand *)command;
- (void)mtu:(CDVInvokedUrlCommand *)command;
- (void)requestConnectionPriority:(CDVInvokedUrlCommand *)command;
- (void)isInitialized:(CDVInvokedUrlCommand *)command;
- (void)isEnabled:(CDVInvokedUrlCommand *)command;
- (void)isScanning:(CDVInvokedUrlCommand *)command;
- (void)wasConnected:(CDVInvokedUrlCommand *)command;
- (void)isConnected:(CDVInvokedUrlCommand *)command;
- (void)isDiscovered:(CDVInvokedUrlCommand *)command;
- (void)hasPermission:(CDVInvokedUrlCommand *)command;
- (void)requestPermission:(CDVInvokedUrlCommand *)command;
- (void)isLocationEnabled:(CDVInvokedUrlCommand *)command;
- (void)requestLocation:(CDVInvokedUrlCommand *)command;

- (void)initializePeripheral:(CDVInvokedUrlCommand *)command;
- (void)addService:(CDVInvokedUrlCommand *)command;
- (void)removeService:(CDVInvokedUrlCommand *)command;
- (void)removeAllServices:(CDVInvokedUrlCommand *)command;
- (void)startAdvertising:(CDVInvokedUrlCommand *)command;
- (void)stopAdvertising:(CDVInvokedUrlCommand *)command;
- (void)isAdvertising:(CDVInvokedUrlCommand *)command;
- (void)respond:(CDVInvokedUrlCommand *)command;
- (void)notify:(CDVInvokedUrlCommand *)command;

@end
