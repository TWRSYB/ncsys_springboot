package com.dc.ncsys_springboot.daoVo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 玉米芯收购表
 * </p>
 *
 * @author sysAdmin
 * @since 2025-08-05 08:55
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@TableName("t_corn_xin_purchase")
public class MixedCornXinPurchaseDo extends CornXinPurchaseDo implements Serializable {

    private static final long serialVersionUID = 1L;

    private PersonDo sellerInfo;
    private List<CornXinPurchaseWeighRecordDo> list_weigh;
    private HashMap<String,String> sum_weight;

}
