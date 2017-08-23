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
        int localHour = LocalDateTime.now().getHour();
        int localMinute = LocalDateTime.now().getMinute();
        int localSecond = LocalDateTime.now().getSecond();
        String localTimeNowFormatted = String.format("%02d:%02d:%02d", localHour, localMinute, localSecond);
        assertThat(timeTeller.getResult(1,1,false), is(localTimeNowFormatted));
    }

    @Test
    public void gotZuluTimeCurrent(){
        LocalDateTime zuluTime = LocalDateTime.now(Clock.systemUTC());
        int zuluHour = zuluTime.getHour();
        int zuluMinute = zuluTime.getMinute();
        int zuluSecond = zuluTime.getSecond();
        String zuluTimeNowFormatted = String.format("%02d:%02d:%02dZ", zuluHour, zuluMinute, zuluSecond);
        assertThat(timeTeller.getResult(2,1,false), is(zuluTimeNowFormatted));
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
        int localHour = LocalDateTime.now().getHour();
        int localMinute = LocalDateTime.now().getMinute();
        int localSecond = LocalDateTime.now().getSecond();
        String localTimeNowFormatted = String.format("%02d:%02d:%02d", localHour, localMinute, localSecond);
        timeTeller.getResult(1,1,true);

        boolean receivedEmail = false;
        for (int readAttempts = 1; (readAttempts <= 5) && (!receivedEmail); readAttempts++ ) {
            IMAPFolder folder = null;
            Store store = null;
            String subject = null;
            Flag flag = null;
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
                    for (int i=0; i < messages.length; i++) {
                        Message msg = messages[i];
                        if (msg.getSubject().equals("TimeTeller")) {
                            if (((String)msg.getContent()).contains(localTimeNowFormatted)){
                                receivedEmail = true;
                                msg.setFlag(Flags.Flag.DELETED, true);
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
        }

        assertThat(receivedEmail, is(true));
    }
}