# Aggregate with CAS multiplier with an aggregate with CAS multiplier

This playground tests the following condition:

A document has sentences, separated by new lines.

Sentences have words, separated by spaces.

The following system is to be run

TopAggregate
  - LineChunker (Chunker), adds annotation "Line" to each line in the document
  - TopCounter, maintains a counter in disk of CAS processed, set to "top.txt".
  - LineAggregate
    - LineSplitter (Splitter), it splits the text into CASes over the Line annotation. Each line goes to a new CAS
    - WordChunker (Chunker), adds annotation "Word" to each space separated word of the CAS processed
    - LineCounter, maintains a counter in disk of CAS processed, set to "lines.txt".
    - WordAggregate
      - WordSplitter (Splitter), it splits the text CASes over the Word annotation, each word goes to a new CAS
      - WordCounter, maintains a counter in disk of CAS processed, set to "words.txt".
  - ExitCounter, maintains a counter in disk of CAS processed, set to "exit.txt".
  
This aggregate is to be run over three documents:

text1.txt:

sentence.
sentence two has 5 words.
sentence three, 4 words.

text2.txt

sentence.
sentence sentence word sentence.
sentence three.

text3.txt

sentence.

text4.txt (empty)

This is executed command-line.

The counters at the end of the run should be:

top.txt: 4
lines.txt: 8
words.txt: 19
exit.txt: 19

Note:
    - `lines.txt` is 8 (not 7) because the empty CAS for `text4.txt` is returned from the LineSplitter.
    - `words.txt` is 19 (not 18) because the empty CAS for `text4.txt` is sent to WordAggregate.

The `out/` folder will have 19 CASes written, with `text4.txt.xmi` indicating the original CAS went through. If the XMI Writer is moved from the aggregate to the CPE, only the 4 original CASes are written.

## Executing

```bash
mvn compile assembly:single
java -jar java -jar ./target/aggregate-ma-0.0.1-SNAPSHOT.jar desc/cpe.xml
```






