Latte Support for PhpStorm
=========================================

<!-- Plugin description -->
Fork of the [original free plugin](https://github.com/nette-intellij/intellij-latte), provides decent support for [Latte](https://latte.nette.org) templates.

<!-- Plugin description end -->

![example](https://i.imgur.com/KjIAx90.gif)


Notice
------------
This plugin is in maintenance mode only (but feel free to contribute new features), errors and performance issues will still be fixed and updates will be on time, if you are looking for a plugin with more features, check out paid [Latte Pro](https://plugins.jetbrains.com/plugin/19661-latte-pro) plugin. This fork has been created as another free plugin, since code completion feature was removed from the original free plugin.

If you have any problems with the plugin, [create an issue](https://github.com/Rixafy/LatteSupport/issues/new/choose) or use #latte channel at the [Nette Discord](https://discord.gg/azXxTbuQVq).


Installation
------------
Settings → Plugins → Browse repositories → Find "Latte Support" → Install Plugin → Apply


Installation from .jar file
------------
Download `instrumented.jar` file from [latest release](https://github.com/Rixafy/LatteSupport/releases) or latest successful [GitHub Actions build](https://github.com/Rixafy/LatteSupport/actions)


Supported Features
------------------

* Syntax highlighting and code completion for `PHP` in `Latte` files
* Type support and reference to classes and methods in `Latte` files (see [{templateType}](https://latte.nette.org/type-system#toc-templatetype))
* Refactoring support for `Latte` files (when changing class name, method name, etc.)
* Live analysis of `Latte` files (unused variables, syntax errors, etc.)


Building
------------

```$xslt
$ ./gradlew buildPlugin
```

Testing in dummy IDE
------------

```$xslt
$ ./gradlew runide
```
