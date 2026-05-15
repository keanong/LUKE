package com.xdel.luke.FixedChecklist;

import android.content.Context;

import com.xdel.luke.JobsHandler;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.List;

public class fixedChecklists {
    ////PS PickSuccess
    ////PF PickFail
    ////DS DeliverySuccess
    ////DF DeliverFail
    ////CS CollectionSuccess
    ////CF CollectionFail
    ////RS ReturnSuccess
    ////RF ReturnFail

    public String[] LockerOKPChecklist = new String[]{"0.DS.PH.MD|TAKE PHOTO OF SEALED SHIPMENT(S) PLACED INSIDE THE LOCKER CABINET.[ZH]将放置在储物柜内, 处于密封状态的货物, 拍摄一张清晰的照片."};
    public String[] LockerPUChecklist = new String[]{"0.DS.PH.MD|TAKE PHOTO OF SHIPMENT(S) COLLECTED.[ZH]将已取货物放, 拍摄一张清晰的照片."};
    public String[] LockerOKPreceiverChecklist = new String[]{"0.DS.PH.MD|TAKE PHOTO OF SEALED SHIPMENT(S) SHOWING DELIVERY UNIT NUMBER (RESIDENTIAL), GENERAL VIEW WITHIN OFFICE PREMISE (COMPANY).[ZH]将处于密封状态的货物, 附带门牌号码 (住宅) 或 在办公室内的景象(公司), 拍摄一张清晰的照片."};

    public String colditem_checklist_item = "0.DS.PH.MD|TAKE PHOTO OF SHIPMENT(S) PLACED INSIDE COOLER BAG/BOX.[ZH]将放置在冷藏袋子或盒子内的货物, 拍摄一张清晰的照片.";
    public String templogger_checklist_item = "0.DS.PH.MD|TAKE 1 PHOTO OF THE TEMPERATURE LOGGER.\n(MUST SHOW TEMPERATURE)[ZH]拍摄 1 张温度记录仪照片.\n（必须显示温度)";

    public String coldnoncold_item_01 = "0.DS.PH.MD|TAKE 1 PHOTO OF COOLER BAG/BOX & NON-COLD SHIPMENT SHOWING DELIVERY UNIT NUMBER (RESIDENTIAL), GENERAL VIEW WITHIN OFFICE PREMISE (COMPANY).[ZH]将冷藏袋或盒子和非冷藏货物, 附带门牌号码 (住宅) 或 在办公室内的景象(公司), 拍摄一张清晰的照片.";
    public String coldnoncold_item_02 = "0.DS.PH.MD|TAKE 1 PHOTO OF COLD SHIPMENT WITH TEMPERATURE LOGGER.\n(MUST SHOW TEMPERATURE)[ZH]拍摄 1 张冷藏货物和温度记录仪的照片.\n必须显示温度)";

    public String coldonly_item_01 = "0.DS.PH.MD|TAKE 1 PHOTO OF COOLER BAG/BOX SHOWING DELIVERY UNIT NUMBER (RESIDENTIAL), GENERAL VIEW WITHIN OFFICE PREMISE (COMPANY).[ZH]将冷藏袋或盒子, 附带门牌号码 (住宅) 或 在办公室内的景象(公司), 拍摄一张清晰的照片.";;
    public String coldonly_item_02 = "0.DS.PH.MD|TAKE 1 PHOTO OF COLD SHIPMENT WITH TEMPERATURE LOGGER.\n(MUST SHOW TEMPERATURE)[ZH]拍摄 1 张冷藏货物和温度记录仪的照片.\n必须显示温度)";

    public String verify_imei_text = "0.DS.V2.MD|PLEASE VERIFY THE IMEI NUMBER BY ENTERING THE LAST 7 DIGITS.[ZH]请输入最后 7 个号码来验证 IMEI 号码.";
    public String verify_sn_text = "0.DS.V3.MD|PLEASE VERIFY THE SERIAL NUMBER BY ENTERING THE LAST 4 CHARACTERS.[ZH]请输入最后 4 个字来验证编号.";
    public String verify_voucher_text = "0.DS.V4.MD|PLEASE VERIFY THE VOUCHER CODE BY ENTERING THE LAST 4 CHARACTERS.[ZH]请输入最后 4 个字来验证优惠券.";

    public String tempreading_item = "0.DS.TL.MD|READING FROM THE TEMPERATURE LOGGER.[ZH]温度记录器的温度读数.";

    public String vc_provided_lb_item = "0.DS.YN.MD|Voucher provided and verified?[ZH]优惠券已提供并已验证?";
    public String vc_type_sighted = "1.DS.RG.MD|Type of voucher sighted[ZH]优惠券类型|Physical VOUCHER (Must collect)[ZH]实体优惠券 (必须拿)|Digital VOUCHER[ZH]数码优惠券";
    public String vc_not_verified_reason_rg_item = "-1.DS.RG.MD|Select reason voucher not verified.[ZH]选择优惠券未验证的原因.|Unable to provide voucher[ZH]无法提供优惠券|Wrong voucher provided[ZH]提供错的优惠券";

