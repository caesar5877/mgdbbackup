package com.moleq.mgdbbackup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class winActivity extends Activity implements OnClickListener
{
	private EditText et_days = null;
	
	private Button btn_save = null;
	private Button btn_cancel = null;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.windows);
		
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_save = (Button) findViewById(R.id.btn_save);
		et_days = (EditText) findViewById(R.id.et_days);
		
		btn_cancel.setOnClickListener(this);
		btn_save.setOnClickListener(this);
	}
	
	public void onClick(View v)
	{
		if (v == btn_cancel)
		{
			winActivity.this.finish();
		}
		if (v == btn_save)
		{
			String dayStr = et_days.getText().toString().trim();
			if("".equals(dayStr))
			{
				et_days.setError("Invalid Copys!");  
				et_days.requestFocus();  
                return;
			}
			Bundle bundle = new Bundle();
			bundle.putString("days", dayStr);
			Intent mIntent = new Intent();
			mIntent.putExtras(bundle);
			setResult(RESULT_OK, mIntent);
			winActivity.this.finish();
		}
	}
}
