var WindowsBluetooth = Windows.Devices.Bluetooth;
var gatt = WindowsBluetooth.GenericAttributeProfile;
var WindowsDeviceInfo = Windows.Devices.Enumeration.DeviceInformation;
var wsc = Windows.Security.Cryptography;
var BluetoothUuidHelper = WindowsBluetooth.BluetoothUuidHelper;
var BluetoothConnectionStatus = WindowsBluetooth.BluetoothConnectionStatus;
var DeviceWatcherStatus = Windows.Devices.Enumeration.DeviceWatcherStatus;

var WATCHER, scanCallback;

var initialized = false;
var cachedServices = [];

var NAME_KEY = "System.ItemNameDisplay";
var RSSI_KEY = "System.Devices.Aep.SignalStrength";
var CONTAINER_ID_KEY = "System.Devices.Aep.DeviceAddress";
var IS_CONNECTABLE_KEY = "System.Devices.Aep.Bluetooth.Le.IsConnectable";
var PROPERTY_COLLECTION = [NAME_KEY, RSSI_KEY, CONTAINER_ID_KEY, IS_CONNECTABLE_KEY];

module.exports = {

  initialize: function (successCallback, errorCallback, params) {

    // If Windows.Devices.Radios namespace is available we will try to use it for
    // initialization. Otherwise fall back to default behaviour which is to
    // search for all Gatt-enabled devices around. See randdusing/cordova-plugin-bluetoothle/#284
    if (Windows.Devices.Radios) {
      return module.exports.initialize2(successCallback, errorCallback, params);
    }

    var selector = "System.Devices.InterfaceClassGuid:=\"{6E3BB679-4372-40C8-9EAA-4509DF260CD8}\" AND System.Devices.InterfaceEnabled:=System.StructuredQueryType.Boolean#True";
    WindowsDeviceInfo.findAllAsync(selector, null).then(function (devices) {
      if (devices.length > 0) {
        initialized = true;
        successCallback({ status: "enabled" });
      } else {
        if (params && params.length > 0 && params[0].request) {
          try {
            Windows.UI.ApplicationSettings.SettingsPane.show();
          } catch (ex) {
            Windows.System.Launcher.launchUriAsync(Windows.Foundation.Uri("ms-settings-bluetooth:"));
          }
        }
        errorCallback({ error: "initialize", message: "No BLE devices found." });
      }
    }, function (error) {
      if (params && params.length > 0 && params[0].request) {
        try {
          Windows.UI.ApplicationSettings.SettingsPane.show();
        } catch (ex) {
          Windows.System.Launcher.launchUriAsync(Windows.Foundation.Uri("ms-settings-bluetooth:"));
        }
      }
      errorCallback({ error: "initialize", message: error.message });
    });
  },

  initialize2: function (successCallback, errorCallback, params) {

    var Radios = Windows.Devices.Radios;

    var request = params[0] ? !!params[0].request : false;
    var statusReceiver = params[0] ? !!params[0].statusReceiver : false;

    // If statusReceiver option is specified we would report state multiple
    // times so we need to instruct cordova not to dispose callbacks
    var callbackOptions = statusReceiver ? { keepCallback: true } : {};

    function reportAdapterState(e) {
      var state;
      switch (e.target.state) {
        case Radios.RadioState.on:
          initialized = true;
          state = 'enabled';
          break;
        default:
          initialized = false;
          state = 'disabled';
      }

      successCallback({ status: state }, callbackOptions);
    }

    Radios.Radio.getRadiosAsync()
    .then(function (radios) {
      // There is a very small chance that there are more than one bluetooth
      // radio device is available so we'll just pick the first one from the list
      var radio = radios.filter(function (radio) {
        return radio.kind === Radios.RadioKind.bluetooth;
      })[0];

      if (!radio) {
        throw { error: "initialize", message: "No bluetooth radios available on device" };
      }

      if (statusReceiver) {
        radio.addEventListener('statechanged', reportAdapterState);
      }

      reportAdapterState({ target: radio });

      if (radio.state !== Radios.RadioState.on && request) {
        // radio.setStateAsync(Windows.Devices.Radios.RadioState.on) doesn't
        // work somehow so try to invoke settings in an old way.
        Windows.System.Launcher.launchUriAsync(Windows.Foundation.Uri("ms-settings-bluetooth:"));
      }
    })
    .done(null, errorCallback);
  },

  retrieveConnected: function (successCallback, errorCallback, params) {
    if (!initialized) {
      errorCallback({ error: "retrieveConnected", message: "Not initialized." });
      return;
    }

    var deviceIdsFound = [];
    var result = [];
    var selector = "System.Devices.InterfaceClassGuid:=\"{6E3BB679-4372-40C8-9EAA-4509DF260CD8}\" AND System.Devices.InterfaceEnabled:=System.StructuredQueryType.Boolean#True";
    if (params[0].services && params[0].services.length > 0) {
      for (var i = 0; i < params[0].services.length; i++) {
        var uuid = params[0].services[i];
        if (uuid.length === 4) {
            uuid = "0000" + uuid + "-0000-1000-8000-00805F9B34FB";
        }
        selector += (i === 0) ? " AND ( " : " OR ";
        selector += "System.DeviceInterface.Bluetooth.ServiceGuid:=\"{" + uuid + "}\"";
      }
      selector += " )";
    }
    WindowsDeviceInfo.findAllAsync(selector, ["System.Devices.ContainerId"]).then(function (devices) {
      for (var i = 0; i < devices.length; i++) {
        var deviceId = devices[i].properties["System.Devices.ContainerId"];
        var deviceName = devices[i].name;
        if (deviceIdsFound.indexOf(deviceId) === -1) {
          deviceIdsFound.push(deviceId);
          result.push({ name: deviceName, address: deviceId });
        }
      }
      successCallback(result);
    }, function (error) {
      errorCallback({ error: "retrieveConnected", message: error.message });
    });
  },

  startScan: function (successCallback, errorCallback, params) {

    if (!WATCHER) {
      // watch BLE devices using device watcher.
      var selector = WindowsBluetooth.BluetoothLEDevice.getDeviceSelectorFromConnectionStatus(false);
      if (params && params[0] && params[0].isConnectable) {
        selector += ' AND System.Devices.Aep.Bluetooth.Le.IsConnectable:=System.StructuredQueryType.Boolean#True';
      }
      WATCHER = WindowsDeviceInfo.createWatcher(selector, PROPERTY_COLLECTION);
    }

    if (WATCHER.status !== DeviceWatcherStatus.started &&
      WATCHER.status !== DeviceWatcherStatus.created &&
      WATCHER.status !== DeviceWatcherStatus.stopped &&
      WATCHER.status !== DeviceWatcherStatus.aborted) {

      errorCallback({ error: "startScan", message: 'Scan already in progress' });
      return;
    }

    scanCallback = function (obj) {

      if (obj.type !== 'added')
        // We're not interested in device characteristics updates
        // unless continuousScan === true (which is not yet supported)
        return;

      var device = obj.detail[0];

      // TODO: Add "advertisement" property - it is not available directly
      // but probably could be obtained using GattDeviceService
      var deviceAddress = device.properties.hasKey(CONTAINER_ID_KEY) && device.properties.lookup(CONTAINER_ID_KEY).toUpperCase();
      // Put device into cache to be able to get it faster in 'connect'

      var deviceInfo = {
        status: 'scanResult',
        rssi: device.properties.hasKey(RSSI_KEY) && device.properties.lookup(RSSI_KEY),
        name: device.properties.hasKey(NAME_KEY) && device.properties.lookup(NAME_KEY),
        address: deviceAddress
      };

      successCallback(deviceInfo, { keepCallback: true });
    };

    WATCHER.addEventListener("added", scanCallback, false);
    // Although we're not interested in device characteristics updated we still need to attach
    // listener to 'updated' event to catch devices that could be found after the initial scan is completed
    WATCHER.addEventListener("updated", scanCallback, false);
    WATCHER.start();

    successCallback({ status: 'scanStarted' }, { keepCallback: true });
  },

  stopScan: function (successCallback, errorCallback) {
    var DeviceWatcherStatus = Windows.Devices.Enumeration.DeviceWatcherStatus;

    if (WATCHER && (WATCHER.status === DeviceWatcherStatus.started ||
      WATCHER.status === DeviceWatcherStatus.enumerationCompleted)) {

      WATCHER.stop();
      WATCHER.removeEventListener("added", scanCallback);
      WATCHER.removeEventListener("updated", scanCallback);

      successCallback({ status: "scanStopped" });
      return;
    }

    errorCallback({ error: "stopScan", message: "Scan is either not yet started or already stopped" });
  },

  connect: function (successCallback, errorCallback, params) {
    if (!initialized) {
      errorCallback({ error: "connect", message: "Not initialized." });
      return;
    }

    var address = params && params[0] && params[0].address;
    if (!address) {
      errorCallback({ error: "connect", message: "Device address is not specified" });
      return;
    }

    getDeviceByAddress(address)
    .then(function(bleDevice){
      if (bleDevice.connectionStatus === BluetoothConnectionStatus.connected) {
        return bleDevice;
      }
      //if we're not already connected, getting the services will cause a connection to happen
      return bleDevice.getGattServicesAsync(WindowsBluetooth.BluetoothCacheMode.uncached).then(function(){
        return bleDevice;
      });
    })
    .done(function (bleDevice) {
      var result = {
        name: bleDevice.deviceInformation.name,
        address: address,
        status: bleDevice.connectionStatus === BluetoothConnectionStatus.connected ? "connected" : "disconnected"
      };

      // Attach listener to device to report disconnected event
      bleDevice.addEventListener('connectionstatuschanged', function connectionStatusListener(e) {
        if (e.target.connectionStatus === BluetoothConnectionStatus.disconnected) {
          result.status = "disconnected";
          successCallback(result);
          bleDevice.removeEventListener('connectionstatuschanged', connectionStatusListener);
        }
      });
      // Need to use keepCallback to be able to report "disconnect" event
      // https://github.com/randdusing/cordova-plugin-bluetoothle#connect
      successCallback(result, { keepCallback: true });
    }, function (err) {
      errorCallback(err);
    });
  },

  close: function (successCallback, errorCallback, params) {
    if (!initialized) {
      errorCallback({ error: "close", message: "Not initialized." });
      return;
    }

    var deviceId;

    if (params && params.length > 0 && params[0].address) {
      deviceId = params[0].address;

      for (var i = 0; i < cachedServices.length;) {
        var service = cachedServices[i];
        if (service.deviceId === deviceId) {
          cachedServices.splice(i, 1);
          service.deviceService.close();
        } else {
          i++;
        }
      }
      getDeviceByAddress(deviceId).then(function(device){
        device.close();
        return device.deviceInformation.pairing.unpairAsync();
      }).done(function(result){
        successCallback({ address: deviceId, status: 'closed'});
      }, function(error){
        errorCallback({ error: "close", message: JSON.stringify(error)});
      });
    }

  },

  discover: function (successCallback, errorCallback, params) {
    if (!initialized) {
      errorCallback({error: "discover", message: "Not initialized."});
      return;
    }

    var address = params && params[0] && params[0].address;
    if (!address) {
      errorCallback({error: "connect", message: "Device address is not specified"});
      return;
    }

    getDeviceByAddress(address).then(function (device) {
      return device.getGattServicesAsync();
    }).then(function (gattDeviceServicesResult) {
      return WinJS.Promise.join(gattDeviceServicesResult.services.map(function (service) {
        return getCharacteristics(service);
      }));
    }).then(function (services) {
      successCallback({
        "status": "discovered",
        "services": services,
        "address": address,
        "name": device.name
      });
    }, function(error) {
      errorCallback({ error: "discover", message: error.message});
    });
  },

  services: function (successCallback, errorCallback, params) {
    if (!initialized) {
      errorCallback({ error: "services", message: "Not initialized." });
      return;
    }

    if (params && params.length > 0 && params[0].address) {
      var deviceId = params[0].address;
      var selector = "System.Devices.ContainerId:={" + deviceId + "} AND System.Devices.InterfaceClassGuid:=\"{6E3BB679-4372-40C8-9EAA-4509DF260CD8}\" AND System.Devices.InterfaceEnabled:=System.StructuredQueryType.Boolean#True";
      WindowsDeviceInfo.findAllAsync(selector, null).then(function (services) {
        if (services.length > 0) {
          var deviceName;
          var serviceIds = [];
          for (var i = 0; i < services.length; i++) {
            serviceIds.push(getUUID(services[i]));
            deviceName = services[i].name;
          }
          successCallback({ status: "services", services: serviceIds, name: deviceName, address: deviceId });
        } else {
          errorCallback({ error: "services", message: "Device not found." });
        }
      }, function (error) {
        errorCallback({ error: "services", message: error.message });
      });
    } else {
      errorCallback({ error: "services", message: "Invalid parameters." });
    }
  },

  characteristics: function (successCallback, errorCallback, params) {
    if (!initialized) {
      errorCallback({ error: "characteristics", message: "Not initialized." });
      return;
    }

    if (params && params.length > 0 && params[0].address && params[0].service) {
      var deviceId = params[0].address;
      var serviceId = params[0].service;

      getService(deviceId, serviceId).then(function (service) {
        var characteristicsResult = [];
        var i;
        if (service.getAllCharacteristics) { // Phone
          var characteristics = service.getAllCharacteristics();
          for (i = 0; i < characteristics.length; i++) {
            characteristicsResult.push(convertCharacteristic(characteristics[i]));
          }
        } else { // Client
          var serviceInfo = getServiceInfo(serviceId);
          if (serviceInfo) {
            for (i = 0; i < serviceInfo.characteristics.length; i++) {
              var characteristicInfo = serviceInfo.characteristics[i];
              var characteristic = service.getCharacteristics(BluetoothUuidHelper.fromShortId(characteristicInfo.uuid))[0];
              if (characteristic) {
                characteristicsResult.push(convertCharacteristic(characteristic));
              }
            }
          } else {
            errorCallback({ error: "characteristics", message: "Unknown service, add the service to getServiceInfos() and try again." });
          }
        }
        successCallback({ status: "characteristics", characteristics: characteristicsResult, name: service.name, service: serviceId, address: deviceId });
      }, function (error) {
        errorCallback({ error: "characteristics", message: error });
      });
    } else {
      errorCallback({ error: "characteristics", message: "Invalid parameters." });
    }
  },

  descriptors: function (successCallback, errorCallback, params) {
    if (!initialized) {
      errorCallback({ error: "descriptors", message: "Not initialized." });
      return;
    }

    if (params && params.length > 0 && params[0].address && params[0].service && params[0].characteristic) {
      var deviceId = params[0].address;
      var serviceId = params[0].service;
      var characteristicId = params[0].characteristic;

      getCharacteristic(deviceId, serviceId, characteristicId).then(function (characteristic, deviceName) {
        var descriptorIds = [];
        var descriptors;
        var i;
        if (characteristic.getAllDescriptors) { // Phone
          descriptors = characteristic.getAllDescriptors();
          for (i = 0; i < descriptors.length; i++) {
            descriptorIds.push(descriptors[i].uuid.substring(4, 8));
          }
        } else { // Client
          var characteristicInfo = getCharacteristicsInfo(serviceId, characteristicId);
          for (i = 0; i < characteristicInfo.descriptors.length; i++) {
            descriptors = characteristic.getDescriptors(BluetoothUuidHelper.fromShortId(characteristicInfo.descriptors[i]));
            for (var j = 0; j < descriptors.length; j++) {
              descriptorIds.push(descriptors[j].uuid.substring(4, 8));
            }
          }
        }
        successCallback({ status: "descriptors", descriptors: descriptorIds, characteristic: characteristicId, name: deviceName, service: serviceId, address: deviceId });
      }, function (error) {
        errorCallback({ error: "descriptors", message: error });
      });
    } else {
      errorCallback({ error: "descriptors", message: "Invalid parameters." });
    }
  },

  read: function (successCallback, errorCallback, params) {
    if (!initialized) {
      errorCallback({ error: "read", message: "Not initialized." });
      return;
    }

    if (params && params.length > 0 && params[0].address && params[0].service && params[0].characteristic) {
      var deviceId = params[0].address;
      var serviceId = params[0].service;
      var characteristicId = params[0].characteristic;

      getCharacteristic(deviceId, serviceId, characteristicId).then(function (characteristic, deviceName) {
        try {
          characteristic.readValueAsync(WindowsBluetooth.BluetoothCacheMode.uncached).done(function (result) {
            if (result.status === gatt.GattCommunicationStatus.success) {
              var value = wsc.CryptographicBuffer.encodeToBase64String(result.value);
              successCallback({ status: "read", value: value, characteristic: characteristicId, name: deviceName, service: serviceId, address: deviceId });
            } else {
              errorCallback({ error: "read", message: "Device unreachable." });
            }
          }, function (error) {
            errorCallback({ error: "read", message: error.message });
          });
        } catch (error) {
          errorCallback({ error: "read", message: error.message });
        }
      }, function (error) {
        errorCallback({ error: "read", message: error });
      });
    } else {
      errorCallback({ error: "read", message: "Invalid parameters." });
    }
  },

  subscribe: function (successCallback, errorCallback, params) {
    if (!initialized) {
      errorCallback({ error: "subscribe", message: "Not initialized." });
      return;
    }

    if (params && params.length > 0 && params[0].address && params[0].service && params[0].characteristic) {
      var deviceId = params[0].address;
      var serviceId = params[0].service;
      var characteristicId = params[0].characteristic;
      var isNotification = params[0].isNotification;

      getCharacteristic(deviceId, serviceId, characteristicId).then(function (characteristic, deviceName) {
        var convertedCharacteristic = convertCharacteristic(characteristic);
        var descriptorValue;
        if (isNotification === null) {
          isNotification = convertedCharacteristic.properties.notify === 'true';
        }

        if (isNotification || isNotification == null) {
          descriptorValue = gatt.GattClientCharacteristicConfigurationDescriptorValue.notify;
        } else {
          descriptorValue = gatt.GattClientCharacteristicConfigurationDescriptorValue.indicate;
        }
        characteristic.writeClientCharacteristicConfigurationDescriptorAsync(descriptorValue).done(function (result) {
          if (result === gatt.GattCommunicationStatus.success) {
            successCallback({ status: "subscribed", characteristic: characteristicId, name: deviceName, service: serviceId, address: deviceId }, { keepCallback: true });
            characteristic.onvaluechanged = function (result) {
              var value = wsc.CryptographicBuffer.encodeToBase64String(result.characteristicValue);
              successCallback({ status: "subscribedResult", value: value, characteristic: characteristicId, name: deviceName, service: serviceId, address: deviceId }, { keepCallback: true });
            };
          } else {
            errorCallback({ error: "subscribe", message: "Device unreachable." });
          }
        }, function (error) {
          errorCallback({ error: "subscribe", message: error.message });
        });
      }, function (error) {
        errorCallback({ error: "subscribe", message: error });
      });
    } else {
      errorCallback({ error: "subscribe", message: "Invalid parameters." });
    }
  },

  unsubscribe: function (successCallback, errorCallback, params) {
    if (!initialized) {
      errorCallback({ error: "unsubscribe", message: "Not initialized." });
      return;
    }

    if (params && params.length > 0 && params[0].address && params[0].service && params[0].characteristic) {
      var deviceId = params[0].address;
      var serviceId = params[0].service;
      var characteristicId = params[0].characteristic;

      getCharacteristic(deviceId, serviceId, characteristicId).then(function (characteristic, deviceName) {
        characteristic.onvaluechanged = null;
        characteristic.writeClientCharacteristicConfigurationDescriptorAsync(gatt.GattClientCharacteristicConfigurationDescriptorValue.none).done(function (result) {
          if (result === gatt.GattCommunicationStatus.success) {
            successCallback({ status: "unsubscribed", characteristic: characteristicId, name: deviceName, service: serviceId, address: deviceId });
          } else {
            errorCallback({ error: "unsubscribe", message: "Device unreachable." });
          }
        }, function (error) {
          errorCallback({ error: "unsubscribe", message: error.message });
        });
      }, function (error) {
        errorCallback({ error: "unsubscribe", message: error });
      });
    } else {
      errorCallback({ error: "unsubscribe", message: "Invalid parameters." });
    }
  },

  write: function (successCallback, errorCallback, params) {
    if (!initialized) {
      errorCallback({ error: "write", message: "Not initialized." });
      return;
    }

    if (params && params.length > 0 && params[0].address && params[0].service && params[0].characteristic && params[0].value) {
      var deviceId = params[0].address;
      var serviceId = params[0].service;
      var characteristicId = params[0].characteristic;
      var value = params[0].value;
      var writeOption;
      if (params[0].type !== undefined && params[0].type === "noResponse") {
          writeOption = gatt.GattWriteOption.writeWithoutResponse;
      } else {
          writeOption = gatt.GattWriteOption.writeWithResponse;
      }

      getCharacteristic(deviceId, serviceId, characteristicId).then(function (characteristic, deviceName) {
        var buffer = wsc.CryptographicBuffer.decodeFromBase64String(value);
        characteristic.writeValueAsync(buffer, writeOption).done(function (result) {
          if (result === gatt.GattCommunicationStatus.success) {
            successCallback({ status: "written", characteristic: characteristicId, name: deviceName, service: serviceId, address: deviceId });
          } else {
            errorCallback({ error: "write", message: "Device unreachable." });
          }
        }, function (error) {
          errorCallback({ error: "write", message: error.message });
        });
      }, function (error) {
        errorCallback({ error: "write", message: error });
      });
    } else {
      errorCallback({ error: "write", message: "Invalid parameters." });
    }
  },

  readDescriptor: function (successCallback, errorCallback, params) {
    if (!initialized) {
      errorCallback({ error: "readDescriptor", message: "Not initialized." });
      return;
    }

    if (params && params.length > 0 && params[0].address && params[0].service && params[0].characteristic && params[0].descriptor) {
      var deviceId = params[0].address;
      var serviceId = params[0].service;
      var characteristicId = params[0].characteristic;
      var descriptorId = params[0].descriptor;

      getDescriptor(deviceId, serviceId, characteristicId, descriptorId).then(function (descriptor, deviceName) {
        descriptor.readValueAsync(WindowsBluetooth.BluetoothCacheMode.uncached).done(function (result) {
          if (result.status === gatt.GattCommunicationStatus.success) {
            var value = wsc.CryptographicBuffer.encodeToBase64String(result.value);
            successCallback({ status: "readDescriptor", value: value, descriptor: descriptorId, characteristic: characteristicId, name: deviceName, service: serviceId, address: deviceId });
          } else {
            errorCallback({ error: "readDescriptor", message: "Device unreachable." });
          }
        }, function (error) {
          errorCallback({ error: "readDescriptor", message: error.message });
        });
      }, function (error) {
        errorCallback({ error: "readDescriptor", message: error });
      });
    } else {
      errorCallback({ error: "readDescriptor", message: "Invalid parameters." });
    }
  },

  writeDescriptor: function (successCallback, errorCallback, params) {
    if (!initialized) {
      errorCallback({ error: "writeDescriptor", message: "Not initialized." });
      return;
    }

    if (params && params.length > 0 && params[0].address && params[0].service && params[0].characteristic && params[0].descriptor && params[0].value) {
      var deviceId = params[0].address;
      var serviceId = params[0].service;
      var characteristicId = params[0].characteristic;
      var descriptorId = params[0].descriptor;
      var value = params[0].value;

      getDescriptor(deviceId, serviceId, characteristicId, descriptorId).then(function (descriptor, deviceName) {
        var buffer = wsc.CryptographicBuffer.decodeFromBase64String(value);
        descriptor.writeValueAsync(buffer).done(function (result) {
          if (result === gatt.GattCommunicationStatus.success) {
            successCallback({ status: "writeDescriptor", descriptor: descriptorId, characteristic: characteristicId, name: deviceName, service: serviceId, address: deviceId });
          } else {
            errorCallback({ error: "writeDescriptor", message: "Device unreachable." });
          }
        }, function (error) {
          errorCallback({ error: "writeDescriptor", message: error.message });
        });
      }, function (error) {
        errorCallback({ error: "writeDescriptor", message: error });
      });
    } else {
      errorCallback({ error: "writeDescriptor", message: "Invalid parameters." });
    }
  },

  isInitialized: function (successCallback, errorCallback, strInput) {
    successCallback({ isInitialized: initialized });
  },

  isConnected: function (successCallback, errorCallback, params) {
    if (!initialized) {
      errorCallback({ error: "isConnected", message: "Not initialized." });
      return;
    }

    var address = params && params[0] && params[0].address;
    if (!address) {
      errorCallback({ error: "isConnected", message: "Device address is not specified" });
      return;
    }

    getDeviceByAddress(address)
    .done(function (bleDevice) {
      if (bleDevice && typeof bleDevice.connectionStatus !== 'undefined') {
        successCallback({
          name: bleDevice.name,
          address: uint64ToAddress(bleDevice.bluetoothAddress),
          isConnected: bleDevice.connectionStatus === BluetoothConnectionStatus.connected
        });
      } else {
        errorCallback({error: "isConnected", message: "Device not found"});
      }
    }, function(error) {
      errorCallback(error);
    });
  },

  rssi: function (successCallback, errorCallback, params) {
    if (!initialized) {
      errorCallback({error: "rssi", message: "Not initialized."});
      return;
    }

    var address = params && params[0] && params[0].address;
    if (!address) {
      errorCallback({ error: "rssi", message: "Device address is not specified" });
      return;
    }

    //Ugh. Windows 10 likes to pretend that a device is connected even if it's off. Force it to find the device if it's on.
    watchForDevice(address, true).done(function(obj) {
      if (obj && obj.properties && obj.properties.hasKey(RSSI_KEY)) {
        var returnedObj = {
          "status": "rssi",
          "rssi": obj.properties.lookup(RSSI_KEY),
          "name": obj.name,
          "address": address
        };

        successCallback(returnedObj);
        return;
      }

      errorCallback({error: "rssi", message: "Invalid object returned from watcher: " + JSON.stringify(obj)});
    }, function(error) {
      errorCallback({ error: "rssi", message: error });
    });
  }
};

