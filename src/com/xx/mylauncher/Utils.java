package com.xx.mylauncher;

import java.util.ArrayList;
import java.util.List;

import com.xx.mylauncher.dao.CellInfoEntity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Utils {
	
	
	private static final boolean DEBUG = true;
	private static final String TAG = "Utils";

	public static void log(String tag, String msg) {
		if (DEBUG) {
			Log.i(tag, msg);
		}
		
	}
	
	public static void logE(String tag, String msg) {
		if (DEBUG) {
			Log.e(tag, msg);
		}
		
	}
	
	public static void log(String tag, String format, Object...objects) {
		if (DEBUG) {
			String msg = String.format(format, objects);
			Log.i(tag, msg);
		}
	}	
	
	public static void logE(String tag, String format, Object...objects) {
		if (DEBUG) {
			String msg = String.format(format, objects);
			Log.e(tag, msg);
		}
	}	
	
	public static void log(boolean debug, String tag, String format, Object...objects) {
		if (DEBUG && debug) {
			String msg = String.format(format, objects);
			Log.i(tag, msg);
		}
	}	
	
	
	public static void log(String tag, Throwable tr,  String format, Object...objects) {
		String msg = String.format(format, objects);
		Log.e(tag, msg, tr);
	}
	
	public static void toast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
		
	}
	
	public static void toast(Context context, String format, Object...msg) {
		Toast.makeText(context, String.format(format, msg), Toast.LENGTH_LONG).show();
		
	}
	
	public static void toastAndlogcat(Context context, String tag, String format, Object...msg) {
		toast(context, format, msg);
		log(tag, format, msg);
	}
	
	public static void toastAndlogcat(Context context, String tag, String msg) {
		toast(context, msg);
		log(tag, msg);
	}
	
	private static int m_iStatusHeight = -1;
	
	/**
	 * 获取状态栏的高度
	 * @param context
	 * @return
	 */
	public static int getStatusHeight(Context context){
		if (m_iStatusHeight == -1) {
			int statusHeight = 0;
			Rect localRect = new Rect();
			((Activity) context).getWindow().getDecorView()
					.getWindowVisibleDisplayFrame(localRect);
			statusHeight = localRect.top;
			if (0 == statusHeight) {
				Class<?> localClass;
				try {
					localClass = Class.forName("com.android.internal.R$dimen");
					Object localObject = localClass.newInstance();
					int i5 = Integer.parseInt(localClass
							.getField("status_bar_height").get(localObject)
							.toString());
					statusHeight = context.getResources()
							.getDimensionPixelSize(i5);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			m_iStatusHeight = statusHeight;
		}
     
        return m_iStatusHeight;
    }
	
	/**
	 * 提取View的位图
	 * @param view
	 * @return
	 */
	public static Bitmap getViewBitmap(View view) {
		view.clearFocus();
		view.setPressed(false);
		
		boolean willNotCache = view.willNotCacheDrawing();
		view.setWillNotCacheDrawing(false);
		
		//Reset the drawing cache background color to fully transparent
		//for the duration of this operation
		int color = view.getDrawingCacheBackgroundColor();
		float alpha = view.getAlpha();
		view.setDrawingCacheBackgroundColor(0);	
		view.setAlpha(1.0f);
		
		if (color != 0) {
			view.destroyDrawingCache();
			
		}
		
		view.buildDrawingCache();
		Bitmap cacheBitmap = view.getDrawingCache();
		if (cacheBitmap == null) {
			Utils.log(TAG, new RuntimeException(), "failed getViewBitmap(%s)", view.toString() );
			return null;
		}
		
		Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
		
		//Restore the view
		view.destroyDrawingCache();
		view.setAlpha(alpha);
		view.setWillNotCacheDrawing(willNotCache);
		view.setDrawingCacheBackgroundColor(color);
		
		return bitmap;
	}
	
	/**
	 * 安全启动应用程序
	 * @param intent
	 * @return true，启动成功
	 */
	public static boolean safetyStartActivity(Intent intent, Context context) {
		boolean result = false;
		try {
			result = true;
			context.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
			result =false;
			log(TAG, "找不到该应用程序");
			Toast.makeText(context, "找不到该应用程序", Toast.LENGTH_SHORT).show();
		}
		
		return result;
	}
	
	
	public static StateListDrawable getStateListDrawable(Context context, Drawable normalDrawable, Drawable pressDrawable) {
		StateListDrawable stateListDrawable = new StateListDrawable();
		stateListDrawable.addState(
				new int[]{android.R.attr.state_pressed }, pressDrawable);

		stateListDrawable.addState(new int[]{}, normalDrawable);
		
		return stateListDrawable;
	}
	
	/**
	 * 根据一个传入的Bitmap创建一个StateListDrawable对象
	 * 效果是缩小变亮
	 * @param context
	 * @param srcBitmap
	 * @return
	 */
	public static StateListDrawable getStateListDrawable(Context context, Bitmap srcBitmap) {
		if (srcBitmap == null) {
			srcBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.item_bg_default);
		}
		final Drawable normalDrawable = new BitmapDrawable(context.getResources(), srcBitmap);
		final int iSrcWidth = srcBitmap.getWidth();
		final int iSrcHeight = srcBitmap.getHeight();
		final float fScale = 0.6f;
		Bitmap pressBitmap = Bitmap.createScaledBitmap(srcBitmap, (int)(iSrcWidth*fScale), (int)(iSrcHeight*fScale), true);
		BitmapDrawable pressDrawable = new BitmapDrawable(context.getResources(), pressBitmap);
		ColorFilter colorMatrix = new ColorMatrixColorFilter(new float[]{
				1, 0, 0, 0, 80, 
				0, 1, 0, 0, 80,
				0, 0, 1, 0, 100, 
				0, 0, 0, 1, 0  });
		
		pressDrawable.setColorFilter(colorMatrix);
		
		return getStateListDrawable(context, normalDrawable, pressDrawable);
	}
	
	public static StateListDrawable getStateListDrawable(Context context, final View view) {
		return getStateListDrawable(context, getViewBitmap(view) );
	}
	
	public static List<CellInfo> convertDbinfosToCellInfo(final List<CellInfoEntity> list) {
		final List<CellInfo> newList = new ArrayList<CellInfo>();
		
		if (list != null) {
			for (CellInfoEntity item : list) {
				final CellInfo cellInfo = new CellInfo();
				cellInfo.setCellHSpan(item.getCellHSpan());
				cellInfo.setCellVSpan(item.getCellVSpan());
				cellInfo.setCellX(item.getCellX());
				cellInfo.setCellY(item.getCellY());
				cellInfo.setHotSeatCellX(item.getHotseatCellX());
				cellInfo.setHotSeatCellY(item.getHotseatCellY());
				cellInfo.setIconName(item.getLabelName());
				cellInfo.setId(item.getId());
				cellInfo.setLocation(item.getCellLocation()==LauncherDBManager.HOTSEAT ? CellInfo.CellLocation.HOTSEAT : CellInfo.CellLocation.WORKSPACE  );
				cellInfo.setScreen(item.getScreen());
				cellInfo.setType(item.getCellType()==LauncherDBManager.SHORT_CUT ? CellInfo.CellType.SHORT_CUT : CellInfo.CellType.WIDGET);
				cellInfo.setWidgetId(item.getWidgetid());
				cellInfo.setPkgName(item.getPkgName());
				
				String pkgName = item.getPkgName();
				String clsName = item.getClsName();
				if (item.getCellType() == LauncherDBManager.SHORT_CUT) {
					if (pkgName != null && clsName != null) {
						ComponentName cName = new ComponentName(pkgName, clsName);
						Intent intent = new Intent();
						intent.setComponent(cName);
//						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
						cellInfo.setIntent(intent);
					}
				}
				
				newList.add(cellInfo);
			}
		}
		
		return newList;
	}
	
		
		public static void debugCellLayoutChildren(String tag, View[][] children) {
			Utils.log(tag, "-------------debug cellLayout children start---------------------");
			CellInfo cellInfo;
			View view;
			for (int i=0; i<children.length; i++) {
				for (int j=0; j<children[i].length; j++) {
					view = children[i][j];
					if (view == null) {
						Utils.log(tag, "children[%d][%d]：null", i, j);
					} else {
						cellInfo = (CellInfo) view.getTag();
						Utils.log(tag, "children[%d][%d]：cellX=%d, cellY=%d, cellHSpan=%d, cellVSpan=%d", 
													i, j, cellInfo.getCellX(), cellInfo.getCellY(), cellInfo.getCellHSpan(), cellInfo.getCellVSpan()  );
					}
				}
				
			}
	
			Utils.log(tag, "-------------debug cellLayout children end---------------------");
		}
	
	
	
	
	
	
	
	
}
