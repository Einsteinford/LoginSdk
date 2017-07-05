package com.hzpz.loginsdk;

/**
 * Created by kk on 17-5-27.
 */

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class WXUserInfo implements Parcelable {
    private String mAppKey = "";
    private String mScope = "";
    private String mSecret = "";
    private String mRedirectUri = "";
    private String mPackageName = "";
    public static final Creator<WXUserInfo> CREATOR = new Creator<WXUserInfo>() {
        public WXUserInfo createFromParcel(Parcel source) {
            return new WXUserInfo(source);
        }

        public WXUserInfo[] newArray(int size) {
            return new WXUserInfo[size];
        }
    };

    public WXUserInfo(Context context, String appKey, String scope, String secret, String redirectUri) {
        this.mAppKey = appKey;
        this.mScope = scope;
        this.mSecret = secret;
        this.mRedirectUri = redirectUri;
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

    public String getSecret() {
        return mSecret;
    }

    public String getRedirectUri() {
        return mRedirectUri;
    }

    public Bundle getUserBundle() {
        Bundle mBundle = new Bundle();
        mBundle.putString("appKey", this.mAppKey);
        mBundle.putString("scope", this.mScope);
        mBundle.putString("secret", this.mSecret);
        mBundle.putString("redirectUri", this.mRedirectUri);
        mBundle.putString("packagename", this.mPackageName);
        return mBundle;
    }

    public static WXUserInfo parseBundleData(Context context, Bundle data) {
        String appKey = data.getString("appKey");
        String scope = data.getString("scope");
        String secret = data.getString("secret");
        String redirectUri = data.getString("redirectUri");
        return new WXUserInfo(context, appKey, scope, secret, redirectUri);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mAppKey);
        dest.writeString(this.mScope);
        dest.writeString(this.mSecret);
        dest.writeString(this.mRedirectUri);
        dest.writeString(this.mPackageName);
    }

    protected WXUserInfo(Parcel in) {
        this.mAppKey = in.readString();
        this.mScope = in.readString();
        this.mSecret = in.readString();
        this.mRedirectUri = in.readString();
        this.mPackageName = in.readString();
    }
}
