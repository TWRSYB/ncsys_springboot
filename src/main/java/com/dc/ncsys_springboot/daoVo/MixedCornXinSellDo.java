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
 * 玉米芯出售表
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-13 16:55
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@TableName("t_corn_xin_sell")
public class MixedCornXinSellDo extends CornXinSellDo implements Serializable {

    private static final long serialVersionUID = 1L;

    private PersonDo buyerPerson;
    private CompanyDo buyerCompany;
    private List<CornXinSellWeighRecordDo> list_weigh;
    private HashMap<String,String> sum_weight;

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "=" +JsonUtils.toJson(this);
	}

}
