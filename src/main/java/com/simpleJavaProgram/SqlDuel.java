package com.simpleJavaProgram;

public class SqlDuel {

    public static void main(String[] args) {
//        将字段装换为Json字符串对象();
//        将Excel数据装换为Json字符串对象();
//        将Excel数据小写字段装换为Json字符串对象();
        将字段装换为CreateSql语句();
//        将驼峰装换为下划线();
//        将下划线转为驼峰();
    }

    private static void 将Excel数据小写字段装换为Json字符串对象() {
        String str = "fhSeries\tCaribbean\n" +
                "fhSysFanhao\t180119-587\n" +
                "fhPrimitiveFanhao\t011918-587\n" +
                "ypSubSeries\t未收录\n" +
                "ypFilmTitle\t僕の彼女が如月ジュリだったら ～主導権とチンコを握られている僕～\n" +
                "ypIssueYmd\t未收录\n" +
                "yyCoexistActressNum\t1\n" +
                "yyActressNo\t2022121001\n" +
                "yyActress\t如月ジュリ\n" +
                "yyCoexistActorNum\t1\n" +
                "yyActorNo\t一般男优\n" +
                "yyLadyboyNum\t0\n" +
                "yyLadyboyNo\t无\n" +
                "yyBeastType\t无\n" +
                "jqSceneType\t家庭\n" +
                "jqRoleType\t夫妻\n" +
                "jqStoryType\t夫妻性爱\n" +
                "analSexLv\t无\n" +
                "analEnemaLv\t无\n" +
                "analInjectSemenLv\t无\n" +
                "analCoexistPenisNum\t0\n" +
                "analEndoscopyYn\t否\n" +
                "xjInjectSemenLv\t内射\n" +
                "xjCoexistPenisNum\t1\n" +
                "xjEndoscopyVaginaYn\t否\n" +
                "kjOralSexLv\t自愿口交\n" +
                "kjMonthCoexistPenisNum\t9999\n" +
                "kjMouthEjaculateLv\t无\n" +
                "kjSwallowSemenLv\t无\n" +
                "tsTieLv\t无\n" +
                "tsWhiteTigerLv\t人工白虎\n" +
                "tsFaceEjaculateLv\t无\n" +
                "tsFemaleEjaculaYn\t否\n" +
                "tsDrindManUrineYn\t否\n" +
                "tsDrindWomanUrineYn\t否\n" +
                "msResume\t \n" +
                "msDetail\t \n" +
                "rcRecordTime\t \n" +
                "rcRecorder\twr\n" +
                "rcLastModifiedTime\t \n" +
                "rcLastModifier\twr\n" +
                "rcDataStatus\t1\n";

        System.out.println("{");
        String[] rows = str.split("\n");
        for (String row : rows) {
            String[] fields = row.split("\t");
            System.out.println("\t\""+ fields[0]+"\" : \"" + fields[1] + "\",");
        }
        System.out.println("}");

    }

    private static void 将下划线转为驼峰() {

        String str = "JQ_DRESS\n" +
                "TS_FACE_RIDING_YN\n" +
                "YP_DURATION\n";
        String[] rows = str.split("\n");
        for (String row : rows) {
            String[] words = row.split("_");
            System.out.print(words[0].toLowerCase());
            for (int i = 1; i < words.length; i++) {
                System.out.print(words[i].substring(0,1) + words[i].substring(1).toLowerCase());
            }
            System.out.println("");
        }
    }

    private static void 将驼峰装换为下划线() {
        String str = "actressNo\n" +
                "nameStage\n" +
                "nameJpTrue\n" +
                "nameCn\n" +
                "nameEn\n" +
                "nameOther\n" +
                "birthYmd\n" +
                "birthCountry\n" +
                "birthPlace\n" +
                "yzAppearanceIndex\n" +
                "yzAppearanceType\n" +
                "scHeight\n" +
                "scFatLv\n" +
                "scWeight\n" +
                "scCup\n" +
                "scTitSizeLv\n" +
                "scBust\n" +
                "scLegsType\n" +
                "scLegsThickness\n" +
                "scHipType\n" +
                "scHipDistance\n" +
                "cdDebutYmd\n" +
                "cdRetireYmd\n";

        String[] rows = str.split("\n");
        for (String row : rows) {
            String s = row.replaceAll("[A-Z]", "_$0").toUpperCase();
            System.out.println(s);
        }


    }

    private static void 将字段装换为Json字符串对象() {
        String str = "";
        System.out.println("{");
        String[] rows = str.split("\n");
        for (String row : rows) {
            String[] fields = row.split("\t");
            String[] words = fields[0].split("_");
            System.out.print("\t\""+  words[0].toLowerCase());
            for (int i = 1; i < words.length; i++) {
                System.out.print(words[i].substring(0,1));
                System.out.print(words[i].substring(1).toLowerCase());
            }
            System.out.print("\" : \"" + fields[1]);
            if (fields.length > 2 && !"".equals(fields[2])) {
                System.out.print(": " + fields[2]);
            }
            System.out.println("\",");

        }
        System.out.println("}");

    }

