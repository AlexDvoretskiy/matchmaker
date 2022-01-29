package com.test.matchmaker.pojo;


import lombok.Data;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;


@Data
public class Group {

	private List<User> userList;

	public Group(@NotNull List<User> userList) {
		this.userList = userList;
	}

	@Nullable
	public User getFirstUser() {
		if (CollectionUtils.isEmpty(userList))
			return null;
		return userList.get(0);
	}

	public void add(User user) {
		userList.add(user);
	}

	public void addAll(Collection<User> userCollection) {
		userList.addAll(userCollection);
	}
}
