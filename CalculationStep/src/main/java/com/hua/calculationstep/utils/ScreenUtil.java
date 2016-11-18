package com.hua.calculationstep.utils;

/**
 * Created by wytiger on 15/1/14.
 */

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;

/**
 * 获取屏幕,分辨率相关
 */
public class ScreenUtil {
    private static Context mCtx;
    private static ScreenUtil mScreenTools;
    
    private ScreenUtil(Context ctx){
        mCtx = ctx.getApplicationContext();
    }

    public static ScreenUtil getInstance(Context ctx){
        if(null == mScreenTools){
            mScreenTools = new ScreenUtil(ctx);
        }
        return mScreenTools;
    }    
   
    public static int dp2px(Context ctx, int dp){
        float density = getDensity(ctx);
        return (int)(dp * density + 0.5);
    }

    public static int px2dp(Context ctx, int px){
        float density = getDensity(ctx);
        return (int)((px - 0.5) / density);
    }

    public  static float getDensity(Context ctx){
        return ctx.getResources().getDisplayMetrics().density;
    }

    
    public static int getScreenWidth(Context ctx){
        return ctx.getResources().getDisplayMetrics().widthPixels;
    }

    
    public static int getScreenHeight(Context ctx){
        return ctx.getResources().getDisplayMetrics().heightPixels;
    }
    
        

    
    /**
	 * 无标题栏
	 * 
	 * @param activity
	 */
	public static void noTitle(Activity activity) {
		// 去掉标题栏
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	/**
	 * 隐藏导航栏
	 * @param rootView 需要隐藏导航栏的根视图
	 */
	public static void noNavigation(Activity activity) {
		
		final int HIDE_NAVIGATION_FLAG = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION 
				| View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;
		activity.getWindow().getDecorView().setSystemUiVisibility(HIDE_NAVIGATION_FLAG);
	}
	
	/**
	 * 隐藏导航栏
	 * @param rootView 需要隐藏导航栏的根视图
	 */
	public static void hideNavigation(Activity activity) {
		noNavigation(activity);
	}
	
	
    /**
	 * 切换为全屏,并强制竖屏
	 * 
	 * @param activity
	 */
	public static void fullScreen(Activity activity) {
		// 去掉标题栏
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 去掉信息栏，全屏
		activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		// 强制竖屏
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// 强制横屏
		// activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}
	
	

    /**
     * 获取状态栏高度
     * @return
     */
    public int getStatusBarHeight(){
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = mCtx.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }
	
	
	/**
	 * 改变屏幕亮度,改为最亮
	 * 
	 * @param activity
	 */
	public static void changeToBrightest(Context context) {
		
		try {
			//先关闭自动亮度
			Settings.System.putInt(context.getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
			//调节亮度 255最亮
			Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);
		} catch (Exception e) {
			L.e("屏幕亮度改变失败" + e.getMessage());
		}
	}
}