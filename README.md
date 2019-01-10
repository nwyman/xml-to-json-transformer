## XML Converter

### Build
mvn clean install

### Run
Running from within target folder:
```$xslt
java -jar xml-json-converter-1.0-SNAPSHOT.jar -i "..\src\main\resources\sample1.xml" -t "..\src\main\resources\transform1.json"
```

```$xslt
java -jar xml-json-converter-1.0-SNAPSHOT.jar -i "..\src\main\resources\sample2.xml" -t "..\src\main\resources\transform2.json" -o "output2.json"
```