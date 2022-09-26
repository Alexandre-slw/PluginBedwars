package com.alexandre.bedwars.utils;

import com.alexandre.core.utils.BroadcastUtils;
import com.alexandre.bedwars.Main;

public class TimeMessage {

	private final int id;
	private final String message;
	private final long time;
	private boolean sent = false;
	
	public TimeMessage(int id, String message, long time) {
		this.id = id;
		this.message = message;
		this.time = time;
	}
	
	public void sendInTime(long ticks) {
		if (this.sent) return;
		if (this.time > ticks) return;

		this.sent = true;
		BroadcastUtils.chat(this.message);
		Main.getParty().onMessageSend(this.id);
	}

	public long getTime() {
		return this.time;
	}

	public int getId() {
		return this.id;
	}
}
