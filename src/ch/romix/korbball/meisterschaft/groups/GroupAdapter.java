package ch.romix.korbball.meisterschaft.groups;

import java.util.LinkedList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GroupAdapter extends BaseAdapter {

	private LinkedList<Group> groups;
	private LayoutInflater inflater;

	public GroupAdapter(Context context, LinkedList<Group> groups) {
		this.groups = groups;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return groups.size();
	}

	@Override
	public Object getItem(int position) {
		return groups.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Group group = groups.get(position);
		View view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
		TextView.class.cast(view.findViewById(android.R.id.text1)).setText(group.getGroupName());
		return view;
	}

}
