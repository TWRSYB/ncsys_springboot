package com.dc.ncsys_springboot.service.impl;

import com.dc.ncsys_springboot.daoVo.NationalAdministrativeDivisionInfoDo;
import com.dc.ncsys_springboot.daoVo.UserDo;
import com.dc.ncsys_springboot.exception.BusinessException;
import com.dc.ncsys_springboot.mapper.NationalAdministrativeDivisionInfoMapper;
import com.dc.ncsys_springboot.service.NationalAdministrativeDivisionInfoService;
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
 * 全国行政区划信息 服务实现类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-01 16:13
 */
@Service
@Transactional
public class NationalAdministrativeDivisionInfoServiceImpl extends ServiceImpl<NationalAdministrativeDivisionInfoMapper, NationalAdministrativeDivisionInfoDo> implements NationalAdministrativeDivisionInfoService {

    @Override
    public ResVo readJsonToDb() {

        UserDo sessionUserDo = SessionUtils.getSessionUser();

        // 读取配置文件中的地区Json数据
        String areaJson = "src/main/resources/jsons/JSON_AREA.json";
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
                String provinceCode = (String) province.get("area_code");
                String provinceName = (String) province.get("area_name");
                System.out.println("省: " + provinceName + " (" + provinceCode + ")");
                NationalAdministrativeDivisionInfoDo provinceDo = new NationalAdministrativeDivisionInfoDo();
                provinceDo.setAreaLv("1");
                provinceDo.setAreaName(provinceName);
                provinceDo.setAreaCode(provinceCode);
                provinceDo.setUpCode("0");
                provinceDo.setAreaPath((String) province.get("area_path"));
                provinceDo.setAreaNameFull((String) province.get("area_name_full"));
                provinceDo.setZhudi((String) province.get("zhudi"));
                provinceDo.setRenkou(province.get("renkou").toString());
                provinceDo.setMianji(province.get("mianji").toString());
                provinceDo.setQuhao((String) province.get("quhao"));
                provinceDo.setYoubian((String) province.get("youbian"));
                provinceDo.setCreateUser(sessionUserDo.getLoginCode());
                provinceDo.setUpdateUser(sessionUserDo.getLoginCode());
                save(provinceDo);

                // 处理市
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> cities = (List<Map<String, Object>>) province.get("children");
                for (Map<String, Object> city : cities) {
                    String cityCode = (String) city.get("area_code");
                    String cityName = (String) city.get("area_name");
                    System.out.println("  市: " + cityName + " (" + cityCode + ")");
                    NationalAdministrativeDivisionInfoDo cityDo = new NationalAdministrativeDivisionInfoDo();
                    cityDo.setAreaLv("2");
                    cityDo.setAreaName(cityName);
                    cityDo.setAreaCode(cityCode);
                    cityDo.setUpCode(provinceCode);
                    cityDo.setAreaPath((String) city.get("area_path"));
                    cityDo.setAreaNameFull((String) city.get("area_name_full"));
                    cityDo.setZhudi((String) city.get("zhudi"));
                    cityDo.setRenkou(city.get("renkou").toString());
                    cityDo.setMianji(city.get("mianji").toString());
                    cityDo.setQuhao((String) city.get("quhao"));
                    cityDo.setYoubian((String) city.get("youbian"));
                    cityDo.setCreateUser(sessionUserDo.getLoginCode());
                    cityDo.setUpdateUser(sessionUserDo.getLoginCode());
                    save(cityDo);

                    // 处理区县
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> areas = (List<Map<String, Object>>) city.get("children");
                    for (Map<String, Object> area : areas) {
                        String areaCode = (String) area.get("area_code");
                        String areaName = (String) area.get("area_name");
                        System.out.println("    区县: " + areaName + " (" + areaCode + ")");
                        NationalAdministrativeDivisionInfoDo areaDo = new NationalAdministrativeDivisionInfoDo();
                        areaDo.setAreaLv("3");
                        areaDo.setAreaName(areaName);
                        areaDo.setAreaCode(areaCode);
                        areaDo.setUpCode(cityCode);
                        areaDo.setAreaPath((String) area.get("area_path"));
                        areaDo.setAreaNameFull((String) area.get("area_name_full"));
                        areaDo.setZhudi((String) area.get("zhudi"));
                        areaDo.setRenkou(area.get("renkou").toString());
                        areaDo.setMianji(area.get("mianji").toString());
                        areaDo.setQuhao((String) area.get("quhao"));
                        areaDo.setYoubian((String) area.get("youbian"));
                        areaDo.setCreateUser(sessionUserDo.getLoginCode());
                        areaDo.setUpdateUser(sessionUserDo.getLoginCode());
                        save(areaDo);
                    }
                }
            }

        } catch (IOException e) {
            throw new BusinessException("全国行政区划信息Json写入数据库失败", e);
        }
        return ResVo.success();
    }
}
