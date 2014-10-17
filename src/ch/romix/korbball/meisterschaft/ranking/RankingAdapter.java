package ch.romix.korbball.meisterschaft.ranking;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import ch.romix.korbball.meisterschaft.R;
import ch.romix.korbball.meisterschaft.favorite.FavoriteStore;
import ch.romix.korbball.meisterschaft.groups.Group;

public class RankingAdapter extends BaseAdapter {

	private List<Map<String, String>> data;
	private LayoutInflater inflater;
	private Resources resources;
	private FavoriteStore favoriteStore;
	private Group group;

	public RankingAdapter(Context context, List<Map<String, String>> data, Group group) {
		this.data = data;
		this.group = group;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		resources = context.getResources();
		favoriteStore = new FavoriteStore(context);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Map<String, String> map = data.get(position);
		View view = inflater.inflate(R.layout.rankingitem, parent, false);
		TextView.class.cast(view.findViewById(R.id.RankingTeam)).setText(map.get(RankingActivity.TEAM_NAME));
		TextView.class.cast(view.findViewById(R.id.rankingGames)).setText(map.get(RankingActivity.GAMES));
		TextView.class.cast(view.findViewById(R.id.rankingRate)).setText(map.get(RankingActivity.RATE));
		TextView.class.cast(view.findViewById(R.id.rankingPoints)).setText(map.get(RankingActivity.POINTS));
		ImageButton favoriteButton = ImageButton.class.cast(view.findViewById(R.id.rankingFavorite));
		if (favoriteStore.isFavorite(map.get(RankingActivity.TEAM_ID))) {
			favoriteButton.setImageDrawable(resources.getDrawable(R.drawable.rate_star_big_on));
		} else {
			favoriteButton.setImageDrawable(resources.getDrawable(R.drawable.rate_star_big_off));
		}
		ImageButton.class.cast(view.findViewById(R.id.rankingFavorite)).setOnClickListener(
				new FavoriteClickListener(map.get(RankingActivity.TEAM_ID), map.get(RankingActivity.TEAM_NAME)));
		return view;
	}

	private final class FavoriteClickListener implements View.OnClickListener {
		private String teamId;
		private String teamName;

		public FavoriteClickListener(String teamId, String teamName) {
			this.teamId = teamId;
			this.teamName = teamName;
		}

		@Override
		public void onClick(View view) {
			ImageButton button = (ImageButton) view;
			if (favoriteStore.isFavorite(teamId)) {
				favoriteStore.removeFavorite(teamId);
				button.setImageDrawable(resources.getDrawable(R.drawable.rate_star_big_off));
			} else {
				favoriteStore.addFavorite(teamId, teamName, group);
				button.setImageDrawable(resources.getDrawable(R.drawable.rate_star_big_on));
			}
		}
	}

}
