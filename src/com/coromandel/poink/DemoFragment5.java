package com.coromandel.poink;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class DemoFragment5 extends Fragment{
	private TextView demopage5content;
	private TextView gotit;
	private Activity parentActivity;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.demo5_fragment, container, false);
		// Inflate the layout for this fragment
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		

		parentActivity = getActivity();
		demopage5content = (TextView) parentActivity.findViewById(R.id.tv_demopage5content);
		gotit = (TextView) parentActivity.findViewById(R.id.tv_gotit);
		
		Typeface tf = Typeface.createFromAsset(parentActivity.getAssets(),"fonts/AlteHaasGroteskRegular.ttf");
		demopage5content.setTypeface(tf, Typeface.NORMAL);
		gotit.setTypeface(tf,Typeface.NORMAL);
		gotit.setOnClickListener(gotitClickedListener);
	}
	
	private OnClickListener gotitClickedListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			PreferencesHelper.setDemoShownSetting(parentActivity, true);
			
			Intent intent = new Intent(parentActivity, BaseFragmentActivity.class);
			/*intent.putExtra(Constants.EXTRA_GCMREGISTRATIONID, gcmRegistrationId);
			intent.putExtra(Constants.EXTRA_EXISTINGUSERORDEVICE, s);*/
			startActivity(intent);
			parentActivity.overridePendingTransition(0, 0);
			
			parentActivity.finish();
		}
	};
}
