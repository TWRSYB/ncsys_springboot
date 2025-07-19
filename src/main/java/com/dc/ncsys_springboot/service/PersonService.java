package com.dc.ncsys_springboot.service;

import com.dc.ncsys_springboot.daoVo.PersonDo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dc.ncsys_springboot.vo.AddressVo;
import com.dc.ncsys_springboot.vo.PageQueryVo;
import com.dc.ncsys_springboot.vo.PageResVo;
import com.dc.ncsys_springboot.vo.ResVo;

import java.util.List;

/**
 * <p>
 * 人员主表 服务类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-14 15:14
 */
public interface PersonService extends IService<PersonDo> {

    ResVo getPersonList(PersonDo personDo);

    ResVo<Object> updateNameByPhoneNum(PersonDo personDo);

    ResVo<List<PersonDo>> getPersonLike(PersonDo personDo);

    PageResVo<PersonDo> pageQuery(PageQueryVo<PersonDo> pageQueryVo);

    ResVo savePerson(PersonDo personDo);

    ResVo<List<AddressVo>> getPersonAddressList(PersonDo personDo);

    ResVo updatePerson(PersonDo personDo);

    PageResVo<PersonDo> getPersonNotInWorker(PageQueryVo<PersonDo> pageQueryVo);
}
