// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.sl.mode;

import de.mossgrabers.controller.sl.SLConfiguration;
import de.mossgrabers.controller.sl.controller.SLControlSurface;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.AbstractMode;


/**
 * Mode for modifying several play/sequence options.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PlayOptionsMode extends AbstractMode<SLControlSurface, SLConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public PlayOptionsMode (final SLControlSurface surface, final IModel model)
    {
        super ("Play options", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITextDisplay d = this.surface.getTextDisplay ().clearRow (0).clearRow (1);
        d.setCell (1, 0, "Oct Down").setCell (1, 1, " Oct Up").setCell (1, 2, "Res Down").setCell (1, 3, " Res Up");
        d.setCell (1, 4, "  Left").setCell (1, 5, "  Right").setCell (1, 7, "Play/Seq");
        d.done (0).done (1);
    }
}