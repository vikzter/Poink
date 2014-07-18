package com.coromandel.poink;



import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;

public class BaseFragmentActivity extends FragmentActivity {

	private IFragmentWithClickEvents currentClickableFragment;

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
            MainFragment firstFragment = new MainFragment();
            
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());
            
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame, firstFragment).commit();
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

		currentClickableFragment = (IFragmentWithClickEvents) fragment;

	}
	
	public void buttonClicked(View view)
    {
    	if (currentClickableFragment!=null)
    	{
    		//currentClickableFragment.handleButtonClick(view);
    		Fragment f = null;
    		
    		if(view.getId()== R.id.button1main)
    		{
    			f= new SettingsFragment();
    			
    			
    		}
    		
    		if(view.getId()== R.id.button1settings)
    		{
    			f= new MainFragment();
    			
    		}
    		
    		// Insert the fragment by replacing any existing fragment
    	    FragmentManager fragmentManager = getSupportFragmentManager();
    	    FragmentTransaction ft = fragmentManager.beginTransaction();
    	    ft.setCustomAnimations(R.anim.slide_in_up, 0,0,R.anim.slide_out_up);
    	    ft.addToBackStack(null);
    	    ft.add(R.id.content_frame, f);
    	    
    	    ft.commit();
    	}
    }

}
