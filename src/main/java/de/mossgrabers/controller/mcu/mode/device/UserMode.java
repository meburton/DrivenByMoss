// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mcu.mode.device;

import de.mossgrabers.controller.mcu.controller.MCUControlSurface;
import de.mossgrabers.controller.mcu.mode.BaseMode;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.parameterprovider.BankParameterProvider;
import de.mossgrabers.framework.parameterprovider.RangeFilterParameterProvider;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode for editing user parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class UserMode extends BaseMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public UserMode (final MCUControlSurface surface, final IModel model)
    {
        super ("User Parameters", surface, model, model.getUserParameterBank ());

        final int surfaceID = surface.getSurfaceID ();
        this.setParameters (new RangeFilterParameterProvider (new BankParameterProvider (model.getUserParameterBank ()), surfaceID * 8, 8));
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final int extenderOffset = this.surface.getExtenderOffset ();
        this.model.getUserParameterBank ().getItem (extenderOffset + index).changeValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        final int param = this.surface.getExtenderOffset () + index;
        return this.model.getUserParameterBank ().getItem (param).getValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;

        final IParameter param = this.model.getUserParameterBank ().getItem (index);
        if (param.doesExist ())
            param.touchValue (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        this.drawDisplay2 ();

        final ITextDisplay d = this.surface.getTextDisplay ().clear ();

        // Row 1 & 2
        final int extenderOffset = this.surface.getExtenderOffset ();
        final IParameterBank parameterBank = this.model.getUserParameterBank ();
        for (int i = 0; i < 8; i++)
        {
            final IParameter param = parameterBank.getItem (extenderOffset + i);
            d.setCell (0, i, param.doesExist () ? StringUtils.shortenAndFixASCII (param.getName (6), 6) : "").setCell (1, i, StringUtils.shortenAndFixASCII (param.getDisplayedValue (6), 6));
        }

        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateKnobLEDs ()
    {
        final int upperBound = this.model.getValueChanger ().getUpperBound ();
        final int extenderOffset = this.surface.getExtenderOffset ();
        final IParameterBank parameterBank = this.model.getUserParameterBank ();
        for (int i = 0; i < 8; i++)
        {
            final IParameter param = parameterBank.getItem (extenderOffset + i);
            this.surface.setKnobLED (i, MCUControlSurface.KNOB_LED_MODE_SINGLE_DOT, param.doesExist () ? Math.max (1, param.getValue ()) : 0, upperBound);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void resetParameter (final int index)
    {
        final int extenderOffset = this.surface.getExtenderOffset ();
        this.model.getUserParameterBank ().getItem (extenderOffset + index).resetValue ();
    }
}