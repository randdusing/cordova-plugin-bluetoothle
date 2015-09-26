var bluetoothle;

var messages = "";

$( document ).ready(function() {
  if (document.URL.match(/^https?:/) || document.URL.match(/^file:/)) {
    init();
  } else {
    document.addEventListener("deviceready", init, false);
  }
});

function init() {
  $("a.initialize").on("click", initialize);

  $("a.enable").on("click", enable);

  $("a.disable").on("click", disable);

  $("a.startScan").on("click", startScan);

  $("a.stopScan").on("click", stopScan);

  $("a.retrieveConnected").on("click", retrieveConnected);

  $("a.isInitialized").on("click", isInitialized);

  $("a.isEnabled").on("click", isEnabled);

  $("a.isScanning").on("click", isScanning);

  $(document).on("click", "a.connect", function()
  {
    var address = getAddress($(this));

    connect(address);

    return false;
  });

  $(document).on("click", "a.reconnect", function()
  {
    var address = getAddress($(this));

    reconnect(address);

    return false;
  });

  $(document).on("click", "a.disconnect", function()
  {
    var address = getAddress($(this));

    disconnect(address);

    return false;
  });

  $(document).on("click", "a.close", function()
  {
    var address = getAddress($(this));

    close(address);

    return false;
  });

  $(document).on("click", "a.discover", function()
  {
    var address = getAddress($(this));

    discover(address);

    return false;
  });

  $(document).on("click", "a.services", function()
  {
    var address = getAddress($(this));

    services(address);

    return false;
  });

  $(document).on("click", "a.rssi", function()
  {
    var address = getAddress($(this));

    rssi(address);

    return false;
  });

  $(document).on("click", "a.mtu", function()
  {
    var address = getAddress($(this));

    mtu(address);

    return false;
  });

  $(document).on("click", "a.isConnected", function()
  {
    var address = getAddress($(this));

    isConnected(address);

    return false;
  });

  $(document).on("click", "a.isDiscovered", function()
  {
    var address = getAddress($(this));

    isDiscovered(address);

    return false;
  });

  $(document).on("click", "a.requestConnectionPriority", function() {
    var address = getAddress($(this));

    requestConnectionPriority(address);

    return false;
  });

  $(document).on("click", "a.characteristics", function()
  {
    var address = getAddress($(this));
    var serviceUuid = getServiceUuid($(this));

    characteristics(address, serviceUuid);

    return false;
  });

  $(document).on("click", "a.read", function()
  {
    var address = getAddress($(this));
    var serviceUuid = getServiceUuid($(this));
    var characteristicUuid = getCharacteristicUuid($(this));

    read(address, serviceUuid, characteristicUuid);

    return false;
  });

  $(document).on("click", "a.subscribe", function()
  {
    var address = getAddress($(this));
    var serviceUuid = getServiceUuid($(this));
    var characteristicUuid = getCharacteristicUuid($(this));

    subscribe(address, serviceUuid, characteristicUuid);

    return false;
  });

  $(document).on("click", "a.unsubscribe", function()
  {
    var address = getAddress($(this));
    var serviceUuid = getServiceUuid($(this));
    var characteristicUuid = getCharacteristicUuid($(this));

    unsubscribe(address, serviceUuid, characteristicUuid);

    return false;
  });

  $(document).on("click", "a.write", function()
  {
    var address = getAddress($(this));
    var serviceUuid = getServiceUuid($(this));
    var characteristicUuid = getCharacteristicUuid($(this));

    var bytes = new Uint8Array(1);
    bytes[0] = 0;
    var value = bluetoothle.bytesToEncodedString(bytes);

    write(address, serviceUuid, characteristicUuid, value);

    return false;
  });

  $(document).on("click", "a.descriptors", function()
  {
    var address = getAddress($(this));
    var serviceUuid = getServiceUuid($(this));
    var characteristicUuid = getCharacteristicUuid($(this));

    descriptors(address, serviceUuid, characteristicUuid);

    return false;
  });

  $(document).on("click", "a.readDescriptor", function()
  {
    var address = getAddress($(this));
    var serviceUuid = getServiceUuid($(this));
    var characteristicUuid = getCharacteristicUuid($(this));
    var descriptorUuid = getDescriptorUuid($(this));

    readDescriptor(address, serviceUuid, characteristicUuid, descriptorUuid);

    return false;
  });

  $(document).on("click", "a.writeDescriptor", function()
  {
    var address = getAddress($(this));
    var serviceUuid = getServiceUuid($(this));
    var characteristicUuid = getCharacteristicUuid($(this));
    var descriptorUuid = getDescriptorUuid($(this));

    var bytes = new Uint8Array(1);
    bytes[0] = 0;
    var value = bluetoothle.bytesToEncodedString(bytes);

    writeDescriptor(address, serviceUuid, characteristicUuid, descriptorUuid, value);

    return false;
  });

  $(document).on("click", ".toggle", function()
  {
    var $item = $(this);

    if ($item.hasClass("active"))
    {
      $item.removeClass("active");
    }
    else
    {
      $item.addClass("active");
    }
  });
}

