// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.controller;

/**
 * All categories for commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public enum CommandCategory
{
    BROWSER("Browser"),
    CLIP("Clip"),
    DEVICE("Device"),
    FX_TRACK("FX Track"),
    GLOBAL("Global"),
    LAYOUT("Layout"),
    MARKER("Marker"),
    MASTER("Master"),
    MIDI_CC("MIDI CC"),
    MODES("Modes"),
    NOTE_INPUT("Note Input"),
    SCENE("Scene"),
    TRACK("Track"),
    TRANSPORT("Transport"),
    USER("User"),
    ACTION("Action");


    private final String name;


    /**
     * Constructor.
     *
     * @param name The name of the category
     */
    private CommandCategory (final String name)
    {
        this.name = name;
    }


    /**
     * Get the name of the command.
     *
     * @return The name
     */
    public String getName ()
    {
        return this.name;
    }
}
