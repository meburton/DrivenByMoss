// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.Push1Display;
import de.mossgrabers.controller.push.controller.PushColorManager;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.scale.Scale;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.Pair;


/**
 * Selection of the scale and base note.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ScalesMode extends BaseMode
{
    private Scales scales;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public ScalesMode (final PushControlSurface surface, final IModel model)
    {
        super ("Scale", surface, model);

        this.scales = model.getScales ();
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        if (index != 0)
            return;

        if (!this.increaseKnobMovement ())
            return;

        this.scales.changeScale (value);
        this.update ();
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        if (index == 0)
        {
            if (this.isPush2)
                this.scales.nextScale ();
            else
                this.scales.prevScale ();
        }
        else if (index > 0 && index < 7)
            this.scales.setScaleOffset (index - 1);
        this.update ();
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        int index = this.isButtonRow (0, buttonID);
        if (index >= 0)
        {
            if (index == 7)
                return this.colorManager.getColorIndex (AbstractFeatureGroup.BUTTON_COLOR_OFF);

            final int offset = this.scales.getScaleOffset ();
            final boolean isFirstOrLast = index == 0 || index == 7;
            return isFirstOrLast ? this.isPush2 ? PushColorManager.PUSH2_COLOR_ORANGE_LO : PushColorManager.PUSH1_COLOR_ORANGE_LO : this.colorManager.getColorIndex (offset == index - 1 ? AbstractMode.BUTTON_COLOR_HI : AbstractFeatureGroup.BUTTON_COLOR_ON);
        }

        index = this.isButtonRow (1, buttonID);
        if (index >= 0)
        {
            final int offset = this.scales.getScaleOffset ();
            final boolean isFirstOrLast = index == 0 || index == 7;
            if (isFirstOrLast)
                return this.isPush2 ? PushColorManager.PUSH2_COLOR2_AMBER : PushColorManager.PUSH1_COLOR2_AMBER;
            return this.colorManager.getColorIndex (offset == index - 1 + 6 ? AbstractMode.BUTTON_COLOR2_HI : AbstractMode.BUTTON_COLOR2_ON);
        }

        return super.getButtonColor (buttonID);
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        if (index == 0)
        {
            if (this.isPush2)
                this.scales.prevScale ();
            else
                this.scales.nextScale ();
        }
        else if (index == 7)
            this.scales.toggleChromatic ();
        else
            this.scales.setScaleOffset (index + 5);
        this.update ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final int selIndex = this.scales.getScale ().ordinal ();
        int pos = 0;
        for (final Pair<String, Boolean> p: Push1Display.createMenuList (4, Scale.getNames (), selIndex))
        {
            display.setBlock (pos, 0, (p.getValue ().booleanValue () ? Push1Display.SELECT_ARROW : " ") + p.getKey ());
            pos++;
        }

        display.setBlock (0, 3, this.scales.getRangeText ());

        final int offset = this.scales.getScaleOffset ();
        for (int i = 0; i < 6; i++)
        {
            display.setCell (2, i + 1, "  " + (offset == i ? Push1Display.SELECT_ARROW : " ") + Scales.BASES[i]);
            display.setCell (3, i + 1, "  " + (offset == 6 + i ? Push1Display.SELECT_ARROW : " ") + Scales.BASES[6 + i]);
        }
        display.setCell (3, 7, this.scales.isChromatic () ? "Chromatc" : "In Key");
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final int selIndex = this.scales.getScale ().ordinal ();
        display.addListElement (6, Scale.getNames (), selIndex);

        final int offset = this.scales.getScaleOffset ();
        final String rangeText = this.scales.getRangeText ();
        for (int i = 0; i < 6; i++)
            display.addOptionElement (i == 3 ? "Note range: " + rangeText : "", Scales.BASES[6 + i], offset == 6 + i, "", Scales.BASES[i], offset == i, false);

        display.addOptionElement ("", this.scales.isChromatic () ? "Chromatc" : "In Key", this.scales.isChromatic (), "", "", false, false);
    }


    private void update ()
    {
        this.surface.getViewManager ().getActive ().updateNoteMapping ();
        final PushConfiguration config = this.surface.getConfiguration ();
        config.setScale (this.scales.getScale ().getName ());
        config.setScaleBase (Scales.BASES[this.scales.getScaleOffset ()]);
        config.setScaleInKey (!this.scales.isChromatic ());
    }
}