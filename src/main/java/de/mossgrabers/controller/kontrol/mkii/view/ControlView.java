// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.mkii.view;

import de.mossgrabers.controller.kontrol.mkii.KontrolProtocolConfiguration;
import de.mossgrabers.controller.kontrol.mkii.controller.KontrolProtocolControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.ControlOnlyView;


/**
 * The view for controlling the DAW.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ControlView extends ControlOnlyView<KontrolProtocolControlSurface, KontrolProtocolConfiguration>
{
    private static final int [] IDENTITY_MAP = Scales.getIdentityMatrix ();


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ControlView (final KontrolProtocolControlSurface surface, final IModel model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        this.delayedUpdateNoteMapping (IDENTITY_MAP);
    }
}