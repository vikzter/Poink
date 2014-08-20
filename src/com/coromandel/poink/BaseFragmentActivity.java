package com.coromandel.poink;



import org.jraf.android.util.activitylifecyclecallbackscompat.app.LifecycleDispatchFragmentActivity;

import com.coromandel.poink.MainFragment.AppStates;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
            //ft.addToBackStack(null);
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
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.action_settings:
	    	if(settingsFragment.isVisible())
	    		return true;
	    	
	    	this.openSettings();
	        return true;
	    case R.id.action_replaydemo:
	    	PreferencesHelper.setDemoShownSetting(this, false);
	    	Toast.makeText(this, getText(R.string.demowillbeplayednexttime), Toast.LENGTH_LONG).show();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
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
			 this.openSettings();
		}
	}
	
	private void openSettings()
	{
		FragmentTransaction ft = fragmentManager.beginTransaction();
	    ft.setCustomAnimations(R.anim.slide_in_up, 0,0,R.anim.slide_out_up);
	    ft.addToBackStack(null);
	    ft.add(R.id.content_frame, settingsFragment);
	    ft.commit();
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
	
	 @Override
	 public void onBackPressed() { 
		 
		 if(settingsFragment.isVisible())
		 {
			 fragmentManager.popBackStack();
			 return;
		 }
		 
	      //TODO: Handle back press on application and in all other starting activities
	    	//return;
		 if(firstFragment!=null && firstFragment.isOn != AppStates.Off & firstFragment.isOn != AppStates.TransitingToOff)
	    	this.checkAndExit();
		 else
			 finish();
		 
	 }
	 
	 private void checkAndExit() {

	        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
	                BaseFragmentActivity.this);

	        alertDialog.setCancelable(false);
	        alertDialog.setPositiveButton(R.string.okgotit, new OnClickListener() {

	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	Intent i = new Intent(Intent.ACTION_MAIN);
	            	i.addCategory(Intent.CATEGORY_HOME);
	            	startActivity(i);
	            }
	        });

	        

	        alertDialog.setMessage(R.string.exitwhenon);
	        alertDialog.show();
	    }

}
