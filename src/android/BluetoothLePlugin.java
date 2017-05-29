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
import android.provider.Settings;
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
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanSettings;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

@SuppressWarnings("unchecked")

public class BluetoothLePlugin extends CordovaPlugin {
  //Initialization related variables
  private final int REQUEST_BT_ENABLE = 59627; /*Random integer*/
  private final int REQUEST_ACCESS_COARSE_LOCATION = 59628;
  private final int REQUEST_LOCATION_SOURCE_SETTINGS = 59629;
  private BluetoothAdapter bluetoothAdapter;
  private boolean isReceiverRegistered = false;
  private boolean isBondReceiverRegistered = false;

  //General callback variables
  private CallbackContext initCallbackContext;
  private CallbackContext scanCallbackContext;
  private CallbackContext permissionsCallback;
  private CallbackContext locationCallback;

  private CallbackContext initPeripheralCallback;
  private BluetoothGattServer gattServer;
  private CallbackContext addServiceCallback;
  private CallbackContext advertiseCallbackContext;
  private boolean isAdvertising = false;

  //Store connections and all their callbacks
  private HashMap<Object, HashMap<Object, Object>> connections;

  //Store bonds
  private HashMap<String, CallbackContext> bonds = new HashMap<String, CallbackContext>();

  //Discovery related variables
  private final int STATE_UNDISCOVERED = 0;
  private final int STATE_DISCOVERING = 1;
  private final int STATE_DISCOVERED = 2;

  //Quick Writes
  private LinkedList<byte[]> queueQuick = new LinkedList<byte[]>();

  //Queueing
  private LinkedList<Operation> queue = new LinkedList<Operation>();

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
  private final String keyIsBonded = "isBonded";
  private final String keyIsConnected = "isConnected";
  private final String keyIsDiscovered = "isDiscovered";
  private final String keyIsDiscoverable = "isDiscoverable";
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
  private final String statusBonded = "bonded";
  private final String statusBonding = "bonding";
  private final String statusUnbonded = "unbonded";
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
  private final String errorBond = "bond";
  private final String errorUnbond = "unbond";
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
  //Bonding
  private final String logBonded = "Device already bonded";
  private final String logBonding = "Device already bonding";
  private final String logUnbonded = "Device already unbonded";
  private final String logBondFail = "Device failed to bond on return";
  private final String logUnbondFail = "Device failed to unbond on return";
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
  private final String baseUuidEnd = "-0000-1000-8000-00805F9B34FB";

  //Client Configuration UUID for notifying/indicating
  private final UUID clientConfigurationDescriptorUuid = UUID.fromString("00002902-0000-1000-8000-00805F9B34FB");

  public BluetoothLePlugin() {

    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
      return;
    }

    createScanCallback();

