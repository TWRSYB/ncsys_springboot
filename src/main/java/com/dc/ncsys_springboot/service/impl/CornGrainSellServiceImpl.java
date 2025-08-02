package com.dc.ncsys_springboot.service.impl;

import com.dc.ncsys_springboot.daoVo.*;
import com.dc.ncsys_springboot.exception.BusinessException;
import com.dc.ncsys_springboot.mapper.*;
import com.dc.ncsys_springboot.service.CornGrainSellService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dc.ncsys_springboot.util.*;
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
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 玉米粒出售表 服务实现类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-31 15:04
 */
@Slf4j
@Transactional
@Service
public class CornGrainSellServiceImpl extends ServiceImpl<CornGrainSellMapper, CornGrainSellDo> implements CornGrainSellService {

    @Autowired
    private CornGrainSellMapper cornGrainSellMapper;
    
    @Autowired
    private PersonMapper personMapper;
    
    @Autowired
    private CompanyMapper companyMapper;
    
    @Autowired
    private CompanyPersonMapper companyPersonMapper;
    
    @Autowired
    private CornGrainSellWeighRecordMapper cornGrainSellWeighRecordMapper;

    @Override
    public PageResVo<CornGrainSellDo> pageQuery(PageQueryVo<CornGrainSellDo> pageQueryVo) {
        PageHelper.startPage(pageQueryVo.getPageNum(), pageQueryVo.getPageSize());
        Page<CornGrainSellDo> page = cornGrainSellMapper.pageQuery(pageQueryVo.getParams());
        return PageResVo.success(page);
    }

