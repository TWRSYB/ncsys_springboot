package com.simpleJavaProgram;

public class ProducerInster {
    public static void main(String[] args) {
//        System.out.println("a\\na");
        将Excel数据转为Insert语句();
    }



    private static void 将Excel数据转为Insert语句() {

        String[] fields = fields_str.replaceAll("\n", "").split("\t");
        int fieldsNum = fields.length;
        String[] all = records_str.split("\t");
//        List<List<String>> recordList = new ArrayList<>();
//        List<String> record1 = new ArrayList<>();
        打印insert固定开头(tableName, fields, fieldsNum);
        for (int i = 0; i < fieldsNum-1; i++) {
//            record1.add(all[i]);
            System.out.print("'"+all[i].replaceAll("\n", "<换行>") + "', ");
        }
        System.out.println("'"+all[fieldsNum-1].substring(0, all[fieldsNum-1].lastIndexOf("\n")).replaceAll("\n", "<换行>") + "');");
//        record1.add(all[fieldsNum-1].substring(0, all[fieldsNum-1].lastIndexOf("\n")));
//        recordList.add(record1);
//        List<String> recordn = new ArrayList<>();
        for (int i = fieldsNum; i < all.length; i+=fieldsNum-1) {
            打印insert固定开头(tableName, fields, fieldsNum);
            System.out.print("'"+all[i-1].substring(all[i-1].lastIndexOf("\n")+1).replaceAll("\n", "<换行>") + "', ");
//            recordn.add(all[i-1].substring(all[i-1].lastIndexOf("\n")));
            for (int j = 0; j < fieldsNum-2; j++) {
//                recordn.add(all[i+j]);
                System.out.print("'"+all[i+j].replaceAll("\n", "<换行>") + "', ");
            }
//            recordn.add(all[i+fieldsNum-2].substring(0, all[i+fieldsNum-2].lastIndexOf("\n")));
            System.out.println("'"+all[i+fieldsNum-2].substring(0, all[i+fieldsNum-2].lastIndexOf("\n")).replaceAll("\n", "<换行>") + "');");
//            recordList.add(recordn);
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
    static String tableName = "D_PRODUCER";

    // 字段名
    static String fields_str = "CS_INVESTOR\tCS_PRODUCER\tNM_JP\tNM_CN\tYY_START_YMD\tYY_STOP_YMD\tXL_SERIES\tMS_RESUME\tMS_DETAIL\n";

    // 数据
    static String records_str = " \tTokyo-Hot\tTokyo-Hot\t东京热\t2003\t \t\"n开头的已经排到八百多，如n0680，都是多人演出。\n" +
            "K开头的为素人片，多以二人演出。\n" +
            "主要系列：\n" +
            "‘撮りおろし彻底陵辱ビデオ’\n" +
            "‘チーム木村 ‐ 饵食珍道中’\n" +
            "‘エクストラビデオ\"\t\"Tokyo-Hot 是一家在美国注册的著名日本成人视频制造商 [1]。成立于2003年。影片均为高清无码视频。在官方网站上说明，内容为220万日裔美国人及美国公众服务。\n" +
            "出演东京热的AV女优一般面容，身材都很好，但多数名气不大，其中比较有影响力的有赤西凉、村上里沙、石川铃华、大泽佑香、真白希実、石黑京香、樱井莉亚等。\n" +
            "其出品的内容主要偏硬调色情，涉及潮吹、无套性交、体内射精、**、口交、群交甚至性虐待等。\"\t\"基本简介\n" +
            "\n" +
            "东京热（英语：Tokyo-Hot）是一家在美国注册的著名视频日本成人制造商。成立于2003年。影片均为高清无码视频。在官方网站上说明，内容为220万日裔美国人及美国公众服务。其出品的内容主要偏硬调色情，涉及潮吹、无套性交、体内射精、颜射、口交、群交甚至性虐待等。因日本成人影片市场浮滥过度，东京热在2010年的市场销售量比2009年减少了15%。tokyo hot（东京热），这家在2003年设立的公司可说是最早发现日本法律漏洞的片商，它里面的作品通常没有经过日本政府的审查，而是送到了美国加州去审查。众所皆知，美国对于所谓的「成人影片」的审查可以说是非常宽松，所以东京热拍片根本放胆来干，中/出、肛/交，轮/奸样样来，有些时候看它们的片子会忍不住想关掉，实在是因为口味太重，受不了了！ \n" +
            "\n" +
            "也许有些人会问，片子口味这么重，女优受得了吗？然后，它们的演出有没有造假的空间？坦白说，从男优中/出、肛/交的片段来看，演出（真的插入）是毋庸置疑的，也就是说性交和肛交的真实性应该是不用怀疑的，至于中出方面从剪接的专业方面来看，问题也不大，也就是说，东京热中的中出片段看不出有剪接的空间，真实的可能性极高。就因为如此，让观众备加同情那些演出的女优，事实上，你别以为那些公司设在美国，女优就可以出国去拍片顺便玩一趟，那是不可能的。\n" +
            "\n" +
            "不管是步兵还是骑兵片，大家都一样是在日本拍片，不同的是步兵片是送到美国去审查，再进口加以发售，女优不仅没有出国爽到，还要演出更加苛刻的内容！据说演出东京热的女优在片酬方面是比一般单体女优来得高，但拿得多被玩得也惨，这钱实在不好赚。而这家公司虽然目前确定经营方式不会受到干扰，但在日本还是相当低调，不但没有登记实体的公司地址，就连对外连络也全部透过网络，这也让它们在作品逐渐打开名声的现在，增添了令人好奇的神秘感\n" +
            "\n" +
            "\n" +
            "\n" +
            "公司演员\n" +
            "出演东京热的AV女优一般面容，身材都很好，但多数名气不大，其中比较有影响力的有赤西凉、村上里沙、石川铃华、大泽佑香、真白希実、石黑京香、樱井莉亚、小爱等。另外，东京热系列有一点很独特，某些女优仅在东京热露过几回面，便名声鹊起，尔后却不再现身AV界，如藤泽安奈，藤木凉子等。一般情况下，人们只是知道这些女优的名字，具体信息了解的很少。\n" +
            "\n" +
            "东京热第445集出现了一个名叫“松田爱华”的中国台湾省出身的女优，东京热官方网站上称其出生于台湾省台北市。影片开头有她教授中文的情节。不过有网友依据她教授的简体中文及表达方式推断她实际上来自于中国大陆。\n" +
            "\n" +
            "\n" +
            "社会评价\n" +
            "1999年因中出片段无剪切，真实性较高，故有人认为相比其他（如港台地区制作的）假性交的18+电影更让人感到新鲜。持反对意见者则认为东京热系列口味过重，其中女优被虐的一些情节会使观众感到不适，并使人对女优的身体状况表示担忧。\n" +
            "\n" +
            "在其大部份影片内容情节上，可约略分为两大部份，前半段为一男一女的性交片段，后半段则以道具玩弄（或虐待）女优后再以多名男性与之性交；唯东京热早期的较为温和的拍摄风格在2007年左右开始逐渐转为强烈（女优的长相样貌水平亦逐渐提高），尤以影片后半段与女优性交之男性人数增加最为明显。但因影片内容主线几为相同，予人单调感，故东京热亦开始每年选取几名面貌姣好的女优拍摄多人杂交之作品。\n" +
            "\n" +
            "整体而言，东京热与其他出版厂商最大之不同在于其影片风格倾向赤裸裸的呈现私处与体内射后之特写，并在各种拍摄场景、角色设定之下透过充分玩弄（或虐待）女优给予观赏者强烈的幻想满足。[1]\"\n" +
            " \t1Pondo\t一本道\t一本道\t2001\t\t\"番号多为一串数字，如022313-538,前六位数字表示发行日期，后三位是影片编号。\n" +
            "Drama Collection（ドラマコレクション）\n" +
            "Gravure Collection\n" +
            "Princess Collection\n" +
            "Model Collection\"\t一本道[1]（英语：1Pondo）是一家在美国注册的日本成人影片制造商[2]，以出品无码影片为卖点，并且提供线上付费下载高分辨率电视画面素质影片，从2001年开始营运，至今已经累计发行2900多部。一本道的作品皆无码，自称是同行业中画面尺寸最大及画面质量最好的。其作品包含大量的体内射精画面。此外，该公司常推出新类型的作品。目前一本道的更新速度是平均每天新增一部新片。\t\" 著名演员\n" +
            "一般情况下，人们只是知道这些女优的名字，具体信息了解的很少。\n" +
            "\n" +
            "出演一本道的AV女优一般来说，面容与身材都很好，但多数名气不大，其中比较有影响力的有、尾野真知子、赤西凉、村上里沙、石川铃华、大泽佑香、真白希实、石黑京香、樱井莉亚等。 另外，一本道系列有一点很独特，某些女优仅在一本道露过几回面，便名声鹊起，尔后却不再现身于成人影片界，如藤泽安奈和藤木凉子等。\n" +
            "\n" +
            "\n" +
            "海外影响\n" +
            "一本道对日本海外的影响较大，网站访问者约七成来自日本本土，其余来自日本海外，以东亚、东南亚和美国地区为主。\n" +
            "\n" +
            "系列作品\n" +
            "主条目：一本道作品列表\n" +
            "Drama Collection（ドラマコレクション）\n" +
            "天使と悪魔\n" +
            "裸之履历书（はだかの履歴書）\n" +
            "今夜のおかず\n" +
            "亲友の彼女\n" +
            "グラマラス\n" +
            "ときめき\n" +
            "CLUB ONE\n" +
            "スジッ娘倶楽部\n" +
            "肉便器育成所\n" +
            "动きウーマン\n" +
            "社团活动日志（部活日誌）\n" +
            "邻のマンご饭\n" +
            "Gravure Collection\n" +
            "少女脱衣\n" +
            "Princess Collection\n" +
            "美尻倶楽部\n" +
            "ビフォー・アフター\n" +
            "高级ソープ\n" +
            "Model Collection\n" +
            "名人（セレブ）\n" +
            "Resort（リゾート）\n" +
            "Special（スペシャル）\n" +
            "POP（ポップ）\n" +
            "Gravure（グラビア）\n" +
            "优雅（エレガンス）\"\n" +
            " \tCaribbean\tカリビアンコム\t加勒比\t2000\t\t\"番号多为一串数字，如022313-538,前六位数字表示发行日期，后三位是影片编号\n" +
            "密室**\n" +
            "女热大陆（Erotic Documentary）\n" +
            "THE 未公开\n" +
            "新入社员のお仕事（School Girl Uniform Club）\n" +
            "禁じられた关系\n" +
            "极上本物泡姫物\n" +
            "见晴らし最高\n" +
            "初めてのAV（My First Time Porn Filming）\n" +
            "美★ジーンズ\n" +
            "カリビアンキューティー（Caribbean Cutie）\n" +
            "夏の想い出（Sweet Summer Memory ）\"\tCaribbeancom（加勒比）公司日语：カリビアンコム，是一家在美国注册的制作公司，所出品的影片均为无马片，从2000年开始营运。Caribbeancom的销售业绩在同行业中属于上等，拥有最多的会员数量以及影片。\t\"产品系列搜索举例：\n" +
            "新入社员のお仕事 Vol.N\n" +
            "カリビアンキューティー Vol.N \"\n" +
            " \tSky High Entertainment\tスカイハイエンターテインメント\t空天使\t\t\t\"sky加3位数字序号组成\n" +
            "SkyAngel\n" +
            "GoldAngel\n" +
            "好色妻降临\n" +
            "美熟女\n" +
            "X COLLECTION系列\"\t是一家在美国注册的日本成人视频制造商，该公司作品均为无马视频。 Sky High Ent. 公司的Sky Angel（简称空天使）系列，一直以来专注于女优的第一次下码，其独特的卖点不但广所众人青睐，而且使同行与其鞭长莫及。\t \n" +
            " \tRED HOT Collection\tレッドホットコレクション\t红热\t\t\t作品：Red Hot Jam （RHJ-xxx） ，Red Hot Fetish Collection（RED-xxx），RED HOT IDOL（CRD-xxx） \t红热是一个在美国注册的日本成人视频制造商，作品均为无码视频。\t \n" +
            " \tSuper Model Media\tスーパーモデルメディア\t超级名模\t2009\t\t番号：SMD、SMDV开头，后加数字，如：SMD-76、SMDV-18有蓝光版的变成SMBD，如：SMBD-76， \u2028有3D、2D版的会变成SM3D2DBD，如：SM3D2DBD-18 \tSuper Model Media（超级名模），作品均为步兵\t\"公司简介\n" +
            "Super Model Media（日语：スーパーモデルメディア）是一家在美国注册的日本色情片制作公司，从2009年开始营运。\n" +
            "\n" +
            "Super Model Media所出品的影片均是无码片，以面容姣好的AV女优而著称，所签约的AV女优亦有一定的知名度，其成人电影在大中华地区多为流传。\n" +
            "\n" +
            "Super Model Media所出品的成人电影均有两种版本，DVD版本与蓝光光碟版本，其蓝光光碟版本常为AV迷收藏的目标。\n" +
            "\n" +
            "所出品的成人电影其系列作品名称为“S Model”。\n" +
            "\n" +
            "Super Model Media的成人电影作品以无套性交、体内射精为卖点。\n" +
            "\n" +
            "系列作品\n" +
            "S Model\n" +
            "\n" +
            "主条目：S Model\n" +
            "\n" +
            "为最主要系列作品，意译为“S级模特”，S级是最上级的意思。每一集均发行两种版本：DVD版本以及蓝光光碟版本。\n" +
            "\n" +
            "3D & 2D Hi-Vision Collction\n" +
            "\n" +
            "3D & 2D Hi-Vision Collction 01 : 星崎アンリ, 铃木さとみ, 羽月希, 直嶋あい, 大冢咲, 花井メイサ, きこうでんみさ, 花野真衣\n" +
            "\n" +
            "3D & 2D Hi-Vision Collction 02 : 小向まな美, かなみ芽梨, 夏原カレン, 原明奈, 天宫まりる, 美咲结衣, 柳田やよい, 青空小夏, 结城リナ\n" +
            "\n" +
            "注释\n" +
            "尚无官方中文译名。目前中文社群习惯使用英文名称Super Model Media。\n" +
            "\n" +
            "演出女优列表\n" +
            "S Model 01 : 杏堂なつ\n" +
            "\n" +
            "S Model 02 : 内山遥\n" +
            "\n" +
            "S Model 03 : 大冢咲\n" +
            "\n" +
            "S Model 04 : 音羽かなで\n" +
            "\n" +
            "S Model 05 : ステファニー\n" +
            "\n" +
            "S Model 06 : 沢木树里\n" +
            "\n" +
            "S Model 07 : 七瀬かずみ\n" +
            "\n" +
            "S Model 08 : SARA\n" +
            "\n" +
            "S Model 09 : 花井美沙（花井メイサ）\n" +
            "\n" +
            "S Model 10 : 佐藤美沙\n" +
            "\n" +
            "S Model 11 : 优木あおい\n" +
            "\n" +
            "S Model 12 : 原明奈\n" +
            "\n" +
            "S Model 13 : 真中ちひろ\n" +
            "\n" +
            "S Model 14 : 羽月希\n" +
            "\n" +
            "S Model 15 : 直嶋爱（直嶋あい）\n" +
            "\n" +
            "S Model 16 : 夏原カレン\n" +
            "\n" +
            "S Model 17 : 武藤クレア\n" +
            "\n" +
            "S Model 18 : 青空小夏\n" +
            "\n" +
            "S Model 19 : 羽月希\n" +
            "\n" +
            "S Model 20 : 天宫まりる\n" +
            "\n" +
            "S Model 21 : 安西琉菜\n" +
            "\n" +
            "S Model 22 : 青山さつき\n" +
            "\n" +
            "S Model 23 : 爱原つばさ\n" +
            "\n" +
            "S Model 24 : 朝仓ことみ\n" +
            "\n" +
            "S Model 25 : 葵ぶるま\n" +
            "\n" +
            "S Model 26 : 相叶りか\n" +
            "\n" +
            "S Model 27 : ももかりん\n" +
            "\n" +
            "S Model 28 : 直美めい\n" +
            "\n" +
            "S Model 29 : 白咲舞\n" +
            "\n" +
            "S Model 30 : 爱内梨花\n" +
            "\n" +
            "S Model 31 : 真田春香\n" +
            "\n" +
            "S Model 32 : 橘ひなた\n" +
            "\n" +
            "S Model 33 : 南野あかり\n" +
            "\n" +
            "S Model 46 : 上条めぐ(三浦芽依)\"\n" +
            " \tCATWALK Entertainment\tキャットウォークエンターテインメント\t猫步毒药\t2009\t\t\"主要系列：\n" +
            "CATWALK POISON\n" +
            "CATWALK POISON DV\n" +
            "CATWALK PERFUME\n" +
            "CW开头，有CWP、CWDV 后加数字编号，如：CWP-64、CWDV-14 其中CWP有蓝光版的会加上BD，如：CWPBD-64 \u2028CWDV有3D、2D版的会加上3D2DBD,如：CW3D2DBD-14 \"\t猫步， 是一家在美国注册的日本 色情片制作公司，从2009年开始营运。\t\"公司简介\n" +
            "在无码色情业制造公司中，CATWALK所签约的AV女优有较高的关注度与知名度，因此拥有一批固定的支持者，其作品在大中华地区广为流传。\n" +
            "\n" +
            "CATWALK迎合了蓝光光碟时代的趋势，从拍摄第一支色情片开始到现在，均有两种版本，DVD版本与蓝光光碟版本，带动了购买蓝光光碟的买气。\n" +
            "\n" +
            "所出品的影片均是无码片，以面容姣好的AV女优而著称。\n" +
            "\n" +
            "CATWALK Entertainment（日语：キャットウォークエンターテインメント）是一家在美国注册的日本色情片制作公司，从2009年开始营运。\n" +
            "\n" +
            "其作品以无套性交、体内射精为卖点。\n" +
            "\n" +
            "系列作品\n" +
            "CATWALK POISON\n" +
            "\n" +
            "主条目：CATWALK POISON\n" +
            "\n" +
            "英文：CATWALK POISON，日文：キャットウォーク ポイズン，意译为猫步毒药。每一集均发行两种版本：DVD版本以及蓝光光碟版本。\n" +
            "\n" +
            "英文：CATWALK PERFUME，日文：キャットウォーク パフューム，意译为猫步香水。\n" +
            "\n" +
            "CATWALK PERFUME 01 : 纹舞兰（纹舞らん）\n" +
            "\n" +
            "CATWALK PERFUME 02 : ユーロギャルズ スペシャルコレクション\n" +
            "\n" +
            "CATWALK PERFUME 03 : nao.\n" +
            "\n" +
            "注释\n" +
            "尚无官方中文译名，有人直译为猫步。目前中文社群习惯使用英文名称CATWALK。\"\n" +
            " \tMUGEN Entertainment\tム ゲンエ ンタ ーテ イ ンメ ント\t无限娱乐\t\t\t\"主要系列：\n" +
            "KIRARI\n" +
            "Desire\n" +
            "Egals\n" +
            "\u2028番号：MX、MG、MW、MUD、MKD-S后加数字，如：MX-35 MG-025 MW-16 MUD-27 MKD-S49，有蓝光版的后变为：MUBD、MKBD-S 后面数字不变；MX、MG、MW的一律变为BD-M、BD-G等，后面数字有变化\"\t\"MUGEN\n" +
            "Entertainment所出品的均是**影片，以面容姣好的AV女优而著称。所出品的成人影片系列作品中，以“Desire”、“Desire\n" +
            "(Blu-ray)”、“Egals”、“Egals\n" +
            "(Blu-ray)”较具知名，其成人影片在大中华地区多为流传。所发售的蓝光光碟版本常为AV迷收藏的目标，且该公司可说是色情业者中采用蓝光光碟的先\n" +
            "驱，在2007年就推出了蓝光光碟的成人影片，而其他业者大多2008年、2009年以后才推出蓝光光碟版本，甚至不少公司在2010年仍没有蓝光光碟版\n" +
            "本。\n" +
            "MUGEN Entertainment的成人影片作品以无套性交、体**精为卖点。\"\t\n" +
            " \tstage 2 media\tステージ 2 メディア\tstage 2 media\t\t\tdvd编号，s2m+xxx， 对应的蓝光，s2mbd+xxx\t主要系列：Encore\t\n" +
            " \tKamikaze Entertainment\tカミカゼエンターテインメント\t神风\t20060718\t20100722\t步兵 下属三个系列比较出名Kamikaze Premium； Kamikaze Street ； Kamikaze Girls 番号分别为KP-xx、 KS-xx、KG-xx \t神风，注册于美国\t步兵 下属三个系列比较出名Kamikaze Premium； Kamikaze Street ； Kamikaze Girls 番号分别为KP-xx、 KS-xx、KG-xx \n" +
            " \tOriental Dream\tオリエンタルドリーム\t东方梦\t\t\tFH-XX\t东方梦， 一家在美国注册的日本制作公司出品的。\t\"公司简介\n" +
            "所出品的影片均是无码片，以面容姣好的AV女优而著称 (对于洋人而言)。\n" +
            "\n" +
            "作品风格比较特殊，有固定的支持群众，镜头落在臀部或阴部特写居多。\n" +
            "\n" +
            "美臀股沟性交，假屌自慰，同性恋等….是该公司常见的风格，和実录出版的风格有些类似。\n" +
            "\n" +
            "Oriental Dream（日语：ァ￡エンタルドリーム）是一家在美国注册的日本色情片制作公司。\n" +
            "\n" +
            "系列作品\n" +
            "Oriental Dream – TG\n" +
            "\n" +
            "TG-01 Tengu Vol.1 J-Girl’s Fuck Marathon 佐田絵里奈\n" +
            "\n" +
            "TG-02 Tengu Vol.2 J-Girl’s Power Drill Dildoing 瀬名えみり\n" +
            "\n" +
            "TG-03 Tengu Vol.3 J-Girl’s Vibrating Rode 城戸さやか 南レイ\n" +
            "\n" +
            "TG-04 Tengu Vol.4 J-Girl’s Creampie Sisters 松島やや 林果\n" +
            "\n" +
            "TG-05 Tengu Vol.5 J-Girl’s Double Holes 水原みなみ 小笠原咲\n" +
            "\n" +
            "Oriental Dream – Miyabi (OD-MB)\n" +
            "\n" +
            "OD-MB01 My Pretty Cospet Vol.1 (僕のかわいいコスペット1) 相澤唯衣\n" +
            "\n" +
            "OD-MB02 My Pretty Cospet Vol.2 (僕のかわいいコスペット2) 中里愛菜\n" +
            "\n" +
            "OD-MB03 My Pretty Cospet Vol.3 (僕のかわいいコスペット3) 内藤花苗\n" +
            "\n" +
            "OD-MB04 My Beautiful CosLady Vol.1 (僕の素敵なCosLady 02) 広瀬奈央美\n" +
            "\n" +
            "OD-MB05 My Pretty Cospet Vol.4 (僕のかわいいコスペット4) 相田はるか\n" +
            "\n" +
            "OD-MB06 My Pretty Cospet Vol.5 (僕のかわいいCospet 5) 森高えみ\n" +
            "\n" +
            "OD-MB07 My Beautiful CosLady Vol.2 (僕の素敵なCosLady 02) 草蒔紅葉\n" +
            "\n" +
            "OD-MB08 My Pretty Cospet Vol.6 (僕のかわいいCospet 6) 望月れいか\n" +
            "\n" +
            "OD-MB09 Cospet Collection 愛葉るび はらだはるな ちあき 若林樹里 中島佐奈 和久井幸\n" +
            "\n" +
            "OD-MB10 My Beautiful CosLady Vol.3 (僕の素敵なコスレディ3) 持月真由\n" +
            "\n" +
            "OD-MB11 My Pretty Cospet Vol.7 飯島直愛\n" +
            "\n" +
            "OD-MB12 Cospet School 6 Fuck (僕の憧れコスペットスクール6ファック) 愛葉るび 千堂まこと 雨城夕紀 沢木もえ 片瀬ゆう 日野美沙\n" +
            "\n" +
            "OD-MB13 Cospet Queen (やっぱり君がコスペットクィーン 5ファック) 白石ひより (2枚組)\n" +
            "\n" +
            "OD-MB14 Milky Cospets Vol.1 (6 FUCK) 中島佐奈 若葉薫子 ユンジャ 玉木あかね 井川ゆき おさない桃花\n" +
            "\n" +
            "OD-MB15 Hotel De Coshifure Vol.1 メイ ミユ アミ アキ ユウ サトコ\n" +
            "\n" +
            "OD-MB16 The Elegance あげは 日高マリア\n" +
            "\n" +
            "OD-MB17 Hotel De Coshifure Vol.2 あみ しずく りり まどか めぐみ はるき\n" +
            "\n" +
            "OD-MB18 My Beautiful CosLady Vol.4 (僕の素敵なコスレディ4) 岩下美季\n" +
            "\n" +
            "OD-MB19 My Pretty Cospet Vol.8 (僕のかわいいコスペット8) 上原絵里香\n" +
            "\n" +
            "OD-MB20 Madam Elegance 水野朋美 伊沢涼子\n" +
            "\n" +
            "OD-MB21 Hotel De Coshifure Vol.3 チヒロ マイコ リオ アサミ マキ アイリ\n" +
            "\n" +
            "OD-MB22 LOVE-HO de coshifure 望月なな いちこ 大城楓 稲沢綾 瀬戸彩\n" +
            "\n" +
            "OD-MB23 My Pretty Cospet Vol.9 (僕のかわいい9) 真山潤\n" +
            "\n" +
            "OD-MB24 My Pretty Cospet Golden Hour Vol.1 (僕の可愛いコスペット Vol.1 ゴールデンアワー) 遠山若菜\n" +
            "\n" +
            "OD-MB25 Golden Hour Vol.2 榊梨緒 愛内みさき\n" +
            "\n" +
            "OD-MB26 Golden Hour Vol.3 (僕の可愛いコスペット3) 堤あまね\n" +
            "\n" +
            "OD-MB27 Room Service さやか まき\n" +
            "\n" +
            "OD-MB28 My Beautiful CosLady Vol.5 (僕の素敵なコスレディ5) 稲森麗奈\n" +
            "\n" +
            "OD-MB29 Milky Cospets Vol.2 相沢もも 常夏みかん 松嶋もえ 椎名かりん 杉浦なな\n" +
            "\n" +
            "OD-MB30 Golden Hour Vol.4 麻生沙紀\n" +
            "\n" +
            "OD-MB31 Golden Hour Vol.5 白瀬あいみ\n" +
            "\n" +
            "OD-MB32 Golden Hour Vol.6 坂下ななえ\n" +
            "\n" +
            "OD-MB33 Golden Hour Vol.7 (ゴールデンアワー 7) AYA\n" +
            "\n" +
            "OD-MB34 Golden Theater 四畳半FUCK 西園寺なな\n" +
            "\n" +
            "OD-MB35 Miyabi All Stars 雅 オールスターズ (2枚組み)\n" +
            "\n" +
            "Oriental Dream – OD-SM\n" +
            "\n" +
            "OD-SM01 さくら 桜田さくら 苺みるく 宇美野ひかり(海野ひかり)\n" +
            "\n" +
            "OD-SM02 Asian Beauty 白石ひより 岡崎美女 苺みるく 宮下杏奈 小野谷実穂 可愛いいな 逢乃うさぎ 葵あげは 可愛ひな\n" +
            "\n" +
            "OD-SM03 Party Room (パーティールーム) 可愛ひな 乙女梨緒 園崎まりな 工藤あやの 原路花 松山理江 ナツコ ミユ ユキエ\n" +
            "\n" +
            "OD-SM04 Sex Animal 5 Actress 高野あゆみ かわいひかる 梅宮かおる 柚木真奈 愛原梨央\n" +
            "\n" +
            "Oriental Dream – OD-TV\n" +
            "\n" +
            "OD-TV01 蕩 Torokeru 白石ひより 可愛ひな\n" +
            "\n" +
            "OD-TV02 CosPlayer Vol.1 三沢真由美\n" +
            "\n" +
            "OD-TV03 Mon Mon Vol.1 (門悶1) 逢乃うさぎ 葵あげは\n" +
            "\n" +
            "OD-TV04 Acme 宇美野ひかり\n" +
            "\n" +
            "Oriental Dream – ZIPANG (OD-ZP)\n" +
            "\n" +
            "OD-ZP01 Mon Mon Vol.2 (門悶2) 小野谷実穂\n" +
            "\n" +
            "OD-ZP02 Mistake 逢野うさぎ (逢乃うさぎ)\n" +
            "\n" +
            "OD-ZP03 Show Girl 稲森麗奈\n" +
            "\n" +
            "OD-ZP04 Milk Hall Vol.1 大滝明日香\n" +
            "\n" +
            "OD-ZP05 Asia Vol.1 鶴瀬愛美\n" +
            "\n" +
            "OD-ZP06 Sport Sex 藤沢理名\n" +
            "\n" +
            "OD-ZP07 Aka Mon (紅門) 白坂ゆう\n" +
            "\n" +
            "OD-ZP08 W’s (ダブルス) Vol.1 純菜ひとみ\n" +
            "\n" +
            "OD-ZP09 W’s (ダブルス) Vol.2 片桐美咲\n" +
            "\n" +
            "OD-ZP10 Man Hall みなとあゆみ\n" +
            "\n" +
            "OD-ZP11 CosPlayer Vol.2 (コスプレイヤー2) 瀬戸彩\n" +
            "\n" +
            "OD-ZP12 Blue Light Tokyo かわいゆい\"\n" +
            " \tTora-Tora-Tora\tトラトラトラ\t虎虎虎\t20070329\t20090226\t\t虎虎虎，在美国注册。 拒绝加入DRP，丧失大量北美市场，拒绝网络化浪潮，于是就挂掉鸟！以前红火的时候，好多骑兵女神就是在虎虎虎下的马，可惜固步自封葬送了自己。\t\n" +
            " \tHEYZO\t哇\t哇\t\t\tHEYZO-xxxx\tHEYZO和加勒比是一个类型的,作为一家新进公司,同样是在美国注册,也是由Dream room做的北美总代理,2012年8月31日开业,目前的更新速度是一周6部(目前已经发行了277部).虽然时间不长,但是其主打的系列很齐全,都是1080P的,视频码率为5M,画质是相当的不错,目前已经对二本道和加勒比形成了很大的冲击!HEYZO3个月一般会员是130美刀,VIP是180美刀,SVIP是220美刀.HEYZO的中文意思就哇的意思,代表惊叹和赞赏的意思\t\n" +
            " \tKOKESHI\t\t\t\t\t番号：KS-xx \t\t番号：KS-xx \n" +
            " \tYuzu\t\t\t\t\t番号：YZD-xxx GOD-xxx YUU-xxx \t\t番号：YZD-xxx GOD-xxx YUU-xxx \n" +
            " \tEMPIRE\t\t\t\t\t番号：EMP-xxx\t\t番号：EMP-xxx\n" +
            " \tC0930\t「人妻斬り」\t人妻斩\t\t\t番号：C0930-pla xxxx C0930-gol xxxx C0930-gitozuna xxxx\t\t番号：C0930-pla xxxx C0930-gol xxxx C0930-gitozuna xxxx\n" +
            " \tPink Puncher\tピンクパンチャー\t粉红女郎\t\t\t番号：PB-xxx\t\"所出品的影片均是无码片，以面容姣好的AV女优而著称。其作品以无套性交、体内射精为卖点。\n" +
            "\n" +
            "Pink Puncher（日语：ピンクパンチャー）是一家在美国注册的日本色情片制作公司。\n" +
            "\n" +
            "发片常见开头：PB\"\t番号：PB-xxx \n" +
            " \tCATCHEYE\t\t\t\t\t番号：DRC-xxx DRG-xxx\t\"CATCHEYE是一家在美国注册的日本色情片制作公司，2010年9月20日开业。\n" +
            "所出品的影片均是无码片，AV女优面容姣好，封面制作精美。通常发片后均冠以：店長推薦作品\"\t番号：DRC-xxx DRG-xxx\n" +
            " \tSAMURAI PORN\tサムライポルノ\t武士色情\t20051028\t20100924\t番号：DSAM-xx EB-xx SMR-xx SP-xx\t\t番号：DSAM-xx EB-xx SMR-xx SP-xx\n" +
            " \tXXX-AV\t\t\t\t\t番号 xxx-av\t\t番号：这部不错 xxx-av 21404 \n" +
            " \tMuraMura\tムラムラ\t\t\t\t番号：复杂，举个例子吧 muramura 112213_985 ，说明这部是2013-11-22发行，把年放在最后，变态\t\t番号：复杂，举个例子吧 muramura 112213_985 ，说明这部是2013-11-22发行，把年放在最后，变态\n" +
            " \tRUBY人妻\t\t\t\t\t番号：MKD-xx \t\t番号：MKD-xx \n" +
            " \tOne Piece Entertainment\t\t\t\t\t番号：OPC-xxx \t\t番号：OPC-xxx \n" +
            " \tJukujo-Club\t熟女俱乐部\t熟女俱乐部\t\t\t番号：Jukujo-Club-xxxx\t\t番号：Jukujo-Club-xxxx \n" +
            " \tStudio Teriyaki\tスタジオテリヤキ\t\t2007\t\t\"番号： PT-xxx \n" +
            "系列作品\n" +
            "BT 系列\n" +
            "DT系列\n" +
            "CT 系列\n" +
            "PT 系列\n" +
            "SMJ 系列\n" +
            "ST 系列\n" +
            "\n" +
            "\"\t\"所出品的影片均是无码片，以面容姣好的AV女优而著称。其作品以无套性交、体内射精为卖点。\n" +
            "\n" +
            "发片最常见词语开头：店長推薦作品。\n" +
            "\n" +
            "其所出品影片广受好评！\n" +
            "\n" +
            "Studio TERIYAKI（日语：スタジァ∑リヤキ）是一家在美国注册的日本色情片制作公司，从2007年开始营运。\"\t\"番号： PT-xxx \n" +
            "系列作品\n" +
            "BT 系列\n" +
            "\n" +
            "BT-001 Aanta Claus Is Coming To Fuck!:ゆず\n" +
            "\n" +
            "BT-003 SPERMA QUEEN:つかもと友希\n" +
            "\n" +
            "BT-004 Premium Model:国仲みさと\n" +
            "\n" +
            "BT-005 EroCos:早见カァ‰\n" +
            "\n" +
            "BT-006 Double Extacy:持田ゆき、みさきなな\n" +
            "\n" +
            "BT-007 Premium Model:NATSU\n" +
            "\n" +
            "BT-008 I am God!:沢尻もも美、南野らん\n" +
            "\n" +
            "BT-009 Different Blood Types, Different Sex:MAYA、水木百合\n" +
            "\n" +
            "BT-010 Amaenbou:中村まゆ、山咲ほのか\n" +
            "\n" +
            "BT-011 The Hottest Big Tits:俗人\n" +
            "\n" +
            "BT-012 I am God! Vol.2:日向まお、如月美雪\n" +
            "\n" +
            "BT-013 Real Sex Document Vol.1:佐藤チカ、二宫春奈\n" +
            "\n" +
            "BT-014 Ero Cos:青山亜里沙\n" +
            "\n" +
            "BT-015 Amaenbou:红叶クレハ\n" +
            "\n" +
            "BT-016 Real Sex Document Vol.2:浅井ゆう子、えいこ\n" +
            "\n" +
            "BT-017 EroCos:藤野みゆき\n" +
            "\n" +
            "折叠编辑本段DT系列\n" +
            "DT-001 Sex with Married Woman Vol.1:高瀬ゆみ\n" +
            "\n" +
            "DT-002 Sex with Married Woman Vol.2:三岛香奈\n" +
            "\n" +
            "DT-003 Sex with Married Woman Vol.3:浅冈みなみ\n" +
            "\n" +
            "DT-004 Sex with Married Woman Vol.4:広末マキ\n" +
            "\n" +
            "DT-005 Sex with Married Woman Vol.5:まりあ\n" +
            "\n" +
            "DT-006 Sex with Extremely Erotic Married Woman:山岸春奈\n" +
            "\n" +
            "　CT 系列\n" +
            "\n" +
            "CT-01 大人の日曜剧场‘渡る世间はエロばかり’( 第一话＆第二话 ):宫泽ゆうな、星优乃\n" +
            "\n" +
            "CT-02 大人の日曜剧场‘渡る世间はエロばかり’( 第三话＆第四话 ):夏川サヤ、河合つかさ、枫はるか\n" +
            "\n" +
            "CT-03 大人の日曜剧场‘渡る世间はエロばかり’( 第五话＆最终话 ):大沢萌、河合つかさ、枫はるか\n" +
            "\n" +
            "CT-04 大人の日曜剧场‘みて肛门’第一章:长谷川なぁみ、瀬名えみり\n" +
            "\n" +
            "CT-05 大人の日曜剧场‘みて肛门’第二章:长瀬あずさ、瀬名えみり、长谷川なぁみ\n" +
            "\n" +
            "CT-06 大人の日曜剧场‘みて肛门’最终章:长瀬あずさ、瀬名えみり、长谷川なぁみ\n" +
            "\n" +
            "CT-07 PIC UP BATTLE Vol.1:俗人\n" +
            "\n" +
            "CT-08 夏の别荘大乱(百度)交物语:美女3名\n" +
            "\n" +
            "CT-09 実录万引き人妻:俗人3名\n" +
            "\n" +
            "CT-10 Pic Up Battle Vol.2:俗人\n" +
            "\n" +
            "CT-11 Shoplifter Couple:俗人\n" +
            "\n" +
            "CT-12 KOKUSEN:青山亜里沙、上原优、绫波あすか、矢沢るい\n" +
            "\n" +
            "CT-13 My Girlfriend Gets XXX:俗人\n" +
            "\n" +
            "CT-14 Swapping Club:俗人\n" +
            "\n" +
            "CT-15 Yarikono Year End Party:俗人\n" +
            "\n" +
            "　PT 系列\n" +
            "\n" +
            "PT-01 新入社员は小悪魔淫乱娘:大沢佑香\n" +
            "\n" +
            "PT-02 新入社员の淫らなお仕事:いつか\n" +
            "\n" +
            "PT-03 新入社员は愈し系淫乱M女:诗织\n" +
            "\n" +
            "PT-04 BIMBO.01:ののか\n" +
            "\n" +
            "PT-05 新入社员はロリ颜淫乱美少女:春野みく\n" +
            "\n" +
            "PT-06 RIMIX:中岛京子\n" +
            "\n" +
            "PT-07 REMIX:爱音ゆう\n" +
            "\n" +
            "PT-08 BIMBO.02:太田ゆうこ、ミュウ\n" +
            "\n" +
            "PT-09 Model Collection:中里爱\n" +
            "\n" +
            "PT-10 REMIX:Maya\n" +
            "\n" +
            "PT-11 Model Collection:永井优、长泽りか、梨々花\n" +
            "\n" +
            "PT-12 REMIX:瀬咲るな\n" +
            "\n" +
            "PT-13 新入社员は清楚な美少女:中村まゆ\n" +
            "\n" +
            "PT-14 SAORI DEBUT:沙织\n" +
            "\n" +
            "PT-15 Model Collection:松下美里\n" +
            "\n" +
            "PT-16 新入社员は巨乳淫乱美少女:小岛奈保\n" +
            "\n" +
            "PT-17 SAORI PROLOGUE:沙织\n" +
            "\n" +
            "PT-18 Cattleya:加斗レア\n" +
            "\n" +
            "PT-19 ItGirl:Ichika\n" +
            "\n" +
            "PT-20 Absolute Girlfriend:みづき伊织\n" +
            "\n" +
            "PT-21 ItGirl:爱嶋リーナ\n" +
            "\n" +
            "PT-22 The Freshman Employee is a Horny Girl:川岛いろは\n" +
            "\n" +
            "PT-23 Misora Loves Massive Cock:进藤美空\n" +
            "\n" +
            "PT-24 Yuuka Loves Massive Cock:翼祐香\n" +
            "\n" +
            "PT-25 REMIX:石黒京香\n" +
            "\n" +
            "PT-26 REMIX:矢沢もえ\n" +
            "\n" +
            "PT-27 It Girl:所まりあ\n" +
            "\n" +
            "PT-28 REMIX:RISA\n" +
            "\n" +
            "PT-29 七海はぶっといのがお好き:七海\n" +
            "\n" +
            "PT-30 REMIX:みづき伊织\n" +
            "\n" +
            "PT-31 ふしだらな关系:ICHIKA\n" +
            "\n" +
            "　SMJ 系列\n" +
            "\n" +
            "SMJ-001 Iron Maiden:沢尻もも美\n" +
            "\n" +
            "SMJ-002 DEEP PURPLE:AMI AMYA\n" +
            "\n" +
            "　ST 系列\n" +
            "\n" +
            "ST-01 SHI6OTO Vol.1 俗人\n" +
            "\n" +
            "ST-02 SHI6OTO Vol.2 俗人\n" +
            "\n" +
            "ST-03 SHI6OTO Vol.3 俗人\n" +
            "\n" +
            "ST-04 SHI6OTO Vol.4 俗人\n" +
            "\n" +
            "ST-05 SHI6OTO Vol.5 俗人\n" +
            "\n" +
            "ST-06 SHI6OTO Vol.6 俗人\n" +
            "\n" +
            "ST-07 SHI6OTO Vol.7 俗人\n" +
            "\n" +
            "ST-08 SHI6OTO Vol.8 俗人\n" +
            "\n" +
            "ST-09 SHI6OTO Vol.9 俗人\n" +
            "\n" +
            "ST-10 SHI6OTO Vol.10 俗人\n" +
            "\n" +
            "ST-11 SHI6OTO Vol.11 俗人\n" +
            "\n" +
            "ST-12 SHI6OTO Vol.12 俗人\n" +
            "\n" +
            "ST-13 SHI6OTO Vol.13 俗人\n" +
            "\n" +
            "ST-14 SHI6OTO Vol.14 俗人\n" +
            "\n" +
            "ST-15 SHI6OTO Vol.15 俗人\"\n" +
            " \tMesubuta\tメス豚\t母猪\t\t\t番号：比较复杂，就举个例子，mesubuta 131122_732_01\t他是日本动作片中的一个系列，里面的多数是素人出现，口味略重偏暴力，而且都是步兵。\t番号：比较复杂，就举个例子，mesubuta 131122_732_01\n" +
            " \tAV9898\t\t AV-酒吧\t\t\t番号：AV9898-xxxx\t\t番号：AV9898-xxxx\n" +
            " \tG-Queen\t無毛宣言\t無毛宣言\t\t\t番号： G-Queen-xxx\t\t番号： G-Queen-xxx\n" +
            " \tPacopacomama\t人妻・熟女\t人妻・熟女\t\t\t人妻・熟女番号： pacopacomama-发行日期，如 pacopacomama-012314\t\t人妻・熟女番号： pacopacomama-发行日期，如 pacopacomama-012314\n" +
            " \tGachinco\tガチん娘！\t\t\t\t番号：gachixxx-HD\t制作公司名 ガチん娘的意思是;真的吗女儿 gachinco是一个日本AV的一个系列 Gachinco(日语:ガチん娘)[1]是一家在美国注册的日本色情片制作公司,所出品的影片均为**片,并提供线上付费下载色情片,从20\t番号：gachixxx-HD\n" +
            " \t10musume \t天然むすめ\t天然素人\t\t\t10musume\t番号：10musume-发行日期_01 ，如10musume-010414_01\t天然むすめ(10musume) 和 [heyzo](https://www.zhihu.com/search?q=heyzo&search_source=Entity&hybrid_search_source=Entity&hybrid_search_extra={\"sourceType\"%3A\"answer\"%2C\"sourceId\"%3A198173383}) 一样是drp这家公司崛起时连带水涨船高起来的片商。天然むすめ(10musume) 最初是类似91的自拍网站，得益于泥轰发达的摄影照相器材普及和高平均网速，很多年前任何泥轰普通人就可以用身边的[摄影器材](https://www.zhihu.com/search?q=摄影器材&search_source=Entity&hybrid_search_source=Entity&hybrid_search_extra={\"sourceType\"%3A\"answer\"%2C\"sourceId\"%3A198173383})自行拍摄自己的作品发布的网站上，一旦网站决定将用户提交的视频出版发行，投递自己作品的网友将可以得到 天然むすめ(10musume) 网站的发行售卖分成，不过最近一两年已经很难在 天然むすめ(10musume) 看到非专业演员表演性质的作品了！我对 天然むすめ(10musume) 映像最深刻的是09年一对小夫妻的自拍作品。总共四部，男几乎的没有露脸，女的表现非常自然可亲，全程是面包车在山间游玩过程的自拍，中途遇到同好者完成了交换，真的是非常好的作品。 天然むすめ(10musume) 不一定都是步兵，骑兵也有。\n" +
            " \tSASUKE\tサスケ\t佐助\t20100915\t\t\"番号： SSKJ-xxx \n" +
            "Sasuke Premium\n" +
            "Sasuke Jam\n" +
            "Sasuke X\"\t\"出品的影片均是无码片，以面容姣好的AV女优而著称。其作品以无套性交、体内射精为卖点。\n" +
            "\n" +
            "SASUKE（日语：サスケ）是一家在美国注册的日本色情片制作公司。2010年9月15日开业。\"\t\"番号： SSKJ-xxx \n" +
            "Sasuke Premium\n" +
            "\n" +
            "日文：サスケプレミアム，英文：Sasuke Premium。该系列作品从2010年9月15日开始。\n" +
            "\n" +
            "Sasuke Premium Vol.1 : 糸矢めい\n" +
            "\n" +
            "Sasuke Premium Vol.2 : 筱めぐみ\n" +
            "\n" +
            "Sasuke Premium Vol.3 : 田村美羽\n" +
            "\n" +
            "Sasuke Premium Vol.4 : 糸矢めい\n" +
            "\n" +
            "Sasuke Premium Vol.5 : 星野あかり\n" +
            "\n" +
            "Sasuke Premium Vol.6 : 糸矢めい\n" +
            "\n" +
            "Sasuke Premium Vol.7 : 乙井なずな\n" +
            "\n" +
            "Sasuke Premium Vol.8 : 筱めぐみ\n" +
            "\n" +
            "Sasuke Premium Vol.9 : 原明奈\n" +
            "\n" +
            "Sasuke Premium Vol.10 : 卯月麻衣\n" +
            "\n" +
            "Sasuke Jam\n" +
            "\n" +
            "日文：サスケプレミアム，英文：Sasuke Jam。该系列作品从2010年10月11日开始。\n" +
            "\n" +
            "Sasuke Jam Vol.1 : 美里麻衣\n" +
            "\n" +
            "Sasuke Jam Vol.2 : 三浦亜沙妃\n" +
            "\n" +
            "Sasuke Jam Vol.3 : 夏野かをり\n" +
            "\n" +
            "Sasuke Jam Vol.4 : 星野あかり\n" +
            "\n" +
            "Sasuke Jam Vol.5 : 早乙女爱良\n" +
            "\n" +
            "Sasuke Jam Vol.6 : 宫嶋かれん\n" +
            "\n" +
            "Sasuke Jam Vol.7 : 星野あかり\n" +
            "\n" +
            "Sasuke Jam Vol.8 : 瀬尾えみり\n" +
            "\n" +
            "Sasuke Jam Vol.9 : 舞浜朱里\n" +
            "\n" +
            "Sasuke Jam Vol.10 : 広瀬ゆな\n" +
            "\n" +
            "Sasuke Jam Vol.11 : 小嶋つかさ、山本あおい、市川さなえ\n" +
            "\n" +
            "Sasuke Jam Vol.12 : 仙道春奈、爱沢圭、古谷えみ\n" +
            "\n" +
            "Sasuke Jam Vol.13 : 上村みきMiki Uemura\n" +
            "\n" +
            "Sasuke Jam Vol.14 : 吉野もな、山本あおい、小嶋つかさ\n" +
            "\n" +
            "Sasuke Jam Vol.26 : 美里麻衣\n" +
            "\n" +
            "Sasuke X\n" +
            "\n" +
            "该系列作品从2010年12月13日开始。\n" +
            "\n" +
            "Sasuke X Vol.1 : 三浦亜沙妃\n" +
            "\n" +
            "Sasuke X Vol.2 : 早乙女爱良\n" +
            "\n" +
            "Sasuke X Vol.3 : 星野あかり\n" +
            "\n" +
            "Sasuke X Vol.4 : 菜々美ねい、椎名りく、青山ゆい\"\n" +
            " \t1000giri\t1000人斬り\t1000人斩\t\t\t 番号： 1000giri+发行日期，如 1000giri-130906 ，说明这部是13年9月6日发行的 \t\t 番号： 1000giri+发行日期，如 1000giri-130906 ，说明这部是13年9月6日发行的 \n" +
            " \tH0930\t「エッチな０９３０」\t\t\t\t番号：太复杂\t熟素\t番号：太复杂\n" +
            " \tH4610\t「エッチな４６１０」\t\t\t\t番号：太复杂\t业余素人,第一次拍片的\t番号：太复杂\n" +
            " \tのぞき本舗中村屋\tのぞき本舗中村屋\t\t\t\t番号：未知\t\t番号：未知\n" +
            " \tAKIBAHONPO\tAKIBAHONPO \t\t\t\t番号：未知\t\t番号：未知\n" +
            " \tJapanese Porn\tジャポルノ\tJaporn\t\t\t\t\"Japorn（日语：ジャポルノ，全称：Japanese Porn），尚无官方中文译名，目前中文社群习惯使用英文名称Japorn。\n" +
            "Japorn（日语：ジャポルノ，全称：Japanese Porn），尚无官方中文译名，目前中文社群习惯使用英文名称Japorn。泛指在美国、欧洲注册但以日本作为主要销售市场的色情片制作公司。狭义上而言，是专指japorn.tv这家色情片商。\"\t\"背景\n" +
            "目前日本的色情片制作公司因法律的规定，必须将生殖器等部位进行马赛克处理。\n" +
            "\n" +
            "但随着互联网的普及，公司在国外制作色情影片，并在国外市场出售，借由网站贩售至日本。\n" +
            "\n" +
            "主要的AV片商\n" +
            "\u200BSky High Entertainment（スカイハイエンターテインメント）\n" +
            "\n" +
            "REDHOT Collection（レッドホットコレクション）\n" +
            "\n" +
            "Studio TERIYAKI（スタジァ∑リヤキ）\n" +
            "\n" +
            "Pink Puncher（ピンクパンチャー）\n" +
            "\n" +
            "Oriental Dream（ァ￡エンタルドリーム）\n" +
            "\n" +
            "SASUKE（サスケ）\n" +
            "\n" +
            "Stage 2 Media（ステージ 2 メディア）\n" +
            "\n" +
            "CATCHEYE\n" +
            "\n" +
            "Samurai Porn（サムライポルノ），2005年10月28日开业，2010年9月24日停业。\n" +
            "\n" +
            "Kamikaze Entertainment（カミカゼエンターテインメント），2006年7月18日开业，2010年7月22日停业。\n" +
            "\n" +
            "Tora-Tora-Tora（トラトラトラ），2007年3月29日开业，2009年2月26日停业。\n" +
            "\n" +
            "MIKADO（ミカド），2008年10月31日开业，2010年2月4日停业。\n" +
            "\n" +
            "AMORZ Entertainment： CATWALK Entertainment（キャットウォーク）\n" +
            "\n" +
            "Super Model Media（スーパーモデルメディア）\n" +
            "\n" +
            "MUGEN Entertainment（ムゲンエンターテインメント）\"\n";
}
