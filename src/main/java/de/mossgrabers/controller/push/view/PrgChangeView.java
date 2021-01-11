// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushColorManager;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * The Program Change view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PrgChangeView extends AbstractView<PushControlSurface, PushConfiguration>
{
    private int []  greens;
    private int []  yellows;
    private int     bankNumber    = 0;
    private int     programNumber = -1;
    private boolean isToggled     = false;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public PrgChangeView (final PushControlSurface surface, final IModel model)
    {
        super ("PrgChnge", surface, model);

        final boolean isPush2 = surface.getConfiguration ().isPush2 ();

        final int greenHi = isPush2 ? PushColorManager.PUSH2_COLOR2_GREEN_HI : PushColorManager.PUSH1_COLOR2_GREEN_HI;
        final int green = isPush2 ? PushColorManager.PUSH2_COLOR2_GREEN : PushColorManager.PUSH1_COLOR2_GREEN;
        final int greenLo = isPush2 ? PushColorManager.PUSH2_COLOR2_GREEN_LO : PushColorManager.PUSH1_COLOR2_GREEN_LO;
        final int greenSpring = isPush2 ? PushColorManager.PUSH2_COLOR2_GREEN_SPRING : PushColorManager.PUSH1_COLOR2_GREEN_SPRING;
        this.greens = new int []
        {
            greenHi,
            green,
            greenLo,
            greenSpring,
            greenHi,
            green,
            greenLo,
            greenSpring
        };

        final int yellowHi = isPush2 ? PushColorManager.PUSH2_COLOR2_YELLOW_HI : PushColorManager.PUSH1_COLOR2_YELLOW_HI;
        final int yellow = isPush2 ? PushColorManager.PUSH2_COLOR2_YELLOW : PushColorManager.PUSH1_COLOR2_YELLOW;
        final int yellowLo = isPush2 ? PushColorManager.PUSH2_COLOR2_YELLOW_LO : PushColorManager.PUSH1_COLOR2_YELLOW_LO;
        final int yellowLime = isPush2 ? PushColorManager.PUSH2_COLOR2_YELLOW_LIME : PushColorManager.PUSH1_COLOR2_YELLOW_LIME;
        this.yellows = new int []
        {
            yellowHi,
            yellow,
            yellowLo,
            yellowLime,
            yellowHi,
            yellow,
            yellowLo,
            yellowLime
        };
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID) || event != ButtonEvent.DOWN)
            return;

        final int newBank = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        if (newBank == this.bankNumber)
            this.isToggled = !this.isToggled;
        else
        {
            this.bankNumber = newBank;
            this.isToggled = false;
            this.surface.sendMidiEvent (0xB0, 32, this.bankNumber);
            // Forces the bank change
            if (this.programNumber != -1)
                this.surface.sendMidiEvent (0xC0, this.programNumber, 0);
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final boolean isPush2 = this.surface.getConfiguration ().isPush2 ();
        final int black = isPush2 ? PushColorManager.PUSH2_COLOR_BLACK : PushColorManager.PUSH1_COLOR_BLACK;

        final int scene = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        if (scene < 0 || scene >= 8 || this.bankNumber != scene)
            return black;

        if (this.isToggled)
            return isPush2 ? PushColorManager.PUSH2_COLOR_SCENE_YELLOW : PushColorManager.PUSH1_COLOR_SCENE_YELLOW;
        return isPush2 ? PushColorManager.PUSH2_COLOR_SCENE_GREEN : PushColorManager.PUSH1_COLOR_SCENE_GREEN;
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final int [] colors = this.isToggled ? this.yellows : this.greens;
        final int selPad = this.isToggled ? this.programNumber >= 64 ? this.programNumber - 64 : -1 : this.programNumber < 64 ? this.programNumber : -1;
        final IPadGrid gridPad = this.surface.getPadGrid ();
        final boolean isPush2 = this.surface.getConfiguration ().isPush2 ();
        final int red = isPush2 ? PushColorManager.PUSH2_COLOR2_RED : PushColorManager.PUSH1_COLOR2_RED;
        for (int i = 36; i < 100; i++)
        {
            final int pad = i - 36;
            final int row = pad / 8;
            gridPad.light (i, selPad == pad ? red : colors[row], -1, false);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;
        this.programNumber = note - 36 + (this.isToggled ? 64 : 0);
        this.surface.sendMidiEvent (0xC0, this.programNumber, 0);
    }
}