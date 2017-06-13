package com.hzpz.loginsdk;

/**
 * Created by kk on 17-5-27.
 */

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class QQUserInfo implements Parcelable {
    private String mAppKey = "";
    private String mScope = "";
    private String mPackageName = "";
    public static final Creator<QQUserInfo> CREATOR = new Creator() {
        public QQUserInfo createFromParcel(Parcel source) {
            return new QQUserInfo(source);
        }

        public QQUserInfo[] newArray(int size) {
            return new QQUserInfo[size];
        }
    };

    public QQUserInfo(Context context, String appKey, String scope) {
        this.mAppKey = appKey;
        this.mScope = scope;
        this.mPackageName = context.getPackageName();
    }

    public QQUserInfo(Context context, String appKey) {
        this.mAppKey = appKey;
        this.mPackageName = context.getPackageName();
    }

    public String getAppKey() {
        return this.mAppKey;
    }

    public String getScope() {
        return this.mScope;
    }

    public String getPackageName() {
        return this.mPackageName;
    }


    public Bundle getAuthBundle() {
        Bundle mBundle = new Bundle();
        mBundle.putString("appKey", this.mAppKey);
        mBundle.putString("scope", this.mScope);
        mBundle.putString("packagename", this.mPackageName);
        return mBundle;
    }

    public static QQUserInfo parseBundleData(Context context, Bundle data) {
        String appKey = data.getString("appKey");
        String redirectUrl = data.getString("redirectUri");
        String scope = data.getString("scope");
        return new QQUserInfo(context, appKey, scope);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mAppKey);
        dest.writeString(this.mScope);
        dest.writeString(this.mPackageName);
    }

    protected QQUserInfo(Parcel in) {
        this.mAppKey = in.readString();
        this.mScope = in.readString();
        this.mPackageName = in.readString();
    }
}
