// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mcu;

import de.mossgrabers.controller.mcu.controller.MCUDeviceType;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IActionSetting;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;

import java.util.Arrays;


/**
 * The configuration settings for MCU.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MCUConfiguration extends AbstractConfiguration
{
    /** Zoom state. */
    public static final Integer       ZOOM_STATE                              = Integer.valueOf (50);
    /** Display time or beats. */
    public static final Integer       DISPLAY_MODE_TIME_OR_BEATS              = Integer.valueOf (51);
    /** Display mode tempo or ticks. */
    public static final Integer       DISPLAY_MODE_TICKS_OR_TEMPO             = Integer.valueOf (52);
    /** Has a display. */
    public static final Integer       HAS_DISPLAY1                            = Integer.valueOf (53);
    /** Has a second display. */
    public static final Integer       HAS_DISPLAY2                            = Integer.valueOf (54);
    /** Has a segment display. */
    public static final Integer       HAS_SEGMENT_DISPLAY                     = Integer.valueOf (55);
    /** Has an assignment display. */
    public static final Integer       HAS_ASSIGNMENT_DISPLAY                  = Integer.valueOf (56);
    /** Has motor faders. */
    public static final Integer       HAS_MOTOR_FADERS                        = Integer.valueOf (57);
    /** Has only 1 fader. */
    public static final Integer       HAS_ONLY_1_FADER                        = Integer.valueOf (58);
    /** Display track names in 1st display. */
    public static final Integer       DISPLAY_TRACK_NAMES                     = Integer.valueOf (59);
    /** Replace the vertical zoom withmode change. */
    public static final Integer       USE_VERT_ZOOM_FOR_MODES                 = Integer.valueOf (60);
    /** Use the faders like the editing knobs. */
    public static final Integer       USE_FADERS_AS_KNOBS                     = Integer.valueOf (61);
    /** Select the channel when touching it's fader. */
    public static final Integer       TOUCH_CHANNEL                           = Integer.valueOf (62);
    /** iCON specific Master VU meter. */
    public static final Integer       MASTER_VU_METER                         = Integer.valueOf (63);
    /** Pin FX tracks to last controller. */
    public static final Integer       PIN_FXTRACKS_TO_LAST_CONTROLLER         = Integer.valueOf (64);

    /** Use a Function button to switch to previous mode. */
    public static final int           FOOTSWITCH_2_PREV_MODE                  = 15;
    /** Use a Function button to switch to next mode. */
    public static final int           FOOTSWITCH_2_NEXT_MODE                  = 16;
    /** Use a Function button to switch to Marker mode. */
    public static final int           FOOTSWITCH_2_SHOW_MARKER_MODE           = 17;
    /** Toggle use faders like editing knobs. */
    public static final int           FOOTSWITCH_2_USE_FADERS_LIKE_EDIT_KNOBS = 18;
    /** Use a Function button to execute an action. */
    public static final int           FOOTSWITCH_2_ACTION                     = 19;

    private static final String       CATEGORY_EXTENDER_SETUP                 = "Extender Setup (requires restart)";
    private static final String       CATEGORY_SEGMENT_DISPLAY                = "Segment Display";
    private static final String       CATEGORY_TRACKS                         = "Tracks (requires restart)";
    private static final String       CATEGORY_ASSIGNABLE_BUTTONS             = "Assignable buttons";

    private static final String       DEVICE_SELECT                           = "<Select a profile>";
    private static final String       DEVICE_BEHRINGER_X_TOUCH_ONE            = "Behringer X-Touch One";
    private static final String       DEVICE_ICON_PLATFORM_M                  = "icon Platform M / M+";
    private static final String       DEVICE_ICON_QCON_PRO_X                  = "icon QConPro X";
    private static final String       DEVICE_MACKIE_MCU_PRO                   = "Mackie MCU Pro";
    private static final String       DEVICE_ZOOM_R16                         = "Zoom R16";

    private static final String []    DEVICE_OPTIONS                          =
    {
        DEVICE_SELECT,
        DEVICE_BEHRINGER_X_TOUCH_ONE,
        DEVICE_ICON_PLATFORM_M,
        DEVICE_ICON_QCON_PRO_X,
        DEVICE_MACKIE_MCU_PRO,
        DEVICE_ZOOM_R16
    };

    private static final String []    ASSIGNABLE_VALUES                       =
    {
        "Toggle Play",
        "Toggle Record",
        "Stop All Clips",
        "Toggle Clip Overdub",
        "Undo",
        "Tap Tempo",
        "New Button",
        "Clip Based Looper",
        "Panel layout arrange",
        "Panel layout mix",
        "Panel layout edit",
        "Add instrument track",
        "Add audio track",
        "Add effect track",
        "Quantize",
        "Previous mode",
        "Next mode",
        "Marker mode",
        "Toggle use faders like editing knobs",
        "Action"
    };

    private static final String []    ASSIGNABLE_BUTTON_NAMES                 =
    {
        "Footswitch 1",
        "Footswitch 2",
        "F1",
        "F2",
        "F3",
        "F4",
        "F5"
    };

    private static final String []    TIME_OR_BEATS_OPTIONS                   =
    {
        "Time",
        "Beats"
    };

    private static final String []    TEMPO_OR_TICKS_OPTIONS                  =
    {
        "Ticks",
        "Tempo"
    };

    private static final String []    MCU_DEVICE_TYPE_OPTIONS                 =
    {
        "Main",
        "Extender",
        "Mackie Extender"
    };

    private static final String [] [] MCU_DEVICE_DESCRIPTORS                  =
    {
        {
            "MCU Device 1"
        },
        {
            "MCU Device 1 - left",
            "MCU Device 2 - right",
        },
        {
            "MCU Device 1 - left",
            "MCU Device 2 - center",
            "MCU Device 3 - right",
        },
        {
            "MCU Device 1 - left",
            "MCU Device 2",
            "MCU Device 3",
            "MCU Device 4 - right",
        }
    };

    private IEnumSetting              zoomStateSetting;
    private IEnumSetting              displayTimeSetting;
    private IEnumSetting              tempoOrTicksSetting;
    private IEnumSetting              hasDisplay1Setting;
    private IEnumSetting              hasDisplay2Setting;
    private IEnumSetting              hasSegmentDisplaySetting;
    private IEnumSetting              hasAssignmentDisplaySetting;
    private IEnumSetting              hasMotorFadersSetting;
    private IEnumSetting              hasOnly1FaderSetting;
    private IEnumSetting              displayTrackNamesSetting;
    private IEnumSetting              useVertZoomForModesSetting;
    private IEnumSetting              useFadersAsKnobsSetting;
    private IEnumSetting              masterVuMeterSetting;

    private boolean                   zoomState;
    private boolean                   displayTime;
    private boolean                   displayTicks;
    private boolean                   hasDisplay1;
    private boolean                   hasDisplay2;
    private boolean                   hasSegmentDisplay;
    private boolean                   hasAssignmentDisplay;
    private boolean                   hasMotorFaders;
    private boolean                   hasOnly1Fader;
    private boolean                   displayTrackNames;
    private boolean                   useVertZoomForModes;
    private boolean                   useFadersAsKnobs;
    private boolean                   masterVuMeter;
    private boolean                   touchChannel;
    private int []                    assignableFunctions                     = new int [7];
    private String []                 assignableFunctionActions               = new String [7];
    private final MCUDeviceType []    deviceTyes;
    private boolean                   includeFXTracksInTrackBank;
    private boolean                   pinFXTracksToLastController;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param numMCUDevices The number of MCU device (main device plus extenders) 1-4
     * @param arpeggiatorModes The available arpeggiator modes
     */
    public MCUConfiguration (final IHost host, final IValueChanger valueChanger, final int numMCUDevices, final ArpeggiatorMode [] arpeggiatorModes)
    {
        super (host, valueChanger, arpeggiatorModes);

        Arrays.fill (this.assignableFunctions, 0);
        Arrays.fill (this.assignableFunctionActions, "");

        this.deviceTyes = new MCUDeviceType [numMCUDevices];
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        ///////////////////////////
        // Hardware

        this.activateHardwareSettings (globalSettings);
        this.activateExtenderSettings (globalSettings);

        ///////////////////////////
        // Segment display

        this.activateSegmentDisplaySettings (globalSettings);

        ///////////////////////////
        // Tracks setup

        this.activateTracksSettings (globalSettings);

        ///////////////////////////
        // Assignable buttons

        this.activateAssignableSettings (globalSettings);

        ///////////////////////////
        // Transport

        this.activateBehaviourOnStopSetting (globalSettings);
        this.activateFlipRecordSetting (globalSettings);

        ///////////////////////////
        // Play and Sequence

        this.activateQuantizeAmountSetting (globalSettings);

        ///////////////////////////
        // Workflow

        this.activateExcludeDeactivatedItemsSetting (globalSettings);
        this.activateNewClipLengthSetting (globalSettings);
        this.activateZoomStateSetting (globalSettings);
        this.activateChannelTouchSetting (globalSettings);
        this.activateKnobSpeedSetting (globalSettings);

        ///////////////////////////
        // Browser

        this.activateBrowserSettings (globalSettings);
    }


    private void activateHardwareSettings (final ISettingsUI settingsUI)
    {
        final IEnumSetting profileSetting = settingsUI.getEnumSetting ("Profile", CATEGORY_HARDWARE_SETUP, DEVICE_OPTIONS, DEVICE_OPTIONS[0]);
        profileSetting.addValueObserver (value -> {
            switch (value)
            {
                case DEVICE_MACKIE_MCU_PRO:
                    this.hasDisplay1Setting.set (ON_OFF_OPTIONS[1]);
                    this.hasDisplay2Setting.set (ON_OFF_OPTIONS[0]);
                    this.hasSegmentDisplaySetting.set (ON_OFF_OPTIONS[1]);
                    this.hasAssignmentDisplaySetting.set (ON_OFF_OPTIONS[1]);
                    this.hasMotorFadersSetting.set (ON_OFF_OPTIONS[1]);
                    this.hasOnly1FaderSetting.set (ON_OFF_OPTIONS[0]);
                    this.displayTrackNamesSetting.set (ON_OFF_OPTIONS[1]);
                    this.useVertZoomForModesSetting.set (ON_OFF_OPTIONS[0]);
                    this.useFadersAsKnobsSetting.set (ON_OFF_OPTIONS[0]);
                    this.setVUMetersEnabled (true);
                    this.masterVuMeterSetting.set (ON_OFF_OPTIONS[0]);
                    break;

                case DEVICE_BEHRINGER_X_TOUCH_ONE:
                    this.hasDisplay1Setting.set (ON_OFF_OPTIONS[1]);
                    this.hasDisplay2Setting.set (ON_OFF_OPTIONS[0]);
                    this.hasSegmentDisplaySetting.set (ON_OFF_OPTIONS[1]);
                    this.hasAssignmentDisplaySetting.set (ON_OFF_OPTIONS[1]);
                    this.hasMotorFadersSetting.set (ON_OFF_OPTIONS[1]);
                    this.hasOnly1FaderSetting.set (ON_OFF_OPTIONS[1]);
                    this.displayTrackNamesSetting.set (ON_OFF_OPTIONS[1]);
                    this.useVertZoomForModesSetting.set (ON_OFF_OPTIONS[0]);
                    this.useFadersAsKnobsSetting.set (ON_OFF_OPTIONS[0]);
                    this.setVUMetersEnabled (true);
                    this.masterVuMeterSetting.set (ON_OFF_OPTIONS[0]);
                    break;

                case DEVICE_ICON_PLATFORM_M:
                    this.hasDisplay1Setting.set (ON_OFF_OPTIONS[0]);
                    this.hasDisplay2Setting.set (ON_OFF_OPTIONS[0]);
                    this.hasSegmentDisplaySetting.set (ON_OFF_OPTIONS[0]);
                    this.hasAssignmentDisplaySetting.set (ON_OFF_OPTIONS[0]);
                    this.hasMotorFadersSetting.set (ON_OFF_OPTIONS[1]);
                    this.hasOnly1FaderSetting.set (ON_OFF_OPTIONS[0]);
                    this.displayTrackNamesSetting.set (ON_OFF_OPTIONS[0]);
                    this.useVertZoomForModesSetting.set (ON_OFF_OPTIONS[1]);
                    this.useFadersAsKnobsSetting.set (ON_OFF_OPTIONS[0]);
                    this.setVUMetersEnabled (false);
                    this.masterVuMeterSetting.set (ON_OFF_OPTIONS[0]);
                    break;

                case DEVICE_ICON_QCON_PRO_X:
                    this.hasDisplay1Setting.set (ON_OFF_OPTIONS[1]);
                    this.hasDisplay2Setting.set (ON_OFF_OPTIONS[1]);
                    this.hasSegmentDisplaySetting.set (ON_OFF_OPTIONS[1]);
                    this.hasAssignmentDisplaySetting.set (ON_OFF_OPTIONS[0]);
                    this.hasMotorFadersSetting.set (ON_OFF_OPTIONS[1]);
                    this.hasOnly1FaderSetting.set (ON_OFF_OPTIONS[0]);
                    this.displayTrackNamesSetting.set (ON_OFF_OPTIONS[0]);
                    this.useVertZoomForModesSetting.set (ON_OFF_OPTIONS[0]);
                    this.useFadersAsKnobsSetting.set (ON_OFF_OPTIONS[0]);
                    this.setVUMetersEnabled (true);
                    this.masterVuMeterSetting.set (ON_OFF_OPTIONS[1]);
                    break;

                case DEVICE_ZOOM_R16:
                    this.hasDisplay1Setting.set (ON_OFF_OPTIONS[0]);
                    this.hasDisplay2Setting.set (ON_OFF_OPTIONS[0]);
                    this.hasSegmentDisplaySetting.set (ON_OFF_OPTIONS[0]);
                    this.hasAssignmentDisplaySetting.set (ON_OFF_OPTIONS[0]);
                    this.hasMotorFadersSetting.set (ON_OFF_OPTIONS[0]);
                    this.hasOnly1FaderSetting.set (ON_OFF_OPTIONS[0]);
                    this.displayTrackNamesSetting.set (ON_OFF_OPTIONS[0]);
                    this.useVertZoomForModesSetting.set (ON_OFF_OPTIONS[0]);
                    this.useFadersAsKnobsSetting.set (ON_OFF_OPTIONS[1]);
                    this.setVUMetersEnabled (false);
                    this.masterVuMeterSetting.set (ON_OFF_OPTIONS[0]);
                    break;

                default:
                    return;
            }

            profileSetting.set (DEVICE_SELECT);
        });

        this.hasDisplay1Setting = settingsUI.getEnumSetting ("Has a display", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        this.hasDisplay1Setting.addValueObserver (value -> {
            this.hasDisplay1 = "On".equals (value);
            this.notifyObservers (HAS_DISPLAY1);
        });
        this.isSettingActive.add (HAS_DISPLAY1);

        this.hasDisplay2Setting = settingsUI.getEnumSetting ("Has a second display", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        this.hasDisplay2Setting.addValueObserver (value -> {
            this.hasDisplay2 = "On".equals (value);
            this.notifyObservers (HAS_DISPLAY2);
        });
        this.isSettingActive.add (HAS_DISPLAY2);

        this.hasSegmentDisplaySetting = settingsUI.getEnumSetting ("Has a segment display", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        this.hasSegmentDisplaySetting.addValueObserver (value -> {
            this.hasSegmentDisplay = "On".equals (value);
            this.notifyObservers (HAS_SEGMENT_DISPLAY);
        });
        this.isSettingActive.add (HAS_SEGMENT_DISPLAY);

        this.hasAssignmentDisplaySetting = settingsUI.getEnumSetting ("Has an assignment display", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        this.hasAssignmentDisplaySetting.addValueObserver (value -> {
            this.hasAssignmentDisplay = "On".equals (value);
            this.notifyObservers (HAS_ASSIGNMENT_DISPLAY);
        });
        this.isSettingActive.add (HAS_ASSIGNMENT_DISPLAY);

        this.hasMotorFadersSetting = settingsUI.getEnumSetting ("Has motor faders", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        this.hasMotorFadersSetting.addValueObserver (value -> {
            this.hasMotorFaders = "On".equals (value);
            this.notifyObservers (HAS_MOTOR_FADERS);
        });
        this.isSettingActive.add (HAS_MOTOR_FADERS);

        this.hasOnly1FaderSetting = settingsUI.getEnumSetting ("Has only 1 fader", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.hasOnly1FaderSetting.addValueObserver (value -> {
            this.hasOnly1Fader = "On".equals (value);
            this.notifyObservers (HAS_ONLY_1_FADER);
        });
        this.isSettingActive.add (HAS_ONLY_1_FADER);

        this.displayTrackNamesSetting = settingsUI.getEnumSetting ("Display track names in 1st display", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.displayTrackNamesSetting.addValueObserver (value -> {
            this.displayTrackNames = "On".equals (value);
            this.notifyObservers (DISPLAY_TRACK_NAMES);
        });
        this.isSettingActive.add (DISPLAY_TRACK_NAMES);

        this.useVertZoomForModesSetting = settingsUI.getEnumSetting ("Use vertical zoom to change tracks", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.useVertZoomForModesSetting.addValueObserver (value -> {
            this.useVertZoomForModes = "On".equals (value);
            this.notifyObservers (USE_VERT_ZOOM_FOR_MODES);
        });
        this.isSettingActive.add (USE_VERT_ZOOM_FOR_MODES);

        this.useFadersAsKnobsSetting = settingsUI.getEnumSetting ("Use faders like editing knobs", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.useFadersAsKnobsSetting.addValueObserver (value -> {
            this.useFadersAsKnobs = "On".equals (value);
            this.notifyObservers (USE_FADERS_AS_KNOBS);
        });
        this.isSettingActive.add (USE_FADERS_AS_KNOBS);

        this.activateEnableVUMetersSetting (settingsUI, CATEGORY_HARDWARE_SETUP);

        this.masterVuMeterSetting = settingsUI.getEnumSetting ("Master VU Meter (iCON extension)", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.masterVuMeterSetting.addValueObserver (value -> {
            this.masterVuMeter = "On".equals (value);
            this.notifyObservers (MASTER_VU_METER);
        });
        this.isSettingActive.add (MASTER_VU_METER);
    }


    private void activateExtenderSettings (final ISettingsUI settingsUI)
    {
        for (int i = 0; i < this.deviceTyes.length; i++)
        {
            final String label = MCU_DEVICE_DESCRIPTORS[this.deviceTyes.length - 1][i];
            final IEnumSetting setting = settingsUI.getEnumSetting (label, CATEGORY_EXTENDER_SETUP, MCU_DEVICE_TYPE_OPTIONS, MCU_DEVICE_TYPE_OPTIONS[i == this.deviceTyes.length - 1 ? 0 : 1]);
            final String value = setting.get ();
            if (MCU_DEVICE_TYPE_OPTIONS[0].equals (value))
                this.deviceTyes[i] = MCUDeviceType.MAIN;
            else if (MCU_DEVICE_TYPE_OPTIONS[1].equals (value))
                this.deviceTyes[i] = MCUDeviceType.EXTENDER;
            else if (MCU_DEVICE_TYPE_OPTIONS[2].equals (value))
                this.deviceTyes[i] = MCUDeviceType.MACKIE_EXTENDER;
        }
    }


    private void activateSegmentDisplaySettings (final ISettingsUI settingsUI)
    {
        this.displayTimeSetting = settingsUI.getEnumSetting ("Display time or beats", CATEGORY_SEGMENT_DISPLAY, TIME_OR_BEATS_OPTIONS, TIME_OR_BEATS_OPTIONS[0]);
        this.displayTimeSetting.addValueObserver (value -> {
            this.displayTime = TIME_OR_BEATS_OPTIONS[0].equals (value);
            this.notifyObservers (DISPLAY_MODE_TIME_OR_BEATS);
        });
        this.isSettingActive.add (DISPLAY_MODE_TIME_OR_BEATS);

        this.tempoOrTicksSetting = settingsUI.getEnumSetting ("Display tempo or ticks/milliseconds", CATEGORY_SEGMENT_DISPLAY, TEMPO_OR_TICKS_OPTIONS, TEMPO_OR_TICKS_OPTIONS[0]);
        this.tempoOrTicksSetting.addValueObserver (value -> {
            this.displayTicks = TEMPO_OR_TICKS_OPTIONS[0].equals (value);
            this.notifyObservers (DISPLAY_MODE_TICKS_OR_TEMPO);
        });
        this.isSettingActive.add (DISPLAY_MODE_TICKS_OR_TEMPO);
    }


    private void activateTracksSettings (final ISettingsUI settingsUI)
    {
        final IEnumSetting includeFXTracksSetting = settingsUI.getEnumSetting ("Include FX and master tracks in track bank", CATEGORY_TRACKS, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.includeFXTracksInTrackBank = "On".equals (includeFXTracksSetting.get ());

        if (this.deviceTyes.length > 1 && this.host.supports (Capability.HAS_EFFECT_BANK))
        {
            final IEnumSetting pinFXTracksToLastControllerSetting = settingsUI.getEnumSetting ("Pin FX tracks to last device", CATEGORY_TRACKS, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
            pinFXTracksToLastControllerSetting.addValueObserver (value -> {
                this.pinFXTracksToLastController = "On".equals (value);
                this.notifyObservers (PIN_FXTRACKS_TO_LAST_CONTROLLER);
            });
            this.isSettingActive.add (PIN_FXTRACKS_TO_LAST_CONTROLLER);
        }
    }


    private void activateAssignableSettings (final ISettingsUI settingsUI)
    {
        for (int i = 0; i < this.assignableFunctions.length; i++)
        {
            final int pos = i;
            final IEnumSetting assignableSetting = settingsUI.getEnumSetting (ASSIGNABLE_BUTTON_NAMES[i], CATEGORY_ASSIGNABLE_BUTTONS, ASSIGNABLE_VALUES, ASSIGNABLE_VALUES[6]);
            assignableSetting.addValueObserver (value -> this.assignableFunctions[pos] = lookupIndex (ASSIGNABLE_VALUES, value));

            final IActionSetting actionSetting = settingsUI.getActionSetting (ASSIGNABLE_BUTTON_NAMES[i] + " - Action", CATEGORY_ASSIGNABLE_BUTTONS);
            actionSetting.addValueObserver (value -> this.assignableFunctionActions[pos] = actionSetting.get ());
        }
    }


    /**
     * Activate the Zoom state setting.
     *
     * @param settingsUI The settings
     */
    protected void activateZoomStateSetting (final ISettingsUI settingsUI)
    {
        this.zoomStateSetting = settingsUI.getEnumSetting ("Zoom", CATEGORY_WORKFLOW, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.zoomStateSetting.addValueObserver (value -> {
            this.zoomState = "On".equals (value);
            this.notifyObservers (ZOOM_STATE);
        });
        this.isSettingActive.add (ZOOM_STATE);
    }


    /**
     * Activate the channel touch select setting.
     *
     * @param settingsUI The settings
     */
    protected void activateChannelTouchSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting touchChannelSetting = settingsUI.getEnumSetting ("Select Channel on Fader Touch", CATEGORY_WORKFLOW, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        touchChannelSetting.addValueObserver (value -> {
            this.touchChannel = "On".equals (value);
            this.notifyObservers (TOUCH_CHANNEL);
        });
        this.isSettingActive.add (TOUCH_CHANNEL);
    }


    /**
     * Is zoom active?
     *
     * @return True if zoom is active
     */
    public boolean isZoomState ()
    {
        return this.zoomState;
    }


    /**
     * Toggles the zoom state.
     */
    public void toggleZoomState ()
    {
        this.zoomStateSetting.set (this.zoomState ? ON_OFF_OPTIONS[0] : ON_OFF_OPTIONS[1]);
    }


    /**
     * Display time in the segment display? Otherwise beats (measures).
     *
     * @return True if the time should be displayed
     */
    public boolean isDisplayTime ()
    {
        return this.displayTime;
    }


    /**
     * Toggle to display time or beats.
     */
    public void toggleDisplayTime ()
    {
        this.displayTimeSetting.set (this.displayTime ? TIME_OR_BEATS_OPTIONS[1] : TIME_OR_BEATS_OPTIONS[0]);
    }


    /**
     * Display ticks in the segment display? Otherwise the tempo.
     *
     * @return True if the ticks should be displayed
     */
    public boolean isDisplayTicks ()
    {
        return this.displayTicks;
    }


    /**
     * Toggle to display tempo or ticks.
     */
    public void toggleDisplayTicks ()
    {
        this.tempoOrTicksSetting.set (this.displayTicks ? TEMPO_OR_TICKS_OPTIONS[1] : TEMPO_OR_TICKS_OPTIONS[0]);
    }


    /**
     * Returns true if it has a main display.
     *
     * @return True if it has a main display
     */
    public boolean hasDisplay1 ()
    {
        return this.hasDisplay1;
    }


    /**
     * Returns true if it has a secondary display.
     *
     * @return True if it has a secondary display
     */
    public boolean hasDisplay2 ()
    {
        return this.hasDisplay2;
    }


    /**
     * Returns true if it has a segment display for tempo and position.
     *
     * @return True if it has a segment display
     */
    public boolean hasSegmentDisplay ()
    {
        return this.hasSegmentDisplay;
    }


    /**
     * Returns true if it has an assignment display for modes.
     *
     * @return True if it has an assignment display
     */
    public boolean hasAssignmentDisplay ()
    {
        return this.hasAssignmentDisplay;
    }


    /**
     * Returns true if it has motor faders.
     *
     * @return True if it has motor faders
     */
    public boolean hasMotorFaders ()
    {
        return this.hasMotorFaders;
    }


    /**
     * Returns true if it has only 1 fader.
     *
     * @return True if it has only 1 fader
     */
    public boolean hasOnly1Fader ()
    {
        return this.hasOnly1Fader;
    }


    /**
     * Returns true if the display names should be written in the 1st display.
     *
     * @return True if the display names should be written in the 1st display
     */
    public boolean isDisplayTrackNames ()
    {
        return this.displayTrackNames;
    }


    /**
     * Toggles if the display names should be written in the 1st display.
     */
    public void toggleDisplayTrackNames ()
    {
        this.displayTrackNamesSetting.set (ON_OFF_OPTIONS[this.displayTrackNames ? 0 : 1]);
    }


    /**
     * Returns true if vertical zoom buttons should be used to change modes.
     *
     * @return True if vertical zoom buttons should be used to change modes
     */
    public boolean useVertZoomForModes ()
    {
        return this.useVertZoomForModes;
    }


    /**
     * Returns true if master VU should be enabled.
     *
     * @return True if master VU should be enabled
     */
    public boolean hasMasterVU ()
    {
        return this.masterVuMeter;
    }


    /**
     * Returns true if faders should be used like the editing knobs.
     *
     * @return True if faders should be used like the editing knobs
     */
    public boolean useFadersAsKnobs ()
    {
        return this.useFadersAsKnobs;
    }


    /**
     * Toggle if faders should be used like the editing knobs.
     */
    public void toggleUseFadersAsKnobs ()
    {
        this.useFadersAsKnobsSetting.set (ON_OFF_OPTIONS[this.useFadersAsKnobs ? 0 : 1]);
    }


    /**
     * Get the assignable function.
     *
     * @param index The index of the assignable
     * @return The function
     */
    public int getAssignable (final int index)
    {
        return this.assignableFunctions[index];
    }


    /**
     * If the assignable function is set to Action this method gets the selected action to execute.
     *
     * @param index The index of the assignable
     * @return The ID of the action to execute
     */
    public String getAssignableAction (final int index)
    {
        return this.assignableFunctionActions[index];
    }


    /**
     * Returns true if touching the channel fader should select the track.
     *
     * @return True if touching the channel fader should select the track
     */
    public boolean isTouchChannel ()
    {
        return this.touchChannel;
    }


    /**
     * Should FX and the master track included in the track bank?
     *
     * @return True to include
     */
    public boolean shouldIncludeFXTracksInTrackBank ()
    {
        return this.includeFXTracksInTrackBank;
    }


    /**
     * Should the FX tracks always be displayed on the last device.
     *
     * @return True to display FX tracks on last device
     */
    public boolean shouldPinFXTracksToLastController ()
    {
        return this.pinFXTracksToLastController;
    }


    /**
     * Get the type of the individual MCU devices.
     *
     * @param index The index of the device (0-3)
     * @return The configured device type
     */
    public MCUDeviceType getDeviceType (final int index)
    {
        return this.deviceTyes[index];
    }


    /**
     * Get the number of MCU devices.
     *
     * @return The number of configured MCU devices (1..N)
     */
    public int getNumMCUDevices ()
    {
        return this.deviceTyes.length;
    }
}
