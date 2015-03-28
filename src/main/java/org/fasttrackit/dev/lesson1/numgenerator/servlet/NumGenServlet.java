package org.fasttrackit.dev.lesson1.numgenerator.servlet;
//test
import org.fasttrackit.dev.lesson1.numgenerator.NumGeneratorBusinessLogic;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

/**
 * Created by condor on 29/11/14.
 * FastTrackIT, 2015
 */


/* didactic purposes

Some items are intentionally not optimized or not coded in the right way

FastTrackIT 2015

*/

public class NumGenServlet extends HttpServlet {

    private static final String REQUEST_PARAM_RESTARTGAME = "requestRestartGame";
    private static final String REQUEST_PARAM_GUESSNUMBER = "requestGuessNumber";
    private static final String SESSION_KEY_RESTART = "_sessionKey_restart";
    private static final String SESSION_KEY_NUMBER_GENERATOR_BUSINESS_LOGIC = "_sessionKey_NumberGeneratorBusinessLogic";
    private static final String VALUE_INIT = "1";

    //json


    public void service(HttpServletRequest request, HttpServletResponse response) {


        HttpSession session = request.getSession(true);

        String requestRestartGame = request.getParameter(REQUEST_PARAM_RESTARTGAME);
        if (requestRestartGame != null && requestRestartGame.equals(VALUE_INIT)) {
            session.setAttribute(SESSION_KEY_RESTART, VALUE_INIT);
        }

        NumGeneratorBusinessLogic nbl;
        if ((session.getAttribute(SESSION_KEY_RESTART) != null) && (session.getAttribute(SESSION_KEY_RESTART).equals(VALUE_INIT))) {
            nbl = new NumGeneratorBusinessLogic();
            session.setAttribute(SESSION_KEY_NUMBER_GENERATOR_BUSINESS_LOGIC, nbl);
            session.setAttribute(SESSION_KEY_RESTART, null);
        } else {
            nbl = (NumGeneratorBusinessLogic) session.getAttribute(SESSION_KEY_NUMBER_GENERATOR_BUSINESS_LOGIC);
            if (nbl == null) {
                nbl = new NumGeneratorBusinessLogic();
                session.setAttribute(SESSION_KEY_NUMBER_GENERATOR_BUSINESS_LOGIC, nbl);
            }
        }

        String jsonResponse;

        if (requestRestartGame != null && requestRestartGame.equals(VALUE_INIT)) {
            jsonResponse = "{\"keyRestartGame\":\"restartOk\"}";
            returnJsonResponse(response, jsonResponse);

        } else {

            String requestGuessNumber = request.getParameter(REQUEST_PARAM_GUESSNUMBER);
            int iGuessNumber = 0;
            boolean isANumber = true;
            try {
                iGuessNumber = Integer.parseInt(requestGuessNumber);
            } catch (NumberFormatException e) {
                isANumber = false;
            }

            if (isANumber) {
                boolean success = nbl.determineGuess(iGuessNumber);
                if(success){
                    sendMail();
                }
                String hint = nbl.getHint();
                int nrGuesses = nbl.getNumGuesses();
                jsonResponse = "{\"keySuccess\":\"" + success + "\", \"keyHint\":\"" + hint + "\", \"keyNrGuesses\":\"" + nrGuesses + "\", \"inputNumber\" :\"" +iGuessNumber +"\"}";

            } else {
                jsonResponse = "{\"keyError\":\"WRONGNUMBERFORMAT\"}";
            }

            returnJsonResponse(response, jsonResponse);

        }
    }

    /**/
    private void returnJsonResponse(HttpServletResponse response, String jsonResponse) {
        response.setContentType("application/json");
        PrintWriter pr = null;
        try {
            pr = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert pr != null;
        pr.write(jsonResponse);
        pr.close();
    }

    private void sendMail(){


        final String username = "pcursuri@gmail.com";
        final String password = "cursfast";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("pcursuri@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("mihai.traian.daniel@gmail.com"));
            message.setSubject("Num-guess");
            message.setText("Congratulation! You have won!");

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
