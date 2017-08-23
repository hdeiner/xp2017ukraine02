package test.com.deinersoft.timeteller;

import com.deinersoft.timeteller.TimeTeller;
import com.sun.mail.imap.IMAPFolder;
import org.junit.Before;
import org.junit.Test;

import javax.mail.*;
import javax.mail.Flags.Flag;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class TimeTellerTest {

    private TimeTeller timeTeller;

    @Before
    public void initialize(){
         timeTeller = new TimeTeller();
    }

    @Test
    public void gotLocalTimeCurrent(){
        assertThat(timeTeller.getResult(1,1,false), is(getFormattedTime(LocalDateTime.now())));
    }

    @Test
    public void gotZuluTimeCurrent(){
        assertThat(timeTeller.getResult(2,1,false), is(getFormattedTime(LocalDateTime.now(Clock.systemUTC()))+"Z"));
    }

    @Test
    public void gotLocalTimeInWordsCurrent(){
        assertThat(timeTeller.getResult(1,2,false), matchesPattern("^(\\s|one|two|three|four|five|six|seven|eight|nine|ten|eleven|twelve|twenty|almost|a|quarter|half|of|past|after|before|at|night|in|the|morning|afternoon|evening|night)+$"));
    }

    @Test
    public void gotZuluTimeInWordsCurrent(){
        assertThat(timeTeller.getResult(2,2,false), matchesPattern("^(\\s|one|two|three|four|five|six|seven|eight|nine|ten|eleven|twelve|twenty|almost|a|quarter|half|of|past|after|before|at|night|in|the|morning|afternoon|evening|night)+Zulu$"));
    }

    @Test
    public void gotEmailForLocalTime(){
        String localTimeNowFormatted = getFormattedTime(LocalDateTime.now());
        timeTeller.getResult(1,1,true);

        boolean receivedEmail = false;
        for (int readAttempts = 1; (readAttempts <= 5) && (!receivedEmail); readAttempts++ ) {
            receivedEmail = lookForTimeTellerEmail(localTimeNowFormatted);
        }
        assertThat(receivedEmail, is(true));
    }

    private String getFormattedTime(LocalDateTime clock){
        int localHour = clock.getHour();
        int localMinute = clock.getMinute();
        int localSecond = clock.getSecond();
        return String.format("%02d:%02d:%02d", localHour, localMinute, localSecond);
    }

    private boolean lookForTimeTellerEmail(String localTimeNowFormatted){
        boolean receivedEmail = false;
        IMAPFolder folder = null;
        Store store = null;
        try {
            Properties props = System.getProperties();
            props.setProperty("mail.store.protocol", "imaps");

            Session session = Session.getDefaultInstance(props, null);
            store = session.getStore("imaps");
            store.connect("imap.googlemail.com","howarddeiner.xyzzy@gmail.com", "birneraccount");

            folder = (IMAPFolder) store.getFolder("inbox");
            if(!folder.isOpen()) {
                folder.open(Folder.READ_WRITE);
                Message[] messages = folder.getMessages();
                for (Message msg : messages) {
                    if (msg.getSubject().equals("TimeTeller")) {
                        if (((String) msg.getContent()).contains(localTimeNowFormatted)) {
                            receivedEmail = true;
                            msg.setFlag(Flag.DELETED, true);
                        }
                    }
                }
            }
        }
        catch (Exception e) { }
        finally {
            try {
                if (folder != null && folder.isOpen()) folder.close(true);
                if (store != null) store.close();
            }
            catch (Exception e) { }
        }

        if (!receivedEmail) {
            try { TimeUnit.SECONDS.sleep(1); }
            catch(InterruptedException e){ }
        }

        return receivedEmail;
    }
}