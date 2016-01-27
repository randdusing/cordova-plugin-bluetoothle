package com.randdusing.bluetoothle;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import android.Manifest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.ParcelUuid;
import android.util.Base64;

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
import android.bluetooth.le.ScanSettings;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.BluetoothLeScanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

@SuppressWarnings("unchecked")

public class BluetoothLePlugin extends CordovaPlugin
{
  //Initialization related variables
  private final int REQUEST_BT_ENABLE = 59627; /*Random integer*/
  private final int REQUEST_ACCESS_COARSE_LOCATION = 59628;
  private BluetoothAdapter bluetoothAdapter;
  private boolean isReceiverRegistered = false;

  //General callback variables
  private CallbackContext initCallbackContext;
  private CallbackContext scanCallbackContext;
  private Object scanLock = new Object();
  private CallbackContext permissionsCallback;

  //Store connections and all their callbacks
  private HashMap<Object, HashMap<Object,Object>> connections;

  //Discovery related variables
  private final int STATE_UNDISCOVERED = 0;
  private final int STATE_DISCOVERING = 1;
  private final int STATE_DISCOVERED = 2;

  //Action Name Strings
  private final String initializeActionName = "initialize";
  private final String enableActionName = "enable";
  private final String disableActionName = "disable";
  private final String startScanActionName = "startScan";
  private final String stopScanActionName = "stopScan";
  private final String retrieveConnectedActionName = "retrieveConnected";
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
  private final String requestConnectionPriorityActionName = "requestConnectionPriority";
  private final String mtuActionName = "mtu";
  private final String hasPermissionName = "hasPermission";
  private final String requestPermissionName = "requestPermission";

  //Object keys
  private final String keyStatus = "status";
  private final String keyError = "error";
  private final String keyMessage = "message";
  private final String keyRequest = "request";
  private final String keyStatusReceiver = "statusReceiver";
  private final String keyName = "name";
  private final String keyAddress = "address";
  private final String keyRssi = "rssi";
  private final String keyScanMode = "scanMode";
  private final String keyMatchMode = "matchMode";
  private final String keyMatchNum = "matchNum";
  private final String keyCallbackType = "callbackType";
  private final String keyAdvertisement = "advertisement";
  private final String keyUuid = "uuid";
  private final String keyService = "service";
  private final String keyServices = "services";
  private final String keyCharacteristic = "characteristic";
  private final String keyCharacteristics = "characteristics";
  private final String keyProperties = "properties";
  private final String keyPermissions = "permissions";
  private final String keyDescriptor = "descriptor";
  private final String keyDescriptors = "descriptors";
  private final String keyValue = "value";
  private final String keyType = "type";
  private final String keyIsInitialized = "isInitialized";
  private final String keyIsEnabled = "isEnabled";
  private final String keyIsScanning = "isScanning";
  private final String keyIsConnected = "isConnected";
  private final String keyIsDiscovered = "isDiscovered";
  private final String keyIsNotification = "isNotification";
  private final String keyPeripheral = "peripheral";
  private final String keyState = "state";
  private final String keyDiscoveredState = "discoveredState";
  private final String keyConnectionPriority = "connectionPriority";
  private final String keyMtu = "mtu";

  //Write Types
  private final String writeTypeNoResponse = "noResponse";

  //Status Types
  private final String statusEnabled = "enabled";
  private final String statusDisabled = "disabled";
  private final String statusScanStarted = "scanStarted";
  private final String statusScanStopped = "scanStopped";
  private final String statusScanResult = "scanResult";
  private final String statusConnected = "connected";
  private final String statusDisconnected = "disconnected";
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
  private final String statusConnectionPriorityRequested = "connectionPriorityRequested";
  private final String statusMtu = "mtu";

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
  private final String propertyConnectionPriorityHigh = "high";
  private final String propertyConnectionPriorityLow = "low";
  private final String propertyConnectionPriorityBalanced = "balanced";

  //Permissions
  private final String permissionRead = "read";
  private final String permissionReadEncrypted = "readEncrypted";
  private final String permissionReadEncryptedMITM = "readEncryptedMITM";
  private final String permissionWrite = "write";
  private final String permissionWriteEncrypted = "writeEncrypted";
  private final String permissionWriteEncryptedMITM = "writeEncryptedMITM";
  private final String permissionWriteSigned = "writeSigned";
  private final String permissionWriteSignedMITM = "writeSignedMITM";

  //Error Types
  private final String errorInitialize = "initialize";
  private final String errorEnable = "enable";
  private final String errorDisable = "disable";
  private final String errorArguments = "arguments";
  private final String errorStartScan = "startScan";
  private final String errorStopScan = "stopScan";
  private final String errorConnect = "connect";
  private final String errorReconnect = "reconnect";
  private final String errorDiscover = "discover";
  private final String errorServices = "services";
  private final String errorCharacteristics = "characteristics";
  private final String errorDescriptors = "descriptors";
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
  private final String errorRequestConnectionPriority = "requestConnectPriority";
  private final String errorMtu = "mtu";

  //Error Messages
  //Initialization
  private final String logNotEnabled = "Bluetooth not enabled";
  private final String logNotDisabled = "Bluetooth not disabled";
  private final String logNotInit = "Bluetooth not initialized";
  private final String logOperationUnsupported = "Operation unsupported";
  //Scanning
  private final String logAlreadyScanning = "Scanning already in progress";
  private final String logScanStartFail = "Scan failed to start";
  private final String logNotScanning = "Not scanning";
  //Connection
  private final String logPreviouslyConnected = "Device previously connected, reconnect or close for new device";
  private final String logConnectFail = "Connection failed";
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
  private final String logSubscribeAlready = "Already subscribed";
  private final String logUnsubscribeFail = "Unable to unsubscribe";
  private final String logUnsubscribeAlready = "Already unsubscribed";
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
  //Request Connection Priority
  private final String logRequestConnectionPriorityNull = "Request connection priority not set";
  private final String logRequestConnectionPriorityInvalid = "Request connection priority is invalid";
  private final String logRequestConnectionPriorityFailed = "Request connection priority failed";
  //MTU
  private final String logMtuFail = "Unable to set MTU";
  private final String logMtuFailReturn = "Unable to set MTU on return";

  private final String logRequiresAPI21 = "Requires API level 21";

