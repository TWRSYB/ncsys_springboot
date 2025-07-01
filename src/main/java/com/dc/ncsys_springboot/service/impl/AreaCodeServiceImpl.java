package com.dc.ncsys_springboot.service.impl;

import com.dc.ncsys_springboot.daoVo.AreaCodeDo;
import com.dc.ncsys_springboot.daoVo.User;
import com.dc.ncsys_springboot.exception.BusinessException;
import com.dc.ncsys_springboot.mapper.AreaCodeMapper;
import com.dc.ncsys_springboot.service.AreaCodeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dc.ncsys_springboot.util.SessionUtils;
import com.dc.ncsys_springboot.vo.ResVo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 地区码值表 服务实现类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-30 15:37
 */
@Service
@Transactional
public class AreaCodeServiceImpl extends ServiceImpl<AreaCodeMapper, AreaCodeDo> implements AreaCodeService {

    @Override
    public ResVo readJsonToDb() {

        User sessionUser = SessionUtils.getSessionUser();

        // 读取配置文件中的地区Json数据
        String areaJson = "src/main/resources/jsons/ChinaAreas3465.json";
        try {
            // 1. 创建ObjectMapper实例（Jackson的核心类）
            ObjectMapper mapper = new ObjectMapper();
            // 2. 读取JSON文件
            File jsonFile = new File(areaJson);

            // 3. 解析JSON为List<Map>结构
            List<Map<String, Object>> provinces = mapper.readValue(
                    jsonFile,
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            for (Map<String, Object> province : provinces) {
                String provinceCode = (String) province.get("code");
                String provinceName = (String) province.get("province");
                System.out.println("省: " + provinceName + " (" + provinceCode + ")");
                AreaCodeDo provinceDo = new AreaCodeDo();
                provinceDo.setAreaCode(provinceCode);
                provinceDo.setAreaName(provinceName);
                provinceDo.setAreaLv("1");
                provinceDo.setUpCode("0");
                provinceDo.setCreateUser(sessionUser.getLoginCode());
                provinceDo.setUpdateUser(sessionUser.getLoginCode());
                save(provinceDo);

                // 处理市
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> cities = (List<Map<String, Object>>) province.get("citys");
                for (Map<String, Object> city : cities) {
                    String cityCode = (String) city.get("code");
                    String cityName = (String) city.get("city");
                    System.out.println("  市: " + cityName + " (" + cityCode + ")");
                    AreaCodeDo cityDo = new AreaCodeDo();
                    cityDo.setAreaCode(cityCode);
                    cityDo.setAreaName(cityName);
                    cityDo.setAreaLv("2");
                    cityDo.setUpCode(provinceCode);
                    cityDo.setCreateUser(sessionUser.getLoginCode());
                    cityDo.setUpdateUser(sessionUser.getLoginCode());
                    save(cityDo);

                    // 处理区县
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> areas = (List<Map<String, Object>>) city.get("areas");
                    for (Map<String, Object> area : areas) {
                        String areaCode = (String) area.get("code");
                        String areaName = (String) area.get("area");
                        System.out.println("    区县: " + areaName + " (" + areaCode + ")");
                        AreaCodeDo areaDo = new AreaCodeDo();
                        areaDo.setAreaCode(areaCode);
                        areaDo.setAreaName(areaName);
                        areaDo.setAreaLv("3");
                        areaDo.setUpCode(cityCode);
                        areaDo.setCreateUser(sessionUser.getLoginCode());
                        areaDo.setUpdateUser(sessionUser.getLoginCode());
                        save(areaDo);
                    }
                }
            }

        } catch (IOException e) {
            throw new BusinessException("地区json写入数据库异常", e);
        }
        return ResVo.success();
    }
}
