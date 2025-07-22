package com.dc.ncsys_springboot.mapper;
import org.apache.ibatis.annotations.Mapper;

import com.dc.ncsys_springboot.daoVo.WorkerAttendanceDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * <p>
 * 工人出工记录表 Mapper 接口
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-20 15:06
 */
@Mapper
public interface WorkerAttendanceMapper extends BaseMapper<WorkerAttendanceDo> {


    List<WorkerAttendanceDo> getAttendanceListByWorkerIdAndYm(@Param("workerId") String workerId, @Param("ym") String ym);
}
