# language-translator
<p>This app will help you to translate any language supported by Google translation API from one to another. The best thing about this app is you don't need any 
  subscription, you just need Google account to host the script provided below.</p>
 
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