    @Override
    public ResVo<Object> saveTrade(MixedCornGrainSellDo mixedCornGrainSellDo) {
        // 获取当前登录用户
        UserDo sessionUserDo = SessionUtils.getSessionUser();
        if (!validateMixedTrade(mixedCornGrainSellDo, "save")) {
            return ResVo.fail("数据校验失败");
        }
        
        // 如果购买人是个人
        if ("个人".equals(mixedCornGrainSellDo.getBuyerType())) {
            // 查询购买人是否已经存在
            PersonDo buyerPerson = mixedCornGrainSellDo.getBuyerPerson();
            PersonDo existingPerson = personMapper.getByPhoneNum(buyerPerson.getPhoneNum());

            if (existingPerson != null) {
                // 如果存在，使用已存在的ID
                if (!existingPerson.getPersonName().equals(buyerPerson.getPersonName())) {
                    return ResVo.fail(555, "手机号码已经绑定姓名为： " + existingPerson.getPersonName() + " 的客户，请检查");
                }
                buyerPerson = existingPerson;

            } else {
                // 如果不存在，插入新记录并获取ID
                IdUtils.generateIdForObject(buyerPerson);
                buyerPerson.setCreateUser(sessionUserDo.getLoginCode());
                buyerPerson.setUpdateUser(sessionUserDo.getLoginCode());
                buyerPerson.setDataStatus("1");
                personMapper.insert(buyerPerson);
            }
            mixedCornGrainSellDo.setBuyerId(buyerPerson.getPersonId());
        }
        
        // 如果购买人是企业
        if ("企业".equals(mixedCornGrainSellDo.getBuyerType())) {
            // 查询购买人是否已经存在
            CompanyDo buyerCompany = mixedCornGrainSellDo.getBuyerCompany();
            CompanyDo existingCompany = companyMapper.getByCompanyPhoneNum(buyerCompany.getCompanyPhoneNum());
            if (existingCompany != null) {
                // 如果存在，使用已存在的ID
                if (!existingCompany.getCompanyName().equals(buyerCompany.getCompanyName())) {
                    return ResVo.fail(666, "电话号码已经绑定企业名称为： " + existingCompany.getCompanyName() + " 的客户，请检查");
                }
                buyerCompany = existingCompany;
            } else {
                // 如果不存在，插入新记录并获取ID
                IdUtils.generateIdForObject(buyerCompany);
                buyerCompany.setCreateUser(sessionUserDo.getLoginCode());
                buyerCompany.setUpdateUser(sessionUserDo.getLoginCode());
                buyerCompany.setDataStatus("1");
            }
            mixedCornGrainSellDo.setBuyerId(buyerCompany.getCompanyId());
            
            // 查询对接人是否已经存在
            String dockPersonName = buyerCompany.getDockPersonName();
            String dockPhoneNum = buyerCompany.getDockPhoneNum();
            PersonDo dockPerson = new PersonDo();
            PersonDo existingDockPerson = personMapper.getByPhoneNum(dockPhoneNum);
            if (existingDockPerson != null) {
                // 如果存在，使用已存在的ID
                if (!existingDockPerson.getPersonName().equals(dockPersonName)) {
                    return ResVo.fail(777, "对接人号码已经绑定姓名为： " + existingDockPerson.getPersonName() + " 的客户，请检查");
                }
                dockPerson = existingDockPerson;
            } else {
                // 如果不存在，插入新记录并获取ID
                dockPerson.setPersonName(dockPersonName);
                dockPerson.setPhoneNum(dockPhoneNum);
                IdUtils.generateIdForObject(dockPerson);
                dockPerson.setCreateUser(sessionUserDo.getLoginCode());
                dockPerson.setUpdateUser(sessionUserDo.getLoginCode());
                dockPerson.setDataStatus("1");
            }
            log.info("对接人ID：{}", dockPerson.getPersonId());
            mixedCornGrainSellDo.setDockPersonId(dockPerson.getPersonId());
            
            // 查询企业人员关系是否存在
            CompanyPersonDo existingCompanyPerson = companyPersonMapper.getByCompanyIdAndPersonId(buyerCompany.getCompanyId(), dockPerson.getPersonId());
            if (existingCompanyPerson == null) {
                // 如果不存在，插入新记录
                CompanyPersonDo companyPerson = new CompanyPersonDo();
                companyPerson.setCompanyId(buyerCompany.getCompanyId());
                companyPerson.setPersonId(dockPerson.getPersonId());
                companyPerson.setCreateUser(sessionUserDo.getLoginCode());
                companyPerson.setUpdateUser(sessionUserDo.getLoginCode());
                companyPerson.setDataStatus("1");
                companyPersonMapper.insert(companyPerson);
            }
            
            if (existingDockPerson == null) {
                personMapper.insert(dockPerson);
            }
            
            if (existingCompany == null) {
                companyMapper.insert(buyerCompany);
            }
        }

        // 插入交易记录
        if (ObjectUtils.isEmpty(mixedCornGrainSellDo.getSerno())) {
            IdUtils.generateIdForObject(mixedCornGrainSellDo);
            mixedCornGrainSellDo.setCreateUser(sessionUserDo.getLoginCode());
        }
        mixedCornGrainSellDo.setUpdateUser(sessionUserDo.getLoginCode());
        mixedCornGrainSellDo.setDataStatus("1");
        mixedCornGrainSellDo.setTradeStatus("待结算");
        boolean insertOrUpdate = cornGrainSellMapper.insertOrUpdate(mixedCornGrainSellDo);
        if (!insertOrUpdate) {
            throw new BusinessException("交易记录插入失败");
        }

        // 删除过磅记录然后重新插入
        int delete = cornGrainSellWeighRecordMapper.deleteByTradeSerno(mixedCornGrainSellDo.getSerno());
        log.info("删除过磅记录数：{}", delete);
        List<CornGrainSellWeighRecordDo> beforeWeighRecordList = mixedCornGrainSellDo.getList_weigh();
        for (int i = 0; i < beforeWeighRecordList.size(); i++) {
            CornGrainSellWeighRecordDo record = beforeWeighRecordList.get(i);
            if (ObjectUtils.isEmpty(record.getWeighId())) {
                record.setWeighId("CornGrainSellWeighRecordDo_" + DateTimeUtil.getMinuteKey() + "_" + (i + 1));
                record.setCreateUser(sessionUserDo.getLoginCode());
            }
            record.setTradeSerno(mixedCornGrainSellDo.getSerno());
            record.setTradeDate(mixedCornGrainSellDo.getTradeDate());
            record.setUpdateUser(sessionUserDo.getLoginCode());
            record.setDataStatus("1");
            cornGrainSellWeighRecordMapper.insert(record);
        }

        return ResVo.success("出售记录保存成功");


    }

