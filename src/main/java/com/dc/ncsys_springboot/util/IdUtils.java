package com.dc.ncsys_springboot.util;

import com.dc.ncsys_springboot.daoVo.*;

public class IdUtils {
    public static void generateIdForObject(Object obj) {
        if (obj instanceof PersonDo person) {
            person.setPersonId("Person_" + person.getPhoneNum() + "_" + DateTimeUtil.getMinuteKey());
        } else if (obj instanceof WorkerDo worker) {
            worker.setWorkerId("Worker_" + worker.getPhoneNum() + "_" + DateTimeUtil.getMinuteKey());
        } else if (obj instanceof WorkerAttendanceDo attendance) {
            attendance.setAttendanceId("WorkerAttendance_" + attendance.getPhoneNum() + "_" + attendance.getDate() + "_" + DateTimeUtil.getMinuteKey());
        } else if (obj instanceof WorkerPayClearDo clear) {
            clear.setClearId("WorkerPayClear_" + clear.getPhoneNum() + "_" + clear.getClearDate() + "_" + DateTimeUtil.getMinuteKey());
        } else if (obj instanceof CornGrainSellDo sell) {
            sell.setSerno("CornGrainSell_" + sell.getTradeDate() + "_" + DateTimeUtil.getMinuteKey());
        } else if (obj instanceof CornGrainPurchaseDo purchase) {
            purchase.setSerno("CornGrainPurchase_" + purchase.getTradeDate() + "_" + DateTimeUtil.getMinuteKey());
        } else if (obj instanceof CornCobPurchaseDo purchase) {
            purchase.setSerno("CornCobPurchase_" + purchase.getTradeDate() + "_" + DateTimeUtil.getMinuteKey());
        }else if (obj instanceof CompanyDo company) {
            company.setCompanyId("Company_" + company.getCompanyPhoneNum() + "_" + DateTimeUtil.getMinuteKey());
        } else if (obj instanceof CornXinPurchaseDo purchase) {
            purchase.setSerno("CornXinPurchase_" + purchase.getTradeDate() + "_" + DateTimeUtil.getMinuteKey());
        } else if (obj instanceof CornXinSellDo sell) {
            sell.setSerno("CornXinSell_" + sell.getTradeDate() + "_" + DateTimeUtil.getMinuteKey());
        } else {
            throw new IllegalArgumentException("Unsupported object type: " + obj.getClass());
        }
    }
}
