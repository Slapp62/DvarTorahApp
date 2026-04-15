const admin = require("firebase-admin");
const {onDocumentCreated, onDocumentUpdated} = require("firebase-functions/v2/firestore");
const {onRequest} = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");
const {defineString} = require("firebase-functions/params");

admin.initializeApp();

const db = admin.firestore();
const desktopSubmissionSecret = defineString("DESKTOP_SUBMISSION_SECRET");
const FIELD_LIMITS = {
  submitterName: 120,
  submitterEmail: 200,
  title: 200,
  occasion: 80,
  sources: 4000,
  documentUrl: 2000,
  body: 5000,
};
const APP_NAME = "Quick Dvar Torah";
const PRIVACY_POLICY_URL = "https://slapp62.github.io/DvarTorahApp/privacy-policy.html";
const ACCOUNT_DELETION_URL = "https://slapp62.github.io/DvarTorahApp/account-deletion.html";
const DESKTOP_INSTRUCTIONS_URL = "https://docs.google.com/document/d/1CZeG2rEpfX6OHArKpSdMwimFb_1TtD0qHcTuqN9LQI8/edit";

function normalizeString(value) {
  return String(value || "").trim();
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
    submitterName: normalizeString(body.submitterName),
    submitterEmail: normalizeString(body.submitterEmail).toLowerCase(),
    title: normalizeString(body.title),
    occasion: normalizedOccasion,
    body: normalizeString(body.body),
    sources: normalizeString(body.sources),
    documentUrl: normalizeString(body.documentUrl),
    contentPolicyAgreed: normalizeBoolean(body.contentPolicyAgreed),
  };

  if (!payload.submitterName) {
    return {error: "Missing submitterName"};
  }
  if (payload.submitterName.length > FIELD_LIMITS.submitterName) {
    return {error: "submitterName is too long"};
  }
  if (!payload.submitterEmail || !payload.submitterEmail.includes("@")) {
    return {error: "Missing valid submitterEmail"};
  }
  if (payload.submitterEmail.length > FIELD_LIMITS.submitterEmail) {
    return {error: "submitterEmail is too long"};
  }
  if (!payload.title) {
    return {error: "Missing title"};
  }
  if (payload.title.length > FIELD_LIMITS.title) {
    return {error: "Title is too long"};
  }
  if (!payload.occasion || !/^[a-z_]+$/.test(payload.occasion)) {
    return {error: "Invalid occasion key"};
  }
  if (payload.occasion.length > FIELD_LIMITS.occasion) {
    return {error: "Occasion key is too long"};
  }
  if (!payload.body || payload.body.length < 40) {
    return {error: "Body is too short"};
  }
  if (payload.body.length > FIELD_LIMITS.body) {
    return {error: "Body is too long"};
  }
  if (payload.sources.length > FIELD_LIMITS.sources) {
    return {error: "Sources are too long"};
  }
  if (payload.documentUrl.length > FIELD_LIMITS.documentUrl) {
    return {error: "Google Docs link is too long"};
  }
  if (!payload.contentPolicyAgreed) {
    return {error: "Content policy must be accepted"};
  }

  return {payload};
}

function getBrevoConfig() {
  return {
    apiKey: normalizeString(process.env.BREVO_API_KEY),
    senderEmail: normalizeString(process.env.BREVO_SENDER_EMAIL),
    senderName: normalizeString(process.env.BREVO_SENDER_NAME) || APP_NAME,
    writerFormUrl: normalizeString(process.env.BREVO_WRITER_FORM_URL),
    writerInstructionsUrl:
      normalizeString(process.env.BREVO_WRITER_INSTRUCTIONS_URL) || DESKTOP_INSTRUCTIONS_URL,
  };
}

