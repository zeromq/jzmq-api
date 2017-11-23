# Changelog

## v0.1.0 (unreleased)

* [#37](https://github.com/zeromq/jzmq-api/pull/37): Improvements for large byte values in FrameBuilder
  * Add error checking for large byte/string values
  * Allow unsigned byte values
* [#36](https://github.com/zeromq/jzmq-api/pull/36): Consistency improvements to Message
  * Removed Message.Frame.wrap() in favor of Message.Frame.of()
* [#34](https://github.com/zeromq/jzmq-api/pull/34): Fixes and enhancements
  * Added putBytes and getBytes methods
  * Fixed bug in FrameBuilder's checkCapacity method
  * Added additional methods to Message, Frame, and FrameBuilder to support code generation (e.g. zproto)
  * Fixed ReactorBuilder's start/run methods
* [#32](https://github.com/zeromq/jzmq-api/pull/32): Added Clone Pattern implementation
  * Added Clone pattern support  …
  * Fixed bug in FrameBuilder when growing buffer dynamically
  * Cleaned up asserts
  * Added multiple subscription support
* [#30](https://github.com/zeromq/jzmq-api/pull/30): Fixes and improvements
  * Fixed shutdown bug in no-context API
  * Removed Object varargs in various API interfaces for more idiomatic Java
  * Removed unnecessary public visibility modifier in various interface declarations
  * Added additional javadoc to various objects and API interfaces
  * Usability improvements to Message
  * Lowered visibility of internal API method in ManagedContext (never released-experimental)
  * Converted handlers (singleton instance fields) in reactors to nested classes for readability
  * Changed BinaryStarClient to forward additional replies
  * Fixed terminate bug in Reactor when running on jeromq
  * Fixed NPE in BinaryStarClient on shutdown
  * Upgraded to jeromq 0.3.6 for build stability
* [#29](https://github.com/zeromq/jzmq-api/pull/29): Multiple code improvements
* [#28](https://github.com/zeromq/jzmq-api/pull/28): Added no-context API
* [#26](https://github.com/zeromq/jzmq-api/pull/26): Added new convenience methods to Message
* [#25](https://github.com/zeromq/jzmq-api/pull/25): Added unregister method to Poller
* [#22](https://github.com/zeromq/jzmq-api/pull/22): Added LoopAdapter class
* [#19](https://github.com/zeromq/jzmq-api/pull/19): Changes to Message.Frame
  * Changed Frame to use ByteBuffer instead of byte array
  * Added various `Frame.put*` methods
* [#14](https://github.com/zeromq/jzmq-api/pull/15): Added support for Clone Pattern
  * Added terminate method to Context with implementation to asynchronously terminate (attempting to easily abort pollers)
  * Added event-driven Reactor
  * Added Binary Star Reactor
  * Added subscribeAll method to SubSocketBuilder
  * Added BeaconReactor
  * Changed PollListener API to accept Pollable
  * Added BinaryStarClient background agent and socket builder
  * Various improvements and test fixes
* [#13](https://github.com/zeromq/jzmq-api/pull/14): Added support for simple queue device
* [#12](https://github.com/zeromq/jzmq-api/pull/12): Changes to support jzmq / jeromq maven profiles
  * Added two maven profiles to pom.xml, jzmq and jeromq
  * Switched to snapshot builds of jzmq and jeromq until next release, as these changes depend on recent changes made to both libraries
  * Changes to accommodate building against both jzmq and jeromq
* [#11](https://github.com/zeromq/jzmq-api/pull/11): Upgraded to Java 7
* [#10](https://github.com/zeromq/jzmq-api/pull/10): Improvements to Poller and Backgroundable
  * Made Poller an interface and moved to api package
  * Added methods to Poller for enable/disable (somewhat analogous to register/unregister)
  * Fixed re-indexing pollables bug
  * Added support for managing background threads other than PAIR sockets
  * Added method to Backgroundable for shutdown, called by ManagedContext
  * Added shadow method to Context for multi-threaded shutdown/cleanup
* [#9](https://github.com/zeromq/jzmq-api/pull/9): Additional enhancements to exception handling
  * Messages return null when encoutering errors
  * Poller throws higher level exceptions
  * Enhanced/refactored Poller
* [#8](https://github.com/zeromq/jzmq-api/pull/8): Advanced exception handling
  * Added new exceptions (ZMQRuntimeException, InvalidSocketException, ContextTerminatedException) all extending from ZMQException
  * Added utility to wrap exceptions from jzmq
  * Added exception handling logic to wrap exceptions
* [#7](https://github.com/zeromq/jzmq-api/pull/7): API improvements
  * New Context.fork method inspired by ZThread.fork
  * New interface: Backgroundable
  * Additional URLs for connect methods
* [#5](https://github.com/zeromq/jzmq-api/pull/5): Improvements to Message API
