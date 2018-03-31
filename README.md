CU Days v1.0
======
An **Android** app for accepted Cornell students and their families to view Cornell Day events. The **iOS** branch can be found [here](https://github.com/cornell-dti/cu-days-ios).
Based on the O-Week app [here](https://github.com/cornell-dti/o-week-android).

Getting Started
------
You will need **Android Studio 3.1** to run the latest version of this app, which uses the following SDKs. Last update **3/31/2018** (remember to check "Show Package Details" on the lower right):

SDK Platforms (tab)
 * Android API 27
   * Android SDK Platform 27

SDK Tools (tab)
 * Android SDK Build-Tools 27.0.2
 * Android Emulator (if you don't have an Android phone)
 * Android SDK Platform-Tools 27.0.0
 * Android SDK Tools 26.1.1
 * Documentation for Android SDK
 * Google Play Services, rev 46
 * Support Repository
   * ConstraintLayout for Android 1.0.2
   * Solver for ConstraintLayout 1.0.2
   * Android Support Repository, rev 47
   * Google Repository 58

Design Choices
------
 * All objects are presumed to **not be <code>null</code>** when passed into a method as a parameter. If an object can be null, use the annotation <code>@Nullable</code>.
 * Syntax:
   * Indent with tabs.
   * Put curly braces on a new line, like so:
   ```java
   if (blah)
   {
      doSomething();
      doSomethingElse();
   }
   ```
   * If a statement fits in a single line, don't use brackets at all, like so:
   ```java
   if (blah)
      doSomething();
   ```
   * ClassesShouldBeNamedLikeThis, as should enums and interfaces. (upper camel case)
   * methodsShouldBeNamedLikeThis, as should non-static or non-final variables. (lower camel case)
   * STATIC_VARS_SHOULD_BE_NAMED_LIKE_THIS, as should any final variables (or variables whose values shouldn't be changed).
 * **RecyclerView**s are used instead of ListViews. Each RecyclerView should have a separate **Adapter** class and at least 1 **ViewHolder** class.
 * <code>TAG</code>s are set on the top of some classes for logging. Set up a shortcut to easily create <code>TAG</code>s for classes by following <a href="https://stackoverflow.com/a/29378779/4028758">this</a> article.
 * An "Event" can refer to 2 things, judging on context:
   1. An <code>Event</code> that will occur during orientation week.
   2. Something to notify listeners of. For example, a click event.
 
Used Libraries
------
 * <a href="https://github.com/google/guava">Guava</a> - a Google Library containing lots of helpful classes for Java. Most notably, immutable data structures (like ImmutableList) and EventBus, which provides a way for classes that do not have references to each other to communicate.
 * <a href="https://github.com/dlew/joda-time-android">JodaTime</a> - a library for immutable time objects, unsupported by Java 7. Includes lots of useful data structures and methods; plus, immutable objects are almost always safer when passing by reference.

Contributors
------
2018
 * **Julia Kruk** - Product Manager
 * **David Chu** - Product Manager
 * **Amanda Ong** - Front-End Developer
 * **Jagger Brulato** - Front-End Developer
 * **Qichen (Ethan) Hu** - Front-End Developer
 * **Arnav Ghosh** - Back-End Developer
 * **Adit Gupta** - Back-End Developer
 * **Jessica Zhao** - Back-End Developer
 * **Cedric Castillo** - Designer
 * **Lisa LaBarbera** - Designer
 * **Justin Park** - Designer

2017
 * **Julia Kruk** - Product Manager
 * **David Chu** - Product Manager
 * **Amanda Ong** - Front-End Developer
 * **Jagger Brulato** - Front-End Developer
 * **Qichen (Ethan) Hu** - Front-End Developer
 * **Arnav Ghosh** - Back-End Developer
 * **Adit Gupta** - Back-End Developer
 * **Cedric Castillo** - Designer
 * **Lisa LaBarbera** - Designer
 * **Justin Park** - Designer
 
2016
 * **Julia Kruk** - Product Manager
 * **Juhwan Park** - Product Manager
 * **David Chu** - Front-End Developer
 * **Vicente Caycedo** - Front-End Developer
 * **Arnav Ghosh** - Back-End Developer

We are a team within **Cornell Design & Tech Initiative**. For more information, see its website [here](http://cornelldti.org/).
<img src="http://cornelldti.org/img/logos/cornelldti-dark.png">
