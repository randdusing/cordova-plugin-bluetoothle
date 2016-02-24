var bluetooth = Windows.Devices.Bluetooth;
var gatt = Windows.Devices.Bluetooth.GenericAttributeProfile;
var deviceInfo = Windows.Devices.Enumeration.DeviceInformation;
var wsc = Windows.Security.Cryptography;

var initialized = false;
var cachedServices = [];

module.exports = {

  initialize: function (successCallback, errorCallback, params) {
    var selector = "System.Devices.InterfaceClassGuid:=\"{6E3BB679-4372-40C8-9EAA-4509DF260CD8}\" AND System.Devices.InterfaceEnabled:=System.StructuredQueryType.Boolean#True";
    deviceInfo.findAllAsync(selector, null).then(function (devices) {
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

  retrieveConnected: function (successCallback, errorCallback, params) {
    if (!initialized) {
      errorCallback({ error: "retrieveConnected", message: "Not initialized." });
      return;
    }

    var services;
    var searchServices;
    var deviceIdsFound = [];
    var result = [];
    var selector = "System.Devices.InterfaceClassGuid:=\"{6E3BB679-4372-40C8-9EAA-4509DF260CD8}\" AND System.Devices.InterfaceEnabled:=System.StructuredQueryType.Boolean#True";
    if (params[0].services && params[0].services.length > 0) {
      for (var i = 0; i < params[0].services.length; i++) {
        var uuid = params[0].services[i];
        if (uuid.length == 4) {
            uuid = "0000" + uuid + "-0000-1000-8000-00805F9B34FB";
        }
        selector += (i == 0) ? " AND ( " : " OR ";
        selector += "System.DeviceInterface.Bluetooth.ServiceGuid:=\"{" + uuid + "}\"";
      }
      selector += " )";
    }
    deviceInfo.findAllAsync(selector, ["System.Devices.ContainerId"]).then(function (devices) {
      for (var i = 0; i < devices.length; i++) {
        var deviceId = devices[i].properties["System.Devices.ContainerId"];
        var deviceName = devices[i].name;
        if (deviceIdsFound.indexOf(deviceId) == -1) {
          deviceIdsFound.push(deviceId);
          result.push({ name: deviceName, address: deviceId });
        }
      }
      successCallback(result);
    }, function (error) {
      errorCallback({ error: "retrieveConnected", message: error.message });
    });
  },

  close: function (successCallback, errorCallback, params) {
    if (!initialized) {
      errorCallback({ error: "close", message: "Not initialized." });
      return;
    }

    if (params && params.length > 0 && params[0].address) {
      var deviceId = params[0].address;

      for (var i = 0; i < cachedServices.length;) {
        var service = cachedServices[i];
        if (service.deviceId == deviceId) {
          cachedServices.splice(i, 1);
          service.deviceService.close();
        } else {
          i++;
        }
      }
    }
  },

  services: function (successCallback, errorCallback, params) {
    if (!initialized) {
      errorCallback({ error: "services", message: "Not initialized." });
      return;
    }

    if (params && params.length > 0 && params[0].address) {
      var deviceId = params[0].address;
      var selector = "System.Devices.ContainerId:={" + deviceId + "} AND System.Devices.InterfaceClassGuid:=\"{6E3BB679-4372-40C8-9EAA-4509DF260CD8}\" AND System.Devices.InterfaceEnabled:=System.StructuredQueryType.Boolean#True";
      deviceInfo.findAllAsync(selector, null).then(function (services) {
        if (services.length > 0) {
          var deviceName;
          var serviceIds = [];
          for (var i = 0; i < services.length; i++) {
              var UuidRe = /\{([0-9a-f]{8}\-[0-9a-f]{4}\-[0-9a-f]{4}\-[0-9a-f]{4}\-[0-9a-f]{12})\}_/;
              var serviceId = UuidRe.exec(services[i].id)[1];
              var re = /0000([0-9a-f]{4})\-0000\-1000\-8000\-00805f9b34fb/;
              var shortUuidMatch = re.exec(serviceId);
              if (shortUuidMatch != null) {
                serviceId = shortUuidMatch[1];
              }
              serviceIds.push(serviceId);
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
        if (service.getAllCharacteristics) { // Phone
          var characteristics = service.getAllCharacteristics();
          for (var i = 0; i < characteristics.length; i++) {
            characteristicsResult.push(convertCharacteristic(characteristics[i]));
          }
        } else { // Client
          var serviceInfo = getServiceInfo(serviceId);
          if (serviceInfo) {
            for (var i = 0; i < serviceInfo.characteristics.length; i++) {
              var characteristicInfo = serviceInfo.characteristics[i];
              var characteristic = service.getCharacteristics(gatt.GattCharacteristic.convertShortIdToUuid(characteristicInfo.uuid))[0];
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
        if (characteristic.getAllDescriptors) { // Phone
          var descriptors = characteristic.getAllDescriptors();
          for (var i = 0; i < descriptors.length; i++) {
            descriptorIds.push(descriptors[i].uuid.substring(4, 8));
          }
        } else { // Client
          var characteristicInfo = getCharacteristicsInfo(serviceId, characteristicId);
          for (var i = 0; i < characteristicInfo.descriptors.length; i++) {
            var descriptors = characteristic.getDescriptors(gatt.GattDescriptor.convertShortIdToUuid(characteristicInfo.descriptors[i]));
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
        characteristic.readValueAsync(bluetooth.BluetoothCacheMode.uncached).done(function (result) {
          if (result.status == gatt.GattCommunicationStatus.success) {
            var value = wsc.CryptographicBuffer.encodeToBase64String(result.value);
            successCallback({ status: "read", value: value, characteristic: characteristicId, name: deviceName, service: serviceId, address: deviceId });
          } else {
            errorCallback({ error: "read", message: "Device unreachable." });
          }
        }, function (error) {
          errorCallback({ error: "read", message: error.message });
        });
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

    if (params && params.length > 0 && params[0].address && params[0].service && params[0].characteristic && params[0].isNotification != undefined) {
      var deviceId = params[0].address;
      var serviceId = params[0].service;
      var characteristicId = params[0].characteristic;
      var isNotification = params[0].isNotification;

      getCharacteristic(deviceId, serviceId, characteristicId).then(function (characteristic, deviceName) {
        var descriptorValue;
        if (isNotification || isNotification == null) {
          descriptorValue = gatt.GattClientCharacteristicConfigurationDescriptorValue.notify;
        } else {
          descriptorValue = gatt.GattClientCharacteristicConfigurationDescriptorValue.indicate;
        }
        characteristic.writeClientCharacteristicConfigurationDescriptorAsync(descriptorValue).done(function (result) {
          if (result == gatt.GattCommunicationStatus.success) {
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
          if (result == gatt.GattCommunicationStatus.success) {
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
      if (params[0].type !== undefined && params[0].type == "noResponse") {
          writeOption = gatt.GattWriteOption.writeWithoutResponse;
      } else {
          writeOption = gatt.GattWriteOption.writeWithResponse;
      }

      getCharacteristic(deviceId, serviceId, characteristicId).then(function (characteristic, deviceName) {
        var buffer = wsc.CryptographicBuffer.decodeFromBase64String(value);
        characteristic.writeValueAsync(buffer, writeOption).done(function (result) {
          if (result == gatt.GattCommunicationStatus.success) {
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
        descriptor.readValueAsync(bluetooth.BluetoothCacheMode.uncached).done(function (result) {
          if (result.status == gatt.GattCommunicationStatus.success) {
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
          if (result == gatt.GattCommunicationStatus.success) {
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

};

function getService(deviceId, serviceId) {
  return new WinJS.Promise(function (successCallback, errorCallback, progressDispatch) {
    for (var i = 0; i < cachedServices.length; i++) {
      var service = cachedServices[i];
      if (service.deviceId == deviceId && service.serviceId == serviceId) {
        successCallback(service.deviceService);
        return;
      }
    }
    if (serviceId.length == 4) {
        serviceId = "0000" + serviceId + "-0000-1000-8000-00805F9B34FB";
    }
    var selector = "System.Devices.ContainerId:={" + deviceId + "} AND System.DeviceInterface.Bluetooth.ServiceGuid:=\"{" + serviceId + "}\" AND System.Devices.InterfaceClassGuid:=\"{6E3BB679-4372-40C8-9EAA-4509DF260CD8}\" AND System.Devices.InterfaceEnabled:=System.StructuredQueryType.Boolean#True";
    deviceInfo.findAllAsync(selector, null).then(function (services) {
      if (services.length > 0) {
        gatt.GattDeviceService.fromIdAsync(services[0].id)
         .then(function (deviceService) {
           if (deviceService) {
             cachedServices.push({ deviceId: deviceId, serviceId: serviceId, deviceService: deviceService });
             successCallback(deviceService);
           } else {
             errorCallback("Error retrieving deviceService, check the app's permissions for this service (plugin.xml).");
           }
         }, function (error) {
           errorCallback(error);
         });
      } else {
        errorCallback("Device or service not found.");
      }
    }, function (error) {
      errorCallback(error);
    });
  });
}

function getCharacteristic(deviceId, serviceId, characteristicId) {
  return new WinJS.Promise(function (successCallback, errorCallback, progressDispatch) {
    getService(deviceId, serviceId).then(function (service) {
      var deviceName = service.name;
      if (characteristicId.length == 4) {
        characteristicId = gatt.GattCharacteristic.convertShortIdToUuid(parseInt("0x" + characteristicId, 16));
      }
      var characteristics = service.getCharacteristics(characteristicId);
      if (characteristics.length > 0) {
        successCallback(characteristics[0], deviceName);
      } else {
        errorCallback("Characteristic not found.");
      }
    }, function (error) {
      errorCallback(error);
    });
  });
}

function getDescriptor(deviceId, serviceId, characteristicId, descriptorId) {
  return new WinJS.Promise(function (successCallback, errorCallback, progressDispatch) {
    getCharacteristic(deviceId, serviceId, characteristicId).then(function (characteristic, deviceName) {
      if (descriptorId.length == 4) {
        descriptorId = gatt.GattDescriptor.convertShortIdToUuid(parseInt("0x" + descriptorId, 16))
      }
      var descriptors = characteristic.getDescriptors(descriptorId);
      if (descriptors.length > 0) {
        successCallback(descriptors[0], deviceName);
      } else {
        errorCallback("Descriptor not found.");
      }
    }, function (error) {
      errorCallback(error);
    });
  });
}

function convertCharacteristic(characteristic) {
  var char = { uuid: characteristic.uuid.substring(4, 8), properties: new Object() };
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
    if (serviceInfo.uuid == uuid) {
      return serviceInfo;
    }
  }
}

function getCharacteristicsInfo(serviceId, characteristicId) {
  var uuid = parseInt("0x" + characteristicId, 16);
  var service = getServiceInfo(serviceId);
  for (var i = 0; i < service.characteristics.length; i++) {
    var characteristicInfo = service.characteristics[i];
    if (characteristicInfo.uuid == uuid) {
      return characteristicInfo;
    }
  }
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
