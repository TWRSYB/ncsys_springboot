package com.dc.ncsys_springboot.service.impl;

import com.dc.ncsys_springboot.constants.ComConst;
import com.dc.ncsys_springboot.daoVo.*;
import com.dc.ncsys_springboot.exception.BusinessException;
import com.dc.ncsys_springboot.mapper.WorkerAttendanceMapper;
import com.dc.ncsys_springboot.mapper.WorkerPayClearMapper;
import com.dc.ncsys_springboot.service.WorkerPayClearService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dc.ncsys_springboot.util.DateTimeUtil;
import com.dc.ncsys_springboot.util.IdUtils;
import com.dc.ncsys_springboot.util.SessionUtils;
import com.dc.ncsys_springboot.vo.PageQueryVo;
import com.dc.ncsys_springboot.vo.PageResVo;
import com.dc.ncsys_springboot.vo.ResVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 工人工钱结算表 服务实现类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-28 08:57
 */
@Slf4j
@Transactional
@Service
public class WorkerPayClearServiceImpl extends ServiceImpl<WorkerPayClearMapper, WorkerPayClearDo> implements WorkerPayClearService {

    @Autowired
    private WorkerPayClearMapper workerPayClearMapper;
    @Autowired
    private WorkerAttendanceMapper workerAttendanceMapper;

    @Override
    public PageResVo<WorkerPayClearDo> pageQuery(PageQueryVo<WorkerPayClearDo> pageQueryVo) {
        PageHelper.startPage(pageQueryVo.getPageNum(), pageQueryVo.getPageSize());
        Page<WorkerPayClearDo> page = workerPayClearMapper.pageQuery(pageQueryVo.getParams());
        return PageResVo.success(page);
    }

    @Override
    public ResVo<Object> clear(MixedWorkerPayClearVo mixedWorkerPayClearVo) {
        // 1. 获取当前登录人
        UserDo sessionUser = SessionUtils.getSessionUser();
        // 2. 校验参数
        validateMixedWorkerPayClearVo(mixedWorkerPayClearVo);
        // 3. 保存结算信息
        WorkerPayClearDo settle = mixedWorkerPayClearVo.getSettle();
        IdUtils.generateIdForObject(settle);
        settle.setCreateUser(sessionUser.getLoginCode());
        settle.setUpdateUser(sessionUser.getLoginCode());
        settle.setDataStatus(ComConst.DATASTATUS_EFFECTIVE);
        workerPayClearMapper.insert(settle);
        // 4. 更新工人出工记录表
        List<WorkerAttendanceDo> listAttendance = settle.getList_settleAttendance();
        for (WorkerAttendanceDo workerAttendanceDo : listAttendance) {
            // 查询并匹配工人出工记录
            WorkerAttendanceDo attendance = workerAttendanceMapper.selectById(workerAttendanceDo.getAttendanceId());
            if (attendance == null) {
                throw new BusinessException("校验失败", "工人出工记录不存在");
            }
            // 状态必须是待结算
            if (!"待结算".equals(attendance.getTradeStatus())) {
                throw new BusinessException("校验失败", "工人出工记录状态必须是待结算");
            }
            // workerId必须匹配
            if (!attendance.getWorkerId().equals(settle.getWorkerId())) {
                throw new BusinessException("校验失败", "工人出工记录工人ID不匹配");
            }
            // 数据状态必须是1
            if (!ComConst.DATASTATUS_EFFECTIVE.equals(attendance.getDataStatus())) {
                throw new BusinessException("校验失败", "工人出工记录数据状态必须是1");
            }
            // 金额匹配
            if (attendance.getDayPay().compareTo(workerAttendanceDo.getDayPay()) != 0) {
                throw new BusinessException("校验失败", "出工记录金额不匹配");
            }
            // 出工日期匹配
            if (!attendance.getDate().equals(workerAttendanceDo.getDate())) {
                throw new BusinessException("校验失败", "出工记录日期不匹配");
            }
            // 更新出工记录状态
            attendance.setTradeStatus("已结算");
            attendance.setUpdateUser(sessionUser.getLoginCode());
            attendance.setClearId(settle.getClearId());
            attendance.setClearDate(settle.getClearDate());
            int i = workerAttendanceMapper.updateById(attendance);
            if (i != 1) {
                throw new BusinessException("校验失败", "出工记录更新失败");
            }
        }
        return ResVo.success("提交结算成功");
    }

