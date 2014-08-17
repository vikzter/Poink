package com.coromandel.poink;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;

import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;


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
	private LinearLayout rootLayout;
	private View view;
	
	@Override
	public void handleButtonClick(View v) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.settings_fragment, container, false);
		return view;
		// Inflate the layout for this fragment
		
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		parentActivity = getActivity();
		
		rootLayout = (LinearLayout) parentActivity.findViewById(R.id.settings_root_layout);
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
		
		secs10.setChecked(true);
		
		RadioGroup rg_angle=(RadioGroup) parentActivity.findViewById(R.id.rg_angle);
		rg_angle.setOnCheckedChangeListener(angleChangedListener);
		
		RadioGroup rg_vibratefor=(RadioGroup) parentActivity.findViewById(R.id.rg_vibratefor);
		rg_vibratefor.setOnCheckedChangeListener(vibratetimeChangedListener);
		
		
		if(Globals.getTiltAngle(parentActivity)==0)
		{
			tiltAngle.setVisibility(View.GONE);
			rg_angle.setVisibility(View.GONE);
		}
		
		share.setOnClickListener(shareClickedListener);
		view.setOnTouchListener(new OnSwipeTouchListener(parentActivity) {
		    @Override
		    public void onSwipeBottom() {
		        BaseFragmentActivity bfa = (BaseFragmentActivity) parentActivity;
		        bfa.settingsClicked();
		    }
		    
		    
		    public void onSwipeTop() {
		        
		    }
		    
		    
		    public void onSwipeRight() {
		        
		    }
		    
		    
		    public void onSwipeLeft() {
		        
		    }
		   

		});
	}
	

	
	private OnClickListener shareClickedListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
			sharingIntent.setType("text/plain");
			
			sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out Poink");
			sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, parentActivity.getString(R.string.shareemailtext));
			startActivity(Intent.createChooser(sharingIntent, parentActivity.getString(R.string.shareviatext)));
		}
	};
	
	private OnCheckedChangeListener angleChangedListener =  new OnCheckedChangeListener() 
    {

        public void onCheckedChanged(RadioGroup group, int checkedId) 
           {
            
            if(degree45.isChecked())
              {
            		Globals.setTiltAngle(parentActivity, 45);
                      
              }
            else if(degree75.isChecked())
              {
            	Globals.setTiltAngle(parentActivity, 75);
              }
         }
    };
    
    
	private OnCheckedChangeListener vibratetimeChangedListener =  new OnCheckedChangeListener() 
    {

        public void onCheckedChanged(RadioGroup group, int checkedId) 
           {
            
            if(secs5.isChecked())
              {

                      Globals.setVibrateFor(parentActivity, 5);
              }
            else if(secs10.isChecked())
              {
            	Globals.setVibrateFor(parentActivity, 10);

              }
         }
    };
}
