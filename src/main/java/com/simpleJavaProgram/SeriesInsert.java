package com.simpleJavaProgram;

public class SeriesInsert {


    public static void main(String[] args) {
        将Excel数据转为Insert语句();
    }



    private static void 将Excel数据转为Insert语句() {

        String[] fields = fields_str.replaceAll("\n", "").split("\t");
        int fieldsNum = fields.length;
        String[] all = records_str.split("\t");
        打印insert固定开头(tableName, fields, fieldsNum);
        for (int i = 0; i < fieldsNum-1; i++) {
            System.out.print("'"+all[i].replaceAll("\n", "<换行>") + "', ");
        }
        System.out.println("'"+all[fieldsNum-1].substring(0, all[fieldsNum-1].lastIndexOf("\n")).replaceAll("\n", "<换行>") + "');");
        for (int i = fieldsNum; i < all.length; i+=fieldsNum-1) {
            打印insert固定开头(tableName, fields, fieldsNum);
            System.out.print("'"+all[i-1].substring(all[i-1].lastIndexOf("\n")+1).replaceAll("\n", "<换行>") + "', ");
            for (int j = 0; j < fieldsNum-2; j++) {
                System.out.print("'"+all[i+j].replaceAll("\n", "<换行>") + "', ");
            }
            System.out.println("'"+all[i+fieldsNum-2].substring(0, all[i+fieldsNum-2].lastIndexOf("\n")).replaceAll("\n", "<换行>") + "');");
        }

    }

    private static void 打印insert固定开头(String tableName, String[] fields, int fieldsNum) {
        System.out.print("INSERT INTO " + tableName + "(");
        for (int i = 0; i < fields.length-1; i++) {
            System.out.print(fields[i] + ", ");
        }
        System.out.print(fields[fieldsNum -1] + ") VALUES (");
    }



    //  表名
    static String tableName = "D_SERIES";

    // 字段名
    static String fields_str = "XL_SIMPLE\tXL_FULL\tNM_JP\tNM_CN\tWZ_GW\tFH_PATTERN\tFH_RULER\tGD_PRODUCER\tGD_MOSAIC\tGD_SUB_SERIES\tMS_RESUME\tMS_DETAIL\n";