function initialize() {
  var paramsObj = {request:true};

  logger("Initialize : " + JSON.stringify(paramsObj));

  bluetoothle.initialize(initializeSuccess, initializeError, paramsObj);

  return false;
}

function initializeSuccess(obj) {
  logger("Initialize Success : " + JSON.stringify(obj));

  if (obj.status == "enabled")
  {
    logger("Enabled");
  }
  else
  {
    logger("Unexpected Initialize Status");
  }
}

function initializeError(obj) {
  logger("Initialize Error : " + JSON.stringify(obj));
}

function enable() {
  logger("Enable");

  bluetoothle.enable(enableSuccess, enableError);

  return false;
}

function enableSuccess(obj) {
  logger("Enable Success : " + JSON.stringify(obj));

  if (obj.status == "enabled")
  {
    logger("Enabled");
  }
  else
  {
    logger("Unexpected Enable Status");
  }
}

function enableError(obj) {
  logger("Enable Error : " + JSON.stringify(obj));
}

function disable() {
  logger("Disable");

  bluetoothle.disable(disableSuccess, disableError);

  return false;
}

function disableSuccess(obj) {
  logger("Disable Success : " + JSON.stringify(obj));

  if (obj.status == "disabled")
  {
    logger("Disabled");
  }
  else
  {
    logger("Unexpected Disable Status");
  }
}

function disableError(obj) {
  logger("Disable Error : " + JSON.stringify(obj));
}

function startScan() {
  //TODO Disconnect / Close all addresses and empty

  var paramsObj = {serviceUuids:[], allowDuplicates: false};

  logger("Start Scan : " + JSON.stringify(paramsObj));

  bluetoothle.startScan(startScanSuccess, startScanError, paramsObj);

  return false;
}

function startScanSuccess(obj) {
  logger("Start Scan Success : " + JSON.stringify(obj));

  if (obj.status == "scanResult")
  {
    logger("Scan Result");

    addDevice(obj.address, obj.name);
  }
  else if (obj.status == "scanStarted")
  {
    logger("Scan Started");
  }
  else
  {
    logger("Unexpected Start Scan Status");
  }
}

function startScanError(obj) {
  logger("Start Scan Error : " + JSON.stringify(obj));
}

function stopScan() {
  logger("Stop Scan");

  bluetoothle.stopScan(stopScanSuccess, stopScanError);

  return false;
}

function stopScanSuccess(obj) {
  logger("Stop Scan Success : " + JSON.stringify(obj));

  if (obj.status == "scanStopped")
  {
    logger("Scan Stopped");
  }
  else
  {
    logger("Unexpected Stop Scan Status");
  }
}

function stopScanError(obj) {
  logger("Stop Scan Error : " + JSON.stringify(obj));
}

function retrieveConnected() {
  var paramsObj = {serviceUuids:["180D"]};

  logger("Retrieve Connected : " + JSON.stringify(paramsObj));

  bluetoothle.retrieveConnected(retrieveConnectedSuccess, retrieveConnectedError, paramsObj);

  return false;
}

function retrieveConnectedSuccess(obj) {
  logger("Retrieve Connected Success : " + JSON.stringify(obj));

  for (var i = 0; i < obj.length; i++)
  {
    var device = obj[i];
    addDevice(device.address, device.name);
  }
}

function retrieveConnectedError(obj) {
  logger("Retrieve Connected Error : " + JSON.stringify(obj));
}

function isInitialized() {
  logger("Is Initialized");

  bluetoothle.isInitialized(isInitializedSuccess);

  return false;
}

