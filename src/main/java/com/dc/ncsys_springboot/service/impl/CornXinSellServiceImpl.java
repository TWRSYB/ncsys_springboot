package com.dc.ncsys_springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dc.ncsys_springboot.daoVo.*;
import com.dc.ncsys_springboot.exception.BusinessException;
import com.dc.ncsys_springboot.mapper.*;
import com.dc.ncsys_springboot.service.CornXinSellService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dc.ncsys_springboot.service.PersonService;
import com.dc.ncsys_springboot.util.*;
import com.dc.ncsys_springboot.vo.PageQueryVo;
import com.dc.ncsys_springboot.vo.PageResVo;
import com.dc.ncsys_springboot.vo.ResVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 玉米芯出售表 服务实现类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-08-06 15:08
 */
@Slf4j
@Transactional
@Service
public class CornXinSellServiceImpl extends ServiceImpl<CornXinSellMapper, CornXinSellDo> implements CornXinSellService {

    @Autowired
    private CornXinSellMapper cornXinSellMapper;
    
    @Autowired
    private CornXinSellWeighRecordMapper cornXinSellWeighRecordMapper;
    
    @Autowired
    private PersonService personService;
    
    @Autowired
    private CompanyMapper companyMapper;
    
    @Autowired
    private CompanyPersonMapper companyPersonMapper;
    
    @Autowired
    private PersonMapper personMapper;
    
    
    @Override
    public PageResVo<CornXinSellDo> pageQuery(PageQueryVo<CornXinSellDo> pageQueryVo) {
        PageHelper.startPage(pageQueryVo.getPageNum(), pageQueryVo.getPageSize());
        Page<CornXinSellDo> page = cornXinSellMapper.pageQuery(pageQueryVo.getParams());
        return PageResVo.success(page);
    }

    @Override
    public ResVo<Object> saveTrade(MixedCornXinSellDo mixedCornXinSellDo) {
        // 获取当前登录用户
        UserDo sessionUserDo = SessionUtils.getSessionUser();
        if (!validateMixedTrade(mixedCornXinSellDo, "save")) {
            return ResVo.fail("数据校验失败");
        }

        ResVo<Object> dealError = dealBuyer(mixedCornXinSellDo, sessionUserDo);
        if (dealError != null) return dealError;

        // 插入交易记录
        if (ObjectUtils.isEmpty(mixedCornXinSellDo.getSerno())) {
            IdUtils.generateIdForObject(mixedCornXinSellDo);
            mixedCornXinSellDo.setCreateUser(sessionUserDo.getLoginCode());
        }
        mixedCornXinSellDo.setUpdateUser(sessionUserDo.getLoginCode());
        mixedCornXinSellDo.setDataStatus("0");
        mixedCornXinSellDo.setTradeStatus("出售中");
        boolean insertOrUpdate = cornXinSellMapper.insertOrUpdate(mixedCornXinSellDo);
        if (!insertOrUpdate) {
            throw new BusinessException("交易记录插入失败");
        }

        // 删除过磅记录然后重新插入
        int delete = cornXinSellWeighRecordMapper.deleteByTradeSerno(mixedCornXinSellDo.getSerno());
        log.info("删除过磅记录数：{}", delete);
        List<CornXinSellWeighRecordDo> beforeWeighRecordList = mixedCornXinSellDo.getList_weigh();
        for (int i = 0; i < beforeWeighRecordList.size(); i++) {
            CornXinSellWeighRecordDo record = beforeWeighRecordList.get(i);
            if (ObjectUtils.isEmpty(record.getWeighId())) {
                record.setWeighId("CornXinSellWeighRecordDo_" + DateTimeUtil.getMinuteKey() + "_" + (i + 1));
                record.setCreateUser(sessionUserDo.getLoginCode());
            }
            record.setTradeSerno(mixedCornXinSellDo.getSerno());
            record.setTradeDate(mixedCornXinSellDo.getTradeDate());
            record.setUpdateUser(sessionUserDo.getLoginCode());
            record.setDataStatus("0");
            cornXinSellWeighRecordMapper.insert(record);
        }

        return ResVo.success("出售记录保存成功");


    }

