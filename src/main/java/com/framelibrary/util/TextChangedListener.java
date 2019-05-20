package com.framelibrary.util;

import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.widget.EditText;

import com.framelibrary.config.BaseApplication;
import com.lljjcoder.style.citylist.Toast.ToastUtils;

/**
 * EditText字符串修改监听
 *
 * @author wangwx
 */

public class TextChangedListener {

    // 限制输入框不能输入汉字
    public static void StringWatcher(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    for (int i = 0; i < s.length(); i++) {
                        char c = s.charAt(i);
                        if (c >= 0x4e00 && c <= 0X9fff) {
                            s.delete(i, i + 1);
                        }
                    }
                }
            }
        });
    }

    //限制不能输入空格与换行
    public static void inputLimitSpaceWrap(int maxLength, EditText... editText) {

        for (int i = 0; i < editText.length; i++) {
            editText[i].setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength), SpaceWrap});
        }
    }

    private static InputFilter SpaceWrap = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source.equals(" ") || source.toString().contentEquals("\n")) {

                ToastUtils.showShortToast(BaseApplication.getInstance().getBaseContext(),"禁止输入空格!");
                return "";

            } else return null;
        }
    };

    /**
     * 根据regular规则限制EditText输入类型
     *
     * @param editText
     */
    public static void InputLimitRegular(EditText... editText) {
        String regular = "+1234567890";
        for (int i = 0; i < editText.length; i++) {
            InputLimitRegular(editText[i], regular);
        }
    }

    /**
     * 根据regular规则限制EditText输入类型
     *
     * @param editText
     * @param regular  限制格式 仅可输入
     *                 //限制输入框只能输入英文和数字
     */
    public static void InputLimitRegular(EditText editText, String regular) {
        //限制输入框只能输入英文和数字
        if (StringUtils.isBlank(regular))
            regular = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        final String finalRegular = regular;
        editText.setKeyListener(new DigitsKeyListener() {
            @Override
            public int getInputType() {
                return InputType.TYPE_TEXT_VARIATION_PASSWORD;
            }

            @Override
            protected char[] getAcceptedChars() {
                char[] ac = finalRegular.toCharArray();
                return ac;
            }
        });
    }
}