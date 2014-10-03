package ch.romix.korbball.meisterschaft;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class Start extends Activity {
	private LocalActivityManager localActivityManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_start);

		localActivityManager = new LocalActivityManager(this, false);

		TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);

		TabSpec tabFavorite = tabHost.newTabSpec("Favoriten");
		TabSpec tabGroups = tabHost.newTabSpec("Gruppen");

		tabFavorite.setIndicator("Favoriten");
		tabFavorite.setContent(new Intent(this, FavoritesActivity.class));
		tabGroups.setIndicator("Gruppen");
		tabGroups.setContent(new Intent(this, GroupsActivity.class));

		localActivityManager.dispatchCreate(savedInstanceState);
		tabHost.setup(localActivityManager);
		tabHost.addTab(tabFavorite);
		tabHost.addTab(tabGroups);

		Button nextFeature = (Button) findViewById(R.id.nextFeature);
		nextFeature.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent pollIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://romixch.typeform.com/to/r3ELGk"));
				startActivity(pollIntent);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		localActivityManager.dispatchResume();
	}

	@Override
	protected void onPause() {
		localActivityManager.dispatchPause(isFinishing());
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(ch.romix.korbball.meisterschaft.R.menu.activity_start, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_info) {
			Intent intent = new Intent(this, Info.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
