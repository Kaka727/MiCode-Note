package net.micode.notes.lock;

import net.micode.notes.R;
import net.micode.notes.lock.LocusPassWordView.OnCompleteListener;
import net.micode.notes.lock.util.StringUtil;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class SetPasswordActivity extends Activity
{
	private LocusPassWordView lpwv;
	private String password;
	private boolean needverify = true;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setpassword_activity);
		setTitle("密码设置");
		lpwv = (LocusPassWordView) this.findViewById(R.id.mLocusPassWordView);
		lpwv.setOnCompleteListener(new OnCompleteListener()
		{
			@Override
			public void onComplete(String mPassword)
			{
				password = mPassword;
				if (needverify)
				{
					if (lpwv.verifyPassword(mPassword))
					{
						showDialog("密码输入正确,请输入新密码!");
						SharedPreferences mySharedPreferences = getSharedPreferences("lock",Activity.MODE_PRIVATE);
						SharedPreferences.Editor editor = mySharedPreferences.edit();
						editor.putBoolean("isLock", true);
						editor.commit();
						needverify = false;
					}
					else
					{
						showDialog("错误的密码,请从新输入!");
						password = "";
					}
				}
			}
		});

		OnClickListener mOnClickListener = new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				switch (v.getId())
				{
				case R.id.tvSave:
					if (StringUtil.isNotEmpty(password))
					{
						lpwv.resetPassWord(password);
						showDialog("密码修改成功,请记住密码.");
						SharedPreferences mySharedPreferences = getSharedPreferences("lock",Activity.MODE_PRIVATE);
						SharedPreferences.Editor editor = mySharedPreferences.edit();
						editor.putBoolean("isLock", true);
						editor.commit();
						SetPasswordActivity.this.finish();
						System.out.println(password);
					}
					else
					{
						showDialog("密码不能为空,请输入密码.");
					}
					break;
				case R.id.tvReset:
					lpwv.clearPassword();
					break;
				}
			}
		};
		TextView buttonSave = (TextView) this.findViewById(R.id.tvSave);
		buttonSave.setOnClickListener(mOnClickListener);
		TextView tvReset = (TextView) this.findViewById(R.id.tvReset);
		tvReset.setOnClickListener(mOnClickListener);
		// �������Ϊ��,ֱ����������
		if (lpwv.isPasswordEmpty())
		{
			this.needverify = false;
			Toast.makeText(SetPasswordActivity.this, "Please set the password", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onStart()
	{
		super.onStart();
	}

	@Override
	protected void onStop()
	{
		super.onStop();
	}

	private void showDialog(String title)
	{
		Toast.makeText(SetPasswordActivity.this, title, Toast.LENGTH_SHORT).show();
	}
}
