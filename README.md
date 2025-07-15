<div align="center" style="margin-bottom: -10%">
    <img src="https://www.jadedmc.net/plugins/JadedSync/images/banner.png" />
</div>

# JadedSync

<img src="https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square" /> <img src="https://img.shields.io/badge/license-MIT-blue.svg?style=flat-square" />


JadedSync is an upcoming data syncing plugin for Paper/Velocity servers. It is designed for developers and networks looking to easily scale their plugins across multiple servers and proxies through the use of Redis. It comes with an API for other plugins to take advantage of in order to easily communicate across servers.

Looking for a list of plugins that support JadedSync? Check out the [Support Plugins](https://github.com/JadedMC/JadedSync/wiki/Supported-Plugins) page.

## Features
* Cross-Server (and Cross-Proxy) communication.
* Automatically add and remove servers from the proxy when a server is started/stopped.
* Remotely control servers from other servers on the network.
* API for plugins to easily become cross-server compatible.

## Maven
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```
```xml
<dependency>
    <groupId>com.github.JadedMC.JadedSync</groupId>
    <artifactId>bukkit</artifactId>
    <version>{VERSION}</version>
</dependency>
```