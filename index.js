/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var addressKey = "address";
var response = "";

//var heartRateServiceUuid = "180d";
var UartServiceUuid = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
var RxCharacteristicUuid = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
var clientCharacteristicConfigDescriptorUuid = "2902";
var TxCharacteristicUuid = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";

var scanTimer = null;
var connectTimer = null;
var reconnectTimer = null;

var iOSPlatform = "iOS";
var androidPlatform = "Android";
var WPPlatform = "Win32NT";

var app = {
    // Application Constructor
    initialize_device: function () {
        this.bindEvents();
    },
    // Bind Event Listeners
    //
    // Bind any events that are required on startup. Common events are:
    // 'load', 'deviceready', 'offline', and 'online'.
    bindEvents: function() {
        document.addEventListener('deviceready', this.onDeviceReady, false);
    },
    // deviceready Event Handler
    //
    // The scope of 'this' is the event. In order to call the 'receivedEvent'
    // function, we must explicitly call 'app.receivedEvent(...);'
    onDeviceReady: function() {
        app.receivedEvent('deviceready');
    },
    // Update DOM on a Received Event
    receivedEvent: function(id) {
        var parentElement = document.getElementById(id);
        var listeningElement = parentElement.querySelector('.listening');
        var receivedElement = parentElement.querySelector('.received');

        listeningElement.setAttribute('style', 'display:none;');
        receivedElement.setAttribute('style', 'display:block;');

        console.log('Received Event: ' + id);
    }
};
$('body').on('click', '#Start', function () {
    bluetoothle.initialize(initializeSuccess, initializeError);
});
$('body').on('click', '#Write', function () {
    var command = $('#fname').val();
    var paramsObj = { "value": command, "serviceUuid": UartServiceUuid, "characteristicUuid": RxCharacteristicUuid };
    if (command != "") {
        response = "";
        bluetoothle.write(writeSuccess, writeError, paramsObj);
    }
});
function initializeSuccess(obj)
{
    alert(obj.status);
    if (obj.status == "initialized")
  {
        console.log("Bluetooth initialized successfully");
        bluetoothle.reconnect(reconnectSuccess, reconnectError);
        reconnectTimer = setTimeout(reconnectTimeout, 5000);
  }
  else
  {
    console.log("Unexpected initialize status: " + obj.status);
  }
}
function initializeError(obj)
{
    console.log("Initialize error: " + obj.error + " - " + obj.message);
    bluetoothle.initialize(initializeSuccess, initializeError);
}
function reconnectSuccess(obj)
{
  if (obj.status == "connected")
  {
    console.log("Reconnected to : " + obj.name + " - " + obj.address);

    clearReconnectTimeout();
    if (window.device.platform == iOSPlatform)
    {
      console.log("Discovering heart rate service");
      var paramsObj = {"serviceUuids":[heartRateServiceUuid]};
      bluetoothle.services(servicesHeartSuccess, servicesHeartError, paramsObj);
    }
    else if (window.device.platform == androidPlatform)
    {
      console.log("Beginning discovery");
      bluetoothle.discover(discoverSuccess, discoverError);
    }
    else if (window.device.platform == WPPlatform) {
        console.log("Discovering...");
        var paramsObj = { "serviceUuids": [UartServiceUuid] };
        bluetoothle.services(servicesHeartSuccess, servicesHeartError, paramsObj);
    }
  }
  else if (obj.status == "connecting")
  {
    console.log("Reconnecting to : " + obj.name + " - " + obj.address);
  }
  else
  {
    console.log("Unexpected reconnect status: " + obj.status);
    disconnectDevice();
  }
}

function reconnectError(obj)
{
  console.log("Reconnect error: " + obj.error + " - " + obj.message);
  disconnectDevice();
}

function reconnectTimeout()
{
  console.log("Reconnection timed out");
}

function clearReconnectTimeout()
{ 
    console.log("Clearing reconnect timeout");
  if (reconnectTimer != null)
  {
    clearTimeout(reconnectTimer);
  }
}

function servicesHeartSuccess(obj) {
    if (obj.status == "discoveredServices") {
        var serviceUuids = obj.serviceUuids;
        for (var i = 0; i < serviceUuids.length; i++) {
            var serviceUuid = serviceUuids[i];
            //alert(serviceUuid);
            if (serviceUuid == UartServiceUuid) {
                console.log("Finding UART characteristics");
                var paramsObj = { "serviceUuid": UartServiceUuid, "characteristicUuids": [TxCharacteristicUuid,RxCharacteristicUuid] };
                bluetoothle.characteristics(characteristicsHeartSuccess, characteristicsHeartError, paramsObj);
                return;
            }
        }
        console.log("Error: heart rate service not found");
    }
    else {
        console.log("Unexpected services heart status: " + obj.status);
    }
    disconnectDevice();
}

function servicesHeartError(obj) {
    console.log("Services heart error: " + obj.error + " - " + obj.message);
    disconnectDevice();
}


