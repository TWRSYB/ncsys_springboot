package com.dc.ncsys_springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dc.ncsys_springboot.daoVo.*;
import com.dc.ncsys_springboot.exception.BusinessException;
import com.dc.ncsys_springboot.mapper.CornXinPurchaseMapper;
import com.dc.ncsys_springboot.mapper.CornXinPurchaseWeighRecordMapper;
import com.dc.ncsys_springboot.mapper.PersonMapper;
import com.dc.ncsys_springboot.service.CornXinPurchaseService;
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
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 玉米芯收购表 服务实现类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-08-05 08:55
 */
@Slf4j
@Transactional
@Service
public class CornXinPurchaseServiceImpl extends ServiceImpl<CornXinPurchaseMapper, CornXinPurchaseDo> implements CornXinPurchaseService {

    @Autowired
    private CornXinPurchaseMapper cornXinPurchaseMapper;
    
    @Autowired
    private PersonMapper personMapper;

    @Autowired
    private PersonService personService;
    
    @Autowired
    private CornXinPurchaseWeighRecordMapper cornXinPurchaseWeighRecordMapper;

    @Override
    public PageResVo<CornXinPurchaseDo> pageQuery(PageQueryVo<CornXinPurchaseDo> pageQueryVo) {
        PageHelper.startPage(pageQueryVo.getPageNum(), pageQueryVo.getPageSize());
        Page<CornXinPurchaseDo> page = cornXinPurchaseMapper.pageQuery(pageQueryVo.getParams());
        return PageResVo.success(page);

    }

    @Override
    public ResVo<Object> saveTrade(MixedCornXinPurchaseDo mixedCornXinPurchaseDo) {
        // 获取当前登录用户
        UserDo sessionUserDo = SessionUtils.getSessionUser();
        if (!validateMixedTrade(mixedCornXinPurchaseDo, "save")) {
            return ResVo.fail("数据校验失败");
        }

        // 校验或插入出售人
        PersonService.ValidateOrInsertPersonResult validateOrInsertResult = personService.validateOrInsertPerson(mixedCornXinPurchaseDo.getSellerInfo());
        PersonDo sellerInfo = validateOrInsertResult.personDo();
        if (validateOrInsertResult.validateResult() == 2) {
            return ResVo.fail(555, "手机号码已经绑定姓名为： " + sellerInfo.getPersonName() + " 的客户，请检查").setData(sellerInfo.getPersonName());
        }
        mixedCornXinPurchaseDo.setSellerId(sellerInfo.getPersonId());

        // 插入交易记录
        if (ObjectUtils.isEmpty(mixedCornXinPurchaseDo.getSerno())) {
            IdUtils.generateIdForObject(mixedCornXinPurchaseDo);
            mixedCornXinPurchaseDo.setCreateUser(sessionUserDo.getLoginCode());
        }
        mixedCornXinPurchaseDo.setUpdateUser(sessionUserDo.getLoginCode());
        mixedCornXinPurchaseDo.setDataStatus("0");
        mixedCornXinPurchaseDo.setTradeStatus("收购中");
        boolean insertOrUpdate = cornXinPurchaseMapper.insertOrUpdate(mixedCornXinPurchaseDo);
        if (!insertOrUpdate) {
            throw new BusinessException("交易记录插入失败");
        }

        // 删除过磅记录然后重新插入
        int delete = cornXinPurchaseWeighRecordMapper.deleteByTradeSerno(mixedCornXinPurchaseDo.getSerno());
        log.info("删除过磅记录数：{}", delete);
        List<CornXinPurchaseWeighRecordDo> beforeWeighRecordList = mixedCornXinPurchaseDo.getList_weigh();
        for (int i = 0; i < beforeWeighRecordList.size(); i++) {
            CornXinPurchaseWeighRecordDo record = beforeWeighRecordList.get(i);
            if (ObjectUtils.isEmpty(record.getWeighId())) {
                record.setWeighId("CornXinPurchaseWeighRecord_" + DateTimeUtil.getMinuteKey() + "_" + (i + 1));
                record.setCreateUser(sessionUserDo.getLoginCode());
            }
            record.setTradeSerno(mixedCornXinPurchaseDo.getSerno());
            record.setTradeDate(mixedCornXinPurchaseDo.getTradeDate());
            record.setUpdateUser(sessionUserDo.getLoginCode());
            record.setUpdateTime(new Date());
            record.setDataStatus("0");
            cornXinPurchaseWeighRecordMapper.insert(record);
        }

        return ResVo.success("收购记录保存成功");
    }

