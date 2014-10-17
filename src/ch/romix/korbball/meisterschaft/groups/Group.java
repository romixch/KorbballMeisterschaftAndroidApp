package ch.romix.korbball.meisterschaft.groups;

public final class Group {

	private final String groupId;
	private final String groupName;

	public Group(String groupId, String groupName) {
		this.groupId = groupId;
		this.groupName = groupName;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getGroupName() {
		return groupName;
	}
}
