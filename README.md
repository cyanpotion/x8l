# x8l
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.xenoamess/x8l/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.xenoamess/x8l)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Build Status](https://travis-ci.org/cyanpotion/x8l.svg?branch=master)](https://travis-ci.org/cyanpotion/x8l)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=cyanpotion_x8l&metric=alert_status)](https://sonarcloud.io/dashboard?id=cyanpotion_x8l)
<!--[![Build status](https://ci.appveyor.com/api/projects/status/594i6j3y8w8o2a69?svg=true)](https://ci.appveyor.com/project/XenoAmess/x8l)-->

<p>
  <b>
      X8l format is a type of data format, it is designed to be replace of xml/json in most cases when you need the data be shorter(while remains readability).
  </b>
</p>
<br/>
<p>
  <b>
      See <a href="https://plugins.jetbrains.com/plugin/13915-x8l">plugin on Jetbrains plugin storage</a><br/>
      See <a href="https://github.com/XenoAmess/x8l_idea_plugin">plugin on github</a><br/>
      See <a href="https://github.com/cyanpotion/x8l">X8l Grammar/SDK</a><br/>
  </b>
</p>
<br/>
<p><img src="https://raw.githubusercontent.com/XenoAmess/x8l_idea_plugin/master/pictures/screenshot.png" width="1080" height="720" alt="screenshot.png"/></p>
<br/>
<p><a href="https://github.com/XenoAmess/x8l_idea_plugin/issues">Send Feedback</a></p>

## aim of x8l
when I deal with markup languages I just find that some of them are unnecessarily swollen.

They use much more disk than they need.

Of cause some of the reasons might be considering ability of random access or other things,

but in most cases, we just use dom and load the whold tree into our memory,
and we just want it be smaller.

After all network, or IO, is still a bottle-neck.

So we bring up a new markup language here.

x8l is a variant of xml, and aim to use as small space as possible.

A naive xml (one without XLS or DTD or some things) can be transform into an x8l, and then transform back without losing data.

A json file can also be transformed into an x8l or transform back (but every values of every attributes will lost their type information and become pure string).

## bench mark.

We use some data from a wiki mirror to get the xml bench mark.

The average ratio after transform from xml to x8l is around 84%,
Which means about 16% of the size can be reduced. 

We use some data from a json file to get the xml bench mark.

The average ratio after transform from json to x8l is around 99%,
Which means about 1% of the size can be reduced. 

(Notice that we only do json bench mark for prove of expandability of x8l.
We want to make sure when a data can be saved as json, it shall be able to be saved to x8l.
Of course due to x8l's lack of type information, all type information of attributes will lost in this process.
)

The details of the bench marks are listed in com.xenoamess.x8l.BenchMark

## basic tutorial of x8l

here goes a basic demo of x8l.

### comment

first,a comment is like this
```text
<<first,a comment is like this> 
```
or this
```text
< <or this this>  
```
or even this this. < in a comment need not be transcoded.
```text
<<<or even this this.< in a comment need not be transcoded.>  
```
use % to transcode. A character after % is treated as a simple character. 
```text
<<use %% to transcode. A character after % is treated as a simple character. for example, %>, and this is still in it.>
```

### attributes

the content between the first < and the second < is treated as "attributes".
```text
<<the content between the first < and the second < is treated as "attributes".>  
```
the order of attributes is important, and node with different order of same attributes are different.

attribute can have = in it.if so, it will be departed into key and value.

key is the part left than the first =,and value is the rest content.

for example, "a" is a key and "b" is a value
```text
<a=b>>
```
remember, the first =.
```text
<a=b=c>>
```
that means this node's key is "a" and value is "b=c"

if there is no "=" in a attribute then the whole string is the key,and "" is the value

notice that both keys and values in attributes will only be treated string, but not float or double or datetime or something.

space chars between attributes are treated as nothing, so does '\r' '\n' '\t'
which means you can write it like this
```text
<views
    windowWidth=1280
    windowHeight=1024
    scale=2.0
    fullScreen=0
>>
```
and it equals to
```text
<views windowWidth=1280 windowHeight=1024 scale=2.0 fullScreen=0>>
```

### children

the content between the second < and the %> is treated as "children".

children must be nodes, and children's parent is the node which it in.

there are now 3 kinds of nodes, "content node", "text node", and "comment node".

only "content node" have attributes and contents.

so what is a text node? A text Node is a text like this.
```text
so what is a text node? A text Node is a text like this.
```

be careful! a space in text node is meaningful and cannot be deleted!

that means these are 3 different nodes:
```text
<<>
```
```text
<< >
```
```text
<<
>
```

if you want to delete all text node with "empty char" content,you can use X8lTree.trim().

that is the basic tutorial.

you can now run the testCases in com.xenoamess.x8l.X8lTest and see the output of the tree of this readme.

that should be helpful.

thanks for reading.



## real case of x8l
```text
<commonSettings
    titleText=GamepadMassage
    gameWindowClassName=com.xenoamess.gamepad_massage.FalseGameWindow
    logoClassName=com.xenoamess.gamepad_massage.FalseWorld
    titleClassName=com.xenoamess.cyan_potion.gameWindowComponents.Title
    worldClassName=com.xenoamess.gamepad_massage.FalseWorld
>>
<views
    windowWidth=1280
    windowHeight=1024
    scale=2.0
    fullScreen=0
>>
<specialSettings
    autoShowGameWindowAfterInit=0
    noConsoleThread=1
>>
<debug>>
<keymap using>
    <GLFW_KEY_W>XENOAMESS_KEY_UP>
    <GLFW_KEY_A>XENOAMESS_KEY_LEFT>
    <GLFW_KEY_S>XENOAMESS_KEY_DOWN>
    <GLFW_KEY_D>XENOAMESS_KEY_RIGHT>
    <GLFW_KEY_UP>XENOAMESS_KEY_UP>
    <GLFW_KEY_LEFT>XENOAMESS_KEY_LEFT>
    <GLFW_KEY_DOWN>XENOAMESS_KEY_DOWN>
    <GLFW_KEY_RIGHT>XENOAMESS_KEY_RIGHT>
    <GLFW_KEY_ESCAPE>XENOAMESS_KEY_ESCAPE>
    <GLFW_KEY_ENTER>XENOAMESS_KEY_ENTER>
    <GLFW_KEY_SPACE>XENOAMESS_KEY_SPACE>
    <GLFW_KEY_LEFT_SHIFT>XENOAMESS_KEY_LEFT_SHIFT>
    <GLFW_KEY_RIGHT_SHIFT>XENOAMESS_KEY_RIGHT_SHIFT>
    <GLFW_MOUSE_BUTTON_LEFT>XENOAMESS_MOUSE_BUTTON_LEFT>
    <GLFW_MOUSE_BUTTON_RIGHT>XENOAMESS_MOUSE_BUTTON_RIGHT>
    <GLFW_MOUSE_BUTTON_MIDDLE>XENOAMESS_MOUSE_BUTTON_MIDDLE>
>
<backup worldClassName=com.xenoamess.rpg_module.world.World>>
<backup worldClassName=com.xenoamess.modern_alchemy.scene.ProgramScene>>
<backup worldClassName=com.xenoamess.gamepad_massage.FalseWorld>>
<backup gameWindowClassName=com.xenoamess.cyan_potion.GameWindow>>

<merge version=1.0>
    <0001>
        <en>Thanks!>
        <<just comment>
<ch>谢谢!>
    >
    <<just comment>
    <0002>
        <en>Hello?>
<<just comment>
<ch>您好？>
    >
>

<merge version=1.0>
    <0001>
        <en>Thanks!>
        <<just comment>
<ch>谢谢!>
    >
>
```
