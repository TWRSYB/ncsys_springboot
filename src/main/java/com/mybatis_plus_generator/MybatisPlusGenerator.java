package com.mybatis_plus_generator;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
// mybatis 3.5.6 版本下不可用
public class MybatisPlusGenerator {

//    public static void main(String[] args) {
//
//        //  1: 代码生成器
//        AutoGenerator autoGenerator = new AutoGenerator();
//        // 全局配置
//        GlobalConfig gc = new GlobalConfig();//注意导入包的时候， 是generator包当中的对象
//        String projectPath = System.getProperty("user.dir");
//        gc.setOutputDir(projectPath + "/src/main/java");
//        gc.setAuthor("sysAdmin");//设置作者
//        gc.setOpen(true);//生成时候是否打开资源管理器
//        gc.setFileOverride(false);//重新生成文件时是否覆盖
//        gc.setServiceName("%sService"); //生成service时候去掉I
//        // gc.setSwagger2(true); 实体属性 Swagger2 注解
//        autoGenerator.setGlobalConfig(gc);
//
//
//        //  2: 数据源配置
//        DataSourceConfig dsc = new DataSourceConfig();
//        dsc.setUrl("jdbc:mysql://localhost:3306/ncsys_dev?useUnicode=true&useSSL=false&characterEncoding=utf8&allowPublicKeyRetrieval=true");
//        // dsc.setSchemaName("public");
//        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
//        dsc.setUsername("root");
//        dsc.setPassword("123456");
//        dsc.setDbType(DbType.MYSQL);
//        autoGenerator.setDataSource(dsc);
//
//
//        //  3: 包配置
//        PackageConfig pc = new PackageConfig();
//        pc.setModuleName(null);
//        pc.setParent("com.mybatis_plus_generator");
//        pc.setController("controller");
//        pc.setEntity("daoVo");
//        pc.setService("service");
//        pc.setMapper("mybatis");
//        autoGenerator.setPackageInfo(pc);
//
////        //  4: 策略配置
////        StrategyConfig strategy = new StrategyConfig();
////        strategy.setNaming(NamingStrategy.underline_to_camel);
////        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
////        strategy.setSuperEntityClass("com.baomidou.mybatisplus.extension.activerecord.Model");
////        strategy.setEntityLombokModel(true);
////        strategy.setRestControllerStyle(true);
////        strategy.setEntityLombokModel(true);
////
////        String tableNames="t_checkgroup,t_checkgroup_checkitem,t_checkitem,t_member,t_menu," +
////                "t_order,t_ordersetting," +
////                "t_permission,t_role,t_role_menu," +
////                "t_role_permission,t_setmeal,t_setmeal_checkgroup,t_user,t_user_role";
////        strategy.setInclude(tableNames.split(","));
////        strategy.setControllerMappingHyphenStyle(true);
////        strategy.setTablePrefix("t_");
////        autoGenerator.setStrategy(strategy);
//
//        // 策略配置
//        StrategyConfig strategy = new StrategyConfig();
//        strategy.setNaming(NamingStrategy.underline_to_camel);
//        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
//        strategy.setSuperEntityClass("com.baomidou.mybatisplus.extension.activerecord.Model");
//        strategy.setEntityLombokModel(true);
//        strategy.setRestControllerStyle(true);
//        strategy.setEntityLombokModel(true);
//
//        String  tableNames="m_user";
//        strategy.setInclude(tableNames.split(","));
//        strategy.setControllerMappingHyphenStyle(true);
//        strategy.setTablePrefix("m_");
//        autoGenerator.setStrategy(strategy);
//
//        //执行
//        autoGenerator.execute();
//
//    }
}
