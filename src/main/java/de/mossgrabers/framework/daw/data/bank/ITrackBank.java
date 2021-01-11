// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.bank;

import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.observer.IIndexedValueObserver;
import de.mossgrabers.framework.observer.INoteObserver;


/**
 * Interface to a track bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface ITrackBank extends IChannelBank<ITrack>
{
    /**
     * Selects the parent track if any (track must be inside a group).
     */
    void selectParent ();


    /**
     * Returns true if there is a parent track.
     *
     * @return True if there is a parent track
     */
    boolean hasParent ();


    /**
     * Returns true if one of the clips of the current bank page is recording.
     *
     * @return True if one of the clips of the current bank page is recording
     */
    boolean isClipRecording ();


    /**
     * Check if there is a send at the given index, which can be edited.
     *
     * @param sendIndex The index of the send
     * @return True if there is a send to edit
     */
    boolean canEditSend (int sendIndex);


    /**
     * DAWs which can put different sends in a slot can return here a name to be displayed for a
     * slot.
     *
     * @param sendIndex The index of the send
     * @return The name to display
     */
    String getEditSendName (int sendIndex);


    /**
     * Add an observer for the items name.
     *
     * @param observer The observer to notify on a name change
     */
    void addNameObserver (IIndexedValueObserver<String> observer);


    /**
     * Add a note observer.
     *
     * @param observer The note observer
     */
    void addNoteObserver (final INoteObserver observer);
}