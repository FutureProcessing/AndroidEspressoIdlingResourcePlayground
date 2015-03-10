Android Espresso 2
==============================

Inside
------------------------------
Android Testing with Espresso 2 and Dagger 2. Synchronization with long running/network operations and mocks injection.

Keywords
------------------------------
Android, Espresso2, Dagger2, DI, Mock, Mocking, jUnit4, AndroidTesting, RxJava, Retrolambda, TestSynchronization

Overview
------------------------------
In this simple code I would like to focus on Espresso2 with Dagger2, especially on usage of Espresso’s IdlingResource and mocking Dagger2 dependencies in tests. Why these topics? Well..to quote Espresso documentation *“Leave your waits, syncs, sleeps, and polls behind and let Espresso gracefully manipulate and assert on the application UI when it is at rest.”* and *“The centerpiece of Espresso is its ability to seamlessly synchronize all test operations with the application under test. By default, Espresso waits for UI events in the current message queue to process and default AsyncTasks to complete before it moves on to the next test operation. This should address the majority of application/test synchronization in your application.”*. Unfortunately this is not completely true. Espresso tries to do its best in the matter of automatic synchronization with Main Thread. It usually works with the emphasis on word ‘usually’ and if you are reading this article then you are probably encountering these ‘rare’ cases when it doesn’t. Of course there is an approach suggested in Espresso documentation: *“In such cases, the first thing we suggest is that you don your testability hat and ask whether the user of non-standard background operations is warranted. In some cases, it may have happened due to poor understanding of Android and the application could benefit from refactoring (for example, by converting custom creation of threads to AsyncTasks).”*. 

So..use AsyncTasks? No, thank you very much. We can all agree that AsyncTasks should be avoided for network or other long running operations. I don’t want to detail why but there are plenty of other articles on that matter. Nowadays you are probably using Retrofit with rxJava Observables for networking. In that case Espresso fails to synchronize waiting. But there is a way to instruct Espresso to make it know when your application is idle. Enter IdlingResource. 

And Dagger2?..because it is great, fun, clean and I read somewhere that you can’t inject mocks when testing with Dagger2. You can but I guess that this will change in the future as my solution is not the cleanest or simplest one.

Test application and use cases
-----------------------------------
Ok, let’s concisely describe our test application and test cases. We have 3 Activities: FirstActivity, SecondActivity and ThirdActivity.

- First activity has two buttons. One simulates network call which lasts 10 seconds and after it has successfully finished it navigates application to SecondActivity which has label “Second Activity”. Typical login use case. Second button just navigates to ThirdActivity.
- SecondActivity has just one button and a label. Pressing the button simulates long running operation (computation or network, doesn’t matter) and changes label to “Running…”. When operation finishes label changes to “SUCCESS”. This may be your typical refresh.
- ThirdActivity uses real network call to obtain Jake’s GitHub repo names and prints the first one it finds. 

So what **test cases** do we have and would like to test?

1. User provides login details (in our case just button click) and clicks “Login” button. Network call is being made and if login is successful new Activity is displayed and it contains some well known text. We want to verify if this well known text is present.
2. User runs some computation or refresh on Activity and after it finishes successfully the same Activity is somehow changed. We want to verify if this change occurred.
3. Typical usage of service that does real networking. We want to mock network call so our test is quick, repeatable and tests our business logic.

Gradle config
----------------------
Let’s see some bits of Gradle config.

Test dependencies for Espresso:

```groovy
    androidTestCompile 'com.android.support.test.espresso:espresso-core:2.0'    
    androidTestCompile 'com.android.support.test:testing-support-lib:0.1'
```

Don’t forget to use new test runner:

```groovy
android {
    compileSdkVersion 21
    buildToolsVersion "21.1.2"

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }

    defaultConfig {
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
```

1. Login Use Case


When button1 on FirstActivity is clicked:
```java
button1OnFirstActivity.setOnClickListener(v -> {
   button1OnFirstActivity.setEnabled(false);
   subscription = Observable.empty()
                     .subscribeOn(Schedulers.newThread())
                     .observeOn(AndroidSchedulers.mainThread())
                     .delay(10, TimeUnit.SECONDS)
                     .subscribe(launchSecondActivity());
        });
```

we wait 10 seconds and launch SecondActivity.

Our test case in Espresso (FirstActivityEspressoTest.java) could look like this:
        // Click on button that simulates long network call and after success goes to SecondActivity
        onView(withId(R.id.button1OnFirstActivity)).perform(click());

        // Validate label on SecondActivity
        onView(withText("Second Activity")).check(ViewAssertions.matches(isDisplayed()));

You can try to run this test but you will see it fails. Espresso will click on button1 and then immediately try to validate presence of “Second Activity” text. This text will however be displayed after 10 seconds on new Activity. Synchronization does not work because we are not using AsyncTasks. You can add Thread.sleep(12000) between click and validation lines but it defeats the whole purpose and is inefficient and can lead to false test failures. This is when we can use IdlingResource. Usage of IdlingResource requires certain design of our application. In other words our application needs to cooperate with its environment, somehow inform interested observers about its state. We would like to minimise impact of our tests on application, ideally to never have our application know about any test dependencies, classes, interfaces.