var androidActions = [
  'initialize',
  'enable',
  'getAdapterInfo',
  'disable',
  'startScan',
  'stopScan',
  'retrieveConnected',
  'bond',
  'unbond',
  'connect',
  'reconnect',
  'disconnect',
  'services',
  'characteristics',
  'descriptors',
  'close',
  'discover',
  'read',
  'subscribe',
  'unsubscribe',
  'write',
  'writeQ',
  'readDescriptor',
  'writeDescriptor',
  'rssi',
  'isInitialized',
  'isEnabled',
  'isScanning',
  'wasConnected',
  'isConnected',
  'isDiscovered',
  'isBonded',
  'requestConnectionPriority',
  'mtu',
  'hasPermission',
  'requestPermission',
  'isLocationEnabled',
  'requestLocation'
];

androidActions.forEach(function(key){
  if (typeof module.exports[key] !== 'undefined') {
    return;
  }

  module.exports[key] = function(successCallback, errorCallback, params) {
    var error = 'Function "' + key + '" is not implemented';
    errorCallback({error: key, message: error});
  };
});

function addressToUint64(deviceAddress) {
  if (typeof deviceAddress === 'string') {
    deviceAddress = parseInt('0x' + deviceAddress.replace(/:/g, ''));
  }
  return deviceAddress;
}

