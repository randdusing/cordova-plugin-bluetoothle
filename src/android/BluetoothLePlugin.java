package com.randdusing.bluetoothle;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
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

public class BluetoothLePlugin extends CordovaPlugin 
{
  //TODO Connection timeout
  //TODO Combine callback contexts for connect and disconnect
  //TODO Verify what needs new threads
  //TODO connecting to new device, clean up should happen first
 
  //Logging related variables
  private final static String TAG = BluetoothLePlugin.class.getSimpleName();
  
  //Callback variables
  private CallbackContext initCallbackContext;
  private CallbackContext scanCallbackContext;
  private CallbackContext discoverCallbackContext;
  private CallbackContext connectCallbackContext;
  private CallbackContext disconnectCallbackContext;
  private CallbackContext subscribeCallbackContext;
  private CallbackContext readCallbackContext;
  private CallbackContext writeCallbackContext;
  private CallbackContext readDescriptorCallbackContext;
  private CallbackContext writeDescriptorCallbackContext;
  
  //Initialization related variables
  private final int REQUEST_BT_ENABLE = 59627;
  private BluetoothAdapter bluetoothAdapter;
  private BluetoothGatt bluetoothGatt;
  
  //Scanning related variables
  private boolean isScanning = false;
  private Handler scanHandler = new Handler();
  private Runnable scanRunnable = null;
      
  //Connection related variables
  private int connectionState = BluetoothProfile.STATE_DISCONNECTED;
  
  //Discovery related variables
  private final int STATE_UNDISCOVERED = 0;
  private final int STATE_DISCOVERING = 1;
  private final int STATE_DISCOVERED = 2;
  private int discoveredState = STATE_UNDISCOVERED;
  
  //Action Name Strings
  private final String initActionName = "init";
  private final String startScanActionName = "startScan";
  private final String stopScanActionName = "stopScan";
  private final String connectActionName = "connect";
  private final String disconnectActionName = "disconnect";
  private final String reconnectActionName = "reconnect";
  private final String closeActionName = "close";
  private final String discoverActionName = "discover";
  private final String subscribeActionName = "subscribe";
  private final String unsubscribeActionName = "unsubscribe";
  private final String readActionName = "read";
  private final String writeActionName = "write";
  private final String readDescriptorActionName = "readDescriptor";
  private final String writeDescriptorActionName = "writeDescriptor";
  private final String isDiscoveredActionName = "isDiscovered";
  private final String isConnectedActionName = "isConnected";
  
  //Log and Callback Message Strings
  private final String logBtNotEnabled = "Bluetooth not enabled";
  private final String logBtNotEnabledUser = "Bluetooth not enabled by user";
  private final String logBtNotSupported = "Hardware doesn't support Bluetooth LE";
  private final String logBtNotInit = "Bluetooth not initialized";
  private final String logAlreadyScanning = "Scanning already in progress";
  private final String logArgsUuid = "Arguments didn't contain valid UUIDs";
  private final String logScanStartFail = "Scan failed to start";
  private final String logNotScanning = "Not scanning";
  private final String logGattConnected = "GATT connected";
  private final String logNoAddress = "No device address";
  private final String logNoDevice = "Device not found";
  private final String logGattNull = "GATT is null";
  private final String logReconnectionDisconnecting = "GATT failed to reconnect due to currently disconnecting";
  private final String logReconnectionFail = "GATT failed to reconnet";
  private final String logNoArgObj = "Argument object not found";
  private final String logNoService = "Service not found";
  private final String logNoCharacteristic = "Characteristic not found";
  private final String logNoDescriptor = "Descriptor not found";
  private final String logAlreadyDiscovering = "Already discovering device";
  private final String logSubscribeFail = "Unable to subscribe";
  private final String logUnsubscribeFail = "Unable to unsubscribe";
  private final String logReadFail = "Unable to read";
  private final String logWriteFail = "Unable to write";
  private final String logWriteValueNotFound = "Write value not found";
  private final String logWriteValueNotSet = "Write value not set";
  private final String logReadDescriptorFail = "Unable to read descriptor";
  private final String logWriteDescriptorFail = "Unable to write descriptor";
  private final String logWriteDescriptorValueNotFound = "Write descriptor value not found";
  private final String logWriteDescriptorValueNotSet = "Write descriptor value not set";
  private final String logNotConnected = "Device not connected";
  private final String logNotDiscovered = "Device not discovered";
  private final String logDescriptorNotRead = "Descriptor not read";
  private final String logDescriptorNotWritten = "Descriptor not written";
  private final String logReadFailReturn = "Unable to read on return";
  private final String logWriteFailReturn = "Unable to write on return";
  private final String logDiscoveryFail = "Unable to discover device";
  
