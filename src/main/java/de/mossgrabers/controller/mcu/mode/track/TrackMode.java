// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mcu.mode.track;

import de.mossgrabers.controller.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.parameterprovider.ChannelParameterProvider;
import de.mossgrabers.framework.parameterprovider.RangeFilterParameterProvider;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode for editing a track parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackMode extends AbstractTrackMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public TrackMode (final MCUControlSurface surface, final IModel model)
    {
        super ("Track", surface, model);

        if (surface.getConfiguration ().shouldPinFXTracksToLastController () && surface.isLastDevice ())
            this.setParameters (new RangeFilterParameterProvider (new ChannelParameterProvider (model.getEffectTrackBank ()), 0, 8));
        else
            this.setParameters (new RangeFilterParameterProvider (new ChannelParameterProvider (model), 0, 8));
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        final ITrack selectedTrack = this.getSelectedTrack ();
        if (selectedTrack == null)
            return 0;

        switch (index)
        {
            case 0:
                return selectedTrack.getVolume ();

            case 1:
                return selectedTrack.getPan ();

            default:
                return this.model.isEffectTrackBankActive () ? 0 : selectedTrack.getSendBank ().getItem (index - 2).getValue ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        final ITrack selectedTrack = this.getSelectedTrack ();
        if (selectedTrack == null)
            return;

        this.isKnobTouched[index] = isTouched;

        switch (index)
        {
            case 0:
                selectedTrack.touchVolume (isTouched);
                break;
            case 1:
                selectedTrack.touchPan (isTouched);
                break;
            default:
                selectedTrack.getSendBank ().getItem (index - 2).touchValue (isTouched);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        if (!this.surface.getConfiguration ().hasDisplay1 ())
            return;

        this.drawDisplay2 ();
        if (!this.drawTrackHeader ())
            return;

        final ITextDisplay d = this.surface.getTextDisplay ().clear ();

        final ITrack selectedTrack = this.getSelectedTrack ();
        if (selectedTrack == null)
        {
            d.notify ("Please select a track...");
            return;
        }

        final boolean displayTrackNames = this.surface.getConfiguration ().isDisplayTrackNames ();
        if (!displayTrackNames)
        {
            d.setCell (0, 0, "Volume");
            d.setCell (0, 1, "Pan");
        }

        d.setCell (1, 0, selectedTrack.getVolumeStr (6));
        d.setCell (1, 1, selectedTrack.getPanStr (6));

        final int sendStart = 2;
        final int sendCount = 6;
        final boolean isEffectTrackBankActive = this.model.isEffectTrackBankActive ();
        final ISendBank sendBank = selectedTrack.getSendBank ();
        for (int i = 0; i < sendCount; i++)
        {
            final int pos = sendStart + i;
            if (!isEffectTrackBankActive && i < sendBank.getItemCount ())
            {
                final ISend send = sendBank.getItem (i);
                if (send.doesExist ())
                {
                    if (!displayTrackNames)
                        d.setCell (0, pos, StringUtils.fixASCII (send.getName ()));
                    d.setCell (1, pos, send.getDisplayedValue (6));
                }
            }
        }

        if (!displayTrackNames)
            d.done (0);
        d.done (1);
    }


    /** {@inheritDoc} */
    @Override
    public void updateKnobLEDs ()
    {
        final int upperBound = this.model.getValueChanger ().getUpperBound ();

        final ITrack t = this.getSelectedTrack ();
        if (t == null)
        {
            for (int i = 0; i < 8; i++)
                this.surface.setKnobLED (i, MCUControlSurface.KNOB_LED_MODE_WRAP, 0, upperBound);
            return;
        }

        this.surface.setKnobLED (0, MCUControlSurface.KNOB_LED_MODE_WRAP, t.getVolume (), upperBound);
        this.surface.setKnobLED (1, MCUControlSurface.KNOB_LED_MODE_BOOST_CUT, t.getPan (), upperBound);

        final int start = 2;
        final int end = 6;

        final boolean isEffectTrackBankActive = this.model.isEffectTrackBankActive ();
        final ISendBank sendBank = t.getSendBank ();
        for (int i = 0; i < end; i++)
        {
            final int value;
            if (!isEffectTrackBankActive && i < sendBank.getItemCount ())
                value = sendBank.getItem (i).getValue ();
            else
                value = 0;
            this.surface.setKnobLED (start + i, MCUControlSurface.KNOB_LED_MODE_WRAP, value, upperBound);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void resetParameter (final int index)
    {
        final ITrack selectedTrack = this.getSelectedTrack ();
        if (selectedTrack == null)
            return;
        switch (index)
        {
            case 0:
                selectedTrack.resetVolume ();
                break;
            case 1:
                selectedTrack.resetPan ();
                break;
            default:
                if (!this.model.isEffectTrackBankActive ())
                    selectedTrack.getSendBank ().getItem (index - 2).resetValue ();
                break;
        }
    }


    private ITrack getSelectedTrack ()
    {
        if (this.surface.getConfiguration ().shouldPinFXTracksToLastController () && this.surface.isLastDevice ())
            return this.model.getEffectTrackBank ().getSelectedItem ();
        return this.model.getCurrentTrackBank ().getSelectedItem ();
    }
}