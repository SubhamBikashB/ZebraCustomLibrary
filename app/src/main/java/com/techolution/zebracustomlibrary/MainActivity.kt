package com.techolution.zebracustomlibrary

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.card.MaterialCardView
import com.techolution.zebramodule.features.disableButtonTrigger
import com.techolution.zebramodule.features.getScanData
import com.techolution.zebramodule.features.initSdk
import com.techolution.zebramodule.features.onDestroyLifeCycle
import com.techolution.zebramodule.features.onPauseLifeCycle
import com.techolution.zebramodule.features.onResumeLifeCycle
import com.techolution.zebramodule.features.startBarCodeScanner
import com.techolution.zebramodule.implementation.ZebraFeaturesImpl

class MainActivity : AppCompatActivity() {


    private lateinit var textView: TextView
    private lateinit var switchCompat: SwitchCompat

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.textView)
        switchCompat = findViewById(R.id.switch1)


        applicationContext.initSdk() {
            if (it) {
                textView.text = "Initialization Success"
            } else {
                textView.text = "Initialization failed"
            }
        }


  /*      switchCompat.setOnCheckedChangeListener { _, boolean ->
            if (boolean) {
                switchCompat.text = "scanning On"
                startBarCodeScanner { res2 ->
                    res2.observe(this@MainActivity) { res ->
                        if (res == "scanDone") {
                            Log.d("getDataScan", "onCreate: ${getScanData()}")
                           // Toast.makeText(this, getScanData(), Toast.LENGTH_SHORT).show()
                            textView.text = getScanData()
                            switchCompat.isChecked = false
                        } else {
                            textView.text = res
                        }
                    }
                }
            } else {
                disableButtonTrigger()
                switchCompat.text = "scanning Off"
            }
        }*/

    }

    override fun onResume() {
        super.onResume()
        onResumeLifeCycle()
    }

    override fun onPause() {
        super.onPause()
        onPauseLifeCycle()
    }

    override fun onDestroy() {
        super.onDestroy()
        onDestroyLifeCycle()
    }

}