function uint64ToAddress(deviceId) {
  if (typeof deviceId === 'string') {
    return deviceId;
  }
  deviceId = deviceId.toString(16);
  return deviceId.match(/.{1,2}/g).join(':');
}

function createWatcherForAddress(deviceAddress, requireConnectable) {
  deviceAddress = addressToUint64(deviceAddress);
  var queryString = WindowsBluetooth.BluetoothLEDevice.getDeviceSelectorFromBluetoothAddress(deviceAddress);

  //Ugh. None of the "isConnected" properties actually work (their value never changes regardless if the device is on or off).
  //The "isConnectable" property is the best I could find that actually changed.
  if (requireConnectable) {
    queryString += 'AND System.Devices.Aep.Bluetooth.Le.IsConnectable:=System.StructuredQueryType.Boolean#True';
  }
  return WindowsDeviceInfo.createWatcher(queryString, PROPERTY_COLLECTION);
}

function findDeviceWithWatcher(deviceAddress) {
  return watchForDevice(deviceAddress).then(function(){
    return WindowsBluetooth.BluetoothLEDevice.fromBluetoothAddressAsync(deviceAddress);
  }).then(function(device){
    if (!device) {
      throw new Error('Unable to find device "' + deviceAddress + '"');
    }
    return device;
  });
}

