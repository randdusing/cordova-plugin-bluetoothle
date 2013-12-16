var bluetoothle = {
  init: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "BluetoothLePlugin", "init", []); 
  }
  scan: function(successCallback, errorCallback, scanLimit) {
    cordova.exec(successCallback, errorCallback, "BluetoothLePlugin", "scan", [scanLimit]); 
  }
}
module.exports = bluetoothle;
