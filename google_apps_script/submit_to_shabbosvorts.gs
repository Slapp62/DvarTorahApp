const SHABBOSVORTS_FUNCTION_URL =
  PropertiesService.getScriptProperties().getProperty("SHABBOSVORTS_FUNCTION_URL") ||
  "https://submitdesktopdvar-4ntfttmd6q-uc.a.run.app";

const SHABBOSVORTS_SHARED_SECRET =
  PropertiesService.getScriptProperties().getProperty("SHABBOSVORTS_SHARED_SECRET") ||
  "YOUR_SHARED_SECRET_HERE";

function onFormSubmit(e) {
  if (!SHABBOSVORTS_FUNCTION_URL) {
    throw new Error("Missing SHABBOSVORTS_FUNCTION_URL script property.");
  }
  if (!SHABBOSVORTS_SHARED_SECRET || SHABBOSVORTS_SHARED_SECRET === "YOUR_SHARED_SECRET_HERE") {
    throw new Error("Missing SHABBOSVORTS_SHARED_SECRET script property.");
  }

  const values = getResponseMap(e);
  if (Object.keys(values).length === 0) {
    throw new Error("No form values found in trigger payload. Use an installable form submit trigger.");
  }
  Logger.log(JSON.stringify(values));

  const payload = {
    submitterName: getFirstValue(values, "Name", "Full Name", "Your Name"),
    submitterEmail: getFirstValue(values, "Email", "Email Address", "Your Email"),
    title: getFirstValue(values, "Title", "Dvar Torah Title"),
    occasion: normalizeOccasionKey(
      getFirstValue(
        values,
        "Parsha",
        "Parsha key",
        "Parsha Key",
        "Parsha / Occasion",
        "Parsha / Yom Tov",
        "Parsha / Yomtov / Occasion",
        "Occasion",
        "Occasion / Parsha / Yom Tov"
      )
    ),
    body: getFirstValue(values, "Body", "Dvar Torah", "Dvar Torah Body", "Content"),
    sources: getFirstValue(values, "Sources", "Source", "Citations"),
    documentUrl: getFirstValue(
      values,
      "Google Docs Link",
      "Google Docs link",
      "Google Doc Link",
      "Google Doc URL",
      "Google Docs URL"
    ),
    contentPolicyAgreed: isAffirmative(
      getFirstValue(
        values,
        "I agree to the content policy",
        "Content Policy",
        "I agree",
        "I agree to the terms",
        "I accept the content policy"
      )
    ),
  };

  const response = UrlFetchApp.fetch(SHABBOSVORTS_FUNCTION_URL, {
    method: "post",
    contentType: "application/json",
    headers: {
      "x-shabbosvorts-secret": SHABBOSVORTS_SHARED_SECRET,
    },
    payload: JSON.stringify(payload),
    muteHttpExceptions: true,
  });

  const code = response.getResponseCode();
  if (code < 200 || code >= 300) {
    throw new Error("Submission failed: " + code + " " + response.getContentText());
  }
}

function getResponseMap(e) {
  const map = {};

  if (e && e.response && typeof e.response.getItemResponses === "function") {
    const itemResponses = e.response.getItemResponses();
    itemResponses.forEach((itemResponse) => {
      const title = itemResponse.getItem().getTitle();
      const response = itemResponse.getResponse();

      if (Array.isArray(response)) {
        map[title] = response.map((value) => String(value).trim());
      } else {
        map[title] = [String(response || "").trim()];
      }
    });
    return map;
  }

  if (e && e.namedValues && typeof e.namedValues === "object") {
    Object.entries(e.namedValues).forEach(([key, value]) => {
      if (Array.isArray(value)) {
        map[key] = value.map((entry) => String(entry).trim());
      } else {
        map[key] = [String(value || "").trim()];
      }
    });
  }

  return map;
}

function getFirstValue(namedValues, ...fieldNames) {
  const normalizedEntries = Object.entries(namedValues).map(([key, value]) => [
    normalizeFieldName(key),
    value,
  ]);

  for (const fieldName of fieldNames) {
    const normalizedTarget = normalizeFieldName(fieldName);
    const match = normalizedEntries.find(([key]) => key === normalizedTarget);
    if (match) {
      const values = match[1];
      if (Array.isArray(values) && values.length > 0) {
        return String(values[0]).trim();
      }
    }
  }

  return "";
}

function normalizeFieldName(value) {
  return String(value || "")
    .trim()
    .toLowerCase()
    .replace(/\s+/g, " ")
    .replace(/[:*]/g, "");
}

