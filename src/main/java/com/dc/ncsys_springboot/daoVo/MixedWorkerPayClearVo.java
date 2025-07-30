package com.dc.ncsys_springboot.daoVo;

import com.dc.ncsys_springboot.util.JsonUtils;
import lombok.Data;

import java.io.Serializable;

@Data
public class MixedWorkerPayClearVo implements Serializable  {

    private static final long serialVersionUID = 1L;

    private WorkerDo worker;

    private WorkerPayClearDo settle;


	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "=" +JsonUtils.toJson(this);
	}

}
