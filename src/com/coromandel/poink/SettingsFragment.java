package com.coromandel.poink;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsFragment extends Fragment implements IFragmentWithClickEvents{

	@Override
	public void handleButtonClick(View v) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.settings_fragment, container, false);
	}
}
