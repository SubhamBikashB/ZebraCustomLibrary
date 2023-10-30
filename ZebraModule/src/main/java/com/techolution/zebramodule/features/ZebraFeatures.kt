package com.techolution.zebramodule.features

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.symbol.emdk.barcode.ScanDataCollection
import com.techolution.zebramodule.implementation.ZebraFeaturesImpl


fun Context.initSdk(callBack: ((Boolean) -> Unit)? = null) {
    //checks emdk manager is ready for use or not
    ZebraFeaturesImpl.checkFeaturesInitialization(this) {
        callBack?.invoke(it)
    }
}

fun startBarCodeScanner(callBack: ((MutableLiveData<String>) -> Unit)? = null) {
    ZebraFeaturesImpl.startScanBarCode {
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