    @Override
    public ResVo<Object> purchaseComplete(MixedCornXinPurchaseDo mixedCornXinPurchaseDo) {
        // 获取当前登录用户
        UserDo sessionUserDo = SessionUtils.getSessionUser();
        if (!validateMixedTrade(mixedCornXinPurchaseDo, "complete")) {
            return ResVo.fail("数据校验失败");
        }

        // 校验或插入出售人
        PersonService.ValidateOrInsertPersonResult validateOrInsertResult = personService.validateOrInsertPerson(mixedCornXinPurchaseDo.getSellerInfo());
        PersonDo sellerInfo = validateOrInsertResult.personDo();
        if (validateOrInsertResult.validateResult() == 2) {
            return ResVo.fail(555, "手机号码已经绑定姓名为： " + sellerInfo.getPersonName() + " 的客户，请检查").setData(sellerInfo.getPersonName());
        }
        mixedCornXinPurchaseDo.setSellerId(sellerInfo.getPersonId());

        // 插入交易记录
        if (ObjectUtils.isEmpty(mixedCornXinPurchaseDo.getSerno())) {
            IdUtils.generateIdForObject(mixedCornXinPurchaseDo);
            mixedCornXinPurchaseDo.setCreateUser(sessionUserDo.getLoginCode());
        }
        mixedCornXinPurchaseDo.setUpdateUser(sessionUserDo.getLoginCode());
        mixedCornXinPurchaseDo.setDataStatus("1");
        mixedCornXinPurchaseDo.setTradeStatus("待结算");
        boolean insertOrUpdate = cornXinPurchaseMapper.insertOrUpdate(mixedCornXinPurchaseDo);
        if (!insertOrUpdate) {
            throw new BusinessException("交易记录插入失败");
        }

        // 删除过磅记录然后重新插入
        int delete = cornXinPurchaseWeighRecordMapper.deleteByTradeSerno(mixedCornXinPurchaseDo.getSerno());
        log.info("删除过磅记录数：{}", delete);
        List<CornXinPurchaseWeighRecordDo> beforeWeighRecordList = mixedCornXinPurchaseDo.getList_weigh();
        for (int i = 0; i < beforeWeighRecordList.size(); i++) {
            CornXinPurchaseWeighRecordDo record = beforeWeighRecordList.get(i);
            if (ObjectUtils.isEmpty(record.getWeighId())) {
                record.setWeighId("CornXinPurchaseWeighRecord_" + DateTimeUtil.getMinuteKey() + "_" + (i + 1));
                record.setCreateUser(sessionUserDo.getLoginCode());
            }
            record.setTradeSerno(mixedCornXinPurchaseDo.getSerno());
            record.setTradeDate(mixedCornXinPurchaseDo.getTradeDate());
            record.setUpdateUser(sessionUserDo.getLoginCode());
            record.setDataStatus("1");
            cornXinPurchaseWeighRecordMapper.insert(record);
        }

        return ResVo.success("收购记录保存成功");
    }

    @Override
    public ResVo<MixedCornXinPurchaseDo> getTradeDetail(CornXinPurchaseDo cornXinPurchaseDo) {
        if (ObjectUtils.isEmpty(cornXinPurchaseDo.getSerno())) {
            throw new BusinessException("交易流水号不能为空");
        }
        CornXinPurchaseDo cornXinPurchaseDo1 = cornXinPurchaseMapper.selectById(cornXinPurchaseDo);
        if (cornXinPurchaseDo1 == null) {
            throw new BusinessException("交易记录不存在");
        }

        // 查询出售人信息
        PersonDo sellerInfo = personMapper.selectById(cornXinPurchaseDo1.getSellerId());
        if (sellerInfo == null) {
            throw new BusinessException("出售人信息不存在");
        }
        // 查询过磅记录
        LambdaQueryWrapper<CornXinPurchaseWeighRecordDo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CornXinPurchaseWeighRecordDo::getTradeSerno, cornXinPurchaseDo1.getSerno());
        wrapper.orderByAsc(CornXinPurchaseWeighRecordDo::getCreateTime);
        List<CornXinPurchaseWeighRecordDo> weighRecordList = cornXinPurchaseWeighRecordMapper.selectList(wrapper);

