package com.coromandel.poink;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class DemoFragmentAdapter extends FragmentPagerAdapter{

	 /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 3;

	
	public DemoFragmentAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int position) {
			
		Fragment f = null;
		switch(position)
		{
		case 0:
			f=new DemoFragment1();
			break;
		case 1:
			f=new DemoFragment2();
			break;
		case 2:
			f=new DemoFragment3();
			break;
		}
		
		return f;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return NUM_PAGES;
	}

}