function watchForDevice(deviceAddress, mustBeConnectable) {
  return new WinJS.Promise(function (resolve, reject) {
    var watcher = createWatcherForAddress(deviceAddress, mustBeConnectable);
    var wasFound = false;

    function watcherCallback(obj) {
      var address = obj.properties.hasKey(CONTAINER_ID_KEY) && obj.properties.lookup(CONTAINER_ID_KEY);
      //The address is returned as a mac address
      if (address && addressToUint64(address) === addressToUint64(deviceAddress)) {
        wasFound = true;
        watcher.stop();
        resolve(obj);
      }
    }
    ["added", "updated"].forEach(function(eventName) {
      watcher.addEventListener(eventName, watcherCallback, false);
    });

    watcher.addEventListener("enumerationcompleted", function(){
      if (!wasFound) {
        watcher.stop();
        reject('Unable to find device');
      }
    });
    watcher.start();
  });
}

function getDeviceByAddress(deviceAddress) {
  return WinJS.Promise.wrap(deviceAddress)
    .then(function (deviceAddress) {
      return addressToUint64(deviceAddress);
    })
    .then(function (deviceAddress) {
      return WindowsBluetooth.BluetoothLEDevice.fromBluetoothAddressAsync(deviceAddress);
    }).then(function(device){
      if (device) {
        return device;
      }
      return findDeviceWithWatcher(deviceAddress);
    });
}

