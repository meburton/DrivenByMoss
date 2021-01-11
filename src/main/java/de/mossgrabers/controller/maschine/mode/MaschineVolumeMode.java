// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mode;

import de.mossgrabers.controller.maschine.MaschineConfiguration;
import de.mossgrabers.controller.maschine.controller.MaschineControlSurface;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.mode.track.VolumeMode;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Arrays;


/**
 * Mode for editing a volume parameter of all tracks.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MaschineVolumeMode extends VolumeMode<MaschineControlSurface, MaschineConfiguration>
{
    private boolean displayVU = false;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public MaschineVolumeMode (final MaschineControlSurface surface, final IModel model)
    {
        super (surface, model, false, surface.getMaschine ().hasMCUDisplay () ? DEFAULT_KNOB_IDS : null);

        this.isKnobTouched = new boolean [9];
        Arrays.fill (this.isKnobTouched, false);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITextDisplay d = this.surface.getTextDisplay ();
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);
            String name = StringUtils.shortenAndFixASCII (t.getName (), 6);
            if (t.isSelected ())
                name = ">" + name;
            d.setCell (0, i, name);

            if (this.displayVU && !this.isKnobTouched[i] && !(this.isKnobTouched[8] && t.isSelected ()))
            {
                final int steps = (int) Math.round (this.model.getValueChanger ().toNormalizedValue (t.getVu ()) * 6);
                d.setCell (1, i, StringUtils.pad ("", steps, '>'));
            }
            else
                d.setCell (1, i, t.getVolumeStr (6));
        }
        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;

        if (index < 8)
            super.onKnobTouch (index, isTouched);
    }


    /**
     * De-/activate to display VU meters.
     */
    public void toggleDisplayVU ()
    {
        this.displayVU = !this.displayVU;
    }
}
