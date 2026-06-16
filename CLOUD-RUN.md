# Deploy backend to Google Cloud Run (project: ev-gridshare)

No load balancer needed — Cloud Run gives an autoscaled HTTPS URL out of the box.
Region: **europe-north1** (Finland, EU — GDPR).

## 0. One-time setup
```bash
gcloud auth login
gcloud config set project ev-gridshare
gcloud services enable run.googleapis.com sqladmin.googleapis.com cloudbuild.googleapis.com
```

## 1. Cloud SQL (Postgres)
```bash
gcloud sql instances create gridshare-db \
  --database-version=POSTGRES_16 --tier=db-f1-micro --region=europe-north1
gcloud sql databases create gridshare --instance=gridshare-db
gcloud sql users create gridshare --instance=gridshare-db --password='<DB_PASSWORD>'

# Connection name → goes into DATABASE_URL (should be ev-gridshare:europe-north1:gridshare-db)
gcloud sql instances describe gridshare-db --format='value(connectionName)'
```

## 2. Env file
```bash
cp deploy/env.example.yaml deploy/env.yaml
# edit deploy/env.yaml — fill DB_PASSWORD, JWT_SECRET, GOOGLE_CLIENT_SECRET,
# TWILIO_AUTH_TOKEN, SENDGRID_API_KEY  (env.yaml is gitignored)
```

## 3. Deploy (Cloud Build builds the Dockerfile)
```bash
gcloud run deploy gridshare-backend \
  --source . \
  --region=europe-north1 \
  --allow-unauthenticated \
  --add-cloudsql-instances=ev-gridshare:europe-north1:gridshare-db \
  --env-vars-file=deploy/env.yaml
```
Flyway applies the schema + seed on first boot. Grab the URL:
```bash
gcloud run services describe gridshare-backend --region=europe-north1 --format='value(status.url)'
# → https://gridshare-backend-xxxxx.europe-north1.run.app
```

## 4. Wire the frontend + OAuth to that URL
- **Cloudflare** (frontend project) env vars → redeploy:
  - `VITE_API_BASE = https://gridshare-backend-xxxxx.run.app`
  - `VITE_API_ORIGIN = https://gridshare-backend-xxxxx.run.app`
- **Google Console** (OAuth client):
  - Authorized redirect URI: `https://gridshare-backend-xxxxx.run.app/login/oauth2/code/google`
  - Authorized JS origin: `https://gridshare.ee` (your frontend domain)

## 5. (Optional) api.gridshare.ee
Cloudflare DNS → CNAME `api` → the run.app host (proxied), or use Cloud Run domain
mapping. Then update VITE_API_BASE / OAuth redirect to `https://api.gridshare.ee`.

## Redeploy later
```bash
gcloud run deploy gridshare-backend --source . --region=europe-north1 --env-vars-file=deploy/env.yaml
```
