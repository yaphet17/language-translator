# language-translator

This app will help you to translate any language supported by Google translation API from one to another. The best thing about this app is you don't need any 
  subscription(:wink:), you just need Google account to host the script provided below:point_down:.
 
 ```
 function doGet(e) {
  var sourceText = '';
  if (e.parameter.q) {
    sourceText = e.parameter.q;
  }

  var sourceLang = 'auto';
  if (e.parameter.source) {
    sourceLang = e.parameter.source;
  }

  var targetLang = 'ja';
  if (e.parameter.target) {
    targetLang = e.parameter.target;
  }

  var translatedText = LanguageApp.translate(sourceText, sourceLang, targetLang);

  var json = {
    translatedText: translatedText,
  };

  // set JSONP callback
  var callback = 'callback';
  if (e.parameter.callback) {
    callback = e.parameter.callback;
  }

  // return JSONP
  return ContentService.createTextOutput(JSON.stringify(json)).setMimeType(
    ContentService.MimeType.JSON
  );
}
```
## Setup
1. Got to [Google Apps Script](https://www.google.com/script/start/) ->Click `Start Scripting`.
2. Create `New project` and replace everything in the editor with the provied script.
3. Click `Deploy` -> `New Deployment` -> Click the gear icon on the top left corner of the popup.
4. Select Web app -> Fill all fields and choose `Anyone` for the field `Who has access`.
5. Copy the generate `Web App` url.
6. Goto` language-translator/src/main/resources/application.properties ` and assign the url to `translator.api.wep-app.url` field.

## How to run
To run this app you need to have [Maven](https://maven.apache.org/) and [JDK](https://www.oracle.com/java/technologies/downloads/) installed on you machine.
