package net.micode.notes.LauncherGuide.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/***
 * Toast 提示工具�? com.myvoice.app.ToastHelper
 * 
 * create at 2014-9-23 下午2:42:03
 */
public class ToastHelper {
	private static Toast toast;

	public static void show(Context ctx, String message) {
		if (ctx == null || TextUtils.isEmpty(message)) {
			return;
		}
		if (null == toast) {
			toast = Toast.makeText(ctx, message, Toast.LENGTH_SHORT);
		} else {
			toast.setText(message);
		}
		toast.show();
	}

	public static void hide() {
		if (null != toast) {
			toast.cancel();
		}
	}

}
