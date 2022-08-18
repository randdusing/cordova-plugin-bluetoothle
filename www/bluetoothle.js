"use strict";
var bluetoothleName = "BluetoothLePlugin";
var bluetoothle = {
  _newReorderer: function(successCallback) {
    let context = {
      callback: successCallback,
      onHold: {},
      nextExpected: 0,
    };
    return bluetoothle._reorderCallback.bind(context);
  },
  _reorderCallback: function(obj, sequence) {
    /**
     * If there is not a sequence number present, just pass the callback through
     * without reordering it.
     */
    if (sequence == null) {
      this.callback(obj);
      return;
    }

    if (sequence != this.nextExpected) console.warn("Received out of order: expected " + this.nextExpected +" got " + sequence);

    this.onHold[sequence] = obj;

    bluetoothle._tryDispatchInOrder.bind(this)();
  },
  _tryDispatchInOrder: function() {
    while (this.nextExpected in this.onHold) {
      try {
        let value = this.onHold[this.nextExpected];
        delete this.onHold[this.nextExpected];

        this.nextExpected += 1;

        this.callback(value);
      } catch (err) {
        console.error("Error in callback in Reorderer", err);
      }
    }
  },
  initialize: function(successCallback, params) {
    cordova.exec(successCallback, successCallback, bluetoothleName, "initialize", [params]);
  },
  enable: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "enable", []);
  },
  disable: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "disable", []);
  },
  getAdapterInfo: function(successCallback) {
    cordova.exec(successCallback, successCallback, bluetoothleName, "getAdapterInfo", []);
  },
  startScan: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "startScan", [params]);
  },
  stopScan: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "stopScan", []);
  },
  retrieveConnected: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "retrieveConnected", [params]);
  },
  bond: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "bond", [params]);
  },
  unbond: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "unbond", [params]);
  },
  connect: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "connect", [params]);
  },
  reconnect: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "reconnect", [params]);
  },
  disconnect: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "disconnect", [params]);
  },
  close: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "close", [params]);
  },
  discover: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "discover", [params]);
  },
  services: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "services", [params]);
  },
  characteristics: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "characteristics", [params]);
  },
  descriptors: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "descriptors", [params]);
  },
  read: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "read", [params]);
  },
  subscribe: function(successCallback, errorCallback, params) {
    cordova.exec(bluetoothle._newReorderer(successCallback), errorCallback, bluetoothleName, "subscribe", [params]);
  },
  unsubscribe: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "unsubscribe", [params]);
  },
  write: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "write", [params]);
  },
  writeQ: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "writeQ", [params]);
  },
  readDescriptor: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "readDescriptor", [params]);
  },
  writeDescriptor: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "writeDescriptor", [params]);
  },
  rssi: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "rssi", [params]);
  },
  mtu: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "mtu", [params]);
  },
  requestConnectionPriority: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "requestConnectionPriority", [params]);
  },
  isInitialized: function(successCallback) {
    cordova.exec(successCallback, successCallback, bluetoothleName, "isInitialized", []);
  },
  isEnabled: function(successCallback) {
    cordova.exec(successCallback, successCallback, bluetoothleName, "isEnabled", []);
  },
  isScanning: function(successCallback) {
    cordova.exec(successCallback, successCallback, bluetoothleName, "isScanning", []);
  },
  isBonded: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "isBonded", [params]);
  },
  wasConnected: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "wasConnected", [params]);
  },
  isConnected: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "isConnected", [params]);
  },
  isDiscovered: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "isDiscovered", [params]);
  },
  hasPermission: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "hasPermission", []);
  },
  requestPermission: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "requestPermission", []);
  },
  hasPermissionBtScan: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "hasPermissionBtScan", []);
  },
  requestPermissionBtScan: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "requestPermissionBtScan", []);
  },
  hasPermissionBtConnect: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "hasPermissionBtConnect", []);
  },
  requestPermissionBtConnect: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "requestPermissionBtConnect", []);
  },
  hasPermissionBtAdvertise: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "hasPermissionBtAdvertise", []);
  },
  requestPermissionBtAdvertise: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "requestPermissionBtAdvertise", []);
  },
  isLocationEnabled: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "isLocationEnabled", []);
  },
  requestLocation: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "requestLocation", []);
  },
  retrievePeripheralsByAddress: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "retrievePeripheralsByAddress", [params])
  },
  initializePeripheral: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "initializePeripheral", [params]);
  },
  addService: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "addService", [params]);
  },
  removeService: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "removeService", [params]);
  },
  removeAllServices: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "removeAllServices", [params]);
  },
  startAdvertising: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "startAdvertising", [params]);
  },
  stopAdvertising: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "stopAdvertising", [params]);
  },
  isAdvertising: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "isAdvertising", []);
  },
  respond: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "respond", [params]);
  },
  notify: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "notify", [params]);
  },
  setPin: function(successCallback, errorCallback, params) {
    cordova.exec(successCallback, errorCallback, bluetoothleName, "setPin", [params]);
  },
  encodedStringToBytes: function(string) {
    var data = atob(string);
    var bytes = new Uint8Array(data.length);
    for (var i = 0; i < bytes.length; i++)
    {
      bytes[i] = data.charCodeAt(i);
    }
    return bytes;
  },
  bytesToEncodedString: function(bytes) {
    return btoa(String.fromCharCode.apply(null, bytes));
  },
  stringToBytes: function(string) {
    var bytes = new ArrayBuffer(string.length * 2);
    var bytesUint16 = new Uint16Array(bytes);
    for (var i = 0; i < string.length; i++) {
      bytesUint16[i] = string.charCodeAt(i);
    }
    return new Uint8Array(bytesUint16);
  },
  bytesToString: function(bytes) {
    return String.fromCharCode.apply(null, new Uint16Array(bytes));
  },
  encodeUnicode: function(str) {
    return btoa(encodeURIComponent(str).replace(/%([0-9A-F]{2})/g, function(match, p1) {
      return String.fromCharCode(parseInt(p1, 16))
    }))
  },
  decodeUnicode: function(str) {
    // Going backwards: from byte stream, to percent-encoding, to original string.
    return decodeURIComponent(atob(str).split('').map(function(c) {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
  },
  bytesToHex: function(bytes) {
    var string = [];
    for (var i = 0; i < bytes.length; i++) {
      string.push("0x" + ("0"+(bytes[i].toString(16))).substr(-2).toUpperCase());
    }
    return string.join(" ");
  },
  SCAN_MODE_OPPORTUNISTIC: -1,
  SCAN_MODE_LOW_POWER: 0,
  SCAN_MODE_BALANCED: 1,
  SCAN_MODE_LOW_LATENCY: 2,
  MATCH_NUM_ONE_ADVERTISEMENT: 1,
  MATCH_NUM_FEW_ADVERTISEMENT: 2,
  MATCH_NUM_MAX_ADVERTISEMENT: 3,
  MATCH_MODE_AGGRESSIVE: 1,
  MATCH_MODE_STICKY: 2,
  CALLBACK_TYPE_ALL_MATCHES: 1,
  CALLBACK_TYPE_FIRST_MATCH: 2,
  CALLBACK_TYPE_MATCH_LOST: 4,
}
module.exports = bluetoothle;
