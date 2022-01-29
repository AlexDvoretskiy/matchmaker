package com.test.matchmaker.service;


import com.test.matchmaker.pojo.Group;
import com.test.matchmaker.pojo.SummaryStatistics;
import com.test.matchmaker.util.MathUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.DoubleSummaryStatistics;
import java.util.LongSummaryStatistics;


@Service
@RequiredArgsConstructor
public class GroupStatisticServiceImpl implements GroupStatisticService {

	private final GroupHelper groupHelper;

	@Override
	public SummaryStatistics getStatistics(@NotNull Group group) {
		DoubleSummaryStatistics skillStatistics = MathUtils.calculateSummaryStatistics(groupHelper.getSkillArray(group));
		DoubleSummaryStatistics latencyStatistics = MathUtils.calculateSummaryStatistics(groupHelper.getLatencyArray(group));
		LongSummaryStatistics timeStatistics = MathUtils.calculateSummaryStatistics(groupHelper.getTimeInQueueArray(group));

		return SummaryStatistics.builder()
				.minSkill(skillStatistics.getMin())
				.maxSkill(skillStatistics.getMax())
				.avgSkill(skillStatistics.getAverage())
				.minLatency(latencyStatistics.getMin())
				.maxLatency(latencyStatistics.getMax())
				.avgLatency(latencyStatistics.getAverage())
				.minTimeInQueue(timeStatistics.getMin())
				.maxTimeInQueue(timeStatistics.getMax())
				.avgTimeInQueue((long) timeStatistics.getAverage())
				.userNames(groupHelper.getUserNameList(group))
		.build();
	}

}