  //Callback Object Strings
  private final String keyStatus = "status";
  private final String keyName = "name";
  private final String keyAddress = "address";
  private final String keyClass = "class";
  private final String keyServiceUuid = "serviceUuid";
  private final String keyCharacteristicUuid = "characteristicUuid";
  private final String keyDescriptorUuid = "descriptorUuid";
  private final String keyServiceUuids = "serviceUuids";
  private final String keyScanLimit = "scanLimit";
  private final String keyValue = "value";
  private final String keyUuid = "uuid";
  private final String keyServices = "services";
  private final String keyCharacteristics = "characteristics";
  private final String keyDescriptors = "descriptors";
  
  private final String statusScanStart = "scanStart";
  private final String statusScanStop = "scanStop";
  private final String statusScanResult = "scanResult";
  private final String statusConnected = "connected";
  private final String statusConnecting = "connecting";
  private final String statusDisconnected = "disconnected";

  private final String valueEnableNotification = "EnableNotification";
  private final String valueEnableIndication = "EnableIndication";
  private final String valueEisableNotification = "DisableNotification";

  @Override
  public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException
  {
    //Execute the specified action
    if (initActionName.equals(action))
    {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          initAction(callbackContext);
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
    else if (disconnectActionName.equals(action))
    {
      disconnectAction(callbackContext);
      return true;      
    }
    else if (reconnectActionName.equals(action))
    {
     reconnectAction(callbackContext);
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
    else if (readActionName.equals(action))
    {
      readAction(args, callbackContext);
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
  
  public void initAction(CallbackContext callbackContext)
  {
    //Determine whether Bluetooth is already initialized
    if (bluetoothAdapter != null && bluetoothAdapter.isEnabled())
    {
      callbackContext.success();
    }
    
    //Check whether the device supports Bluetooth LE
    //Not necessary if app manifest contains: <uses-feature android:name="android.hardware.bluetooth_le" android:required="true"/>
    if (!cordova.getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
    {
      callbackContext.error(logBtNotSupported);
      return;
    }
    
    //Get Bluetooth adapter via Bluetooth Manager
    BluetoothManager bluetoothManager = (BluetoothManager) cordova.getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
    bluetoothAdapter = bluetoothManager.getAdapter();

    //Check if bluetooth adapter is null or disabled
    if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled())
    {
      //Request Bluetooth be enabled
      initCallbackContext = callbackContext;
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      cordova.startActivityForResult(this, enableBtIntent, REQUEST_BT_ENABLE);
    }
    //Else successful
    else
    {
      callbackContext.success();
    }
  }
  
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent intent)
  {
    //If this was a Bluetooth enablement request...
    if (requestCode == REQUEST_BT_ENABLE)
    {
      //If Bluetooth was enabled...
      if (resultCode == Activity.RESULT_OK)
      {
        //After requesting, check again whether it's enabled
        BluetoothManager bluetoothManager = (BluetoothManager) cordova.getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        //Bluetooth wasn't enabled
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled())
        {
          initCallbackContext.error(logBtNotEnabled);
        }
        //Bluetooth was enabled
        else
        {
          initCallbackContext.success();
        }
      }
      //Else user didn't enable Bluetooth
      else
      {
        initCallbackContext.error(logBtNotEnabledUser);
      }
      
      initCallbackContext = null;
    }
  }
  
  public void startScanAction(JSONArray args, CallbackContext callbackContext)
  {
    //Check if Bluetooth was initialized
    if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled())
    {
      callbackContext.error(logBtNotInit);
      return;
    }
    
    //If the adapter is already scanning, don't call another scan.
    if (isScanning)
    {
      callbackContext.error(logAlreadyScanning);
      return;
    }
    
    //Get any passed arguments
    JSONObject obj = getArgsObject(args);
    
    UUID[] serviceUuids = null;
    int scanLimit = 10000;
    
    if (obj != null)
    {
      serviceUuids = getServiceUuids(obj);
      int checkScanLimit = getScanLimit(obj);
      if (checkScanLimit > 0)
      {
        scanLimit = checkScanLimit;
      }
    }

    //Start the scan with or without service UUIDs
    boolean result = false;
    if (serviceUuids == null)
    {
      Log.d(TAG, "without uuids");
      result = bluetoothAdapter.startLeScan(scanCallback);
    }
    else
    {
      Log.d(TAG, "with uuids");
      result = bluetoothAdapter.startLeScan(serviceUuids, scanCallback);
    }
    
    if (!result)
    {
      callbackContext.error(logScanStartFail);
      return;
    }
    
    //Set scanning state
    isScanning = true;
    
    JSONObject returnObj = new JSONObject();
    try
    {
      returnObj.put(keyStatus, statusScanStart);
    } 
    catch (JSONException e) 
    {
    }
    
    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
    pluginResult.setKeepCallback(true);
    callbackContext.sendPluginResult(pluginResult);
    
    //Save the callback context for reporting back found devices
    scanCallbackContext = callbackContext;
    
    //Create a new thread that will stop the scanning
    scanRunnable = new Runnable()
    {
      @Override
      public void run()
      {
        //Stop the scan and reset scanning related variables
        bluetoothAdapter.stopLeScan(scanCallback);
        isScanning = false;
        scanRunnable = null;
        
        //This shouldn't be null unless there's a thread race condition issue
        if (scanCallbackContext != null)
        {
          JSONObject returnObj = new JSONObject();
          
          try
          {
            returnObj.put(keyStatus, statusScanStop);
          }
          catch (JSONException e)
          {
          }
          
          scanCallbackContext.success(returnObj);
          scanCallbackContext = null;
        }
      }
    };
    
    //Schedule the stop scan thread after X milliseconds
    scanHandler.postDelayed(scanRunnable, scanLimit);
  }
  
  public void stopScanAction(CallbackContext callbackContext)
  {
    //Check if plugin was initialized
    if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled())
    {
      callbackContext.error(logBtNotInit);
      return;
    }
    
    //Check if already scanning
    if (!isScanning)
    {
      callbackContext.error(logNotScanning);
      return;
    }
    
    //If stopped manually, cancel the delayed callback to automatically stop the scan
    if (scanRunnable != null)
    {
      scanHandler.removeCallbacks(scanRunnable); 
    }
    
    //Remove the scanCallbackContext, scanRunnable and scanHandler references. Really should be in block above.
    scanCallbackContext = null;
    scanRunnable = null;
    
    //Set scanning state
    isScanning = false;
    
    bluetoothAdapter.stopLeScan(scanCallback);

    callbackContext.success();
  }

