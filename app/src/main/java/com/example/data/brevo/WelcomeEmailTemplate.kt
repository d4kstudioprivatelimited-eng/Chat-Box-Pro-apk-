package com.example.data.brevo

/**
 * Generates an official, high-polish HTML/CSS Welcome Email template
 * for Chat Box Pro users upon completing their profile setup.
 *
 * Replicates the D 4K Studio Private Limited brand aesthetic with emerald green
 * and gold accents, verified badge, and feature highlights.
 */
object WelcomeEmailTemplate {

    fun generateWelcomeEmailHtml(
        recipientEmail: String,
        userName: String,
        appName: String = "Chat Box Pro",
        companyName: String = "D 4K Studio Private Limited",
        developerName: String = "Danish Raja Ji",
        headquarters: String = "Naya Basti, Bokaro Thermal, Jharkhand, India",
        websiteUrl: String = "https://mr4k09.000.pe",
        phoneNumber: String = "+91 9430320021"
    ): String {
        val displayName = userName.ifBlank { recipientEmail.substringBefore("@").replaceFirstChar { it.uppercase() } }

        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>✨ Welcome to $appName — Your Profile is Live!</title>
</head>
<body style="margin: 0; padding: 20px 10px; background-color: #e2e8f0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; color: #1e293b;">
    <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0" align="center">
        <tr>
            <td align="center">
                <table role="presentation" width="100%" style="max-width: 580px; background-color: #ffffff; border-radius: 20px; overflow: hidden; box-shadow: 0 10px 25px rgba(0,0,0,0.1); border: 1px solid #cbd5e1;" cellspacing="0" cellpadding="0" border="0">
                    
                    <!-- Top Green Accent Bar -->
                    <tr>
                        <td style="height: 5px; background: linear-gradient(90deg, #10b981, #25D366, #059669);"></td>
                    </tr>

                    <!-- Header Banner -->
                    <tr>
                        <td style="background: linear-gradient(180deg, #0b1426 0%, #111e38 100%); padding: 36px 28px; text-align: center;">
                            <div style="display: inline-block; border: 1px solid #25D366; background-color: rgba(37, 211, 102, 0.15); color: #25D366; font-size: 11px; font-weight: 800; padding: 5px 16px; border-radius: 20px; letter-spacing: 1px; text-transform: uppercase; margin-bottom: 14px;">
                                &#9733; OFFICIAL VERIFIED ACCOUNT
                            </div>
                            <h1 style="color: #ffffff; font-size: 26px; font-weight: 800; margin: 0 0 6px 0; letter-spacing: -0.5px;">
                                Welcome to $appName
                            </h1>
                            <p style="color: #94a3b8; font-size: 13px; font-weight: 500; margin: 0; letter-spacing: 0.2px;">
                                Next-Generation Encrypted Realtime Messenger • $companyName
                            </p>
                        </td>
                    </tr>

                    <!-- Body Content -->
                    <tr>
                        <td style="padding: 32px 28px 24px 28px;">
                            <p style="font-size: 18px; margin: 0 0 14px 0; color: #0f172a; font-weight: 700;">
                                Hello <span style="color: #059669;">$displayName</span>,
                            </p>
                            <p style="font-size: 14px; line-height: 22px; color: #334155; margin: 0 0 20px 0;">
                                Congratulations! Your profile setup for <strong>$appName</strong> is now complete and your cryptographic identity is fully registered on our private cloud network.
                            </p>

                            <!-- Features List Card -->
                            <div style="background-color: #f0fdf4; border: 1px solid #bbf7d0; border-radius: 14px; padding: 18px 20px; margin-bottom: 24px;">
                                <div style="font-size: 12px; font-weight: 800; color: #166534; letter-spacing: 1px; text-transform: uppercase; margin-bottom: 10px;">
                                    🚀 YOUR INCLUDED PRO CAPABILITIES
                                </div>
                                <ul style="margin: 0; padding-left: 18px; font-size: 13px; color: #15803d; line-height: 24px;">
                                    <li><strong>End-to-End Encryption:</strong> Zero eavesdropping on messages or media.</li>
                                    <li><strong>VoIP Voice & Video Calling:</strong> High-definition crystal clear audio/video.</li>
                                    <li><strong>24-Hour Stories & Status:</strong> Ephemeral photo and text updates.</li>
                                    <li><strong>Self-Extracting APK:</strong> Offline distribution right from the Settings menu.</li>
                                    <li><strong>11-Language Switcher:</strong> Instant multilingual support in real-time.</li>
                                </ul>
                            </div>

                            <!-- Account Details Box -->
                            <div style="background-color: #f8fafc; border: 1px solid #e2e8f0; border-radius: 12px; padding: 16px 20px; margin-bottom: 20px;">
                                <table width="100%" cellspacing="0" cellpadding="4" style="font-size: 13px; color: #334155;">
                                    <tr>
                                        <td width="35%" style="color: #64748b; font-weight: 600;">Account Email:</td>
                                        <td style="font-weight: 700; color: #0f172a;">$recipientEmail</td>
                                    </tr>
                                    <tr>
                                        <td style="color: #64748b; font-weight: 600;">Display Name:</td>
                                        <td style="font-weight: 700; color: #0f172a;">$displayName</td>
                                    </tr>
                                    <tr>
                                        <td style="color: #64748b; font-weight: 600;">Security Level:</td>
                                        <td style="color: #059669; font-weight: 700;">256-Bit E2EE Cryptographic Key</td>
                                    </tr>
                                </table>
                            </div>

                            <p style="font-size: 13px; line-height: 19px; color: #64748b; margin: 0 0 16px 0;">
                                You can customize your settings, biometric app lock, and privacy preferences anytime within the app.
                            </p>
                        </td>
                    </tr>

                    <!-- Footer -->
                    <tr>
                        <td style="background-color: #0b1426; padding: 26px 28px; text-align: center; border-top: 1px solid #1e293b;">
                            <div style="display: inline-block; background-color: rgba(59, 130, 246, 0.15); border: 1px solid #3b82f6; border-radius: 6px; padding: 6px 14px; margin-bottom: 12px;">
                                <span style="color: #60a5fa; font-size: 11px; font-weight: 800; letter-spacing: 0.5px;">
                                    ⚡ POWERED & SECURED BY $companyName
                                </span>
                            </div>
                            
                            <p style="color: #94a3b8; font-size: 12px; margin: 0 0 4px 0; font-weight: 600;">
                                Lead Developer: <strong style="color: #f1f5f9;">$developerName</strong>
                            </p>
                            <p style="color: #64748b; font-size: 11px; margin: 0 0 16px 0;">
                                Headquarters: $headquarters
                            </p>

                            <!-- Action Buttons -->
                            <table role="presentation" cellspacing="0" cellpadding="0" border="0" align="center" style="margin: 0 auto 16px auto;">
                                <tr>
                                    <td style="padding: 0 6px;">
                                        <a href="$websiteUrl" target="_blank" style="background-color: #2563eb; color: #ffffff; text-decoration: none; font-size: 12px; font-weight: 700; padding: 8px 18px; border-radius: 6px; display: inline-block;">
                                            Official Website
                                        </a>
                                    </td>
                                    <td style="padding: 0 6px;">
                                        <a href="tel:$phoneNumber" style="background-color: #1e293b; color: #94a3b8; border: 1px solid #334155; text-decoration: none; font-size: 12px; font-weight: 600; padding: 8px 16px; border-radius: 6px; display: inline-block;">
                                            $phoneNumber
                                        </a>
                                    </td>
                                </tr>
                            </table>

                            <p style="color: #475569; font-size: 10px; margin: 0;">
                                &copy; 2026 $companyName. All rights reserved.
                            </p>
                        </td>
                    </tr>

                </table>
            </td>
        </tr>
    </table>
</body>
</html>
        """.trimIndent()
    }
}
