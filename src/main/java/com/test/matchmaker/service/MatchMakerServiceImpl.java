package com.test.matchmaker.service;


import com.test.matchmaker.pojo.Group;
import com.test.matchmaker.pojo.SummaryStatistics;
import com.test.matchmaker.pojo.User;
import com.test.matchmaker.util.MathUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;


@Slf4j
@Service
@RequiredArgsConstructor
public class MatchMakerServiceImpl implements MatchMakerService {

	@Value("${service.matchmaker.groupSize}")
	private int groupSize;
	@Value("${service.matchmaker.minBalancePercent}")
	private double minBalancePercent;
	@Value("${service.matchmaker.groupLatencySec}")
	private long groupLatencySec;

	private final GroupHelper groupHelper;
	private final GroupStatisticService groupStatisticService;

	private Queue<User> queue = new ConcurrentLinkedQueue<>();
	private Group currentGroup;


	@Override
	public void addToQueue(User user) {
		queue.add(user);
	}

	@Scheduled(fixedDelayString = "${service.matchmaker.formGroupDelay}")
	private void tryToFormTeam() {
		if (log.isDebugEnabled())
			log.debug("Запущено формирование группы");

		Group group = getCurrentGroup();
		if (group == null)
			return;

		double groupBalance = checkSkillBalance(group);
		long groupLatency = groupHelper.getGroupLatency(group);

		if (log.isDebugEnabled())
			log.debug("Группа сформирована [groupBalance = {}%, groupLatency = {}] : {}", groupBalance, groupLatency, group);

		if (groupBalance < minBalancePercent && groupLatency < groupLatencySec) {
			rebalanceGroup(group);
		} else {
			printGroup(group);
		}
	}

	private void rebalanceGroup(Group group) {
		if (queue.isEmpty())
			return;

		if (log.isDebugEnabled())
			log.debug("Перебалансировка группы: " + group);

		boolean wasRebalancedByLatency = rebalanceGroupByLatency(group);
		boolean wasRebalancedBySkill = rebalanceGroupBySkill(group);

		if (log.isDebugEnabled() && wasRebalancedByLatency)
			log.debug("Группа перебалансирована по задержке: " + group);
		if (log.isDebugEnabled() && wasRebalancedBySkill)
			log.debug("Группа перебалансирована по навыкам: " + group);
	}

	private boolean rebalanceGroupByLatency(Group group) {
		User userWithHighestLatency = groupHelper.findUserWithHighestLatency(group);
		if (userWithHighestLatency == null)
			return false;

		for (User newUser : queue) {
			if (newUser.getLatency() < userWithHighestLatency.getLatency()) {
				return replaceUser(group, newUser, userWithHighestLatency);
			}
		}
		return false;
	}

	private boolean rebalanceGroupBySkill(Group group) {
		List<User> groupUsers = group.getUserList();
		groupUsers.sort(Comparator.comparingDouble(User::getSkill));

		double maxDiff = 0;
		User userToReplace = null;
		for (int i = 0; i < groupUsers.size() - 1; i++) {
			double diff = groupUsers.get(i).getSkill() - groupUsers.get(i + 1).getSkill();

			if (diff > maxDiff) {
				maxDiff = diff;
				userToReplace = groupUsers.get(i + 1);
			}
		}

		if (userToReplace != null) {
			double minDiff = maxDiff;
			User userForReplace = null;
			for (User user : queue) {
				double diff = userToReplace.getSkill() - user.getSkill();

				if (diff > 0 && diff < minDiff) {
					minDiff = diff;
					userForReplace = user;
				}
			}

			if (userForReplace != null) {
				return replaceUser(group, userForReplace, userToReplace);
			}
		}
		return false;
	}

	private boolean replaceUser(Group group, User newUser, User targetUser) {
		boolean wasReplaced = groupHelper.replaceUser(group, newUser, targetUser);
		if (wasReplaced)
			queue.add(targetUser);

		return wasReplaced;
	}

	private Group getCurrentGroup() {
		if (currentGroup == null) {
			currentGroup = groupHelper.createNewGroupFromQueue(queue);
		}
		return currentGroup;
	}

	private double checkSkillBalance(Group group) {
		return MathUtils.calculateVariation(groupHelper.getSkillArray(group));
	}

	private void printGroup(Group group) {
		SummaryStatistics statistic = groupStatisticService.getStatistics(group);
		System.out.println(statistic);
		currentGroup = null;
	}
}
