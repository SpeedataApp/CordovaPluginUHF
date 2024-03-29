package com.speedata.cordova.plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;
import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.UHFManager;
import com.speedata.libuhf.bean.SpdInventoryData;
import com.speedata.libuhf.bean.SpdReadData;
import com.speedata.libuhf.bean.SpdWriteData;
import com.speedata.libuhf.interfaces.OnSpdInventoryListener;
import com.speedata.libuhf.interfaces.OnSpdReadListener;
import com.speedata.libuhf.interfaces.OnSpdWriteListener;
import com.speedata.libutils.ConfigUtils;
import com.speedata.libutils.ReadBean;
import com.uhf.structures.OnInventoryListener;
import com.uhf.structures.OnReadWriteListener;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.os.SystemProperties;
import com.speedata.libuhf.utils.SharedXmlUtil;

public class UHF extends CordovaPlugin {
	private static CallbackContext callbackContext;
	private final String LOG_TAG = "UHF";
	private IUHFService uhfService;
	private Context mContext;
	private boolean isSuccess = false;
	private SharedXmlUtil sharedXmlUtil;
	
	/**
     * 扫头模式
     */
    public static final int MODE_SCAN = 1;
    /**
     * 超高频单次模式
     */
    public static final int MODE_UHF = 2;
    /**
     * 超高频重复模式
     */
    public static final int MODE_UHF_RE = 3;

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		mContext = webView.getContext();
		uhfService = UHFManager.getUHFService(mContext);
		final Gson gson = new Gson();
		
		sharedXmlUtil = SharedXmlUtil.getInstance(mContext, "rfid_float_button");
		
