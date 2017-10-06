package com.route.test.imchatdemo.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.route.test.imchatdemo.R;
import com.route.test.imchatdemo.activity.LoginActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetFragment extends Fragment implements View.OnClickListener {


    private Button bt_tuichu;

    public SetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set, null);
        bt_tuichu = view.findViewById(R.id.bt_tuichu);
        bt_tuichu.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        EMClient.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub

            }
        });

    }
}
