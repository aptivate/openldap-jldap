/* **************************************************************************
 * $Id: Timer.java,v 1.2 2000/03/14 18:17:32 smerrill Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 * 
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY. 
 ***************************************************************************/
 
package com.novell.ldap.client;

/**
 * This class times LDAP operations, if requested, when the
 * operation is sent to the server. The application specifies time
 * to measure. If the thread is not stopped (requestStop) before the
 * thread compeletes, the operation is timed out
 */
/* package*/ class Timer extends Thread {
	private TimerListener listener;
	private int msLimit;
	private int msgId;

    /**
     * Construct a timer to time a message
     *
     * @param msgID     The messageID to time
     * @param msLimit   The interval to time in milliseconds
     * @param listener  The listener associated with this message
     */
	/* package */ Timer(int msgId, int msLimit, TimerListener listener) {
		this.msgId = msgId;
		this.msLimit = msLimit;
		this.listener = listener;
		setDaemon(true);
	}

    /**
     * Stop timing this message.  This is ususally done when a message is complete
     * or has been abandoned.
     */
	/* package */ void requestStop() {
		interrupt();
	}

    /**
     * The thread implementing the timer.  It sleeps for the
     * specified interval.  If it awakens from the sleep,
     * it times out the operation.
     */
	public void run() {
		try {
			sleep(msLimit);
			listener.timedOut(msgId);
		}
		catch(InterruptedException e) {
		}
	}
}
