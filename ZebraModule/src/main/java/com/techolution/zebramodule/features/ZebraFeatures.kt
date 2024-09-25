package com.techolution.zebramodule.features

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.techolution.zebramodule.implementation.Event
import com.techolution.zebramodule.implementation.ResponseModel
import com.techolution.zebramodule.implementation.ZebraFeaturesImpl


fun Context.initSdk(callBack: ((Boolean) -> Unit)? = null) {
    //checks emdk manager is ready for use or not
    ZebraFeaturesImpl.checkFeaturesInitialization(this) {
        callBack?.invoke(it)
    }
}
fun enableButtonTrigger(){
    ZebraFeaturesImpl.setEnableButtonTrigger()
}

fun startBarCodeScanner(callBack: ((MutableLiveData<Event<ResponseModel>>) -> Unit)? = null){
    ZebraFeaturesImpl.startScanBarCode(){
        callBack?.invoke(it)
    }
}

fun disableButtonTrigger(){
    ZebraFeaturesImpl.disableButtonTrigger()
}

fun getScanData(): String? {
    return ZebraFeaturesImpl.getScanData()?.data
}

fun onResumeLifeCycle() {
    ZebraFeaturesImpl.onResumeLifeCycle()
}

fun onPauseLifeCycle() {
    ZebraFeaturesImpl.onPauseLifeCycle()
}

fun onDestroyLifeCycle() {
    ZebraFeaturesImpl.onDestroyLifeCycle()
}

fun softScan(){
    ZebraFeaturesImpl.softScan()
}