import urllib.request

url = "https://raw.githubusercontent.com/android/nowinandroid/main/core/designsystem/src/main/res/values/font_certs.xml"
try:
    urllib.request.urlretrieve(url, "app/src/main/res/values/font_certs.xml")
    print("Success")
except Exception as e:
    print(f"Failed: {e}")

