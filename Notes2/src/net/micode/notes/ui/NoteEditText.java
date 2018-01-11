/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.micode.notes.ui;

import android.content.Context;
import android.graphics.Rect;
import android.text.Layout;
import android.text.Selection;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.widget.EditText;

import net.micode.notes.R;

import java.util.HashMap;
import java.util.Map;

public class NoteEditText extends EditText {
    private static final String TAG = "NoteEditText";
    private int mIndex;
    private int mSelectionStartBeforeDelete;
    //字面翻译，删除前的选择，即选择要删除的项目

    private static final String SCHEME_TEL = "tel:" ;//电话
    private static final String SCHEME_HTTP = "http:" ;//网址
    private static final String SCHEME_EMAIL = "mailto:" ;//邮件
    /*
     * 功能描述：使用散列表Hashmap，实现键对值的匹配
     */
    private static final Map<String, Integer> sSchemaActionResMap = new HashMap<String, Integer>();
    static {
        sSchemaActionResMap.put(SCHEME_TEL, R.string.note_link_tel);
        //将字符串tel:与电话号码匹配起来
        sSchemaActionResMap.put(SCHEME_HTTP, R.string.note_link_web);
        //将字符串http:与网址匹配起来
        sSchemaActionResMap.put(SCHEME_EMAIL, R.string.note_link_email);
        //将mailto:与邮箱匹配起来
    }

    /**
     * 功能描述：设置文本内容改变的事件监听器，当文本内容删除或增加时调用此函数
     */
    public interface OnTextViewChangeListener {
        /**
         * 如下，当按下删除键删除文本操作执行时执行下面的方法
         * Delete current edit text when {@link KeyEvent#KEYCODE_DEL} happens
         * and the text is null
         */
        void onEditTextDelete(int index, String text);

        /**
         * 如下，当按下回车键时执行下面的方法实现换行操作
         * Add edit text after current edit text when {@link KeyEvent#KEYCODE_ENTER}
         * happen
         */
        void onEditTextEnter(int index, String text);

        /**
         * 当文本改变时隐藏或展示的项目
         * Hide or show item option when text change
         */
        void onTextChange(int index, boolean hasText);
    }

    private OnTextViewChangeListener mOnTextViewChangeListener;
    //使用上面声明的接口声明一个实例，来实现里面的方法
    public NoteEditText(Context context) {
        super(context, null);
        //使用上下文context获取基本数据
        mIndex = 0;
        //索引的初始值为0
    }
    
    /*
     * 功能描述：设置索引
     */
    public void setIndex(int index) {
        mIndex = index;
    }
    
