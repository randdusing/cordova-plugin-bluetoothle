var bluetoothleName = "BluetoothLePlugin";
var bluetoothle = {
  init: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "init", []); 
  },
  startScan: function(successCallback, errorCallback, paramsObj) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "startScan", [paramsObj]); 
  },
  stopScan: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "stopScan", []);
  },
  connect: function(successCallback, errorCallback, paramsObj) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "connect", [paramsObj]);
  },
  disconnect: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "disconnect", []);
  },
  close: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "close", []);
  },
  discover: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "discover", []);
  },
  subscribe: function(successCallback, errorCallback, paramsObj) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "subscribe", [paramsObj]);
  },
  unsubscribe: function(successCallback, errorCallback, paramsObj) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "unsubscribe", [paramsObj]);
  },
  read: function(successCallback, errorCallback, paramsObj) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "read", [paramsObj]);
  },
  write: function(successCallback, errorCallback, paramsObj) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "write", [paramsObj]);
  },
  readDescriptor: function(successCallback, errorCallback, paramsObj) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "readDescriptor", [paramsObj]);
  },
  writeDescriptor: function(successCallback, errorCallback, paramsObj) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "writeDescriptor", [paramsObj]);
  },
  isConnected: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "isConnected", []);
  },
  isDiscovered: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "isDiscovered", []);
  }
}
module.exports = bluetoothle;