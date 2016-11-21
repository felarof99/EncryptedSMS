package com.shadowfax.apps.securesms;

import java.math.BigInteger;

import com.shadowfax.apps.securesms.commonmodules.PreferenceOperations;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends Activity{
	private static TextView settings_public_key_size;
	private static Button settings_inc_public_key_size,settings_dec_public_key_size;
	
	int publicKeySize;
	
	private void init()
	{
		
		settings_public_key_size=(TextView)findViewById(R.id.settings_public_key_size);
		settings_inc_public_key_size=(Button)findViewById(R.id.settings_inc_public_key_size);
		settings_dec_public_key_size=(Button)findViewById(R.id.settings_dec_public_key_size);
	}
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
        init();
        final PreferenceOperations secure_sms_pref=new PreferenceOperations(Settings.this);
        try
        {
        	publicKeySize=secure_sms_pref.getPublicKeySizePref();
        }
        catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}

        //Toast.makeText(getApplicationContext(), String.valueOf(publicKeySize), Toast.LENGTH_SHORT).show();
        settings_public_key_size.setText(String.valueOf(publicKeySize));
        
        settings_inc_public_key_size.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				publicKeySize=2*publicKeySize;
				if(publicKeySize<=1024)
				{
					settings_public_key_size.setText(String.valueOf(publicKeySize));
					secure_sms_pref.storePublicKeySizePref(publicKeySize);
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Max Size Reached!", Toast.LENGTH_SHORT).show();
					publicKeySize=publicKeySize/2;
				}
				
			}
		});
        settings_dec_public_key_size.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				publicKeySize=publicKeySize/2;
				if(publicKeySize>=16)
				{
					settings_public_key_size.setText(String.valueOf(publicKeySize));
					secure_sms_pref.storePublicKeySizePref(publicKeySize);
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Min Size Reached!", Toast.LENGTH_SHORT).show();
					publicKeySize=publicKeySize*2;
				}
				
			}
		});
        
        
    }
}
