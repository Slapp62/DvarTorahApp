package com.quickdvartorah.app.data.model

enum class OccasionCategory { PARSHA, YOM_TOV, SPECIAL_OCCASION }

enum class ParshaOccasion(
    val key: String,
    val displayNameEn: String,
    val displayNameHe: String,
    val category: OccasionCategory
) {
    // Bereishit
    BEREISHIT("bereishit", "Bereishit", "בְּרֵאשִׁית", OccasionCategory.PARSHA),
    NOACH("noach", "Noach", "נֹחַ", OccasionCategory.PARSHA),
    LECH_LECHA("lech_lecha", "Lech Lecha", "לֶךְ-לְךָ", OccasionCategory.PARSHA),
    VAYERA("vayera", "Vayera", "וַיֵּרָא", OccasionCategory.PARSHA),
    CHAYEI_SARA("chayei_sara", "Chayei Sara", "חַיֵּי שָׂרָה", OccasionCategory.PARSHA),
    TOLDOT("toldot", "Toldot", "תּוֹלְדֹת", OccasionCategory.PARSHA),
    VAYETZEI("vayetzei", "Vayetzei", "וַיֵּצֵא", OccasionCategory.PARSHA),
    VAYISHLACH("vayishlach", "Vayishlach", "וַיִּשְׁלַח", OccasionCategory.PARSHA),
    VAYESHEV("vayeshev", "Vayeshev", "וַיֵּשֶׁב", OccasionCategory.PARSHA),
    MIKETZ("miketz", "Miketz", "מִקֵּץ", OccasionCategory.PARSHA),
    VAYIGASH("vayigash", "Vayigash", "וַיִּגַּשׁ", OccasionCategory.PARSHA),
    VAYECHI("vayechi", "Vayechi", "וַיְחִי", OccasionCategory.PARSHA),

    // Shemot
    SHEMOT("shemot", "Shemot", "שְׁמוֹת", OccasionCategory.PARSHA),
    VAERA("vaera", "Vaera", "וָאֵרָא", OccasionCategory.PARSHA),
    BO("bo", "Bo", "בֹּא", OccasionCategory.PARSHA),
    BESHALACH("beshalach", "Beshalach", "בְּשַׁלַּח", OccasionCategory.PARSHA),
    YITRO("yitro", "Yitro", "יִתְרוֹ", OccasionCategory.PARSHA),
    MISHPATIM("mishpatim", "Mishpatim", "מִּשְׁפָּטִים", OccasionCategory.PARSHA),
    TERUMAH("terumah", "Terumah", "תְּרוּמָה", OccasionCategory.PARSHA),
    TETZAVEH("tetzaveh", "Tetzaveh", "תְּצַוֶּה", OccasionCategory.PARSHA),
    KI_TISA("ki_tisa", "Ki Tisa", "כִּי תִשָּׂא", OccasionCategory.PARSHA),
    VAYAKHEL("vayakhel", "Vayakhel", "וַיַּקְהֵל", OccasionCategory.PARSHA),
    PEKUDEI("pekudei", "Pekudei", "פְקוּדֵי", OccasionCategory.PARSHA),
    VAYAKHEL_PEKUDEI("vayakhel_pekudei", "Vayakhel-Pekudei", "וַיַּקְהֵל־פְקוּדֵי", OccasionCategory.PARSHA),

    // Vayikra
    VAYIKRA("vayikra", "Vayikra", "וַיִּקְרָא", OccasionCategory.PARSHA),
    TZAV("tzav", "Tzav", "צַו", OccasionCategory.PARSHA),
    SHEMINI("shemini", "Shemini", "שְּׁמִינִי", OccasionCategory.PARSHA),
    TAZRIA("tazria", "Tazria", "תַזְרִיעַ", OccasionCategory.PARSHA),
    METZORA("metzora", "Metzora", "מְּצֹרָע", OccasionCategory.PARSHA),
    TAZRIA_METZORA("tazria_metzora", "Tazria-Metzora", "תַזְרִיעַ־מְּצֹרָע", OccasionCategory.PARSHA),
    ACHAREI_MOT("acharei_mot", "Acharei Mot", "אַחֲרֵי מוֹת", OccasionCategory.PARSHA),
    KEDOSHIM("kedoshim", "Kedoshim", "קְדֹשִׁים", OccasionCategory.PARSHA),
    ACHAREI_MOT_KEDOSHIM("acharei_mot_kedoshim", "Acharei Mot-Kedoshim", "אַחֲרֵי מוֹת־קְדֹשִׁים", OccasionCategory.PARSHA),
    EMOR("emor", "Emor", "אֱמֹר", OccasionCategory.PARSHA),
    BEHAR("behar", "Behar", "בְּהַר", OccasionCategory.PARSHA),
    BECHUKOTAI("bechukotai", "Bechukotai", "בְּחֻקֹּתַי", OccasionCategory.PARSHA),
    BEHAR_BECHUKOTAI("behar_bechukotai", "Behar-Bechukotai", "בְּהַר־בְּחֻקֹּתַי", OccasionCategory.PARSHA),

    // Bamidbar
    BAMIDBAR("bamidbar", "Bamidbar", "בְּמִדְבַּר", OccasionCategory.PARSHA),
    NASO("naso", "Naso", "נָשֹׂא", OccasionCategory.PARSHA),
    BEHAALOTECHA("behaalotecha", "Behaalotecha", "בְּהַעֲלֹתְךָ", OccasionCategory.PARSHA),
    SHELACH("shelach", "Shelach", "שְׁלַח-לְךָ", OccasionCategory.PARSHA),
    KORACH("korach", "Korach", "קֹרַח", OccasionCategory.PARSHA),
    CHUKAT("chukat", "Chukat", "חֻקַּת", OccasionCategory.PARSHA),
    BALAK("balak", "Balak", "בָּלָק", OccasionCategory.PARSHA),
    CHUKAT_BALAK("chukat_balak", "Chukat-Balak", "חֻקַּת־בָּלָק", OccasionCategory.PARSHA),
    PINCHAS("pinchas", "Pinchas", "פִּינְחָס", OccasionCategory.PARSHA),
    MATOT("matot", "Matot", "מַטּוֹת", OccasionCategory.PARSHA),
    MASEI("masei", "Masei", "מַסְעֵי", OccasionCategory.PARSHA),
    MATOT_MASEI("matot_masei", "Matot-Masei", "מַטּוֹת־מַסְעֵי", OccasionCategory.PARSHA),

    // Devarim
    DEVARIM("devarim", "Devarim", "דְּבָרִים", OccasionCategory.PARSHA),
    VAETCHANAN("vaetchanan", "Vaetchanan", "וָאֶתְחַנַּן", OccasionCategory.PARSHA),
    EIKEV("eikev", "Eikev", "עֵקֶב", OccasionCategory.PARSHA),
    REEH("reeh", "Reeh", "רְאֵה", OccasionCategory.PARSHA),
    SHOFTIM("shoftim", "Shoftim", "שֹׁפְטִים", OccasionCategory.PARSHA),
    KI_TEITZEI("ki_teitzei", "Ki Teitzei", "כִּי-תֵצֵא", OccasionCategory.PARSHA),
    KI_TAVO("ki_tavo", "Ki Tavo", "כִּי-תָבוֹא", OccasionCategory.PARSHA),
    NITZAVIM("nitzavim", "Nitzavim", "נִצָּבִים", OccasionCategory.PARSHA),
    VAYEILECH("vayeilech", "Vayeilech", "וַיֵּלֶךְ", OccasionCategory.PARSHA),
    NITZAVIM_VAYEILECH("nitzavim_vayeilech", "Nitzavim-Vayeilech", "נִצָּבִים־וַיֵּלֶךְ", OccasionCategory.PARSHA),
    HAAZINU("haazinu", "Haazinu", "הַאֲזִינוּ", OccasionCategory.PARSHA),
    VEZOT_HABRACHA("vezot_habracha", "Vezot HaBracha", "וְזֹאת הַבְּרָכָה", OccasionCategory.PARSHA),

    // Yom Tov
    ROSH_HASHANA("rosh_hashana", "Rosh Hashana", "רֹאשׁ הַשָּׁנָה", OccasionCategory.YOM_TOV),
    ASERES_YEMEI_TESHUVAH("aseres_yemei_teshuvah", "Aseres Yemei Teshuvah", "עֲשֶׂרֶת יְמֵי תְּשׁוּבָה", OccasionCategory.YOM_TOV),
    YOM_KIPPUR("yom_kippur", "Yom Kippur", "יוֹם כִּפּוּר", OccasionCategory.YOM_TOV),
    SUKKOT("sukkot", "Sukkot", "סוּכּוֹת", OccasionCategory.YOM_TOV),
    HOSHANA_RABBA("hoshana_rabba", "Hoshana Rabba", "הוֹשַׁעְנָא רַבָּה", OccasionCategory.YOM_TOV),
    SHEMINI_ATZERET("shemini_atzeret", "Shemini Atzeret", "שְׁמִינִי עֲצֶרֶת", OccasionCategory.YOM_TOV),
    SIMCHAT_TORAH("simchat_torah", "Simchat Torah", "שִׂמְחַת תּוֹרָה", OccasionCategory.YOM_TOV),
    CHANUKAH("chanukah", "Chanukah", "חֲנֻכָּה", OccasionCategory.YOM_TOV),
    TAANIS_ESTHER("taanis_esther", "Taanis Esther", "תַּעֲנִית אֶסְתֵּר", OccasionCategory.YOM_TOV),
    PURIM("purim", "Purim", "פּוּרִים", OccasionCategory.YOM_TOV),
    PESACH("pesach", "Pesach", "פֶּסַח", OccasionCategory.YOM_TOV),
    LEIL_HASEDER("leil_haseder", "Leil HaSeder", "לֵיל הַסֵּדֶר", OccasionCategory.YOM_TOV),
    SHEVII_SHEL_PESACH("shevii_shel_pesach", "Shevii Shel Pesach", "שְׁבִיעִי שֶׁל פֶּסַח", OccasionCategory.YOM_TOV),
    SEFIRAT_HAOMER("sefirat_haomer", "Sefirat HaOmer", "סְפִירַת הָעוֹמֶר", OccasionCategory.YOM_TOV),
    LAG_BAOMER("lag_baomer", "Lag BaOmer", "ל\"ג בָּעוֹמֶר", OccasionCategory.YOM_TOV),
    SHAVUOT("shavuot", "Shavuot", "שָׁבוּעוֹת", OccasionCategory.YOM_TOV),
    SEVENTEENTH_OF_TAMMUZ("seventeenth_of_tammuz", "17th of Tammuz", "י\"ז בְּתַמּוּז", OccasionCategory.YOM_TOV),
    THREE_WEEKS("three_weeks", "Three Weeks", "בֵּין הַמְּצָרִים", OccasionCategory.YOM_TOV),
    TISHA_BAAV("tisha_baav", "Tisha B'Av", "תִּשְׁעָה בְּאָב", OccasionCategory.YOM_TOV),
    TU_BAAV("tu_baav", "Tu B'Av", "ט\"ו בְּאָב", OccasionCategory.YOM_TOV),
    TENTH_OF_TEVET("tenth_of_tevet", "10th of Tevet", "עֲשָׂרָה בְּטֵבֵת", OccasionCategory.YOM_TOV),

    // Special occasions
    BRIS_MILAH("bris_milah", "Bris Milah", "בְּרִית מִילָה", OccasionCategory.SPECIAL_OCCASION),
    PIDYON_HABEN("pidyon_haben", "Pidyon HaBen", "פִּדְיוֹן הַבֵּן", OccasionCategory.SPECIAL_OCCASION),
    ENGAGEMENT("engagement", "Engagement", "אֵירוּסִין", OccasionCategory.SPECIAL_OCCASION),
    WEDDING("wedding", "Wedding", "חֲתוּנָּה", OccasionCategory.SPECIAL_OCCASION),
    SHEVA_BERACHOT("sheva_berachot", "Sheva Berachot", "שֶׁבַע בְּרָכוֹת", OccasionCategory.SPECIAL_OCCASION),
    BAR_MITZVAH("bar_mitzvah", "Bar Mitzvah", "בַּר מִצְוָה", OccasionCategory.SPECIAL_OCCASION),
    BAT_MITZVAH("bat_mitzvah", "Bat Mitzvah", "בַּת מִצְוָה", OccasionCategory.SPECIAL_OCCASION);

    companion object {
        fun fromKey(key: String): ParshaOccasion? = entries.find { it.key == key }

        fun fromHebcalName(name: String): ParshaOccasion? {
            val normalized = name
                .lowercase()
                .replace("’", "")
                .replace("'", "")
                .replace("-", "_")
                .replace(" ", "_")
                .replace(",", "_")
                .replace("__", "_")

            return when (normalized) {
                "bereshit" -> BEREISHIT
                "noach" -> NOACH
                "lech_lecha" -> LECH_LECHA
                "vayera" -> VAYERA
                "chayei_sara" -> CHAYEI_SARA
                "toldot" -> TOLDOT
                "vayetzei" -> VAYETZEI
                "vayishlach" -> VAYISHLACH
                "vayeshev" -> VAYESHEV
                "miketz" -> MIKETZ
                "vayigash" -> VAYIGASH
                "vayechi" -> VAYECHI
                "shemot" -> SHEMOT
                "vaera" -> VAERA
                "bo" -> BO
                "beshalach" -> BESHALACH
                "yitro" -> YITRO
                "mishpatim" -> MISHPATIM
                "terumah" -> TERUMAH
                "tetzaveh" -> TETZAVEH
                "ki_tisa" -> KI_TISA
                "vayakhel" -> VAYAKHEL
                "pekudei" -> PEKUDEI
                "vayakhel_pekudei" -> VAYAKHEL_PEKUDEI
                "vayikra" -> VAYIKRA
                "tzav" -> TZAV
                "shemini", "shmini" -> SHEMINI
                "tazria" -> TAZRIA
                "metzora" -> METZORA
                "tazria_metzora" -> TAZRIA_METZORA
                "acharei_mot" -> ACHAREI_MOT
                "kedoshim" -> KEDOSHIM
                "acharei_mot_kedoshim" -> ACHAREI_MOT_KEDOSHIM
                "emor" -> EMOR
                "behar" -> BEHAR
                "bechukotai" -> BECHUKOTAI
                "behar_bechukotai" -> BEHAR_BECHUKOTAI
                "bamidbar" -> BAMIDBAR
                "naso" -> NASO
                "behaalotecha" -> BEHAALOTECHA
                "shelach", "shelach_lecha" -> SHELACH
                "korach" -> KORACH
                "chukat", "hukat" -> CHUKAT
                "balak" -> BALAK
                "chukat_balak", "hukat_balak" -> CHUKAT_BALAK
                "pinchas" -> PINCHAS
                "matot" -> MATOT
                "masei" -> MASEI
                "matot_masei" -> MATOT_MASEI
                "devarim" -> DEVARIM
                "vaetchanan" -> VAETCHANAN
                "eikev", "ekev" -> EIKEV
                "reeh", "reeh" -> REEH
                "shoftim" -> SHOFTIM
                "ki_teitzei" -> KI_TEITZEI
                "ki_tavo" -> KI_TAVO
                "nitzavim" -> NITZAVIM
                "vayeilech" -> VAYEILECH
                "nitzavim_vayeilech" -> NITZAVIM_VAYEILECH
                "haazinu" -> HAAZINU
                "vezot_habracha", "vezot_haberakhah" -> VEZOT_HABRACHA
                "rosh_hashana", "rosh_hashanah" -> ROSH_HASHANA
                "aseres_yemei_teshuvah", "aseret_yemei_teshuvah" -> ASERES_YEMEI_TESHUVAH
                "yom_kippur" -> YOM_KIPPUR
                "sukkot", "succot", "sukkos" -> SUKKOT
                "hoshana_rabba", "hoshana_rabbah", "hoshana_raba" -> HOSHANA_RABBA
                "shemini_atzeret", "shmini_atzeret" -> SHEMINI_ATZERET
                "simchat_torah", "simchas_torah" -> SIMCHAT_TORAH
                "chanukah", "hanukkah", "chanuka" -> CHANUKAH
                "taanis_esther", "taanit_esther" -> TAANIS_ESTHER
                "purim" -> PURIM
                "pesach", "passover" -> PESACH
                "leil_haseder", "lel_haseder", "seder_night" -> LEIL_HASEDER
                "shevii_shel_pesach", "seventh_day_pesach" -> SHEVII_SHEL_PESACH
                "sefirat_haomer", "sefiras_haomer", "omer" -> SEFIRAT_HAOMER
                "lag_baomer", "lag_laomer" -> LAG_BAOMER
                "shavuot", "shavuos" -> SHAVUOT
                "seventeenth_of_tammuz", "shivah_asar_btammuz", "17_tammuz", "17th_of_tammuz" -> SEVENTEENTH_OF_TAMMUZ
                "three_weeks", "bein_hametzarim" -> THREE_WEEKS
                "tisha_baav", "tisha_bav", "tishah_beav" -> TISHA_BAAV
                "tu_baav", "tu_bav" -> TU_BAAV
                "tenth_of_tevet", "asara_bteves", "asarah_btevet", "10_tevet" -> TENTH_OF_TEVET
                "bris_milah", "brit_milah", "bris", "brit" -> BRIS_MILAH
                "pidyon_haben", "pidyon" -> PIDYON_HABEN
                "engagement", "erusin", "eirusin", "vort" -> ENGAGEMENT
                "wedding", "chasuna", "chasunah", "chatuna", "chuppah" -> WEDDING
                "sheva_berachot", "sheva_berachos", "sheva_brachos" -> SHEVA_BERACHOT
                "bar_mitzvah", "bar_mitzva" -> BAR_MITZVAH
                "bat_mitzvah", "bat_mitzva" -> BAT_MITZVAH
                else -> null
            }
        }
    }
}