		uhfService.setOnInventoryListener(new OnSpdInventoryListener() {

			@Override
			public void getInventoryData(SpdInventoryData arg0) {
				// TODO Auto-generated method stub
				Log.d(LOG_TAG, "getInventoryData");
				String objectStr = gson.toJson(arg0);
				sendString(objectStr);
			}
			
			@Override
            public void onInventoryStatus(int status) {
                uhfService.inventoryStart();
            }
		});
		uhfService.setOnReadListener(new OnSpdReadListener() {

			@Override
			public void getReadData(SpdReadData arg0) {
				// TODO Auto-generated method stub
				Log.d(LOG_TAG, "getReadData");
				String objectStr = gson.toJson(arg0);
				sendString(objectStr);
			}
		});
		uhfService.setOnWriteListener(new OnSpdWriteListener() {

			@Override
			public void getWriteData(SpdWriteData arg0) {
				// TODO Auto-generated method stub
				int status = arg0.getStatus();
				Log.d(LOG_TAG, "getWriteData" + status);
				if (status == 0) {

					isSuccess = true;
					String objectStr = gson.toJson(arg0);
					sendString(objectStr);
				} else {
					if (!isSuccess) {
						String objectStr = gson.toJson(arg0);
						sendString(objectStr);
					}
				}

			}
		});
	}

	public static CallbackContext getCurrentCallbackContext() {
		return callbackContext;
	}

	private void registerNotifyCallback(CallbackContext callbackContext) {

		PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
		result.setKeepCallback(true);
		callbackContext.sendPluginResult(result);

	}

	private void sendString(String type) {

		PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,
				type);
		pluginResult.setKeepCallback(true);
		CallbackContext pushCallback = getCurrentCallbackContext();
		if (pushCallback != null) {
			pushCallback.sendPluginResult(pluginResult);
		}

	}

	@Override
	public boolean execute(String action, JSONArray args,
			final CallbackContext callbackContextID) {
		try {
			if (action.equals("openDev")) {

				int result = uhfService.openDev();
				Log.d(LOG_TAG, "openDev" + result);
				if (result == 0) {
					callbackContextID.success("success");
				} else {

					callbackContextID.error("power error");
				}
			} else if (action.equals("closeDev")) {
				Log.d(LOG_TAG, "closeDev");
				try {
					uhfService.closeDev();
					callbackContextID.success("success");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					callbackContextID.error(Log.getStackTraceString(e));
				}
			} else if (action.equals("readArea")) {
				try {
					int area = args.getInt(0);
					int addr = args.getInt(1);
					int count = args.getInt(2);
					String passwd = args.getString(3);
					int readResult = uhfService.readArea(area, addr, count,
							passwd);
					Log.d(LOG_TAG, "readArea" + readResult);
					if (readResult == 0) {
						callbackContextID.success("success");
					} else {
						callbackContextID.error("ErrorCode:" + readResult);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					callbackContextID.error(Log.getStackTraceString(e));
				}

			} else if (action.equals("getReadAreaResult")) {
				Log.d(LOG_TAG, "getReadAreaResult");
				callbackContext = callbackContextID;

				cordova.getThreadPool().execute(new Runnable() {
					@Override
					public void run() {
						registerNotifyCallback(callbackContextID);
					}
				});

			} else if (action.equals("writeArea")) {
				isSuccess = false;
				try {
					int area = args.getInt(0);
					int addr = args.getInt(1);
					String passwd = args.getString(2);
					String content = args.getString(3);
					byte[] contentBytes = content.getBytes();
					int result = uhfService.writeArea(area, addr,
							contentBytes.length / 2, passwd, contentBytes);
					Log.d(LOG_TAG, "writeArea" + result);
					if (result == 0) {
						callbackContextID.success("success");
					} else {
						callbackContextID.error("ErrorCode:" + result);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					callbackContextID.error(Log.getStackTraceString(e));
				}
			} else if (action.equals("getWriteAreaResult")) {
				Log.d(LOG_TAG, "getWriteAreaResult");
				callbackContext = callbackContextID;

				cordova.getThreadPool().execute(new Runnable() {
					@Override
					public void run() {
						registerNotifyCallback(callbackContextID);
					}
				});

			} else if (action.equals("setAntennaPower")) {
				try {
					int power = args.getInt(0);
					int result = uhfService.setAntennaPower(power);
					Log.d(LOG_TAG, "setAntennaPower" + result);
					if (result == 0) {
						callbackContextID.success("success");
					} else {
						callbackContextID.error("ErrorCode:" + result);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					callbackContextID.error(Log.getStackTraceString(e));
				}
			} else if (action.equals("getAntennaPower")) {
				int result = uhfService.getAntennaPower();
				Log.d(LOG_TAG, "getAntennaPower" + result);
				if (result != -1) {
					callbackContextID.success(result);
				} else {
					callbackContextID.error("ErrorCode:" + result);
				}
			} else if (action.equals("setFreqRegion")) {
				try {
					int region = args.getInt(0);
					int result = uhfService.setFreqRegion(region);
					Log.d(LOG_TAG, "setFreqRegion" + result);
					if (result == 0) {
						callbackContextID.success("success");
					} else {
						callbackContextID.error("ErrorCode:" + result);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					callbackContextID.error(Log.getStackTraceString(e));
				}
			} else if (action.equals("getFreqRegion")) {
				int result = uhfService.getFreqRegion();
				Log.d(LOG_TAG, "getFreqRegion" + result);
				if (result != -1) {
					callbackContextID.success(result);
				} else {
					callbackContextID.error("ErrorCode:" + result);
				}
			} else if (action.equals("inventoryStart")) {
				Log.d(LOG_TAG, "inventoryStart");
				try {
					uhfService.inventoryStart();
					callbackContextID.success("success");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					callbackContextID.error(Log.getStackTraceString(e));
				}
			} else if (action.equals("inventoryStop")) {
				Log.d(LOG_TAG, "inventoryStop");
				try {
					uhfService.inventoryStop();
					callbackContextID.success("success");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					callbackContextID.error(Log.getStackTraceString(e));
				}
			} else if (action.equals("getInventoryResult")) {
				Log.d(LOG_TAG, "getInventoryResult");
				callbackContext = callbackContextID;

				cordova.getThreadPool().execute(new Runnable() {
					@Override
					public void run() {
						registerNotifyCallback(callbackContextID);
					}
				});

			} else if (action.equals("selectCard")) {
				Log.d(LOG_TAG, "selectCard");
				try {
					boolean isSelect = args.getBoolean(0);
					int result = -1;
					if (isSelect) {
						String content = args.getString(1);
						//byte[] contentBytes = content.getBytes();
						result = uhfService.selectCard(1, content, true);
					} else {
						result = uhfService.selectCard(1, "", false);
					}
					Log.d(LOG_TAG, "selectCard" + result);
					if (result == 0) {
						callbackContextID.success("success");
					} else {
						callbackContextID.error("ErrorCode:" + result);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					callbackContextID.error(Log.getStackTraceString(e));
				}
			} else if (action.equals("setQueryTagGroup")) {
				Log.d(LOG_TAG, "setQueryTagGroup");
				try {
					int selected = args.getInt(0);
					int session = args.getInt(1);
					int target = args.getInt(2);
					int result = uhfService.setQueryTagGroup(selected, session, target);
					Log.d(LOG_TAG, "setQueryTagGroup" + result);
					if (result == 0) {
						callbackContextID.success("success");
					} else {
						callbackContextID.error("ErrorCode:" + result);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					callbackContextID.error(Log.getStackTraceString(e));
				}
			} else if (action.equals("getQueryTagGroup")) {
				int result = uhfService.getQueryTagGroup();
				Log.d(LOG_TAG, "getQueryTagGroup" + result);
				if (result != -1) {
					callbackContextID.success(result);
				} else {
					callbackContextID.error("ErrorCode:" + result);
				}
			} else if (action.equals("setInvMode")) {
				int invm = args.getInt(0);
				int addr = args.getInt(1);
				int length = args.getInt(2);
				int result = uhfService.setInvMode(invm, addr, length);
				Log.d(LOG_TAG, "setInvMode" + result);
				if (result == 0) {
					callbackContextID.success(result);
				} else {
					callbackContextID.error("ErrorCode:" + result);
				}
			} else if (action.equals("getInvMode")) {
				int type = args.getInt(0);
				int result = uhfService.getInvMode(type);
				Log.d(LOG_TAG, "getInvMode" + result);
				if (result != -1) {
					callbackContextID.success(result);
				} else {
					callbackContextID.error("ErrorCode:" + result);
				}
			} else if (action.equals("switchInvMode")) {
				int mode = args.getInt(0);
				int result = uhfService.switchInvMode(mode);
				Log.d(LOG_TAG, "switchInvMode" + result);
				if (result == 0) {
					callbackContextID.success(result);
				} else {
					callbackContextID.error("ErrorCode:" + result);
				}
			} else if (action.equals("getSwitchInvMode")) {
				int result = uhfService.getSwitchInvMode();
				Log.d(LOG_TAG, "getSwitchInvMode" + result);
				if (result != -1) {
					callbackContextID.success(result);
				} else {
					callbackContextID.error("ErrorCode:" + result);
				}
			} else if (action.equals("updateVersion")) {
				String filePath = args.getString(0);
				String fileName = args.getString(1);
				int result = uhfService.updateVersion(filePath, fileName);
				Log.d(LOG_TAG, "updateVersion" + result);
				if (result != -1) {
					callbackContextID.success(result);
				} else {
					callbackContextID.error("ErrorCode:" + result);
				}
			} else if (action.equals("switchUhfScan")) {
				int isUhf = args.getInt(0);
				if(isUhf == 0){
					SystemProperties.set("persist.sys.PistolKey", "scan");
					sharedXmlUtil.write("current_mode", MODE_SCAN);
				}else if(isUhf == 1){
					SystemProperties.set("persist.sys.PistolKey", "uhf");
					 sharedXmlUtil.write("current_mode", MODE_UHF);
				}else if(isUhf == 2){
					SystemProperties.set("persist.sys.PistolKey", "uhf");
					sharedXmlUtil.write("current_mode", MODE_UHF_RE);
				}
				int result = sharedXmlUtil.read("current_mode", MODE_SCAN);
				Log.d(LOG_TAG, "switchUhfScan" + result);
				callbackContextID.success(result);
			} 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			callbackContextID.error(Log.getStackTraceString(e));
		}
		return true;
	}
}
