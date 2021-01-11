// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apc.command.continuous;

import de.mossgrabers.controller.apc.APCConfiguration;
import de.mossgrabers.controller.apc.controller.APCControlSurface;
import de.mossgrabers.framework.command.continuous.PlayPositionCommand;
import de.mossgrabers.framework.command.continuous.TempoCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.Timeout;
import de.mossgrabers.framework.view.Views;


/**
 * Additionally, display BPM on the grid.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class APCPlayPositionCommand extends PlayPositionCommand<APCControlSurface, APCConfiguration>
{
    private final TempoCommand<APCControlSurface, APCConfiguration> tempoCommand;
    private final Timeout                                           timeout;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param timeout The timeout object
     */
    public APCPlayPositionCommand (final IModel model, final APCControlSurface surface, final Timeout timeout)
    {
        super (model, surface);

        this.tempoCommand = new TempoCommand<> (model, surface);
        this.timeout = timeout;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        if (this.surface.isPressed (ButtonID.TAP_TEMPO))
        {
            this.tempoCommand.execute (value);

            final ViewManager viewManager = this.surface.getViewManager ();
            if (!viewManager.isActive (Views.TEMPO))
                viewManager.setTemporary (Views.TEMPO);
            this.timeout.delay (viewManager::restore);

            return;
        }

        super.execute (value);
    }
}
