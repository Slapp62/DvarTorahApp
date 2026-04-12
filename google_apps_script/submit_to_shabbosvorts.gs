const SHABBOSVORTS_FUNCTION_URL = "YOUR_FUNCTION_URL_HERE";
const SHABBOSVORTS_SHARED_SECRET = "YOUR_SHARED_SECRET_HERE";

function onFormSubmit(e) {
  const values = e.namedValues || {};
  const payload = {
    submitterName: getFirstValue(values, "Name"),
    submitterEmail: getFirstValue(values, "Email"),
    title: getFirstValue(values, "Title"),
    occasion: getFirstValue(values, "Parsha key"),
    body: getFirstValue(values, "Body"),
    sources: getFirstValue(values, "Sources"),
    documentUrl: getFirstValue(values, "Google Docs link"),
    contentPolicyAgreed: isAffirmative(getFirstValue(values, "I agree to the content policy")),
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

function getFirstValue(namedValues, fieldName) {
  const values = namedValues[fieldName];
  return Array.isArray(values) && values.length > 0 ? String(values[0]).trim() : "";
}

function isAffirmative(value) {
  const normalized = String(value || "").trim().toLowerCase();
  return normalized === "true" || normalized === "yes" || normalized === "on" || normalized === "1";
}
