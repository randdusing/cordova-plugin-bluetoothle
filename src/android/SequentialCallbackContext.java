// Inspiration taken from cordova-plugin-ble-central

package com.randdusing.bluetoothle;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class SequentialCallbackContext {
    private int sequence;
    private CallbackContext context;

    public SequentialCallbackContext(CallbackContext context) {
        this.context = context;
        this.sequence = 0;
    }

    private int getNextSequenceNumber() {
        synchronized(this) {
            return this.sequence++; 
        }
    }

    public CallbackContext getContext() {
      return this.context;
    }

    public PluginResult createSequentialResult(JSONObject returnObj) {
        List<PluginResult> resultList = new ArrayList<PluginResult>(2);

        PluginResult dataResult = new PluginResult(PluginResult.Status.OK, returnObj);
        PluginResult sequenceResult = new PluginResult(PluginResult.Status.OK, this.getNextSequenceNumber()); 

        resultList.add(dataResult);
        resultList.add(sequenceResult);
        
        return new PluginResult(PluginResult.Status.OK, resultList);
    }

    public void sendSequentialResult(JSONObject returnObj) {
        PluginResult result = this.createSequentialResult(returnObj);
        result.setKeepCallback(true);

        this.context.sendPluginResult(result);
    }
}