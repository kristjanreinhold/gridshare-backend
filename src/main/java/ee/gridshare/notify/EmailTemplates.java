package ee.gridshare.notify;

/** Branded HTML email layout (table-based + inline styles for email-client compatibility). */
public final class EmailTemplates {

    private EmailTemplates() {}

    /** A simple card: heading, intro paragraph, and a CTA button. */
    public static String card(String heading, String intro, String ctaLabel, String ctaUrl) {
        return """
            <!doctype html>
            <html lang="et">
            <body style="margin:0;padding:0;background:#f4f6fb;font-family:-apple-system,Segoe UI,Roboto,Helvetica,Arial,sans-serif;">
              <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background:#f4f6fb;">
                <tr><td align="center" style="padding:32px 16px;">
                  <table role="presentation" width="480" cellpadding="0" cellspacing="0" style="max-width:480px;background:#ffffff;border-radius:14px;overflow:hidden;box-shadow:0 2px 14px rgba(10,15,30,0.08);">
                    <tr><td style="padding:22px 28px;background:#0a0f1e;">
                      <span style="font-size:18px;font-weight:700;color:#ffffff;letter-spacing:0.3px;">Grid<span style="color:#00d05a;">Share</span> EV</span>
                    </td></tr>
                    <tr><td style="padding:30px 28px;">
                      <h1 style="font-size:20px;line-height:1.3;margin:0 0 12px;color:#0a0f1e;">%s</h1>
                      <p style="font-size:15px;line-height:1.65;color:#475569;margin:0 0 26px;">%s</p>
                      <a href="%s" style="display:inline-block;background:#16a34a;color:#ffffff;text-decoration:none;padding:13px 26px;border-radius:10px;font-size:15px;font-weight:600;">%s</a>
                    </td></tr>
                    <tr><td style="padding:18px 28px;border-top:1px solid #eef1f6;font-size:12px;color:#94a3b8;line-height:1.5;">
                      GridShare EV — eralaadijate jagamisturg.<br/>
                      Said selle kirja, kuna sinu laadijaga seotud broneering muutus.
                    </td></tr>
                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """
                .formatted(heading, intro, ctaUrl, ctaLabel);
    }
}
