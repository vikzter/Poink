package com.coromandel.poink;



import org.jraf.android.util.activitylifecyclecallbackscompat.app.LifecycleDispatchFragmentActivity;


import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;

public class BaseFragmentActivity extends LifecycleDispatchFragmentActivity {

	//private IFragmentWithClickEvents currentClickableFragment;
	MainFragment firstFragment;
	SettingsFragment settingsFragment;
	FragmentManager fragmentManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_basefragment);
		
		// Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.content_frame) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            firstFragment = new MainFragment();
            settingsFragment = new SettingsFragment();
            
            fragmentManager = getSupportFragmentManager();
            		
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());
            
            
            // Add the fragment to the 'fragment_container' FrameLayout
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.addToBackStack(null);
            ft.add(R.id.content_frame, firstFragment).commit();
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
	
	protected void settingsClicked()
	{
		if(settingsFragment.isVisible())
		{
			fragmentManager.popBackStack();
		}else
		{
			 FragmentTransaction ft = fragmentManager.beginTransaction();
	    	    ft.setCustomAnimations(R.anim.slide_in_up, 0,0,R.anim.slide_out_up);
	    	    ft.addToBackStack(null);
	    	    ft.add(R.id.content_frame, settingsFragment);
	    	   ft.commit();
		}
	}
	
	
	public void buttonClicked(View view)
    {
		switch(view.getId())
		{
			case R.id.imgv_settings:
				this.settingsClicked();
			break;
			
			default:
				if(settingsFragment.isVisible())
				{
					settingsFragment.handleButtonClick(view);
				}else 
				{
					firstFragment.handleButtonClick(view);
				}
		}
    }
	
	public void layoutClicked(View v)
	{
		if(settingsFragment.isVisible())
		{
			fragmentManager.popBackStack();
		}
		
	}

}