function getService(deviceId, serviceId) {
  for (var i = 0; i < cachedServices.length; i++) {
    var service = cachedServices[i];
    if (service.deviceId === deviceId && service.serviceId === serviceId) {
      return WinJS.Promise.as(service.deviceService);
    }
  }

  if (serviceId.length === 4) {
    serviceId = BluetoothUuidHelper.fromShortId(parseInt("0x" + serviceId, 16));
  }

  return getDeviceByAddress(deviceId)
    .then(function (device) {
      return device.getGattServicesForUuidAsync(serviceId);
    }).then(function (servicesResult) {
      if (!servicesResult.services) {
        throw new Error("Device or service not found.");
      }

      return servicesResult.services;
    }).then(function (services) {
      if (services.length > 0) {
        return services[0];
      }
      throw new Error("Device or service not found.");
    }).then(function (deviceService) {
      if (deviceService) {
        cachedServices.push({deviceId: deviceId, serviceId: serviceId, deviceService: deviceService});
        return deviceService;
      }

      throw new Error("Error retrieving deviceService, check the app's permissions for this service (plugin.xml).");
    });
}

function getCharacteristic(deviceId, serviceId, characteristicId) {
  return getService(deviceId, serviceId).then(function (service) {
    if (characteristicId.length === 4) {
      characteristicId = BluetoothUuidHelper.fromShortId(parseInt("0x" + characteristicId, 16));
    }
    return service.getCharacteristicsForUuidAsync(characteristicId);
  }).then(function(characteristicsResult) {
    if (!characteristicsResult.characteristics) {
      throw new Error("Characteristic not found.");
    }
    return characteristicsResult.characteristics;
  }).then(function(characteristics) {
    if (characteristics.length > 0) {
      return characteristics[0];
    }
    throw new Error("Characteristic not found.");
  });
}

