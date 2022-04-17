# Screen Translator

> This Android app is a Quick Setting tile to translate the current screen using Naver Papago (네이버 파파고) app.

## Demo

https://user-images.githubusercontent.com/23007879/163716868-2f5020cc-e247-4208-98ac-e1ff01d14a61.mp4

Notice that using the Screen Translator app is much quicker than the traditional screenshot and share UI.

## How does it work?

This app doesn't have any activity / UI.

1. When user touches the quick settings tile, an intent is sent by `ScreenTranslatorTileService::onClick` to trigger `ScreenTranslatorAccessibilityService::onStartCommand`
2. If the user didn't give accessibility permissions yet, they are <u>redirected to accessibility settings page</u>.
3. If the accessibility permissions are present, a <u>swipe up gesture is sent</u> to system uisng accessiblity service to collapse notification panel from which quick settings tile was triggered.
4. Finally, a <u>screenshot is taken</u> using accessiblity service and sent to Papago app for further translation inside their app's activity.
5. If Papago app is not installed in the system, user is notified of the same through a toast.

## Author

Vamsi Krishna Reddy Satti - [vamsi3](https://github.com/vamsi3)

## License

This project is licensed under the MIT License - please see the [LICENSE](LICENSE) file for details.

## Disclaimer

NAVER, Papago and all related logos are trademarks of NAVER Corporation or its affiliates.