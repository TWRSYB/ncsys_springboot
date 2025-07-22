package com.dc.ncsys_springboot.util;

import com.dc.ncsys_springboot.daoVo.PersonDo;
import com.dc.ncsys_springboot.daoVo.WorkerAttendanceDo;
import com.dc.ncsys_springboot.daoVo.WorkerDo;

public class IdUtils {
    public static void generateIdForObject(Object obj) {
        if (obj instanceof PersonDo person) {
            person.setPersonId("People_" + person.getPhoneNum() + "_" + DateTimeUtil.getMinuteKey());
        } else if (obj instanceof WorkerDo worker) {
            worker.setWorkerId("Worker_" + worker.getPhoneNum() + "_" + DateTimeUtil.getMinuteKey());
        } else if (obj instanceof WorkerAttendanceDo attendance) {
            attendance.setAttendanceId("WorkerAttendance_" + attendance.getDate() + "_" + DateTimeUtil.getMinuteKey());
        } else {
            throw new IllegalArgumentException("Unsupported object type: " + obj.getClass());
        }
    }
}