    // 数据
    static String records_str = "n\tn\tn\tn\t\tnxxxx\tn开头的已经排到八百多，如n0680，都是多人演出。\tTokyo-Hot\t步兵\t\tn开头的已经排到八百多，如n0680，都是多人演出。\t \n" +
            "k\tk\tk\tk\t\tkxxxx\tK开头的为素人片，多以二人演出。\tTokyo-Hot\t步兵\t\tK开头的为素人片，多以二人演出。\t \n" +
            "1pon\t1Pondo\t一本道\t一本道\t\tMMddyy-xxx\t番号多为一串数字，如022313-538,前六位数字表示发行日期，后三位是影片编号。\t1Pondo\t步兵\tDrama Collection（ドラマコレクション）,Gravure Collection,Princess Collection,Model Collection\t番号多为一串数字，如022313-538,前六位数字表示发行日期，后三位是影片编号。\t \n" +
            "carib\tCaribbeancom\tカリビアンコム\t加勒比\t\tMMddyy-xxx\t番号多为一串数字，如022313-538,前六位数字表示发行日期，后三位是影片编号\tCaribbean\t步兵\t密室**,女热大陆（Erotic Documentary）,THE 未公开,新入社员のお仕事（School Girl Uniform Club）,禁じられた关系,极上本物泡姫物,见晴らし最高,初めてのAV（My First Time Porn Filming）,美★ジーンズ,カリビアンキューティー（Caribbean Cutie）,夏の想い出（Sweet Summer Memory ）\t番号多为一串数字，如022313-538,前六位数字表示发行日期，后三位是影片编号\t \n" +
            "Sky\tSky\t空天使\t空天使\t\tSky-xxx\tsky加3位数字序号组成\tSky High Entertainment\t步兵\tSkyAngel,GoldAngel,好色妻降临,美熟女,X COLLECTION系列\tsky加3位数字序号组成\t \n" +
            "RHJ\tRed Hot Jam\t红热果酱\t红热果酱\t\tRHJ-xxx\tRHJ加3位数字序号组成\tRED HOT Collection\t步兵\t\tRHJ加3位数字序号组成\t \n" +
            "Red\tRed Hot Fetish Collection\t红色\t红色\t\tRED-xxx\tRED加3位数字序号组成\tRED HOT Collection\t步兵\t\"Red Hot Fetish Collection\n" +
            "Hot Debut Collection File\"\tRED加3位数字序号组成\t \n" +
            "CRD\tRED HOT IDOL\t红色\t红色\t\tCRD-xxx\tCRD加3位数字序号组成\tRED HOT Collection\t步兵\tRED HOT IDOL\tCRD加3位数字序号组成\t \n" +
            "SMD\tSMD\t超级名模\t超级名模\t\tSMD-x\tSMD加序号组成\tSuper Model Media\t步兵\t\tSMD加序号组成\t\n" +
            "SMDV\tSMDV\t超级名模\t超级名模\t\tSMDV-x\tSMDV加序号组成\tSuper Model Media\t步兵\t\tSMDV加序号组成\t\n" +
            "SMBD\tSMBD\t超级名模\t超级名模\t\tSMBD-x\tSMBD加序号组成\tSuper Model Media\t步兵\t\tSMBD加序号组成\t\n" +
            "SM3D2DBD\tSM3D2DBD\t超级名模\t超级名模\t\tSM3D2DBD-x\tSM3D2DBD加序号组成\tSuper Model Media\t步兵\t\tSM3D2DBD加序号组成\t\n" +
            "CWP\tCATWALK POISON\t猫步毒药\t猫步毒药\t\tCWP-x\tCWP加序号组成\tCATWALK Entertainment\t步兵\t\tCWP加序号组成\t\n" +
            "CWPBD\tCATWALK POISON BD\t猫步毒药\t猫步毒药\t\tCWPBD-x\tCWP加序号组成\tCATWALK Entertainment\t步兵\t\tCWP加序号组成\t\n" +
            "CWDV\tCATWALK POISON DV\t猫步毒药\t猫步毒药\t\tCWDV-x\tCWDV加序号组成\tCATWALK Entertainment\t步兵\t\tCWDV加序号组成\t\n" +
            "CW3D2DBD\tCATWALK POISON DV\t猫步毒药\t猫步毒药\t\tCW3D2DBD-x\tCWDV加序号组成\tCATWALK Entertainment\t步兵\t\tCWDV加序号组成\t\n" +
            "MX\tMX\tMX\tMX\t\tMX-x\t\tMUGEN Entertainment\t步兵\t\t\t\n" +
            "MG\tMG\tMG\tMG\t\tMG-x\t\tMUGEN Entertainment\t步兵\t\t\t\n" +
            "MW\tMW\tMW\tMW\t\tMW-x\t\tMUGEN Entertainment\t步兵\t\t\t\n" +
            "MUD\tMUD\tMUD\tMUD\t\tMUD-x\t\tMUGEN Entertainment\t步兵\t\t\t\n" +
            "MKD-S\tMKD-S\tMKD-S\tMKD-S\t\tMKD-S-x\t\tMUGEN Entertainment\t步兵\t\t\t\n" +
            "MKBD-S\tMKBD-S\tMKBD-S\tMKBD-S\t\tMKBD-S-x\t\tMUGEN Entertainment\t步兵\t\t\t\n" +
            "s2m\ts2m\ts2m\ts2m\t\ts2mxxx\t\tstage 2 media\t步兵\t\t\t\n" +
            "s2mbd\ts2mbd\ts2mbd\ts2mbd\t\ts2mbdxxx\t\tstage 2 media\t步兵\t\t\t\n" +
            "KP\tKamikaze Premium\t神风特攻队\t神风特攻队\t\tKP-x\t\tKamikaze Entertainment\t步兵\t\t\t\n" +
            "KS\tKamikaze Street\t神风街\t神风街\t\tKS-x\t\tKamikaze Entertainment\t步兵\t\t\t\n" +
            "KG\tKamikaze Girls\t神风女孩\t神风女孩\t\tKG-x\t\tKamikaze Entertainment\t步兵\t\t\t\n" +
            "FH\tFetish Hood\t恋物癖\t恋物癖\t\tFH-x\t\tOriental Dream\t步兵\t\t\t\n" +
            "TRP\tPLATINUM\t\t\t\tTRP-x\t\tTora-Tora-Tora\t步兵\t\t\t\n" +
            "TRG\tGold\t\t\t\tTRG-x\t\tTora-Tora-Tora\t步兵\t\t\t\n" +
            "HEYZO\tHEYZO\t\t\t\tHEYZO-xxxx\t\tHEYZO\t步兵\t\t\t\n" +
            "KSC\tKOKESHI COWGIRL\t\t\t\tKSC-x\t\tKOKESHI\t步兵\t\t\t\n" +
            "YZD\tYZD\t\t\t\tYZD-xxx\t\tYuzu\t步兵\t\t\t\n" +
            "GOD\tGOD\t\t\t\tGOD-xxx\t\tYuzu\t步兵\t\t\t\n" +
            "YUU\tYUU\t\t\t\tYUU-xxx\t\tYuzu\t步兵\t\t\t\n" +
            "EMP\tEMP\t\t\t\tEMP-xxx\t\tEMPIRE\t步兵\t\t\t\n" +
            "C0930-pla\tC0930-pla\t\t\t\tC0930-pla xxxx\t\tC0930\t步兵\t\t\t\n" +
            "C0930-gol \tC0930-gol \t\t\t\tC0930-gol xxxx\t\tC0930\t步兵\t\t\t\n" +
            "C0930-gitozuna\tC0930-gitozuna\t\t\t\tC0930-gitozuna xxxx\t\tC0930\t步兵\t\t\t\n" +
            "PB\tPB\t\t\t\tPB-xxx\t\tPink Puncher\t步兵\t\t\t\n" +
            "DRC\tDRC\t\t\t\tDRC-xxx\t\tCATCHEYE\t步兵\t\t\t\n" +
            "DRG\tDRG\t\t\t\tDRG-xxx\t\tCATCHEYE\t步兵\t\t\t\n" +
            "DSAM\tDSAM\t\t\t\tDSAM-x\t\tSAMURAI PORN\t步兵\t\t\t\n" +
            "EB\tEB\t\t\t\tEB-x\t\tSAMURAI PORN\t步兵\t\t\t\n" +
            "SMR\tSMR\t\t\t\tSMR-x\t\tSAMURAI PORN\t步兵\t\t\t\n" +
            "SP\tSP\t\t\t\tSP-x\t\tSAMURAI PORN\t步兵\t\t\t\n" +
            "xxx-av\txxx-av\t\t\t\txxx-av xxxxx\t\tXXX-AV\t步兵\t\t\t\n" +
            "muramura\tmuramura\t\t\t\tmuramura MMddyy-xxx\t\tMuraMura\t步兵\t\t\t\n" +
            "MKD\tMKD\t\t\t\tMKD-x\t\tRUBY人妻\t步兵\t\t\t\n" +
            "OPC\tOPC\t\t\t\tOPC-xxx\t\tOne Piece Entertainment\t步兵\t\t\t\n" +
            "Jukujo-Club\tJukujo-Club\t\t\t\tJukujo-Club-xxxx\t\tJukujo-Club\t步兵\t\t\t\n" +
            "PT\tPT\t\t\t\tPT-x\t\tStudio Teriyaki\t步兵\t\t\t\n" +
            "BT\tBT\t\t\t\tBT-x\t\tStudio Teriyaki\t步兵\t\t\t\n" +
            "CT\tCT\t\t\t\tCT-x\t\tStudio Teriyaki\t步兵\t\t\t\n" +
            "DT\tDT\t\t\t\tDT-x\t\tStudio Teriyaki\t步兵\t\t\t\n" +
            "SMJ\tSMJ\t\t\t\tSMJ-x\t\tStudio Teriyaki\t步兵\t\t\t\n" +
            "ST\tST\t\t\t\tST-x\t\tStudio Teriyaki\t步兵\t\t\t\n" +
            "mesubuta\tmesubuta\t\t\t\tmesubuta yyMMdd_xxx_01\t\tMesubuta\t步兵\t\t\t\n" +
            "AV9898\tAV9898\t\t\t\tAV9898-xxxx\t\tAV9898\t步兵\t\t\t\n" +
            "G-Queen\tG-Queen\t\t\t\tG-Queen-xxx\t\tG-Queen\t步兵\t\t\t\n" +
            "pacopacomama\tpacopacomama\t\t\t\tpacopacomama-MMddyy\t\tPacopacomama\t步兵\t\t\t\n" +
            "gach\tgach\t\t\t\tgachixxx-HD\t\tGachinco\t步兵\t\t\t\n" +
            "10musume\t10musume\t\t\t\t10musume-MMddyy_01\t\t10musume \t步兵\t\t\t\n" +
            "SSKJ\tSSKJ\t\t\t\tSSKJ-xxx \t\tSASUKE\t步兵\t\t\t\n" +
            "1000giri\t1000giri\t\t\t\t1000giri-yyMMdd\t\t1000giri\t步兵\t\t\t\n";


}
