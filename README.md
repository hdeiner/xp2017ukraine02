# TimeTeller
------------

This project is for the XP Days Ukraine 2017 conference, and represents the first state of code for the "Improving Your Organization’s Technical Prowess With Legacy Code Retreats" talk.

Please do not judge me on this code alone. ☺ 

This code is designed to be an example of legacy code that works, but is nothing that we are proud of and pretty hard to maintain.

#### The goal at this point is to:
* We now are interested in putting together some automated tests that will enable us to actually refactor and change code and not worry that we broke things.
* The goal is to not change any code, but to simply add automated tests.  Sometimes, you will need to add code to even be able to do this.  But remember that until we have these "gold master" tests in place, our house of cards could come crashing down upon us.
* Note that these tests will not be able to test everything.  That usually requires changing the code, to allow for true unit testing of all code paths.  For example, in this code, since I'm testing time, I had to cheat two ways.  One cheat has to to with the clock used.  I'm forced at this stage to use the system clock for whatever the time happens to be.  First, the time could change between the time I get my result and the time that I try to verify it in the test.  And, I did not want to go through too many hoops trying to test the "time in words" results.  So, I used a regular expression to test against, which is only testing that the right words come up (note that Zulu is a differentiator).
* I know that there are so many eggregious and glaring coding issues that it's hard to not make changes.  But when working with legacy code, it's important to try not to change the code quite yet.  We need a safety net to allow us to know if any changes we make affect functionality.  Once that safety net is in place, we can start to make things better.