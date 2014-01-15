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

public class BluetoothLePlugin extends CordovaPlugin {
  
  //TODO Bubble up exceptions
  //TODO Add timeout for connected
  //TODO Return objects to end user (only use success?)
  //TODO Check for multithreaded
  //TODO Reconnect action
  //TODO Handle in background?

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
  private boolean autoDiscover = false;
  private final int STATE_UNDISCOVERED = 0;
  private final int STATE_DISCOVERING = 1;
  private final int STATE_DISCOVERED = 2;
  private int discoveredState = STATE_UNDISCOVERED;
  
  //Heart Rate specific, but this should be made more generic
  private final static UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
  private final static UUID UUID_CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
  
  //Activity state (read, subscribe, write)
  private final int STATE_INACTIVE = 0;
  private final int STATE_READ = 1;
  private final int STATE_SUBSCRIBE = 2;
  private final int STATE_WRITE = 3;
  private int activityState = STATE_INACTIVE;
  
  //private String writeValue = null;
  
  //Action Name Strings
  private final String initActionName = "init";
  private final String startScanActionName = "startScan";
  private final String stopScanActionName = "stopScan";
  private final String connectActionName = "connect";
  private final String disconnectActionName = "disconnect";
  private final String closeActionName = "close";
  private final String discoverActionName = "discover";
  private final String subscribeActionName = "subscribe";
  private final String unsubscribeActionName = "unsubscribe";
  private final String readActionName = "read";
  private final String writeActionName = "write";
  private final String characteristicsActionName = "characteristics";
  private final String isDiscoveredActionName = "isDiscovered";
  private final String isConnectedActionName = "isConnected";
  
  //Log and Callback Message Strings
  private final String logBtNotEnabled = "Bluetooth not enabled";
  private final String logBtEnabled = "Bluetooth enabled";
  private final String logBtNotEnabledUser = "Bluetooth not enabled by user";
  private final String logBtAlreadyInit = "Bluetooth already initialized";
  private final String logBtNotSupported = "Hardware doesn't support Bluetooth LE";
  private final String logBtEnableRequested = "Bluetooth enablement requested";
  private final String logBtNotInit = "Bluetooth not initialized";
  private final String logBtScanning = "Scanning already in progress";
  private final String logArgsUuid = "Arguments didn't contain valid UUIDs";
  private final String logArgsScanLimit = "Arguments didn't contain valid scan limit";
  private final String logScanStop = "Scan stopped";
  private final String logScanStopAuto = "Scan stopped automatically";
  private final String logScanStartWithUuids = "Scan started with UUIDs";
  private final String logScanStartWithoutUuids = "Scan started without UUIDs";
  private final String logNotScanning = "Not scanning";
  private final String logDeviceFound = "Device found: {0}";
  private final String logScanResultError = "Error forming scan result JSON object";
  
  //Callback Object Strings
  private final String statusKey = "status";
  private final String nameKey = "name";
  private final String addressKey = "address";
  private final String classKey = "class";
  private final String statusStopScan = "stopScan";
  
