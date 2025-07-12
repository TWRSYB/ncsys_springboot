package com.dc.ncsys_springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dc.ncsys_springboot.daoVo.*;
import com.dc.ncsys_springboot.exception.BusinessException;
import com.dc.ncsys_springboot.mapper.CornCobPurchaseMapper;
import com.dc.ncsys_springboot.mapper.CornCobPurchaseWeighRecordMapper;
import com.dc.ncsys_springboot.mapper.PersonMapper;
import com.dc.ncsys_springboot.service.CornCobPurchaseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import com.dc.ncsys_springboot.util.DateTimeUtil;
import com.dc.ncsys_springboot.util.SessionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <p>
 * 玉米棒收购表 服务实现类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-01 21:59
 */
@Slf4j
@Service
@Transactional
public class CornCobPurchaseServiceImpl extends ServiceImpl<CornCobPurchaseMapper, CornCobPurchaseDo> implements CornCobPurchaseService {

    // 预编译正则模式，提高性能
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    @Autowired
    private CornCobPurchaseMapper cornCobPurchaseMapper;

    @Autowired
    private PersonMapper personMapper;

    @Autowired
    private CornCobPurchaseWeighRecordMapper cornCobPurchaseWeighRecordMapper;

    @Override
    public PageResVo<CornCobPurchaseDo> getList(PageQueryVo<CornCobPurchaseDo> queryVo) {
        PageHelper.startPage(queryVo.getPageNum(), queryVo.getPageSize());
        // 根据姓名或手机号码查询出售人
        if (!ObjectUtils.isEmpty(queryVo.getParams()) && !ObjectUtils.isEmpty(queryVo.getParams().get("seller"))) {
            String seller = queryVo.getParams().get("seller");
            PersonDo sellerDo = personMapper.getByPhoneNumOrName(seller);
            if (sellerDo != null) {
                queryVo.getParams().put("sellerId", sellerDo.getPersonId());
            } else {
                queryVo.getParams().put("sellerId", seller);
            }
        }
        Page<CornCobPurchaseDo> cornCobPurchaseDoList = cornCobPurchaseMapper.getList(queryVo.getParams());
        return PageResVo.success(cornCobPurchaseDoList);
    }

