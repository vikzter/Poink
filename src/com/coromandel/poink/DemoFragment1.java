package com.coromandel.poink;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DemoFragment1 extends Fragment{
	
	private TextView demopage1content;
	private Activity parentActivity;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.demo1_fragment, container, false);
		// Inflate the layout for this fragment
		
		
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		

		parentActivity = getActivity();
		demopage1content = (TextView) parentActivity.findViewById(R.id.tv_demopage1content);
		
		Typeface tf = Typeface.createFromAsset(parentActivity.getAssets(),"fonts/AlteHaasGroteskRegular.ttf");
		demopage1content.setTypeface(tf, Typeface.NORMAL);
		
	}

}
