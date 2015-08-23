//Advertisement support
//Test two blocking devices
//WasConnected
//Name not reutnred on android after scan

var bluetoothle;

var jqmReady = $.Deferred();
var pgReady = $.Deferred();

var app =
{
  callback: null,
  initialize: function(callback)
  {
    this.callback = callback;

    //If testing on a desktop, automatically resolve PhoneGap
    if (document.URL.match(/^https?:/) || document.URL.match(/^file:/))
    {
      pgReady.resolve();
    }
    //Else if on a mobile device, add event listener for deviceready
    else
    {
      document.addEventListener("deviceready", onDeviceReady, false);
    }
  }
};

$(document).on("pagecreate", function()
{
  //Resolve jQuery Mobile
  jqmReady.resolve();
  $(document).off("pagecreate");
});

$.when(jqmReady, pgReady).then(function()
{
  //When PhoneGap and jQuery Mobile are resolved, start the app
  if (app.callback !== null)
  {
    app.callback();
  }
});

function onDeviceReady()
{
  //Resolve PhoneGap after deviceready has fired
  pgReady.resolve();
}

app.initialize(function()
{
  $("a.initialize").on("vclick", initialize);

  $("a.enable").on("vclick", enable);

  $("a.disable").on("vclick", disable);

  $("a.startScan").on("vclick", startScan);

  $("a.stopScan").on("vclick", stopScan);

  $("a.retrieveConnected").on("vclick", retrieveConnected);

  $("a.isInitialized").on("vclick", isInitialized);

  $("a.isEnabled").on("vclick", isEnabled);

  $("a.isScanning").on("vclick", isScanning);

  $(document).on("vclick", "a.connect", function()
  {
    var address = getAddress($(this));

    connect(address);

    return false;
  });

  $(document).on("vclick", "a.reconnect", function()
  {
    var address = getAddress($(this));

    reconnect(address);

    return false;
  });

  $(document).on("vclick", "a.disconnect", function()
  {
    var address = getAddress($(this));

    disconnect(address);

    return false;
  });

  $(document).on("vclick", "a.close", function()
  {
    var address = getAddress($(this));

    close(address);

    return false;
  });

  $(document).on("vclick", "a.discover", function()
  {
    var address = getAddress($(this));

    discover(address);

    return false;
  });

  $(document).on("vclick", "a.services", function()
  {
    var address = getAddress($(this));

    services(address);

    return false;
  });

  $(document).on("vclick", "a.rssi", function()
  {
    var address = getAddress($(this));

    rssi(address);

    return false;
  });

  $(document).on("vclick", "a.isConnected", function()
  {
    var address = getAddress($(this));

    isConnected(address);

    return false;
  });

  $(document).on("vclick", "a.isDiscovered", function()
  {
    var address = getAddress($(this));

    isDiscovered(address);

    return false;
  });

  $(document).on("vclick", "a.characteristics", function()
  {
    var address = getAddress($(this));
    var serviceUuid = getServiceUuid($(this));

    characteristics(address, serviceUuid);

    return false;
  });

  $(document).on("vclick", "a.read", function()
  {
    var address = getAddress($(this));
    var serviceUuid = getServiceUuid($(this));
    var characteristicUuid = getCharacteristicUuid($(this));

    read(address, serviceUuid, characteristicUuid);

    return false;
  });

  $(document).on("vclick", "a.subscribe", function()
  {
    var address = getAddress($(this));
    var serviceUuid = getServiceUuid($(this));
    var characteristicUuid = getCharacteristicUuid($(this));

    subscribe(address, serviceUuid, characteristicUuid);

    return false;
  });

  $(document).on("vclick", "a.unsubscribe", function()
  {
    var address = getAddress($(this));
    var serviceUuid = getServiceUuid($(this));
    var characteristicUuid = getCharacteristicUuid($(this));

    unsubscribe(address, serviceUuid, characteristicUuid);

    return false;
  });

  $(document).on("vclick", "a.write", function()
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

  $(document).on("vclick", "a.descriptors", function()
  {
    var address = getAddress($(this));
    var serviceUuid = getServiceUuid($(this));
    var characteristicUuid = getCharacteristicUuid($(this));

    descriptors(address, serviceUuid, characteristicUuid);

    return false;
  });

  $(document).on("vclick", "a.readDescriptor", function()
  {
    var address = getAddress($(this));
    var serviceUuid = getServiceUuid($(this));
    var characteristicUuid = getCharacteristicUuid($(this));
    var descriptorUuid = getDescriptorUuid($(this));

    readDescriptor(address, serviceUuid, characteristicUuid, descriptorUuid);

    return false;
  });

  $(document).on("vclick", "a.writeDescriptor", function()
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

  $(document).on("vclick", ".toggle", function()
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
});

function initialize()
{
  var paramsObj = {request:true};

  console.log("Initialize : " + JSON.stringify(paramsObj));

  bluetoothle.initialize(initializeSuccess, initializeError, paramsObj);

  return false;
}

function initializeSuccess(obj)
{
  console.log("Initialize Success : " + JSON.stringify(obj));

  if (obj.status == "enabled")
  {
    console.log("Enabled");
  }
  else
  {
    console.log("Unexpected Initialize Status");
  }
}

function initializeError(obj)
{
  console.log("Initialize Error : " + JSON.stringify(obj));
}

function enable()
{
  console.log("Enable");

  bluetoothle.enable(enableSuccess, enableError);

  return false;
}

function enableSuccess(obj)
{
  console.log("Enable Success : " + JSON.stringify(obj));

  if (obj.status == "enabled")
  {
    console.log("Enabled");
  }
  else
  {
    console.log("Unexpected Enable Status");
  }
}

function enableError(obj)
{
  console.log("Enable Error : " + JSON.stringify(obj));
}

function disable()
{
  console.log("Disable");

  bluetoothle.disable(disableSuccess, disableError);

  return false;
}

function disableSuccess(obj)
{
  console.log("Disable Success : " + JSON.stringify(obj));

  if (obj.status == "disabled")
  {
    console.log("Disabled");
  }
  else
  {
    console.log("Unexpected Disable Status");
  }
}

function disableError(obj)
{
  console.log("Disable Error : " + JSON.stringify(obj));
}

function startScan()
{
  //TODO Disconnect / Close all addresses and empty

  var paramsObj = {serviceUuids:[]};

  console.log("Start Scan : " + JSON.stringify(paramsObj));

  bluetoothle.startScan(startScanSuccess, startScanError, paramsObj);

  return false;
}

function startScanSuccess(obj)
{
  console.log("Start Scan Success : " + JSON.stringify(obj));

  if (obj.status == "scanResult")
  {
    console.log("Scan Result");

    addDevice(obj.address, obj.name);
  }
  else if (obj.status == "scanStarted")
  {
    console.log("Scan Started");
  }
  else
  {
    console.log("Unexpected Start Scan Status");
  }
}

function startScanError(obj)
{
  console.log("Start Scan Error : " + JSON.stringify(obj));
}

function stopScan()
{
  console.log("Stop Scan");

  bluetoothle.stopScan(stopScanSuccess, stopScanError);

  return false;
}

function stopScanSuccess(obj)
{
  console.log("Stop Scan Success : " + JSON.stringify(obj));

  if (obj.status == "scanStopped")
  {
    console.log("Scan Stopped");
  }
  else
  {
    console.log("Unexpected Stop Scan Status");
  }
}

function stopScanError(obj)
{
  console.log("Stop Scan Error : " + JSON.stringify(obj));
}

function retrieveConnected()
{
  //TODO Add to readme that at least one is required
  var paramsObj = {serviceUuids:["180D"]};

  console.log("Retrieve Connected : " + JSON.stringify(paramsObj));

  bluetoothle.retrieveConnected(retrieveConnectedSuccess, retrieveConnectedError, paramsObj);

  return false;
}

function retrieveConnectedSuccess(obj)
{
  console.log("Retrieve Connected Success : " + JSON.stringify(obj));

  for (var i = 0; i < obj.length; i++)
  {
    addDevice(obj[i]);
  }
}

function retrieveConnectedError(obj)
{
  console.log("Retrieve Connected Error : " + JSON.stringify(obj));
}

function isInitialized()
{
  console.log("Is Initialized");

  bluetoothle.isInitialized(isInitializedSuccess);

  return false;
}

function isInitializedSuccess(obj)
{
  console.log("Is Initialized Success : " + JSON.stringify(obj));

  if (obj.isInitialized)
  {
    console.log("Is Initialized : true");
  }
  else
  {
    console.log("Is Initialized : false");
  }
}

function isEnabled()
{
  console.log("Is Enabled");

  bluetoothle.isEnabled(isEnabledSuccess);

  return false;
}

function isEnabledSuccess(obj)
{
  console.log("Is Enabled Success : " + JSON.stringify(obj));

  if (obj.isEnabled)
  {
    console.log("Is Enabled : true");
  }
  else
  {
    console.log("Is Enabled : false");
  }
}

function isScanning()
{
  console.log("Is Scanning");

  bluetoothle.isScanning(isScanningSuccess);

  return false;
}

function isScanningSuccess(obj)
{
  console.log("Is Scanning Success : " + JSON.stringify(obj));

  if (obj.isScanning)
  {
    console.log("Is Scanning : true");
  }
  else
  {
    console.log("Is Scanning : false");
  }
}

function connect(address)
{
  var paramsObj = {address:address};

   console.log("Connect : " + JSON.stringify(paramsObj));

  bluetoothle.connect(connectSuccess, connectError, paramsObj);

  return false;
}

function connectSuccess(obj)
{
  console.log("Connect Success : " + JSON.stringify(obj));

  if (obj.status == "connected")
  {
    console.log("Connected");
  }
  else if (obj.status == "connecting")
  {
    console.log("Connecting");
  }
  else
  {
    console.log("Unexpected Connect Status");
  }
}

function connectError(obj)
{
  console.log("Connect Error : " + JSON.stringify(obj));
}

function reconnect(address)
{
  var paramsObj = {address:address};

  console.log("Reconnect : " + JSON.stringify(paramsObj));

  bluetoothle.reconnect(reconnectSuccess, reconnectError, paramsObj);

  return false;
}

function reconnectSuccess(obj)
{
  console.log("Reconnect Success : " + JSON.stringify(obj));

  if (obj.status == "connected")
  {
    console.log("Connected");
  }
  else if (obj.status == "connecting")
  {
    console.log("Connecting");
  }
  else
  {
    console.log("Unexpected Reconnect Status");
  }
}

function reconnectError(obj)
{
  console.log("Reconnect Error : " + JSON.stringify(obj));
}

function disconnect(address)
{
  var paramsObj = {address:address};

  console.log("Disconnect : " + JSON.stringify(paramsObj));

  bluetoothle.disconnect(disconnectSuccess, disconnectError, paramsObj);

  return false;
}

function disconnectSuccess(obj)
{
  console.log("Disconnect Success : " + JSON.stringify(obj));

  if (obj.status == "disconnected")
  {
    console.log("Disconnected");
  }
  else if (obj.status == "disconnecting")
  {
    console.log("Disconnecting");
  }
  else
  {
    console.log("Unexpected Disconnect Status");
  }
}

function disconnectError(obj)
{
  console.log("Disconnect Error : " + JSON.stringify(obj));
}

function close(address)
{
  var paramsObj = {address:address};

  console.log("Close : " + JSON.stringify(paramsObj));

  bluetoothle.close(closeSuccess, closeError, paramsObj);

  return false;
}

function closeSuccess(obj)
{
  console.log("Close Success : " + JSON.stringify(obj));

  if (obj.status == "closed")
  {
    console.log("Closed");
  }
  else
  {
    console.log("Unexpected Close Status");
  }
}

function closeError(obj)
{
  console.log("Close Error : " + JSON.stringify(obj));
}

function discover(address)
{
  var paramsObj = {address:address};

  console.log("Discover : " + JSON.stringify(paramsObj));

  bluetoothle.discover(discoverSuccess, discoverError, paramsObj);

  return false;
}

function discoverSuccess(obj)
{
  console.log("Discover Success : " + JSON.stringify(obj));

  if (obj.status == "discovered")
  {
    console.log("Discovered");

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
    console.log("Unexpected Discover Status");
  }
}

function discoverError(obj)
{
  console.log("Discover Error : " + JSON.stringify(obj));
}

function services(address)
{
  var paramsObj = {address:address, serviceUuids:[]};

  console.log("Services : " + JSON.stringify(paramsObj));

  bluetoothle.services(servicesSuccess, servicesError, paramsObj);

  return false;
}

function servicesSuccess(obj)
{
  console.log("Services Success : " + JSON.stringify(obj));

  if (obj.status == "services")
  {
    console.log("Services");

    var serviceUuids = obj.serviceUuids;

    for (var i = 0; i < serviceUuids.length; i++)
    {
      addService(obj.address, serviceUuids[i]);
    }
  }
  else
  {
    console.log("Unexpected Services Status");
  }
}

function servicesError(obj)
{
  console.log("Services Error : " + JSON.stringify(obj));
}

function rssi(address)
{
  var paramsObj = {address:address};

  console.log("RSSI : " + JSON.stringify(paramsObj));

  bluetoothle.rssi(rssiSuccess, rssiError, paramsObj);

  return false;
}

function rssiSuccess(obj)
{
  console.log("RSSI Success : " + JSON.stringify(obj));

  if (obj.status == "rssi")
  {
    console.log("RSSI");
  }
  else
  {
    console.log("Unexpected RSSI Status");
  }
}

function rssiError(obj)
{
  console.log("RSSI Error : " + JSON.stringify(obj));
}

function isConnected(address)
{
  var paramsObj = {address:address};

  console.log("Is Connected : " + JSON.stringify(paramsObj));

  bluetoothle.isConnected(isConnectedSuccess, paramsObj);

  return false;
}

function isConnectedSuccess(obj)
{
  console.log("Is Connected Success : " + JSON.stringify(obj));

  if (obj.isConnected)
  {
    console.log("Is Connected : true");
  }
  else
  {
    console.log("Is Connected : false");
  }
}

function isDiscovered(address)
{
  var paramsObj = {address:address};

  console.log("Is Discovered : " + JSON.stringify(paramsObj));

  bluetoothle.isDiscovered(isDiscoveredSuccess, paramsObj);

  return false;
}

function isDiscoveredSuccess(obj)
{
  console.log("Is Discovered Success : " + JSON.stringify(obj));

  if (obj.isDiscovered)
  {
    console.log("Is Discovered : true");
  }
  else
  {
    console.log("Is Discovered : false");
  }
}

function characteristics(address, serviceUuid)
{
  var paramsObj = {address:address, serviceUuid:serviceUuid, characteristicUuids:[]};

  console.log("Characteristics : " + JSON.stringify(paramsObj));

  bluetoothle.characteristics(characteristicsSuccess, characteristicsError, paramsObj);

  return false;
}

function characteristicsSuccess(obj)
{
  console.log("Characteristics Success : " + JSON.stringify(obj));

  if (obj.status == "characteristics")
  {
    console.log("Characteristics");

    var characteristics = obj.characteristics;

    for (var i = 0; i < characteristics.length; i++)
    {
      addCharacteristic(obj.address, obj.serviceUuid, characteristics[i].characteristicUuid);
    }
  }
  else
  {
    console.log("Unexpected Characteristics Status");
  }
}

function characteristicsError(obj)
{
  console.log("Characteristics Error : " + JSON.stringify(obj));
}

function descriptors(address, serviceUuid, characteristicUuid)
{
  var paramsObj = {address:address, serviceUuid:serviceUuid, characteristicUuid:characteristicUuid};

  console.log("Descriptors : " + JSON.stringify(paramsObj));

  bluetoothle.descriptors(descriptorsSuccess, descriptorsError, paramsObj);

  return false;
}

function descriptorsSuccess(obj)
{
  console.log("Descriptors Success : " + JSON.stringify(obj));

  if (obj.status == "descriptors")
  {
    console.log("Descriptors");

    var descriptorUuids = obj.descriptorUuids;

    for (var i = 0; i < descriptorUuids.length; i++)
    {
      addDescriptor(obj.address, obj.serviceUuid, obj.characteristicUuid, descriptorUuids[i]);
    }
  }
  else
  {
    console.log("Unexpected Descriptors Status");
  }
}

function descriptorsError(obj)
{
  console.log("Descriptors Error : " + JSON.stringify(obj));
}

function read(address, serviceUuid, characteristicUuid)
{
  var paramsObj = {address:address, serviceUuid:serviceUuid, characteristicUuid:characteristicUuid};

  console.log("Read : " + JSON.stringify(paramsObj));

  bluetoothle.read(readSuccess, readError, paramsObj);

  return false;
}

function readSuccess(obj)
{
  console.log("Read Success : " + JSON.stringify(obj));

  if (obj.status == "read")
  {
    /*var bytes = bluetoothle.encodedStringToBytes(obj.value);
    console.log("Read : " + bytes[0]);*/

    console.log("Read");
  }
  else
  {
    console.log("Unexpected Read Status");
  }
}

function readError(obj)
{
  console.log("Read Error : " + JSON.stringify(obj));
}

function subscribe(address, serviceUuid, characteristicUuid)
{
  var paramsObj = {address:address, serviceUuid:serviceUuid, characteristicUuid:characteristicUuid};

  console.log("Subscribe : " + JSON.stringify(paramsObj));

  bluetoothle.subscribe(subscribeSuccess, subscribeError, paramsObj);

  return false;
}

function subscribeSuccess(obj)
{
  console.log("Subscribe Success : " + JSON.stringify(obj));

  if (obj.status == "subscribedResult")
  {
    console.log("Subscribed Result");
  }
  else if (obj.status == "subscribed")
  {
    console.log("Subscribed");
  }
  else
  {
    console.log("Unexpected Subscribe Status");
  }
}

function subscribeError(obj)
{
  console.log("Subscribe Error : " + JSON.stringify(obj));
}

function unsubscribe(address, serviceUuid, characteristicUuid)
{
  var paramsObj = {address:address, serviceUuid:serviceUuid, characteristicUuid:characteristicUuid};

  console.log("Unsubscribe : " + JSON.stringify(paramsObj));

  bluetoothle.unsubscribe(unsubscribeSuccess, unsubscribeError, paramsObj);

  return false;
}

function unsubscribeSuccess(obj)
{
  console.log("Unsubscribe Success : " + JSON.stringify(obj));

  if (obj.status == "unsubscribed")
  {
    console.log("Unsubscribed");
  }
  else
  {
    console.log("Unexpected Unsubscribe Status");
  }
}

function unsubscribeError(obj)
{
  console.log("Unsubscribe Error : " + JSON.stringify(obj));
}

function write(address, serviceUuid, characteristicUuid, value)
{
  var paramsObj = {address:address, serviceUuid:serviceUuid, characteristicUuid:characteristicUuid, value:value};

  console.log("Write : " + JSON.stringify(paramsObj));

  bluetoothle.write(writeSuccess, writeError, paramsObj);

  return false;
}

function writeSuccess(obj)
{
  console.log("Write Success : " + JSON.stringify(obj));

  if (obj.status == "written")
  {
    console.log("Written");
  }
  else
  {
    console.log("Unexpected Write Status");
  }
}

function writeError(obj)
{
  console.log("Write Error : " + JSON.stringify(obj));
}

function readDescriptor(address, serviceUuid, characteristicUuid, descriptorUuid)
{
  var paramsObj = {address:address, serviceUuid:serviceUuid, characteristicUuid:characteristicUuid, descriptorUuid:descriptorUuid};

  console.log("Read Descriptor : " + JSON.stringify(paramsObj));

  bluetoothle.readDescriptor(readDescriptorSuccess, readDescriptorError, paramsObj);

  return false;
}

function readDescriptorSuccess(obj)
{
  console.log("Read Descriptor Success : " + JSON.stringify(obj));

  if (obj.status == "readDescriptor")
  {
    console.log("Read Descriptor");
  }
  else
  {
    console.log("Unexpected Read Descriptor Status");
  }
}

function readDescriptorError(obj)
{
  console.log("Read Descriptor Error : " + JSON.stringify(obj));
}

function writeDescriptor(address, serviceUuid, characteristicUuid, descriptorUuid, value)
{
  var paramsObj = {address:address, serviceUuid:serviceUuid, characteristicUuid:characteristicUuid, descriptorUuid:descriptorUuid, value:value};

  console.log("Write Descriptor : " + JSON.stringify(paramsObj));

  bluetoothle.writeDescriptor(writeDescriptorSuccess, writeDescriptorError, paramsObj);

  return false;
}

function writeDescriptorSuccess(obj)
{
  console.log("Write Descriptor Success : " + JSON.stringify(obj));

  if (obj.status == "writeDescriptor")
  {
    console.log("Write Descriptor");
  }
  else
  {
    console.log("Unexpected Write Descriptor Status");
  }
}

function writeDescriptorError(obj)
{
  console.log("Write Descriptor Error : " + JSON.stringify(obj));
}

function addDevice(address, name)
{
  var $devices = $(".devices");

  var $check = $devices.find("li[data-address='{0}']".format(address));
  if ($check.length > 0)
  {
    return;
  }

  var template = $("#device").text().format(address, name);

  $devices.append(template);
}

function getAddress($item)
{
  return $item.parents("li[data-address]").attr("data-address");
}

function addService(address, serviceUuid)
{
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

function getServiceUuid($item)
{
  return $item.parents("li[data-serviceUuid]").attr("data-serviceUuid");
}

function addCharacteristic(address, serviceUuid, characteristicUuid)
{
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

function getCharacteristicUuid($item)
{
  return $item.parents("li[data-characteristicUuid]").attr("data-characteristicUuid");
}

function addDescriptor(address, serviceUuid, characteristicUuid, descriptorUuid)
{
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

function getDescriptorUuid($item)
{
  return $item.parents("li[data-descriptorUuid]").attr("data-descriptorUuid");
}

String.prototype.format = function()
{
  var args = arguments;
  return this.replace(/{(\d+)}/g, function(match, number)
  {
    return typeof args[number] != 'undefined' ? args[number] : match;
  });
};
