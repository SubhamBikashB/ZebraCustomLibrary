package com.techolution.zebracustomlibrary;

import static com.techolution.zebramodule.features.ZebraFeaturesKt.disableButtonTrigger;
import static com.techolution.zebramodule.features.ZebraFeaturesKt.enableButtonTrigger;
import static com.techolution.zebramodule.features.ZebraFeaturesKt.getScanData;
import static com.techolution.zebramodule.features.ZebraFeaturesKt.initSdk;
import static com.techolution.zebramodule.features.ZebraFeaturesKt.onDestroyLifeCycle;
import static com.techolution.zebramodule.features.ZebraFeaturesKt.onPauseLifeCycle;
import static com.techolution.zebramodule.features.ZebraFeaturesKt.onResumeLifeCycle;
import static com.techolution.zebramodule.features.ZebraFeaturesKt.softScan;
import static com.techolution.zebramodule.features.ZebraFeaturesKt.startBarCodeScanner;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.techolution.zebramodule.implementation.Event;
import com.techolution.zebramodule.implementation.ResponseModel;

public class ScanningHelper {

    public boolean initializeSdk(Context applicationContext) {
        final Boolean[] data = {false};
        initSdk(applicationContext, aBoolean -> {
            data[0] = aBoolean;
            return null;
        });
        return data[0];
    }
    public interface Callback {
        void invoke(MutableLiveData<Event<ResponseModel>> data);
    }
    public void startScanner( Callback callBack ){
        startBarCodeScanner(responseModel -> {
            if (callBack != null){
                callBack.invoke(responseModel);
            }
            return null;
        });
    }

    public void disableButtonTriggerScanning(){
        disableButtonTrigger();
    }
    public void enableButtonTriggerScanning(){
       enableButtonTrigger();
    }

    public String getScanningData() {
        return getScanData();
    }

    public void onResumeLifeCycleOperation() {
        onResumeLifeCycle();
    }

    public void onPauseLifeCycleOperation() {
        onPauseLifeCycle();
    }

    public void onDestroyLifeCycleOperation() {
        onDestroyLifeCycle();
    }

    public void softScanning(){
        softScan();
    }

}
