/**
 * Generates the exact HTML/CSS OTP Verification Email layout matching the
 * official D 4K Studio Private Limited screenshots.
 *
 * @param {string} recipientEmail - Recipient's email address
 * @param {string} otpCode - 6-digit verification passcode
 * @param {string} recipientName - Recipient's name (defaults to name derived from email)
 * @param {string} appName - Name of the application (defaults to 'Chat Box Pro')
 * @returns {string} Fully styled HTML document
 */
function generateOtpEmailHtml({
  recipientEmail,
  otpCode,
  recipientName = '',
  appName = 'Chat Box Pro'
}) {
  const displayName = recipientName || (recipientEmail ? recipientEmail.split('@')[0] : 'User');
  const formattedOtp = otpCode.split('').join(' ');

  return `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>🔐 [${otpCode}] Official Verification Code — D 4K Studio Private Limited</title>
</head>
<body style="margin: 0; padding: 20px 10px; background-color: #f1f5f9; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; color: #1e293b;">
  <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0" align="center">
    <tr>
      <td align="center">
        <table role="presentation" width="100%" style="max-width: 580px; background-color: #ffffff; border-radius: 20px; overflow: hidden; box-shadow: 0 10px 25px rgba(0,0,0,0.08); border: 1px solid #cbd5e1;" cellspacing="0" cellpadding="0" border="0">
          
          <!-- Top Orange/Gold Bar Accent -->
          <tr>
            <td style="height: 5px; background: linear-gradient(90deg, #f59e0b, #fbbf24, #f59e0b);"></td>
          </tr>

          <!-- Header Banner (Dark Blue Gradient) -->
          <tr>
            <td style="background: linear-gradient(180deg, #0b1426 0%, #111e38 100%); padding: 36px 28px; text-align: center;">
              <div style="display: inline-block; border: 1px solid #f59e0b; background-color: rgba(245, 158, 11, 0.12); color: #fbbf24; font-size: 11px; font-weight: 800; padding: 5px 16px; border-radius: 20px; letter-spacing: 1px; text-transform: uppercase; margin-bottom: 14px;">
                &#10004; OFFICIAL VERIFIED SECURITY
              </div>
              <h1 style="color: #ffffff; font-size: 26px; font-weight: 800; margin: 0 0 6px 0; letter-spacing: -0.5px;">
                D 4K Studio Private Limited
              </h1>
              <p style="color: #94a3b8; font-size: 13px; font-weight: 500; margin: 0; letter-spacing: 0.2px;">
                Secure Identity &amp; Cloud Verification Service
              </p>
            </td>
          </tr>

          <!-- Body Content Area -->
          <tr>
            <td style="padding: 32px 28px 24px 28px;">
              <p style="font-size: 16px; margin: 0 0 14px 0; color: #0f172a;">
                Hello <span style="color: #2563eb; font-weight: 700;">${displayName}</span>,
              </p>
              <p style="font-size: 14px; line-height: 22px; color: #334155; margin: 0 0 20px 0;">
                A secure verification request was initiated for your account on <strong>D 4K Studio Private Limited</strong>. Please use the confidential verification details below to complete your <strong>Developer Console Registration for "${appName}"</strong>:
              </p>

              <!-- Light Blue Callout Notice Box -->
              <div style="background-color: #eff6ff; border: 1px solid #bfdbfe; border-radius: 10px; padding: 14px 18px; margin-bottom: 26px;">
                <p style="font-size: 13px; line-height: 19px; color: #1e40af; margin: 0;">
                  Once verified, your project "${appName}" will be activated as your custom Automatic Email Sender Name for all your app users.
                </p>
              </div>

              <!-- Passcode Box with Rounded Rect & Dark Code Display -->
              <div style="background-color: #f8fafc; border: 1px solid #e2e8f0; border-radius: 16px; padding: 24px 16px; text-align: center; margin-bottom: 26px;">
                <div style="font-size: 11px; font-weight: 700; color: #64748b; letter-spacing: 1.5px; text-transform: uppercase; margin-bottom: 12px;">
                  ONE-TIME SECURITY PASSCODE (TAP CODE TO COPY)
                </div>
                <div style="background-color: #0b1426; border-radius: 12px; padding: 14px 24px; display: inline-block; margin: 0 auto 12px auto; box-shadow: 0 4px 12px rgba(0,0,0,0.15);">
                  <span style="color: #fbbf24; font-size: 36px; font-weight: 900; letter-spacing: 8px; font-family: 'Courier New', Courier, monospace; display: inline-block; vertical-align: middle;">
                    ${formattedOtp}
                  </span>
                </div>
                <div style="font-size: 12px; font-weight: 600; color: #2563eb;">
                  &#9201; Valid for 10 Minutes &bull; Single-Use Only
                </div>
              </div>

              <!-- Security Information Bullet Box (Blue Left Border) -->
              <div style="border-left: 4px solid #3b82f6; background-color: #f8fafc; border-radius: 0 8px 8px 0; padding: 14px 18px; margin-bottom: 20px;">
                <ul style="margin: 0; padding-left: 14px; font-size: 13px; color: #334155; line-height: 22px;">
                  <li><strong>Service / Application:</strong> D 4K Studio Private Limited</li>
                  <li><strong>Action Requested:</strong> Developer Console Registration for "${appName}"</li>
                  <li><strong>Security Standard:</strong> 256-Bit SSL &amp; TiDB Cloud Encrypted</li>
                </ul>
              </div>

              <!-- Security Notice -->
              <p style="font-size: 13px; line-height: 19px; color: #64748b; margin: 0 0 8px 0;">
                &#128274; <strong>Security Notice:</strong> This verification is valid for <strong>10 minutes</strong>. Never share this code or link with anyone.
              </p>
            </td>
          </tr>

          <!-- Footer Area (Dark Background Matching Screenshots) -->
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

              <!-- Two Action Buttons (Official Website & Phone) -->
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

module.exports = { generateOtpEmailHtml };
