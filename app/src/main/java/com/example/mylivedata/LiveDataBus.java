package com.example.mylivedata;

import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class LiveDataBus {

     private static LiveDataBus liveDataBus;

     //应用中所有数据持有类的集合
     private Map<String,MyMutableLiveData<Object>> map;

     private LiveDataBus(){
         map=new HashMap<>();
     }
     public static LiveDataBus getInstance(){
         if(liveDataBus!=null){
             return liveDataBus;
         }
         synchronized (LiveDataBus.class){
             if(liveDataBus==null){
                 liveDataBus=new LiveDataBus();
             }
         }
         return liveDataBus;
     }
     public<T> MyMutableLiveData<T> with(String key,Class<T> clazz){
         if(!map.containsKey(key)){
             map.put(key,new MyMutableLiveData<Object>());
         }
         return (MyMutableLiveData<T>) map.get(key);
     }
     public MyMutableLiveData<Object> with(String key){
         return with(key,Object.class);
     }


      public void remove(String key){
         if(map.containsKey(key)){
             map.remove(key);
         }
      }

      public <T> void post(String key,T t){
         if(Looper.getMainLooper()==Looper.myLooper()){
             with(key).setValue(t);
         }else {
             with(key).postValue(t);
         }
      }

    /**
     * hook的执行时间？注册观察者的时候
     * @param <T>
     */
      public static class MyMutableLiveData<T> extends MutableLiveData<T>{
         //目的：使得在observe被调用的时候，能够保证 if (observer.mLastVersion >= mVersion) （livedata源码里面的）成立

          @Override
          public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
              super.observe(owner, observer);
              try {
                  hook(observer);
              } catch (Exception e) {
                  e.printStackTrace();
              }

          }
          public void observeSticky(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer){
              super.observe(owner, observer);
          }

          /**要修改observer.mLastVersion的值那么思考：（逆向思维）
           * mLastVersion-》observer-》iterator.next().getValue()-》mObservers
           * 反射使用的时候，正好相反
           *
           * mObservers-》函数（iterator.next().getValue()）-》observer-》mLastVersion
           * 通过hook，将observer.mLastVersion = mVersion
           * @param observer
           * @throws Exception
           */
          private void hook(Observer<? super T> observer) throws Exception {
              Class<LiveData> liveDataClass = LiveData.class;
              Field fieldmObservers = liveDataClass.getDeclaredField("mObservers");
              fieldmObservers.setAccessible(true);
              Object mObservers = fieldmObservers.get(this);
              Class<?> mObserversClass = mObservers.getClass();

              Method methodget = mObserversClass.getDeclaredMethod("get", Object.class);
              methodget.setAccessible(true);
              Object entry = methodget.invoke(mObservers, observer);
              Object observerWrapper = ((Map.Entry) entry).getValue();
              Class<?> mObserver = observerWrapper.getClass().getSuperclass();//observer

              Field mLastVersion = mObserver.getDeclaredField("mLastVersion");
              mLastVersion.setAccessible(true);
              Field mVersion = liveDataClass.getDeclaredField("mVersion");
              mVersion.setAccessible(true);
              Object mVersionObject = mVersion.get(this);
              mLastVersion.set(observerWrapper,mVersionObject);
          }
      }
}
