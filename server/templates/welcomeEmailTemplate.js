/**
 * Generates the official Welcome Email for Chat Box Pro matching the exact D 4K Studio layout.
 *
 * @param {string} recipientEmail - Recipient's email address
 * @param {string} recipientName - Recipient's display name
 * @param {string} avatarUrl - URL of profile DP uploaded to Supabase storage
 * @param {string} appName - Application name (defaults to 'Chat Box Pro')
 * @returns {string} Fully styled HTML document
 */
function generateWelcomeEmailHtml({
  recipientEmail,
  recipientName = '',
  avatarUrl = '',
  appName = 'Chat Box Pro'
}) {
  const displayName = recipientName || (recipientEmail ? recipientEmail.split('@')[0] : 'User');

  return `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>✨ Welcome to ${appName} — Official Account Activated</title>
</head>
<body style="margin: 0; padding: 20px 10px; background-color: #f1f5f9; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; color: #1e293b;">
  <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0" align="center">
    <tr>
      <td align="center">
        <table role="presentation" width="100%" style="max-width: 580px; background-color: #ffffff; border-radius: 20px; overflow: hidden; box-shadow: 0 10px 25px rgba(0,0,0,0.08); border: 1px solid #cbd5e1;" cellspacing="0" cellpadding="0" border="0">
          
          <!-- Top Emerald Bar Accent -->
          <tr>
            <td style="height: 5px; background: linear-gradient(90deg, #10b981, #25D366, #059669);"></td>
          </tr>

          <!-- Header Banner (Dark Blue Gradient) -->
          <tr>
            <td style="background: linear-gradient(180deg, #0b1426 0%, #111e38 100%); padding: 36px 28px; text-align: center;">
              <div style="display: inline-block; border: 1px solid #25D366; background-color: rgba(37, 211, 102, 0.15); color: #25D366; font-size: 11px; font-weight: 800; padding: 5px 16px; border-radius: 20px; letter-spacing: 1px; text-transform: uppercase; margin-bottom: 14px;">
                &#9733; OFFICIAL VERIFIED ACCOUNT
              </div>
              <h1 style="color: #ffffff; font-size: 26px; font-weight: 800; margin: 0 0 6px 0; letter-spacing: -0.5px;">
                Welcome to ${appName}
              </h1>
              <p style="color: #94a3b8; font-size: 13px; font-weight: 500; margin: 0; letter-spacing: 0.2px;">
                Next-Generation Encrypted Realtime Messenger &bull; D 4K Studio Private Limited
              </p>
            </td>
          </tr>

          <!-- Body Content Area -->
          <tr>
            <td style="padding: 32px 28px 24px 28px;">
              <p style="font-size: 18px; margin: 0 0 14px 0; color: #0f172a; font-weight: 700;">
                Hello <span style="color: #059669;">${displayName}</span>,
              </p>
              <p style="font-size: 14px; line-height: 22px; color: #334155; margin: 0 0 20px 0;">
                Congratulations! Your profile setup for <strong>${appName}</strong> is now complete, your Profile DP has been secured via encrypted Supabase cloud storage, and your account is active on our real-time network.
              </p>

              <!-- Pro Capabilities Box -->
              <div style="background-color: #f0fdf4; border: 1px solid #bbf7d0; border-radius: 14px; padding: 18px 20px; margin-bottom: 24px;">
                <div style="font-size: 12px; font-weight: 800; color: #166534; letter-spacing: 1px; text-transform: uppercase; margin-bottom: 10px;">
                  &#128640; INCLUDED PRO CAPABILITIES
                </div>
                <ul style="margin: 0; padding-left: 18px; font-size: 13px; color: #15803d; line-height: 24px;">
                  <li><strong>End-to-End Encryption:</strong> Zero third-party surveillance on chats &amp; media.</li>
                  <li><strong>VoIP HD Voice &amp; Video Calling:</strong> Ultra low-latency crystal clear calls.</li>
                  <li><strong>24-Hour Stories &amp; Status:</strong> Ephemeral visual updates with privacy filters.</li>
                  <li><strong>Offline APK Self-Extraction:</strong> Export &amp; distribute ${appName} via Bluetooth or WhatsApp.</li>
                  <li><strong>11-Language Global Switcher:</strong> Instant real-time localization.</li>
                </ul>
              </div>

              <!-- Account Metadata Details -->
              <div style="background-color: #f8fafc; border: 1px solid #e2e8f0; border-radius: 12px; padding: 16px 20px; margin-bottom: 20px;">
                <table width="100%" cellspacing="0" cellpadding="4" style="font-size: 13px; color: #334155;">
                  <tr>
                    <td width="35%" style="color: #64748b; font-weight: 600;">Account Email:</td>
                    <td style="font-weight: 700; color: #0f172a;">${recipientEmail}</td>
                  </tr>
                  <tr>
                    <td style="color: #64748b; font-weight: 600;">Display Name:</td>
                    <td style="font-weight: 700; color: #0f172a;">${displayName}</td>
                  </tr>
                  <tr>
                    <td style="color: #64748b; font-weight: 600;">Security Standard:</td>
                    <td style="color: #059669; font-weight: 700;">256-Bit SSL &amp; Cloud E2EE Encrypted</td>
                  </tr>
                </table>
              </div>

              <p style="font-size: 13px; line-height: 19px; color: #64748b; margin: 0 0 16px 0;">
                You can manage your biometrics, App Lock PIN, and privacy settings anytime within the app.
              </p>
            </td>
          </tr>

          <!-- Footer Area Matching Screenshots -->
          <tr>
            <td style="background-color: #0b1426; padding: 26px 28px; text-align: center; border-top: 1px solid #1e293b;">
              <div style="display: inline-block; background-color: rgba(59, 130, 246, 0.15); border: 1px solid #3b82f6; border-radius: 6px; padding: 6px 14px; margin-bottom: 12px;">
                <span style="color: #60a5fa; font-size: 11px; font-weight: 800; letter-spacing: 0.5px;">
                  &#9889; POWERED &amp; SECURED BY D 4K STUDIO PRIVATE LIMITED
                </span>
              </div>
              
              <p style="color: #94a3b8; font-size: 12px; margin: 0 0 4px 0; font-weight: 600;">
                Lead Developer: <strong style="color: #f1f5f9;">Danish Raja Ji</strong>
              </p>
              <p style="color: #64748b; font-size: 11px; margin: 0 0 16px 0;">
                Headquarters: Naya Basti, Bokaro Thermal, Jharkhand, India
              </p>

              <!-- Two Action Buttons -->
              <table role="presentation" cellspacing="0" cellpadding="0" border="0" align="center" style="margin: 0 auto 16px auto;">
                <tr>
                  <td style="padding: 0 6px;">
                    <a href="https://mr4k09.000.pe" target="_blank" style="background-color: #2563eb; color: #ffffff; text-decoration: none; font-size: 12px; font-weight: 700; padding: 8px 18px; border-radius: 6px; display: inline-block;">
                      Official Website
                    </a>
                  </td>
                  <td style="padding: 0 6px;">
                    <a href="tel:+919430320021" style="background-color: #1e293b; color: #fbbf24; border: 1px solid #334155; text-decoration: none; font-size: 12px; font-weight: 700; padding: 8px 16px; border-radius: 6px; display: inline-block;">
                      +91 9430320021
                    </a>
                  </td>
                </tr>
              </table>

              <p style="color: #475569; font-size: 10px; margin: 0;">
                &copy; 2026 D 4K Studio Private Limited. All rights reserved.
              </p>
            </td>
          </tr>

        </table>
      </td>
    </tr>
  </table>
</body>
</html>`;
}

module.exports = { generateWelcomeEmailHtml };
