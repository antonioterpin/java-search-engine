# Barebone search engine in Java

Early experiments (2016) with Java to implement a Search engine:
- Crawls websites up to desired depth from a seed
- Computes page ranks

Many things left as TODOs :)

```bash
javac -sourcepath src -cp ".:lib/jsoup.jar" -d out WebBrowser.java
```

```bash
java -cp ":out/:lib/jsoup.jar" WebBrowser
```