package com.novell.ldap.client;

public class Timer extends Thread {
	private TimerListener listener;
	private int msLimit;
	private int msgId;

	public Timer(int msgId, int msLimit, TimerListener listener) {
		this.msgId = msgId;
		this.msLimit = msLimit;
		this.listener = listener;
		setDaemon(true);
	}

	public void requestStop() {
		interrupt();
	}

	public void run() {
		try {
			sleep(msLimit);
			listener.timedOut(msgId);
		}
		catch(InterruptedException e) {
		}
	}
}