  @Override
  public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException
  {
    //TODO Verify which ones need new threads
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
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          startScanAction(args, callbackContext);
        }
      });
      
      return true;
    }
    else if (stopScanActionName.equals(action)) 
    {
      stopScanAction(callbackContext);
      
      return true;
    }
    else if (connectActionName.equals(action))
    {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          connectAction(args, callbackContext);
        }
      });
      return true;
    }
    else if (disconnectActionName.equals(action))
    {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          disconnectAction(callbackContext);
        }
      });
      return true;      
    }
    else if (closeActionName.equals(action))
    {
      closeAction(callbackContext);
      return true;
    }
    else if (discoverActionName.equals(action))
    {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          discoverAction(callbackContext);
        }
      });
      return true;
    }
    else if (characteristicsActionName.equals(action))
    {
      characteristicsAction(args, callbackContext);
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
    //Dont allow this function to be called multiple times
    if (bluetoothAdapter != null)
    {
      Log.d(TAG, logBtAlreadyInit);
      callbackContext.error(logBtAlreadyInit);
      return;
    }
    
    //Check whether the device supports Bluetooth LE
    //Not necessary if app manifest contains: <uses-feature android:name="android.hardware.bluetooth_le" android:required="true"/>
    if (!cordova.getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
    {
      Log.d(TAG, logBtNotSupported);
      callbackContext.error(logBtNotSupported);
      return;
    }
    
    //Get Bluetooth adapter via Bluetooth Manager
    BluetoothManager bluetoothManager = (BluetoothManager) cordova.getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
    bluetoothAdapter = bluetoothManager.getAdapter();

    //Check if bluetooth adapter is null or disabled
    if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled())
    {
      Log.d(TAG, logBtEnableRequested);
      
      //Request Bluetooth be enabled
      initCallbackContext = callbackContext;
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      cordova.startActivityForResult(this, enableBtIntent, REQUEST_BT_ENABLE);
    }
    else
    {
      Log.d(TAG, logBtEnabled);
      callbackContext.success(logBtEnabled);
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
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled())
        {
          Log.d(TAG, logBtNotEnabled);
          initCallbackContext.error(logBtNotEnabled);
          //Set to null just in case
          bluetoothAdapter = null;
        }
        //Bluetooth was enabled
        else
        {
          Log.d(TAG, logBtEnabled);
          initCallbackContext.success(logBtEnabled);
        }
      }
      //Else user didn't enable Bluetooth
      else
      {
        Log.d(TAG, logBtNotEnabledUser);
        initCallbackContext.error(logBtNotEnabledUser);
        bluetoothAdapter = null;
      }
      
      initCallbackContext = null;
    }
  }
  
  public void startScanAction(JSONArray args, CallbackContext callbackContext)
  {
    //Check if plugin was initialized
    if (bluetoothAdapter == null)
    {
      Log.d(TAG, logBtNotInit);
      callbackContext.error(logBtNotInit);
      return;
    }
    
    //If the adapter is already scanning, don't call another scan.
    if (isScanning)
    {
      Log.d(TAG, logBtScanning);
      callbackContext.error(logBtScanning);
      return;
    }
    
    //Save the callback context for reporting back found devices
    scanCallbackContext = callbackContext;
    
    //Get arg lengths, should be two or defaults will be used
    int argsLength = args.length();
    
    //Get the service UUIDs for filtering the scan
	  UUID[] uuids = null;
	  if (argsLength > 0)
	  {
	    try
	    {
	      //Get the array of UUID strings from arguments
	      JSONArray array = args.getJSONArray(0);
	      
	      //Check whether it's null
	      if (array != null)
	      {
	        //Create temporary array list for building array of UUIDs
	        ArrayList<UUID> arrayList = new ArrayList<UUID>();
	        
	        //Iterate through the UUID strings
	        for (int i = 0; i < array.length(); i++)
	        {
	          String uuidString = array.getString(i);
	          
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
	      
	    }
	    //Invalid argument
	    catch (JSONException ex)
	    {
	      Log.d(TAG, ex.getMessage());
	    }  
	  }
	  
    //Get the scan limit from the javascript arguments
    int scanLimit = 10000;
    if (argsLength > 1)
    {
      int check = args.optInt(1);
      
      //Set scan limit unless the limit is 0
      if (check != 0)
      {
        scanLimit = check;
      }
      else
      {
        Log.d(TAG, logArgsScanLimit);
      }
    }
	  
    //Create a new thread that will stop the scanning
    scanRunnable = new Runnable()
    {
      @Override
      public void run()
      {
          Log.d(TAG, logScanStopAuto);

          //This shouldn't be null unless there's a thread race condition issue
          if (scanCallbackContext != null)
          {
            JSONObject output = new JSONObject();
            
            try
            {
              output.put(statusKey, statusStopScan);
            }
            catch (JSONException ex)
            {
              Log.d(TAG, ex.getMessage());
            }
            
            PluginResult result = new PluginResult(PluginResult.Status.OK, output);
            result.setKeepCallback(true);
            scanCallbackContext.sendPluginResult(result);
            scanCallbackContext = null;
          }
          
          //Stop the scan and reset scanning related variables
          bluetoothAdapter.stopLeScan(scanCallback);
          isScanning = false;
          scanRunnable = null;
      }
    };
    
    //Schedule the stop scan thread after X milliseconds
    scanHandler.postDelayed(scanRunnable, scanLimit);

    //Set scanning state and start the scan with or without service UUIDs
    isScanning = true;
    if (uuids == null)
    {
      Log.d(TAG, logScanStartWithoutUuids);
      bluetoothAdapter.startLeScan(scanCallback);
    }
    else
    {
      Log.d(TAG, logScanStartWithUuids);
      bluetoothAdapter.startLeScan(uuids, scanCallback);
    }
  }
  
  public void stopScanAction(CallbackContext callbackContext)
  {
    //Check if plugin was initialized
    if (bluetoothAdapter == null)
    {
      Log.d(TAG, logBtNotInit);
      callbackContext.error(logBtNotInit);
      return;
    }
    
    //TODO is isScannning necessary, can bluetoothAdapter.isDiscovering() be sued?
    
    //Check if already scanning
    if (!isScanning)
    {
      Log.d(TAG, logNotScanning);
      callbackContext.error(logNotScanning);
      return;
    }
    
    //If stopped manually, cancel the delayed callback to automatically stop the scan
    //Should both be null or both not null
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
    Log.d(TAG, logScanStop);
    callbackContext.success(logScanStop);
  }

  private LeScanCallback scanCallback = new LeScanCallback()
  {
    @Override
    public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord)
    {
      //TODO: why is this runnable here?
      cordova.getActivity().runOnUiThread(new Runnable()
      {
        @Override
        public void run() {
          Log.d(TAG, String.format(logDeviceFound, device.getName()));
          JSONObject output = new JSONObject();
          try
          {
            output.put(nameKey, device.getName());
            output.put(addressKey, device.getAddress());
            output.put(classKey, device.getBluetoothClass().getDeviceClass());
            
            PluginResult result = new PluginResult(PluginResult.Status.OK, output);
            result.setKeepCallback(true);
            scanCallbackContext.sendPluginResult(result);
          }
          catch (JSONException ex)
          {
            Log.d(TAG, logScanResultError);
          }
        }
      });
    }
  };
  
  private void connectAction(JSONArray args, CallbackContext callbackContext)
  { 
    //Ensure bluetooth device isn't currently in connected, connecting or disconnecting states
    if (connectionState != BluetoothProfile.STATE_DISCONNECTED)
    {
      Log.d(TAG, "Bluetooth device isn't disconnected");
      callbackContext.error("Bluetooth device isn't disconnected.");
      return;
    }
    
    int argsLength = args.length();
    
    //Get the address string from arguments
    String address = null;
    if (argsLength > 0)
    {
      try
      {
        address = args.getString(0);
      }
      catch (JSONException ex)
      {
        callbackContext.error(ex.getMessage());
        Log.d(TAG, ex.getMessage());
        return;
      }
    }
    
    //Validate address format
    if (!BluetoothAdapter.checkBluetoothAddress(address))
    {
      callbackContext.error("Invalid address");
      Log.d(TAG, "Invalid address");
      return;
    }
    
    //Get the device
    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
    
    //If device wasn't found...
    if (device == null)
    {
      callbackContext.error("Device not found: " + address);
      Log.d(TAG, "Device not found: " + address);
      return;
    }
    
    //See if autodiscover is set to true
    if (argsLength > 1)
    {
      try
      {
        autoDiscover = args.getBoolean(1);
      }
      catch (JSONException ex)
      {
        Log.d(TAG, ex.getMessage());
      }
    }
    
    bluetoothGatt = device.connectGatt(cordova.getActivity().getApplicationContext(), false, gattCallback);
    connectionState = BluetoothProfile.STATE_CONNECTING;
    connectCallbackContext = callbackContext;
  }
  
  private void disconnectAction(CallbackContext callbackContext)
  {
    if (bluetoothGatt == null)
    {
      Log.d(TAG, "Bluetooth gatt was null");
      callbackContext.success("Bluetooth GATT was null");
      return;
    }
    
    //TODO Determine if unsubscribe is needed
    subscribeCallbackContext = null;
    readCallbackContext = null;
    writeCallbackContext = null;
    
    activityState = STATE_INACTIVE;
    
    bluetoothGatt.disconnect();
    connectionState = BluetoothProfile.STATE_DISCONNECTING;
    disconnectCallbackContext = callbackContext;
  }
  
  private void closeAction(CallbackContext callbackContext)
  {
    //TODO Determine if unsubscribe and disconnect need to be implicity called
    if (bluetoothGatt == null)
    { 
      Log.d(TAG, "Bluetooth gatt was null");
      callbackContext.success("Bluetooth GATT was null");
      return;
    }
    
    activityState = STATE_INACTIVE;
    
    if (connectionState != BluetoothProfile.STATE_DISCONNECTED)
    {
      Log.d(TAG, "Must disconnect before closing...");
      bluetoothGatt.disconnect();
      connectionState = BluetoothProfile.STATE_DISCONNECTED;
    }
    
    bluetoothGatt.close();
    bluetoothGatt = null;
    connectCallbackContext = null;
    disconnectCallbackContext = null;
    
    subscribeCallbackContext = null;
    readCallbackContext = null;
    writeCallbackContext = null;
    
    callbackContext.success("Bluetooth GATT closed");
  }
  
  private void discoverAction(CallbackContext callbackContext)
  {
    if (bluetoothGatt == null)
    {
      callbackContext.error("Discover: GATT is null");
      return;
    }
    
    if (connectionState != BluetoothAdapter.STATE_CONNECTED)
    {
      callbackContext.error("Discover: GATT isn't connected");
      return;
    }
    
    //TODO: Is this still accessible if disconnected?
    if (discoveredState != STATE_UNDISCOVERED)
    {
      //Build services
      callbackContext.success("Services already discovered");
      return;
    }
    
    discoveredState = STATE_DISCOVERING;
    bluetoothGatt.discoverServices();
    discoverCallbackContext = callbackContext;
  }
  
  private void subscribeAction(JSONArray args, CallbackContext callbackContext)
  {
    BluetoothGattCharacteristic characteristic = getCharacteristic(args, callbackContext);
    
    if (characteristic == null)
    {
      return;
    }
    
    boolean subscribed = bluetoothGatt.setCharacteristicNotification(characteristic, true);
    
    if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid()))
    {
      //TODO Error handling and callback
      BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID_CLIENT_CHARACTERISTIC_CONFIG);
      descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
      bluetoothGatt.writeDescriptor(descriptor);
    }
    
    if (subscribed)
    {
      subscribeCallbackContext = callbackContext;
      activityState = STATE_SUBSCRIBE;
    }
    else
    {
      callbackContext.error("Unable to subscribe");
    }
  }
  
  private void unsubscribeAction(JSONArray args, CallbackContext callbackContext)
  {
    BluetoothGattCharacteristic characteristic = getCharacteristic(args, callbackContext);
    
    if (characteristic == null)
    {
      return;
    }
    
    boolean unsubscribed = bluetoothGatt.setCharacteristicNotification(characteristic, false);
    
    activityState = STATE_INACTIVE;
    subscribeCallbackContext = null;
    
    if (unsubscribed)
    {
      callbackContext.success("Unsubscribed");
    }
    else
    {
      callbackContext.error("Unable to unsubscribe");
    }
    
  }

  private void readAction(JSONArray args, CallbackContext callbackContext)
  {
    BluetoothGattCharacteristic characteristic = getCharacteristic(args, callbackContext);
    
    if (characteristic == null)
    {
      return;
    }
    
    boolean read = bluetoothGatt.readCharacteristic(characteristic);
    
    if (read)
    {
      readCallbackContext = callbackContext;
      activityState = STATE_READ;
    }
    else
    {
      callbackContext.error("Unable to read");
    } 
  }
  
  private void writeAction(JSONArray args, CallbackContext callbackContext)
  {
    BluetoothGattCharacteristic characteristic = getCharacteristic(args, callbackContext);
    
    if (characteristic == null)
    {
      return;
    }
    
    if (args.length() != 3)
    {
      callbackContext.error("Invalid number of arguments");
      return;
    }
    
    //TODO: Get uuids and value (determing type) and set characteristic value
    
    boolean write = bluetoothGatt.writeCharacteristic(characteristic);
    
    if (write)
    {
      writeCallbackContext = callbackContext;
      activityState = STATE_INACTIVE;
    }
    else
    {
      callbackContext.error("Unable to write");
    } 
  }
  
  private void characteristicsAction(JSONArray args, CallbackContext callbackContext)
  {
    BluetoothGattService service = getService(args, callbackContext);
    
    if (service == null)
    {
      return;
    }
    
    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
    
    JSONArray array = new JSONArray();
    
    for (BluetoothGattCharacteristic characteristic : characteristics)
    {
      array.put(characteristic.getUuid().toString());
    }
    
    callbackContext.success(array);
  }
  
  private void isConnectedAction(CallbackContext callbackContext)
  {
    if (connectionState == BluetoothAdapter.STATE_CONNECTED)
    {
      callbackContext.success("Bluetooth is connected to a device.");
    }
    else
    {
      callbackContext.error("Bluetooth isn't connected to a device.");
    }
  }
  
  private void isDiscoveredAction(CallbackContext callbackContext)
  {
    if (discoveredState == STATE_DISCOVERED)
    {
      callbackContext.success("Bluetooth has discovered services.");
    }
    else
    {
      callbackContext.error("Bluetooth hasn't discovered device services.");
    }
  }
  
  //TODO: Combine with getCharacteristic
  private BluetoothGattService getService(JSONArray args, CallbackContext callbackContext)
  {
    String uuidServiceString = null;
    
    if (args.length() == 1)
    {
      try
      {
        uuidServiceString = args.getString(0);
      }
      catch (JSONException ex)
      {
        callbackContext.error("Invalid string argument");
        Log.d(TAG, "Invalid string argument");
        return null;
      }
    }
    
    UUID uuidService = null;
    
    try
    {
      uuidService = UUID.fromString(uuidServiceString);
    }
    catch (NullPointerException ex)
    {
      Log.d(TAG, "UUID was null");
      callbackContext.error("UUID was null");
      return null;
    }
    catch (IllegalArgumentException ex)
    {
      Log.d(TAG, "UUID had illegal argument");
      callbackContext.error("UUID had illegal argument");
      return null;
    }
    
    if (bluetoothGatt == null)
    {
      Log.d(TAG, "GATT is null");
      callbackContext.error("GATT is null");
      return null;
    }
    
    //Check if connected
    if (connectionState != BluetoothAdapter.STATE_CONNECTED)
    {
      Log.d(TAG, "GATT isn't connected");
      callbackContext.error("GATT isn't connected");
    }
    
    BluetoothGattService service = bluetoothGatt.getService(uuidService);
    
    if (service == null)
    {
      Log.d(TAG, "Service doesn't exist");
      callbackContext.error("Service doesn't exist");
      return null;
    }
    
    return service;
  }
  
  private BluetoothGattCharacteristic getCharacteristic(JSONArray args, CallbackContext callbackContext)
  {
    String uuidServiceString = null;
    String uuidCharacteristicString = null;
    
    if (args.length() >= 2)
    {
      try
      {
        uuidServiceString = args.getString(0);
        uuidCharacteristicString = args.getString(1);
      }
      catch (JSONException ex)
      {
        callbackContext.error("Invalid string argument");
        Log.d(TAG, "Invalid string argument");
        return null;
      }
    }
    
    UUID uuidService = null;
    UUID uuidCharacteristic = null;
    
    try
    {
      uuidService = UUID.fromString(uuidServiceString);
      uuidCharacteristic = UUID.fromString(uuidCharacteristicString);
    }
    catch (NullPointerException ex)
    {
      Log.d(TAG, "UUID was null");
      callbackContext.error("UUID was null");
      return null;
    }
    catch (IllegalArgumentException ex)
    {
      Log.d(TAG, "UUID had illegal argument");
      callbackContext.error("UUID had illegal argument");
      return null;
    }
    
    if (bluetoothGatt == null)
    {
      Log.d(TAG, "GATT is null");
      callbackContext.error("GATT is null");
      return null;
    }
    
    //Check if connected
    if (connectionState != BluetoothAdapter.STATE_CONNECTED)
    {
      Log.d(TAG, "GATT isn't connected");
      callbackContext.error("GATT isn't connected");
    }
    
    BluetoothGattService service = bluetoothGatt.getService(uuidService);
    
    if (service == null)
    {
      Log.d(TAG, "Service doesn't exist - " + uuidService.toString());
      callbackContext.error("Service doesn't exist");
      return null;
    }
    
    for (BluetoothGattCharacteristic entry : service.getCharacteristics())
    {
      Log.d(TAG, "Characteristic: " + entry.getUuid());
    }
    
    BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuidCharacteristic);
    
    if (characteristic == null)
    {
      Log.d(TAG, "Characteristic doesn't exist - " + uuidCharacteristic.toString());
      callbackContext.error("Characteristic doesn't exist");
      return null;
    }
    
    return characteristic;
  }

  //GATT callback for connecting/disconnecting, discovering, subscribing, reading and writing
  private final BluetoothGattCallback gattCallback =  new BluetoothGattCallback()
  {
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
    {
      connectionState = newState;
      BluetoothDevice device = gatt.getDevice();
      //Device was connected
      if (newState == BluetoothProfile.STATE_CONNECTED)
      {
        if (connectCallbackContext == null)
        {
          Log.d(TAG, "Successful connection without connectCallback...");
        }
        else
        {
          JSONObject output = new JSONObject();
          try
          {
            Log.d(TAG, "Device ad" + device.getAddress());
            output.put("address", device.getAddress());
            output.put("name", device.getName());
            output.put("status", "connected");
            output.put("autoDiscover", autoDiscover);
            
            PluginResult result = new PluginResult(PluginResult.Status.OK, output);
            result.setKeepCallback(true);
            connectCallbackContext.sendPluginResult(result);
          }
          catch (JSONException ex)
          {
            Log.d(TAG, "Error creating connection json object");
          }
          
          Log.d(TAG, "Connection successful!");
        }
        
        if (autoDiscover)
        {
          gatt.discoverServices();
          Log.d(TAG, "Iniated auto discover");
        }
      }
      //Device was disconnected
      else if (newState == BluetoothProfile.STATE_DISCONNECTED)
      {        
        //Device may disconnect without calling disconnect. So make sure callback isn't null
        if (disconnectCallbackContext == null)
        {
          if (connectCallbackContext != null)
          {
            Log.d(TAG, "Connected device dropped");
            JSONObject output = new JSONObject();
            try
            {
              output.put("address", device.getAddress());
              output.put("status", "disconnected");
              
              PluginResult result = new PluginResult(PluginResult.Status.OK, output);
              result.setKeepCallback(true);
              connectCallbackContext.sendPluginResult(result);
            }
            catch (JSONException ex)
            {
              Log.d(TAG, "Error creating connection json object");
            }
          }
          else
          {
            Log.d(TAG, "Shouldn't happen");
          }
        }
        else
        {
          disconnectCallbackContext.success("Disconnection successful!");
          Log.d(TAG, "Disconnection successful!");
          disconnectCallbackContext = null;
        }
        discoveredState = STATE_UNDISCOVERED;
        activityState = STATE_INACTIVE;
        autoDiscover = false;
        discoverCallbackContext = null;
        subscribeCallbackContext = null;
        readCallbackContext = null;
        writeCallbackContext = null;
      }
    }
  
    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status)
    {
      //Services discovered
      if (status == BluetoothGatt.GATT_SUCCESS)
      {
        List<BluetoothGattService> services = gatt.getServices();
        
        JSONArray array = new JSONArray();
        
        for (BluetoothGattService service : services)
        {
          array.put(service.getUuid().toString());
          Log.d(TAG, "Service Found: " + service.getUuid().toString());
        }
        
        discoveredState = STATE_DISCOVERED;
        if (discoverCallbackContext == null)
        {
          if (connectCallbackContext != null)
          {
            connectCallbackContext.success(array);
            Log.d(TAG, "Auto discovered");
          }
          else
          {
            Log.d(TAG, "Discovery shouldn't happen");
          }
        }
        else
        {
          discoverCallbackContext.success(array);
          discoverCallbackContext = null;
        }
      }
      else
      {
        discoveredState = STATE_UNDISCOVERED;
        
        if (discoverCallbackContext == null)
        {
          if (connectCallbackContext != null)
          {
            connectCallbackContext.error("Unable to auto discover");
            Log.d(TAG, "Auto discovered failed");
          }
          else
          {
            Log.d(TAG, "Discovery fail shouldn't happen");
          }
        }
        else
        {
          
          discoverCallbackContext.error("Unable to discover services");
          discoverCallbackContext = null;
        }

      }
    }
  
    @Override
    // Result of a characteristic read operation
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
    {
        if (status == BluetoothGatt.GATT_SUCCESS)
        {
          //Return read bytes
          final byte[] value = characteristic.getValue();

          readCallbackContext.success(value);
        }
        else
        {
          readCallbackContext.error("Error reading");
        }
        
        readCallbackContext = null;
        activityState = STATE_INACTIVE;
    }
    
    @Override
    // Result of a characteristic write operation
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
    {
        if (status == BluetoothGatt.GATT_SUCCESS)
        {
          //Return written bytes
          final byte[] value = characteristic.getValue();
          /*if (value != null && value.length > 0) 
          {
            final StringBuilder stringBuilder = new StringBuilder(value.length);
            for(byte byteChar : value)
              stringBuilder.append(String.format("%02X ", byteChar));
          }*/
          
          //TODO: Compare with write value
          
          writeCallbackContext.success(value);
        }
        else
        {
          writeCallbackContext.error("Error writing");
        }
        
        activityState = STATE_INACTIVE;
        writeCallbackContext = null;
    }
    
    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
    {
      //Read subscribed bytes
      final byte[] value = characteristic.getValue();
      
      PluginResult result = new PluginResult(PluginResult.Status.OK, value);
      result.setKeepCallback(true);
      subscribeCallbackContext.sendPluginResult(result);
    }
  
    @Override
    public void onDescriptorWrite (BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
    {
      if (status == BluetoothGatt.GATT_SUCCESS)
      {
        Log.d(TAG, "Descriptor set");
        //subscribeCallbackContext.success("Descriptor set");
      }
      else
      {
        Log.d(TAG, "Descriptor not set");
        //subscribeCallbackContext.error("Descriptor not set");
      }
    }
  
  };
}