    public String[] getLockerOKPChecklist() {
        return LockerOKPChecklist;
    }

    public String[] getLockerPUChecklist() {
        return LockerPUChecklist;
    }

    public String[] getLockerOKPreceiverChecklist() {
        return LockerOKPreceiverChecklist;
    }

    public String[] getColdNonCold_list() {
        return new String[]{coldnoncold_item_01, coldnoncold_item_02};
    }

    public String[] getCold_list() {
        return new String[]{coldonly_item_01, coldonly_item_02};
    }

    public boolean clitemExist(List<String> CheckListList, String item) {
        boolean asd = false;
        try {
            if (CheckListList != null && CheckListList.size() > 0) {
                String c = "";
                Context cx = myApp.getAppContext();
                for (int i = 0; i <= CheckListList.size() -1; i++) {
                    c = CheckListList.get(i);
                    if (objGlobalMethods.IsEqualString(c, cx.getString(R.string.residential_photo_checklist_item)) ||
                            objGlobalMethods.IsEqualString(c, cx.getString(R.string.residential_photo_checklist_item_alps))) {
                        CheckListList.remove(i);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at fixedChecklists clitemExist. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return asd;
        }
    }

    public List<String> rtnEditedChecklist(List<String> CheckListList) {
        try {
            if (CheckListList != null && CheckListList.size() > 0) {
                String c = "";
                Context cx = myApp.getAppContext();
                for (int i = 0; i <= CheckListList.size() -1; i++) {
                    c = CheckListList.get(i);
                    if (objGlobalMethods.IsEqualString(c, cx.getString(R.string.residential_photo_checklist_item)) ||
                            objGlobalMethods.IsEqualString(c, cx.getString(R.string.residential_photo_checklist_item_alps))) {
                        CheckListList.remove(i);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at fixedChecklists rtnEditedChecklist. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return CheckListList;
        }
    }

    public List<String> rtnEditedChecklistTempReader(List<String> CheckListList) {
        try {
            boolean removed = false;
            if (CheckListList != null && CheckListList.size() > 0) {
                String c = "";
                Context cx = myApp.getAppContext();
                for (int i = 0; i <= CheckListList.size() -1; i++) {
                    c = CheckListList.get(i);
                    if (objGlobalMethods.IsEqualString(c, cx.getString(R.string.tl_checklist_item0))) {
                        CheckListList.remove(i);
                        removed = true;
                        break;
                    }
                }

                if (removed)
                    CheckListList.add(0, cx.getString(R.string.tl_checklist_item0));
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at fixedChecklists rtnEditedChecklistTempReader. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return CheckListList;
        }
    }

    public List<String> rtnEditedChecklistIFAddedTempReader(List<String> CheckListList) {
        try {
            boolean exist = false;
            if (CheckListList != null && CheckListList.size() > 0) {
                String c = "";
                Context cx = myApp.getAppContext();
                for (int i = 0; i <= CheckListList.size() -1; i++) {
                    c = CheckListList.get(i);
                    if (objGlobalMethods.IsEqualString(c, cx.getString(R.string.tl_checklist_item0))) {
                        exist = true;
                        break;
                    }
                }

                if (!exist)
                    CheckListList.add(0, cx.getString(R.string.tl_checklist_item0));
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at fixedChecklists rtnEditedChecklistIFAddedTempReader. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return CheckListList;
        }
    }

    public List<String> rtnEditedChecklistForV2V3V4Existent(List<String> CheckListList, boolean v2, boolean v3, boolean v4) {
        int index = 0;
        String[] codesArr = null;
        String codes = "";
        String ControlCode = "";
        try {
            boolean exist = false;
            if (CheckListList != null && CheckListList.size() > 0) {
                String c = "";
                Context cx = myApp.getAppContext();
                for (int i = CheckListList.size() -1; i >= 0; i--) {
                    c = CheckListList.get(i);
                    index = c.indexOf("|");
                    if (index >= 0) {
                        codes = c.substring(0, index);
                        if (codes != "") {
                            codesArr = codes.split("\\.");
                            ControlCode = codesArr[2];
                            if (ControlCode.equals("V2") && !v2) {
                                CheckListList.remove(i);
                            } else if (ControlCode.equals("V3") && !v3) {
                                CheckListList.remove(i);
                            } else if (ControlCode.equals("V4") && !v4) {
                                CheckListList.remove(i);
                            }
                        }

                        if (!v4 && objGlobalMethods.IsEqualString(c, vc_provided_lb_item))
                            CheckListList.remove(i);

                        if (!v4 && objGlobalMethods.IsEqualString(c, vc_not_verified_reason_rg_item))
                            CheckListList.remove(i);

                        if (!v4 && objGlobalMethods.IsEqualString(c, vc_type_sighted))
                            CheckListList.remove(i);
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at fixedChecklists rtnEditedChecklistForV2V3V4Existent. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return CheckListList;
        }
    }

}
