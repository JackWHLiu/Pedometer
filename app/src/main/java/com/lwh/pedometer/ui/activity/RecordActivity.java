package com.lwh.pedometer.ui.activity;

import java.util.List;

import com.alwh.framework.core.annotation.OnClick;
import com.lwh.pedometer.R;
import com.lwh.pedometer.base.BaseActivity;
import com.lwh.pedometer.material.DividerItemDecoration;
import com.lwh.pedometer.material.RecordAdapter;
import com.lwh.pedometer.pojo.Step;
import com.lwh.pedometer.util.DbUtils;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class RecordActivity extends BaseActivity {

	RecyclerView rv;
	
	@Override
	protected void doBusiness() {
		List<Step> list = DbUtils.queryAll(Step.class,"date",true);
		//数据倒序排列
		rv.setAdapter(new RecordAdapter(this,list));
		if (list==null) {
			error(TAG,"adapter null");
		}
		rv.setLayoutManager(new LinearLayoutManager(this));
		rv.setItemAnimator(new DefaultItemAnimator());
		rv.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.HORIZONTAL_LIST));
	}
	
	@OnClick(R.id.record_titlebar_back)
	public void back(View v){
		finish();
	}
	
}
