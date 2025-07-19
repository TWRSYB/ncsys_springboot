package com.dc.ncsys_springboot.service.impl;

import com.dc.ncsys_springboot.constants.ComConst;
import com.dc.ncsys_springboot.daoVo.PersonDo;
import com.dc.ncsys_springboot.daoVo.UserDo;
import com.dc.ncsys_springboot.daoVo.WorkerDo;
import com.dc.ncsys_springboot.mapper.PersonMapper;
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
}