function isInitializedSuccess(obj) {
  logger("Is Initialized Success : " + JSON.stringify(obj));

  if (obj.isInitialized)
  {
    logger("Is Initialized : true");
  }
  else
  {
    logger("Is Initialized : false");
  }
}

function isEnabled() {
  logger("Is Enabled");

  bluetoothle.isEnabled(isEnabledSuccess);

  return false;
}

function isEnabledSuccess(obj) {
  logger("Is Enabled Success : " + JSON.stringify(obj));

  if (obj.isEnabled)
  {
    logger("Is Enabled : true");
  }
  else
  {
    logger("Is Enabled : false");
  }
}

function isScanning() {
  logger("Is Scanning");

  bluetoothle.isScanning(isScanningSuccess);

  return false;
}

function isScanningSuccess(obj) {
  logger("Is Scanning Success : " + JSON.stringify(obj));

  if (obj.isScanning)
  {
    logger("Is Scanning : true");
  }
  else
  {
    logger("Is Scanning : false");
  }
}

function connect(address) {
  var paramsObj = {address:address};

   logger("Connect : " + JSON.stringify(paramsObj));

  bluetoothle.connect(connectSuccess, connectError, paramsObj);

  return false;
}

function connectSuccess(obj) {
  logger("Connect Success : " + JSON.stringify(obj));

  if (obj.status == "connected")
  {
    logger("Connected");
  }
  else if (obj.status == "connecting")
  {
    logger("Connecting");
  }
  else
  {
    logger("Unexpected Connect Status");
  }
}

function connectError(obj) {
  logger("Connect Error : " + JSON.stringify(obj));
}

function reconnect(address) {
  var paramsObj = {address:address};

  logger("Reconnect : " + JSON.stringify(paramsObj));

  bluetoothle.reconnect(reconnectSuccess, reconnectError, paramsObj);

  return false;
}

function reconnectSuccess(obj) {
  logger("Reconnect Success : " + JSON.stringify(obj));

  if (obj.status == "connected")
  {
    logger("Connected");
  }
  else if (obj.status == "connecting")
  {
    logger("Connecting");
  }
  else
  {
    logger("Unexpected Reconnect Status");
  }
}

function reconnectError(obj) {
  logger("Reconnect Error : " + JSON.stringify(obj));
}

function disconnect(address) {
  var paramsObj = {address:address};

  logger("Disconnect : " + JSON.stringify(paramsObj));

  bluetoothle.disconnect(disconnectSuccess, disconnectError, paramsObj);

  return false;
}

function disconnectSuccess(obj) {
  logger("Disconnect Success : " + JSON.stringify(obj));

  if (obj.status == "disconnected")
  {
    logger("Disconnected");
  }
  else if (obj.status == "disconnecting")
  {
    logger("Disconnecting");
  }
  else
  {
    logger("Unexpected Disconnect Status");
  }
}

function disconnectError(obj) {
  logger("Disconnect Error : " + JSON.stringify(obj));
}

function close(address) {
  var paramsObj = {address:address};

  logger("Close : " + JSON.stringify(paramsObj));

  bluetoothle.close(closeSuccess, closeError, paramsObj);

  return false;
}

function closeSuccess(obj) {
  logger("Close Success : " + JSON.stringify(obj));

  if (obj.status == "closed")
  {
    logger("Closed");
  }
  else
  {
    logger("Unexpected Close Status");
  }
}

function closeError(obj) {
  logger("Close Error : " + JSON.stringify(obj));
}

function discover(address) {
  var paramsObj = {address:address};

  logger("Discover : " + JSON.stringify(paramsObj));

  bluetoothle.discover(discoverSuccess, discoverError, paramsObj);

  return false;
}

function discoverSuccess(obj) {
  logger("Discover Success : " + JSON.stringify(obj));

  if (obj.status == "discovered")
  {
    logger("Discovered");

    var address = obj.address;

    var services = obj.services;

    for (var i = 0; i < services.length; i++)
    {
      var service = services[i];

      addService(address, service.serviceUuid);

      var characteristics = service.characteristics;

      for (var j = 0; j < characteristics.length; j++)
      {
        var characteristic = characteristics[j];

        addCharacteristic(address, service.serviceUuid, characteristic.characteristicUuid);

        var descriptors = characteristic.descriptors;

        for (var k = 0; k < descriptors.length; k++)
        {
          var descriptor = descriptors[k];

          addDescriptor(address, service.serviceUuid, characteristic.characteristicUuid, descriptor.descriptorUuid);
        }
      }
    }
  }
  else
  {
    logger("Unexpected Discover Status");
  }
}