    private boolean validateMixedTrade(MixedCornGrainSellDo mixedCornGrainSellDo, String step) {
        // 校验数据
        if (mixedCornGrainSellDo == null) {
            throw new BusinessException("无效的交易数据");
        }

        // 交易日期校验
        String tradeDate = mixedCornGrainSellDo.getTradeDate();
        if (ObjectUtils.isEmpty(tradeDate)) {
            throw new BusinessException("交易日期不能为空");
        }
        // 日期格式校验
        if (!DateTimeUtil.isDateStr(tradeDate)) {
            throw new BusinessException("交易日期格式不正确");
        }

        // 计重方校验
        String weighSide = mixedCornGrainSellDo.getWeighSide();
        if (ObjectUtils.isEmpty(weighSide)) {
            throw new BusinessException("计重方不能为空");
        }
        List<String> weighSideList = FieldUtil.getEnumList("t_corn_grain_sell", "weigh_side");
        if (weighSideList != null && !weighSideList.contains(weighSide)) {
            throw new BusinessException("计重方错误");
        }

        // 购买人类型校验
        String buyerType = mixedCornGrainSellDo.getBuyerType();
        List<String> buyerTypeList = FieldUtil.getEnumList("t_corn_grain_sell", "buyer_type");
        if (ObjectUtils.isEmpty(buyerType)) {
            throw new BusinessException("购买人类型不能为空");
        }
        if (buyerTypeList != null && !buyerTypeList.contains(buyerType)) {
            throw new BusinessException("购买人类型错误");
        }

        // 购买人为企业
        if ("企业".equals(buyerType)) {
            CompanyDo buyerCompany = mixedCornGrainSellDo.getBuyerCompany();
            if (buyerCompany == null) {
                throw new BusinessException("购买人企业信息不能为空");
            }
            // 购买企业名称校验
            String buyerCompanyName = buyerCompany.getCompanyName();
            if (ObjectUtils.isEmpty(buyerCompanyName)) {
                throw new BusinessException("购买企业名称不能为空");
            }
            // 购买企业电话校验
            String buyerCompanyPhoneNum = buyerCompany.getCompanyPhoneNum();
            if (ObjectUtils.isEmpty(buyerCompanyPhoneNum)) {
                throw new BusinessException("购买企业电话不能为空");
            }
            // 购买企业电话格式校验
            if (!RegexUtils.isTel(buyerCompanyPhoneNum)) {
                throw new BusinessException("购买企业电话格式不正确");
            }
            // 对接人校验
            String dockPersonName = buyerCompany.getDockPersonName();
            if (ObjectUtils.isEmpty(dockPersonName)) {
                throw new BusinessException("对接人名称不能为空");
            }
            // 对接人姓名校验
            if (!RegexUtils.isChinese(dockPersonName)) {
                throw new BusinessException("对接人姓名格式不正确");
            }
            // 对接人手机号校验
            String dockPhoneNum = buyerCompany.getDockPhoneNum();
            if (ObjectUtils.isEmpty(dockPhoneNum)) {
                throw new BusinessException("对接人手机号不能为空");
            }
            // 对接人手机号格式校验
            if (!RegexUtils.isMobile(dockPhoneNum)) {
                throw new BusinessException("对接人手机号格式不正确");
            }

        }

        // 购买人为个人
        if ("个人".equals(buyerType)) {
            // 购买人校验
            PersonDo buyerPerson = mixedCornGrainSellDo.getBuyerPerson();
            if (buyerPerson == null) {
                throw new BusinessException("购买人信息不能为空");
            }

            // 购买人名称校验
            String buyerName = buyerPerson.getPersonName();
            if (ObjectUtils.isEmpty(buyerName)) {
                throw new BusinessException("购买人名称不能为空");
            }

            // 购买人手机号校验
            String buyerPhone = buyerPerson.getPhoneNum();
            if (ObjectUtils.isEmpty(buyerPhone)) {
                throw new BusinessException("购买人手机号不能为空");
            }
            // 购买人手机号格式校验
            if (!RegexUtils.isMobile(buyerPhone)) {
                throw new BusinessException("购买人手机号格式不正确");
            }
        }

        BigDecimal totalWeight = mixedCornGrainSellDo.getTotalWeight();
        // 如果不是保存操作
        if (!"save".equals(step)) {

            // 结算方式校验
            String clearingForm = mixedCornGrainSellDo.getClearingForm();
            if (ObjectUtils.isEmpty(clearingForm)) {
                throw new BusinessException("结算方式不能为空");
            }

            // 结算方式校验
            List<String> clearingFormList = FieldUtil.getEnumList("t_corn_grain_sell", "clearing_form");
            if (clearingFormList != null && !clearingFormList.contains(clearingForm)) {
                throw new BusinessException("结算方式错误");
            }

            // 如果时延结
            if ("延结".equals(clearingForm)) {
                // 计划结算日期校验
                String planClearingDate = mixedCornGrainSellDo.getPlanClearingDate();
                if (ObjectUtils.isEmpty(planClearingDate)) {
                    throw new BusinessException("计划结算日期不能为空");
                }
                // 计划结算日期格式校验
                if (!DateTimeUtil.isDateStr(planClearingDate)) {
                    throw new BusinessException("计划结算日期格式不正确");
                }
            } else {
                // 计划结算日期校验
                String planClearingDate = mixedCornGrainSellDo.getPlanClearingDate();
                if (!ObjectUtils.isEmpty(planClearingDate)) {
                    throw new BusinessException("非延结, 不能有计划结算日期");
                }
                mixedCornGrainSellDo.setPlanClearingDate(null);
            }

            // 单价校验
            BigDecimal unitPrice = mixedCornGrainSellDo.getUnitPrice();
            if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("单价必须大于0");
            }
            // 单价范围再0.8~1.9之间
            if (unitPrice.compareTo(new BigDecimal("0.8")) < 0 || unitPrice.compareTo(new BigDecimal("1.9")) > 0) {
                throw new BusinessException("单价范围在0.8~1.9之间");
            }

            // 总重量校验
            if (totalWeight == null || totalWeight.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("总重量必须大于0");
            }

            // 总金额校验
            BigDecimal totalPrice = mixedCornGrainSellDo.getTotalPrice();
            if (totalPrice == null || totalPrice.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("总金额必须大于0");
            }
            // 总金额必须等于单价乘以总重量(转换为斤)
            log.info("总金额: {}", totalPrice);
            log.info("单价: {}", unitPrice);
            log.info("总重量: {}", totalWeight);
            if (totalPrice.compareTo(unitPrice.multiply(totalWeight).multiply(new BigDecimal(2))) != 0) {
                throw new BusinessException("总金额必须 = 单价 x 总重量 x 2");
            }
        }