    @Override
    public ResVo<MixedCornXinSellDo> getTradeDetail(CornXinSellDo cornXinSellDo) {
        if (ObjectUtils.isEmpty(cornXinSellDo.getSerno())) {
            throw new BusinessException("交易流水号不能为空");
        }
        CornXinSellDo existCornXinSell = cornXinSellMapper.selectById(cornXinSellDo);
        if (existCornXinSell == null) {
            throw new BusinessException("交易记录不存在");
        }

        MixedCornXinSellDo mixedCornXinSellDo = new MixedCornXinSellDo();
        BeanUtils.copyProperties(existCornXinSell, mixedCornXinSellDo);

        // 查询购买人信息
        // 如果购买人是个人
        if (existCornXinSell.getBuyerType().equals("个人")) {
            PersonDo buyerPerson = personMapper.selectById(existCornXinSell.getBuyerId());
            if (buyerPerson == null) {
                throw new BusinessException("购买人信息不存在");
            }
            mixedCornXinSellDo.setBuyerPerson(buyerPerson);
        } else {
            // 如果购买人是企业
            CompanyDo buyerCompany = companyMapper.selectById(existCornXinSell.getBuyerId());
            if (buyerCompany == null) {
                throw new BusinessException("购买企业信息不存在");
            }
            // 查询对接人信息
            PersonDo dockPerson = personMapper.selectById(mixedCornXinSellDo.getDockPersonId());
            if (dockPerson == null) {
                throw new BusinessException("对接人信息不存在");
            }
            buyerCompany.setDockPersonId(dockPerson.getPersonId());
            buyerCompany.setDockPersonName(dockPerson.getPersonName());
            buyerCompany.setDockPhoneNum(dockPerson.getPhoneNum());
            mixedCornXinSellDo.setBuyerCompany(buyerCompany);
        }
        // 查询过磅记录
        LambdaQueryWrapper<CornXinSellWeighRecordDo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CornXinSellWeighRecordDo::getTradeSerno, existCornXinSell.getSerno());
        wrapper.orderByAsc(CornXinSellWeighRecordDo::getCreateTime);
        List<CornXinSellWeighRecordDo> weighRecordList = cornXinSellWeighRecordMapper.selectList(wrapper);

