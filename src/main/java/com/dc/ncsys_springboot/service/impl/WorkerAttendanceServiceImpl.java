package com.dc.ncsys_springboot.service.impl;

import com.dc.ncsys_springboot.constants.ComConst;
import com.dc.ncsys_springboot.daoVo.UserDo;
import com.dc.ncsys_springboot.daoVo.WorkerAttendanceDo;
import com.dc.ncsys_springboot.daoVo.WorkerDo;
import com.dc.ncsys_springboot.exception.BusinessException;
import com.dc.ncsys_springboot.mapper.WorkerAttendanceMapper;
import com.dc.ncsys_springboot.mapper.WorkerMapper;
import com.dc.ncsys_springboot.service.WorkerAttendanceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dc.ncsys_springboot.util.DateTimeUtil;
import com.dc.ncsys_springboot.util.FieldUtil;
import com.dc.ncsys_springboot.util.IdUtils;
import com.dc.ncsys_springboot.util.SessionUtils;
import com.dc.ncsys_springboot.vo.ResVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

/**
 * <p>
 * 工人出工记录表 服务实现类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-20 15:06
 */
@Slf4j
@Transactional
@Service
public class WorkerAttendanceServiceImpl extends ServiceImpl<WorkerAttendanceMapper, WorkerAttendanceDo> implements WorkerAttendanceService {

    @Autowired
    private WorkerAttendanceMapper workerAttendanceMapper;

    @Autowired
    private WorkerMapper workerMapper;

    @Override
    public ResVo<List<Map<String, Object>>> getWorkerAndAttendanceList(String ym) {
        // 获取当前登录人
        UserDo sessionUser = SessionUtils.getSessionUser();
        // 如果不是管理员或系统管理员则返回空列表
        if (!sessionUser.getRoleCode().equals(ComConst.ROLE_SYS_ADMIN) && !sessionUser.getRoleCode().equals(ComConst.ROLE_MANAGER)) {
            return ResVo.success(null);
        }

        // 查询所有工人
        List<WorkerDo> workerList = workerMapper.pageQuery(null);

        List<Map<String, Object>> list = new ArrayList<>();
        workerList.forEach(worker -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("workerId", worker.getWorkerId());
            map.put("personName", worker.getPersonName());
            map.put("phoneNum", worker.getPhoneNum());
            map.put("idNum", worker.getIdNum());
            map.put("dailyPay", worker.getDailyPay());
            map.put("address", worker.getAddress());
            // 查询工人的出工记录
            List<WorkerAttendanceDo> attendanceList = workerAttendanceMapper.getAttendanceListByWorkerIdAndYm(worker.getWorkerId(), ym);
            attendanceList.forEach(attendance -> {
                map.put(attendance.getDate(), attendance);
            });
            list.add(map);
        });


