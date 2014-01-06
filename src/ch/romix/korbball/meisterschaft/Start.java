package ch.romix.korbball.meisterschaft;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Start extends Activity {

	private static final long ONE_MONTH_IN_MILLIS = 30L * 24 * 60 * 60 * 1000;
	private static final long ONE_DAY_IN_MILLIS = 24L * 60 * 60 * 1000;
	static final String GROUP_NAME = "name";
	static final String GROUP_ID = "id";
	private static final String LAST_POPUP_DATE = "LAST_POPUP_DATE";
	private List<Map<String, String>> groupsByGroupId;
	private SimpleAdapter simpleAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_start);
		ListView listleague = (ListView) findViewById(R.id.listLeague);
		groupsByGroupId = new LinkedList<Map<String, String>>();
		simpleAdapter = new SimpleAdapter(this, groupsByGroupId, android.R.layout.simple_list_item_1, new String[] { GROUP_NAME },
				new int[] { android.R.id.text1 });
		listleague.setAdapter(simpleAdapter);
		listleague.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent myIntent = new Intent(view.getContext(), RankingActivity.class);
				Map<String, String> map = groupsByGroupId.get((int) id);
				myIntent.putExtra(RankingActivity.INTENT_GROUP_ID, map.get(GROUP_ID));
				myIntent.putExtra(RankingActivity.INTENT_GROUP_NAME, map.get(GROUP_NAME));
				startActivity(myIntent);
			}
		});
		new GetGroupsTask(groupsByGroupId, simpleAdapter, this).execute();
		askToFillFeedbackForm();
	}

	private void askToFillFeedbackForm() {
		if (shouldShowPopupNow()) {
			showPopup();
		}
	}

	private boolean shouldShowPopupNow() {
		SharedPreferences preferences = getPreferences(Activity.MODE_PRIVATE);
		final long nextPopupInMillis = preferences.getLong(LAST_POPUP_DATE, System.currentTimeMillis());
		return System.currentTimeMillis() > nextPopupInMillis;
	}

	private void showPopup() {
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Was darf es sein?");
		builder.setMessage("Liebe(r) Korbballer(in). Damit ich beim nächsten Update deine liebste Funktion ausliefern kann, bitte ich dich, mir kurz ein paar Ideen zu bewerten.");
		builder.setPositiveButton("Jetzt bewerten", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://romixch.typeform.com/to/r3ELGk"));
				startActivity(browserIntent);
				setNextPopupTime(ONE_MONTH_IN_MILLIS);
			}
		});
		builder.setNegativeButton("Später erinnern", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				setNextPopupTime(ONE_DAY_IN_MILLIS);
			}
		});
		builder.create();
		builder.show();
	}

	private void setNextPopupTime(long showAgainInMillis) {
		getPreferences(MODE_PRIVATE).edit().putLong(LAST_POPUP_DATE, System.currentTimeMillis() + showAgainInMillis).commit();
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
