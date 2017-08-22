package test.com.deinersoft.timeteller;

import com.deinersoft.timeteller.TimeTeller;
import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.LocalDateTime;

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
        assertThat(timeTeller.getResult(1,2,false), matchesPattern("^(\\s|one|two|three|four|five|six|seven|eight|nine|ten|eleven|twelve|almost|a|quarter|half|of|after|before|at|night|in|the|morning|afternoon|evening|night)+$"));
    }

    @Test
    public void gotZuluTimeInWordsCurrent(){
        assertThat(timeTeller.getResult(2,2,false), matchesPattern("^(\\s|one|two|three|four|five|six|seven|eight|nine|ten|eleven|twelve|almost|a|quarter|half|of|after|before|at|night|in|the|morning|afternoon|evening|night)+Zulu$"));
    }
}
