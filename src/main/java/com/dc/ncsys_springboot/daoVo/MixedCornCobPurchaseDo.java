package com.dc.ncsys_springboot.daoVo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dc.ncsys_springboot.util.JsonUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 玉米棒收购表
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-02 14:45
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@TableName("t_corn_cob_purchase")
public class MixedCornCobPurchaseDo extends CornCobPurchaseDo implements Serializable  {

    private static final long serialVersionUID = 1L;

    // 出售人
    private PersonDo sellerInfo;

    private List<CornCobPurchaseWeighRecordDo> list_weighBeforeThresh;

    private HashMap<String,String> sum_weightBeforeThresh;

    private List<CornCobPurchaseWeighRecordDo> list_weighAfterThresh;

    private HashMap<String,String> sum_weightAfterThresh;


	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "=" +JsonUtils.toJson(this);
	}

}
