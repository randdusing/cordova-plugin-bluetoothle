var bluetoothleName = "BluetoothLePlugin";
var bluetoothle = {
  init: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "BluetoothLePlugin", "init", []); 
  },
  startScan: function(successCallback, errorCallback, uuids, scanLimit) {
    cordova.exec(successCallback, errorCallback, "BluetoothLePlugin", "startScan", [uuids, scanLimit]); 
  },
  stopScan: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "BluetoothLePlugin", "stopScan", []);
  },
  connect: function(successCallback, errorCallback, address, autoDiscover) {
    cordova.exec(successCallback, errorCallback, "BluetoothLePlugin", "connect", [address, autoDiscover]);
  },
  disconnect: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "BluetoothLePlugin", "disconnect", []);
  },
  close: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "BluetoothLePlugin", "close", []);
  },
  discover: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "BluetoothLePlugin", "discover", []);
  },
  subscribe: function(successCallback, errorCallback, serviceUuid, characteristicUuid) {
    cordova.exec(successCallback, errorCallback, "BluetoothLePlugin", "subscribe", [serviceUuid, characteristicUuid]);
  },
  unsubscribe: function(successCallback, errorCallback, serviceUuid, characteristicUuid) {
    cordova.exec(successCallback, errorCallback, "BluetoothLePlugin", "unsubscribe", [serviceUuid, characteristicUuid]);
  },
  read: function(successCallback, errorCallback, serviceUuid, characteristicUuid) {
    cordova.exec(successCallback, errorCallback, "BluetoothLePlugin", "read", [serviceUuid, characteristicUuid]);
  },
  write: function(successCallback, errorCallback, serviceUuid, characteristicUuid, value) {
    cordova.exec(successCallback, errorCallback, "BluetoothLePlugin", "write", [serviceUuid, characteristicUuid, value]);
  },
  characteristics: function(successCallback, errorCallback, serviceUuid) {
    cordova.exec(successCallback, errorCallback, "BluetoothLePlugin", "characteristics", [serviceUuid]);
  },
  isConnected: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "BluetoothLePlugin", "isConnected", []);
  },
  isDiscovered: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "BluetoothLePlugin", "isDiscovered", []);
  }
}
module.exports = bluetoothle;
