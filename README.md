# AndroidCameraNotify
*AndroidCameraNotify is a simple Android Project used by me to test some android apis.
I decided to make it public in hope that anyone will need some kind of snippets from this project!*

# Features
- When you open the app, you are requested for permission
- Then you can take a photo ( **solved @^&#$& camera distortion problem** )
- The photo taken is automatically sent to the server via a **POST-Form** ( to a php file ).
- On the website you can list all the photos, to everything you want with them.
- Also, the app Notifies you every hour( can be modified ) to take a photo. The notification is Facebook's ChatHead like on the screen, on the upper right corner, and lasts for some seconds on the screen.
- If you are in the app, the notification is not shown.
- Also, if you restart the phone or something, the notification will still appear even if you do not open the app :).

# Permissions Needed

- android.permission.CAMERA ( .... )
- android.permission.WRITE_EXTERNAL_STORAGE ( for saving image on the phone )
- android.permission.RECEIVE_BOOT_COMPLETED ( for preserving notification after reboot )
- android.permission.INTERNET ( for sending the photo over internet )
- ACTION_MANAGE_OVERLAY_PERMISSION ( for the notification )

# Also ..

- The app uses: BootReceiver, WakefulBootReceiver, surfaceView, AlarmManager, ( old ) Camera Api

# Configuration
*The variables that can be configured are in the **strings.xml** file*

- **uploadUrl** - where the photo is sent
- **notifyInterval** - internal for the notification
- **notifyTimeOnScreen** - how much time the notification lasts

# Known bugs

- Sometimes, app crashses before you approve the permissions

# Disclaimer

- This project is not production-ready, you have to catch some errors and stylish a little the app. It is only purposed to show some functionalities.
- Also, the PHPFiles are not secured, you have to pay attention on them.

# License

Included in file **LICENSE**
