# gm-engine

A dicerolling library written in Clojure

## Installation

COMPILE:

Requires Leiningen installed

* Clone from https://github.com/TuesdayHat/gm-engine.git
* From command line navigate to the project folder, run
    $ lein run [args]



STANDALONE:

* Download .jar file from releases
* From command line, navigate to the folder where you downloaded the .jar

$ java -jar gm-engine.jar [args]
    

## Arguments
* []d[] -- marks dice, 
  * ex 3d6 => roll 3 six-sided dice
* +, -, *, / -- do math. order of operations applies.
* (...) -- do whatever is within the parenthesis first. Math function.
* k[] -- roll and keep highest [] dice. Applies to a dice roll 
  * ex 4d6k3 => keep highest 3 dice 
* kl[] -- roll and keep lowest [] dice. Applies to a dice roll
* > -- Dicepool. Return number of dice which roll at or above a target number 
  * ex 6d6>5 => roll 6 six sided dice, tell how many rolled 5 or 6
* # -- comments. Everything written after the # will be returned as-is

## Examples
$ lein run 3d6+5
=> [15 nil]

$ java -jar gm-engine.jar 1d20+6#will save
=> [22 #will save]

## TODO
* The Great Refactor of the parser function
* Add multiple line parsing
* Add ability to have it return raw output of all rolls in a command
* Attach this engine to other user interfaces.
  * Plans for Discord Bot, standalone application GUI, mobile app

### Bugs
* very little error checking
  * keeping more dice than rolled will crash the engine
* Commenting is mildly buggy
  * should display nothing if no comment was given, instead of nil
  * should exclude the # itself

## License

Copyright © 2018 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
