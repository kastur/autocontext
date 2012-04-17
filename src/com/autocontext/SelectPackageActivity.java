package com.autocontext;
import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

public class SelectPackageActivity extends Activity implements OnClickListener {
	PackageManager pkgMan;
	LinearLayout layout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		setContentView(layout);
		
		pkgMan = getPackageManager();
		List<ApplicationInfo> pkgList = pkgMan.getInstalledApplications(0);

		for (ApplicationInfo pkg : pkgList) {
			
			Button b = new Button(this);
			b.setText(pkg.packageName);
			b.setTag(pkg.packageName);
			b.setOnClickListener((OnClickListener)this);
			layout.addView(b);
		}
		
		Button b = new Button(this);
		b.setOnClickListener((android.view.View.OnClickListener)this);
		b.setText("Cancel");
		b.setTag("cancel");
		layout.addView(b);
	}

	public void onClick(View view) {
		String tag = (String)view.getTag(); 
		if (tag.equals("cancel")) {
			setResult(RESULT_CANCELED);
		} else {
			Intent intent = new Intent();
			intent.putExtra("packageName", tag);
			setResult(RESULT_OK, intent);
		}
		finish();
	}
}
