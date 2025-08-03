package com.dc.ncsys_springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dc.ncsys_springboot.daoVo.*;
import com.dc.ncsys_springboot.exception.BusinessException;
import com.dc.ncsys_springboot.mapper.CornGrainPurchaseMapper;
import com.dc.ncsys_springboot.mapper.CornGrainPurchaseWeighRecordMapper;
import com.dc.ncsys_springboot.mapper.PersonMapper;
import com.dc.ncsys_springboot.service.CornGrainPurchaseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;


/**
 * <p>
 * 玉米粒收购表 服务实现类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-13 16:55
 */
@Slf4j
@Service
public class CornGrainPurchaseServiceImpl extends ServiceImpl<CornGrainPurchaseMapper, CornGrainPurchaseDo> implements CornGrainPurchaseService {


    @Autowired
    private CornGrainPurchaseMapper cornGrainPurchaseMapper;

    @Autowired
    private PersonMapper personMapper;

    @Autowired
    private CornGrainPurchaseWeighRecordMapper cornGrainPurchaseWeighRecordMapper;


    @Override
    public PageResVo<CornGrainPurchaseDo> getList(PageQueryVo<CornGrainPurchaseDo> pageQueryVo) {
        PageHelper.startPage(pageQueryVo.getPageNum(), pageQueryVo.getPageSize());
        Page<CornGrainPurchaseDo> page = cornGrainPurchaseMapper.getList(pageQueryVo.getParams());
        return PageResVo.success(page);
    }

    @Override
    public ResVo<Object> saveTrade(MixedCornGrainPurchaseDo mixedCornGrainPurchaseDo) {
        // 获取当前登录用户
        UserDo sessionUserDo = SessionUtils.getSessionUser();
        if (!validateMixedTrade(mixedCornGrainPurchaseDo, "save")) {
            return ResVo.fail("数据校验失败");
        }

        // 查询出售人是否已经存在
        PersonDo sellerInfo = mixedCornGrainPurchaseDo.getSellerInfo();
        PersonDo existingSeller = personMapper.getByPhoneNum(sellerInfo.getPhoneNum());

        if (existingSeller != null) {
            // 如果存在，使用已存在的ID
            if (!existingSeller.getPersonName().equals(sellerInfo.getPersonName())) {
                return ResVo.fail(555, "手机号码已经绑定姓名为： " + existingSeller.getPersonName() + " 的客户，请检查");
            }
            sellerInfo = existingSeller;

        } else {
            // 如果不存在，插入新记录并获取ID
            IdUtils.generateIdForObject(sellerInfo);
            sellerInfo.setCreateUser(sessionUserDo.getLoginCode());
            sellerInfo.setUpdateUser(sessionUserDo.getLoginCode());
            sellerInfo.setDataStatus("1");
            personMapper.insert(sellerInfo);
        }

        mixedCornGrainPurchaseDo.setSellerId(sellerInfo.getPersonId());
        // 插入交易记录
        if (ObjectUtils.isEmpty(mixedCornGrainPurchaseDo.getSerno())) {
            mixedCornGrainPurchaseDo.setSerno("CornGrainPurchase_" + DateTimeUtil.getMinuteKey());
            mixedCornGrainPurchaseDo.setCreateUser(sessionUserDo.getLoginCode());
        }
        mixedCornGrainPurchaseDo.setUpdateUser(sessionUserDo.getLoginCode());
        mixedCornGrainPurchaseDo.setDataStatus("0");
        mixedCornGrainPurchaseDo.setTradeStatus("收购中");
        boolean insertOrUpdate = cornGrainPurchaseMapper.insertOrUpdate(mixedCornGrainPurchaseDo);
        if (!insertOrUpdate) {
            throw new BusinessException("交易记录插入失败");
        }

        // 删除过磅记录然后重新插入
        int delete = cornGrainPurchaseWeighRecordMapper.deleteByTradeSerno(mixedCornGrainPurchaseDo.getSerno());
        log.info("删除过磅记录数：{}", delete);
        List<CornGrainPurchaseWeighRecordDo> beforeWeighRecordList = mixedCornGrainPurchaseDo.getList_weigh();
        for (int i = 0; i < beforeWeighRecordList.size(); i++) {
            CornGrainPurchaseWeighRecordDo record = beforeWeighRecordList.get(i);
            if (ObjectUtils.isEmpty(record.getWeighId())) {
                record.setWeighId("CornGrainPurchase_WeighRecord_BeforeThresh" + DateTimeUtil.getMinuteKey() + "_" + (i + 1));
                record.setCreateUser(sessionUserDo.getLoginCode());
            }
            record.setTradeSerno(mixedCornGrainPurchaseDo.getSerno());
            record.setTradeDate(mixedCornGrainPurchaseDo.getTradeDate());
            record.setUpdateUser(sessionUserDo.getLoginCode());
            record.setUpdateTime(new Date());
            record.setDataStatus("0");
            cornGrainPurchaseWeighRecordMapper.insert(record);
        }

        return ResVo.success("收购记录保存成功");
    }

