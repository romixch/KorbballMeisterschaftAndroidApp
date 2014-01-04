package ch.romix.korbball.meisterschaft;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class GameAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private final LinkedList<Game> games;
	private final Context context;
	private final String currentTeam;
	private final Map<Integer, GameUIData> uiDataByRowId;

	@SuppressLint("UseSparseArrays")
	public GameAdapter(Context context, LinkedList<Game> games, String currentTeam) {
		this.context = context;
		this.games = games;
		this.currentTeam = currentTeam;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		uiDataByRowId = new HashMap<Integer, GameUIData>();
		buildGameUIData();
	}

	private void buildGameUIData() {
		uiDataByRowId.clear();
		int rowId = 0;
		String lastDay = "";
		for (Game game : games) {
			if (!lastDay.equals(game.getDay())) {
				uiDataByRowId.put(rowId++, GameUIData.createDay(game.getDay()));
			}
			uiDataByRowId.put(rowId++, GameUIData.createGame(game, currentTeam));
			lastDay = game.getDay();
		}
	}

	@Override
	public int getCount() {
		return uiDataByRowId.size();
	}

	@Override
	public Object getItem(int position) {
		GameUIData gameUIData = uiDataByRowId.get(position);
		return gameUIData.getData();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public void notifyDataSetChanged() {
		buildGameUIData();
		super.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = uiDataByRowId.get(position).getView(inflater, parent, context);
		return rowView;
	}

}