    private static void 将字段装换为CreateSql语句() {
        String str = "TS_KISS_YN\t接吻与否\t是, 否\n" +
                "TS_BIG_BALANUS_YN\t大龟头与否\t是, 否\n" +
                "TS_PENIS_SIZE_LV\t鸡巴大小级别\t0-未收录, 1-极小, 2-小, 3-正常, 4-大, 5-巨大\n" +
                "TS_TIT_SIZE_LV\t奶子大小级别\t0-未收录, 1-极小, 2-小, 3-正常, 4-大, 5-巨大\n";

        String[] rows = str.split("\n");
        for (String row : rows) {
            String[] fields = row.split("\t");
            int i1 = 8 - fields[0].length()/4 ;
            System.out.print("\t" + fields[0]);
            for (int i = 0; i < i1; i++) {
                System.out.print("\t");
            }
            if (fields[0].endsWith("NUM")){
                System.out.print("\t\t\tINT" + "\t\t\t\t\tNOT NULL"+ "\t\tDEFAULT 9999\t\t\t");
            } else if (fields[0].equals("MS_RESUME")){
                System.out.print("\t\t\tVARCHAR(300)" + "\t\tNOT NULL"+ "\t\tDEFAULT ' '\t\t\t\t");
            } else if (fields[0].equals("MS_DETAIL")){
                System.out.print("\t\t\tVARCHAR(1000)" + "\t\tNOT NULL"+ "\t\tDEFAULT ' '\t\t\t\t");
            } else if (fields[0].endsWith("LV")){
                System.out.print("\t\t\tVARCHAR(30)" + "\t\t\tNOT NULL"+ "\t\tDEFAULT ' '\t\t\t\t");
            } else if (fields[0].endsWith("STATUS")){
                System.out.print("\t\t\tCHAR(1)" + "\t\t\t\tNOT NULL"+ "\t\tDEFAULT '0'\t\t\t\t");
            } else if (fields[0].endsWith("TYPE")){
                System.out.print("\t\t\tVARCHAR(30)" + "\t\t\tNOT NULL"+ "\t\tDEFAULT '未收录'\t\t\t");
            } else if (fields[0].endsWith("YMD")){
                System.out.print("\t\t\tCHAR(8)" + "\t\t\t\tNOT NULL"+ "\t\tDEFAULT '未收录'\t\t\t");
            } else if (fields[0].endsWith("TIME")){
                System.out.print("\t\t\tTIMESTAMP" + "\t\t\tNOT NULL"+ "\t\tDEFAULT CURRENT_TIMESTAMP");
            } else {
                System.out.print("\t\t\tVARCHAR(50)" + "\t\t\tNOT NULL"+ "\t\tDEFAULT '未收录'\t\t\t");
            }
            System.out.print( "\t\t\tCOMMENT '" + fields[1]);
            if (fields.length > 2 && !"".equals(fields[2])) {
                System.out.print(": " + fields[2]);
            }
            System.out.println("',");
        }
    }

    private static void 将Excel数据装换为Json字符串对象() {
        String str = "keyActressNo\t2022121001\n" +
                "nameStage\t如月ジュリ\n" +
                "nameJpTrue\t未收录\n" +
                "nameCn\t未收录\n" +
                "nameEn\t未收录\n" +
                "nameOther\t未收录\n" +
                "birthYmd\t未收录\n" +
                "birthCountry\t日本\n" +
                "birthPlace\t未收录\n" +
                "yzAppearanceIndex\t8\n" +
                "yzAppearanceType\t未收录\n" +
                "scHeight\t未收录\n" +
                "scFatLv\t未收录\n" +
                "scWeight\t未收录\n" +
                "scCup\tA\n" +
                "scTitSizeLv\t很小\n" +
                "scBust\t未收录\n" +
                "scLegsType\t微X\n" +
                "scLegsThickness\t较细\n" +
                "scHipType\t较囵\n" +
                "scHipDistance\t0.5\n" +
                "cdDebutYmd\t未收录\n" +
                "cdRetireYmd\t未收录\n" +
                "rcRecordTime\t20221210\n" +
                "rcRecorder\twr\n" +
                "rcLastModifiedTime\t20221210\n" +
                "rcLastModifier\twr\n" +
                "rcDataStatus\t1\n";

        System.out.println("{");
        String[] rows = str.split("\n");
        for (String row : rows) {
            String[] fields = row.split("\t");
            String[] words = fields[0].split("_");
            System.out.print("\t\""+  words[0].toLowerCase());
            for (int i = 1; i < words.length; i++) {
                System.out.print(words[i].substring(0,1));
                System.out.print(words[i].substring(1).toLowerCase());
            }
            System.out.println("\" : \"" + fields[1] + "\",");

        }
        System.out.println("}");
    }
}