function buildWriterApprovalEmail({displayName, writerFormUrl, writerInstructionsUrl}) {
  const firstName = normalizeString(displayName).split(/\s+/)[0] || "there";

  return {
    subject: `You're approved to write for ${APP_NAME}`,
    htmlContent: `
      <div style="margin:0;padding:0;background:#f8f3e9;font-family:Georgia,'Times New Roman',serif;color:#162a43;">
        <div style="max-width:640px;margin:0 auto;padding:32px 20px;">
          <div style="background:#fbf7ef;border-radius:24px;padding:32px 28px;box-shadow:0 18px 40px rgba(22,42,67,0.08);">
            <div style="font-size:12px;letter-spacing:0.18em;text-transform:uppercase;color:#b8842d;font-family:Arial,sans-serif;font-weight:700;margin-bottom:12px;">
              Writer Approved
            </div>
            <h1 style="margin:0 0 16px;font-size:34px;line-height:1.15;color:#162a43;">
              You're approved to write for ${APP_NAME}, ${firstName}
            </h1>
            <p style="margin:0 0 18px;font-size:18px;line-height:1.7;color:#4a5c70;">
              You now have writer access. The goal is to submit Divrei Torah that feel natural to say over at a Shabbos table: clear, relatable, rooted in Torah, and focused on one strong idea.
            </p>
            <div style="background:#f1e8d7;border-radius:18px;padding:18px 20px;margin:24px 0;">
              <div style="font-family:Arial,sans-serif;font-size:13px;font-weight:700;color:#162a43;margin-bottom:10px;">Best way to submit from desktop:</div>
              <ul style="padding-left:18px;margin:0;color:#4a5c70;font-size:15px;line-height:1.7;">
                <li>Open the desktop submission form and submit your Dvar Torah there.</li>
                <li>Read the submission instructions before your first piece.</li>
                <li>Aim for something that could comfortably be shared over a Shabbos table.</li>
              </ul>
            </div>
            <div style="margin:26px 0 8px;">
              ${writerFormUrl ? `<a href="${writerFormUrl}" style="display:inline-block;margin-right:12px;margin-bottom:10px;padding:12px 18px;border-radius:999px;background:#162a43;color:#ffffff;text-decoration:none;font-family:Arial,sans-serif;font-size:14px;font-weight:700;">Open Submission Form</a>` : ""}
              <a href="${writerInstructionsUrl}" style="display:inline-block;margin-right:12px;margin-bottom:10px;padding:12px 18px;border-radius:999px;background:#f1e8d7;color:#162a43;text-decoration:none;font-family:Arial,sans-serif;font-size:14px;font-weight:700;">Submission Instructions</a>
            </div>
            <div style="margin:6px 0 8px;">
              <a href="${PRIVACY_POLICY_URL}" style="display:inline-block;margin-right:12px;margin-bottom:10px;padding:12px 18px;border-radius:999px;background:#fbf7ef;color:#162a43;text-decoration:none;font-family:Arial,sans-serif;font-size:14px;font-weight:700;border:1px solid rgba(22,42,67,0.08);">Privacy Policy</a>
              <a href="${ACCOUNT_DELETION_URL}" style="display:inline-block;margin-bottom:10px;padding:12px 18px;border-radius:999px;background:#fbf7ef;color:#162a43;text-decoration:none;font-family:Arial,sans-serif;font-size:14px;font-weight:700;border:1px solid rgba(22,42,67,0.08);">Account Deletion</a>
            </div>
          </div>
        </div>
      </div>
    `.trim(),
    textContent: [
      `You're approved to write for ${APP_NAME}, ${firstName}.`,
      "",
      "You now have writer access.",
      "The best way to submit from desktop is through the Google Form.",
      "",
      writerFormUrl ? `Submission Form: ${writerFormUrl}` : "Submission Form: add BREVO_WRITER_FORM_URL to enable this link.",
      `Submission Instructions: ${writerInstructionsUrl}`,
      "",
      `Privacy Policy: ${PRIVACY_POLICY_URL}`,
      `Account Deletion: ${ACCOUNT_DELETION_URL}`,
    ].join("\n"),
  };
}

async function sendBrevoEmail({toEmail, toName, subject, htmlContent, textContent}) {
  const {apiKey, senderEmail, senderName} = getBrevoConfig();

  if (!apiKey || !senderEmail) {
    throw new Error("Brevo welcome email is not configured");
  }

  const response = await fetch("https://api.brevo.com/v3/smtp/email", {
    method: "POST",
    headers: {
      "accept": "application/json",
      "api-key": apiKey,
      "content-type": "application/json",
    },
    body: JSON.stringify({
      sender: {
        name: senderName,
        email: senderEmail,
      },
      to: [
        {
          email: toEmail,
          name: toName || undefined,
        },
      ],
      subject,
      htmlContent,
      textContent,
    }),
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`Brevo send failed: ${response.status} ${errorText}`);
  }

  return response.json();
}

exports.submitDesktopDvar = onRequest(
  {
    cors: true,
    region: "us-central1",
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

exports.sendWriterApprovalEmail = onDocumentUpdated(
  {
    document: "writer_applications/{applicationId}",
    region: "us-central1",
  },
  async (event) => {
    const before = event.data?.before?.data() || {};
    const after = event.data?.after?.data() || {};
    const applicationRef = event.data?.after?.ref;

    if (!applicationRef) {
      logger.warn("sendWriterApprovalEmail: missing application ref");
      return;
    }

    const statusChangedToApproved =
      normalizeString(before.status) !== "approved" &&
      normalizeString(after.status) === "approved";

    if (!statusChangedToApproved) {
      return;
    }

    if (after.writerApprovalEmailStatus === "sent") {
      return;
    }

    const applicantUid = normalizeString(after.applicantUid) || event.params.applicationId;
    const applicantEmail = normalizeString(after.applicantEmail).toLowerCase();
    const applicantName = normalizeString(after.applicantName);

    if (!applicantEmail || !applicantEmail.includes("@")) {
      logger.warn("Writer approval email skipped: missing applicant email", {
        applicantUid,
      });
      await applicationRef.set({
        writerApprovalEmailStatus: "skipped_missing_email",
      }, {merge: true});
      return;
    }

    const {apiKey, senderEmail, writerFormUrl, writerInstructionsUrl} = getBrevoConfig();
    if (!apiKey || !senderEmail) {
      logger.warn("Brevo writer approval email config is incomplete; skipping send", {
        applicantUid,
      });
      await applicationRef.set({
        writerApprovalEmailStatus: "skipped_unconfigured",
      }, {merge: true});
      return;
    }

    try {
      const emailContent = buildWriterApprovalEmail({
        displayName: applicantName,
        writerFormUrl,
        writerInstructionsUrl,
      });

      await sendBrevoEmail({
        toEmail: applicantEmail,
        toName: applicantName,
        subject: emailContent.subject,
        htmlContent: emailContent.htmlContent,
        textContent: emailContent.textContent,
      });

      await applicationRef.set({
        writerApprovalEmailStatus: "sent",
        writerApprovalEmailSentAt: admin.firestore.FieldValue.serverTimestamp(),
      }, {merge: true});

      await db.collection("users").doc(applicantUid).set({
        writerApprovalEmailStatus: "sent",
        writerApprovalEmailSentAt: admin.firestore.FieldValue.serverTimestamp(),
      }, {merge: true});
    } catch (error) {
      logger.error("sendWriterApprovalEmail failed", error);
      await applicationRef.set({
        writerApprovalEmailStatus: "failed",
        writerApprovalEmailError: normalizeString(error.message).slice(0, 500),
      }, {merge: true});
    }
  }
);
