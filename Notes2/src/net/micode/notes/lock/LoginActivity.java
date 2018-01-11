package net.micode.notes.lock;

import net.micode.notes.R;
import net.micode.notes.lock.LocusPassWordView.OnCompleteListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private LocusPassWordView lpwv;
	private int num = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		setTitle("系统登录");
		lpwv = (LocusPassWordView) this.findViewById(R.id.mLocusPassWordView);
		lpwv.setOnCompleteListener(new OnCompleteListener() {
			@Override
			public void onComplete(String mPassword) {
				// ���������ȷ,�������ҳ�档
				if (lpwv.verifyPassword(mPassword)) {
					Toast.makeText(LoginActivity.this, "登录成功！",
							Toast.LENGTH_SHORT).show();
					LoginActivity.this.finish();
				} else {
					Toast.makeText(LoginActivity.this, "密码输入错误,请从新输入",
							Toast.LENGTH_SHORT).show();
					lpwv.clearPassword();
					num++;
					if (num == 5) {
						new AlertDialog.Builder(LoginActivity.this)
								.setTitle("错误")
								.setMessage("错误密码识别超过五次")
								.setPositiveButton("确定",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialoginterface,
													int i) {
												// ��ť�¼�
												LoginActivity.this.finish();
											}
										}).show();
					}
				}
			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();
		// �������Ϊ��,�������������Ľ���
		View noSetPassword = (View) this.findViewById(R.id.tvNoSetPassword);
		if (lpwv.isPasswordEmpty()) {
			noSetPassword.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(LoginActivity.this,
							SetPasswordActivity.class);
					// ���µ�Activity
					startActivity(intent);
				}

			});
			noSetPassword.setVisibility(View.VISIBLE);
		} else {
			noSetPassword.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
}