function characteristicsHeartSuccess(obj) {
    if (obj.status == "discoveredCharacteristics") {
        var characteristicUuids = obj.characteristicUuids;
        console.log("UART characteristics found");
        for (var i = 0; i < characteristicUuids.length; i++) {
            var characteristicUuid = characteristicUuids[i];
            if (characteristicUuid == TxCharacteristicUuid) {
                console.log("Subscribing to heart rate for 5 seconds");
                var paramsObj = { "serviceUuid": UartServiceUuid, "characteristicUuid": TxCharacteristicUuid };
                bluetoothle.subscribe(subscribeSuccess, subscribeError, paramsObj);
                //setTimeout(unsubscribeDevice, 5000);
                return;
            }
        }
        console.log("Error: Heart rate measurement characteristic not found.");
    }
    else {
        console.log("Unexpected characteristics heart status: " + obj.status);
    }
    disconnectDevice();
}

function characteristicsHeartError(obj) {
    console.log("Characteristics heart error: " + obj.error + " - " + obj.message);
    disconnectDevice();
}
function subscribeSuccess(obj) {
    if (obj.status == "subscribedResult") {
        response += obj.value;
        //$('#result').html(str);
        print_resault(response);
    }
    else if (obj.status == "subscribed") {
        console.log("Subscription started");
    }
    else {
        console.log("Unexpected subscribe status: " + obj.status);
        disconnectDevice();
    }
}

function subscribeError(obj) {
    console.log("Subscribe error: " + obj.error + " - " + obj.message);
    disconnectDevice();
}


function writeSuccess(obj) {
    if (obj.status == "written") {
        //unsubscribeDevice();
    }
    else {
        console.log("Unexpected write status: " + obj.status);
        disconnectDevice();
    }
}
function writeError(obj) {
    console.log("Write error: " + obj.error + " - " + obj.message);
    disconnectDevice();
}

function unsubscribeDevice()
{
  console.log("Unsubscribing heart service");
  var paramsObj = { "serviceUuid": UartServiceUuid, "characteristicUuid": TxCharacteristicUuid };
  bluetoothle.unsubscribe(unsubscribeSuccess, unsubscribeError, paramsObj);
}

function unsubscribeSuccess(obj)
{
    if (obj.status == "unsubscribed")
    {
        console.log("Unsubscribed device");
        //console.log("Reading client configuration descriptor");

        //var paramsObj = { "serviceUuid": UartServiceUuid, "characteristicUuid": TxCharacteristicUuid, "descriptorUuid": clientCharacteristicConfigDescriptorUuid };
        //bluetoothle.readDescriptor(readDescriptorSuccess, readDescriptorError, paramsObj);
        return;
    }
    else
  {
    console.log("Unexpected unsubscribe status: " + obj.status);
    disconnectDevice();
  }
}

function unsubscribeError(obj)
{
  console.log("Unsubscribe error: " + obj.error + " - " + obj.message);
  disconnectDevice();
}
function readSuccess(obj){
    if (obj.status == "read") {
        alert(obj.value);
    }
    else {
        console.log("Unexpected read status: " + obj.status);
        disconnectDevice();
    }
}
function readError(obj) {
    console.log("Read error: " + obj.error + " - " + obj.message);
    disconnectDevice();
}

function readDescriptorSuccess(obj)
{
    if (obj.status == "readDescriptor")
    {
        var bytes = bluetoothle.encodedStringToBytes(obj.value);
        var u16Bytes = new Uint16Array(bytes.buffer);
        console.log("Read descriptor value: " + u16Bytes[0]);
        disconnectDevice();
    }
    else
  {
    console.log("Unexpected read descriptor status: " + obj.status);
    disconnectDevice();
  }
}

function readDescriptorError(obj)
{
  console.log("Read Descriptor error: " + obj.error + " - " + obj.message);
  disconnectDevice();
}

function disconnectDevice()
{
  bluetoothle.disconnect(disconnectSuccess, disconnectError);
}

function disconnectSuccess(obj)
{
    if (obj.status == "disconnected")
    {
        console.log("Disconnect device");
        closeDevice();
    }
    else if (obj.status == "disconnecting")
    {
        console.log("Disconnecting device");
    }
    else
  {
    console.log("Unexpected disconnect status: " + obj.status);
  }
}

function disconnectError(obj)
{
  console.log("Disconnect error: " + obj.error + " - " + obj.message);
}

function closeDevice()
{
  bluetoothle.close(closeSuccess, closeError);
}

function closeSuccess(obj)
{
    if (obj.status == "closed")
    {
        console.log("Closed device");
    }
    else
  {
    console.log("Unexpected close status: " + obj.status);
  }
}

function closeError(obj)
{
  console.log("Close error: " + obj.error + " - " + obj.message);
}
function print_resault(result)
{
    if (result.indexOf(">")!=-1)
    {
        document.getElementById("result").innerHTML = result.replace(/(?:\r\n|\r|\n)/g, '<br />');
    }
    return;
}