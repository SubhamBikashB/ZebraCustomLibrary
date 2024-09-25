package com.techolution.zebramodule.implementation

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.symbol.emdk.EMDKManager
import com.symbol.emdk.EMDKResults
import com.symbol.emdk.barcode.BarcodeManager
import com.symbol.emdk.barcode.ScanDataCollection
import com.symbol.emdk.barcode.Scanner
import com.symbol.emdk.barcode.ScannerException
import com.symbol.emdk.barcode.ScannerInfo
import com.symbol.emdk.barcode.ScannerResults
import com.symbol.emdk.barcode.StatusData


object ZebraFeaturesImpl : EMDKManager.EMDKListener, Scanner.DataListener, Scanner.StatusListener, BarcodeManager.ScannerConnectionListener {


    private var recieverMessage:MutableLiveData<Event<ResponseModel>> = MutableLiveData()

    private var deviceList = arrayListOf<ScannerInfo>()
    private var defaultScannerIndex = 0 // Keep the selected scanner
    private var updatedScannerIndex = 0 // After changing the scanner Index
    private var scanData:ScanDataCollection.ScanData ? = null

    private var triggerEnabled = false
    private var bSoftTriggerSelected = false
    private var bExtScannerDisconnected = false

    //variables
    private var emdkManager: EMDKManager? = null
    private var barcodeManager: BarcodeManager? = null
    private var scanner: Scanner? = null

