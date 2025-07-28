package com.dc.ncsys_springboot.service.impl;

import com.dc.ncsys_springboot.constants.ComConst;
import com.dc.ncsys_springboot.daoVo.PersonDo;
import com.dc.ncsys_springboot.daoVo.UserDo;
import com.dc.ncsys_springboot.daoVo.WorkerAttendanceDo;
import com.dc.ncsys_springboot.daoVo.WorkerDo;
import com.dc.ncsys_springboot.mapper.PersonMapper;
import com.dc.ncsys_springboot.mapper.WorkerAttendanceMapper;
import com.dc.ncsys_springboot.mapper.WorkerMapper;
import com.dc.ncsys_springboot.service.WorkerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dc.ncsys_springboot.util.IdUtils;
import com.dc.ncsys_springboot.util.SessionUtils;
import com.dc.ncsys_springboot.vo.PageQueryVo;
import com.dc.ncsys_springboot.vo.PageResVo;
import com.dc.ncsys_springboot.vo.ResVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 工人表 服务实现类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-19 11:43
 */
@Service
public class WorkerServiceImpl extends ServiceImpl<WorkerMapper, WorkerDo> implements WorkerService {

    @Autowired
    private WorkerMapper workerMapper;

    @Autowired
    private PersonMapper personMapper;

    @Autowired
    private WorkerAttendanceMapper workerAttendanceMapper;

    @Override
    public PageResVo<WorkerDo> pageQuery(PageQueryVo<WorkerDo> pageQueryVo) {
        PageHelper.startPage(pageQueryVo.getPageNum(), pageQueryVo.getPageSize());
        Page<WorkerDo> workerPage = workerMapper.pageQuery(pageQueryVo.getParams());
        return PageResVo.success(workerPage);
    }

    @Override
    public ResVo<Object> saveWorker(WorkerDo workerDo) {
        // 获取当前登录人
        UserDo sessionUser = SessionUtils.getSessionUser();

        // 检查人员ID是否存在
        PersonDo personDo = personMapper.selectById(workerDo.getPersonId());
        if (personDo == null) {
            return ResVo.fail("人员不存在");
        }
        // 匹配手机号
        if (!personDo.getPhoneNum().equals(workerDo.getPhoneNum())) {
            return ResVo.fail("人员不匹配");
        }

        IdUtils.generateIdForObject(workerDo);
        workerDo.setCreateUser(sessionUser.getLoginCode());
        workerDo.setUpdateUser(sessionUser.getLoginCode());
        workerDo.setDataStatus(ComConst.DATASTATUS_EFFECTIVE);

        // 保存工人信息
        int rows = workerMapper.insert(workerDo);
        if (rows == 1) {
            return ResVo.success("保存成功");
        } else {
            return ResVo.fail("保存失败");
        }

    }

    @Override
    public ResVo<Object> updateWorker(WorkerDo workerDo) {
        // 获取当前登录人
        UserDo sessionUser = SessionUtils.getSessionUser();

        // 查询工人是否存在
        WorkerDo existingWorker = workerMapper.selectById(workerDo.getWorkerId());
        if (existingWorker == null) {
            return ResVo.fail("工人不存在");
        }

        // 匹配人员ID
        if (!existingWorker.getPersonId().equals(workerDo.getPersonId())) {
            return ResVo.fail("人员不匹配");
        }

        // 更新工人信息
        existingWorker.setDailyPay(workerDo.getDailyPay());
        existingWorker.setUpdateUser(sessionUser.getLoginCode());
        existingWorker.setRemark(workerDo.getRemark());

        int rows = workerMapper.updateById(existingWorker);
        if (rows == 1) {
            return ResVo.success("更新成功");
        } else {
            return ResVo.fail("更新失败");
        }

    }

    @Override
    public ResVo<List<WorkerDo>> getWorkerList() {
        // 获取当前登录人
        UserDo sessionUser = SessionUtils.getSessionUser();
        // 如果不是管理员或系统管理员则返回空列表
        if (!sessionUser.getRoleCode().equals(ComConst.ROLE_SYS_ADMIN) && !sessionUser.getRoleCode().equals(ComConst.ROLE_MANAGER)) {
            return ResVo.success(null);
        }
        // 查询所有工人
        List<WorkerDo> workerList = workerMapper.pageQuery(null);
        return ResVo.success("获取工人列表成功", workerList);

    }

    @Override
    public PageResVo<WorkerDo> pageQueryWageSettle(PageQueryVo<WorkerDo> pageQueryVo) {
        PageHelper.startPage(pageQueryVo.getPageNum(), pageQueryVo.getPageSize());
        Page<WorkerDo> workerPage = workerMapper.pageQuery(pageQueryVo.getParams());
        // 遍历工人, 查询薪资结算情况
        for (WorkerDo workerDo : workerPage.getResult()) {
            List<WorkerAttendanceDo> attendanceList = workerAttendanceMapper.getAttendanceListByWorkerId(workerDo.getWorkerId());
            double settleDays = 0.0;
            BigDecimal settlePay = new BigDecimal(0);
            double unSettleDays = 0.0;
            BigDecimal unSettlePay = new BigDecimal(0);
            double totalDays = 0.0;
            BigDecimal totalPay = new BigDecimal(0);

            for (WorkerAttendanceDo workerAttendanceDo : attendanceList) {
                if (workerAttendanceDo.getMorningYn().equals(ComConst.YN_Y)) {
                    totalDays += 0.5;
                    totalPay = totalPay.add(workerAttendanceDo.getMorningPay());
                    if (workerAttendanceDo.getTradeStatus().equals("已结算")) {
                        settleDays += 0.5;
                        settlePay = settlePay.add(workerAttendanceDo.getMorningPay());
                    } else {
                        unSettleDays += 0.5;
                        unSettlePay = unSettlePay.add(workerAttendanceDo.getMorningPay());
                    }
                }
                if (workerAttendanceDo.getAfternoonYn().equals(ComConst.YN_Y)) {
                    totalDays += 0.5;
                    totalPay = totalPay.add(workerAttendanceDo.getAfternoonPay());
                    if (workerAttendanceDo.getTradeStatus().equals("已结算")) {
                        settleDays += 0.5;
                        settlePay = settlePay.add(workerAttendanceDo.getAfternoonPay());
                    } else {
                        unSettleDays += 0.5;
                        unSettlePay = unSettlePay.add(workerAttendanceDo.getAfternoonPay());
                    }
                }

            }
            workerDo.setTotalDays(totalDays);
            workerDo.setTotalPay(totalPay);
            workerDo.setSettleDays(settleDays);
            workerDo.setSettlePay(settlePay);
            workerDo.setUnSettleDays(unSettleDays);
            workerDo.setUnSettlePay(unSettlePay);
        }


        return PageResVo.success(workerPage);
    }
}
