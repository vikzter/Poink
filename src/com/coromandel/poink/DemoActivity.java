package com.coromandel.poink;

import org.jraf.android.util.activitylifecyclecallbackscompat.app.LifecycleDispatchFragmentActivity;


import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Menu;

public class DemoActivity extends LifecycleDispatchFragmentActivity {

	DemoFragmentAdapter mAdapter; 
	ViewPager mPager; 
	PageIndicator mIndicator; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_demo);
		
		if(PreferencesHelper.getDemoShownSetting(this))
		{
			Intent intent = new Intent(this, BaseFragmentActivity.class);
			

		    startActivity(intent);
		    overridePendingTransition(0,0);
		    finish();
		}else
		{
		
		 mAdapter = new DemoFragmentAdapter(getSupportFragmentManager()); 
		 mPager = (ViewPager)findViewById(R.id.pager); 
		 mPager.setAdapter(mAdapter); 
		 mIndicator = (CirclePageIndicator)findViewById(R.id.indicator); 
		 mIndicator.setViewPager(mPager); 
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		// TODO Auto-generated method stub
		super.onAttachFragment(fragment);

		

	}
}