    private void validateMixedWorkerPayClearVo(MixedWorkerPayClearVo mixedWorkerPayClearVo) {
        if (mixedWorkerPayClearVo == null) {
            throw new BusinessException("校验失败", "参数不能为空");
        }
        WorkerDo worker = mixedWorkerPayClearVo.getWorker();
        if (worker == null) {
            throw new BusinessException("校验失败", "工人不能为空");
        }
        validateWorker(worker);
        WorkerPayClearDo settle = mixedWorkerPayClearVo.getSettle();
        if (settle == null) {
            throw new BusinessException("校验失败", "结算不能为空");
        }

        String workerId = settle.getWorkerId();
        if (!worker.getWorkerId().equals(workerId)) {
            throw new BusinessException("校验失败", "工人ID不匹配");
        }

        // 结算日期必须是今天
        String clearDate = settle.getClearDate();
        if (!DateTimeUtil.getYMD().equals(clearDate)) {
            throw new BusinessException("校验失败", "结算日期必须是今天");
        }

        String involveDayList = settle.getInvolveDayList();
        if (ObjectUtils.isEmpty(involveDayList)) {
            throw new BusinessException("校验失败", "涉及日期列表不能为空");
        }

        Double involveDays = settle.getInvolveDays();
        if (involveDays <= 0) {
            throw new BusinessException("校验失败", "涉及天数必须大于0");
        }
        // 涉及金额必须大于等于0
        BigDecimal involvePay = settle.getInvolvePay();
        if (involvePay.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("校验失败", "涉及金额必须大于等于0");
        }

        // 补价不能超过涉及金额的20%
        BigDecimal premium = settle.getPremium();
        if (premium.compareTo(involvePay.multiply(new BigDecimal("0.2"))) > 0) {
            throw new BusinessException("校验失败", "补价不能超过涉及金额的20%");
        }

        BigDecimal clearingAmount = settle.getClearingAmount();
        if (clearingAmount.compareTo(involvePay.add(premium)) != 0) {
            throw new BusinessException("校验失败", "结算金额必须等于涉及金额加补价");
        }

        List<String> listSettleYmd = settle.getList_settleYmd();
        String settleYmd = String.join(",", listSettleYmd);
        if (!settleYmd.equals(involveDayList)) {
            throw new BusinessException("校验失败", "涉及日期列表与结算日期列表不一致");
        }

        // 要结算的出工列表
        List<WorkerAttendanceDo> listSettleAttendance = settle.getList_settleAttendance();
        if (listSettleAttendance == null || listSettleAttendance.isEmpty()) {
            throw new BusinessException("校验失败", "要结算的出工列表不能为空");
        }
        // 结算日期列表与出工列表中的日期数量必须一致
        if (listSettleYmd.size() != listSettleAttendance.size()) {
            throw new BusinessException("校验失败", "结算日期列表与出工列表中的日期数量必须一致");
        }
        // 校验出工列表中的日期是否与结算日期列表一致
        for (WorkerAttendanceDo attendance : listSettleAttendance) {
            if (!listSettleYmd.contains(attendance.getDate())) {
                throw new BusinessException("校验失败", "出工列表中的日期必须与结算日期列表一致");
            }
        }
        // 校验出工列表中的工人ID是否与结算的工人ID一致
        for (WorkerAttendanceDo attendance : listSettleAttendance) {
            if (!workerId.equals(attendance.getWorkerId())) {
                throw new BusinessException("校验失败", "出工列表中的工人ID必须与结算的工人ID一致");
            }
        }
        // 出工数据的状态必须是待结算
        for (WorkerAttendanceDo attendance : listSettleAttendance) {
            if (!"待结算".equals(attendance.getTradeStatus())) {
                throw new BusinessException("校验失败", "出工数据的状态必须是待结算");
            }
        }
        // 出工列表中的出工金额之和必须与涉及金额一致
        BigDecimal sum = BigDecimal.ZERO;
        for (WorkerAttendanceDo attendance : listSettleAttendance) {
            sum = sum.add(attendance.getDayPay());
        }
        if (sum.compareTo(involvePay) != 0) {
            throw new BusinessException("校验失败", "出工列表中的出工金额之和必须与涉及金额一致");
        }


    }

    private void validateWorker(WorkerDo worker) {
        // 工人ID不能为空
        String workerId = worker.getWorkerId();
        if (ObjectUtils.isEmpty(workerId)) {
            throw new BusinessException("校验失败", "工人ID为空");
        }
        // 未结算天数必须大于0
        Double unSettleDays = worker.getUnSettleDays();
        if (unSettleDays <= 0) {
            throw new BusinessException("校验失败", "未结算天数必须大于0");
        }
        // 未结算金额必须大于等于0
        BigDecimal unSettlePay = worker.getUnSettlePay();
        if (unSettlePay.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("校验失败", "未结算金额必须大于等于0");
        }
    }
}
