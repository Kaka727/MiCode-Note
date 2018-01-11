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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;

import net.micode.notes.R;
/*
 * 功能描述:实现下拉菜单
 */
public class DropdownMenu {
    private Button mButton;
    private PopupMenu mPopupMenu;//弹出菜单
    private Menu mMenu;
    /*
     *功能描述：实现DropdownMenu的构建
     *参数：上下文、button、菜单id
     */
    public DropdownMenu(Context context, Button button, int menuId) {
        mButton = button;
        //将参数button赋值
        mButton.setBackgroundResource(R.drawable.dropdown_icon);
        //给button设置背景图标R.drawable.dropdown_icon
        mPopupMenu = new PopupMenu(context, mButton);
        //根据上下文和button新建一个弹出菜单
        mMenu = mPopupMenu.getMenu();
        //获取弹出菜单的菜单项目
        mPopupMenu.getMenuInflater().inflate(menuId, mMenu);
        //为弹出菜单的button设置监听事件
        mButton.setOnClickListener(new OnClickListener() {
        	//如果点击触发监听事件，则弹出显示弹出菜单
            public void onClick(View v) {
                mPopupMenu.show();
            }
        });
    }
    /*
     * 功能描述：为弹出菜单的菜单项目设置监听事件
     */
    public void setOnDropdownMenuItemClickListener(OnMenuItemClickListener listener) {
        if (mPopupMenu != null) {
        	//如果弹出菜单存在
            mPopupMenu.setOnMenuItemClickListener(listener);
            //执行相应的动作
        }
    }
    /*
     * 功能描述：通过菜单项目的id获取菜单项
     */
    public MenuItem findItem(int id) {
        return mMenu.findItem(id);
    }
    /*
     * 功能描述：为弹出菜单的button设置文本信息
     */
    public void setTitle(CharSequence title) {
        mButton.setText(title);
    }
}