    createAdvertiseCallback();
  }

  //Actions
  @Override
  public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
    //Execute the specified action
    if ("initialize".equals(action)) {
      initializeAction(args, callbackContext);
    } else if ("enable".equals(action)) {
      enableAction(callbackContext);
    } else if ("getAdapterInfo".equals(action)) {
      getAdapterInfoAction(callbackContext);
    } else if ("disable".equals(action)) {
      disableAction(callbackContext);
    } else if ("startScan".equals(action)) {
      startScanAction(args, callbackContext);
    } else if ("stopScan".equals(action)) {
      stopScanAction(callbackContext);
    } else if ("retrieveConnected".equals(action)) {
      retrieveConnectedAction(args, callbackContext);
    } else if ("bond".equals(action)) {
      bondAction(args, callbackContext);
    } else if ("unbond".equals(action)) {
      unbondAction(args, callbackContext);
    } else if ("connect".equals(action)) {
      connectAction(args, callbackContext);
    } else if ("reconnect".equals(action)) {
      reconnectAction(args, callbackContext);
    } else if ("disconnect".equals(action)) {
      disconnectAction(args, callbackContext);
    } else if ("services".equals(action)) {
      JSONObject returnObj = new JSONObject();
      addProperty(returnObj, keyError, errorServices);
      addProperty(returnObj, keyMessage, logOperationUnsupported);
      callbackContext.error(returnObj);
    } else if ("characteristics".equals(action)) {
      JSONObject returnObj = new JSONObject();
      addProperty(returnObj, keyError, errorCharacteristics);
      addProperty(returnObj, keyMessage, logOperationUnsupported);
      callbackContext.error(returnObj);
    } else if ("descriptors".equals(action)) {
      JSONObject returnObj = new JSONObject();
      addProperty(returnObj, keyError, errorDescriptors);
      addProperty(returnObj, keyMessage, logOperationUnsupported);
      callbackContext.error(returnObj);
    } else if ("close".equals(action)) {
      closeAction(args, callbackContext);
    } else if ("discover".equals(action)) {
      discoverAction(args, callbackContext);
    } else if ("read".equals(action)) {
      Operation operation = new Operation("read", args, callbackContext);
      queue.add(operation);
      queueStart();
    } else if ("subscribe".equals(action)) {
      Operation operation = new Operation("subscribe", args, callbackContext);
      queue.add(operation);
      queueStart();
    } else if ("unsubscribe".equals(action)) {
      Operation operation = new Operation("unsubscribe", args, callbackContext);
      queue.add(operation);
      queueStart();
    } else if ("write".equals(action)) {
      Operation operation = new Operation("write", args, callbackContext);
      queue.add(operation);
      queueStart();
    } else if ("writeQ".equals(action)) {
      writeQAction(args, callbackContext);
    } else if ("readDescriptor".equals(action)) {
      Operation operation = new Operation("readDescriptor", args, callbackContext);
      queue.add(operation);
      queueStart();
    } else if ("writeDescriptor".equals(action)) {
      Operation operation = new Operation("writeDescriptor", args, callbackContext);
      queue.add(operation);
      queueStart();
    } else if ("rssi".equals(action)) {
      rssiAction(args, callbackContext);
    } else if ("isInitialized".equals(action)) {
      isInitializedAction(callbackContext);
    } else if ("isEnabled".equals(action)) {
      isEnabledAction(callbackContext);
    } else if ("isScanning".equals(action)) {
      isScanningAction(callbackContext);
    } else if ("wasConnected".equals(action)) {
      wasConnectedAction(args, callbackContext);
    } else if ("isConnected".equals(action)) {
      isConnectedAction(args, callbackContext);
    } else if ("isDiscovered".equals(action)) {
      isDiscoveredAction(args, callbackContext);
    } else if ("isBonded".equals(action)) {
      isBondedAction(args, callbackContext);
    } else if ("requestConnectionPriority".equals(action)) {
      requestConnectionPriorityAction(args, callbackContext);
    } else if ("mtu".equals(action)) {
      mtuAction(args, callbackContext);
    } else if ("hasPermission".equals(action)) {
      hasPermissionAction(callbackContext);
    } else if ("requestPermission".equals(action)) {
      requestPermissionAction(callbackContext);
    } else if ("isLocationEnabled".equals(action)) {
      isLocationEnabledAction(callbackContext);
    } else if ("requestLocation".equals(action)) {
      requestLocationAction(callbackContext);
    } else if ("initializePeripheral".equals(action)) {
      initializePeripheralAction(args, callbackContext);
    } else if ("addService".equals(action)) {
      addServiceAction(args, callbackContext);
    } else if ("removeService".equals(action)) {
      removeServiceAction(args, callbackContext);
    } else if ("removeAllServices".equals(action)) {
      removeAllServicesAction(args, callbackContext);
    } else if ("startAdvertising".equals(action)) {
      startAdvertisingAction(args, callbackContext);
    } else if ("stopAdvertising".equals(action)) {
      stopAdvertisingAction(args, callbackContext);
    } else if ("isAdvertising".equals(action)) {
      isAdvertisingAction(callbackContext);
    } else if ("respond".equals(action)) {
      respondAction(args, callbackContext);
    } else if ("notify".equals(action)) {
      notifyAction(args, callbackContext);
    } else {
      return false;
    }
    return true;
  }

  private void initializePeripheralAction(JSONArray args, CallbackContext callbackContext) {
    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
      JSONObject returnObj = new JSONObject();

      addProperty(returnObj, "error", "initializePeripheral");
      addProperty(returnObj, "message", logOperationUnsupported);

      callbackContext.error(returnObj);
      return;
    }

    initPeripheralCallback = callbackContext;

    //Re-opening Gatt server seems to cause some issues
    if (gattServer == null) {
      Activity activity = cordova.getActivity();
      BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
      gattServer = bluetoothManager.openGattServer(activity.getApplicationContext(), bluetoothGattServerCallback);
    }

    JSONObject returnObj = new JSONObject();
    addProperty(returnObj, keyStatus, statusEnabled);

    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
    pluginResult.setKeepCallback(true);
    initPeripheralCallback.sendPluginResult(pluginResult);

    //TODO standardize with main init function as well
  }

  private void addServiceAction(JSONArray args, CallbackContext callbackContext) {
    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext)) {
      return;
    }

    addServiceCallback = callbackContext;

    UUID uuid = getUUID(obj.optString("service", null));

    BluetoothGattService service = new BluetoothGattService(uuid, BluetoothGattService.SERVICE_TYPE_PRIMARY);

    JSONArray characteristicsIn = obj.optJSONArray("characteristics");

    for (int i = 0; i < characteristicsIn.length(); i++) {
      JSONObject characteristicIn = null;

      try {
        characteristicIn = characteristicsIn.getJSONObject(i);
      } catch (JSONException ex) {
        continue;
      }

      UUID characteristicUuid = getUUID(characteristicIn.optString("uuid", null));

      boolean includeClientConfiguration = false;

      JSONObject propertiesIn = characteristicIn.optJSONObject("properties");
      int properties = 0;
      if (propertiesIn != null) {
        if (propertiesIn.optString("broadcast", null) != null) {
          properties |= BluetoothGattCharacteristic.PROPERTY_BROADCAST;
        }
        if (propertiesIn.optString("extendedProps", null) != null) {
          properties |= BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS;
        }
        if (propertiesIn.optString("indicate", null) != null) {
          properties |= BluetoothGattCharacteristic.PROPERTY_INDICATE;
          includeClientConfiguration = true;
        }
        if (propertiesIn.optString("notify", null) != null) {
          properties |= BluetoothGattCharacteristic.PROPERTY_NOTIFY;
          includeClientConfiguration = true;
        }
        if (propertiesIn.optString("read", null) != null) {
          properties |= BluetoothGattCharacteristic.PROPERTY_READ;
        }
        if (propertiesIn.optString("signedWrite", null) != null) {
          properties |= BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE;
        }
        if (propertiesIn.optString("write", null) != null) {
          properties |= BluetoothGattCharacteristic.PROPERTY_WRITE;
        }
        if (propertiesIn.optString("writeNoResponse", null) != null) {
          properties |= BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE;
        }
        if (propertiesIn.optString(propertyNotifyEncryptionRequired, null) != null) {
          properties |= 0x100;
        }
        if (propertiesIn.optString(propertyIndicateEncryptionRequired, null) != null) {
          properties |= 0x200;
        }
      }

      JSONObject permissionsIn = characteristicIn.optJSONObject("permissions");
      int permissions = 0;
      if (permissionsIn != null) {
        if (permissionsIn.optString("read", null) != null) {
          permissions |= BluetoothGattCharacteristic.PERMISSION_READ;
        }
        if (permissionsIn.optString("readEncrypted", null) != null) {
          permissions |= BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED;
        }
        if (permissionsIn.optString("readEncryptedMITM", null) != null) {
          permissions |= BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED_MITM;
        }
        if (permissionsIn.optString("write", null) != null) {
          permissions |= BluetoothGattCharacteristic.PERMISSION_WRITE;
        }
        if (permissionsIn.optString("writeEncrypted", null) != null) {
          permissions |= BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED;
        }
        if (permissionsIn.optString("writeEncryptedMITM", null) != null) {
          permissions |= BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED_MITM;
        }
        if (permissionsIn.optString("writeSigned", null) != null) {
          permissions |= BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED;
        }
        if (permissionsIn.optString("writeSignedMITM", null) != null) {
          permissions |= BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED_MITM;
        }
      }

      BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(characteristicUuid, properties, permissions);

      if (includeClientConfiguration) {
        BluetoothGattDescriptor descriptor = new BluetoothGattDescriptor(clientConfigurationDescriptorUuid, BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        characteristic.addDescriptor(descriptor);
      }

      JSONArray descriptorsIn = obj.optJSONArray("descriptors");

      if (descriptorsIn != null) {
        for (int j = 0; j < descriptorsIn.length(); i++) {
          JSONObject descriptorIn = null;

          try {
            descriptorIn = descriptorsIn.getJSONObject(i);
          } catch (JSONException ex) {
            continue;
          }

          UUID descriptorUuid = getUUID(descriptorIn.optString("uuid", null));

          permissionsIn = descriptorIn.optJSONObject("permissions");
          permissions = 0;
          if (permissionsIn != null) {
            if (permissionsIn.optString("read", null) != null) {
              permissions |= BluetoothGattDescriptor.PERMISSION_READ;
            }
            if (permissionsIn.optString("readEncrypted", null) != null) {
              permissions |= BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED;
            }
            if (permissionsIn.optString("readEncryptedMITM", null) != null) {
              permissions |= BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED_MITM;
            }
            if (permissionsIn.optString("write", null) != null) {
              permissions |= BluetoothGattDescriptor.PERMISSION_WRITE;
            }
            if (permissionsIn.optString("writeEncrypted", null) != null) {
              permissions |= BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED;
            }
            if (permissionsIn.optString("writeEncryptedMITM", null) != null) {
              permissions |= BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED_MITM;
            }
            if (permissionsIn.optString("writeSigned", null) != null) {
              permissions |= BluetoothGattDescriptor.PERMISSION_WRITE_SIGNED;
            }
            if (permissionsIn.optString("writeSignedMITM", null) != null) {
              permissions |= BluetoothGattDescriptor.PERMISSION_WRITE_SIGNED_MITM;
            }
          }

          BluetoothGattDescriptor descriptor = new BluetoothGattDescriptor(descriptorUuid, permissions);

          characteristic.addDescriptor(descriptor);
        }
      }

      service.addCharacteristic(characteristic);
    }

    boolean result = gattServer.addService(service);
    if (result) {
      JSONObject returnObj = new JSONObject();

      addProperty(returnObj, "service", uuid.toString());
      addProperty(returnObj, "status", "serviceAdded");

      callbackContext.success(returnObj);
    } else {
      JSONObject returnObj = new JSONObject();

      addProperty(returnObj, "service", uuid.toString());
      addProperty(returnObj, "error", "service");
      addProperty(returnObj, "message", "Failed to add service");

      callbackContext.error(returnObj);
    }
  }

  private void removeServiceAction(JSONArray args, CallbackContext callbackContext) {
    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext)) {
      return;
    }

    UUID uuid = getUUID(obj.optString("service", null));

    BluetoothGattService service = gattServer.getService(uuid);
    if (service == null) {
      JSONObject returnObj = new JSONObject();

      addProperty(returnObj, "service", uuid.toString());
      addProperty(returnObj, "error", "service");
      addProperty(returnObj, "message", "Service doesn't exist");

      callbackContext.error(returnObj);
      return;
    }

    boolean result = gattServer.removeService(service);
    if (result) {
      JSONObject returnObj = new JSONObject();

      addProperty(returnObj, "service", uuid.toString());
      addProperty(returnObj, "status", "serviceRemoved");

      callbackContext.success(returnObj);
    } else {
      JSONObject returnObj = new JSONObject();

      addProperty(returnObj, "service", uuid.toString());
      addProperty(returnObj, "error", "service");
      addProperty(returnObj, "message", "Failed to remove service");

      callbackContext.error(returnObj);
    }
  }

  private void removeAllServicesAction(JSONArray args, CallbackContext callbackContext) {
    gattServer.clearServices();

    JSONObject returnObj = new JSONObject();

    addProperty(returnObj, "status", "allServicesRemoved");

    callbackContext.success(returnObj);
  }

  private void startAdvertisingAction(JSONArray args, CallbackContext callbackContext) {
    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext)) {
      return;
    }

    BluetoothLeAdvertiser advertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
    if (advertiser == null || !bluetoothAdapter.isMultipleAdvertisementSupported()) {
      JSONObject returnObj = new JSONObject();

      addProperty(returnObj, "error", "startAdvertising");
      addProperty(returnObj, "message", "Advertising isn't supported");

      callbackContext.error(returnObj);
      return;
    }

    AdvertiseSettings.Builder settingsBuilder = new AdvertiseSettings.Builder();

    String modeS = obj.optString("mode", "balanced");
    int mode = AdvertiseSettings.ADVERTISE_MODE_BALANCED;
    if (modeS.equals("lowLatency")) {
      mode = AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY;
    } else if (modeS.equals("lowPower")) {
      mode = AdvertiseSettings.ADVERTISE_MODE_LOW_POWER;
    }
    settingsBuilder.setAdvertiseMode(mode);

    boolean connectable = obj.optBoolean("connectable", true);
    settingsBuilder.setConnectable(connectable);

    int timeout = obj.optInt("timeout", 1000);
    if (timeout < 1 || timeout > 180000) {
      JSONObject returnObj = new JSONObject();

      addProperty(returnObj, "error", "startAdvertising");
      addProperty(returnObj, "message", "Invalid timeout (1 - 180000)");

      callbackContext.error(returnObj);
      return;
    }
    settingsBuilder.setTimeout(timeout);

    String txPowerLevelS = obj.optString("txPowerLevel", "medium");
    int txPowerLevel = AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM;
    if (txPowerLevelS.equals("high")) {
      txPowerLevel = AdvertiseSettings.ADVERTISE_TX_POWER_HIGH;
    } else if (txPowerLevelS.equals("low")) {
      txPowerLevel = AdvertiseSettings.ADVERTISE_TX_POWER_LOW;
    } else if (txPowerLevelS.equals("ultraLow")) {
      txPowerLevel = AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW;
    }
    settingsBuilder.setTxPowerLevel(txPowerLevel);
    AdvertiseSettings advertiseSettings = settingsBuilder.build();

    AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();

    int manufacturerId = obj.optInt("manufacturerId", 0);
    byte[] manufacturerSpecificData = getPropertyBytes(obj, "manufacturerSpecificData");
    if (manufacturerId >= 0 && manufacturerSpecificData != null) {
      dataBuilder.addManufacturerData(manufacturerId, manufacturerSpecificData);
    }

    //dataBuilder.addServiceData();
    UUID uuid = getUUID(obj.optString("service", null));
    if (uuid != null) {
      dataBuilder.addServiceUuid(new ParcelUuid(uuid));
    }

    dataBuilder.setIncludeDeviceName(obj.optBoolean("includeDeviceName", true));

    dataBuilder.setIncludeTxPowerLevel(obj.optBoolean("includeTxPowerLevel", true));

    AdvertiseData advertiseData = dataBuilder.build();

    advertiseCallbackContext = callbackContext;

    advertiser.startAdvertising(advertiseSettings, advertiseData, advertiseCallback);
  }

  private void stopAdvertisingAction(JSONArray args, CallbackContext callbackContext) {
    BluetoothLeAdvertiser advertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
    if (advertiser == null || !bluetoothAdapter.isMultipleAdvertisementSupported()) {
      JSONObject returnObj = new JSONObject();

      addProperty(returnObj, "error", "startAdvertising");
      addProperty(returnObj, "message", "Advertising isn't supported");

      callbackContext.error(returnObj);
      return;
    }

    advertiser.stopAdvertising(advertiseCallback);

    JSONObject returnObj = new JSONObject();
    addProperty(returnObj, "status", "advertisingStopped");
    callbackContext.success(returnObj);
  }

  private void isAdvertisingAction(CallbackContext callbackContext) {
    JSONObject returnObj = new JSONObject();

    addProperty(returnObj, "isAdvertising", isAdvertising);

    callbackContext.success(returnObj);
  }

  private void respondAction(JSONArray args, CallbackContext callbackContext) {
    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext)) {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext)) {
      return;
    }

    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

    int requestId = obj.optInt("requestId", 0); //TODO validate?
    int status = obj.optInt("status", 0);
    int offset = obj.optInt("offset", 0);
    byte[] value = getPropertyBytes(obj, "value");

    boolean result = gattServer.sendResponse(device, requestId, 0, offset, value);
    if (result) {
      JSONObject returnObj = new JSONObject();
      addProperty(returnObj, "status", "responded");
      addProperty(returnObj, "requestId", requestId);
      callbackContext.success(returnObj);
    } else {
      JSONObject returnObj = new JSONObject();
      addProperty(returnObj, "error", "respond");
      addProperty(returnObj, "message", "Failed to respond");
      addProperty(returnObj, "requestId", requestId);
      callbackContext.error(returnObj);
    }
  }

  private void notifyAction(JSONArray args, CallbackContext callbackContext) {
    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext)) {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext)) {
      return;
    }
    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

    UUID serviceUuid = getUUID(obj.optString("service", null));
    BluetoothGattService service = gattServer.getService(serviceUuid);
    if (service == null) {
      JSONObject returnObj = new JSONObject();
      addProperty(returnObj, "error", "service");
      addProperty(returnObj, "message", "Service not found");
      callbackContext.error(returnObj);
    }

    UUID characteristicUuid = getUUID(obj.optString("characteristic", null));
    BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUuid);
    if (characteristic == null) {
      JSONObject returnObj = new JSONObject();
      addProperty(returnObj, "error", "characteristic");
      addProperty(returnObj, "message", "Characteristic not found");
      callbackContext.error(returnObj);
    }

    byte[] value = getPropertyBytes(obj, "value");
    boolean setResult = characteristic.setValue(value);
    if (!setResult) {
      JSONObject returnObj = new JSONObject();
      addProperty(returnObj, "error", "respond");
      addProperty(returnObj, "message", "Failed to set value");
      callbackContext.error(returnObj);
    }

    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(clientConfigurationDescriptorUuid);
    byte[] descriptorValue = descriptor.getValue();

    boolean isIndicate = false;
    if (Arrays.equals(descriptorValue, BluetoothGattDescriptor.ENABLE_INDICATION_VALUE)) {
      isIndicate = true;
    }

    //Wait for onNotificationSent event
    boolean result = gattServer.notifyCharacteristicChanged(device, characteristic, isIndicate);
    if (!result) {
      JSONObject returnObj = new JSONObject();
      addProperty(returnObj, "error", "notify");
      addProperty(returnObj, "message", "Failed to notify");
      callbackContext.error(returnObj);
    }
  }

  public void hasPermissionAction(CallbackContext callbackContext) {
    JSONObject returnObj = new JSONObject();

    addProperty(returnObj, "hasPermission", cordova.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION));

    callbackContext.success(returnObj);
  }

  public void requestPermissionAction(CallbackContext callbackContext) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      JSONObject returnObj = new JSONObject();
      addProperty(returnObj, keyError, "requestPermission");
      addProperty(returnObj, keyMessage, logOperationUnsupported);
      callbackContext.error(returnObj);
      return;
    }

    permissionsCallback = callbackContext;
    cordova.requestPermission(this, REQUEST_ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
  }

  public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
    if (permissionsCallback == null) {
      return;
    }

    //Just call hasPermission again to verify
    JSONObject returnObj = new JSONObject();

    addProperty(returnObj, "requestPermission", cordova.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION));

    permissionsCallback.success(returnObj);
  }

  private void isLocationEnabledAction(CallbackContext callbackContext) {
    JSONObject returnObj = new JSONObject();

    addProperty(returnObj, "isLocationEnabled", isLocationEnabled());

    callbackContext.success(returnObj);
  }

  private boolean isLocationEnabled() {
    boolean result = true;

    //Only applies to Android 6.0, which requires the users to have location services enabled to scan for devices
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      try {
        result = (Settings.Secure.getInt(cordova.getActivity().getContentResolver(), Settings.Secure.LOCATION_MODE) != Settings.Secure.LOCATION_MODE_OFF);
      } catch (Settings.SettingNotFoundException e) {
        result = true; //Probably better to default to true
      }
    }

    return result;
  }

  private void requestLocationAction(CallbackContext callbackContext) {
    locationCallback = callbackContext;

    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    cordova.startActivityForResult(this, intent, REQUEST_LOCATION_SOURCE_SETTINGS);
  }

  private void initializeAction(JSONArray args, CallbackContext callbackContext) {
    //Save init callback
    initCallbackContext = callbackContext;

    if (bluetoothAdapter != null) {
      JSONObject returnObj = new JSONObject();
      PluginResult pluginResult;

      if (bluetoothAdapter.isEnabled()) {
        addProperty(returnObj, keyStatus, statusEnabled);

        pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
        pluginResult.setKeepCallback(true);
        initCallbackContext.sendPluginResult(pluginResult);
      } else {
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

    if (obj != null && getStatusReceiver(obj)) {
      //Add a receiver to pick up when Bluetooth state changes
      activity.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
      isReceiverRegistered = true;
    }

    //Get Bluetooth adapter via Bluetooth Manager
    BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
    bluetoothAdapter = bluetoothManager.getAdapter();

    connections = new HashMap<Object, HashMap<Object, Object>>();

    JSONObject returnObj = new JSONObject();

    //If it's already enabled,
    if (bluetoothAdapter.isEnabled()) {
      addProperty(returnObj, keyStatus, statusEnabled);
      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
      pluginResult.setKeepCallback(true);
      initCallbackContext.sendPluginResult(pluginResult);
      return;
    }

    boolean request = false;
    if (obj != null) {
      request = getRequest(obj);
    }

    //Request user to enable Bluetooth
    if (request) {
      //Request Bluetooth to be enabled
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      cordova.startActivityForResult(this, enableBtIntent, REQUEST_BT_ENABLE);
    } else {
      //No request, so send back not enabled
      addProperty(returnObj, keyStatus, statusDisabled);
      addProperty(returnObj, keyMessage, logNotEnabled);
      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
      pluginResult.setKeepCallback(true);
      initCallbackContext.sendPluginResult(pluginResult);
    }
  }

  
  /**
  * Retrieves a minimal set of adapter details 
  * (address, name, initialized state, enabled state, scanning state, discoverable state)
  */
  private void getAdapterInfoAction(CallbackContext callbackContext) {    
    JSONObject returnObj = new JSONObject();    

    // Not yet initialized
    if (bluetoothAdapter == null) {      
      Activity activity = cordova.getActivity();
      BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
      BluetoothAdapter bluetoothAdapterTmp = bluetoothManager.getAdapter();

      // Since the adapter is not officially initialized, retrieve only the address and the name from the temp ad-hoc adapter
      addProperty(returnObj, keyAddress, bluetoothAdapterTmp.getAddress());
      addProperty(returnObj, keyName, bluetoothAdapterTmp.getName());
      addProperty(returnObj, keyIsInitialized, false);
      addProperty(returnObj, keyIsEnabled, false);
      addProperty(returnObj, keyIsScanning, false);
      addProperty(returnObj, keyIsDiscoverable, false);
      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
      pluginResult.setKeepCallback(true);
      callbackContext.sendPluginResult(pluginResult);
      return;      
    } else {
      // Already initialized, so use the bluetoothAdapter class property to get all the info
      addProperty(returnObj, keyAddress, bluetoothAdapter.getAddress());
      addProperty(returnObj, keyName, bluetoothAdapter.getName());
      addProperty(returnObj, keyIsInitialized, true);
      addProperty(returnObj, keyIsEnabled, bluetoothAdapter.isEnabled());
      addProperty(returnObj, keyIsScanning, (scanCallbackContext != null));
      addProperty(returnObj, keyIsDiscoverable, bluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE);
      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
      pluginResult.setKeepCallback(true);
      callbackContext.sendPluginResult(pluginResult);      
      return;
    }
    
  }

  private void enableAction(CallbackContext callbackContext) {
    if (isNotInitialized(callbackContext, false)) {
      return;
    }

    if (isNotDisabled(callbackContext)) {
      return;
    }

    boolean result = bluetoothAdapter.enable();

    if (!result) {
      //Throw an enabling error
      JSONObject returnObj = new JSONObject();

      addProperty(returnObj, keyError, errorEnable);
      addProperty(returnObj, keyMessage, logNotEnabled);

      callbackContext.error(returnObj);
    }

    //Else listen to initialize callback for enabling
  }

  private void disableAction(CallbackContext callbackContext) {
    if (isNotInitialized(callbackContext, true)) {
      return;
    }

    boolean result = bluetoothAdapter.disable();

    if (!result) {
      //Throw a disabling error
      JSONObject returnObj = new JSONObject();

      addProperty(returnObj, keyError, errorDisable);
      addProperty(returnObj, keyMessage, logNotDisabled);

      callbackContext.error(returnObj);
    }

    //Else listen to initialize callback for disabling
  }

  private synchronized void startScanAction(JSONArray args, CallbackContext callbackContext) {
    if (isNotInitialized(callbackContext, true)) {
      return;
    }

    //If the adapter is already scanning, don't call another scan.
    if (scanCallbackContext != null) {
      JSONObject returnObj = new JSONObject();
      addProperty(returnObj, keyError, errorStartScan);
      addProperty(returnObj, keyMessage, logAlreadyScanning);
      callbackContext.error(returnObj);
      return;
    }

    //Get the service UUIDs from the arguments
    JSONObject obj = getArgsObject(args);
    //Default to empty object if null, ideally part of getArgsObject, but not sure how other functions would be affected
    if (obj == null) {
      obj = new JSONObject();
    }
    UUID[] uuids = getServiceUuids(obj);

    //Save the callback context for reporting back found connections. Also the isScanning flag
    scanCallbackContext = callbackContext;

    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
      boolean result = uuids.length == 0 ? bluetoothAdapter.startLeScan(scanCallbackKitKat) : bluetoothAdapter.startLeScan(uuids, scanCallbackKitKat);

      if (!result) { // scan did not start
        JSONObject returnObj = new JSONObject();
        addProperty(returnObj, keyError, errorStartScan);
        addProperty(returnObj, keyMessage, logScanStartFail);
        callbackContext.error(returnObj);
        scanCallbackContext = null;
        return;
      }
    } else {
      /* build the ScanFilters */
      ArrayList<ScanFilter> scanFilter = new ArrayList<ScanFilter>();
      for (UUID uuid : uuids) {
        ScanFilter.Builder builder = new ScanFilter.Builder();
        builder.setServiceUuid(new ParcelUuid(uuid));
        scanFilter.add(builder.build());
      }

      /* build the ScanSetting */
      ScanSettings.Builder scanSettings = new ScanSettings.Builder();
      scanSettings.setReportDelay(0);

      int scanMode = obj.optInt(keyScanMode, ScanSettings.SCAN_MODE_LOW_LATENCY);
      try {
        scanSettings.setScanMode(scanMode);
      } catch (java.lang.IllegalArgumentException e) {
      }

      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
        int matchMode = obj.optInt(keyMatchMode, ScanSettings.MATCH_MODE_AGGRESSIVE);
        try {
          scanSettings.setMatchMode(matchMode);
        } catch (java.lang.IllegalArgumentException e) {
        }

        int matchNum = obj.optInt(keyMatchNum, ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT);
        try {
          scanSettings.setNumOfMatches(matchNum);
        } catch (java.lang.IllegalArgumentException e) {
        }

        int callbackType = obj.optInt(keyCallbackType, ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
        try {
          scanSettings.setCallbackType(callbackType);
        } catch (java.lang.IllegalArgumentException e) {
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

  private synchronized void stopScanAction(CallbackContext callbackContext) {
    if (isNotInitialized(callbackContext, true)) {
      return;
    }

    JSONObject returnObj = new JSONObject();

    //Check if already scanning
    if (scanCallbackContext == null) {
      addProperty(returnObj, keyError, errorStopScan);
      addProperty(returnObj, keyMessage, logNotScanning);
      callbackContext.error(returnObj);
      return;
    }

    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
      bluetoothAdapter.stopLeScan(scanCallbackKitKat);
    } else {
      bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
    }

    //Set scanning state
    scanCallbackContext = null;

    //Inform user
    addProperty(returnObj, keyStatus, statusScanStopped);
    callbackContext.success(returnObj);
  }

  private void retrieveConnectedAction(JSONArray args, CallbackContext callbackContext) {
    //Filtering by service UUID only works if the service UUIDs have already been discovered/cached previously
    if (isNotInitialized(callbackContext, true)) {
      return;
    }

    /*JSONObject obj = getArgsObject(args);

    UUID[] serviceUuids = serviceUuids = getServiceUuids(obj);*/

    JSONArray returnArray = new JSONArray();

    Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
    for (BluetoothDevice device : devices) {
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

  private void bondAction(JSONArray args, CallbackContext callbackContext) {
    if (!isBondReceiverRegistered) {
      cordova.getActivity().registerReceiver(mBondReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
      isBondReceiverRegistered = true;
    }

    if (isNotInitialized(callbackContext, true)) {
      return;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext)) {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext)) {
      return;
    }

    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
    if (device == null) {
      JSONObject returnObj = new JSONObject();

      addProperty(returnObj, keyError, errorBond);
      addProperty(returnObj, keyMessage, logNoDevice);
      addProperty(returnObj, keyAddress, address);

      callbackContext.error(returnObj);
      return;
    }

    CallbackContext checkCallback = (CallbackContext) bonds.get(address);
    if (checkCallback != null) {
      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);

      addProperty(returnObj, keyError, errorBond);
      addProperty(returnObj, keyMessage, logBonding);

      callbackContext.error(returnObj);
      return;
    }

    int bondState = device.getBondState();
    if (bondState == BluetoothDevice.BOND_BONDED || bondState == BluetoothDevice.BOND_BONDING) {
      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);

      addProperty(returnObj, keyError, errorBond);
      addProperty(returnObj, keyMessage, bondState == BluetoothDevice.BOND_BONDED ? logBonded : logBonding);

      callbackContext.error(returnObj);
      return;
    }

    bonds.put(address, callbackContext);

    boolean result = device.createBond();

    if (!result) {
      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);

      addProperty(returnObj, keyError, errorBond);
      addProperty(returnObj, keyMessage, logBondFail);

      callbackContext.error(returnObj);
      bonds.remove(address);
    }
  }

  private void unbondAction(JSONArray args, CallbackContext callbackContext) {
    if (!isBondReceiverRegistered) {
      cordova.getActivity().registerReceiver(mBondReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
      isBondReceiverRegistered = true;
    }

    if (isNotInitialized(callbackContext, true)) {
      return;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext)) {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext)) {
      return;
    }

    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
    if (device == null) {
      JSONObject returnObj = new JSONObject();

      addProperty(returnObj, keyError, errorBond);
      addProperty(returnObj, keyMessage, logNoDevice);
      addProperty(returnObj, keyAddress, address);

      callbackContext.error(returnObj);
      return;
    }

    CallbackContext checkCallback = (CallbackContext) bonds.get(address);
    if (checkCallback != null) {
      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);

      addProperty(returnObj, keyError, errorBond);
      addProperty(returnObj, keyMessage, logBonding);

      callbackContext.error(returnObj);
      return;
    }

    int bondState = device.getBondState();
    if (bondState == BluetoothDevice.BOND_NONE || bondState == BluetoothDevice.BOND_BONDING) {
      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);

      addProperty(returnObj, keyError, errorUnbond);
      addProperty(returnObj, keyMessage, bondState == BluetoothDevice.BOND_NONE ? logUnbonded : logBonding);

      callbackContext.error(returnObj);
      return;
    }

    bonds.put(address, callbackContext);

    boolean result = false;
    try {
      java.lang.reflect.Method mi = device.getClass().getMethod("removeBond");
      Boolean returnValue = (Boolean) mi.invoke(device);
      result = returnValue.booleanValue();
    } catch (Exception e) {
      Log.d("BLE", e.getMessage());
    }

    if (!result) {
      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);

      addProperty(returnObj, keyError, errorUnbond);
      addProperty(returnObj, keyMessage, logUnbondFail);

      callbackContext.error(returnObj);
      bonds.remove(address);
    }
  }

  private void connectAction(JSONArray args, CallbackContext callbackContext) {
    if (isNotInitialized(callbackContext, true)) {
      return;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext)) {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext)) {
      return;
    }

    if (wasConnected(address, callbackContext)) {
      return;
    }

    JSONObject returnObj = new JSONObject();

    //Ensure device exists
    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
    if (device == null) {
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

    boolean autoConnect = false;
    if (obj != null) {
      autoConnect = obj.optBoolean("autoConnect", false);
    }

    BluetoothGatt bluetoothGatt = device.connectGatt(cordova.getActivity().getApplicationContext(), autoConnect, bluetoothGattCallback);

    connection.put(keyPeripheral, bluetoothGatt);

    connections.put(device.getAddress(), connection);
  }

  private void reconnectAction(JSONArray args, CallbackContext callbackContext) {
    if (isNotInitialized(callbackContext, true)) {
      return;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext)) {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext)) {
      return;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null) {
      return;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt) connection.get(keyPeripheral);
    BluetoothDevice device = bluetoothGatt.getDevice();

    if (isNotDisconnected(connection, device, callbackContext)) {
      return;
    }

    JSONObject returnObj = new JSONObject();

    addDevice(returnObj, device);

    boolean result = bluetoothGatt.connect();

    if (!result) {
      addProperty(returnObj, keyError, errorReconnect);
      addProperty(returnObj, keyMessage, logReconnectFail);
      callbackContext.error(returnObj);
      return;
    }

    connection.put(keyState, BluetoothProfile.STATE_CONNECTING);
    //connection.put(keyDiscoveredState, STATE_UNDISCOVERED); //Devices stays discovered even if disconnected (but not closed)
    connection.put(operationConnect, callbackContext);
  }

  private void disconnectAction(JSONArray args, CallbackContext callbackContext) {
    if (isNotInitialized(callbackContext, true)) {
      return;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext)) {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext)) {
      return;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null) {
      return;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt) connection.get(keyPeripheral);
    BluetoothDevice device = bluetoothGatt.getDevice();

    if (isDisconnected(connection, device, callbackContext)) {
      return;
    }

    int state = Integer.valueOf(connection.get(keyState).toString());

    JSONObject returnObj = new JSONObject();

    //Return disconnecting status and keep callback
    addDevice(returnObj, device);

    //If it's connecting, cancel attempt and return disconnect
    if (state == BluetoothProfile.STATE_CONNECTING) {
      addProperty(returnObj, keyStatus, statusDisconnected);
      connection.put(keyState, BluetoothProfile.STATE_DISCONNECTED);

      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);

      connection.remove(operationConnect);
    } else {
      //Very unlikely that this is DISCONNECTING
      connection.put(operationConnect, callbackContext);
    }

    bluetoothGatt.disconnect();
  }

  private void closeAction(JSONArray args, CallbackContext callbackContext) {
    if (isNotInitialized(callbackContext, true)) {
      return;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext)) {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext)) {
      return;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null) {
      return;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt) connection.get(keyPeripheral);
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

    //Check for queued operations in progress on this device
    Operation operation = queue.peek();
    if (operation != null && operation.device != null && operation.device.getAddress().equals(address)) {
      queueRemove();
    }
  }

  private void discoverAction(JSONArray args, CallbackContext callbackContext) {
    if (isNotInitialized(callbackContext, true)) {
      return;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext)) {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext)) {
      return;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null) {
      return;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt) connection.get(keyPeripheral);
    BluetoothDevice device = bluetoothGatt.getDevice();

    if (isNotConnected(connection, device, callbackContext)) {
      return;
    }

    JSONObject returnObj = new JSONObject();

    addDevice(returnObj, device);

    int discoveredState = Integer.valueOf(connection.get(keyDiscoveredState).toString());
    //Already initiated discovery
    if (discoveredState == STATE_DISCOVERING) {
      addProperty(returnObj, keyError, errorDiscover);
      addProperty(returnObj, keyMessage, logAlreadyDiscovering);
      callbackContext.error(returnObj);
      return;
    } else if (discoveredState == STATE_DISCOVERED) {
      //Already discovered
      returnObj = getDiscovery(bluetoothGatt);
      callbackContext.success(returnObj);
      return;
    }

    //Else undiscovered, so start discovery
    connection.put(keyDiscoveredState, STATE_DISCOVERING);
    connection.put(operationDiscover, callbackContext);

    if (obj != null && obj.optBoolean("clearCache", false)) {
      refreshDeviceCache(bluetoothGatt);
    }

    bluetoothGatt.discoverServices();
  }

  private boolean refreshDeviceCache(BluetoothGatt gatt) {
    try {
      BluetoothGatt localBluetoothGatt = gatt;
      java.lang.reflect.Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
      if (localMethod != null) {
        boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
        return bool;
      }
    } 
    catch (Exception localException) {
      Log.e("BLE", "An exception occured while refreshing device cache");
    }
    return false;
  }

  private boolean readAction(Operation operation) {
    JSONArray args = operation.args;
    CallbackContext callbackContext = operation.callbackContext;

    if (isNotInitialized(callbackContext, true)) {
      return false;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext)) {
      return false;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext)) {
      return false;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null) {
      return false;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt) connection.get(keyPeripheral);
    BluetoothDevice device = bluetoothGatt.getDevice();

    if (isNotConnected(connection, device, callbackContext)) {
      return false;
    }

    BluetoothGattService service = getService(bluetoothGatt, obj);

    if (isNotService(service, device, callbackContext)) {
      return false;
    }

    BluetoothGattCharacteristic characteristic = getCharacteristic(obj, service);

    if (isNotCharacteristic(characteristic, device, callbackContext)) {
      return false;
    }

    UUID characteristicUuid = characteristic.getUuid();

    AddCallback(characteristicUuid, connection, operationRead, callbackContext);

    boolean result = bluetoothGatt.readCharacteristic(characteristic);

    if (!result) {
      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);

      addCharacteristic(returnObj, characteristic);

      addProperty(returnObj, keyError, errorRead);
      addProperty(returnObj, keyMessage, logReadFail);

      callbackContext.error(returnObj);

      RemoveCallback(characteristicUuid, connection, operationRead);

      return false;
    }

    operation.device = device;

    return true;
  }

  private boolean subscribeAction(Operation operation) {
    JSONArray args = operation.args;
    CallbackContext callbackContext = operation.callbackContext;

    if (isNotInitialized(callbackContext, true)) {
      return false;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext)) {
      return false;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext)) {
      return false;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null) {
      return false;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt) connection.get(keyPeripheral);
    BluetoothDevice device = bluetoothGatt.getDevice();

    if (isNotConnected(connection, device, callbackContext)) {
      return false;
    }

    BluetoothGattService service = getService(bluetoothGatt, obj);

    if (isNotService(service, device, callbackContext)) {
      return false;
    }

    BluetoothGattCharacteristic characteristic = getCharacteristic(obj, service);

    if (isNotCharacteristic(characteristic, device, callbackContext)) {
      return false;
    }

    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(clientConfigurationDescriptorUuid);

    if (isNotDescriptor(descriptor, device, callbackContext)) {
      return false;
    }

    UUID characteristicUuid = characteristic.getUuid();

    JSONObject returnObj = new JSONObject();

    addDevice(returnObj, device);

    addCharacteristic(returnObj, characteristic);

    CallbackContext checkExisting = GetCallback(characteristicUuid, connection, operationSubscribe);
    if (checkExisting != null) {
      addProperty(returnObj, keyError, errorSubscription);
      addProperty(returnObj, keyMessage, logSubscribeAlready);
      callbackContext.error(returnObj);
      return false;
    }

    boolean result = false;

    //Use properties to determine whether notification or indication should be used
    if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == BluetoothGattCharacteristic.PROPERTY_NOTIFY) {
      result = descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
    } else {
      result = descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
    }

    if (!result) {
      addProperty(returnObj, keyError, errorWriteDescriptor);
      addProperty(returnObj, keyMessage, logWriteDescriptorValueNotSet);
      callbackContext.error(returnObj);
      return false;
    }

    AddCallback(characteristicUuid, connection, operationSubscribe, callbackContext);

    //Write the descriptor value
    result = bluetoothGatt.writeDescriptor(descriptor);

    if (!result) {
      addProperty(returnObj, keyError, errorWriteDescriptor);
      addProperty(returnObj, keyMessage, logWriteDescriptorFail);
      callbackContext.error(returnObj);
      RemoveCallback(characteristicUuid, connection, operationSubscribe);
      return false;
    }

    operation.device = device;

    return true;
  }

  private boolean unsubscribeAction(Operation operation) {
    JSONArray args = operation.args;
    CallbackContext callbackContext = operation.callbackContext;

    if (isNotInitialized(callbackContext, true)) {
      return false;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext)) {
      return false;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext)) {
      return false;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null) {
      return false;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt) connection.get(keyPeripheral);
    BluetoothDevice device = bluetoothGatt.getDevice();

    if (isNotConnected(connection, device, callbackContext)) {
      return false;
    }

    BluetoothGattService service = getService(bluetoothGatt, obj);

    if (isNotService(service, device, callbackContext)) {
      return false;
    }

    BluetoothGattCharacteristic characteristic = getCharacteristic(obj, service);

    if (isNotCharacteristic(characteristic, device, callbackContext)) {
      return false;
    }

    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(clientConfigurationDescriptorUuid);

    if (isNotDescriptor(descriptor, device, callbackContext)) {
      return false;
    }

    UUID characteristicUuid = characteristic.getUuid();

    JSONObject returnObj = new JSONObject();

    addDevice(returnObj, device);

    addCharacteristic(returnObj, characteristic);

    CallbackContext checkExisting = GetCallback(characteristicUuid, connection, operationSubscribe);
    if (checkExisting == null) {
      addProperty(returnObj, keyError, errorSubscription);
      addProperty(returnObj, keyMessage, logUnsubscribeAlready);
      callbackContext.error(returnObj);
      return false;
    }
    RemoveCallback(characteristicUuid, connection, operationSubscribe);

    //Set the descriptor for disabling notification/indication
    boolean result = descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);

    if (!result) {
      addProperty(returnObj, keyError, errorWriteDescriptor);
      addProperty(returnObj, keyMessage, logWriteDescriptorValueNotSet);
      callbackContext.error(returnObj);
      return false;
    }

    AddCallback(characteristicUuid, connection, operationUnsubscribe, callbackContext);

    //Write the actual descriptor value
    result = bluetoothGatt.writeDescriptor(descriptor);

    if (!result) {
      addProperty(returnObj, keyError, errorWriteDescriptor);
      addProperty(returnObj, keyMessage, logWriteDescriptorFail);
      callbackContext.error(returnObj);
      RemoveCallback(characteristicUuid, connection, operationUnsubscribe);
      return false;
    }

    operation.device = device;

    return true;
  }

  private boolean writeAction(Operation operation) {
    JSONArray args = operation.args;
    CallbackContext callbackContext = operation.callbackContext;

    if (isNotInitialized(callbackContext, true)) {
      return false;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext)) {
      return false;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext)) {
      return false;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null) {
      return false;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt) connection.get(keyPeripheral);
    BluetoothDevice device = bluetoothGatt.getDevice();

    if (isNotConnected(connection, device, callbackContext)) {
      return false;
    }

    BluetoothGattService service = getService(bluetoothGatt, obj);

    if (isNotService(service, device, callbackContext)) {
      return false;
    }

    BluetoothGattCharacteristic characteristic = getCharacteristic(obj, service);

    if (isNotCharacteristic(characteristic, device, callbackContext)) {
      return false;
    }

    UUID characteristicUuid = characteristic.getUuid();

    JSONObject returnObj = new JSONObject();

    addDevice(returnObj, device);

    addCharacteristic(returnObj, characteristic);

    byte[] value = getPropertyBytes(obj, keyValue);

    if (value == null) {
      addProperty(returnObj, keyError, errorWrite);
      addProperty(returnObj, keyMessage, logWriteValueNotFound);
      callbackContext.error(returnObj);
      return false;
    }

    int writeType = this.getWriteType(obj);
    characteristic.setWriteType(writeType);

    boolean result = characteristic.setValue(value);

    if (!result) {
      addProperty(returnObj, keyError, errorWrite);
      addProperty(returnObj, keyMessage, logWriteValueNotSet);
      callbackContext.error(returnObj);
      return false;
    }

    AddCallback(characteristicUuid, connection, operationWrite, callbackContext);

    result = bluetoothGatt.writeCharacteristic(characteristic);

    if (!result) {
      addProperty(returnObj, keyError, errorWrite);
      addProperty(returnObj, keyMessage, logWriteFail);
      callbackContext.error(returnObj);
      RemoveCallback(characteristicUuid, connection, operationWrite);

      return false;
    }

    operation.device = device;

    return true;
  }

  private void writeQAction(JSONArray args, CallbackContext callbackContext) {
    if (isNotInitialized(callbackContext, true)) {
      return;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext)) {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext)) {
      return;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null) {
      return;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt) connection.get(keyPeripheral);
    BluetoothDevice device = bluetoothGatt.getDevice();

    if (isNotConnected(connection, device, callbackContext)) {
      return;
    }

    BluetoothGattService service = getService(bluetoothGatt, obj);

    if (isNotService(service, device, callbackContext)) {
      return;
    }

    BluetoothGattCharacteristic characteristic = getCharacteristic(obj, service);

    if (isNotCharacteristic(characteristic, device, callbackContext)) {
      return;
    }

    UUID characteristicUuid = characteristic.getUuid();

    JSONObject returnObj = new JSONObject();

    addDevice(returnObj, device);

    addCharacteristic(returnObj, characteristic);

    byte[] value = getPropertyBytes(obj, keyValue);

    if (value == null) {
      addProperty(returnObj, keyError, errorWrite);
      addProperty(returnObj, keyMessage, logWriteValueNotFound);
      callbackContext.error(returnObj);
      return;
    }

    int writeType = this.getWriteType(obj);
    characteristic.setWriteType(writeType);

    AddCallback(characteristicUuid, connection, operationWrite, callbackContext);

    queueQuick.clear();

    int length = value.length;
    int chunkSize = 20;
    int offset = 0;

    do {
      int thisChunkSize = length - offset > chunkSize ? chunkSize : length - offset;

      byte[] chunk = Arrays.copyOfRange(value, offset, offset + thisChunkSize);

      offset += thisChunkSize;

      queueQuick.add(chunk);

    } while (offset < length);

    writeQ(connection, characteristic, bluetoothGatt);
  }

  private void writeQ(HashMap<Object, Object> connection, BluetoothGattCharacteristic characteristic, BluetoothGatt bluetoothGatt) {
    byte[] value = queueQuick.poll();

    if (value == null) {
      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, bluetoothGatt.getDevice());

      addCharacteristic(returnObj, characteristic);

      addProperty(returnObj, keyError, errorWrite);
      addProperty(returnObj, keyMessage, "Queue was empty");

      CallbackContext callbackContext = GetCallback(characteristic.getUuid(), connection, operationWrite);
      RemoveCallback(characteristic.getUuid(), connection, operationWrite);

      callbackContext.error(returnObj);

      return;
    }

    boolean result = characteristic.setValue(value);
    if (!result) {
      queueQuick.clear();

      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, bluetoothGatt.getDevice());

      addCharacteristic(returnObj, characteristic);

      addProperty(returnObj, keyError, errorWrite);
      addProperty(returnObj, keyMessage, logWriteValueNotSet);

      CallbackContext callbackContext = GetCallback(characteristic.getUuid(), connection, operationWrite);
      RemoveCallback(characteristic.getUuid(), connection, operationWrite);

      callbackContext.error(returnObj);
      return;
    }

    result = bluetoothGatt.writeCharacteristic(characteristic);
    if (!result) {
      queueQuick.clear();

      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, bluetoothGatt.getDevice());

      addCharacteristic(returnObj, characteristic);

      addProperty(returnObj, keyError, errorWrite);
      addProperty(returnObj, keyMessage, logWriteFail);

      CallbackContext callbackContext = GetCallback(characteristic.getUuid(), connection, operationWrite);
      RemoveCallback(characteristic.getUuid(), connection, operationWrite);

      callbackContext.error(returnObj);
    }
  }

  private boolean readDescriptorAction(Operation operation) {
    JSONArray args = operation.args;
    CallbackContext callbackContext = operation.callbackContext;

    if (isNotInitialized(callbackContext, true)) {
      return false;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext)) {
      return false;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext)) {
      return false;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null) {
      return false;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt) connection.get(keyPeripheral);
    BluetoothDevice device = bluetoothGatt.getDevice();

    if (isNotConnected(connection, device, callbackContext)) {
      return false;
    }

    BluetoothGattService service = getService(bluetoothGatt, obj);

    if (isNotService(service, device, callbackContext)) {
      return false;
    }

    BluetoothGattCharacteristic characteristic = getCharacteristic(obj, service);

    if (isNotCharacteristic(characteristic, device, callbackContext)) {
      return false;
    }

    BluetoothGattDescriptor descriptor = getDescriptor(obj, characteristic);

    if (isNotDescriptor(descriptor, device, callbackContext)) {
      return false;
    }

    UUID descriptorUuid = descriptor.getUuid();
    UUID characteristicUuid = characteristic.getUuid();

    AddDescriptorCallback(descriptorUuid, characteristicUuid, connection, operationRead, callbackContext);

    boolean result = bluetoothGatt.readDescriptor(descriptor);

    if (!result) {
      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);

      addDescriptor(returnObj, descriptor);

      addProperty(returnObj, keyError, errorReadDescriptor);
      addProperty(returnObj, keyMessage, logReadDescriptorFail);

      callbackContext.error(returnObj);

      RemoveDescriptorCallback(descriptorUuid, characteristicUuid, connection, operationRead);

      return false;
    }

    operation.device = device;

    return true;
  }

  private boolean writeDescriptorAction(Operation operation) {
    JSONArray args = operation.args;
    CallbackContext callbackContext = operation.callbackContext;

    if (isNotInitialized(callbackContext, true)) {
      return false;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext)) {
      return false;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext)) {
      return false;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null) {
      return false;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt) connection.get(keyPeripheral);
    BluetoothDevice device = bluetoothGatt.getDevice();

    if (isNotConnected(connection, device, callbackContext)) {
      return false;
    }

    BluetoothGattService service = getService(bluetoothGatt, obj);

    if (isNotService(service, device, callbackContext)) {
      return false;
    }

    BluetoothGattCharacteristic characteristic = getCharacteristic(obj, service);

    if (isNotCharacteristic(characteristic, device, callbackContext)) {
      return false;
    }

    BluetoothGattDescriptor descriptor = getDescriptor(obj, characteristic);

    if (isNotDescriptor(descriptor, device, callbackContext)) {
      return false;
    }

    UUID descriptorUuid = descriptor.getUuid();
    UUID characteristicUuid = characteristic.getUuid();

    JSONObject returnObj = new JSONObject();

    addDevice(returnObj, device);

    addDescriptor(returnObj, descriptor);

    //Let subscribe/unsubscribe take care of it
    if (descriptor.getUuid().equals(clientConfigurationDescriptorUuid)) {
      addProperty(returnObj, keyError, errorWriteDescriptor);
      addProperty(returnObj, keyMessage, logWriteDescriptorNotAllowed);
      callbackContext.error(returnObj);
      return false;
    }

    //TODO get property type

    byte[] value = getPropertyBytes(obj, keyValue);

    if (value == null) {
      addProperty(returnObj, keyError, errorWriteDescriptor);
      addProperty(returnObj, keyMessage, logWriteDescriptorValueNotFound);
      callbackContext.error(returnObj);
      return false;
    }

    boolean result = descriptor.setValue(value);

    if (!result) {
      addProperty(returnObj, keyError, errorWriteDescriptor);
      addProperty(returnObj, keyMessage, logWriteDescriptorValueNotSet);
      callbackContext.error(returnObj);
      return false;
    }

    AddDescriptorCallback(descriptorUuid, characteristicUuid, connection, operationWrite, callbackContext);

    result = bluetoothGatt.writeDescriptor(descriptor);

    if (!result) {
      addProperty(returnObj, keyError, errorWriteDescriptor);
      addProperty(returnObj, keyMessage, logWriteDescriptorFail);
      callbackContext.error(returnObj);
      RemoveDescriptorCallback(descriptorUuid, characteristicUuid, connection, operationWrite);
      return false;
    }

    operation.device = device;

    return true;
  }

  private void rssiAction(JSONArray args, CallbackContext callbackContext) {
    if (isNotInitialized(callbackContext, true)) {
      return;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext)) {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext)) {
      return;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null) {
      return;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt) connection.get(keyPeripheral);
    BluetoothDevice device = bluetoothGatt.getDevice();

    if (isNotConnected(connection, device, callbackContext)) {
      return;
    }

    connection.put(operationRssi, callbackContext);

    boolean result = bluetoothGatt.readRemoteRssi();

    if (!result) {
      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);

      addProperty(returnObj, keyError, errorRssi);
      addProperty(returnObj, keyMessage, logRssiFail);

      callbackContext.error(returnObj);

      connection.remove(operationRssi);
      return;
    }
  }

  private void mtuAction(JSONArray args, CallbackContext callbackContext) {
    if (isNotInitialized(callbackContext, true)) {
      return;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext)) {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext)) {
      return;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null) {
      return;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt) connection.get(keyPeripheral);
    BluetoothDevice device = bluetoothGatt.getDevice();

    if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);

      addProperty(returnObj, keyError, errorMtu);
      addProperty(returnObj, keyMessage, logRequiresAPI21);

      callbackContext.error(returnObj);
      return;
    }

    if (isNotConnected(connection, device, callbackContext)) {
      return;
    }

    connection.put(operationMtu, callbackContext);

    int mtu = getMtu(obj);

    boolean result = bluetoothGatt.requestMtu(mtu);

    if (!result) {
      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);

      addProperty(returnObj, keyError, errorMtu);
      addProperty(returnObj, keyMessage, logMtuFail);

      callbackContext.error(returnObj);

      connection.remove(operationMtu);
    }
  }

  private void isInitializedAction(CallbackContext callbackContext) {
    boolean result = (bluetoothAdapter != null);

    JSONObject returnObj = new JSONObject();
    addProperty(returnObj, keyIsInitialized, result);

    callbackContext.success(returnObj);
  }

  private void isEnabledAction(CallbackContext callbackContext) {
    boolean result = (bluetoothAdapter != null && bluetoothAdapter.isEnabled());

    JSONObject returnObj = new JSONObject();
    addProperty(returnObj, keyIsEnabled, result);

    callbackContext.success(returnObj);
  }

  private void isScanningAction(CallbackContext callbackContext) {
    boolean result = (scanCallbackContext != null);

    JSONObject returnObj = new JSONObject();
    addProperty(returnObj, keyIsScanning, result);

    callbackContext.success(returnObj);
  }

  private void isBondedAction(JSONArray args, CallbackContext callbackContext) {
    if (isNotInitialized(callbackContext, true)) {
      return;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext)) {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext)) {
      return;
    }

    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
    if (device == null) {
      JSONObject returnObj = new JSONObject();

      addProperty(returnObj, keyError, errorBond);
      addProperty(returnObj, keyMessage, logNoDevice);
      addProperty(returnObj, keyAddress, address);

      callbackContext.error(returnObj);
      return;
    }

    boolean result = (device.getBondState() == BluetoothDevice.BOND_BONDED);

    JSONObject returnObj = new JSONObject();

    addProperty(returnObj, keyIsBonded, result);

    addDevice(returnObj, device);

    callbackContext.success(returnObj);
  }

  private void wasConnectedAction(JSONArray args, CallbackContext callbackContext) {
    if (isNotInitialized(callbackContext, true)) {
      return;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext)) {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext)) {
      return;
    }

    HashMap<Object, Object> connection = connections.get(address);
    if (connection == null) {
      JSONObject returnObj = new JSONObject();

      addProperty(returnObj, "wasConnected", false);

      addProperty(returnObj, keyAddress, address);

      callbackContext.success(returnObj);

      return;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt) connection.get(keyPeripheral);

    BluetoothDevice device = bluetoothGatt.getDevice();

    JSONObject returnObj = new JSONObject();

    addProperty(returnObj, "wasConnected", true);

    addDevice(returnObj, device);

    callbackContext.success(returnObj);
  }

  private void isConnectedAction(JSONArray args, CallbackContext callbackContext) {
    if (isNotInitialized(callbackContext, true)) {
      return;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext)) {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext)) {
      return;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null) {
      return;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt) connection.get(keyPeripheral);

    int state = Integer.valueOf(connection.get(keyState).toString());

    boolean result = (state == BluetoothAdapter.STATE_CONNECTED);

    BluetoothDevice device = bluetoothGatt.getDevice();

    JSONObject returnObj = new JSONObject();

    addProperty(returnObj, keyIsConnected, result);

    addDevice(returnObj, device);

    callbackContext.success(returnObj);
  }

  private void isDiscoveredAction(JSONArray args, CallbackContext callbackContext) {
    if (isNotInitialized(callbackContext, true)) {
      return;
    }

    JSONObject obj = getArgsObject(args);
    if (isNotArgsObject(obj, callbackContext)) {
      return;
    }

    String address = getAddress(obj);
    if (isNotAddress(address, callbackContext)) {
      return;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null) {
      return;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt) connection.get(keyPeripheral);
    BluetoothDevice device = bluetoothGatt.getDevice();

    if (isNotConnected(connection, device, callbackContext)) {
      return;
    }

    int state = Integer.valueOf(connection.get(keyDiscoveredState).toString());

    boolean result = (state == STATE_DISCOVERED);

    JSONObject returnObj = new JSONObject();

    addProperty(returnObj, keyIsDiscovered, result);

    addDevice(returnObj, device);

    callbackContext.success(returnObj);
  }

  private void requestConnectionPriorityAction(JSONArray args, CallbackContext callbackContext) {
    if (isNotInitialized(callbackContext, true)) {
      return;
    }

    JSONObject obj = getArgsObject(args);

    if (isNotArgsObject(obj, callbackContext)) {
      return;
    }

    String address = getAddress(obj);

    if (isNotAddress(address, callbackContext)) {
      return;
    }

    HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
    if (connection == null) {
      return;
    }

    BluetoothGatt bluetoothGatt = (BluetoothGatt) connection.get(keyPeripheral);

    if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, bluetoothGatt.getDevice());

      addProperty(returnObj, keyError, errorRequestConnectionPriority);
      addProperty(returnObj, keyMessage, logRequiresAPI21);

      callbackContext.error(returnObj);
      return;
    }

    String priority = obj.optString(keyConnectionPriority, null);

    int androidPriority = BluetoothGatt.CONNECTION_PRIORITY_BALANCED;

    if (priority == null) {
      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, bluetoothGatt.getDevice());

      addProperty(returnObj, keyError, errorRequestConnectionPriority);
      addProperty(returnObj, keyMessage, logRequestConnectionPriorityNull);

      callbackContext.error(returnObj);
      return;
    } else if (priority.equals(propertyConnectionPriorityLow)) {
      androidPriority = BluetoothGatt.CONNECTION_PRIORITY_LOW_POWER;
    } else if (priority.equals(propertyConnectionPriorityBalanced)) {
      androidPriority = BluetoothGatt.CONNECTION_PRIORITY_BALANCED;
    } else if (priority.equals(propertyConnectionPriorityHigh)) {
      androidPriority = BluetoothGatt.CONNECTION_PRIORITY_HIGH;
    } else {
      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, bluetoothGatt.getDevice());

      addProperty(returnObj, keyError, errorRequestConnectionPriority);
      addProperty(returnObj, keyMessage, logRequestConnectionPriorityInvalid);

      callbackContext.error(returnObj);
      return;
    }

    boolean result = bluetoothGatt.requestConnectionPriority(androidPriority);
    if (!result) {
      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, bluetoothGatt.getDevice());

      addProperty(returnObj, keyError, errorRequestConnectionPriority);
      addProperty(returnObj, keyMessage, logRequestConnectionPriorityFailed);

      callbackContext.error(returnObj);
    } else {
      JSONObject returnObj = new JSONObject();

      addProperty(returnObj, keyStatus, statusConnectionPriorityRequested);

      addDevice(returnObj, bluetoothGatt.getDevice());

      callbackContext.success(returnObj);
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    if (isReceiverRegistered) {
      cordova.getActivity().unregisterReceiver(mReceiver);
    }
    if (isBondReceiverRegistered) {
      cordova.getActivity().unregisterReceiver(mBondReceiver);
    }
  }

  private BroadcastReceiver mReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (initCallbackContext == null) {
        return;
      }

      if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
        JSONObject returnObj = new JSONObject();
        PluginResult pluginResult;

        switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
          case BluetoothAdapter.STATE_OFF:
            addProperty(returnObj, keyStatus, statusDisabled);
            addProperty(returnObj, keyMessage, logNotEnabled);

            connections = new HashMap<Object, HashMap<Object, Object>>();
            scanCallbackContext = null;

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

  private final BroadcastReceiver mBondReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
        int previousBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -1);

        String address = device.getAddress();

        CallbackContext callback = (CallbackContext) bonds.get(address);
        if (callback == null) {
          return;
        }

        JSONObject returnObj = new JSONObject();

        addDevice(returnObj, device);

        boolean keepCallback = false;

        switch (bondState) {
          case BluetoothDevice.BOND_BONDED:
            addProperty(returnObj, keyStatus, statusBonded);
            break;
          case BluetoothDevice.BOND_BONDING:
            addProperty(returnObj, keyStatus, statusBonding);
            keepCallback = true;
            break;
          case BluetoothDevice.BOND_NONE:
            addProperty(returnObj, keyStatus, statusUnbonded);
            break;
        }

        if (!keepCallback) {
          bonds.remove(address);
        }

        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
        pluginResult.setKeepCallback(keepCallback);
        callback.sendPluginResult(pluginResult);
      }
    }
  };

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    //If this was a Bluetooth enablement request...
    if (requestCode == REQUEST_BT_ENABLE) {
      //If callback doesnt exist, no reason to proceed
      if (initCallbackContext == null) {
        return;
      }

      //Whether the result code was successful or not, just check whether Bluetooth is enabled
      if (!bluetoothAdapter.isEnabled()) {
        JSONObject returnObj = new JSONObject();
        addProperty(returnObj, keyStatus, statusDisabled);
        addProperty(returnObj, keyMessage, logNotEnabled);

        PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, returnObj);
        pluginResult.setKeepCallback(true);
        initCallbackContext.sendPluginResult(pluginResult);
      }
    } else if (requestCode == REQUEST_LOCATION_SOURCE_SETTINGS) {
      if (locationCallback != null) {
        JSONObject returnObj = new JSONObject();

        addProperty(returnObj, "requestLocation", isLocationEnabled());

        locationCallback.success(returnObj);

        locationCallback = null;
      }
    }
  }

  //Scan Callback for KitKat
  private LeScanCallback scanCallbackKitKat = new LeScanCallback() {
    @Override
    public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
      synchronized (BluetoothLePlugin.this) {

        if (scanCallbackContext == null) {
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

  //API 21+ Scan and Advertise Callbacks
  private ScanCallback scanCallback = null;
  private AdvertiseCallback advertiseCallback = null;

  private void createScanCallback() {
    scanCallback = new ScanCallback() {
      @Override
      public void onBatchScanResults(List<ScanResult> results) {
        if (scanCallbackContext == null)
          return;
      }

      @Override
      public void onScanFailed(int errorCode) {
        synchronized (BluetoothLePlugin.this) {
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
        synchronized (BluetoothLePlugin.this) {
          if (scanCallbackContext == null)
            return;

          JSONObject returnObj = new JSONObject();

          addDevice(returnObj, result.getDevice());
          if(result.getScanRecord().getDeviceName() != null){
            addProperty(returnObj, keyName, result.getScanRecord().getDeviceName().replace("\0", ""));
          }
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

  private void createAdvertiseCallback() {
    advertiseCallback = new AdvertiseCallback() {
      @Override
      public void onStartFailure(int errorCode) {
        isAdvertising = false;

        if (advertiseCallbackContext == null)
          return;

        JSONObject returnObj = new JSONObject();
        addProperty(returnObj, keyError, "startAdvertising");

        if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_ALREADY_STARTED) {
          addProperty(returnObj, keyMessage, "Already started");
        } else if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_DATA_TOO_LARGE) {
          addProperty(returnObj, keyMessage, "Too large data");
        } else if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_FEATURE_UNSUPPORTED) {
          addProperty(returnObj, keyMessage, "Feature unsupported");
        } else if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_INTERNAL_ERROR) {
          addProperty(returnObj, keyMessage, "Internal error");
        } else if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_TOO_MANY_ADVERTISERS) {
          addProperty(returnObj, keyMessage, "Too many advertisers");
        } else {
          addProperty(returnObj, keyMessage, "Advertising error");
        }

        advertiseCallbackContext.error(returnObj);
        advertiseCallbackContext = null;
      }

      @Override
      public void onStartSuccess(AdvertiseSettings settingsInEffect) {
        isAdvertising = true;

        if (advertiseCallbackContext == null)
          return;

        JSONObject returnObj = new JSONObject();

        addProperty(returnObj, "mode", settingsInEffect.getMode());
        addProperty(returnObj, "timeout", settingsInEffect.getTimeout());
        addProperty(returnObj, "txPowerLevel", settingsInEffect.getTxPowerLevel());
        addProperty(returnObj, "isConnectable", settingsInEffect.isConnectable());

        addProperty(returnObj, keyStatus, "advertisingStarted");

        advertiseCallbackContext.success(returnObj);
        advertiseCallbackContext = null;
      }
    };
  }

  private String formatUuid(UUID uuid) {
    String uuidString = uuid.toString().toUpperCase();

    if (uuidString.startsWith(baseUuidStart) && uuidString.endsWith(baseUuidEnd)) {
      return uuidString.substring(4, 8);
    }

    return uuidString;
  }

  //Helpers for BluetoothGatt classes
  private UUID getUUID(String value) {
    if (value == null) {
      return null;
    }

    if (value.length() == 4) {
      value = baseUuidStart + value + baseUuidEnd;
    }

    UUID uuid = null;

    try {
      uuid = UUID.fromString(value);
    } catch (Exception ex) {
      return null;
    }

    return uuid;
  }

  private BluetoothGattService getService(BluetoothGatt bluetoothGatt, JSONObject obj) {
    UUID uuid = getUUID(obj.optString("service", null));

    BluetoothGattService service = bluetoothGatt.getService(uuid);

    if (service == null) {
      return null;
    }

    return service;
  }

  private BluetoothGattCharacteristic getCharacteristic(JSONObject obj, BluetoothGattService service) {
    UUID uuid = getUUID(obj.optString("characteristic", null));

    BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuid);

    if (characteristic == null) {
      return null;
    }

    return characteristic;
  }

  private BluetoothGattDescriptor getDescriptor(JSONObject obj, BluetoothGattCharacteristic characteristic) {
    UUID uuid = getUUID(obj.optString("descriptor", null));

    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(uuid);

    if (descriptor == null) {
      return null;
    }

    return descriptor;
  }

  //Helpers for Callbacks
  private void queueStart() {
    //Attempt to start the queue whenever a new operation is added
    if (queue.size() > 1) {
      //There was already something in the queue so wait for queueNext to be called
      return;
    }

    //Added to queue and immediately ready for processing
    queueNext();
  }

  private void queueNext() {
    //Start to process the next command
    Operation operation = queue.peek();
    //If the operation was unsuccessful, remove immediately and start next
    boolean result = true;
    if (operation.type.equals("read")) {
      result = readAction(operation);
    } else if (operation.type.equals("write")) {
      result = writeAction(operation);
    } else if (operation.type.equals("readDescriptor")) {
      result = readDescriptorAction(operation);
    } else if (operation.type.equals("writeDescriptor")) {
      result = writeDescriptorAction(operation);
    } else if (operation.type.equals("subscribe")) {
      result = subscribeAction(operation);
    } else {
      result = unsubscribeAction(operation);
    }
    if (!result) {
      queueRemove();
    }
  }

  private void queueRemove() {
    //Ensure the queue has something in it, this should never be empty
    if (queue.size() == 0) {
      return;
    }
    //Remove front of the queue
    queue.poll();

    //See if there's anything left to process
    if (queue.size() == 0) {
      return;
    }

    //Start the next item
    queueNext();
  }

  private HashMap<Object, Object> EnsureCallback(UUID characteristicUuid, HashMap<Object, Object> connection) {
    HashMap<Object, Object> characteristicCallbacks = (HashMap<Object, Object>) connection.get(characteristicUuid);

    if (characteristicCallbacks != null) {
      return characteristicCallbacks;
    }

    characteristicCallbacks = new HashMap<Object, Object>();
    connection.put(characteristicUuid, characteristicCallbacks);

    return characteristicCallbacks;
  }

  private void AddCallback(UUID characteristicUuid, HashMap<Object, Object> connection, String operationType, CallbackContext callbackContext) {
    HashMap<Object, Object> characteristicCallbacks = EnsureCallback(characteristicUuid, connection);

    characteristicCallbacks.put(operationType, callbackContext);
  }

  private CallbackContext GetCallback(UUID characteristicUuid, HashMap<Object, Object> connection, String operationType) {
    HashMap<Object, Object> characteristicCallbacks = (HashMap<Object, Object>) connection.get(characteristicUuid);

    if (characteristicCallbacks == null) {
      return null;
    }

    //This may return null
    return (CallbackContext) characteristicCallbacks.get(operationType);
  }

  private CallbackContext[] GetCallbacks(HashMap<Object, Object> connection) {
    ArrayList<CallbackContext> callbacks = new ArrayList<CallbackContext>();

    for (Object key : connection.keySet()) {

      if (key instanceof String) {
        if (key.equals(operationDiscover) || key.equals(operationRssi) || key.equals(operationMtu)) {
          CallbackContext callback = (CallbackContext) connection.get(key);
          if (callback == null) {
            continue;
          }

          callbacks.add(callback);
        }

        continue;
      }

      if (!(key instanceof UUID)) {
        continue;
      }

      HashMap<Object, Object> characteristic = (HashMap<Object, Object>) connection.get(key);
      GetMoreCallbacks(characteristic, callbacks);
    }

    return callbacks.toArray(new CallbackContext[callbacks.size()]);
  }

  private void GetMoreCallbacks(HashMap<Object, Object> lower, ArrayList<CallbackContext> callbacks) {
    for (Object key : lower.keySet()) {
      if (key instanceof UUID) {
        HashMap<Object, Object> next = (HashMap<Object, Object>) lower.get(key);
        GetMoreCallbacks(next, callbacks);
        continue;
      }

      if (!(key instanceof String)) {
        continue;
      }

      CallbackContext callback = (CallbackContext) lower.get(key);

      if (callback == null) {
        continue;
      }

      callbacks.add(callback);
    }
  }

  private void RemoveCallback(UUID characteristicUuid, HashMap<Object, Object> connection, String operationType) {
    HashMap<Object, Object> characteristicCallbacks = (HashMap<Object, Object>) connection.get(characteristicUuid);

    if (characteristicCallbacks == null) {
      return;
    }

    characteristicCallbacks.remove(operationType);
  }

  private HashMap<Object, Object> EnsureDescriptorCallback(UUID descriptorUuid, UUID characteristicUuid, HashMap<Object, Object> connection) {
    HashMap<Object, Object> characteristicCallbacks = EnsureCallback(characteristicUuid, connection);

    HashMap<Object, Object> descriptorCallbacks = (HashMap<Object, Object>) characteristicCallbacks.get(descriptorUuid);

    if (descriptorCallbacks != null) {
      return descriptorCallbacks;
    }

    descriptorCallbacks = new HashMap<Object, Object>();
    characteristicCallbacks.put(descriptorUuid, descriptorCallbacks);

    return descriptorCallbacks;
  }

  private void AddDescriptorCallback(UUID descriptorUuid, UUID characteristicUuid, HashMap<Object, Object> connection, String operationType, CallbackContext callbackContext) {
    HashMap<Object, Object> descriptorCallbacks = EnsureDescriptorCallback(descriptorUuid, characteristicUuid, connection);

    descriptorCallbacks.put(operationType, callbackContext);
  }

  private CallbackContext GetDescriptorCallback(UUID descriptorUuid, UUID characteristicUuid, HashMap<Object, Object> connection, String operationType) {
    HashMap<Object, Object> characteristicCallbacks = (HashMap<Object, Object>) connection.get(characteristicUuid);

    if (characteristicCallbacks == null) {
      return null;
    }

    HashMap<Object, Object> descriptorCallbacks = (HashMap<Object, Object>) characteristicCallbacks.get(descriptorUuid);

    if (descriptorCallbacks == null) {
      return null;
    }

    //This may return null
    return (CallbackContext) descriptorCallbacks.get(operationType);
  }

  private void RemoveDescriptorCallback(UUID descriptorUuid, UUID characteristicUuid, HashMap<Object, Object> connection, String operationType) {
    HashMap<Object, Object> characteristicCallbacks = (HashMap<Object, Object>) connection.get(characteristicUuid);

    if (characteristicCallbacks == null) {
      return;
    }

    HashMap<Object, Object> descriptorCallbacks = (HashMap<Object, Object>) characteristicCallbacks.get(descriptorUuid);

    if (descriptorCallbacks == null) {
      return;
    }

    descriptorCallbacks.remove(descriptorUuid);
  }

  //Helpers to Check Conditions
  private boolean isNotInitialized(CallbackContext callbackContext, boolean checkIsNotEnabled) {
    if (bluetoothAdapter == null) {
      JSONObject returnObj = new JSONObject();

      addProperty(returnObj, keyError, errorInitialize);
      addProperty(returnObj, keyMessage, logNotInit);

      callbackContext.error(returnObj);

      return true;
    }

    if (checkIsNotEnabled) {
      return isNotEnabled(callbackContext);
    } else {
      return false;
    }
  }

  private boolean isNotEnabled(CallbackContext callbackContext) {
    if (!bluetoothAdapter.isEnabled()) {
      JSONObject returnObj = new JSONObject();

      addProperty(returnObj, keyError, errorEnable);
      addProperty(returnObj, keyMessage, logNotEnabled);

      callbackContext.error(returnObj);

      return true;
    }

    return false;
  }

  private boolean isNotDisabled(CallbackContext callbackContext) {
    if (bluetoothAdapter.isEnabled()) {
      JSONObject returnObj = new JSONObject();

      addProperty(returnObj, keyError, errorDisable);
      addProperty(returnObj, keyMessage, logNotDisabled);

      callbackContext.error(returnObj);

      return true;
    }

    return false;
  }

  private boolean isNotArgsObject(JSONObject obj, CallbackContext callbackContext) {
    if (obj != null) {
      return false;
    }

    JSONObject returnObj = new JSONObject();

    addProperty(returnObj, keyError, errorArguments);
    addProperty(returnObj, keyMessage, logNoArgObj);

    callbackContext.error(returnObj);

    return true;
  }

  private boolean isNotAddress(String address, CallbackContext callbackContext) {
    if (address == null) {
      JSONObject returnObj = new JSONObject();

      addProperty(returnObj, keyError, errorConnect);
      addProperty(returnObj, keyMessage, logNoAddress);

      callbackContext.error(returnObj);
      return true;
    }

    return false;
  }

  private boolean isNotService(BluetoothGattService service, BluetoothDevice device, CallbackContext callbackContext) {
    if (service != null) {
      return false;
    }

    JSONObject returnObj = new JSONObject();

    addProperty(returnObj, keyError, errorService);
    addProperty(returnObj, keyMessage, logNoService);

    addDevice(returnObj, device);

    callbackContext.error(returnObj);

    return true;
  }

  private boolean isNotCharacteristic(BluetoothGattCharacteristic characteristic, BluetoothDevice device, CallbackContext callbackContext) {
    if (characteristic != null) {
      return false;
    }

    JSONObject returnObj = new JSONObject();

    addProperty(returnObj, keyError, errorCharacteristic);
    addProperty(returnObj, keyMessage, logNoCharacteristic);

    addDevice(returnObj, device);

    callbackContext.error(returnObj);

    return true;
  }

  private boolean isNotDescriptor(BluetoothGattDescriptor descriptor, BluetoothDevice device, CallbackContext callbackContext) {
    if (descriptor != null) {
      return false;
    }

    JSONObject returnObj = new JSONObject();

    addProperty(returnObj, keyError, errorDescriptor);
    addProperty(returnObj, keyMessage, logNoDescriptor);

    addDevice(returnObj, device);

    callbackContext.error(returnObj);

    return true;
  }

  private boolean isNotDisconnected(HashMap<Object, Object> connection, BluetoothDevice device, CallbackContext callbackContext) {
    int state = Integer.valueOf(connection.get(keyState).toString());

    //Determine whether the device is currently connected including connecting and disconnecting
    //Certain actions like connect and reconnect can only be done while completely disconnected
    if (state == BluetoothProfile.STATE_DISCONNECTED) {
      return false;
    }

    JSONObject returnObj = new JSONObject();

    addProperty(returnObj, keyError, errorIsNotDisconnected);
    addProperty(returnObj, keyMessage, logIsNotDisconnected);

    addDevice(returnObj, device);

    callbackContext.error(returnObj);

    return true;
  }

  private boolean isDisconnected(HashMap<Object, Object> connection, BluetoothDevice device, CallbackContext callbackContext) {
    int state = Integer.valueOf(connection.get(keyState).toString());

    //Determine whether the device is currently disconnected NOT including connecting and disconnecting
    //Certain actions like disconnect can be done while connected, connecting, disconnecting
    if (state != BluetoothProfile.STATE_DISCONNECTED) {
      return false;
    }

    JSONObject returnObj = new JSONObject();

    addProperty(returnObj, keyError, errorIsDisconnected);
    addProperty(returnObj, keyMessage, logIsDisconnected);

    addDevice(returnObj, device);

    callbackContext.error(returnObj);

    return true;
  }

  private boolean isNotConnected(HashMap<Object, Object> connection, BluetoothDevice device, CallbackContext callbackContext) {
    int state = Integer.valueOf(connection.get(keyState).toString());

    //Determine whether the device is currently disconnected including connecting and disconnecting
    //Certain actions like read/write operations can only be done while completely connected
    if (state == BluetoothProfile.STATE_CONNECTED) {
      return false;
    }

    JSONObject returnObj = new JSONObject();

    addProperty(returnObj, keyError, errorIsNotConnected);
    addProperty(returnObj, keyMessage, logIsNotConnected);

    addDevice(returnObj, device);

    callbackContext.error(returnObj);

    return true;
  }

  private boolean wasConnected(String address, CallbackContext callbackContext) {
    HashMap<Object, Object> connection = connections.get(address);
    if (connection != null) {
      BluetoothGatt peripheral = (BluetoothGatt) connection.get(keyPeripheral);
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

  private HashMap<Object, Object> wasNeverConnected(String address, CallbackContext callbackContext) {
    HashMap<Object, Object> connection = connections.get(address);
    if (connection != null) {
      return connection;
    }

    JSONObject returnObj = new JSONObject();

    addProperty(returnObj, keyError, errorNeverConnected);
    addProperty(returnObj, keyMessage, logNeverConnected);
    addProperty(returnObj, keyAddress, address);

    callbackContext.error(returnObj);

    return null;
  }

  private void addDevice(JSONObject returnObj, BluetoothDevice device) {
    addProperty(returnObj, keyAddress, device.getAddress());
    addProperty(returnObj, keyName, device.getName());
  }

  private void addService(JSONObject returnObj, BluetoothGattService service) {
    addProperty(returnObj, keyService, formatUuid(service.getUuid()));
  }

  private void addCharacteristic(JSONObject returnObj, BluetoothGattCharacteristic characteristic) {
    addService(returnObj, characteristic.getService());
    addProperty(returnObj, keyCharacteristic, formatUuid(characteristic.getUuid()));
  }

  private void addDescriptor(JSONObject returnObj, BluetoothGattDescriptor descriptor) {
    addCharacteristic(returnObj, descriptor.getCharacteristic());
    addProperty(returnObj, keyDescriptor, formatUuid(descriptor.getUuid()));
  }

  //General Helpers
  private void addProperty(JSONObject obj, String key, Object value) {
    //Believe exception only occurs when adding duplicate keys, so just ignore it
    try {
      if (value == null) {
        obj.put(key, JSONObject.NULL);
      } else {
        obj.put(key, value);
      }
    } catch (JSONException e) {
    }
  }

  private void addPropertyBytes(JSONObject obj, String key, byte[] bytes) {
    String string = Base64.encodeToString(bytes, Base64.NO_WRAP);

    addProperty(obj, key, string);
  }

  private JSONObject getArgsObject(JSONArray args) {
    if (args.length() == 1) {
      try {
        return args.getJSONObject(0);
      } catch (JSONException ex) {
      }
    }

    return null;
  }

  private byte[] getPropertyBytes(JSONObject obj, String key) {
    String string = obj.optString(key, null);

    if (string == null) {
      return null;
    }

    byte[] bytes = Base64.decode(string, Base64.NO_WRAP);

    if (bytes == null || bytes.length == 0) {
      return null;
    }

    return bytes;
  }

  private UUID[] getServiceUuids(JSONObject obj) {
    if (obj == null) {
      return new UUID[]{};
    }

    JSONArray array = obj.optJSONArray(keyServices);

    if (array == null) {
      return new UUID[]{};
    }

    //Create temporary array list for building array of UUIDs
    ArrayList<UUID> arrayList = new ArrayList<UUID>();

    //Iterate through the UUID strings
    for (int i = 0; i < array.length(); i++) {
      String value = array.optString(i, null);

      if (value == null) {
        continue;
      }

      if (value.length() == 4) {
        value = baseUuidStart + value + baseUuidEnd;
      }

      //Try converting string to UUID and add to list
      try {
        UUID uuid = UUID.fromString(value);
        arrayList.add(uuid);
      } catch (Exception ex) {
      }
    }

    UUID[] uuids = new UUID[arrayList.size()];
    uuids = arrayList.toArray(uuids);
    return uuids;
  }

  private String getAddress(JSONObject obj) {
    //Get the address string from arguments
    String address = obj.optString(keyAddress, null);

    if (address == null) {
      return null;
    }

    //Validate address format
    if (!BluetoothAdapter.checkBluetoothAddress(address)) {
      return null;
    }

    return address;
  }

  private boolean getRequest(JSONObject obj) {
    return obj.optBoolean(keyRequest, false);
  }

  private boolean getStatusReceiver(JSONObject obj) {
    return obj.optBoolean(keyStatusReceiver, true);
  }

  private int getWriteType(JSONObject obj) {
    String writeType = obj.optString(keyType, null);

    if (writeType == null || !writeType.equals(writeTypeNoResponse)) {
      return BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT;
    }
    return BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE;
  }

  private int getMtu(JSONObject obj) {
    int mtu = obj.optInt(keyMtu);

    if (mtu == 0) {
      return 23;
    }

    return mtu;
  }

  private JSONObject getDiscovery(BluetoothGatt bluetoothGatt) {
    JSONObject deviceObject = new JSONObject();

    BluetoothDevice device = bluetoothGatt.getDevice();

    addProperty(deviceObject, keyStatus, statusDiscovered);

    addDevice(deviceObject, device);

    JSONArray servicesArray = new JSONArray();

    List<BluetoothGattService> services = bluetoothGatt.getServices();

    for (BluetoothGattService service : services) {
      JSONObject serviceObject = new JSONObject();

      addProperty(serviceObject, keyUuid, formatUuid(service.getUuid()));

      JSONArray characteristicsArray = new JSONArray();

      List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();

      for (BluetoothGattCharacteristic characteristic : characteristics) {
        JSONObject characteristicObject = new JSONObject();

        addProperty(characteristicObject, keyUuid, formatUuid(characteristic.getUuid()));
        addProperty(characteristicObject, keyProperties, getProperties(characteristic));
        addProperty(characteristicObject, keyPermissions, getPermissions(characteristic));

        JSONArray descriptorsArray = new JSONArray();

        List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();

        for (BluetoothGattDescriptor descriptor : descriptors) {
          JSONObject descriptorObject = new JSONObject();

          addProperty(descriptorObject, keyUuid, formatUuid(descriptor.getUuid()));
          addProperty(descriptorObject, keyPermissions, getPermissions(descriptor));

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

  private JSONObject getProperties(BluetoothGattCharacteristic characteristic) {
    int properties = characteristic.getProperties();

    JSONObject propertiesObject = new JSONObject();

    if ((properties & BluetoothGattCharacteristic.PROPERTY_BROADCAST) == BluetoothGattCharacteristic.PROPERTY_BROADCAST) {
      addProperty(propertiesObject, propertyBroadcast, true);
    }

    if ((properties & BluetoothGattCharacteristic.PROPERTY_READ) == BluetoothGattCharacteristic.PROPERTY_READ) {
      addProperty(propertiesObject, propertyRead, true);
    }

    if ((properties & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) == BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) {
      addProperty(propertiesObject, propertyWriteWithoutResponse, true);
    }

    if ((properties & BluetoothGattCharacteristic.PROPERTY_WRITE) == BluetoothGattCharacteristic.PROPERTY_WRITE) {
      addProperty(propertiesObject, propertyWrite, true);
    }

    if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == BluetoothGattCharacteristic.PROPERTY_NOTIFY) {
      addProperty(propertiesObject, propertyNotify, true);
    }

    if ((properties & BluetoothGattCharacteristic.PROPERTY_INDICATE) == BluetoothGattCharacteristic.PROPERTY_INDICATE) {
      addProperty(propertiesObject, propertyIndicate, true);
    }

    if ((properties & BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE) == BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE) {
      addProperty(propertiesObject, propertyAuthenticatedSignedWrites, true);
    }

    if ((properties & BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS) == BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS) {
      addProperty(propertiesObject, propertyExtendedProperties, true);
    }

    if ((properties & 0x100) == 0x100) {
      addProperty(propertiesObject, propertyNotifyEncryptionRequired, true);
    }

    if ((properties & 0x200) == 0x200) {
      addProperty(propertiesObject, propertyIndicateEncryptionRequired, true);
    }

    return propertiesObject;
  }

  private JSONObject getPermissions(BluetoothGattCharacteristic characteristic) {
    int permissions = characteristic.getPermissions();

    JSONObject permissionsObject = new JSONObject();

    if ((permissions & BluetoothGattCharacteristic.PERMISSION_READ) == BluetoothGattCharacteristic.PERMISSION_READ) {
      addProperty(permissionsObject, permissionRead, true);
    }

    if ((permissions & BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED) == BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED) {
      addProperty(permissionsObject, permissionReadEncrypted, true);
    }

    if ((permissions & BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED_MITM) == BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED_MITM) {
      addProperty(permissionsObject, permissionReadEncryptedMITM, true);
    }

    if ((permissions & BluetoothGattCharacteristic.PERMISSION_WRITE) == BluetoothGattCharacteristic.PERMISSION_WRITE) {
      addProperty(permissionsObject, permissionWrite, true);
    }

    if ((permissions & BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED) == BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED) {
      addProperty(permissionsObject, permissionWriteEncrypted, true);
    }

    if ((permissions & BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED_MITM) == BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED_MITM) {
      addProperty(permissionsObject, permissionWriteEncryptedMITM, true);
    }

    if ((permissions & BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED) == BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED) {
      addProperty(permissionsObject, permissionWriteSigned, true);
    }

    if ((permissions & BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED_MITM) == BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED_MITM) {
      addProperty(permissionsObject, permissionWriteSignedMITM, true);
    }

    return permissionsObject;
  }

  private JSONObject getPermissions(BluetoothGattDescriptor descriptor) {
    int permissions = descriptor.getPermissions();

    JSONObject permissionsObject = new JSONObject();

    if ((permissions & BluetoothGattDescriptor.PERMISSION_READ) == BluetoothGattDescriptor.PERMISSION_READ) {
      addProperty(permissionsObject, permissionRead, true);
    }

    if ((permissions & BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED) == BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED) {
      addProperty(permissionsObject, permissionReadEncrypted, true);
    }

    if ((permissions & BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED_MITM) == BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED_MITM) {
      addProperty(permissionsObject, permissionReadEncryptedMITM, true);
    }

    if ((permissions & BluetoothGattDescriptor.PERMISSION_WRITE) == BluetoothGattDescriptor.PERMISSION_WRITE) {
      addProperty(permissionsObject, permissionWrite, true);
    }

    if ((permissions & BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED) == BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED) {
      addProperty(permissionsObject, permissionWriteEncrypted, true);
    }

    if ((permissions & BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED_MITM) == BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED_MITM) {
      addProperty(permissionsObject, permissionWriteEncryptedMITM, true);
    }

    if ((permissions & BluetoothGattDescriptor.PERMISSION_WRITE_SIGNED) == BluetoothGattDescriptor.PERMISSION_WRITE_SIGNED) {
      addProperty(permissionsObject, permissionWriteSigned, true);
    }

    if ((permissions & BluetoothGattDescriptor.PERMISSION_WRITE_SIGNED_MITM) == BluetoothGattDescriptor.PERMISSION_WRITE_SIGNED_MITM) {
      addProperty(permissionsObject, permissionWriteSignedMITM, true);
    }

    return permissionsObject;
  }

  //Bluetooth callback for connecting, discovering, reading and writing
  private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
      //Get the connected device
      BluetoothDevice device = gatt.getDevice();
      String address = device.getAddress();

      //Check for queued operations in progress on this device
      if (newState == BluetoothProfile.STATE_DISCONNECTED) {
        Operation operation = queue.peek();
        if (operation != null && operation.device != null && operation.device.getAddress().equals(address)) {
          queueRemove();
        }
      }

      HashMap<Object, Object> connection = connections.get(address);
      if (connection == null) {
        return;
      }

      CallbackContext callbackContext = (CallbackContext) connection.get(operationConnect);

      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);

      int oldState = Integer.valueOf(connection.get(keyState).toString());
      if (status != BluetoothGatt.GATT_SUCCESS && oldState == BluetoothProfile.STATE_CONNECTING) {
        //Clear out all the callbacks
        connection = new HashMap<Object, Object>();
        connection.put(keyPeripheral, gatt);
        connection.put(keyState, BluetoothProfile.STATE_DISCONNECTED);
        connection.put(keyDiscoveredState, STATE_UNDISCOVERED);

        connections.put(device.getAddress(), connection);

        if (callbackContext == null) {
          return;
        }

        addProperty(returnObj, keyError, errorConnect);
        addProperty(returnObj, keyMessage, logConnectFail);

        callbackContext.error(returnObj);

        return;
      }

      connection.put(keyState, newState);

      //Device was connected
      if (newState == BluetoothProfile.STATE_CONNECTED) {
        if (callbackContext == null) {
          return;
        }

        addProperty(returnObj, keyStatus, statusConnected);

        //Keep connection call back for disconnect
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
        pluginResult.setKeepCallback(true);
        callbackContext.sendPluginResult(pluginResult);
      } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
        //Device was disconnected
        CallbackContext[] callbacks = GetCallbacks(connection);
        addProperty(returnObj, keyError, errorIsDisconnected);
        addProperty(returnObj, keyMessage, logIsDisconnected);

        for (CallbackContext callback : callbacks) {
          callback.error(returnObj);
        }

        returnObj.remove(keyError);
        returnObj.remove(keyMessage);

        //Save the old discovered state
        Object discoveredState = connection.get(keyDiscoveredState);

        //Clear out all the callbacks
        connection = new HashMap<Object, Object>();
        connection.put(keyPeripheral, gatt);
        connection.put(keyState, BluetoothProfile.STATE_DISCONNECTED);

        //Save state in new connection
        connection.put(keyDiscoveredState, discoveredState);

        connections.put(device.getAddress(), connection);

        if (callbackContext == null) {
          return;
        }

        addProperty(returnObj, keyStatus, statusDisconnected);

        callbackContext.success(returnObj);
      }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
      //Get the connected device
      BluetoothDevice device = gatt.getDevice();
      String address = device.getAddress();

      HashMap<Object, Object> connection = connections.get(address);
      if (connection == null) {
        return;
      }

      int discoveredState = (status == BluetoothGatt.GATT_SUCCESS) ? STATE_DISCOVERED : STATE_UNDISCOVERED;
      connection.put(keyDiscoveredState, discoveredState);

      CallbackContext callbackContext = (CallbackContext) connection.get(operationDiscover);
      connection.remove(operationDiscover);

      //Shouldn't happen, but check for null callback
      if (callbackContext == null) {
        return;
      }

      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);

      //If successfully discovered, return list of services, characteristics and descriptors
      if (status == BluetoothGatt.GATT_SUCCESS) {
        returnObj = getDiscovery(gatt);
        callbackContext.success(returnObj);
      } else {
        //Else it failed
        addProperty(returnObj, keyError, errorDiscover);
        addProperty(returnObj, keyMessage, logDiscoveryFail);
        callbackContext.error(returnObj);
      }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
      queueRemove();

      //Get the connected device
      BluetoothDevice device = gatt.getDevice();
      String address = device.getAddress();

      HashMap<Object, Object> connection = connections.get(address);
      if (connection == null) {
        return;
      }

      UUID characteristicUuid = characteristic.getUuid();

      CallbackContext callbackContext = GetCallback(characteristicUuid, connection, operationRead);
      RemoveCallback(characteristicUuid, connection, operationRead);

      //If no callback, just return
      if (callbackContext == null) {
        return;
      }

      JSONObject returnObj = new JSONObject();

      addCharacteristic(returnObj, characteristic);

      addDevice(returnObj, device);

      //If successfully read, return value
      if (status == BluetoothGatt.GATT_SUCCESS) {
        addProperty(returnObj, keyStatus, statusRead);
        addPropertyBytes(returnObj, keyValue, characteristic.getValue());
        callbackContext.success(returnObj);
      } else {
        //Else it failed
        addProperty(returnObj, keyError, errorRead);
        addProperty(returnObj, keyMessage, logReadFailReturn);
        callbackContext.error(returnObj);
      }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
      //Get the connected device
      BluetoothDevice device = gatt.getDevice();
      String address = device.getAddress();

      HashMap<Object, Object> connection = connections.get(address);
      if (connection == null) {
        return;
      }

      UUID characteristicUuid = characteristic.getUuid();

      CallbackContext callbackContext = GetCallback(characteristicUuid, connection, operationSubscribe);

      //If no callback, just return
      if (callbackContext == null) {
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
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
      queueRemove();

      //Get the connected device
      BluetoothDevice device = gatt.getDevice();
      String address = device.getAddress();

      HashMap<Object, Object> connection = connections.get(address);
      if (connection == null) {
        return;
      }

      UUID characteristicUuid = characteristic.getUuid();

      CallbackContext callbackContext = GetCallback(characteristicUuid, connection, operationWrite);

      //Check if any other write commands are queued up
      if (queueQuick.size() > 0) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
          writeQ(connection, characteristic, gatt);
        } else {
          //If there was an error, clear the queue
          queueQuick.clear();

          if (callbackContext == null) {
            return;
          }

          JSONObject returnObj = new JSONObject();

          addDevice(returnObj, device);
          addCharacteristic(returnObj, characteristic);

          addProperty(returnObj, keyError, errorWrite);
          addProperty(returnObj, keyMessage, logWriteFailReturn);
          callbackContext.error(returnObj);
        }

        return;
      }

      RemoveCallback(characteristicUuid, connection, operationWrite);

      //If no callback, just return
      if (callbackContext == null) {
        return;
      }

      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);
      addCharacteristic(returnObj, characteristic);

      //If write was successful, return the written value
      if (status == BluetoothGatt.GATT_SUCCESS) {
        addProperty(returnObj, keyStatus, statusWritten);
        addPropertyBytes(returnObj, keyValue, characteristic.getValue());
        callbackContext.success(returnObj);
      } else {
        //Else it failed
        addProperty(returnObj, keyError, errorWrite);
        addProperty(returnObj, keyMessage, logWriteFailReturn);
        callbackContext.error(returnObj);
      }
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
      queueRemove();

      //Get the connected device
      BluetoothDevice device = gatt.getDevice();
      String address = device.getAddress();

      HashMap<Object, Object> connection = connections.get(address);
      if (connection == null) {
        return;
      }

      BluetoothGattCharacteristic characteristic = descriptor.getCharacteristic();
      UUID characteristicUuid = characteristic.getUuid();
      UUID descriptorUuid = descriptor.getUuid();

      CallbackContext callbackContext = GetDescriptorCallback(descriptorUuid, characteristicUuid, connection, operationRead);
      RemoveDescriptorCallback(descriptorUuid, characteristicUuid, connection, operationRead);

      //If callback is null, just return
      if (callbackContext == null) {
        return;
      }

      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);

      addDescriptor(returnObj, descriptor);

      //If descriptor was successful, return the written value
      if (status == BluetoothGatt.GATT_SUCCESS) {
        addProperty(returnObj, keyStatus, statusReadDescriptor);
        addPropertyBytes(returnObj, keyValue, descriptor.getValue());
        callbackContext.success(returnObj);
      } else {
        //Else it failed
        addProperty(returnObj, keyError, errorReadDescriptor);
        addProperty(returnObj, keyMessage, logReadDescriptorFailReturn);
        callbackContext.error(returnObj);
      }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
      queueRemove();

      //Get the connected device
      BluetoothDevice device = gatt.getDevice();
      String address = device.getAddress();

      HashMap<Object, Object> connection = connections.get(address);
      if (connection == null) {
        return;
      }

      BluetoothGattCharacteristic characteristic = descriptor.getCharacteristic();
      UUID characteristicUuid = characteristic.getUuid();
      UUID descriptorUuid = descriptor.getUuid();

      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);

      addDescriptor(returnObj, descriptor);

      //See if notification/indication is enabled or disabled and use subscribe/unsubscribe callback instead
      if (descriptorUuid.equals(clientConfigurationDescriptorUuid)) {
        if (descriptor.getValue() == BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE) {
          //Unsubscribe to the characteristic
          boolean result = gatt.setCharacteristicNotification(characteristic, false);

          CallbackContext callbackContext = GetCallback(characteristicUuid, connection, operationUnsubscribe);

          //If no callback, just return
          if (callbackContext == null) {
            return;
          }

          if (status != BluetoothGatt.GATT_SUCCESS) {
            addProperty(returnObj, keyError, errorSubscription);
            addProperty(returnObj, keyMessage, logUnsubscribeFail);
            callbackContext.error(returnObj);
            return;
          }

          if (!result) {
            addProperty(returnObj, keyError, errorSubscription);
            addProperty(returnObj, keyMessage, logUnsubscribeFail);
            callbackContext.error(returnObj);
            return;
          }

          //Get the unsubscribed operation callback and clear
          addProperty(returnObj, keyStatus, statusUnsubscribed);

          callbackContext.success(returnObj);
        } else {
          //Subscribe to the characteristic
          boolean result = gatt.setCharacteristicNotification(characteristic, true);

          CallbackContext callbackContext = GetCallback(characteristicUuid, connection, operationSubscribe);

          //If no callback, just return
          if (callbackContext == null) {
            return;
          }

          if (!result) {
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

      CallbackContext callbackContext = GetDescriptorCallback(descriptorUuid, characteristicUuid, connection, operationWrite);
      RemoveDescriptorCallback(descriptorUuid, characteristicUuid, connection, operationWrite);

      //If callback is null, just return
      if (callbackContext == null) {
        return;
      }

      //If descriptor was written, return written value
      if (status == BluetoothGatt.GATT_SUCCESS) {
        addProperty(returnObj, keyStatus, statusWrittenDescriptor);
        addPropertyBytes(returnObj, keyValue, descriptor.getValue());
        callbackContext.success(returnObj);
      } else {
        //Else it failed
        addProperty(returnObj, keyError, errorWriteDescriptor);
        addProperty(returnObj, keyMessage, logWriteDescriptorFailReturn);
        callbackContext.error(returnObj);
      }
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
      //Get the connected device
      BluetoothDevice device = gatt.getDevice();
      String address = device.getAddress();

      HashMap<Object, Object> connection = connections.get(address);
      if (connection == null) {
        return;
      }

      CallbackContext callbackContext = (CallbackContext) connection.get(operationRssi);
      connection.remove(operationRssi);

      //If no callback, just return
      if (callbackContext == null) {
        return;
      }

      JSONObject returnObj = new JSONObject();
      addDevice(returnObj, device);

      //If successfully read RSSI, return value
      if (status == BluetoothGatt.GATT_SUCCESS) {
        addProperty(returnObj, keyStatus, statusRssi);
        addProperty(returnObj, keyRssi, rssi);
        callbackContext.success(returnObj);
      } else {
        //Else it failed
        addProperty(returnObj, keyError, errorRssi);
        addProperty(returnObj, keyMessage, logRssiFailReturn);
        callbackContext.error(returnObj);
      }
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
      BluetoothDevice device = gatt.getDevice();
      String address = device.getAddress();

      HashMap<Object, Object> connection = connections.get(address);
      if (connection == null) {
        return;
      }

      CallbackContext callbackContext = (CallbackContext) connection.get(operationMtu);
      connection.remove(operationMtu);

      if (callbackContext == null) {
        return;
      }

      JSONObject returnObj = new JSONObject();
      addDevice(returnObj, device);

      if (status == BluetoothGatt.GATT_SUCCESS) {
        addProperty(returnObj, keyStatus, statusMtu);
        addProperty(returnObj, keyMtu, mtu);
        callbackContext.success(returnObj);
      } else {
        addProperty(returnObj, keyError, errorMtu);
        addProperty(returnObj, keyMessage, logMtuFailReturn);
        callbackContext.error(returnObj);
      }
    }
  };

  //Bluetooth callback for connecting, discovering, reading and writing
  private BluetoothGattServerCallback bluetoothGattServerCallback = new BluetoothGattServerCallback() {
    public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
      if (initPeripheralCallback == null) {
        return;
      }

      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);
      addCharacteristic(returnObj, characteristic);

      addProperty(returnObj, "status", "readRequested");
      addProperty(returnObj, "requestId", requestId);
      addProperty(returnObj, "offset", offset);

      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
      pluginResult.setKeepCallback(true);
      initPeripheralCallback.sendPluginResult(pluginResult);
    }

    public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
      if (initPeripheralCallback == null) {
        return;
      }

      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);
      addCharacteristic(returnObj, characteristic);

      addProperty(returnObj, "status", "writeRequested");
      addProperty(returnObj, "requestId", requestId);
      addProperty(returnObj, "offset", offset);
      addPropertyBytes(returnObj, "value", value);

      addProperty(returnObj, "preparedWrite", preparedWrite);
      addProperty(returnObj, "responseNeeded", responseNeeded);

      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
      pluginResult.setKeepCallback(true);
      initPeripheralCallback.sendPluginResult(pluginResult);
    }

    public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
      if (initPeripheralCallback == null) {
        return;
      }

      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);

      if (newState == BluetoothGatt.STATE_CONNECTED) {
        addProperty(returnObj, "status", "connected");
      } else {
        addProperty(returnObj, "status", "disconnected");
      }

      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
      pluginResult.setKeepCallback(true);
      initPeripheralCallback.sendPluginResult(pluginResult);
    }

    public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
      if (initPeripheralCallback == null) {
        return;
      }

      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);
      addDescriptor(returnObj, descriptor);

      addProperty(returnObj, "status", "readRequested");
      addProperty(returnObj, "requestId", requestId);
      addProperty(returnObj, "offset", offset);

      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
      pluginResult.setKeepCallback(true);
      initPeripheralCallback.sendPluginResult(pluginResult);
    }

    public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
      if (initPeripheralCallback == null) {
        return;
      }

      if (descriptor.getUuid().equals(clientConfigurationDescriptorUuid)) {
        JSONObject returnObj = new JSONObject();

        addDevice(returnObj, device);
        addCharacteristic(returnObj, descriptor.getCharacteristic());

        if (Arrays.equals(value, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)) {
          addProperty(returnObj, "status", "unsubscribed");
        } else {
          addProperty(returnObj, "status", "subscribed");
        }

        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
        pluginResult.setKeepCallback(true);
        initPeripheralCallback.sendPluginResult(pluginResult);

        gattServer.sendResponse(device, requestId, 0, offset, value);

        return;
      }

      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);
      addDescriptor(returnObj, descriptor);

      addProperty(returnObj, "status", "writeRequested");
      addProperty(returnObj, "requestId", requestId);
      addProperty(returnObj, "offset", offset);
      addPropertyBytes(returnObj, "value", value);

      addProperty(returnObj, "preparedWrite", preparedWrite);
      addProperty(returnObj, "responseNeeded", responseNeeded);

      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
      pluginResult.setKeepCallback(true);
      initPeripheralCallback.sendPluginResult(pluginResult);
    }

    //TODO implement this later
    public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
      //Log.d("BLE", "execute write");
    }

    public void onMtuChanged(BluetoothDevice device, int mtu) {
      if (initPeripheralCallback == null) {
        return;
      }

      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);
      addProperty(returnObj, "status", "mtuChanged");
      addProperty(returnObj, "mtu", mtu);

      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
      pluginResult.setKeepCallback(true);
      initPeripheralCallback.sendPluginResult(pluginResult);
    }

    public void onNotificationSent(BluetoothDevice device, int status) {
      if (initPeripheralCallback == null) {
        return;
      }

      JSONObject returnObj = new JSONObject();

      addDevice(returnObj, device);

      if (status == BluetoothGatt.GATT_SUCCESS) {
        addProperty(returnObj, "status", "notificationSent");
      } else {
        addProperty(returnObj, "error", "notificationSent");
        addProperty(returnObj, "message", "Unable to send notification");
      }

      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
      pluginResult.setKeepCallback(true);
      initPeripheralCallback.sendPluginResult(pluginResult);
    }

    public void onServiceAdded(int status, BluetoothGattService service) {
      if (addServiceCallback == null) {
        return;
      }

      JSONObject returnObj = new JSONObject();

      addService(returnObj, service);

      if (status == BluetoothGatt.GATT_SUCCESS) {
        addProperty(returnObj, "status", "serviceAdded");
        addServiceCallback.success(returnObj);
      } else {
        addProperty(returnObj, "error", "service");
        addProperty(returnObj, "message", "Unable to add service");
        addServiceCallback.error(returnObj);
      }
    }
  };
}
