package com.techolution.zebracustomlibrary;

import static com.techolution.zebramodule.features.ZebraFeaturesKt.getScanData;
import static com.techolution.zebramodule.features.ZebraFeaturesKt.onDestroyLifeCycle;
import static com.techolution.zebramodule.features.ZebraFeaturesKt.onPauseLifeCycle;
import static com.techolution.zebramodule.features.ZebraFeaturesKt.onResumeLifeCycle;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.techolution.zebramodule.implementation.Event;
import com.techolution.zebramodule.implementation.ResponseModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class MainActivity2 extends AppCompatActivity {

    SwitchCompat switchCompat;
    TextView textView;

    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        textView = findViewById(R.id.textView);
        switchCompat = findViewById(R.id.switch1);
        button = findViewById(R.id.button);
        ScanningHelper implementation = new ScanningHelper();

        implementation.initializeSdk(getApplicationContext());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                implementation.softScanning();
            }
        });

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    implementation.enableButtonTriggerScanning();
                    switchCompat.setText("scanning On");
                    implementation.startScanner(new ScanningHelper.Callback() {
                        @Override
                        public void invoke(MutableLiveData<Event<ResponseModel>> data) {
                            data.observe(MainActivity2.this, new Observer<Event<ResponseModel>>() {
                                @Override
                                public void onChanged(Event<ResponseModel> responseModelEvent) {
                                    if (responseModelEvent!=null){
                                        ResponseModel responseEvent = responseModelEvent.getContentIfNotHandled();
                                        if (responseEvent!=null){
                                            if (Objects.equals(responseEvent.getStatus(), 200)) {
                                                JSONObject jsoObject = prepareJsoObject("200", "Scanning successful", implementation.getScanningData());
                                                Log.d("MainActivityScanner", "onCreate: " + jsoObject);
                                                textView.setText(getScanData());
                                            } else {
                                                JSONObject jsoObject = prepareJsoObject("300",responseEvent.getMessage(), "");
                                                Log.d("MainActivityScanner", "onCreate: " + jsoObject);
                                                textView.setText(responseEvent.getMessage());
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    });
                }else {
                    implementation.disableButtonTriggerScanning();
                    switchCompat.setText("scanning Off");
                }
            }
        });

    }

    public JSONObject prepareJsoObject(String statusCode, String message, String data) {
        JSONObject jsObject = new JSONObject();
        try {
            jsObject.put("statusCode", statusCode);
            jsObject.put("message", message);
            jsObject.put("data", data);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return jsObject;
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPauseLifeCycle();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyLifeCycle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onResumeLifeCycle();
    }
}