        // 组装返回结果
        MixedCornXinPurchaseDo mixedCornXinPurchaseDo = new MixedCornXinPurchaseDo();
        BeanUtils.copyProperties(cornXinPurchaseDo1, mixedCornXinPurchaseDo);
        mixedCornXinPurchaseDo.setSellerInfo(sellerInfo);
        mixedCornXinPurchaseDo.setList_weigh(weighRecordList);
        return ResVo.success("查询交易详情成功", mixedCornXinPurchaseDo);
    }

    @Override
    public ResVo<Object> settleTrade(MixedCornXinPurchaseDo mixedCornXinPurchaseDo) {
        // 获取当前登录用户
        UserDo sessionUserDo = SessionUtils.getSessionUser();
        // 校验数据
        if (!validateMixedTrade(mixedCornXinPurchaseDo, "settle")) {
            return ResVo.fail("数据校验失败");
        }
        // 查询出售人是否已经存在
        PersonDo sellerInfo = mixedCornXinPurchaseDo.getSellerInfo();
        PersonDo existingSeller = personMapper.getByPhoneNum(sellerInfo.getPhoneNum());
        if (existingSeller == null) {
            throw new BusinessException("出售人信息不存在");
        }
        // 查询交易记录
        CornXinPurchaseDo cornXinPurchaseDo = cornXinPurchaseMapper.selectById(mixedCornXinPurchaseDo.getSerno());
        if (cornXinPurchaseDo == null) {
            throw new BusinessException("交易记录不存在");
        }
        // 交易状态必须为待结算
        if (!"待结算".equals(cornXinPurchaseDo.getTradeStatus())) {
            throw new BusinessException("交易状态必须为待结算");
        }
        // 数据状态必须为1
        if (!"1".equals(cornXinPurchaseDo.getDataStatus())) {
            throw new BusinessException("数据状态必须为1");
        }

        // 匹配入参总价和数据库数据总价
        if (mixedCornXinPurchaseDo.getTotalPrice().compareTo(cornXinPurchaseDo.getTotalPrice())!= 0) {
            throw new BusinessException("总价不匹配");
        }

        // 更新交易记录(实际结算日期/补价/最终结算金额/备注/交易状态)
        cornXinPurchaseDo.setActualClearingDate(mixedCornXinPurchaseDo.getActualClearingDate());
        cornXinPurchaseDo.setPremium(mixedCornXinPurchaseDo.getPremium());
        cornXinPurchaseDo.setClearingAmount(mixedCornXinPurchaseDo.getClearingAmount());
        cornXinPurchaseDo.setRemark(mixedCornXinPurchaseDo.getRemark());
        cornXinPurchaseDo.setTradeStatus("已结算");
        mixedCornXinPurchaseDo.setUpdateUser(sessionUserDo.getLoginCode());

        int update = cornXinPurchaseMapper.updateById(cornXinPurchaseDo);
        if (update == 0) {
            throw new BusinessException("交易记录更新失败");
        }
        return ResVo.success("交易结算成功");
    }


    private boolean validateMixedTrade(MixedCornXinPurchaseDo mixedCornXinPurchaseDo, String step) {
        // 校验数据
        if (mixedCornXinPurchaseDo == null) {
            throw new BusinessException("无效的交易数据");
        }

        // 交易日期校验
        String tradeDate = mixedCornXinPurchaseDo.getTradeDate();
        if (ObjectUtils.isEmpty(tradeDate)) {
            throw new BusinessException("交易日期不能为空");
        }
        // 日期格式校验
        if (!DateTimeUtil.isDateStr(tradeDate)) {
            throw new BusinessException("交易日期格式不正确");
        }

        // 出售人校验
        PersonDo sellerInfo = mixedCornXinPurchaseDo.getSellerInfo();
        if (sellerInfo == null) {
            throw new BusinessException("出售人信息不能为空");
        }

        // 出售人名称校验
        String sellerName = sellerInfo.getPersonName();
        if (ObjectUtils.isEmpty(sellerName)) {
            throw new BusinessException("出售人名称不能为空");
        }

        // 出售人手机号校验
        String sellerPhone = sellerInfo.getPhoneNum();
        if (ObjectUtils.isEmpty(sellerPhone)) {
            throw new BusinessException("出售人手机号不能为空");
        }
        // 出售人手机号格式校验
        if (!RegexUtils.isMobile(sellerPhone)) {
            throw new BusinessException("出售人手机号格式不正确");
        }

        BigDecimal totalWeight = mixedCornXinPurchaseDo.getTotalWeight();
        // 如果不是保存操作
        if (!"save".equals(step)) {

            // 结算方式校验
            String clearingForm = mixedCornXinPurchaseDo.getClearingForm();
            if (ObjectUtils.isEmpty(clearingForm)) {
                throw new BusinessException("结算方式不能为空");
            }

            // 结算方式只能是特定字段
            List<String> clearingFormList = FieldUtil.getEnumList("t_corn_xin_purchase", "clearing_form");
            if (clearingFormList != null && !clearingFormList.contains(clearingForm)) {
                throw new BusinessException("结算方式只能是: " + String.join("、", clearingFormList));
            }

            // 单价校验
            BigDecimal unitPrice = mixedCornXinPurchaseDo.getUnitPrice();
            if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("单价必须大于0");
            }
            // 单价范围再0.4~1.7之间
            if (unitPrice.compareTo(new BigDecimal("0.2")) < 0 || unitPrice.compareTo(new BigDecimal("1.5")) > 0) {
                throw new BusinessException("单价范围在0.2~1.5之间");
            }

            // 总重量校验
            if (totalWeight == null || totalWeight.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("总重量必须大于0");
            }

            // 总金额校验
            BigDecimal totalPrice = mixedCornXinPurchaseDo.getTotalPrice();
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
                BigDecimal clearingAmount = mixedCornXinPurchaseDo.getClearingAmount();
                BigDecimal premium = mixedCornXinPurchaseDo.getPremium();
                if (clearingAmount.compareTo(totalPrice.add(premium)) != 0) {
                    throw new BusinessException("结算金额必须等于总金额+补价");
                }
                // 结算日期不能为空
                String actualClearingDate = mixedCornXinPurchaseDo.getActualClearingDate();
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
        List<CornXinPurchaseWeighRecordDo> list_weigh = mixedCornXinPurchaseDo.getList_weigh();
        // 过磅净重求和
        BigDecimal sumNetWeight = BigDecimal.ZERO;
        for (CornXinPurchaseWeighRecordDo record : list_weigh) {
            validateWeighRecord(record, step);
            if (!"save".equals(step)) {
                // 非保存时，承运方不能为空
                if (ObjectUtils.isEmpty(record.getCarrier())) {
                    throw new BusinessException("承运方不能为空");
                }
                sumNetWeight = sumNetWeight.add(record.getNetWeight());
            }
        }

        // 非保存时，总重必须等于过磅净重求和
        if (!"save".equals(step)) {
            // 总重必须等于过磅净重求和
            if (totalWeight.compareTo(sumNetWeight)!= 0) {
                throw new BusinessException("总重量必须等于过磅净重求和");
            }
        }

        return true;

    }

    private void validateWeighRecord(CornXinPurchaseWeighRecordDo record, String step) {
        // 毛重必须大于0
        BigDecimal grossWeight = record.getGrossWeight();
        if (grossWeight == null || grossWeight.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("毛重必须大于0");
        }

        BigDecimal tareWeight = record.getTareWeight();
        BigDecimal netWeight = record.getNetWeight();

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
            if (tareWeight.add(netWeight).compareTo(grossWeight)!= 0) {
                throw new BusinessException("皮重和净重之和必须等于毛重");
            }

        } else {
            // 当皮重存在时
            if (tareWeight!= null) {
                // 净重必须大于等于0
                if (netWeight == null || netWeight.compareTo(BigDecimal.ZERO) < 0) {
                    throw new BusinessException("净重必须大于等于0");
                }
                // 皮重和净重之和必须等于毛重
                if (tareWeight.add(netWeight).compareTo(grossWeight)!= 0) {
                    throw new BusinessException("皮重和净重之和必须等于毛重");
                }
            } else {
                // 净重必须不存在
                if (netWeight!= null) {
                    throw new BusinessException("净重必须不存在");
                }
            }
        }
    }
    
    
    
    
    
    
}
