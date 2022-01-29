package com.test.matchmaker.pojo;


import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class SummaryStatistics {

	private double minSkill;
	private double maxSkill;
	private double avgSkill;

	private double minLatency;
	private double maxLatency;
	private double avgLatency;

	private long minTimeInQueue;
	private long maxTimeInQueue;
	private long avgTimeInQueue;

	private List<String> userNames;

}
