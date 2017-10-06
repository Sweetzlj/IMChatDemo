package com.route.test.imchatdemo.fragment;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.route.test.imchatdemo.R;
import com.route.test.imchatdemo.activity.AddContactActivity;
import com.route.test.imchatdemo.activity.ChatActivity;
import com.route.test.imchatdemo.activity.NewFriendsMsgActivity;
import com.route.test.imchatdemo.adapter.FriendsAdapter;
import com.route.test.imchatdemo.base.BaseFragment;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends BaseFragment implements View.OnClickListener {

    private List<String> usernames;
    private RecyclerView recycler;
    private ImageView em_add;
    private TextView tv_shenqing;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    friendsAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    private FriendsAdapter friendsAdapter;

    @Override
    protected void initView(View view) {
        recycler = view.findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(layoutManager);

        em_add = view.findViewById(R.id.em_add);
        em_add.setOnClickListener(this);
        tv_shenqing = view.findViewById(R.id.tv_shenqing);
        tv_shenqing.setOnClickListener(this);
    }

    @Override
    protected void updateTitleBar() {

    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_friends;
    }

    @Override
    protected void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            friendsAdapter = new FriendsAdapter(getContext(), usernames);
                            recycler.setAdapter(friendsAdapter);
                            handler.sendEmptyMessage(1);

                            friendsAdapter.setOnRecyclerClick(new FriendsAdapter.OnRecyclerClick() {
                                @Override
                                public void onClick(int position) {
                                    startActivity(new Intent(getActivity(), ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, usernames.get(position)));
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_shenqing:
                Intent intent1  = new Intent(getActivity(), NewFriendsMsgActivity.class);
                startActivity(intent1);
                break;
            case R.id.em_add:
                Intent intent  = new Intent(getActivity(), AddContactActivity.class);
                startActivity(intent);
                break;
        }
    }
}
