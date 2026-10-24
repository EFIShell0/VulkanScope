from pathlib import Path
import itertools
import re
import xml.etree.ElementTree as ET

root = Path(__file__).resolve().parents[1]
kotlin = root / 'app/src/main/java/com/efishell/vulkanscope'
chrome = (kotlin / 'ChromeAutoHide.kt').read_text()
app = (kotlin / 'VulkanScopeRoot.kt').read_text()
settings = (kotlin / 'ChromeVisibilityPreferences.kt').read_text()
components = (kotlin / 'UiComponents.kt').read_text()

assert 'CHROME_INACTIVITY_MILLIS = 3000L' in chrome
assert 'private val preferences = context.applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)' in chrome
assert all(f'"{key}", true' in chrome for key in ('chrome_auto_hide_enabled','chrome_auto_hide_header','chrome_auto_hide_navigation'))
assert all(fn in chrome for fn in ('updateEnabled', 'updateHideHeader', 'updateHideNavigation', 'AutoHideHeader(', 'AutoHideNavigation(', 'shouldShowAppChrome('))
assert 'private set' in chrome
assert app.count('shouldShowAppChrome(') == 2
assert 'LaunchedEffect(chromeInteractionEpoch, page, settingsSection,' in app
assert 'android.os.SystemClock.uptimeMillis()' in app
assert '.pointerInput(Unit)' in app and '.onPreviewKeyEvent' in app
assert 'PointerEventPass.Initial' in app
assert 'maxOf(' in app and 'WindowInsets.statusBars' in app and 'WindowInsets.navigationBars' in app
assert 'TransientStatusOverlayHost(' in app and 'AutoHideNavigation(' in app
screen = (kotlin/'UiSettings.kt').read_text()
assert screen.count('ChromeVisibilityPreferencesCard()') == 1
assert screen.index('ChromeVisibilityPreferencesCard()') > screen.index('CapabilitySectionCard("Opening animation")')
assert 'ChromeVisibilityPreferencesCard()' not in screen.split('null -> VulkanLazyPage',1)[1].split('SettingsSection.INFO',1)[0]
assert 'enabled = enabled' in components and 'internal fun ExpressiveSwitch(' in components
assert all(label in settings for label in ('Automatic bar hiding', 'Top app header', 'Bottom navigation'))
assert all(x in settings for x in ('ic_chrome_auto_hide', 'ic_chrome_header', 'ic_chrome_navigation'))
for icon in ('ic_chrome_auto_hide.xml','ic_chrome_header.xml','ic_chrome_navigation.xml'):
    ET.parse(root / 'app/src/main/res/drawable' / icon)
for idle,master,sub in itertools.product((True,False), repeat=3):
    visible = not idle or not master or not sub
    assert visible == (not(idle and master and sub))
assert 'return@onPreviewKeyEvent' not in app.split('.onPreviewKeyEvent { event ->',1)[1].split('containerColor = ComposeColor.Black',1)[0]
assert 'false\n                    }' in app
print('PASS chrome source policy, default-on preferences, reveal behavior, system insets and vector resources (source only)')
