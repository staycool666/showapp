/**
 * PhoneGap is available under *either* the terms of the modified BSD license *or* the
 * MIT License (2008). See http://opensource.org/licenses/alphabetical for full text.
 *
 * Copyright (c) Matt Kane 2010
 * Copyright (c) 2011, IBM Corporation
 * Copyright (c) 2013, Maciej Nux Jaros
 */
package com.xiyu.plugins;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import android.util.Log;

/**
 * This calls out to the ZXing barcode reader and returns the result.
 *
 * @sa https://github.com/apache/cordova-android/blob/master/framework/src/org/apache/cordova/CordovaPlugin.java
 */
public class BarCodeScanner extends CordovaPlugin {
    public static final int REQUEST_CODE = 0x0ba7c0de;

    private static final String SCAN = "scan";
    private static final String ENCODE = "encode";
    private static final String CANCELLED = "cancelled";
    private static final String FORMAT = "format";
    private static final String TEXT = "text";
    private static final String DATA = "data";
    private static final String TYPE = "type";
    private static final String SCAN_INTENT = "com.google.zxing.client.android.SCAN";
    private static final String ENCODE_DATA = "ENCODE_DATA";
    private static final String ENCODE_TYPE = "ENCODE_TYPE";
    private static final String ENCODE_INTENT = "com.phonegap.plugins.barcodescanner.ENCODE";
    private static final String TEXT_TYPE = "TEXT_TYPE";
    private static final String EMAIL_TYPE = "EMAIL_TYPE";
    private static final String PHONE_TYPE = "PHONE_TYPE";
    private static final String SMS_TYPE = "SMS_TYPE";

    private static final String LOG_TAG = "BarcodeScanner";

    private CallbackContext callbackContext;
    public Activity activity;


    /**
     * Executes the request.
     *
     * This method is called from the WebView thread. To do a non-trivial amount of work, use:
     *     cordova.getThreadPool().execute(runnable);
     *
     * To run on the UI thread, use:
     *     cordova.getActivity().runOnUiThread(runnable);
     *
     * @param action          The action to execute.
     * @param args            The exec() arguments.
     * @param callbackContext The callback context used when calling back into JavaScript.
     * @return                Whether the action was valid.
     *
     * @sa https://github.com/apache/cordova-android/blob/master/framework/src/org/apache/cordova/CordovaPlugin.java
     */
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
        activity = this.cordova.getActivity();


            //检查是否由拍照权限
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i("TEST","Granted");
            //init(barcodeScannerView, getIntent(), null);
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CAMERA}, 1);//1 can be another integer
        }

        if (action.equals(ENCODE)) {
            JSONObject obj = args.optJSONObject(0);
            if (obj != null) {
                String type = obj.optString(TYPE);
                String data = obj.optString(DATA);

                // If the type is null then force the type to text
                if (type == null) {
                    type = TEXT_TYPE;
                }

                if (data == null) {
                    callbackContext.error("User did not specify data to encode");
                    return true;
                }

                encode(type, data);
            } else {
                callbackContext.error("User did not specify data to encode");
                return true;
            }
        } else if (action.equals(SCAN)) {
            scan(args);
        } else {
            return false;
        }
        return true;
    }

    /**
     * Starts an intent to scan and decode a barcode.
     */
    public void scan(JSONArray args) {
        Intent intentScan = new Intent(SCAN_INTENT);
        intentScan.addCategory(Intent.CATEGORY_DEFAULT);

        // add config as intent extras
        if(args.length() > 0) {

            JSONObject obj;
            JSONArray names;
            String key;
            Object value;

            for(int i=0; i<args.length(); i++) {

                try {
                    obj = args.getJSONObject(i);
                } catch(JSONException e) {
                    Log.i("CordovaLog", e.getLocalizedMessage());
                    continue;
                }

                names = obj.names();
                for(int j=0; j<names.length(); j++) {
                    try {
                        key = names.getString(j);
                        value = obj.get(key);

                        if(value instanceof Integer) {
                            intentScan.putExtra(key, (Integer)value);
                        } else if(value instanceof String) {
                            intentScan.putExtra(key, (String)value);
                        }

                    } catch(JSONException e) {
                        Log.i("CordovaLog", e.getLocalizedMessage());
                        continue;
                    }
                }
            }

        }

        // avoid calling other phonegap apps
        intentScan.setPackage(this.cordova.getActivity().getApplicationContext().getPackageName());

        this.cordova.startActivityForResult((CordovaPlugin) this, intentScan, REQUEST_CODE);
    }

    /**
     * Called when the barcode scanner intent completes.
     *
     * @param requestCode The request code originally supplied to startActivityForResult(),
     *                       allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param intent      An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.i("88888", "requestCode="+requestCode);
        Log.i("88888", "resultCode="+resultCode);
        Log.i("88888", "Activity.RESULT_CANCELED="+Activity.RESULT_CANCELED);
        Log.i("88888", "Activity.RESULT_OK="+Activity.RESULT_OK);
        //Log.i("88888", "Activity.SCAN_RESULT="+intent.getStringExtra("SCAN_RESULT"));
        //Log.i("88888", "Activity.SCAN_RESULT_FORMAT="+intent.getStringExtra("SCAN_RESULT_FORMAT"));

        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put(TEXT, intent.getStringExtra("SCAN_RESULT"));
                    obj.put(FORMAT, intent.getStringExtra("SCAN_RESULT_FORMAT"));
                    obj.put(CANCELLED, false);
                } catch (JSONException e) {
                    Log.d(LOG_TAG, "This should never happen");
                }
                //this.success(new PluginResult(PluginResult.Status.OK, obj), this.callback);
                this.callbackContext.success(obj);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put(TEXT, "nothings");
                    obj.put(FORMAT, "123");
                    obj.put(CANCELLED, true);
                } catch (JSONException e) {
                    Log.d(LOG_TAG, "This should never happen");
                }
                //this.success(new PluginResult(PluginResult.Status.OK, obj), this.callback);
                this.callbackContext.success(obj);
            } else {
                //this.error(new PluginResult(PluginResult.Status.ERROR), this.callback);
                this.callbackContext.error("Unexpected error");
            }
        }
    }

    /**
     * Initiates a barcode encode.
     *
     * @param type Endoiding type.
     * @param data The data to encode in the bar code.
     */
    public void encode(String type, String data) {
        Intent intentEncode = new Intent(activity, com.google.zxing.client.android.encode.EncodeActivity.class);
        intentEncode.putExtra(ENCODE_TYPE, type);
        intentEncode.putExtra(ENCODE_DATA, data);
        // avoid calling other phonegap apps
        intentEncode.setPackage(this.cordova.getActivity().getApplicationContext().getPackageName());

      //  this.cordova.getActivity().startActivity(intentEncode);
        this.cordova.startActivityForResult((CordovaPlugin) this, intentEncode, REQUEST_CODE);
    }
}
