var bluetoothle = {
  test: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "BluetoothLePlugin", "test", []); 
  }
}
module.exports = bluetoothle;
