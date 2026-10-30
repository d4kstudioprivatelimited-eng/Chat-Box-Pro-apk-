# Chat Box Pro — Node.js Email Proxy Server (Hosted on Render)

Secure middleman email proxy server designed exclusively for **Chat Box Pro**, powered by **D 4K Studio Private Limited** (Lead Developer: **Danish Raja Ji**).

## 🚀 Purpose & Separation of Concerns
1. **Zero Database Operations**: The server has only one job: securely dispatching OTP verification and Welcome emails through the Brevo SMTP API.
2. **Anti-IP Blocking Architecture**: Running on Render provides a fixed, reliable cloud server environment with static outgoing IP addresses, preventing mobile client IP rate-limiting and security blocks.
3. **Secret Protection**: Keeps the `BREVO_API_KEY` protected on the server environment.

---

## 🛠️ Deploying to Render in 3 Minutes

### Option A: Using Render Blueprints (Automatic)
1. Fork or push this repository to your GitHub account.
2. Log in to [dashboard.render.com](https://dashboard.render.com).
3. Click **New +** &rarr; **Blueprint**.
4. Connect your repository. Render will automatically detect `/server/render.yaml`.
5. Under Environment Variables, enter your live `BREVO_API_KEY` from Brevo (Sendinblue).
6. Click **Apply**. Render will deploy your service at `https://chatboxpro-email-proxy.onrender.com`.

### Option B: Manual Web Service
- **Root Directory**: `server`
- **Build Command**: `npm install`
- **Start Command**: `node index.js`
- **Environment Variables**:
  - `PORT`: `10000`
  - `BREVO_API_KEY`: `xkeysib-...` (Your Brevo API Key)
  - `SENDER_NAME`: `Chat Box Pro`
  - `SENDER_EMAIL`: `noreply@mr4k09.000.pe`

---

## 📡 API Endpoints

### 1. Health Check
`GET /health`
```json
{
  "status": "ok",
  "service": "D 4K Studio Secure Mail Proxy",
  "version": "1.2.0",
  "platform": "Render"
}
```

### 2. Send Branded OTP Email
`POST /send-otp`
```json
{
  "email": "user@example.com",
  "name": "Alex Developer",
  "appName": "Chat Box Pro",
  "otp": "963208"
}
```
**Response**:
```json
{
  "success": true,
  "message": "Official verification code sent to user@example.com",
  "otp": "963208",
  "messageId": "<202610291122.9812@smtp-relay.brevo.com>"
}
```

### 3. Send Official Welcome Email
`POST /send-welcome`
```json
{
  "email": "user@example.com",
  "name": "Alex Developer",
  "avatarUrl": "https://your-supabase-url.supabase.co/storage/v1/object/public/avatars/avatar_123.jpg",
  "appName": "Chat Box Pro"
}
```
