package com.route.test.imchatdemo.base;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


/**
 * baseFragment里面来做栈管理
 */
public class FragmentBuilder {
	int containerViewId;
	private FragmentManager mFragmentManager;
	private static FragmentBuilder mFragmentBuilder;
	private FragmentTransaction mFragmentTransaction;
	private BaseFragment mBaseNowFragment;

	private FragmentBuilder() {
		init();
	}

	public synchronized static FragmentBuilder getFragmentBuilder() {
		if (mFragmentBuilder == null) {
			mFragmentBuilder = new FragmentBuilder();
		}
		return mFragmentBuilder;
	}
	public FragmentBuilder containerId(int containerViewId){
		this.containerViewId = containerViewId;
		return this;
	}


	// 初始化FragmentManager
	public void init() {
		mFragmentManager = MyApp.mBaseActivity.getSupportFragmentManager();
	}

	// 打开一个Fragment(隐藏上一个Fragment添加并显示当前Fragment，并将当前Fragment添加到回退栈中)
	public FragmentBuilder satrt(Class<? extends BaseFragment> fragmentClass) {
		mFragmentTransaction = mFragmentManager.beginTransaction();
		mBaseNowFragment = (BaseFragment) mFragmentManager
				.findFragmentByTag(fragmentClass.getSimpleName());
		try {
			if (mBaseNowFragment == null) {
				mBaseNowFragment = fragmentClass.newInstance();
				mFragmentTransaction.add(containerViewId,
						mBaseNowFragment, fragmentClass.getSimpleName());
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		// 隐藏上一个Fragment
		if (MyApp.mBaseLastFragment != null) {
			mFragmentTransaction.hide(MyApp.mBaseLastFragment);
		}
		// 显示现在的Frgament
		mFragmentTransaction.show(mBaseNowFragment);
		// 并将现在的Frgament添加到回退栈中
		mFragmentTransaction.addToBackStack(fragmentClass.getSimpleName());
		return this;
	}

	// 将现在的BaseFragment赋值给上一个Fragment 并返回现在的BaseFragment
	public BaseFragment build() {
		MyApp.mBaseLastFragment = mBaseNowFragment;
		mFragmentTransaction.commit();
		return mBaseNowFragment;
	}

	/**
	 * 获取Bundle值
	 *
	 * @param bundle
	 * @return
	 */
	public FragmentBuilder getBundle(Bundle bundle) {
		if (bundle != null) {
			mBaseNowFragment.setParams(bundle);
		}
		return this;
	}



}
