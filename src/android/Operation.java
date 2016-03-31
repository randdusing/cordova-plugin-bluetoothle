package com.randdusing.bluetoothle;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;

public class Operation {
  public String type;
  public JSONArray args;
  public CallbackContext callbackContext;

  public Operation(String type, JSONArray args, CallbackContext callbackContext) {
    this.type = type;
    this.args = args;
    this.callbackContext = callbackContext;
  }
}
