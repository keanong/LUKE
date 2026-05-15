package com.xdel.luke.objClasses;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdel.luke.JobsHandler2;
import com.xdel.luke.R;

import java.util.ArrayList;

/**
 * Created by kean.ong on 05/05/2017.
 */
public class AddressListNewSQLite_PostItemAdapter  extends ArrayAdapter<AddressListNewSQLite_PostData> {
    private ChildViewHolder ch;
    private Context _context;
    public AddressListNewSQLite_PostItemAdapter(Context context, ArrayList<AddressListNewSQLite_PostData> addresslistnewPostData){
        super(context, 0, addresslistnewPostData);
        this._context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.address_list_row_new, parent, false);
                ch = new ChildViewHolder();
                ch.lblAddressListRowNewAddress = (TextView) convertView.findViewById(R.id.lblAddressListRowNewAddress);
                ch.lblCount = (TextView) convertView.findViewById(R.id.lblCount);
                ch.hfPostal = (TextView) convertView.findViewById(R.id.hfPostal);
                ch.lblAddressListRowNewTiming = (TextView) convertView.findViewById(R.id.lblAddressListRowNewTiming);
                ch.lblLAT = (TextView) convertView.findViewById(R.id.lblLAT);
                ch.lblLNG = (TextView) convertView.findViewById(R.id.lblLNG);
                ch.LL_AddressPostal = (LinearLayout) convertView.findViewById(R.id.LL_AddressPostal);

                //ch.imgL = (ImageView) convertView.findViewById(R.id.AddressListLockimg);
                //ch.imgV = (ImageView) convertView.findViewById(R.id.AddressListDNFimg);
                //ch.imgS = (ImageView) convertView.findViewById(R.id.AddressListReqSignimg);
                //ch.AddressList2wayimg = (ImageView) convertView.findViewById(R.id.AddressList2wayimg);
                //ch.AddressListDOCODimg = (ImageView) convertView.findViewById(R.id.AddressListDOCODimg);
                //ch.AddressListCannotTakePhotoimg = (ImageView) convertView.findViewById(R.id.AddressListCannotTakePhotoimg);
                //ch.AddressListRecOnlyimg = (ImageView) convertView.findViewById(R.id.AddressListRecOnlyimg);
                //ch.AddressListLetterboximg = (ImageView) convertView.findViewById(R.id.AddressListLetterboximg);
                //ch.AddressListUnderDoorimg = (ImageView) convertView.findViewById(R.id.AddressListUnderDoorimg);
                //ch.AddressListGuardimg = (ImageView) convertView.findViewById(R.id.AddressListGuardimg);
                //ch.AddressListnetsimg = (ImageView) convertView.findViewById(R.id.AddressListnetsimg);
                //ch.AddressListccimg = (ImageView) convertView.findViewById(R.id.AddressListccimg);
                //ch.imgExclam = (ImageView) convertView.findViewById(R.id.imgExclam);
                //ch.imgAdhoc = (ImageView) convertView.findViewById(R.id.imgAdhoc);

                ch.lblAddressListcoldimg = (TextView) convertView.findViewById(R.id.lblAddressListcoldimg);
                ch.lblAddressListnetsimg = (TextView) convertView.findViewById(R.id.lblAddressListnetsimg);
                ch.lblAddressListLockimg = (TextView) convertView.findViewById(R.id.lblAddressListLockimg);
                ch.lblAddressListDNFimg = (TextView) convertView.findViewById(R.id.lblAddressListDNFimg);
                ch.lblAddressList2wayimg = (TextView) convertView.findViewById(R.id.lblAddressList2wayimg);
                ch.lblAddressListDOCODimg = (TextView) convertView.findViewById(R.id.lblAddressListDOCODimg);
                ch.lblAddressListccimg = (TextView) convertView.findViewById(R.id.lblAddressListccimg);
                ch.lblAddressListLetterboximg = (TextView) convertView.findViewById(R.id.lblAddressListLetterboximg);
                ch.lblAddressListUnderDoorimg = (TextView) convertView.findViewById(R.id.lblAddressListUnderDoorimg);
                ch.lblAddressListGuardimg = (TextView) convertView.findViewById(R.id.lblAddressListGuardimg);
                ch.lblAddressListDoorstepimg = (TextView) convertView.findViewById(R.id.lblAddressListDoorstepimg);
                ch.lblAddressListContactlessimg = convertView.findViewById(R.id.lblAddressListContactlessimg);
                ch.lblLunchtime = convertView.findViewById(R.id.lblLunchtime);

