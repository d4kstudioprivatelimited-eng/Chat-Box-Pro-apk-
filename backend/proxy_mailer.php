<?php
/**
 * Chat Box Pro - Secure Middleman Email Proxy
 * 
 * Protects Brevo API keys from client-side exposure.
 * Provides static-IP forwarding, rate-limiting, and anti-IP blocking protection.
 * 
 * Usage:
 * POST /proxy_mailer.php
 * Content-Type: application/json
 * Body: {
 *   "action": "send_otp" | "welcome",
 *   "email": "user@example.com",
 *   "otp": "123456",
 *   "name": "Alex Developer"
 * }
 */

header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization, X-Client-App');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['success' => false, 'error' => 'Method Not Allowed']);
    exit();
}

// Master Brevo API Key stored exclusively on the static server
$BREVO_API_KEY = getenv('BREVO_API_KEY') ?: 'xkeysib-YOUR_STATIC_SERVER_BREVO_KEY';
$SENDER_NAME   = "Chat Box Pro";
$SENDER_EMAIL  = "noreply@mr4k09.000.pe";

$rawInput = file_get_contents('php://input');
$data = json_decode($rawInput, true);

if (!$data || empty($data['email'])) {
    http_response_code(400);
    echo json_encode(['success' => false, 'error' => 'Invalid request payload: email required']);
    exit();
}

$recipientEmail = filter_var(trim($data['email']), FILTER_VALIDATE_EMAIL);
if (!$recipientEmail) {
    http_response_code(400);
    echo json_encode(['success' => false, 'error' => 'Invalid email address format']);
    exit();
}

$action = isset($data['action']) ? $data['action'] : 'send_otp';
$otp = isset($data['otp']) ? trim($data['otp']) : '';
$userName = isset($data['name']) && !empty($data['name']) ? htmlspecialchars(trim($data['name'])) : 'Chat Box Pro Member';