  private final String operationConnect = "connect";
  private final String operationDiscover = "discover";
  private final String operationRssi = "rssi";
  private final String operationRead = "read";
  private final String operationSubscribe = "subscribe";
  private final String operationUnsubscribe = "unsubscribe";
  private final String operationWrite = "write";
  private final String operationMtu = "mtu";

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
    else if (enableActionName.equals(action))
    {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          enableAction(callbackContext);
        }
      });
      return true;
    }
    else if (disableActionName.equals(action))
    {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          disableAction(callbackContext);
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
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          stopScanAction(callbackContext);
        }
      });
      return true;
    }
    else if (retrieveConnectedActionName.equals(action))
    {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          retrieveConnectedAction(args, callbackContext);
        }
      });
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
    else if (reconnectActionName.equals(action))
    {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          reconnectAction(args, callbackContext);
        }
      });
      return true;
    }
    else if (disconnectActionName.equals(action))
    {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          disconnectAction(args, callbackContext);
        }
      });
      return true;
    }
    else if (servicesActionName.equals(action))
    {
      JSONObject returnObj = new JSONObject();
      addProperty(returnObj, keyError, errorServices);
      addProperty(returnObj, keyMessage, logOperationUnsupported);
      callbackContext.error(returnObj);
      return true;
    }
    else if (characteristicsActionName.equals(action))
    {
      JSONObject returnObj = new JSONObject();
      addProperty(returnObj, keyError, errorCharacteristics);
      addProperty(returnObj, keyMessage, logOperationUnsupported);
      callbackContext.error(returnObj);
      return true;
    }
    else if (descriptorsActionName.equals(action))
    {
      JSONObject returnObj = new JSONObject();
      addProperty(returnObj, keyError, errorDescriptors);
      addProperty(returnObj, keyMessage, logOperationUnsupported);
      callbackContext.error(returnObj);
      return true;
    }
    else if (closeActionName.equals(action))
    {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          closeAction(args, callbackContext);
        }
      });
      return true;
    }
    else if (discoverActionName.equals(action))
    {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          discoverAction(args, callbackContext);
        }
      });
      return true;
    }
    else if (readActionName.equals(action))
    {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          readAction(args, callbackContext);
        }
      });
      return true;
    }
    else if (subscribeActionName.equals(action))
    {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          subscribeAction(args, callbackContext);
        }
      });
      return true;
    }
    else if (unsubscribeActionName.equals(action))
    {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          unsubscribeAction(args, callbackContext);
        }
      });
      return true;
    }
    else if (writeActionName.equals(action))
    {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          writeAction(args, callbackContext);
        }
      });
      return true;
    }
    else if (readDescriptorActionName.equals(action))
    {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          readDescriptorAction(args, callbackContext);
        }
      });
      return true;
    }
    else if (writeDescriptorActionName.equals(action))
    {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          writeDescriptorAction(args, callbackContext);
        }
      });
      return true;
    }
    else if (rssiActionName.equals(action))
    {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          rssiAction(args, callbackContext);
        }
      });
      return true;
    }
    else if (isInitializedActionName.equals(action))
    {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          isInitializedAction(callbackContext);
        }
      });
      return true;
    }
    else if (isEnabledActionName.equals(action))
    {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          isEnabledAction(callbackContext);
        }
      });
      return true;
    }
    else if (isScanningActionName.equals(action))
    {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          isScanningAction(callbackContext);
        }
      });
      return true;
    }
    else if (isConnectedActionName.equals(action))
    {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          isConnectedAction(args,callbackContext);
        }
      });
      return true;
    }
    else if (isDiscoveredActionName.equals(action))
    {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          isDiscoveredAction(args, callbackContext);
        }
      });
      return true;
    }
    else if (requestConnectionPriorityActionName.equals(action))
    {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          requestConnectionPriorityAction(args, callbackContext);
        }
      });
      return true;
    }
    else if (mtuActionName.equals(action))
    {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          mtuAction(args, callbackContext);
        }
      });
      return true;
    }
    else if (hasPermissionName.equals(action))
    {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          hasPermissionAction(callbackContext);
        }
      });
      return true;
    }
    else if (requestPermissionName.equals(action))
    {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          requestPermissionAction(callbackContext);
        }
      });
      return true;
    }
    return false;
  }

  public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException
  {
    if (permissionsCallback == null) {
      return;
    }

    //Just call hasPermission again to verify
    JSONObject returnObj = new JSONObject();

    addProperty(returnObj, "requestPermission", cordova.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION));

    permissionsCallback.success(returnObj);
  }

  public void hasPermissionAction(CallbackContext callbackContext) {
    JSONObject returnObj = new JSONObject();

    addProperty(returnObj, "hasPermission", cordova.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION));

    callbackContext.success(returnObj);
  }

  public void requestPermissionAction(CallbackContext callbackContext) {
    permissionsCallback = callbackContext;
    cordova.requestPermission(this, REQUEST_ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
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
        addProperty(returnObj, keyStatus, statusDisabled);
        addProperty(returnObj, keyMessage, logNotEnabled);

        pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
        pluginResult.setKeepCallback(true);
        initCallbackContext.sendPluginResult(pluginResult);
      }

      return;
    }

    Activity activity = cordova.getActivity();

    JSONObject obj = getArgsObject(args);

    if (obj != null && getStatusReceiver(obj))
    {
      //Add a receiver to pick up when Bluetooth state changes
      activity.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
      isReceiverRegistered = true;
    }

    //Get Bluetooth adapter via Bluetooth Manager
    BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
    bluetoothAdapter = bluetoothManager.getAdapter();

    connections = new HashMap<Object, HashMap<Object,Object>>();

    JSONObject returnObj = new JSONObject();

    //If it's already enabled,
    if (bluetoothAdapter.isEnabled())
    {
      addProperty(returnObj, keyStatus, statusEnabled);
      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
      pluginResult.setKeepCallback(true);
      initCallbackContext.sendPluginResult(pluginResult);
      return;
    }

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
      addProperty(returnObj, keyStatus, statusDisabled);
      addProperty(returnObj, keyMessage, logNotEnabled);
      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
      pluginResult.setKeepCallback(true);
      initCallbackContext.sendPluginResult(pluginResult);
    }
  }

  private void enableAction(CallbackContext callbackContext)
  {
    if (isNotInitialized(callbackContext, false))
    {
      return;
    }

    if (isNotDisabled(callbackContext))
    {
      return;
    }

    boolean result = bluetoothAdapter.enable();

    if (!result)
    {
      //Throw an enabling error
      JSONObject returnObj = new JSONObject();

      addProperty(returnObj, keyError, errorEnable);
      addProperty(returnObj, keyMessage, logNotEnabled);

      callbackContext.error(returnObj);
    }

    //Else listen to initialize callback for enabling
  }

  private void disableAction(CallbackContext callbackContext)
  {
    if (isNotInitialized(callbackContext, true))
    {
      return;
    }

    boolean result = bluetoothAdapter.disable();

    if (!result)
    {
      //Throw a disabling error
      JSONObject returnObj = new JSONObject();

      addProperty(returnObj, keyError, errorDisable);
      addProperty(returnObj, keyMessage, logNotDisabled);

      callbackContext.error(returnObj);
    }

    //Else listen to initialize callback for disabling
  }

  private void startScanAction(JSONArray args, CallbackContext callbackContext)
  {
    synchronized(scanLock) {
      if (isNotInitialized(callbackContext, true))
      {
        return;
      }

      //If the adapter is already scanning, don't call another scan.
      if (scanCallbackContext != null)
      {
        JSONObject returnObj = new JSONObject();
        addProperty(returnObj, keyError, errorStartScan);
        addProperty(returnObj, keyMessage, logAlreadyScanning);
        callbackContext.error(returnObj);
        return;
      }

      //Get the service UUIDs from the arguments
      JSONObject obj = getArgsObject(args);
      UUID[] uuids = getServiceUuids(obj);

      //Save the callback context for reporting back found connections. Also the isScanning flag
      scanCallbackContext = callbackContext;
      
      if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP)
      {
        boolean result = uuids.length==0 ? bluetoothAdapter.startLeScan(scanCallbackKitKat) : bluetoothAdapter.startLeScan(uuids, scanCallbackKitKat);

        if (!result) // scan did not start
        {
          JSONObject returnObj = new JSONObject();
          addProperty(returnObj, keyError, errorStartScan);
          addProperty(returnObj, keyMessage, logScanStartFail);
          callbackContext.error(returnObj);
          scanCallbackContext = null;
          return;
        }
      }
      else
      {
        /* build the ScanFilters */
        ArrayList<ScanFilter> scanFilter = new ArrayList<ScanFilter>();
        for (UUID uuid : getServiceUuids(obj)) {
          ScanFilter.Builder builder = new ScanFilter.Builder();
          builder.setServiceUuid(new ParcelUuid(uuid));
          scanFilter.add(builder.build());
        }

        /* build the ScanSetting */
        ScanSettings.Builder scanSettings = new ScanSettings.Builder();
        scanSettings.setReportDelay(0);

        int scanMode = obj.optInt(keyScanMode, ScanSettings.SCAN_MODE_LOW_LATENCY);
        try { scanSettings.setScanMode(scanMode); }
        catch(java.lang.IllegalArgumentException e) {
        }

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
        {
          int matchMode = obj.optInt(keyMatchMode, ScanSettings.MATCH_MODE_AGGRESSIVE);
          try { scanSettings.setMatchMode(matchMode); }
          catch(java.lang.IllegalArgumentException e) {
          }
          
          int matchNum = obj.optInt(keyMatchNum, ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT);
          try { scanSettings.setNumOfMatches(matchNum); }
          catch(java.lang.IllegalArgumentException e) {
          }
          
          int callbackType = obj.optInt(keyCallbackType, ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
          try { scanSettings.setCallbackType(callbackType); }
          catch(java.lang.IllegalArgumentException e) {
          }
        }

        //Start the scan with or without service UUIDs
        bluetoothAdapter.getBluetoothLeScanner().startScan(scanFilter, scanSettings.build(), scanCallback);
      }

      {
        JSONObject returnObj = new JSONObject();

        //Notify user of started scan and save callback
        addProperty(returnObj, keyStatus, statusScanStarted);

        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
        pluginResult.setKeepCallback(true);
        callbackContext.sendPluginResult(pluginResult);
      }
      
    }
  }

  private void stopScanAction(CallbackContext callbackContext)
  {
    synchronized(scanLock) {
      if (isNotInitialized(callbackContext, true))
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
      
      if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP)
      {
        bluetoothAdapter.stopLeScan(scanCallbackKitKat);
      }
      else
      {
        bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
      }

      //Set scanning state
      scanCallbackContext = null;

      //Inform user
      addProperty(returnObj, keyStatus, statusScanStopped);
      callbackContext.success(returnObj);
    }
  }

  private void retrieveConnectedAction(JSONArray args, CallbackContext callbackContext)
  {
    //Filtering by service UUID only works if the service UUIDs have already been discovered/cached previously
    if (isNotInitialized(callbackContext, true))
    {
      return;
    }

    /*JSONObject obj = getArgsObject(args);

    UUID[] serviceUuids = serviceUuids = getServiceUuids(obj);*/

    JSONArray returnArray = new JSONArray();

    Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
    for (BluetoothDevice device : devices)
    {
      if (device.getType() != BluetoothDevice.DEVICE_TYPE_LE) {
        continue;
      }
      
      /*if (serviceUuids != null)
      {
        ParcelUuid[] uuids = device.getUuids();

        if (uuids == null)
        {
          continue;
        }

        Set<UUID> set = new HashSet<UUID>();

        for (ParcelUuid uuid : uuids)
        {
          set.add(uuid.getUuid());
        }

        boolean flag = false;

        for (UUID uuid : serviceUuids)
        {
          if (!set.contains(uuid))
          {
            flag = true;
            break;
          }
        }

        if (flag)
        {
          continue;
        }
      }*/

      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);

      returnArray.put(returnObj);
    }

    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnArray);
    pluginResult.setKeepCallback(true);
    callbackContext.sendPluginResult(pluginResult);
  }

  private void connectAction(JSONArray args, CallbackContext callbackContext)
  {
    if (isNotInitialized(callbackContext, true))
    {
      return;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext))
    {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext))
    {
      return;
    }

    if (wasConnected(address, callbackContext))
    {
      return;
    }

    JSONObject returnObj = new JSONObject();

    //Ensure device exists
    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
    if (device == null)
    {
      addProperty(returnObj, keyError, errorConnect);
      addProperty(returnObj, keyMessage, logNoDevice);
      addProperty(returnObj, keyAddress, address);
      callbackContext.error(returnObj);
      return;
    }

    HashMap<Object, Object> connection = new HashMap<Object, Object>();

    connection.put(keyState, BluetoothProfile.STATE_CONNECTING);
    connection.put(keyDiscoveredState, STATE_UNDISCOVERED);
    connection.put(operationConnect, callbackContext);

    BluetoothGatt bluetoothGatt = device.connectGatt(cordova.getActivity().getApplicationContext(), false, new BluetoothGattCallbackExtends());

    connection.put(keyPeripheral, bluetoothGatt);

    connections.put(device.getAddress(), connection);
  }

  private void reconnectAction(JSONArray args, CallbackContext callbackContext)
  {
    if (isNotInitialized(callbackContext, true))
    {
      return;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext))
    {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext))
    {
      return;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null)
    {
      return;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt)connection.get(keyPeripheral);
    BluetoothDevice device = bluetoothGatt.getDevice();

    if (isNotDisconnected(connection, device, callbackContext))
    {
      return;
    }

    JSONObject returnObj = new JSONObject();

    addDevice(returnObj, device);

    boolean result = bluetoothGatt.connect();

    if (!result)
    {
      addProperty(returnObj, keyError, errorReconnect);
      addProperty(returnObj, keyMessage, logReconnectFail);
      callbackContext.error(returnObj);
      return;
    }

    connection.put(keyState, BluetoothProfile.STATE_CONNECTING);
    connection.put(keyDiscoveredState, STATE_UNDISCOVERED);
    connection.put(operationConnect, callbackContext);
  }

  private void disconnectAction(JSONArray args, CallbackContext callbackContext)
  {
    if (isNotInitialized(callbackContext, true))
    {
      return;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext))
    {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext))
    {
      return;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null)
    {
      return;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt)connection.get(keyPeripheral);
    BluetoothDevice device = bluetoothGatt.getDevice();

    if (isDisconnected(connection, device, callbackContext))
    {
      return;
    }

    int state = Integer.valueOf(connection.get(keyState).toString());

    JSONObject returnObj = new JSONObject();

    //Return disconnecting status and keep callback
    addDevice(returnObj, device);

    //If it's connecting, cancel attempt and return disconnect
    if (state == BluetoothProfile.STATE_CONNECTING)
    {
      addProperty(returnObj, keyStatus, statusDisconnected);
      connection.put(keyState, BluetoothProfile.STATE_DISCONNECTED);

      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);

      connection.remove(operationConnect);
    }
    //Very unlikely that this is DISCONNECTING
    else
    {
      connection.put(operationConnect, callbackContext);
    }

    bluetoothGatt.disconnect();
  }

  private void closeAction(JSONArray args, CallbackContext callbackContext)
  {
    if (isNotInitialized(callbackContext, true))
    {
      return;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext))
    {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext))
    {
      return;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null)
    {
      return;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt)connection.get(keyPeripheral);
    BluetoothDevice device = bluetoothGatt.getDevice();

    /* Make disconnect/close less annoying
    if (isNotDisconnected(connection, device, callbackContext))
    {
      return;
    }
    */

    JSONObject returnObj = new JSONObject();

    addProperty(returnObj, keyStatus, statusClosed);

    addDevice(returnObj, device);

    bluetoothGatt.close();

    connections.remove(device.getAddress());

    callbackContext.success(returnObj);
  }

  private void discoverAction(JSONArray args, CallbackContext callbackContext)
  {
    if (isNotInitialized(callbackContext, true))
    {
      return;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext))
    {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext))
    {
      return;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null)
    {
      return;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt)connection.get(keyPeripheral);
    BluetoothDevice device = bluetoothGatt.getDevice();

    if (isNotConnected(connection, device, callbackContext))
    {
      return;
    }

    JSONObject returnObj = new JSONObject();

    addDevice(returnObj, device);

    int discoveredState = Integer.valueOf(connection.get(keyDiscoveredState).toString());
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
      returnObj = getDiscovery(bluetoothGatt);
      callbackContext.success(returnObj);
      return;
    }

    //Else undiscovered, so start discovery
    connection.put(keyDiscoveredState, STATE_DISCOVERING);
    connection.put(operationDiscover, callbackContext);

    bluetoothGatt.discoverServices();
  }

  private void readAction(JSONArray args, CallbackContext callbackContext)
  {
    if (isNotInitialized(callbackContext, true))
    {
      return;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext))
    {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext))
    {
      return;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null)
    {
      return;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt)connection.get(keyPeripheral);
    BluetoothDevice device = bluetoothGatt.getDevice();

    if (isNotConnected(connection, device, callbackContext))
    {
      return;
    }

    BluetoothGattService service = getService(bluetoothGatt, obj);

    if (isNotService(service, device, callbackContext))
    {
      return;
    }

    BluetoothGattCharacteristic characteristic = getCharacteristic(obj, service);

    if (isNotCharacteristic(characteristic, device, callbackContext))
    {
      return;
    }

    UUID characteristicUuid = characteristic.getUuid();

    AddCallback(characteristicUuid, connection, operationRead, callbackContext);

    boolean result = bluetoothGatt.readCharacteristic(characteristic);

    if (!result)
    {
      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);

      addCharacteristic(returnObj, characteristic);

      addProperty(returnObj, keyError, errorRead);
      addProperty(returnObj, keyMessage, logReadFail);

      callbackContext.error(returnObj);

      RemoveCallback(characteristicUuid, connection, operationRead);
    }
  }

  private void subscribeAction(JSONArray args, CallbackContext callbackContext)
  {
    if (isNotInitialized(callbackContext, true))
    {
      return;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext))
    {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext))
    {
      return;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null)
    {
      return;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt)connection.get(keyPeripheral);
    BluetoothDevice device = bluetoothGatt.getDevice();

    if (isNotConnected(connection, device, callbackContext))
    {
      return;
    }

    BluetoothGattService service = getService(bluetoothGatt, obj);

    if (isNotService(service, device, callbackContext))
    {
      return;
    }

    BluetoothGattCharacteristic characteristic = getCharacteristic(obj, service);

    if (isNotCharacteristic(characteristic, device, callbackContext))
    {
      return;
    }

    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(clientConfigurationDescriptorUuid);

    if (isNotDescriptor(descriptor, device, callbackContext))
    {
      return;
    }

    UUID characteristicUuid = characteristic.getUuid();

    JSONObject returnObj = new JSONObject();

    addDevice(returnObj, device);

    addCharacteristic(returnObj, characteristic);
    
    CallbackContext checkExisting = GetCallback(characteristicUuid, connection, operationSubscribe);
    if (checkExisting != null)
    {
      addProperty(returnObj, keyError, errorSubscription);
      addProperty(returnObj, keyMessage, logSubscribeAlready);
      callbackContext.error(returnObj);
      return;
    }

    boolean result = false;

    //Set the descriptor for notification
    if (obj.optBoolean(keyIsNotification, true))
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
      addProperty(returnObj, keyError, errorWriteDescriptor);
      addProperty(returnObj, keyMessage, logWriteDescriptorValueNotSet);
      callbackContext.error(returnObj);
      return;
    }

    AddCallback(characteristicUuid, connection, operationSubscribe, callbackContext);

    //Write the descriptor value
    result = bluetoothGatt.writeDescriptor(descriptor);

    if (!result)
    {
      addProperty(returnObj, keyError, errorWriteDescriptor);
      addProperty(returnObj, keyMessage, logWriteDescriptorFail);
      callbackContext.error(returnObj);
      RemoveCallback(characteristicUuid, connection, operationSubscribe);
    }
  }

  private void unsubscribeAction(JSONArray args, CallbackContext callbackContext)
  {
    if (isNotInitialized(callbackContext, true))
    {
      return;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext))
    {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext))
    {
      return;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null)
    {
      return;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt)connection.get(keyPeripheral);
    BluetoothDevice device = bluetoothGatt.getDevice();

    if (isNotConnected(connection, device, callbackContext))
    {
      return;
    }

    BluetoothGattService service = getService(bluetoothGatt, obj);

    if (isNotService(service, device, callbackContext))
    {
      return;
    }

    BluetoothGattCharacteristic characteristic = getCharacteristic(obj, service);

    if (isNotCharacteristic(characteristic, device, callbackContext))
    {
      return;
    }

    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(clientConfigurationDescriptorUuid);

    if (isNotDescriptor(descriptor, device, callbackContext))
    {
      return;
    }

    UUID characteristicUuid = characteristic.getUuid();

    JSONObject returnObj = new JSONObject();

    addDevice(returnObj, device);

    addCharacteristic(returnObj, characteristic);
    
    CallbackContext checkExisting = GetCallback(characteristicUuid, connection, operationSubscribe);
    if (checkExisting == null)
    {
      addProperty(returnObj, keyError, errorSubscription);
      addProperty(returnObj, keyMessage, logUnsubscribeAlready);
      callbackContext.error(returnObj);
      return;
    }
    RemoveCallback(characteristicUuid, connection, operationSubscribe);

    //Set the descriptor for disabling notification/indication
    boolean result = descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);

    if (!result)
    {
      addProperty(returnObj, keyError, errorWriteDescriptor);
      addProperty(returnObj, keyMessage, logWriteDescriptorValueNotSet);
      callbackContext.error(returnObj);
      return;
    }

    AddCallback(characteristicUuid, connection, operationUnsubscribe, callbackContext);

    //Write the actual descriptor value
    result = bluetoothGatt.writeDescriptor(descriptor);

    if (!result)
    {
      addProperty(returnObj, keyError, errorWriteDescriptor);
      addProperty(returnObj, keyMessage, logWriteDescriptorFail);
      callbackContext.error(returnObj);
      RemoveCallback(characteristicUuid, connection, operationUnsubscribe);
    }
  }

  private void writeAction(JSONArray args, CallbackContext callbackContext)
  {
    if (isNotInitialized(callbackContext, true))
    {
      return;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext))
    {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext))
    {
      return;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null)
    {
      return;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt)connection.get(keyPeripheral);
    BluetoothDevice device = bluetoothGatt.getDevice();

    if (isNotConnected(connection, device, callbackContext))
    {
      return;
    }

    BluetoothGattService service = getService(bluetoothGatt, obj);

    if (isNotService(service, device, callbackContext))
    {
      return;
    }

    BluetoothGattCharacteristic characteristic = getCharacteristic(obj, service);

    if (isNotCharacteristic(characteristic, device, callbackContext))
    {
      return;
    }

    UUID characteristicUuid = characteristic.getUuid();

    JSONObject returnObj = new JSONObject();

    addDevice(returnObj, device);

    addCharacteristic(returnObj, characteristic);

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

    AddCallback(characteristicUuid, connection, operationWrite, callbackContext);

    result = bluetoothGatt.writeCharacteristic(characteristic);

    if (!result)
    {
      addProperty(returnObj, keyError, errorWrite);
      addProperty(returnObj, keyMessage, logWriteFail);
      callbackContext.error(returnObj);
      RemoveCallback(characteristicUuid, connection, operationWrite);
    }
  }

  private void readDescriptorAction(JSONArray args, CallbackContext callbackContext)
  {
    if (isNotInitialized(callbackContext, true))
    {
      return;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext))
    {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext))
    {
      return;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null)
    {
      return;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt)connection.get(keyPeripheral);
    BluetoothDevice device = bluetoothGatt.getDevice();

    if (isNotConnected(connection, device, callbackContext))
    {
      return;
    }

    BluetoothGattService service = getService(bluetoothGatt, obj);

    if (isNotService(service, device, callbackContext))
    {
      return;
    }

    BluetoothGattCharacteristic characteristic = getCharacteristic(obj, service);

    if (isNotCharacteristic(characteristic, device, callbackContext))
    {
      return;
    }

    BluetoothGattDescriptor descriptor = getDescriptor(obj, characteristic);

    if (isNotDescriptor(descriptor, device, callbackContext))
    {
      return;
    }

    UUID descriptorUuid = descriptor.getUuid();
    UUID characteristicUuid = characteristic.getUuid();

    AddDescriptorCallback(descriptorUuid, characteristicUuid, connection, operationRead, callbackContext);

    boolean result = bluetoothGatt.readDescriptor(descriptor);

    if (!result)
    {
      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);

      addDescriptor(returnObj, descriptor);

      addProperty(returnObj, keyError, errorReadDescriptor);
      addProperty(returnObj, keyMessage, logReadDescriptorFail);

      callbackContext.error(returnObj);

      RemoveDescriptorCallback(descriptorUuid, characteristicUuid, connection, operationRead);

      return;
    }
  }

  private void writeDescriptorAction(JSONArray args, CallbackContext callbackContext)
  {
    if (isNotInitialized(callbackContext, true))
    {
      return;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext))
    {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext))
    {
      return;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null)
    {
      return;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt)connection.get(keyPeripheral);
    BluetoothDevice device = bluetoothGatt.getDevice();

    if (isNotConnected(connection, device, callbackContext))
    {
      return;
    }

    BluetoothGattService service = getService(bluetoothGatt, obj);

    if (isNotService(service, device, callbackContext))
    {
      return;
    }

    BluetoothGattCharacteristic characteristic = getCharacteristic(obj, service);

    if (isNotCharacteristic(characteristic, device, callbackContext))
    {
      return;
    }

    BluetoothGattDescriptor descriptor = getDescriptor(obj, characteristic);

    if (isNotDescriptor(descriptor, device, callbackContext))
    {
      return;
    }

    UUID descriptorUuid = descriptor.getUuid();
    UUID characteristicUuid = characteristic.getUuid();

    JSONObject returnObj = new JSONObject();

    addDevice(returnObj, device);

    addDescriptor(returnObj, descriptor);

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

    AddDescriptorCallback(descriptorUuid, characteristicUuid, connection, operationWrite, callbackContext);

    result = bluetoothGatt.writeDescriptor(descriptor);

    if (!result)
    {
      addProperty(returnObj, keyError, errorWriteDescriptor);
      addProperty(returnObj, keyMessage, logWriteDescriptorFail);
      callbackContext.error(returnObj);
      RemoveDescriptorCallback(descriptorUuid, characteristicUuid, connection, operationWrite);
      return;
    }
  }

  private void rssiAction(JSONArray args, CallbackContext callbackContext)
  {
    if (isNotInitialized(callbackContext, true))
    {
      return;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext))
    {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext))
    {
      return;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null)
    {
      return;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt)connection.get(keyPeripheral);
    BluetoothDevice device = bluetoothGatt.getDevice();

    if (isNotConnected(connection, device, callbackContext))
    {
      return;
    }

    connection.put(operationRssi, callbackContext);

    boolean result = bluetoothGatt.readRemoteRssi();

    if (!result)
    {
      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);

      addProperty(returnObj, keyError, errorRssi);
      addProperty(returnObj, keyMessage, logRssiFail);

      callbackContext.error(returnObj);

      connection.remove(operationRssi);
      return;
    }
  }

  private void mtuAction(JSONArray args, CallbackContext callbackContext)
  {
    if (isNotInitialized(callbackContext, true))
    {
      return;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext))
    {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext))
    {
      return;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null)
    {
      return;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt)connection.get(keyPeripheral);
    BluetoothDevice device = bluetoothGatt.getDevice();

    if (Build.VERSION.SDK_INT < 21)
    {
      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);

      addProperty(returnObj, keyError, errorMtu);
      addProperty(returnObj, keyMessage, logRequiresAPI21);

      callbackContext.error(returnObj);
      return;
    }

    if (isNotConnected(connection, device, callbackContext))
    {
      return;
    }

    connection.put(operationMtu, callbackContext);

    int mtu = getMtu(obj);

    boolean result = bluetoothGatt.requestMtu(mtu);

    if (!result)
    {
      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);

      addProperty(returnObj, keyError, errorMtu);
      addProperty(returnObj, keyMessage, logMtuFail);

      callbackContext.error(returnObj);

      connection.remove(operationMtu);
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

  private void isConnectedAction(JSONArray args, CallbackContext callbackContext)
  {
    if (isNotInitialized(callbackContext, true))
    {
      return;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext))
    {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext))
    {
      return;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null)
    {
      return;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt)connection.get(keyPeripheral);

    int state = Integer.valueOf(connection.get(keyState).toString());

    boolean result = (state == BluetoothAdapter.STATE_CONNECTED);

    BluetoothDevice device = bluetoothGatt.getDevice();

    JSONObject returnObj = new JSONObject();

    addProperty(returnObj, keyIsConnected, result);

    addDevice(returnObj, device);

    callbackContext.success(returnObj);
  }

  private void isDiscoveredAction(JSONArray args, CallbackContext callbackContext)
  {
    if (isNotInitialized(callbackContext, true))
    {
      return;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext))
    {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext))
    {
      return;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null)
    {
      return;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt)connection.get(keyPeripheral);
    BluetoothDevice device = bluetoothGatt.getDevice();

    if (isNotConnected(connection, device, callbackContext))
    {
      return;
    }

    int state = Integer.valueOf(connection.get(keyDiscoveredState).toString());

    boolean result = (state == STATE_DISCOVERED);

    JSONObject returnObj = new JSONObject();

    addProperty(returnObj, keyIsDiscovered, result);

    addDevice(returnObj, device);

    callbackContext.success(returnObj);
  }

  private void requestConnectionPriorityAction(JSONArray args, CallbackContext callbackContext)
  {
    if(isNotInitialized(callbackContext, true))
    {
      return;
    }

    JSONObject obj = getArgsObject(args);

    if (isNotArgsObject(obj, callbackContext))
    {
      return;
    }

    String address = getAddress(obj);

    if (isNotAddress(address, callbackContext))
    {
      return;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null)
    {
      return;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt)connection.get(keyPeripheral);

    if (Build.VERSION.SDK_INT < 21)
    {
      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, bluetoothGatt.getDevice());

      addProperty(returnObj, keyError, errorRequestConnectionPriority);
      addProperty(returnObj, keyMessage, logRequiresAPI21);

      callbackContext.error(returnObj);
      return;
    }

    String priority = obj.optString(keyConnectionPriority, null);

    int androidPriority = BluetoothGatt.CONNECTION_PRIORITY_BALANCED;

    if (priority == null)
    {
        JSONObject returnObj = new JSONObject();

        addDevice(returnObj, bluetoothGatt.getDevice());

        addProperty(returnObj, keyError, errorRequestConnectionPriority);
        addProperty(returnObj, keyMessage, logRequestConnectionPriorityNull);

        callbackContext.error(returnObj);
      return;
    }
    else if (priority.equals(propertyConnectionPriorityLow))
    {
      androidPriority = BluetoothGatt.CONNECTION_PRIORITY_LOW_POWER;
    }
    else if (priority.equals(propertyConnectionPriorityBalanced))
    {
      androidPriority = BluetoothGatt.CONNECTION_PRIORITY_BALANCED;
    }
    else if (priority.equals(propertyConnectionPriorityHigh))
    {
      androidPriority = BluetoothGatt.CONNECTION_PRIORITY_HIGH;
    }
    else
    {
    JSONObject returnObj = new JSONObject();

      addDevice(returnObj, bluetoothGatt.getDevice());

      addProperty(returnObj, keyError, errorRequestConnectionPriority);
      addProperty(returnObj, keyMessage, logRequestConnectionPriorityInvalid);

      callbackContext.error(returnObj);
      return;
    }


    boolean result = bluetoothGatt.requestConnectionPriority(androidPriority);

    if (!result)
    {
      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, bluetoothGatt.getDevice());

      addProperty(returnObj, keyError, errorRequestConnectionPriority);
      addProperty(returnObj, keyMessage, logRequestConnectionPriorityFailed);

      callbackContext.error(returnObj);
    }
    else
    {
      JSONObject returnObj = new JSONObject();

      addProperty(returnObj, keyStatus, statusConnectionPriorityRequested);

      addDevice(returnObj, bluetoothGatt.getDevice());

      callbackContext.success(returnObj);
    }
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

            addProperty(returnObj, keyStatus, statusDisabled);
            addProperty(returnObj, keyMessage, logNotEnabled);

            connections = new HashMap<Object, HashMap<Object,Object>>();
            synchronized(scanLock) {
              scanCallbackContext = null;
            }

            pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
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
        addProperty(returnObj, keyStatus, statusDisabled);
        addProperty(returnObj, keyMessage, logNotEnabled);

        PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, returnObj);
        pluginResult.setKeepCallback(true);
        initCallbackContext.sendPluginResult(pluginResult);
      }
    }
  }

  //Scan Callback for KitKat
  private LeScanCallback scanCallbackKitKat = new LeScanCallback()
  {
    @Override
    public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord)
    {
      synchronized(scanLock) {
        if (scanCallbackContext == null)
        {
          return;
        }

        JSONObject returnObj = new JSONObject();

        addDevice(returnObj, device);

        addProperty(returnObj, keyRssi, rssi);
        addPropertyBytes(returnObj, keyAdvertisement, scanRecord);
        addProperty(returnObj, keyStatus, statusScanResult);

        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
        pluginResult.setKeepCallback(true);
        scanCallbackContext.sendPluginResult(pluginResult);
      }
    }
  };
  
  //Scan Callback
  private ScanCallback scanCallback = null;
  
  //TODO Is there a cleaner way to prevent this from running on KitKat
  protected void pluginInitialize() {
    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
      return;
    }
    
    scanCallback = new ScanCallback()
    {
      @Override
      public void onBatchScanResults(List<ScanResult> results) {
        if (scanCallbackContext == null)
          return;
      }

      @Override
      public void onScanFailed(int errorCode) {
        synchronized(scanLock) {
          if (scanCallbackContext == null)
            return;

          JSONObject returnObj = new JSONObject();
          addProperty(returnObj, keyError, errorStartScan);

          if (errorCode == ScanCallback.SCAN_FAILED_ALREADY_STARTED) {
            addProperty(returnObj, keyMessage, "Scan already started");
          } else if (errorCode == ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED) {
            addProperty(returnObj, keyMessage, "Application registration failed");
          } else if (errorCode == ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED) {
            addProperty(returnObj, keyMessage, "Feature unsupported");
          } else if (errorCode == ScanCallback.SCAN_FAILED_INTERNAL_ERROR) {
            addProperty(returnObj, keyMessage, "Internal error");
          } else {
            addProperty(returnObj, keyMessage, logScanStartFail);
          }

          scanCallbackContext.error(returnObj);
          scanCallbackContext = null;
        }
      }

      @Override
      public void onScanResult(int callbackType, ScanResult result) {
        synchronized(scanLock) {
          if (scanCallbackContext == null)
            return;

          JSONObject returnObj = new JSONObject();

          addDevice(returnObj, result.getDevice());
          addProperty(returnObj, keyRssi, result.getRssi());
          addPropertyBytes(returnObj, keyAdvertisement, result.getScanRecord().getBytes());
          addProperty(returnObj, keyStatus, statusScanResult);

          PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
          pluginResult.setKeepCallback(true);
          scanCallbackContext.sendPluginResult(pluginResult);
        }
      }
    };
  }

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
  private BluetoothGattService getService(BluetoothGatt bluetoothGatt, JSONObject obj)
  {
    String uuidServiceValue = obj.optString(keyService, null);

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
    String uuidCharacteristicValue = obj.optString(keyCharacteristic, null);

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
    String uuidDescriptorValue = obj.optString(keyDescriptor, null);

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
  private HashMap<Object, Object> EnsureCallback(UUID characteristicUuid, HashMap<Object, Object> connection)
  {
    HashMap<Object, Object> characteristicCallbacks = (HashMap<Object, Object>)connection.get(characteristicUuid);

    if (characteristicCallbacks != null)
    {
      return characteristicCallbacks;
    }

    characteristicCallbacks = new HashMap<Object, Object>();
    connection.put(characteristicUuid, characteristicCallbacks);

    return characteristicCallbacks;
  }

  private void AddCallback(UUID characteristicUuid, HashMap<Object,Object> connection, String operationType, CallbackContext callbackContext)
  {
    HashMap<Object, Object> characteristicCallbacks = EnsureCallback(characteristicUuid, connection);

    characteristicCallbacks.put(operationType, callbackContext);
  }

  private CallbackContext GetCallback(UUID characteristicUuid, HashMap<Object, Object> connection, String operationType)
  {
    HashMap<Object, Object> characteristicCallbacks = (HashMap<Object,Object>)connection.get(characteristicUuid);

    if (characteristicCallbacks == null)
    {
      return null;
    }

    //This may return null
    return (CallbackContext)characteristicCallbacks.get(operationType);
  }

  private CallbackContext[] GetCallbacks(HashMap<Object, Object> connection)
  {
    ArrayList<CallbackContext> callbacks = new ArrayList<CallbackContext>();

    for (Object key : connection.keySet()) {
      if (!(key instanceof UUID)) {
        continue;
      }

      HashMap<Object, Object> characteristic = (HashMap<Object,Object>) connection.get(key);
      for (Object keyCallback : characteristic.keySet()) {
        if (!(keyCallback instanceof String)) {
          continue;
        }

        CallbackContext callback = (CallbackContext)characteristic.get(keyCallback);

        if (callback == null) {
          continue;
        }

        callbacks.add(callback);
      }
    }

    return callbacks.toArray(new CallbackContext[callbacks.size()]);
  }

  private void RemoveCallback(UUID characteristicUuid, HashMap<Object, Object> connection, String operationType)
  {
    HashMap<Object, Object> characteristicCallbacks = (HashMap<Object, Object>)connection.get(characteristicUuid);

    if (characteristicCallbacks == null)
    {
      return;
    }

    characteristicCallbacks.remove(operationType);
  }

  private HashMap<Object, Object> EnsureDescriptorCallback(UUID descriptorUuid, UUID characteristicUuid, HashMap<Object, Object> connection)
  {
    HashMap<Object, Object> characteristicCallbacks = EnsureCallback(characteristicUuid, connection);

    HashMap<Object, Object> descriptorCallbacks = (HashMap<Object, Object>)characteristicCallbacks.get(descriptorUuid);

    if (descriptorCallbacks != null)
    {
      return descriptorCallbacks;
    }

    descriptorCallbacks = new HashMap<Object, Object>();
    characteristicCallbacks.put(descriptorUuid, descriptorCallbacks);

    return descriptorCallbacks;
  }

  private void AddDescriptorCallback(UUID descriptorUuid, UUID characteristicUuid, HashMap<Object,Object> connection, String operationType, CallbackContext callbackContext)
  {
    HashMap<Object, Object> descriptorCallbacks = EnsureDescriptorCallback(descriptorUuid, characteristicUuid, connection);

    descriptorCallbacks.put(operationType, callbackContext);
  }

  private CallbackContext GetDescriptorCallback(UUID descriptorUuid, UUID characteristicUuid, HashMap<Object, Object> connection, String operationType)
  {
    HashMap<Object, Object> characteristicCallbacks = (HashMap<Object,Object>)connection.get(characteristicUuid);

    if (characteristicCallbacks == null)
    {
      return null;
    }

    HashMap<Object, Object> descriptorCallbacks = (HashMap<Object,Object>)characteristicCallbacks.get(descriptorUuid);

    if (descriptorCallbacks == null)
    {
      return null;
    }

    //This may return null
    return (CallbackContext)descriptorCallbacks.get(operationType);
  }

  private void RemoveDescriptorCallback(UUID descriptorUuid, UUID characteristicUuid, HashMap<Object, Object> connection, String operationType)
  {
    HashMap<Object, Object> characteristicCallbacks = (HashMap<Object,Object>)connection.get(characteristicUuid);

    if (characteristicCallbacks == null)
    {
      return;
    }

    HashMap<Object, Object> descriptorCallbacks = (HashMap<Object,Object>)characteristicCallbacks.get(descriptorUuid);

    if (descriptorCallbacks == null)
    {
      return;
    }

    descriptorCallbacks.remove(descriptorUuid);
  }

  //Helpers to Check Conditions
  private boolean isNotInitialized(CallbackContext callbackContext, boolean checkIsNotEnabled)
  {
    if (bluetoothAdapter == null)
    {
      JSONObject returnObj = new JSONObject();

      addProperty(returnObj, keyError, errorInitialize);
      addProperty(returnObj, keyMessage, logNotInit);

      callbackContext.error(returnObj);

      return true;
    }

    if (checkIsNotEnabled)
    {
      return isNotEnabled(callbackContext);
    }
    else
    {
      return false;
    }
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

  private boolean isNotDisabled(CallbackContext callbackContext)
  {
    if (bluetoothAdapter.isEnabled())
    {
      JSONObject returnObj = new JSONObject();

      addProperty(returnObj, keyError, errorDisable);
      addProperty(returnObj, keyMessage, logNotDisabled);

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

  private boolean isNotAddress(String address, CallbackContext callbackContext)
  {
    if (address == null)
    {
      JSONObject returnObj = new JSONObject();

      addProperty(returnObj, keyError, errorConnect);
      addProperty(returnObj, keyMessage, logNoAddress);

      callbackContext.error(returnObj);
      return true;
    }

    return false;
  }

  private boolean isNotService(BluetoothGattService service, BluetoothDevice device, CallbackContext callbackContext)
  {
    if (service != null)
    {
      return false;
    }

    JSONObject returnObj = new JSONObject();

    addProperty(returnObj, keyError, errorService);
    addProperty(returnObj, keyMessage, logNoService);

    addDevice(returnObj, device);

    callbackContext.error(returnObj);

    return true;
  }

  private boolean isNotCharacteristic(BluetoothGattCharacteristic characteristic, BluetoothDevice device, CallbackContext callbackContext)
  {
    if (characteristic != null)
    {
      return false;
    }

    JSONObject returnObj = new JSONObject();

    addProperty(returnObj, keyError, errorCharacteristic);
    addProperty(returnObj, keyMessage, logNoCharacteristic);

    addDevice(returnObj, device);

    callbackContext.error(returnObj);

    return true;
  }

  private boolean isNotDescriptor(BluetoothGattDescriptor descriptor, BluetoothDevice device, CallbackContext callbackContext)
  {
    if (descriptor != null)
    {
      return false;
    }

    JSONObject returnObj = new JSONObject();

    addProperty(returnObj, keyError, errorDescriptor);
    addProperty(returnObj, keyMessage, logNoDescriptor);

    addDevice(returnObj, device);

    callbackContext.error(returnObj);

    return true;
  }

  private boolean isNotDisconnected(HashMap<Object,Object> connection, BluetoothDevice device, CallbackContext callbackContext)
  {
    int state = Integer.valueOf(connection.get(keyState).toString());

    //Determine whether the device is currently connected including connecting and disconnecting
    //Certain actions like connect and reconnect can only be done while completely disconnected
    if (state == BluetoothProfile.STATE_DISCONNECTED)
    {
      return false;
    }

    JSONObject returnObj = new JSONObject();

    addProperty(returnObj, keyError, errorIsNotDisconnected);
    addProperty(returnObj, keyMessage, logIsNotDisconnected);

    addDevice(returnObj, device);

    callbackContext.error(returnObj);

    return true;
  }

  private boolean isDisconnected(HashMap<Object,Object> connection, BluetoothDevice device, CallbackContext callbackContext)
  {
    int state = Integer.valueOf(connection.get(keyState).toString());

    //Determine whether the device is currently disconnected NOT including connecting and disconnecting
    //Certain actions like disconnect can be done while connected, connecting, disconnecting
    if (state != BluetoothProfile.STATE_DISCONNECTED)
    {
      return false;
    }

    JSONObject returnObj = new JSONObject();

    addProperty(returnObj, keyError, errorIsDisconnected);
    addProperty(returnObj, keyMessage, logIsDisconnected);

    addDevice(returnObj, device);

    callbackContext.error(returnObj);

    return true;
  }

  private boolean isNotConnected(HashMap<Object,Object> connection, BluetoothDevice device, CallbackContext callbackContext)
  {
    int state = Integer.valueOf(connection.get(keyState).toString());

    //Determine whether the device is currently disconnected including connecting and disconnecting
    //Certain actions like read/write operations can only be done while completely connected
    if (state == BluetoothProfile.STATE_CONNECTED)
    {
      return false;
    }

    JSONObject returnObj = new JSONObject();

    addProperty(returnObj, keyError, errorIsNotConnected);
    addProperty(returnObj, keyMessage, logIsNotConnected);

    addDevice(returnObj, device);

    callbackContext.error(returnObj);

    return true;
  }

  private boolean wasConnected(String address, CallbackContext callbackContext)
  {
    HashMap<Object, Object> connection = connections.get(address);
    if (connection != null)
    {
      BluetoothGatt peripheral = (BluetoothGatt)connection.get(keyPeripheral);
      BluetoothDevice device = peripheral.getDevice();

      JSONObject returnObj = new JSONObject();

      addProperty(returnObj, keyError, errorConnect);
      addProperty(returnObj, keyMessage, logPreviouslyConnected);

      addDevice(returnObj, device);

      callbackContext.error(returnObj);

      return true;
    }
    return false;
  }

  private HashMap<Object, Object> wasNeverConnected(String address, CallbackContext callbackContext)
  {
    HashMap<Object, Object> connection = connections.get(address);
    if (connection != null)
    {
      return connection;
    }

    JSONObject returnObj = new JSONObject();

    addProperty(returnObj, keyError, errorNeverConnected);
    addProperty(returnObj, keyMessage, logNeverConnected);
    addProperty(returnObj, keyAddress, address);

    callbackContext.error(returnObj);

    return null;
  }

  private void addDevice(JSONObject returnObj, BluetoothDevice device)
  {
    addProperty(returnObj, keyAddress, device.getAddress());
    addProperty(returnObj, keyName, device.getName());
  }

  private void addCharacteristic(JSONObject returnObj, BluetoothGattCharacteristic characteristic)
  {
    addProperty(returnObj, keyService, formatUuid(characteristic.getService().getUuid()));
    addProperty(returnObj, keyCharacteristic, formatUuid(characteristic.getUuid()));
  }

  private void addDescriptor(JSONObject returnObj, BluetoothGattDescriptor descriptor)
  {
    addCharacteristic(returnObj, descriptor.getCharacteristic());
    addProperty(returnObj, keyDescriptor, formatUuid(descriptor.getUuid()));
  }

  //General Helpers
  private void addProperty(JSONObject obj, String key, Object value)
  {
    //Believe exception only occurs when adding duplicate keys, so just ignore it
    try
    {
      if (value == null) {
        obj.put(key, JSONObject.NULL);
      } else {
        obj.put(key, value);
      }
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
    if (obj == null) {
      return new UUID[] {};
    }
    
    JSONArray array = obj.optJSONArray(keyServices);

    if (array == null)
    {
      return new UUID[] {};
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

    UUID[] uuids = new UUID[arrayList.size()];
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

  private boolean getStatusReceiver(JSONObject obj)
  {
    return obj.optBoolean(keyStatusReceiver, true);
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

  private int getMtu(JSONObject obj)
  {
    int mtu = obj.optInt(keyMtu);

    if (mtu == 0)
    {
      return 23;
    }

    return mtu;
  }

  private JSONObject getDiscovery(BluetoothGatt bluetoothGatt)
  {
    JSONObject deviceObject = new JSONObject();

    BluetoothDevice device = bluetoothGatt.getDevice();

    addProperty(deviceObject, keyStatus, statusDiscovered);

    addDevice(deviceObject, device);

    JSONArray servicesArray = new JSONArray();

    List<BluetoothGattService> services = bluetoothGatt.getServices();

    for (BluetoothGattService service : services)
    {
      JSONObject serviceObject = new JSONObject();

      addProperty(serviceObject, keyUuid, formatUuid(service.getUuid()));

      JSONArray characteristicsArray = new JSONArray();

      List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();

      for (BluetoothGattCharacteristic characteristic : characteristics)
      {
        JSONObject characteristicObject = new JSONObject();

        addProperty(characteristicObject, keyUuid, formatUuid(characteristic.getUuid()));
        addProperty(characteristicObject, keyProperties, getProperties(characteristic));
        addProperty(characteristicObject, keyPermissions, getPermissions(characteristic));

        JSONArray descriptorsArray = new JSONArray();

        List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();

        for (BluetoothGattDescriptor descriptor : descriptors)
        {
          JSONObject descriptorObject = new JSONObject();

          addProperty(descriptorObject, keyUuid, formatUuid(descriptor.getUuid()));

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

  private JSONObject getPermissions(BluetoothGattCharacteristic characteristic)
  {
    int permissions = characteristic.getPermissions();
    
    JSONObject permissionsObject = new JSONObject();

    if ((permissions & BluetoothGattCharacteristic.PERMISSION_READ) == BluetoothGattCharacteristic.PERMISSION_READ)
    {
      addProperty(permissionsObject, permissionRead, true);
    }

    if ((permissions & BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED) == BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED)
    {
      addProperty(permissionsObject, permissionReadEncrypted, true);
    }

    if ((permissions & BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED_MITM) == BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED_MITM)
    {
      addProperty(permissionsObject, permissionReadEncryptedMITM, true);
    }

    if ((permissions & BluetoothGattCharacteristic.PERMISSION_WRITE) == BluetoothGattCharacteristic.PERMISSION_WRITE)
    {
      addProperty(permissionsObject, permissionWrite, true);
    }

    if ((permissions & BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED) == BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED)
    {
      addProperty(permissionsObject, permissionWriteEncrypted, true);
    }

    if ((permissions & BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED_MITM) == BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED_MITM)
    {
      addProperty(permissionsObject, permissionWriteEncryptedMITM, true);
    }

    if ((permissions & BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED) == BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED)
    {
      addProperty(permissionsObject, permissionWriteSigned, true);
    }

    if ((permissions & BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED_MITM) == BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED_MITM)
    {
      addProperty(permissionsObject, permissionWriteSignedMITM, true);
    }

    return permissionsObject;
  }

  //Bluetooth callback for connecting, discovering, reading and writing
  private final class BluetoothGattCallbackExtends extends BluetoothGattCallback
  {
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
    {
      //Get the connected device
      BluetoothDevice device = gatt.getDevice();
      String address = device.getAddress();

      HashMap<Object, Object> connection = connections.get(address);
      if (connection == null)
      {
        return;
      }

      CallbackContext callbackContext = (CallbackContext)connection.get(operationConnect);

      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);

      int oldState = Integer.valueOf(connection.get(keyState).toString());
      if (status != BluetoothGatt.GATT_SUCCESS && oldState == BluetoothProfile.STATE_CONNECTING)
      {
        //Clear out all the callbacks
        connection = new HashMap<Object, Object>();
        connection.put(keyPeripheral, gatt);
        connection.put(keyState, BluetoothProfile.STATE_DISCONNECTED);

        connections.put(device.getAddress(), connection);

        if (callbackContext == null)
        {
          return;
        }

        addProperty(returnObj, keyError, errorConnect);
        addProperty(returnObj, keyMessage, logConnectFail);

        callbackContext.error(returnObj);

        return;
      }

      connection.put(keyState, newState);

      //Device was connected
      if (newState == BluetoothProfile.STATE_CONNECTED)
      {
        if (callbackContext == null)
        {
          return;
        }

        addProperty(returnObj, keyStatus, statusConnected);

        //Keep connection call back for disconnect
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
        pluginResult.setKeepCallback(true);
        callbackContext.sendPluginResult(pluginResult);
      }
      //Device was disconnected
      else if (newState == BluetoothProfile.STATE_DISCONNECTED)
      {
        CallbackContext[] callbacks = GetCallbacks(connection);
        addProperty(returnObj, keyError, errorIsDisconnected);
        addProperty(returnObj, keyMessage, logIsDisconnected);

        for (CallbackContext callback : callbacks){
          callback.error(returnObj);
        }

        returnObj.remove(keyError);
        returnObj.remove(keyMessage);

        //Clear out all the callbacks
        connection = new HashMap<Object, Object>();
        connection.put(keyPeripheral, gatt);
        connection.put(keyState, BluetoothProfile.STATE_DISCONNECTED);

        connections.put(device.getAddress(), connection);

        if (callbackContext == null)
        {
          return;
        }

        addProperty(returnObj, keyStatus, statusDisconnected);

        callbackContext.success(returnObj);
      }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status)
    {
      //Get the connected device
      BluetoothDevice device = gatt.getDevice();
      String address = device.getAddress();

      HashMap<Object, Object> connection = connections.get(address);
      if (connection == null)
      {
        return;
      }

      int discoveredState = (status == BluetoothGatt.GATT_SUCCESS) ? STATE_DISCOVERED : STATE_UNDISCOVERED;
      connection.put(keyDiscoveredState, discoveredState);

      CallbackContext callbackContext = (CallbackContext) connection.get(operationDiscover);
      connection.remove(operationDiscover);

      //Shouldn't happen, but check for null callback
      if (callbackContext == null)
      {
        return;
      }

      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);

      //If successfully discovered, return list of services, characteristics and descriptors
      if (status == BluetoothGatt.GATT_SUCCESS)
      {
        returnObj = getDiscovery(gatt);
        callbackContext.success(returnObj);
      }
      //Else it failed
      else
      {
        addProperty(returnObj, keyError, errorDiscover);
        addProperty(returnObj, keyMessage, logDiscoveryFail);
        callbackContext.error(returnObj);
      }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
    {
      //Get the connected device
      BluetoothDevice device = gatt.getDevice();
      String address = device.getAddress();

      HashMap<Object, Object> connection = connections.get(address);
      if (connection == null)
      {
        return;
      }

      UUID characteristicUuid = characteristic.getUuid();

      CallbackContext callbackContext = GetCallback(characteristicUuid, connection, operationRead);
      RemoveCallback(characteristicUuid, connection, operationRead);

      //If no callback, just return
      if (callbackContext == null)
      {
        return;
      }

      JSONObject returnObj = new JSONObject();

      addCharacteristic(returnObj, characteristic);

      addDevice(returnObj, device);

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
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
    {
      //Get the connected device
      BluetoothDevice device = gatt.getDevice();
      String address = device.getAddress();

      HashMap<Object, Object> connection = connections.get(address);
      if (connection == null)
      {
        return;
      }

      UUID characteristicUuid = characteristic.getUuid();

      CallbackContext callbackContext = GetCallback(characteristicUuid, connection, operationSubscribe);

      //If no callback, just return
      if (callbackContext == null)
      {
        return;
      }

      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);

      addCharacteristic(returnObj, characteristic);

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
      //Get the connected device
      BluetoothDevice device = gatt.getDevice();
      String address = device.getAddress();

      HashMap<Object, Object> connection = connections.get(address);
      if (connection == null)
      {
        return;
      }

      UUID characteristicUuid = characteristic.getUuid();

      CallbackContext callbackContext = GetCallback(characteristicUuid, connection, operationWrite);
      RemoveCallback(characteristicUuid, connection, operationWrite);

      //If no callback, just return
      if (callbackContext == null)
      {
        return;
      }

      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);
      addCharacteristic(returnObj, characteristic);

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
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
    {
      //Get the connected device
      BluetoothDevice device = gatt.getDevice();
      String address = device.getAddress();

      HashMap<Object, Object> connection = connections.get(address);
      if (connection == null)
      {
        return;
      }

      BluetoothGattCharacteristic characteristic = descriptor.getCharacteristic();
      UUID characteristicUuid = characteristic.getUuid();
      UUID descriptorUuid = descriptor.getUuid();

      CallbackContext callbackContext = GetDescriptorCallback(descriptorUuid, characteristicUuid, connection, operationRead);
      RemoveDescriptorCallback(descriptorUuid, characteristicUuid, connection, operationRead);

      //If callback is null, just return
      if (callbackContext == null)
      {
        return;
      }

      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);

      addDescriptor(returnObj, descriptor);

      //If descriptor was successful, return the written value
      if (status == BluetoothGatt.GATT_SUCCESS)
      {
        addProperty(returnObj, keyStatus, statusReadDescriptor);
        addPropertyBytes(returnObj, keyValue, descriptor.getValue());
        callbackContext.success(returnObj);
      }
      //Else it failed
      else
      {
        addProperty(returnObj, keyError, errorReadDescriptor);
        addProperty(returnObj, keyMessage, logReadDescriptorFailReturn);
        callbackContext.error(returnObj);
      }
    }

    @Override
    public void onDescriptorWrite (BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
    {
      //Get the connected device
      BluetoothDevice device = gatt.getDevice();
      String address = device.getAddress();

      HashMap<Object, Object> connection = connections.get(address);
      if (connection == null)
      {
        return;
      }

      BluetoothGattCharacteristic characteristic = descriptor.getCharacteristic();
      UUID characteristicUuid = characteristic.getUuid();
      UUID descriptorUuid = descriptor.getUuid();

      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);

      addDescriptor(returnObj, descriptor);

      //See if notification/indication is enabled or disabled and use subscribe/unsubscribe callback instead
      if (descriptorUuid.equals(clientConfigurationDescriptorUuid))
      {
        if (descriptor.getValue() == BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)
        {
          //Unsubscribe to the characteristic
          boolean result = gatt.setCharacteristicNotification(characteristic, false);

          CallbackContext callbackContext = GetCallback(characteristicUuid, connection, operationUnsubscribe);

          //If no callback, just return
          if (callbackContext == null)
          {
            return;
          }

          if (status != BluetoothGatt.GATT_SUCCESS)
          {
            addProperty(returnObj, keyError, errorSubscription);
            addProperty(returnObj, keyMessage, logUnsubscribeFail);
            callbackContext.error(returnObj);
            return;
          }

          if (!result)
          {
            addProperty(returnObj, keyError, errorSubscription);
            addProperty(returnObj, keyMessage, logUnsubscribeFail);
            callbackContext.error(returnObj);
            return;
          }

          //Get the unsubscribed operation callback and clear
          addProperty(returnObj, keyStatus, statusUnsubscribed);

          callbackContext.success(returnObj);
        }
        else
        {
          //Subscribe to the characteristic
          boolean result = gatt.setCharacteristicNotification(characteristic, true);

          CallbackContext callbackContext = GetCallback(characteristicUuid, connection, operationSubscribe);

          //If no callback, just return
          if (callbackContext == null)
          {
            return;
          }

          if (!result)
          {
            addProperty(returnObj, keyError, errorSubscription);
            addProperty(returnObj, keyMessage, logSubscribeFail);
            callbackContext.error(returnObj);
            return;
          }

          addProperty(returnObj, keyStatus, statusSubscribed);

          PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
          pluginResult.setKeepCallback(true);
          callbackContext.sendPluginResult(pluginResult);
        }

        return;
      }

      CallbackContext callbackContext = GetDescriptorCallback(descriptorUuid, characteristicUuid, connection, operationRead);
      RemoveDescriptorCallback(descriptorUuid, characteristicUuid, connection, operationRead);

      //If callback is null, just return
      if (callbackContext == null)
      {
        return;
      }

      //If descriptor was written, return written value
      if (status == BluetoothGatt.GATT_SUCCESS)
      {
        addProperty(returnObj, keyStatus, statusWrittenDescriptor);
        addPropertyBytes(returnObj, keyValue, descriptor.getValue());
        callbackContext.success(returnObj);
      }
      //Else it failed
      else
      {
        addProperty(returnObj, keyError, errorWriteDescriptor);
        addProperty(returnObj, keyMessage, logWriteDescriptorFailReturn);
        callbackContext.error(returnObj);
      }
    }

    @Override
    public void onReadRemoteRssi (BluetoothGatt gatt, int rssi, int status)
    {
      //Get the connected device
      BluetoothDevice device = gatt.getDevice();
      String address = device.getAddress();

      HashMap<Object, Object> connection = connections.get(address);
      if (connection == null)
      {
        return;
      }

      CallbackContext callbackContext = (CallbackContext)connection.get(operationRssi);
      connection.remove(operationRssi);

      //If no callback, just return
      if (callbackContext == null)
      {
        return;
      }

      JSONObject returnObj = new JSONObject();
      addDevice(returnObj, device);

      //If successfully read RSSI, return value
      if (status == BluetoothGatt.GATT_SUCCESS)
      {
        addProperty(returnObj, keyStatus, statusRssi);
        addProperty(returnObj, keyRssi, rssi);
        callbackContext.success(returnObj);
      }
      //Else it failed
      else
      {
        addProperty(returnObj, keyError, errorRssi);
        addProperty(returnObj, keyMessage, logRssiFailReturn);
        callbackContext.error(returnObj);
      }
    }

    @Override
    public void onMtuChanged (BluetoothGatt gatt, int mtu, int status)
    {
      BluetoothDevice device = gatt.getDevice();
      String address = device.getAddress();

      HashMap<Object, Object> connection = connections.get(address);
      if (connection == null)
      {
        return;
      }

      CallbackContext callbackContext = (CallbackContext)connection.get(operationMtu);
      connection.remove(operationMtu);

      if (callbackContext == null)
      {
        return;
      }

      JSONObject returnObj = new JSONObject();
      addDevice(returnObj, device);

      if (status == BluetoothGatt.GATT_SUCCESS)
      {
        addProperty(returnObj, keyStatus, statusMtu);
        addProperty(returnObj, keyMtu, mtu);
        callbackContext.success(returnObj);
      }
      else
      {
        addProperty(returnObj, keyError, errorMtu);
        addProperty(returnObj, keyMessage, logMtuFailReturn);
        callbackContext.error(returnObj);
      }
    }
  }
}
