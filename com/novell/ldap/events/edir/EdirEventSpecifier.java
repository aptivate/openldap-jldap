/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999-2002 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/
package com.novell.ldap.events.edir;

/**
 * This class is a bean for Specifying the Edirectory Events
 * classification ,events type and event filterstring. These parameter
 * are used for registering for edirectory events.
 * 
 * <p>
 * <b>Note:</b> The filterString argument is used only by
 * <i>registerforFilterEvent()</i> request and the simple register
 * request <i>registerforEvent()</i> ignores the same.
 * </p>
 *
 * @see EdirEventSource#registerforEvent
 * @see EdirEventSource#registerforFilterEvent
 */
public class EdirEventSpecifier {
    private final int eventClassification;
    private final int eventType;
    private final String filter;

    /**
     * Constructor of the EdirEventSpecifier with Classification and Type
     * specified.
     *
     * @param classification The Edirectory Event Classification.
     * @param type The Edirectory Event Types.
     */
    public EdirEventSpecifier(final int classification, final int type) {
        this(classification, type, null);
    }

    /**
     * Default Constructor for this class with all the input parameter.
     *
     * @param classification The Edirectory Event Classification.
     * @param type The Edirectory Event Types.
     * @param afilter The search Filter for filtering the events.
     */
    public EdirEventSpecifier(
        final int classification, final int type, final String afilter
    ) {
        eventClassification = classification;
        eventType = type;
        filter = afilter;
    }

    /**
     * The Edirectory Event Classification Specified for this Event.
     *
     * @return Event Classification as int.
     */
    public int getEventclassification() {
        return eventClassification;
    }

    /**
     * The Edirectory Event Type Specified for this Event.
     *
     * @return Event Type as int.
     */
    public int getEventtype() {
        return eventType;
    }

    /**
     * The Edirectory Event Filter Specified for this Event.
     *
     * @return Filter as String.
     */
    public String getFilter() {
        return filter;
    }
}
