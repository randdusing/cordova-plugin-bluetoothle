package com.randdusing.bluetoothle;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Base64;
import android.util.Log;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BluetoothLePlugin extends CordovaPlugin 
{
  //Callback variables
  private CallbackContext initCallbackContext;
  private CallbackContext scanCallbackContext;
  private CallbackContext connectCallbackContext;
  private CallbackContext discoverCallbackContext;
  private CallbackContext descriptorCallbackContext;
  private CallbackContext rssiCallbackContext;
  private HashMap<UUID, HashMap<String, CallbackContext>> operationCallbackContexts;
  
  //Initialization related variables
  private final int REQUEST_BT_ENABLE = 59627; /*Random integer*/
  private BluetoothAdapter bluetoothAdapter;
  private boolean isReceiverRegistered = false;

  //Connection related variables
  private BluetoothGatt bluetoothGatt;
  private int connectionState = BluetoothProfile.STATE_DISCONNECTED;
  
  //Discovery related variables
  private final int STATE_UNDISCOVERED = 0;
  private final int STATE_DISCOVERING = 1;
  private final int STATE_DISCOVERED = 2;
  private int discoveredState = STATE_UNDISCOVERED;
  
  //Action Name Strings
  private final String initializeActionName = "initialize";
  private final String startScanActionName = "startScan";
  private final String stopScanActionName = "stopScan";
  private final String connectActionName = "connect";
  private final String reconnectActionName = "reconnect";
  private final String disconnectActionName = "disconnect";
  private final String closeActionName = "close";
  private final String discoverActionName = "discover";
  private final String servicesActionName = "services";
  private final String characteristicsActionName = "characteristics";
  private final String descriptorsActionName = "descriptors";
  private final String readActionName = "read";
  private final String subscribeActionName = "subscribe";
  private final String unsubscribeActionName = "unsubscribe";
  private final String writeActionName = "write";
  private final String readDescriptorActionName = "readDescriptor";
  private final String writeDescriptorActionName = "writeDescriptor";
  private final String rssiActionName = "rssi";
  private final String isInitializedActionName = "isInitialized";
  private final String isEnabledActionName = "isEnabled";
  private final String isScanningActionName = "isScanning";
  private final String isDiscoveredActionName = "isDiscovered";
  private final String isConnectedActionName = "isConnected";
  
  //Object keys
  private final String keyStatus = "status";
  private final String keyError = "error";
  private final String keyMessage = "message";
  private final String keyRequest = "request";
  private final String keyName = "name";
  private final String keyAddress = "address";
  private final String keyRssi = "rssi";
  private final String keyAdvertisement = "advertisement";
  private final String keyServiceUuids = "serviceUuids";
  private final String keyServiceUuid = "serviceUuid";
  private final String keyCharacteristicUuid = "characteristicUuid";
  private final String keyDescriptorUuid = "descriptorUuid";
  private final String keyServices = "services";
  private final String keyCharacteristics = "characteristics";
  private final String keyProperties = "properties";
  private final String keyDescriptors = "descriptors";
  private final String keyValue = "value";
  private final String keyType = "type";
	private final String keyIsInitialized = "isInitialized";
  private final String keyIsEnabled = "isEnabled";
	private final String keyIsScanning = "isScanning";
  private final String keyIsConnected = "isConnected";
  private final String keyIsDiscovered = "isDiscovered";
  private final String keyIsNotification = "isNotification";
  
  //Write Types
  private final String writeTypeNoResponse = "noResponse";
  
  //Status Types
  private final String statusEnabled = "enabled";
  private final String statusScanStarted = "scanStarted";
  private final String statusScanStopped = "scanStopped";
  private final String statusScanResult = "scanResult";
  private final String statusConnected = "connected";
  private final String statusConnecting = "connecting";
  private final String statusDisconnected = "disconnected";
  private final String statusDisconnecting = "disconnecting";
  private final String statusClosed = "closed";
  private final String statusDiscovered = "discovered";
  private final String statusRead = "read";
  private final String statusSubscribed = "subscribed";
  private final String statusSubscribedResult = "subscribedResult";
  private final String statusUnsubscribed = "unsubscribed";
  private final String statusWritten = "written";
  private final String statusReadDescriptor = "readDescriptor";
  private final String statusWrittenDescriptor = "writtenDescriptor";
  private final String statusRssi = "rssi";
  
  //Properties
  private final String propertyBroadcast = "broadcast";
  private final String propertyRead = "read";
  private final String propertyWriteWithoutResponse = "writeWithoutResponse";
  private final String propertyWrite = "write";
  private final String propertyNotify = "notify";
  private final String propertyIndicate = "indicate";
  private final String propertyAuthenticatedSignedWrites = "authenticatedSignedWrites";
  private final String propertyExtendedProperties = "extendedProperties";
  private final String propertyNotifyEncryptionRequired = "notifyEncryptionRequired";
  private final String propertyIndicateEncryptionRequired = "indicateEncryptionRequired";
  
  //Error Types
  private final String errorInitialize = "initialize";
  private final String errorEnable = "enable";
  private final String errorArguments = "arguments";
  private final String errorStartScan = "startScan";
  private final String errorStopScan = "stopScan";
  private final String errorConnect = "connect";
  private final String errorReconnect = "reconnect";
  private final String errorDiscover = "discover";
  private final String errorRead = "read";
  private final String errorSubscription = "subscription";
  private final String errorWrite = "write";
  private final String errorReadDescriptor = "readDescriptor";
  private final String errorWriteDescriptor = "writeDescriptor";
  private final String errorRssi = "rssi";
  private final String errorNeverConnected = "neverConnected";
  private final String errorIsNotDisconnected = "isNotDisconnected";
  private final String errorIsNotConnected = "isNotConnected";
  private final String errorIsDisconnected = "isDisconnected";
  private final String errorService = "service";
  private final String errorCharacteristic = "characteristic";
  private final String errorDescriptor = "descriptor";
  
  //Error Messages
  //Initialization
  private final String logNotEnabled = "Bluetooth not enabled";
  private final String logNotInit = "Bluetooth not initialized";
  //Scanning
  private final String logAlreadyScanning = "Scanning already in progress";
  private final String logScanStartFail = "Scan failed to start";
  private final String logNotScanning = "Not scanning";
  //Connection
  private final String logPreviouslyConnected = "Device previously connected, reconnect or close for new device";
  private final String logNeverConnected = "Never connected to device";
  private final String logIsNotConnected = "Device isn't connected";
  private final String logIsNotDisconnected = "Device isn't disconnected";
  private final String logIsDisconnected = "Device is disconnected";
  private final String logNoAddress = "No device address";
  private final String logNoDevice = "Device not found";
  private final String logReconnectFail = "Reconnection to device failed";
  //Discovery
  private final String logAlreadyDiscovering = "Already discovering device";
  private final String logDiscoveryFail = "Unable to discover device";
  //Read/write
  private final String logNoArgObj = "Argument object not found";
  private final String logNoService = "Service not found";
  private final String logNoCharacteristic = "Characteristic not found";
  private final String logNoDescriptor = "Descriptor not found";
  private final String logReadFail = "Unable to read";
  private final String logReadFailReturn = "Unable to read on return";
  private final String logSubscribeFail = "Unable to subscribe";
  private final String logUnsubscribeFail = "Unable to unsubscribe";
  private final String logWriteFail = "Unable to write";
  private final String logWriteFailReturn = "Unable to write on return";
  private final String logWriteValueNotFound = "Write value not found";
  private final String logWriteValueNotSet = "Write value not set";
  private final String logReadDescriptorFail = "Unable to read descriptor";
  private final String logReadDescriptorFailReturn = "Unable to read descriptor on return";
  private final String logWriteDescriptorNotAllowed = "Unable to write client configuration descriptor";
  private final String logWriteDescriptorFail = "Unable to write descriptor";
  private final String logWriteDescriptorValueNotFound = "Write descriptor value not found";
  private final String logWriteDescriptorValueNotSet = "Write descriptor value not set";
  private final String logWriteDescriptorFailReturn = "Descriptor not written on return";
  private final String logRssiFail = "Unable to read RSSI";
  private final String logRssiFailReturn = "Unable to read RSSI on return";
  
  private final String operationRead = "read";
  private final String operationSubscribe = "subscribe";
  private final String operationUnsubscribe = "unsubscribe";
  private final String operationWrite = "write";
  
  private final String baseUuidStart = "0000";
  private final String baseUuidEnd = "-0000-1000-8000-00805f9b34fb";
  
  //Client Configuration UUID for notifying/indicating
  private final UUID clientConfigurationDescriptorUuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
  
  //Actions
  @Override
  public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException
  {
    //Execute the specified action
    if (initializeActionName.equals(action))
    {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          initializeAction(args, callbackContext);
        }
      });
      return true;
    }
    else if (startScanActionName.equals(action))
    {
      startScanAction(args, callbackContext);
      return true;
    }
    else if (stopScanActionName.equals(action)) 
    {
      stopScanAction(callbackContext);
      return true;
    }
    else if (connectActionName.equals(action))
    {
      connectAction(args, callbackContext);
      return true;
    }
    else if (reconnectActionName.equals(action))
    {
      reconnectAction(callbackContext);
      return true;
    }
    else if (disconnectActionName.equals(action))
    {
      disconnectAction(callbackContext);
      return true;      
    }
    else if (servicesActionName.equals(action))
    {
      callbackContext.success();
      return true;      
    }
    else if (characteristicsActionName.equals(action))
    {
      callbackContext.success();
      return true;      
    }
    else if (descriptorsActionName.equals(action))
    {
      callbackContext.success();
      return true;      
    }
    else if (closeActionName.equals(action))
    {
      closeAction(callbackContext);
      return true;
    }
    else if (discoverActionName.equals(action))
    {
      discoverAction(callbackContext);
      return true;
    }
    else if (readActionName.equals(action))
    {
      readAction(args, callbackContext);
      return true;
    }
    else if (subscribeActionName.equals(action))
    {
      subscribeAction(args, callbackContext);
      return true;
    }
    else if (unsubscribeActionName.equals(action))
    {
      unsubscribeAction(args, callbackContext);
      return true;
    }
    else if (writeActionName.equals(action))
    {
      writeAction(args, callbackContext);
      return true;
    }
    else if (readDescriptorActionName.equals(action))
    {
      readDescriptorAction(args, callbackContext);
      return true;
    }
    else if (writeDescriptorActionName.equals(action))
    {
      writeDescriptorAction(args, callbackContext);
      return true;
    }
    else if (rssiActionName.equals(action))
    {
      rssiAction(callbackContext);
      return true;
    }
    else if (isInitializedActionName.equals(action))
    {
    	isInitializedAction(callbackContext);
    	return true;
    }
    else if (isEnabledActionName.equals(action))
    {
    	isEnabledAction(callbackContext);
    	return true;
    }
    else if (isScanningActionName.equals(action))
    {
    	isScanningAction(callbackContext);
    	return true;
    }
    else if (isConnectedActionName.equals(action))
    {
      isConnectedAction(callbackContext);
      return true;
    }
    else if (isDiscoveredActionName.equals(action))
    {
      isDiscoveredAction(callbackContext);
      return true;
    }
    return false;
  }

  private void initializeAction(JSONArray args, CallbackContext callbackContext)
  { 
    //Save init callback
    initCallbackContext = callbackContext;
    
  	if (bluetoothAdapter != null)
  	{
  		JSONObject returnObj = new JSONObject();
			PluginResult pluginResult;
			
			if (bluetoothAdapter.isEnabled())
			{
				addProperty(returnObj, keyStatus, statusEnabled);
				
				pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
	      pluginResult.setKeepCallback(true);
	      initCallbackContext.sendPluginResult(pluginResult);
			}
			else
			{
				addProperty(returnObj, keyError, errorEnable);
				addProperty(returnObj, keyMessage, logNotEnabled);
				
				pluginResult = new PluginResult(PluginResult.Status.ERROR, returnObj);
	      pluginResult.setKeepCallback(true);
	      initCallbackContext.sendPluginResult(pluginResult);
			}
				
  		return;
  	}
  	
    JSONObject returnObj = new JSONObject();
    
    //Add a receiver to pick up when Bluetooth state changes
    cordova.getActivity().registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    isReceiverRegistered = true;
    
    //Get Bluetooth adapter via Bluetooth Manager
    BluetoothManager bluetoothManager = (BluetoothManager) cordova.getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
    bluetoothAdapter = bluetoothManager.getAdapter();
    
    //If it's already enabled, 
    if (bluetoothAdapter.isEnabled())
    {
    	addProperty(returnObj, keyStatus, statusEnabled);
    	PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
      pluginResult.setKeepCallback(true);
      initCallbackContext.sendPluginResult(pluginResult);
    	return;
    }
    
    JSONObject obj = getArgsObject(args);
    
    boolean request = false;
    if (obj != null)
    {
    	request = getRequest(obj);
    }
    
    //Request user to enable Bluetooth
  	if (request)
  	{
      //Request Bluetooth to be enabled
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      cordova.startActivityForResult(this, enableBtIntent, REQUEST_BT_ENABLE);
  	}
  	//No request, so send back not enabled
  	else
  	{
  		addProperty(returnObj, keyError, errorEnable);
    	addProperty(returnObj, keyMessage, logNotEnabled);
    	PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, returnObj);
      pluginResult.setKeepCallback(true);
      initCallbackContext.sendPluginResult(pluginResult);
  	}
  }
  
  private void startScanAction(JSONArray args, CallbackContext callbackContext)
  {
  	if (isNotInitialized(callbackContext))
  	{
  		return;
  	}
  	
  	JSONObject returnObj = new JSONObject();
    
    //If the adapter is already scanning, don't call another scan.
    if (scanCallbackContext != null)
    {
    	addProperty(returnObj, keyError, errorStartScan);
    	addProperty(returnObj, keyMessage, logAlreadyScanning);
      callbackContext.error(returnObj);
      return;
    }
    
    //Get the service UUIDs from the arguments
    JSONObject obj = getArgsObject(args);
    
    UUID[] serviceUuids = null;
    
    if (obj != null)
    {
      serviceUuids = getServiceUuids(obj);
    }
    
    //Save the callback context for reporting back found devices. Also the isScanning flag
    scanCallbackContext = callbackContext;

    //Start the scan with or without service UUIDs
    boolean result;
    if (serviceUuids == null || serviceUuids.length == 0)
    {
      result = bluetoothAdapter.startLeScan(scanCallback);
    }
    else
    {
      result = bluetoothAdapter.startLeScan(serviceUuids, scanCallback);
    }
    
    //If the scan didn't start...
    if (!result)
    {
    	addProperty(returnObj, keyError, errorStartScan);
    	addProperty(returnObj, keyMessage, logScanStartFail);
      callbackContext.error(returnObj);
      scanCallbackContext = null;
      return;
    }
    
    //Notify user of started scan and save callback
    addProperty(returnObj, keyStatus, statusScanStarted);
    
    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
    pluginResult.setKeepCallback(true);
    callbackContext.sendPluginResult(pluginResult);
  }
  
  private void stopScanAction(CallbackContext callbackContext)
  {
    if (isNotInitialized(callbackContext))
    {
    	return;
    }
    
  	JSONObject returnObj = new JSONObject();
    
    //Check if already scanning
    if (scanCallbackContext == null)
    {
    	addProperty(returnObj, keyError, errorStopScan);
    	addProperty(returnObj, keyMessage, logNotScanning);
      callbackContext.error(returnObj);
      return;
    }
    
    //Stop the scan
    bluetoothAdapter.stopLeScan(scanCallback);
    
    //Set scanning state
    scanCallbackContext = null;

    //Inform user
    addProperty(returnObj, keyStatus, statusScanStopped);
    callbackContext.success(returnObj);
  }

  private void connectAction(JSONArray args, CallbackContext callbackContext)
  { 
    if (isNotInitialized(callbackContext))
    {
    	return;
    }
    
  	JSONObject returnObj = new JSONObject();
    
    if (bluetoothGatt != null)
    {
      addProperty(returnObj, keyError, errorConnect);
      addProperty(returnObj, keyMessage, logPreviouslyConnected);
      callbackContext.error(returnObj);
      return;
    }
    
    //Get the address string
    JSONObject obj = getArgsObject(args);
    
    if (isNotArgsObject(obj, callbackContext))
    {
      return;
    }
    
    //Get address
    String address = getAddress(obj);
    
    if (address == null)
    {
    	addProperty(returnObj, keyError, errorConnect);
    	addProperty(returnObj, keyMessage, logNoAddress);
      callbackContext.error(returnObj);
      return;
    }
    
    //Get the device
    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
    
    //If device wasn't found...
    if (device == null)
    {
    	addProperty(returnObj, keyError, errorConnect);
    	addProperty(returnObj, keyMessage, logNoDevice);
      callbackContext.error(returnObj);
      return;
    }
    
    //Connect!
    connectCallbackContext = callbackContext;
    connectionState = BluetoothProfile.STATE_CONNECTING;
    bluetoothGatt = device.connectGatt(cordova.getActivity().getApplicationContext(), false, gattCallback);

    //Return connecting status
    addProperty(returnObj, keyStatus, statusConnecting);
    addProperty(returnObj, keyName, device.getName());
    addProperty(returnObj, keyAddress, device.getAddress());
    
    //Keep the callback
    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
    pluginResult.setKeepCallback(true);
    callbackContext.sendPluginResult(pluginResult);
  }
   
  private void reconnectAction(CallbackContext callbackContext)
  {
    if (isNotInitialized(callbackContext))
    {
    	return;
    }
    
    if (wasNeverConnected(callbackContext))
    {
      return;
    }
    
    if (isNotDisconnected(callbackContext))
    {
      return;
    }
    
    JSONObject returnObj = new JSONObject();
    
    connectCallbackContext = callbackContext;
    
    boolean result = bluetoothGatt.connect();
    
    if (!result)
    {
    	addProperty(returnObj, keyError, errorReconnect);
    	addProperty(returnObj, keyMessage, logReconnectFail);
      callbackContext.error(returnObj);
      connectCallbackContext = null;
      return;
    }
    
    connectionState = BluetoothProfile.STATE_CONNECTING;
    
    BluetoothDevice device = bluetoothGatt.getDevice();
    
    //Return connecting status and keep callback
    addProperty(returnObj, keyStatus, statusConnecting);
    addProperty(returnObj, keyName, device.getName());
    addProperty(returnObj, keyAddress, device.getAddress());
    
    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
    pluginResult.setKeepCallback(true);
    callbackContext.sendPluginResult(pluginResult);
  }
  
  private void disconnectAction(CallbackContext callbackContext)
  {
    if (isNotInitialized(callbackContext))
    {
    	return;
    }
    
    if (wasNeverConnected(callbackContext))
    {
      return;
    }
    
    if (isDisconnected(callbackContext))
    {
      return;
    }
    
  	JSONObject returnObj = new JSONObject();
  	
    BluetoothDevice device = bluetoothGatt.getDevice();
    
    //Return disconnecting status and keep callback
    addProperty(returnObj, keyName, device.getName());
    addProperty(returnObj, keyAddress, device.getAddress());
    
    //If it's connecting, cancel attempt and return disconnect
    if (connectionState == BluetoothProfile.STATE_CONNECTING)
    {
    	addProperty(returnObj, keyStatus, statusDisconnected);
    	connectionState = BluetoothProfile.STATE_DISCONNECTED;
    	
      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);
    }
    //Very unlikely that this is DISCONNECTING
    else
    {
      addProperty(returnObj, keyStatus, statusDisconnecting);
      connectionState = BluetoothProfile.STATE_DISCONNECTING;
      
      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
      pluginResult.setKeepCallback(true);
      callbackContext.sendPluginResult(pluginResult);
      
      //Call disconnect and change connection station
      connectCallbackContext = callbackContext;
    }
    
    bluetoothGatt.disconnect();
  }

  private void closeAction(CallbackContext callbackContext)
  {  
    if (isNotInitialized(callbackContext))
    {
    	return;
    }
    
    if (wasNeverConnected(callbackContext))
    {
      return;
    }
    
    if (isNotDisconnected(callbackContext))
    {
      return;
    }
    
    JSONObject returnObj = new JSONObject();
    
    BluetoothDevice device = bluetoothGatt.getDevice();
    
    addProperty(returnObj, keyStatus, statusClosed);
    addProperty(returnObj, keyAddress, device.getAddress());
    addProperty(returnObj, keyName, device.getName());
    
    bluetoothGatt.close();
    bluetoothGatt = null;
    
    discoveredState = STATE_UNDISCOVERED;

    connectCallbackContext = null;
    
    ClearOperationCallbacks();
    
    callbackContext.success(returnObj);
  }
  
  private void discoverAction(CallbackContext callbackContext)
  {
    if (isNotInitialized(callbackContext))
    {
    	return;
    }
    
    if (wasNeverConnected(callbackContext))
    {
      return;
    }
     
    if (isNotConnected(callbackContext))
    {
    	return;
    }
    
    JSONObject returnObj = new JSONObject();
    
    //Already initiated discovery
    if (discoveredState == STATE_DISCOVERING)
    {
    	addProperty(returnObj, keyError, errorDiscover);
    	addProperty(returnObj, keyMessage, logAlreadyDiscovering);
      callbackContext.error(returnObj);
      return;
    }
    //Already discovered
    else if (discoveredState == STATE_DISCOVERED)
    {
      returnObj = getDiscovery();
      callbackContext.success(returnObj);
      return;
    }
    
    //Else undiscovered, so start discovery
    discoveredState = STATE_DISCOVERING;
    discoverCallbackContext = callbackContext;
    bluetoothGatt.discoverServices();
  }

  private void readAction(JSONArray args, CallbackContext callbackContext)
  {
  	if (isNotInitialized(callbackContext))
    {
    	return;
    }
    
    if (wasNeverConnected(callbackContext))
    {
      return;
    }
  	
    if (isNotConnected(callbackContext))
    {
    	return;
    }
    
    JSONObject obj = getArgsObject(args);
    
    if (isNotArgsObject(obj, callbackContext))
    {
      return;
    }
    
    BluetoothGattService service = getService(obj);
    
    if (isNotService(service, callbackContext))
    {
    	return;
    }
    
    BluetoothGattCharacteristic characteristic = getCharacteristic(obj, service);
    
    if (isNotCharacteristic(characteristic, callbackContext))
    {
    	return;
    }
    
    UUID characteristicUuid = characteristic.getUuid();
    
    AddCallback(characteristicUuid, operationRead, callbackContext);
    
    boolean result = bluetoothGatt.readCharacteristic(characteristic);
    
    if (!result)
    {
    	JSONObject returnObj = new JSONObject();
    	addProperty(returnObj, keyServiceUuid, formatUuid(service.getUuid()));
    	addProperty(returnObj, keyCharacteristicUuid, formatUuid(characteristicUuid));
    	addProperty(returnObj, keyError, errorRead);
    	addProperty(returnObj, keyMessage, logReadFail);
      callbackContext.error(returnObj);
      RemoveCallback(characteristicUuid, operationRead);
    }
  }
   
  private void subscribeAction(JSONArray args, CallbackContext callbackContext)
  {
    if (isNotInitialized(callbackContext))
    {
    	return;
    }
    
    if (wasNeverConnected(callbackContext))
    {
      return;
    }
    
    if (isNotConnected(callbackContext))
    {
    	return;
    }
    
    JSONObject obj = getArgsObject(args);
    
    if (isNotArgsObject(obj, callbackContext))
    {
      return;
    }
    
    boolean isNotification = obj.optBoolean(keyIsNotification, true);
    
    BluetoothGattService service = getService(obj);
    
    if (isNotService(service, callbackContext))
    {
    	return;
    }
    
    BluetoothGattCharacteristic characteristic = getCharacteristic(obj, service);
    
    if (isNotCharacteristic(characteristic, callbackContext))
    {
    	return;
    }
    
    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(clientConfigurationDescriptorUuid);
    
    if (isNotDescriptor(descriptor, callbackContext))
    {
    	return;
    }
    
    UUID characteristicUuid = characteristic.getUuid();
    
  	JSONObject returnObj = new JSONObject();
  	
  	addProperty(returnObj, keyServiceUuid, formatUuid(service.getUuid()));
  	addProperty(returnObj, keyCharacteristicUuid, formatUuid(characteristicUuid));
  	
  	//Subscribe to the characteristic
    boolean result = bluetoothGatt.setCharacteristicNotification(characteristic, true);
    
    if (!result)
    {
    	addProperty(returnObj, keyError, errorSubscription);
    	addProperty(returnObj, keyMessage, logSubscribeFail);
      callbackContext.error(returnObj);
      return;
    }
    
    //Set the descriptor for notification
    if (isNotification)
    {
    	result = descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
    }
    //Or for indication
    else
    {
    	result = descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
    }
    
  	if (!result)
  	{
  		//Clean up
  		bluetoothGatt.setCharacteristicNotification(characteristic, false);
  		
  		addProperty(returnObj, keyError, errorWriteDescriptor);
  		addProperty(returnObj, keyMessage, logWriteDescriptorValueNotSet);
  		callbackContext.error(returnObj);
  		return;
  	}
  	
    AddCallback(characteristicUuid, operationSubscribe, callbackContext);
    
    //Write the descriptor value
  	result = bluetoothGatt.writeDescriptor(descriptor);
  	
  	if (!result)
  	{
  		//Clean up
  		bluetoothGatt.setCharacteristicNotification(characteristic, false);

  		addProperty(returnObj, keyError, errorWriteDescriptor);
  		addProperty(returnObj, keyMessage, logWriteDescriptorFail);
  		callbackContext.error(returnObj);
  		RemoveCallback(characteristicUuid, operationSubscribe);
  	}
  }
  
  private void unsubscribeAction(JSONArray args, CallbackContext callbackContext)
  {
  	if (isNotInitialized(callbackContext))
    {
    	return;
    }
    
    if (wasNeverConnected(callbackContext))
    {
      return;
    }
  	
    if (isNotConnected(callbackContext))
    {
    	return;
    }
    
    JSONObject obj = getArgsObject(args);
    
    if (isNotArgsObject(obj, callbackContext))
    {
      return;
    }
    
    BluetoothGattService service = getService(obj);
    
    if (isNotService(service, callbackContext))
    {
    	return;
    }
    
    BluetoothGattCharacteristic characteristic = getCharacteristic(obj, service);
    
    if (isNotCharacteristic(characteristic, callbackContext))
    {
    	return;
    }
    
    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(clientConfigurationDescriptorUuid);
    
    if (isNotDescriptor(descriptor, callbackContext))
    {
    	return;
    }
    
    UUID characteristicUuid = characteristic.getUuid();
    
    JSONObject returnObj = new JSONObject();
  
  	addProperty(returnObj, keyServiceUuid, formatUuid(service.getUuid()));
  	addProperty(returnObj, keyCharacteristicUuid, formatUuid(characteristicUuid));
  	
  	//Unsubscribe to the characteristic
    boolean result = bluetoothGatt.setCharacteristicNotification(characteristic, false);
    
    if (!result)
    {
    	addProperty(returnObj, keyError, errorSubscription);
    	addProperty(returnObj, keyMessage, logUnsubscribeFail);
      callbackContext.error(returnObj);
      return;
    }
    
    //Set the descriptor for disabling notification/indication
    result = descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
      
  	if (!result)
  	{
  		addProperty(returnObj, keyError, errorWriteDescriptor);
  		addProperty(returnObj, keyMessage, logWriteDescriptorValueNotSet);
  		callbackContext.error(returnObj);
  		return;
  	}
  	
  	AddCallback(characteristicUuid, operationUnsubscribe, callbackContext);
    
    //Write the actual descriptor value
  	result = bluetoothGatt.writeDescriptor(descriptor);
  	
  	if (!result)
  	{
  		addProperty(returnObj, keyError, errorWriteDescriptor);
  		addProperty(returnObj, keyMessage, logWriteDescriptorFail);
  		callbackContext.error(returnObj);
  		RemoveCallback(characteristicUuid, operationUnsubscribe);
  	}
  }

  private void writeAction(JSONArray args, CallbackContext callbackContext)
  {
  	if (isNotInitialized(callbackContext))
    {
    	return;
    }
    
    if (wasNeverConnected(callbackContext))
    {
      return;
    }
  	
    if (isNotConnected(callbackContext))
    {
    	return;
    }
    
    JSONObject obj = getArgsObject(args);
    
    if (isNotArgsObject(obj, callbackContext))
    {
      return;
    }
    
    BluetoothGattService service = getService(obj);
    
    if (isNotService(service, callbackContext))
    {
    	return;
    }
    
    BluetoothGattCharacteristic characteristic = getCharacteristic(obj, service);
    
    if (isNotCharacteristic(characteristic, callbackContext))
    {
    	return;
    }
    
    UUID characteristicUuid = characteristic.getUuid();
    
  	JSONObject returnObj = new JSONObject();
  	addProperty(returnObj, keyServiceUuid, formatUuid(service.getUuid()));
  	addProperty(returnObj, keyCharacteristicUuid, formatUuid(characteristicUuid));
  	
    byte[] value = getPropertyBytes(obj, keyValue);
    
    if (value == null)
    {
    	addProperty(returnObj, keyError, errorWrite);
    	addProperty(returnObj, keyMessage, logWriteValueNotFound);
      callbackContext.error(returnObj);
      return;
    }
    
    int writeType = this.getWriteType(obj);
    characteristic.setWriteType(writeType);
    
    boolean result = characteristic.setValue(value);
    
    if (!result)
    {
    	addProperty(returnObj, keyError, errorWrite);
    	addProperty(returnObj, keyMessage, logWriteValueNotSet);
      callbackContext.error(returnObj);
      return;
    }
    
    AddCallback(characteristicUuid, operationWrite, callbackContext);
    
    result = bluetoothGatt.writeCharacteristic(characteristic);
    
    if (!result)
    {
    	addProperty(returnObj, keyError, errorWrite);
    	addProperty(returnObj, keyMessage, logWriteFail);
      callbackContext.error(returnObj);
      RemoveCallback(characteristicUuid, operationWrite);
    }
  }
  
  private void readDescriptorAction(JSONArray args, CallbackContext callbackContext)
  {
  	if (isNotInitialized(callbackContext))
    {
    	return;
    }
    
    if (wasNeverConnected(callbackContext))
    {
      return;
    }
  	
    if (isNotConnected(callbackContext))
    {
    	return;
    }
    
    JSONObject obj = getArgsObject(args);
    
    if (isNotArgsObject(obj, callbackContext))
    {
      return;
    }
    
    BluetoothGattService service = getService(obj);
    
    if (isNotService(service, callbackContext))
    {
    	return;
    }
    
    BluetoothGattCharacteristic characteristic = getCharacteristic(obj, service);
    
    if (isNotCharacteristic(characteristic, callbackContext))
    {
    	return;
    }
  	
    BluetoothGattDescriptor descriptor = getDescriptor(obj, characteristic);
    
    if (isNotDescriptor(descriptor, callbackContext))
    {
    	return;
    }
    
    descriptorCallbackContext = callbackContext;
    
    boolean result = bluetoothGatt.readDescriptor(descriptor);
    
    if (!result)
    {
      JSONObject returnObj = new JSONObject();
      addProperty(returnObj, keyServiceUuid, formatUuid(service.getUuid()));
      addProperty(returnObj, keyCharacteristicUuid, formatUuid(characteristic.getUuid()));
      addProperty(returnObj, keyDescriptorUuid, formatUuid(descriptor.getUuid()));
    	addProperty(returnObj, keyError, errorReadDescriptor);
    	addProperty(returnObj, keyMessage, logReadDescriptorFail);
      callbackContext.error(returnObj);
      descriptorCallbackContext = null;
      return;
    }
  }
  
  private void writeDescriptorAction(JSONArray args, CallbackContext callbackContext)
  {
  	if (isNotInitialized(callbackContext))
    {
    	return;
    }
    
    if (wasNeverConnected(callbackContext))
    {
      return;
    }
  	
    if (isNotConnected(callbackContext))
    {
    	return;
    }
    
    JSONObject obj = getArgsObject(args);
    
    if (isNotArgsObject(obj, callbackContext))
    {
      return;
    }
    
    BluetoothGattService service = getService(obj);
    
    if (isNotService(service, callbackContext))
    {
    	return;
    }
    
    BluetoothGattCharacteristic characteristic = getCharacteristic(obj, service);
    
    if (isNotCharacteristic(characteristic, callbackContext))
    {
    	return;
    }
  	
    BluetoothGattDescriptor descriptor = getDescriptor(obj, characteristic);
    
    if (isNotDescriptor(descriptor, callbackContext))
    {
    	return;
    }
    
  	JSONObject returnObj = new JSONObject();
    
    addProperty(returnObj, keyServiceUuid, formatUuid(service.getUuid()));
  	addProperty(returnObj, keyCharacteristicUuid, formatUuid(characteristic.getUuid()));
  	addProperty(returnObj, keyDescriptorUuid, formatUuid(descriptor.getUuid()));
  	
  	//Let subscribe/unsubscribe take care of it
    if (descriptor.getUuid().equals(clientConfigurationDescriptorUuid))
    {
    	addProperty(returnObj, keyError, errorWriteDescriptor);
    	addProperty(returnObj, keyMessage, logWriteDescriptorNotAllowed);
    	callbackContext.error(returnObj);
    	return;
    }
    
    byte[] value = getPropertyBytes(obj, keyValue);
    
    if (value == null)
    {
    	addProperty(returnObj, keyError, errorWriteDescriptor);
    	addProperty(returnObj, keyMessage, logWriteDescriptorValueNotFound);
      callbackContext.error(returnObj);
      return;
    }
    
    boolean result = descriptor.setValue(value);
    
    if (!result)
    {
    	addProperty(returnObj, keyError, errorWriteDescriptor);
    	addProperty(returnObj, keyMessage, logWriteDescriptorValueNotSet);
      callbackContext.error(returnObj);
      return;
    }
    
    descriptorCallbackContext = callbackContext;
    
    result = bluetoothGatt.writeDescriptor(descriptor);
    
    if (!result)
    {
    	addProperty(returnObj, keyError, errorWriteDescriptor);
    	addProperty(returnObj, keyMessage, logWriteDescriptorFail);
      callbackContext.error(returnObj);
      descriptorCallbackContext = null;
      return;
    }
  }
  
  private void rssiAction(CallbackContext callbackContext)
  {
    if (isNotInitialized(callbackContext))
    {
    	return;
    }
    
    if (wasNeverConnected(callbackContext))
    {
      return;
    }
  	
    if (isNotConnected(callbackContext))
    {
    	return;
    }
     
    rssiCallbackContext = callbackContext;
    
    boolean result = bluetoothGatt.readRemoteRssi();
    
    if (!result)
    {
      JSONObject returnObj = new JSONObject();
    	addProperty(returnObj, keyError, errorRssi);
    	addProperty(returnObj, keyMessage, logRssiFail);
      callbackContext.error(returnObj);
      rssiCallbackContext = null;
      return;
    }
  }
  
  private void isInitializedAction(CallbackContext callbackContext)
  {
  	boolean result = (bluetoothAdapter != null);
  	
    JSONObject returnObj = new JSONObject();
  	addProperty(returnObj, keyIsInitialized, result);
  	
    callbackContext.success(returnObj);
  }
  
  private void isEnabledAction(CallbackContext callbackContext)
  {
  	boolean result = (bluetoothAdapter != null && bluetoothAdapter.isEnabled());
  	
    JSONObject returnObj = new JSONObject();
  	addProperty(returnObj, keyIsEnabled, result);
  	
    callbackContext.success(returnObj);
  }
  
  private void isScanningAction(CallbackContext callbackContext)
  {
  	boolean result = (scanCallbackContext != null);
  	
    JSONObject returnObj = new JSONObject();
  	addProperty(returnObj, keyIsScanning, result);
  	
  	callbackContext.success(returnObj);
  }
  
  private void isConnectedAction(CallbackContext callbackContext)
  {
  	boolean result = (connectionState == BluetoothAdapter.STATE_CONNECTED);
    
  	JSONObject returnObj = new JSONObject();
  	addProperty(returnObj, keyIsConnected, result);
  	
  	callbackContext.success(returnObj);
  }
  
  private void isDiscoveredAction(CallbackContext callbackContext)
  {
  	boolean result = (discoveredState == STATE_DISCOVERED);
    
  	JSONObject returnObj = new JSONObject();
  	addProperty(returnObj, keyIsDiscovered, result);
  	
  	callbackContext.success(returnObj);

  }

  @Override
  public void onDestroy()
  {
      super.onDestroy();
      
      if (isReceiverRegistered)
      {
      	cordova.getActivity().unregisterReceiver(mReceiver);
      }
  }
  
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (initCallbackContext == null)
			{
				return;
			}
			
			if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED))
			{
				JSONObject returnObj = new JSONObject();
				PluginResult pluginResult;
				
				switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR))
				{
					case BluetoothAdapter.STATE_OFF:
					//case BluetoothAdapter.STATE_TURNING_OFF:
					//case BluetoothAdapter.STATE_TURNING_ON:
						
						addProperty(returnObj, keyError, errorEnable);
						addProperty(returnObj, keyMessage, logNotEnabled);
						
						scanCallbackContext = null;
						connectCallbackContext = null;
						ClearOperationCallbacks();
						bluetoothGatt = null;
						
						pluginResult = new PluginResult(PluginResult.Status.ERROR, returnObj);
			      pluginResult.setKeepCallback(true);
			      initCallbackContext.sendPluginResult(pluginResult);
			      
			      break;
					case BluetoothAdapter.STATE_ON:
						
						addProperty(returnObj, keyStatus, statusEnabled);
						
						pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
			      pluginResult.setKeepCallback(true);
			      initCallbackContext.sendPluginResult(pluginResult);
			      
						break;
				}
			}
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		//If this was a Bluetooth enablement request...
		if (requestCode == REQUEST_BT_ENABLE)
		{
			//If callback doesnt exist, no reason to proceed
			if (initCallbackContext == null)
			{
				return;
			}
			
			//Whether the result code was successful or not, just check whether Bluetooth is enabled
			if (!bluetoothAdapter.isEnabled())
			{
				JSONObject returnObj = new JSONObject();
				addProperty(returnObj, keyError, errorEnable);
		  	addProperty(returnObj, keyMessage, logNotEnabled);
		  	
		  	PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, returnObj);
	      pluginResult.setKeepCallback(true);
	      initCallbackContext.sendPluginResult(pluginResult);
			}
		}
	}
  
  //Scan Callback
  private LeScanCallback scanCallback = new LeScanCallback()
  {
    @Override
    public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord)
    {
    	if (scanCallbackContext == null)
    	{
    		return;
    	}
    	
      JSONObject returnObj = new JSONObject();
      
      addProperty(returnObj, keyName, device.getName());
      addProperty(returnObj, keyAddress, device.getAddress());
      addProperty(returnObj, keyRssi, rssi);
      addPropertyBytes(returnObj, keyAdvertisement, scanRecord);
      addProperty(returnObj, keyStatus, statusScanResult);
      
      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
      pluginResult.setKeepCallback(true);
      scanCallbackContext.sendPluginResult(pluginResult);
    }
  };
  
  //Bluetooth callback for connecting, discovering, reading and writing
  
  private final BluetoothGattCallback gattCallback =  new BluetoothGattCallback()
  {
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
    {
      //Get the connected device
      BluetoothDevice device = gatt.getDevice();
      
      connectionState = newState;
      
      //Device was connected
      if (newState == BluetoothProfile.STATE_CONNECTED)
      {
      	operationCallbackContexts = new HashMap<UUID, HashMap<String, CallbackContext>>();
      	
        //This shouldn't happen
        if (connectCallbackContext == null)
        {
          return;
        }

	      //Create json object with address, name and connection status
	      JSONObject returnObj = new JSONObject();
	      addProperty(returnObj, keyStatus, statusConnected);
	      addProperty(returnObj, keyAddress, device.getAddress());
	      addProperty(returnObj, keyName, device.getName());

	      //Keep connection call back for disconnect
	      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
	      pluginResult.setKeepCallback(true);
	      connectCallbackContext.sendPluginResult(pluginResult);
      }
      //Device was disconnected
      else if (newState == BluetoothProfile.STATE_DISCONNECTED)
      {      
      	ClearOperationCallbacks();

        if (connectCallbackContext == null)
        {
          return;
        }
        
        JSONObject returnObj = new JSONObject();
        addProperty(returnObj, keyStatus, statusDisconnected);
        addProperty(returnObj, keyAddress, device.getAddress());
        addProperty(returnObj, keyName, device.getName());
        
        connectCallbackContext.success(returnObj);
        connectCallbackContext = null;
      }
    }
  
    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status)
    {
    	if (status == BluetoothGatt.GATT_SUCCESS)
    	{
    		discoveredState = STATE_DISCOVERED;
    	}
    	else
    	{
    		discoveredState = STATE_UNDISCOVERED;
    	}
    	
      //Shouldn't happen, but check for null callback
      if (discoverCallbackContext == null)
      {
        return;
      }
      
      JSONObject returnObj = new JSONObject();
      
      //If successfully discovered, return list of services, characteristics and descriptors
      if (status == BluetoothGatt.GATT_SUCCESS)
      {
        returnObj = getDiscovery();
        discoverCallbackContext.success(returnObj);
      }
      //Else it failed
      else
      {
      	addProperty(returnObj, keyError, errorDiscover);
	      addProperty(returnObj, keyMessage, logDiscoveryFail);
	      discoverCallbackContext.error(returnObj);
      }
      
      //Clear the callback
      discoverCallbackContext = null;
    }
  
    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
    { 
    	UUID characteristicUuid = characteristic.getUuid();
    	
    	CallbackContext callbackContext = GetCallback(characteristicUuid, operationRead);
    	
    	//If no callback, just return
      if (callbackContext == null)
      {
        return;
      }
      
      JSONObject returnObj = new JSONObject();
      addProperty(returnObj, keyServiceUuid, formatUuid(characteristic.getService().getUuid()));
      addProperty(returnObj, keyCharacteristicUuid, formatUuid(characteristicUuid));
      
      //If successfully read, return value
      if (status == BluetoothGatt.GATT_SUCCESS)
      {
	      addProperty(returnObj, keyStatus, statusRead);
	      addPropertyBytes(returnObj, keyValue, characteristic.getValue());
	      callbackContext.success(returnObj);
      }
      //Else it failed
      else
      {
      	addProperty(returnObj, keyError, errorRead);
	      addProperty(returnObj, keyMessage, logReadFailReturn);
	      callbackContext.error(returnObj);
      }
      
      //Clear callback
      RemoveCallback(characteristicUuid, operationRead);
    }
    
    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
    {
    	UUID characteristicUuid = characteristic.getUuid();
    	
    	CallbackContext callbackContext = GetCallback(characteristicUuid, operationSubscribe);
    	
    	//If no callback, just return
      if (callbackContext == null)
      {
        return;
      }
      
      JSONObject returnObj = new JSONObject();
      addProperty(returnObj, keyServiceUuid, formatUuid(characteristic.getService().getUuid()));
      addProperty(returnObj, keyCharacteristicUuid, formatUuid(characteristicUuid));   
      addProperty(returnObj, keyStatus, statusSubscribedResult);
      addPropertyBytes(returnObj, keyValue, characteristic.getValue());

      //Return the characteristic value
      PluginResult result = new PluginResult(PluginResult.Status.OK, returnObj);
      result.setKeepCallback(true);
      callbackContext.sendPluginResult(result);
    }
    
    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
    {
    	UUID characteristicUuid = characteristic.getUuid();
    	
    	CallbackContext callbackContext = GetCallback(characteristicUuid, operationWrite);
    	
    	//If no callback, just return
      if (callbackContext == null)
      {
        return;
      }
      
      JSONObject returnObj = new JSONObject();
      addProperty(returnObj, keyServiceUuid, formatUuid(characteristic.getService().getUuid()));
      addProperty(returnObj, keyCharacteristicUuid, formatUuid(characteristicUuid));
      
      //If write was successful, return the written value
      if (status == BluetoothGatt.GATT_SUCCESS)
      {
        addProperty(returnObj, keyStatus, statusWritten);
        addPropertyBytes(returnObj, keyValue, characteristic.getValue());
        callbackContext.success(returnObj);
      }
      //Else it failed
      else
      {
      	addProperty(returnObj, keyError, errorWrite);
        addProperty(returnObj, keyMessage, logWriteFailReturn);
        callbackContext.error(returnObj);
      }
      
      //Clear callback
      RemoveCallback(characteristicUuid, operationWrite);
    }
    
    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
    {
      //If callback is null, just return
      if (descriptorCallbackContext == null)
      {
        return;
      }
      
    	BluetoothGattCharacteristic characteristic = descriptor.getCharacteristic();
      
      JSONObject returnObj = new JSONObject();
      
      addProperty(returnObj, keyServiceUuid, formatUuid(characteristic.getService().getUuid()));
      addProperty(returnObj, keyCharacteristicUuid, formatUuid(characteristic.getUuid()));   
      addProperty(returnObj, keyDescriptorUuid, formatUuid(descriptor.getUuid()));
      
      //If descriptor was successful, return the written value
      if (status == BluetoothGatt.GATT_SUCCESS)
      {
        addProperty(returnObj, keyStatus, statusReadDescriptor);
        addPropertyBytes(returnObj, keyValue, descriptor.getValue());
        descriptorCallbackContext.success(returnObj);
      }
      //Else it failed
      else
      {
      	addProperty(returnObj, keyError, errorReadDescriptor);
        addProperty(returnObj, keyMessage, logReadDescriptorFailReturn);
        descriptorCallbackContext.error(returnObj);
      }

      //Clear callback
      descriptorCallbackContext = null;
    }
  
    @Override
    public void onDescriptorWrite (BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
    {      
    	BluetoothGattCharacteristic characteristic = descriptor.getCharacteristic();
    	UUID characteristicUuid = characteristic.getUuid();
      
      JSONObject returnObj = new JSONObject();
      
      addProperty(returnObj, keyServiceUuid, formatUuid(characteristic.getService().getUuid()));
      addProperty(returnObj, keyCharacteristicUuid, formatUuid(characteristicUuid));  
      
      //See if notification/indication is enabled or disabled and use subscribe/unsubscribe callback instead
      if (descriptor.getUuid().equals(clientConfigurationDescriptorUuid))
      {
      	
      	if (descriptor.getValue() == BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)
      	{
      		CallbackContext callbackContext = GetCallback(characteristicUuid, operationUnsubscribe);
        	
        	//If no callback, just return
          if (callbackContext == null)
          {
            return;
          }
          
      		//Get the unsubscribed operation callback and clear
      		addProperty(returnObj, keyStatus, statusUnsubscribed);
      		
      		callbackContext.success(returnObj);
      	}
      	else
      	{
      		CallbackContext callbackContext = GetCallback(characteristicUuid, operationSubscribe);
        	
        	//If no callback, just return
          if (callbackContext == null)
          {
            return;
          }
          
      		addProperty(returnObj, keyStatus, statusSubscribed);
      	  
      	  PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
      	  pluginResult.setKeepCallback(true);
      	  callbackContext.sendPluginResult(pluginResult);
      	}

    	  return;
      }
      
    	//If callback is null, just return
      if (descriptorCallbackContext == null)
      {
        return;
      }
      
      addProperty(returnObj, keyDescriptorUuid, formatUuid(descriptor.getUuid()));
      
			//If descriptor was written, return written value
      if (status == BluetoothGatt.GATT_SUCCESS)
      {
      	addProperty(returnObj, keyStatus, statusWrittenDescriptor);
      	addPropertyBytes(returnObj, keyValue, descriptor.getValue());
      	descriptorCallbackContext.success(returnObj);
      }
      //Else it failed
      else
      {
      	addProperty(returnObj, keyError, errorWriteDescriptor);
        addProperty(returnObj, keyMessage, logWriteDescriptorFailReturn);
        descriptorCallbackContext.error(returnObj);
      }
      
      //Clear callback
      descriptorCallbackContext = null;
    }
  
    @Override
    public void onReadRemoteRssi (BluetoothGatt gatt, int rssi, int status)
    {
      //If no callback, just return
      if (rssiCallbackContext == null)
      {
        return;
      }
      
      JSONObject returnObj = new JSONObject();
      
      //If successfully read RSSI, return value
      if (status == BluetoothGatt.GATT_SUCCESS)
      {
        addProperty(returnObj, keyStatus, statusRssi);
        addProperty(returnObj, keyRssi, rssi);
        rssiCallbackContext.success(returnObj);
      }
      //Else it failed
      else
      {
      	addProperty(returnObj, keyError, errorRssi);
	      addProperty(returnObj, keyMessage, logRssiFailReturn);
	      rssiCallbackContext.error(returnObj);
      }
      
      //Clear callback
      rssiCallbackContext = null;
    }
  
  };
  
  private String formatUuid(UUID uuid)
  {
  	String uuidString = uuid.toString();
  	
  	if (uuidString.startsWith(baseUuidStart) && uuidString.endsWith(baseUuidEnd))
  	{
  		return uuidString.substring(4, 8);
  	}
  	
  	return uuidString;
  }
  
  //Helpers for BluetoothGatt classes
  private BluetoothGattService getService(JSONObject obj)
  {
    String uuidServiceValue = obj.optString(keyServiceUuid, null);
    
    if (uuidServiceValue == null)
    {
      return null;
    }
    
    if (uuidServiceValue.length() == 4)
    {
    	uuidServiceValue = baseUuidStart + uuidServiceValue + baseUuidEnd;
    }
    
    UUID uuidService = null;
    
    try
    {
      uuidService = UUID.fromString(uuidServiceValue);
    }
    catch (Exception ex)
    {
      return null;
    }
    
    BluetoothGattService service = bluetoothGatt.getService(uuidService);
    
    if (service == null)
    {
      return null;
    }
    
    return service;
  }
  
  private BluetoothGattCharacteristic getCharacteristic(JSONObject obj, BluetoothGattService service)
  { 
    String uuidCharacteristicValue = obj.optString(keyCharacteristicUuid, null);
    
    if (uuidCharacteristicValue == null)
    {
      return null;
    }
    
    if (uuidCharacteristicValue.length() == 4)
    {
    	uuidCharacteristicValue = baseUuidStart + uuidCharacteristicValue + baseUuidEnd;
    }
    
    UUID uuidCharacteristic = null;
    
    try
    {
      uuidCharacteristic = UUID.fromString(uuidCharacteristicValue);
    }
    catch (Exception ex)
    {
      return null;
    }
    
    BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuidCharacteristic);
    
    if (characteristic == null)
    {
      return null;
    }
    
    return characteristic;
  }

  private BluetoothGattDescriptor getDescriptor(JSONObject obj, BluetoothGattCharacteristic characteristic)
  {
    String uuidDescriptorValue = obj.optString(keyDescriptorUuid, null);
    
    if (uuidDescriptorValue == null)
    {
      return null;
    }
    
    if (uuidDescriptorValue.length() == 4)
    {
    	uuidDescriptorValue = baseUuidStart + uuidDescriptorValue + baseUuidEnd;
    }
    
    UUID uuidDescriptor = null;
    
    try
    {
      uuidDescriptor = UUID.fromString(uuidDescriptorValue);
    }
    catch (Exception ex)
    {
      return null;
    }
    
    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(uuidDescriptor);
    
    if (descriptor == null)
    {
      return null;
    }
    
    return descriptor;
  }

  //Helpers for Callbacks
  private HashMap<String, CallbackContext> EnsureCallback(UUID characteristicUuid)
  {
  	//Ensure the callback map has an entry for the characteristic
  	if (!operationCallbackContexts.containsKey(characteristicUuid))
    {
    	operationCallbackContexts.put(characteristicUuid, new HashMap<String, CallbackContext>());
    }
  	
  	return operationCallbackContexts.get(characteristicUuid);
  }
  
  private void AddCallback(UUID characteristicUuid, String operationType, CallbackContext callbackContext)
  {
  	HashMap<String, CallbackContext> characteristicCallbackContexts = EnsureCallback(characteristicUuid);
  	
  	characteristicCallbackContexts.put(operationType, callbackContext);
  }
  
  private CallbackContext GetCallback(UUID characteristicUuid, String operationType)
  {
  	HashMap<String, CallbackContext> characteristicCallbackContexts = operationCallbackContexts.get(characteristicUuid);
  	
  	if (characteristicCallbackContexts == null)
  	{
  		return null;
  	}
  	
  	//This may return null
  	return characteristicCallbackContexts.get(operationType);
  }
  
  private void RemoveCallback(UUID characteristicUuid, String operationType)
  {
  	HashMap<String, CallbackContext> characteristicCallbackContexts = operationCallbackContexts.get(characteristicUuid);
  	
  	if (characteristicCallbackContexts == null)
  	{
  		return;
  	}
  	
  	characteristicCallbackContexts.remove(operationType);
  }
  
  private void ClearOperationCallbacks()
  {
  	operationCallbackContexts = new HashMap<UUID, HashMap<String, CallbackContext>>();
    discoverCallbackContext = null;
    descriptorCallbackContext = null;
    rssiCallbackContext = null;	
  }
  
  //Helpers to Check Conditions
  private boolean isNotInitialized(CallbackContext callbackContext)
  {
    if (bluetoothAdapter == null)
    {
	    JSONObject returnObj = new JSONObject();
	    
	    addProperty(returnObj, keyError, errorInitialize);
	    addProperty(returnObj, keyMessage, logNotInit);
	    
	    callbackContext.error(returnObj);

      return true;
    }
    
    return isNotEnabled(callbackContext);
  }
  
  private boolean isNotEnabled(CallbackContext callbackContext)
  {
  	if (!bluetoothAdapter.isEnabled())
  	{
  		JSONObject returnObj = new JSONObject();
	    
	    addProperty(returnObj, keyError, errorEnable);
	    addProperty(returnObj, keyMessage, logNotEnabled);
	    
	    callbackContext.error(returnObj);

      return true;
  	}
  	
  	return false;
  }

  private boolean isNotArgsObject(JSONObject obj, CallbackContext callbackContext)
  {
  	if (obj != null)
  	{
  		return false;
  	}
  	
    JSONObject returnObj = new JSONObject();
    
    addProperty(returnObj, keyError, errorArguments);
    addProperty(returnObj, keyMessage, logNoArgObj);
    
    callbackContext.error(returnObj);
    
    return true;
  }
  
  private boolean isNotService(BluetoothGattService service, CallbackContext callbackContext)
  {
  	if (service != null)
  	{
  		return false;
  	}
  	
  	JSONObject returnObj = new JSONObject();
  	
  	addProperty(returnObj, keyError, errorService);
  	addProperty(returnObj, keyMessage, logNoService);
    
    callbackContext.error(returnObj);
    
    return true;
  }
  
  private boolean isNotCharacteristic(BluetoothGattCharacteristic characteristic, CallbackContext callbackContext)
  {
  	if (characteristic != null)
  	{
  		return false;
  	}
  	
  	JSONObject returnObj = new JSONObject();
  	
  	addProperty(returnObj, keyError, errorCharacteristic);
  	addProperty(returnObj, keyMessage, logNoCharacteristic);
    
    callbackContext.error(returnObj);
  	
  	return true;
  }
  
  private boolean isNotDescriptor(BluetoothGattDescriptor descriptor, CallbackContext callbackContext)
  {
  	if (descriptor != null)
  	{
  		return false;
  	}
  	
  	JSONObject returnObj = new JSONObject();
  	
  	addProperty(returnObj, keyError, errorDescriptor);
  	addProperty(returnObj, keyMessage, logNoDescriptor);
    
    callbackContext.error(returnObj);
    
    return true;
  }
  
  private boolean isNotDisconnected(CallbackContext callbackContext)
  {
    //Determine whether the device is currently connected including connecting and disconnecting
    //Certain actions like connect and reconnect can only be done while completely disconnected
    if (connectionState == BluetoothProfile.STATE_DISCONNECTED)
  	{
  		return false;
  	}
  	
  	JSONObject returnObj = new JSONObject();
  	
  	addProperty(returnObj, keyError, errorIsNotDisconnected);
  	addProperty(returnObj, keyMessage, logIsNotDisconnected);
    
    callbackContext.error(returnObj);
    
    return true;
  }
  
  private boolean isDisconnected(CallbackContext callbackContext)
  {
    //Determine whether the device is currently disconnected NOT including connecting and disconnecting
    //Certain actions like disconnect can be done while connected, connecting, disconnecting
  	if (connectionState != BluetoothProfile.STATE_DISCONNECTED)
  	{
  		return false;
  	}
  	
  	JSONObject returnObj = new JSONObject();
  	
  	addProperty(returnObj, keyError, errorIsDisconnected);
  	addProperty(returnObj, keyMessage, logIsDisconnected);
    
    callbackContext.error(returnObj);
    
    return true;
  }
  
  private boolean isNotConnected(CallbackContext callbackContext)
  {
    //Determine whether the device is currently disconnected including connecting and disconnecting
    //Certain actions like read/write operations can only be done while completely connected
  	if (connectionState == BluetoothProfile.STATE_CONNECTED)
  	{
  		return false;
  	}
  	
  	JSONObject returnObj = new JSONObject();
  	
  	addProperty(returnObj, keyError, errorIsNotConnected);
  	addProperty(returnObj, keyMessage, logIsNotConnected);
    
    callbackContext.error(returnObj);
    
    return true;
  }
  
  private boolean wasNeverConnected(CallbackContext callbackContext)
  {
    //Determine whether a connection was ever attempted on the device
    if (bluetoothGatt != null)
    {
      return false;
    }
    
    if (callbackContext != null)
    {
	    JSONObject returnObj = new JSONObject();
	  	
	  	addProperty(returnObj, keyError, errorNeverConnected);
	  	addProperty(returnObj, keyMessage, logNeverConnected);
	    
	    callbackContext.error(returnObj);
    }
    
    return true;
  }
  
  //General Helpers
  private void addProperty(JSONObject obj, String key, Object value)
  {
  	//Believe exception only occurs when adding duplicate keys, so just ignore it
  	try
  	{
  		obj.put(key, value);
  	}
  	catch (JSONException e)
  	{
  		
  	}
  }
  
  private void addPropertyBytes(JSONObject obj, String key, byte[] bytes)
  {
  	String string = Base64.encodeToString(bytes, Base64.NO_WRAP);
  	
  	addProperty(obj, key, string);
  }
  
  private JSONObject getArgsObject(JSONArray args)
  {
    if (args.length() == 1)
    {
      try
      {
        return args.getJSONObject(0);
      }
      catch (JSONException ex)
      {
      }
    }
    
    return null;
  }
  
  private byte[] getPropertyBytes(JSONObject obj, String key)
  {
    String string = obj.optString(key, null);
    
    if (string == null)
    {
      return null;
    }
    
    byte[] bytes = Base64.decode(string, Base64.NO_WRAP);
    
    if (bytes == null || bytes.length == 0)
    {
      return null;
    }
    
    return bytes;
  }
  
  private UUID[] getServiceUuids(JSONObject obj)
  {
    JSONArray array = obj.optJSONArray(keyServiceUuids);
    
    if (array == null)
    {
    	return null;
    }
    
	  //Create temporary array list for building array of UUIDs
	  ArrayList<UUID> arrayList = new ArrayList<UUID>();
	  
	  //Iterate through the UUID strings
	  for (int i = 0; i < array.length(); i++)
	  {
	    String value = array.optString(i, null);
	    
	    if (value == null)
	    {
	      continue;
	    }
	    
	    if (value.length() == 4)
	    {
	    	value = baseUuidStart + value + baseUuidEnd;
	    }
	    
	    
	    //Try converting string to UUID and add to list
	    try
	    {
	      UUID uuid = UUID.fromString(value);
	      arrayList.add(uuid);
	    }
	    catch (Exception ex)
	    {
	    }
	  }
	  
	  //If anything was actually added, convert list to array
	  int size = arrayList.size();
    
    if (size == 0)
    {
      return null;
    }
    
    UUID[] uuids = new UUID[size];
    uuids = arrayList.toArray(uuids);
    return uuids;
  }
  
  private String getAddress(JSONObject obj)
  {
    //Get the address string from arguments
    String address = obj.optString(keyAddress, null);
    
    if (address == null)
    {
      return null;
    }
    
    //Validate address format
    if (!BluetoothAdapter.checkBluetoothAddress(address))
    {
      return null;
    }
    
    return address;
  }
  
  private boolean getRequest(JSONObject obj)
  {
  	return obj.optBoolean(keyRequest, false);
  }
  
  private int getWriteType(JSONObject obj)
  {
  	String writeType = obj.optString(keyType, null);
    
    if (writeType == null || !writeType.equals(writeTypeNoResponse))
    {
      return BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT;
    }
    return BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE;
  }
  
  private JSONObject getDiscovery()
  {
    JSONObject deviceObject = new JSONObject();
    
    BluetoothDevice device = bluetoothGatt.getDevice();
    
    addProperty(deviceObject, keyStatus, statusDiscovered);
    addProperty(deviceObject, keyAddress, device.getAddress());
    addProperty(deviceObject, keyName, device.getName());
    
    JSONArray servicesArray = new JSONArray();
    
    List<BluetoothGattService> services = bluetoothGatt.getServices();
    
    for (BluetoothGattService service : services)
    {
      JSONObject serviceObject = new JSONObject();
      
      addProperty(serviceObject, keyServiceUuid, formatUuid(service.getUuid()));
      
      JSONArray characteristicsArray = new JSONArray();
      
      List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
      
      for (BluetoothGattCharacteristic characteristic : characteristics)
      {
        JSONObject characteristicObject = new JSONObject();
        
        addProperty(characteristicObject, keyCharacteristicUuid, formatUuid(characteristic.getUuid()));
        addProperty(characteristicObject, keyProperties, getProperties(characteristic));
        
        JSONArray descriptorsArray = new JSONArray();
        
        List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
        
        for (BluetoothGattDescriptor descriptor : descriptors)
        {
          JSONObject descriptorObject = new JSONObject();
          
          addProperty(descriptorObject, keyDescriptorUuid, formatUuid(descriptor.getUuid()));
          
          descriptorsArray.put(descriptorObject); 
        }
        
        addProperty(characteristicObject, keyDescriptors, descriptorsArray);
        
        characteristicsArray.put(characteristicObject);
      }
      
      addProperty(serviceObject, keyCharacteristics, characteristicsArray);
      
      servicesArray.put(serviceObject);
    }
    
    addProperty(deviceObject, keyServices, servicesArray);
    
    return deviceObject;
  }

  private JSONObject getProperties(BluetoothGattCharacteristic characteristic)
  {
  	int properties = characteristic.getProperties();

  	JSONObject propertiesObject = new JSONObject();
  	
  	if ((properties & BluetoothGattCharacteristic.PROPERTY_BROADCAST) == BluetoothGattCharacteristic.PROPERTY_BROADCAST)
  	{
  		addProperty(propertiesObject, propertyBroadcast, true);
  	}
  	
  	if ((properties & BluetoothGattCharacteristic.PROPERTY_READ) == BluetoothGattCharacteristic.PROPERTY_READ)
  	{
  		addProperty(propertiesObject, propertyRead, true);
  	}
  	
  	if ((properties & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) == BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)
  	{
  		addProperty(propertiesObject, propertyWriteWithoutResponse, true);
  	}
  	
  	if ((properties & BluetoothGattCharacteristic.PROPERTY_WRITE) == BluetoothGattCharacteristic.PROPERTY_WRITE)
  	{
  		addProperty(propertiesObject, propertyWrite, true);
  	}

  	if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == BluetoothGattCharacteristic.PROPERTY_NOTIFY)
  	{
  		addProperty(propertiesObject, propertyNotify, true);
  	}

  	if ((properties & BluetoothGattCharacteristic.PROPERTY_INDICATE) == BluetoothGattCharacteristic.PROPERTY_INDICATE)
  	{
  		addProperty(propertiesObject, propertyIndicate, true);
  	}
  	
  	if ((properties & BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE) == BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE)
  	{
  		addProperty(propertiesObject, propertyAuthenticatedSignedWrites, true);
  	}
  	
  	if ((properties & BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS) == BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS)
  	{
  		addProperty(propertiesObject, propertyExtendedProperties, true);
  	}
  	
  	if ((properties & 0x100) == 0x100)
  	{
  		addProperty(propertiesObject, propertyNotifyEncryptionRequired, true);
  	}

  	if ((properties & 0x200) == 0x200)
  	{
  		addProperty(propertiesObject, propertyIndicateEncryptionRequired, true);
  	}

  	return propertiesObject;
  }
}