function normalizeOccasionKey(value) {
  const normalized = String(value || "")
    .trim()
    .toLowerCase()
    .replace(/’/g, "")
    .replace(/'/g, "")
    .replace(/-/g, "_")
    .replace(/\s+/g, "_")
    .replace(/,/g, "_")
    .replace(/__+/g, "_");

  const aliases = {
    bereshit: "bereishit",
    bereishit: "bereishit",
    noach: "noach",
    lech_lecha: "lech_lecha",
    vayera: "vayera",
    chayei_sara: "chayei_sara",
    toldot: "toldot",
    vayetzei: "vayetzei",
    vayishlach: "vayishlach",
    vayeshev: "vayeshev",
    miketz: "miketz",
    vayigash: "vayigash",
    vayechi: "vayechi",
    shemot: "shemot",
    vaera: "vaera",
    bo: "bo",
    beshalach: "beshalach",
    yitro: "yitro",
    mishpatim: "mishpatim",
    terumah: "terumah",
    tetzaveh: "tetzaveh",
    ki_tisa: "ki_tisa",
    vayakhel: "vayakhel",
    pekudei: "pekudei",
    vayakhel_pekudei: "vayakhel_pekudei",
    vayikra: "vayikra",
    tzav: "tzav",
    shemini: "shemini",
    shmini: "shemini",
    tazria: "tazria",
    metzora: "metzora",
    tazria_metzora: "tazria_metzora",
    acharei_mot: "acharei_mot",
    kedoshim: "kedoshim",
    acharei_mot_kedoshim: "acharei_mot_kedoshim",
    emor: "emor",
    behar: "behar",
    bechukotai: "bechukotai",
    behar_bechukotai: "behar_bechukotai",
    bamidbar: "bamidbar",
    naso: "naso",
    behaalotecha: "behaalotecha",
    shelach: "shelach",
    shelach_lecha: "shelach",
    korach: "korach",
    chukat: "chukat",
    hukat: "chukat",
    balak: "balak",
    chukat_balak: "chukat_balak",
    hukat_balak: "chukat_balak",
    pinchas: "pinchas",
    matot: "matot",
    masei: "masei",
    matot_masei: "matot_masei",
    devarim: "devarim",
    vaetchanan: "vaetchanan",
    eikev: "eikev",
    ekev: "eikev",
    reeh: "reeh",
    shoftim: "shoftim",
    ki_teitzei: "ki_teitzei",
    ki_tavo: "ki_tavo",
    nitzavim: "nitzavim",
    vayeilech: "vayeilech",
    nitzavim_vayeilech: "nitzavim_vayeilech",
    haazinu: "haazinu",
    vezot_habracha: "vezot_habracha",
    vezot_haberakhah: "vezot_habracha",
    rosh_hashana: "rosh_hashana",
    rosh_hashanah: "rosh_hashana",
    aseres_yemei_teshuvah: "aseres_yemei_teshuvah",
    aseret_yemei_teshuvah: "aseres_yemei_teshuvah",
    yom_kippur: "yom_kippur",
    sukkot: "sukkot",
    succot: "sukkot",
    sukkos: "sukkot",
    hoshana_rabba: "hoshana_rabba",
    hoshana_rabbah: "hoshana_rabba",
    hoshana_raba: "hoshana_rabba",
    shemini_atzeret: "shemini_atzeret",
    shmini_atzeret: "shemini_atzeret",
    simchat_torah: "simchat_torah",
    simchas_torah: "simchat_torah",
    chanukah: "chanukah",
    hanukkah: "chanukah",
    chanuka: "chanukah",
    taanis_esther: "taanis_esther",
    taanit_esther: "taanis_esther",
    purim: "purim",
    pesach: "pesach",
    passover: "pesach",
    leil_haseder: "leil_haseder",
    lel_haseder: "leil_haseder",
    seder_night: "leil_haseder",
    shevii_shel_pesach: "shevii_shel_pesach",
    seventh_day_pesach: "shevii_shel_pesach",
    sefirat_haomer: "sefirat_haomer",
    sefiras_haomer: "sefirat_haomer",
    omer: "sefirat_haomer",
    lag_baomer: "lag_baomer",
    lag_laomer: "lag_baomer",
    shavuot: "shavuot",
    shavuos: "shavuot",
    seventeenth_of_tammuz: "seventeenth_of_tammuz",
    shivah_asar_btammuz: "seventeenth_of_tammuz",
    "17_tammuz": "seventeenth_of_tammuz",
    "17th_of_tammuz": "seventeenth_of_tammuz",
    three_weeks: "three_weeks",
    bein_hametzarim: "three_weeks",
    tisha_baav: "tisha_baav",
    tisha_bav: "tisha_baav",
    tishah_beav: "tisha_baav",
    tu_baav: "tu_baav",
    tu_bav: "tu_baav",
    tenth_of_tevet: "tenth_of_tevet",
    asara_bteves: "tenth_of_tevet",
    asarah_btevet: "tenth_of_tevet",
    bris_milah: "bris_milah",
    brit_milah: "bris_milah",
    bris: "bris_milah",
    brit: "bris_milah",
    pidyon_haben: "pidyon_haben",
    pidyon: "pidyon_haben",
    engagement: "engagement",
    erusin: "engagement",
    eirusin: "engagement",
    vort: "engagement",
    wedding: "wedding",
    chasuna: "wedding",
    chasunah: "wedding",
    chatuna: "wedding",
    chuppah: "wedding",
    sheva_berachot: "sheva_berachot",
    sheva_berachos: "sheva_berachot",
    sheva_brachos: "sheva_berachot",
    bar_mitzvah: "bar_mitzvah",
    bar_mitzva: "bar_mitzvah",
    bat_mitzvah: "bat_mitzvah",
    bat_mitzva: "bat_mitzvah",
  };

  return aliases[normalized] || normalized;
}

function isAffirmative(value) {
  const normalized = String(value || "").trim().toLowerCase();
  return [
    "true",
    "yes",
    "on",
    "1",
    "i agree",
    "agree",
    "accepted",
    "i accept",
  ].includes(normalized);
}
