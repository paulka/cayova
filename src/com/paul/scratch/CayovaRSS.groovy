package com.paul.scratch

import net.sf.json.JSONArray
import net.sf.json.JSONObject
import net.sf.json.JSONSerializer
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat

class CayovaRSS {

    static final DateTimeFormatter formatISO = ISODateTimeFormat.dateTime();
    static final DateTimeFormatter formatRFC822 = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss Z");

    public static void main(String[] args) throws Exception {

        while (true) {
            RunUpdate()
            sleep(120000)
        }
    }

    private static void RunUpdate() {
        Feed feed = BuildCayovaFeedEnvelope()
        String jsonTxt = GetReponse("https://cayova.com/api/timelines/global?count=30&_=1370005561362");
        JSONSerializer.toJSON(jsonTxt).getJSONArray("objects").each { post ->
            feed.entries.add(GetPost(post))
        }
        RSSFeedWriter writer = new RSSFeedWriter(feed, "C:\\Users\\paul.kavanagh\\Documents\\Dropbox\\db\\cayova.rss");
        try {
            writer.write()
        } catch (Exception e) {
            e.printStackTrace()
        }
        System.out.println(formatRFC822.print(new Date().toString()) + ":" + feed.toString());
    }

    private static FeedMessage GetPost(post) {
        //TODO: Inline posted images, add enclosures for audio and video.
        JSONObject contact = post.getJSONObject("sender").getJSONObject("contact")
        String body = post.getString("body")
        String link = "https://cayova.com/post/" + post.getString("uid")
        FeedMessage message = new FeedMessage(
                author: contact.getString("displayName"),
                link: link,
                guid: link,
                description: body,
                title: (body.length() > 60) ? body.subSequence(0, 60) + "..." : body,
                modified: formatRFC822.print(formatISO.parseDateTime(post.getString("modified"))))
        message.enclosures = GetEnclosures(post)
        message
    }

    private static List<Enclosure> GetEnclosures(post) {
        List<Enclosure> enclosures = []
        Object attachments = post.get("attachments")
        if (attachments instanceof JSONArray) {
            attachments.each { attachment ->
                Object links = post.get("links")
                if (attachments instanceof JSONArray) {
                    links.each { link ->
                        if (link.getString("rel") == "original") {
                            Enclosure enc = new Enclosure(length: 0, type: link.getString("mimeType"), url: link.getString("url"))
                            enclosures.add(enc)
                        }
                    }
                }
            }
        }
        enclosures
    }




    protected static Feed BuildCayovaFeedEnvelope() {
        new Feed(title: "CAYOVA.com Global Timeline",
                link: "https://cayova.com/timeline/",
                description: "The CAYOVA Global Timeline RSS Feed",
                language: "en-IE",
                pubDate: formatRFC822.print(new DateTime()),
                image: "https://cayova.com/images/apple-touch-icon-retina.png")
    }

    public static String GetReponse(String strURL) {
        try {
            URL url = new URL(strURL);
            URLConnection con = url.openConnection();
            //String encoding = con.getContentEncoding();
            String body = convertStreamToString(con.getInputStream());
            //System.out.println("   " + body);
            body
        } catch (Exception ex) {
            throw ex
        }
    }


    public static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

}