function discoverError(obj) {
  logger("Discover Error : " + JSON.stringify(obj));
}

function services(address) {
  var paramsObj = {address:address, serviceUuids:[]};

  logger("Services : " + JSON.stringify(paramsObj));

  bluetoothle.services(servicesSuccess, servicesError, paramsObj);

  return false;
}

function servicesSuccess(obj) {
  logger("Services Success : " + JSON.stringify(obj));

  if (obj.status == "services")
  {
    logger("Services");

    var serviceUuids = obj.serviceUuids;

    for (var i = 0; i < serviceUuids.length; i++)
    {
      addService(obj.address, serviceUuids[i]);
    }
  }
  else
  {
    logger("Unexpected Services Status");
  }
}

function servicesError(obj) {
  logger("Services Error : " + JSON.stringify(obj));
}

function rssi(address) {
  var paramsObj = {address:address};

  logger("RSSI : " + JSON.stringify(paramsObj));

  bluetoothle.rssi(rssiSuccess, rssiError, paramsObj);

  return false;
}

function rssiSuccess(obj) {
  logger("RSSI Success : " + JSON.stringify(obj));

  if (obj.status == "rssi")
  {
    logger("RSSI");
  }
  else
  {
    logger("Unexpected RSSI Status");
  }
}

function rssiError(obj) {
  logger("RSSI Error : " + JSON.stringify(obj));
}

function mtu(address) {
  var paramsObj = {address:address, mtu: 10};

  logger("MTU : " + JSON.stringify(paramsObj));

  bluetoothle.mtu(mtuSuccess, mtuError, paramsObj);

  return false;
}

function mtuSuccess(obj) {
  logger("MTU Success : " + JSON.stringify(obj));

  if (obj.status == "mtu")
  {
    logger("MTU");
  }
  else
  {
    logger("Unexpected MTU Status");
  }
}

function mtuError(obj) {
  logger("MTU Error : " + JSON.stringify(obj));
}

function isConnected(address) {
  var paramsObj = {address:address};

  logger("Is Connected : " + JSON.stringify(paramsObj));

  bluetoothle.isConnected(isConnectedSuccess, paramsObj);

  return false;
}

function isConnectedSuccess(obj) {
  logger("Is Connected Success : " + JSON.stringify(obj));

  if (obj.isConnected)
  {
    logger("Is Connected : true");
  }
  else
  {
    logger("Is Connected : false");
  }
}

function isDiscovered(address) {
  var paramsObj = {address:address};

  logger("Is Discovered : " + JSON.stringify(paramsObj));

  bluetoothle.isDiscovered(isDiscoveredSuccess, paramsObj);

  return false;
}

function isDiscoveredSuccess(obj) {
  logger("Is Discovered Success : " + JSON.stringify(obj));

  if (obj.isDiscovered)
  {
    logger("Is Discovered : true");
  }
  else
  {
    logger("Is Discovered : false");
  }
}

function requestConnectionPriority(address) {
  var paramsObj = {address:address, connectionPriority:"high"};

  logger("Request Connection Priority : " + JSON.stringify(paramsObj));

  bluetoothle.requestConnectionPriority(requestConnectionPrioritySuccess, requestConnectionPriorityError, paramsObj);

  return false;
}

function requestConnectionPrioritySuccess(obj) {
  logger("Request Connection Priority Success : " + JSON.stringify(obj));

  if (obj.status == "connectionPriorityRequested")
  {
    logger("ConnectionPriorityRequested");
  }
  else
  {
    logger("Unexpected Request Connection Priority Status");
  }
}

function requestConnectionPriorityError(obj) {
  logger("Request Connection Priority Error : " + JSON.stringify(obj));
}

function characteristics(address, serviceUuid) {
  var paramsObj = {address:address, serviceUuid:serviceUuid, characteristicUuids:[]};

  logger("Characteristics : " + JSON.stringify(paramsObj));

  bluetoothle.characteristics(characteristicsSuccess, characteristicsError, paramsObj);

  return false;
}

