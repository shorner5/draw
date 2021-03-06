package com.stuhorner.drawingsample;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.transition.Explode;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * Created by Stu on 4/7/2016.
 */
public class MatchOverlayFragment extends DialogFragment {
    public MatchOverlayFragment() {}

    static public MatchOverlayFragment newInstance(String name, String UID) {
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("UID", UID);
        MatchOverlayFragment fragment = new MatchOverlayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.layout_match_overlay, new LinearLayout(getActivity()), false);
        Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.setContentView(view);

        Button name = (Button) view.findViewById(R.id.match_name);
        Button viewProfile = (Button) view.findViewById(R.id.match_view_profile);
        name.setText(String.format(getResources().getString(R.string.send_message), getArguments().getString("name")));
        viewProfile.setText(String.format(getResources().getString(R.string.match_view_profile), getArguments().getString("name")));

        Animation anim = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_in);
        Animation anim2 = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_in);
        anim2.setInterpolator(new OvershootInterpolator());
        anim2.setDuration(400);
        anim2.setStartOffset(200);
        anim.setInterpolator(new OvershootInterpolator());
        anim.setDuration(400);
        name.startAnimation(anim);
        viewProfile.startAnimation(anim2);

        addAnimation(name);
        addAnimation(viewProfile);

        return builder;
    }

    private void addAnimation(final Button button) {
        final Animation scaleDown = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.scale_down_small);
        scaleDown.setFillAfter(true);
        final Animation scaleUp = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.scale_up_small);
        scaleUp.setFillAfter(true);

        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        button.startAnimation(scaleDown);
                        break;
                    case MotionEvent.ACTION_UP:
                        handleButtonPress(button);
                        break;
                }
                return false;
            }
        });
    }

    private void handleButtonPress(Button button) {
        switch (button.getId()) {
            case R.id.match_name:
                Intent intent = new Intent(getActivity(), ChatPage.class);
                intent.putExtra("UID", getArguments().getString("UID"));
                intent.putExtra("name", getArguments().getString("name"));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.fade_out);
                break;
            case R.id.match_view_profile:
                Intent intent2 = new Intent(getActivity(), ProfileActivity.class);
                intent2.putExtra("UID", getArguments().getString("UID"));
                intent2.putExtra("buttons_off", true);
                startActivity(intent2);
                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.fade_out);
                break;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 200);
    }
}
