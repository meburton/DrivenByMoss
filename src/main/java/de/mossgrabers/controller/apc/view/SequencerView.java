// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apc.view;

import de.mossgrabers.controller.apc.APCConfiguration;
import de.mossgrabers.controller.apc.controller.APCControlSurface;
import de.mossgrabers.controller.apc.mode.NoteMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractNoteSequencerView;


/**
 * The sequencer view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SequencerView extends AbstractNoteSequencerView<APCControlSurface, APCConfiguration>
{

    private boolean[] editedSteps = new boolean[128];

    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SequencerView (final APCControlSurface surface, final IModel model)
    {
        super ("Sequencer", surface, model, surface.isMkII ());

        this.numDisplayRows = 5;
        this.numSequencerRows = 4;
    }


    /** {@inheritDoc} */
    @Override
    protected void handleSequencerArea (final int index, final int x, final int y, final int velocity)
    {
        if (!this.isActive ())
            return;

        final ModeManager modeManager = this.surface.getModeManager ();
        final INoteClip cursorClip = this.getClip ();
        final int mappedNote = this.keyManager.map (y);
        final int editMidiChannel = this.configuration.getMidiEditChannel ();
        final int adjustedVelocity = this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : this.surface.getButton (ButtonID.get (ButtonID.PAD1, index)).getPressedVelocity ();

        if (this.handleSequencerAreaButtonCombinations (cursorClip, editMidiChannel, x, y, mappedNote, adjustedVelocity))
            return;

        final int state = cursorClip.getStep (editMidiChannel, x, mappedNote).getState ();

        if (velocity > 0)
        {
            if (state == IStepInfo.NOTE_OFF)
            {
                cursorClip.toggleStep(editMidiChannel, x, mappedNote, adjustedVelocity);
                this.editedSteps[x] = true;
            }

            final NoteMode noteMode = (NoteMode) modeManager.get (Modes.NOTE);
            noteMode.setValues (cursorClip, editMidiChannel, x, mappedNote);
            modeManager.setActive (Modes.NOTE);
        }
        else
        {
            // Turn off Note mode
            if (modeManager.isActive (Modes.NOTE))
                modeManager.restore ();

            if (this.editedSteps[x])
            {
                this.editedSteps[x] = false;
                return;
            }
            else if (state == IStepInfo.NOTE_START)
            {
                cursorClip.toggleStep(editMidiChannel, x, mappedNote, adjustedVelocity);
            }
        }
    }


    public void setStepEdited (final int index)
    {
        this.editedSteps[index] = true;
    }

    
    /** {@inheritDoc} */
    private boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final int channel, final int step, final int row, final int note, final int velocity)
    {
        // Handle note duplicate function
        final IHwButton duplicateButton = this.surface.getButton (ButtonID.DUPLICATE);
        if (duplicateButton != null && duplicateButton.isPressed ())
        {
            duplicateButton.setConsumed ();
            final IStepInfo noteStep = clip.getStep (channel, step, note);
            if (noteStep.getState () == IStepInfo.NOTE_START)
                this.copyNote = noteStep;
            else if (this.copyNote != null)
                clip.setStep (channel, step, note, this.copyNote);
            return true;
        }

        // Change length of a note or create a new one with a length
        final int offset = row * clip.getNumSteps ();
        for (int s = 0; s < step; s++)
        {
            final IHwButton button = this.surface.getButton (ButtonID.get (ButtonID.PAD1, offset + s));
            if (button.isPressed ())
            {
                // button.setConsumed ();
                final int length = step - s + 1;
                final double duration = length * Resolution.getValueAt (this.getResolutionIndex ());
                final int state = note < 0 ? 0 : clip.getStep (channel, s, note).getState ();
                if (state == IStepInfo.NOTE_START)
                    clip.updateStepDuration (channel, s, note, duration);
                else
                    clip.setStep (channel, s, note, velocity, duration);
                return true;
            }
        }

        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID) || event != ButtonEvent.DOWN || !this.isActive ())
            return;

        switch (buttonID)
        {
            case SCENE1:
                this.scales.nextScale ();
                this.notifyScale ();
                break;

            case SCENE2:
                this.scales.prevScale ();
                this.notifyScale ();
                break;

            case SCENE3:
                this.scales.toggleChromatic ();
                final boolean isChromatic = this.scales.isChromatic ();
                this.surface.getConfiguration ().setScaleInKey (!isChromatic);
                this.surface.getDisplay ().notify (isChromatic ? "Chromatic" : "In Key");
                break;

            case SCENE4:
                this.onOctaveUp (event);
                break;

            case SCENE5:
                this.onOctaveDown (event);
                break;

            default:
                // Not used
                break;
        }
        this.updateNoteMapping ();
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        if (buttonID == ButtonID.SCENE3)
            return ColorManager.BUTTON_STATE_OFF;
        return this.isActive () ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        super.updateNoteMapping ();
        this.updateScale ();
    }


    private void notifyScale ()
    {
        final String name = this.scales.getScale ().getName ();
        this.surface.getConfiguration ().setScale (name);
        this.surface.getDisplay ().notify (name);
    }
}