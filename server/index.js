/**
 * Chat Box Pro - Secure Node.js Email Proxy Server
 * Hosted on Render for D 4K Studio Private Limited.
 *
 * Dedicated strictly to calling the Brevo SMTP API from a static, reliable server environment
 * to prevent IP blocking, protect the Brevo API Key, and maintain complete separation of concerns.
 */

require('dotenv').config();
const express = require('express');
const cors = require('cors');
const { generateOtpEmailHtml } = require('./templates/otpEmailTemplate');
const { generateWelcomeEmailHtml } = require('./templates/welcomeEmailTemplate');

const app = express();
const PORT = process.env.PORT || 10000;
const BREVO_API_KEY = process.env.BREVO_API_KEY || '';
const BREVO_API_URL = 'https://api.brevo.com/v3/smtp/email';

const SENDER_NAME = process.env.SENDER_NAME || 'Chat Box Pro';
const SENDER_EMAIL = process.env.SENDER_EMAIL || 'noreply@mr4k09.000.pe';

// Enable CORS for mobile app requests and cloud services
app.use(cors({
  origin: '*',
  methods: ['GET', 'POST', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization', 'X-Client-App']
}));

app.use(express.json());

// In-memory OTP storage for optional proxy-side verification
const activeOtpSessions = new Map();
const OTP_EXPIRY_MS = 10 * 60 * 1000; // 10 minutes

/**
 * Health check endpoint
 */
app.get(['/', '/health'], (req, res) => {
  res.json({
    status: 'ok',
    service: 'D 4K Studio Secure Mail Proxy',
    version: '1.2.0',
    platform: 'Render',
    company: 'D 4K Studio Private Limited',
    leadDeveloper: 'Danish Raja Ji',
    brevoKeyConfigured: Boolean(BREVO_API_KEY && BREVO_API_KEY !== 'my_brevo_api_key_placeholder'),
    timestamp: new Date().toISOString()
  });
});

/**
 * Helper function to forward an email to Brevo SMTP API
 */
async function sendBrevoEmail({ recipientEmail, recipientName, subject, htmlContent }) {
  if (!BREVO_API_KEY || BREVO_API_KEY === 'my_brevo_api_key_placeholder') {
    console.warn('[Proxy Warning] BREVO_API_KEY is not configured. Emulating successful dispatch.');
    return {
      success: true,
      simulated: true,
      message: 'Email dispatch simulated (Configure BREVO_API_KEY on Render Environment Variables for live delivery).'
    };
  }

  const payload = {
    sender: {
      name: SENDER_NAME,
      email: SENDER_EMAIL
    },
    to: [
      {
        email: recipientEmail,
        name: recipientName || recipientEmail.split('@')[0]
      }
    ],
    subject,
    htmlContent
  };

  const response = await fetch(BREVO_API_URL, {
    method: 'POST',
    headers: {
      'api-key': BREVO_API_KEY,
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      'User-Agent': 'ChatBoxPro-RenderProxy/1.2.0'
    },
    body: JSON.stringify(payload)
  });

  const responseText = await response.text();
  let json;
  try {
    json = JSON.parse(responseText);
  } catch {
    json = { raw: responseText };
  }

  if (!response.ok) {
    const errorMsg = json?.message || `Brevo HTTP Error ${response.status}`;
    console.error(`[Brevo API Error ${response.status}]:`, responseText);
    const err = new Error(errorMsg);
    err.status = response.status;
    err.details = json;
    throw err;
  }

  return {
    success: true,
    messageId: json?.messageId,
    data: json
  };
}

/**
 * POST /send-otp
 * Body: { email, name?, appName?, otp? }
 */
app.post(['/send-otp', '/api/send-otp'], async (req, res) => {
  try {
    const { email, name = '', appName = 'Chat Box Pro', otp } = req.body;

    if (!email || !email.includes('@')) {
      return res.status(400).json({
        success: false,
        error: 'Valid recipient email address is required.'
      });
    }

    // Generate or use provided 6-digit OTP
    const otpCode = otp ? String(otp).trim() : String(Math.floor(100000 + Math.random() * 900000));

    // Store in active sessions map
    activeOtpSessions.set(email.toLowerCase().trim(), {
      otp: otpCode,
      createdAt: Date.now()
    });

    const htmlContent = generateOtpEmailHtml({
      recipientEmail: email,
      otpCode,
      recipientName: name,
      appName
    });

    const subject = `🔐 [${otpCode}] Official Verification Code — D 4K Studio Private Limited`;

    const dispatchResult = await sendBrevoEmail({
      recipientEmail: email.trim(),
      recipientName: name,
      subject,
      htmlContent
    });

    console.log(`[OTP Dispatched] Sent OTP [${otpCode}] to ${email}`);

    res.json({
      success: true,
      message: `Official verification code sent to ${email}`,
      otp: otpCode, // Provided for client verification flow
      messageId: dispatchResult.messageId,
      simulated: dispatchResult.simulated || false
    });
  } catch (err) {
    console.error('[send-otp Error]:', err.message);
    res.status(err.status || 500).json({
      success: false,
      error: err.message || 'Failed to dispatch verification email via Brevo.',
      details: err.details || null
    });
  }
});

/**
 * POST /verify-otp
 * Body: { email, otp }
 */
app.post(['/verify-otp', '/api/verify-otp'], (req, res) => {
  const { email, otp } = req.body;
  if (!email || !otp) {
    return res.status(400).json({
      success: false,
      error: 'Email and OTP are required.'
    });
  }

  const key = email.toLowerCase().trim();
  const session = activeOtpSessions.get(key);

  if (!session) {
    return res.status(404).json({
      success: false,
      error: 'No active OTP session found. Please request a new code.'
    });
  }

  if (Date.now() - session.createdAt > OTP_EXPIRY_MS) {
    activeOtpSessions.delete(key);
    return res.status(410).json({
      success: false,
      error: 'OTP has expired (10 minutes validity). Please request a new code.'
    });
  }

  if (session.otp === String(otp).trim()) {
    activeOtpSessions.delete(key); // consume OTP
    return res.json({
      success: true,
      message: 'OTP verified successfully!'
    });
  }

  res.status(401).json({
    success: false,
    error: 'Incorrect verification code. Please check your email and try again.'
  });
});

/**
 * POST /send-welcome
 * Body: { email, name, avatarUrl?, appName? }
 */
app.post(['/send-welcome', '/api/send-welcome'], async (req, res) => {
  try {
    const { email, name = '', avatarUrl = '', appName = 'Chat Box Pro' } = req.body;

    if (!email || !email.includes('@')) {
      return res.status(400).json({
        success: false,
        error: 'Valid recipient email address is required.'
      });
    }

    const htmlContent = generateWelcomeEmailHtml({
      recipientEmail: email,
      recipientName: name,
      avatarUrl,
      appName
    });

    const subject = `✨ Welcome to ${appName} — Your Account is Ready!`;

    const dispatchResult = await sendBrevoEmail({
      recipientEmail: email.trim(),
      recipientName: name,
      subject,
      htmlContent
    });

    console.log(`[Welcome Dispatched] Sent Welcome email to ${email} (${name})`);

    res.json({
      success: true,
      message: `Welcome email sent successfully to ${email}`,
      messageId: dispatchResult.messageId,
      simulated: dispatchResult.simulated || false
    });
  } catch (err) {
    console.error('[send-welcome Error]:', err.message);
    res.status(err.status || 500).json({
      success: false,
      error: err.message || 'Failed to dispatch welcome email via Brevo.',
      details: err.details || null
    });
  }
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`====================================================`);
  console.log(`🚀 Chat Box Pro - Node.js Email Proxy Server Active`);
  console.log(`📡 Listening on http://0.0.0.0:${PORT}`);
  console.log(`🏢 Powered by D 4K Studio Private Limited`);
  console.log(`👨‍💻 Lead Developer: Danish Raja Ji`);
  console.log(`====================================================`);
});
