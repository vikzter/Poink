package com.coromandel.poink;


import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class BaseAnimationFragment extends android.support.v4.app.Fragment {

    

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {

        Animation anim = AnimationUtils.loadAnimation(getActivity(), nextAnim);

        anim.setAnimationListener(new AnimationListener() {

            public void onAnimationStart(Animation animation) {
               
                
            }

            public void onAnimationRepeat(Animation animation) {
               
               
            }

            public void onAnimationEnd(Animation animation) {
            	correctViewActualPosition();
               
            }
        });
        

        return anim;
    }
    private void correctViewActualPosition()
    {
    	View parent = (View) this.getView().getParent();
		
		int height = parent.getHeight();
		int moveBy = height * 20 / 100;

		int mainheight = this.getView().getHeight();
		int mainwidth = this.getView().getWidth();
		int maintop = this.getView().getTop();
		
		
		LayoutParams params = new LayoutParams(mainwidth, mainheight);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL,-1);
		params.setMargins(20, maintop+moveBy, 20, 0);
		this.getView().setLayoutParams(params);
    }
}