    @Override
    public ResVo<MixedCornGrainPurchaseDo> getTradeDetail(CornGrainPurchaseDo cornGrainPurchaseDo) {
        if (ObjectUtils.isEmpty(cornGrainPurchaseDo.getSerno())) {
            throw new BusinessException("交易流水号不能为空");
        }
        CornGrainPurchaseDo cornGrainPurchaseDo1 = cornGrainPurchaseMapper.selectById(cornGrainPurchaseDo);
        if (cornGrainPurchaseDo1 == null) {
            throw new BusinessException("交易记录不存在");
        }

        // 查询出售人信息
        PersonDo sellerInfo = personMapper.selectById(cornGrainPurchaseDo1.getSellerId());
        if (sellerInfo == null) {
            throw new BusinessException("出售人信息不存在");
        }
        // 查询过磅记录
        LambdaQueryWrapper<CornGrainPurchaseWeighRecordDo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CornGrainPurchaseWeighRecordDo::getTradeSerno, cornGrainPurchaseDo1.getSerno());
        wrapper.orderByAsc(CornGrainPurchaseWeighRecordDo::getCreateTime);
        List<CornGrainPurchaseWeighRecordDo> weighRecordList = cornGrainPurchaseWeighRecordMapper.selectList(wrapper);

        // 组装返回结果
        MixedCornGrainPurchaseDo mixedCornGrainPurchaseDo = new MixedCornGrainPurchaseDo();
        BeanUtils.copyProperties(cornGrainPurchaseDo1, mixedCornGrainPurchaseDo);
        mixedCornGrainPurchaseDo.setSellerInfo(sellerInfo);
        mixedCornGrainPurchaseDo.setList_weigh(weighRecordList);
        return ResVo.success("查询交易详情成功", mixedCornGrainPurchaseDo);

    }

    @Override
    public ResVo<Object> purchaseComplete(MixedCornGrainPurchaseDo mixedCornGrainPurchaseDo) {
        // 获取当前登录用户
        UserDo sessionUserDo = SessionUtils.getSessionUser();
        if (!validateMixedTrade(mixedCornGrainPurchaseDo, "complete")) {
            return ResVo.fail("数据校验失败");
        }

        // 查询出售人是否已经存在
        PersonDo sellerInfo = mixedCornGrainPurchaseDo.getSellerInfo();
        PersonDo existingSeller = personMapper.getByPhoneNum(sellerInfo.getPhoneNum());

        if (existingSeller != null) {
            // 如果存在，使用已存在的ID
            if (!existingSeller.getPersonName().equals(sellerInfo.getPersonName())) {
                return ResVo.fail(555, "手机号码已经绑定姓名为： " + existingSeller.getPersonName() + " 的客户，请检查");
            }
            sellerInfo = existingSeller;

        } else {
            // 如果不存在，插入新记录并获取ID
            IdUtils.generateIdForObject(sellerInfo);
            sellerInfo.setCreateUser(sessionUserDo.getLoginCode());
            sellerInfo.setUpdateUser(sessionUserDo.getLoginCode());
            sellerInfo.setDataStatus("1");
            personMapper.insert(sellerInfo);
        }

        mixedCornGrainPurchaseDo.setSellerId(sellerInfo.getPersonId());
        // 插入交易记录
        if (ObjectUtils.isEmpty(mixedCornGrainPurchaseDo.getSerno())) {
            IdUtils.generateIdForObject(mixedCornGrainPurchaseDo);
            mixedCornGrainPurchaseDo.setCreateUser(sessionUserDo.getLoginCode());
        }
        mixedCornGrainPurchaseDo.setUpdateUser(sessionUserDo.getLoginCode());
        mixedCornGrainPurchaseDo.setDataStatus("1");
        mixedCornGrainPurchaseDo.setTradeStatus("待结算");
        boolean insertOrUpdate = cornGrainPurchaseMapper.insertOrUpdate(mixedCornGrainPurchaseDo);
        if (!insertOrUpdate) {
            throw new BusinessException("交易记录插入失败");
        }

        // 删除过磅记录然后重新插入
        int delete = cornGrainPurchaseWeighRecordMapper.deleteByTradeSerno(mixedCornGrainPurchaseDo.getSerno());
        log.info("删除过磅记录数：{}", delete);
        List<CornGrainPurchaseWeighRecordDo> beforeWeighRecordList = mixedCornGrainPurchaseDo.getList_weigh();
        for (int i = 0; i < beforeWeighRecordList.size(); i++) {
            CornGrainPurchaseWeighRecordDo record = beforeWeighRecordList.get(i);
            if (ObjectUtils.isEmpty(record.getWeighId())) {
                record.setWeighId("CornGrainPurchaseWeighRecord_" + DateTimeUtil.getMinuteKey() + "_" + (i + 1));
                record.setCreateUser(sessionUserDo.getLoginCode());
            }
            record.setTradeSerno(mixedCornGrainPurchaseDo.getSerno());
            record.setTradeDate(mixedCornGrainPurchaseDo.getTradeDate());
            record.setUpdateUser(sessionUserDo.getLoginCode());
            record.setDataStatus("1");
            cornGrainPurchaseWeighRecordMapper.insert(record);
        }

        return ResVo.success("收购记录保存成功");

    }

    @Override
    public ResVo<Object> settleTrade(MixedCornGrainPurchaseDo mixedCornGrainPurchaseDo) {
        // 获取当前登录用户
        UserDo sessionUserDo = SessionUtils.getSessionUser();
        // 校验数据
        if (!validateMixedTrade(mixedCornGrainPurchaseDo, "settle")) {
            return ResVo.fail("数据校验失败");
        }
        // 查询出售人是否已经存在
        PersonDo sellerInfo = mixedCornGrainPurchaseDo.getSellerInfo();
        PersonDo existingSeller = personMapper.getByPhoneNum(sellerInfo.getPhoneNum());
        if (existingSeller == null) {
            throw new BusinessException("出售人信息不存在");
        }
        // 查询交易记录
        CornGrainPurchaseDo cornGrainPurchaseDo = cornGrainPurchaseMapper.selectById(mixedCornGrainPurchaseDo.getSerno());
        if (cornGrainPurchaseDo == null) {
            throw new BusinessException("交易记录不存在");
        }
        // 交易状态必须为待结算
        if (!"待结算".equals(cornGrainPurchaseDo.getTradeStatus())) {
            throw new BusinessException("交易状态必须为待结算");
        }
        // 数据状态必须为1
        if (!"1".equals(cornGrainPurchaseDo.getDataStatus())) {
            throw new BusinessException("数据状态必须为1");
        }

        // 匹配入参总价和数据库数据总价
        if (mixedCornGrainPurchaseDo.getTotalPrice().compareTo(cornGrainPurchaseDo.getTotalPrice())!= 0) {
            throw new BusinessException("总价不匹配");
        }

        // 更新交易记录(实际结算日期/补价/最终结算金额/备注/交易状态)
        cornGrainPurchaseDo.setActualClearingDate(mixedCornGrainPurchaseDo.getActualClearingDate());
        cornGrainPurchaseDo.setPremium(mixedCornGrainPurchaseDo.getPremium());
        cornGrainPurchaseDo.setClearingAmount(mixedCornGrainPurchaseDo.getClearingAmount());
        cornGrainPurchaseDo.setRemark(mixedCornGrainPurchaseDo.getRemark());
        cornGrainPurchaseDo.setTradeStatus("已结算");
        mixedCornGrainPurchaseDo.setUpdateUser(sessionUserDo.getLoginCode());

        int update = cornGrainPurchaseMapper.updateById(cornGrainPurchaseDo);
        if (update == 0) {
            throw new BusinessException("交易记录更新失败");
        }
        return ResVo.success("交易结算成功");
    }

    private boolean validateMixedTrade(MixedCornGrainPurchaseDo mixedCornGrainPurchaseDo, String step) {
        // 校验数据
        if (mixedCornGrainPurchaseDo == null) {
            throw new BusinessException("无效的交易数据");
        }

        // 交易日期校验
        String tradeDate = mixedCornGrainPurchaseDo.getTradeDate();
        if (ObjectUtils.isEmpty(tradeDate)) {
            throw new BusinessException("交易日期不能为空");
        }
        // 日期格式校验
        if (!DateTimeUtil.isDateStr(tradeDate)) {
            throw new BusinessException("交易日期格式不正确");
        }

        // 出售人校验
        PersonDo sellerInfo = mixedCornGrainPurchaseDo.getSellerInfo();
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

        BigDecimal totalWeight = mixedCornGrainPurchaseDo.getTotalWeight();
        // 如果不是保存操作
        if (!"save".equals(step)) {

            // 结算方式校验
            String clearingForm = mixedCornGrainPurchaseDo.getClearingForm();
            if (ObjectUtils.isEmpty(clearingForm)) {
                throw new BusinessException("结算方式不能为空");
            }

            // 结算方式只能是现结或延结
            List<String> clearingFormList = FieldUtil.getEnumList("t_corn_grain_purchase", "clearing_form");
            if (clearingFormList != null && !clearingFormList.contains(clearingForm)) {
                throw new BusinessException("结算方式只能是现结或延结");
            }

            // 单价校验
            BigDecimal unitPrice = mixedCornGrainPurchaseDo.getUnitPrice();
            if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("单价必须大于0");
            }
            // 单价范围再0.4~1.7之间
            if (unitPrice.compareTo(new BigDecimal("0.4")) < 0 || unitPrice.compareTo(new BigDecimal("1.7")) > 0) {
                throw new BusinessException("单价范围在0.4~1.7之间");
            }

            // 总重量校验
            if (totalWeight == null || totalWeight.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("总重量必须大于0");
            }

            // 总金额校验
            BigDecimal totalPrice = mixedCornGrainPurchaseDo.getTotalPrice();
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
                BigDecimal clearingAmount = mixedCornGrainPurchaseDo.getClearingAmount();
                BigDecimal premium = mixedCornGrainPurchaseDo.getPremium();
                if (clearingAmount.compareTo(totalPrice.add(premium)) != 0) {
                    throw new BusinessException("结算金额必须等于总金额+补价");
                }
                // 结算日期不能为空
                String actualClearingDate = mixedCornGrainPurchaseDo.getActualClearingDate();
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
        List<CornGrainPurchaseWeighRecordDo> list_weigh = mixedCornGrainPurchaseDo.getList_weigh();
        // 脱粒前过磅净重求和
        BigDecimal sumNetWeight = BigDecimal.ZERO;
        for (CornGrainPurchaseWeighRecordDo record : list_weigh) {
            validateWeighRecord(record, step);
            if (!"save".equals(step)) {
                // 非保存时，承运方不能为空
                if (ObjectUtils.isEmpty(record.getCarrier())) {
                    throw new BusinessException("承运方不能为空");
                }
                sumNetWeight = sumNetWeight.add(record.getNetWeight());
            }
        }

        // 非保存时，总重必须等于脱粒前过磅净重求和
        if (!"save".equals(step)) {
            // 总重必须等于脱粒前过磅净重求和
            if (totalWeight.compareTo(sumNetWeight)!= 0) {
                throw new BusinessException("总重量必须等于脱粒前过磅净重求和");
            }
        }

        return true;

    }

    private void validateWeighRecord(CornGrainPurchaseWeighRecordDo record, String step) {
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
