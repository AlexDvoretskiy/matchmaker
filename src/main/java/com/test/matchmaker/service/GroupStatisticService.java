package com.test.matchmaker.service;


import com.test.matchmaker.pojo.Group;
import com.test.matchmaker.pojo.SummaryStatistics;

public interface GroupStatisticService {

	public SummaryStatistics getStatistics(Group group);
}
