# README

### 文件说明

* Test 为Cordova创建的测试程序 步骤如下

  ```
  1.cordova create Test com.speedata.cordova  #创建Test项目
  2.cordova platform add android  #添加Android平台
  3.cordova build  #先编译看下自己环境有没有问题
  4cordova plugin add /$PATH$/UHFPlugin #添加uhf的插件,$PATH$为插件所在目录
  5.替换index.html  #index.html为测试调用界面 
  6.cordova build 
  ```

​       测试环境

```
Cordova 9.0.0 (cordova-lib@9.0.1)
android 8.1.0
```





* UHFPlugin 插件
* UHF插件接口说明   uhf.js方法说明
* Test.apk  Android测试程序