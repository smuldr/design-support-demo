Android Design Support Library Demo
===================================

When Google announced that Android apps should follow their Material Design standards, they did not give the developers a lot of tools to actually implement this new look and feel. Of course Google’s own apps were all quickly updated and looked amazing, but the rest of us were left with little more than fancy design guidelines and no real components to use in our apps.

So last weeks release of the [http://android-developers.blogspot.nl/2015/05/android-design-support-library.html](Android Design Support Library) came as a relief to many. It promises to help us quickly create nice looking apps that are consistent with the rest of the platform, without having to roll everything for ourselves. Think of it as AppCompat’s UI-centric companion.

The NavigationView is the part of this library which I thought the most interesting. It helps you create the slick sliding-over-everything navigation drawer that is such a recognizable part of material apps. I will demonstrate how to use this component and how to avoid some common mistakes.

## Basic Setup

The basic setup is pretty straightforward, you add a DrawerLayout and NavigationView to your main layout resource:

```xml
<android.support.v4.widget.DrawerLayout
  android:id="@+id/drawer_layout"
  xmlns:android="http://schemas.android.com/apk/res/android"
  xmlns:app="http://schemas.android.com/apk/res-auto"
  android:layout_width="match_parent"
  android:layout_height="match_parent"
  android:fitsSystemWindows="true">

  <!-- The main content view -->
  <LinearLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <!-- Toolbar instead of ActionBar so the drawer can slide on top -->
    <android.support.v7.widget.Toolbar
      android:id="@+id/toolbar"
      android:layout_width="match_parent"
      android:layout_height="@dimen/abc_action_bar_default_height_material"
      android:background="?attr/colorPrimary"
      android:minHeight="?attr/actionBarSize"
      android:theme="@style/AppTheme.Toolbar"
      app:titleTextAppearance="@style/AppTheme.Toolbar.Title"/>

    <!-- Real content goes here -->
    <FrameLayout
      android:id="@+id/content"
      android:layout_width="match_parent"
      android:layout_height="0dp"
      android:layout_weight="1"/>
  </LinearLayout>

  <!-- The navigation drawer -->
  <android.support.design.widget.NavigationView
    android:id="@+id/navigation"
    android:layout_width="wrap_content"
    android:layout_height="match_parent"
    android:layout_gravity="start"
    android:background="@color/ternary"
    app:headerLayout="@layout/drawer_header"
    app:itemIconTint="@color/drawer_item_text"
    app:itemTextColor="@color/drawer_item_text"
    app:menu="@menu/drawer"/>

</android.support.v4.widget.DrawerLayout>
```

And a `drawer.xml` menu resource for the navigation items:

```xml
<menu xmlns:android="http://schemas.android.com/apk/res/android">
  <!-- group with single selected item so only one item is highlighted in the nav menu -->
  <group android:checkableBehavior="single">
    <item
      android:id="@+id/drawer_item_1"
      android:icon="@drawable/ic_info"
      android:title="@string/item_1"/>
    <item
      android:id="@+id/drawer_item_2"
      android:icon="@drawable/ic_help"
      android:title="@string/item_2"/>
  </group>
</menu>
```

Then wire it up in your Activity. Notice the nice `onNavigationItemSelected(MenuItem)`` callback:

```java
public class MainActivity extends AppCompatActivity implements
    NavigationView.OnNavigationItemSelectedListener {

  private static final long DRAWER_CLOSE_DELAY_MS = 250;
  private static final String NAV_ITEM_ID = "navItemId";

  private final Handler mDrawerActionHandler = new Handler();
  private DrawerLayout mDrawerLayout;
  private ActionBarDrawerToggle mDrawerToggle;
  private int mNavItemId;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    // load saved navigation state if present
    if (null == savedInstanceState) {
      mNavItemId = R.id.drawer_item_1;
    } else {
      mNavItemId = savedInstanceState.getInt(NAV_ITEM_ID);
    }

    // listen for navigation events
    NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
    navigationView.setNavigationItemSelectedListener(this);

    // select the correct nav menu item
    navigationView.getMenu().findItem(mNavItemId).setChecked(true);

    // set up the hamburger icon to open and close the drawer
    mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open,
        R.string.close);
    mDrawerLayout.setDrawerListener(mDrawerToggle);
    mDrawerToggle.syncState();

    navigate(mNavItemId);
  }

  private void navigate(final int itemId) {
    // perform the actual navigation logic, updating the main content fragment etc
  }

  @Override
  public boolean onNavigationItemSelected(final MenuItem menuItem) {
    // update highlighted item in the navigation menu
    menuItem.setChecked(true);
    mNavItemId = menuItem.getItemId();

    // allow some time after closing the drawer before performing real navigation
    // so the user can see what is happening
    mDrawerLayout.closeDrawer(GravityCompat.START);
    mDrawerActionHandler.postDelayed(new Runnable() {
      @Override
      public void run() {
        navigate(menuItem.getItemId());
      }
    }, DRAWER_CLOSE_DELAY_MS);
    return true;
  }

  @Override
  public void onConfigurationChanged(final Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    mDrawerToggle.onConfigurationChanged(newConfig);
  }

  @Override
  public boolean onOptionsItemSelected(final MenuItem item) {
    if (item.getItemId() == android.support.v7.appcompat.R.id.home) {
      return mDrawerToggle.onOptionsItemSelected(item);
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {
    if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
      mDrawerLayout.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  protected void onSaveInstanceState(final Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(NAV_ITEM_ID, mNavItemId);
  }
}
```

## Extra Style

This setup results in a nice-looking menu with some default styling. If you want to go a bit further, you can add a header view to the drawer and add some colors to the navigation menu itself:

```xml
<android.support.design.widget.NavigationView
  android:id="@+id/navigation"
  android:layout_width="wrap_content"
  android:layout_height="match_parent"
  android:layout_gravity="start"
  android:background="@color/drawer_bg"
  app:headerLayout="@layout/drawer_header"
  app:itemIconTint="@color/drawer_item"
  app:itemTextColor="@color/drawer_item"
  app:menu="@menu/drawer"/>
```

Where the drawer_item color is actually a ColorStateList, where the `checked` state is used by the current active navigation item:

```
<selector xmlns:android="http://schemas.android.com/apk/res/android">
  <item android:color="@color/drawer_item_checked" android:state_checked="true" />
  <item android:color="@color/drawer_item_default" />
</selector>
```

## Open Issues

The current version of the library does come with its limitiations. My main issue is with the system that highlights the current item in the navigation menu. The `itemBackground` attribute for the NavigationView does not handle the `checked` state of the item correctly: somehow either all items are highlighted or none of them are. This makes this attribute basically unusable for most apps. I ran into more trouble when trying to work with submenu's in the navigation items. Once again the highlighting refused to work as expected: updating the selected item in a submenu makes the highlight overlay disappear altogether. In the end it seems that managing the selected item is still a chore that has to be solved manually in the app itself, which is not what I expected from what is supposed to be a drag-and-drop component aimed to take work away from the developers.

## Conclusion

I think the NavigationView component missed the mark a little. My initial impression was pretty positive: I was able to quickly put together a nice looking navigation menu with very little code. The issues with the highlighting of the current item makes it more difficult to use than I would expect, but let's hope that these quirks are removed in an upcoming release of the design library.
