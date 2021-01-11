// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.fire.view;

import de.mossgrabers.controller.fire.FireConfiguration;
import de.mossgrabers.controller.fire.controller.FireColorManager;
import de.mossgrabers.controller.fire.controller.FireControlSurface;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IClip;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.midi.INoteRepeat;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Simulates the missing buttons (in contrast to Fire Pro) on the grid.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ShiftView extends AbstractView<FireControlSurface, FireConfiguration> implements IFireView
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ShiftView (final FireControlSurface surface, final IModel model)
    {
        super ("Shift", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();

        // Note Repeat
        final INoteRepeat noteRepeat = this.surface.getMidiInput ().getDefaultNoteInput ().getNoteRepeat ();

        // - on/off
        padGrid.light (84, DAWColor.getColorIndex ((noteRepeat.isActive () ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_GRAY).getColor ()));

        // Octave
        final int octaves = noteRepeat.getOctaves ();
        for (int i = 0; i < 4; i++)
        {
            padGrid.light (36 + i, DAWColor.getColorIndex ((octaves == i ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_GRAY).getColor ()));
            padGrid.light (52 + i, DAWColor.getColorIndex ((octaves == 4 + i ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_GRAY).getColor ()));
        }
        padGrid.light (68, DAWColor.getColorIndex ((octaves == 8 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_GRAY).getColor ()));

        padGrid.light (69, 0);
        padGrid.light (70, 0);
        padGrid.light (71, 0);
        padGrid.light (85, 0);

        // Dec/Inc Arp Mode
        padGrid.light (86, DAWColor.getColorIndex (ColorEx.WHITE));
        padGrid.light (87, DAWColor.getColorIndex (ColorEx.WHITE));

        // Note Repeat period
        final int periodIndex = Resolution.getMatch (noteRepeat.getPeriod ());
        padGrid.light (88, DAWColor.getColorIndex ((periodIndex == 0 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_BLUE).getColor ()));
        padGrid.light (72, DAWColor.getColorIndex ((periodIndex == 2 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_BLUE).getColor ()));
        padGrid.light (56, DAWColor.getColorIndex ((periodIndex == 4 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_BLUE).getColor ()));
        padGrid.light (40, DAWColor.getColorIndex ((periodIndex == 6 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_BLUE).getColor ()));

        padGrid.light (89, DAWColor.getColorIndex ((periodIndex == 1 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_PINK).getColor ()));
        padGrid.light (73, DAWColor.getColorIndex ((periodIndex == 3 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_PINK).getColor ()));
        padGrid.light (57, DAWColor.getColorIndex ((periodIndex == 5 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_PINK).getColor ()));
        padGrid.light (41, DAWColor.getColorIndex ((periodIndex == 7 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_PINK).getColor ()));

        // Note Repeat length
        final int lengthIndex = Resolution.getMatch (noteRepeat.getNoteLength ());
        padGrid.light (90, DAWColor.getColorIndex ((lengthIndex == 0 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_BLUE).getColor ()));
        padGrid.light (74, DAWColor.getColorIndex ((lengthIndex == 2 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_BLUE).getColor ()));
        padGrid.light (58, DAWColor.getColorIndex ((lengthIndex == 4 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_BLUE).getColor ()));
        padGrid.light (42, DAWColor.getColorIndex ((lengthIndex == 6 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_BLUE).getColor ()));

        padGrid.light (91, DAWColor.getColorIndex ((lengthIndex == 1 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_PINK).getColor ()));
        padGrid.light (75, DAWColor.getColorIndex ((lengthIndex == 3 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_PINK).getColor ()));
        padGrid.light (59, DAWColor.getColorIndex ((lengthIndex == 5 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_PINK).getColor ()));
        padGrid.light (43, DAWColor.getColorIndex ((lengthIndex == 7 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_PINK).getColor ()));

        // New clip length
        final FireConfiguration configuration = this.surface.getConfiguration ();
        final int clipLengthIndex = configuration.getNewClipLength ();
        for (int i = 0; i < 8; i++)
            padGrid.light (44 + i, DAWColor.getColorIndex ((i == clipLengthIndex ? DAWColor.DAW_COLOR_RED : DAWColor.DAW_COLOR_LIGHT_ORANGE).getColor ()));

        // Not used
        for (int i = 0; i < 8; i++)
        {
            padGrid.light (60 + i, 0);
            padGrid.light (76 + i, 0);
        }

        padGrid.light (92, 0);
        padGrid.light (96, 0);

        // Duplicate
        if (configuration.isDuplicateModeActive ())
            padGrid.light (93, FireColorManager.FIRE_COLOR_DARK_OCEAN, FireColorManager.FIRE_COLOR_BLUE, true);
        else
            padGrid.light (93, FireColorManager.FIRE_COLOR_DARK_OCEAN);

        padGrid.light (94, FireColorManager.FIRE_COLOR_GREEN);

        // Delete
        if (configuration.isDeleteModeActive ())
            padGrid.light (95, FireColorManager.FIRE_COLOR_DARK_RED, FireColorManager.FIRE_COLOR_RED, true);
        else
            padGrid.light (95, FireColorManager.FIRE_COLOR_DARK_RED);

        // Add tracks
        padGrid.light (97, DAWColor.getColorIndex (ColorEx.ORANGE));
        padGrid.light (98, DAWColor.getColorIndex (ColorEx.BLUE));
        padGrid.light (99, DAWColor.getColorIndex (ColorEx.PINK));
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        final FireConfiguration configuration = this.surface.getConfiguration ();

        switch (note)
        {
            case 36:
            case 37:
            case 38:
            case 39:
                this.setNoteRepeatOctave (note - 36);
                break;
            case 52:
            case 53:
            case 54:
            case 55:
                this.setNoteRepeatOctave (note - 48);
                break;
            case 68:
                this.setNoteRepeatOctave (note - 60);
                break;

            case 86:
                configuration.setNoteRepeatMode (configuration.prevArpeggiatorMode ());
                this.mvHelper.delayDisplay ( () -> "Arp: " + configuration.getNoteRepeatMode ().getName ());
                break;
            case 87:
                configuration.setNoteRepeatMode (configuration.nextArpeggiatorMode ());
                this.mvHelper.delayDisplay ( () -> "Arp: " + configuration.getNoteRepeatMode ().getName ());
                break;

            case 84:
                configuration.toggleNoteRepeatActive ();
                this.mvHelper.delayDisplay ( () -> "Note Repeat: " + (configuration.isNoteRepeatActive () ? "On" : "Off"));
                break;

            case 88:
                this.setPeriod (0);
                break;
            case 89:
                this.setPeriod (1);
                break;
            case 72:
                this.setPeriod (2);
                break;
            case 73:
                this.setPeriod (3);
                break;
            case 56:
                this.setPeriod (4);
                break;
            case 57:
                this.setPeriod (5);
                break;
            case 40:
                this.setPeriod (6);
                break;
            case 41:
                this.setPeriod (7);
                break;

            case 90:
                this.setNoteLength (0);
                break;
            case 91:
                this.setNoteLength (1);
                break;
            case 74:
                this.setNoteLength (2);
                break;
            case 75:
                this.setNoteLength (3);
                break;
            case 58:
                this.setNoteLength (4);
                break;
            case 59:
                this.setNoteLength (5);
                break;
            case 42:
                this.setNoteLength (6);
                break;
            case 43:
                this.setNoteLength (7);
                break;

            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
                final int newClipLength = note - 44;
                configuration.setNewClipLength (newClipLength);
                this.surface.getDisplay ().notify ("Clip len: " + AbstractConfiguration.getNewClipLengthValue (newClipLength));
                break;

            case 93:
                configuration.toggleDuplicateModeActive ();
                this.surface.getDisplay ().notify ("Duplicate " + (configuration.isDuplicateModeActive () ? "Active" : "Off"));
                break;

            case 94:
                final IClip clip = this.model.getCursorClip ();
                if (clip.doesExist ())
                {
                    clip.duplicateContent ();
                    this.surface.getDisplay ().notify ("Double clip");
                }
                else
                    this.surface.getDisplay ().notify ("No clip.");

                break;

            case 95:
                configuration.toggleDeleteModeActive ();
                this.surface.getDisplay ().notify ("Delete " + (configuration.isDeleteModeActive () ? "Active" : "Off"));
                break;

            case 97:
                this.model.getApplication ().addInstrumentTrack ();
                return;
            case 98:
                this.model.getApplication ().addAudioTrack ();
                return;
            case 99:
                this.model.getApplication ().addEffectTrack ();
                return;

            default:
                // Not used
                break;
        }
    }


    private void setPeriod (final int index)
    {
        this.surface.getConfiguration ().setNoteRepeatPeriod (Resolution.values ()[index]);
        this.surface.scheduleTask ( () -> this.surface.getDisplay ().notify ("Period: " + Resolution.getNameAt (index)), 100);
    }


    private void setNoteLength (final int index)
    {
        this.surface.getConfiguration ().setNoteRepeatLength (Resolution.values ()[index]);
        this.surface.scheduleTask ( () -> this.surface.getDisplay ().notify ("Note Len: " + Resolution.getNameAt (index)), 100);
    }


    private void setNoteRepeatOctave (final int octave)
    {
        final FireConfiguration configuration = this.surface.getConfiguration ();
        configuration.setNoteRepeatOctave (octave);
        this.mvHelper.delayDisplay ( () -> "Octave: " + configuration.getNoteRepeatOctave ());
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        return viewManager.get (viewManager.getActiveIDIgnoreTemporary ()).getButtonColor (buttonID);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        // Relay to the actually active view
        final ViewManager viewManager = this.surface.getViewManager ();
        viewManager.get (viewManager.getActiveIDIgnoreTemporary ()).onButton (buttonID, event, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void onSelectKnobValue (final int value)
    {
        // Relay to the actually active view
        final ViewManager viewManager = this.surface.getViewManager ();
        final IView previousView = viewManager.get (viewManager.getActiveIDIgnoreTemporary ());
        if (previousView instanceof IFireView)
            ((IFireView) previousView).onSelectKnobValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public int getSoloButtonColor (final int index)
    {
        // Relay to the actually active view
        final ViewManager viewManager = this.surface.getViewManager ();
        final IView previousView = viewManager.get (viewManager.getActiveIDIgnoreTemporary ());
        return previousView instanceof IFireView ? ((IFireView) previousView).getSoloButtonColor (index) : 0;
    }
}