                convertView.setTag(ch);
            } else {
                ch = (ChildViewHolder) convertView.getTag();
            }

            final AddressListNewSQLite_PostData data = getItem(position);
            /*Integer jobCount = 0;
            try {
                jobCount = Integer.parseInt(data.Count);
            } catch (Exception e) {

            }*/

            ch.lblAddressListRowNewAddress.setText(data.Address);
            ch.lblCount.setText(data.Count);
            ch.lblCount.setTextColor(data.NewlyInsertedCT ? Color.RED : Color.BLACK);
            ch.hfPostal.setText(data.Postal);
            ch.lblLAT.setText(String.valueOf(data.LAT));
            ch.lblLNG.setText(String.valueOf(data.LNG));
            ch.lblAddressListRowNewTiming.setText(!data.TimingStr.equals("") ? data.TimingStr : "");
            ch.lblLunchtime.setVisibility(data.HASLUNCHTIME ? View.VISIBLE : View.GONE);

            Boolean isIntendedRecOnly = data.isIntendedRecOnlyCount != null && data.isIntendedRecOnlyCount > 0 ? true : false;
            Boolean isTotallyCannotSlot = data.isTotallyCannotSlot != null && data.isTotallyCannotSlot > 0 ? true : false;
            Boolean canSlotLB = data.canSlotLBCount != null && data.canSlotLBCount > 0 ? true : false;
            Boolean canSlotDoor = data.canSlotDoorCount != null && data.canSlotDoorCount > 0 ? true : false;
            Boolean canGuard = data.canGuardCount != null && data.canGuardCount > 0 ? true : false;
            Boolean canDoorstep = data.canDoorstepCount != null && data.canDoorstepCount > 0 ? true : false;
            Boolean contactless = data.contactlessCount != null && data.contactlessCount > 0 ? true : false;
            /*Boolean isPickAndSend = data.isPickAndSendCount != null && data.isPickAndSendCount > 0 ? true : false;
            Boolean hasJobChangesCount = data.JobChangesCount != null && data.JobChangesCount > 0 ? true : false;*/


            ch.lblAddressListcoldimg.setOnClickListener(null);
            ch.lblAddressListcoldimg.setText(data.ColdCount > 0 ? _context.getString(R.string.icontxt_cold) : "");
            if (data.ColdCount > 0){
                ch.lblAddressListcoldimg.setOnClickListener(objGlobalMethods.rtnImgV_OnClickListener(18));
            }

            ch.lblAddressListnetsimg.setOnClickListener(null);
            ch.lblAddressListnetsimg.setText(data.ReqNetsCount > 0 && data.isDelivery ? _context.getString(R.string.icontxt_nets) : "");
            if (data.ReqNetsCount > 0 && data.isDelivery){
                ch.lblAddressListnetsimg.setOnClickListener(objGlobalMethods.rtnImgV_OnClickListener(12));
            }

            ch.lblAddressListLockimg.setText(data.OTP != null && data.OTP && data.isDelivery ? _context.getString(R.string.icontxt_otp) : "");
            ch.lblAddressListLockimg.setOnClickListener(null);
            ch.lblAddressListLockimg.setOnClickListener(data.OTP != null && data.OTP && data.isDelivery ? objGlobalMethods.rtnImgV_OnClickListener(1) : null);

            ch.lblAddressListDNFimg.setText(data.DNF != null && data.DNF ? _context.getString(R.string.icontxt_dnf) : "");
            ch.lblAddressListDNFimg.setOnClickListener(null);
            ch.lblAddressListDNFimg.setOnClickListener(data.DNF != null && data.DNF ? objGlobalMethods.rtnImgV_OnClickListener(2) : null);

            ch.lblAddressList2wayimg.setText(data.HasP2 != null && data.HasP2 > 0 ? _context.getString(R.string.icontxt_2way) : "");
            ch.lblAddressList2wayimg.setOnClickListener(null);
            ch.lblAddressList2wayimg.setOnClickListener(data.HasP2 != null && data.HasP2 > 0 ? objGlobalMethods.rtnImgV_OnClickListener(5) : null);

            ch.lblAddressListDOCODimg.setOnClickListener(null);
            if (data.HasTradeIn && data.isDelivery) {
                ch.lblAddressListDOCODimg.setText(_context.getString(R.string.icontxt_tradein));
                ch.lblAddressListDOCODimg.setOnClickListener(data.HasTradeIn && data.isDelivery ? objGlobalMethods.rtnImgV_OnClickListener(4) : null);
            } else {
                ch.lblAddressListDOCODimg.setText("");
            }

            ch.lblAddressListccimg.setOnClickListener(null);
            ch.lblAddressListccimg.setText(data.ReqVisaCount > 0 && data.isDelivery ? _context.getString(R.string.icontxt_visa) : "");
            if (data.ReqVisaCount > 0 && data.isDelivery){
                ch.lblAddressListccimg.setOnClickListener(objGlobalMethods.rtnImgV_OnClickListener(13));
            }

            ch.lblAddressListLetterboximg.setText(!isIntendedRecOnly && !isTotallyCannotSlot && canSlotLB ? _context.getString(R.string.icontxt_mailbox) : "");
            ch.lblAddressListLetterboximg.setOnClickListener(null);
            ch.lblAddressListLetterboximg.setOnClickListener(!isIntendedRecOnly && !isTotallyCannotSlot && canSlotLB ? objGlobalMethods.rtnImgV_OnClickListener(7) : null);

            ch.lblAddressListUnderDoorimg.setText(!isIntendedRecOnly && !isTotallyCannotSlot && canSlotDoor ? _context.getString(R.string.icontxt_door) : "");
            ch.lblAddressListUnderDoorimg.setOnClickListener(null);
            ch.lblAddressListUnderDoorimg.setOnClickListener(!isIntendedRecOnly && !isTotallyCannotSlot && canSlotDoor ? objGlobalMethods.rtnImgV_OnClickListener(8) : null);

            ch.lblAddressListGuardimg.setText(!isIntendedRecOnly && !isTotallyCannotSlot && canGuard ? _context.getString(R.string.icontxt_guard) : "");
            ch.lblAddressListGuardimg.setOnClickListener(null);
            ch.lblAddressListGuardimg.setOnClickListener(!isIntendedRecOnly && !isTotallyCannotSlot && canGuard ? objGlobalMethods.rtnImgV_OnClickListener(9) : null);

            ch.lblAddressListDoorstepimg.setText(!isIntendedRecOnly && !isTotallyCannotSlot && canDoorstep ? _context.getString(R.string.icontxt_doorstep) : "");

            ch.lblAddressListContactlessimg.setText(contactless ? _context.getString(R.string.icontxt_contactless) : "");

            /*ch.imgL.setImageResource(data.OTP != null && data.OTP && data.isDelivery ? R.mipmap.ic_lock : R.mipmap.ic_blank);
            if (!(data.OTP != null && data.OTP && data.isDelivery)) {
                ch.imgL.setImageDrawable(null);
            }
            ch.imgL.setOnClickListener(null);
            ch.imgL.setOnClickListener(data.OTP != null && data.OTP && data.isDelivery ? objGlobalMethods.rtnImgV_OnClickListener(1) : null);*/


            /*ch.imgV.setImageResource(data.DNF != null && data.DNF ? R.mipmap.ic_toggle_star : R.mipmap.ic_blank);
            if (!(data.DNF != null && data.DNF)) {
                ch.imgV.setImageDrawable(null);
            }
            ch.imgV.setOnClickListener(null);
            ch.imgV.setOnClickListener(data.DNF != null && data.DNF ? objGlobalMethods.rtnImgV_OnClickListener(2) : null);*/

            /*ch.imgS.setImageResource(data.SIGNATURE != null && data.SIGNATURE && data.isDelivery ? R.mipmap.ic_reqsignature : R.mipmap.ic_blank);
            if (!(data.SIGNATURE != null && data.SIGNATURE && data.isDelivery)) {
                ch.imgS.setImageDrawable(null);
            }
            ch.imgS.setOnClickListener(null);
            ch.imgS.setOnClickListener(data.SIGNATURE != null && data.SIGNATURE && data.isDelivery ? objGlobalMethods.rtnImgV_OnClickListener(3) : null);*/

            /*ch.AddressListnetsimg.setOnClickListener(null);
            ch.AddressListnetsimg.setImageResource(data.ReqNetsCount > 0 && data.isDelivery ? R.mipmap.ic_action_nets : R.mipmap.ic_blank);
            if (!(data.ReqNetsCount > 0 && data.isDelivery)) {
                ch.AddressListnetsimg.setImageDrawable(null);
            }
            if (data.ReqNetsCount > 0 && data.isDelivery){
                ch.AddressListnetsimg.setOnClickListener(objGlobalMethods.rtnImgV_OnClickListener(12));
            }*/

            /*ch.AddressListccimg.setOnClickListener(null);
            ch.AddressListccimg.setImageResource(data.ReqVisaCount > 0 && data.isDelivery ? R.mipmap.ic_action_cc : R.mipmap.ic_blank);
            if (!(data.ReqVisaCount > 0 && data.isDelivery)) {
                ch.AddressListccimg.setImageDrawable(null);
            }
            if (data.ReqVisaCount > 0 && data.isDelivery){
                ch.AddressListccimg.setOnClickListener(objGlobalMethods.rtnImgV_OnClickListener(13));
            }*/

            /*ch.AddressListDOCODimg.setOnClickListener(null);
            if (data.HasTradeIn && data.isDelivery) {
                ch.AddressListDOCODimg.setImageResource(R.mipmap.ic_action_add_shopping_cart);
                ch.AddressListDOCODimg.setOnClickListener(data.HasTradeIn && data.isDelivery ? objGlobalMethods.rtnImgV_OnClickListener(4) : null);
            } else {
                ch.AddressListDOCODimg.setImageResource(R.mipmap.ic_blank);
                ch.AddressListDOCODimg.setImageDrawable(null);
            }*/

            /*ch.AddressList2wayimg.setImageResource(data.HasP2 != null && data.HasP2 > 0 ? R.mipmap.ic_action_2way : R.mipmap.ic_blank);
            if (!(data.HasP2 != null && data.HasP2 > 0)) {
                ch.AddressList2wayimg.setImageDrawable(null);
            }
            ch.AddressList2wayimg.setOnClickListener(null);
            ch.AddressList2wayimg.setOnClickListener(data.HasP2 != null && data.HasP2 > 0 ? objGlobalMethods.rtnImgV_OnClickListener(5) : null);*/

            if (data.SetlateColor) {
                ch.lblAddressListRowNewTiming.setTextColor(Color.RED);
            }else {
                ch.lblAddressListRowNewAddress.setTextColor(Color.parseColor("#000000"));
                ch.lblAddressListRowNewTiming.setTextColor(Color.parseColor("#0026ff"));
            }

            /*ch.AddressListLetterboximg.setImageResource(!isIntendedRecOnly && !isTotallyCannotSlot && canSlotLB ? R.mipmap.ic_action_letterbox : R.mipmap.ic_blank);
            if (!(!isIntendedRecOnly && !isTotallyCannotSlot && canSlotLB)) {
                ch.AddressListLetterboximg.setImageDrawable(null);
            }
            ch.AddressListLetterboximg.setOnClickListener(null);
            ch.AddressListLetterboximg.setOnClickListener(!isIntendedRecOnly && !isTotallyCannotSlot && canSlotLB ? objGlobalMethods.rtnImgV_OnClickListener(7) : null);*/

            /*ch.AddressListUnderDoorimg.setImageResource(!isIntendedRecOnly && !isTotallyCannotSlot && canSlotDoor ? R.mipmap.ic_action_door : R.mipmap.ic_blank);
            if (!(!isIntendedRecOnly && !isTotallyCannotSlot && canSlotDoor)) {
                ch.AddressListUnderDoorimg.setImageDrawable(null);
            }
            ch.AddressListUnderDoorimg.setOnClickListener(null);
            ch.AddressListUnderDoorimg.setOnClickListener(!isIntendedRecOnly && !isTotallyCannotSlot && canSlotDoor ? objGlobalMethods.rtnImgV_OnClickListener(8) : null);*/

            /*ch.AddressListGuardimg.setImageResource(!isIntendedRecOnly && !isTotallyCannotSlot && canGuard ? R.mipmap.ic_action_guard : R.mipmap.ic_blank);
            if (!(!isIntendedRecOnly && !isTotallyCannotSlot && canGuard)) {
                ch.AddressListGuardimg.setImageDrawable(null);
            }
            ch.AddressListGuardimg.setOnClickListener(null);
            ch.AddressListGuardimg.setOnClickListener(!isIntendedRecOnly && !isTotallyCannotSlot && canGuard ? objGlobalMethods.rtnImgV_OnClickListener(9) : null);*/
            /*ch.AddressListRecOnlyimg.setImageResource(isIntendedRecOnly ? R.mipmap.ic_action_personally : isTotallyCannotSlot ? R.mipmap.ic_action_explicit : R.mipmap.ic_blank);
            if (!isIntendedRecOnly && !isTotallyCannotSlot) {
                ch.AddressListRecOnlyimg.setImageDrawable(null);
            }
            ch.AddressListRecOnlyimg.setOnClickListener(null);
            ch.AddressListRecOnlyimg.setOnClickListener(isIntendedRecOnly ? objGlobalMethods.rtnImgV_OnClickListener(10)
                    : isTotallyCannotSlot ? objGlobalMethods.rtnImgV_OnClickListener(11) : null);*/

            /*ch.imgExclam.setImageResource(hasJobChangesCount ? R.mipmap.ic_action_noti : R.mipmap.ic_blank);
            if (!hasJobChangesCount) {
                ch.imgExclam.setImageDrawable(null);
            }*/
            /*ch.imgAdhoc.setImageResource(isPickAndSend ? R.mipmap.ic_action_adhoc2: R.mipmap.ic_blank);
            if (!isPickAndSend) {
                ch.imgAdhoc.setImageDrawable(null);
            }*/
            //ch.imgExclam.setOnClickListener(null);
            //ch.imgAdhoc.setOnClickListener(null);

            /*if (isPickAndSend) {
                ch.imgAdhoc.setOnClickListener(jobCount == 0 ? null :objGlobalMethods.rtnImgV_OnClickListener(jobCount > 1 ? 14 : 15));
            }*/

            /*if (hasJobChangesCount) {
                ch.imgExclam.setOnClickListener(jobCount == 0 ? null :objGlobalMethods.rtnImgV_OnClickListener(jobCount > 1 ? 16 : 17));
            }*/

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at AddressListNewSQLite_PostItemAdapter. Exception: " + e.getMessage().toString(),"","","","");
        }

        return convertView;
    }

    public void manageBlinkRedEffect(View v1, View v2, boolean start) {
        try {
            ObjectAnimator anim = ObjectAnimator.ofInt(v1, "backgroundColor", Color.WHITE, Color.RED, Color.WHITE);
            anim.setDuration(500);
            anim.setEvaluator(new ArgbEvaluator());
            anim.setRepeatMode(ValueAnimator.REVERSE);
            anim.setRepeatCount(Animation.INFINITE);

            ObjectAnimator anim2 = ObjectAnimator.ofInt(v2, "backgroundColor", Color.WHITE, Color.RED, Color.WHITE);
            anim2.setDuration(500);
            anim2.setEvaluator(new ArgbEvaluator());
            anim2.setRepeatMode(ValueAnimator.REVERSE);
            anim2.setRepeatCount(Animation.INFINITE);

            anim.start();
            anim2.start();

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at objGlobalMethods manageBlinkEffect. Exception: " + e.getMessage(),"","","","");
        }
    }

    public void manageBlinkEffect(View v, boolean start) {
        try {
            Animation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(500); //You can manage the time of the blink with this parameter
            anim.setStartOffset(20);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(Animation.INFINITE);
            if (start) {
                v.startAnimation(anim);
            } else {
                v.clearAnimation(); // cancel blink animation
                v.setAlpha(1.0f); // restore original alpha
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at objGlobalMethods manageBlinkEffect. Exception: " + e.getMessage(),"","","","");
        }
    }

    public final class ChildViewHolder {
        TextView lblAddressListRowNewAddress;
        TextView lblCount;
        TextView hfPostal;
        TextView hfJobsInfoArrJsonStr;
        TextView lblAddressListRowNewTiming;
        TextView lblLAT;
        TextView lblLNG;
        TextView lblLunchtime;

        TextView lblAddressListcoldimg;
        TextView lblAddressListnetsimg;
        TextView lblAddressListLockimg;
        TextView lblAddressListDNFimg;
        TextView lblAddressList2wayimg;
        TextView lblAddressListDOCODimg;
        TextView lblAddressListccimg;
        TextView lblAddressListLetterboximg;
        TextView lblAddressListUnderDoorimg;
        TextView lblAddressListGuardimg;
        TextView lblAddressListDoorstepimg;
        TextView lblAddressListContactlessimg;

        LinearLayout LL_AddressPostal;

        //ImageView imgL;
        //ImageView imgV;
        //ImageView imgS;
        //ImageView AddressList2wayimg;
        //ImageView AddressListDOCODimg;
        //ImageView AddressListCannotTakePhotoimg;
        //ImageView AddressListRecOnlyimg;
        //ImageView AddressListLetterboximg;
        //ImageView AddressListUnderDoorimg;
        //ImageView AddressListGuardimg;
        //ImageView AddressListnetsimg;
        //ImageView AddressListccimg;
        //ImageView imgExclam;
        //ImageView imgAdhoc;
    }
}
