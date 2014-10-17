package ch.romix.korbball.meisterschaft.favorite;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.romix.korbball.meisterschaft.R;

public class FavoritesActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorites);

		LinearLayout favoriteView = (LinearLayout) findViewById(R.id.favoriteScrollViewLayout);
		LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

		FavoriteStore favoriteStore = new FavoriteStore(this);
		String[] favoritedTeams = favoriteStore.getFavorites();

		if (favoritedTeams.length > 0) {
			View findViewById = findViewById(R.id.textHowToAddFavorites);
			findViewById.setVisibility(View.GONE);
		}

		for (String favoriteId : favoritedTeams) {
			View favoriteTeamView = inflater.inflate(R.layout.favorite, favoriteView, false);

			setText(favoriteTeamView, R.id.favoriteTeamName, favoriteStore.getFavoriteName(favoriteId));
			setText(favoriteTeamView, R.id.favoriteGroupName, favoriteStore.getFavoriteGroupName(favoriteId));

			favoriteView.addView(favoriteTeamView);
		}
	}

	private void setText(View view, int textViewResource, String text) {
		TextView teamView = (TextView) view.findViewById(textViewResource);
		teamView.setText(text);
	}
}
