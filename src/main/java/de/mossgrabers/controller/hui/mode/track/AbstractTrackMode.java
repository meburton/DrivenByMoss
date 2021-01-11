// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.hui.mode.track;

import de.mossgrabers.controller.hui.HUIConfiguration;
import de.mossgrabers.controller.hui.HUIControllerSetup;
import de.mossgrabers.controller.hui.controller.HUIControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Abstract base mode for all track modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractTrackMode extends AbstractMode<HUIControlSurface, HUIConfiguration>
{
    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     */
    public AbstractTrackMode (final String name, final HUIControlSurface surface, final IModel model)
    {
        super (name, surface, model, false, model.getCurrentTrackBank ());

        model.addTrackBankObserver (this::switchBanks);
    }


    protected ITextDisplay drawTrackHeader ()
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITextDisplay d = this.surface.getTextDisplay ().clear ();

        // Format track names
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);
            d.setCell (0, i, StringUtils.shortenAndFixASCII (t.getName (), 4));
        }

        return d;
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (row == 0)
            this.resetParameter (index);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();

        for (int i = 0; i < 8; i++)
        {
            final ITrack track = tb.getItem (i);

            final boolean exists = track.doesExist ();

            // HUI_INSERT1 is used on icon for selection
            if (buttonID == ButtonID.get (ButtonID.ROW_SELECT_1, i) || buttonID == ButtonID.get (ButtonID.ROW5_1, i))
                return exists && track.isSelected () ? HUIControllerSetup.HUI_BUTTON_STATE_ON : HUIControllerSetup.HUI_BUTTON_STATE_OFF;
            if (buttonID == ButtonID.get (ButtonID.ROW2_1, i))
                return exists && track.isRecArm () ? HUIControllerSetup.HUI_BUTTON_STATE_ON : HUIControllerSetup.HUI_BUTTON_STATE_OFF;
            if (buttonID == ButtonID.get (ButtonID.ROW3_1, i))
                return exists && track.isSolo () ? HUIControllerSetup.HUI_BUTTON_STATE_ON : HUIControllerSetup.HUI_BUTTON_STATE_OFF;
            if (buttonID == ButtonID.get (ButtonID.ROW4_1, i))
                return exists && track.isMute () ? HUIControllerSetup.HUI_BUTTON_STATE_ON : HUIControllerSetup.HUI_BUTTON_STATE_OFF;
        }

        return HUIControllerSetup.HUI_BUTTON_STATE_OFF;
    }


    /**
     * Update the knob LEDs.
     */
    public abstract void updateKnobLEDs ();


    protected abstract void resetParameter (int index);
}