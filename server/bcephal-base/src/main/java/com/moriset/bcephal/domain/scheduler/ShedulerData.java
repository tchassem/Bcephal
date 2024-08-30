package com.moriset.bcephal.domain.scheduler;

import java.util.concurrent.ScheduledFuture;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShedulerData {
	private ShedulerBrowserData data;
	private ScheduledFuture<?> Scheduled;

	private Runnable cancel;
}