function characteristicsSuccess(obj) {
  logger("Characteristics Success : " + JSON.stringify(obj));

  if (obj.status == "characteristics")
  {
    logger("Characteristics");

    var characteristics = obj.characteristics;

    for (var i = 0; i < characteristics.length; i++)
    {
      addCharacteristic(obj.address, obj.serviceUuid, characteristics[i].characteristicUuid);
    }
  }
  else
  {
    logger("Unexpected Characteristics Status");
  }
}

function characteristicsError(obj) {
  logger("Characteristics Error : " + JSON.stringify(obj));
}

function descriptors(address, serviceUuid, characteristicUuid) {
  var paramsObj = {address:address, serviceUuid:serviceUuid, characteristicUuid:characteristicUuid};

  logger("Descriptors : " + JSON.stringify(paramsObj));

  bluetoothle.descriptors(descriptorsSuccess, descriptorsError, paramsObj);

  return false;
}

function descriptorsSuccess(obj) {
  logger("Descriptors Success : " + JSON.stringify(obj));

  if (obj.status == "descriptors")
  {
    logger("Descriptors");

    var descriptorUuids = obj.descriptorUuids;

    for (var i = 0; i < descriptorUuids.length; i++)
    {
      addDescriptor(obj.address, obj.serviceUuid, obj.characteristicUuid, descriptorUuids[i]);
    }
  }
  else
  {
    logger("Unexpected Descriptors Status");
  }
}

function descriptorsError(obj) {
  logger("Descriptors Error : " + JSON.stringify(obj));
}

function read(address, serviceUuid, characteristicUuid) {
  var paramsObj = {address:address, serviceUuid:serviceUuid, characteristicUuid:characteristicUuid};

  logger("Read : " + JSON.stringify(paramsObj));

  bluetoothle.read(readSuccess, readError, paramsObj);

  return false;
}

function readSuccess(obj) {
  logger("Read Success : " + JSON.stringify(obj));

  if (obj.status == "read")
  {
    /*var bytes = bluetoothle.encodedStringToBytes(obj.value);
    logger("Read : " + bytes[0]);*/

    logger("Read");
  }
  else
  {
    logger("Unexpected Read Status");
  }
}

function readError(obj) {
  logger("Read Error : " + JSON.stringify(obj));
}

function subscribe(address, serviceUuid, characteristicUuid) {
  var paramsObj = {address:address, serviceUuid:serviceUuid, characteristicUuid:characteristicUuid};

  logger("Subscribe : " + JSON.stringify(paramsObj));

  bluetoothle.subscribe(subscribeSuccess, subscribeError, paramsObj);

  return false;
}

function subscribeSuccess(obj) {
  logger("Subscribe Success : " + JSON.stringify(obj));

  if (obj.status == "subscribedResult")
  {
    logger("Subscribed Result");
  }
  else if (obj.status == "subscribed")
  {
    logger("Subscribed");
  }
  else
  {
    logger("Unexpected Subscribe Status");
  }
}

function subscribeError(obj) {
  logger("Subscribe Error : " + JSON.stringify(obj));
}

function unsubscribe(address, serviceUuid, characteristicUuid) {
  var paramsObj = {address:address, serviceUuid:serviceUuid, characteristicUuid:characteristicUuid};

  logger("Unsubscribe : " + JSON.stringify(paramsObj));

  bluetoothle.unsubscribe(unsubscribeSuccess, unsubscribeError, paramsObj);

  return false;
}

function unsubscribeSuccess(obj) {
  logger("Unsubscribe Success : " + JSON.stringify(obj));

  if (obj.status == "unsubscribed")
  {
    logger("Unsubscribed");
  }
  else
  {
    logger("Unexpected Unsubscribe Status");
  }
}

function unsubscribeError(obj) {
  logger("Unsubscribe Error : " + JSON.stringify(obj));
}

function write(address, serviceUuid, characteristicUuid, value) {
  var paramsObj = {address:address, serviceUuid:serviceUuid, characteristicUuid:characteristicUuid, value:value};

  logger("Write : " + JSON.stringify(paramsObj));

  bluetoothle.write(writeSuccess, writeError, paramsObj);

  return false;
}

function writeSuccess(obj) {
  logger("Write Success : " + JSON.stringify(obj));

  if (obj.status == "written")
  {
    logger("Written");
  }
  else
  {
    logger("Unexpected Write Status");
  }
}

function writeError(obj) {
  logger("Write Error : " + JSON.stringify(obj));
}

