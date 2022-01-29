package com.test.matchmaker.service;


import com.test.matchmaker.pojo.Group;
import com.test.matchmaker.pojo.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;


@Service
public class GroupHelper {

	@Value("${service.matchmaker.groupSize}")
	private int groupSize;

	@Nullable
	public Group createNewGroupFromQueue(@NotNull Queue<User> queue) {
		if (CollectionUtils.isEmpty(queue) || queue.size() < groupSize)
			return null;

		List<User> users = new LinkedList<>();
		Iterator<User> queueIterator = queue.iterator();

		int count = 0;
		while (queueIterator.hasNext() && count < groupSize) {
			users.add(queue.poll());
			count++;
		}
		return new Group(users);
	}

	public long getGroupLatency(@NotNull Group group) {
		User firstUser = group.getFirstUser();
		if (firstUser != null) {
			LocalDateTime firstUserDateTime = firstUser.getRequestDateTime();
			Duration duration = Duration.between(firstUserDateTime, LocalDateTime.now());
			return duration.toSeconds();
		} else {
			throw new IllegalStateException("Ошибка при формировании команды: список игроков пуст");
		}
	}

	@Nullable
	public User findUserWithHighestLatency(@NotNull Group group) {
		return 	group.getUserList().stream()
				.max(Comparator.comparing(User::getLatency))
				.orElse(null);
	}

	public boolean replaceUser(@NotNull Group group, User newUser, User targetUser) {
		List<User> userList = group.getUserList();
		for (int i = 0; i < userList.size(); i++) {
			if (userList.get(i).equals(targetUser)) {
				userList.set(i, newUser);
				return true;
			}
		}
		return false;
	}

	public List<String> getUserNameList(@NotNull Group group) {
		return group.getUserList().stream()
				.map(User::getName)
				.collect(Collectors.toList());
	}

	public long[] getTimeInQueueArray(@NotNull Group group) {
		LocalDateTime now = LocalDateTime.now();
		return group.getUserList().stream()
				.mapToLong(user -> Duration.between(user.getRequestDateTime(), now).toSeconds())
				.toArray();
	}

	public double[] getSkillArray(@NotNull Group group) {
		return getAsDoubleArray(group, User::getSkill);
	}

	public double[] getLatencyArray(@NotNull Group group) {
		return getAsDoubleArray(group, User::getLatency);
	}

	private double[] getAsDoubleArray(@NotNull Group group, ToDoubleFunction<? super User> mapper) {
		return group.getUserList().stream()
				.mapToDouble(mapper)
				.toArray();
	}
}