My solution is to provide stream (Observable) of Activity events like Created or Destroyed from global Application object. It minimises changes in application and so can be easily introduced into existing code base. Test case can then subscribe to this Observable and provide Espresso with all the data required to know when application is idle and displaying SecondActivity. All we need to do is to add internal class to our custom Application class:

    private static class ActivityEventProducer implements ActivityLifecycleCallbacks, Observable.OnSubscribe<ActivityEvent> {

        private ArrayBlockingQueue<ActivityEvent> activityEvents = new ArrayBlockingQueue<>(256, false);
        private boolean anyOneSubscribed;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            if(!anyOneSubscribed) {
                return;
            }
            ActivityEvent activityEvent = new ActivityEvent();
            activityEvent.setActivityClass(activity.getClass());
            activityEvent.setEventKind(ActivityEventKind.CREATED);
            activityEvents.add(activityEvent);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            if(!anyOneSubscribed) {
                return;
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            if(!anyOneSubscribed) {
                return;
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            if(!anyOneSubscribed) {
                return;
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {
            if(!anyOneSubscribed) {
                return;
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            if(!anyOneSubscribed) {
                return;
            }
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if(!anyOneSubscribed) {
                return;
            }
            ActivityEvent activityEvent = new ActivityEvent();
            activityEvent.setActivityClass(activity.getClass());
            activityEvent.setEventKind(ActivityEventKind.DESTROYED);
            activityEvents.add(activityEvent);
        }

        @Override
        public void call(Subscriber<? super ActivityEvent> subscriber) {
            anyOneSubscribed = true;
            try {
                while(!subscriber.isUnsubscribed()) {
                    ActivityEvent activityEvent = activityEvents.take();
                    subscriber.onNext(activityEvent);
                }
            } catch(Exception e) {
                subscriber.onError(e);
            } finally {
                anyOneSubscribed = false;
                activityEvents.clear();
            }
        }
    }

This class combines two aspects. Firstly, it is ActivityLifecycleCallbacks implementation so when registered will receive lifecycle events from each Activity. Secondly it is rxJava OnSubscribe implementation and will be called when someone subscribes to it. The code is very simple: ArrayBlockingQueue is filled with events for Activity creation or destruction and when there is at least one element in this array, subscriber will be notified. Otherwise subscriber will wait for events (array is blocking). 

All we need to do in Application onCreate is to register instance of this class:
    @Override
    public void onCreate() {
        super.onCreate();
        ActivityEventProducer activityEventProducer = new ActivityEventProducer();
        _activityEventStream = Observable.create(activityEventProducer);
        registerActivityLifecycleCallbacks(activityEventProducer);

You don’t have to touch any Activities or change any other design.

Our test case needs to change to use this stream with IdlingResource. Remember we need to wait for SecondActivity creation:
    @Test
    public void firstActivityTest() throws InterruptedException {
        secondActivityCreatedIdlingResource = new SecondActivityCreatedIdlingResource();

        GlobalApplication.activityEventStream().
                subscribeOn(Schedulers.newThread())
                         .observeOn(AndroidSchedulers.mainThread())
                         .subscribe(secondActivityCreatedIdlingResource);

        // Click on button that simulates long network call and after success goes to SecondActivity
        onView(withId(R.id.button1OnFirstActivity)).perform(click());

        // Wait until SecondActivity is created
        registerIdlingResources(secondActivityCreatedIdlingResource);

        // Validate label on SecondActivity
        onView(withText("Second Activity")).check(ViewAssertions.matches(isDisplayed()));
    }

The crucial line is registerIdlingResources(secondActivityCreatedIdlingResource);
It instructs Espresso to synchronize on our implementation. So lets implement it SecondActivityCreatedIdlingResource:
    private static class SecondActivityCreatedIdlingResource extends Subscriber<ActivityEvent> implements IdlingResource {
        private volatile ResourceCallback resourceCallback;
        private volatile boolean secondActivityCreated;

        @Override
        public String getName() {
            return "SecondActivity Created";
        }

        @Override
        public boolean isIdleNow() {
            return secondActivityCreated;
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
            this.resourceCallback = resourceCallback;
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public void onNext(ActivityEvent activityEvent) {
            if(secondActivityCreated(activityEvent)) {
                secondActivityCreated = true;
                resourceCallback.onTransitionToIdle();
            }
        }

        private boolean secondActivityCreated(ActivityEvent activityEvent) {
            return activityEvent.getActivityClass().equals(SecondActivity.class) && activityEvent.getEventKind() == ActivityEventKind.CREATED;
        }
    }

This class is both IdlingResource and Subscriber. It starts with isIdleNow returning false - so Espresso immediately waits. But when second activity is created and we get this creation event it will report that resource is idle and Espresso will continue and hence validate.

Don’t forget to unregister everything in test teardown:
    @After
    public void tearDown() {
        unregisterIdlingResources(secondActivityCreatedIdlingResource);
        secondActivityCreatedIdlingResource.unsubscribe();
    }


By the way in the current solution there is no progress bar or any other UI indicator during login call. This is bad design and can really confuse the user. Interestingly, when I added progress bar control during login the test passes on Nexus 5 with Lollipop. It looks like Espresso correctly detects constant load on application UI events loop and synchronizes wait until SecondActivity is ready. Very cool! Unfortunately it fails on Galaxy 4 with Android 4.4. I’m not completely sure what is going on under the hood but for the purpose of demonstration of IdlingResource I removed ProgressBar.

Secundo by the way: You can see that the test is not extending ActivityInstrumentationTestCase2. This is thanks to invaluable and legenedary Jake Wharton and ActivityRule

Test now passes no matter if progress bar is present or not on both Nexus 5 and SGS4.

2. Bla
3. Bla
