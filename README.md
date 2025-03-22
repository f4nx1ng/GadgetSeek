# GadgetSeek

## Envirnment
windows 11
JDK17

python 3.7

DeepSeek API key

### Startup option selection
We recommend using Intellij IDEA

## Setup
https://tai-e.pascal-lab.net/docs/current/reference/en/setup-in-intellij-idea.html

## Static Analysis Usage

1.write taint-config.yml and options.yml like https://tai-e.pascal-lab.net/docs/current/reference/en/taint-analysis.html

2.put the testing jar and config file into in directory

3.run Tai-e with following argument
`--options-file=D:\xxx\Tai-e\java-benchmarks\AspectJWeaver\options.yml`
you can replace `AspectJWeaver` to any other dataset

## LLM Usage

1. put a path output by static analysis in a directory，use `pathdeal.py` to deal with the path and save it to `DealResult`

2. put the java files in a directory, like `CC6Sample` and set up api key in  `main.py`

3. run main.py

## payload Usage

use the ReflectionsAPI to generate payload, like `NewBadException.java`