        // 校验过磅记录
        List<CornGrainSellWeighRecordDo> list_weigh = mixedCornGrainSellDo.getList_weigh();
        // 脱粒前过磅净重求和
        BigDecimal sumNetWeight = BigDecimal.ZERO;
        for (CornGrainSellWeighRecordDo record : list_weigh) {
            BigDecimal grossWeight;
            BigDecimal tareWeight;
            BigDecimal netWeight;
            // 根据计重方获取毛重、皮重、净重
            if ("我方".equals(weighSide)) {
                grossWeight = record.getOurGrossWeight();
                tareWeight = record.getOurTareWeight();
                netWeight = record.getOurNetWeight();
            } else {
                grossWeight = record.getGrossWeight();
                tareWeight = record.getTareWeight();
                netWeight = record.getNetWeight();
            }
            // 毛重必须大于0
            if (grossWeight == null || grossWeight.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("毛重必须大于0");
            }

            // 不是保存
            if (!"save".equals(step)) {
                // 皮重必须大于等于0
                if (tareWeight == null || tareWeight.compareTo(BigDecimal.ZERO) < 0) {
                    throw new BusinessException("皮重必须大于等于0");
                }
                // 净重必须大于0
                if (netWeight == null || netWeight.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new BusinessException("净重必须大于0");
                }
                // 皮重和净重之和必须等于毛重
                if (tareWeight.add(netWeight).compareTo(grossWeight) != 0) {
                    throw new BusinessException("皮重和净重之和必须等于毛重");
                }
                // 非保存时，承运方不能为空
                if (ObjectUtils.isEmpty(record.getCarrier())) {
                    throw new BusinessException("承运方不能为空");
                }
                // 求和净重
                sumNetWeight = sumNetWeight.add(netWeight);
                
            } else {
                // 当皮重存在时
                if (tareWeight != null) {
                    // 净重必须大于等于0
                    if (netWeight == null || netWeight.compareTo(BigDecimal.ZERO) < 0) {
                        throw new BusinessException("净重必须大于等于0");
                    }
                    // 皮重和净重之和必须等于毛重
                    if (tareWeight.add(netWeight).compareTo(grossWeight) != 0) {
                        throw new BusinessException("皮重和净重之和必须等于毛重");
                    }
                } else {
                    // 净重必须不存在
                    if (netWeight != null) {
                        throw new BusinessException("净重必须不存在");
                    }
                }
            }
        }

        // 非保存时，总重必须等于脱粒前过磅净重求和
        if (!"save".equals(step)) {
            // 总重必须等于脱粒前过磅净重求和
            if (totalWeight.compareTo(sumNetWeight) != 0) {
                throw new BusinessException("总重量必须等于脱粒前过磅净重求和");
            }
        }

        return true;

    }




}
