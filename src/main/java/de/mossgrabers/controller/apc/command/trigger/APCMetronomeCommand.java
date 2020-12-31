// Written by Mitch Burton
// (c) 2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apc.command.trigger;

import de.mossgrabers.controller.apc.APCConfiguration;
import de.mossgrabers.controller.apc.controller.APCControlSurface;
import de.mossgrabers.framework.command.trigger.transport.MetronomeCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


public class APCMetronomeCommand extends MetronomeCommand<APCControlSurface, APCConfiguration>
{
    private boolean didChangeView = false;
    private boolean wasHeld = false;

    /**
     * Constructor.
     * 
     * @param model The model
     * @param surface The surface
     */
    public APCMetronomeCommand (final IModel model, final APCControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        final ViewManager viewManager = this.surface.getViewManager ();

        switch (event)
        {
            case DOWN:
                if (!viewManager.isActive (Views.SESSION))
                {
                    viewManager.setActive (Views.SESSION);
                    this.didChangeView = true;
                }
                else
                {
                    viewManager.restore ();
                }
                break;

            case LONG:
                this.wasHeld = true;
                break;

            case UP:
                if (viewManager.isActive (Views.SESSION) && this.didChangeView && this.wasHeld)
                    viewManager.restore ();
                this.didChangeView = false;
                this.wasHeld = false;
                break;

            default:
                // Ignore
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
            this.model.getTransport ().toggleMetronome ();
    }
}