        return ResVo.success("获取工人出工数据成功", list);
    }

    @Override
    public ResVo<Object> addWorkerAttendance(WorkerAttendanceDo workerAttendanceDo) {
        // 获取当前登录人
        UserDo sessionUser = SessionUtils.getSessionUser();
        // 入参校验
        if (ObjectUtils.isEmpty(workerAttendanceDo)) {
            throw new BusinessException("添加失败", "入参为空");
        }

        validateWorkerAttendance(workerAttendanceDo);

        // 如果不是管理员或系统管理员则返回失败
        if (!sessionUser.getRoleCode().equals(ComConst.ROLE_SYS_ADMIN) && !sessionUser.getRoleCode().equals(ComConst.ROLE_MANAGER)) {
            throw new BusinessException("添加失败", "无权限添加出工记录");
        }
        // 查询工人是否存在
        WorkerDo worker = workerMapper.selectById(workerAttendanceDo.getWorkerId());
        if (worker == null) {
            throw new BusinessException("添加失败", "工人不存在");
        }
        // 赋值
        IdUtils.generateIdForObject(workerAttendanceDo);
        workerAttendanceDo.setCreateUser(sessionUser.getLoginCode());
        workerAttendanceDo.setUpdateUser(sessionUser.getLoginCode());
        workerAttendanceDo.setDataStatus(ComConst.DATASTATUS_EFFECTIVE);
        if (!ObjectUtils.isEmpty(workerAttendanceDo.getMorningYn()) && !ObjectUtils.isEmpty(workerAttendanceDo.getAfternoonYn())) {
            workerAttendanceDo.setTradeStatus("待结算");
        } else {
            workerAttendanceDo.setTradeStatus("记录中");
        }
        int insert = workerAttendanceMapper.insert(workerAttendanceDo);
        if (insert == 1) {
            return ResVo.success("添加出工记录成功");
        } else {
            throw new BusinessException("添加失败", "添加数量不为1");
        }

    }

    @Override
    public ResVo<Object> updateWorkerAttendance(WorkerAttendanceDo workerAttendanceDo) {
        // 获取当前登录人
        UserDo sessionUser = SessionUtils.getSessionUser();
        // 入参校验
        if (ObjectUtils.isEmpty(workerAttendanceDo)) {
            throw new BusinessException("添加失败", "入参为空");
        }
        validateWorkerAttendance(workerAttendanceDo);
        // 如果不是管理员或系统管理员则返回失败
        if (!sessionUser.getRoleCode().equals(ComConst.ROLE_SYS_ADMIN) &&!sessionUser.getRoleCode().equals(ComConst.ROLE_MANAGER)) {
            throw new BusinessException("添加失败", "无权限修改出工记录");
        }

        // 查询出工记录是否存在
        WorkerAttendanceDo attendance = workerAttendanceMapper.selectById(workerAttendanceDo.getAttendanceId());
        if (attendance == null) {
            throw new BusinessException("添加失败", "出工记录不存在");
        }
        // 如果已经结算, 则返回失败
        if (attendance.getTradeStatus().equals("已结算")) {
            throw new BusinessException("添加失败", "已结算的出工记录不能修改");
        }
        // 匹配工人ID
        if (!attendance.getWorkerId().equals(workerAttendanceDo.getWorkerId())) {
            throw new BusinessException("添加失败", "工人ID不匹配");
        }
        // 匹配日期
        if (!attendance.getDate().equals(workerAttendanceDo.getDate())) {
            throw new BusinessException("添加失败", "日期不匹配");
        }
        // 匹配交易状态
        if (!attendance.getTradeStatus().equals(workerAttendanceDo.getTradeStatus())) {
            throw new BusinessException("添加失败", "交易状态不匹配");
        }
        // 赋值
        workerAttendanceDo.setUpdateUser(sessionUser.getLoginCode());
        if (!ObjectUtils.isEmpty(workerAttendanceDo.getMorningYn()) &&!ObjectUtils.isEmpty(workerAttendanceDo.getAfternoonYn())) {
            workerAttendanceDo.setTradeStatus("待结算");
        } else {
            workerAttendanceDo.setTradeStatus("记录中");
        }
        int update = workerAttendanceMapper.updateById(workerAttendanceDo);
        if (update == 1) {
            return ResVo.success("修改出工记录成功");
        } else {
            throw new BusinessException("修改失败", "修改数量不为1");
        }
    }

    private void validateWorkerAttendance(WorkerAttendanceDo workerAttendanceDo) {
        // 工人ID不能为空
        if (ObjectUtils.isEmpty(workerAttendanceDo.getWorkerId())) {
            throw new BusinessException("添加失败", "工人ID不能为空");
        }
        // 日期不能为空
        if (ObjectUtils.isEmpty(workerAttendanceDo.getDate())) {
            throw new BusinessException("添加失败", "日期不能为空");
        }
        // 日期格式校验
        if (!DateTimeUtil.isDateStr(workerAttendanceDo.getDate())) {
            throw new BusinessException("添加失败", "日期格式错误");
        }
        // 上午出工与否不能为空
        String morningYn = workerAttendanceDo.getMorningYn();
        if (ObjectUtils.isEmpty(morningYn)) {
            throw new BusinessException("添加失败", "上午出工与否不能为空");
        }

        List<String> enumList = FieldUtil.getEnumList("t_worker_attendance", "morning_yn");
        if (enumList != null && !enumList.contains(morningYn)) {
            throw new BusinessException("添加失败", "上午出工与否只能为" + enumList);
        }

        BigDecimal morningPay = workerAttendanceDo.getMorningPay();
        // 如果上午出工, 则上午工钱不能为空
        if (morningYn.equals("Y")) {
            if (morningPay == null || morningPay.compareTo(new BigDecimal(0)) <= 0) {
                throw new BusinessException("添加失败", "上午工钱不能为空");
            }
        } else {
            if (morningPay != null) {
                throw new BusinessException("添加失败", "上午不出工不能有工钱");
            }
        }

        // 下午出工校验
        String afternoonYn = workerAttendanceDo.getAfternoonYn();
        BigDecimal afternoonPay = workerAttendanceDo.getAfternoonPay();
        if (!ObjectUtils.isEmpty(afternoonYn)) {
            List<String> enumList1 = FieldUtil.getEnumList("t_worker_attendance", "afternoon_yn");
            if (enumList1 != null && !enumList1.contains(afternoonYn)) {
                throw new BusinessException("添加失败", "下午出工与否只能为" + enumList1);
            }
            if (afternoonYn.equals("Y")) {
                if (afternoonPay == null || afternoonPay.compareTo(new BigDecimal(0)) <= 0) {
                    throw new BusinessException("添加失败", "下午工钱不能为空");
                }
            } else {
                if (afternoonPay != null) {
                    throw new BusinessException("添加失败", "下午不出工不能有工钱");
                }
            }
            // 当日工钱校验
            BigDecimal dayPay = workerAttendanceDo.getDayPay();
            // 如果上午或下午有出工, 则当日工钱不能为空
            if (morningYn.equals("Y") || afternoonYn.equals("Y")) {
                if (dayPay == null || dayPay.compareTo(new BigDecimal(0)) <= 0) {
                    throw new BusinessException("添加失败", "当日工钱不能为空");
                }
                // 如果上午或下午有出工, 则当日工钱必须等于上午和下午的工钱之和
                BigDecimal totalPay = Stream.of(morningPay, afternoonPay)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (dayPay.compareTo(totalPay) != 0) {
                    throw new BusinessException("添加失败", "当日工钱必须等于上午和下午的工钱之和");
                }
            } else {
                if (dayPay.compareTo(new BigDecimal(0)) != 0) {
                    throw new BusinessException("添加失败", "当日不出工不能有工钱");
                }
            }
        }




    }
}
