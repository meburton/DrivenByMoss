// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.bitwig.framework.daw.data.ParameterImpl;
import de.mossgrabers.bitwig.framework.daw.data.RangedValueImpl;
import de.mossgrabers.bitwig.framework.daw.data.RawParameterImpl;
import de.mossgrabers.bitwig.framework.daw.data.Util;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.constants.AutomationMode;
import de.mossgrabers.framework.daw.constants.TransportConstants;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.utils.StringUtils;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.TimeSignatureValue;
import com.bitwig.extension.controller.api.Transport;

import java.text.DecimalFormat;


/**
 * Encapsulates the Transport instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TransportImpl implements ITransport
{
    private static final String            ACTION_JUMP_TO_END = "jump_to_end_of_arrangement";

    private static final AutomationMode [] AUTOMATION_MODES   = new AutomationMode []
    {
        AutomationMode.LATCH,
        AutomationMode.TOUCH,
        AutomationMode.WRITE
    };

    private final ControllerHost           host;
    private final IApplication             application;
    private final IValueChanger            valueChanger;
    private final Transport                transport;

    private final RawParameterImpl         tempoParameter;
    private final IParameter               crossfadeParameter;
    private final IParameter               metronomeVolumeParameter;


    /**
     * Constructor
     *
     * @param host The host
     * @param application The application
     * @param valueChanger The value changer
     */
    public TransportImpl (final ControllerHost host, final IApplication application, final IValueChanger valueChanger)
    {
        this.host = host;
        this.application = application;
        this.valueChanger = valueChanger;
        this.transport = host.createTransport ();

        this.transport.isPlaying ().markInterested ();
        this.transport.isArrangerRecordEnabled ().markInterested ();
        this.transport.isArrangerOverdubEnabled ().markInterested ();
        this.transport.isClipLauncherAutomationWriteEnabled ().markInterested ();
        this.transport.isClipLauncherOverdubEnabled ().markInterested ();
        this.transport.isArrangerAutomationWriteEnabled ().markInterested ();
        this.transport.automationWriteMode ().markInterested ();
        this.transport.isArrangerLoopEnabled ().markInterested ();
        this.transport.isPunchInEnabled ().markInterested ();
        this.transport.isPunchOutEnabled ().markInterested ();
        this.transport.isMetronomeEnabled ().markInterested ();
        this.transport.isMetronomeTickPlaybackEnabled ().markInterested ();
        this.transport.isMetronomeAudibleDuringPreRoll ().markInterested ();
        this.transport.preRoll ().markInterested ();
        this.transport.getPosition ().markInterested ();

        this.crossfadeParameter = new ParameterImpl (valueChanger, this.transport.crossfade ());
        this.metronomeVolumeParameter = new RangedValueImpl ("Metronome Volume", valueChanger, this.transport.metronomeVolume ());
        this.tempoParameter = new RawParameterImpl (valueChanger, this.transport.tempo (), TransportConstants.MIN_TEMPO, TransportConstants.MAX_TEMPO);

        final TimeSignatureValue ts = this.transport.timeSignature ();
        ts.numerator ().markInterested ();
        ts.denominator ().markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        Util.setIsSubscribed (this.transport.isPlaying (), enable);
        Util.setIsSubscribed (this.transport.isArrangerRecordEnabled (), enable);
        Util.setIsSubscribed (this.transport.isArrangerOverdubEnabled (), enable);
        Util.setIsSubscribed (this.transport.isClipLauncherAutomationWriteEnabled (), enable);
        Util.setIsSubscribed (this.transport.isClipLauncherOverdubEnabled (), enable);
        Util.setIsSubscribed (this.transport.isArrangerAutomationWriteEnabled (), enable);
        Util.setIsSubscribed (this.transport.automationWriteMode (), enable);
        Util.setIsSubscribed (this.transport.isArrangerLoopEnabled (), enable);
        Util.setIsSubscribed (this.transport.isPunchInEnabled (), enable);
        Util.setIsSubscribed (this.transport.isPunchOutEnabled (), enable);
        Util.setIsSubscribed (this.transport.isMetronomeEnabled (), enable);
        Util.setIsSubscribed (this.transport.isMetronomeTickPlaybackEnabled (), enable);
        Util.setIsSubscribed (this.transport.isMetronomeAudibleDuringPreRoll (), enable);
        Util.setIsSubscribed (this.transport.preRoll (), enable);
        Util.setIsSubscribed (this.transport.getPosition (), enable);

        this.crossfadeParameter.enableObservers (enable);
        this.metronomeVolumeParameter.enableObservers (enable);
        this.tempoParameter.enableObservers (enable);

        final TimeSignatureValue ts = this.transport.timeSignature ();
        Util.setIsSubscribed (ts.numerator (), enable);
        Util.setIsSubscribed (ts.denominator (), enable);
    }


    /** {@inheritDoc} */
    @Override
    public void play ()
    {
        this.transport.play ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPlaying ()
    {
        return this.transport.isPlaying ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void restart ()
    {
        this.transport.restart ();
    }


    /** {@inheritDoc} */
    @Override
    public void stop ()
    {
        this.transport.stop ();
    }


    /** {@inheritDoc} */
    @Override
    public void stopAndRewind ()
    {
        this.transport.stop ();
        // Delay the position movement to make sure that the playback is really stopped
        this.host.scheduleTask ( () -> this.transport.setPosition (0), 100);
    }


    /** {@inheritDoc} */
    @Override
    public void record ()
    {
        this.transport.record ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isRecording ()
    {
        return this.transport.isArrangerRecordEnabled ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isArrangerOverdub ()
    {
        return this.transport.isArrangerOverdubEnabled ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleOverdub ()
    {
        this.transport.isArrangerOverdubEnabled ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isLauncherOverdub ()
    {
        return this.transport.isClipLauncherOverdubEnabled ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setLauncherOverdub (final boolean on)
    {
        this.transport.isClipLauncherOverdubEnabled ().set (on);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleLauncherOverdub ()
    {
        this.transport.isClipLauncherOverdubEnabled ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void setMetronome (final boolean on)
    {
        this.transport.isMetronomeEnabled ().set (on);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMetronomeOn ()
    {
        return this.transport.isMetronomeEnabled ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMetronome ()
    {
        this.transport.isMetronomeEnabled ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMetronomeTicksOn ()
    {
        return this.transport.isMetronomeTickPlaybackEnabled ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMetronomeTicks ()
    {
        this.transport.isMetronomeTickPlaybackEnabled ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void setMetronomeTicks (final boolean on)
    {
        this.transport.isMetronomeTickPlaybackEnabled ().set (on);
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getMetronomeVolumeParameter ()
    {
        return this.metronomeVolumeParameter;
    }


    /** {@inheritDoc} */
    @Override
    public String getMetronomeVolumeStr ()
    {
        return this.transport.metronomeVolume ().displayedValue ().getLimited (6) + " dB";
    }


    /** {@inheritDoc} */
    @Override
    public int getMetronomeVolume ()
    {
        return this.valueChanger.fromNormalizedValue (this.transport.metronomeVolume ().get ());
    }


    /** {@inheritDoc} */
    @Override
    public void changeMetronomeVolume (final int control)
    {
        this.transport.metronomeVolume ().inc (Double.valueOf (this.valueChanger.calcKnobChange (control)), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public void setMetronomeVolume (final int value)
    {
        this.transport.metronomeVolume ().set (Integer.valueOf (value), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPrerollMetronomeEnabled ()
    {
        return this.transport.isMetronomeAudibleDuringPreRoll ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void togglePrerollMetronome ()
    {
        this.transport.isMetronomeAudibleDuringPreRoll ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void setLoop (final boolean on)
    {
        this.transport.isArrangerLoopEnabled ().set (on);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleLoop ()
    {
        this.transport.isArrangerLoopEnabled ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isLoop ()
    {
        return this.transport.isArrangerLoopEnabled ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isWritingClipLauncherAutomation ()
    {
        return this.transport.isClipLauncherAutomationWriteEnabled ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isWritingArrangerAutomation ()
    {
        return this.transport.isArrangerAutomationWriteEnabled ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public AutomationMode [] getAutomationWriteModes ()
    {
        return AUTOMATION_MODES;
    }


    /** {@inheritDoc} */
    @Override
    public AutomationMode getAutomationWriteMode ()
    {
        return AutomationMode.lookup (this.transport.automationWriteMode ().get ());
    }


    /** {@inheritDoc} */
    @Override
    public void setAutomationWriteMode (final AutomationMode mode)
    {
        switch (mode)
        {
            case TRIM_READ:
            case READ:
                this.transport.isArrangerAutomationWriteEnabled ().set (false);
                break;

            case WRITE:
            case TOUCH:
            case LATCH:
            case LATCH_PREVIEW:
                this.transport.isArrangerAutomationWriteEnabled ().set (true);
                this.transport.automationWriteMode ().set (mode.getIdentifier ());
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void toggleWriteArrangerAutomation ()
    {
        this.transport.toggleWriteArrangerAutomation ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleWriteClipLauncherAutomation ()
    {
        this.transport.toggleWriteClipLauncherAutomation ();
    }


    /** {@inheritDoc} */
    @Override
    public void resetAutomationOverrides ()
    {
        this.transport.resetAutomationOverrides ();
    }


    /** {@inheritDoc} */
    @Override
    public void returnToArrangement ()
    {
        this.transport.returnToArrangement ();
    }


    /** {@inheritDoc} */
    @Override
    public String getPositionText ()
    {
        return this.transport.getPosition ().getFormatted ( (beatTime, isAbsolute, timeSignatureNumerator, timeSignatureDenominator, timeSignatureTicks) -> StringUtils.formatTimeLong (this.getTempo (), beatTime, true));
    }


    /** {@inheritDoc} */
    @Override
    public String getBeatText ()
    {
        return this.transport.getPosition ().getFormatted ( (beatTime, isAbsolute, timeSignatureNumerator, timeSignatureDenominator, timeSignatureTicks) -> {
            final int quartersPerMeasure = 4 * timeSignatureNumerator / timeSignatureDenominator;
            return StringUtils.formatMeasuresLong (quartersPerMeasure, beatTime, 1, true);
        });
    }


    /** {@inheritDoc} */
    @Override
    public void setPositionToEnd ()
    {
        this.application.invokeAction (ACTION_JUMP_TO_END);

        // Force moving the end of the arranger into view
        this.changePosition (false, true);
        this.changePosition (true, true);
    }


    /** {@inheritDoc} */
    @Override
    public void setPosition (final double beats)
    {
        this.transport.getPosition ().set (beats);
    }


    /** {@inheritDoc} */
    @Override
    public void changePosition (final boolean increase, final boolean slow)
    {
        final double frac = slow ? TransportConstants.INC_FRACTION_TIME_SLOW : TransportConstants.INC_FRACTION_TIME;
        this.transport.getPosition ().inc (increase ? frac : -frac);
    }


    /** {@inheritDoc} */
    @Override
    public void setPunchIn (final boolean enable)
    {
        this.transport.isPunchInEnabled ().set (enable);
    }


    /** {@inheritDoc} */
    @Override
    public void togglePunchIn ()
    {
        this.transport.isPunchInEnabled ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPunchInEnabled ()
    {
        return this.transport.isPunchInEnabled ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setPunchOut (final boolean enable)
    {
        this.transport.isPunchOutEnabled ().set (enable);
    }


    /** {@inheritDoc} */
    @Override
    public void togglePunchOut ()
    {
        this.transport.isPunchOutEnabled ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPunchOutEnabled ()
    {
        return this.transport.isPunchOutEnabled ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getTempoParameter ()
    {
        return this.tempoParameter;
    }


    /** {@inheritDoc} */
    @Override
    public void tapTempo ()
    {
        this.transport.tapTempo ();
    }


    /** {@inheritDoc} */
    @Override
    public void changeTempo (final boolean increase, final boolean slow)
    {
        final double offset = slow ? 0.01 : 1;
        this.tempoParameter.incRawValue (increase ? offset : -offset);
    }


    /** {@inheritDoc} */
    @Override
    public void setTempo (final double tempo)
    {
        this.tempoParameter.setRawValue (tempo);
    }


    /** {@inheritDoc} */
    @Override
    public double getTempo ()
    {
        return this.tempoParameter.getRawValue ();
    }


    /** {@inheritDoc} */
    @Override
    public String formatTempo (final double tempo)
    {
        return new DecimalFormat ("#.00").format (tempo);
    }


    /** {@inheritDoc} */
    @Override
    public String formatTempoNoFraction (final double tempo)
    {
        return new DecimalFormat ("###").format (tempo);
    }


    /** {@inheritDoc} */
    @Override
    public double rescaleTempo (final double tempo, final int maxValue)
    {
        final double v = tempo - TransportConstants.MIN_TEMPO;
        return v * (maxValue - 1) / (TransportConstants.MAX_TEMPO - TransportConstants.MIN_TEMPO);
    }


    /** {@inheritDoc} */
    @Override
    public void setTempoIndication (final boolean isTouched)
    {
        this.tempoParameter.setIndication (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void setCrossfade (final int value)
    {
        this.crossfadeParameter.setValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getCrossfadeParameter ()
    {
        return this.crossfadeParameter;
    }


    /** {@inheritDoc} */
    @Override
    public int getCrossfade ()
    {
        return this.crossfadeParameter.getValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void changeCrossfade (final int control)
    {
        this.crossfadeParameter.inc (this.valueChanger.calcKnobChange (control));
    }


    /** {@inheritDoc} */
    @Override
    public String getPreroll ()
    {
        return this.transport.preRoll ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public int getPrerollAsBars ()
    {
        switch (this.getPreroll ())
        {
            case TransportConstants.PREROLL_NONE:
                return 0;
            case TransportConstants.PREROLL_1_BAR:
                return 1;
            case TransportConstants.PREROLL_2_BARS:
                return 2;
            case TransportConstants.PREROLL_4_BARS:
                return 4;
            default:
                return 0;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setPreroll (final String preroll)
    {
        this.transport.preRoll ().set (preroll);
    }


    /** {@inheritDoc} */
    @Override
    public void setPrerollAsBars (final int preroll)
    {
        switch (preroll)
        {
            case 0:
                this.setPreroll (TransportConstants.PREROLL_NONE);
                break;
            case 1:
                this.setPreroll (TransportConstants.PREROLL_1_BAR);
                break;
            case 2:
                this.setPreroll (TransportConstants.PREROLL_2_BARS);
                break;
            case 4:
                this.setPreroll (TransportConstants.PREROLL_4_BARS);
                break;
            default:
                this.host.errorln ("Unknown Preroll length: " + preroll);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getNumerator ()
    {
        return this.transport.timeSignature ().numerator ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public int getDenominator ()
    {
        return this.transport.timeSignature ().denominator ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public int getQuartersPerMeasure ()
    {
        return 4 * this.getNumerator () / this.getDenominator ();
    }
}