    @Override
    public ResVo<Object> saveTrade(MixedCornCobPurchaseDo mixedCornCobPurchaseDo) {
        // 获取当前登录用户
        User sessionUser = SessionUtils.getSessionUser();
        if (!validateMixedTrade(mixedCornCobPurchaseDo, "save")) {
            return ResVo.fail("数据校验失败");
        }

        // 查询出售人是否已经存在
        PersonDo sellerInfo = mixedCornCobPurchaseDo.getSellerInfo();
        PersonDo existingSeller = personMapper.getByPhoneNum(sellerInfo.getPhoneNum());

        if (existingSeller != null) {
            // 如果存在，使用已存在的ID
            if (!existingSeller.getPersonName().equals(sellerInfo.getPersonName())) {
                return ResVo.fail(555, "手机号码已经绑定姓名为： " + existingSeller.getPersonName() + " 的客户，请检查");
            }
            sellerInfo = existingSeller;

        } else {
            // 如果不存在，插入新记录并获取ID
            sellerInfo.setPersonId("People_" + sellerInfo.getPhoneNum() + "_" + DateTimeUtil.getMinuteKey());
            sellerInfo.setCreateUser(sessionUser.getUserId());
            sellerInfo.setUpdateUser(sessionUser.getUserId());
            sellerInfo.setDataStatus("1");
            personMapper.insert(sellerInfo);
        }

        mixedCornCobPurchaseDo.setSellerId(sellerInfo.getPersonId());
        // 插入交易记录
        if (ObjectUtils.isEmpty(mixedCornCobPurchaseDo.getSerno())) {
            mixedCornCobPurchaseDo.setSerno("CornCobPurchase_" + DateTimeUtil.getMinuteKey());
            mixedCornCobPurchaseDo.setCreateUser(sessionUser.getUserId());
        }
        mixedCornCobPurchaseDo.setUpdateUser(sessionUser.getUserId());
        mixedCornCobPurchaseDo.setDataStatus("0");
        mixedCornCobPurchaseDo.setTradeStatus("收购中");
        boolean insertOrUpdate = cornCobPurchaseMapper.insertOrUpdate(mixedCornCobPurchaseDo);
        if (!insertOrUpdate) {
            throw new BusinessException("交易记录插入失败");
        }
        // 插入过磅记录然后重新插入
        int delete = cornCobPurchaseWeighRecordMapper.deleteByTradeSerno(mixedCornCobPurchaseDo.getSerno());
        log.info("删除过磅记录数：{}", delete);
        List<CornCobPurchaseWeighRecordDo> beforeWeighRecordList = mixedCornCobPurchaseDo.getList_weighBeforeThresh();
        for (int i = 0; i < beforeWeighRecordList.size(); i++) {
            CornCobPurchaseWeighRecordDo record = beforeWeighRecordList.get(i);
            if (ObjectUtils.isEmpty(record.getWeighId())) {
                record.setWeighId("CornCobPurchase_WeighRecord_BeforeThresh" + DateTimeUtil.getMinuteKey() + "_" + (i + 1));
                record.setCreateUser(sessionUser.getUserId());
            }
            record.setTradeSerno(mixedCornCobPurchaseDo.getSerno());
            record.setTradeDate(mixedCornCobPurchaseDo.getTradeDate());
            record.setWeighType("脱粒前");
            record.setUpdateUser(sessionUser.getUserId());
            record.setUpdateTime(new Date());
            record.setDataStatus("0");
            cornCobPurchaseWeighRecordMapper.insert(record);
        }

        if ("Y".equals(mixedCornCobPurchaseDo.getThreshingYn())) {
            List<CornCobPurchaseWeighRecordDo> afterWeighRecordList = mixedCornCobPurchaseDo.getList_weighAfterThresh();
            for (int i = 0; i < afterWeighRecordList.size(); i++) {
                CornCobPurchaseWeighRecordDo record = afterWeighRecordList.get(i);
                if (ObjectUtils.isEmpty(record.getWeighId())) {
                    record.setWeighId("CornCobPurchase_WeighRecord_AfterThresh" + DateTimeUtil.getMinuteKey() + "_" + (i + 1));
                    record.setCreateUser(sessionUser.getUserId());
                }
                record.setTradeSerno(mixedCornCobPurchaseDo.getSerno());
                record.setTradeDate(mixedCornCobPurchaseDo.getTradeDate());
                record.setWeighType("脱粒后");
                record.setUpdateUser(sessionUser.getUserId());
                record.setUpdateTime(new Date());
                record.setDataStatus("0");
                cornCobPurchaseWeighRecordMapper.insert(record);
            }
        }
        return ResVo.success("收购记录保存成功");
    }

