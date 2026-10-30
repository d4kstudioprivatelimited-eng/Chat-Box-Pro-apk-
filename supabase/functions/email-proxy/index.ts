// Supabase Edge Function: email-proxy
// Deploy with: supabase functions deploy email-proxy --no-verify-jwt
// Environment Variables: BREVO_API_KEY

import { serve } from "https://deno.land/std@0.168.0/http/server.ts";

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type, x-client-app",
  "Access-Control-Allow-Methods": "POST, OPTIONS",
};

serve(async (req: Request) => {
  if (req.method === "OPTIONS") {
    return new Response("ok", { headers: corsHeaders });
  }

  try {
    const { action, email, otp, name } = await req.json();

    if (!email) {
      return new Response(
        JSON.stringify({ success: false, error: "Recipient email is required" }),
        { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } }
      );
    }

    const brevoApiKey = Deno.env.get("BREVO_API_KEY") || "";
    const appName = "Chat Box Pro";
    const userName = name || "Chat Box Pro Member";

    let subject = "";
    let htmlContent = "";

    if (action === "welcome") {
      subject = "✨ Welcome to Chat Box Pro — Your Account is Ready!";
      htmlContent = `
        <!DOCTYPE html>
        <html>
        <head><meta charset="utf-8"></head>
        <body style="margin:0;padding:24px;background-color:#0b0f19;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;color:#f3f4f6;">
          <div style="max-width:560px;margin:0 auto;background:#111827;border-radius:18px;border:1px solid #1f2937;overflow:hidden;box-shadow:0 12px 30px rgba(0,0,0,0.5);">
            <div style="background:linear-gradient(135deg, #0d1b2a 0%, #1b263b 100%);padding:28px 24px;text-align:center;border-bottom:3px solid #25D366;">
              <div style="display:inline-block;padding:6px 14px;background:rgba(37,211,102,0.15);border:1px solid #25D366;border-radius:20px;font-size:11px;font-weight:700;color:#25D366;letter-spacing:1px;text-transform:uppercase;margin-bottom:12px;">★ OFFICIAL VERIFIED WELCOME</div>
              <h1 style="margin:0;font-size:24px;font-weight:800;color:#ffffff;">Welcome to Chat Box Pro</h1>
              <p style="margin:6px 0 0;font-size:13px;color:#9ca3af;">Next-Generation Private Messenger</p>
            </div>
            <div style="padding:28px 24px;">
              <p style="font-size:16px;line-height:1.6;color:#e5e7eb;margin-top:0;">Hello <strong>${userName}</strong>,</p>
              <p style="font-size:14px;line-height:1.6;color:#9ca3af;">Your profile setup is complete and your Chat Box Pro account is fully verified! You can now enjoy private, high-speed messaging with zero third-party telemetry.</p>
              <div style="margin:20px 0;background:#0d1117;border-radius:12px;padding:16px;border:1px solid #21262d;">
                <div style="font-weight:700;color:#25D366;font-size:13px;margin-bottom:8px;">🚀 INCLUDED PRO CAPABILITIES</div>
                <ul style="margin:0;padding-left:20px;font-size:13px;color:#cbd5e1;line-height:1.8;">
                  <li>End-to-End Encrypted 1-on-1 & Group Messaging</li>
                  <li>VoIP HD Voice & Video Calling</li>
                  <li>24-Hour Ephemeral Stories & Status Updates</li>
                  <li>Self-Extracting Offline APK Export & P2P Sharing</li>
                  <li>Multi-Language Global Switcher (11 Languages)</li>
                </ul>
              </div>
            </div>
            <div style="background:#090d16;padding:20px 24px;text-align:center;border-top:1px solid #1f2937;font-size:12px;color:#6b7280;">
              <p style="margin:0 0 6px;color:#9ca3af;font-weight:600;">⚡ POWERED &amp; SECURED BY D 4K STUDIO PRIVATE LIMITED</p>
              <p style="margin:0;">Lead Developer: Danish Raja Ji &bull; Website: mr4k09.000.pe</p>
            </div>
          </div>
        </body>
        </html>
      `;
    } else {
      // Default: send_otp
      const code = otp || Math.floor(100000 + Math.random() * 900000).toString();
      subject = `🔐 [${code}] Official Verification Code — Chat Box Pro`;
      htmlContent = `
        <!DOCTYPE html>
        <html>
        <head><meta charset="utf-8"></head>
        <body style="margin:0;padding:24px;background-color:#0b0f19;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;color:#f3f4f6;">
          <div style="max-width:560px;margin:0 auto;background:#111827;border-radius:18px;border:1px solid #1f2937;overflow:hidden;box-shadow:0 12px 30px rgba(0,0,0,0.5);">
            <div style="background:linear-gradient(135deg, #0d1b2a 0%, #1b263b 100%);padding:28px 24px;text-align:center;border-bottom:3px solid #fbbf24;">
              <div style="display:inline-block;padding:6px 14px;background:rgba(251,191,36,0.15);border:1px solid #fbbf24;border-radius:20px;font-size:11px;font-weight:700;color:#fbbf24;letter-spacing:1px;text-transform:uppercase;margin-bottom:12px;">✓ OFFICIAL VERIFIED SECURITY</div>
              <h1 style="margin:0;font-size:24px;font-weight:800;color:#ffffff;">Chat Box Pro</h1>
              <p style="margin:6px 0 0;font-size:13px;color:#9ca3af;">Secure Identity &amp; Cloud Verification Service</p>
            </div>
            <div style="padding:28px 24px;">
              <p style="font-size:15px;line-height:1.6;color:#e5e7eb;margin-top:0;">Hello,</p>
              <p style="font-size:14px;line-height:1.6;color:#9ca3af;">A secure verification request was initiated for your account. Please use the confidential 6-digit code below to complete your registration:</p>
              <div style="margin:24px 0;background:#030712;border:2px solid #fbbf24;border-radius:14px;padding:20px;text-align:center;">
                <div style="font-size:11px;font-weight:700;color:#9ca3af;letter-spacing:1.5px;text-transform:uppercase;margin-bottom:8px;">ONE-TIME SECURITY PASSCODE</div>
                <div style="font-size:38px;font-weight:900;letter-spacing:10px;color:#fbbf24;font-family:monospace;">${code}</div>
                <div style="margin-top:10px;font-size:12px;color:#6b7280;">⏱ Valid for 10 Minutes &bull; Single-Use Only</div>
              </div>
            </div>
            <div style="background:#090d16;padding:20px 24px;text-align:center;border-top:1px solid #1f2937;font-size:12px;color:#6b7280;">
              <p style="margin:0 0 6px;color:#9ca3af;font-weight:600;">⚡ POWERED &amp; SECURED BY D 4K STUDIO PRIVATE LIMITED</p>
              <p style="margin:0;">Lead Developer: Danish Raja Ji &bull; Headquarters: Bokaro Thermal, Jharkhand, India</p>
            </div>
          </div>
        </body>
        </html>
      `;
    }

    if (!brevoApiKey || brevoApiKey === "my_brevo_api_key_placeholder") {
      return new Response(
        JSON.stringify({
          success: true,
          message: "Secure proxy mock dispatch success (configure BREVO_API_KEY on Edge Function secret for live SMTP).",
          otp: otp || "123456",
        }),
        { headers: { ...corsHeaders, "Content-Type": "application/json" } }
      );
    }

    const brevoResp = await fetch("https://api.brevo.com/v3/smtp/email", {
      method: "POST",
      headers: {
        "api-key": brevoApiKey,
        "Content-Type": "application/json",
        "Accept": "application/json",
      },
      body: JSON.stringify({
        sender: { name: appName, email: "noreply@mr4k09.000.pe" },
        to: [{ email, name: userName }],
        subject,
        htmlContent,
      }),
    });

    const resJson = await brevoResp.json();
    return new Response(
      JSON.stringify({ success: brevoResp.ok, data: resJson, otp }),
      {
        status: brevoResp.status,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      }
    );
  } catch (err: any) {
    return new Response(
      JSON.stringify({ success: false, error: err.message || "Internal server error" }),
      { status: 500, headers: { ...corsHeaders, "Content-Type": "application/json" } }
    );
  }
});
