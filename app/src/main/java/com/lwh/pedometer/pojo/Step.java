package com.lwh.pedometer.pojo;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Default;
import com.litesuits.orm.db.annotation.NotNull;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 计步的数据
 */
@Table("step")
public class Step extends BaseBmobObject implements Parcelable{

	private static final long serialVersionUID = 8489569461561644831L;
	public static final String KEY_ID = "_id";
	public static final String KEY_DATE = "date";
	public static final String KEY_STEP = "step";
	
	@PrimaryKey(AssignType.AUTO_INCREMENT)
	private int _id;
	@NotNull
	@Column("date")
	private String date;
	@NotNull
	@Column("step")
	@Default("0")
	private String step;

	public int getId() {
		return _id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	static Parcelable.Creator<Step> CREATOR = new Parcelable.Creator<Step>() {

		@Override
		public Step createFromParcel(Parcel source) {
			Bundle bundle = source.readBundle();
			Step stepData = new Step();
			stepData._id = bundle.getInt(KEY_ID);
			stepData.date = bundle.getString(KEY_DATE);
			stepData.step = bundle.getString(KEY_STEP);
			return stepData;
		}

		@Override
		public Step[] newArray(int size) {
			return new Step[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Bundle bundle = new Bundle();
		bundle.putInt(KEY_ID, _id);
		bundle.putString(KEY_DATE, date);
		bundle.putString(KEY_STEP, step);
		dest.writeBundle(bundle);
	}
}