function getDescriptor(deviceId, serviceId, characteristicId, descriptorId) {
  return getCharacteristic(deviceId, serviceId, characteristicId).then(function (characteristic) {
    if (descriptorId.length === 4) {
      descriptorId = BluetoothUuidHelper.fromShortId(parseInt("0x" + descriptorId, 16));
    }
    return characteristic.getDescriptorsForUuidAsync(descriptorId);
  }).then(function(descriptorsResult) {
    if (!descriptorsResult.descriptors) {
      throw new Error("Descriptor not found.");
    }
    return descriptorsResult.descriptors;
  }).then(function(descriptors){
    if (descriptors.length > 0) {
      return descriptors[0];
    }
    throw new Error("Descriptor not found.");
  });
}

function convertCharacteristic(characteristic) {
  var char = { uuid: getUUID(characteristic), properties: {} };
  if (characteristic.characteristicProperties & 1) {
    char.properties.broadcast = "true";
  }
  if (characteristic.characteristicProperties & 2) {
    char.properties.read = "true";
  }
  if (characteristic.characteristicProperties & 4) {
    char.properties.writeWithoutResponse = "true";
  }
  if (characteristic.characteristicProperties & 8) {
    char.properties.write = "true";
  }
  if (characteristic.characteristicProperties & 16) {
    char.properties.notify = "true";
  }
  if (characteristic.characteristicProperties & 32) {
    char.properties.indicate = "true";
  }
  if (characteristic.characteristicProperties & 64) {
    char.properties.authenticatedSignedWrites = "true";
  }
  if (characteristic.characteristicProperties & 128) {
    char.properties.extendedProperties = "true";
  }
  if (characteristic.characteristicProperties & 256) {
    char.properties.reliableWrites = "true";
  }
  if (characteristic.characteristicProperties & 512) {
    char.properties.writableAuxilaries = "true";
  }
  return char;
}

function getServiceInfo(serviceId) {
  var uuid = parseInt("0x" + serviceId, 16);
  var serviceInfos = getServiceInfos();
  for (var i = 0; i < serviceInfos.length; i++) {
    var serviceInfo = serviceInfos[i];
    if (serviceInfo.uuid === uuid) {
      return serviceInfo;
    }
  }
}

function getCharacteristicsInfo(serviceId, characteristicId) {
  var uuid = parseInt("0x" + characteristicId, 16);
  var service = getServiceInfo(serviceId);
  for (var i = 0; i < service.characteristics.length; i++) {
    var characteristicInfo = service.characteristics[i];
    if (characteristicInfo.uuid === uuid) {
      return characteristicInfo;
    }
  }
}

function convertDescriptor(descriptor) {
  return {
    uuid: getUUID(descriptor)
  };
}

function convertService(service) {
  return {
    uuid: getUUID(service)
  };
}

function getDescriptors(characteristic) {
  return characteristic.getDescriptorsAsync().then(function (descriptorsResult) {
    return descriptorsResult.descriptors.map(function (descriptor) {
      return convertDescriptor(descriptor);
    });
  });
}

function getCharacteristics(service) {
  return service.getCharacteristicsAsync().then(function (characteristicsResults) {
    return characteristicsResults.characteristics;
  }).then(function (characteristics) {
    return WinJS.Promise.join(characteristics.map(function (characteristic) {
      return getDescriptors(characteristic).then(function (descriptors) {
        var converted = convertCharacteristic(characteristic);
        converted.descriptors = descriptors;
        return converted;
      });
    })).then(function (characteristics) {
      var converted = convertService(service);
      converted.characteristics = characteristics;
      return converted;
    });
  });
}

function getUUID(service) {
  var UuidRe = /([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})/i;
  var serviceId = UuidRe.exec(service.uuid)[1];
  var re = /0000([0-9a-f]{4})-0000-1000-8000-00805f9b34fb/;
  var shortUuidMatch = re.exec(serviceId);
  if (shortUuidMatch !== null) {
    serviceId = shortUuidMatch[1];
  }

  return serviceId.toUpperCase();
}