function readDescriptor(address, serviceUuid, characteristicUuid, descriptorUuid) {
  var paramsObj = {address:address, serviceUuid:serviceUuid, characteristicUuid:characteristicUuid, descriptorUuid:descriptorUuid};

  logger("Read Descriptor : " + JSON.stringify(paramsObj));

  bluetoothle.readDescriptor(readDescriptorSuccess, readDescriptorError, paramsObj);

  return false;
}

function readDescriptorSuccess(obj) {
  logger("Read Descriptor Success : " + JSON.stringify(obj));

  if (obj.status == "readDescriptor")
  {
    logger("Read Descriptor");
  }
  else
  {
    logger("Unexpected Read Descriptor Status");
  }
}

function readDescriptorError(obj) {
  logger("Read Descriptor Error : " + JSON.stringify(obj));
}

function writeDescriptor(address, serviceUuid, characteristicUuid, descriptorUuid, value) {
  var paramsObj = {address:address, serviceUuid:serviceUuid, characteristicUuid:characteristicUuid, descriptorUuid:descriptorUuid, value:value};

  logger("Write Descriptor : " + JSON.stringify(paramsObj));

  bluetoothle.writeDescriptor(writeDescriptorSuccess, writeDescriptorError, paramsObj);

  return false;
}

function writeDescriptorSuccess(obj) {
  logger("Write Descriptor Success : " + JSON.stringify(obj));

  if (obj.status == "writeDescriptor")
  {
    logger("Write Descriptor");
  }
  else
  {
    logger("Unexpected Write Descriptor Status");
  }
}

function writeDescriptorError(obj) {
  logger("Write Descriptor Error : " + JSON.stringify(obj));
}

function addDevice(address, name) {
  var $devices = $(".devices");

  var $check = $devices.find("li[data-address='{0}']".format(address));
  if ($check.length > 0)
  {
    return;
  }

  var template = $("#device").text().format(address, name);

  $devices.append(template);
}

function getAddress($item) {
  return $item.parents("li[data-address]").attr("data-address");
}

function addService(address, serviceUuid) {
  var $devices = $(".devices");

  var $services = $devices.find("li[data-address='{0}'] ul.services".format(address));

  var $check = $services.find("li[data-serviceUuid='{0}']".format(serviceUuid));
  if ($check.length > 0)
  {
    return;
  }

  var template = $("#service").text().format(serviceUuid);

  $services.append(template);
}

function getServiceUuid($item) {
  return $item.parents("li[data-serviceUuid]").attr("data-serviceUuid");
}

function addCharacteristic(address, serviceUuid, characteristicUuid) {
  var $devices = $(".devices");

  var $services = $devices.find("li[data-address='{0}'] ul.services".format(address));

  var $characteristics = $services.find("li[data-serviceUuid='{0}'] ul.characteristics".format(serviceUuid));

  var $check = $characteristics.find("li[data-characteristicUuid='{0}']".format(characteristicUuid));
  if ($check.length > 0)
  {
    return;
  }

  var template = $("#characteristic").text().format(characteristicUuid);

  $characteristics.append(template);
}

function getCharacteristicUuid($item) {
  return $item.parents("li[data-characteristicUuid]").attr("data-characteristicUuid");
}

function addDescriptor(address, serviceUuid, characteristicUuid, descriptorUuid) {
  var $devices = $(".devices");

  var $services = $devices.find("li[data-address='{0}'] ul.services".format(address));

  var $characteristics = $services.find("li[data-serviceUuid='{0}'] ul.characteristics".format(serviceUuid));

  var $descriptors = $characteristics.find("li[data-characteristicUuid='{0}'] ul.descriptors".format(characteristicUuid));

  var $check = $descriptors.find("li[data-descriptorUuid='{0}']".format(descriptorUuid));
  if ($check.length > 0)
  {
    return;
  }

  var template = $("#descriptor").text().format(descriptorUuid);

  $descriptors.append(template);
}

function getDescriptorUuid($item) {
  return $item.parents("li[data-descriptorUuid]").attr("data-descriptorUuid");
}

function logger(message) {
  console.log(message);

  messages += message + "\r\n";
}

String.prototype.format = function() {
  var args = arguments;
  return this.replace(/{(\d+)}/g, function(match, number)
  {
    return typeof args[number] != 'undefined' ? args[number] : match;
  });
};
