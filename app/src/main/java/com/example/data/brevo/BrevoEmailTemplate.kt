package com.example.data.brevo

/**
 * Generates an HTML/CSS email template that replicates the structure, layout, styling,
 * and colors from the official D 4K Studio Private Limited / Chat Box Pro verification email.
 */
object BrevoEmailTemplate {

    fun generateVerificationEmailHtml(
        recipientEmail: String,
        otpCode: String,
        appName: String = "Chat Box Pro",
        companyName: String = "D 4K Studio Private Limited",
        developerName: String = "Danish Raja Ji",
        headquarters: String = "Naya Basti, Bokaro Thermal, Jharkhand, India",
        websiteUrl: String = "https://mr4k09.000.pe",
        phoneNumber: String = "+91 9430320021"
    ): String {
        val emailPrefix = recipientEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
        val formattedOtp = otpCode.map { "$it " }.joinToString("").trim()

        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>🔐 [$otpCode] Official Verification Code — $appName</title>
</head>
<body style="margin: 0; padding: 20px 10px; background-color: #e2e8f0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; color: #1e293b;">
    <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0" align="center">
        <tr>
            <td align="center">
                <table role="presentation" width="100%" style="max-width: 580px; background-color: #ffffff; border-radius: 20px; overflow: hidden; box-shadow: 0 10px 25px rgba(0,0,0,0.1); border: 1px solid #cbd5e1;" cellspacing="0" cellpadding="0" border="0">
                    
                    <!-- Top Gold Accent Bar -->
                    <tr>
                        <td style="height: 5px; background: linear-gradient(90deg, #f59e0b, #fbbf24, #f59e0b);"></td>
                    </tr>

                    <!-- Header Banner -->
                    <tr>
                        <td style="background: linear-gradient(180deg, #0b1426 0%, #111e38 100%); padding: 36px 28px; text-align: center;">
                            <div style="display: inline-block; border: 1px solid #f59e0b; background-color: rgba(245, 158, 11, 0.12); color: #fbbf24; font-size: 11px; font-weight: 800; padding: 5px 16px; border-radius: 20px; letter-spacing: 1px; text-transform: uppercase; margin-bottom: 14px;">
                                &#10004; OFFICIAL VERIFIED SECURITY
                            </div>
                            <h1 style="color: #ffffff; font-size: 26px; font-weight: 800; margin: 0 0 6px 0; letter-spacing: -0.5px;">
                                $companyName
                            </h1>
                            <p style="color: #94a3b8; font-size: 13px; font-weight: 500; margin: 0; letter-spacing: 0.2px;">
                                Secure Identity & Cloud Verification Service • $appName
                            </p>
                        </td>
                    </tr>

                    <!-- Body Content -->
                    <tr>
                        <td style="padding: 32px 28px 24px 28px;">
                            <p style="font-size: 16px; margin: 0 0 14px 0; color: #0f172a;">
                                Hello <span style="color: #2563eb; font-weight: 700;">$emailPrefix</span>,
                            </p>
                            <p style="font-size: 14px; line-height: 22px; color: #334155; margin: 0 0 20px 0;">
                                A secure verification request was initiated for your account on <strong>$companyName</strong>. Please use the confidential verification details below to complete your <strong>Developer Console Registration for "$appName"</strong>:
                            </p>

                            <!-- Light Blue Notice Box -->
                            <div style="background-color: #eff6ff; border: 1px solid #bfdbfe; border-radius: 10px; padding: 14px 18px; margin-bottom: 26px;">
                                <p style="font-size: 13px; line-height: 19px; color: #1e40af; margin: 0;">
                                    Once verified, your project "$appName" will be activated as your custom Automatic Email Sender Name for all your app users.
                                </p>
                            </div>

                            <!-- Passcode Box -->
                            <div style="background-color: #f8fafc; border: 1px solid #e2e8f0; border-radius: 16px; padding: 24px 16px; text-align: center; margin-bottom: 26px;">
                                <div style="font-size: 11px; font-weight: 700; color: #64748b; letter-spacing: 1.5px; text-transform: uppercase; margin-bottom: 12px;">
                                    ONE-TIME SECURITY PASSCODE (TAP CODE TO COPY)
                                </div>
                                <div style="background-color: #0b1426; border-radius: 12px; padding: 14px 24px; display: inline-block; margin: 0 auto 12px auto; box-shadow: 0 4px 12px rgba(0,0,0,0.15);">
                                    <span style="color: #fbbf24; font-size: 36px; font-weight: 900; letter-spacing: 8px; font-family: 'Courier New', Courier, monospace; display: inline-block; vertical-align: middle;">
                                        $formattedOtp
                                    </span>
                                </div>
                                <div style="font-size: 12px; font-weight: 600; color: #2563eb;">
                                    &#9201; Valid for 10 Minutes &bull; Single-Use Only
                                </div>
                            </div>

                            <!-- Metadata Info Card -->
                            <div style="border-left: 4px solid #3b82f6; background-color: #f8fafc; border-radius: 0 8px 8px 0; padding: 14px 18px; margin-bottom: 20px;">
                                <ul style="margin: 0; padding-left: 14px; font-size: 13px; color: #334155; line-height: 22px;">
                                    <li><strong>Service / Application:</strong> $companyName</li>
                                    <li><strong>Action Requested:</strong> Developer Console Registration for "$appName"</li>
                                    <li><strong>Security Standard:</strong> 256-Bit SSL &amp; TiDB Cloud Encrypted</li>
                                </ul>
                            </div>

                            <p style="font-size: 13px; line-height: 19px; color: #64748b; margin: 0 0 8px 0;">
                                &#128274; <strong>Security Notice:</strong> This verification is valid for <strong>10 minutes</strong>. Never share this code or link with anyone.
                            </p>
                        </td>
                    </tr>

                    <!-- Footer Section -->
                    <tr>
                        <td style="background-color: #0b1426; border-top: 3px solid #f59e0b; padding: 28px 24px; text-align: center;">
                            <div style="display: inline-block; background-color: rgba(56, 189, 248, 0.12); color: #38bdf8; font-size: 11px; font-weight: 800; padding: 6px 14px; border-radius: 8px; letter-spacing: 0.8px; margin-bottom: 14px; text-transform: uppercase;">
                                &#9889; POWERED & SECURED BY $companyName
                            </div>
                            <p style="color: #ffffff; font-size: 14px; font-weight: 700; margin: 0 0 4px 0;">
                                Lead Developer: $developerName
                            </p>
                            <p style="color: #94a3b8; font-size: 12px; margin: 0 0 18px 0;">
                                Headquarters: $headquarters
                            </p>

                            <!-- Action Buttons -->
                            <table role="presentation" cellspacing="0" cellpadding="0" border="0" align="center" style="margin: 0 auto 20px auto;">
                                <tr>
                                    <td style="padding: 0 6px;">
                                        <a href="$websiteUrl" target="_blank" style="background-color: #2563eb; color: #ffffff; text-decoration: none; padding: 9px 18px; border-radius: 8px; font-size: 12px; font-weight: 700; display: inline-block;">
                                            Official Website
                                        </a>
                                    </td>
                                    <td style="padding: 0 6px;">
                                        <a href="tel:${phoneNumber.replace(" ", "")}" style="background-color: transparent; border: 1px solid #f59e0b; color: #fbbf24; text-decoration: none; padding: 8px 18px; border-radius: 8px; font-size: 12px; font-weight: 700; display: inline-block;">
                                            $phoneNumber
                                        </a>
                                    </td>
                                </tr>
                            </table>

                            <p style="color: #64748b; font-size: 11px; margin: 0;">
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