function getServiceInfos() {
  return [
   {
     uuid: 0x1811,
     characteristics: [
       { uuid: 0x2A47, descriptors: [] },
       { uuid: 0x2A46, descriptors: [0x2902, ] },
       { uuid: 0x2A48, descriptors: [] },
       { uuid: 0x2A45, descriptors: [0x2902, ] },
       { uuid: 0x2A44, descriptors: [] },
     ]
   },
  {
    uuid: 0x180F,
    characteristics: [
      { uuid: 0x2A19, descriptors: [0x2904, 0x2902, ] },
    ]
  },
  {
    uuid: 0x1810,
    characteristics: [
      { uuid: 0x2A35, descriptors: [0x2902, ] },
      { uuid: 0x2A36, descriptors: [0x2902, ] },
      { uuid: 0x2A49, descriptors: [] },
    ]
  },
  {
    uuid: 0x181B,
    characteristics: [
      { uuid: 0x2A9B, descriptors: [] },
      { uuid: 0x2A9C, descriptors: [0x2902, ] },
    ]
  },
  {
    uuid: 0x181E,
    characteristics: [
      { uuid: 0x2AA4, descriptors: [] },
      { uuid: 0x2AA5, descriptors: [] },
    ]
  },
  {
    uuid: 0x181F,
    characteristics: [
      { uuid: 0x2AA7, descriptors: [0x2902, ] },
      { uuid: 0x2AA8, descriptors: [] },
      { uuid: 0x2AA9, descriptors: [] },
      { uuid: 0x2AAA, descriptors: [] },
      { uuid: 0x2AAB, descriptors: [] },
      { uuid: 0x2A52, descriptors: [0x2902, ] },
      { uuid: 0x2AAC, descriptors: [0x2902, ] },
    ]
  },
  {
    uuid: 0x1805,
    characteristics: [
      { uuid: 0x2A2B, descriptors: [0x2902, ] },
      { uuid: 0x2A0F, descriptors: [] },
      { uuid: 0x2A14, descriptors: [] },
    ]
  },
  {
    uuid: 0x1818,
    characteristics: [
      { uuid: 0x2A63, descriptors: [0x2902, 0x2903, ] },
      { uuid: 0x2A65, descriptors: [] },
      { uuid: 0x2A5D, descriptors: [] },
      { uuid: 0x2A64, descriptors: [0x2902, ] },
      { uuid: 0x2A66, descriptors: [0x2902, ] },
    ]
  },
  {
    uuid: 0x1816,
    characteristics: [
      { uuid: 0x2A5B, descriptors: [0x2902, ] },
      { uuid: 0x2A5C, descriptors: [] },
      { uuid: 0x2A5D, descriptors: [] },
      { uuid: 0x2A55, descriptors: [0x2902, ] },
    ]
  },
  {
    uuid: 0x180A,
    characteristics: [
      { uuid: 0x2A29, descriptors: [] },
      { uuid: 0x2A24, descriptors: [] },
      { uuid: 0x2A25, descriptors: [] },
      { uuid: 0x2A27, descriptors: [] },
      { uuid: 0x2A26, descriptors: [] },
      { uuid: 0x2A28, descriptors: [] },
      { uuid: 0x2A23, descriptors: [] },
      { uuid: 0x2A50, descriptors: [] },
    ]
  },
  {
    uuid: 0x181A,
    characteristics: [
      { uuid: 0x2A7D, descriptors: [] },
      { uuid: 0x2A73, descriptors: [0x290C, 0x290D, 0x290B, 0x2901, 0x2906, ] },
      { uuid: 0x2A72, descriptors: [0x290C, 0x290D, 0x290B, 0x2901, 0x2906, ] },
      { uuid: 0x2A7B, descriptors: [0x290C, 0x290D, 0x290B, 0x2901, 0x2906, ] },
      { uuid: 0x2A6C, descriptors: [0x290C, 0x290D, 0x290B, 0x2901, 0x2906, ] },
      { uuid: 0x2A74, descriptors: [0x290C, 0x290D, 0x290B, 0x2901, 0x2906, ] },
      { uuid: 0x2A7A, descriptors: [0x290C, 0x290D, 0x290B, 0x2901, 0x2906, ] },
      { uuid: 0x2A6F, descriptors: [0x290C, 0x290D, 0x290B, 0x2901, 0x2906, ] },
      { uuid: 0x2A77, descriptors: [0x290C, 0x290D, 0x290B, 0x2901, 0x2906, ] },
      { uuid: 0x2A75, descriptors: [0x290C, 0x290D, 0x290B, 0x2901, 0x2906, ] },
      { uuid: 0x2A78, descriptors: [0x290C, 0x290D, 0x290B, 0x2901, 0x2906, ] },
      { uuid: 0x2A6D, descriptors: [0x290C, 0x290D, 0x290B, 0x2901, 0x2906, ] },
      { uuid: 0x2A6E, descriptors: [0x290C, 0x290D, 0x290B, 0x2901, 0x2906, ] },
      { uuid: 0x2A71, descriptors: [0x290C, 0x290D, 0x290B, 0x2901, 0x2906, ] },
      { uuid: 0x2A70, descriptors: [0x290C, 0x290D, 0x290B, 0x2901, 0x2906, ] },
      { uuid: 0x2A76, descriptors: [0x290C, 0x290D, 0x290B, 0x2901, 0x2906, ] },
      { uuid: 0x2A79, descriptors: [0x290C, 0x290D, 0x290B, 0x2901, 0x2906, ] },
      { uuid: 0x2AA3, descriptors: [0x290C, 0x290D, 0x290B, 0x2901, 0x2906, ] },
      { uuid: 0x2A2C, descriptors: [0x290C, 0x290D, 0x290B, 0x2901, 0x2906, ] },
      { uuid: 0x2AA0, descriptors: [0x290C, 0x290D, 0x290B, 0x2901, 0x2906, ] },
      { uuid: 0x2AA1, descriptors: [0x290C, 0x290D, 0x290B, 0x2901, 0x2906, ] },
    ]
  },
  {
    uuid: 0x1800,
    characteristics: [
      { uuid: 0x2A00, descriptors: [] },
      { uuid: 0x2A01, descriptors: [] },
      { uuid: 0x2A02, descriptors: [] },
      { uuid: 0x2A03, descriptors: [] },
      { uuid: 0x2A04, descriptors: [] },
    ]
  },
  {
    uuid: 0x1801,
    characteristics: [
      { uuid: 0x2A05, descriptors: [] },
    ]
  },
  {
    uuid: 0x1808,
    characteristics: [
      { uuid: 0x2A18, descriptors: [0x2902, ] },
      { uuid: 0x2A34, descriptors: [0x2902, ] },
      { uuid: 0x2A51, descriptors: [] },
      { uuid: 0x2A52, descriptors: [0x2902, ] },
    ]
  },
  {
    uuid: 0x1809,
    characteristics: [
      { uuid: 0x2A1C, descriptors: [0x2902, ] },
      { uuid: 0x2A1D, descriptors: [] },
      { uuid: 0x2A1E, descriptors: [0x2902, ] },
      { uuid: 0x2A21, descriptors: [0x2902, 0x2906, ] },
    ]
  },
  {
    uuid: 0x180D,
    characteristics: [
      { uuid: 0x2A37, descriptors: [0x2902, ] },
      { uuid: 0x2A38, descriptors: [] },
      { uuid: 0x2A39, descriptors: [] },
    ]
  },
  {
    uuid: 0x1812,
    characteristics: [
      { uuid: 0x2A4E, descriptors: [] },
      { uuid: 0x2A4D, descriptors: [0x2902, 0x2908, ] },
      { uuid: 0x2A4B, descriptors: [0x2907, ] },
      { uuid: 0x2A22, descriptors: [0x2902, ] },
      { uuid: 0x2A32, descriptors: [] },
      { uuid: 0x2A33, descriptors: [0x2902, ] },
      { uuid: 0x2A4A, descriptors: [] },
      { uuid: 0x2A4C, descriptors: [] },
    ]
  },
  {
    uuid: 0x1802,
    characteristics: [
      { uuid: 0x2A06, descriptors: [] },
    ]
  },
  {
    uuid: 0x1820,
    characteristics: [
    ]
  },
  {
    uuid: 0x1803,
    characteristics: [
      { uuid: 0x2A06, descriptors: [] },
    ]
  },
  {
    uuid: 0x1819,
    characteristics: [
      { uuid: 0x2A6A, descriptors: [] },
      { uuid: 0x2A67, descriptors: [0x2902, ] },
      { uuid: 0x2A69, descriptors: [] },
      { uuid: 0x2A6B, descriptors: [0x2902, ] },
      { uuid: 0x2A68, descriptors: [0x2902, ] },
    ]
  },
  {
    uuid: 0x1807,
    characteristics: [
      { uuid: 0x2A11, descriptors: [] },
    ]
  },
  {
    uuid: 0x180E,
    characteristics: [
      { uuid: 0x2A3F, descriptors: [0x2902, ] },
      { uuid: 0x2A41, descriptors: [0x2902, ] },
      { uuid: 0x2A40, descriptors: [] },
    ]
  },
  {
    uuid: 0x1806,
    characteristics: [
      { uuid: 0x2A16, descriptors: [] },
      { uuid: 0x2A17, descriptors: [] },
    ]
  },
  {
    uuid: 0x1814,
    characteristics: [
      { uuid: 0x2A53, descriptors: [0x2902, ] },
      { uuid: 0x2A54, descriptors: [] },
      { uuid: 0x2A5D, descriptors: [] },
      { uuid: 0x2A55, descriptors: [0x2902, ] },
    ]
  },
  {
    uuid: 0x1813,
    characteristics: [
      { uuid: 0x2A4F, descriptors: [] },
      { uuid: 0x2A31, descriptors: [0x2902, ] },
    ]
  },
  {
    uuid: 0x1804,
    characteristics: [
      { uuid: 0x2A07, descriptors: [] },
    ]
  },
  {
    uuid: 0x181C,
    characteristics: [
      { uuid: 0x2A8A, descriptors: [] },
      { uuid: 0x2A90, descriptors: [] },
      { uuid: 0x2A87, descriptors: [] },
      { uuid: 0x2A80, descriptors: [] },
      { uuid: 0x2A85, descriptors: [] },
      { uuid: 0x2A8C, descriptors: [] },
      { uuid: 0x2A98, descriptors: [] },
      { uuid: 0x2A8E, descriptors: [] },
      { uuid: 0x2A96, descriptors: [] },
      { uuid: 0x2A8D, descriptors: [] },
      { uuid: 0x2A92, descriptors: [] },
      { uuid: 0x2A91, descriptors: [] },
      { uuid: 0x2A7F, descriptors: [] },
      { uuid: 0x2A83, descriptors: [] },
      { uuid: 0x2A93, descriptors: [] },
      { uuid: 0x2A86, descriptors: [] },
      { uuid: 0x2A97, descriptors: [] },
      { uuid: 0x2A8F, descriptors: [] },
      { uuid: 0x2A88, descriptors: [] },
      { uuid: 0x2A89, descriptors: [] },
      { uuid: 0x2A7E, descriptors: [] },
      { uuid: 0x2A84, descriptors: [] },
      { uuid: 0x2A81, descriptors: [] },
      { uuid: 0x2A82, descriptors: [] },
      { uuid: 0x2A8B, descriptors: [] },
      { uuid: 0x2A94, descriptors: [] },
      { uuid: 0x2A95, descriptors: [] },
      { uuid: 0x2A99, descriptors: [0x2902, ] },
      { uuid: 0x2A9A, descriptors: [] },
      { uuid: 0x2A9F, descriptors: [0x2902, ] },
      { uuid: 0x2AA2, descriptors: [] },
    ]
  },
  {
    uuid: 0x181D,
    characteristics: [
      { uuid: 0x2A9E, descriptors: [] },
      { uuid: 0x2A9D, descriptors: [0x2902, ] },
    ]
  },
  // Medisana BS 430 Connect (Body Analysis Scale)
  {
    uuid: 0x78b2,
    characteristics: [
      { uuid: 0x8a20, descriptors: [] },
      { uuid: 0x8a21, descriptors: [0x2902, ] },
      { uuid: 0x8a22, descriptors: [0x2902, ] },
      { uuid: 0x8a81, descriptors: [] },
      { uuid: 0x8a82, descriptors: [0x2902, ] },
    ]
  }
  // ---------------------------------------------
  ];
}

require("cordova/exec/proxy").add("BluetoothLePlugin", module.exports);
