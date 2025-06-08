package com.mybatis_plus_generator;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.model.ClassAnnotationAttributes;

/**
 * MyBatis-Plus 代码生成器示例 (v3.5.6)
 * 注意：运行前请确保已正确配置数据库连接信息
 */
public class CodeGenerator {

    public static void main(String[] args) {
        String tableName = "s_table_design_sql";
        generator(tableName);
    }

    public static void generator(String tableName) {
        String tablePrefix = tableName.substring(0, tableName.indexOf("_") + 1);
        // ===================== 1. 数据源配置 =====================
        DataSourceConfig dataSource = new DataSourceConfig.Builder(
                "jdbc:mysql://localhost:3306/ncsys_dev?useSSL=false&serverTimezone=Asia/Shanghai",
                "root",
                "123456")
                .build();

        // ===================== 2. 全局配置 =====================
        GlobalConfig globalConfig = new GlobalConfig.Builder()
                .outputDir(System.getProperty("user.dir") + "/src/main/java") // 输出目录（建议使用绝对路径）
                .author("sysAdmin") // 作者信息（生成类注释中使用）
                .disableOpenDir() // 是否打开输出目录（默认true）
                /*
                 * 时间类型策略
                 * DateType.ONLY_DATE
                 * DateType.SQL_PACK
                 * DateType.TIME_PACK: LocalDateTime
                 */
                .dateType(DateType.ONLY_DATE)
                .commentDate("yyyy-MM-dd HH:mm") // 注释日期格式
                //.enableSwagger() // 开启Swagger支持（需要自行引入swagger依赖）
                .build();

        // ===================== 3. 包配置 =====================
        PackageConfig packageConfig = new PackageConfig.Builder()
                .parent("com.dc.ncsys_springboot") // 父包名（组织名+项目名）
//                .moduleName("system") // 模块名（可选，会作为父包下的子包）
                // 各层包名配置（按需修改）
                .entity("daoVo")       // 实体类包名
                .mapper("mapper")       // Mapper接口包名
                .service("service")     // Service接口包名
                .serviceImpl("service.impl") // Service实现类包名
                .controller("controller")  // Controller包名
                .build();

        // ===================== 4. 策略配置 =====================
        StrategyConfig strategyConfig = new StrategyConfig.Builder()
                // ----------------- 全局策略 -----------------
                .addInclude(tableName) // 需要生成的表名（多个表用逗号分隔）
                .enableSkipView() // 开启跳过视图（默认false）
                //.disableSqlFilter() // 禁用SQL过滤（默认true）
                .addTablePrefix(tablePrefix) // 表前缀过滤（例如设置"sys_"会去除表前缀）

                // ----------------- 实体类策略 -----------------
                .entityBuilder()
//                .enableLombok() // 开启Lombok
                .enableLombok(new ClassAnnotationAttributes("@Data", "lombok.Data"))
                .enableChainModel() // 开启链式模型（即setter返回this）
//                .enableColumnConstant() // 表字段名转换为驼峰命名（默认true）
//                .naming(NamingStrategy.underline_to_camel) // 命名策略（默认下划线转驼峰命名）
//                .columnNaming(NamingStrategy.underline_to_camel) // 列名命名策略(默认为 null，未指定按照 naming 执行)
                .enableFileOverride() // 允许覆盖已有文件
                //.logicDeleteColumnName("deleted") // 逻辑删除字段名（需与数据库字段对应）
                //.enableActiveRecord() // 开启 ActiveRecord 模型（需继承Model类）
                //.idType(IdType.AUTO) // 全局主键类型（需与数据库对应）
                .formatFileName("%sDo") // 格式化文件名称（示例：%sEntity）
                .build()

                // ----------------- Mapper策略 -----------------
                .mapperBuilder()
//                .enableMapperAnnotation() // 开启 @Mapper 注解 deprecated
                .enableBaseResultMap() // 开启 BaseResultMap 生成
                .enableBaseColumnList() // 开启 BaseColumnList
                .formatMapperFileName("%sMapper") // 格式化Mapper文件名称
                .formatXmlFileName("%sMapper") // 格式化Xml文件名称
                .enableFileOverride() // 允许覆盖已有文件
                .build()

                // ----------------- Service策略 -----------------
                .serviceBuilder()
                .formatServiceFileName("%sService") // 格式化Service接口名称
                .formatServiceImplFileName("%sServiceImpl") // 格式化Service实现类名称
                .build()

                // ----------------- Controller策略 -----------------
                .controllerBuilder()
                .enableRestStyle() // 开启生成 @RestController
//                .enableHyphenStyle() // 开启驼峰转连字符（@RequestMapping("/userInfo") -> "/user-info"）
                .formatFileName("%sController") // 格式化Controller名称
                .build();

        // ===================== 5. 创建生成器 =====================
        AutoGenerator generator = new AutoGenerator(dataSource)
                .global(globalConfig)
                .packageInfo(packageConfig)
                .strategy(strategyConfig);

        // ===================== 6. 执行生成 =====================
        generator.execute();

        System.out.println("代码生成完成！");
    }
}