    @Override
    public ResVo<MixedCornCobPurchaseDo> getTradeDetail(CornCobPurchaseDo cornCobPurchaseDo) {
        if (ObjectUtils.isEmpty(cornCobPurchaseDo.getSerno())) {
            throw new BusinessException("交易流水号不能为空");
        }
        CornCobPurchaseDo cornCobPurchaseDo1 = cornCobPurchaseMapper.selectById(cornCobPurchaseDo);
        if (cornCobPurchaseDo1 == null) {
            throw new BusinessException("交易记录不存在");
        }

        // 查询出售人信息
        PersonDo sellerInfo = personMapper.selectById(cornCobPurchaseDo1.getSellerId());
        if (sellerInfo == null) {
            throw new BusinessException("出售人信息不存在");
        }
        // 查询脱粒前过磅记录
        LambdaQueryWrapper<CornCobPurchaseWeighRecordDo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CornCobPurchaseWeighRecordDo::getTradeSerno, cornCobPurchaseDo1.getSerno());
        wrapper.eq(CornCobPurchaseWeighRecordDo::getWeighType, "脱粒前");
        wrapper.orderByAsc(CornCobPurchaseWeighRecordDo::getCreateTime);
        List<CornCobPurchaseWeighRecordDo> beforeWeighRecordList = cornCobPurchaseWeighRecordMapper.selectList(wrapper);
        // 查询脱粒后过磅记录
        wrapper.clear();
        wrapper.eq(CornCobPurchaseWeighRecordDo::getTradeSerno, cornCobPurchaseDo1.getSerno());
        wrapper.eq(CornCobPurchaseWeighRecordDo::getWeighType, "脱粒后");
        wrapper.orderByAsc(CornCobPurchaseWeighRecordDo::getCreateTime);
        List<CornCobPurchaseWeighRecordDo> afterWeighRecordList = cornCobPurchaseWeighRecordMapper.selectList(wrapper);
        // 组装返回结果
        MixedCornCobPurchaseDo mixedCornCobPurchaseDo = new MixedCornCobPurchaseDo();
        BeanUtils.copyProperties(cornCobPurchaseDo1, mixedCornCobPurchaseDo);
        mixedCornCobPurchaseDo.setSellerInfo(sellerInfo);
        mixedCornCobPurchaseDo.setList_weighBeforeThresh(beforeWeighRecordList);
        mixedCornCobPurchaseDo.setList_weighAfterThresh(afterWeighRecordList);
        return ResVo.success("查询交易详情成功", mixedCornCobPurchaseDo);

    }


    private boolean validateMixedTrade(MixedCornCobPurchaseDo mixedCornCobPurchaseDo, String step) {
        // 校验数据
        if (mixedCornCobPurchaseDo == null) {
            throw new BusinessException("无效的交易数据");
        }

        // 交易日期校验
        String tradeDate = mixedCornCobPurchaseDo.getTradeDate();
        if (ObjectUtils.isEmpty(tradeDate)) {
            throw new BusinessException("交易日期不能为空");
        }
        // 日期格式校验
        if (!DateTimeUtil.isDateStr(tradeDate)) {
            throw new BusinessException("交易日期格式不正确");
        }

        // 出售人校验
        PersonDo sellerInfo = mixedCornCobPurchaseDo.getSellerInfo();
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
        if (!PHONE_PATTERN.matcher(sellerPhone).matches()) {
            throw new BusinessException("出售人手机号格式不正确");
        }

        // 校验过磅记录
        List<CornCobPurchaseWeighRecordDo> beforeWeighRecordList = mixedCornCobPurchaseDo.getList_weighBeforeThresh();
        for (CornCobPurchaseWeighRecordDo record : beforeWeighRecordList) {
            validateWeighRecord(record, step);
            if (!"save".equals(step)) {
                // 非保存时，承运方不能为空
                if (ObjectUtils.isEmpty(record.getCarrier())) {
                    throw new BusinessException("承运方不能为空");
                }
            }
        }
        if ("Y".equals(mixedCornCobPurchaseDo.getThreshingYn())) {
            List<CornCobPurchaseWeighRecordDo> afterWeighRecordList = mixedCornCobPurchaseDo.getList_weighAfterThresh();
            for (CornCobPurchaseWeighRecordDo record : afterWeighRecordList) {
                validateWeighRecord(record, step);
            }
        }

        return true;
    }

    private static void validateWeighRecord(CornCobPurchaseWeighRecordDo record, String step) {
        // 毛重必须大于0
        BigDecimal grossWeight = record.getGrossWeight();
        if (grossWeight == null || grossWeight.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("毛重必须大于0");
        }

        BigDecimal tareWeight = record.getTareWeight();
        BigDecimal netWeight = record.getNetWeight();


        // 皮重和净重不能为空
        if (tareWeight == null && netWeight == null) {
            // 不是保存
            if (!"save".equals(step)) {
                throw new BusinessException("皮重和净重不能为空");
            }
        } else if (tareWeight != null && netWeight != null) {
            // 皮重必须大于等于0
            if (tareWeight.compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException("皮重必须大于等于0");
            }
            // 净重必须大于0
            if (netWeight.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("净重必须大于0");
            }
            // 皮重和净重之和必须等于毛重
            if (tareWeight.add(netWeight).compareTo(grossWeight) != 0) {
                throw new BusinessException("皮重和净重之和必须等于毛重");
            }
        } else {
            throw new BusinessException("皮重和净重必须同时存在或者同时不存在");
        }
    }
}
