// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode.device;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.parameterprovider.BankParameterProvider;

import java.util.List;
import java.util.function.BooleanSupplier;


/**
 * The device parameter mode. The knobs control the value of the parameter on the parameter page.
 * device.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ParameterMode<S extends IControlSurface<C>, C extends Configuration> extends AbstractMode<S, C>
{
    protected final ICursorDevice cursorDevice;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happending with a setter otherwise relative
     *            change method is used
     */
    public ParameterMode (final S surface, final IModel model, final boolean isAbsolute)
    {
        this (surface, model, isAbsolute, null, surface::isShiftPressed);
    }


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happending with a setter otherwise relative
     *            change method is used
     * @param isAlternativeFunction Callback function to execute the secondary function, e.g. a
     *            shift button
     */
    public ParameterMode (final S surface, final IModel model, final boolean isAbsolute, final BooleanSupplier isAlternativeFunction)
    {
        this (surface, model, isAbsolute, null, isAlternativeFunction);
    }


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happending with a setter otherwise relative
     *            change method is used
     * @param controls The IDs of the knobs or faders to control this mode
     */
    public ParameterMode (final S surface, final IModel model, final boolean isAbsolute, final List<ContinuousID> controls)
    {
        this (surface, model, isAbsolute, controls, surface::isShiftPressed);
    }


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happending with a setter otherwise relative
     *            change method is used
     * @param controls The IDs of the knobs or faders to control this mode
     * @param isAlternativeFunction Callback function to execute the secondary function, e.g. a
     *            shift button
     */
    public ParameterMode (final S surface, final IModel model, final boolean isAbsolute, final List<ContinuousID> controls, final BooleanSupplier isAlternativeFunction)
    {
        super ("Parameters", surface, model, isAbsolute, model.getCursorDevice ().getParameterBank (), controls, isAlternativeFunction);

        this.cursorDevice = this.model.getCursorDevice ();

        if (controls != null)
            this.setParameters (new BankParameterProvider (this.cursorDevice.getParameterBank ()));
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        if (!this.cursorDevice.doesExist ())
            return;
        final IParameter item = this.cursorDevice.getParameterBank ().getItem (index);
        if (item == null || !item.doesExist ())
            return;
        if (this.isAbsolute)
            item.setValue (value);
        else
            item.changeValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;

        if (!this.cursorDevice.doesExist ())
            return;

        final IParameter item = this.cursorDevice.getParameterBank ().getItem (index);
        if (!item.doesExist ())
            return;

        if (isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);
            item.resetValue ();
        }
        item.touchValue (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        if (!this.cursorDevice.doesExist ())
            return -1;
        final IParameter item = this.cursorDevice.getParameterBank ().getItem (index);
        return item != null && item.doesExist () ? item.getValue () : -1;
    }


    /** {@inheritDoc} */
    @Override
    public String getSelectedItemName ()
    {
        if (!this.cursorDevice.doesExist ())
            return null;
        final IParameterPageBank parameterPageBank = this.cursorDevice.getParameterPageBank ();
        return this.cursorDevice.getName () + " - " + parameterPageBank.getSelectedItem ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        if (this.isAlternativeFunction.getAsBoolean ())
            this.cursorDevice.getDeviceBank ().selectPreviousPage ();
        else
            this.cursorDevice.selectPrevious ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        if (this.isAlternativeFunction.getAsBoolean ())
            this.cursorDevice.getDeviceBank ().selectNextPage ();
        else
            this.cursorDevice.selectNext ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        this.cursorDevice.getParameterBank ().scrollBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        this.cursorDevice.getParameterBank ().scrollForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectItem (final int index)
    {
        this.cursorDevice.getParameterPageBank ().selectPage (index);
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItem ()
    {
        if (this.isAlternativeFunction.getAsBoolean ())
            return this.cursorDevice.getDeviceBank ().canScrollPageBackwards ();
        return this.cursorDevice.canSelectPreviousFX ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItem ()
    {
        if (this.isAlternativeFunction.getAsBoolean ())
            return this.cursorDevice.getDeviceBank ().canScrollPageForwards ();
        return this.cursorDevice.canSelectNextFX ();
    }
}