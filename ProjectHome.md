An Amahric Tweet retriever and a couple of other methods

Download the source code and modify source code and add your twitter secret and access token accordingly.

Also modify the database connection with proper database name, user name, passowrd.
Feel free to modify the code so that it can accept such parameters from config file or from user input.

Once modified with your secret and access token, run the following from the command line to generate the jar file

```
mvn clean compile assembly:single
```

There other copule of methods

**FilterNonAmharicCharacter** removes non Amahric texts from the document

**Ngram**  Generate ngram from the document provided.

**WordFrequency** generate sorted word frequency, can be used to generate corpus dictionary

while running these methods, all of them require file name as first argument