        // 组装返回结果
        mixedCornXinSellDo.setList_weigh(weighRecordList);
        return ResVo.success("查询交易详情成功", mixedCornXinSellDo);
    }

    @Override
    public ResVo<Object> sellComplete(MixedCornXinSellDo mixedCornXinSellDo) {
        // 获取当前登录用户
        UserDo sessionUserDo = SessionUtils.getSessionUser();
        if (!validateMixedTrade(mixedCornXinSellDo, "complete")) {
            return ResVo.fail("数据校验失败");
        }

        ResVo<Object> dealError = dealBuyer(mixedCornXinSellDo, sessionUserDo);
        if (dealError != null) return dealError;

        // 插入交易记录
        if (ObjectUtils.isEmpty(mixedCornXinSellDo.getSerno())) {
            IdUtils.generateIdForObject(mixedCornXinSellDo);
            mixedCornXinSellDo.setCreateUser(sessionUserDo.getLoginCode());
        }
        mixedCornXinSellDo.setUpdateUser(sessionUserDo.getLoginCode());
        mixedCornXinSellDo.setDataStatus("1");
        mixedCornXinSellDo.setTradeStatus("待结算");
        boolean insertOrUpdate = cornXinSellMapper.insertOrUpdate(mixedCornXinSellDo);
        if (!insertOrUpdate) {
            throw new BusinessException("交易记录插入失败");
        }

        // 删除过磅记录然后重新插入
        int delete = cornXinSellWeighRecordMapper.deleteByTradeSerno(mixedCornXinSellDo.getSerno());
        log.info("删除过磅记录数：{}", delete);
        List<CornXinSellWeighRecordDo> beforeWeighRecordList = mixedCornXinSellDo.getList_weigh();
        for (int i = 0; i < beforeWeighRecordList.size(); i++) {
            CornXinSellWeighRecordDo record = beforeWeighRecordList.get(i);
            if (ObjectUtils.isEmpty(record.getWeighId())) {
                record.setWeighId("CornXinSellWeighRecordDo_" + DateTimeUtil.getMinuteKey() + "_" + (i + 1));
                record.setCreateUser(sessionUserDo.getLoginCode());
            }
            record.setTradeSerno(mixedCornXinSellDo.getSerno());
            record.setTradeDate(mixedCornXinSellDo.getTradeDate());
            record.setUpdateUser(sessionUserDo.getLoginCode());
            record.setDataStatus("1");
            cornXinSellWeighRecordMapper.insert(record);
        }

        return ResVo.success("出售记录保存成功");
    }

    @Override
    public ResVo<Object> settleTrade(MixedCornXinSellDo mixedCornXinSellDo) {
        // 获取当前登录用户
        UserDo sessionUserDo = SessionUtils.getSessionUser();
        // 校验数据
        if (!validateMixedTrade(mixedCornXinSellDo, "settle")) {
            return ResVo.fail("数据校验失败");
        }

        // 验证购买人存在
        if ("个人".equals(mixedCornXinSellDo.getBuyerType())) {
            PersonDo buyerPerson = personMapper.selectById(mixedCornXinSellDo.getBuyerId());
            if (buyerPerson == null) {
                return ResVo.fail("购买人不存在");
            }
        } else {
            // 如果购买人是企业
            CompanyDo buyerCompany = companyMapper.selectById(mixedCornXinSellDo.getBuyerId());
            if (buyerCompany == null) {
                return ResVo.fail("购买企业不存在");
            }
        }

        // 查询交易记录
        CornXinSellDo cornXinSellDo = cornXinSellMapper.selectById(mixedCornXinSellDo.getSerno());
        if (cornXinSellDo == null) {
            throw new BusinessException("交易记录不存在");
        }
        // 交易状态必须为待结算
        if (!"待结算".equals(cornXinSellDo.getTradeStatus())) {
            throw new BusinessException("交易状态必须为待结算");
        }
        // 数据状态必须为1
        if (!"1".equals(cornXinSellDo.getDataStatus())) {
            throw new BusinessException("数据状态必须为1");
        }

        // 匹配入参总价和数据库数据总价
        if (mixedCornXinSellDo.getTotalPrice().compareTo(cornXinSellDo.getTotalPrice())!= 0) {
            throw new BusinessException("总价不匹配");
        }

        // 更新交易记录(实际结算日期/补价/最终结算金额/备注/交易状态)
        cornXinSellDo.setActualClearingDate(mixedCornXinSellDo.getActualClearingDate());
        cornXinSellDo.setPremium(mixedCornXinSellDo.getPremium());
        cornXinSellDo.setClearingAmount(mixedCornXinSellDo.getClearingAmount());
        cornXinSellDo.setRemark(mixedCornXinSellDo.getRemark());
        cornXinSellDo.setTradeStatus("已结算");
        mixedCornXinSellDo.setUpdateUser(sessionUserDo.getLoginCode());

        int update = cornXinSellMapper.updateById(cornXinSellDo);
        if (update == 0) {
            throw new BusinessException("交易记录更新失败");
        }
        return ResVo.success("交易结算成功");
    }


    private ResVo<Object> dealBuyer(MixedCornXinSellDo mixedCornXinSellDo, UserDo sessionUserDo) {
        // 如果购买人是个人
        if ("个人".equals(mixedCornXinSellDo.getBuyerType())) {
            // 校验或插入购买人
            PersonService.ValidateOrInsertPersonResult validateOrInsertResult = personService.validateOrInsertPerson(mixedCornXinSellDo.getBuyerPerson());
            PersonDo buyerInfo = validateOrInsertResult.personDo();
            if (validateOrInsertResult.validateResult() == 2) {
                return ResVo.fail(555, "手机号码已经绑定姓名为： " + buyerInfo.getPersonName() + " 的客户，请检查").setData(buyerInfo.getPersonName());
            }
            mixedCornXinSellDo.setBuyerId(buyerInfo.getPersonId());
        } else {
            // 如果购买人是企业
            CompanyDo buyerCompany = mixedCornXinSellDo.getBuyerCompany();
            String dockPersonName = buyerCompany.getDockPersonName();
            String dockPhoneNum = buyerCompany.getDockPhoneNum();

            // 查询购买企业是否已经存在
            CompanyDo existingCompany = companyMapper.getByCompanyPhoneNum(buyerCompany.getCompanyPhoneNum());
            if (existingCompany != null) {
                // 如果存在，使用已存在的ID
                if (!existingCompany.getCompanyName().equals(buyerCompany.getCompanyName())) {
                    return ResVo.fail(666, "电话号码已经绑定企业名称为： " + existingCompany.getCompanyName() + " 的客户，请检查").setData(existingCompany.getCompanyName());
                }
                buyerCompany = existingCompany;
            } else {
                // 如果不存在，插入新记录并获取ID
                IdUtils.generateIdForObject(buyerCompany);
                buyerCompany.setCreateUser(sessionUserDo.getLoginCode());
                buyerCompany.setUpdateUser(sessionUserDo.getLoginCode());
                buyerCompany.setDataStatus("1");
                // 此时还不能插入, 因为如果走到下面的777会导致无法回滚
            }
            mixedCornXinSellDo.setBuyerId(buyerCompany.getCompanyId());

            PersonService.ValidateOrInsertPersonResult validateOrInsertResult = personService.validateOrInsertPerson(new PersonDo().setPersonName(dockPersonName).setPhoneNum(dockPhoneNum));
            PersonDo dockPerson = validateOrInsertResult.personDo();
            if (validateOrInsertResult.validateResult() == 2) {
                return ResVo.fail(777, "对接人号码已经绑定姓名为： " + dockPerson.getPersonName() + " 的客户，请检查").setData(dockPerson.getPersonName());
            }
            mixedCornXinSellDo.setDockPersonId(dockPerson.getPersonId());

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

            if (existingCompany == null) {
                companyMapper.insert(buyerCompany);
            }
        }
        return null;
    }

    private boolean validateMixedTrade(MixedCornXinSellDo mixedCornXinSellDo, String step) {
        // 校验数据
        if (mixedCornXinSellDo == null) {
            throw new BusinessException("无效的交易数据");
        }

        // 交易日期校验
        String tradeDate = mixedCornXinSellDo.getTradeDate();
        if (ObjectUtils.isEmpty(tradeDate)) {
            throw new BusinessException("交易日期不能为空");
        }
        // 日期格式校验
        if (!DateTimeUtil.isDateStr(tradeDate)) {
            throw new BusinessException("交易日期格式不正确");
        }

        // 计重方校验
        String weighSide = mixedCornXinSellDo.getWeighSide();
        if (ObjectUtils.isEmpty(weighSide)) {
            throw new BusinessException("计重方不能为空");
        }
        List<String> weighSideList = FieldUtil.getEnumList("t_corn_xin_sell", "weigh_side");
        if (weighSideList != null && !weighSideList.contains(weighSide)) {
            throw new BusinessException("计重方错误");
        }

        // 购买人类型校验
        String buyerType = mixedCornXinSellDo.getBuyerType();
        List<String> buyerTypeList = FieldUtil.getEnumList("t_corn_xin_sell", "buyer_type");
        if (ObjectUtils.isEmpty(buyerType)) {
            throw new BusinessException("购买人类型不能为空");
        }
        if (buyerTypeList != null && !buyerTypeList.contains(buyerType)) {
            throw new BusinessException("购买人类型错误");
        }

        // 购买人为企业
        if ("企业".equals(buyerType)) {
            CompanyDo buyerCompany = mixedCornXinSellDo.getBuyerCompany();
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
            PersonDo buyerPerson = mixedCornXinSellDo.getBuyerPerson();
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

        BigDecimal totalWeight = mixedCornXinSellDo.getTotalWeight();
        // 如果不是保存操作
        if (!"save".equals(step)) {

            // 结算方式校验
            String clearingForm = mixedCornXinSellDo.getClearingForm();
            if (ObjectUtils.isEmpty(clearingForm)) {
                throw new BusinessException("结算方式不能为空");
            }

            // 结算方式校验
            List<String> clearingFormList = FieldUtil.getEnumList("t_corn_xin_sell", "clearing_form");
            if (clearingFormList != null && !clearingFormList.contains(clearingForm)) {
                throw new BusinessException("结算方式错误");
            }

            // 如果时延结
            if ("延结".equals(clearingForm)) {
                // 计划结算日期校验
                String planClearingDate = mixedCornXinSellDo.getPlanClearingDate();
                if (ObjectUtils.isEmpty(planClearingDate)) {
                    throw new BusinessException("计划结算日期不能为空");
                }
                // 计划结算日期格式校验
                if (!DateTimeUtil.isDateStr(planClearingDate)) {
                    throw new BusinessException("计划结算日期格式不正确");
                }
            } else {
                // 计划结算日期校验
                String planClearingDate = mixedCornXinSellDo.getPlanClearingDate();
                if (!ObjectUtils.isEmpty(planClearingDate)) {
                    throw new BusinessException("非延结, 不能有计划结算日期");
                }
                mixedCornXinSellDo.setPlanClearingDate(null);
            }

            // 单价校验
            BigDecimal unitPrice = mixedCornXinSellDo.getUnitPrice();
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
            BigDecimal totalPrice = mixedCornXinSellDo.getTotalPrice();
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

            // 如果是结算
            if ("settle".equals(step)) {
                // 结算金额必须等于总金额+补价
                BigDecimal clearingAmount = mixedCornXinSellDo.getClearingAmount();
                BigDecimal premium = mixedCornXinSellDo.getPremium();
                if (clearingAmount.compareTo(totalPrice.add(premium)) != 0) {
                    throw new BusinessException("结算金额必须等于总金额+补价");
                }
                // 结算日期不能为空
                String actualClearingDate = mixedCornXinSellDo.getActualClearingDate();
                if (ObjectUtils.isEmpty(actualClearingDate)) {
                    throw new BusinessException("结算日期不能为空");
                }
                // 结算日期格式校验
                if (!DateTimeUtil.isDateStr(actualClearingDate)) {
                    throw new BusinessException("结算日期格式不正确");
                }
                // 结算日期不能小于交易日期
                if (DateTimeUtil.parseDate(actualClearingDate).isBefore(DateTimeUtil.parseDate(tradeDate))) {
                    throw new BusinessException("结算日期不能小于交易日期");
                }
            }
        }

        // 校验过磅记录
        List<CornXinSellWeighRecordDo> list_weigh = mixedCornXinSellDo.getList_weigh();
        // 过磅净重求和
        BigDecimal sumNetWeight = BigDecimal.ZERO;
        for (CornXinSellWeighRecordDo record : list_weigh) {
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

        // 非保存时，总重必须等于过磅净重求和
        if (!"save".equals(step)) {
            // 总重必须等于过磅净重求和
            if (totalWeight.compareTo(sumNetWeight) != 0) {
                throw new BusinessException("总重量必须等于过磅净重求和");
            }
        }
        return true;

    }




}
