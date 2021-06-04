# KTXHelper

![Build](https://github.com/Aqinn/KTXHelper/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/com.github.aqinn.ktxhelper.svg)](https://plugins.jetbrains.com/plugin/com.github.aqinn.ktxhelper)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/com.github.aqinn.ktxhelper.svg)](https://plugins.jetbrains.com/plugin/com.github.aqinn.ktxhelper)

## Description

<!-- Plugin description -->
<h3>English Description:</h3>
Kotlin Android Extensions is a plugin developed by the Kotlin team to allow us to write less code during development. View binding is currently included.<br>
View binding requires only three steps:<br>
<ol>
  <li>
    Add the plugin configuration to the build.gradle file in the Module<br>
    <code>apply plugin: 'kotlin-android-extensions'</code>
  </li>
  <li>
    Introduce resource files in activities, Fragments, Adapters, and custom views that need to be bound to the View<br>
    <code>import kotlinx.android.synthetic.main.layout_name.*</code>
  </li>
  <li>
    At the point of use, the view is accessed directly using the corresponding ID in the XML
  </li>
</ol>
This plugin is used to generate the code in Step 2 with one click.
<hr/>
<h3>中文说明：</h3>
Kotlin Android Extensions 是 Kotlin 团队开发的一个插件，目的是让我们在开发过程中更少的编写代码。目前包括了视图绑定的功能。<br>
视图绑定仅需三步：<br>
<ol>
  <li>
    在 Module 中的 build.gradle 文件添加插件配置<br>
    <code>apply plugin: 'kotlin-android-extensions'</code>
  </li>
  <li>
    在需要绑定视图的 Activity、Fragment、Adapter 及自定义 View 中引入资源文件<br>
    <code>import kotlinx.android.synthetic.main.layout_name.*</code>
  </li>
  <li>
    在使用的位置，直接使用 xml 中对应的 id 访问视图
  </li>
</ol>
本插件的作用是一键生成步骤 2 中的代码。
<hr/>
<h4>Source code: <a href="https://github.com/Aqinn/KTXHelper">GitHub Page</a></h4>
<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "KTXHelper"</kbd> >
  <kbd>Install Plugin</kbd>

- Manually:

  Download the [latest release](https://github.com/Aqinn/KTXHelper/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