    fun checkFeaturesInitialization(applicationContext:Context,callBack:((Boolean)->Unit)?=null) {
        try {//checks emdk manager is ready for use or not
            val results = EMDKManager.getEMDKManager(applicationContext, this)
            if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
                callBack?.invoke(false)
            } else {
                callBack?.invoke(true)
            }
        } catch (e: Exception) {
            callBack?.invoke(false)
           e.printStackTrace()
        }
    }

    fun getScanData(): ScanDataCollection.ScanData? {
       return scanData
   }

    fun startScanBarCode(callBack: ((MutableLiveData<Event<ResponseModel>>) -> Unit)?=null){
        recieverMessage.postValue(null)
        callBack?.invoke(recieverMessage)
    }

     fun setEnableButtonTrigger(){
         if (scanner!=null){
             if (!scanner?.isReadPending!! && !bExtScannerDisconnected) {
                 try {
                     cancelRead()
                     triggerEnabled = true
                     scanner?.read()
                 } catch (e: ScannerException) {
                     e.printStackTrace()
                 }
             }
         }
     } 

     fun disableButtonTrigger(){
        try {
            scanner?.cancelRead()
            triggerEnabled = false
        } catch (e: ScannerException) {
            e.printStackTrace()
        }
    }

    fun onResumeLifeCycle(){
        deInitScanner()
        deInitBarcodeManager()
        // The application is in foreground
        if (emdkManager != null) {
            // Acquire the barcode manager resources
            initBarcodeManager()
            // Enumerate scanner devices
            getScannerDevices()
            // Initialize scanner
            initScanner()
        }
    }

    fun onPauseLifeCycle(){
        // The application is in background
        // Release the barcode manager resources
        deInitScanner()
        deInitBarcodeManager()

    /*    emdkManager.release()
        scanner.release()
        barcodeManager?.addConnectionListener()*/
    }

    fun onDestroyLifeCycle(){
        // Release all the resources
        if (emdkManager != null) {
            emdkManager?.release()
            emdkManager = null
        }
    }

    override fun onOpened(p0: EMDKManager?) {
        this.emdkManager = p0

        // Acquire the barcode manager resources
        initBarcodeManager()
        // Enumerate scanner devices
        getScannerDevices()
        // init scanner
        initScanner()

    }

    override fun onClosed() {
        if (emdkManager != null) {
            emdkManager?.release()
            emdkManager = null
        }
    }

    override fun onData(p0: ScanDataCollection?) {
        bSoftTriggerSelected  = false
        if (p0 != null && p0.result == ScannerResults.SUCCESS) {
            scanData = null
            scanData = p0.scanData.last()
            responseMaker(200,"ScanDone", scanData?.data.toString())
        }
    }

    override fun onStatus(p0: StatusData?) {
        when (p0?.state) {
            StatusData.ScannerStates.IDLE -> {
                // set trigger type
                if (bSoftTriggerSelected) {
                    scanner?.triggerType = Scanner.TriggerType.SOFT_ONCE
                    bSoftTriggerSelected = false
                }else{
                    scanner?.triggerType = Scanner.TriggerType.HARD
                }
                // submit read
                if (!scanner?.isReadPending!! && !bExtScannerDisconnected) {
                    try {
                        if (triggerEnabled){
                            scanner?.read()
                        }else{
                            scanner?.cancelRead()
                        }
                    } catch (e: Exception) {
                        responseMaker(300,e.message.toString(),"")
                    }
                }
            }

            StatusData.ScannerStates.WAITING -> {
                responseMaker(300,"Scanner is waiting for trigger press...","")
            }

            StatusData.ScannerStates.SCANNING -> {
                responseMaker(300,"Scanning...","")
            }

            StatusData.ScannerStates.DISABLED -> {
                responseMaker(400,p0.friendlyName + " is disabled.","")
            }

            StatusData.ScannerStates.ERROR -> {
                responseMaker(400,"An error has occurred.","")
            }
            else -> {}
        }
    }

    override fun onConnectionChange(scannerInfo: ScannerInfo?, connectionState: BarcodeManager.ConnectionState?) {
        var scannerName = ""
        val scannerNameExtScanner: String? = scannerInfo?.friendlyName
        if (deviceList.isNotEmpty()) {
            scannerName = deviceList[updatedScannerIndex].friendlyName
        }

        if (scannerName.equals(scannerNameExtScanner, ignoreCase = true)) {
            when (connectionState) {
                BarcodeManager.ConnectionState.CONNECTED -> {
                    bSoftTriggerSelected = false
                    synchronized(this) {
                        initScanner()
                        bExtScannerDisconnected = false
                    }
                }
                BarcodeManager.ConnectionState.DISCONNECTED -> {
                    bExtScannerDisconnected = true
                    synchronized(this) { deInitScanner() }
                }
                else -> {}
            }
        } else {
            bExtScannerDisconnected = false
        }
    }

    private fun initBarcodeManager() {
        barcodeManager = emdkManager?.getInstance(EMDKManager.FEATURE_TYPE.BARCODE) as BarcodeManager
        // Add connection listener
        if (barcodeManager != null) {
            barcodeManager?.addConnectionListener(this)
        }
    }

    private fun deInitBarcodeManager() {
        if (emdkManager != null) {
            emdkManager?.release(EMDKManager.FEATURE_TYPE.BARCODE)
        }
    }

    private fun getScannerDevices() {
        if (barcodeManager != null) {
            deviceList.clear()
            barcodeManager?.supportedDevicesInfo?.let { deviceList.addAll(it) }
            if (deviceList.isNotEmpty()) {
                for (i in deviceList) {
                    if (i.isDefaultScanner) {
                        defaultScannerIndex = deviceList.indexOf(i)
                        updatedScannerIndex = defaultScannerIndex
                    }
                }
            }
        }
    }

    private fun initScanner() {
        if (scanner == null) {
            if (deviceList.isNotEmpty()) {
                if (barcodeManager != null) {
                    scanner = barcodeManager?.getDevice(deviceList[updatedScannerIndex])
                }
            } else {
                return
            }
            if (scanner != null) {
                scanner?.addDataListener(this)
                scanner?.addStatusListener(this)
                try {
                    scanner?.enable()
                } catch (e: ScannerException) {
                    deInitScanner()
                }
            }
        }
    }

    private fun deInitScanner() {
        if (scanner != null) {
            try {
                scanner?.disable()
            } catch (e: Exception) {
               e.printStackTrace()
            }
            try {
                scanner?.removeDataListener(this)
                scanner?.removeStatusListener(this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                scanner?.release()
            } catch (e: Exception) {
               e.printStackTrace()
            }
            scanner = null
        }
    }

    private fun cancelRead() {
        if (scanner != null) {
            if (scanner?.isReadPending == true) {
                try {
                    scanner?.cancelRead()
                } catch (e: ScannerException) {
                  e.printStackTrace()
                }
            }
        }
    }
    private fun responseMaker(status:Int,message:String,data:String){
    recieverMessage.postValue(Event(ResponseModel(status, message, data)))
    }
    fun softScan(){
        bSoftTriggerSelected = true
        cancelRead()
    }
}