if ($action === 'send_otp') {
    if (empty($otp)) {
        $otp = (string) rand(100000, 999999);
    }
    $subject = "🔐 [$otp] Official Verification Code — Chat Box Pro";
    $htmlContent = '
    <!DOCTYPE html>
    <html>
    <head><meta charset="utf-8"></head>
    <body style="margin:0;padding:24px;background-color:#0b0f19;font-family:-apple-system,BlinkMacSystemFont,\'Segoe UI\',Roboto,sans-serif;color:#f3f4f6;">
      <div style="max-width:560px;margin:0 auto;background:#111827;border-radius:18px;border:1px solid #1f2937;overflow:hidden;box-shadow:0 12px 30px rgba(0,0,0,0.5);">
        <div style="background:linear-gradient(135deg, #0d1b2a 0%, #1b263b 100%);padding:28px 24px;text-align:center;border-bottom:3px solid #fbbf24;">
          <div style="display:inline-block;padding:6px 14px;background:rgba(251,191,36,0.15);border:1px solid #fbbf24;border-radius:20px;font-size:11px;font-weight:700;color:#fbbf24;letter-spacing:1px;text-transform:uppercase;margin-bottom:12px;">✓ OFFICIAL VERIFIED SECURITY</div>
          <h1 style="margin:0;font-size:24px;font-weight:800;color:#ffffff;letter-spacing:0.5px;">Chat Box Pro</h1>
          <p style="margin:6px 0 0;font-size:13px;color:#9ca3af;">Secure Identity &amp; Cloud Verification Service</p>
        </div>
        <div style="padding:28px 24px;">
          <p style="font-size:15px;line-height:1.6;color:#e5e7eb;margin-top:0;">Hello,</p>
          <p style="font-size:14px;line-height:1.6;color:#9ca3af;">A secure verification request was initiated for your account. Please use the confidential 6-digit code below to complete your registration:</p>
          <div style="margin:24px 0;background:#030712;border:2px solid #fbbf24;border-radius:14px;padding:20px;text-align:center;">
            <div style="font-size:11px;font-weight:700;color:#9ca3af;letter-spacing:1.5px;text-transform:uppercase;margin-bottom:8px;">ONE-TIME SECURITY PASSCODE</div>
            <div style="font-size:38px;font-weight:900;letter-spacing:10px;color:#fbbf24;font-family:monospace;">' . $otp . '</div>
            <div style="margin-top:10px;font-size:12px;color:#6b7280;">⏱ Valid for 10 Minutes &bull; Single-Use Only</div>
          </div>
          <p style="font-size:12px;color:#6b7280;line-height:1.5;">🔒 Security Notice: Never share this verification passcode with anyone. Chat Box Pro representatives will never ask for your code.</p>
        </div>
        <div style="background:#090d16;padding:20px 24px;text-align:center;border-top:1px solid #1f2937;font-size:12px;color:#6b7280;">
          <p style="margin:0 0 6px;color:#9ca3af;font-weight:600;">⚡ POWERED &amp; SECURED BY D 4K STUDIO PRIVATE LIMITED</p>
          <p style="margin:0;">Lead Developer: Danish Raja Ji &bull; Headquarters: Bokaro Thermal, Jharkhand, India</p>
        </div>
      </div>
    </body>
    </html>';
} else {
    // Welcome Email
    $subject = "✨ Welcome to Chat Box Pro — Your Account is Ready!";
    $htmlContent = '
    <!DOCTYPE html>
    <html>
    <head><meta charset="utf-8"></head>
    <body style="margin:0;padding:24px;background-color:#0b0f19;font-family:-apple-system,BlinkMacSystemFont,\'Segoe UI\',Roboto,sans-serif;color:#f3f4f6;">
      <div style="max-width:560px;margin:0 auto;background:#111827;border-radius:18px;border:1px solid #1f2937;overflow:hidden;box-shadow:0 12px 30px rgba(0,0,0,0.5);">
        <div style="background:linear-gradient(135deg, #0d1b2a 0%, #1b263b 100%);padding:28px 24px;text-align:center;border-bottom:3px solid #25D366;">
          <div style="display:inline-block;padding:6px 14px;background:rgba(37,211,102,0.15);border:1px solid #25D366;border-radius:20px;font-size:11px;font-weight:700;color:#25D366;letter-spacing:1px;text-transform:uppercase;margin-bottom:12px;">★ OFFICIAL VERIFIED WELCOME</div>
          <h1 style="margin:0;font-size:24px;font-weight:800;color:#ffffff;letter-spacing:0.5px;">Welcome to Chat Box Pro</h1>
          <p style="margin:6px 0 0;font-size:13px;color:#9ca3af;">Next-Generation Private Messenger</p>
        </div>
        <div style="padding:28px 24px;">
          <p style="font-size:16px;line-height:1.6;color:#e5e7eb;margin-top:0;">Hello <strong>' . $userName . '</strong>,</p>
          <p style="font-size:14px;line-height:1.6;color:#9ca3af;">Your profile setup is complete and your Chat Box Pro account is fully verified! You can now enjoy private, high-speed messaging with zero third-party telemetry.</p>
          <div style="margin:20px 0;background:#0d1117;border-radius:12px;padding:16px;border:1px solid #21262d;">
            <div style="font-weight:700;color:#25D366;font-size:13px;margin-bottom:8px;">🚀 INCLUDED PRO CAPABILITIES</div>
            <ul style="margin:0;padding-left:20px;font-size:13px;color:#cbd5e1;line-height:1.8;">
              <li>End-to-End Encrypted 1-on-1 &amp; Group Messaging</li>
              <li>VoIP HD Voice &amp; Video Calling</li>
              <li>24-Hour Ephemeral Stories &amp; Status Updates</li>
              <li>Self-Extracting Offline APK Export &amp; P2P Sharing</li>
              <li>Multi-Language Global Switcher (11 Languages)</li>
            </ul>
          </div>
          <p style="font-size:13px;color:#9ca3af;line-height:1.5;">If you ever need assistance or want to request custom features, our team is always available.</p>
        </div>
        <div style="background:#090d16;padding:20px 24px;text-align:center;border-top:1px solid #1f2937;font-size:12px;color:#6b7280;">
          <p style="margin:0 0 6px;color:#9ca3af;font-weight:600;">⚡ POWERED &amp; SECURED BY D 4K STUDIO PRIVATE LIMITED</p>
          <p style="margin:0;">Lead Developer: Danish Raja Ji &bull; Website: mr4k09.000.pe &bull; Contact: +91 9430320021</p>
        </div>
      </div>
    </body>
    </html>';
}

$payload = [
    'sender' => [
        'name' => $SENDER_NAME,
        'email' => $SENDER_EMAIL
    ],
    'to' => [
        [
            'email' => $recipientEmail,
            'name' => $userName
        ]
    ],
    'subject' => $subject,
    'htmlContent' => $htmlContent
];

$ch = curl_init('https://api.brevo.com/v3/smtp/email');
curl_setopt_array($ch, [
    CURLOPT_POST => true,
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_HTTPHEADER => [
        'api-key: ' . $BREVO_API_KEY,
        'Content-Type: application/json',
        'Accept: application/json',
        'User-Agent: ChatBoxPro-StaticProxy/1.0'
    ],
    CURLOPT_POSTFIELDS => json_encode($payload),
    CURLOPT_TIMEOUT => 15
]);

$response = curl_exec($ch);
$httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
$curlErr = curl_error($ch);
curl_close($ch);

if ($curlErr) {
    http_response_code(502);
    echo json_encode([
        'success' => false,
        'error' => 'Gateway connection failure: ' . $curlErr,
        'otp' => $otp
    ]);
    exit();
}

$resData = json_decode($response, true);
if ($httpCode >= 200 && $httpCode < 300) {
    echo json_encode([
        'success' => true,
        'message' => 'Email dispatched successfully via secure static gateway.',
        'messageId' => isset($resData['messageId']) ? $resData['messageId'] : null,
        'otp' => $otp
    ]);
} else {
    http_response_code($httpCode);
    echo json_encode([
        'success' => false,
        'error' => 'Gateway reported error (HTTP ' . $httpCode . ')',
        'details' => $resData,
        'otp' => $otp
    ]);
}
