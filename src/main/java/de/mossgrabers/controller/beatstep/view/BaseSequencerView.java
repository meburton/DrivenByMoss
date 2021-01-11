// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.beatstep.view;

import de.mossgrabers.controller.beatstep.BeatstepConfiguration;
import de.mossgrabers.controller.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.view.AbstractSequencerView;


/**
 * Base class for Beatstep sequencers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class BaseSequencerView extends AbstractSequencerView<BeatstepControlSurface, BeatstepConfiguration> implements BeatstepView
{
    protected TrackEditing extensions;
    protected int          selectedPad;
    protected boolean      isPlayMode = true;


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The controller
     * @param model The model
     * @param rows The number of rows of the sequencer
     * @param cols The number of columns of the sequencer
     */
    public BaseSequencerView (final String name, final BeatstepControlSurface surface, final IModel model, final int rows, final int cols)
    {
        super (name, surface, model, rows, cols, false);

        this.extensions = new TrackEditing (surface, model);
    }


    protected void changeScrollPosition (final boolean isTurnedRight)
    {
        if (!this.isActive ())
            return;

        if (isTurnedRight)
            this.getClip ().scrollStepsPageForward ();
        else
            this.getClip ().scrollStepsPageBackwards ();
    }


    protected void changeResolution (final int value)
    {
        if (!this.isActive ())
            return;

        final boolean isInc = value >= 65;
        this.selectedResolutionIndex = Resolution.change (this.selectedResolutionIndex, isInc);
        this.getClip ().setStepLength (Resolution.getValueAt (this.selectedResolutionIndex));
    }
}