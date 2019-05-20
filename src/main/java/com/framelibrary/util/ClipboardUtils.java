package com.framelibrary.util;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.HONEYCOMB;

/**
 * 使用ClipboardUtils兼容API LEVEL 11以下实现复杂粘贴
 *
 * @author wangwx
 */
public class ClipboardUtils {
    private static ClipboardManager mClipboardManager;
    private static ClipboardManager mNewCliboardManager;

    private static void instance(Context context) {
        if (SDK_INT >= HONEYCOMB) {
            if (mNewCliboardManager == null)
                mNewCliboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        } else {
            if (mClipboardManager == null)
                mClipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        }
    }

    /**
     * 为剪切板设置内容
     *
     * @param context
     * @param text
     */
    public static void setText(Context context, CharSequence text) {
        if (SDK_INT >= HONEYCOMB) {
            instance(context);
            // Creates a new text clip to put on the clipboard
            ClipData clip = ClipData.newPlainText("simple text", text);

            // Set the clipboard's primary clip.
            mNewCliboardManager.setPrimaryClip(clip);
        } else {
            instance(context);
            mClipboardManager.setText(text);
        }
    }

    /**
     * 获取剪切板的内容
     *
     * @param context
     * @return
     */
    @SuppressLint("LongLogTag")
    public static CharSequence getText(Context context) {
        StringBuilder sb = new StringBuilder();
        if (SDK_INT >= HONEYCOMB) {
            instance(context);
            if (!mNewCliboardManager.hasPrimaryClip()) {
                Log.d("android.content.ClipboardManager", "Clipboard is empty");
                return sb.toString();
            } else {
                ClipData clipData = (mNewCliboardManager).getPrimaryClip();
                int count = clipData.getItemCount();

                for (int i = 0; i < count; ++i) {

                    ClipData.Item item = clipData.getItemAt(i);
                    CharSequence str = item.coerceToText(context);
                    Log.i("mengdd", "item : " + i + ": " + str);
                    sb.append(str);
                }
            }

        } else {
            instance(context);
            sb.append(mClipboardManager.getText());
        }
        return sb.toString();
    }
}