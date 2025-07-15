<div align="center" style="margin-bottom: -10%">
    <img src="https://www.jadedmc.net/plugins/JadedSync/images/banner.png" />
</div>

# JadedSync

<img src="https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square" /> <img src="https://img.shields.io/badge/license-MIT-blue.svg?style=flat-square" />


JadedSync is an upcoming data syncing plugin for Paper/Velocity servers. It is designed for developers and networks looking to easily scale their plugins across multiple servers and proxies through the use of Redis. It comes with an API for other plugins to take advantage of in order to easily communicate across servers.

## Maven
```xml
<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
```xml
<dependency>
	    <groupId>com.github.JadedMC</groupId>
	    <artifactId>JadedSync</artifactId>
	    <version>-{VERSION}</version>
	</dependency>
```