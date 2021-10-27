package com.randdusing.bluetoothle;

import android.app.DownloadManager;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.json.JSONObject;

import java.io.File;

import no.nordicsemi.android.dfu.DfuServiceController;
import no.nordicsemi.android.dfu.DfuServiceInitiator;

public class FirmwareUpgradeDownloadedReceiver extends BroadcastReceiver {

    static BluetoothDevice device;
    static CordovaInterface cordova;
    static CallbackContext callbackContext;
    static JSONObject returnObj;
    static DownloadManager downloadManager;
    static long fileDownloadRef;

    public FirmwareUpgradeDownloadedReceiver() {
    }

    public void onReceive(Context ctx, Intent intent) {
        String action = intent.getAction();
        Log.d("BLE", "ON RECEIVE " + action);
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            long downloadedFileRef = intent.getLongExtra("extra_download_id", 0L);
            if (downloadedFileRef > 0 && device != null && cordova != null && callbackContext != null && returnObj != null && downloadManager != null && fileDownloadRef > 0 && fileDownloadRef == downloadedFileRef) {

                Bundle bundle = intent.getExtras();
                DownloadManager.Query q = new DownloadManager.Query();
                q.setFilterById(bundle.getLong(DownloadManager.EXTRA_DOWNLOAD_ID));
                Cursor c = downloadManager.query(q);
                String filePath = "";
                if (c.moveToFirst()) {
                    int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        filePath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    }
                }
                c.close();
                if(filePath!="") {
                    filePath = filePath.replaceAll("file://","");
                    final DfuServiceInitiator starter = new DfuServiceInitiator(device.getAddress())
                            .setDeviceName(device.getName())
                            .setKeepBond(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        DfuServiceInitiator.createDfuNotificationChannel(cordova.getActivity());
                    }
                    starter.setPrepareDataObjectDelay(300L);
                    File file = new File(filePath);
                    if (file.exists()) {
                        Log.d("BLE", "FILE EXISTS " + filePath);
                        Uri firmwareUri = Uri.fromFile(file);
                        starter.setZip(firmwareUri);
                        // final DfuServiceController controller = starter.start(cordova.getActivity(), DfuService.class);
                        starter.start(cordova.getActivity(), DfuService.class);
                        Log.d("BLE", "UPGRADE STARTED");
                        if (callbackContext != null && returnObj != null) {
                            callbackContext.success(returnObj);
                        }
                        return;
                    }
                }
                Log.d("BLE", "FILE NOT FOUND " + filePath);
                if (callbackContext != null && returnObj != null) {
                    callbackContext.error("Firmware download failed");
                }
            }
        }
    }
}