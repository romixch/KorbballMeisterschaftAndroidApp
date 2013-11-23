package ch.romix.korbball.meisterschaft;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Info extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.info);

		Button commentButton = (Button) findViewById(R.id.info_comment);
		commentButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("play://details?id=ch.romix.korbball.meisterschaft"));
				try {
					startActivity(intent);
				} catch (ActivityNotFoundException e) {
					// try to open it in browser
					intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=ch.romix.korbball.meisterschaft"));
					startActivity(intent);
				}
			}
		});
	}
}
