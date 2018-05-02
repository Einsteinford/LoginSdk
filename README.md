# LoginSdk
a sample module for social login and share

封装了微博，微信，QQ登录及分享功能，基本用3步就可以完成部署与使用，以及之后的回调。暂时统一返回了AccessToken，OpenId，NickName，HeadImage四个通用字段，之后可以添加更多的字段

## 混淆说明

----

```
# QQ 
-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}
# QQ end

# 微信 
-keep class com.tencent.mm.opensdk.** {*;}
-keep class com.tencent.wxop.** {*;}
-keep class com.tencent.mm.sdk.** {*;}
# 微信 end

#微博
-keep class com.sina.weibo.sdk.** { *; }
# 微博 end
```
