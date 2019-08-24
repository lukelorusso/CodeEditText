VerticalSeekBar
===============

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Platform](https://img.shields.io/badge/platform-android-green.svg)](http://developer.android.com/index.html) [![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16) [![Download](https://api.bintray.com/packages/lukelorusso/maven/com.lukelorusso:codeedittext/images/download.svg?version=1.0.0) ](https://bintray.com/lukelorusso/maven/com.lukelorusso:codeedittext/1.0.0/link)

## Presentation ##

This is the source code of an Android library: `-=:[ CodeEditText ]:=-`

- - -

## Why would you need it? ##

*"Input codes easily, numbers or alphabetical, long or short, visible or masked, shown as you like!"*  

**Introducing a fancy and highly customizable EditText, redesigned for codes input.**

What you got:
- chose the max length
- use it with any inputType
- you can mask your input AND choose the character you want to mask it
- customize the layout as you like
- horizontal scroll with auto-focus while typing
- and much more!

![Demo 1](press/demo1.gif)
![Demo 2](press/demo2.gif)

![Demo 3](press/demo3.gif)
![Demo 4](press/demo4.gif)

- - -

## How to use it? ##

Make sure to include the library in your app's build.gradle:

```groovy
    implementation 'com.lukelorusso:codeedittext:1.0.0'
```  

Add the view to your layout:
```xml
<com.lukelorusso.codeedittext.CodeEditText
        android:id="@+id/cetMyCode"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
```  

maybe add some attributes... here you got some, we'll discuss them later
```
        ...
        android:inputType="text"
        android:maxLength="6"
        android:text="09af"
        app:cet_codeMaskChar="#"
        app:cet_maskTheCode="true"
        app:cet_scrollDurationInMillis="300"
        ...
```  

All of them can be also set programmatically.

- - -

# Customization #

## Attributes ##

To set your code programmatically:
```kotlin
cetMyCode.text = "1234"
```

...and the same thing for the maxLength:
```kotlin
cetMyCode.maxLength = 6
```

If the input layout is too big for the screen, it will scrollable and will automatically focus on the portion of the code that the user is typing.

To change the duration of the scrolling effect (in milliseconds):
```kotlin
cetMyCode.scrollDurationInMillis = 300
```

Do you need a particular inputType?
```kotlin
cetMyCode.inputType = InputType.TYPE_CLASS_TEXT // choosing a password type will not mask the input
```

To mask the input (the "password" scenario) just set this boolean:
```kotlin
cetMyCode.maskTheCode = true
```

If you don't like dots:
```kotlin
cetMyCode.codeMaskChar = '#' // or whatever other Char you like :)
```

## Callbacks ##

For your convenience, here's how you intercept the code:
```kotlin
cetMyCode.setOnCodeChangedListener { (code, completed) ->
    // the code has changed
    if (completed) {
        // the code has been fully entered (code.length == maxLength)
    }
}
```

## Design ##

As an example, you can find a custom [**item_code_edit_text.xml**](/codeedittext-example/src/main/res/layout/item_code_edit_text.xml) inside the `res/layout` folder of `codeedittext-example` project.

This is the layout of a single character and it's ENTIRELY CUSTOMIZABLE! You can modify it as you wish; just remember to keep the `TextView`'s id unchanged 😉

Just copy it to your `res/layout` folder and start to change dimensions, text's size, colors or even add more stuff... it's all up to you!

- - -

# Explore! #

Feel free to checkout and launch the example app 🎡

- - -

# Copyright #

Make with 💚 by [Luca Lorusso](http://lukelorusso.com), licensed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)