  private LeScanCallback scanCallback = new LeScanCallback()
  {
    @Override
    public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord)
    {
      JSONObject returnObj = new JSONObject();
      try
      {
        returnObj.put(keyName, device.getName());
        returnObj.put(keyAddress, device.getAddress());
        returnObj.put(keyClass, device.getBluetoothClass().getDeviceClass());
        returnObj.put(keyStatus, statusScanResult);
        
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
        pluginResult.setKeepCallback(true);
        scanCallbackContext.sendPluginResult(pluginResult);
      }
      catch (JSONException e)
      {
      }
    }
  };
  
  private void connectAction(JSONArray args, CallbackContext callbackContext)
  { 
    //Check if plugin was initialized
    if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled())
    {
      callbackContext.error(logBtNotInit);
      return;
    }
    
    //Ensure bluetooth device isn't currently in connected, connecting or disconnecting states
    if (connectionState != BluetoothProfile.STATE_DISCONNECTED)
    {
      callbackContext.error(logGattConnected);
      return;
    }
    
    //Get argument object
    JSONObject obj = getArgsObject(args);
    
    if (obj == null)
    {
      callbackContext.error(logNoArgObj);
      return;
    }
    
    //Get address
    String address = getAddress(obj);
    
    if (address == null)
    {
      callbackContext.error(logNoAddress);
      return;
    }
    
    //Get the device
    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
    
    //If device wasn't found...
    if (device == null)
    {
      callbackContext.error(logNoDevice);
      return;
    }
    
    bluetoothGatt = device.connectGatt(cordova.getActivity().getApplicationContext(), false, gattCallback);
    connectionState = BluetoothProfile.STATE_CONNECTING;
    connectCallbackContext = callbackContext;
  }
  
  private void disconnectAction(CallbackContext callbackContext)
  {
    //Check if gatt was ever even connected
    if (bluetoothGatt == null)
    {
      callbackContext.success(logGattNull);
      return;
    }
    
    //Clear the contexts
    connectCallbackContext = null;
    discoverCallbackContext = null;
    subscribeCallbackContext = null;
    readCallbackContext = null;
    writeCallbackContext = null;
    readDescriptorCallbackContext = null;
    writeDescriptorCallbackContext = null;

    //Call disconnect and change connection station
    bluetoothGatt.disconnect();
    connectionState = BluetoothProfile.STATE_DISCONNECTING;
    disconnectCallbackContext = callbackContext;
  }

  private void reconnectAction(CallbackContext callbackContext)
  {
    //Check if bluetooth gatt was ever connected
    if (bluetoothGatt == null)
    { 
      callbackContext.error(logGattNull);
      return;
    }
    
    //See whether gatt is already connected
    if (connectionState == BluetoothProfile.STATE_CONNECTED)
    {
      JSONObject returnObj = new JSONObject();
      try
      {
        returnObj.put(keyStatus, statusConnected);
        
        callbackContext.success(returnObj);
      }
      catch (JSONException e)
      {
      }
      return;
    }
    else if (connectionState == BluetoothProfile.STATE_CONNECTING)
    {
      JSONObject returnObj = new JSONObject();
      try
      {
        returnObj.put(keyStatus, statusConnecting);
        
        callbackContext.success(returnObj);
      }
      catch (JSONException e)
      {
      }
      return;
    }
    else if (connectionState == BluetoothProfile.STATE_DISCONNECTING)
    {
      callbackContext.error(logReconnectionDisconnecting);
      return;
    }
    
    boolean result = bluetoothGatt.connect();
    
    if (result)
    {
      connectionState = BluetoothProfile.STATE_CONNECTING;
      connectCallbackContext = callbackContext;
    }
    else
    {
      callbackContext.error(logReconnectionFail);
    }
  }
  
  private void closeAction(CallbackContext callbackContext)
  {
    //Check if bluetooth gatt was ever connected
    if (bluetoothGatt == null)
    { 
      callbackContext.success(logGattNull);
      return;
    }
    
    //Reset states
    connectionState = BluetoothProfile.STATE_DISCONNECTED;
    discoveredState = STATE_UNDISCOVERED;
    
    //Reset callback contexts
    connectCallbackContext = null;
    disconnectCallbackContext = null;
    discoverCallbackContext = null;
    subscribeCallbackContext = null;
    readCallbackContext = null;
    writeCallbackContext = null;
    readDescriptorCallbackContext = null;
    writeDescriptorCallbackContext = null;
    
    //Close and reset gatt
    bluetoothGatt.close();
    bluetoothGatt = null;

    callbackContext.success();
  }
  
  private void discoverAction(CallbackContext callbackContext)
  {
    //Check if gatt was ever even connected
    if (bluetoothGatt == null)
    {
      Log.d(TAG, logGattNull);
      callbackContext.success(logGattNull);
      return;
    }
    
    if (discoveredState == STATE_DISCOVERING)
    {
      callbackContext.error(logAlreadyDiscovering);
      return;
    }
    else if (discoveredState == STATE_DISCOVERED)
    {
      JSONObject returnObject = getDiscovery();
      callbackContext.success(returnObject);
      return;
    }
    
    //Start discovery
    discoveredState = STATE_DISCOVERING;
    bluetoothGatt.discoverServices();
    discoverCallbackContext = callbackContext;
  }
  
  private void subscribeAction(JSONArray args, CallbackContext callbackContext)
  {
    JSONObject obj = getArgsObject(args);
    
    if (obj == null)
    {
      callbackContext.error(logNoArgObj);
      return;
    }
    
    BluetoothGattService service = getService(obj);
    
    if (service == null)
    {
      callbackContext.error(logNoService);
      return;
    }
    
    BluetoothGattCharacteristic characteristic = getCharacteristic(obj, service);
    
    if (characteristic == null)
    {
      callbackContext.error(logNoCharacteristic);
      return;
    }
    
    boolean result = bluetoothGatt.setCharacteristicNotification(characteristic, true);
    
    if (result)
    {
      subscribeCallbackContext = callbackContext;
    }
    else
    {
      callbackContext.error(logSubscribeFail);
    }
  }
  
  private void unsubscribeAction(JSONArray args, CallbackContext callbackContext)
  {
    JSONObject obj = getArgsObject(args);
    
    if (obj == null)
    {
      callbackContext.error(logNoArgObj);
      return;
    }
    
    BluetoothGattService service = getService(obj);
    
    if (service == null)
    {
      callbackContext.error(logNoService);
      return;
    }
    
    BluetoothGattCharacteristic characteristic = getCharacteristic(obj, service);
    
    if (characteristic == null)
    {
      callbackContext.error(logNoCharacteristic);
      return;
    }
    
    boolean result = bluetoothGatt.setCharacteristicNotification(characteristic, false);
    
    subscribeCallbackContext = null;
    
    if (result)
    {
      callbackContext.success();
    }
    else
    {
      callbackContext.error(logUnsubscribeFail);
    }
    
  }

  private void readAction(JSONArray args, CallbackContext callbackContext)
  {
    JSONObject obj = getArgsObject(args);
    
    if (obj == null)
    {
      callbackContext.error(logNoArgObj);
      return;
    }
    
    BluetoothGattService service = getService(obj);
    
    if (service == null)
    {
      callbackContext.error(logNoService);
      return;
    }
    
    BluetoothGattCharacteristic characteristic = getCharacteristic(obj, service);
    
    if (characteristic == null)
    {
      callbackContext.error(logNoCharacteristic);
      return;
    }
    
    boolean result = bluetoothGatt.readCharacteristic(characteristic);
    
    if (result)
    {
      readCallbackContext = callbackContext;
    }
    else
    {
      callbackContext.error(logReadFail);
    } 
  }
  
  private void writeAction(JSONArray args, CallbackContext callbackContext)
  {
    JSONObject obj = getArgsObject(args);
    
    if (obj == null)
    {
      callbackContext.error(logNoArgObj);
      return;
    }
    
    BluetoothGattService service = getService(obj);
    
    if (service == null)
    {
      callbackContext.error(logNoService);
      return;
    }
    
    BluetoothGattCharacteristic characteristic = getCharacteristic(obj, service);
    
    if (characteristic == null)
    {
      callbackContext.error(logNoCharacteristic);
      return;
    }
    
    String value = getValue(obj);
    
    if (value != null)
    {
      callbackContext.error(logWriteValueNotFound);
      return;
    }
    
    boolean result = characteristic.setValue(value);
    
    if (!result)
    {
      callbackContext.error(logWriteValueNotSet);
      return;
    }
    
    result = bluetoothGatt.writeCharacteristic(characteristic);
    
    if (result)
    {
      writeCallbackContext = callbackContext;
    }
    else
    {
      callbackContext.error(logWriteFail);
    } 
  }
  
  private void readDescriptorAction(JSONArray args, CallbackContext callbackContext)
  {
    JSONObject obj = getArgsObject(args);
    
    if (obj == null)
    {
      callbackContext.error(logNoArgObj);
      return;
    }
    
    BluetoothGattService service = getService(obj);
    
    if (service == null)
    {
      callbackContext.error(logNoService);
      return;
    }
    
    BluetoothGattCharacteristic characteristic = getCharacteristic(obj, service);
    
    if (characteristic == null)
    {
      callbackContext.error(logNoCharacteristic);
      return;
    }
    
    BluetoothGattDescriptor descriptor = getDescriptor(obj, characteristic);
    
    if (descriptor == null)
    {
      callbackContext.error(logNoDescriptor);
      return;
    }
    
    boolean result = bluetoothGatt.readDescriptor(descriptor);
    
    if (result)
    {
      readDescriptorCallbackContext = callbackContext;
    }
    else
    {
      callbackContext.error(logReadDescriptorFail);
    }  
  }
  
  private void writeDescriptorAction(JSONArray args, CallbackContext callbackContext)
  {
    JSONObject obj = getArgsObject(args);
    
    if (obj == null)
    {
      callbackContext.error(logNoArgObj);
      return;
    }
    
    BluetoothGattService service = getService(obj);
    
    if (service == null)
    {
      callbackContext.error(logNoService);
      return;
    }
    
    BluetoothGattCharacteristic characteristic = getCharacteristic(obj, service);
    
    if (characteristic == null)
    {
      callbackContext.error(logNoCharacteristic);
      return;
    }
    
    BluetoothGattDescriptor descriptor = getDescriptor(obj, characteristic);
    
    if (descriptor == null)
    {
      callbackContext.error(logNoDescriptor);
      return;
    }
    
    byte[] value = getDescriptorValue(obj);
    
    if (value == null)
    {
      callbackContext.error(logWriteDescriptorValueNotFound);
      return;
    }
    
    boolean result = descriptor.setValue(value);
    
    if (!result)
    {
      callbackContext.error(logWriteDescriptorValueNotSet);
      return;
    }
    
    result = bluetoothGatt.writeDescriptor(descriptor);
    
    if (result)
    {
      writeDescriptorCallbackContext = callbackContext;
    }
    else
    {
      callbackContext.error(logWriteDescriptorFail);
    }
  }
  
  private void isConnectedAction(CallbackContext callbackContext)
  {
    if (connectionState == BluetoothAdapter.STATE_CONNECTED)
    {
      callbackContext.success();
    }
    else
    {
      callbackContext.error(logNotConnected);
    }
  }
  
  private void isDiscoveredAction(CallbackContext callbackContext)
  {
    if (discoveredState == STATE_DISCOVERED)
    {
      callbackContext.success();
    }
    else
    {
      callbackContext.error(logNotDiscovered);
    }
  }

  private JSONObject getArgsObject(JSONArray args)
  {
    int length = args.length();
    if (length == 0)
    {
      return null;
    }
    else if (length == 1)
    {
      try
      {
        return args.getJSONObject(0);
      }
      catch (JSONException ex)
      {
        return null;
      }
    }
    else
    {
      return null;
    }
  }
  
  private String getValue(JSONObject obj)
  {
    return obj.optString(keyValue, null);
  }
  
  private UUID[] getServiceUuids(JSONObject obj)
  {
    UUID[] uuids = null; 
    
    JSONArray array = obj.optJSONArray(keyServiceUuids);
    
    //Check whether it's null
    if (array != null)
    {
      //Create temporary array list for building array of UUIDs
      ArrayList<UUID> arrayList = new ArrayList<UUID>();
      
      //Iterate through the UUID strings
      for (int i = 0; i < array.length(); i++)
      {
        String uuidString = array.optString(i, null);
        
        if (uuidString == null)
        {
          continue;
        }
        
        //Try converting string to UUID and add to list
        try
        {
          UUID uuid = UUID.fromString(uuidString);
          arrayList.add(uuid);
        }
        catch (Exception ex)
        {
          Log.d(TAG, ex.getMessage());
        }
      }
      
      //If anything was actually added, convert list to array
      int size = arrayList.size();
      if (size > 0)
      {
        uuids = new UUID[size];
        uuids = arrayList.toArray(uuids);
      }
    }
    //No UUIDS
    else
    {
      Log.d(TAG, logArgsUuid);
    }
    
    return uuids;
  }
  
  private int getScanLimit(JSONObject obj)
  {
    return obj.optInt(keyScanLimit);
  }
  
  private String getAddress(JSONObject obj)
  {
    //Get the address string from arguments
    String address = obj.optString(keyAddress, null);
    
    if (address == null)
    {
      Log.d(TAG, "Address property not set");
      return null;
    }
    
    //Validate address format
    if (!BluetoothAdapter.checkBluetoothAddress(address))
    {
      Log.d(TAG, "Invalid address");
      return null;
    }
    
    return address;
  }
  
  private JSONObject getDiscovery()
  {
    JSONObject deviceObject = new JSONObject();
    
    try
    {
      deviceObject.put(keyAddress, bluetoothGatt.getDevice().getAddress());
      deviceObject.put(keyName, bluetoothGatt.getDevice().getName());
      
      JSONArray servicesArray = new JSONArray();
      
      List<BluetoothGattService> services = bluetoothGatt.getServices();
      
      for (BluetoothGattService service : services)
      {
        JSONObject serviceObject = new JSONObject();
        
        serviceObject.put(keyUuid, service.getUuid().toString());
        
        JSONArray characteristicsArray = new JSONArray();
        
        List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
        
        for (BluetoothGattCharacteristic characteristic : characteristics)
        {
          JSONObject characteristicObject = new JSONObject();
          
          characteristicObject.put(keyUuid, characteristic.getUuid().toString());
          
          JSONArray descriptorsArray = new JSONArray();
          
          List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
          
          for (BluetoothGattDescriptor descriptor : descriptors)
          {
            JSONObject descriptorObject = new JSONObject();
            
            descriptorObject.put(keyUuid, descriptor.getUuid().toString());
            
            descriptorsArray.put(descriptorObject); 
          }
          
          characteristicObject.put(keyDescriptors, descriptorsArray);
          
          characteristicsArray.put(characteristicObject);
        }
        
        serviceObject.put(keyCharacteristics, characteristicsArray);
        
        servicesArray.put(serviceObject);
      }
      
      deviceObject.put(keyServices, servicesArray);
    }
    catch (JSONException ex)
    {
    }
    
    return deviceObject;
  }
  
  private byte[] getDescriptorValue(JSONObject obj)
  {
    String descriptorValue = obj.optString(keyValue, null);
    
    if (descriptorValue == null)
    {
      return null;
    }

    if (descriptorValue.equals(valueEnableNotification))
    {
      return BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
    }
    else if (descriptorValue.equals(valueEnableIndication))
    {
      return BluetoothGattDescriptor.ENABLE_INDICATION_VALUE;
    }
    else if (descriptorValue.equals(valueEisableNotification))
    {
      return BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
    }
    else
    {
      return null;
    }
  }
  
  private BluetoothGattService getService(JSONObject obj)
  {
    if (bluetoothGatt == null)
    {
      Log.d(TAG, "GATT is null");
      return null;
    }
    
    //Check if connected
    if (connectionState != BluetoothAdapter.STATE_CONNECTED)
    {
      Log.d(TAG, "Not connected to device");
      return null;
    }
    
    String uuidServiceString = obj.optString(keyServiceUuid, null);
    
    if (uuidServiceString == null)
    {
      Log.d(TAG, "Service UUID empty");
      return null;
    }
    
    UUID uuidService = null;
    
    try
    {
      uuidService = UUID.fromString(uuidServiceString);
    }
    catch (Exception ex)
    {
      Log.d(TAG, "Unable to create UUID from string");
      return null;
    }
    
    BluetoothGattService service = bluetoothGatt.getService(uuidService);
    
    if (service == null)
    {
      Log.d(TAG, logNoService);
      return null;
    }
    
    return service;
  }
  
  private BluetoothGattCharacteristic getCharacteristic(JSONObject obj, BluetoothGattService service)
  { 
    String uuidCharacteristicString = obj.optString(keyCharacteristicUuid, null);
    
    if (uuidCharacteristicString == null)
    {
      Log.d(TAG, "Characteristic UUID empty");
      return null;
    }
    
    UUID uuidCharacteristic = null;
    
    try
    {
      uuidCharacteristic = UUID.fromString(uuidCharacteristicString);
    }
    catch (Exception ex)
    {
      Log.d(TAG, "Unable to create UUID from string");
      return null;
    }
    
    BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuidCharacteristic);
    
    if (characteristic == null)
    {
      Log.d(TAG, logNoCharacteristic);
      return null;
    }
    
    return characteristic;
  }

  private BluetoothGattDescriptor getDescriptor(JSONObject obj, BluetoothGattCharacteristic characteristic)
  {
    String uuidDescriptorString = obj.optString(keyDescriptorUuid, null);
    
    if (uuidDescriptorString == null)
    {
      Log.d(TAG, "Descriptor UUID empty");
      return null;
    }
    
    UUID uuidDescriptor = null;
    
    try
    {
      uuidDescriptor = UUID.fromString(uuidDescriptorString);
    }
    catch (Exception ex)
    {
      Log.d(TAG, "Unable to create UUID from string");
      return null;
    }
    
    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(uuidDescriptor);
    
    if (descriptor == null)
    {
      Log.d(TAG, logNoDescriptor);
      return null;
    }
    
    return descriptor;
  }
  
  private final BluetoothGattCallback gattCallback =  new BluetoothGattCallback()
  {
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
    {
      //Get the connected device
      BluetoothDevice device = gatt.getDevice();
      
      //Device was connected
      if (newState == BluetoothProfile.STATE_CONNECTED)
      {
        if (connectCallbackContext == null)
        {
          //This shouldn't happen
        }
        else
        {
          //Create json object with address, name and connection status
          JSONObject returnObj = new JSONObject();
          try
          {
            returnObj.put(keyAddress, device.getAddress());
            returnObj.put(keyName, device.getName());
            returnObj.put(keyStatus, statusConnected);
          }
          catch (JSONException e)
          {
          }
          
          //Keep connection call back for disconnect
          PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
          pluginResult.setKeepCallback(true);
          connectCallbackContext.sendPluginResult(pluginResult);
        }
      }
      //Device was disconnected
      else if (newState == BluetoothProfile.STATE_DISCONNECTED)
      {      
        //Create return object
        JSONObject returnObj = new JSONObject();
        try
        {
          returnObj.put(keyAddress, device.getAddress());
          returnObj.put(keyName, device.getName());
          returnObj.put(keyStatus, statusDisconnected);
        }
        catch (JSONException e)
        {
        }
        
        //Disconnect initiated from device
        if (disconnectCallbackContext == null && connectCallbackContext != null)
        {
          connectCallbackContext.success(returnObj);
          connectCallbackContext = null; 
        }
        //Disconnect initiated from user
        else if (disconnectCallbackContext != null)
        {
          disconnectCallbackContext.success(returnObj);
          disconnectCallbackContext = null;
        }
        //Else shouldn't occur
        else
        {
        }
        
        discoveredState = STATE_UNDISCOVERED;
        discoverCallbackContext = null;
        subscribeCallbackContext = null;
        readCallbackContext = null;
        writeCallbackContext = null;
        readDescriptorCallbackContext = null;
        writeDescriptorCallbackContext = null;
      }
      
      //Set state of device to connected or disconnected
      connectionState = newState;
    }
  
    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status)
    {
      //Shouldn't happen, but check for null callback
      if (discoverCallbackContext == null)
      {
        discoveredState = STATE_UNDISCOVERED;
        return;
      }
      
      //If successfully discovered, return list of services, characteristics and descriptors
      if (status == BluetoothGatt.GATT_SUCCESS)
      {
        discoveredState = STATE_DISCOVERED;

        JSONObject returnObj = getDiscovery();
        discoverCallbackContext.success(returnObj);
      }
      //Else it failed
      else
      {
        discoveredState = STATE_UNDISCOVERED;
        
        discoverCallbackContext.error(logDiscoveryFail);
      }
      
      //Clear the callback
      discoverCallbackContext = null;
    }
  
    @Override
    // Result of a characteristic read operation
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
    {
      //If no callback, just return
      if (readCallbackContext == null)
      {
        return;
      }
      
      //If the read was successful, return the value
      if (status == BluetoothGatt.GATT_SUCCESS)
      {
        readCallbackContext.success(characteristic.getValue());
      }
      //Else return error with message
      else
      {
        readCallbackContext.error(logReadFailReturn);
      }
      
      //Clear callback
      readCallbackContext = null;
    }
    
    @Override
    // Result of a characteristic write operation
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
    {
      //If no callback, just return
      if (writeCallbackContext == null)
      {
        return;
      }
      
      //If write was successful, return the written value
      if (status == BluetoothGatt.GATT_SUCCESS)
      {
        writeCallbackContext.success(characteristic.getValue());
      }
      //Else return error with message
      else
      {
        writeCallbackContext.error(logWriteFailReturn);
      }
      
      //Clear callback
      writeCallbackContext = null;
    }
    
    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
    {
      //If callback is null, just return
      if (subscribeCallbackContext == null)
      {
        return;
      }
      
      //Return the characteristic value
      PluginResult result = new PluginResult(PluginResult.Status.OK, characteristic.getValue());
      result.setKeepCallback(true);
      subscribeCallbackContext.sendPluginResult(result);
    }
    
    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
    {
      //If callback is null, just return
      if (readDescriptorCallbackContext == null)
      {
        return;
      }
      
      //If descriptor was successfully read, return value
      if (status == BluetoothGatt.GATT_SUCCESS)
      {
        readDescriptorCallbackContext.success(descriptor.getValue());
      }
      //Else return error with message
      else
      {
        readDescriptorCallbackContext.error(logDescriptorNotRead);
      }
      
      //Clear callback
      readDescriptorCallbackContext = null;
    }
  
    @Override
    public void onDescriptorWrite (BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
    {
      //If callback is null, just return
      if (writeDescriptorCallbackContext == null)
      {
        return;
      }
      
      //If descriptor was written, return written value
      if (status == BluetoothGatt.GATT_SUCCESS)
      {
        writeDescriptorCallbackContext.success(descriptor.getValue());
      }
      //Else return error with message
      else
      {
        writeDescriptorCallbackContext.error(logDescriptorNotWritten);
      }
      
      //Clear callback
      writeDescriptorCallbackContext = null;
    }
  };
}
