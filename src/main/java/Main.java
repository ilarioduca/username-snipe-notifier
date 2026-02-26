import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    /* list of names to test:

    takethetime (free)
    bealo (release window 1d17h left)

    */

    // search settings
    static final String username = "username";
    static final int freq = 30; // how often you want to send a request

    // network and format settings
    static final HttpClient client = HttpClient.newHttpClient();
    static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss");

    // token manually collected from https://www.minecraft.net/en-us/msaprofile/mygames/editprofile
    // f12, network, request named "profile", under response headers
    // lasts for 24h
    // todo automatic token fetch
    static final String bearerToken = "token goes here";

    // email settings
    final static String from = "sender@gmail.com";
    final static String to = "receiver1@gmail.com,receiver2@gmail.com"; // can be multiple, separated by comma
    final static String subject = "Username \"" + username + "\" now available";
    final static String password = "xxxx xxxx xxxx xxxx"; // your gmail app password

    //colors
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";


    public static void main(String[] args) {
        System.out.println("watching for username: " + username);
        System.out.println("checking every " + freq + " seconds...\n");

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(Main::printTokenExpiry, 0, 600, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(Main::checkName, 0, freq, TimeUnit.SECONDS);
    }


    static void checkName() {
        String time = LocalTime.now().format(fmt);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.minecraftservices.com/minecraft/profile/name/" + username + "/available"))
                    .header("Authorization", bearerToken)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            System.out.print("[" + time + "] username: " + ANSI_BLUE + username +ANSI_RESET + " status: " + body.replace("\n", "").replace("\r", "") + " → ");

            if (body.contains("\"AVAILABLE\"")) {
                System.out.println(ANSI_GREEN + "CLAIM IT!\n" + ANSI_RESET);
                sendEmail(from, to, subject, password);
                alertUser();

                // during drop window but not released yet
            } else if (body.contains("\"NOT_AVAILABLE\"")) {
                System.out.println("not yet"); // doesnt work

                // held by someone else (for some reason also applies before and during the drop window, until it drops)
            } else if (body.contains("\"DUPLICATE\"")) {
                System.out.println(ANSI_RED + "taken (or not released yet)" + ANSI_RESET);

                // bad word in name
            } else if (body.contains("\"NOT_ALLOWED\"")) {
                System.out.println(ANSI_RED + "not allowed" + ANSI_RESET);

            } else {
                System.out.println("??? unexpected: " + response.statusCode());
            }

        } catch (Exception e) {
            System.out.println("[" + time + "] !!!  error: " + e.getMessage());
        }
    }

    static void printTokenExpiry() {
        try {
            // JWT is three base64 parts split by "."
            //
            String[] parts = bearerToken.split("\\.");
            // decode the payload (the middle), which contains the expiry time
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));

            // extract the "exp" field
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("\"exp\":(\\d+)").matcher(payload);
            if (m.find()) {
                long exp = Long.parseLong(m.group(1));
                long now = System.currentTimeMillis() / 1000;
                long secondsLeft = exp - now;
                long hoursLeft = secondsLeft / 3600;
                long minutesLeft = (secondsLeft % 3600) / 60;
                System.out.println("\n>>>>>>>>>> Session token expires in: " + hoursLeft + "h " + minutesLeft + "m <<<<<<<<<<\n");
            }
        } catch (Exception e) {
            System.out.println("couldn't parse token: " + e.getMessage());
        }
    }

    static void alertUser() {
        for (int i = 0; i < 5; i++) {
            System.out.println(ANSI_GREEN + "CLAIM IT NOW → https://www.minecraft.net/en-us/msaprofile/mygames/editprofile" + ANSI_RESET);
        }
    }

    // this only works with gmail because i'm lazy
    // change props to use a different service
    static void sendEmail(String from, String to, String subject, String password) {

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText("change it: \n\nhttps://www.minecraft.net/en-us/msaprofile/mygames/editprofile");
            Transport.send(message);
            System.out.println("email sent");
        } catch (MessagingException e) {
            System.out.println("failed to send email: " + e.getMessage());
        }
    }

    static String fetchToken() {

        //todo implement

        return "a";
    }

}