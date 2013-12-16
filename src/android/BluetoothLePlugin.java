package com.randdusing.bluetoothle;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BluetoothLePlugin extends CordovaPlugin {

  private BluetoothManager bluetoothManager;
  private BluetoothAdapter bluetoothAdapter;
  
  private boolean scanning;
  private Handler scanHandler;

  // Stops scanning after 10 seconds.
  private static final long SCAN_LIMIT = 10000;
  
  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException
  {
    if ("init".equals(action))
    {
      initAction(callbackContext);
      /*cordova.getThreadPool().execute(new Runnable() {
        public void run() {
            // Main Code goes here
            callbackContext.success(); 
        }
      });*/
      /*cordova.getActivity().runOnUiThread(new Runnable() {
        public void run() {
            ...
            callbackContext.success(); // Thread-safe.
        }
      });*/
      return true;
    }
    else if ("scan".equals(action))
    {
      scanAction(args, callbackContext);
    }
    return false;
  }
  
  public void initAction(CallbackContext callbackContext)
  {
    //Get bluetooth manager and bluetooth adapter
    bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
    bluetoothAdapter = bluetoothManager.getAdapter();
    Log.d("Initialized bluetooth managed and adapter.");
    
    //Check if bluetooth adapter is null or disabled and request bluetooth enablement
    //TODO Verify the null behavior
    if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled())
    {
      Log.d("Bluetooth not enabled, trying to enable...");
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
      Log.d("Logging");
      
      bluetoothAdapter = bluetoothManager.getAdapter();
      
      if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled())
      {
        Log.d("Bluetooth still not enabled on retry, canceling...");
        callbackContext.error("Bluetooth still not enabled on retry, canceling...");
      }
      else
      {
        Log.d("Bluetooth enabled on retry");
        callbackContext.success("Bluetooth enabled on retry");
      }
      
    }
    else
    {
      Log.d("Bluetooth enabled");
      callbackContext.success("Bluetooth enabled");
    }
  }
  
  public void scanAction(JSONArray args, CallbackContext callbackContext)
  {
    int scanLimit = args.getInt(0);
  }

  private void scan(final boolean enable)
  {
    if (enable)
    {
      // Stops scanning after a pre-defined scan period.
      scanHandler.postDelayed(new Runnable()
      {
        @Override
        public void run()
        {
            scanning = false;
            bluetoothAdapter.stopLeScan(mLeScanCallback);
        }
      }, SCAN_LIMIT);

      scanning = true;
      bluetoothAdapter.startLeScan(mLeScanCallback);
    }
    else
    {
      scanning = false;
      bluetoothAdapter.stopLeScan(mLeScanCallback);
    }
  }
  
  private LeDeviceListAdapter mLeDeviceListAdapter;
  
  // Device scan callback.
  private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback()
  {
    @Override
    public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord)
    {
      runOnUiThread(new Runnable()
      {
        @Override
        public void run() {
           mLeDeviceListAdapter.addDevice(device);
           mLeDeviceListAdapter.notifyDataSetChanged();
           //Notify phonegap!
        }
      });
    }
  };
}