    /*
     * 功能描述：设置监听器，将传递进来的参数listener赋值给mOnTextViewChangeListener
     */
    public void setOnTextViewChangeListener(OnTextViewChangeListener listener) {
        mOnTextViewChangeListener = listener;
    }
    /*
     * Attributeset自定义控件
     */
    public NoteEditText(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.editTextStyle);
    }

    public NoteEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    @Override
    /*
     * 功能描述：实现点击事件(non-Javadoc)
     * @see android.widget.TextView#onTouchEvent(android.view.MotionEvent)
     */
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            	//按钮按下逻辑
                int x = (int) event.getX();
                //获取左上角x坐标
                int y = (int) event.getY();
                //获取右下角y坐标
                x -= getTotalPaddingLeft();
                y -= getTotalPaddingTop();
                x += getScrollX();
                y += getScrollY();

                Layout layout = getLayout();
                int line = layout.getLineForVertical(y);
                //显示第一行的行号
                int off = layout.getOffsetForHorizontal(line, x);
                //显示第一个字符的索引
                Selection.setSelection(getText(), off);
                //将选中文本选中
                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    /*
     * 功能描述：按键按下事件触发实现(non-Javadoc)
     * @see android.widget.TextView#onKeyDown(int, android.view.KeyEvent)
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        //如果按键按下的是回车键
            case KeyEvent.KEYCODE_ENTER:
            	//如果按键按下的是回车键
                if (mOnTextViewChangeListener != null) {
                	//如果监听器无事件，返回假（此时应该是text控件中午文本）
                    return false;
                }
                break;
            case KeyEvent.KEYCODE_DEL:
            	//如果按键按下的是删除键
                mSelectionStartBeforeDelete = getSelectionStart();
                //getSelectionStart()是获取当前选择的起始索引
                //设置将“删除前的选择开始处”索引
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    /*
     * 功能描述：按键抬起触发的事件实现(non-Javadoc)
     * @see android.widget.TextView#onKeyUp(int, android.view.KeyEvent)
     */
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch(keyCode) {
            case KeyEvent.KEYCODE_DEL:
            	//如果松开的是删除键
                if (mOnTextViewChangeListener != null) {
                	//如果文本为空
                    if (0 == mSelectionStartBeforeDelete && mIndex != 0) {
                    	//如果选中删除索引为0，且删除的结束索引不为空
                        mOnTextViewChangeListener.onEditTextDelete(mIndex, getText().toString());
                        //执行删除操作
                        return true;
                    }
                } else {
                    Log.d(TAG, "OnTextViewChangeListener was not seted");
                    //输出错误信息
                }
                break;
            case KeyEvent.KEYCODE_ENTER:
            	//如果松开的是回车键
                if (mOnTextViewChangeListener != null) {
                	//如果监听不为空
                    int selectionStart = getSelectionStart();
                    //获取选中的起始位置
                    String text = getText().subSequence(selectionStart, length()).toString();
                    //
                    setText(getText().subSequence(0, selectionStart));
                    mOnTextViewChangeListener.onEditTextEnter(mIndex + 1, text);
                    //触发事件行数+1
                } else {
                    Log.d(TAG, "OnTextViewChangeListener was not seted");
                    //报错信息
                }
                break;
            default:
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if (mOnTextViewChangeListener != null) {
        	//如果当前文本控件改变监听事件不为空
            if (!focused && TextUtils.isEmpty(getText())) {
            	//如果当前文本为空
                mOnTextViewChangeListener.onTextChange(mIndex, false);
                //不设置监听事件
            } else {
                mOnTextViewChangeListener.onTextChange(mIndex, true);
                //设置监听事件
            }
        }
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        if (getText() instanceof Spanned) {
            int selStart = getSelectionStart();
            //获取选择的片段的起始位置
            int selEnd = getSelectionEnd();
          //获取选择的片段的终止位置

            int min = Math.min(selStart, selEnd);
            //获取当前选择的索引的较小值
            int max = Math.max(selStart, selEnd);
           //获取当前选择的索引的较大值
            final URLSpan[] urls = ((Spanned) getText()).getSpans(min, max, URLSpan.class);
            //为选中的
            if (urls.length == 1) {
                int defaultResId = 0;
                //初始化可链接的字符串的id
                for(String schema: sSchemaActionResMap.keySet()) {
                    if(urls[0].getURL().indexOf(schema) >= 0) {
                    	//如果链接的字符串的索引大于零
                        defaultResId = sSchemaActionResMap.get(schema);
                        //将可链接的字符串的地址取出
                        break;
                    }
                }

                if (defaultResId == 0) {
                	//如果可链接的地址为0，换句话说没有改变初值
                    defaultResId = R.string.note_link_other;
                    //字符串链接到other
                }

                menu.add(0, 0, 0, defaultResId).setOnMenuItemClickListener(
                		//将链接的字符串呼叫的菜单中这只监听事件
                        new OnMenuItemClickListener() {
                        	//新建一个菜单的项目
                            public boolean onMenuItemClick(MenuItem item) {
                                // goto a new intent
                                urls[0].onClick(NoteEditText.this);
                                //执行点击事件
                                return true;
                            }
                        });
            }
        }
        super.onCreateContextMenu(menu);
        //创建contextMenu菜单
    }
}
