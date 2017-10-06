package com.route.test.imchatdemo.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment  {
	private static FragmentManager mFragmentManager;
	private static Object mBaseNowFragment;
	Bundle params;
	//Fragment的View加载完毕的标记
	private boolean isViewCreated;
	//Fragment对用户可见的标记
	private boolean isUIVisible;




	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		//在layoutID里面方法fragment的布局
		View view = inflater.inflate(layoutId(), container, false);
		//在该方法内实现view的初始化
		initView(view);
		//在该方法内初始化titlebar，建议titlebar放到activyty中
		updateTitleBar();
		//初始化數據
		initData();

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		//如果该方法走了，说明view创建完成了，把view创建标识改为true
		isViewCreated = true;
		//调用懒加载方法
		lazyLoad();
	}

	/**
	 * 初始化View控件
	 *
	 * @param view
	 */
	protected abstract void initView(View view);



	/**
	 * 更改标题
	 */
	protected abstract void updateTitleBar();

	/**
	 * Fragment加载布局
	 */
	protected abstract int layoutId();
	/**
	 * 初始化數據
	 */
	protected abstract void initData();

	/**
	 * 设置数据
	 * @param bundle
	 */
	public void setParams(Bundle bundle){
		this.params = bundle;
	}

	public Bundle getParams(){
		return params;
	}

	/**
	 * add hide show 旧的Fragemnt在跳转到新的Fragment之前会回调这个方法 旧的Frgament不会调用onStop()方法
	 * 新的Fragment也不会回调这个方法
	 */
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);

		if (hidden)
			onHidden();
		else
			onShow();

	}

	public void onShow() {
		updateTitleBar();
	}

	public void onHidden() {

	}

	@Override
	public void onDestroyView() {
		layoutId();
		//页面销毁,恢复标记
		isViewCreated = false;
		isUIVisible = false;
		super.onDestroyView();
	}

	////////////////////////////


	@Override//如果fragment在ui前台可见，则isVisibleToUser为true，反之false
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		//isVisibleToUser这个boolean值表示:该Fragment的UI 用户是否可见
		if (isVisibleToUser) {
			isUIVisible = true;
			lazyLoad();
		} else {
			isUIVisible = false;
		}
	}

	/**
	 * 如果view创建完成，并且该fragment在ui界面可见，符合这两个条件，我们就加载数据，加载数据只需要实现loadData的抽象方法即可
	 */
	private void lazyLoad() {
		//这里进行双重标记判断,是因为setUserVisibleHint会多次回调,并且会在onCreateView执行前回调,必须确保onCreateView加载完毕且页面可见,才加载数据
		if (isViewCreated && isUIVisible) {
			loadData();
			//数据加载完毕,恢复标记,防止重复加载
			isViewCreated = false;
			isUIVisible = false;


		}
	}


	protected  void loadData(){};


}
