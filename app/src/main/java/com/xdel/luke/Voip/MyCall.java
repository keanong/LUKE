package com.xdel.luke.Voip;

import com.xdel.luke.objClasses.objGlobalMethods;

import org.pjsip.pjsua2.AudioMedia;
import org.pjsip.pjsua2.Call;
import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallMediaInfo;
import org.pjsip.pjsua2.CallMediaInfoVector;
import org.pjsip.pjsua2.Media;
import org.pjsip.pjsua2.OnCallMediaStateParam;
import org.pjsip.pjsua2.OnCallStateParam;
import org.pjsip.pjsua2.StreamInfo;
import org.pjsip.pjsua2.VideoPreview;
import org.pjsip.pjsua2.VideoWindow;
import org.pjsip.pjsua2.pjmedia_type;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsua2;
import org.pjsip.pjsua2.pjsua_call_media_status;

public class MyCall extends Call
{
    public VideoWindow vidWin;
    public VideoPreview vidPrev;

    public MyCall(MyAccount acc, int call_id)
    {
        super(acc, call_id);
        vidWin = null;
    }

    @Override
    public void onCallState(OnCallStateParam prm)
    {
        VoipApp.observer.notifyCallState(this);
        try {
            CallInfo ci = getInfo();
            if (ci.getState() ==
                    pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED)
            {
                this.delete();
            }
        } catch (Exception e) {
            return;
        }
    }

    @Override
    public void onCallMediaState(OnCallMediaStateParam prm)
    {
        CallInfo ci;
        try {
            ci = getInfo();
        } catch (Exception e) {
            return;
        }

        CallMediaInfoVector cmiv = ci.getMedia();

        for (int i = 0; i < cmiv.size(); i++) {
            CallMediaInfo cmi = cmiv.get(i);
            if (cmi.getType() == pjmedia_type.PJMEDIA_TYPE_AUDIO &&
                    (cmi.getStatus() ==
                            pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE ||
                            cmi.getStatus() ==
                                    pjsua_call_media_status.PJSUA_CALL_MEDIA_REMOTE_HOLD))
            {
                try {
                    StreamInfo si = getStreamInfo(i);
                    objGlobalMethods.writeToFile("Codec in use: " + si.getCodecName() + "/" + si.getCodecClockRate(), "", "", "", "");
                } catch (Exception e) {
                    // stats unavailable; not critical
                }

                // unfortunately, on Java too, the returned Media cannot be
                // downcasted to AudioMedia
                Media m = getMedia(i);
                AudioMedia am = AudioMedia.typecastFromMedia(m);
                try {
                    am.adjustRxLevel(1.5f);
                } catch (Exception e) {
                    String s = e.getMessage().toString();
                    objGlobalMethods.writeToFile("Exception at MyCall onCallMediaState adjustRxLevel. Exception: " + s,"","","","");
                }
                // connect ports
                try {
                    VoipApp.ep.audDevManager().getCaptureDevMedia().
                            startTransmit(am);
                    am.startTransmit(VoipApp.ep.audDevManager().
                            getPlaybackDevMedia());
                } catch (Exception e) {
                    continue;
                }
            } else if (cmi.getType() == pjmedia_type.PJMEDIA_TYPE_VIDEO &&
                    cmi.getStatus() ==
                            pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE &&
                    cmi.getVideoIncomingWindowId() != pjsua2.INVALID_ID)
            {
                vidWin = new VideoWindow(cmi.getVideoIncomingWindowId());
                vidPrev = new VideoPreview(cmi.getVideoCapDev());
            }
        }

        VoipApp.observer.notifyCallMediaState(this);
    }
}
