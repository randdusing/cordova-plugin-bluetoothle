package com.randdusing.bluetoothle;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import android.bluetooth.BluetoothDevice;

public class Operation {
  public String type;
  public JSONArray args;
  public CallbackContext callbackContext;
  public BluetoothDevice device;

  public Operation(String type, JSONArray args, CallbackContext callbackContext) {
    this.type = type;
    this.args = args;
    this.callbackContext = callbackContext;
  }
}
