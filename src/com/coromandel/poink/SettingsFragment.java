package com.coromandel.poink;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RadioButton;


import android.widget.TextView;




public class SettingsFragment extends BaseAnimationFragment implements IFragmentWithClickEvents{

	private Activity parentActivity;
	
	
	private TextView share;
	private TextView tiltAngle;
	private TextView vibrateFor;
	private RadioButton degree45;
	private RadioButton degree75;
	private RadioButton secs5;
	private RadioButton secs10;
	
	
	@Override
	public void handleButtonClick(View v) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.settings_fragment, container, false);
		// Inflate the layout for this fragment
		
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		parentActivity = getActivity();
		
		share = (TextView) parentActivity.findViewById(R.id.tv_sharecircle);
		tiltAngle = (TextView) parentActivity.findViewById(R.id.tv_angletostartalerts);
		vibrateFor=(TextView)parentActivity.findViewById(R.id.tv_vibratefor);
		degree45=(RadioButton)parentActivity.findViewById(R.id.rb_angle45);
		degree75=(RadioButton)parentActivity.findViewById(R.id.rb_angle75);
		secs5=(RadioButton)parentActivity.findViewById(R.id.rb_vibrate5);
		secs10=(RadioButton)parentActivity.findViewById(R.id.rb_vibrate10);
		
		Typeface tf = Typeface.createFromAsset(parentActivity.getAssets(),"fonts/AlteHaasGroteskRegular.ttf");
		
		share.setTypeface(tf,Typeface.NORMAL);
		tiltAngle.setTypeface(tf,Typeface.NORMAL);
		vibrateFor.setTypeface(tf,Typeface.NORMAL);
		degree45.setTypeface(tf,Typeface.NORMAL);
		degree75.setTypeface(tf,Typeface.NORMAL);
		secs5.setTypeface(tf,Typeface.NORMAL);
		secs10.setTypeface(tf,Typeface.NORMAL);
		
		
		
	}
	
}
