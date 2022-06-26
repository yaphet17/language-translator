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
## Tech Stack
  - [JavaFX](https://openjfx.io/) for UI.
  - [Maven](https://maven.apache.org/) for dependency management.
  - [Unirest](https://github.com/Kong/unirest-java) for making an HTTP request to Google translation API.
## Setup
 - Host the provided script
     1. Got to [Google Apps Script](https://www.google.com/script/start/) ->Click `Start Scripting`.
     2. Create `New project` and replace everything in the editor with the provied script.
     3. Click `Deploy` -> `New Deployment` -> Click the gear icon on the top left corner of the popup.
     4. Select Web app -> Fill all fields and choose `Anyone` for the field `Who has access`.
     5. Copy the generate `Web App` url.
     6. Goto` language-translator/src/main/resources/application.properties ` and assign the url to `translator.api.wep-app.url` field.
  - Run the application
      - You need to have jdk installed in your pc and JAVA_HOME configured in enviroment variables.
      - Download [Maven](https://maven.apache.org/download.cgi) and configure M2_HOME in enviroment variables.
      - Clone this repository to your pc
      - Open the project in you favorite IDE and run it.
      - After running it you will notice an executable jar file is created in `target` folder. You can use this jar file on any computer 
      as long as the appropriate JDK is installed.
  
  
![Screenshot from 2022-06-26 21-13-56](https://user-images.githubusercontent.com/78301074/175828531-fa18f4a1-f6f7-4a42-8a8f-2b1622ef9d61.png)
