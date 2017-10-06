package com.route.test.imchatdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.route.test.imchatdemo.R;
import com.route.test.imchatdemo.view.InviteMessage;
import com.route.test.imchatdemo.view.InviteMessgeDao;
import com.route.test.imchatdemo.view.NewFriendsMsgAdapter;

import java.util.List;


/**
 * 申请与通知
 *
 */
public class NewFriendsMsgActivity extends Activity {
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_friends_msg);

		listView = (ListView) findViewById(R.id.list);
		InviteMessgeDao dao = new InviteMessgeDao(this);
		List<InviteMessage> msgs = dao.getMessagesList();
		NewFriendsMsgAdapter adapter = new NewFriendsMsgAdapter(this, 1, msgs);
		listView.setAdapter(adapter);
		dao.saveUnreadMessageCount(0);

	}

	public void back(View view) {
		finish();
	}

}
