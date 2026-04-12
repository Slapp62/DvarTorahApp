const admin = require("firebase-admin");
const {onRequest} = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");
const {defineSecret} = require("firebase-functions/params");

admin.initializeApp();

const db = admin.firestore();
const desktopSubmissionSecret = defineSecret("DESKTOP_SUBMISSION_SECRET");
const FIELD_LIMITS = {
  submitterName: 120,
  submitterEmail: 200,
  title: 200,
  occasion: 80,
  sources: 4000,
  documentUrl: 2000,
  body: 40000,
};

function normalizeString(value, maxLength) {
  return String(value || "").trim().slice(0, maxLength);
}

function normalizeBoolean(value) {
  if (typeof value === "boolean") {
    return value;
  }

  const normalized = String(value || "").trim().toLowerCase();
  return normalized === "true" || normalized === "yes" || normalized === "on" || normalized === "1";
}

function validatePayload(body) {
  const normalizedOccasion = normalizeString(body.occasion, FIELD_LIMITS.occasion).toLowerCase();
  const payload = {
    submitterName: normalizeString(body.submitterName, FIELD_LIMITS.submitterName),
    submitterEmail: normalizeString(body.submitterEmail, FIELD_LIMITS.submitterEmail).toLowerCase(),
    title: normalizeString(body.title, FIELD_LIMITS.title),
    occasion: normalizedOccasion,
    body: normalizeString(body.body, FIELD_LIMITS.body),
    sources: normalizeString(body.sources, FIELD_LIMITS.sources),
    documentUrl: normalizeString(body.documentUrl, FIELD_LIMITS.documentUrl),
    contentPolicyAgreed: normalizeBoolean(body.contentPolicyAgreed),
  };

  if (!payload.submitterName) {
    return {error: "Missing submitterName"};
  }
  if (!payload.submitterEmail || !payload.submitterEmail.includes("@")) {
    return {error: "Missing valid submitterEmail"};
  }
  if (!payload.title) {
    return {error: "Missing title"};
  }
  if (!payload.occasion || !/^[a-z_]+$/.test(payload.occasion)) {
    return {error: "Invalid occasion key"};
  }
  if (!payload.body || payload.body.length < 40) {
    return {error: "Body is too short"};
  }
  if (!payload.contentPolicyAgreed) {
    return {error: "Content policy must be accepted"};
  }

  return {payload};
}

exports.submitDesktopDvar = onRequest(
  {
    cors: true,
    region: "us-central1",
    secrets: [desktopSubmissionSecret],
  },
  async (req, res) => {
    if (req.method !== "POST") {
      res.status(405).json({error: "Method not allowed"});
      return;
    }

    const configuredSecret = desktopSubmissionSecret.value();
    if (!configuredSecret) {
      logger.error("DESKTOP_SUBMISSION_SECRET is not configured");
      res.status(500).json({error: "Submission endpoint is not configured"});
      return;
    }

    const providedSecret = String(req.get("x-shabbosvorts-secret") || "").trim();
    if (!providedSecret || providedSecret !== configuredSecret) {
      res.status(401).json({error: "Unauthorized"});
      return;
    }

    const {payload, error} = validatePayload(req.body || {});
    if (error) {
      res.status(400).json({error});
      return;
    }

    try {
      const now = admin.firestore.FieldValue.serverTimestamp();
      const docRef = await db.collection("external_submissions").add({
        ...payload,
        status: "pending",
        submittedAt: now,
        reviewedAt: null,
        reviewedBy: "",
        adminNote: "",
        publishedDvarId: "",
      });

      res.status(200).json({
        ok: true,
        submissionId: docRef.id,
      });
    } catch (submissionError) {
      logger.error("submitDesktopDvar failed", submissionError);
      res.status(500).json({error: "Could not create submission"});
    }
  }
);
