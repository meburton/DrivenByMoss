// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.display.IDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.IMode;

import java.util.function.Supplier;


/**
 * Helper functions which would otherwise be repeated in different view and mode classes.
 *
 * @param <C> The type of the configuration
 * @param <S> The type of the control surface
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MVHelper<S extends IControlSurface<C>, C extends Configuration>
{
    private static final String NONE          = "None";
    private static final int    DISPLAY_DELAY = 100;

    private final IModel        model;
    private final ITransport    transport;
    private final S             surface;
    private final IDisplay      display;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public MVHelper (final IModel model, final S surface)
    {
        this.model = model;
        this.surface = surface;
        this.display = this.surface == null ? null : this.surface.getDisplay ();
        this.transport = this.model == null ? null : this.model.getTransport ();
    }


    /**
     * Display the name of the selected track.
     */
    public void notifySelectedTrack ()
    {
        this.delayDisplay ( () -> {

            final ITrackBank currentTrackBank = this.model.getCurrentTrackBank ();
            final ITrack selectedTrack = currentTrackBank.getSelectedItem ();
            if (selectedTrack == null || !selectedTrack.doesExist ())
                return "Selected track: None";
            return selectedTrack.getPosition () + 1 + ": " + selectedTrack.getName ();

        });
    }


    /**
     * Display the name of the selected cursor device.
     */
    public void notifySelectedDevice ()
    {
        this.delayDisplay ( () -> {

            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            return "Selected device: " + (cursorDevice.doesExist () ? cursorDevice.getName () : NONE);

        });
    }


    /**
     * Display the name of the selected parameter page.
     */
    public void notifySelectedParameterPage ()
    {
        this.delayDisplay ( () -> {

            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            if (cursorDevice.doesExist ())
            {
                final String selectedItem = cursorDevice.getParameterPageBank ().getSelectedItem ();
                if (selectedItem != null)
                    return "Page: " + selectedItem;
            }
            return "Page: " + NONE;

        });
    }


    /**
     * Display the name of the selected user parameter page.
     */
    public void notifySelectedUserPage ()
    {
        this.delayDisplay ( () -> {

            final IParameterBank userBank = this.model.getUserParameterBank ();
            final int page = userBank.getScrollPosition () / userBank.getPageSize ();
            return "User Page: " + (page + 1);

        });
    }


    /**
     * Display the name of the selected item in the current mode.
     *
     * @param mode The mode
     */
    public void notifySelectedItem (final IMode mode)
    {
        this.delayDisplay (mode::getSelectedItemName);
    }


    /**
     * Display the current tempo.
     */
    public void notifyTempo ()
    {
        this.delayDisplay ( () -> "Tempo: " + this.transport.formatTempo (this.transport.getTempo ()));
    }


    /**
     * Notify a text after 200ms.
     *
     * @param supplier The supplier to provide the text
     */
    public void delayDisplay (final Supplier<String> supplier)
    {
        this.surface.scheduleTask ( () -> this.display.notify (supplier.get ()), DISPLAY_DELAY);
    }
}
